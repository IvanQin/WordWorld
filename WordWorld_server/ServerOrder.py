import json


class ServerOrder:
    GAME_START_ORDER = 'game_start'
    GAME_STOP_ORDER = 'game_stop'
    REMOVE_WORD_ORDER = 'remove_word'
    GAME_FINISH_ORDER = 'game_finish'
    GAME_WORDS_ORDER = 'game_words'

    def create_order(self, server_action, param="", param2=""):
        order = dict()
        if server_action == self.GAME_START_ORDER:
            order['action'] = self.GAME_START_ORDER
            order['opponent_name'] = param
        elif server_action == self.GAME_STOP_ORDER:
            order['action'] = self.GAME_STOP_ORDER
        elif server_action == self.REMOVE_WORD_ORDER:
            order['action'] = self.REMOVE_WORD_ORDER
            order['word_id'] = param
            order['hit_result'] = param2
        elif server_action == self.GAME_FINISH_ORDER:
            order['action'] = self.GAME_FINISH_ORDER
        elif server_action == self.GAME_WORDS_ORDER:
            order['action'] = self.GAME_WORDS_ORDER
            order['param'] = param
        order_json = json.dumps(order)
        return order_json
