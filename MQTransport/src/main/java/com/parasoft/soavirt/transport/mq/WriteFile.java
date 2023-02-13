package com.parasoft.soavirt.transport.mq;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class WriteFile {
    public static void writeFile(String filename, String contentToWrite){
        try {
          Date datetime = new Date();
          FileWriter myWriter = new FileWriter(filename, true);
          myWriter.write(datetime + " : " + contentToWrite + "\n");
          myWriter.close();
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      }

}
