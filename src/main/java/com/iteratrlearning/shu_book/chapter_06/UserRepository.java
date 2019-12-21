package com.iteratrlearning.shu_book.chapter_06;

import java.util.Optional;

// tag::UserRepository[]
public interface UserRepository extends AutoCloseable {
    boolean add(User user);

    Optional<User> get(String userId);

    void update(User user);

    void clear();

    FollowStatus follow(User follower, User userToFollow);
}
// end::UserRepository[]
