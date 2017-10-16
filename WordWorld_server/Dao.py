import MySQLdb


class Dao:
    # DRIVER = "com.mysql.jdbc.Driver"
    # URL = "jdbc:mysql://115.159.98.92:3306/wordworld"
    DBNAME = "db02"
    USER = "root"
    PWD = "ubuntu"
    HOST = "10.105.125.132"

    def getConn(self):
        conn = MySQLdb.connect(host=self.HOST, user=self.USER, passwd=self.PWD, db=self.DBNAME, charset='utf8')
        return conn

    def closeAll(self, cursor, conn):
        cursor.close()
        conn.close()
