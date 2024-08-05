package org.itsallcode.openfasttrace.importer.asciidoc;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

class AsciiDocImporter implements Importer
{
    private static final String ATTRIBUTE_OFT_COVERS = "oft-covers";
    private static final String ATTRIBUTE_OFT_DEPENDS = "oft-depends";
    private static final String ATTRIBUTE_OFT_NEEDS = "oft-needs";
    private static final String ATTRIBUTE_OFT_SID = "oft-sid";
    private static final String ATTRIBUTE_OFT_SKIPPED = "oft-skipped";
    private static final String ATTRIBUTE_OFT_TITLE = "oft-title";

    private static final String CONTENT_MODEL_SIMPLE = "simple";

    private static final String KEY_ROLE = "role";

    private static final String ROLE_COMMENT = ":comment";
    private static final String ROLE_DESCRIPTION = ":description";
    private static final String ROLE_RATIONALE = ":rationale";
    private static final String ROLE_SPECITEM = ":specitem";

    private static final Logger LOG = Logger.getLogger(AsciiDocImporter.class.getName());

    private final InputFile file;
    private final String content;
    private final ImportEventListener listener;

    AsciiDocImporter(final String content, final ImportEventListener listener)
    {
        this.content = Objects.requireNonNull(content);
        this.file = null;
        this.listener = Objects.requireNonNull(listener);
    }

    AsciiDocImporter(final InputFile file, final ImportEventListener listener)
    {
        this.content = null;
        this.file = Objects.requireNonNull(file);
        this.listener = Objects.requireNonNull(listener);
    }

    private static List<String> getAttributeValueAsList(final StructuralNode block, final String attributeName)
    {
        return Optional.ofNullable(block.getAttribute(attributeName))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(stringAttribute -> StreamSupport.stream(Arrays.spliterator(stringAttribute.split(",")), false)
                        .map(String::trim)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    private static List<String> getCoveredIds(final StructuralNode block)
    {
        return getAttributeValueAsList(block, ATTRIBUTE_OFT_COVERS);
    }

    private Location getLocation(final StructuralNode block)
    {
        return Location.builder()
                .path(Optional.ofNullable(this.file).map(InputFile::getPath).orElse("verbatim"))
                .line(block.getSourceLocation().getLineNumber()).build();
    }

    private Optional<String> getStringContent(final StructuralNode node)
    {
        switch (node.getContentModel())
        {
        case CONTENT_MODEL_SIMPLE:
            LOG.fine("extracting SIMPLE content");
            return Optional.ofNullable(node.getContent())
                    .filter(String.class::isInstance)
                    .map(String.class::cast);
        default:
            LOG.fine(() -> "'%s' content model not (yet) supported [%s]".formatted(node.getContentModel(),
                    getLocation(node)));
            return Optional.empty();
        }
    }

    // [impl->dsn~adoc-specification-item-title~1]
    private void processSpecificationItemTitle(final StructuralNode block)
    {
        Optional.ofNullable(block.getAttribute(ATTRIBUTE_OFT_TITLE))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .or(() -> Optional.ofNullable(block.getTitle()))
                .ifPresent(s -> {
                    LOG.fine(() -> String.format("setting spec item title: %s", s));
                    this.listener.setTitle(s);
                });
    }

    // [impl->dsn~adoc-depends-list~1]
    private void processSpecificationItemDepends(final StructuralNode block)
    {
        getAttributeValueAsList(block, ATTRIBUTE_OFT_DEPENDS).stream()
                .map(dependsOnId -> new SpecificationItemId.Builder(dependsOnId).build())
                .forEach(this.listener::addDependsOnId);
    }

    // [impl->dsn~adoc-needs-coverage-list~1]
    private void processSpecificationItemNeeds(final StructuralNode block)
    {
        getAttributeValueAsList(block, ATTRIBUTE_OFT_NEEDS)
                .forEach(this.listener::addNeededArtifactType);
    }

    // [impl->dsn~adoc-covers-list~1]
    private void processSpecificationItemCovers(final StructuralNode block)
    {
        getCoveredIds(block).stream()
                .map(coversId -> new SpecificationItemId.Builder(coversId).build())
                .forEach(this.listener::addCoveredId);
    }

    // [impl->dsn~adoc-specification-item-description~1]
    private void processSpecificationItemDescription(final StructuralNode block)
    {
        final Optional<StructuralNode> descriptionBlock = block.findBy(Map.of(KEY_ROLE, ROLE_DESCRIPTION)).stream()
                .findFirst();
        descriptionBlock
                .or(() -> block.getBlocks().stream()
                        .filter(node -> !"rationale".equals(node.getRole()) && !"comment".equals(node.getRole()))
                        .findFirst())
                .flatMap(this::getStringContent)
                .ifPresent(listener::appendDescription);
    }

    // [impl->dsn~adoc-specification-item-rationale~1]
    private void processSpecificationItemRationale(final StructuralNode block)
    {
        block.findBy(Map.of(KEY_ROLE, ROLE_RATIONALE)).stream().findFirst()
                .flatMap(this::getStringContent)
                .ifPresent(listener::appendRationale);
    }

    // [impl->dsn~adoc-specification-item-comment~1]
    private void processSpecificationItemComment(final StructuralNode block)
    {
        block.findBy(Map.of(KEY_ROLE, ROLE_COMMENT)).stream().findFirst()
                .flatMap(this::getStringContent)
                .ifPresent(listener::appendComment);
    }

    private void processSpecificationItemBlock(final String sid, final StructuralNode block)
    {
        final SpecificationItemId specItemId = new SpecificationItemId.Builder(sid).build();
        final Location location = getLocation(block);
        LOG.fine(() -> String.format("adding specification item [ID: %s, location: %s]", specItemId,
                location));

        this.listener.beginSpecificationItem();
        this.listener.setId(specItemId);
        this.listener.setLocation(location);
        processSpecificationItemTitle(block);
        processSpecificationItemDepends(block);
        processSpecificationItemNeeds(block);
        processSpecificationItemCovers(block);
        processSpecificationItemDescription(block);
        processSpecificationItemRationale(block);
        processSpecificationItemComment(block);
        this.listener.endSpecificationItem();
    }

    private void processForwardingBlock(final String skippedType, final StructuralNode block)
    {
        final List<String> coveredSpecItems = getCoveredIds(block);
        if (coveredSpecItems.isEmpty() || coveredSpecItems.size() > 1)
        {
            LOG.severe(
                    () -> """
                            Cannot process .specitem [%s]. A .specitem used to forward needs must have an \
                            'oft-covers' attribute with exactly one spec item ID.
                            """.formatted(getLocation(block)));
            return;
        }
        final SpecificationItemId coveredSid = new SpecificationItemId.Builder(coveredSpecItems.get(0)).build();
        final SpecificationItemId specItemId = new SpecificationItemId.Builder()
                .artifactType(skippedType)
                .name(coveredSid.getName())
                .revision(coveredSid.getRevision())
                .build();
        final Location location = getLocation(block);
        LOG.fine(() -> "adding forwarding specification item [ID: %s, location: %s]".formatted(specItemId,
                location));

        this.listener.beginSpecificationItem();
        this.listener.setId(specItemId);
        this.listener.setLocation(location);
        this.listener.addCoveredId(coveredSid);
        processSpecificationItemNeeds(block);
        this.listener.endSpecificationItem();
    }

    // [impl->dsn~adoc-specification-item-id~1]
    // [impl->dsn~adoc-artifact-forwarding-notation~1]
    private void processSpecificationItem(final StructuralNode block)
    {
        Optional.ofNullable(block.getAttribute(ATTRIBUTE_OFT_SID))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .filter(attributeValue -> !attributeValue.isBlank())
                .ifPresentOrElse(
                        sid -> processSpecificationItemBlock(sid, block),
                        () -> Optional.ofNullable(block.getAttribute(ATTRIBUTE_OFT_SKIPPED))
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .filter(skippedItemAttributeValue -> !skippedItemAttributeValue.isBlank())
                                .ifPresentOrElse(
                                        skippedType -> processForwardingBlock(skippedType, block),
                                        () -> LOG.severe(() -> """
                                                Cannot process .specitem [%s]. A .specitem block must \
                                                have either an 'oft-sid' or an 'oft-skipped' attribute.
                                                """.formatted(getLocation(block)))));
    }

    // [impl->dsn~adoc-specification-item-markup~1]
    private Document parseAsciiDoc()
    {
        final Options options = Options.builder().sourcemap(true).build();
        try (final Asciidoctor asciidoctor = Asciidoctor.Factory.create())
        {
            if (this.file != null)
            {
                LOG.fine(() -> "AsciiDoc Importer is reading specification items from AsciiDoc file: %s"
                        .formatted(this.file.toPath()));
                return asciidoctor.loadFile(this.file.toPath().toFile(), options);
            }
            else if (this.content != null)
            {
                LOG.fine("AsciiDoc Importer is reading specification items from AsciiDoc document");
                return asciidoctor.load(content, options);
            }
            else
            {
                // this cannot happen because the constructors require either
                // file or document being provided
                throw new IllegalStateException("either input file or document must be provided");
            }
        }
    }

    // [impl->dsn~adoc-specification-item-markup~1]
    @Override
    public void runImport()
    {
        final Document document = parseAsciiDoc();

        document.findBy(Map.of(KEY_ROLE, ROLE_SPECITEM)).forEach(node -> {
            LOG.fine(() -> String.format("found specitem block [id: %s]", node.getId()));
            processSpecificationItem(node);
        });
    }
}
