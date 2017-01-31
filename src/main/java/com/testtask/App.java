package com.testtask;

import com.testtask.common.Common;
import com.testtask.inputfilegenerator.FileGeneratorImpl;
import com.testtask.inputfilegenerator.IFileGenerator;
import com.testtask.message.KeyValueMessage;
import com.testtask.queue.IMessageQueue;
import com.testtask.queue.MessageQueueImpl;
import com.testtask.thread.Consumer;
import com.testtask.thread.MessagesInspector;
import com.testtask.thread.Producer;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.testtask.common.Common.DEFAULT_LINES_NUMBER;

/**
 * Entry point.
 *
 */
public class App
{
    private static final String DEFAULT_REPLACEABLE_WORD = "jujube";
    private static final String DEFAULT_REPLACEMENT_WORD = "cookie";

    private static final Logger log = Logger.getLogger(App.class);

    private final MessagesInspector _messagesInspector;
    private final List<Producer> _producers;
    private final Consumer _consumer;

    public App(String replaceableWord, String replacementWord, List<String> inputFiles) {
        StringBuilder builder = new StringBuilder("replaceableWord=" + replaceableWord).
                append(", ").append("replacementWord=").append(replacementWord).append(", ").
                append("inputFiles=").append(inputFiles);
        log.info("App constructor params: " + builder);

        IMessageQueue<KeyValueMessage> messageQueue = new MessageQueueImpl<>();
        _producers = new ArrayList<>();
        for (String file : inputFiles) {
            Producer producer = new Producer(messageQueue, file);
            _producers.add(producer);
        }
        _messagesInspector = new MessagesInspector(messageQueue, replaceableWord, replacementWord);
        _consumer = new Consumer(messageQueue, Common.getFilePath(inputFiles.get(0)) + "ResultFile");
    }

    /*
     * java -jar <name>.jar jujube cookie c:\TestApp\InputFile0
     * args[0] - replaceableWord (e.g. jujube),
     * args[1] - replacementWord (e.g. cookie),
     * args[n], n > 2 - input files names.
     */
    public static void main( String[] args ) {
        String replaceableWord = null;
        String replacementWord = null;
        IFileGenerator fileGenerator = new FileGeneratorImpl(DEFAULT_LINES_NUMBER);
        List<String> inputFiles = new ArrayList<>();
        //List<String> inputFiles = new ArrayList<>(fileGenerator.generateInputFile(7));

        log.info("parse args: " + Arrays.toString(args));
        if (args.length > 0) {
            int i = 0;
            for (String param : args) {
                if (i == 0) {
                    replaceableWord = param;
                } else if (i == 1) {
                    replacementWord = param;
                } else {
                    inputFiles.add(param);
                }
                i++;
            }
        }

        if (replaceableWord == null) replaceableWord = DEFAULT_REPLACEABLE_WORD;
        if (replacementWord == null) replacementWord = DEFAULT_REPLACEMENT_WORD;
        if (inputFiles.size() == 0) inputFiles.add(fileGenerator.generateInputFile());

        (new App(replaceableWord, replacementWord, inputFiles)).execute();
    }

    public void execute() {
        log.info("start execution.");
        ExecutorService executor = Executors.newCachedThreadPool();
        for (Producer producer : _producers) {
            executor.execute(producer);
        }
        executor.execute(_messagesInspector);
        executor.execute(_consumer);
        executor.shutdown();
    }
}
