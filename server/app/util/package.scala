import play.twirl.api.Html

package object util {

	implicit def stringToHtml(arg:String):Html = new Html(arg)
	implicit def optStringToOptHtml(arg:Option[String]):Option[Html] = {
		if (arg.isDefined) Some(new Html(arg.get)) else None
	}

	case class Article(title:Html,body:Html,subtitle:Option[Html]=None)

}

