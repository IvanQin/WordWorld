package cn.turbosu.wordworld;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Transport {

	String IP_ADDR = "115.159.98.92";
	int PORT = 8086;

	    public Transport(String ip, int port){
	    	IP_ADDR=ip;
	    	PORT=port;
		}
	    
	   
	    public String sendAndReceive(String msg) {

            Socket socket = null;
            String ret="";
            try {  
            	
                byte[] bs = msg.getBytes("utf-8");
            	socket = new Socket(IP_ADDR, PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                output.write(bs);
                byte[] rs0=new byte[10000];
    			int k=input.read(rs0);
				socket.close();
                output.close();
                input.close();

    			if(k<=0){
   					ret="error";
   				}
    			else{
	    			byte[] rs=new byte[k];
	    			for(int i=0;i<k;i++)rs[i]=rs0[i];
	    			ret=new String(rs,"UTF-8");
	    			}


	            } catch (Exception e) {
	                System.out.println(":" + e.getMessage());
	                ret="error";

	                return ret;
	            } 
	            return ret;

	        }       
	}    

