#import "RnPushdy.h"
#import <PushdySDK/PushdySDK-Swift.h>

@implementation RnPushdy
RCT_EXPORT_MODULE()

static RnPushdy *_sharedInstance = nil;
static RNPushdyDelegateHandler *delegate = nil;
static dispatch_once_t onceToken;

- (NSNumber *)multiply:(double)a b:(double)b {
    NSNumber *result = @(a * b);

    return result;
}

+ (instancetype)sharedInstance {
  return _sharedInstance;
}

- (instancetype)init {
  self = [super init];
  [self initDelegateHandler];
  if (self) {
    _sharedInstance = self;
  }
  return self;
}

- (void)emitEventWithName:(NSString *)name body:(NSDictionary *)body {
  if ([name isEqualToString:@"onNotificationOpened"]) {
    [self emitOnNotificationOpened:body];
  }

  // Add other events if needed
}

- (void)initDelegateHandler {
  
  dispatch_once(&onceToken, ^{
    delegate = [[RNPushdyDelegateHandler alloc] init];
  });

  [RNPushdySDK setDelegateHandler:delegate];
}

- (void)getAllBanners:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)getApplicationIconBadgeNumber:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)getBannerData:(NSString *)bannerId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)getDeviceId:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)getDeviceToken:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [RNPushdySDK getDeviceToken:resolve reject:reject];
}


- (void)getInitialNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [RNPushdySDK getInitialNotification:resolve reject:reject];
}


- (void)getPendingEvents:(double)count resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
}


- (void)getPendingNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
}


- (void)getPendingNotifications:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
}


- (void)getPlayerID:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [RNPushdySDK getPlayerID:resolve reject:reject];
}


- (void)getReadyForHandlingNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)handleCustomInAppBannerPressed:(NSString *)notificationId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)initPushdy:(NSDictionary *)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [RNPushdySDK initPushdy:options resolve:resolve reject:reject];
}


- (void)isAppOpenedFromPush:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)isNotificationEnabled:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)isRemoteNotificationRegistered:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)makeCrash:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)pushAttributeArray:(NSString *)attr value:(NSArray *)value commitImmediately:(BOOL)commitImmediately resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)pushPendingEvents:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)registerForPushNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)removeInitialNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [RNPushdySDK removeInitialNotification:resolve reject:reject];
}


- (void)removePendingEvents:(double)count resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)sampleMethod:(NSString *)stringArgument numberArgument:(double)numberArgument resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setApplicationIconBadgeNumber:(double)count resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setApplicationId:(NSString *)applicationId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  [RNPushdySDK setApplicationId: applicationId resolve:resolve reject:reject];
}

- (void)setAttributeFromOption:(JS::NativeRnPushdy::SpecSetAttributeFromOptionOptions &)options resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setAttributeFromValueContainer:(NSString *)attr valueContainer:(JS::NativeRnPushdy::SpecSetAttributeFromValueContainerValueContainer &)valueContainer commitImmediately:(BOOL)commitImmediately resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setBadgeOnForeground:(BOOL)enable resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setCustomMediaKey:(NSString *)mediaKey resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setCustomPushBanner:(NSString *)viewType resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
}


- (void)setDeviceId:(NSString *)deviceId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setPendingEvents:(NSArray *)events resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setPushBannerAutoDismiss:(BOOL)autoDismiss resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setPushBannerDismissDuration:(double)sec resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)setSubscribedEvents:(NSArray *)subscribedEventNames resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)startHandleIncommingNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)stopHandleIncommingNotification:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)subscribe:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)trackBanner:(NSString *)bannerId type:(NSString *)type resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)trackEvent:(NSString *)eventName eventProperties:(NSDictionary *)eventProperties immediate:(BOOL)immediate resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
}


- (void)useSDKHandler:(BOOL)enabled resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject { 
  
}


- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRnPushdySpecJSI>(params);
}

@end


@implementation RNPushdyDelegateHandler

- (void)sendEventToJsWithEventName:(NSString *)eventName body:(NSDictionary<NSString *, id> *)body {
  NSLog(@"Sending event to JS: %@, body: %@", eventName, body);
  [[RnPushdy sharedInstance] emitEventWithName:eventName body:body];
}

- (void)onNotificationOpened:(NSDictionary<NSString *,id> *)notification fromState:(NSString *)fromState {
  NSLog(@"[Pushdy] Notification opened from state: %@", fromState);
  [RNPushdySDK onNotificationOpened:notification fromState:fromState];
  
  NSDictionary *universalNotification = [RNPushdySDK toRNPushdyStructureObjcC:notification];
      [self sendEventToJsWithEventName:@"onNotificationOpened" body:@{
          @"notification": universalNotification,
          @"fromState": fromState
      }];
}

- (void)onNotificationReceived:(NSDictionary<NSString *,id> *)notification fromState:(NSString *)fromState {
  NSLog(@"[Pushdy] Notification received from state: %@", fromState);
  
  NSDictionary *universalNotification = [RNPushdySDK toRNPushdyStructureObjcC:notification];
      [self sendEventToJsWithEventName:@"onNotificationReceived" body:@{
          @"notification": universalNotification,
          @"fromState": fromState
      }];
}

- (BOOL)readyForHandlingNotification {
  return YES;
}

- (void)onRemoteNotificationRegistered:(NSString *)deviceToken {
  NSLog(@"[Pushdy] Device token registered: %@", deviceToken);
  [self sendEventToJsWithEventName:@"onRemoteNotificationRegistered" body:@{ @"deviceToken": deviceToken }];
}

- (void)onRemoteNotificationFailedToRegister:(NSError *)error {
  NSLog(@"[Pushdy] Failed to register device token: %@", error);
  [self sendEventToJsWithEventName:@"onRemoteNotificationFailedToRegister" body:@{ @"error": error.localizedDescription }];
}

@end
