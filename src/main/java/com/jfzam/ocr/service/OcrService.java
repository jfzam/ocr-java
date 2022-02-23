package com.jfzam.ocr.service;

import com.jfzam.ocr.common.OCRUtil;
import com.jfzam.ocr.dto.OcrResult;
import com.jfzam.ocr.dto.OcrValidationResult;

import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

@Service
public class OcrService {
	
	@Autowired
	private Tesseract tesseract;

	public List<OcrResult> ocr(MultipartFile file) throws IOException, TesseractException {

		// set new directory
		String newDir = file.getOriginalFilename().replace('.', '_').replace(' ', '_');

		String newFileDir = "./src/main/resources/static/img/preprocessing/" + newDir;
		new File(newFileDir).mkdirs();

		File dir = new File(newFileDir);

		String rawName = "raw.jpg";
		String greyscaleName = "greyscale.jpg";
		String denoiseName = "denoise.jpg";
		String simpleThresName = "simple_threshold.jpg";
		String adaptThresName = "adaptive_threshold.jpg";

		String rawFileName = newFileDir + "/" + rawName;
		String greyscaleFileName = newFileDir + "/" + greyscaleName;
		String denoiseFileName = newFileDir + "/" + denoiseName;
		String simplThresFileName = newFileDir + "/" + simpleThresName;
		String adaptThresFileName = newFileDir + "/" + adaptThresName;

		// convert multipart to mat
		Mat raw = multipartFile2Mat(file);

		// save raw image
		Imgcodecs.imwrite(rawFileName, raw);

		// image preprocessing
		Mat greyscale = greyScaling(raw, greyscaleFileName);
		Mat denoised = denoising(greyscale, denoiseFileName);
		Mat simplThres = simpleThreshold(denoised, simplThresFileName);
		Mat adaptThres = adaptiveThreshold(denoised, adaptThresFileName);

		String rawResult = tesseract.doOCR(mat2BufferedImage(raw)); // .replaceAll("[^a-zA-Z ]","");
		String denoisedResult = tesseract.doOCR(mat2BufferedImage(denoised)); // .replaceAll("[^a-zA-Z ]","");
		String simplThresResult = tesseract.doOCR(mat2BufferedImage(simplThres)); // .replaceAll("[^a-zA-Z ]","");
		String adaptThresResult = tesseract.doOCR(mat2BufferedImage(adaptThres)); // .replaceAll("[^a-zA-Z ]","");

		// list of results
		List<OcrResult> ocrResult = new ArrayList<OcrResult>();

		ocrResult.add(new OcrResult(rawName, rawResult, "img/preprocessing/" + newDir + "/" + rawName));
		ocrResult.add(new OcrResult(denoiseName, denoisedResult, "img/preprocessing/" + newDir + "/" + denoiseName));
		ocrResult.add(new OcrResult(simpleThresName, simplThresResult,
				"img/preprocessing/" + newDir + "/" + simpleThresName));
		ocrResult.add(
				new OcrResult(adaptThresName, adaptThresResult, "img/preprocessing/" + newDir + "/" + adaptThresName));

		return ocrResult;
	}

	public BufferedImage mat2BufferedImage(Mat mat) throws IOException {
		// Encoding the image
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, matOfByte);
		// Storing the encoded Mat in a byte array
		byte[] byteArray = matOfByte.toArray();
		// Preparing the Buffered Image
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage bufImage = ImageIO.read(in);
		return bufImage;
	}

	public Mat multipartFile2Mat(MultipartFile file) throws IOException {

		// convert to File
		File convFile = new File("./src/main/resources/static/img/" + file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();

		BufferedImage image = ImageIO.read(convFile);
		BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		imageCopy.getGraphics().drawImage(image, 0, 0, null);

		byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();
		Mat img = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		img.put(0, 0, data);

		if (convFile.delete()) {
			System.out.println("Deleted the file: " + convFile.getName());
		} else {
			System.out.println("Failed to delete the file.");
		}

		return img;
	}

	public Mat greyScaling(Mat mat, String imagePath) {
		Mat matRes = new Mat();
		Imgproc.cvtColor(mat, matRes, Imgproc.COLOR_RGB2GRAY);
		Imgcodecs.imwrite(imagePath, matRes);

		return matRes;
	}

	public Mat denoising(Mat mat, String imagePath) {
		Mat matRes = new Mat();
		Photo.fastNlMeansDenoising(mat, matRes, 14, 7, 21);
		Imgcodecs.imwrite(imagePath, matRes);

		return matRes;
	}

	public Mat simpleThreshold(Mat mat, String imagePath) {
		Mat matRes = new Mat();
		Imgproc.threshold(mat, matRes, 64, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite(imagePath, matRes);

		return matRes;
	}

	public Mat adaptiveThreshold(Mat mat, String imagePath) {
		Mat matRes = new Mat();
		Imgproc.adaptiveThreshold(mat, matRes, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 15);
		Imgcodecs.imwrite(imagePath, matRes);

		return matRes;
	}

	public String extractTextFromScannedDocument(PDDocument document) throws IOException, TesseractException {

		// Extract images from file
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		StringBuilder out = new StringBuilder();

		for (int page = 0; page < document.getNumberOfPages(); page++) {
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

			// Create a temp image file
			File temp = File.createTempFile("tempfile_" + page, ".png");
			ImageIO.write(bim, "png", temp);

			String result = tesseract.doOCR(temp);
			out.append(result);

			// Delete temp file
			temp.delete();

		}

		return out.toString();

	}

	public List<Word> extractTextFromImageFile(MultipartFile file, String pageIteratorLevel)
			throws IOException, TesseractException {

		// get page iterator
		int iterator = 0;

		switch (pageIteratorLevel.trim()) {
		case "0":
		case "BLOCK":
			iterator = 0;
			break;

		case "1":
		case "PARA":
			iterator = 1;
			break;

		case "2":
		case "TEXTLINE":
			iterator = 2;
			break;

		case "3":
		case "WORD":
			iterator = 3;
			break;

		case "4":
		case "SYMBOL":
			iterator = 4;
			break;
		}

		// temp directory
		String dir = "./src/main/resources/static/img/preprocessing/";

		// convet multipartfile to mat
		Mat raw = multipartFile2Mat(file);

		// image pre-processing
		Mat greyscale = greyScaling(raw, dir + "greyscale.jpg");
		Mat denoised = denoising(greyscale, dir + "denoise.jpg");
		Mat adaptThres = adaptiveThreshold(denoised, dir + "adaptivethreshold.jpg");

		//return tesseract.getWords(mat2BufferedImage(adaptThres), iterator);
		return tesseract.getWords(mat2BufferedImage(adaptThres), iterator);
	}

	public OcrValidationResult validateCardFromImageFile(MultipartFile file, String firstName, String middleName,
			String lastName, String idType, String idNumber) throws IOException, TesseractException {

		// temp directory
		String dir = "./src/main/resources/static/img/preprocessing/";

		// convert multipartfile to mat
		Mat raw = multipartFile2Mat(file);

		// image pre-processing
		Mat greyscale = greyScaling(raw, dir + "greyscale.jpg");
		Mat denoised = denoising(greyscale, dir + "denoise.jpg");
		Mat simplThres = simpleThreshold(denoised, dir + "simplethreshold.jpg");
		Mat adaptThres = adaptiveThreshold(denoised, dir + "adaptivethreshold.jpg");

		// intialize boolean vars
		boolean fn, mn, ln, idt, idn;
		fn = mn = ln = idt = idn = false;
		
		List<Mat> mats = new ArrayList<Mat>();
		
		mats.add(0, adaptThres);
		mats.add(1, simplThres);
		//mats.add(2, denoised); 
		//mats.add(3, greyscale);

		//validation
		
		for (int i=0; i<mats.size(); i++) {
			
			String ocr = tesseract.doOCR(mat2BufferedImage(mats.get(i))).toUpperCase();
				fn = ocr.contains(firstName.toUpperCase());
			if (!mn)
				mn = ocr.contains(middleName.toUpperCase());
			if (!ln)
				ln = ocr.contains(lastName.toUpperCase());
			if (!idt)
				idt = ocr.contains(idType.toUpperCase());
			if (!idn)
				idn = ocr.contains(idNumber.toUpperCase());

			
			if (fn && mn && ln && idt && idn) {
				break;
			}
		}
		return new OcrValidationResult(OCRUtil.verify(fn), OCRUtil.verify(mn), OCRUtil.verify(ln), OCRUtil.verify(idt), OCRUtil.verify(idn));
	}

}