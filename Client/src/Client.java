import java.net.*;
import java.io.*;
import java.util.Random;

public class Client {
    public static void main(String[] args)  {
        String[] port;
        String key,value;
        String configFilePath = "D:\\Config.txt";
        Random rand = new Random();
        try{
            Socket socket;
            DataInputStream inStream;
            DataOutputStream outStream;

            BufferedReader br;
            String clientMessage = "", serverMessage; // to store the client message and the server message respectively
            while(!clientMessage.equals("bye")){
                port = ReadFile.read(configFilePath).split(","); // reading the ports again in case there was a new added server (new node)

                socket = new Socket("127.0.0.1",Integer.parseInt(port[rand.nextInt(port.length)]));
                inStream = new DataInputStream(socket.getInputStream());
                outStream = new DataOutputStream(socket.getOutputStream());
                br = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Enter : \n 1 --> for get()\n 2 --> for add()  ");
                System.out.print(">> ");
                clientMessage = br.readLine(); // reading the client message

                if(clientMessage.equals("1")){
                    System.out.print("Enter the key >> ");
                    clientMessage = "c get " +  br.readLine();
                    //operation.trim()+ " -> " +
                }else if(clientMessage.equals("2")){
                    System.out.print("Enter the key >> ");
                    key = br.readLine();
                    System.out.print("Enter the value >> ");
                    value = br.readLine();
                    clientMessage = "c " + "insert " + key.trim() + " " + value.trim(); // to send the key and value in one reqeust
                }else{
                    System.out.print("Please, enter a valid input.");
                    continue;
                }
                outStream.writeUTF(clientMessage); // sending the client message to the server
                outStream.flush();
                serverMessage = inStream.readUTF(); // receiving the server message from the server
                System.out.println("Message received from server: " + serverMessage); // printing the server message
                inStream.close();
                outStream.close();
                socket.close();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}