package com.parasoft.soavirt.transport.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.parasoft.api.ICustomMessage;
import com.parasoft.api.ScriptingContext;
import com.parasoft.api.transport.CustomTransportConfiguration;
import com.parasoft.api.transport.ICustomConnection;
import com.parasoft.api.transport.ICustomTransport;

public class FileTransport implements ICustomTransport {
    private final static String NL_IS_DIRECTORY = " is a directory"; //$NON-NLS-1$
    private final static String NL_CREATE_FAILED = "Failed to create file "; //$NON-NLS-1$
    private final static String NL_WAITTIME_NULL = "Wait time connot be null"; //$NON-NLS-1$
    private final static String NL_WAITTIME_EMPTY = "Wait time connot be empty, specific a positive integer or zero (milliseconds)"; //$NON-NLS-1$
    private final static String NL_READ_FAILED = "Failed to read response file bytes from the file. buffer size created "; //$NON-NLS-1$
    private final static String NL_OUTPUT_FILE = "The response (output) file "; //$NON-NLS-1$
    private final static String NL_NOT_FOUND = " was not found after waiting for "; //$NON-NLS-1$
    private final static String NL_MILLISECONDS = " milliseconds"; //$NON-NLS-1$
    private final static String NL_TOO_BIG = "The response file is too big: "; //$NON-NLS-1$
    private final static String NL_BYTES = " bytes"; //$NON-NLS-1$

    public FileTransport() {
    }

    @Override
    public ICustomConnection createNewConnection(CustomTransportConfiguration transportConfig) throws Throwable {
        return null;
    }

    @Override
    public ICustomMessage invoke(ICustomConnection transportConnection, CustomTransportConfiguration transportConfig, ICustomMessage request, ScriptingContext context) throws Throwable {
        FileTransportConfiguration config = new FileTransportConfiguration(transportConfig);
        String requestPath = config.getInputFilePath(context);
        File requestFile = new File(requestPath);
        request.setHeader(FileContents.NL_FILE_PATH, requestFile.getCanonicalPath());
        if (requestFile.isDirectory()) {
            context.report(requestFile.getCanonicalPath() + NL_IS_DIRECTORY);
            return null;
        }
        if (!requestFile.exists()) {
            if (!requestFile.createNewFile()) {
                context.report(NL_CREATE_FAILED + requestFile.getCanonicalPath());
                return null;
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(requestFile);
            fos.write(request.getBodyBytes());
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        String waitTimeStr = config.getWaitTime(context);
        if (waitTimeStr == null) {
            context.report(NL_WAITTIME_NULL);
            return null;
        }
        if (waitTimeStr.length() == 0) {
            context.report(NL_WAITTIME_EMPTY);
            return null;
        }
        long waitTime = 0;
        try {
            waitTime = Long.parseLong(waitTimeStr);
        } catch (NumberFormatException e) {
            throw e;
        }
        Thread.sleep(waitTime);
        String responsePath = config.getOutputFilePath(context);
        File responseFile = new File(responsePath);
        if (!responseFile.exists()) {
            context.report(NL_OUTPUT_FILE + responseFile.getCanonicalPath() + NL_NOT_FOUND + waitTime + NL_MILLISECONDS);
            return null;
        }
        FileInputStream fis = null;
        byte[] responseBytes = null;
        try {
            fis = new FileInputStream(responseFile);
            long length = responseFile.length();
            if (length > 50 * 1024*1024) {
                context.report(NL_TOO_BIG + length + NL_BYTES);
                return null;
            }
            responseBytes = new byte[(int)length]; // parasoft-suppress PB.NUM.CLP "Suppressing violations that would be risky to change in legacy code."
            int result = fis.read(responseBytes);
            if (result < 0) {
                context.report(NL_READ_FAILED + (int) length); // parasoft-suppress PB.NUM.CLP "Suppressing violations that would be risky to change in legacy code."
                return null;
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return new FileContents(responseBytes, responseFile.getCanonicalPath());
    }
}
