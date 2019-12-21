package com.iteratrlearning.shu_book.chapter_06.examples;

import com.iteratrlearning.shu_book.chapter_06.ReceiverEndPoint;
import com.iteratrlearning.shu_book.chapter_06.Twoot;


public class PrintingEndPointSnippets {
    public static void main(String[] args) {
// tag::anonymous_class[]
        final ReceiverEndPoint anonymousClass = new ReceiverEndPoint() {
            @Override
            public void onTwoot(final Twoot twoot) {
                System.out.println(twoot.getSenderId() + ": " + twoot.getContent());
            }
        };
// end::anonymous_class[]

// tag::lambda[]
        final ReceiverEndPoint lambda =
            twoot -> System.out.println(twoot.getSenderId() + ": " + twoot.getContent());
// end::lambda[]

    }

}

