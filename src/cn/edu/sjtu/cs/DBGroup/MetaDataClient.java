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

    public void handleMessage(Message message){
        if (message.header == MessageHeader.OK)
            log("Operation finished");
        else if (message.header <= 24 && message.header >= 21) // an ugly hacker
            log(MessageHeader.CLIENT_RESPONSE[message.header - MessageHeader.CLIENT_RESPONSE_BASE]);
    }

    private void output(String content){
        System.out.println("STDOUT: " + content);
    }

    private void log(String content){
        System.out.println("STDERR: " + content);
    }

    public static void main(String[] args) throws IOException{
        MetaDataClient client = new MetaDataClient(LeaderServer.HostAddress, LeaderServer.ClientPort);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = br.readLine()) != null){
            int oper = Integer.parseInt(line);
            String filename;
            FileMetaData data;

            if (oper == 1){
                System.out.println("traverse: ");
                client.sendMessage(new Message(MessageHeader.CLIENT_TRAVERSE));
                Message response = client.waitForMessage();
                System.out.println(response.content);
                continue;
            }

            // Other operations
            System.out.print("Enter filename: ");
            filename = br.readLine();

            if (oper == 2){
                System.out.println("Add Directory");
                client.sendMessage(new Message(MessageHeader.CLIENT_MAKE_DIRECTORY, filename));
                client.handleMessage(client.waitForMessage());
            } else if (oper == 3){
                System.out.println("Add File");
                client.sendMessage(new Message(MessageHeader.CLIENT_CREATE_FILE, filename));
                client.handleMessage(client.waitForMessage());
            } else if (oper == 4){
                System.out.println("remove Directory");
                client.sendMessage(new Message(MessageHeader.CLIENT_REMOVE_DIRECTORY, filename));
                client.handleMessage(client.waitForMessage());
            } else if (oper == 5){
                System.out.println("remove file");
                client.sendMessage(new Message(MessageHeader.CLIENT_REMOVE_FILE, filename));
                client.handleMessage(client.waitForMessage());
            }
        }

    }
}
