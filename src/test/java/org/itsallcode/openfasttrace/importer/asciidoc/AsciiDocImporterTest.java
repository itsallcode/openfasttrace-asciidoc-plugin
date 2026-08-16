package org.itsallcode.openfasttrace.importer.asciidoc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import org.itsallcode.openfasttrace.api.core.LocatedSpecificationItemId;
import org.itsallcode.openfasttrace.api.core.SourcePosition;
import org.itsallcode.openfasttrace.api.core.SourceRange;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsciiDocImporterTest
{
    @Mock
    private ImportEventListener listener;
    private InOrder inOrder;

    private static LocatedSpecificationItemId locatedIdAtLine(final String id, final int line)
    {
        final SpecificationItemId expectedId = new SpecificationItemId.Builder(id).build();
        final SourceRange expectedRange = new SourceRange(new SourcePosition(line, 0),
                new SourcePosition(line + 1, 0));
        return argThat(locatedId -> expectedId.equals(locatedId.getId())
                && expectedRange.equals(locatedId.getRange())
                && locatedId.getArtifactTypeRange().isEmpty()
                && locatedId.getNameRange().isEmpty()
                && locatedId.getRevisionRange().isEmpty());
    }

    private static int findBlockStartLine(final String content)
    {
        final var lines = content.lines().toList();
        for (int line = 0; line < lines.size(); line++)
        {
            if ("## A Requirement".equals(lines.get(line)) || "====".equals(lines.get(line)))
            {
                return line;
            }
        }
        throw new IllegalArgumentException("No specification item block start found");
    }

    @BeforeEach
    void setup()
    {
        inOrder = inOrder(listener);
    }

    @ParameterizedTest
    @ValueSource(strings =
    {
            """
                    # Spec

                    [.specitem]
                    ## A Requirement
                    """,
            """
                    # Spec

                    [.specitem]
                    ====
                    ====
                    """
    })
    void testImporterIgnoresSpecItemWithoutId(final String content)
    {
        final var importer = new AsciiDocImporter(content, listener);
        importer.runImport();
        verifyNoMoreInteractions(listener);
    }

    // [utest->dsn~adoc-specification-item-markup~1]
    // [utest->dsn~adoc-specification-item-id~1]
    // [utest->dsn~adoc-specification-item-title~1]
    // [utest->dsn~adoc-specification-item-description~1]
    // [utest->dsn~adoc-specification-item-rationale~1]
    // [utest->dsn~adoc-specification-item-comment~1]
    // [utest->dsn~adoc-depends-list~1]
    // [utest->dsn~adoc-covers-list~1]
    // [utest->dsn~adoc-needs-coverage-list~1]
    @ParameterizedTest
    @ValueSource(strings =
    {
            """
                    # Spec

                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1", oft-tags="Priority1, OtherComponent"]
                    ## A Requirement

                    The description

                    [.rationale]
                    The rationale

                    [.comment]
                    A comment
                    """,
            """
                    # Spec

                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1", oft-tags="Priority1, OtherComponent"]
                    ## A Requirement

                    [.rationale]
                    The rationale

                    [.description]
                    The description

                    [.comment]
                    A comment
                    """,
            """
                    # Spec

                    .A Requirement
                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1", oft-tags="Priority1, OtherComponent"]
                    ====

                    The description

                    [.rationale]
                    The rationale

                    [.comment]
                    A comment
                    ====
                    """,
            """
                    # Spec

                    .A Requirement
                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1", oft-tags="Priority1, OtherComponent"]
                    ====

                    [.rationale]
                    The rationale

                    [.description]
                    The description

                    [.comment]
                    A comment
                    ====
                    """
    })
    void testImporterReadsFullSpecItem(final String content)
    {
        final var importer = new AsciiDocImporter(content, listener);
        importer.runImport();
        final int line = findBlockStartLine(content);
        inOrder.verify(listener).beginSpecificationItem();
        verify(listener).setId(locatedIdAtLine("dsn~detail-design~1", line));
        verify(listener).setLocation(any());
        verify(listener).setTitle("A Requirement");
        verify(listener).addDependsOnId(locatedIdAtLine("dsn~grand-design~1", line));
        verify(listener).addDependsOnId(locatedIdAtLine("arch~general-constraints~1", line));
        verify(listener).addNeededArtifactType("impl");
        verify(listener).addNeededArtifactType("utest");
        verify(listener).addCoveredId(locatedIdAtLine("req~first-requirement~1", line));
        verify(listener).addCoveredId(locatedIdAtLine("req~second-requirement~1", line));
        verify(listener).addTag("Priority1");
        verify(listener).addTag("OtherComponent");
        verify(listener).appendDescription("The description");
        verify(listener).appendRationale("The rationale");
        verify(listener).appendComment("A comment");
        inOrder.verify(listener).endSpecificationItem();
        verifyNoMoreInteractions(listener);
    }

    // [utest->dsn~adoc-artifact-forwarding-notation~1]
    @Test
    void testImporterReadsForwardingSpecItem()
    {
        final var content = """
                # Spec

                ## Design

                [.specitem, oft-skipped="dsn", oft-needs="impl, utest", oft-covers="req~first-requirement~1"]
                --
                --
                """;
        final var importer = new AsciiDocImporter(content, listener);
        importer.runImport();
        inOrder.verify(listener).beginSpecificationItem();
        verify(listener).setId(locatedIdAtLine("dsn~first-requirement~1", 5));
        verify(listener).setLocation(
                argThat(location -> "verbatim".equals(location.getPath()) && location.getLine() == 6));
        verify(listener).addNeededArtifactType("impl");
        verify(listener).addNeededArtifactType("utest");
        verify(listener).addCoveredId(locatedIdAtLine("req~first-requirement~1", 5));
        inOrder.verify(listener).endSpecificationItem();
        verifyNoMoreInteractions(listener);
    }

    @Test
    void testImporterReadsSpecItemNestedInTableCell()
    {
        final var content = """
                .Test Table
                [%autowidth]
                |===
                |Column 1 |Column 2

                |Default Style
                a|
                Asciidoc Style

                [.specitem, oft-sid="req~nested-in-table~1", oft-needs="dsn"]
                --
                The description
                --
                |===
                """;
        final var importer = new AsciiDocImporter(content, listener);
        importer.runImport();
        inOrder.verify(listener).beginSpecificationItem();
        verify(listener).setId(locatedIdAtLine("req~nested-in-table~1", 10));
        verify(listener).setLocation(
                argThat(location -> "verbatim".equals(location.getPath())));
        verify(listener).addNeededArtifactType("dsn");
        verify(listener).appendDescription("The description");
        inOrder.verify(listener).endSpecificationItem();
        verifyNoMoreInteractions(listener);
    }
}
