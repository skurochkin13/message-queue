package com.testtask.queue;

import java.util.Iterator;

/**
 * IMessageQueueIterator interface.
 * hasNext()->next()->update data->complete()
 *
 */
public interface IMessageQueueIterator<E> extends Iterator<E> {
    void complete();
}
