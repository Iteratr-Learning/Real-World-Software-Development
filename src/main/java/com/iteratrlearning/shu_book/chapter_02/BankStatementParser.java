package com.iteratrlearning.shu_book.chapter_02;

import java.util.List;

public interface BankStatementParser {
    BankStatement parseFrom(String line);
    List<BankStatement> parseLinesFrom(List<String> lines);
}
