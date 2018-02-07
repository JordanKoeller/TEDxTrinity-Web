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
import util._

//import play.twirl.api.Html

@Singleton
class Test @Inject() (
  protected val dbConfigProvider: DatabaseConfigProvider,
  cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._
  
  var madeEvents:List[TEDEvent] = Nil

  def index = Action {
    val events = generateEvents
    val htmlList = formatEvents(generateEvents)
    Ok(views.html.main("Events",htmlList))
  }

  def formatEvents(events:List[TEDEvent]):Html = {
    val articleList = events.map{article =>
      val title = views.html.tedTitle(article)
      val body = views.html.tedEventBody(article)
      val prettyTitle = title//viewStyles.html.style1(Article(title,"") :: Nil)
      viewStyles.html.accordion(prettyTitle,body).toString()
    }
    new Html(articleList.mkString(""))
  }


  private def getSidebar(ind: Int): Html = {
    val cal = views.html.calendar()
    val navOpts = Array(("/Events", "Upcoming Events"), ("/AboutTED", "About TED"), ("/Sponsors", "Sponsors"), ("/Contact", "Contact Us"))
    views.html.sidebar(navOpts, ind, cal)
  }
  
  private def generateEvents = {
    val event = TEDEvent("Title","subtitle",20,4,2020,4,20,"Speaker","venue",420,
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
    val event2 = TEDEvent("Title2","subtitle2",22,4,2022,4,22,"Speaker2","venue2",420,
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        "https://d3ui957tjb5bqd.cloudfront.net/uploads/2014/05/final-tickets.jpg")
    val event3 = TEDEvent("This is a TED Event", "Subtitle goes here", 18, 2, 2018, 4, 22,
                          "Speakers include: Alice, Bob, Charlie, Daniel, Eric, Frances, Gary, Hannah, Indigo",
                          "Ruth Taylor Recital Hall",
                          420,
                          "A descriptive Lorum Ipsum: Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                          "http://wikimotive.com/wikiblog/wp-content/uploads/sites/2/2013/10/events-heavenly-header1.jpg")
    event3:: event2 :: event :: madeEvents
  }
  def postEvent = Action {request =>
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
    addEvent(event.get)
    println("Successfully added")
    Ok
//    eventForm.bindFromRequest().fold(
//        badForm => {
//          null
//        },
//        goodForm => {
//          addEvent(goodForm)
//          Ok
//        }
//    )
  }
  
  def addEvent(event:TEDEvent) = {
    madeEvents = event :: madeEvents
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