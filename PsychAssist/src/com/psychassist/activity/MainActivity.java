package com.psychassist.activity;


import com.example.psychassist.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String TAG = "MainActivity";
	private Button setCredButton;
	private Button viewContactsButton;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!isNetworkEnabled()){
			Toast.makeText(getApplicationContext(), "Please enable network connection to proceed.", Toast.LENGTH_LONG).show();
		}
		
		setCredButton  = (Button) findViewById(R.id.buttonSetCred);
		viewContactsButton = (Button) findViewById(R.id.buttonViewContacts);
		
		
		if(!isCredSet()){
			viewContactsButton.setEnabled(false);
		} else {
			viewContactsButton.setEnabled(true);
		}
		
		
        setCredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
                Intent setCred=new Intent(getApplicationContext(),SetCredentials.class);
                startActivity(setCred);
            }
        });				
		
        viewContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { 
                Intent viewPatients=new Intent(getApplicationContext(),PatientListActivity.class);
                startActivity(viewPatients);
            }
        }); 
        
	}	

	
	private boolean isCredSet(){
		 
		SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
		String userId = prefs.getString(getString(R.string.var_user_id), "-1");
		String vaultId = prefs.getString(getString(R.string.var_vault_id), "-1");
		Log.i(TAG, userId);
		Log.i(TAG, vaultId);
		if(userId.equals("-1") || vaultId.equals("-1")){
			return false;
		} else {
			return true;
		}
		
	}
	
	private boolean isNetworkEnabled(){
		  ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		  if (networkInfo != null && networkInfo.isConnected()) {
		  		return true;
		  } else {
                return false;
		  }
	}
	


}
