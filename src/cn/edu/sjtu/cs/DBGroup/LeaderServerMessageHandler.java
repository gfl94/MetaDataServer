package cn.edu.sjtu.cs.DBGroup;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by gefei on 16-4-16.
 */
public class LeaderServerMessageHandler {

    private ServerSocket listener;
    private static int PORT = 9889;
    private boolean leaderServerIsAlive = false;
    private Vector<Handler> handlers_vector = null;
    private Handler[] handlers = null;
    public int numberOfFollower;

    public boolean setup(){
        try{
            listener = new ServerSocket(PORT);
        }catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("Error creating ServerSocket");
            return false;
        }
        leaderServerIsAlive = true;
        return true;
    }

    public void close(){
        if (listener != null)
            try{
                listener.close();
                for (int i = 0; i < numberOfFollower; ++i)
                    handlers[i].close();

            }catch (IOException e){
                System.out.println("close error");
                e.printStackTrace();
            }
    }

    public boolean waitForConnection(int number){
        if (!leaderServerIsAlive) return false;

        numberOfFollower = number;
        handlers_vector = new Vector<>();

        int current = 0;
        try{
            while (current < number) {
                Handler handler = new Handler(listener.accept());
                handlers_vector.add(handler);
                handler.start();
                System.out.println("Socket " + current + " started");
                current++;
            }
        }catch (IOException ioe){
            System.out.println("Error creating listener thread");
        }
        handlers = new Handler[numberOfFollower];
        for (int i = 0; i < numberOfFollower; ++i)
            handlers[i] = handlers_vector.elementAt(i);
        return true;
    }

    public Message sendMessage(Message message, int number){
        if (number >= numberOfFollower) return new Message(MessageHeader.BAD);
        System.out.println("[DEBUG] to " + number + " " +message.header);
        Handler handler = handlers[number];
        handler.sendMessage(message);
//        Message response = new Message();
//        handler.waitForMessage(response);
        return new Message(MessageHeader.OK);
    }



    public Message handleMessage(Message message){
        Message response = new Message();
        switch (message.header){
            case MessageHeader.PING:
                response.header = MessageHeader.PONG;
                break;
            case MessageHeader.PONG:
                break;
        }
        response.header = MessageHeader.PONG;
        return response;
    }

    protected class Handler extends Thread{
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public void close(){
            if (socket != null){
                try{
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        public boolean sendMessage(Message message){
            try{
                out.writeObject(message);
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
            return true;
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

        public Handler(Socket socket){
            System.out.println("Handler created");
            this.socket = socket;
            try{
                in = new ObjectInputStream(this.socket.getInputStream());
                out = new ObjectOutputStream(this.socket.getOutputStream());
            }catch(IOException e){
                System.out.println("Error creating input & output streaming");
            }
        }

        public void run(){
            try{
                while (true){
                    Object object = in.readObject();
                    if (object == null) continue;
                    Message message = (Message) object;
                    if (message.header == MessageHeader.PING)
                        out.writeObject(new Message(MessageHeader.PONG));
                    out.writeObject(handleMessage(message));
                }
            } catch (IOException ioe){
                System.out.println("Handler error.");
                ioe.printStackTrace();
            } catch (ClassNotFoundException cfe){
                System.out.println("Object not found.");
            }
        }
    }
}
