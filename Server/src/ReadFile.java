import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner; // Import the Scanner class to read text files

public class ReadFile {
    static String read(String path,int port,String wanted) {
        int y = -1;
        String data = "";
        String RF = "";
        String QR = "";
        String QW = "";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            data = myReader.nextLine();
            y = Integer.parseInt(data);
            data = myReader.nextLine();
            y++;
            RF = myReader.nextLine();
            QW = myReader.nextLine();
            QR = myReader.nextLine();
            myReader.close();
            if (port != 0) {
                FileWriter myWriter = new FileWriter(path);
                if (y == 1)
                    myWriter.write(y + "\n" + port + "\n" + RF + "\n" + QW + "\n" + QR);
                else
                    myWriter.write(y + "\n" + data + "," + port + "\n" + RF + "\n" + QW + "\n" + QR);
                myWriter.close();
            }

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (wanted) {
            case "nodes":
                return y - 1 + "";
            case "ports":
                return data;
            case "RF":
                return RF;
            case "QW":
                return QW;
            case "QR":
                return QR;
        }

        return "";
    }
}