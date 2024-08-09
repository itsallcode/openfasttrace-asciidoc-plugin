# OpenFastTrace AsciiDoc Plugin Developer Guide

This guide provides technical information for developers who want to contribute to OpenFastTrace AsciiDoc plugin.

## Setup

### Getting the AsciiDoc Plugin Library via Maven

```xml
<dependency>
    <groupId>org.itsallcode</groupId>
    <artifactId>openfasttrace-asciidoc-plugin</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Getting the AsciiDoc Plugin Library via Gradle

```groovy
dependencies {
    compile 'org.itsallcode:openfasttrace-asciidoc-plugin:0.1.0'
}
```

## Dependencies

### Build Dependencies

The list below shows all build time dependencies in alphabetical order. Note that except the Maven build tool all required modules are downloaded automatically by Maven.

| Dependency                                                        | Purpose                               | License                       |
| ----------------------------------------------------------------- | ------------------------------------- | ----------------------------- |
| [Apache Maven](https://maven.apache.org/)                         | Build tool                            | Apache License 2.0            |
| [AsciiDoctorJ](https://docs.asciidoctor.org/asciidoctorj/latest/) | Framework for parsing AsciiDoc        | Apache License 2.0            |
| [OpenFastTrace API](https://github.com/itsallcode/openfasttrace)  | Interface to the OpenFaceTrace engine | GNU General Public License V3 |

### Test Dependencies

| Dependency                                                                   | Purpose                                 | License                       |
| ---------------------------------------------------------------------------- | --------------------------------------- | ----------------------------- |
| [Hamcrest Auto Matcher](https://github.com/itsallcode/hamcrest-auto-matcher) | Speed-up for building Hamcrest matchers | GNU General Public License V3 |
| [JUnit](https://junit.org/junit5)                                            | Unit testing framework                  | Eclipse Public License 1.0    |
| [Mockito](https://github.com/mockito/mockito)                                | Mocking framework                       | MIT License                   |

### Runtime Dependencies

| Dependency                                                   | Purpose                  | License                       |
| ------------------------------------------------------------ | ------------------------ | ----------------------------- |
| [OpenFastTrace](https://github.com/itsallcode/openfasttrace) | The OpenFaceTrace engine | GNU General Public License V3 |

### Preparations

OpenFastTrace uses [Apache Maven](https://maven.apache.org) as technical project management tool that resolves and downloads the build-dependencies before building the packages.

### Installation of Initial Build Dependencies on Linux

#### Debian or Ubuntu

```bash
apt-get install openjdk-17-jdk maven
```

## Configure Maven Toolchains


OFT uses Maven Toolchains to configure the correct JDK version (see the [documentation](https://maven.apache.org/guides/mini/guide-using-toolchains.html) for details). To configure the Toolchains plugin create file ` ~/.m2/toolchains.xml` with the following content. Adapt the paths to your JDKs.

```xml
<toolchains xmlns="http://maven.apache.org/TOOLCHAINS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/TOOLCHAINS/1.1.0 http://maven.apache.org/xsd/toolchains-1.1.0.xsd">
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>17</version>
        </provides>
        <configuration>
            <jdkHome>/usr/lib/jvm/java-17-openjdk-amd64/</jdkHome>
        </configuration>
    </toolchain>
        <toolchain>
        <type>jdk</type>
        <provides>
            <version>21</version>
        </provides>
        <configuration>
            <jdkHome>/usr/lib/jvm/java-21-openjdk-amd64/</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

## Essential Build Steps

### Clone Git Repository

```bash
git clone https://github.com/itsallcode/openfasttrace-asciidoc-plugin.git
```
### Test and Build

#### Run Unit Tests

```bash
mvn test
```

#### Run Unit and Integration Tests and Additional Checks

```bash
mvn verify
```

#### Build the AsciiDoc OFT Plugin

```bash
mvn package -DskipTests
```

### Specify Java Version

By default, the AsciiDoc plugin is built with Java 17. To build and test with a later version of Java, add argument `-Djava.version=21` to the Maven command.

```bash
mvn -Djava.version=21 package -DskipTests
```

### Speed Up Build

By default, Maven builds the AsciiDoc OFT Plugin modules sequentially. To speed up the build and execute the modules in parallel, add argument `-T 1C` to the Maven command.

```bash
mvn -T 1C package -DskipTests
```

### Configure the `itsallcode style` formatter

This project comes with formatter and save actions configuration for Eclipse.

If you use a different IDE like IntelliJ, please import the formatter configuration [itsallcode_formatter.xml](itsallcode_formatter.xml).

### Configure Logging

We use [`java.util.logging`](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) for logging. To configure log level and formatting, add the following system property:

```bash
-Djava.util.logging.config.file=src/test/resources/logging.properties
```

### Check for updated dependencies / plugins

Display dependencies and plugins with newer versions:

```bash
mvn --update-snapshots versions:display-dependency-updates versions:display-plugin-updates
```

Automatically upgrade dependencies:

```bash
mvn --update-snapshots versions:use-latest-releases versions:update-properties
```

### Reproducible Build

This project is configured to produce exactly the same artifacts each time when building from the same Git commit. See the [Maven Guide to Configuring for Reproducible Builds](https://maven.apache.org/guides/mini/guide-reproducible-builds.html).

* Verify correct configuration of the reproducible build (also included in phase `verify`):
  ```bash
  mvn initialize artifact:check-buildplan
  ```
* Verify that the build produces excatly the same artifacts:
  ```bash
  mvn clean install -DskipTests
  mvn clean verify artifact:compare -DskipTests
  ```

The build will use the last Git commit timestamp as timestamp for files in `.jar` archives.

### Run local sonar analysis

```bash
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar -Dsonar.token=[token]
```

See analysis results at [sonarcloud.io](https://sonarcloud.io/dashboard?id=org.itsallcode%3Aopenfasttrace-asciidoc-plugin).

## Creating a Release

**NOTE**: This currently only works for release version numbers, not SNAPSHOT versions.

### Prepare the Release

1. Checkout the `main` branch.
2. Create a new "prepare-release" branch.
3. Update version in
    * `pom.xml` (`version` element)
    * `README.md`
    * `doc/developer_guide.md`
4. Add changes in new version to `doc/changes/changes.md` and `doc/changes/changes_$VERSION.md` and update the release date.
5. Commit and push changes.
6. Create a new pull request, have it reviewed and merged to `main`.

### Perform the Release

1. Start the release workflow
  * Run command `gh workflow run release.yml --repo itsallcode/openfasttrace-asciidoc-plugin --ref main`
  * or go to [GitHub Actions](https://github.com/itsallcode/openfasttrace-asciidoc-plugin/actions/workflows/release.yml) and start the `release.yml` workflow on branch `main`.
2. Update title and description of the newly created [GitHub release](https://github.com/itsallcode/openfasttrace-asciidoc-plugin/releases).
3. After some time the release will be available at [Maven Central](https://repo1.maven.org/maven2/org/itsallcode/openfasttrace-asciidoc-plugin/).
