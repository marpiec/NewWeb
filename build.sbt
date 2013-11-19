name := "NewWeb"

version := "1.0"

scalaVersion := "2.10.3"

resolvers ++= Seq("spray repo" at "http://repo.spray.io",
                  "Local Maven" at "file:///C:/maven-repo/")

libraryDependencies ++= Seq("org.eclipse.jetty" % "jetty-server" % "9.0.6.v20130930",
                            "org.eclipse.jetty" % "jetty-servlet" % "9.0.6.v20130930",
                            "io.spray" % "spray-can" % "1.2-RC2",
                            "io.spray" % "spray-routing" % "1.2-RC2",
                            "io.spray" % "spray-servlet" % "1.2-RC2",
                            "com.typesafe.akka" %% "akka-actor" % "2.2.3",
        					"com.typesafe.akka" %% "akka-slf4j" % "2.2.3",
        					"pl.mpieciukiewicz.mpjsons" % "mpjsons" % "0.6.1-SNAPSHOT",
        					"net.sf.json-lib" % "json-lib" % "2.4" classifier "jdk15",
        					"xom" % "xom" % "1.2.5",
							"ch.qos.logback"      % "logback-classic"  % "1.0.13",
						    "com.typesafe.akka"  %% "akka-testkit"     % "2.2.3"        % "test")
							
							
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

seq(lessSettings:_*)