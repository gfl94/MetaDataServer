package cn.edu.sjtu.cs.DBGroup;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by gefei on 16-4-18.
 */
public class DirectoryTree{

//    public static void main(String[] args){
//        DirectoryTree dt = new DirectoryTree();
//        System.out.println(dt.traverse());
//
//        dt.mkdir("/hello");
//        dt.createFile("/hello/world");
//        dt.createFile("/hello/world");
//        dt.mkdir("/hello/gegege");
//        dt.createFile("/hello/gefei");
//        dt.createFile("/hello/gegege/dsfsafas");
//        dt.createFile("/hello/gegege/dassss");
//
//        dt.mkdir("/kdfjdis/dfdks");
//        dt.createFile("/hello");
//        System.out.println(dt.traverse());
//
//        System.out.println("*******************************");
//        dt.traverseSingleNode("/hello/gegege");
//
//        System.out.println("Finished. Main function ends");
//    }

    // for ls command
    public void traverseSingleNode(String path){
        TreeNode p = findNode(path);
        if (p == null) return;
        StringBuilder sb = new StringBuilder();
        if (!p.metadata.isDirectory)
            sb.append(p.nodename);
        else {
            p = p.child;
            while (p != null){
                sb.append(p.nodename + "  ");
                p = p.sibling;
            }
            sb.append("\n");
        }
        output(sb.toString());
    }

    //for mkdir command
    public boolean mkdir(String path, FileMetaData data){
        return addNode(path, data);
    }

    //for create file
    public boolean createFile(String path, FileMetaData data){
        return addNode(path, data);
    }

    //for rmdir
    public boolean rmdir(String path){
        TreeNode p = findNode(path);
        if (p == null || !p.metadata.isDirectory){
            log("rmdir error: directory does not exist");
            return false;
        }
        if (p.child != null) {
            log("remove a node with children");
            return false;
        }
        return removeNode(p.parent, p.nodename);
    }

    //for rm
    public boolean removeFile(String path){
        TreeNode p = findNode(path);
        if (p == null){
            log("No such file");
            return false;
        }
        return removeNode(p.parent, p.nodename);
    }

    public FileMetaData getFileMetaData(String path){
        TreeNode p = findNode(path);
        if (p == null) return null;
        return p.metadata;
    }

    private TreeNode root;

    public DirectoryTree(){
        root = new TreeNode("/", null);
    }

    public boolean addNode(String path, FileMetaData data){
        if (findNode(path) != null){
            log("Add failure: " + path +  " node already exists");
            return false;
        }
        TreeNode p = findNode(getParentPath(path)); // find parent node
        if (p == null){
            log("Add failure: " + path +  "  Parent node does not exists");
            return false;
        }

        String nodename = getFileName(path);
        return addNode(p, nodename, data);
    }

    private boolean addNode(TreeNode parent, String nodename, FileMetaData metaData){
        if (parent == null) return false;
        TreeNode newNode = new TreeNode(nodename, metaData);
        newNode.parent = parent;
        if (parent.child == null) parent.child = newNode;
        else{
            TreeNode tmp = parent.child;
            while (tmp.sibling != null)
                tmp = tmp.sibling;
            tmp.sibling = newNode;
        }
        return true;
    }

    private boolean removeNode(TreeNode parent, String filename){
        if (parent == null || parent.child == null)
            return false;

        // find previous and current pointer to the deleted node
        TreeNode prev = parent, cur = parent.child;
        while (cur != null && !cur.nodename.equals(filename)){
            prev = cur;
            cur = cur.sibling;
        }

        if (cur == null) return false;
        // node deletion
        if (prev == parent) prev.child = cur.sibling;
        else prev.sibling = cur.sibling;

        return true;
    }


    private String[] pathConverter(String path){
        if (path.equals("/")) return null;
        return path.substring(1, path.length()).split("/");
    }

    private String getFileName(String path){
        return path.substring(path.lastIndexOf("/") + 1);
    }

    private String getParentPath(String path){
        String[] subString = pathConverter(path);
        if (subString == null || subString.length == 1) return "/";
        else
            return path.substring(0, path.lastIndexOf("/"));
    }

    private String singleNodePath(TreeNode p){
        TreeNode tmp = p;
        StringBuilder sb = new StringBuilder();
        while (tmp != null){
            sb.insert(0, tmp.nodename);
            tmp = tmp.parent;
            if (tmp != null && !tmp.nodename.equals("/"))
                sb.insert(0, "/");
        }
        return sb.toString();
    }

    public String traverse(){
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> pq = new LinkedList<>();
        pq.add(root);
        while (!pq.isEmpty()){
            TreeNode tmp = pq.poll();
            sb.append(singleNodePath(tmp));
            TreeNode p = tmp.child;
            while (p != null){
                pq.add(p);
                p = p.sibling;
            }
            if (!pq.isEmpty()) sb.append("\n");
        }
        return sb.toString();
    }

    public TreeNode findNode(String path){
        if (root == null) return null;
        String[] subString = pathConverter(path);
        if (subString == null) return root;
        TreeNode p = root.child;

        int i = 0;
        while (p != null){
            if (p.nodename.equals(subString[i])){
                if (++i == subString.length) return p;
                p = p.child;
            } else {
                p = p.sibling;
            }

        }
        return null;
    }

    private void log(String content){
        System.out.println("logger: " + content);
    }

    private void output(String content){
        System.out.println("output: " + content);
    }

    class TreeNode{
        String nodename;
        TreeNode sibling;
        TreeNode child;
        TreeNode parent;

        FileMetaData metadata;

        public TreeNode(String nodename, FileMetaData metaData){
            this.nodename = nodename;
            this.sibling = null;
            this.child = null;
            this.parent = null;
            this.metadata = metaData;
        }
    }
}
