package com.jfzam.ocr.dto;

public class OcrValidationResult {

	private int firstName;
	private int middleName;
	private int lastName;
	private int idType;
	private int idNumber;
	
	public OcrValidationResult(int firstName, int middleName, int lastName, int idType, int idNumber) {
		super();
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.idType = idType;
		this.idNumber = idNumber;
	}

	public int getFirstName() {
		return firstName;
	}

	public void setFirstName(int firstName) {
		this.firstName = firstName;
	}

	public int getMiddleName() {
		return middleName;
	}

	public void setMiddleName(int middleName) {
		this.middleName = middleName;
	}

	public int getLastName() {
		return lastName;
	}

	public void setLastName(int lastName) {
		this.lastName = lastName;
	}

	public int getIdType() {
		return idType;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}

	public int getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}
	
}
