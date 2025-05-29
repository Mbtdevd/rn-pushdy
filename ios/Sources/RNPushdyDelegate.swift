//
//  RNPushdyDelegate.swift
//  RnPushdy
//
//  Created by MBT-M4 on 23/5/25.
//

import Foundation
import PushdySDK
// import React
// import React.RCTEventEmitter

/**
 This class was intend to send event from RNPushdy to JS thread on PushdySDK events triggered
 All configuration was done in RNPushdy
 */
@objc class RNPushdyDelegate: NSObject, PushdyDelegate {
  public func sendEventToJs(eventName:String, body:[AnyHashable : Any] = [:]) {
  
  }
  
    
   public func onNotificationOpened(_ notification: [String : Any], fromState: String) {
       print("{RNPushdy.onNotificationOpened} from state: \(fromState)")
     RNPushdySDK.setLocalData(key: "initialNotification", value: notification);
     
     RNPushdySDK.setIsAppOpenedFromPush(isPushOpening: true);
       let universalNotification = RNPushdySDK.toRNPushdyStructure(notification)
       sendEventToJs(eventName: "onNotificationOpened", body:["notification": universalNotification, "fromState": fromState])
   }
   
   public func onNotificationReceived(_ notification: [String : Any], fromState: String) {
       print("{RNPushdy.onNotificationReceived} from state: \(fromState)")
       
       let universalNotification = RNPushdySDK.toRNPushdyStructure(notification)
       sendEventToJs(eventName: "onNotificationReceived", body:["notification": universalNotification, "fromState": fromState])
   }
    
    /**
     This function should be declare for RNPushdyDelegate. By default, consider that
     adding RNPushdyDelegate success means open push notification can be handle by React Native App.
     */
    public func readyForHandlingNotification() -> Bool {
        return true;
    }
   
   public func onRemoteNotificationRegistered(_ deviceToken: String) {
       print("{RNPushdy.onRemoteNotificationRegistered} deviceToken: \(deviceToken)")
       
       sendEventToJs(eventName: "onRemoteNotificationRegistered", body:["deviceToken": deviceToken])
   }
   
   public func onRemoteNotificationFailedToRegister(_ error: NSError) {
       print("{RNPushdy.onRemoteNotificationFailedToRegister} error: \(error)")
       
       sendEventToJs(eventName: "onRemoteNotificationFailedToRegister", body:["error": error])
   }
}
