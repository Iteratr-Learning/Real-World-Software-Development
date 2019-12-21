package com.iteratrlearning.shu_book.chapter_06;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.iteratrlearning.shu_book.chapter_06.DeleteStatus.NOT_YOUR_TWOOT;
import static com.iteratrlearning.shu_book.chapter_06.DeleteStatus.SUCCESS;
import static com.iteratrlearning.shu_book.chapter_06.DeleteStatus.UNKNOWN_TWOOT;
import static com.iteratrlearning.shu_book.chapter_06.FollowStatus.INVALID_USER;
import static com.iteratrlearning.shu_book.chapter_06.Position.INITIAL_POSITION;

public class Twootr {

    private final TwootRepository twootRepository;
    private final UserRepository userRepository;

    public Twootr(final UserRepository userRepository, final TwootRepository twootRepository) {
        this.userRepository = userRepository;
        this.twootRepository = twootRepository;
    }

    public Optional<SenderEndPoint> onLogon(
        final String userId, final String password, final ReceiverEndPoint receiverEndPoint) {

        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(password, "password");

        // tag::optional_onLogon[]
        var authenticatedUser = userRepository
            .get(userId)
            .filter(userOfSameId ->
            {
                var hashedPassword = KeyGenerator.hash(password, userOfSameId.getSalt());
                return Arrays.equals(hashedPassword, userOfSameId.getPassword());
            });

        authenticatedUser.ifPresent(user ->
        {
            user.onLogon(receiverEndPoint);
            twootRepository.query(
                new TwootQuery()
                    .inUsers(user.getFollowing())
                    .lastSeenPosition(user.getLastSeenPosition()),
                user::receiveTwoot);
            userRepository.update(user);
        });

        return authenticatedUser.map(user -> new SenderEndPoint(user, this));
        // end::optional_onLogon[]
    }

    public RegistrationStatus onRegisterUser(final String userId, final String password) {
        var salt = KeyGenerator.newSalt();
        var hashedPassword = KeyGenerator.hash(password, salt);
        var user = new User(userId, hashedPassword, salt, INITIAL_POSITION);
        return userRepository.add(user) ? RegistrationStatus.SUCCESS : RegistrationStatus.DUPLICATE;
    }

    FollowStatus onFollow(final User follow, final String userIdToFollow) {
        return userRepository.get(userIdToFollow)
            .map(userToFollow -> userRepository.follow(follow, userToFollow))
            .orElse(INVALID_USER);
    }

    Position onSendTwoot(final String id, final User user, final String content) {
        var userId = user.getId();
        var twoot = twootRepository.add(id, userId, content);
        // tag::stream_onSendTwoot[]
        user.followers()
            .filter(User::isLoggedOn)
            .forEach(follower ->
            {
                follower.receiveTwoot(twoot);
                userRepository.update(follower);
            });
        // end::stream_onSendTwoot[]

        return twoot.getPosition();
    }

    DeleteStatus onDeleteTwoot(final String userId, final String id) {
        return twootRepository
            .get(id)
            .map(twoot ->
            {
                var canDeleteTwoot = twoot.getSenderId().equals(userId);
                if (canDeleteTwoot) {
                    twootRepository.delete(twoot);
                }
                return canDeleteTwoot ? SUCCESS : NOT_YOUR_TWOOT;
            })
            .orElse(UNKNOWN_TWOOT);
    }
}
