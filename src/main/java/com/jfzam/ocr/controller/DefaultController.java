package com.jfzam.ocr.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jfzam.ocr.dto.OcrResult;
import com.jfzam.ocr.service.OcrService;

import net.sourceforge.tess4j.TesseractException;

import com.jfzam.ocr.common.OCRUtil;

@Controller
@RequestMapping("/ocr")
public class DefaultController {

	@Autowired
	private OcrService ocrService;

	@GetMapping("/image")   
	   public String image(Model model) throws IOException {
		
		return "image";
	}
	
	@GetMapping("/pdf")   
	   public String pdf(Model model) throws IOException {
		
		return "pdf";
	}
	
	@GetMapping("/transunion")   
	   public String transunion(Model model) throws IOException {
		
		return "transunion";
	}
	
	@GetMapping("/validation")   
	   public String validation(Model model) throws IOException {
		
		return "validation";
	}

	@GetMapping("")
	public String home(Model model) throws IOException {

		return "image";
	}

	

}
