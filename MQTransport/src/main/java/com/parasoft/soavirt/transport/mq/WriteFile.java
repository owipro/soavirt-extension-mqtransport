package com.parasoft.soavirt.transport.mq;

import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {
    public static void writeFile(String filename, String contentToWrite){
        try {
          FileWriter myWriter = new FileWriter(filename);
          myWriter.write("Content: " + contentToWrite);
          myWriter.close();
          System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      }

}
