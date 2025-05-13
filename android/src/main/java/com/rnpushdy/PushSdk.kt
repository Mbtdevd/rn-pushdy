package com.reactNativePushdy

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Notification
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.pushdy.Pushdy
import com.pushdy.Pushdy.PushdyDelegate
import org.json.JSONException
import org.json.JSONObject

class PushdySdk(reactContext: ReactApplicationContext) : PushdyDelegate, ActivityLifecycleCallbacks {

  private val mainAppContext: Context? = null
  private val smallIcon: Int? = null
  private var deviceId: String? = null
  private var readyForHandlingNotification = true
  private var reactContext: ReactApplicationContext = reactContext

  /**
   * This is list of event name those JS was already subscribed
   * Save this list to ensure that event will be sent to JS successfully
   *
   * - If this set size = 0 then JS event handler was not ready
   * - A default `enableFlag` event was subscribed to ensure that Set size always > 0 if JS was ready to handle
   * So If RNPushdyJS subscribe to no event, we will subscribe to default `enableFlag` event
   */
  private var subscribedEventNames: Set<String> = HashSet()

  /**
   * Return is app open from push or not
   * @return boolean
   */
  /**
   * This should return true when app open from push when in background or when app was killed
   */
  var isAppOpenedFromPush: Boolean = false


  /**
   * onRemoteNotificationRegistered fired when react context was not ready
   * OR when react-native-pushdy's JS thread have not subscribed to event
   * The result is your event will be fired and forgot,
   * Because of that, we change onRemoteNotificationRegistered to isRemoteNotificationRegistered
   */
  var isRemoteNotificationRegistered: Boolean = false
  override fun onRemoteNotificationRegistered(deviceToken: String) {
    this.isRemoteNotificationRegistered = true

    val params = Arguments.createMap()
    params.putString("deviceToken", deviceToken)
    sendEvent("onRemoteNotificationRegistered", params)
  }

  override fun onActivityPaused(activity: Activity) {
    Log.d("RNPushdy", "onActivityPaused")
    if (this.isAppOpenedFromPush) {
      this.isAppOpenedFromPush = false
    }
  }




  @Throws(Exception::class)
  fun initPushdy(options: ReadableMap) {
    var deviceId: String? = ""
    if (!options.hasKey("deviceId")) {
      throw Exception("RNPushdy.initPushdy: Invalid param: options.deviceId is required")
    } else {
      deviceId = options.getString("deviceId")

      if (deviceId == null || deviceId === "") {
        throw Exception("RNPushdy.initPushdy: Invalid param: options.deviceId cannot be empty")
      }
    }

    this.deviceId = deviceId
  }

  fun isNotificationEnabled(): Boolean {
    return Pushdy.isNotificationEnabled()
  }

  fun startHandleIncomingNotification() {
    readyForHandlingNotification = true
  }

  fun stopHandleIncomingNotification() {
    readyForHandlingNotification = false
  }

  override fun readyForHandlingNotification(): Boolean {
    return readyForHandlingNotification
  }

  fun setPushBannerAutoDismiss(autoDismiss: Boolean) {
    Pushdy.setPushBannerAutoDismiss(autoDismiss)
  }

  fun setPushBannerDismissDuration(sec: Float) {
    Pushdy.setPushBannerDismissDuration(sec)
  }

  fun useSDKHandler(enabled: Boolean) {
    Pushdy.useSDKHandler(enabled)
  }

  fun handleCustomInAppBannerPressed(notificationId: String) {
    Pushdy.handleCustomInAppBannerPressed(notificationId)
  }

  fun setCustomMediaKey(mediaKey: String?) {
    // Pushdy.setCustomMediaKey(mediaKey);
  }

  fun setDeviceId(deviceId: String?) {
    if (deviceId != null) {
      this.deviceId = deviceId
      Pushdy.setDeviceID(deviceId)
    }
  }

  fun getDeviceId(): String? {
    return Pushdy.getDeviceID()
  }

  fun getDeviceToken(): String? {
    return Pushdy.getDeviceToken()
  }

  fun getPendingNotification(): WritableMap? {
    val notification = Pushdy.getPendingNotification() ?: return null

    var data: WritableMap = WritableNativeMap()

    try {
      val jo = JSONObject(notification)
      data = ReactNativeJson.convertJsonToMap(jo)
    } catch (e: JSONException) {
      e.printStackTrace()
      Log.e("RNPushdy", "getPendingNotification Exception " + e.message)
    }

    return data
  }

  fun getPendingNotifications(): List<WritableMap> {
    val notifications = Pushdy.getPendingNotifications()
    val items: MutableList<WritableMap> = ArrayList()
    for (notification in notifications) {
      var data: WritableMap = WritableNativeMap()
      try {
        val jo = JSONObject(notification)
        data = ReactNativeJson.convertJsonToMap(jo)
      } catch (e: JSONException) {
        e.printStackTrace()
        Log.e("RNPushdy", "getPendingNotification Exception " + e.message)
      }

      items.add(data)
    }

    return items
  }

  fun getInitialNotification(): WritableMap? {
    var data: WritableMap = WritableNativeMap()

    try {
      var initialNotification: String? = null
      initialNotification = RNPushdyData.getString(reactContext, "initialNotification")
      if (initialNotification != null) {
        val jo = JSONObject(initialNotification)
        data = ReactNativeJson.convertJsonToMap(jo)
      } else {
        // fix wrong behavior when getInitialNotification always return {} when intialNotification is null
        // expected value when got no data: null
        return null
      }
    } catch (e: JSONException) {
      e.printStackTrace()
      Log.e("RNPushdy", "getPendingNotification Exception " + e.message)
    }

    return data
  }

  fun removeInitialNotification() {
    RNPushdyData.removeString(reactContext, "initialNotification")
  }

  fun setAttribute(attr: String, value: Any, commitImmediately: Boolean) {
    Pushdy.setAttribute(attr, value, commitImmediately)
  }

  fun pushAttribute(attr: String, value: Array<Any?>, commitImmediately: Boolean) {
    // NOTE: Pushdy.pushAttribute used for pushing every array element, it does not support to push array of elements
    // This might be bug of android SDK, so please do not use this function until SDK confirmed
    Pushdy.pushAttribute(attr, value, commitImmediately)
  }

  fun getPlayerID(): String? {
    return Pushdy.getPlayerID()
  }

  fun setBadgeOnForeground(enable: Boolean) {
    Pushdy.setBadgeOnForeground(enable)
  }

  fun setSubscribedEvents(subscribedEventNames: ArrayList<String>) {
    this.subscribedEventNames = HashSet(subscribedEventNames)
  }

  fun getPendingEvents(count: Number?): List<HashMap<String, Any>>? {
    return Pushdy.getPendingEvents((count as Int?)!!)
  }

  fun setPendingEvents(events: List<HashMap<String?, Any?>?>) {
    val convertedEvents = events.filterNotNull().map { it.filterKeys { key -> key != null } as HashMap<String, Any> }
    Pushdy.setPendingEvents(convertedEvents.toMutableList())
  }

  fun setApplicationId(applicationId: String) {
    Pushdy.setApplicationId(applicationId)
  }

  fun removePendingEvents(count: Number?) {
    Pushdy.removePendingEvents((count as Int?)!!)
  }

  fun trackEvent(eventName: String, attributes: HashMap<String, Any>, immediate: Boolean) {
    Pushdy.trackEvent(eventName, attributes, immediate, null, null)
  }

  fun pushPendingEvents() {
    Pushdy.pushPendingEvents(null, null)
  }

  fun subscribe() {
    Pushdy.subscribe()
  }

  fun getAllBanners(): com.google.gson.JsonArray? {
    return Pushdy.getAllBanners()
  }

  fun trackBanner(bannerId: String, type: String) {
    Pushdy.trackBanner(bannerId, type)
  }

  fun getBannerData(bannerId: String): com.google.gson.JsonObject? {
    return Pushdy.getBannerData(bannerId)
  }

  override fun customNotification(
    title: String,
    body: String,
    image: String,
    data: Map<String, Any>
  ): Notification? {
    TODO("Not yet implemented")
  }

  override fun onNotificationOpened(notification: String, fromState: String) {
    Log.d("RNPushdy", "onNotificationOpened: notification: $notification")
    var noti: WritableMap = WritableNativeMap()
    try {
      val jo = JSONObject(notification)
      noti = ReactNativeJson.convertJsonToMap(jo)
      RNPushdyData.setString(reactContext, "initialNotification", notification)
    } catch (e: JSONException) {
      e.printStackTrace()
      Log.e("RNPushdy", "onNotificationReceived Exception " + e.message)
    }

    val params = Arguments.createMap()
    params.putString("fromState", fromState)
    params.putMap("notification", RNPushdyData.toRNPushdyStructure(noti))

    sendEvent("onNotificationOpened", params)
    this.isAppOpenedFromPush = true
  }

  override fun onNotificationReceived(notification: String, fromState: String) {
    Log.d("RNPushdy", "onNotificationReceived: notification: $notification")

    var noti: WritableMap = WritableNativeMap()
    try {
      val jo = JSONObject(notification)
      noti = ReactNativeJson.convertJsonToMap(jo)
    } catch (e: JSONException) {
      e.printStackTrace()
      Log.e("RNPushdy", "onNotificationReceived Exception " + e.message)
    }

    val params = Arguments.createMap()
    params.putString("fromState", fromState)
    params.putMap("notification", RNPushdyData.toRNPushdyStructure(noti))

    sendEvent("onNotificationReceived", params)
  }

  override fun onRemoteNotificationFailedToRegister(e: Exception) {
    val params = Arguments.createMap()
    params.putString("exceptionMessage", e.message)
    sendEvent("onRemoteNotificationFailedToRegister", params)
  }

  //TODO: New Implement beyond here

  /**
   * Send event from native to JsThread
   * If the reactConetext has not available yet, => retry
   */
  private fun sendEvent(eventName: String, params: WritableMap? = null, retryCount: Int = 0) {
    var delayRetry = 1000L
    var maxRetry = 5

    /**
     * When you wake up your app from BG/closed state,
     * JS thread might not be available or ready to receive event
     */
    val jsThreadState = reactContext.lifecycleState
    val jsHandlerReady = subscribedEventNames.size > 0
    val jsSubscribeThisEvent = subscribedEventNames.contains(eventName)

    // Log.d("RNPushdy", "this.subscribedEventNames.size() = " + this.subscribedEventNames.size());
//       Log.d("RNPushdy", "jsThreadState = " + jsThreadState);
//       Log.d("RNPushdy", "reactActivated = " + Boolean.toString(reactActivated));
    Log.d("RNPushdy", "jsHandlerReady = " + subscribedEventNames.size.toString())
    Log.d("RNPushdy", "subscribedEventNames = " + subscribedEventNames.toString())

    if (jsThreadState == LifecycleState.RESUMED) {
      if (jsHandlerReady) {
        // Delay for some second to ensure react context work
        if (jsSubscribeThisEvent) {
          // this.sendEventWithDelay(eventName, params, 0);
          reactContext
            .getJSModule(
              DeviceEventManagerModule.RCTDeviceEventEmitter::class.java
            )
            .emit(eventName, params)
          Log.d("RNPushdy", "sendEvent: Emitted: $eventName")
        } else {
          Log.d(
            "RNPushdy",
            "sendEvent: Skip because JS not register the $eventName"
          )
        }

        // exit function to Prevent retry
        return
      } else {
        // Continue to Retry section
        // JS handle was ready so we increase the retry interval
        delayRetry = 300L
        maxRetry = 100 // around 30 secs
      }
    } else {
      // if (!reactActivated) {
      //   // Reset if subscribedEventNames JS is not ready
      //   this.subscribedEventNames = new HashSet<>();
      // }
      // continue to retry section
    }

    // ====== If cannot send then retry: ====
    Log.e(
      "RNPushdy",
      "sendEvent: $eventName was skipped because reactContext is null or not ready"
    )
  }

  /**
   * ===================  Pushdy hook =============================
   */
  /*========================
  Tried to support initPushdy SDK from JS whenever we want, without success.
  I stored the draft version here, to remind anyone who wanna init the SDK from JS,
  It's not possible at the moment, depend on PushdySDK and its working flow.
  // the OLD flow is:
  // 1. Init PushdySDK to do some required work (see the SDK)
  // 2. Whenever you wanna start Pushdy (often on JS App mounting), call setDeviceId, Pushdy SDK will create a `Player` on the dashboard
  // 3. From now on, PushdySDK is ready to work.
  Currently, the flow is:
  1. registerSdk to init native SDK and prepare
  2. Whenever you wanna start Pushdy (often on JS App mounting), call initPushdy, Pushdy SDK will create a `Player` on the dashboard
  3. From now on, PushdySDK is ready to work.

  public void old_registerSdk(android.content.Context mainAppContext, Integer smallIcon) {
    this.mainAppContext = mainAppContext;
    this.smallIcon = smallIcon;

    // Listen to activity change
    Pushdy.registerActivityLifecycle(mainAppContext);
  }

  public void old_initPushdy(ReadableMap options) throws Exception {
    String clientKey = "";
    if (!options.hasKey("clientKey")) {
      throw new Exception("RNPushdy.initPushdy: Invalid param: options.clientKey is required");
    } else {
      clientKey = options.getString("clientKey");

      if (clientKey == null) {
        throw new Exception("RNPushdy.initPushdy: Invalid param: options.clientKey cannot be empty");
      }
    }

    if (options.hasKey("deviceId")) {
      this.setDeviceId(options.getString("deviceId"));
    }

    if (this.smallIcon != null) {
      Pushdy.initWith(this.mainAppContext, clientKey, this, this.smallIcon);
    } else {
      Pushdy.initWith(this.mainAppContext, clientKey, this);
    }
    Pushdy.registerForRemoteNotification();
    Pushdy.setBadgeOnForeground(true);
  }
  ======================== */




  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
  }

  override fun onActivityStarted(activity: Activity) {
    Log.d("RNPushdy", "onActivityStarted")
  }

  override fun onActivityResumed(activity: Activity) {
  }



  override fun onActivityStopped(activity: Activity) {
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }

  override fun onActivityDestroyed(activity: Activity) {
  }

}
