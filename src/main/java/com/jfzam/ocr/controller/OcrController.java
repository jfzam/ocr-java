package com.jfzam.ocr.controller;

import com.jfzam.ocr.dto.OcrValidationResult;
import com.jfzam.ocr.service.OcrService;

import net.sourceforge.tess4j.Word;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr-api")
public class OcrController {
	@Autowired
	private OcrService ocrService;

	@PostMapping("/validate/card")
	public @ResponseBody ResponseEntity<String> validateCardFromImageFile(@RequestParam("file") MultipartFile file,
			@RequestParam("firstname") String firstName, @RequestParam("middlename") String middleName,
			@RequestParam("lastname") String lastName, @RequestParam("idtype") String idType,
			@RequestParam("idnumber") String idNumber) {
		try {
			// List<Word> words = ocrService.extractTextFromImageFile(file,
			// pageIteratorLevel);

			OcrValidationResult result = ocrService.validateCardFromImageFile(file, firstName, middleName, lastName,
					idType, idNumber);

			JSONObject obj = new JSONObject();
			JSONObject validation = new JSONObject();

			obj.put("fileName", file.getOriginalFilename());
			validation.put("firstName", result.getFirstName());
			validation.put("middleName", result.getMiddleName());
			validation.put("lastName", result.getLastName());
			validation.put("idType", result.getIdType());
			validation.put("idNumber", result.getIdNumber());
			obj.put("validation", validation);

			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/extract/image")
	public @ResponseBody ResponseEntity<String> extractTextFromImageFile(@RequestParam("file") MultipartFile file,
			@RequestParam("pageiteratorlevel") String pageIteratorLevel) {
		try {
			List<Word> words = ocrService.extractTextFromImageFile(file, pageIteratorLevel);

			JSONObject obj = new JSONObject();

			obj.put("fileName", file.getOriginalFilename());
			obj.put("text", words);

			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/extract/pdf")
	public @ResponseBody ResponseEntity<String> extractTextFromPDFFile(@RequestParam("file") MultipartFile file) {
		try {
			// Load file into PDFBox class
			PDDocument document = Loader.loadPDF(file.getBytes()); // PDDocument.load(file.getBytes()); old version

			PDFTextStripper stripper = new PDFTextStripper();
			String strippedText = stripper.getText(document);

			// Check text exists into the file
			if (strippedText.trim().isEmpty()) {
				strippedText = ocrService.extractTextFromScannedDocument(document);
			}

			JSONObject obj = new JSONObject();
			obj.put("fileName", file.getOriginalFilename());
			obj.put("text", strippedText.toString());

			return new ResponseEntity<String>(obj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}