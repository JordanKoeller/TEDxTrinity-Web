package util

object DatabaseCodeGen extends App {
  slick.codegen.SourceCodeGenerator.main(
      Array("slick.jdbc.PostgresProfile", "org.postgresql.Driver",
          "jdbc:postgresql://ec2-54-235-148-19.compute-1.amazonaws.com:5432/dcegnlpg1iu06?user=wlpnclrrhqlewa&password=5b84c825241693cc64516a7456186712d78c4eb6779aad3948d42f7298608fc0&sslmode=require",
          "/home/jkoeller/Documents/Trinity/TEDxTrinity/website/play-scalajs/server/app/models",
          "models")
      )
//      Array(profile, jdbcDriver, url, outputFolder, pkg)
//    Array("slick.driver.MySQLDriver", "com.mysql.jdbc.Driver", 
//        "jdbc:mysql://localhost/datadrawing?user=datadrawing&password=NextPointerGoesHere", 
//        "/users/maguilar/Local/HTML-Documents/WebApps/workspace_play_project/webappsproject2/playproject/server/app", 
      //"models", "datadrawing", "NextPointerGoesHere")
//  )
}