package com.parasoft.soavirt.transport.mq;

import com.parasoft.api.ICustomMessage;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;
import com.parasoft.api.transport.ICustomConnection;
import com.parasoft.api.transport.ICustomTransport;

import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
//import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;

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

        //Get Queue Manager
        String queueManagerName = config.getQueueManager(context);
        //Get queue details
        String putQueueName = config.getPutQueue(context);
        String getQueueName = config.getGetQueue(context);

        String strMsgTxtFinal = "";

        System.out.println("QM " + queueManagerName );
        WriteFile.writeFile("TestFile.txt","QM " + queueManagerName + " Put Queue " + getQueueName + " Get Queue " + getQueueName);


        try {
          // Create a connection to the QueueManager
          System.out.println("Connecting to queue manager: " + queueManagerName);
          MQQueueManager queueManager = new MQQueueManager(queueManagerName);

          // PUT Queue open options
          int putOpenOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT | MQConstants.MQOO_BROWSE;
    
          // Queue connect
          System.out.println("Accessing PUT queue: " + putQueueName);
          MQQueue putQueue = queueManager.accessQueue(putQueueName, putOpenOptions);
   
          //Send 5 messages to the queue, the 4th one with a different message
          int t;
          String messageText;
   
          for (t=0;t<5;t++){
             // Define a simple IBM MQ Message ...
             MQMessage msg = new MQMessage();
             // ... and write some text in UTF8 format
             if (t==3) messageText="Fourth message";
             else messageText="Other message";
   
             msg.writeUTF(messageText);
   
             // Specify the default put message options
             MQPutMessageOptions pmo = new MQPutMessageOptions();
       
             // Put the message to the queue
             System.out.println("Sending a message...\"" + messageText + "\"");
             putQueue.put(msg, pmo);
           }

          // GET Queue open options
          int getOpenOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT | MQConstants.MQOO_BROWSE;
    
          // Connect to GET queue
          System.out.println("Accessing GET queue: " + putQueueName);
          MQQueue getQueue = queueManager.accessQueue(getQueueName, getOpenOptions);

          //Keep GETting messages until we find the one with the body text we want
          String msgID, msgText;
          int i=0;
          int msgLen;
          byte[] msgIDhex;
   
          do{
               MQMessage rcvMessage = new MQMessage();
       
               // Specify default get message options
               MQGetMessageOptions gmo = new MQGetMessageOptions();
   
               //Use MQGMO_BROWSE_FIRST on the first message and MQGMO_BROWSE_NEXT on subsequent
               if (i==0){
               gmo.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_BROWSE_FIRST;        
               }
               else{
                   gmo.options = MQConstants.MQGMO_WAIT | MQConstants.MQGMO_BROWSE_NEXT | MQConstants.MQGMO_CONVERT;
               }
   
               // Get the message off the queue.
               System.out.println("Getting Message...i=" + i);
               getQueue.get(rcvMessage, gmo);
       
               // And display the message text...
              // msgText = rcvMessage.readUTF();
              msgLen = rcvMessage.getMessageLength();
              msgText = rcvMessage.readUTF(); 
   
          
          //     rcvMessage.seek(0);
           //    byte data[] = new byte[rcvMessage.getMessageLength()];
          //     msgText = rcvMessage.readFully(data);
   
               //Output received message length, body text and ID (readable)
               msgIDhex = rcvMessage.messageId;
               msgID = BytesToHex.bytesToHex(rcvMessage.messageId);
               System.out.println("Message " + i + " length is: " + msgLen);
               System.out.println("Message " + i + " body text is: " + msgText);
               System.out.println("Message " + i + " ID is: " + msgID);
   
               i++;
          }
          while (!msgText.equals("Fourth message"));
   
          // Specify message options to retrieve the message, matched by message ID
          MQGetMessageOptions gmo = new MQGetMessageOptions();
          gmo.options = MQConstants.MQGMO_WAIT;
          gmo.matchOptions = MQConstants.MQMO_MATCH_MSG_ID;
   
          //Get the 4th messgage by messageID 
          MQMessage rcvMessage = new MQMessage();
          rcvMessage.messageId = msgIDhex;
          System.out.println("Retrieving Message " + i);
          getQueue.get(rcvMessage, gmo);
         
          //Output received message length, body text and ID (readable)
          msgID = BytesToHex.bytesToHex(rcvMessage.messageId);
          System.out.println("The 4th message length is: " + msgLen);
          System.out.println("The 4th message is: " + msgText);
          System.out.println("The 4th messageID is: " + msgID);
   
          strMsgTxtFinal = msgText;
          WriteFile.writeFile("Msg.txt","strMsgTxtFinal: " + strMsgTxtFinal);

          // Close the queue
          System.out.println("Closing the queue");
          getQueue.close();
    
          // Disconnect from the QueueManager
          System.out.println("Disconnecting from the Queue Manager");
          queueManager.disconnect();
          System.out.println("Done!");
        }
        catch (MQException ex) {
          System.out.println("An IBM MQ Error occurred : Completion Code " + ex.completionCode
              + " Reason Code " + ex.reasonCode);
          ex.printStackTrace();
          for (Throwable t = ex.getCause(); t != null; t = t.getCause()) {
            System.out.println("... Caused by ");
            t.printStackTrace();
          }
    
        }
        catch (java.io.IOException ex) {
          System.out.println("An IOException occurred whilst writing to the message buffer: " + ex);
        }



        return new MQContents(strMsgTxtFinal);
    }

}
