package com.jfzam.ocr;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.sourceforge.tess4j.Tesseract;
import nu.pattern.OpenCV;

@SpringBootApplication
public class OcrApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcrApplication.class, args);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Bean
	Tesseract getTesseract() {
		Tesseract tesseract = new Tesseract();
		tesseract.setDatapath("./tessdata_best");
		tesseract.setTessVariable("user_defined_dpi", "300");
		tesseract.setTessVariable("tessedit_char_whitelist","abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 ");
		//tesseract.setHocr(true);
		return tesseract;
	}

}
