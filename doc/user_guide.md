# OpenFastTrace AsciiDoc Plugin User Guide

## Source Locations

The plugin reports the source file and line at which Asciidoctor locates each specification item. It also attaches a source range to IDs declared with `oft-sid` and to references declared with `oft-depends` and `oft-covers`. This allows OpenFastTrace integrations to navigate from an imported item or ID reference back to the corresponding AsciiDoc block.

Asciidoctor exposes the start line of a structural block, but no column or individual attribute positions. The plugin therefore reports the complete start line of the block as the source range for all IDs and references in that block. The range uses zero-based line numbers and columns, starts at column zero, and ends at column zero of the following line. Artifact type, name, and revision component ranges are not available and remain unset.

For example, all IDs in the following block are located on the line where Asciidoctor reports the `specitem` block, rather than at their exact positions inside the attribute list:

```asciidoc
[.specitem, oft-sid="dsn~example~1", oft-covers="req~example~1"]
== Example

Description of the specification item.
```
