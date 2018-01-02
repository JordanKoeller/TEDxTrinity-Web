package controllers

import javax.inject._

import play.api.mvc._
import play.twirl.api.Html
import models.TEDEvent

//import play.twirl.api.Html

@Singleton
class Application @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    val calendar = views.html.calendar()
    val sidebar = getSidebar(0)
    val event1 = getEvent
    val l = views.html.homeLicenseStatement()
    val content = new Html(event1.toString() + l.toString())
    Ok(views.html.main(sidebar, content))
  }
  
  private def getEvent:Html = {
    val event = TEDEvent("This is a TEDxTrinity Event",
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
    views.html.tedEvent(event)
  }
  
  private def getSidebar(ind:Int):Html = {
    val cal = views.html.calendar()
    val navOpts = Array(("/Events","Upcoming Events"),("/AboutTED","About TED"),("/Sponsors","Sponsors"),("/Contact","Contact Us"))
    views.html.sidebar(navOpts,ind,cal)
  }
  
  def sponsors = Action {
    val calendar = views.html.calendar()
    val sidebar = getSidebar(2)
    val sponsors = views.html.sponsors()
    Ok(views.html.main(sidebar,sponsors))
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
