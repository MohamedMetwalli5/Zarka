import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class ReadFile {
    static String read(String path) {
        int y;
        String data = ""; // number of ports
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            data = myReader.nextLine(); // to make the client read the number of ports directly from the next line
            y = Integer.parseInt(data); // converting number of ports from string to integer
            if(y == 0)
                return "none";
            System.out.println(data);
            data = myReader.nextLine(); // reading the ports themselves using the same variable
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return data;
    }
}