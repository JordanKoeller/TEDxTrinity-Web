package models

import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

class TEDEvent(val title:String,
    val subtitle:Option[String],
    val date:LocalDateTime,
    val speaker:String,
    val venue:String,
    val maxSeats:Int,
    val description:String,
    val imgURL:Option[String]) {
  
  private var registeredGuests = 0
  
  def dayOfWeek:String = {
    val dayNum = date.getDayOfWeek.getValue
    dayNum match {
      case 1 => "Monday"
      case 2 => "Tuesday"
      case 3 => "Wednesday"
      case 4 => "Thursday"
      case 5 => "Friday"
      case 6 => "Saturday"
      case 7 => "Sunday"
    }
  }
  
  def time:String = {
    val hr = date.getHour
    val min = date.getMinute
    val hrFormatted = hr % 12
    val amPm = if (hr >= 12) "PM" else "AM"
    s"$hrFormatted:$min $amPm"
  }

  def dateNumberString(delimiter:String = "/"):String = {
    val month = date.getMonth.getValue
    val day = date.getDayOfMonth
    val year = date.getYear
    month+delimiter+day+delimiter+year
  }
  
  def monthSuffix:String = {
	  val month = date.getMonth.getValue
			  month match {
			  case 1 => "uary"
			  case 2 => "ruary"
			  case 3 => ""
			  case 4 => ""
			  case 5 => ""
			  case 6 => ""
			  case 7 => "y"
			  case 8 => "ust"
			  case 9 => "ember"
			  case 10 =>"ober"
			  case 11 =>"ember"
			  case 12 =>"ember"
	  }
    
  }
  
  def monthPrefix:String = {
    val month = date.getMonth.getValue
    month match {
      case 1 => "Jan"
      case 2 => "Feb"
      case 3 => "March"
      case 4 => "April"
      case 5 => "May"
      case 6 => "June"
      case 7 => "Jul"
      case 8 => "Aug"
      case 9 => "Sept"
      case 10 => "Oct"
      case 11 => "Nov"
      case 12 => "Dec"
    }
  }
  
  def addGuest() = {
    if (registeredGuests + 1 > maxSeats) println("EVENT FULL")//hrow EventFullException("")
    else registeredGuests += 1
  }
  
  def rmGuest() = {
    if (registeredGuests > 0) registeredGuests -= 1
  }
  
  def numAttendants:Int = {
    registeredGuests
  }
  
}

object TEDEvent {
  
  def apply(title:String,
      subtitle:String,
      day:Int,
      month:Int,
      year:Int,
      hr:Int,
      min:Int,
      speaker:String,
      venue:String,
      maxSeats:Int,
      description:String,
      imgURL:String = ""):TEDEvent = {
    val date = LocalDateTime.of(year,month,day,hr,min)
    val st = if (subtitle != "") Some(subtitle) else None
    val im = if (imgURL != "") Some(imgURL) else None
    new TEDEvent(title,st,date,speaker,venue,maxSeats,description,im)
  }
  
}