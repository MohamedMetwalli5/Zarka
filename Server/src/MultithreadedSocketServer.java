import java.io.IOException;
import java.net.*;
import java.util.*;

@SuppressWarnings("InfiniteLoopStatement")
public class MultithreadedSocketServer {
    public static void main(String[] args) throws IOException {
        Hashtable<String, String> VN1 = new Hashtable<>();
        Hashtable<String, String> VN2 = new Hashtable<>();
        Hashtable<String, String> VN3 = new Hashtable<>();
        int[] no_Elements = { 0 };
        List<Hashtable<String,String>> mem = new ArrayList<>();
        mem.add(VN1);mem.add(VN2);mem.add(VN3);
        Scanner in = new Scanner(System.in);
        String sharedFilePath = "D:\\ServerClientSharedFile.txt";
        String Config = "D:\\Config.txt";
        String SSTpath = "D:\\\\SST\\\\"; // should be checked
//        String sharedFilePath = "C:\\Users\\Kimo Store\\Desktop\\ServerClientSharedFile.txt";
//        String configFilePath = "C:\\Users\\Kimo Store\\Desktop\\Config.txt";


        int ConsistingHashingFullRange = 1001;
        int port = in.nextInt(); // getting the port number from the client for the first time only
        int counter = 0; // the number of clients
        int server_ID = Integer.parseInt(ReadFile.read(Config, port,"nodes")); //The id of the server
        List<PortRange> unsortedPortRangeList = new ArrayList<>();
        PortRange newAddedServer = new PortRange(); // this is a temp object just to call the re-balancing function
        List<String> ports = new ArrayList<>();
        int RF = Integer.parseInt(ReadFile.read(Config,0,"RF" ));
        SST lsm = new SST(SSTpath + port );
        try (ServerSocket server = new ServerSocket(port)){
            System.out.println("Server Started ....");
            /* We here, create a new server*/
            // Consistency hashing logic is Done here!
            try {
                unsortedPortRangeList = newAddedServer.ReadPortsTable(sharedFilePath);
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if(server_ID < 1){
                int end1 = new Random().nextInt(ConsistingHashingFullRange);
                while (end1 == 0 || end1 == ConsistingHashingFullRange-1)
                    end1 = new Random().nextInt(ConsistingHashingFullRange);
                int end2 = new Random().nextInt(ConsistingHashingFullRange-end1);
                while (end2 == 0 || end2 == ConsistingHashingFullRange-end1-1)
                    end1 = new Random().nextInt(ConsistingHashingFullRange-end1);
                PortRange newAddedServervn1 = new PortRange(port, 0,end1,0);
                PortRange newAddedServervn2 = new PortRange(port, end1+1,end2+end1,1);
                PortRange newAddedServervn3 = new PortRange(port, end2+end1,ConsistingHashingFullRange-1,2);
                unsortedPortRangeList.add(newAddedServervn1);
                unsortedPortRangeList.add(newAddedServervn2);
                unsortedPortRangeList.add(newAddedServervn3);
            } else{
                ports = Arrays.asList(ReadFile.read(Config, 0, "ports").split(","));
                Sender sender = new Sender();
                String x = "";
                int y;
                PortRange NewServer;
                for (int i = 0;i<3;i++) {
                    y =  new Random().nextInt(ConsistingHashingFullRange);
                    NewServer = new PortRange(port, 0,y, i); // the number of to range is a temp number
                    x = x + y ;
                    if (i != 3)
                        x+= "," ;
                    newAddedServer.RebalanceRanges(NewServer, unsortedPortRangeList,null,-55);
                }
                x = port + " " + x;
                for (int i = 0; i <ports.size()-1;i++){
                    sender.start(Integer.parseInt(ports.get(i)),x);
                }
            }
            newAddedServer.WritePortRangeTable(sharedFilePath,unsortedPortRangeList);
            while (true){
                counter++; // incrementing the number of clients
                Socket serverClient = server.accept();  //server accept the client connection request
                System.out.println(" >> " + "Client No:" + counter + " started!");
                ServerClientThread sct = new ServerClientThread(serverClient, counter,mem,no_Elements,ports,unsortedPortRangeList,RF,port,lsm); //send  the request to a separate thread
                sct.start();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}