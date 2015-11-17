package com.foxconn.idsbg.cloud.directupload.model;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class OSSSignKey {
	static{
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	private static byte[] HmacSHA1(String data, byte[] key) throws Exception  {
	     String algorithm="HmacSHA1";
	     Mac mac = Mac.getInstance(algorithm);
	     mac.init(new SecretKeySpec(key, algorithm));
	     return mac.doFinal(data.getBytes("UTF8"));
	}
	
	public static String getSignature(String data, byte[] key) throws Exception{
		byte[] bytesSign = HmacSHA1(data, key);
		return Base64.encodeBase64String(bytesSign);
	}
	
	public static String getPolicy(String bucketName, String key){
		// gen expire time
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date expiration = new Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * 60; // 限制預簽名 1 Hour.
		expiration.setTime(msec);
		String expirationString = sdFormat.format(expiration);
		long maxSize = 1024 * 1024 * 10; // 限制檔案大小 10MiB
		
		String policy_document =
		          "{\"expiration\": \"" + expirationString + "\"," +
		            "\"conditions\": [" +
		              "{\"bucket\": \"" + bucketName + "\"}," +
		              "[\"starts-with\", \"$key\", \"" + key + "\"]," +
		              "[\"content-length-range\", 0, " + maxSize + "]" +
		            "]" +
		          "}";
		
		System.out.println("policy_document: " + policy_document);
		
		try {
			String policy = new String(Base64.encodeBase64(policy_document.getBytes("UTF-8")), "ASCII");
			return policy;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
