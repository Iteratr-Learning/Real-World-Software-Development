package com.iteratrlearning.shu_book.chapter_03;

@FunctionalInterface
public interface BankTransactionFilter {
    boolean test(BankTransaction bankTransaction);
}