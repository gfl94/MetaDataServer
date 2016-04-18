package cn.edu.sjtu.cs.DBGroup;

import java.io.*;
import java.net.Socket;

/**
 * Created by gefei on 16-4-16.
 */
public class FollowerServerMessageHandler extends Thread{
    private Socket socket;
    private static int PORT = 9889;
    private static String SERVER_ADDRESS = "localhost";
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private FollowerServer followerServer;

    public FollowerServerMessageHandler(FollowerServer followerServer){
        this.followerServer = followerServer;
    }

    public boolean setup(){
        try{
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
//            System.out.println(System.currentTimeMillis());
            sendMessage(new Message(MessageHeader.PING));
            Message response = new Message(MessageHeader.OK);
            if (waitForMessage(response) && response.header == MessageHeader.PONG)
                System.out.println("Connected to server");
//                System.out.println(System.currentTimeMillis());
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendMessage(Message message){
        try{
            out.writeObject(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean waitForMessage(Message message){
        try{
            Object object;
            while (true){
                object = in.readObject();
                if (object == null) continue;
                else break;
            }
            Message received = (Message) object;
            message.header = received.header;
            message.metaData = received.metaData;
        }catch(IOException ioe){
            ioe.printStackTrace();
            return false;
        }catch (ClassNotFoundException cfe){
            cfe.printStackTrace();
            return false;
        }
        return true;
    }

    public Message handleMessage(Message message){
        System.out.println("[DEBUG] message type: " + message.header);
        Message response = new Message();
        switch (message.header){
            case MessageHeader.PING:
                response.header = MessageHeader.PONG;
                break;
            case MessageHeader.PONG:
                break;
            case MessageHeader.ADD:
                followerServer.persistToFollower(message.metaData);
                break;
            case MessageHeader.TRAVERSE:
                followerServer.traverse();
                break;
            case MessageHeader.REMOVE:
                followerServer.remove(message.metaData);
        }
        return response;
    }

    public void run(){
        try{
            while (true){
                Object object = in.readObject();
                if (object == null) continue;
                Message message = (Message) object;
                System.out.println("[DEBUG] message type: " + message.header);
                if (message.header != MessageHeader.OK && message.header != MessageHeader.PONG)
                    out.writeObject(handleMessage(message));
            }
        }catch (IOException e){
            System.out.println("IOE");
        }catch (ClassNotFoundException cfe){
            cfe.printStackTrace();
            System.out.println("CFE");
        }catch (ClassCastException e){
            e.printStackTrace();
            System.out.println("Cast Error");
        }
    }

    public void close(){
        if (socket != null)
            try{
                socket.close();
            }catch (IOException e){
                System.out.println("Close Error");
                e.printStackTrace();
            }
    }
}
