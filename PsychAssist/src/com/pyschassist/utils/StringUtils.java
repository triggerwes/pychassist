package com.pyschassist.utils;

import android.util.Log;

public class StringUtils {

	public static String keyToString(String key) {
		Log.i("StringUtils", "Key:=" + key);
		StringBuffer buf = new StringBuffer();
		buf.append(key.substring(0,8) + "-");
		buf.append(key.substring(8,12) + "-");
		buf.append(key.substring(12,16) + "-");
		buf.append(key.substring(16,20) + "-");
		buf.append(key.substring(20));
		
		Log.i("StringUtils", "Formatted key:=" + buf.toString());
		return buf.toString(); 
	}
	
	public static String keyStringToKey(String keyString){
		return keyString.replace("-","");
	}	
	
	
}
