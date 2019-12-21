package com.iteratrlearning.shu_book.chapter_06;

import com.iteratrlearning.shu_book.chapter_06.in_memory.InMemoryTwootRepository;
import org.junit.Before;

public class InMemoryTwootRepositoryTest extends AbstractTwootRepositoryTest
{
    @Before
    public void setUp()
    {
        repository = new InMemoryTwootRepository();

    }
}
