#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(SmileIdentity, RCTEventEmitter)

RCT_EXTERN_METHOD(captureSelfie:(NSDictionary *)options onCompleteCallback:(RCTResponseSenderBlock)callback/)

@end
