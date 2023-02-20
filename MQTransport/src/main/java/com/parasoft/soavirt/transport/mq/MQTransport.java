package com.parasoft.soavirt.transport.mq;

import com.parasoft.api.ICustomMessage;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;
import com.parasoft.api.transport.ICustomConnection;
import com.parasoft.api.transport.ICustomTransport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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

        //Get Queue Manager and Queues
        String queueManagerName = config.getQueueManager(context);
        String putQueueName = config.getPutQueue(context);
        String getQueueName = config.getGetQueue(context);

        String strMsgTxtFinal = "";

        //DETETE - testing reportEvent - Why doesn't this report?
        context.reportEvent("HERE1",//"QM " + queueManagerName + " Put Queue " + getQueueName + " Get Queue " + getQueueName,
         //   "MQTransport Extension",
            ScriptingContext.ERROR);
   //     context.report("HERE2");

        //DELETE - get unused variable
        String testx = context.getValue("Testx");
        WriteFile.writeFile("TestFile.txt","Testx: " + testx);
        
        //DELETE - get unused Data Source value
        String messageToSend = context.getValue("MessageDetails", "Request");
        WriteFile.writeFile("TestFile.txt","messageToSend: " + messageToSend + ", row: " + context.getDataSourceRowIndex());
        
        System.out.println("QM " + queueManagerName );
        WriteFile.writeFile("TestFile.txt","QM " + queueManagerName + " Put Queue " + getQueueName + " Get Queue " + getQueueName);

        try {
          // Create a connection to the QueueManager
          System.out.println("Connecting to queue manager: " + queueManagerName);
          MQQueueManager queueManager = new MQQueueManager(queueManagerName);

          // PUT Queue open options
          int putOpenOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT | MQConstants.MQOO_BROWSE;
    
          // Queue connect
//          System.out.println("Accessing PUT queue: " + putQueueName);
          WriteFile.writeFile("TestFile.txt","Accessing PUT queue: " + putQueueName);

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
//             System.out.println("Sending a message...\"" + messageText + "\"");
             WriteFile.writeFile("TestFile.txt","Sending message: " + (t+1));
             putQueue.put(msg, pmo);
           }

          // GET Queue open options
          int getOpenOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT | MQConstants.MQOO_BROWSE;
    
          // Connect to GET queue
          WriteFile.writeFile("TestFile.txt","Accessing GET queue: " + getQueueName);
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
               WriteFile.writeFile("TestFile.txt","Getting Message " + (i+1));
               getQueue.get(rcvMessage, gmo);
               WriteFile.writeFile("TestFile.txt","Got Message " + (i+1));
               WriteFile.writeFile("TestFile.txt","Message Length " + rcvMessage.getMessageLength());
               WriteFile.writeFile("TestFile.txt","Message content rcvMessage.readStringOfCharLength(rcvMessage.getMessageLength()): " + rcvMessage.readStringOfCharLength(rcvMessage.getMessageLength()));

               rcvMessage.seek(0);
               byte buffer[] = new byte[rcvMessage.getMessageLength()];
               rcvMessage.readFully(buffer);
               WriteFile.writeFile("TestFile.txt","Message content buffer: " + buffer);

               // And display the message text...
              // msgText = rcvMessage.readUTF();
              msgLen = rcvMessage.getMessageLength();
              WriteFile.writeFile("TestFile.txt","Message Length msgLen: " + msgLen);
              WriteFile.writeFile("TestFile.txt","Message Length msgLen: " + msgLen);

              msgText = rcvMessage.readStringOfCharLength(msgLen);
              WriteFile.writeFile("TestFile.txt","Message Length msgLen: " + msgLen);

              WriteFile.writeFile("TestFile.txt","Message content buffer: " + buffer);

              WriteFile.writeFile("TestFile.txt","Message text msgText: " + msgText);

              WriteFile.writeFile("TestFile.txt","Message " + (i+1) + " text " + msgText);
              
          //     rcvMessage.seek(0);
           //    byte data[] = new byte[rcvMessage.getMessageLength()];
          //     msgText = rcvMessage.readFully(data);
   
               //Output received message length, body text and ID (readable)
               msgIDhex = rcvMessage.messageId;
               msgID = BytesToHex.bytesToHex(rcvMessage.messageId);
               WriteFile.writeFile("TestFile.txt","Message " + i + " length is: " + msgLen);
               WriteFile.writeFile("TestFile.txt","Message " + i + " body text is: " + msgText);
               WriteFile.writeFile("TestFile.txt","Message " + i + " ID is: " + msgID);
   
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
          WriteFile.writeFile("TestFile.txt","Retrieving Message " + (i+1));
          getQueue.get(rcvMessage, gmo);
         
          //Output received message length, body text and ID (readable)
          msgID = BytesToHex.bytesToHex(rcvMessage.messageId);
          WriteFile.writeFile("TestFile.txt","The 4th message length is: " + msgLen);
          WriteFile.writeFile("TestFile.txt","The 4th message is: " + msgText);
          WriteFile.writeFile("TestFile.txt","The 4th messageID is: " + msgID);
   
          strMsgTxtFinal = msgText;
          WriteFile.writeFile("TestFile.txt","strMsgTxtFinal: " + strMsgTxtFinal);

          // Close the queue
          WriteFile.writeFile("TestFile.txt","Closing the queue");
          getQueue.close();
    
          // Disconnect from the QueueManager
          WriteFile.writeFile("TestFile.txt","Disconnecting from the Queue Manager");
          queueManager.disconnect();
          WriteFile.writeFile("TestFile.txt","Done!");
        }
        catch (MQException ex) {
            WriteFile.writeFile("TestFile.txt","An IBM MQ Error occurred : Completion Code " + ex.completionCode
              + " Reason Code " + ex.reasonCode);
          ex.printStackTrace();
          for (Throwable t = ex.getCause(); t != null; t = t.getCause()) {
              WriteFile.writeFile("TestFile.txt","... Caused by ");
            t.printStackTrace();
          }
    
        }
        catch (java.io.IOException ex) {
          System.out.println("An IOException occurred whilst writing to the message buffer: " + ex);
        }

        //Map mqHeaders = new Map();
        WriteFile.writeFile("TestFile.txt","strMsgTxtFinal: OUTSIDE: " + strMsgTxtFinal);

        //Test adding headers to response
        Map<String, List<String>> mqHeaders = new HashMap<String, List<String>>();
        List<String> headerList1 = new ArrayList<String>();
        headerList1.add("First Header Item 1");
        headerList1.add("First Header Item 2");
        mqHeaders.put("Header 1",headerList1);

        List<String> headerList2 = new ArrayList<String>();
        headerList2.add("Second Header");
        mqHeaders.put("Body",headerList2);

        //Create response object to return to SOATest
        MQContents mqcontents = new MQContents(strMsgTxtFinal, mqHeaders);

        return mqcontents;
    }

}
