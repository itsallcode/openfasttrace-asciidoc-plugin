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

### Runtime Dependencies

| Dependency | Purpose | License |
|------------|---------|---------|
| TBD        | TBD     | TBD     |

### Build Dependencies

| Dependency | Purpose | License |
|------------|---------|---------|
| TBD        | TBD     | TBD     |

### Test Dependencies

| Dependency | Purpose | License |
|------------|---------|---------|
| TBD        | TBD     | TBD     |

### Preparations

<!-- Description -->

### Installation of Initial Build Dependencies on Linux

#### Debian or Ubuntu

```bash
# Add bash commands
```

## Configure Maven Toolchains

```xml
<!-- Add your Maven toolchain XML configuration -->
```

## Essential Build Steps

### Clone Git Repository

```bash
# Add git clone command
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

<!-- Continue with the rest of the sections... -->
