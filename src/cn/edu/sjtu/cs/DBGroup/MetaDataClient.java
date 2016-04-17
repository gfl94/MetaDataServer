package cn.edu.sjtu.cs.DBGroup;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

/**
 * Created by gefei on 16-4-16.
 */
public class MetaDataClient {
    public static void main(String[] args) throws IOException{
        Socket socket = new Socket("localhost", LeaderServer.ClientPort);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = br.readLine()) != null){
            int oper = Integer.parseInt(line);
            String filename;
            FileMetaData data;

            if (oper == 1){
                System.out.println("traverse");
                out.writeObject(new Message(MessageHeader.CLIENTTRAVERSE));
                boolean received = false;
                while (!received){
                    try {
                        Object object = in.readObject();
                        Message m = (Message) object;
                        if (m.header == MessageHeader.CLIENTTRAVERSERESPONSE){
                            System.out.println(m.response);
                            received = true;
                        }
                    } catch (ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
                continue;
            }

            // Other operations
            while (true) {
                System.out.print("Enter filename: ");
                filename = br.readLine();
                try {
                    data = new FileMetaData(filename);
                    break;
                } catch (NoSuchFieldError e) {
                    e.printStackTrace();
                }
            }

            if (oper == 2){
                System.out.println("Add");
                out.writeObject(new Message(MessageHeader.CLIENTADD, data));
            } else if (oper == 3){
                System.out.println("remove");
                out.writeObject(new Message(MessageHeader.CLIENTREMOVE, data));
            }
        }

    }
}
