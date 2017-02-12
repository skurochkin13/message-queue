package com.testtask.queue;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Message queue implementation.
 * Iterating through queue is required for correct work.
 *
 */
public class MessageQueueImpl<E> implements IMessageQueue<E>
{
    private final static int MAX_CAPACITY = 10000;
    private final static long AWAIT_TIME = 100000L; //just for testing purposes

    private static final Logger log = Logger.getLogger(MessageQueueImpl.class);

    private final BlockingQueue<E> _messageQueue = new LinkedBlockingQueue<>(MAX_CAPACITY);
    //counter of checked messages that are ready for dequeue
    private final AtomicInteger _counter = new AtomicInteger(0);
    private final ReentrantLock _lock = new ReentrantLock();
    //conditions for check and take
    private final Condition _notAllChecked = _lock.newCondition();
    private final Condition _notAllTaken = _lock.newCondition();

    public void queue(E message) throws InterruptedException {
        _lock.lockInterruptibly();
        try {
            log.debug("queue message. counter=" + _counter.get());
            _messageQueue.put(message);
            _notAllChecked.signal();
        }
        finally {
            _lock.unlock();
        }
    }

    public E dequeue() throws InterruptedException {
        _lock.lockInterruptibly();
        try {
            E message;
            while (_counter.get() == 0) {
                _notAllTaken.await();
            }
            log.debug("dequeue message. counter=" + _counter.get());
            message = _messageQueue.take();
            _counter.decrementAndGet();
            return message;
        } finally {
            _lock.unlock();
        }
    }

    public int size() {
        return _messageQueue.size();
    }

    public IMessageQueueIterator<E> iterator() {
        return new IMessageQueueIteratorImpl<>(_messageQueue.iterator());
    }

    private class IMessageQueueIteratorImpl<E> implements IMessageQueueIterator<E> {
        private Iterator<E> _iterator;

        IMessageQueueIteratorImpl(Iterator<E> iterator) {
            _iterator = iterator;
        }

        public boolean hasNext() {
            boolean hasNext = false;
            try {
                _lock.lockInterruptibly();
                try {
                    while (!_iterator.hasNext() && _messageQueue.size() > 0) {
                        _notAllChecked.awaitNanos(AWAIT_TIME);
                    }
                    hasNext = _iterator.hasNext();
                } finally {
                    _lock.unlock();
                }
            } catch (InterruptedException e) {
                log.error("IMessageQueueIteratorImpl: " + e);
                Thread.currentThread().interrupt();
            }
            return hasNext;
        }

        public E next() {
            return _iterator.next();
        }

        public void complete() {
            _lock.lock();
            try {
                log.debug("IMessageQueueIteratorImpl: complete(): counter=" + _counter.get());
                _counter.incrementAndGet();
                _notAllTaken.signal();
            } finally {
                _lock.unlock();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
