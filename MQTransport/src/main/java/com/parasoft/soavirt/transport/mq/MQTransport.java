package com.parasoft.soavirt.transport.mq;

import java.io.FileWriter;
import java.io.IOException;

import com.parasoft.api.ICustomMessage;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;
import com.parasoft.api.transport.ICustomConnection;
import com.parasoft.api.transport.ICustomTransport;

public class MQTransport implements ICustomTransport {

    public MQTransport() {

    }

    @Override
    public ICustomConnection createNewConnection(CustomTransportConfiguration transportConfig) throws Throwable {
        return null;
    }

    @Override
    public ICustomMessage invoke(ICustomConnection transportConnection, CustomTransportConfiguration transportConfig, ICustomMessage request, ScriptingContext context) throws Throwable {
        MQTransportConfiguration config = new MQTransportConfiguration(transportConfig);
        String queueManager = config.getQueueManager(context);
        System.out.println("HERE " + queueManager);
        writeFileTest("File Content " + queueManager);

        return new MQContents("example text");
    }

    public void writeFileTest(String contentToWrite){
          try {
            FileWriter myWriter = new FileWriter("oscarfile.txt");
            myWriter.write("Content: " + contentToWrite);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        }



}
