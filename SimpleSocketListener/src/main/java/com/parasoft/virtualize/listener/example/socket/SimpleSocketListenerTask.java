package com.parasoft.virtualize.listener.example.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.parasoft.api.Application;
import com.parasoft.api.DefaultCustomMessage;
import com.parasoft.api.ICustomMessage;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.responder.IMessageCompletionHandler;
import com.parasoft.api.responder.IMessageHandler;
import com.parasoft.api.responder.MessageHandlerException;

public class SimpleSocketListenerTask implements Runnable {
    private Socket socket;
    private final IMessageHandler handler;
    private final String eofToken;
    private final ExecutorService service;

    public SimpleSocketListenerTask(Socket socket, IMessageHandler handler, String eofToken, ExecutorService service) {
        this.socket = socket;
        this.handler = handler;
        this.eofToken = eofToken;
        this.service = service;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            try (OutputStream out = socket.getOutputStream()) {
                String message = readRequest(eofToken, in);
                if (message.length() > 0) {
                    Application.getContext().reportEvent("request received: " + message, ScriptingContext.INFO);

                    ICustomMessage<String> request = new DefaultCustomMessage<>(message, null);
                    handler.handleMessage(request, service, new IMessageCompletionHandler() {
                        @Override
                        public void completed(byte[] response) {
                            try {
                                out.write(response);
                                out.flush();
                            } catch (IOException e) {
                                Application.getContext().reportEvent("error writting response: " + e.getMessage(), ScriptingContext.ERROR);
                            }
                        }
                        @Override
                        public void failed(MessageHandlerException t) {
                            Application.getContext().reportEvent("error handling request: " + t.getMessage(), ScriptingContext.ERROR);
                        }
                    });
                } else {
                    Application.getContext().reportEvent("Empty request received.", ScriptingContext.WARN);
                }
            }
        } catch (IOException e) {
            Application.getContext().reportEvent("error reading request: " + e.getMessage(), ScriptingContext.ERROR);
        }
    }

    private String readRequest(String eofToken, BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = in.readLine()) != null) {
            if (line.equals(eofToken)) {
                break;
            }
            sb.append(line);
            sb.append("\r\n");
        }
        return sb.toString();
    }

}
