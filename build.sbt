lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """cyp_mis_2020""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      jdbc,
      evolutions,
      javaJpa,
      javaCore,
      javaWs,
      ehcache,
      ws,
      "com.typesafe.play" % "play-cache_2.13" % "2.8.8",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.3",
      "org.hibernate" % "hibernate-entitymanager" % "5.4.24.Final",
      "org.apache.poi" % "poi" % "3.17",
      "org.apache.poi" % "poi-ooxml" % "3.17",
      "org.awaitility" % "awaitility" % "4.0.1" % "test",
      "org.assertj" % "assertj-core" % "3.14.0" % "test",
      "org.apache.pdfbox" % "pdfbox" % "2.0.1",
      "org.hibernate" % "hibernate-entitymanager" % "4.2.8.Final",
      "org.mockito" % "mockito-core" % "3.1.0" % "test"),

    Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked"),
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),
    PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
  )
libraryDependencies += guice
libraryDependencies += "com.oracle.database.jdbc" % "ojdbc8" % "19.8.0.0"
libraryDependencies += "com.oracle.database.nls" % "orai18n" % "12.2.0.1"
libraryDependencies += "org.elasticsearch.client" % "elasticsearch-rest-client" % "7.11.1"
libraryDependencies += "org.elasticsearch.client" % "transport" % "7.11.1"
libraryDependencies += "org.elasticsearch" % "elasticsearch" % "7.11.1"
libraryDependencies += "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.11.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.14.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.14.1"
resolvers += "Oracle" at "https://maven.oracle.com"
credentials += Credentials("Oracle", "maven.oracle.com", "manolis.papaspyropoulos@gmail.com", "-j^fWnc,9mig6&s")

