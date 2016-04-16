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
//        Socket socket = new Socket("localhost", LeaderServer.ClientPort);
//
//        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//
//        String line;
//        while ((line = br.readLine()) != null){
//            int oper = Integer.parseInt(line);
//            if (oper == 1){
//                System.out.println("traverse");
//                out.writeObject(new Message(MessageHeader.CLIENTTRAVERSE, null));
//            } else if (oper == 2){
//                System.out.println("Add");
//                line = br.readLine();
//                int tmp = Integer.parseInt(line);
//                out.writeObject(new Message(MessageHeader.CLIENTADD, new FileMetaData(line, line, line, line, tmp)));
//            } else if (oper == 3){
//                System.out.println("remove");
//                line = br.readLine();
//                int tmp = Integer.parseInt(line);
//                out.writeObject(new Message(MessageHeader.CLIENTREMOVE, new FileMetaData(line, line, line, line, tmp)));
//            }
//        }

        Path f = Paths.get(new File("./MetaDataServer.iml").getAbsolutePath());
        BasicFileAttributes tmp = Files.readAttributes(f, BasicFileAttributes.class);

        System.out.println(tmp.creationTime());

    }
}
