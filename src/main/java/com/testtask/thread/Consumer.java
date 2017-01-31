package com.testtask.thread;

import com.testtask.message.KeyValueMessage;
import com.testtask.queue.IMessageQueue;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static com.testtask.common.Common.KEY_VALUE_DELIMITER;

/**
 * Queue Consumer class.
 *
 */
public class Consumer implements Runnable
{
    private static final Logger log = Logger.getLogger(Consumer.class);

    private final IMessageQueue _messageQueue;
    private final String _resultFile;

    public Consumer(IMessageQueue messageQueue, String resultFile) {
        _messageQueue = messageQueue;
        _resultFile = resultFile;
    }

    public void run() {
        File file = new File(_resultFile);

        try {
            if (file.exists()) {
                if (file.delete())
                    log.debug(_resultFile + " old result file deleted.");
            }
            if (file.createNewFile()) {
                try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                    while (true) {
                        try {
                            log.debug("try to get message from queue. queue size=" + _messageQueue.size());
                            out.println(convertMessageToString((KeyValueMessage) _messageQueue.dequeue()));

                            if (_messageQueue.size() == 0) {
                                break;
                            }
                        } catch (InterruptedException e) {
                            log.error(e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private String convertMessageToString(KeyValueMessage message) {
        return message.getKey() + KEY_VALUE_DELIMITER + message.getValue();
    }
}
