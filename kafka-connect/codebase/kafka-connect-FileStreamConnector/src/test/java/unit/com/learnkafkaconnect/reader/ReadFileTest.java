package com.learnkafkaconnect.reader;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReadFileTest {

    ReadFile readFile = new ReadFile();

    @Test
    public void readFileContent() throws IOException {
        //given
        String fileName = "src/test/resources/data/testing.txt";
        int startingLineNumber = 1;
        int endingLineNumber = 2;


        //when
        List<String> fileContent = readFile.readFileContent(fileName,startingLineNumber,endingLineNumber);


        //then
        assertEquals(2, fileContent.size());

    }


}
