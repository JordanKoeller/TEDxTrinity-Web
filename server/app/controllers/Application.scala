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
import util._
import play.api.data.Forms._
import play.api.data._

@Singleton
class Application @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  
    val eventForm = Form(
    mapping(
      "title" -> text,
      "subtitle" -> text,
      "speaker" -> text,
      "description" -> text,
      "venue" -> text,
      "date" -> text,
      "time" -> text,
      "numSeats" -> text,
      "mediaLink" -> text)(TEDEvent.apply)(TEDEvent.unapply))
  
  updateModel()

  def index = Action { 
    val calendar = views.html.calendar()
    val sidebar = getSidebar(0)
    val events = TEDEventList.list
    val content = formatEvents(events)
    Ok(views.html.main("Upcoming Events", content))
  }

  private def formatEvents(events:List[TEDEvent]):Html = {
    val articleList = events.map{article =>
      val title = views.html.tedTitle(article)
      val body = views.html.tedEventBody(article)
      val prettyTitle = title//viewStyles.html.style1(Article(title,"") :: Nil)
      // val prettyBody = viewStyles.html.style4(Article("",body,image = article.imgURL))
      viewStyles.html.accordion(prettyTitle,body).toString()
    }
    new Html(articleList.mkString(""))
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
    val sponsorsIcon = "TULogo.png"
    val prettySponsors = viewStyles.html.style1(Article("<a href=\"https://new.trinity.edu\">Trinity University</a>","",image=Some(sponsorsIcon)))
    Ok(views.html.main("Sponsors", prettySponsors))
  }

  def aboutTED = Action {
    // val aboutTed = views.html.aboutTedContent()
    val aboutTed = viewStyles.html.style4(util.aboutTedText.content)
    val sidebar = getSidebar(1)
    Ok(views.html.main("About TEDx", aboutTed))
  }

  def contact = Action {
    val people = peopleList
    val icons = people.map(p => new Html(p.picLink))
    val peopleArticles = people.map(p => Article(p.name,p.description,Some(p.email + " | " + p.classDesc),Some(p.picLink)))
    val htmlList = peopleArticles.map(i => viewStyles.html.style1(i))
    Ok(views.html.main("Our Team",htmlList.mkString(",")))
  }

  def postEvent = Action {implicit request =>
    eventForm.bindFromRequest().fold(
        badForm => {
          null
        },
        goodForm => {
          addEvent(goodForm)
          Ok
        }
    )
  }
  
  
  
  def submitEventForm(auth: String, title: String, subtitle: String, speaker: String, desc: String, venue: String, date: String, time: String, seats: String, link: String) = Action {
    if (auth == "fi2933fi8as9lss3982jvb398skil") {
      val event = TEDEvent(title, subtitle,speaker,desc,venue,date,time,seats,link)
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
        clean(event.title),
        if (event.subtitle.isDefined) Some(clean(event.subtitle.get)) else None,
        clean(event.speaker),
        clean(event.description),
        clean(event.venue),
        sqldate,
        sqltime,
        event.maxSeats,
        0,
        if (event.imgURL.isDefined) Some(clean(event.imgURL.get)) else None)
      db.run(EventDescriptions += eventDesc)
    }
    finalQuery.map{i =>
      updateModel()
  }
}
  
  private def updateModel() = {
    val events = db.run(EventDescriptions.take(100).result)
    events.map{seq => 
      TEDEventList.updateList(seq)
    }
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
