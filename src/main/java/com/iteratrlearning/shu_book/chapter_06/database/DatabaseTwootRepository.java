package com.iteratrlearning.shu_book.chapter_06.database;

import com.iteratrlearning.shu_book.chapter_06.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DatabaseTwootRepository implements TwootRepository {
    private final StatementRunner statementRunner;
    private Position position = Position.INITIAL_POSITION;

    public DatabaseTwootRepository() {
        try {
            var conn = DatabaseConnection.get();
            conn.createStatement()
                .executeUpdate(
                    "CREATE TABLE IF NOT EXISTS " +
                        "twoots (" +
                        "position INT IDENTITY," +
                        "id VARCHAR(36) UNIQUE NOT NULL," +
                        "senderId VARCHAR(15) NOT NULL," +
                        "content VARCHAR(140) NOT NULL" +
                        ")");
            statementRunner = new StatementRunner(conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void query(final TwootQuery twootQuery, final Consumer<Twoot> callback) {
        if (!twootQuery.hasUsers()) {
            return;
        }

        var lastSeenPosition = twootQuery.getLastSeenPosition();
        var inUsers = twootQuery.getInUsers();

        statementRunner.query(
            "SELECT * " +
                "FROM   twoots " +
                "WHERE  senderId IN " + usersTuple(inUsers) +
                "AND    twoots.position > " + lastSeenPosition.getValue(), rs ->

                callback.accept(extractTwoot(rs)));
    }

    private Twoot extractTwoot(final ResultSet rs) throws SQLException {
        var position = new Position(rs.getInt(1));
        var id = rs.getString(2);
        var senderId = rs.getString(3);
        var content = rs.getString(4);
        return new Twoot(id, senderId, content, position);
    }

    @Override
    public Optional<Twoot> get(final String id) {
        return statementRunner.extract(
            "SELECT * FROM twoots WHERE id = ?",
            stmt ->
            {
                stmt.setString(1, id);
                var resultSet = stmt.executeQuery();
                return resultSet.next()
                    ? Optional.of(extractTwoot(resultSet))
                    : Optional.empty();
            });
    }

    @Override
    public void delete(final Twoot twoot) {
        statementRunner.withStatement(
            "DELETE FROM twoots WHERE position = ?",
            stmt -> {
                stmt.setInt(1, position.getValue());
                stmt.executeUpdate();
            });
    }

    // tag::usersTupleLoop[]
    private String usersTupleLoop(final Set<String> following) {
        List<String> quotedIds = new ArrayList<>();
        for (String id : following) {
            quotedIds.add("'" + id + "'");
        }
        return '(' + String.join(",", quotedIds) + ')';
    }
    // end::usersTupleLoop[]

    // tag::usersTuple[]
    private String usersTuple(final Set<String> following) {
        return following
            .stream()
            .map(id -> "'" + id + "'")
            .collect(Collectors.joining(",", "(", ")"));
    }
    // end::usersTuple[]

    @Override
    public Twoot add(final String id, final String userId, final String content) {
        statementRunner.withStatement("INSERT INTO twoots (id, senderId, content) VALUES (?,?, ?)", stmt -> {
            stmt.setString(1, id);
            stmt.setString(2, userId);
            stmt.setString(3, content);
            stmt.executeUpdate();
            final ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                position = new Position(rs.getInt(1));
            }
        });

        return new Twoot(id, userId, content, position);
    }

    @Override
    public void clear() {
        statementRunner.update("delete from twoots");
        statementRunner.update("DROP SCHEMA PUBLIC CASCADE");
    }
}
