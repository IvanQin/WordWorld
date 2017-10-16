class User:
    userName = ""
    userEmail = ""
    password = ""
    is_Vip = 0  # 0: no VIP, 1: VIP

    def __init__(self, userName="", userEmail="", password="", is_Vip=0):
        self.userName = userName
        self.userEmail = userEmail
        self.password = password
        self.is_Vip = is_Vip

    def __del__(self):
        pass

    def getUsername(self):
        return self.userName

    def setUsername(self, username):
        self.userName = username

    def getPassword(self):
        return self.password

    def setPassword(self, password):
        self.password = password

    def getEmail(self):
        return self.userEmail

    def setEmail(self, Email):
        self.userEmail = Email

    def getVip(self):
        return self.is_Vip

    def setVip(self, Vip):
        self.is_Vip = Vip
