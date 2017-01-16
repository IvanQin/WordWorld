package cn.turbosu.wordworld;

/**
 * Created by TurboSu on 16/11/30.
 */
public class Word{

    String english;
    String chinese;
    public Word(String english,String chinese){
        this.chinese=chinese;
        this.english=english;
    }
    public String getEnglish(){
        return english;
    }
    public String getChinese(){
        return chinese;
    }
}