package controllers

import java.sql.Date
import java.sql.Time
import java.time.ZoneId
import java.time.temporal.ChronoField

import scala.concurrent.ExecutionContext

import javax.inject._
import models.TEDEvent
import models.TEDEventList
import models.Tables.EventDescriptions
import models.Tables.EventDescriptionsRow
import models.Tables.Events
import models.Tables.EventsRow
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile
import scala.concurrent.Await
import scala.concurrent.duration.Duration

//import play.twirl.api.Html

@Singleton
class Test @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  

  def index = Action {
    val calendar = views.html.calendar()
    val sidebar = getSidebar(0)
    val events = generateEvents
    val htmlList = events.map(i => views.html.tedEvent(i))
    val l = views.html.homeLicenseStatement()
    val content = new Html(htmlList.mkString("") + l.toString())
    Ok(views.html.main(sidebar, content))
  }


  private def getSidebar(ind: Int): Html = {
    val cal = views.html.calendar()
    val navOpts = Array(("/Events", "Upcoming Events"), ("/AboutTED", "About TED"), ("/Sponsors", "Sponsors"), ("/Contact", "Contact Us"))
    views.html.sidebar(navOpts, ind, cal)
  }
  
  private def generateEvents = {
    val event = TEDEvent("Title","subtitle",20,4,2020,4,20,"Speaker","venue",420,"description")
    val event2 = TEDEvent("Title2","subtitle2",22,4,2022,4,22,"Speaker2","venue2",420,"description2")
    event2 :: event :: Nil
  }

  
  private def clean(str:String):String = {
    val specials = Array(("%2F" -> "/"),("%3A" -> ":"),( "%2E" -> "."))
    var s = str
    specials.foreach{subst =>
      s = s.replaceAll(subst._1, subst._2)
    }
    s
  }

}