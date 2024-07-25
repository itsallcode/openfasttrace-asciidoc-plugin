package org.itsallcode.openfasttrace.importer.asciidoc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AsciiDocImporterTest
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
        Mockito.verifyNoMoreInteractions(listener);
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

                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1"]
                    ## A Requirement

                    The description

                    [.rationale]
                    The rationale

                    [.comment]
                    A comment
                    """,
            """
                    # Spec

                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1"]
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
                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1"]
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
                    [.specitem, oft-sid="dsn~detail-design~1", oft-depends="dsn~grand-design~1, arch~general-constraints~1", oft-needs="impl, utest", oft-covers="req~first-requirement~1, req~second-requirement~1"]
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
        Mockito.verify(listener).setId(new SpecificationItemId.Builder("dsn~detail-design~1").build());
        Mockito.verify(listener).setLocation(any());
        Mockito.verify(listener).setTitle("A Requirement");
        Mockito.verify(listener).addDependsOnId(new SpecificationItemId.Builder("dsn~grand-design~1").build());
        Mockito.verify(listener).addDependsOnId(new SpecificationItemId.Builder("arch~general-constraints~1").build());
        Mockito.verify(listener).addNeededArtifactType("impl");
        Mockito.verify(listener).addNeededArtifactType("utest");
        Mockito.verify(listener).addCoveredId(new SpecificationItemId.Builder("req~first-requirement~1").build());
        Mockito.verify(listener).addCoveredId(new SpecificationItemId.Builder("req~second-requirement~1").build());
        Mockito.verify(listener).appendDescription("The description");
        Mockito.verify(listener).appendRationale("The rationale");
        Mockito.verify(listener).appendComment("A comment");
        inOrder.verify(listener).endSpecificationItem();
        Mockito.verifyNoMoreInteractions(listener);
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
        Mockito.verify(listener)
                .setId(new SpecificationItemId.Builder("dsn~first-requirement~1").build());
        Mockito.verify(listener).setLocation(any());
        Mockito.verify(listener).addNeededArtifactType("impl");
        Mockito.verify(listener).addNeededArtifactType("utest");
        Mockito.verify(listener)
                .addCoveredId(new SpecificationItemId.Builder("req~first-requirement~1").build());
        inOrder.verify(listener).endSpecificationItem();
        Mockito.verifyNoMoreInteractions(listener);
    }
}