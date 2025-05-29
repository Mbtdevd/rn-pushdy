#import <RnPushdySpec/RnPushdySpec.h>
#import <PushdySDK/PushdySDK-Swift.h>
#import <RnPushdy/RnPushdy-Swift.h>

NS_ASSUME_NONNULL_BEGIN

@interface RnPushdy : NativeRnPushdySpecBase <NativeRnPushdySpec>

+ (instancetype)sharedInstance;

- (void)emitEventWithName:(NSString *)name body:(NSDictionary *)body;

@end

@interface RNPushdyDelegateHandler : NSObject <PushdyDelegate>

- (void)sendEventToJsWithEventName:(NSString *)eventName body:(NSDictionary<NSString *, id> *)body;

@end
NS_ASSUME_NONNULL_END

