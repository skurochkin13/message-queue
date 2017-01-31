package com.testtask;

import com.testtask.inputfilegenerator.FileGeneratorImpl;
import org.junit.Ignore;

import java.util.List;

/**
 * Just quick an example.
 *
 */
public class TestApp
{
    private static final int LINES_NUMBER = 100;
    private static final int FILES_NUMBER = 3;
    private static final String DEFAULT_REPLACEABLE_WORD = "jujube";
    private static final String DEFAULT_REPLACEMENT_WORD = "cookie";

    @Ignore
    public void test() {
        // generate test file(s) with input for Producer
        FileGeneratorImpl fileGenerator = new FileGeneratorImpl(LINES_NUMBER);
        List<String> inputFiles = fileGenerator.generateInputFile(FILES_NUMBER);
        // specify replaceable/replacement words and execute
        (new App(DEFAULT_REPLACEABLE_WORD, DEFAULT_REPLACEMENT_WORD, inputFiles)).execute();
    }
}
