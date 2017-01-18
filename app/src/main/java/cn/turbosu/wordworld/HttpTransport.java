package cn.turbosu.wordworld;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Qinyifan on 17/1/12.
 */

public class HttpTransport {
    final static String LOG_TAG = "HTTPTransport";
    String IP_ADDR = "";
    int port = 8086;

    HttpTransport(String IP_ADDR,int port){
        this.IP_ADDR = IP_ADDR;
        this.port = port;
    }

    /**
     * Handle HTTP GET method
     * @param url request url
     * @param param request param in (var1=value1&var2=value2...varn=valuen)
     * @return HTTP response body in String
     */
    protected String get(String url, String param){
        BufferedReader bufferedReader;
        DataInputStream datainputStream = null;
        String ret = null;
        try{
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            datainputStream = new DataInputStream(connection.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(datainputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine())!=null){
                content.append(line);
            }
            ret = content.toString();
        }catch (IOException e){
            Log.e(HttpTransport.LOG_TAG,"IOException when open connection in GET");
            e.printStackTrace();
        }
        finally {
            try{
                if (datainputStream != null) {
                    datainputStream.close();
                }
            }catch (IOException e2){
                Log.e(HttpTransport.LOG_TAG,"IOException when closing the output buffer of GET");
                e2.printStackTrace();
            }

        }
        return ret;
    }

    /**
     * HTTP POST method
     * @param url request url
     * @param param param in (var1=value1&var2=value2...varn=valuen)
     * @return HTTP response body in String
     */
    protected String post(String url, String param){
        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        String ret = null;

        try{
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)realUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            dataOutputStream = new DataOutputStream(connection.getOutputStream());
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(dataOutputStream));
            bufferedWriter.write(param);
            bufferedWriter.flush();

            dataInputStream = new DataInputStream(connection.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine())!= null){
                content.append(line);
            }
            ret = content.toString();

        }catch (MalformedURLException e){
            Log.e(HttpTransport.LOG_TAG,"Invalid POST URL!");

        }catch (IOException e2){
            Log.e(HttpTransport.LOG_TAG,"IOException when open connection in POST");
        }
        finally {
            try{
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (dataOutputStream != null){
                    dataOutputStream.close();
                }
            }catch (IOException e3){

            }
        }
        return ret;
    }
}
