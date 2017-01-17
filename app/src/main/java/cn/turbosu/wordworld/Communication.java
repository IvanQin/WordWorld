package cn.turbosu.wordworld;

import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;

import cn.turbosu.wordworld.HttpTransport;

/**
 * Created by Qinyifan on 17/1/13.
 */

public class Communication extends HttpTransport {
    final static String LOG_TAG = "Communication";
    final static String IP_ADDR = "115.159.98.92";
    final static int port = 8086;

    Communication() {
        super(IP_ADDR, port);
    }

    /**
     * Get words (a bunch of words from the server as the cache words)
     * @param getWordTemplate the necessary info for get Words
     * @return words in ArrayList format
     */
    public ArrayList<Word> getWords(GetWordTemplate getWordTemplate) {
        String param = getWordTemplate.parseParam();
        param += "&action=getWords";
        String contentAll = get(IP_ADDR, param);
        // every line is a word in json format
        String[] content = contentAll.split("\n");
        ArrayList<Word> wordsAll = new ArrayList<Word>();
        for (String wordString : content) {
            //json to Word format
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject wordJSON = (JSONObject) jsonParser.parse(wordString);
                Word word = new Word((String) wordJSON.get("EN"), (String) wordJSON.get("CN"));
                wordsAll.add(word);
            } catch (ParseException e) {
                Log.e(Communication.LOG_TAG, "Cannot parse the word from JSON format!");
            }
        }
        return wordsAll;
    }

    /**
     * User authentication.
     * @param user User info
     * @return isValid of invalid user
     */
    public boolean userAuthentication(User user) {
        boolean isValid = false;
        StringBuilder param_tmp = new StringBuilder();
        String contentAll;

        param_tmp.append("action=userAuthentication");
        param_tmp.append("&userEmail=" + user.getUserEmail());
        param_tmp.append("&password=" + user.getPassword());
        String param = param_tmp.toString();

        contentAll = post(IP_ADDR, param);  // use post

        String[] content = contentAll.split("\n");
        HashMap<String,String> info = new HashMap<>(); // return info in HaspMap format <Key, Value>
        for (String item : content) {
            // Here, every line should be in key=value format instead of json format
            String[] info_piece = item.split("=");
            info.put(info_piece[0], info_piece[1]);
        }

        if ("true".equals(info.get("isValid"))){
            isValid = true;
        }
        else {
            isValid = false;
        }
        return isValid;
    }

    public boolean userRegistration(User user){
        return true;
    }


}
