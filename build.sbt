lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.1",
    guice,
    specs2 % Test,
    "com.typesafe.play" %% "play-slick" % "3.0.3",
    "com.typesafe.slick" %% "slick-codegen" % "3.2.1",
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
),
/*// Dependencies for silhouette
  libraryDependencies ++= Seq(
  		"com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.3",
		"com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.3",
		"com.mohiva" %% "play-silhouette-persistence" % "5.0.3",
"com.mohiva" %% "play-silhouette-testkit" % "5.0.3" % "test",
	"net.codingwell" %% "scala-guice" % "4.1.1",
	"com.iheart" %% "ficus" % "1.4.1"
),
*/

  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.3"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.2",
  organization := "com.example"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
