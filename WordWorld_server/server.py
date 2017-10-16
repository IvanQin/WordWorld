"""
Created by Qinyifan on 17/1/16.
"""

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import urlparse
from ActionHandler import *
from Log import *

HOST = '10.105.125.132'
PORT = 8080


class RequestHandler(BaseHTTPRequestHandler):
    TAG = 'server'

    def do_GET(self):
        """
        Handle HTTP GET request
        :return: corresponding return object
        """
        parsed_path = urlparse.urlparse(self.path)
        query = parsed_path.query
        get_info = {}
        ret_dict = {}
        action = ""
        if query != '':
            query_set = str(query).split('&')
            for item in query_set:
                if '=' in item:
                    [key, value] = item.split('=')
                    get_info[key] = value

        # show return columns
        if 'action' in get_info:
            action = get_info['action']
            ret_dict = ActionHandler.response_to_action(get_info, action)

            status = ret_dict.get('status')
            if status == 'Success':
                if action == 'getWords':
                    # here words_get is like [{},{},...{}], every word is in dict format
                    send_buffer = ""
                    words_get = ret_dict.get('words_get')
                    for word in words_get:
                        json_word = json.dumps(word)
                        # json_word
                        # every json should be a {"word":word, "phonetic":phonetic, "interp":interp,"r":r,"w":w}
                        send_buffer = send_buffer + json_word + '\n'
                    self.response('GET', 'text/plain', send_buffer)
        else:
            Log.w(self.TAG, "no action in GET! param")

    def do_POST(self):
        content_length = int(self.headers['content-length'])
        content = self.rfile.read(content_length)
        content_set = str(content).split('&')
        post_info = {}
        ret_dict = {}
        for item in content_set:
            [key, value] = item.split('=')
            post_info[key] = value
        if 'action' in post_info:
            action = post_info['action']
            ret_dict = ActionHandler.response_to_action(post_info, action)
            status = ret_dict.get('status')
            Log.i(self.TAG, status)
            if status == 'Success':
                if action == 'userAuthentication':
                    user_name = ret_dict.get('user_name')
                    self.response('POST', 'text/plain', 'isValid=true\n' + 'userName=' + user_name + '\n')
                elif action == 'register':
                    self.response('POST', 'text/plain', 'isRegSuccess=true\n')
            else:
                if action == 'userAuthentication':
                    self.response('POST', 'text/plain', 'isValid=false\n')
                elif action == 'register':
                    self.response('POST', 'text/plain', 'isRegSuccess=false\n')
        else:

            Log.w(self.TAG, 'no action in POST param')

    def response(self, request_type, content_type, send_buffer=""):
        charset = 'UTF-8'
        content = send_buffer.encode(charset)
        self.send_response(200)
        self.send_header('Content-type', content_type + ';charset=' + charset)
        self.end_headers()
        self.wfile.write(content)


if __name__ == '__main__':
    from BaseHTTPServer import HTTPServer

    server = HTTPServer((HOST, PORT), RequestHandler)
    print 'Server is started, using Ctrl+C to stop'
    server.serve_forever()
