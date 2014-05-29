package com.pyschassist.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.psychassist.model.Patient;
import com.psychassist.model.Patient.Address;
import com.psychassist.model.Patient.MedicalInformation;
import com.psychassist.model.Patient.PhoneNumber;

public class JSONUtils {


	 public static String createJSONInitialPatientList() throws JSONException{
		JSONArray jsonPatients = new JSONArray(); 
		JSONObject patientList = new JSONObject();
		patientList.put("Patients", jsonPatients);	
		
		return patientList.toString();
	 }
	 
	public ArrayList<Patient> createPatientListArray(String response) {
		ArrayList<Patient> patients = new ArrayList<Patient>();
		try {

			JSONObject patientList = new JSONObject(response);
			JSONArray jsonPatients = new JSONArray(patientList.getString("Patients"));
			
			for (int i = 0; i < jsonPatients.length(); i++) {
				JSONObject patObj = jsonPatients.getJSONObject(i);
				
				Patient patient = new Patient();
				patient.setName(patObj.getString("name"));
				patient.setSurname(patObj.getString("surname"));
				
				try {
					patient.setDocId(patObj.getString("docId"));
				} catch (Exception e) {}
				
				patients.add(patient);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return patients;
	}
	
	public static String createJSONPatientList(ArrayList<Patient> mPatients) throws JSONException{
		JSONArray jsonPatients = new JSONArray(); 
		JSONObject jsonList = new JSONObject();
		
		
        for(Patient patient:mPatients){
    		JSONObject patientJson = new JSONObject();
    		patientJson.put("name", patient.getName());
    		patientJson.put("surname", patient.getSurname());
    		patientJson.put("docId", patient.getDocId());
    		jsonPatients.put(patientJson);        	
        }

		
		jsonList.put("Patients", jsonPatients);
		
		return jsonList.toString();
	}

	public static Patient createPatientObject(String data) throws JSONException {
		JSONObject jObj = new JSONObject(data);

		Patient patient = new Patient();
		patient.setName(jObj.getString("name"));
		patient.setSurname(jObj.getString("surname")); 
		patient.setEmail(jObj.getString("email"));

		Address add = createAddressObject(patient, jObj.getJSONObject("address"));
		List<MedicalInformation> medList = createMedicalInfoObject(patient,
				jObj.getJSONArray("medInformation"));
		List<PhoneNumber> phoneList = createPhoneObject(patient,
				jObj.getJSONArray("phoneNumber"));

		patient.setAddress(add);
		patient.setMedicalList(medList);
		patient.setPhoneList(phoneList);

		return patient;
	}

	public static String createJSONPatient(Patient patient) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		
		if(patient.getDocId() != null){
			jsonObj.put("docId", patient.getDocId());			
		}

		jsonObj.put("name", patient.getName());
		jsonObj.put("surname", patient.getSurname());
		jsonObj.put("email", patient.getEmail());
		
        if(patient.getAddress() != null){
    		JSONObject jsonAdd = createJSONAddress(patient);
    		jsonObj.put("address", jsonAdd);        	
        }

        if(patient.getPhoneList() != null){
    		JSONArray jsonPhoneArr = createJSONPhone(patient);
    		jsonObj.put("phoneNumber", jsonPhoneArr);        	
        }

        if(patient.getMedicalList() != null){
    		JSONArray jsonMedicalArr = createJSONMedicalInfo(patient);
    		jsonObj.put("medInformation", jsonMedicalArr);        	
        }
        Log.i("JSON",jsonObj.toString());
		return jsonObj.toString();
	}

	private static Address createAddressObject(Patient patient,JSONObject address) throws JSONException {
		Address add = patient.new Address();
		add.setAddress(address.getString("address"));
		add.setCity(address.getString("city"));
		add.setState(address.getString("state")); 
		add.setZipCode(address.getString("zip"));
        
		return add;
	}

	private static List<MedicalInformation> createMedicalInfoObject(
			Patient patient, JSONArray jMedArr) throws JSONException {
		
		List<MedicalInformation> medList = new ArrayList<MedicalInformation>();
		
		for (int i = 0; i < jMedArr.length(); i++) {
			JSONObject medObj = jMedArr.getJSONObject(i);
			MedicalInformation med = patient.new MedicalInformation();
			med.setDiagnosis(medObj.getString("diagnosis"));
			med.setNotes(medObj.getString("notes"));
			medList.add(med);
		}

		return medList;
	}

	private static List<PhoneNumber> createPhoneObject(Patient patient,
			JSONArray jPhnArr) throws JSONException {
		
		List<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();
		
		for (int i = 0; i < jPhnArr.length(); i++) {
			JSONObject phnObj = jPhnArr.getJSONObject(i);
			PhoneNumber phn = patient.new PhoneNumber();
			phn.setType(phnObj.getString("type"));
			phn.setNumber(phnObj.getString("num"));
			phoneList.add(phn);
		}
		
		return phoneList;

	}

	private static JSONObject createJSONAddress(Patient patient) throws JSONException {
		JSONObject jsonAdd = new JSONObject();		 
		
		jsonAdd.put("address", patient.getAddress().getAddress());
		jsonAdd.put("city", patient.getAddress().getCity());
		jsonAdd.put("state", patient.getAddress().getState());		
		jsonAdd.put("zip", patient.getAddress().getZipCode());
		 

		return jsonAdd;
	}

	private static JSONArray createJSONPhone(Patient patient) throws JSONException {
		JSONArray jsonPhoneArr = new JSONArray();
		 
		for (PhoneNumber pn : patient.getPhoneList()) {
			JSONObject pnObj = new JSONObject();
			pnObj.put("num", pn.getNumber());
			pnObj.put("type", pn.getType());
			jsonPhoneArr.put(pnObj);
		}

		return jsonPhoneArr;
	}

	private static JSONArray createJSONMedicalInfo(Patient patient)
			throws JSONException {
		
		JSONArray jsonMedicalArr = new JSONArray();
		
		for (MedicalInformation medInfo : patient.getMedicalList()) {
			JSONObject medObj = new JSONObject();
			medObj.put("diagnosis", medInfo.getDiagnosis());
			medObj.put("notes", medInfo.getNotes());
			jsonMedicalArr.put(medObj);
		}

		return jsonMedicalArr;
	}
}
