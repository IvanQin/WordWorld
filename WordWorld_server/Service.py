import MySQLdb
from User import *
from DaoImpl import *


class Service:
    dao_impl = DaoImpl()
    word_book_name_set = ['CET4', 'CET6', 'TOEFL', 'GRE', 'CET4', 'CET6', 'TOEFL', 'GRE', 'CET4', 'CET6', 'TOEFL',
                          'GRE', 'CET4', 'CET6', 'TOEFL', 'GRE', 'CET4', 'CET6', 'TOEFL', 'GRE']

    def is_useremail_exist(self, useremail):
        user = self.dao_impl.select_one_from_email(useremail)
        if user is None:
            return False
        return True

    def register(self, useremail, password, name):
        is_register_success = False
        user = User(userEmail=useremail, password=password, userName=name)
        # check if the user email is duplicated 
        if self.is_useremail_exist(useremail) is True:
            return is_register_success  # False
        if self.dao_impl.insert_user(user) is True:
            is_register_success = True
        return is_register_success

    def can_login(self, userEmail, password):
        user = None
        is_valid = False
        user = self.dao_impl.select_one_from_email(userEmail)
        if user is None:
            is_valid = False
            return is_valid, None
        elif password != (user.getPassword()):
            is_valid = False
            return is_valid, None
        else:
            is_valid = True

        return is_valid, user.getUsername()

    def get_words(self, number_of_words, wordlist_id, pick_style, from_word=""):
        words_array = []
        # id:1=CET4,2=CET6, 3=TOEFL, 4= GRE, 5=CET4,6=CET6,7=TOEFL,8=GRE
        book_name = self.word_book_name_set[int(wordlist_id)]
        words_array = self.dao_impl.select_words(number_of_words, book_name, pick_style, from_word)
        return words_array


'''------ below for debug ------ '''

if __name__ == '__main__':
    service = Service()
    '''
    useremail = 'yfqin13@fudan.edu.cn'
    if service.is_useremail_exist(useremail):
        print '%s exist' % useremail
    else:
        print '%s does not exist' % useremail
    useremail = '123'
    if service.is_useremail_exist(useremail):
        print '%s exist' % useremail
    else:
        print '%s does not exist' % useremail

    if service.register('875346282@qq.com', 'abcde'):
        print 'Successfully registered'
    print service.is_useremail_exist('875346282@qq.com')
    print service.can_login('875346282@qq.com', 'abcd')
    print service.can_login('875346282@qq.com', 'abcde')
    print service.can_login('1231', 'abcd')
    '''
    print service.get_words(number_of_words=10, wordlist_id=1, pick_style='random')
