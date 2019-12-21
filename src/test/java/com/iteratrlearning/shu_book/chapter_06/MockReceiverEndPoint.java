package com.iteratrlearning.shu_book.chapter_06;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

// tag::mockReceiverEndPoint[]
public class MockReceiverEndPoint implements ReceiverEndPoint
{
    private final List<Twoot> receivedTwoots = new ArrayList<>();

    @Override
    public void onTwoot(final Twoot twoot)
    {
        receivedTwoots.add(twoot);
    }

    public void verifyOnTwoot(final Twoot twoot)
    {
        assertThat(
            receivedTwoots,
            contains(twoot));
    }
}
// end::mockReceiverEndPoint[]