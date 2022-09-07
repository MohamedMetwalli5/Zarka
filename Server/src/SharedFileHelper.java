import java.io.*;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files

public class SharedFileHelper {
    private int hashedKey;
    private final String path;
    public SharedFileHelper(int hashedKey, String path){
        this.hashedKey = hashedKey;
        this.path = path;
    }

    private String getVirtualNodeValue(String line){
        return line.split(" ")[3]; // returning the prt number in which this hashed key exist
    }

    private String gePortValue(int hashedKey, String line){
        String[] x = line.split(" ");
        int currentRangeFrom = Integer.parseInt(x[1]), currentRangeTo = Integer.parseInt(x[2]);
        //System.out.println(hashedKey);
        if(hashedKey >= currentRangeFrom && hashedKey <= currentRangeTo){
            return x[0]; // returning the prt number in which this hashed key exist
        }
        return null;
    }


    public ArrayList<PortAndVirtualNodeObject> InsertPortAndVirtualNodesNumbers(int replicationFactor, int fullRangeOfHashing,int offsetValue){ // It works like a nested loop
        String portNumber , virtualNodeNumber;
        ArrayList<PortAndVirtualNodeObject> result = new ArrayList<>();

        for(int i=0;i<replicationFactor;i++){
            // reading the shared file
            if(i >= 1){
                this.hashedKey = (this.hashedKey+offsetValue)%fullRangeOfHashing;
            }
            try {
                File myObj = new File(this.path);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String line = myReader.nextLine();
                    portNumber = gePortValue(this.hashedKey, line); // it returns an integer or null value
                    if(portNumber != null){ // we found a port where the hashed key lies in its range
                        virtualNodeNumber = getVirtualNodeValue(line);
                        PortAndVirtualNodeObject obj = new PortAndVirtualNodeObject(Integer.parseInt(portNumber), Integer.parseInt(virtualNodeNumber));
                        result.add(obj);
                        break;
                    }
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        }

        return result;
    }

}