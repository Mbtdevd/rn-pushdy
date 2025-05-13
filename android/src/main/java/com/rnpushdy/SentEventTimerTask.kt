package com.reactNativePushdy

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import java.util.Date
import java.util.TimerTask

/*
Usage
SentEventTimerTask task = new SentEventTimerTask() {
 public void run() {
   Log.d("Pushdy", "[" + Thread.currentThread().getName() + "] Task performed on: " + new Date());
   sendEvent(this.getEventName(), this.getParams());
 }
};
task.setEventName(eventName);
task.setParams(params);
Timer timer = new Timer("PushdySendEventRetry");
timer.schedule(task, 200L); // delay in ms
*/
class SentEventTimerTask : TimerTask() {
  var eventName: String = ""
  var params: WritableMap? = null
  var retryCount: Int = 0
  var maxRetryCount: Int = 5
  var delay: Int = 0
  var reactContext: ReactApplicationContext? = null

  override fun run() {
    Log.d(
      "Pushdy",
      "[Default run()] Task performed on: " + Date() + " | " + "Thread's name: " + Thread.currentThread().name
    )
  }
}
