package controllers

import javax.inject._

import play.api.mvc._
import play.twirl.api.Html
import models.TEDEvent
import models.TEDEventList

//import play.twirl.api.Html

@Singleton
class Application @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  var currEvent:TEDEvent = null

  def index = Action {
    val calendar = views.html.calendar()
    val sidebar = getSidebar(0)
    val events = TEDEventList.list
    val htmlList = events.map(i => views.html.tedEvent(i))
    val event1 = getEvent
    val l = views.html.homeLicenseStatement()
    val content = new Html(htmlList.mkString("") + l.toString())
    Ok(views.html.main(sidebar, content))
  }

  private def getEvent: Html = {
    val event = TEDEvent(
      "This is a TEDxTrinity Event",
      "Should be super fun!",
      17,
      2,
      2018,
      17,
      30,
      "Bob Ross",
      "Laurie Auditorium",
      200,
      "Happy Little Clouds",
      "https://upload.wikimedia.org/wikipedia/en/thumb/7/70/Bob_at_Easel.jpg/220px-Bob_at_Easel.jpg")
    if (currEvent != null) views.html.tedEvent(currEvent) else views.html.tedEvent(event)
  }

  //  def submitEventForm(data:String) = Action {
  //    getResponse = data
  //    Ok(views.html.sponsors())
  //  }

  def submitEventForm(auth: String, title: String, subtitle: String, speaker: String, desc: String, venue: String, date: String, time: String, seats: String, link: String) = Action {
    if (auth == "fi2933fi8as9lss3982jvb398skil") {
       val split = date.split("-")
       val day = split(2).toInt
       val month = split(1).toInt
       val yr = split(0).toInt
       val timeSplit = time.split(":")
       val hr = timeSplit(0).toInt
       val min = timeSplit(1).toInt
       val event = TEDEvent(title,subtitle,day,month,yr,hr,min,speaker,venue,seats.toInt,desc,link)
       TEDEventList.addEvent(event)
       Ok
    } else {
      Ok
    }
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

}
