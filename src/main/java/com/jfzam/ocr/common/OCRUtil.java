package com.jfzam.ocr.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;

public class OCRUtil {

	public OCRUtil() {

	}
	
	public static int verify(boolean b) {
		return b ? 1: 0;
	}

	public static String convertFileIntoBase64String(File file) throws IOException {
		
		FileInputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byteArrayOutputStream = new ByteArrayOutputStream();
			int b;
			byte[] buffer = new byte[1024];
			while ((b = fileInputStream.read(buffer)) != -1) {
				byteArrayOutputStream.write(buffer, 0, b);
			}

			byte[] fileBytes = byteArrayOutputStream.toByteArray();
			fileInputStream.close();
			byteArrayOutputStream.close();

			byte[] encoded = Base64.encodeBase64(fileBytes);
			String encodedString = new String(encoded);
			return encodedString;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (byteArrayOutputStream != null) {
				byteArrayOutputStream.close();
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		return new String();
	}
}
