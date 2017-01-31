package com.testtask.inputfilegenerator;

import java.util.List;

/**
 * Interface of testing files generator.
 *
 */
public interface IFileGenerator
{
    String generateInputFile();
    List<String> generateInputFile(int numFiles);
}
