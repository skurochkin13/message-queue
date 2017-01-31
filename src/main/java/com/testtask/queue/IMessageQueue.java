package com.testtask.queue;

import java.util.Iterator;

/**
 * Message queue interface.
 *
 */
public interface IMessageQueue<E>
{
    void queue(E message) throws InterruptedException;
    E dequeue() throws InterruptedException;
    int size();
    Iterator<E> iterator();
}
