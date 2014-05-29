package com.psychassist.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Patient implements Serializable {
 
	private static final long serialVersionUID = 1L;
	private UUID id;
    private String name;
	private String surname;
	private String email;
	private String docId;
	private Address address;
    private List<PhoneNumber> phoneList;
    private List<MedicalInformation> medicalList;

    public Patient(){
    	id = UUID.randomUUID();
    }

	public class Address {
        private String address;
		private String city;
        private String state;
        private String zipCode;
        
        // accessors        
        public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getZipCode() {
			return zipCode;
		}
		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}
    }

    public class PhoneNumber {
        private String type; 
		private String number;
		
	    // accessors		
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		
	    public String getType() {
		   return type;
	    }
		
	    public void setType(String type) {
		   this.type = type;
		}
	}
    
    public class MedicalInformation {
        private String diagnosis; 
		private String notes;
		
	    // accessors
		public String getDiagnosis() {
			return diagnosis;
		}
		public void setDiagnosis(String diagnosis) {
			this.diagnosis = diagnosis;
		}
		public String getNotes() {
			return notes;
		}
		public void setNotes(String notes) {
			this.notes = notes;
		}
	}    
    

    // accessors    
    
	public UUID getId() {
		return id;
	}
	
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<PhoneNumber> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<PhoneNumber> phoneList) {
		this.phoneList = phoneList;
	}    
    
	public List<MedicalInformation> getMedicalList() {
		return medicalList;
	}

	public void setMedicalList(List<MedicalInformation> medicalList) {
		this.medicalList = medicalList;
	}	
	
    public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	
	@Override
	public String toString() {
		return name + " " + surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
