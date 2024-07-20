package org.itsallcode.openfasttrace.importer.asciidoc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class AsciiDocImporterTest
{
    @Test
    void dummyTest()
    {
        assertDoesNotThrow(() -> new AsciiDocImporter(null, null).runImport());
    }
}
