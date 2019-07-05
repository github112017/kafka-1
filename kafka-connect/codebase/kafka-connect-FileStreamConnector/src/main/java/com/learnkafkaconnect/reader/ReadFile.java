package com.learnkafkaconnect.reader;

import com.learnkafkaconnect.task.FileStreamSourceTaskV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {

    private static final Logger log = LoggerFactory.getLogger(FileStreamSourceTaskV2.class);


    public List<String> readFileContent(String fileName , int startingLineNumber, int endingLineNumber) throws IOException {

        List<String> strings = new ArrayList<>();
        LineNumberReader lineNumberReader =  null;
        try{
            String lineValue=null;
            lineNumberReader = new LineNumberReader(new FileReader(fileName));
            while ((lineValue = lineNumberReader.readLine())!=null){
                int lineNumber = lineNumberReader.getLineNumber();
                log.info("line Number  {} , line Value : {} ", lineNumber , lineValue);
                if(lineNumber>=startingLineNumber &&lineNumber<=endingLineNumber)
                    strings.add(lineValue);
            }
        }catch (IOException e){
            log.error("Exception Occurred in readFileContent : " + e);
        }finally {
            if(lineNumberReader!=null)
                lineNumberReader.close();
        }
        return  strings;
    }

    public static void main(String[] args) {

        //Users/z001qgd/Dilip/study/Tech-Reference-Guide/codebase/kafka/kafka-connect/codebase/kafka-connect-FileStreamConnector/src/test/resources/data/testing.txt


    }

}
