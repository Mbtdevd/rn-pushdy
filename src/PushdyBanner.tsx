/**
 * This component is used to display Pushdy banner
 * as a Popup WebView.
 * They will have 3 different types: TOP_BANNER: 0, BOTTOM_BANNER: 1, MIDDLE_BANNER: 2
 * If type is TOP_BANNER or BOTTOM_BANNER, the banner will be displayed as a Popup WebView and will be placed at the top or bottom of the screen.
 * If type is MIDDLE_BANNER, the banner will be displayed as a Popup WebView and will be placed at the center of the screen.
 */
import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
  useMemo,
} from 'react';
import { Dimensions, Image, StyleSheet, View } from 'react-native';
import { CameraRoll } from '@react-native-camera-roll/camera-roll';
import { TouchableOpacity } from 'react-native-gesture-handler';
import { SafeAreaView } from 'react-native-safe-area-context';
import Share from 'react-native-share';
import { captureRef } from 'react-native-view-shot';
import WebView from 'react-native-webview';
import type { WebViewMessageEvent } from 'react-native-webview';

// @ts-ignore
import icSave from './assets/save.png';
// @ts-ignore
import icClose from './assets/close.png';
// @ts-ignore
import icShare from './assets/share.png';

import {
  getStatusBarHeight,
  requestPermisionMediaAndroid,
  isTablet,
} from './Util';
import EventBus, { EventName } from './EventBus';

const color = {
  white: '#ffffff',
  grey1: '#1a1a1a',
};

const SCREEN_WIDTH =
  Dimensions.get('window').width || Dimensions.get('screen').width;

const SCREEN_HEIGHT =
  Dimensions.get('window').height || Dimensions.get('screen').height;

interface BannerState {
  bannerWidth: number;
  bannerHeight: number;
  border: number;
  html: string;
  url: string;
  margin: number;
  isShow: boolean;
  key: string;
  shareUrl: string;
  containerWidth: number;
  containerHeight: number;
  bannerId: string;
  onShow: () => void;
  translateY: number;
  btnCOpacity: number;
  bannerData: any; // Using any for now, but ideally should define a more specific type
}

interface BannerData {
  border?: number;
  html?: string;
  url?: string;
  margin?: number;
  key?: string;
  shareUrl?: string;
  bannerId?: string;
  onShow?: () => void;
  bannerData?: any; // Using any for now, but ideally should define a more specific type
}

interface PushdyBannerProps {
  bottomView?: React.ReactNode;
  topView?: React.ReactNode;
  userName?: string;
  userAvatar?: string;
  userGender?: 'female' | 'male';
  onShow?: () => void;
  onHide?: () => void;
  onAction?: (action: 'save' | 'share' | 'copylink') => void;
  bottomViewSize?: number;
}

interface PushdyBannerRef {
  showBanner: (data: BannerData) => void;
  hideBanner: () => void;
  capture: (isSilent?: boolean) => Promise<string>;
}

/**
 * How it works:
 * 1. When the app is launched, we will check if there is any banner to display.
 * 2. Display the banner with full height and check to size of the banner.
 * 3. If the banner is too large, we will resize it to fit the screen.
 * 4. Change the banner position and show it.
 * 5. If click outside the banner, we will close the banner.
 * @param {{
 *  bottomView?: React.Component,
 *  topView?: React.Component,
 *  userName?: string,
 *  userAvatar?: string,
 *  userGender?: 'female' | 'male',
 *  onShow?: () => void,
 *  onHide?: () => void,
 *  onAction?: (action: 'save' | 'share' | 'copylink') => void,
 *  bottomViewSize?: number,
 * }} props
 * @returns
 */
const PushdyBannerR = forwardRef<PushdyBannerRef, PushdyBannerProps>(
  (props, ref) => {
    const { bottomViewSize = 50 } = props;

    const webViewRef = useRef<WebView>(null);
    const viewShotRef = useRef<View>(null);
    // const translateY = useSharedValue(+Dimensions.get('window').height);
    // const btnCOpacity = useSharedValue(0);
    const [state, setState] = useState<BannerState>({
      bannerWidth: Dimensions.get('window').width,
      bannerHeight: Dimensions.get('window').height,
      border: 10,
      html: '',
      url: '',
      margin: 20,
      isShow: false,
      key: Math.random().toString(),
      shareUrl: '',
      containerWidth: Dimensions.get('window').width,
      containerHeight: Dimensions.get('window').height,
      bannerId: '',
      onShow: () => {},
      translateY: Dimensions.get('window').height,
      btnCOpacity: 0,
      bannerData: null,
    });

    const onPressOutside = () => {
      hideBannerWithAnimation();
    };

    const onLoadEnd = () => {
      // inject javascript to get the size of the banner
      const widthBanner = Math.min(450, SCREEN_WIDTH - state.margin * 3);
      // check if the height of the banner is larger than 65% of the screen height when calculate.
      // Consider reduce width of the banner to fit the screen.
      let maxHeight =
        SCREEN_HEIGHT * (isTablet() ? 0.8 : 0.65) + bottomViewSize;

      let jsCode = `
      var body = document.getElementsByTagName('body')[0];
      // // remove all in body but keep the banner div with class banner-pushdy
      // while (body.firstChild) {
      //   if (body.firstChild.className !== 'banner-pushdy') {
      //     body.removeChild(body.firstChild);
      //   } else {
      //     break;
      //   }
      // }

      // add body style margin: 0 padding: 0
      body.style.margin = '0';
      body.style.padding = '0';

      // add this meta to head <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no" >
      var meta = document.createElement('meta');
      meta.name = 'viewport';
      meta.content = 'initial-scale=1.0, maximum-scale=1.0, user-scalable=no';
      document.getElementsByTagName('head')[0].appendChild(meta);
      var banner = document.getElementsByClassName('banner-pushdy')[0];
      var bannerHeight = banner.offsetHeight;
      var bannerWidth = banner.offsetWidth;
      let newHeight = Math.floor(bannerHeight*${widthBanner}/bannerWidth)
      let newWidth = ${widthBanner}

      // check if the height of the banner is larger than maxHeight when calculate.
      // Consider reduce width of the banner to fit the screen.
      if (newHeight > ${maxHeight}) {
        newWidth = Math.floor(bannerWidth*${maxHeight}/bannerHeight)
        newHeight = Math.floor(${maxHeight})
      }

      var styleContent = document.getElementsByClassName("wrap-card")[0]
      styleContent.style.width = newWidth + "px"
      styleContent.style.height = newHeight + "px"
      var heightNew = newHeight + "px"
      styleContent.style['background-size'] = newWidth + "px "+ heightNew;

      // add user and avatar if have;
      var user_name = document.getElementsByClassName("userename")[0]
      var user_avatar = document.getElementById("avatar_")
      var greeting = document.getElementsByClassName("greeting")[0]
      let user_name_text = "${props.userName ? props.userName : ''}"
      let user_avatar_src = "${props.userAvatar ? props.userAvatar : ''}"
      let user_gender = "${props.userGender ? props.userGender : ''}"

      if(user_name && user_name_text) {
        user_name.innerHTML = "${props.userName}"
      }
      if(user_avatar && user_avatar_src) {
        user_avatar.src = "${props.userAvatar}"
      }

      if (greeting && user_gender) {
        if (user_gender === 'male') {
          greeting.innerHTML = "Kính tặng anh"
        } else if (user_gender === 'female') {
          greeting.innerHTML = "Kính tặng chị"
        }
      }

      window.ReactNativeWebView.postMessage(JSON.stringify({bannerHeight: newHeight, bannerWidth: newWidth }));
    `;

      webViewRef.current?.injectJavaScript(jsCode);
    };

    /**
     *
     * @param {WebViewMessageEvent} event
     */
    const onMessage = (event: WebViewMessageEvent) => {
      try {
        // handle message from the banner
        let data = JSON.parse(event.nativeEvent.data);
        if (data.bannerHeight && data.bannerWidth) {
          console.log(
            '{PushdyBanner} -> got banner size, prepare to show banner',
            data
          );
          setState((prevState) => ({
            ...prevState,
            bannerHeight: data.bannerHeight,
            bannerWidth: data.bannerWidth,
          }));
          setTimeout(() => {
            setState((prevState) => ({
              ...prevState,
              isShow: true,
            }));
          }, 250);
        }
      } catch (error) {}
    };

    const animeHide2 = () => {
      setState((prevState) => ({
        ...prevState,
        isShow: false,
        url: '',
        html: '',
        key: Math.random().toString(),
        shareUrl: '',
        bannerId: '',
      }));
    };

    const animeHide = () => {
      // translateY.value = withTiming(+Dimensions.get('window').height, {}, () => {
      //   runOnJS(animeHide2)();
      // });

      setState((prevState) => ({
        ...prevState,
        translateY: +Dimensions.get('window').height,
      }));
      requestAnimationFrame(() => {
        animeHide2();
      });
    };

    const hideBannerWithAnimation = () => {
      // btnCOpacity.value = withTiming(
      //   0,
      //   {
      //     duration: 100,
      //   },
      //   () => {
      //     runOnJS(animeHide)();
      //   },
      // );

      setState((prevState) => ({
        ...prevState,
        btnCOpacity: 0,
      }));
      requestAnimationFrame(() => {
        animeHide();
        EventBus.emit(EventName.ON_HIDE_PUSHDY_BANNER, state.bannerId);
        props.onHide && props.onHide();
      });
    };

    const showBanner = (data: BannerData) => {
      // When call showBanner, dissmiss all popup
      console.log('{PushdyBanner} -> onShowBanner', data);
      //@ts-ignore
      setState((prevState) => ({
        ...prevState,
        border: data?.border ?? 10,
        html: data?.html,
        url: data?.url ?? '',
        margin: data?.margin ?? 20,
        key: data?.key ?? Math.random().toString(),
        shareUrl: data?.shareUrl ?? '',
        bannerId: data?.bannerId ?? '',
        onShow: data?.onShow ?? (() => {}),
        bannerData: data?.bannerData,
      }));
      EventBus.emit(
        EventName.ON_SHOW_PUSHDY_BANNER,
        data?.bannerId ?? '',
        state.bannerData
      );
    };

    const hideBanner = () => {
      console.log('{PushdyBanner} -> onHideBanner');
      hideBannerWithAnimation();
    };

    const saveBannerToImage = async (isSilent = false) => {
      return new Promise<string>((resolve, reject) => {
        setState((prevState) => ({
          ...prevState,
          border: 0,
        }));
        setTimeout(() => {
          captureRef(viewShotRef, {
            result: 'tmpfile',
            format: 'jpg',
          })
            .then(async (path) => {
              if (isSilent) {
                resolve(path);
                return;
              }

              let isGranted = await requestPermisionMediaAndroid();
              if (isGranted) {
                CameraRoll.save(path)
                  .then(() => {
                    // Alert to notify user that the banner is saved to gallery.
                    EventBus.emit(
                      EventName.ON_ACTION_PUSHDY_BANNER,
                      state.bannerId,
                      'save',
                      path,
                      state.bannerData
                    );

                    props.onAction && props.onAction('save');

                    resolve(path);
                  })
                  .catch((err) => {
                    // Alert to notify user that the banner is not saved to gallery.
                    EventBus.emit(
                      EventName.ON_ERROR_PUSHDY_BANNER,
                      state.bannerId,
                      'save',
                      err,
                      state.bannerData
                    );

                    reject(err);
                  });
              } else {
                // Alert to ask user to grant permission.
                EventBus.emit(
                  EventName.ON_ERROR_PUSHDY_BANNER,
                  state.bannerId,
                  'save',
                  'PERMISSION_NOT_GRANTED',
                  state.bannerData
                );
                reject('PERMISSION_NOT_GRANTED');
              }
            })
            .catch((err) => {
              console.log('{PushdyBanner} -> saveBannerToImage -> err', err);
              EventBus.emit(
                EventName.ON_ERROR_PUSHDY_BANNER,
                state.bannerId,
                'save',
                err,
                state.bannerData
              );
              reject(err);
            })
            .finally(() => {
              setState((prevState) => ({
                ...prevState,
                border: 10,
              }));
            });
        }, 250);
      });
    };

    const onShown = () => {
      // show the button container
      // btnCOpacity.value = withSpring(1, {
      //   duration: 200,
      // });

      setState((prevState) => ({
        ...prevState,
        btnCOpacity: 1,
      }));

      if (typeof state.onShow === 'function') {
        state.onShow();
        props.onShow && props.onShow();
      }
    };

    const onPressShare = () => {
      console.log('{PushdyBanner} -> onPressShare');
      EventBus.emit(
        EventName.ON_ACTION_PUSHDY_BANNER,
        state.bannerId,
        'share',
        state.bannerData
      );
      props.onAction && props.onAction('share');
      // hideBanner();
      // share with base64.
      setState((prevState) => ({
        ...prevState,
        border: 0,
      }));
      setTimeout(() => {
        captureRef(viewShotRef, {
          result: 'tmpfile',
          format: 'jpg',
        })
          .then(async (path) => {
            Share.open({
              url: path,
            });
          })
          .catch((err) => {
            console.log('{PushdyBanner} -> saveBannerToImage -> err', err);
            EventBus.emit(
              EventName.ON_ERROR_PUSHDY_BANNER,
              state.bannerId,
              'share',
              err,
              state.bannerData
            );
          })
          .finally(() => {
            setState((prevState) => ({
              ...prevState,
              border: 10,
            }));
          });
      }, 250);
    };

    // const onPressCopylink = () => {
    //   Clipboard.setString(state.shareUrl);

    //   // Alert to notify user that the banner link is copied to clipboard.
    //   EventBus.emit('show_toast', 'Đã sao chép link thiệp', state.bannerData);
    // };

    useEffect(() => {
      EventBus.on(EventName.SHOW_PUSHDY_BANNER, showBanner);
      EventBus.on(EventName.HIDE_PUSHDY_BANNER, hideBanner);
      return () => {
        EventBus.off(EventName.SHOW_PUSHDY_BANNER, showBanner);
        EventBus.off(EventName.HIDE_PUSHDY_BANNER, hideBanner);
      };
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const animShow = () => {
      // translateY.value = withSpring(
      //   0,
      //   {
      //     mass: 0.9,
      //     damping: 10,
      //     stiffness: 45,
      //     overshootClamping: false,
      //     restDisplacementThreshold: 0.01,
      //     restSpeedThreshold: 2,
      //     duration: 300,
      //   },
      //   () => {
      //     runOnJS(onShown)();
      //   },
      // );

      setState((prevState) => ({
        ...prevState,
        translateY: 0,
      }));
      requestAnimationFrame(() => {
        onShown();
      });
    };

    useEffect(() => {
      if (state.isShow) {
        // show the banner
        animShow();

        // re-render the banner when the screen size is changed
        Dimensions.addEventListener('change', () => {
          setState((prevState) => ({
            ...prevState,
            containerWidth: Dimensions.get('window').width,
            containerHeight: Dimensions.get('window').height,
          }));
        });
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [state.isShow]);

    useImperativeHandle(ref, () => {
      return {
        showBanner,
        hideBanner,
        capture: saveBannerToImage,
      };
    });

    const conntainerStyles = {
      display: state.isShow ? undefined : 'none',
      flex: 1,
      position: 'absolute',
      top: 0,
      // backgroundColor: 'rgba(0, 0, 0, 0.8)',
      // paddingHorizontal: 20,
      justifyContent: 'center',
      alignItems: 'center',
    };

    const viewSnapshotStyle = {
      width: state.bannerWidth,
      height: state.bannerHeight,
      borderTopLeftRadius: state.border,
      borderTopRightRadius: state.border,
      borderBottomLeftRadius: state.border,
      borderBottomRightRadius: state.border,
      overflow: 'hidden',
      alignSelf: 'center',

      // position: 'absolute',
      // marginLeft: Dimensions.get('window').width / 2 - state.bannerWidth / 2 - 20,
      marginTop:
        state.containerHeight / 2 -
        state.bannerHeight / 2 -
        getStatusBarHeight() -
        50 / 2,
    };

    const webViewAutoStyle = {
      width: state.bannerWidth,
      height: state.bannerHeight,
      borderTopLeftRadius: state.border,
      borderTopRightRadius: state.border,
      borderBottomLeftRadius: state.border,
      borderBottomRightRadius: state.border,
      overflow: 'hidden',
    };

    const animatedStyle = {
      transform: [{ translateY: state.translateY }],
    };

    const viewStyle: any[] = [
      {
        width: Dimensions.get('window').width,
        height: Dimensions.get('window').height,
        borderRadius: state.border,
      },
      animatedStyle,
    ];

    const btnAnimatedStyle = {
      opacity: state.btnCOpacity,
    };

    const btnCStyle = [styles.btnC, btnAnimatedStyle];

    const source = useMemo(() => {
      if (state.url) {
        return { uri: state.url };
      }
      return { html: state.html };
    }, [state.url, state.html]);

    return (
      <SafeAreaView style={conntainerStyles as any}>
        <View style={styles.hideBackground} />
        <TouchableOpacity style={styles.hiddenTouch} onPress={onPressOutside}>
          <View collapsable={false} style={viewStyle}>
            {props.topView}
            <View
              collapsable={false}
              pointerEvents="none"
              style={viewSnapshotStyle as any}
              ref={viewShotRef}
            >
              <WebView
                key={state.key}
                ref={webViewRef}
                showsVerticalScrollIndicator={false}
                scalesPageToFit={true}
                scrollEnabled={false}
                setSupportMultipleWindows={false}
                textInteractionEnabled={false}
                overScrollMode={'never'}
                source={source}
                onMessage={onMessage}
                onLoadEnd={onLoadEnd}
                style={webViewAutoStyle as any}
              />
            </View>
            <View style={btnCStyle}>
              <TouchableOpacity
                style={[
                  styles.btn,
                  {
                    backgroundColor: '#E61D42',
                  },
                ]}
                onPress={() => saveBannerToImage()}
              >
                <Image source={icSave} style={styles.icon} />
              </TouchableOpacity>
              <TouchableOpacity
                style={[
                  styles.btn,
                  {
                    backgroundColor: '#01BC75',
                  },
                ]}
                onPress={onPressShare}
              >
                <Image source={icShare as any} style={styles.icon} />
              </TouchableOpacity>
              {/* <TouchableOpacity style={styles.btn} onPress={onPressCopylink}>
              <IconSvg name={'icCopy'} size={16} color={color.grey1} />
            </TouchableOpacity> */}
              <TouchableOpacity
                style={[
                  styles.btn,
                  {
                    backgroundColor: '#DFDFDF',
                  },
                ]}
                onPress={onPressOutside}
              >
                <Image source={icClose} style={styles.iconClose} />
              </TouchableOpacity>
            </View>
            {props.bottomView}
          </View>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }
);

export const PushdyBanner = React.memo(PushdyBannerR);

const styles = StyleSheet.create({
  hiddenTouch: {
    width: Dimensions.get('window').width,
    height: Dimensions.get('window').height,
  },
  hideBackground: {
    position: 'absolute',
    width: 10000,
    height: 10000,
    backgroundColor: 'rgba(0, 0, 0, 0.8)',
  },
  btnC: {
    alignSelf: 'center',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },

  btn: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: color.white,
    alignItems: 'center',
    justifyContent: 'center',
    marginHorizontal: 10,
    marginTop: 10,
  },

  icon: {
    width: 40,
    height: 40,
    tintColor: 'white',
  },
  iconClose: {
    width: 40,
    height: 40,
    tintColor: '#7f7f7f',
  },
});
