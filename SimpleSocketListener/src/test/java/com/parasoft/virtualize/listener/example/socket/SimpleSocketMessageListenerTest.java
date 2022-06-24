/*
 * (C) Copyright Parasoft Corporation 2022.  All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Parasoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.parasoft.virtualize.listener.example.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import com.parasoft.api.ICustomMessage;
import com.parasoft.api.responder.ICustomMessageListenerConfiguration;
import com.parasoft.api.responder.IMessageCompletionHandler;
import com.parasoft.api.responder.IMessageHandler;

public class SimpleSocketMessageListenerTest {
    private static final int PORT = 38786;
    private static final String EOF_TOKEN = "END";
    private static final String REQUEST = "Good Morning\r\n"
            + "END\r\n";

    @Test
    void testListener() throws UnknownHostException, IOException {
        // Given
        ICustomMessageListenerConfiguration customConfig = mock(ICustomMessageListenerConfiguration.class);
        when(customConfig.getValue("Port")).thenReturn(Integer.toString(PORT));
        when(customConfig.getValue("EOF Token")).thenReturn(EOF_TOKEN);

        IMessageHandler handler = mock(IMessageHandler.class);
        doAnswer(input -> {
            @SuppressWarnings("unchecked")
            ICustomMessage<String> request = (ICustomMessage<String>)input.getArgument(0);
            IMessageCompletionHandler callback = (IMessageCompletionHandler)input.getArgument(2);
            String response = request.getBodyString() + "Good Night\r\nEND\r\n";
            callback.completed(response.getBytes());
            return null;
        }).when(handler).handleMessage(any(), any(), any());

        // When
        SimpleSocketMessageListener listener = new SimpleSocketMessageListener();

        // Then
        assertTrue(listener.isReady(customConfig));

        listener.startup(customConfig, handler);

        String response = sendAndRecieve();

        listener.shutdown(customConfig);

        assertEquals("Good Morning\r\nGood Night\r\n", response);
    }

    private String sendAndRecieve() throws UnknownHostException, IOException {
        try (Socket socket = new Socket("localhost", PORT)) {
            try (OutputStream out = socket.getOutputStream()) {
                out.write(REQUEST.getBytes());

                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    return readResponse(EOF_TOKEN, in);
                }
            }
        }
    }

    private String readResponse(String eofToken, BufferedReader in) throws IOException {
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
