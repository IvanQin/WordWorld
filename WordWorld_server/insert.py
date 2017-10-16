# coding=utf-8
from Dao import *

dao = Dao()
conn = dao.getConn()
cur = conn.cursor()
f = open('WORD1.csv')
for line in f.readlines():
    word, english, inter, book, id = str(line).split(',')
    english = str(english).strip('"')
    inter = str(inter).strip('"')
    sql = 'insert into words value(%s,%s,%s,%s,%s)'
    param = (word, english, inter, book, id)
    cur.execute(sql, param)
f.close()
