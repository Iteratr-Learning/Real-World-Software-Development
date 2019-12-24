package com.iteratrlearning.shu_book.chapter_03;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class BankStatementProcessorTest {

    @Test
    public void shouldFilterTransactionsInFebruary() {

        final BankTransaction februarySalary
                = new BankTransaction(LocalDate.of(2019, Month.FEBRUARY, 14), 2_000, "Salary");

        final BankTransaction marchRoyalties
                = new BankTransaction(LocalDate.of(2019, Month.MARCH, 20), 500, "Royalties");

        final List<BankTransaction> bankTransactions
                = List.of(februarySalary,
                          marchRoyalties);

        final BankStatementProcessor bankStatementProcessor = new BankStatementProcessor(bankTransactions);
        final List<BankTransaction> transactions
                = bankStatementProcessor.findTransactions(new BankTransactionIsInFebruaryAndExpensive());

        assertThat(transactions, contains(februarySalary));
        assertThat(transactions, hasSize(1));
    }


    class BankTransactionIsInFebruaryAndExpensive implements BankTransactionFilter {
        @Override
        public boolean test(final BankTransaction bankTransaction) {
            return bankTransaction.getDate().getMonth() == Month.FEBRUARY && bankTransaction.getAmount() >= 1_000;
        }
    }
}