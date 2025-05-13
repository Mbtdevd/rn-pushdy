package com.reactNativePushdy

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object ReactNativeJson {
  fun convertMapToWritableMap(map: java.util.HashMap<*, *>): WritableMap {
    val writableMap = Arguments.createMap()

    for ((k, v) in map) {
      if (v == null) {
        writableMap.putNull(k.toString())
      } else {
        val varType = v.javaClass.simpleName

        when (varType) {
          "String" -> writableMap.putString(k.toString(), v as String)
          "Integer" -> writableMap.putInt(k.toString(), v as Int)
          "Double" -> writableMap.putDouble(k.toString(), v as Double)
          "Boolean" -> writableMap.putBoolean(k.toString(), v as Boolean)
          "LinkedTreeMap" -> writableMap.putMap(k.toString(), convertMapToWritableMap(v as HashMap<String, *>))
          else -> {
            val msg =
              "[ERROR] [Pushdy] ReactNativeJson.convertMapToWritableMap: Unhandled varType: $varType | Please notice Pushdy's developer to fix this problem"
            Log.e("Pushdy", msg)
            writableMap.putString(k.toString(), msg)
          }
        }
      }
    }

    return writableMap
  }

  /**
   *
   * @param writableMap
   * @return
   * @throws Exception
   */
  @Throws(Exception::class)
  fun convertWritableMapToMap(writableMap: WritableMap?): Map<String, *> {
    throw Exception("This function was not implemented")

    //    Map<String, ?> map = new HashMap<>();
//
//    ReadableMapKeySetIterator iterator = writableMap.keySetIterator();
//    while (iterator.hasNextKey()) {
//      String key = iterator.nextKey();
//      switch (writableMap.getType(key)) {
//        case Null:
//          map.put(key, null);
//          break;
//        case Boolean:
//          map.put(key, writableMap.getBoolean(key));
//          break;
//        case Number:
//          map.put(key, writableMap.getDouble(key));
//          break;
//        case String:
//          map.put(key, writableMap.getString(key));
//          break;
//        case Map:
//          map.put(key, convertMapToJson(writableMap.getMap(key)));
//          break;
//        case Array:
//          map.put(key, convertArrayToJson(writableMap.getArray(key)));
//          break;
//      }
//    }

//    return map;
  }

  fun convertJavaMapToJson(map: Map<String?, *>): JSONObject {
    return JSONObject(map)
  }

  fun convertJsonToJavaMap(jsonObject: JSONObject): HashMap<String, *> {
    val m = Gson().fromJson(
      jsonObject.toString(),
      HashMap::class.java
    )


    return m as HashMap<String, *>
  }

  @Throws(JSONException::class)
  fun convertJsonToMap(jsonObject: JSONObject): WritableMap {
    val map: WritableMap = WritableNativeMap()

    val iterator = jsonObject.keys()
    while (iterator.hasNext()) {
      val key = iterator.next()
      val value = jsonObject[key]
      if (value is JSONObject) {
        map.putMap(key, convertJsonToMap(value))
      } else if (value is JSONArray) {
        map.putArray(key, convertJsonToArray(value))
      } else if (value is Boolean) {
        map.putBoolean(key, value)
      } else if (value is Int) {
        map.putInt(key, value)
      } else if (value is Double) {
        map.putDouble(key, value)
      } else if (value is String) {
        map.putString(key, value)
      } else {
        map.putString(key, value.toString())
      }
    }
    return map
  }

  @Throws(JSONException::class)
  fun convertJsonToArray(jsonArray: JSONArray): WritableArray {
    val array: WritableArray = WritableNativeArray()

    for (i in 0..<jsonArray.length()) {
      val value = jsonArray[i]
      if (value is JSONObject) {
        array.pushMap(convertJsonToMap(value))
      } else if (value is JSONArray) {
        array.pushArray(convertJsonToArray(value))
      } else if (value is Boolean) {
        array.pushBoolean(value)
      } else if (value is Int) {
        array.pushInt(value)
      } else if (value is Double) {
        array.pushDouble(value)
      } else if (value is String) {
        array.pushString(value)
      } else {
        array.pushString(value.toString())
      }
    }
    return array
  }

  @Throws(JSONException::class)
  fun convertMapToJson(readableMap: ReadableMap): JSONObject {
    val `object` = JSONObject()
    val iterator = readableMap.keySetIterator()
    while (iterator.hasNextKey()) {
      val key = iterator.nextKey()
      when (readableMap.getType(key)) {
        ReadableType.Null -> `object`.put(key, JSONObject.NULL)
        ReadableType.Boolean -> `object`.put(key, readableMap.getBoolean(key))
        ReadableType.Number -> `object`.put(key, readableMap.getDouble(key))
        ReadableType.String -> `object`.put(key, readableMap.getString(key))
        ReadableType.Map -> `object`.put(key, convertMapToJson(readableMap.getMap(key)!!))
        ReadableType.Array -> `object`.put(
          key, convertArrayToJson(
            readableMap.getArray(key)!!
          )
        )
      }
    }
    return `object`
  }

  @Throws(JSONException::class)
  fun convertArrayToJson(readableArray: ReadableArray): JSONArray {
    val array = JSONArray()
    for (i in 0..<readableArray.size()) {
      when (readableArray.getType(i)) {
        ReadableType.Null -> {}
        ReadableType.Boolean -> array.put(readableArray.getBoolean(i))
        ReadableType.Number -> array.put(readableArray.getDouble(i))
        ReadableType.String -> array.put(readableArray.getString(i))
        ReadableType.Map -> array.put(convertMapToJson(readableArray.getMap(i)!!))
        ReadableType.Array -> array.put(convertArrayToJson(readableArray.getArray(i)!!))
      }
    }
    return array
  }
}
