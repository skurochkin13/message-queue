package com.testtask.thread;

import com.testtask.message.KeyValueMessage;
import com.testtask.queue.IMessageQueue;
import com.testtask.queue.MessageQueueIterator;
import org.apache.log4j.Logger;

/**
 * Controller of messages in queue.
 * Iterates across queue and replaces values of message if it needed.
 *
 */
public class MessagesInspector implements Runnable
{
    private static final long EXECUTION_DELAY = 2000L;

    private static final Logger log = Logger.getLogger(MessagesInspector.class);

    private final IMessageQueue<KeyValueMessage> _messageQueue;
    private final String _replaceableWord;
    private final String _replacementWord;

    public MessagesInspector(IMessageQueue<KeyValueMessage> messageQueue,
                             String replaceableWord, String replacementWord) {
        _messageQueue = messageQueue;
        _replaceableWord = replaceableWord;
        _replacementWord = replacementWord;
    }

    public void run() {
        try {
            Thread.currentThread();
            Thread.sleep(EXECUTION_DELAY);
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        }

        MessageQueueIterator<KeyValueMessage> iterator =
                (MessageQueueIterator<KeyValueMessage>) _messageQueue.iterator();
        while (true) {
            log.debug("try to get next message to check. queue size=" + _messageQueue.size());
            if (iterator.hasNext()) {
                updateIfNecessary(iterator.next());
                iterator.complete();
            } else {
                //statement just for testing as way to break the thread
                if (_messageQueue.size() == 0) {
                    log.info("break message inspector thread.");
                    break;
                }
            }
        }
    }

    private void updateIfNecessary(KeyValueMessage message) {
        if (message != null) {
            if (message.getValue().equals(_replaceableWord)) {
                log.debug("update message with key=" + message.getKey());
                message.setValue(_replacementWord);
            }
        }
    }
}
