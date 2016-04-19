package cn.edu.sjtu.cs.DBGroup;

import java.io.*;
import java.net.Socket;

/**
 * Created by gefei on 16-4-16.
 */
public class MetaDataClient {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public MetaDataClient(String host, int port){
        try{
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message){
        try{
            out.writeObject(message);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Message waitForMessage(){
        while (true){
            try {
                Object object = in.readObject();
                if (object != null) {
                    Message m = (Message) object;
                    return m;
                }
            }catch(IOException ioe){
                ioe.printStackTrace();
            }catch (ClassNotFoundException cfe){
                cfe.printStackTrace();
            }
        }
    }

    public boolean handleMessage(Message message){
        if (message.header == MessageHeader.OK){
            log("Operation finished");
            return true;
        }
        else if (message.header <= 24 && message.header >= 21) // an ugly hacker{
            output(MessageHeader.CLIENT_RESPONSE[message.header - MessageHeader.CLIENT_RESPONSE_BASE]);
        else if (message.header == MessageHeader.CLIENT_LS_RESPONSE){
            output(message.content);
        }
        return false;
    }

    private void output(String content){
        System.out.println(content);
    }

    private void log(String content){
        System.out.println("STDERR: " + content);
    }

    public void traverse(){
        System.out.println("traverse: ");
        sendMessage(new Message(MessageHeader.CLIENT_TRAVERSE));
        Message response = waitForMessage();
        System.out.println(response.content);
    }

    public void printMetaDist(){
        System.out.println("printMetaDist: ");
        sendMessage(new Message(MessageHeader.CLIENT_META_DIST));
        Message response = waitForMessage();
        System.out.println(response.content);
    }

    public boolean mkdir(String dirPath){
        System.out.println("Add Directory");
        sendMessage(new Message(MessageHeader.CLIENT_MAKE_DIRECTORY, dirPath));
        return handleMessage(waitForMessage());
    }

    public boolean createFile(String filePath){
        System.out.println("Add File");
        sendMessage(new Message(MessageHeader.CLIENT_CREATE_FILE, filePath));
        return handleMessage(waitForMessage());
    }

    public boolean rmdir(String dirPath){
        System.out.println("remove Directory");
        sendMessage(new Message(MessageHeader.CLIENT_REMOVE_DIRECTORY, dirPath));
        return handleMessage(waitForMessage());
    }

    public boolean rm(String filePath){
        System.out.println("remove file");
        sendMessage(new Message(MessageHeader.CLIENT_REMOVE_FILE, filePath));
        return handleMessage(waitForMessage());
    }

    public boolean ls(String path){
        System.out.println("ls command");
        sendMessage(new Message(MessageHeader.CLIENT_LS, path));
        return handleMessage(waitForMessage());
    }

//    public static final String[] commands = {"mkdir", "touch", "rmdir", "rm", "ls"};

    public void process(String[] commands){
        String command = commands[0];
        String path = "/";
        if (commands.length >= 2)
            path = commands[1];
        System.out.println("process: " + command);
        if (command.equals("mkdir"))
            mkdir(path);
        else if (command.equals("touch"))
            createFile(path);
        else if (command.equals("rmdir"))
            rmdir(path);
        else if (command.equals("rm"))
            rm(path);
        else if (command.equals("ls"))
            ls(path);
        else if (command.equals("traverse"))
            traverse();
        else if (command.equals("metadist"))
            printMetaDist();
        else{
            System.out.println("Command not recognised");
        }

    }

    public static void main(String[] args) throws IOException{
        MetaDataClient client = new MetaDataClient(LeaderServer.HostAddress, LeaderServer.ClientPort);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            String line = br.readLine();
            String[] commands = line.split(" ");

            if (commands[0].trim().equals("exit")){
                System.out.println("Good-Bye");
                break;
            }
            client.process(commands);
        }

    }
}
