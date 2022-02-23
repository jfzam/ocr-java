package com.jfzam.ocr.dto;

public class OcrResult {
	private String name;
	private String result;
	private String imagePath;

	public OcrResult(String name, String result, String imagePath) {
		setName(name);
		setResult(result);
		setImagePath(imagePath);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
