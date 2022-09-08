import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


class ServerClientThread extends Thread {
    Socket serverClient;
    int clientNo;
    int RF ;
    List<Hashtable<String,String>> mem;
    int[] no_Elements;
    List<PortRange> unsortedPortRangeList;
    List<String> ports;
    int offsetValue = 200, fullRangeOfHashing = 1001; // we decided them
    SST lsm;

    String Shared = "D:\\ServerClientSharedFile.txt";
    String Config = "D:\\Config.txt";
    int port;
    ServerClientThread(Socket inSocket, int counter
            , List<Hashtable<String,String>> mem, int[] no_Elements, List<String> ports, List<PortRange> unsortedPortRangeList, int RF, int port, SST lsm){
        serverClient = inSocket;
        clientNo = counter;
        this.mem = mem;
        this.no_Elements = no_Elements;
        this.ports = ports;
        this.unsortedPortRangeList = unsortedPortRangeList;
        this.RF = RF;
        this.port = port;
        this.lsm = lsm;
    }
    PortRange newAddedServer = new PortRange();
    public void run(){
        int hashedKey ;
        try{
            DataInputStream inStream = new DataInputStream(serverClient.getInputStream());
            DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());
            String clientMessage;
            while(true){
                clientMessage=inStream.readUTF();
                if (clientMessage.equals("bye")){
                    break;
                }
                System.out.println("Message received from client " + clientNo + ": "+ clientMessage);
                String[] temp = clientMessage.trim().split(" ");
                if (temp[0].equals("p")){
                    if (temp[1].equals("insert")) {
                        mem.get(Integer.parseInt(temp[4])).put(temp[2], temp[3]);
                        System.out.println("inserting");
                        no_Elements[0] ++;
                        if (no_Elements[0] == 100){
                            lsm.addToLsm(mem);
                        }
                        outStream.writeUTF("success"); // sending the server message
                        outStream.flush();
                        break;
                    }
                    else if(temp[1].equals("get")){
                        String res = mem.get(Integer.parseInt(temp[3])).get(temp[2]);
                        if (res == null) {
                            res = lsm.get(temp[2]);
                        }
                        outStream.writeUTF(res); // sending the server message
                        outStream.flush();
                        break;
                    }
                    else {
                        String[] x = temp[2].split(",");
                        PortRange newAddedServervn1 = new PortRange(Integer.parseInt(temp[1]), 0,Integer.parseInt(x[0]),1);
                        newAddedServer.RebalanceRanges(newAddedServervn1, unsortedPortRangeList,this.mem,this.port);
                        PortRange newAddedServervn2 = new PortRange(Integer.parseInt(temp[1]), 0,Integer.parseInt(x[1]),2);
                        newAddedServer.RebalanceRanges(newAddedServervn2, unsortedPortRangeList,this.mem,this.port);
                        PortRange newAddedServervn3 = new PortRange(Integer.parseInt(temp[1]), 0,Integer.parseInt(x[2]),3);
                        newAddedServer.RebalanceRanges(newAddedServervn3, unsortedPortRangeList,this.mem,this.port);
                        break;
                    }
                } else {
                    ArrayList<PortAndVirtualNodeObject> PortsAndVirtualNodes ; // to save the port numbers and virtual nodes numbers where the value is stored
                    Sender sender = new Sender();
                    if (temp[1].equals("insert")) {
                        hashedKey = Math.abs(temp[2].hashCode() % 1001);
                        SharedFileHelper sfr = new SharedFileHelper(hashedKey, Shared);
                        String massege = "p insert " + temp[2] + " " + temp[3] + " ";
                        int[] success = {0};
                        int QW = Integer.parseInt(ReadFile.read(Config, 0, "QW"));
                        PortsAndVirtualNodes = sfr.InsertPortAndVirtualNodesNumbers(RF, fullRangeOfHashing, offsetValue); // that suits the hashed key to be stored in
                        for (PortAndVirtualNodeObject x : PortsAndVirtualNodes) {
                            System.out.println("in " + x.portNumber + " with virtual node = " + x.virtualNodeNumber);
                            if (x.portNumber == port){
                                mem.get(x.virtualNodeNumber).put(temp[2], temp[3]);
                                no_Elements[0] ++;
                                if (no_Elements[0] == 100){
                                    lsm.addToLsm(mem);
                                }
                                success[0]++;
                                continue;
                            }
                            sender.insert(x.portNumber, massege + x.virtualNodeNumber, success);
                        }
                        long startTime = System.currentTimeMillis(); //fetch starting time
                        while (success[0] < QW && (System.currentTimeMillis()-startTime) < 20000) {}
                        if (success[0]>=QW) {
                            System.out.print("Message to client " + clientNo + ": Success");
                            outStream.writeUTF("Success"); // sending the server message
                        }
                        else{
                            System.out.print("Message to client " + clientNo + ": Faild");
                            outStream.writeUTF("Faild"); // sending the server message
                        }
                        outStream.flush();
                        break;
                    }
                    else if (temp[1].equals("get")){
                        hashedKey = Math.abs(temp[2].hashCode() % 1001);
                        Child child = new Child();
                        SharedFileHelper sfr = new SharedFileHelper(hashedKey, Shared);
                        String massege = "p get " + temp[2] + " " ;
                        int QR = Integer.parseInt(ReadFile.read(Config, 0, "QR"));
                        PortsAndVirtualNodes = sfr.InsertPortAndVirtualNodesNumbers(RF, fullRangeOfHashing, offsetValue); // that suits the hashed key to be stored in
                        for (PortAndVirtualNodeObject x : PortsAndVirtualNodes) {
                            System.out.println("in " + x.portNumber + " with virtual node = " + x.virtualNodeNumber);
                            if(x.portNumber == port){
                                String res = mem.get(x.virtualNodeNumber).get(temp[2]);
                                if (res == null){
                                    res = lsm.get(temp[2]);
                                }
                                if (child.values.contains(res)){
                                    int i = child.values.indexOf(res);
                                    child.repeated.set(i, child.repeated.get(i) + 1);
                                    if(child.repeated.get(i)==QR){
                                        child.max = i;
                                    }
                                }
                                else {
                                    child.values.add(res);
                                    child.repeated.add(1);
                                }
                                continue;
                            }

                            sender.get(x.portNumber, massege + x.virtualNodeNumber ,child ,QR);
                        }
                        long startTime = System.currentTimeMillis(); //fetch starting time
                        while (child.max == -1 && (System.currentTimeMillis()-startTime) < 20000){}
                        if(child.max!=-1) {
                            System.out.print("Message to client " + clientNo + ": " + child.values.get(child.max));
                            outStream.writeUTF(child.values.get(child.max)); // sending the server message
                        }
                        else {
                            System.out.print("Message to client " + clientNo + ": not found");
                            outStream.writeUTF("not found"); // sending the server message
                        }
                        outStream.flush();
                        break;
                    }
                }

            }
            inStream.close();
            outStream.close();
            serverClient.close();
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }finally{
            System.out.println("Client -" + clientNo + " exit!! ");
        }

    }
}

