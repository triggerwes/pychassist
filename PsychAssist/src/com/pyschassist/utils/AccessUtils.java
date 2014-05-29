package com.pyschassist.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.psychassist.R;

public class AccessUtils {
	
	private static String TAG = "AccessUtils";
	
	public static String getVaultId(Context myContext) {
		SharedPreferences prefs = myContext.getSharedPreferences(myContext.getString(R.string.preference_key), Context.MODE_PRIVATE);
		String vaultId = prefs.getString(myContext.getString(R.string.var_vault_id), "-1");
		return vaultId;
	}

	public static String getUserId(Context myContext) {
		SharedPreferences prefs = myContext.getSharedPreferences(myContext.getString(R.string.preference_key), Context.MODE_PRIVATE);
		String userId = prefs.getString(myContext.getString(R.string.var_user_id), "-1"); 
		return userId;
	}

	public static String getDocId(Context myContext) {
		SharedPreferences prefs = myContext.getSharedPreferences(myContext.getString(R.string.preference_key), Context.MODE_PRIVATE);
		String docId = prefs.getString(myContext.getString(R.string.var_doc_id), "-1");
		return docId;
	}	

}
