package cn.edu.sjtu.cs.DBGroup;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public static final String HostAddress = "localhost";

    private HashMap<String, int[]> metaStorage;
    private DirectoryTree dt = new DirectoryTree();

    public void setNumberOfFollower(int number){
        this.numberOfFollower = number;
    }

    public static void main(String[] args){
        LeaderServer leader = new LeaderServer();
        leader.setNumberOfFollower(3);
        leader.setup();

        System.out.println("setup finished");
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
                    while(true){  //TODO can be optimized
                        Object object = in.readObject();
                        Message message = (Message) object;
                        if (message.header == MessageHeader.CLIENT_CREATE_FILE){
                            if (!addElement(message.content, false))
                                out.writeObject(new Message(MessageHeader.CLIENT_CREATE_FILE_FAILURE));
                            else
                                out.writeObject(new Message(MessageHeader.OK));
                        } else if (message.header == MessageHeader.CLIENT_MAKE_DIRECTORY){
                            if (!addElement(message.content, true))
                                out.writeObject(new Message(MessageHeader.CLIENT_MAKE_DIRECTORY_FAILURE));
                            else
                                out.writeObject(new Message(MessageHeader.OK));
                        } else if (message.header == MessageHeader.CLIENT_TRAVERSE){
                            String response = travese();
                            out.writeObject(new Message(MessageHeader.CLIENT_TRAVERSE_RESPONSE, response));
                        } else if (message.header == MessageHeader.CLIENT_REMOVE_DIRECTORY){
                            if (!removeElement(message.content, true))
                                out.writeObject(new Message(MessageHeader.CLIENT_REMOVE_DIRECTORY_FAILURE));
                            else
                                out.writeObject(new Message(MessageHeader.OK));
                        } else if (message.header == MessageHeader.CLIENT_REMOVE_FILE){
                            if (!removeElement(message.content, false))
                                out.writeObject(new Message(MessageHeader.CLIENT_REMOVE_FILE_FAILURE));
                            else
                                out.writeObject(new Message(MessageHeader.OK));
                        } else if (message.header == MessageHeader.CLIENT_LS){
                            String response = ls_oper(message.content);
                            if (response != null)
                                out.writeObject(new Message(MessageHeader.CLIENT_LS_RESPONSE, response));
                            else
                                out.writeObject(new Message(MessageHeader.BAD));
                        } else if (message.header == MessageHeader.CLIENT_META_DIST){
                            out.writeObject(new Message(MessageHeader.CLIENT_META_DIST, printMetaInfoDist()));
                        } else if (message.header == MessageHeader.CLIENT_META_REQUEST){
                            out.writeObject(new Message(MessageHeader.CLIENT_META_DIST_RESPONSE, printMetaInfo(message.content)));
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

    public synchronized boolean addElement(String filename, boolean isDirectory){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        FileMetaData data =new FileMetaData(filename, isDirectory, dateFormat.format(new Date()));

        boolean result;
        if (isDirectory)
            result = dt.mkdir(data.filename, data);
        else
            result = dt.createFile(data.filename, data);
        if (!result) return false;

        int[] positions = pickFollowerToStore();
        for (int i = 0; i < Replicants; ++i){
            leaderHandler.sendMessage(new Message(MessageHeader.ADD, data), positions[i]);
        }
        metaStorage.put(data.filename, positions);
        return true;
    }

    public synchronized boolean removeElement(String path, boolean isDirectory){
        if (!metaStorage.containsKey(path)){
            return false;
        }

        FileMetaData data = dt.getFileMetaData(path);
        if (data == null) return false;

        boolean result;
        if (isDirectory) result = dt.rmdir(path);
        else result = dt.removeFile(path);
        if (!result) return false;

        int [] pos = metaStorage.get(path);
        for (int i : pos){
            leaderHandler.sendMessage(new Message(MessageHeader.REMOVE, data), i);
        }
        metaStorage.remove(path);
        return true;
    }

    public String ls_oper(String path){
        return dt.traverseSingleNode(path);
    }

    public String travese(){
//        return new Message(MessageHeader.CLIENT_TRAVERSE_RESPONSE, dt.traverse());
        return dt.traverse();
    }

    public String printMetaInfoDist(){
        StringBuilder sb = new StringBuilder();
        for (String key : metaStorage.keySet()){
            sb.append(key + "  ");
            int [] tmp = metaStorage.get(key);
            for (int i : tmp)
                sb.append(i + "  ");
        }
        return sb.toString();
    }

    public FileMetaData printMetaInfo(String path){
        return dt.getFileMetaData(path);
    }
}
