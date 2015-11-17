package com.foxconn.idsbg.cloud.directupload.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.foxconn.idsbg.cloud.directupload.model.S3SignKey;

@Controller
@RequestMapping("/s3")
public class DirectS3Controller {
	static{
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView pageUpload(@RequestParam(required = true) String bucket,
									@RequestParam(required = true) String region,
									@RequestParam(required = true) String key) throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("s3upload");
		
		String ACCESS_KEY_ID = "";
		String ACCESS_KEY_SECRET = "";
		Date current = new Date();
		
		/**
		 * 建立簽名需要使用的 credential
		 * 日期格式為 yyyyMMdd > 20151101
		 */
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
		String dateStamp = sdFormat.format(current);
		String credential = ACCESS_KEY_ID + "/" + dateStamp + "/" + region + "/s3/aws4_request";
		
		/**
		 * ACL 為設定上傳成功後的基本 Permission
		 */
		String acl = "public-read";
		
		/**
		 * 組合 StringToSign 的 Base64 值，詳細可參考
		 * http://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-HTTPPOSTConstructPolicy.html
		 */
		SimpleDateFormat sdFormat2 = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		String xDate = sdFormat2.format(current);
		String successRedirect = "http://example.com/successful_upload.html";
		String stringToSign = S3SignKey.getPolicy(bucket, key, credential, xDate, acl, successRedirect);
		
		/**
		 * 將 StringToSign 作簽名
		 */
		byte[] keyBytes = S3SignKey.getSignatureKey(ACCESS_KEY_SECRET, dateStamp, region, "s3");
		byte[] signatureBytes = S3SignKey.HmacSHA256(stringToSign, keyBytes);
		String signature = S3SignKey.bytesToHex(signatureBytes);
		System.out.println("signature: " + signature);
		
		
		model.addObject("bucket", bucket);
		model.addObject("key", key);
		model.addObject("region", region);
		model.addObject("credential", credential);
		model.addObject("acl", acl);
		model.addObject("xDate", xDate);
		model.addObject("signature", signature);
		model.addObject("successRedirect", successRedirect);
		model.addObject("policy", stringToSign);
		return model;
	}
}
