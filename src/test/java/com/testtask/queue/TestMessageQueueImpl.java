package com.testtask.queue;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnits for MessageQueueImpl
 *
 */
public class TestMessageQueueImpl
{
    private static final String[] messages = {"m1", "m2", "m3"};
    private IMessageQueue<String> messageQueue;

    @Test
    public void testQueue() throws InterruptedException {
        messageQueue = new MessageQueueImpl();
        for (String message : messages) {
            messageQueue.queue(message);
        }
        assertEquals(messages.length, messageQueue.size());
    }

    @Test
    public void testDequeue() throws InterruptedException {
        testQueue();
        IMessageQueueIterator<String> itr = (IMessageQueueIterator<String>) messageQueue.iterator();
        int i = 0;
        while (itr.hasNext()) {
            itr.next();
            itr.complete();
            assertTrue((messageQueue.dequeue()).equals(messages[i++]));
        }
        assertEquals(0, messageQueue.size());
    }
}
