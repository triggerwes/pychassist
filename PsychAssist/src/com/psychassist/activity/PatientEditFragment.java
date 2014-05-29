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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.psychassist.R;

 
import com.psychassist.model.Patient;
import com.psychassist.model.PatientList;
import com.psychassist.model.Patient.Address;
import com.psychassist.model.Patient.MedicalInformation;
import com.psychassist.model.Patient.PhoneNumber;
import com.pyschassist.utils.JSONUtils;
import com.pyschassist.utils.ServiceVaultHandler;

public class PatientEditFragment extends DialogFragment {

	public static final String PATIENT = "patient";
	public static final String POS = "Pos";
	
	public String TAG = "PatientEditFragment";
	private Patient patient; 
	
	private EditText fname;
	private EditText lname;
	private EditText email;
	private EditText addressLine;
	private EditText homeNumber;
	private EditText ofcNumber;
	private EditText cellNumber;
	private EditText city;
	private EditText state;
	private EditText zip;
	private EditText notes;
	private EditText diagnosis;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		patient = (Patient) getArguments().getSerializable(PATIENT); 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_add_patient, parent, false);
		initializeFields(v);
		setFields(patient);
		addPhoneWatcher();
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.add_contacts, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			updateRecord();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	private void updateRecord(){
		String origFname = patient.getName();
		String origLname = patient.getSurname();
		retrieveFieldsAsPatient();
		
		if(! fname.getText().toString().equals(origFname) ||
		   ! lname.getText().toString().equals(origLname)){
			 updateMasterList();
		} else {
			 new updatePatientRecord().execute(retrieveFields());
		} 	
	}
	
	public void updateMasterList() {
		try {
			PatientList.replace(patient);
			ArrayList<Patient> mPatients = PatientList.getPatients();
			String jsonList = JSONUtils.createJSONPatientList(mPatients);
			new updatePatientList().execute(jsonList);
		} catch (JSONException e) {
			Log.d(TAG, "Error in updateMasterList");
		} catch (Exception e) {
			Log.d(TAG, "Error in updateMasterList");
		}
		


	}
	
	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;

		Intent i = new Intent();
		i.putExtra(PATIENT, patient);

		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}

	private void initializeFields(View v) {
		fname = (EditText) v.findViewById(R.id.editTextFirstName);
		lname = (EditText) v.findViewById(R.id.editTextLastName);
		email = (EditText) v.findViewById(R.id.editTextEmail);
		addressLine = (EditText) v.findViewById(R.id.editTextAddress);
		homeNumber = (EditText) v.findViewById(R.id.editTextHomeNumber);
		ofcNumber = (EditText) v.findViewById(R.id.editTextWorkNumber);
		cellNumber = (EditText) v.findViewById(R.id.editTextMobileNumber);
		city = (EditText) v.findViewById(R.id.editTextCity);
		state = (EditText) v.findViewById(R.id.editTextState);
		zip = (EditText) v.findViewById(R.id.editTextZip);
		notes = (EditText) v.findViewById(R.id.editTextNotes);
		diagnosis = (EditText) v.findViewById(R.id.editTextDiagnosis);
	}

	private void addPhoneWatcher() {
		cellNumber
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		ofcNumber
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

		homeNumber
				.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
	}

	public static PatientEditFragment newInstance(Patient patient) {
		Bundle args = new Bundle();
		args.putSerializable(PATIENT, patient);
 

		PatientEditFragment fragment = new PatientEditFragment();
		fragment.setArguments(args);
		return fragment;
	}

	public void setFields(Patient data) {
		try {
			fname.setText(data.getName());
			lname.setText(data.getSurname());
			email.setText(data.getEmail());
			addressLine.setText(data.getAddress().getAddress());

			city.setText(data.getAddress().getCity());
			state.setText(data.getAddress().getState());
			zip.setText(data.getAddress().getZipCode());

			List<PhoneNumber> phones = data.getPhoneList();
			for (PhoneNumber phone : phones) {
				if (phone.getType().equals("HOM")) {
					homeNumber.setText(phone.getNumber());
				}

				if (phone.getType().equals("OFC")) {
					ofcNumber.setText(phone.getNumber());
				}

				if (phone.getType().equals("CEL")) {
					cellNumber.setText(phone.getNumber());
				}
			}

			List<MedicalInformation> medInfos = data.getMedicalList();
			for (MedicalInformation medInfo : medInfos) {
				diagnosis.setText(medInfo.getDiagnosis());
				notes.setText(medInfo.getDiagnosis());
			}
		} catch (Exception e) {
			// TODO: add error handling here
		}
	}

	public void retrieveFieldsAsPatient(){
		
		patient.setName(fname.getText().toString());
		patient.setSurname(lname.getText().toString());
		patient.setEmail(email.getText().toString());

		patient.setAddress(createAddress(patient));

		List<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();
		phoneList.add(createPhone(patient, "OFC", ofcNumber.getText()
				.toString()));
		phoneList.add(createPhone(patient, "CEL", cellNumber.getText()
				.toString()));
		phoneList.add(createPhone(patient, "HOM", homeNumber.getText()
				.toString()));
		patient.setPhoneList(phoneList);

		List<MedicalInformation> medList = new ArrayList<MedicalInformation>();
		medList.add(createMedInfo(patient, notes.getText().toString(),
				diagnosis.getText().toString()));
		patient.setMedicalList(medList);

	}
	
	public String retrieveFields() {        
		 
		String data = "";
		try {
			data = JSONUtils.createJSONPatient(patient);
		} catch (JSONException e) {
			Log.d(TAG, "Error in retrieve fields");
		}

		return data;
	}

	private MedicalInformation createMedInfo(Patient patient, String notes,
			String diagnosis) {
		MedicalInformation med = patient.new MedicalInformation();
		med.setDiagnosis(diagnosis);
		med.setNotes(notes);
		return med;
	}

	private PhoneNumber createPhone(Patient patient, String type, String value) {
		PhoneNumber phn = patient.new PhoneNumber();
		phn.setType(type);
		phn.setNumber(value);

		return phn;
	}

	private Address createAddress(Patient patient) {
		Address add = patient.new Address();
		add.setAddress(addressLine.getText().toString());
		add.setCity(city.getText().toString());
		add.setState(state.getText().toString());
		add.setZipCode(zip.getText().toString());

		return add;
	}

	private class updatePatientRecord extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Updating patient record...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String data = (String) params[0];
			String result = "";
			try {
				result = new ServiceVaultHandler(getActivity()
						.getApplicationContext(), patient.getDocId())
						.updateRecord(data);
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

					sendResult(Activity.RESULT_OK);
					FragmentManager fm = getFragmentManager();
					if (fm.getBackStackEntryCount() > 0) {
						Log.i("MainActivity", "popping backstack");
						fm.popBackStack();
					} else {
						Log.i("MainActivity",
								"nothing on backstack, calling super");
						getActivity().finish();
					}

				} catch (JSONException je) {
					Log.d(TAG, "No result string found. This is ok.");
				} catch (Exception e) {
					Log.d(TAG, "Error happened:=" + response + e.getMessage());
				}
			} else {
				Log.d(TAG, "No response");
			}
		}
	}

	private class updatePatientList extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Updating patient list...");
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
					dialog.dismiss();
					new updatePatientRecord().execute(retrieveFields());
				} catch (JSONException je) {  
					dialog.dismiss(); 
				} catch (Exception e) {
					Log.d(TAG, "Error happened:=" + response + e.getMessage());
					dialog.dismiss();
				}
			} else {
				dialog.dismiss();
			}
		}
	}
}
