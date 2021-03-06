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
  
  updateModel()

  def index = Action { 
    val events = TEDEventList.list
    val content = formatEvents(events)
    Ok(views.html.main("Upcoming Events", content))
  }

//  private def formatEvents(events:List[TEDEvent]):Html = {
//    val articleList = events.map{article =>
//      val title = views.html.tedTitle(article)
//      val body = views.html.tedEventBody(article)
//      val prettyTitle = title
//      viewStyles.html.accordion(prettyTitle,body).toString()
//    }
//    new Html(articleList.mkString(""))
//  }

  def formatEvents(events:List[TEDEvent]):Html = {
    val articleList = events.map{article =>
      val title = views.html.tedTitle(article)
      val body = views.html.tedEventBody(article)
      val leftPane = Article(article.title,article.subtitle.get)
      val rightPane = Article("","",image=article.imgURL)
      val prettyTitle = viewStyles.html.splitStyle(leftPane,rightPane)//viewStyles.html.style1(Article(title,"") :: Nil)
      viewStyles.html.accordion(prettyTitle,body).toString()
    }
    new Html(articleList.mkString(""))
  }
  def sponsors = Action {
    val calendar = views.html.calendar()
    val sponsors = views.html.sponsors()
    val sponsorsIcon = "TULogo.png"
    val prettySponsors = viewStyles.html.style1(Article("<a href=\"https://new.trinity.edu\">Trinity University</a>","",image=Some(sponsorsIcon)))
    Ok(views.html.main("Sponsors", prettySponsors))
  }

  def aboutTED = Action {
    // val aboutTed = views.html.aboutTedContent()
    val aboutTed = viewStyles.html.style4(util.aboutTedText.content)
    Ok(views.html.main("About TEDx", aboutTed))
  }

  def contact = Action {
    val people = peopleList
    val icons = people.map(p => new Html(p.picLink))
    val peopleArticles = people.map(p => Article(p.name,p.description,Some(p.email + " | " + p.classDesc),Some(p.picLink)))
    val htmlList = peopleArticles.map(i => viewStyles.html.style1(i))
    Ok(views.html.main("Our Team",htmlList.mkString(",")))
  }

  def postEvent = Action {request =>
    try {
      
    println("Received Request")
    println(request.body.asJson)
    println("Trying to process")
    val event = request.body.asJson.map {json => 
      val title = (json \ "title").as[String]
      val subtitle = (json \ "subtitle").as[String]
      val speaker = (json \ "speaker").as[String]
      val description = (json \ "description").as[String]
      val venue = (json \ "venue").as[String]
      val date = (json \ "date").as[String]
      val time = (json \ "time").as[String]
      val numSeats = (json \ "numSeats").as[String]
      val mediaLink = (json \ "mediaLink").as[String]
      val event = TEDEvent(title,subtitle,speaker,description,venue,date,time,numSeats,mediaLink)
      event
    }
    println("About to add")
    val eventGotten = event.get
    println("Called get")
    addEvent(eventGotten)
    println("Successfully added")
    Ok
    }
    catch {
      case e:Throwable => {
        println("Failed to post correctly")
        println("Error Message: " + e.toString())
        null
      }
    }
  }
  
  
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
		  

  private def addEvent(event: TEDEvent) = {
    println("Made it into the event")
    val date = event.date.atZone(ZoneId.of("America/Chicago"))
    val epochsec = date.getLong(ChronoField.INSTANT_SECONDS) * 1000
    val sqldate = new Date(epochsec)
    val sqltime = new Time(epochsec)
    val eventRow = EventsRow(0, sqldate, sqltime)
    val id = 0
      val eventDesc = EventDescriptionsRow(
        id,
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
    val finalQuery =  db.run(EventDescriptions += eventDesc)
    println("Just passed db.run")
    finalQuery.map{i =>
      println("Query Update Returned " + i)
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
