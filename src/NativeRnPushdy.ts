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

  getPendingNotification(): Promise<{ [key: string]: string }>;
  getPendingNotifications(): Promise<Array<{ [key: string]: string }>>;
  getInitialNotification(): Promise<{ [key: string]: string }>;
  removeInitialNotification(): Promise<boolean>;

  setAttributeFromValueContainer(
    attr: string,
    valueContainer: { data: string | number | boolean },
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

  getPendingEvents(count: number): Promise<Array<{ [key: string]: string }>>;
  setPendingEvents(events: Array<{ [key: string]: string }>): Promise<boolean>;

  setApplicationId(applicationId: string): Promise<boolean>;
  removePendingEvents(count: number): Promise<boolean>;

  trackEvent(
    eventName: string,
    eventProperties: { [key: string]: string | number | boolean },
    immediate: boolean
  ): Promise<boolean>;

  pushPendingEvents(): Promise<boolean>;
  subscribe(): Promise<boolean>;

  getAllBanners(): Promise<Array<{ [key: string]: string }>>;
  trackBanner(bannerId: string, type: string): Promise<boolean>;
  getBannerData(bannerId: string): Promise<{ [key: string]: string }>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('RnPushdy');
