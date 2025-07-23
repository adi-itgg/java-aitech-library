# AITech Java Library ⚡

**AITech Java Library** is a modern, modular set of Java utilities designed for high-performance backend systems. It focuses on **clean architecture**, **no reflection**, and native integration with **Vert.x**, **MapStruct**, and **Avaje Inject**.

> 🧠 **Zero Reflection**: This library avoids runtime reflection entirely. Instead, it uses `LambdaMetaFactory` for ultra-fast accessors and clean runtime behavior.


[![Build Status](https://github.com/adi-itgg/java-aitech-library/actions/workflows/maven.yml/badge.svg)](https://github.com/adi-itgg/java-aitech-library/actions/workflows/maven.yml)
[![CodeQL](https://github.com/adi-itgg/java-aitech-library/actions/workflows/codeql.yml/badge.svg)](https://github.com/adi-itgg/java-aitech-library/actions/workflows/codeql.yml)
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/10407/badge)](https://www.bestpractices.dev/projects/10407)

[![OpenSSF Scorecard](https://img.shields.io/ossf-scorecard/github.com/adi-itgg/java-aitech-library?label=openssf%20scorecard&style=flat)](https://securityscorecards.dev/viewer/?uri=github.com/adi-itgg/java-aitech-library)
[![Known Vulnerabilities](https://snyk.io/test/github/adi-itgg/java-aitech-library/badge.svg)](https://snyk.io/test/github/adi-itgg/java-aitech-library)

[![Jitpack](https://jitpack.io/v/adi-itgg/java-aitech-library.svg)](https://jitpack.io/#adi-itgg/java-aitech-library)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.adi-itgg/bom.svg?label=Maven%20Central)](https://search.maven.org/artifact/io.github.adi-itgg/bom)

[![Coverage Status](https://coveralls.io/repos/github/adi-itgg/java-aitech-library/badge.svg?branch=main)](https://coveralls.io/github/adi-itgg/java-aitech-library?branch=main)
[![Codecov](https://codecov.io/gh/adi-itgg/java-aitech-library/branch/main/graph/badge.svg)](https://codecov.io/gh/adi-itgg/java-aitech-library)

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/adi-itgg/java-aitech-library/blob/main/LICENSE)
[![Security Policy](https://img.shields.io/badge/security-policy-blue.svg)](https://github.com/adi-itgg/java-aitech-library/blob/main/.github/SECURITY.md)

## 📦 Modules Overview

### 1. BOM (Bill of Materials)

Centralized dependency management for all modules.

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.github.adi-itgg</groupId>
      <artifactId>bom</artifactId>
      <version>${aitech.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

### 2. MapStruct SPI Plugin

Provides a custom SPI to support fluent-style accessors when using MapStruct.

✅ Works seamlessly with:

* Lombok's `@Accessors(fluent = true)`
* Fast property resolution without reflection

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
    <annotationProcessorPaths>
      <path>
        <groupId>io.github.adi-itgg</groupId>
        <artifactId>mapstruct-spi-impl</artifactId>
        <version>${aitech.version}</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

---

### 3. Vert.x Bootstrap (Virtual Threads)

Launch your Vert.x application using **Java Virtual Threads** (Java 21+), improving scalability without complex thread management.

```xml
<dependency>
  <groupId>io.github.adi-itgg</groupId>
  <artifactId>vertx</artifactId>
  <version>${aitech.version}</version>
</dependency>
```

```java
public class Main {
  public static void main(String[] args) {
    VirtualThreadLauncher.run(MyVerticle.class, args);
  }
}
```

---

### 4. Vert.x Config Loader

Smart configuration loader with support for:

* Multiple profiles (e.g., `config-dev.yml`)
* YAML format with fallback
* Environment key auto-binding

```java
ConfigRetrieverOptions options = new ConfigRetrieverOptions()
  .setIncludeDefaultStores(false)
  .setScanPeriod(-1);

ConfigRetrieverExtended retriever = ConfigRetrieverExtended.create(vertx, options)
  .args(args)
  .format("yml")
  .build();

JsonObject config = await(retriever.getConfig());
```

---

### 5. Vert.x ORM (PostgreSQL)

Lightweight, annotation-based ORM for Vert.x with **zero reflection**.

Example entity:

```java
@Data
@Accessors(fluent = true)
@Entity(name = "t_user")
public final class UserEntity {
  @Id private String id;
  @CreatedAt private OffsetDateTime createdAt;
  @UpdatedAt private OffsetDateTime updatedAt;
}
```

Insert example:

```java
Future<UserEntity> result = pgRepository.insert(userEntity);
```

Features:

* SQL-first approach
* No proxies or reflection
* Annotation-based (via compile-time processors)

---

### 6. Vert.x HTTP Handler

A declarative HTTP routing system with built-in:

* Body parsing
* Input validation
* Response mapping
* Exception handling

```java
RouterBuilder.create(routers)
  .setRequestValidations(List.of(...))
  .exceptionHandler(new GlobalExceptionHandler())
  .responseMapper(new DefaultResponseMapper())
  .init(router)
  .withBodyHandler()
  .appendRouter();
```

Example route definition:

```java
@Route("/api/user")
@RequiredArgsConstructor
public final class UserRouter {
  private final UserHandler handler;

  @GET
  public UserResponse get(@QueryParam String id) {
    return handler.findById(id);
  }
}
```

---

## ✨ Key Advantages

* ✅ **Zero reflection** – powered by `LambdaMetaFactory` for optimal runtime performance
* ⚡ Built-in support for Virtual Threads
* 🧩 Clean separation between config, handler, model, and persistence
* 📦 Compatible with modern toolchains: Lombok, MapStruct, Avaje Inject

---

## 📚 Getting Started

1. Import the BOM into your `pom.xml`
2. Choose modules you need: `vertx`, `orm`, `http`, `mapstruct-spi`, etc.
3. Set up your config, handlers, and entities using examples above.

---

## 🧪 Requirements

* Java 21+
* Vert.x 4.5+
* Maven 3.9+

---

> Fast, clean, reflection-free Java development.


### Use with awesome java libraries
- [Lombok](https://github.com/projectlombok/lombok) - Java annotations for code generation
- [Avaje Inject](https://github.com/avaje/avaje-inject) - Java Dependency Injection Framework
- [Mapstruct](https://github.com/mapstruct/mapstruct) - Java Bean Mapping Framework
