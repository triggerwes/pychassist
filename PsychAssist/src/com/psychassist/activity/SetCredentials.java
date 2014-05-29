package com.psychassist.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.psychassist.R;

import com.psychassist.model.Patient;
import com.psychassist.model.PatientList;
import com.pyschassist.utils.AccessUtils;
import com.pyschassist.utils.JSONUtils;
import com.pyschassist.utils.ServiceVaultHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetCredentials extends Activity {
	final Context context = this;
	private EditText vaultIdEditText;	
	private EditText userIdEditText;
	private EditText docIdEditText;
	
	private Button connectButton;
	private String vaultId;
	private String userId;
	private String docId;
	private ArrayList<Patient> mPatients;
	private String TAG = "Set Credentials";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_credentials);
		
		initializeFields();
		
		clickSave();
	}
	
	private void clickSave(){
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
        		vaultId = vaultIdEditText.getText().toString(); 
        		userId = userIdEditText.getText().toString();
        		docId = docIdEditText.getText().toString();
        		try {
       			    JSONObject obj = isValidCredentials();               
					if(obj.getString("status").equals("success")){						
							setCredentials(vaultId,userId,docId);
					} else {
						showDialog(obj.getString("message"));
					}
				} catch (JSONException e) {
					showDialog(e.getMessage());
				} catch (Exception ex) { 
					showDialog(ex.getMessage()); 
				}
            }
        });		
	}
	
	private void initializeFields(){
		boolean isSaveDisabled = false;
		
		vaultIdEditText = (EditText) findViewById(R.id.editTextVaultId);		
		userIdEditText = (EditText) findViewById(R.id.editTextUserId);	
		docIdEditText = (EditText) findViewById(R.id.editTextDocId2);
		
		connectButton = (Button) findViewById(R.id.buttonSave);		
		
		String userId = AccessUtils.getUserId(getApplicationContext());
		String vaultId = AccessUtils.getVaultId(getApplicationContext());
		String docId = AccessUtils.getDocId(getApplicationContext());
		
		if(!userId.equals("-1")){
			userIdEditText.setText(userId);
			userIdEditText.setEnabled(false);
			isSaveDisabled = true;
		}
		
		if(!vaultId.equals("-1")){
			vaultIdEditText.setText(vaultId);
			vaultIdEditText.setEnabled(false);
			isSaveDisabled = true;
		}
		
		if(!docId.equals("-1")){
			docIdEditText.setText(docId);
			docIdEditText.setEnabled(false);
			isSaveDisabled = true;
		}
		
		if(isSaveDisabled){
			connectButton.setEnabled(false);
		}
		
		
	}
	
	private void showDialog(String err){
		
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.error_dialog);
		dialog.setTitle("Set Credentials Error");
		TextView text = (TextView) dialog.findViewById(R.id.textViewError);
		text.setText(err);
		
		Button dialogButton = (Button) dialog.findViewById(R.id.buttonOk);
		dialogButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	private JSONObject isValidCredentials() throws JSONException{
		String continueFlag = "success";
		StringBuffer errString = new StringBuffer();
		
        if(vaultId.length() == 0 ){
        	errString.append("Vault Id is required.\n");
        	continueFlag = "error";
        } else if( vaultId.length() < 36){
        	continueFlag = "error";
        	errString.append("Vault Id should contain 32 characters.\n");
        }
        
        if(userId.length() == 0 ){
        	errString.append("User Id is required.\n");
        	continueFlag = "error";
        } else if(userId.length() < 36){
        	errString.append("User Id should contain 32 characters.\n");
        	continueFlag = "error";        	
        }
        
        JSONObject obj = new JSONObject();
        obj.put("status", continueFlag);
        obj.put("message", errString);
        
		return obj;
		
	}
	
	private void setCredentials(String vaultId,String userId, String docId) throws Exception { 
		setPreferences(getString(R.string.var_vault_id),vaultId);
		setPreferences(getString(R.string.var_user_id),userId); 
		if(docId.trim().length() > 0){
			if(docId.trim().length() == 36){
				setPreferences(getString(R.string.var_doc_id),docId);
				new getPatientListTask().execute();
			} else {
				rollBackPreferences();
				throw new Exception("Document ID should be 36 characters long");
			}
		} else {
			new createRecordTask().execute(JSONUtils.createJSONInitialPatientList());			
		}
		
	
	}
	
	private class createRecordTask extends AsyncTask<String, Void, String> {  
		
		private final ProgressDialog dialog = new ProgressDialog(getBaseContext());
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Verifying credentials...");
			dialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) { 
			String data = (String) params[0];
			String result = "";
			try {
				result = new ServiceVaultHandler(getBaseContext(),"").createRecord(data);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.i("isCredSet", "Error");
				showDialog( e.getMessage());
			}
			return result;
		} 
		
        @Override
        protected void onPostExecute(String response) {
    		if(response.trim().length() > 0){
    			try{
	    			JSONObject resp = new JSONObject(response); 
	    			String docId = "";
	    			if(resp.getString("result").equals("error")){
	    				JSONObject err = new JSONObject(resp.getString("error"));
	    				throw new Exception(err.getString("message"));
	    			}
	    			
    				docId = resp.getString("document_id");    				
    				setPreferences(getString(R.string.var_doc_id),docId);
    				dialog.dismiss();
    				
    				//go back to main page
    				finish();

    				
    			} catch(Exception e){
    				showDialog( e.getMessage());
    				rollBackPreferences();
    			}
    		} else {
    			rollBackPreferences();
    		}
        }		
	}		

	private void setPreferences(String label, String key){
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
		prefs.edit().putString(label, key).commit();
	}
	
	private void rollBackPreferences(){
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
		prefs.edit().putString(getString(R.string.var_vault_id),"-1").commit();
		prefs.edit().putString(getString(R.string.var_user_id),"-1").commit();
		prefs.edit().putString(getString(R.string.var_doc_id),"-1").commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.set_credentials, menu);
		return true;
	}

	private class getPatientListTask extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(SetCredentials.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Downloading contacts...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				result = new ServiceVaultHandler(SetCredentials.this,"").getRecord();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.i(TAG, "Error");
				rollBackPreferences();
				dialog.dismiss();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String response) {
			Log.d(TAG, response);
			if (response.trim().length() > 0) {
				try {
					JSONObject resp = new JSONObject(response);
					
					if (resp.getString("result").equals("error")) {
						JSONObject err = new JSONObject(resp.getString("error"));
						throw new Exception(err.getString("message"));
					}

				} catch (JSONException je) {
					PatientList.setJsonPatients(response);
					mPatients = setPatients(response);
					PatientList.setPatients(mPatients);
					dialog.dismiss();
					Intent patientList = new Intent(getApplicationContext(),
							PatientListActivity.class);
					startActivity(patientList);
					// finish();
				} catch (Exception e) {
					showDialog(e.getMessage()
							+ " Please verify and retype credentials");
					rollBackPreferences();
					dialog.dismiss();
				}
			} else {
				  showDialog("Please contact app maker if problem persists");
				  rollBackPreferences();
				  dialog.dismiss();
			}

		}
	}

	private ArrayList<Patient> setPatients(String response) {
		ArrayList<Patient> patients = new ArrayList<Patient>();
		try {

			JSONObject patientList = new JSONObject(response);
			JSONArray jsonPatients = new JSONArray(patientList.getString("Patients"));
			for (int i = 0; i < jsonPatients.length(); i++) {
				JSONObject patObj = jsonPatients.getJSONObject(i);
				Patient patient = new Patient();
				patient.setName(patObj.getString("name"));
				patient.setSurname(patObj.getString("surname"));
				try{
					patient.setDocId(patObj.getString("docId"));
				} catch(Exception e){}
				patients.add(patient);

			}

			Log.d(TAG, "Size of Patients:=" + patients.size());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return patients;
	}	
	
}
