package com.iteratrlearning.shu_book.chapter_06.database;

import com.iteratrlearning.shu_book.chapter_06.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseUserRepository implements UserRepository {

    private static final int ID = 1;
    private static final int PASSWORD = 2;
    private static final int SALT = 3;
    private static final int POSITION = 4;

    private static final int FOLLOWER = 1;
    private static final int USER_TO_FOLLOW = 2;

    private final Connection conn;
    private final StatementRunner statementRunner;

    private final Map<String, User> userIdToUser;

    public DatabaseUserRepository() {
        try {
            conn = DatabaseConnection.get();
            createTables();
            statementRunner = new StatementRunner(conn);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        userIdToUser = loadFromDatabase();
    }

    private void createTables() throws SQLException {
        conn.createStatement()
            .executeUpdate(
                "CREATE TABLE IF NOT EXISTS " +
                    "users (" +
                    "id VARCHAR(15) NOT NULL," +
                    "password VARBINARY(20) NOT NULL," +
                    "salt VARBINARY(16) NOT NULL," +
                    "position INT NOT NULL" +
                    ")");
        conn.createStatement()
            .executeUpdate(
                "CREATE TABLE IF NOT EXISTS " +
                    "followers (" +
                    "follower VARCHAR(15) NOT NULL," +
                    "userToFollow VARCHAR(15) NOT NULL" +
                    ")");
    }

    private Map<String, User> loadFromDatabase() {
        var users = new HashMap<String, User>();
        statementRunner.query("SELECT id, password, salt, position FROM users", resultSet -> {
            var id = resultSet.getString(ID);
            var password = resultSet.getBytes(PASSWORD);
            var salt = resultSet.getBytes(SALT);
            var position = new Position(resultSet.getInt(POSITION));
            var user = new User(id, password, salt, position);
            users.put(id, user);
        });
        statementRunner.query("SELECT follower, userToFollow from followers", resultSet -> {
            var followerId = resultSet.getString(FOLLOWER);
            var userToFollowId = resultSet.getString(USER_TO_FOLLOW);
            var follower = users.get(followerId);
            var userToFollow = users.get(userToFollowId);
            userToFollow.addFollower(follower);
        });
        return users;
    }

    @Override
    public Optional<User> get(final String userId) {
        return Optional.ofNullable(userIdToUser.get(userId));
    }

    @Override
    public boolean add(final User user) {
        final String userId = user.getId();

        var success = userIdToUser.putIfAbsent(userId, user) == null;

        if (success) {
            statementRunner.withStatement(
                "INSERT INTO users (id, password, salt, position) VALUES (?,?,?,?)", stmt -> {
                    stmt.setString(ID, userId);
                    stmt.setBytes(PASSWORD, user.getPassword());
                    stmt.setBytes(SALT, user.getSalt());
                    stmt.setInt(POSITION, user.getLastSeenPosition().getValue());
                    stmt.executeUpdate();
                });
        }

        return success;
    }

    @Override
    public void update(final User user) {
        statementRunner.withStatement(
            "UPDATE users SET position=? WHERE id=?", stmt -> {
                stmt.setInt(1, user.getLastSeenPosition().getValue());
                stmt.setString(2, user.getId());
                stmt.executeUpdate();
            });
    }

    @Override
    public FollowStatus follow(final User follower, final User userToFollow) {
        var followStatus = userToFollow.addFollower(follower);
        if (followStatus == FollowStatus.SUCCESS) {
            statementRunner.withStatement(
                "INSERT INTO followers (follower, userToFollow) VALUES (?,?)", stmt -> {
                    stmt.setString(FOLLOWER, follower.getId());
                    stmt.setString(USER_TO_FOLLOW, userToFollow.getId());
                    stmt.executeUpdate();
                });
        }
        return followStatus;
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }

    public void clear() {
        statementRunner.update("delete from users");
        statementRunner.update("delete from followers");
        userIdToUser.clear();
    }

}
