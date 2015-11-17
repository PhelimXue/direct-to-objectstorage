package com.foxconn.idsbg.cloud.directupload.model;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class S3SignKey {
	static{
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	public static byte[] HmacSHA256(String data, byte[] key) throws Exception  {
	     String algorithm="HmacSHA256";
	     Mac mac = Mac.getInstance(algorithm);
	     mac.init(new SecretKeySpec(key, algorithm));
	     return mac.doFinal(data.getBytes("UTF8"));
	}

	public static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception  {
	     byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
	     byte[] kDate    = HmacSHA256(dateStamp, kSecret);
	     byte[] kRegion  = HmacSHA256(regionName, kDate);
	     byte[] kService = HmacSHA256(serviceName, kRegion);
	     byte[] kSigning = HmacSHA256("aws4_request", kService);
	     return kSigning;
	}
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String getPolicy(String bucketName, String key, String credential, String xDate, String acl, String successRedirect){
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
		              "{\"acl\": \"" + acl + "\"}," +
		              "{\"success_action_redirect\": \"" + successRedirect + "\"}," +
		              //"[\"starts-with\", \"$Content-Type\", \"\"]," +
		              //"{\"x-amz-meta-uuid\": \"14365123651274\"}," +
		              "[\"content-length-range\", 0, " + maxSize + "]," +
		              "{\"x-amz-credential\": \"" + credential + "\"}," +
		              "{\"x-amz-algorithm\": \"AWS4-HMAC-SHA256\"}," + 
		              "{\"x-amz-date\": \"" + xDate + "\"}" + 
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
