import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  multiply(a: number, b: number): number;

  sampleMethod(
    stringArgument: string,
    numberArgument: number
  ): Promise<(string | number)[]>;

  makeCrash(): Promise<void>;

  initPushdy(options: { [key: string]: string }): Promise<boolean>;
  isRemoteNotificationRegistered(): Promise<boolean>;
  isAppOpenedFromPush(): Promise<boolean>;
  isNotificationEnabled(): Promise<boolean>;

  // iOS-specific methods
  registerForPushNotification(): Promise<boolean>;
  setApplicationIconBadgeNumber(count: number): Promise<void>;
  getApplicationIconBadgeNumber(): Promise<number>;

  // iOS-specific attribute setter
  setAttributeFromOption(options: {
    attr: string;
    data: any;
    immediately: boolean;
  }): Promise<boolean>;

  startHandleIncommingNotification(): Promise<boolean>;
  stopHandleIncommingNotification(): Promise<boolean>;
  getReadyForHandlingNotification(): Promise<boolean>;

  setPushBannerAutoDismiss(autoDismiss: boolean): Promise<boolean>;
  setPushBannerDismissDuration(sec: number): Promise<boolean>;

  setCustomPushBanner(viewType: string): Promise<boolean>;
  useSDKHandler(enabled: boolean): Promise<boolean>;
  handleCustomInAppBannerPressed(notificationId: string): Promise<boolean>;

  setCustomMediaKey(mediaKey: string): Promise<boolean>;
  setDeviceId(deviceId: string): Promise<boolean>;
  getDeviceId(): Promise<string>;
  getDeviceToken(): Promise<string>;

  getPendingNotification(): Promise<{ [key: string]: any }>;
  getPendingNotifications(): Promise<Array<{ [key: string]: any }>>;
  getInitialNotification(): Promise<{ [key: string]: any }>;
  removeInitialNotification(): Promise<boolean>;

  setAttributeFromValueContainer(
    attr: string,
    valueContainer: { data: any },
    commitImmediately: boolean
  ): Promise<boolean>;

  pushAttributeArray(
    attr: string,
    value: Array<string | number | boolean>,
    commitImmediately: boolean
  ): Promise<boolean>;

  getPlayerID(): Promise<string>;

  setBadgeOnForeground(enable: boolean): Promise<boolean>;
  setSubscribedEvents(subscribedEventNames: string[]): Promise<boolean>;

  getPendingEvents(count: number): Promise<Array<{ [key: string]: any }>>;
  setPendingEvents(events: Array<{ [key: string]: any }>): Promise<boolean>;

  setApplicationId(applicationId: string): Promise<boolean>;
  removePendingEvents(count: number): Promise<boolean>;

  trackEvent(
    eventName: string,
    eventProperties: { [key: string]: any },
    immediate: boolean
  ): Promise<boolean>;

  pushPendingEvents(): Promise<boolean>;
  subscribe(): Promise<boolean>;

  getAllBanners(): Promise<Array<{ [key: string]: any }>>;
  trackBanner(bannerId: string, type: string): Promise<boolean>;
  getBannerData(bannerId: string): Promise<{ [key: string]: any }>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RnPushdy');
