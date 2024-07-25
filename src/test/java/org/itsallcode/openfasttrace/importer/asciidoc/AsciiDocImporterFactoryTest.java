package org.itsallcode.openfasttrace.importer.asciidoc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AsciiDocImporterFactoryTest
{
    // [utest->dsn~asciidoc-file-extensions~1]
    @Test
    void testFactorySupportsAsciiDocFileExtension()
    {
        final var factory = new AsciiDocImporterFactory();
        final var inputFile = Mockito.mock(InputFile.class);
        when(inputFile.getPath()).thenReturn("/non/existing/path/to/file.adoc");
        assertTrue(factory.supportsFile(inputFile));
    }
}
