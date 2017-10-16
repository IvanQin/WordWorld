from datetime import datetime


class Log:
    @staticmethod
    def i(TAG, message):
        now = datetime.now()
        t = now.strftime('%Y-%m-%d %H:%M:%S')
        print '[' + t + '][INFO]' + TAG + ':' + message

    @staticmethod
    def w(TAG, message):
        now = datetime.now()
        t = now.strftime('%Y-%m-%d %H:%M:%S')
        print '\033[1;33;40m',
        print '[' + t + '][WARNING]' + TAG + ':' + message,
        print '\033[0m'

    @staticmethod
    def e(TAG, message):
        now = datetime.now()
        t = now.strftime('%Y-%m-%d %H:%M:%S')
        print '\033[1;31;40m',
        print '[' + t + '][ERROR]' + TAG + ':' + message,
        print '\033[0m'
