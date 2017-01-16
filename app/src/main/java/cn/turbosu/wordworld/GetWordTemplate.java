package cn.turbosu.wordworld;

/**
 * Created by Qinyifan on 17/1/13.
 */

public class GetWordTemplate {
    private String userId;
    private int numberOfWords;
    private String wordListId;

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setNumberOfWords(int numberOfWords){
        this.numberOfWords = numberOfWords;
    }

    public void setWordListId(String wordListId){
        this.wordListId = wordListId;
    }

    public String getUserId (){
        return this.userId;
    }
    public int getNumberOfWords(){
        return this.numberOfWords;
    }
    public String getWordListId(){
        return this.wordListId;
    }
    public String parseParam() {
        String param = "" +
                "userId=" + this.userId + "&" +
                "numberOfWords=" + this.numberOfWords + "&" +
                "wordListId=" + this.wordListId;
        return param;
    }

}
