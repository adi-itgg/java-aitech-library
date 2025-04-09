# AITech Java Library

#### focused at performance and ease of use

[![Build Status](https://github.com/adi-itgg/java-aitech-library/actions/workflows/maven.yml/badge.svg)](https://github.com/adi-itgg/java-aitech-library/actions/workflows/maven.yml)
[![CodeQL](https://github.com/adi-itgg/java-aitech-library/actions/workflows/codeql.yml/badge.svg)](https://github.com/adi-itgg/java-aitech-library/actions/workflows/codeql.yml)
[![OpenSSF Scorecard](https://img.shields.io/ossf-scorecard/github.com/adi-itgg/java-aitech-library?label=openssf%20scorecard&style=flat)](https://securityscorecards.dev/viewer/?uri=github.com/adi-itgg/java-aitech-library)
[![Coverage Status](https://coveralls.io/repos/github/adi-itgg/java-aitech-library/badge.svg?branch=main)](https://coveralls.io/github/adi-itgg/java-aitech-library?branch=main)
[![Codecov](https://codecov.io/gh/adi-itgg/java-aitech-library/branch/main/graph/badge.svg)](https://codecov.io/gh/adi-itgg/java-aitech-library)
[![Jitpack](https://jitpack.io/v/adi-itgg/java-aitech-library.svg)](https://jitpack.io/#adi-itgg/java-aitech-library)
[![Known Vulnerabilities](https://snyk.io/test/github/adi-itgg/java-aitech-library/badge.svg)](https://snyk.io/test/github/adi-itgg/java-aitech-library)


## BOM

This module contains dependencies for all other modules.

### Usage:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.aitech</groupId>
      <artifactId>bom</artifactId>
      <version>${library.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

## Mapstruct SPI implementation

This module contains Mapstruct SPI implementation.

Features:
* Fluent setter & getter accessor naming strategy

### Usage

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>${maven-compiler-plugin.version}</version>
  <configuration>
    <annotationProcessorPaths>
      <path>
        <groupId>io.aitech</groupId>
        <artifactId>mapstruct-spi-impl</artifactId>
        <version>${library.version}</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

## Vert.x

This module contains Vert.x extensions.

Features:
* Virtual Thread Launcher

### Usage

Replace `io.vertx.core.Launcher` to `io.aitech.vertx.VTLauncher`

```xml
<dependency>
  <groupId>io.aitech</groupId>
  <artifactId>vertx</artifactId>
  <version>${library.version}</version>
</dependency>
```

## Vert.x Config

### Usage
```java
ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
  .setIncludeDefaultStores(false)
  .setScanPeriod(-1L);
ConfigRetrieverExtended configRetrieverExtended = ConfigRetrieverExtended.create(vertx, configRetrieverOptions)
  .args(args.toArray(new String[0])) // --profiles=development or -p=dev
  .format("yml")
  .build();

JsonObject config = await(configRetrieverExtended.getConfig());
```

```xml
<dependency>
  <groupId>io.aitech</groupId>
  <artifactId>vertx-config</artifactId>
  <version>${library.version}</version>
</dependency>
```


## Vert.x Config Yaml

this module contains Vert.x Config Yaml(yml) extension.

Features:
* Customize Environment Key With Default Value `${ APP_PROFILE | development }`
* Customize Configuration for Yaml LoaderOptions
* Set or Get value easily with `YmlJsonObject`. ex: `config.getString("postgres.host")`

### Usage

```xml
<dependency>
  <groupId>io.aitech</groupId>
  <artifactId>vertx-config-yml</artifactId>
  <version>${library.version}</version>
</dependency>
```

## Vert.x Database ORM

Initialization repository
```java
Pool pool = Pool.pool(vertx, pgConnectOptions, pgPoolOpt);

PgRepositoryOptions pgRepositoryOptions = PgRepositoryOptions.newBuilder()
  .pool(pool)
  .build();

PgRepository pgRepository = PgRepository.create(pgRepositoryOptions);
```

pom.xml
```xml
<dependency>
  <groupId>io.aitech</groupId>
  <artifactId>vertx-database-orm</artifactId>
  <version>${library.version}</version>
</dependency>
```

## Vert.x Http

Initialization router
```java
RouterBuilder routeBuilder = RouterBuilder.create(routers)
      .setRequestValidations(List.of(jktValidator))
      .exceptionHandler(exceptionHandler)
      .responseMapper(responseMapper)
      .init(router)
      .withBodyHandler()
      .appendRouter();
```

### Usage

```java
@Route("/transaction")
@Authenticated
@Consumes("application/json")
@RequiredArgsConstructor
public final class TransactionRouter {

  private final TransactionHandler transactionHandler;

  @POST
  public TransactionInquiryResponse inquiry(@ContextData(Constant.ContextKeys.CLIENT_CONFIG_ENTITY) ClientConfigEntity clientConfigEntity, @RequestBody TransactionInquiryRequest request) {
    return transactionHandler.inquiry(clientConfigEntity, request);
  }

  @POST
  public TransactionRemitResponse remit(@RequestBody TransactionRemitRequest request) {
    return transactionHandler.remit(request);
  }
}
```

pom.xml
```xml
<dependency>
  <groupId>io.aitech</groupId>
  <artifactId>vertx-http</artifactId>
  <version>${library.version}</version>
</dependency>
```


### Use with awesome java libraries
- [Lombok](https://github.com/projectlombok/lombok) - Java annotations for code generation
- [Avaje Inject](https://github.com/avaje/avaje-inject) - Java Dependency Injection Framework
- [Mapstruct](https://github.com/mapstruct/mapstruct) - Java Bean Mapping Framework
