package controllers

import javax.inject._

import com.example.playscalajs.shared.SharedMessages
import play.api.mvc._

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    val calendar = views.html.calendar()
    val sidebar = views.html.sidebar(calendar)
    val event1 = views.html.tedEvent()
    
    Ok(views.html.main(sidebar,event1))
  }
  
  def testTemplate = Action {
    Ok(views.html.testTemplate())
  }
  
  def aboutTED = Action {
    val aboutTed = views.html.aboutTedContent()
    val calendar = views.html.calendar()
    val sidebar = views.html.sidebar(calendar)
    Ok(views.html.main(sidebar,aboutTed))
  }
  

}
