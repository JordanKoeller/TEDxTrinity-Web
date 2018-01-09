package controllers

import java.sql.Date
import java.sql.Time
import java.time.ZoneId
import java.time.temporal.ChronoField

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

import javax.inject._
import models.TEDEvent
import models.Tables.Events
import models.Tables.EventsRow
import models.Tables.EventDescriptionsRow
import models.Tables.EventDescriptions
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc._
import slick.jdbc.JdbcProfile

@Singleton
class QueryController @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  def getEvents = {
    val query = db.run(Events.sortBy(_.eventDate).result)
    val time = Duration.Inf
    val ret = Await.result(query, time)
    println(ret.mkString(","))
    // Need to combine with the EventsDescriptions table to get full information

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
      println("Constructed Event")
      addEvent(event)
      Ok
    } else {
      Ok
    }
  }

  def addEvent(event: TEDEvent) = {
    println("REceived event")
    val date = event.date.atZone(ZoneId.of("America/Chicago"))
    val epochsec = date.getLong(ChronoField.INSTANT_SECONDS) * 1000
    val sqldate = new Date(epochsec)
    val sqltime = new Time(epochsec)
    val eventRow = EventsRow(0, sqldate, sqltime)
    println("Constructed")
    val eventID = db.run(Events returning Events.map(_.eventId) += eventRow)
    eventID.map { id =>
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
      println("Inside body. Initialized last db.run")
    }
  }
}

//object QueryController {
//  private val controller = new QueryController()
//  
//  def addEvent(event:TEDEvent) = controller.addEvent(event)
//  
//  def getEvents = controller.getEvents
//  
//  
//}