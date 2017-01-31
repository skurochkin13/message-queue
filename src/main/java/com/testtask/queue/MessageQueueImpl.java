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
 * Inspector thread is required for correct work.
 *
 */
public class MessageQueueImpl<E> implements IMessageQueue<E>
{
    private final static int MAX_CAPACITY = 10000;
    private final static long NANOS_TIMEOUT = 1000000;
    private static final Logger log = Logger.getLogger(MessageQueueImpl.class);

    private final BlockingQueue<E> _messageQueue;
    private final AtomicInteger _counter;
    private final ReentrantLock _lock;
    private final Condition _notAllChecked;
    private final Condition _notAllGet;

    public MessageQueueImpl() {
        _messageQueue = new LinkedBlockingQueue<>(MAX_CAPACITY);
        _counter = new AtomicInteger(0);
        _lock = new ReentrantLock();
        _notAllChecked = _lock.newCondition();
        _notAllGet = _lock.newCondition();
    }

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
            while (_counter.get() == 0) {
                _notAllGet.await();
            }
            log.debug("dequeue message. counter=" + _counter.get());
            _counter.decrementAndGet();
            return _messageQueue.take();
        } finally {
            _lock.unlock();
        }
    }

    public int size() {
        return _messageQueue.size();
    }

    public Iterator<E> iterator() {
        return new MessageQueueIterator(_messageQueue.iterator());
    }

    private class MessageQueueIterator implements Iterator<E> {
        private Iterator<E> _iterator;

        MessageQueueIterator(Iterator<E> iterator) {
            _iterator = iterator;
        }

        public boolean hasNext() {
            boolean hasNext = false;
            try {
                _lock.lockInterruptibly();
                try {
                    while (!_iterator.hasNext() && _messageQueue.size() > 0) {
                        _notAllChecked.awaitNanos(NANOS_TIMEOUT);
                    }
                    hasNext = _iterator.hasNext();
                } finally {
                    _lock.unlock();
                }
            } catch (InterruptedException e) {
                log.error("MessageQueueIterator: " + e);
                Thread.currentThread().interrupt();
            }
            return hasNext;
        }

        public E next() {
            E next = null;
            try {
                _lock.lockInterruptibly();
                try {
                    log.debug("MessageQueueIterator: next(): counter=" + _counter.get());
                    next = _iterator.next();
                    _counter.incrementAndGet();
                    _notAllGet.signal();
                } finally {
                    _lock.unlock();
                }
            } catch (InterruptedException e) {
                log.error("MessageQueueIterator: " + e);
                Thread.currentThread().interrupt();
            }
            return next;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
