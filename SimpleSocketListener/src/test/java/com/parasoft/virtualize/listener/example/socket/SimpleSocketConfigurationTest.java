/*
 * (C) Copyright Parasoft Corporation 2022.  All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF Parasoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.parasoft.virtualize.listener.example.socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.parasoft.api.responder.ICustomMessageListenerConfiguration;

public class SimpleSocketConfigurationTest {

    @Test
    void testGetPort() {
        // Given
        ICustomMessageListenerConfiguration customConfig = mock(ICustomMessageListenerConfiguration.class);
        when(customConfig.getValue("Port")).thenReturn("38786");

        // When
        SimpleSocketConfiguration config = new SimpleSocketConfiguration(customConfig);
        int port = config.getPort();

        // Then
        assertEquals(38786, port);
    }
}
