package org.itsallcode.openfasttrace.importer.asciidoc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

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
        inOrder.verify(listener).beginSpecificationItem();
        verify(listener).setId(new SpecificationItemId.Builder("dsn~detail-design~1").build());
        verify(listener).setLocation(any());
        verify(listener).setTitle("A Requirement");
        verify(listener).addDependsOnId(new SpecificationItemId.Builder("dsn~grand-design~1").build());
        verify(listener).addDependsOnId(new SpecificationItemId.Builder("arch~general-constraints~1").build());
        verify(listener).addNeededArtifactType("impl");
        verify(listener).addNeededArtifactType("utest");
        verify(listener).addCoveredId(new SpecificationItemId.Builder("req~first-requirement~1").build());
        verify(listener).addCoveredId(new SpecificationItemId.Builder("req~second-requirement~1").build());
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
        verify(listener).setId(new SpecificationItemId.Builder("dsn~first-requirement~1").build());
        verify(listener).setLocation(
                argThat(location -> "verbatim".equals(location.getPath()) && location.getLine() == 6));
        verify(listener).addNeededArtifactType("impl");
        verify(listener).addNeededArtifactType("utest");
        verify(listener).addCoveredId(new SpecificationItemId.Builder("req~first-requirement~1").build());
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
        verify(listener)
                .setId(new SpecificationItemId.Builder("req~nested-in-table~1").build());
        verify(listener).setLocation(
                argThat(location -> "verbatim".equals(location.getPath())));
        verify(listener).addNeededArtifactType("dsn");
        verify(listener).appendDescription("The description");
        inOrder.verify(listener).endSpecificationItem();
        verifyNoMoreInteractions(listener);
    }
}
