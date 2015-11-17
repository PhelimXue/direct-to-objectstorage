package com.foxconn.idsbg.cloud.directupload.controller;

import java.util.TimeZone;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.foxconn.idsbg.cloud.directupload.model.OSSSignKey;

@Controller
@RequestMapping("oss")
public class DirectOSSController {
	static{
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public ModelAndView pageUpload(@RequestParam(required = true) String bucket,
									@RequestParam(required = true) String region,
									@RequestParam(required = true) String key) throws Exception{
		ModelAndView model = new ModelAndView();
		model.setViewName("ossupload");
		
		String ACCESS_KEY_ID = "";
		String ACCESS_KEY_SECRET = "";
		
		/**
		 * 將 StringToSign 作簽名
		 */
		String stringToSign = OSSSignKey.getPolicy(bucket, key);
		String signature = OSSSignKey.getSignature(stringToSign, ACCESS_KEY_SECRET.getBytes());
		System.out.println("signature: " + signature);
		
		String successRedirect = "http://example.com/successful_upload.html";
		
		model.addObject("bucket", bucket);
		model.addObject("key", key);
		model.addObject("region", region);
		model.addObject("OSSAccessKeyId", ACCESS_KEY_ID);
		model.addObject("signature", signature);
		model.addObject("policy", stringToSign);
		model.addObject("successRedirect", successRedirect);
		return model;
	}
}
