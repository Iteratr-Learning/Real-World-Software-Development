package com.iteratrlearning.shu_book.chapter_06.database;

import java.sql.*;

class StatementRunner {
    private final Connection conn;

    StatementRunner(final Connection conn) {
        this.conn = conn;
    }

    // tag::extract[]
    <R> R extract(final String sql, final Extractor<R> extractor) {
        try (var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.clearParameters();
            return extractor.run(stmt);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
    // end::extract[]

    void withStatement(final String sql, final With<PreparedStatement> withPreparedStatement) {
        extract(sql, stmt -> {
            withPreparedStatement.run(stmt);
            return null;
        });
    }

    void update(final String sql) {
        withStatement(sql, PreparedStatement::execute);
    }

    void query(final String sql, final With<ResultSet> withPreparedStatement) {
        withStatement(sql, statement -> {
            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                withPreparedStatement.run(resultSet);
            }
        });
    }
}
