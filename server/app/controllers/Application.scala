package controllers

import javax.inject._

<<<<<<< HEAD
import play.api.mvc._
import play.twirl.api.Html
import models.TEDEvent

//import play.twirl.api.Html

@Singleton
class Application @Inject() (cc: ControllerComponents) extends AbstractController(cc) {
=======
import com.example.playscalajs.shared.SharedMessages
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
>>>>>>> 6b61aaba4af71bde1d85b96eb5757d317dee65a1

  def index = Action {
    val calendar = views.html.calendar()
    val sidebar = views.html.sidebar(calendar)
<<<<<<< HEAD
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
  
  def sponsors = Action {
    val calendar = views.html.calendar()
    val sidebar = views.html.sidebar(calendar)
    val sponsors = views.html.sponsors()
    Ok(views.html.main(sidebar,sponsors))
  }

=======
    val event1 = views.html.tedEvent()
    
    Ok(views.html.main(sidebar,event1))
  }
  
  def testTemplate = Action {
    Ok(views.html.testTemplate())
  }
  
>>>>>>> 6b61aaba4af71bde1d85b96eb5757d317dee65a1
  def aboutTED = Action {
    val aboutTed = views.html.aboutTedContent()
    val calendar = views.html.calendar()
    val sidebar = views.html.sidebar(calendar)
<<<<<<< HEAD
    Ok(views.html.main(sidebar, aboutTed))
  }
=======
    Ok(views.html.main(sidebar,aboutTed))
  }
  
>>>>>>> 6b61aaba4af71bde1d85b96eb5757d317dee65a1

}
