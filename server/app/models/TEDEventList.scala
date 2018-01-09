package models

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicReference

object TEDEventList {
  private val buf = new AtomicReference(Array[TEDEvent]())
  
  
  def updateList(seq:Seq[Tables.EventDescriptionsRow]):Unit = {
    val eventList = seq.map{eventRow =>
      val date = eventRow.eventDate.toLocalDate()
      val time = eventRow.eventTime.toLocalTime()
      val dateTime = LocalDateTime.of(date, time).atZone(ZoneId.of("GMT")).withZoneSameInstant(ZoneId.of("America/Chicago"))
      TEDEvent(
          eventRow.title,
          eventRow.subtitle.getOrElse(""),
          dateTime.getDayOfMonth,
          dateTime.getMonthValue,
          dateTime.getYear,
          dateTime.getHour,
          dateTime.getMinute,
          eventRow.speaker,
          eventRow.venue,
          eventRow.maxSeats,
          eventRow.description,
          eventRow.mediaLink.getOrElse(""))
    }
    buf.set(eventList.toArray)
  }
  
  def list:List[TEDEvent] = {
    buf.get.toList
  }
}