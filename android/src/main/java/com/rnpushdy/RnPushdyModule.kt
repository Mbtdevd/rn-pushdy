package com.rnpushdy

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = RnPushdyModule.NAME)
class RnPushdyModule(reactContext: ReactApplicationContext) :
  NativeRnPushdySpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  override fun sampleMethod(stringArgument: String?, numberArgument: Double, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun makeCrash(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun initPushdy(options: ReadableMap?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun isRemoteNotificationRegistered(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun isAppOpenedFromPush(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun isNotificationEnabled(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun startHandleIncommingNotification(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun stopHandleIncommingNotification(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getReadyForHandlingNotification(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setPushBannerAutoDismiss(autoDismiss: Boolean, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setPushBannerDismissDuration(sec: Double, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setCustomPushBanner(viewType: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun useSDKHandler(enabled: Boolean, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun handleCustomInAppBannerPressed(notificationId: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setCustomMediaKey(mediaKey: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setDeviceId(deviceId: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getDeviceId(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getDeviceToken(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getPendingNotification(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getPendingNotifications(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getInitialNotification(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun removeInitialNotification(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setAttributeFromValueContainer(
    attr: String?,
    valueContainer: ReadableMap?,
    commitImmediately: Boolean,
    promise: Promise?
  ) {
    TODO("Not yet implemented")
  }

  override fun pushAttributeArray(
    attr: String?,
    value: ReadableArray?,
    commitImmediately: Boolean,
    promise: Promise?
  ) {
    TODO("Not yet implemented")
  }

  override fun getPlayerID(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setBadgeOnForeground(enable: Boolean, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setSubscribedEvents(subscribedEventNames: ReadableArray?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getPendingEvents(count: Double, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setPendingEvents(events: ReadableArray?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun setApplicationId(applicationId: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun removePendingEvents(count: Double, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun trackEvent(
    eventName: String?,
    eventProperties: ReadableMap?,
    immediate: Boolean,
    promise: Promise?
  ) {
    TODO("Not yet implemented")
  }

  override fun pushPendingEvents(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun subscribe(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getAllBanners(promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun trackBanner(bannerId: String?, type: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  override fun getBannerData(bannerId: String?, promise: Promise?) {
    TODO("Not yet implemented")
  }

  companion object {
    const val NAME = "RnPushdy"
  }
}
