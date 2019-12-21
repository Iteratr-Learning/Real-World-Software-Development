package com.iteratrlearning.shu_book.chapter_02;

public class MainApplication {

    public static void main(String[] args) throws Exception {

        BankStatementAnalyzer bankStatementAnalyzer
                = new BankStatementAnalyzer();

        BankStatementParser bankStatementParser
                = new BankStatementCSVParser();

        bankStatementAnalyzer.analyze("bank-data-simple.csv", bankStatementParser);

    }
}
