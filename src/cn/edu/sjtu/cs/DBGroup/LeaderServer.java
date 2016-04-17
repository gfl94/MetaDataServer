package cn.edu.sjtu.cs.DBGroup;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by gefei on 16-4-16.
 */
public class LeaderServer {
    public static final int Replicants = 2;
    private LeaderServerMessageHandler leaderHandler;
    private int numberOfFollower;
    private int [] currentInfosInFollower;
    private int current;

    public static final int ClientPort = 5566;

    private HashMap<String, int[]> metaStorage;

    public void setNumberOfFollower(int number){
        this.numberOfFollower = number;
    }

    private class ClientListener extends Thread{
        private ServerSocket listener = null;
        public ClientListener(int port){
            try{
                listener = new ServerSocket(port);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            try {
                while (true) {
                    new ClientHandler(listener.accept()).start();
                    System.out.println("A client connected");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        class ClientHandler extends Thread{
            private Socket socket;
            private ObjectInputStream in;
            private ObjectOutputStream out;


            public ClientHandler(Socket socket){
                this.socket = socket;
                try{
                    in = new ObjectInputStream(this.socket.getInputStream());
                    out = new ObjectOutputStream(this.socket.getOutputStream());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            public void run(){
                try{
                    while(true){
                        Object object = in.readObject();
                        Message message = (Message) object;
                        if (message.header == MessageHeader.CLIENTADD){
                            addElement(message.metaData);
                        } else if (message.header == MessageHeader.CLIENTTRAVERSE){
                            Message responese = travese();
                            out.writeObject(responese);
                        } else if (message.header == MessageHeader.CLIENTREMOVE){
                            removeElement(message.metaData);
                        }
                    }
                }catch (ClassNotFoundException e){
                    e.printStackTrace();
                }catch (ClassCastException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

    }

    public void setup(){
        leaderHandler = new LeaderServerMessageHandler();
        leaderHandler.setup();
        leaderHandler.waitForConnection(numberOfFollower);
        currentInfosInFollower = new int[numberOfFollower];
        current = 0;
        metaStorage = new HashMap<String, int[]>();

        new ClientListener(ClientPort).start();
    }

    public void close(){
        leaderHandler.close();
    }

    private int[] pickFollowerToStore(){
        int[] result = new int[Replicants];
        for (int i = 0; i < Replicants; ++i){
            result[i] = current;
            current++;
            if (current >= numberOfFollower) current -= numberOfFollower;
        }
        return result;
    }

    public synchronized boolean addElement(FileMetaData data){
        int[] positions = pickFollowerToStore();
        for (int i = 0; i < Replicants; ++i){
            leaderHandler.sendMessage(new Message(MessageHeader.ADD, data), positions[i]);
        }
        System.out.print("Position: ");
        for (int i = 0; i < Replicants; ++i){
            System.out.print(positions[i] + "   ");
        }
        System.out.println();
        metaStorage.put(data.filename, positions);
        return true;
    }

    public synchronized boolean removeElement(FileMetaData data){
        if (!metaStorage.containsKey(data.filename)){
            return false;
        }
        int [] pos = metaStorage.get(data.filename);
        for (int i : pos){
            leaderHandler.sendMessage(new Message(MessageHeader.REMOVE, data), i);
        }
        metaStorage.remove(data.filename);
        return true;
    }

    public Message travese(){
        StringBuilder sb = new StringBuilder();
        for (String key : metaStorage.keySet()){
            sb.append(key + "  ");
            int [] tmp = metaStorage.get(key);
            for (int i : tmp){
                sb.append(i + " ");
            }
            sb.append("\n");
        }
        for (int i = 0; i < numberOfFollower; ++i)
            leaderHandler.sendMessage(new Message(MessageHeader.TRAVERSE), i);
        return new Message(MessageHeader.CLIENTTRAVERSERESPONSE, sb.toString());
    }
}
