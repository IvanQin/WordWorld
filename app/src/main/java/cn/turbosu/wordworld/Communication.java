package cn.turbosu.wordworld;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import cn.turbosu.wordworld.HttpTransport;

/**
 * Created by Qinyifan on 17/1/13.
 */

public class Communication extends HttpTransport{
    final static String LOG_TAG = "Communication";
    final static String IP_ADDR = "115.159.98.92";
    final static int port = 8086;

    Communication(){
        super(IP_ADDR, port);
    }

    public ArrayList<Word> getWords(GetWordTemplate getWordTemplate){
        String param = getWordTemplate.parseParam();
        param += "&action=getWords";
        String contentAll = get(IP_ADDR,param);
        // every line is a word in json format
        String[] content = contentAll.split("\n");
        ArrayList<Word> wordsAll = new ArrayList<Word>();
        for (String wordString : content){
            //json to Word format
            JSONParser jsonParser= new JSONParser();
            try {
                JSONObject wordJSON = (JSONObject) jsonParser.parse(wordString);
                Word word = new Word((String)wordJSON.get("EN"),(String)wordJSON.get("CN"));
                wordsAll.add(word);
            }catch (ParseException e){
                Log.e(Communication.LOG_TAG,"Cannot parse the word from JSON format!");
            }
        }
        return wordsAll;
    }
}
