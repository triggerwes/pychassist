package com.psychassist.model;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;

public class PatientList {
    private static ArrayList<Patient> mPatients;
    private static PatientList sPatientList; 
	private static String jsonPatients;
	
	private PatientList(Context appContext,Activity a){
     		mPatients = new ArrayList<Patient>(); 
	} 
	
	public static String getJsonPatients(){
		return jsonPatients;
	}	

	public static void setJsonPatients(String jsonPatients) {
		PatientList.jsonPatients = jsonPatients;
	}
	
	public static ArrayList<Patient> getPatients() {
		return mPatients;
	}	
	
	public static void setPatients(ArrayList<Patient> mPatients) {
		PatientList.mPatients = mPatients;
	}	
	
	public static PatientList get(Context c, Activity a){
    	
    	if(sPatientList == null){
    		sPatientList = new PatientList(c.getApplicationContext(),a);
    	}
    	
    	return sPatientList;
    } 
	
	public Patient getPatient(UUID id){
		for(Patient p : mPatients){
			if(p.getId().equals(id)){
				return p;
			}
		}
		return null;
	}
	
	public static void remove(String docId){
		int pos = -1;
		for(int i = 0; i < mPatients.size();i++){
			Patient p = mPatients.get(i);
			if(p.getDocId() != null && p.getDocId().equals(docId)){
				pos = i;
			}
		}
		
		if(pos > -1){
			mPatients.remove(pos);
			setPatients(mPatients);
		}
	}
	
	public static void removeWithoutId(String name, String surName){
		int pos = -1;
		for(int i = 0; i < mPatients.size();i++){
			Patient p = mPatients.get(i);
			if(p.getName().equals(name) && p.getSurname().equals(surName)){
				pos = i;
			}
		}
		
		if(pos > -1){
			mPatients.remove(pos);
			setPatients(mPatients);
		}
	}
	
	public static void add(Patient p){
		mPatients.add(p);
	}	
		
	public static void replace(Patient patient){
		int pos = -1;
		for(int i = 0; i < mPatients.size();i++){
			Patient p = mPatients.get(i);
			if(p.getDocId() != null && p.getDocId().equals(patient.getDocId())){
				pos = i;
			}
		}
		
		if(pos > -1){
			mPatients.set(pos, patient);
			setPatients(mPatients);
		}
	}	
		 
	
}
