package com.psychassist.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.psychassist.R;
import com.psychassist.model.Patient;
import com.psychassist.model.Patient.MedicalInformation;
import com.psychassist.model.Patient.PhoneNumber;
import com.psychassist.model.PatientList;
import com.pyschassist.utils.JSONUtils;
import com.pyschassist.utils.ServiceVaultHandler;

public class PatientFragment extends Fragment {
	private TextView textViewAddress;
	private TextView textViewCity;
	private TextView textViewState;
	private TextView textViewZip;
	private TextView textViewHome;
	private TextView textViewWork;
	private TextView textViewMobile;
	private TextView textViewEmail;
	private TextView textViewDiagnosis;
	private TextView textViewNotes;

	private String docId = "";
	private Patient patient;
	
	private static final String DOCUMENT_ID = "Document";
	private static final String NAME = "name";
	private static final String SURNAME = "surname";
	private static final String PATIENT = "patient";	
	private static final String POS = "Pos";
	
	private String TAG = "View Patients";
	private static final int REQUEST_CODE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Log.d(TAG,"Inside onCreate");
		if((Patient) getArguments().getSerializable(PATIENT) != null){
			Log.d(TAG,"Inside new patient");
			patient = (Patient) getArguments().getSerializable(PATIENT);
			setFields(patient);
		} else {
			docId = (String) getArguments().getString(DOCUMENT_ID);
			String name = (String) getArguments().getString(NAME);
			String surname = (String) getArguments().getString(SURNAME);
			getActivity().setTitle(name + " " + surname);
			
			
			if (docId != null) {				
				new getPatientRecord().execute(docId);
			} else {
				patient.setName(name);
				patient.setSurname(surname);				
			}

		}



	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		Log.d(TAG,"Inside onCreateView");
		View v = inflater.inflate(R.layout.fragment_patient, parent, false);
		initializeFields(v);
		if(patient != null){
			Log.d(TAG,"Inside call to setFields");
			Log.d(TAG,"Patient Name:=" + patient.getName());
			setFields(patient);
		}
		return v;
	}

	private void initializeFields(View v) {
		textViewAddress = (TextView) v.findViewById(R.id.textViewAddress);
		textViewCity = (TextView) v.findViewById(R.id.textViewCity);
		textViewState = (TextView) v.findViewById(R.id.textViewState);
		textViewZip = (TextView) v.findViewById(R.id.textViewZip);
		textViewHome = (TextView) v.findViewById(R.id.textViewHome);
		textViewWork = (TextView) v.findViewById(R.id.textViewWork);
		textViewMobile = (TextView) v.findViewById(R.id.textViewMobile);
		textViewEmail = (TextView) v.findViewById(R.id.textViewEmail);
		textViewDiagnosis = (TextView) v.findViewById(R.id.textViewDiagnosis);
		textViewNotes = (TextView) v.findViewById(R.id.textViewNotes);
	}

 
	
	public void setFields(Patient patient) {
		try { 
			getActivity().setTitle(patient.getName() + " " + patient.getSurname()); 
			textViewEmail.setText(patient.getEmail());
			textViewAddress.setText(patient.getAddress().getAddress());
			textViewCity.setText(patient.getAddress().getCity());
			textViewState.setText(patient.getAddress().getState());
			textViewZip.setText(patient.getAddress().getZipCode());

			List<PhoneNumber> phones = patient.getPhoneList();
			for (PhoneNumber phone : phones) {
				if (phone.getType().equals("HOM")) {
					textViewHome.setText(phone.getNumber());
				}

				if (phone.getType().equals("OFC")) {
					textViewWork.setText(phone.getNumber());
				}

				if (phone.getType().equals("CEL")) {
					textViewMobile.setText(phone.getNumber());
				}
			}

			List<MedicalInformation> medInfos = patient.getMedicalList();
			for (MedicalInformation medInfo : medInfos) {
				textViewDiagnosis.setText(medInfo.getDiagnosis());
				textViewNotes.setText(medInfo.getDiagnosis());
			}

		} catch (Exception e) {
			// TODO: add error handling here
		}
	}

	public static PatientFragment newInstance(String docId, String name,
			String surname) {
		Bundle args = new Bundle();
		args.putString(DOCUMENT_ID, docId);
		args.putString(NAME, name);
		args.putString(SURNAME, surname);
		

		PatientFragment fragment = new PatientFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	public static PatientFragment newInstance(Patient newData) {
		Bundle args = new Bundle();
		args.putSerializable(PATIENT, newData);
		

		PatientFragment fragment = new PatientFragment();
		fragment.setArguments(args);
		return fragment;
	}	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.patient, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit: 
            FragmentManager fm = getActivity().getSupportFragmentManager();   
            PatientEditFragment updateFragment = PatientEditFragment.newInstance(patient);  
            
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragmentContainer, updateFragment);  
            transaction.addToBackStack(null);
            transaction.commit();
            
            updateFragment.setTargetFragment(PatientFragment.this, REQUEST_CODE);
			return true;
		case R.id.action_delete:
			deletePatientRecord();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return;
	    if (requestCode == REQUEST_CODE) {
	       Log.d(TAG,"Inside ActivityResult");
	       patient = (Patient) data.getSerializableExtra(PATIENT);
	       
	    }
	}

	public void deletePatientRecord() {
		if (docId != null) {
			new removePatientRecord().execute(docId);
		} else {
			updateMasterList();
		}
	}


	
	public void updateMasterList() {

		if (docId != null) {
			Log.d(TAG, "DocId inside updateMasterList:=" + docId);
			PatientList.remove(docId);
			Log.d(TAG, "Called remove");
		} else {
			PatientList
					.removeWithoutId(patient.getName(), patient.getSurname());
		}

		ArrayList<Patient> mPatients = PatientList.getPatients();
		String jsonList = "";
		try {
			jsonList = JSONUtils.createJSONPatientList(mPatients);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Log.d(TAG,"Updated JsonList:=" + jsonList);
		new updatePatientList().execute(jsonList);
	}

	private class updatePatientList extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Updating patient master list...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String data = (String) params[0];
			String result = "";
			try {
				result = new ServiceVaultHandler(getActivity()
						.getApplicationContext(), "").updateRecord(data);
			} catch (Exception e) {
				Log.i(TAG, "Error");
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
					dialog.dismiss();
					getActivity().finish();
				} catch (JSONException je) {
					Log.d(TAG, "No result string found. This is ok.");
				} catch (Exception e) {
					Log.d(TAG, "Error happened:=" + response + e.getMessage());
					// showDialog( e.getMessage()
					// +" Please contact app maker if problem persists");
				}
			} else {
				// showDialog("Please contact app maker if problem persists");
			}
		}
	}

	private class removePatientRecord extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Deleting patient record...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String docId = (String) params[0];
			String result = "";
			try {
				result = new ServiceVaultHandler(getActivity()
						.getApplicationContext(), docId).deleteRecord();
			} catch (Exception e) {
				Log.i(TAG, "Error");
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
				} catch (Exception e) {
					Log.d(TAG, "Error happened:=" + e.getMessage());
				} finally {
					dialog.dismiss();
					updateMasterList();
				}
			} else {
				// showDialog("Please contact app maker if problem persists");
			}
		}

	}

	private class getPatientRecord extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Downloading patient record...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String docId = (String) params[0];			
			String result = "";
			try {
				result = new ServiceVaultHandler(getActivity(), docId)
						.getRecord();
			} catch (Exception e) {
				Log.i(TAG, "Error in doInBackground");
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
					Log.d(TAG, "No error string found. This is ok.");
					Log.d(TAG, "Record retrieve:=" + response);
					try {
						patient = JSONUtils.createPatientObject(response);	
						patient.setDocId(docId);
					} catch (JSONException e) {					
					}
					setFields(patient);
					dialog.dismiss();
				} catch (Exception e) {
					Log.d(TAG,
							"Error happened onPostExecute:=" + response
									+ e.getMessage());
					dialog.dismiss();
					// TODO:add error handling here
				}
			} else {
				// TODO:add error handling here
			}

		}
	}

}
