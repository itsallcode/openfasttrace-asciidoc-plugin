# OpenFastTrace AsciiDoc Plugin 1.0.0, released 2026-08-??

Code name: Located Specification Item IDs

## Summary

This release upgrades the plugin to OpenFastTrace 4.9.0 and reports source locations for specification item IDs and references as a whole-line source range. Exact
columns and ID component ranges are unavailable from the parser and remain unset.

OpenFastTrace integrations can use these locations to navigate back to the corresponding AsciiDoc block.

## Features

* 1.0.0: Support located specification item IDs
