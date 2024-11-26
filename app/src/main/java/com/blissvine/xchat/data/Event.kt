package com.blissvine.xchat.data


open class Event<out  T>(val content : T){
  var hasBeenHandled = false
    fun getcontentOrNull(): T? {
         return if (hasBeenHandled) null
      else {
        hasBeenHandled = true
           content
         }
    }
}