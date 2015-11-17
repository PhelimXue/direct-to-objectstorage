<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AWS S3</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script>
$(document).ready(function(){
	var key = '';
	$("#fileUpload").on('change',function() {
		filename = $("#fileUpload").val().split('\\').pop();
		key = $("#key").val();
		$("#key").val(key + filename);
    });
});
</script>
</head>
<body>
<h1>AWS S3 Direct Upload Example</h1>
  <form action="http://${bucket}.s3-${region}.amazonaws.com/" method="post" enctype="multipart/form-data">
    Key to upload: 
    <input type="text" name="key" id="key" value="${key}" readonly /><br />
    <input type="hidden" name="acl" value="${acl}" />
    <input type="hidden" name="success_action_redirect" value="${successRedirect}" />
    <input type="hidden" name="X-Amz-Credential" value="${credential}" />
    <input type="hidden" name="X-Amz-Algorithm" value="AWS4-HMAC-SHA256" />
    <input type="hidden" name="X-Amz-Signature" value="${signature}" />
    <input type="hidden" name="X-Amz-Date" value="${xDate}" />
    <input type="hidden" name="Policy" value="${policy}" />
    File: <input type="file" name="file" id="fileUpload" /> <br />
    <!-- The elements after this will be ignored -->
    <input type="submit" name="submit" value="Upload to Amazon S3" />
  </form>
</body>
</html>