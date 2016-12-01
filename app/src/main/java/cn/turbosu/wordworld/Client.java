package cn.turbosu.wordworld;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Client {
	    public static final String IP_ADDR = "115.159.98.92";
	    public static final int PORT = 8080;
	    String name;
	    Transport trans;
	    public Client(String name){
	    	this.name=name;
	    	trans=new Transport(IP_ADDR,PORT);
	    }
	    public Word getWord(){
			Word ans = null;
			try {
				JSONObject obj = new JSONObject();
				obj.put("type", "get");
				obj.put("name", name);
				String msg = obj.toJSONString();
				String rec = trans.sendAndReceive(msg);
				JSONParser parser = new JSONParser();
				JSONObject objRec = (JSONObject)parser.parse(rec);
				ans = new Word((String)objRec.get("EN"),(String)objRec.get("CN"));
			}catch (Exception e){
				e.printStackTrace();
			}
           	return ans;
	    }

}    
	    

