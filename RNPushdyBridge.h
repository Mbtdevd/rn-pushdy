//
//  RNPushdyBridge.h
//  Pods
//
//  Created by MBT-M4 on 28/5/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface RNPushdyBridge : NSObject

+ (void)emitEvent:(NSString *)name body:(NSDictionary *)body;

@end

NS_ASSUME_NONNULL_END
