package com.iteratrlearning.shu_book.chapter_02;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

// tag::analyzersimple[]
public class BankStatementAnalyzerSimple {

    private static final String RESOURCES = "src/test/resources/";

    public static void main(String[] args) throws Exception {
            Path path = Paths.get(RESOURCES + "bank-data-simple.csv");
            List<String> lines = Files.readAllLines(path);
            double total = 0;
            for(String line: lines) {
                String[] columns = line.split(",");
                double amount = Double.parseDouble(columns[1]);
                total += amount;
            }

            System.out.println("The total for all transactions is " + total);
    }
}
// end::analyzersimple[]
