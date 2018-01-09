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
class Application @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  
  updateModel()

  def index = Action {
    val calendar = views.html.calendar()
    val sidebar = getSidebar(0)
    val events = TEDEventList.list
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

  def sponsors = Action {
    val calendar = views.html.calendar()
    val sidebar = getSidebar(2)
    val sponsors = views.html.sponsors()
    Ok(views.html.main(sidebar, sponsors))
  }

  def aboutTED = Action {
    val aboutTed = views.html.aboutTedContent()
    val calendar = views.html.calendar()
    val sidebar = getSidebar(1)
    Ok(views.html.main(sidebar, aboutTed))
  }

  def contact = Action {
    val content = views.html.contact()
    val calendar = views.html.calendar()
    val sidebar = getSidebar(3)
    Ok(views.html.main(sidebar, content))
  }
  
  
  
  def submitEventForm(auth: String, title: String, subtitle: String, speaker: String, desc: String, venue: String, date: String, time: String, seats: String, link: String) = Action {
    if (auth == "fi2933fi8as9lss3982jvb398skil") {
      val split = date.split("-")
      val day = split(2).toInt
      val month = split(1).toInt
      val yr = split(0).toInt
      val timeSplit = time.split(":")
      val hr = timeSplit(0).toInt
      val min = timeSplit(1).toInt
      val event = TEDEvent(title, subtitle, day, month, yr, hr, min, speaker, venue, seats.toInt, desc, link)
      addEvent(event)
      Ok
    } else {
      Ok
    }
  }

  private def addEvent(event: TEDEvent) = {
    val date = event.date.atZone(ZoneId.of("America/Chicago"))
    val epochsec = date.getLong(ChronoField.INSTANT_SECONDS) * 1000
    val sqldate = new Date(epochsec)
    val sqltime = new Time(epochsec)
    val eventRow = EventsRow(0, sqldate, sqltime)
    val eventID = db.run(Events returning Events.map(_.eventId) += eventRow)
    val finalQuery = eventID.map { id =>
      val eventDesc = EventDescriptionsRow(
        id.toInt,
        event.title,
        event.subtitle,
        event.speaker,
        event.description,
        event.venue,
        sqldate,
        sqltime,
        event.maxSeats,
        0,
        event.imgURL)
      db.run(EventDescriptions += eventDesc)
    }
    finalQuery.map{i =>
      updateModel()
  }
}
  
  private def updateModel() = {
    val events = db.run(EventDescriptions.sortBy(_.eventDate).take(100).result)
    events.map{seq => 
      TEDEventList.updateList(seq)
    }
  }

}
