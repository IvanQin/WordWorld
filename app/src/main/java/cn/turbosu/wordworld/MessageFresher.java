package cn.turbosu.wordworld;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TurboSu on 16/6/26.
 */

public class MessageFresher {

    private static Client client;

    public static void start(final TexObject englishWord, final TexObject chineseWord){
        final Client client = new Client("sss");

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(Flags.needNewWorld){
                        Flags.needNewWorld = false;
                        //Word word = client.getWord();
                        Word word = WordList.getNext();
                        englishWord.setTexture(BitmapBuilder.getBitmap(word.getEnglish()));
                        chineseWord.setTexture(BitmapBuilder.getBitmap(word.getChinese()));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

        };
        //handler.postDelayed(runnable, 2000);
        new Thread(runnable).start();

    }

}
