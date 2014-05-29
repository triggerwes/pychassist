package com.psychassist.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import com.psychassist.model.Patient.Address;
import com.psychassist.model.Patient.MedicalInformation;
import com.psychassist.model.Patient.PhoneNumber;
import com.psychassist.model.PatientList;
import com.pyschassist.utils.JSONUtils;
import com.pyschassist.utils.ServiceVaultHandler;

public class PatientAddFragment extends Fragment {

	private String TAG = "Add Patients";
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
	private Patient patient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_add_patient, parent, false);
		
		initializeFields(v);

		addPhoneWatcher();

		return v;
	}
	
	private void initializeFields(View v){
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.add_contacts, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			addPatientRecord();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Patient createPatient() {
		Patient p = new Patient();
		p.setName(fname.getText().toString());
		p.setSurname(lname.getText().toString());
		p.setEmail(email.getText().toString());

		p.setAddress(createAddress(p));

		List<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();
		phoneList.add(createPhone(p, "OFC", ofcNumber.getText().toString()));
		phoneList.add(createPhone(p, "CEL", cellNumber.getText().toString()));
		phoneList.add(createPhone(p, "HOM", homeNumber.getText().toString()));
		p.setPhoneList(phoneList);

		List<MedicalInformation> medList = new ArrayList<MedicalInformation>();
		medList.add(createMedInfo(p, notes.getText().toString(), diagnosis
				.getText().toString()));
		p.setMedicalList(medList);

		return p;
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

	public void addPatientRecord() {
		patient = createPatient();
		try {
			new addPatientRecord().execute(JSONUtils.createJSONPatient(patient));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void updateMasterList(Patient patient) {
		ArrayList<Patient> mPatients = PatientList.getPatients();
		String jsonList = "";
		try {
			jsonList = JSONUtils.createJSONPatientList(mPatients);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new updatePatientList().execute(jsonList);
		PatientList.add(patient);
	}

	private class addPatientRecord extends AsyncTask<String, Void, String> {

		private final ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Creating patient record...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String data = (String) params[0];
			String result = "";
			try {
				result = new ServiceVaultHandler(getActivity().getApplicationContext(), "").createRecord(data);
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
					patient.setDocId(resp.getString("document_id"));
					updateMasterList(patient);
				} catch (JSONException je) {
					Log.d(TAG, "No result string found. This is ok.");
					dialog.dismiss();
				} catch (Exception e) {
					Log.d(TAG, "Error happened:=" + e.getMessage());
					dialog.dismiss();
					// showDialog( e.getMessage() +
					// " Please contact app maker if problem persists");
				}
			} else {
				dialog.dismiss();
				// showDialog("Please contact app maker if problem persists");
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
				} catch (JSONException je) { 
					Log.d(TAG, "No result string found. This is ok.");
					dialog.dismiss();
					getActivity().finish();
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
		}
	}

}
