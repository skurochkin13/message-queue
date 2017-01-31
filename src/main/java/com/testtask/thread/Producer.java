package com.testtask.thread;

import com.testtask.message.KeyValueMessage;
import com.testtask.queue.IMessageQueue;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.testtask.common.Common.KEY_VALUE_DELIMITER;

/**
 * Queue Producer class.
 *
 */
public class Producer implements Runnable
{
    private static final Logger log = Logger.getLogger(Producer.class);

    private final IMessageQueue _messageQueue;
    private final String _fileName;

    public Producer(IMessageQueue messageQueue, String fileName) {
        _messageQueue = messageQueue;
        _fileName = fileName;
    }

    public void run() {
        File file = new File(_fileName);
        log.debug("start queue messages from " + _fileName + "file.");
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
                String str;
                while ((str = in.readLine()) != null) {
                    KeyValueMessage message = convertStrToMessage(str);
                    if (message != null) {
                        try {
                            _messageQueue.queue(convertStrToMessage(str));
                        } catch (InterruptedException e) {
                            log.error(e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        } catch(IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private KeyValueMessage convertStrToMessage(String str) {
        int delimiterPos = str.indexOf(KEY_VALUE_DELIMITER);
        try {
            int key = Integer.parseInt(str.substring(0, delimiterPos).trim());
            String val = str.substring(delimiterPos + 1, str.length()).trim();
            return new KeyValueMessage(key, val);
        } catch(NumberFormatException e) {
            log.debug(e);
            return null;
        }
    }
}
