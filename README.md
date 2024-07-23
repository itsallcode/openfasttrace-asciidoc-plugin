# <img src="doc/openfasttrace_logo.svg" alt="OFT logo" width="150"/> OpenFastTrace AsciiDoc Plugin

## What is OpenFastTrace AsciiDoc Plugin?

This plugin allows OpenFastTrace (OFT) to parse and trace requirements in AsciiDoc files. Requirement tracing keeps track of whether you implemented everything you planned to in your specifications. It also identifies obsolete parts of your product and helps you to get rid of them.

For more details about requirement tracing and how to use OpenFastTrace, refer to the [user guide](doc/user_guide.md).

_[Place a screenshot of a tracing report generated using this plugin here]_

## Project Information

[![Build](https://github.com/itsallcode/openfasttrace-asciidoc-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/itsallcode/openfasttrace-asciidoc-plugin/actions/workflows/build.yml)

Sonarcloud status:

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=bugs)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=org.itsallcode%3Aopenfasttrace-asciidoc-plugin&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin)

## What is AsciiDoc?

[AsciiDoc](https://asciidoc.org/) is a human-readable document format, semantically equivalent to DocBook XML, but using plain-text mark-up conventions. AsciiDoc documents can be created using any text editor and can be viewed and edited in source form without requiring additional tools.

AsciiDoc is highly configurable: both the AsciiDoc source file syntax and the backend output markups (which can be almost any type of SGML/XML markup) can be customized and extended by the user.

## How does AsciiDoc fit into OFT?

In the context of OpenFastTrace (OFT), AsciiDoc files can be used to write the software requirements specification. The AsciiDoc format allows for requirement IDs to be embedded in the text, which can then be traced by OFT.

Using the AsciiDoc Plugin, OFT is able to read and parse AsciiDoc files, trace the requirements mentioned in the files, and generate a requirement tracing report. This allows for easy tracking of the implementation status of each requirement directly from the specification document.

**User Guides**

* [📖 User Guide](doc/user_guide.md)

**News and Discussions**

* [📢 Blog](https://blog.itsallcode.org/)
* [🗨️ Discussion Board](https://github.com/itsallcode/openfasttrace/discussions)

**Information for Contributors**

* [🎟️ Project Board](https://github.com/orgs/itsallcode/projects/3/views/1)
* [🦮 Developer Guide](doc/developer_guide.md)
* [🎁 Contributing Guide](CONTRIBUTING.md)

## Using OpenFastTrace AsciiDoc Plugin

The plugin can be used with the standard OFT command line, integrated into Maven or Gradle build cycles, or applied through a continuous integration pipeline.

For more detailed information on how to use OpenFastTrack with AsciiDoc Plugin, consult our [user guide](doc/user_guide.md).

## Getting OpenFastTrace AsciiDoc Plugin

The OpenFastTrace AsciiDoc Plugin is supplied as a Java Archive (JAR) which can be obtained from:

* [Maven Central](#)
* [GitHub](https://github.com/itsallcode/openfasttrace-asciidoc-plugin/releases)

More details about adding the plugin to an existing establishment are outlined in the [developer guide](doc/developer_guide.md).

## Installation

### Runtime Dependencies

OpenFastTrace AsciiDoc Plugin needs a Java 17 (or later) runtime environment to run properly.

#### Installation of Runtime Dependencies on Linux

##### Ubuntu or Debian

To install Java Runtime Environment:

    apt-get install openjdk-17-jre

## Running OpenFastTrace With the AsciiDoc Plugin

To run OFT with the plugin, you can add the plugin to the Java classpath or one of OFT's plugin search directories.

For more details, please check out the OFT user guide's chapter on plugins.

<!-- TODO: add link -->
