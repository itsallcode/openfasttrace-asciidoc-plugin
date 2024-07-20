package org.itsallcode.openfasttrace.importer.asciidoc;

import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

class AsciiDocImporter implements Importer
{
    private static final Logger LOGGER = Logger.getLogger(AsciiDocImporter.class.getName());
    private final InputFile file;
    private final ImportEventListener listener;

    AsciiDocImporter(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
        this.listener = listener;
    }

    @Override
    public void runImport()
    {
        LOGGER.finest(() -> "Importing AsciiDoc file '" + file + "'");
    }
}
