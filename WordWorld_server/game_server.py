#! usr/bin/python
import socket
import time
import threading
from Log import *
from GameMessage import *
from Queue import Queue
from ServerOrder import ServerOrder
from Service import *
import random


class GameServer:
    HOST = '10.105.125.132'
    PORT = 8000
    buffer_size = 10000
    TAG = 'game_server'
    game_message = None
    SLEEP_INTERVAL = 1
    task = None
    client_set = None
    is_game_running = False
    is_waiting_player = False
    server_order = None
    service = None
    GAME_WORDS_NUMBER = 20

    HIT_WORD_MSG = "hit_word"
    GAME_EXIT_MSG = "game_exit"
    SUCCESS_HIT = "success_hit"
    FAIL_HIT = "fail_hit"
    OTHER_HIT = "other_hit"

    GAME_START_ORDER = 'game_start'
    GAME_STOP_ORDER = 'game_stop'
    GAME_FINISH_ORDER = 'game_finish'
    GAME_WORDS_ORDER = 'game_words'
    REMOVE_WORD_ORDER = 'remove_word'

    HEART_MSG = "heart_msg"

    hit_word_id_set = []
    game_word_list = []

    def __init__(self):
        self.game_message = GameMessage()
        self.task = Queue()
        self.client_set = dict()
        self.server_order = ServerOrder()
        self.service = Service()

    def recover_stat(self):
        self.hit_word_id_set = []
        self.game_word_list = []
        self.is_waiting_player = False
        self.is_game_running = False
        self.client_set.clear()
        Log.i(self.TAG, "recover status for the next game!")

    def listen(self, client):
        """
        Keeps listen from the client, and check if 'action' is included in the received message.
        If so, create a task into the queue. And the game monitor will check it.
        :param client: a client dict with its ip, socket_send, email
        :return: no return
        """
        clientsocket = client.get('socket_listen')
        ip = client.get('ip')
        email = client.get('email')
        Log.i(self.TAG, "start a new listen socket")
        flag = 3
        while True:
            try:

                # check the flag status

                if flag <= 0:  # the client has dropped
                    socket_send_op = self.get_opponent_send_socket(email=email)
                    if socket_send_op is not None:
                        stop_game_json = self.server_order.create_order(server_action=self.GAME_STOP_ORDER)
                        socket_send_op.send(stop_game_json)
                    self.close_socket()
                    self.recover_stat()

                message = clientsocket.recv(self.buffer_size)
                time.sleep(self.SLEEP_INTERVAL)

                # if there is no message from the client
                if message == "":
                    flag -= 1
                    continue
                else:
                    message = self.get_real_message(message)
                    if message == "":
                        continue

                Log.i(self.TAG, message)
                content = json.loads(message)
                if 'action' in content:
                    action = content.get('action')
                    info = content.get('info')
                    word_id = content.get('wordId')

                    # new a task and put it into the task queue
                    # task has the following elements:{'action','ip','socket_send','info','email'}
                    current_task = dict()
                    current_task['action'] = action
                    current_task['ip'] = ip
                    current_task['socket_send'] = self.client_set[email].get('socket_send')
                    current_task['info'] = info
                    current_task['word_id'] = word_id
                    current_task['email'] = email
                    self.task.put(current_task)

            except IOError:
                Log.e(self.TAG, "IO error occur in listen thread")
                self.close_socket()
                self.recover_stat()

            if not self.is_game_running and not self.is_waiting_player:
                break

    def get_real_message(self, message):
        message_set = message.split('\n')
        message = ""
        for tmp in message_set:
            if tmp != "" and tmp != self.HEART_MSG:
                message = tmp
                break
        return message

    def wait_connection(self, serversocket):
        while True:
            Log.i(self.TAG, "waiting for a connection")
            clientsocket, addr = serversocket.accept()
            localtime = time.asctime(time.localtime(time.time()))
            Log.i(self.TAG, "<%s> receive client from %s" % (localtime, addr))
            ret_obj = None
            try:
                message = clientsocket.recv(self.buffer_size)
                message = self.get_real_message(message)
                Log.i(self.TAG, message)
                ret_obj = self.game_message.message_handler(message, addr)  # ret_obj is a dict
            except IOError:
                Log.e(self.TAG, "IO error occurs in receive")
            if not ret_obj:
                continue

            # setup means a new thread should be created to support a socket
            if 'setup' in ret_obj:
                connection_type = ret_obj.get('type')
                player_email = ret_obj.get('player_email')
                player_name = ret_obj.get('player_name')
                # client is a dict
                # client[player_email] = {'socket_listen':socket_listen,'socket_send':socket_send,'ip':ip}
                if connection_type == 'listen':
                    Log.i(self.TAG, "Get listen socket from %s" % player_email)
                    if player_email in self.client_set.keys():
                        client = self.client_set[player_email]
                        client['socket_listen'] = clientsocket

                    else:
                        client = dict()
                        client['ip'] = addr
                        client['socket_listen'] = clientsocket
                        client['email'] = player_email
                        client['player_name'] = player_name
                        self.client_set[player_email] = client

                    # if connection comes means
                    self.is_waiting_player = True

                    client = self.client_set[player_email]
                    listen_thread = threading.Thread(target=self.listen, args=(client,))
                    listen_thread.start()
                    # self.client_socket_listen_set.append(client_listen)

                elif connection_type == 'send':
                    Log.i(self.TAG, "Get send socket from %s" % player_email)
                    if player_email in self.client_set.keys():
                        client = self.client_set[player_email]
                        client['socket_send'] = clientsocket

                    else:
                        client = dict()
                        client['ip'] = addr
                        client['socket_send'] = clientsocket
                        client['email'] = player_email
                        client['player_name'] = player_name
                        self.client_set[player_email] = client

            if self.is_player_full():
                Log.i(self.TAG, "player is ready, game start!")
                self.game_init()
                self.is_game_running = True
                self.is_waiting_player = False

                # game monitor thread start
                monitor_thread = threading.Thread(target=self.game_monitor)
                monitor_thread.start()
            else:
                Log.i(self.TAG, "wait for another player...")

    def game_init(self):
        email_set = self.client_set.keys()
        player_email_1 = email_set[0]
        player_email_2 = email_set[1]
        player_1 = self.client_set[player_email_1]
        player_2 = self.client_set[player_email_2]
        player_1['opponent_email'] = player_email_2
        player_2['opponent_email'] = player_email_1
        player_1['score'] = 0
        player_2['score'] = 0
        player_name_1 = player_1['player_name']
        player_name_2 = player_2['player_name']

        socket_send_1 = player_1['socket_send']
        socket_send_2 = player_2['socket_send']

        if socket_send_1 is None:
            Log.w(self.TAG, "send socket is none! <useremail=%s>" % player_email_1)
        else:
            Log.i(self.TAG, "get send socket <useremail=%s>" % player_email_1)

        if socket_send_2 is None:
            Log.w(self.TAG, "send socket is none! <useremail=%s>" % player_email_2)
        else:
            Log.i(self.TAG, "get send socket! <useremail=%s>" % player_email_2)

        # send the random words to both clients
        random_wordlist_id = random.randint(a=0, b=10)
        self.game_word_list = self.service.get_words(number_of_words=self.GAME_WORDS_NUMBER,
                                                     wordlist_id=random_wordlist_id,
                                                     pick_style='random')
        game_word_list = ""
        for game_word in self.game_word_list:
            game_word_list = game_word_list + game_word + '\n'
        # game_word_list_json = json.dumps(self.game_word_list)
        game_word_json_msg = self.server_order.create_order(server_action=self.GAME_WORDS_ORDER,
                                                            param=game_word_list)
        content_length = len(game_word_json_msg)
        len_of_content_length = len(unicode(content_length))
        socket_send_1.send('!' + unicode(len_of_content_length) + unicode(content_length) + game_word_json_msg)
        socket_send_2.send('!' + unicode(len_of_content_length) + unicode(content_length) + game_word_json_msg)

        time.sleep(self.SLEEP_INTERVAL)
        # inform the 2 players that the game starts

        start_json_msg_1 = self.server_order.create_order(server_action=self.GAME_START_ORDER, param=player_name_2)
        start_json_msg_2 = self.server_order.create_order(server_action=self.GAME_START_ORDER, param=player_name_1)
        Log.i(self.TAG, "send message '" + start_json_msg_1 + "' to client1!")
        Log.i(self.TAG, "send message '" + start_json_msg_2 + "' to client2!")
        socket_send_1.send(start_json_msg_1)
        socket_send_2.send(start_json_msg_2)

    def is_player_full(self):
        if len(self.client_set.keys()) < 2:
            Log.i(self.TAG, "less than 2 players!")
            return False
        if len(self.client_set.keys()) > 2:
            Log.w(self.TAG, "more than 2 players!")
            self.close_socket()
            self.recover_stat()
            return False

        for email in self.client_set.keys():
            client = self.client_set[email]
            if 'socket_send' not in client.keys():
                return False
            if 'socket_listen' not in client.keys():
                return False
        # all sockets are established (include 4 sockets)
        # ready for starting game
        return True

    def get_send_socket(self, email):
        client = self.client_set[email]
        socket_send = client['socket_send']
        return socket_send

    def get_opponent_send_socket(self, email):
        client = self.client_set[email]
        if 'opponent_email' in client:
            opponent_email = client['opponent_email']
            opponent = self.client_set[opponent_email]
            socket_send_op = opponent['socket_send']
            return socket_send_op
        return None

    def get_all_send_socket(self):
        email_set = self.client_set.keys()
        if len(email_set) == 2:
            player_email_1 = email_set[0]
            player_email_2 = email_set[1]
            player_1 = self.client_set[player_email_1]
            player_2 = self.client_set[player_email_2]
            socket_send_1 = player_1['socket_send']
            socket_send_2 = player_2['socket_send']
            return socket_send_1, socket_send_2
        elif len(email_set) == 1:
            player_email_1 = email_set[0]
            player_1 = self.client_set[player_email_1]
            socket_send_1 = player_1['socket_send']
            return socket_send_1, None
        else:
            return None, None

    def get_all_listen_socket(self):
        email_set = self.client_set.keys()
        if len(email_set) == 2:
            player_email_1 = email_set[0]
            player_email_2 = email_set[1]
            player_1 = self.client_set[player_email_1]
            player_2 = self.client_set[player_email_2]
            socket_listen_1 = player_1['socket_listen']
            socket_listen_2 = player_2['socket_listen']
            return socket_listen_1, socket_listen_2
        elif len(email_set) == 1:
            player_email_1 = email_set[0]
            player_1 = self.client_set[player_email_1]
            socket_listen_1 = player_1['socket_listen']
            return socket_listen_1, None
        else:
            return None, None

    def close_socket(self):
        socket_send_1, socket_send_2 = self.get_all_send_socket()
        socket_listen_1, socket_listen_2 = self.get_all_listen_socket()
        if socket_send_1 is not None:
            socket_send_1.close()
        if socket_send_2 is not None:
            socket_send_2.close()
        if socket_listen_1 is not None:
            socket_listen_1.close()
        if socket_listen_2 is not None:
            socket_listen_2.close()

    def game_monitor(self):
        while True:
            if not self.task.empty():
                # current_task has 4 elements
                # current_task:{'action','ip','socket_send','info','email'}
                current_task = self.task.get()
                action = current_task['action']
                # TODO:(specified action)
                if action == self.HIT_WORD_MSG:
                    # one click the right meaning of the words, the words will disappear from the other's view
                    word_id = current_task['word_id']
                    if word_id in self.hit_word_id_set:
                        # the opponent has already remove the words
                        # then inform the player to remove the words (actually no use because the word should have
                        # been removed)
                        email = current_task['email']
                        socket_send = self.get_send_socket(email)
                        remove_word_json = self.server_order.create_order(server_action=self.REMOVE_WORD_ORDER,
                                                                          param=word_id, param2=self.FAIL_HIT)
                        socket_send.send(remove_word_json)
                    else:
                        # add the word into the hit word set
                        self.hit_word_id_set.append(word_id)

                        email = current_task['email']
                        # inform the player to remove the words and say success_hit
                        socket_send = self.get_send_socket(email)
                        remove_word_json = self.server_order.create_order(server_action=self.REMOVE_WORD_ORDER,
                                                                          param=word_id, param2=self.SUCCESS_HIT)
                        socket_send.send(remove_word_json)
                        # inform the opponent to remove the words and say fail

                        socket_send_op = self.get_opponent_send_socket(email=email)
                        remove_word_json = self.server_order.create_order(server_action=self.REMOVE_WORD_ORDER,
                                                                          param=word_id, param2=self.OTHER_HIT)
                        socket_send_op.send(remove_word_json)

                elif action == self.GAME_EXIT_MSG:
                    email = current_task['email']
                    socket_send_op = self.get_opponent_send_socket(email=email)
                    stop_game_json = self.server_order.create_order(server_action=self.GAME_STOP_ORDER)
                    socket_send_op.send(stop_game_json)
                    self.close_socket()
                    self.recover_stat()

            time.sleep(self.SLEEP_INTERVAL)

            # if all of the word has been hit, then finish the game
            if len(self.hit_word_id_set) == self.GAME_WORDS_NUMBER:
                self.finish_game()

            if not self.is_game_running:
                break

    def finish_game(self):
        # 1. inform the players 2.close the sockets 3.recover the data 4.(optional) record the data

        # get both send socket
        Log.i(self.TAG, "trying to finish game...")
        socket_send_1, socket_send_2 = self.get_all_send_socket()
        # send finish_game_order to both
        finish_game_json = self.server_order.create_order(server_action=self.GAME_FINISH_ORDER)
        socket_send_1.send(finish_game_json)
        socket_send_2.send(finish_game_json)
        self.close_socket()
        self.recover_stat()

    def set_up_server(self, port):
        # socket.setdefaulttimeout(20)
        serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_address = (self.HOST, port)
        serversocket.bind(server_address)
        serversocket.listen(10)
        return serversocket


if __name__ == '__main__':
    server = GameServer()
    serversocket = server.set_up_server(server.PORT)
    server.wait_connection(serversocket)
