package com.testtask.thread;

import com.testtask.common.Common;
import com.testtask.message.KeyValueMessage;
import com.testtask.queue.IMessageQueue;
import com.testtask.queue.MessageQueueImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.testtask.common.Common.FILE_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * JUnits for Consumer class.
 *
 */
public class TestConsumer
{
    private static final String PATH = Common.getPropertyValue(FILE_PATH);
    private static final String FILE_NAME = PATH + "TestResult";
    private static final KeyValueMessage[] messages = {new KeyValueMessage(1, "v1"),
            new KeyValueMessage(2, "v2"), new KeyValueMessage(3, "v3")};

    @BeforeClass
    public static void setUp() {
        Common.createPathIfNeeded(PATH);
    }

    @Test
    public void testRun() throws IOException, InterruptedException {
        IMessageQueue<KeyValueMessage> messageQueue = Mockito.mock(MessageQueueImpl.class);
        when(messageQueue.dequeue()).thenReturn(messages[0], messages[1], messages[2]);
        when(messageQueue.size()).thenReturn(3, 2, 2, 1, 1, 0);
        Consumer consumer = new Consumer(messageQueue, FILE_NAME);

        consumer.run();
        verify(messageQueue, times(3)).dequeue();
        File file = new File(FILE_NAME);
        assertTrue(file.exists());
        int counter = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String str;
            while ((str = in.readLine()) != null) {
                KeyValueMessage message = messages[counter++];
                assertTrue(str.equals(message.getKey() + "," + message.getValue()));
            }
        }
        assertEquals(messages.length, counter);
    }
}
