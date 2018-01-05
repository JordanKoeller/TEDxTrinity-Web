package models

object TEDEventList {
  private val buf = collection.mutable.ArrayBuffer[TEDEvent]()
  
  def addEvent(event:TEDEvent):Unit = {
    buf += event
  }
  
  def list:List[TEDEvent] = {
    buf.toList
  }
}