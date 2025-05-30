import { Platform } from 'react-native';
import { PERMISSIONS, RESULTS, request } from 'react-native-permissions';
import { getStatusBarHeight as _getStatusBarHeight } from 'react-native-status-bar-height';
import DeviceInfo from 'react-native-device-info';
import coerce from 'semver/functions/coerce';
import lte from 'semver/functions/lte';
import type { SemVer } from 'semver';

export const isAndroidAbove = (version: string): boolean => {
  const osVersion = DeviceInfo.getSystemVersion();
  return (
    Platform.OS === 'android' &&
    lte(coerce(version) as SemVer, coerce(osVersion) as SemVer)
  );
};

export const isTablet = (): boolean => {
  const DeviceModel = DeviceInfo.isTablet();
  return DeviceModel;
};

export const requestPermisionMediaAndroid = async (
  callback: () => void = () => {}
): Promise<boolean | undefined> => {
  try {
    if (Platform.OS === 'ios') {
      callback();
      return true;
    }

    if (isAndroidAbove('13.0.0')) {
      const granted1 = await request(PERMISSIONS.ANDROID.READ_MEDIA_IMAGES);
      const granted2 = await request(PERMISSIONS.ANDROID.READ_MEDIA_VIDEO);

      if (granted1 === RESULTS.GRANTED && granted2 === RESULTS.GRANTED) {
        callback();
      }
      return granted1 === RESULTS.GRANTED && granted2 === RESULTS.GRANTED;
    } else {
      let result = await request(PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE);
      if (result === RESULTS.GRANTED) {
        callback();
      }

      return result === RESULTS.GRANTED;
    }
  } catch (e) {
    console.log('{Permission} --> requestPermisionMediaAndroid --> err:', e);
    return false;
  }
};

export const getStatusBarHeight = (): number => {
  const DeviceModel = DeviceInfo.getDeviceId();
  const StatusBarHeight = {
    height:
      DeviceModel.toLowerCase().trim() == 'iphone14,7' ||
      DeviceModel.toLowerCase().trim() == 'iphone15,2'
        ? 49
        : _getStatusBarHeight(),
  };

  return StatusBarHeight.height;
};

/**
 * @deprecated
 * No longer needed. Server will build banner
 * and send to client.
 */
export const buildBanner = (_data: Record<string, any>): void => {};
