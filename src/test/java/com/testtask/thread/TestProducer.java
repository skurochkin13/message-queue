package com.testtask.thread;

import com.testtask.inputfilegenerator.FileGeneratorImpl;
import com.testtask.message.KeyValueMessage;
import com.testtask.queue.MessageQueueImpl;
import com.testtask.queue.MessageQueueIterator;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.testtask.common.Common.KEY_VALUE_DELIMITER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnits for Producer class.
 *
 */
public class TestProducer
{
    private static final int LINES_NUMBER = 13;

    @Test
    public void testRun() throws IOException, InterruptedException {
        FileGeneratorImpl fileGenerator = new FileGeneratorImpl(LINES_NUMBER);
        String fileName = fileGenerator.generateInputFile();

        KeyValueMessage[] msgArr = new KeyValueMessage[LINES_NUMBER];
        File file = new File(fileName);
        assertTrue(file.exists());
        int counter = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String str;
            while ((str = in.readLine()) != null) {
                msgArr[counter++] = toKeyValueMessage(str);
            }
        }
        assertEquals(LINES_NUMBER, counter);

        MessageQueueImpl<KeyValueMessage> messageQueue = new MessageQueueImpl<>();
        Producer producer = new Producer(messageQueue, fileName);
        producer.run();
        MessageQueueIterator<KeyValueMessage> itr = messageQueue.iterator();
        counter = 0;
        while (itr.hasNext()) {
            itr.next();
            itr.complete();
            assertTrue((messageQueue.dequeue()).equals(msgArr[counter++]));
        }
    }

    private KeyValueMessage toKeyValueMessage(String str) {
        int delimiterPos = str.indexOf(KEY_VALUE_DELIMITER);
        int key = Integer.parseInt(str.substring(0, delimiterPos).trim());
        String val = str.substring(delimiterPos + 1, str.length()).trim();
        return new KeyValueMessage(key, val);
    }
}
