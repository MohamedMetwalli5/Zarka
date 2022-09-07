import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PortRange {
    int portNumber, fromRange, toRange,vn;
    List<Pair> keysValuesToMigrateList;
    public PortRange(int portNumber, int fromRange, int toRange,int vn){
        this.portNumber = portNumber;
        this.fromRange = fromRange;
        this.toRange = toRange;
        this.vn = vn;
        keysValuesToMigrateList = new ArrayList<Pair>();
    }

    public PortRange(){
        this.portNumber = 0;
        this.fromRange = 0;
        this.toRange = 0;
        this.vn = 0;
    }

    public int getFromRange(){
        return this.fromRange;
    }


    public void RebalanceRanges(PortRange NewAddedServer, List<PortRange> unsortedPortRangeList, List<Hashtable<String, String>> mem, int currentPort) throws IOException {
        List<PortRange> SortedPortRangeList = unsortedPortRangeList.stream()
                .sorted(Comparator.comparing(PortRange::getFromRange))
                .collect(Collectors.toList());

        for(int i=SortedPortRangeList.size()-1;i>=0;i--){
            if(NewAddedServer.toRange > SortedPortRangeList.get(i).fromRange){
                NewAddedServer.fromRange = SortedPortRangeList.get(i).fromRange;
                SortedPortRangeList.get(i).fromRange = NewAddedServer.toRange+1;
                SST temp = new SST("D:\\SST\\" + NewAddedServer.portNumber );
                if(SortedPortRangeList.get(i).portNumber == currentPort){ // there is a possibility here that
                    if(mem != null && currentPort != -55){
                        System.out.println("rehash");
                        Rehash(mem.get(SortedPortRangeList.get(i).vn), SortedPortRangeList.get(i).fromRange, SortedPortRangeList.get(i).toRange,NewAddedServer);
                    }
                }
                temp.rehash(this.keysValuesToMigrateList);
                break;
            }
        }
        unsortedPortRangeList.add(NewAddedServer);

    }

    private void Rehash(Hashtable<String, String> currentHashTable, int fromRange, int toRange, PortRange newAddedServer){
        Enumeration<String> e = currentHashTable.keys();
        boolean flag ;
        while (e.hasMoreElements()) {
            // Getting the key of a particular entry
            String k = e.nextElement();
            flag = false;
            int hashKey = k.hashCode()%1001;
            if (hashKey<0)
                hashKey += 1001;
            if(hashKey < fromRange || hashKey > toRange){
                System.out.println(hashKey);
                if (hashKey >= newAddedServer.fromRange && hashKey <= newAddedServer.toRange){
                    flag = false;
                }
                else {
                    for(int i=1;i<5;i++){
                        if ((hashKey+(i)*200)%1001 >= newAddedServer.fromRange && (hashKey+(i)*200)%1001 <= newAddedServer.toRange){
                            flag = false;
                            break;
                        }
                        else if((hashKey+(i)*200)%1001 > fromRange || (hashKey+(i)*200)%1001 < toRange){
                            flag = true;
                            break;
                        }
                    }
                }
                if(!flag){
                    Pair pair = new Pair(k, currentHashTable.get(k)); // the key-value pair that must migrate
                    this.keysValuesToMigrateList.add(pair);
                    currentHashTable.remove(k); // removing the (key,value) pair from the hash table
                }
            }
        }
        Set<String> setOfKeys = currentHashTable.keySet();

        for(Pair y :this.keysValuesToMigrateList){
            System.out.println("key " + y.key + "   value " + y.value);
        }
    }


    public List<PortRange> ReadPortsTable(String path){
        List<PortRange> PortRangeList = new ArrayList<>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] temp = line.split(" ");
                PortRange pr = new PortRange(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]),Integer.parseInt(temp[3]));
                PortRangeList.add(pr);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return PortRangeList;
    }

    public void WritePortRangeTable(String path, List<PortRange> PortRangeList) throws IOException {
        FileWriter fw = new FileWriter(path); //the true will append the new data
        try {
            for(PortRange obj : PortRangeList){
                fw.write(obj.portNumber + " " + obj.fromRange + " " + obj.toRange+ " " + obj.vn);//appends the string [[portNumber] [fromRange] [toRange]] to the file
                fw.write("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        fw.close();
    }

}