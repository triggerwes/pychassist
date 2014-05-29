package com.psychassist.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.psychassist.R;
import com.psychassist.model.Patient;
import com.psychassist.model.PatientList;
import com.pyschassist.utils.ServiceVaultHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PatientListFragment extends ListFragment {
	private ArrayList<Patient> mPatients;
	private static final String TAG = "PatientListFragment";
	private ArrayAdapter<Patient> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.patient_list);
		
		mPatients = PatientList.getPatients();
		if (mPatients == null) {
			new getPatientList().execute();
		} else {
			adapter = new ArrayAdapter<Patient>(getActivity(),
					android.R.layout.simple_list_item_1, mPatients);
			setListAdapter(adapter);
		}
	}

	private class getPatientList extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

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
				result = new ServiceVaultHandler(getActivity(),"").getRecord();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				Log.i(TAG, "Error");
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
					Log.d(TAG, "No result string found. This is ok.");
					PatientList.setJsonPatients(response);
					mPatients = setPatients(response);
					PatientList.setPatients(mPatients);
					dialog.dismiss();
//					adapter = new ArrayAdapter<Patient>(getActivity(),
//							android.R.layout.simple_list_item_1, mPatients);
//					setListAdapter(adapter);
				} catch (Exception e) {
					Log.d(TAG, "Error happened:=" + response + e.getMessage());
					dialog.dismiss();
					// showDialog( e.getMessage() +
					// " Please contact app maker if problem persists");
				}
			} else {
				dialog.dismiss();
				// showDialog("Please contact app maker if problem persists");
			}
			if(mPatients == null){
				adapter = new ArrayAdapter<Patient>(getActivity(),
						android.R.layout.simple_list_item_1, new ArrayList<Patient>());
				
			} else {
				adapter = new ArrayAdapter<Patient>(getActivity(),
						android.R.layout.simple_list_item_1, mPatients);

			}
			setListAdapter(adapter);
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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Patient p = (Patient) (getListAdapter()).getItem(position); 
		Intent i = new Intent(getActivity(), PatientActivity.class);
		i.putExtra("Document", p.getDocId());
		i.putExtra("name", p.getName());
		i.putExtra("surname",p.getSurname());
		i.putExtra("Mode", "View");

		startActivity(i);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.main, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Icon clicked");
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent i = new Intent(getActivity(), PatientActivity.class);
			i.putExtra("Mode", "Add");
			startActivity(i);
			Log.d(TAG, "Inside add icon clicked");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		Log.d(TAG, "Resume called");
		super.onResume();
		mPatients = PatientList.getPatients();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		} else if (mPatients == null) {
			Log.d(TAG, "MPatients is null");
		} else {
			Log.d(TAG, "MPatients size:=" + mPatients.size());
			adapter = new ArrayAdapter<Patient>(getActivity(),
					android.R.layout.simple_list_item_1, mPatients);
			setListAdapter(adapter);
		}
	}
}
