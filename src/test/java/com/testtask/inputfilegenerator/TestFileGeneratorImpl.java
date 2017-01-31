package com.testtask.inputfilegenerator;

import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnits for FileGeneratorImpl.
 *
 */
public class TestFileGeneratorImpl
{
    private static final int LINES_NUMBER = 100;
    private static final int LINES_NUMBER_INVALID = -1;
    private static final int FILES_NUMBER = 3;
    private static final int FILES_NUMBER_INVALID = 0;

    @Test
    public void testGenerateInputFileOneFile() throws IOException {
        IFileGenerator generator = new FileGeneratorImpl(LINES_NUMBER);
        String fileName = generator.generateInputFile();
        File file = new File(fileName);
        assertTrue(file.exists());
        int counter = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            while (in.readLine() != null) {
                counter++;
            }
        }
        assertEquals(LINES_NUMBER, counter);
    }

    @Test
    public void testGenerateInputFileInvalidArgs() throws IOException {
        IFileGenerator generator = new FileGeneratorImpl(LINES_NUMBER_INVALID);
        List<String> fileName = generator.generateInputFile(FILES_NUMBER_INVALID);
        assertEquals(1, fileName.size());
        File file = new File(fileName.get(0));
        assertTrue(file.exists());
        int counter = getLinesNumber(file);
        assertEquals(LINES_NUMBER, counter);
    }

    @Test
    public void testGenerateInputFileSeveralFiles() {
        IFileGenerator generator = new FileGeneratorImpl(LINES_NUMBER);
        List<String> fileName = generator.generateInputFile(FILES_NUMBER);
        for (String name : fileName) {
            File file = new File(name);
            assertTrue(file.exists());
        }
    }

    private int getLinesNumber(File file) throws IOException {
        int counter = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            while (in.readLine() != null) {
                counter++;
            }
        }
        return counter;
    }
}
