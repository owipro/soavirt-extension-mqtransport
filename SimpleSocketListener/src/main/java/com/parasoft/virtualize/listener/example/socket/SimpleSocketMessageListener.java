package com.parasoft.virtualize.listener.example.socket;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.parasoft.api.Application;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.responder.ICustomMessageListener;
import com.parasoft.api.responder.ICustomMessageListenerConfiguration;
import com.parasoft.api.responder.IMessageHandler;

public class SimpleSocketMessageListener implements ICustomMessageListener {
    private ServerSocket serverSocket = null;
    private ExecutorService service = null;

    @Override
    public boolean isReady(ICustomMessageListenerConfiguration customConfig) {
        SimpleSocketConfiguration config = new SimpleSocketConfiguration(customConfig);
        return config.getPort() > 0;
    }

    @Override
    public synchronized void startup(ICustomMessageListenerConfiguration customConfig, IMessageHandler handler) {
        SimpleSocketConfiguration config = new SimpleSocketConfiguration(customConfig);
        service = Executors.newFixedThreadPool(5);

        try {
            String eofToken = config.getEOFToken();
            serverSocket = new ServerSocket(config.getPort());
            Application.showMessage("Socket listener started on port " + config.getPort() + " and EOF token: " + eofToken);

            service.submit(() -> {
                while (!service.isShutdown()) {
                    try {
                        Socket socket = serverSocket.accept();
                        service.submit(new SimpleSocketListenerTask(socket, handler, eofToken, service));
                    } catch (IOException e) {
                        if (!service.isShutdown()) {
                            Application.getContext().report("Error accepting socket: " + e.getMessage(), ScriptingContext.ERROR);
                        }
                    }
                }
            });

            Application.getContext().reportEvent("Simple Socket: listener started ", ScriptingContext.INFO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void shutdown(ICustomMessageListenerConfiguration config) {
        if (service != null) {
            service.shutdown();
            try {
                service.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // Done waiting
            }
            service.shutdownNow();
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Application.showMessage(e.getMessage());
            }
        }

        Application.showMessage("Socket listener stopped");
        Application.getContext().reportEvent("Simple Socket: listener stopped", ScriptingContext.INFO);
    }

}