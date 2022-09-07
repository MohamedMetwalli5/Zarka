import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Sender extends Thread {
    public void start(int port,String nodes){
        try {
            Socket sender = new Socket("127.0.0.1",port);
            String Message;
            DataInputStream inStream = new DataInputStream(sender.getInputStream());
            DataOutputStream outStream = new DataOutputStream(sender.getOutputStream());
            Message = nodes;
            outStream.writeUTF("p " + Message); // sending the client message to the server
            outStream.flush();
            inStream.close();
            outStream.close();
            sender.close();
        }catch (Exception e ) {
            System.out.println(e.getMessage());
        }

    }

    public void insert(int port,String massege ,int[] success) {
        try {
            Socket sender = new Socket("127.0.0.1",port);
            String temp;
            DataInputStream inStream = new DataInputStream(sender.getInputStream());
            DataOutputStream outStream = new DataOutputStream(sender.getOutputStream());
            temp = massege;
            outStream.writeUTF(temp); // sending the client message to the server
            outStream.flush();
            temp = inStream.readUTF(); // receiving the server message from the server
            System.out.println("response received from server: " + temp); // printing the server message
            if (temp.equals("success"))
                success[0] ++;
            inStream.close();
            outStream.close();
            sender.close();
        }catch (Exception e ) {
            System.out.println(e.getMessage());
        }
    }

    public void get(int port,String key,Child node,int quram) {
        try{
            Socket sender = new Socket("127.0.0.1",port);
            String temp;
            DataInputStream inStream = new DataInputStream(sender.getInputStream());
            DataOutputStream outStream = new DataOutputStream(sender.getOutputStream());
            temp = key;
            outStream.writeUTF(temp); // sending the client message to the server
            outStream.flush();
            temp = inStream.readUTF(); // receiving the server message from the server
            System.out.println("response received from server: " + temp); // printing the server message
            if (node.values.contains(temp)){
                int i = node.values.indexOf(temp);
                node.repeated.set(i, node.repeated.get(i) + 1);
                if(node.repeated.get(i)==quram){
                    node.max = i;
                }
            }
            else {
                node.values.add(temp);
                node.repeated.add(1);
            }
            inStream.close();
            outStream.close();
            sender.close();
        }catch (Exception e ) {
            System.out.println(e.getMessage());
        }
    }

}
