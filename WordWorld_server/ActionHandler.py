"""
Created by Qinyifan on 17/1/16.
"""
from Service import *
from Log import *
import json


class ActionHandler:
    @staticmethod
    def response_to_action(info, action):
        """
        If 'action' appears in the params, then refer to this method to process
        :param info: the key and value in GET url or POST body
        :param action: the value of action
        :return: depends on the action
        """
        TAG = 'ActionHandler:response_to_action'
        service = Service()
        ret_dict = dict()
        if action == 'getWords':
            # user_id = info['userId']
            number_of_words = info['numberOfWords']
            wordlist_id = info['wordListId']
            pick_style = info['style']
            if pick_style == 'continuous':
                from_word = info['fromWord']
            else:
                from_word = ""
            # word_get:should be an array with every words in dict format [{},{},...,{}]
            # every dict should be a {'word':word, 'phonetic':phonetic, 'interp':interp,'id':id ,'r':r,'w':w}
            # here r means right recite times, w means wrong recite times
            words_get = service.get_words(number_of_words, wordlist_id, pick_style, from_word)
            if words_get:
                ret_dict['status'] = 'Success'
                ret_dict['words_get'] = words_get

        elif action == 'register':
            email = info['userEmail']
            password = info['password']
            name = info['userName']
            is_success = service.register(email, password, name)
            if is_success:
                ret_dict['status'] = 'Success'
                Log.i(TAG, 'user:%s has successfully registered' % email)
            else:
                ret_dict['status'] = 'Fail'
                Log.w(TAG, 'registeration failed <email=%s>' % email)

        elif action == 'userAuthentication':
            email = info['userEmail']
            password = info['password']
            is_success, user_name = service.can_login(email, password)
            if is_success:
                ret_dict['status'] = 'Success'
                ret_dict['user_name'] = user_name
                Log.i(TAG, 'user:%s has successfully login' % email)
            else:
                ret_dict['status'] = 'Fail'
                Log.w(TAG, 'login failed <email=%s>' % email)

        return ret_dict
