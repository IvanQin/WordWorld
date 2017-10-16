from Log import *
import json


class GameMessage:
    """
    Handle the message(json format) from the client.
    """
    TAG = 'GameMessage'
    player_ip_set = []

    def message_handler(self, message, addr):
        ret_obj = {}
        Log.i(self.TAG, message)
        if message == "":
            return None

        try:
            mes_json = json.loads(message)
        except ValueError:
            Log.w(self.TAG, "the request is invalid!!")
            return None

        if 'action' in mes_json:
            action = mes_json.get('action')
            if action == 'game_connect':
                # get the ip and playeremail
                player_email = mes_json.get('playerEmail')
                player_ip = addr
                connection_type = mes_json.get('connectionType')
                player_name = mes_json.get('playerName')
                ret_obj['setup'] = True
                ret_obj['type'] = connection_type
                ret_obj['player_email'] = player_email
                ret_obj['player_ip'] = addr
                ret_obj['player_name'] = player_name
                """
                if player_ip in self.player_ip_set:
                    # if the ip is already in the waiting list then return keep waiting
                    ret_obj['status'] = 'already'
                else:
                    # if the ip is no in the waiting list then add into waiting
                    self.player_ip_set.append(player_ip)
                    ret_obj['status'] = 'add'
                
                if len(player_ip_set) >= 2:# if 2 player is already waiting in the queue
                    ret_obj['status'] = 'begin'
                """
            elif action == 'game_exit':
                # get the ip and playeremail
                player_email = mes_json.get('playerEmail')
                player_ip = addr

                """
                if player_ip in self.player_ip_set:
                    ret_obj['status'] = 'remove'
                    self.player_ip_set.remove(player_ip)
                else:
                    Log.w(self.TAG, 'player %s:%s is not waiting in the queue!'%(player_email,player_ip))
                
                if len(player_ip_set) < 2:
                    # game over
                    ret_object['status'] = 'over'
                    Log.i(self.TAG, 'Less than 2 player, game over!')
                """

        else:
            Log.w(self.TAG, "no param 'action' in the received message!")

        return ret_obj
