package com.testtask.queue;

import java.util.Iterator;

/**
 * MessageQueueIterator interface.
 * hasNext()->next()->update data->complete()
 *
 */
public interface MessageQueueIterator<E> extends Iterator<E> {
    void complete();
}
