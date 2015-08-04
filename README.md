## Starter Kit for building standalone application embedding Jetty Application Server

http://jetty-bootstrap.io

[![Build Status](https://drone.io/github.com/teknux-org/jetty-bootstrap/status.png)](https://drone.io/github.com/teknux-org/jetty-bootstrap/latest)

### Description
Jetty-Bootstrap library make it easy to embed Jetty into your maven project and package a standalone app. Provides a fluent API to expose web application(s) and service(s), configure the server settings and package the whole application with a low footprint.

- Read the [documentation](https://github.com/teknux-org/jetty-bootstrap/wiki) for usage details.
- Browse sample projects source using this lib [here](https://github.com/teknux-org/jetty-bootstrap-examples).

### Requirements
- Java 7
- Maven

### Sample
In case you have your war file on the file system (e.g. /tmp/webapp.war), it is as simple as this:
```java
JettyBootstrap bootstrap = new JettyBootstrap();
bootstrap.addWarApp("/tmp/webapp.war", "/contextName");
bootstrap.startServer();
```

Maven dependency to include on your project
```xml
<dependency>
	<groupId>org.teknux</groupId>
	<artifactId>jetty-bootstrap</artifactId>
	<version>x.x.x</version>
</dependency>
```

### Sponsor
- Supported by JetBrains
- Developed with IntelliJ IDEA

![](https://www.jetbrains.com/company/docs/logo_jetbrains.png)

![](https://www.jetbrains.com/idea/docs/logo_intellij_idea.png)
