package org.itsallcode.openfasttrace.importer.asciidoc;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * {@link ImporterFactory} for AsciiDoc files
 */
public class AsciiDocImporterFactory extends RegexMatchingImporterFactory
{
    /** Creates a new instance. */
    public AsciiDocImporterFactory()
    {
        super("(?i).*\\.asciidoc");
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new AsciiDocImporter(file, listener);
    }
}
