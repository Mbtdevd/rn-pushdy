//
//  RNPushdy.swift
//  RnPushdy
//
//  Created by MBT-M4 on 23/5/25.
//

import Foundation
import os
import PushdySDK


/**
 Prevent this module re-intialized due some error;
 */
var didIntialized:Bool = false;
/**
 Check RNPushdy has been connect to PushdySDK success:
 that means PushdySDK has: deviceId was set, delegate, delegateHandler, launchOptions.
 */
var initlizedWithPushdy:Bool = false;

@objc(RNPushdySDK)
public class RNPushdySDK: NSObject {
  private static var delegateHandler: PushdyDelegate? = nil;
  @objc public static var instance:RNPushdySDK? = nil;
  
  @objc private static var clientKey:String? = nil
  @objc private static var delegate:UIApplicationDelegate? = nil
  @objc private static var launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
  @objc private static var mIsAppOpenedFromPush: Bool = false
  
  override init() {
    super.init()
    
    // React native will create new instance after we create singleton instance in AppDelegate.m
    // So i react do reinit, we need to grant new instance to RNPushdy.instance
    // This ensure that RCTBridge will work properly
    RNPushdySDK.instance = self
  }
  
  /**
   * See android SDK and document to know why we use this var
   */
  private var isRemoteNotificationRegistered:Bool = false
  
  /*
   - Expose singleton instance for using in AppDelegate.m
   - TODO: Check this fn is still be used or not
   */
  @objc public static func getInstance() -> RNPushdySDK {
    if RNPushdySDK.instance == nil {
      RNPushdySDK.instance = RNPushdySDK();
      print("[ERROR] This case should not happen: Manually init RNPushdy() instance")
    }
    
    return instance!;
  }
  
  @objc
  public static func sayHello(_
                              stringArgument: String, numberArgument: Int
  ) -> Void {
    let msg:String = "stringArgument: \(stringArgument), numberArgument: \(numberArgument)"
    
    NSLog(msg);
  }
  
  @objc public static func setDelegateHandler(_ handler: PushdyDelegate) {
    RNPushdySDK.delegateHandler = handler
    NSLog("Pushdy Delegate Handler Set!")
    Pushdy.setDelegateHandler(delegateHandler: delegateHandler!)
  }
  
  @objc
  public static func testFnPromise(_ resolve: @escaping (Any?) -> Void,
                                   reject: @escaping (String, String, NSError?) -> Void) {
    // Simulate success
    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
      resolve("✅ Task completed from Swift")
    }
    
    // OR, for error:
    // reject("ERR_CODE", "Something went wrong", nil)
  }
  
  
  
  @objc
  public static func registerSdk(_ clientKey:String, delegate:UIApplicationDelegate, launchOptions: [UIApplication.LaunchOptionsKey: Any]?) {
    // if have intialized, prevent intializing again;
    if (didIntialized){
      return;
    }
    
    didIntialized = true;
    self.clientKey = clientKey
    self.delegate = delegate
    self.launchOptions = launchOptions
    
    let deviceData:[String:Any]? = self.getLocalData(key: "deviceId");
    let deviceId = deviceData?["id"] as? String;
    /**
     If having deviceId in localStorage, try to get it and initialized right now without delegateHandler.
     if not, do not intialize. Wait for initPushdy from react-native to initialize
     */
    if (deviceId != nil) {
      Pushdy.setDeviceID(deviceId!);
      Pushdy.initWith(clientKey: self.clientKey!
                      , delegate: self.delegate!
                      , launchOptions: self.launchOptions)
      initlizedWithPushdy = true;
      /**
       Need to call this to observe the incoming push notification.
       */
      Pushdy.registerForPushNotifications();
    }
    
    self.mIsAppOpenedFromPush = self.checkIsAppOpenedFromPush(_launchOptions: launchOptions);
    
    if #available(iOS 13.0, *){
      NotificationCenter.default.addObserver(self, selector: #selector(self.appEntersBackground), name: UIScene.willDeactivateNotification, object: nil);
    } else {
      NotificationCenter.default.addObserver(self, selector: #selector(self.appEntersBackground), name: UIApplication.willResignActiveNotification, object: nil);
    }
  }
  
  @objc
  public static func checkIsAppOpenedFromPush(_launchOptions:[UIApplication.LaunchOptionsKey: Any]?) ->Bool {
    if let launchOptions = _launchOptions, let _ = launchOptions[UIApplication.LaunchOptionsKey.remoteNotification] as? [String : Any] {
      return true;
    } else {
      return false;
    }
  }
  
  @objc
  public static func setIsAppOpenedFromPush(isPushOpening: Bool)->Void {
    self.mIsAppOpenedFromPush = isPushOpening;
  }
  
  @objc
  public static func appEntersBackground() {
    RNPushdySDK.self.mIsAppOpenedFromPush = false;
  }
  
  @objc
  public static func testFn() -> String {
    return "Hello from RNPushdy!"
  }
  
  @objc
  public static func initPushdy(_
                                options: NSDictionary,
                                resolve: @escaping (Any?) -> Void,
                                reject: @escaping (String, String, NSError?) -> Void
  ) {
    var deviceId:String = "";
    if options["deviceId"] != nil {
      deviceId = options["deviceId"] as! String
    } else {
      reject("InvalidArgument", "RNPushdy.initPushdy: Invalid param: options.deviceId is required", NSError(domain: "", code: 200, userInfo: nil))
    }
    if deviceId.isEmpty {
      reject("InvalidArgument", "RNPushdy.initPushdy: Invalid param: options.deviceId cannot be empty", NSError(domain: "", code: 200, userInfo: nil))
    }
    let dict: [String:Any] = [
      "id": deviceId,
    ];
    RNPushdySDK.setLocalData(key: "deviceId", value: dict);
    /**
     If RNPushdy doesn't intialized with Pushdy from registerSdk due to not having deviceId
     in localStorage, try to initialize it here
     */
    /**
     -------- NEW FLOW --------
     If HAVE deviceId,
     1. try to initialize with PushdySDK without handler called by registerSdk (invoked by applicationDidFinishLauchingWithOptions.
     2. try to add delegateHandler to PushdySDK later called by initPushdy(invoked by initPushdy when ReactNative App start and ready to handle message.
     If not,
     1. registerSdk normally. (without initialize with Pushdy)
     2. initPushdy when ReactNative App start and ready to handle message.
     */
    if(!initlizedWithPushdy){
      Pushdy.setDeviceID(deviceId);
      Pushdy.initWith(clientKey: RNPushdySDK.clientKey!
                      , delegate: RNPushdySDK.delegate!
                      , delegaleHandler:  delegateHandler!
                      , launchOptions: RNPushdySDK.launchOptions)
    } else {
      /**
       Try to set handler for RNPushdy that intialized by registerSdk.
       */
      //          Pushdy.setDelegateHandler(delegateHandler: delegateHandler!);
    }
    
    //old
    //        Pushdy.setDeviceID(deviceId);
    //        Pushdy.initWith(clientKey: RNPushdy.clientKey!
    //        , delegate: RNPushdy.delegate!
    //        , delegaleHandler: RNPushdyDelegate()
    //        , launchOptions: RNPushdy.launchOptions)
    
    
    /**
     * If user allowed, you still need to call this to register UNUserNotificationCenter delegation
     * Otherwise, you still receive push in bg but not fg, you cannot handle push click action
     * Android was registered by default so you don't need to register for android
     
     === ios token flow: ===
     JS ready
     RNPushdy.initWith(deviceToken passed from JS)
     Pushdy.registerForPushNotifications()
     notificationCenter.requestAuthorization()
     application.registerForRemoteNotifications()
     ---> register xong --->
     OS > application:didRegisterForRemoteNotificationsWithDeviceToken:
     
     */
    Pushdy.registerForPushNotifications()
    
    resolve(true)
  }
  
  @objc
  public static func getDeviceToken(_
                                    resolve: @escaping (Any?) -> Void,
                                    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let t:String? = Pushdy.getDeviceToken()
    
    print("[RNPushdy.getDeviceToken] got token: %s", t ?? "nil")
    resolve(t ?? "")
  }
  
  @objc
  public static func getPlayerID(_
                                 resolve: @escaping (Any?) -> Void,
                                 reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    resolve(Pushdy.getPlayerID())
  }
  
  @objc
  public static func getInitialNotification(_
                                            resolve: @escaping (Any?) -> Void,
                                            reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    // if have pending notification --> this means that open push from background
    let pendingNotification = Pushdy.getPendingNotification()
    
    if (pendingNotification != nil) {
      RNPushdySDK.setLocalData(key: "initialNotification", value: pendingNotification!)
      resolve(pendingNotification)
      // then pop it, treats it as completed when and save it to initialNotification
      // for more consistent flow
      Pushdy.popPendingNotification()
      return
    }
    
    let initialNotification:[String: Any]? = RNPushdySDK.getLocalData(key: "initialNotification");
    if initialNotification != nil {
      let universalNotification = RNPushdySDK.toRNPushdyStructure(initialNotification ?? [:]);
      resolve(universalNotification)
    } else {
      resolve(nil);
    }
  }
  //
  @objc
  public static func removeInitialNotification(_
                                               resolve: @escaping (Any?) -> Void,
                                               reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    RNPushdySDK.removeLocalData(key: "initialNotification");
    resolve(true);
  }
  
  @objc
  public static func setApplicationId(
    _ appId: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    // NSLog("RNPushdy.setApplicationId: \(appId)")
    Pushdy.setApplicationId(appId)
    resolve(true)
  }
  
  @objc
  public static func onNotificationOpened(_ notification: [String : Any], fromState: String) {
    print("{RNPushdy.onNotificationOpened} from state: \(fromState)")
    RNPushdySDK.setLocalData(key: "initialNotification", value: notification);
  }
  
  @objc
  public static func toRNPushdyStructureObjcC(_ notification:[String : Any]) -> [String : Any] {
    var universalNotification:[String : Any] = [:]
    var nData:[String : Any] = [:];
    var platformOption:[String : Any] = [:];
    
    for (k, v) in notification {
      switch k {
      case "title",
        "subtitle",
        "body",
        "image",
        "aps":
        universalNotification[k] = v
        break;
      case "alertAction",
        "badge":
        platformOption[k] = v
        break;
      default:
        if k.starts(with: "_") {
          universalNotification[k] = v
        } else {
          nData[k] = v
        }
      }
    }
    
    universalNotification["data"] = nData
    universalNotification["ios"] = platformOption
    
    return universalNotification
  }
  
  // Code from here
  
  @objc
  public static func registerForPushNotifications(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.registerForPushNotifications()
    resolve(true)
  }
  
  @objc
  public static func isRemoteNotificationRegistered(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    resolve(isRemoteNotificationRegistered)
  }
  
  @objc
  public static func isNotificationEnabled(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.checkNotificationEnabled { (enabled: Bool) in
      resolve(enabled)
    }
  }
  
  @objc
  public static func setPushBannerAutoDismiss(
    autoDismiss: Bool,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.setPushBannerAutoDismiss(autoDismiss)
    resolve(true)
  }
  
  @objc
  public static func setPushBannerDismissDuration(
    seconds: NSNumber,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.setPushBannerDismissDuration(Double(truncating: seconds))
    resolve(true)
  }
  
  /*
   WIP: This func is not complete and might not work.
   - TODO: Implement this func
   */
  @objc
  public static func setCustomPushBanner(
    viewType: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    var banner: UIView = UIView()
    switch (viewType) {
    case "largeIconAsBigImage":
      banner = UIView()
      break
    case "todo":
      banner = UIView()
      break
    default:
      NSLog("[Pushdy] setCustomPushBanner: Invalid viewType: %@", viewType)
    }
    
    do {
      try Pushdy.setCustomPushBanner(banner)
      resolve(true)
    } catch {
      reject("PushdySDK_ERR", "[Pushdy] setCustomPushBanner: oh got exception!", error as NSError)
    }
  }
  
  @objc
  public static func setCustomMediaKey(
    mediaKey: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    PDYNotificationView.setCustomMediaKey(mediaKey)
    resolve(true)
  }
  
  @objc
  public static func setDeviceId(
    id: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.setDeviceID(id)
    resolve(true)
  }
  
  @objc
  public static func getDeviceId(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    resolve(Pushdy.getDeviceID())
  }
  
  @objc
  public static func getPendingNotification(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let pendingNotification = Pushdy.getPendingNotification()
    if pendingNotification == nil {
      resolve(nil)
    } else {
      let universalNotification = RNPushdySDK.toRNPushdyStructure(pendingNotification!)
      resolve(universalNotification)
    }
  }
  
  @objc
  public static func getPendingNotifications(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let pendingNotifications: [[String: Any]] = Pushdy.getPendingNotifications()
    let universalNotifications: [[String: Any]] = pendingNotifications.map { i -> [String: Any] in
      return RNPushdySDK.toRNPushdyStructure(i)
    }
    resolve(universalNotifications)
  }
  
  @objc
  public static func isAppOpenedFromPush(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    resolve(RNPushdySDK.self.mIsAppOpenedFromPush)
  }
  
  @objc
  public static func setAttributeFromValueContainer(
    attr: String,
    valueContainer: NSDictionary,
    commitImmediately: Bool,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    do {
      let data: Any = valueContainer["data"] as Any
      try Pushdy.setAttribute(attr, value: data, commitImmediately: commitImmediately)
      resolve(true)
    } catch {
      reject("PushdySDK_ERR", "[Pushdy] setAttribute: oh got exception!", error as NSError)
    }
  }
  
  @objc
  public static func setAttributeFromOption(
    options: NSDictionary,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) {
    var attr: String = ""
    if options["attr"] != nil {
      attr = options["attr"] as! String
    } else {
      reject("InvalidArgument", "RNPushdy.initPushdy: Invalid param: options.deviceId is required", NSError(domain: "", code: 200, userInfo: nil))
      return
    }
    
    do {
      let data = options["data"] as Any
      let immediately = options["immediately"] as! Bool
      try Pushdy.setAttribute(attr, value: data, commitImmediately: immediately)
      resolve(true)
      return
    } catch {
      reject("PushdySDK_ERR", "[Pushdy] setAttribute: oh got exception!", error as NSError)
      return
    }
  }
  
  @objc
  public static func setAttribute(
    attr: String,
    value: String,
    commitImmediately: Bool,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    do {
      try Pushdy.setAttribute(attr, value: value, commitImmediately: commitImmediately)
      resolve(true)
    } catch {
      reject("PushdySDK_ERR", "[Pushdy] setAttribute: oh got exception!", error as NSError)
    }
  }
  
  @objc
  public static func pushAttribute(
    attr: String,
    values: [String],
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    do {
      try Pushdy.pushAttribute(attr, value: values)
      resolve(true)
    } catch {
      reject("PushdySDK_ERR", "[Pushdy] pushAttribute: oh got exception!", error as NSError)
    }
  }
  
  @objc
  public static func setApplicationIconBadgeNumber(
    count: NSNumber,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let countInt = count.intValue
    Pushdy.setApplicationIconBadgeNumber(countInt)
    resolve(true)
  }
  
  @objc
  public static func getApplicationIconBadgeNumber(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    resolve(Pushdy.getApplicationIconBadgeNumber())
  }
  
  @objc
  public static func useSDKHandler(
    _ enabled: Bool,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.useSDKHandler(enabled)
    resolve(true)
  }
  
  @objc
  public static func handleCustomInAppBannerPressed(
    _ notificationId: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.handleCustomInAppBannerPressed(notificationId)
    resolve(true)
  }
  
  @objc
  public static func getPendingEvents(
    _ count: NSNumber,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let countInt = count.intValue
    let pendingEvents = Pushdy.getPendingTrackEvents(count: countInt)
    resolve(pendingEvents)
  }
  
  @objc
  public static func removePendingEvents(
    _ count: NSNumber,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let countInt = count.intValue
    Pushdy.removePendingTrackingEvents(countInt)
    resolve(true)
  }
  
  @objc
  public static func setPendingEvents(
    _ events: NSArray,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.setPendingTrackEvents(events as! [NSObject])
    resolve(true)
  }
  
  @objc
  public static func trackEvent(
    _ eventName: String,
    params: NSDictionary,
    immediate: Bool,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    NSLog("RNPushdy.trackEvent: \(eventName)")
    do {
      try Pushdy.trackEvent(eventName: eventName, params: params, immediate: immediate, completion: { _ in }, failure: { _, _ in })
    } catch {}
    resolve(true)
  }
  
  @objc
  public static func pushPendingEvents(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    try? Pushdy.pushPendingEvents()
    resolve(true)
  }
  
  @objc
  public static func subscribe(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.subscribe()
    resolve(true)
  }
  
  @objc
  public static func getAllBanners(
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let banners = Pushdy.getAllBanners()
    resolve(banners)
  }
  
  @objc
  public static func trackBanner(
    _ bannerId: String,
    type: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    Pushdy.trackBanner(bannerId: bannerId, type: type)
    resolve(true)
  }
  
  @objc
  public static func getBannerData(
    _ bannerId: String,
    resolve: @escaping (Any?) -> Void,
    reject: @escaping (String, String, NSError?) -> Void
  ) -> Void {
    let bannerData = Pushdy.getBannerData(bannerId: bannerId)
    resolve(bannerData)
  }
  
  
}
