jetty-bootstrap
===============

Jetty embedded bootstrap library. Make it easy to embed Jetty into your maven project and package a standalone app.

In case you have your war file on the file system (e.g. /tmp/webapp.war), it is as simple as this:

```java
JettyBootstrap bootstrap = new JettyBootstrap();
bootstrap.addWar("/tmp/webapp.war", "/contextName");
bootstrap.startJetty();
```

Maven dependency to include on your project
```xml
<dependency>
	<groupId>org.genux</groupId>
	<artifactId>jetty-bootstrap</artifactId>
	<version>1.0-0-SNAPSHOT</version>
</dependency>
```