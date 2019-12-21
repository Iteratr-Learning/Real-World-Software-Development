package com.iteratrlearning.shu_book.chapter_04;

import java.io.File;
import java.io.IOException;

// tag::importer[]
interface Importer {
    Document importFile(File file) throws IOException;
}
// end::importer[]
