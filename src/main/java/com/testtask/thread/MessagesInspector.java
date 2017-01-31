package com.testtask.thread;

import com.testtask.message.KeyValueMessage;
import com.testtask.queue.IMessageQueue;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * Controller of messages in queue.
 * Iterates across queue and replaces values of message if it needed.
 *
 */
public class MessagesInspector implements Runnable
{
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
        Iterator<KeyValueMessage> iterator = _messageQueue.iterator();
        while (true) {
            log.debug("try to get next message to check. queue size=" + _messageQueue.size());
            if (iterator.hasNext()) {
                updateIfNecessary(iterator.next());
            } else {
                log.info("break message inspector thread.");
                break;
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
