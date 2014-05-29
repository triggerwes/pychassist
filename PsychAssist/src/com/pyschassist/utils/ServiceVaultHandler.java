package com.pyschassist.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

public class ServiceVaultHandler {

	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String GET = "GET";
	private static final String DELETE = "DELETE";
	private static final String BASEURL = "https://api.truevault.com/v1/";
	private String vaultId = "";
	private String userId = "";
	private String docId = "";
	private Context myContext;
	private String TAG = "ServiceVaultHandler";

	public ServiceVaultHandler(Context context,String docId) {
		this.myContext = context;
		this.vaultId = AccessUtils.getVaultId(myContext);
		this.userId = AccessUtils.getUserId(myContext);
		Log.d(TAG,"userID from service vault util:=" + userId);
		if(docId.trim().length() > 0){
			this.docId = docId;
		} else {
			this.docId = AccessUtils.getDocId(myContext);			
		}

	}
	
	
	public String createRecord(String data) throws UnsupportedEncodingException {
		return processHTTPRequest(POST, data);
	}

	public String updateRecord(String data) throws UnsupportedEncodingException {
		return processHTTPRequest(PUT, data);
	}

	public String getRecord() throws UnsupportedEncodingException { 
		return processHTTPRequest(GET, "");
	}

	public String deleteRecord() throws UnsupportedEncodingException {
		return processHTTPRequest(DELETE, "");
	}

	private String getAuthorization() throws UnsupportedEncodingException {
		
		return "Basic "	+ Base64.encodeToString((userId + ":").getBytes("UTF-8"), Base64.NO_WRAP);
	}

	private String processHTTPRequest(String method, String data) {		
		String resultString = "";
		boolean isSuccess = true;
		try {
			URL obj = new URL(createURL(method));
			Log.i(TAG, createURL(method));
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			Log.i(TAG, "GetAuthorization:" + getAuthorization());
			
			// Set Headers
			conn.setRequestProperty("Authorization", getAuthorization());
			conn.setRequestMethod(method);
			if(method.equals(POST) || method.equals(PUT)){
				conn.setDoOutput(true);
			}
			conn.connect();

			// /Write body
			if (method.equals(POST) || method.equals(PUT)) {
				OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
				out.write("document=" + Base64.encodeToString(data.getBytes(), Base64.NO_WRAP));
				out.flush();
				out.close();
			}

			// Process response
			int status = 0;
			try{
				status = conn.getResponseCode();
			} catch (IOException e){
				status = conn.getResponseCode();
			}
			if (status > 200) {
				isSuccess = false;
				resultString = convertStreamToString(conn.getErrorStream()); 
			} else {
				resultString = convertStreamToString(conn.getInputStream());
			}
			
			Log.i(TAG, resultString);
			conn.disconnect();
		} catch (Exception e) {
			resultString = "Communication with the server error";
		}
		Log.i(TAG, "ProcessHTTPRequest End");
		 
		if(method.equals(GET) && isSuccess ){
			resultString =  new String(Base64.decode(resultString, Base64.NO_WRAP));
		}
		
		return resultString;
	}
	
	private String createURL(String method) {
		String httpUrl = "";
		if (method.equals(POST)) {
			httpUrl = BASEURL + "vaults/" + vaultId + "/documents";
		} else {
			Log.i("ServiceVaultHandler", docId);
			httpUrl = BASEURL + "vaults/" + vaultId + "/documents/" + docId;			
		}

		return httpUrl; 
	}	
	

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			try {				
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
