package com.iteratrlearning.shu_book.chapter_06.examples;

import com.iteratrlearning.shu_book.chapter_06.ReceiverEndPoint;
import com.iteratrlearning.shu_book.chapter_06.Twoot;

// tag::PrintingEndPoint[]
public class PrintingEndPoint implements ReceiverEndPoint {
    @Override
    public void onTwoot(final Twoot twoot) {
        System.out.println(twoot.getSenderId() + ": " + twoot.getContent());
    }
}
// end::PrintingEndPoint[]
