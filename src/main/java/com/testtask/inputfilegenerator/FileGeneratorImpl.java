package com.testtask.inputfilegenerator;

import com.testtask.common.Common;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.testtask.common.Common.KEY_VALUE_DELIMITER;
import static com.testtask.common.Common.DEFAULT_LINES_NUMBER;
import static com.testtask.common.Common.FILE_PATH;

/**
 * Implementation of files for testing generator.
 *
 */
public class FileGeneratorImpl implements IFileGenerator
{
    private static final String PATH = Common.getPropertyValue(FILE_PATH);
    private static final String FILE_NAME = PATH + "InputFile";
    private static final int VALUES_ARRAY_SIZE = 10;
    private static final int MIN_KEY_VALUE = 1000;
    private static final int MAX_KEY_VALUE = 9000;
    private static final String[] valueNames =
            {"jujube", "cookie", "apple", "bread", "milk", "tea", "oranges", "meat", "tomato", "chocolate"};

    private static final Logger log = Logger.getLogger(FileGeneratorImpl.class);

    private static int fileCount = 0;
    private final Random _random;
    private final int _linesNumber;

    public FileGeneratorImpl(int linesNumber) {
        _random = new Random();
        _linesNumber = linesNumber > 0 ? linesNumber : DEFAULT_LINES_NUMBER;
    }

    public String generateInputFile() {
        String fileName = getTestFile();
        populateFile(fileName);
        log.debug("generated input file: " + fileName);
        return fileName;
    }

    public List<String> generateInputFile(int numFiles) {
        int fileCount = numFiles > 0 ? numFiles : 1;
        List<String> filesList = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            String fileName = getTestFile();
            populateFile(fileName);
            filesList.add(fileName);
        }
        log.debug("generated input files: " + filesList);
        return filesList;
    }

    private static String getTestFile() {
        return FILE_NAME + fileCount++;
    }

    private String getNextKeyValueString() {
        int keyVal = MIN_KEY_VALUE + _random.nextInt(MAX_KEY_VALUE);
        return keyVal + KEY_VALUE_DELIMITER + valueNames[_random.nextInt(VALUES_ARRAY_SIZE)];
    }

    private void populateFile(String fileName) {
        File file = new File(fileName);
        Common.createPathIfNeeded(PATH);
        try {
            if (file.exists()) {
                if (file.delete())
                    log.info(fileName + " old file deleted");
            }

            if (file.createNewFile()) {
                StringBuilder input = new StringBuilder();
                for (int i = 0; i < _linesNumber; i++) {
                    input.append(getNextKeyValueString()).append("\n");
                }

                try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                    out.print(input.toString());
                }
            }
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}

