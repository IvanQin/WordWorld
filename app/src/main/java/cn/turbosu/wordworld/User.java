package cn.turbosu.wordworld;

/**
 * Created by Qinyifan on 17/1/17.
 */

public class User {
    private String userName;
    private String userEmail;
    private String password;
    private boolean isVip;

    User (){}

    User (String userEmail, String password){
        this.userEmail = userEmail;
        this.password = password;
    }

    public boolean setUserName (String userName){
        this.userName = userName;
        return true;
    }

    public boolean setUserEmail (String userEmail){
        this.userEmail = userEmail;
        return true;
    }

    public boolean setPassword (String password){
        this.password = password;
        return true;
    }

    public String getUserName (){
        return this.userName;
    }

    public String getUserEmail(){
        return this.userEmail;
    }

    public String getPassword(){
        return this.password;
    }


}
