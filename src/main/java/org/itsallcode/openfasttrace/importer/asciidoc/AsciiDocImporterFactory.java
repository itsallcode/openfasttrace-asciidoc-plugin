package org.itsallcode.openfasttrace.importer.asciidoc;

import java.util.Objects;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * A factory for {@link AsciiDocImporter}s.
 */
public class AsciiDocImporterFactory extends AbstractRegexMatchingImporterFactory
{
    /**
     * Creates a new importer for AsciiDoc files having an {@code .adoc} suffix.
     */
    // [impl->dsn~asciidoc-file-extensions~1]
    public AsciiDocImporterFactory()
    {
        super("(?i).*\\.adoc");
    }

    @Override
    public int getPriority() {
        return 9100; // Behind the Gherkin importer but before the generic ones
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new AsciiDocImporter(Objects.requireNonNull(file), Objects.requireNonNull(listener));
    }
}
