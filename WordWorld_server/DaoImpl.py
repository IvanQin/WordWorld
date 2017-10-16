# encoding=utf-8
import MySQLdb
from Dao import *
from User import *
from Log import *
import json


class DaoImpl:
    TAG = "DaoImpl"
    dao = None

    def __init__(self):
        self.dao = Dao()

    def select_one_from_email(self, useremail):
        user = User()

        conn = self.dao.getConn()
        cur = conn.cursor()

        sql = "select * from users u where u.userEmail = %s"
        param = (useremail,)

        row_number = cur.execute(sql, param)

        if row_number > 1:
            Log.e(self.TAG, 'userEmail:%s is not unique' % useremail)
            # acutally here cannot return None
            return None

        if row_number == 0:
            Log.w(self.TAG, 'no rows is selected')
            return None

        # here the row_number should be 1
        rows = cur.fetchall()
        r = rows[0]  # the first and the last record of the query set

        user.setUsername(r[0])
        user.setEmail(r[1])
        user.setPassword(r[2])
        user.setVip(r[3])

        self.dao.closeAll(cur, conn)
        return user

    def insert_user(self, user):
        sql = "insert into users values (%s,%s,%s,%s)"
        param = (user.getUsername(), user.getEmail(), user.getPassword(), user.getVip())
        Log.i(self.TAG, str(param))
        # print("inser Sql is     "+ sql)
        conn = self.dao.getConn()
        cur = conn.cursor()
        row_number = cur.execute(sql, param)
        conn.commit()
        self.dao.closeAll(cur, conn)
        if row_number == 1:
            return True
        return False

    def select_words(self, number_of_words, book_name, pick_style, from_word=""):
        sql = ""
        words_array = []

        conn = self.dao.getConn()
        cur = conn.cursor()
        Log.i(self.TAG, 'get into select_words')
        if pick_style == 'first':
            # check if total words number is larger than the required number
            # if less than, pick the rest words
            total_row_number = self.get_row_number(book_name)

            if total_row_number < number_of_words:
                number_of_words = total_row_number
                Log.w(self.TAG, "the required words number is larger than the total number of words!")

            sql = "select * from words where words.book=%s limit %s"
            param = (book_name, number_of_words)

            row_number = cur.execute(sql % param)
            Log.i(self.TAG, "select %s words from book %s <style=first>" % (number_of_words, book_name))

        elif pick_style == 'random':
            sql = "select * from words where words.book='%s' order by rand() limit %s"
            param = (book_name, number_of_words)

            row_number = cur.execute(sql % param)
            Log.i(self.TAG, "select %s words from book %s <style=random>" % (number_of_words, book_name))

        elif pick_style == 'continuous':
            # check if rest words number is larger than the required number
            # if less than, pick the rest words

            rest_row_number = self.get_row_number(book_name, from_word)
            if rest_row_number > number_of_words:
                number_of_words = rest_row_number
                Log.w(self.TAG, "the required words number is larger than the rest rows of number from %s" % from_word)

            sql = "select * from words where words.book=%s words.word>%s limit %s"
            param = (book_name, from_word, number_of_words)

            row_number = cur.execute(sql, param)
            Log.i(self.TAG, "select %s words from book %s <style=continuous>" % (number_of_words, book_name))

        elif pick_style == 'total':
            # if pick all of the words in one book
            sql = "select * from words where words.book=%s"
            param = (book_name,)
            row_number = cur.execute(sql, param)
            Log.i(self.TAG, " select all words from the book %s" % book_name)

        else:
            Log.w(self.TAG, "Unknown pick_style %s" % pick_style)

        rows = cur.fetchall()
        for r in rows:
            # print r
            tmp_word = dict()
            tmp_word['word'] = r[0]
            tmp_word['phonetic'] = r[1]
            tmp_word['interp'] = r[2]
            tmp_word['book'] = r[3]
            tmp_word['id'] = r[4]
            tmp_word['r'] = r[5]
            tmp_word['w'] = r[6]
            words_array.append(json.dumps(tmp_word))
        # print words_array
        Log.i(self.TAG, "length" + str(len(words_array)))
        self.dao.closeAll(cur, conn)
        return words_array

    def get_row_number(self, table_name, from_word=""):
        """
        Get total number of the rows in the specific table after the 'from_word'.
        If from_word = "", which means the total number of the rows in the table.
        @param: from_word: from which word the count begins?
        @param: table_name: book_name
        """
        Log.w(self.TAG, 'from_word:' + from_word)

        if from_word == "":
            sql = "select count (*) from words where words.book=%s"
            param = (table_name,)
        else:
            sql = "select count (*) from words where words.book=%s words.word>%s"
            param = (table_name, from_word)

        conn = self.dao.getConn()
        cur = conn.cursor()
        Log.i(self.TAG, 'execute sql:' + sql)
        ####### !!! attention!!! table name cannot use as the param!
        row_number = cur.execute(sql, param)

        rows = cur.fetchall()
        # Log.i(self.TAG, 'row_number'+row_number)
        if row_number > 1:
            Log.e(self.TAG, 'Error in get number of row: multiple columns')
            # acutally here cannot return None
            return 0

        # here only 1/0 column is influenced
        r = rows[0]
        row_number = r[0]
        self.dao.closeAll(cur, conn)

        return row_number
