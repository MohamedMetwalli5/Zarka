import java.io.*;
import java.util.*;

public class SST {
    AVLTree segmant;
    AVLTree temp;
    int segmants;
    String path;
    File myObj;
    public SST(String path) {
        try {
            this.path = path;
            try {
                this.myObj = new File(this.path);
                this.myObj.mkdir();
                this.myObj = new File(this.path + "\\SST.txt");
                if (this.myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                }
            }catch (Exception e){}
            this.segmants = 0;
            this.segmant = new AVLTree();
            this.temp = new AVLTree();
        }catch (Exception e){}
    }
    public void addToLsm(List<Hashtable<String,String>> mem) throws IOException {
        try {
            for (int i = 0; i < 3; i++) {//Hashtable<String,String> x : mem){
                Enumeration<String> e = mem.get(i).keys();
                while (e.hasMoreElements()) {
                    // Getting the key of a particular entry
                    String key = e.nextElement();
                    segmant.insert(key, mem.get(i).get(key));
                    if (segmant.elemnts == 200) {
                        writeToDisk();
                        segmant = new AVLTree();
                        segmants++;
                        if (segmants == 5){
                            compact(this.path + "SST.txt");
                            segmants = 0;
                        }
                    }
                }
                mem.set(i, new Hashtable<>());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void compact(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Hashtable<String,String> lines = new Hashtable<String,String>(10000); // maybe should be bigger
        String line;
        while ((line = reader.readLine()) != null) {
            lines.put(line.split(" ")[0],line.split(" ")[1]);
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        Enumeration<String> e = lines.keys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            writer.write(key + " " + lines.get(key));
            writer.newLine();
        }
        writer.close();
    }

    public void rehash(List<Pair> keysTorehash) throws IOException {
        try {
            for (Pair x : keysTorehash) {
                this.segmant.insert(x.key, x.value);
            }
            writeToDisk();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String get(String key) throws IOException {
        try {
            Node x = segmant.find(key);
            BufferedReader in;
            File file = this.myObj;
            if (x!= null)
                return x.key;
            else
                in = new BufferedReader(new InputStreamReader(new ReverseLineInputStream(file)));
            while(true) {
                String line = in.readLine();
                if (line == null || line.split(" ")[0].equals(key)) {
                    if(line != null)
                        return line.split(" ")[1];
                    break;
                }
                System.out.println("X:" + line);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "not found";
    }

    public void writeToDisk() throws IOException {
        List<Node> temp = new ArrayList<>();
        temp.add(segmant.root);
        Node x;
        FileWriter myWriter = new FileWriter(this.myObj.getPath(),true);
        while (temp.size()!=0){
            x = temp.get(0);
            temp.remove(0);
            if(x==null) {
                continue;
            }
            myWriter.write(x.key + " " + x.value + "\n");
            temp.add(x.left);
            temp.add(x.right);
        }
        myWriter.close();
    }
}
