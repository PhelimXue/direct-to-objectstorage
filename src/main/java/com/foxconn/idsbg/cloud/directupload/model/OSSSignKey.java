package com.foxconn.idsbg.cloud.directupload.model;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;

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
	
	public static String getPolicy(String bucketName, String key) throws Exception{
		// gen expire time
		//SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date expiration = new Date();
		long msec = expiration.getTime();
		msec += 1000 * 60 * 60; // 限制預簽名 1 Hour.
		expiration.setTime(msec);
		long maxSize = 1024 * 1024 * 10; // 限制檔案大小 10MiB
		
		/* Policy 原有的樣子
		String expirationString = sdFormat.format(expiration);
		String policy_document =
		          "{\"expiration\": \"" + expirationString + "\"," +
		            "\"conditions\": [" +
		              "{\"bucket\": \"" + bucketName + "\"}," +
		              "[\"starts-with\", \"$key\", \"" + key + "\"]," +
		              "[\"content-length-range\", 0, " + maxSize + "]" +
		            "]" +
		          "}";
		
		System.out.println("policy_document: " + policy_document);
		*/
		
		PolicyConditions policyConds = new PolicyConditions();
		policyConds.addConditionItem("bucket", bucketName);
		policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, key);
		policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 1, maxSize);

		// 生成Post Policy字符串
		String postPolicy = generatePostPolicy(expiration, policyConds);
		System.out.println("policy_document: " + postPolicy);
		
		try {
			String policy = new String(Base64.encodeBase64(postPolicy.getBytes("UTF-8")), "ASCII");
			return policy;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 這個是從 aliyun client 的程式抓出來的
	private static String generatePostPolicy(Date expiration, PolicyConditions conds) {
		String formatedExpiration = DateUtil.formatIso8601Date(expiration);
		String jsonizedExpiration = String.format("\"expiration\":\"%s\"", formatedExpiration);
		String jsonizedConds = conds.jsonize();
        
        StringBuilder postPolicy = new StringBuilder();
        postPolicy.append("{");
        postPolicy.append(String.format("%s,%s", jsonizedExpiration, jsonizedConds));
        postPolicy.append("}");

        return postPolicy.toString();
	}
}
