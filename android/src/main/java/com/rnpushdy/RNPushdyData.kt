package com.reactNativePushdy

import android.content.Context
import com.facebook.react.bridge.Dynamic
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap

object RNPushdyData {
  /**
   * Convert PushdySDK datastructure into RNPushdy datastructure.
   * RNPushdy DS must be indentical between iOS & Android.
   * This's called Universal Data Structure (UDS).
   *
   * Example notification param:
   * notification:
   * notification:
   * body: "Ít nhất 7 tỉnh thành sẽ bị ảnh hưởng, cần sẵn sàng tinh thần ứng phó"
   * image: "https://vortex.accuweather.com/adc2010/images/icons-numbered/01-l.png"
   * title: "Bão số 6 hướng đi khó lường"
   * data:
   * _nms_image: "https://znews-photo.zadn.vn/w660/Uploaded/cqdhmdxwp/2019_08_14/kuncherry90_67233597_965480387137244_1646091755794003933_n_copy.jpg"
   * _notification_id: "4a28399a-7365-45d4-92cc-a2089d953feb"
   * _nms_payload: "eyJub3RpZmljYXRpb24iOnsidGl0bGUiOiJBbmggVsaw4bujbmcgZOG7sSDEkeG7i25oIGLDoW4gdGl2aSIsImJvZHkiOiJIw6NuZyB4ZSB04bu3IHBow7ogUGjhuqFtIE5o4bqtdCBWxrDhu6NuZyB24burYSBjw7MgdGhv4bqjIHRodeG6rW4gbOG7i2NoIHPhu60sIGzhuqFpIHLDsiBy4buJIHRpbiBt4bubaSB24buBIHRpdmkuIEjDo25nIHhlIHThu7cgcGjDuiBQaOG6oW0gTmjhuq10IFbGsOG7o25nIHbhu6thIGPDsyB0aG/huqMgdGh14bqtbiBs4buLY2ggc+G7rSwgbOG6oWkgcsOyIHLhu4kgdGluIG3hu5tpIHbhu4EgdGl2aS4gSMOjbmcgeGUgdOG7tyBwaMO6IFBo4bqhbSBOaOG6rXQgVsaw4bujbmcgduG7q2EgY8OzIHRob+G6oyB0aHXhuq1uIGzhu4tjaCBz4butLCBs4bqhaSByw7IgcuG7iSB0aW4gbeG7m2kgduG7gSB0aXZpIn0sImRhdGEiOnsicHVzaF9hY3Rpb24iOiJuYXZfdG9fYXJ0aWNsZV9kZXRhaWwiLCJwdXNoX2RhdGEiOnsiYXJ0aWNsZV9pZCI6MTc5MjY5fSwidGl0bGUiOiJCw6NvIHPhu5EgNiBoxrDhu5tuZyDEkWkga2jDsyBsxrDhu51uZyIsImJvZHkiOiLDjXQgbmjhuqV0IDcgdOG7iW5oIHRow6BuaCBz4bq9IGLhu4sg4bqjbmggaMaw4bufbmcsIGPhuqduIHPhurVuIHPDoG5nIHRpbmggdGjhuqduIOG7qW5nIHBow7MiLCJpbWFnZSI6Imh0dHBzOi8vdm9ydGV4LmFjY3V3ZWF0aGVyLmNvbS9hZGMyMDEwL2ltYWdlcy9pY29ucy1udW1iZXJlZC8wMS1sLnBuZyIsIl9ub3RpZmljYXRpb25faWQiOiI0YTI4Mzk5YS03MzY1LTQ1ZDQtOTJjYy1hMjA4OWQ5NTNmZWIifX0="
   *
   * Example UDS:
   * universalNotification:
   * _nms_image: "https://znews-photo.zadn.vn/w660/Uploaded/cqdhmdxwp/2019_08_14/kuncherry90_67233597_965480387137244_1646091755794003933_n_copy.jpg"
   * _notification_id: "4a28399a-7365-45d4-92cc-a2089d953feb"
   * _nms_payload: "eyJub3RpZmljYXRpb24iOnsidGl0bGUiOiJBbmggVsaw4bujbmcgZOG7sSDEkeG7i25oIGLDoW4gdGl2aSIsImJvZHkiOiJIw6NuZyB4ZSB04bu3IHBow7ogUGjhuqFtIE5o4bqtdCBWxrDhu6NuZyB24burYSBjw7MgdGhv4bqjIHRodeG6rW4gbOG7i2NoIHPhu60sIGzhuqFpIHLDsiBy4buJIHRpbiBt4bubaSB24buBIHRpdmkuIEjDo25nIHhlIHThu7cgcGjDuiBQaOG6oW0gTmjhuq10IFbGsOG7o25nIHbhu6thIGPDsyB0aG/huqMgdGh14bqtbiBs4buLY2ggc+G7rSwgbOG6oWkgcsOyIHLhu4kgdGluIG3hu5tpIHbhu4EgdGl2aS4gSMOjbmcgeGUgdOG7tyBwaMO6IFBo4bqhbSBOaOG6rXQgVsaw4bujbmcgduG7q2EgY8OzIHRob+G6oyB0aHXhuq1uIGzhu4tjaCBz4butLCBs4bqhaSByw7IgcuG7iSB0aW4gbeG7m2kgduG7gSB0aXZpIn0sImRhdGEiOnsicHVzaF9hY3Rpb24iOiJuYXZfdG9fYXJ0aWNsZV9kZXRhaWwiLCJwdXNoX2RhdGEiOnsiYXJ0aWNsZV9pZCI6MTc5MjY5fSwidGl0bGUiOiJCw6NvIHPhu5EgNiBoxrDhu5tuZyDEkWkga2jDsyBsxrDhu51uZyIsImJvZHkiOiLDjXQgbmjhuqV0IDcgdOG7iW5oIHRow6BuaCBz4bq9IGLhu4sg4bqjbmggaMaw4bufbmcsIGPhuqduIHPhurVuIHPDoG5nIHRpbmggdGjhuqduIOG7qW5nIHBow7MiLCJpbWFnZSI6Imh0dHBzOi8vdm9ydGV4LmFjY3V3ZWF0aGVyLmNvbS9hZGMyMDEwL2ltYWdlcy9pY29ucy1udW1iZXJlZC8wMS1sLnBuZyIsIl9ub3RpZmljYXRpb25faWQiOiI0YTI4Mzk5YS03MzY1LTQ1ZDQtOTJjYy1hMjA4OWQ5NTNmZWIifX0="
   * body: "Ít nhất 7 tỉnh thành sẽ bị ảnh hưởng, cần sẵn sàng tinh thần ứng phó"
   * image: "https://vortex.accuweather.com/adc2010/images/icons-numbered/01-l.png"
   * title: "Bão số 6 hướng đi khó lường"
   * ...
   * data:
   * push_action: "nav_to_article_detail"
   * push_data: "{"article_id":179269}"
   * ...
   *
   * @param notification The notification from PushdySDK
   * @return Universal data structure
   */
  fun toRNPushdyStructure(notification: WritableMap): WritableMap {
    val universalNotification: WritableMap = WritableNativeMap()
    val nData: WritableMap = WritableNativeMap()
    val platformOption: WritableMap = WritableNativeMap()


    val excludedKeys: MutableSet<String> = HashSet()
    excludedKeys.add("notification")
    excludedKeys.add("data")

    val notiKey = notification.getMap("notification")
    val dataKey = notification.getMap("data")
    val restKey: ReadableMap = copyExclude(notification, excludedKeys)

    if (dataKey != null) {
      nData.merge(dataKey)
    }

    if (notiKey != null) {
      copyBaseOnFieldName(notiKey, universalNotification, nData, platformOption)
    }

    copyBaseOnFieldName(restKey, universalNotification, nData, platformOption)

    universalNotification.putMap("data", nData)
    universalNotification.putMap("android", platformOption)

    return universalNotification
  }

  /**
   * Copy sourceMap to `sourceMap` or `data` base on field name
   *
   * @param sourceMap
   * @param notification
   * @param data
   * @param platformOption
   */
  private fun copyBaseOnFieldName(
    sourceMap: ReadableMap,
    notification: WritableMap,
    data: WritableMap,
    platformOption: WritableMap
  ) {
    val i = sourceMap.keySetIterator()
    while (i.hasNextKey()) {
      val key = i.nextKey()
      when (key) {
        "title", "subtitle", "body", "image" -> notification.putString(
          key,
          sourceMap.getString(key)
        )

        "autoCancel" -> platformOption.putString(key, sourceMap.getString(key))
        else -> if (key.startsWith("_")) {
          copyDynamicField(sourceMap, notification, key)
        } else {
          copyDynamicField(sourceMap, data, key)
        }
      }
    }
  }


  fun copyDynamicField(srcMap: WritableMap, dstMap: WritableMap, key: String) {
    // TODO: java.lang.AssertionError: Illegal type provided
    // reproduction: Receive push in foreground
    when (srcMap.getType(key)) {
      ReadableType.Null -> dstMap.putNull(key)
      ReadableType.Boolean -> dstMap.putBoolean(key, srcMap.getBoolean(key))
      ReadableType.Number -> dstMap.putDouble(key, srcMap.getDouble(key))
      ReadableType.String -> dstMap.putString(key, srcMap.getString(key))
      ReadableType.Map ->         // dstMap.putMap(key, DataHelper.toWritableMap(srcMap.getMap(key))); // react-native@0.60.x
        dstMap.putMap(key, srcMap.getMap(key)) // react-native@0.61.x
      ReadableType.Array ->         // dstMap.putArray(key, DataHelper.toWritableArray(srcMap.getArray(key))); // react-native@0.60.x
        dstMap.putArray(key, srcMap.getArray(key)) // react-native@0.61.x
    }
  }

  fun copyDynamicField(srcMap: ReadableMap, dstMap: WritableMap, key: String) {
    // TODO: java.lang.AssertionError: Illegal type provided
    // reproduction: Receive push in foreground
    when (srcMap.getType(key)) {
      ReadableType.Null -> dstMap.putNull(key)
      ReadableType.Boolean -> dstMap.putBoolean(key, srcMap.getBoolean(key))
      ReadableType.Number -> dstMap.putDouble(key, srcMap.getDouble(key))
      ReadableType.String -> dstMap.putString(key, srcMap.getString(key))
      ReadableType.Map ->          // dstMap.putMap(key, DataHelper.toWritableMap(srcMap.getMap(key))); // react-native@0.60.x
        dstMap.putMap(key, srcMap.getMap(key)) // react-native@0.61.x
      ReadableType.Array ->         // dstMap.putArray(key, DataHelper.toWritableArray(srcMap.getArray(key))); // react-native@0.60.x
        dstMap.putArray(key, srcMap.getArray(key)) // react-native@0.61.x
    }
  }

  fun convertDynamicFieldToJavaType(field: Dynamic): Any? {
    var value: Any? = null
    value = when (field.type) {
      ReadableType.Null -> null
      ReadableType.Boolean -> field.asBoolean()
      ReadableType.Number -> field.asDouble()
      ReadableType.String -> field.asString()
      ReadableType.Map -> field.asMap().toHashMap()
      ReadableType.Array -> field.asArray().toArrayList().toTypedArray()
    }

    return value
  }


  /**
   * [WIP] Create a copy of map with only specified keys
   *
   * @param map The source map
   * @param keys The list of key would be copy to output map
   */
  fun pick(map: WritableMap?, keys: Array<String?>?): WritableMap {
    return WritableNativeMap()
  }

  /**
   * Create a copy of map with only specified keys
   *
   * @param map The source map
   * @param excludedKeys The list of key would be copy to output map
   */
  fun copyExclude(map: WritableMap, excludedKeys: Set<String>): WritableMap {
    val out: WritableMap = WritableNativeMap()
    val i = map.keySetIterator()

    while (i.hasNextKey()) {
      val key = i.nextKey()
      if (!excludedKeys.contains(key)) {
        copyDynamicField(map, out, key)
      }
    }

    return out
  }

  fun setString(context: Context, key: String?, value: String?) {
    context.getSharedPreferences("PushdyStorageRef", Context.MODE_PRIVATE).edit()
      .putString(key, value).commit()
  }

  fun getString(context: Context, key: String?): String? {
    return context.getSharedPreferences("PushdyStorageRef", Context.MODE_PRIVATE)
      .getString(key, null)
  }

  fun removeString(context: Context, key: String?) {
    context.getSharedPreferences("PushdyStorageRef", Context.MODE_PRIVATE).edit().remove(key)
      .commit()
  }
}
