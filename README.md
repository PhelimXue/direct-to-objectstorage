# Direct Upload to Object Storage

### 概述
在自己建立服務平台時，總會有需求是希望使用者來訪問我們的網站，但在作上傳 Object 時又希望可以讓使用者不用透過我們自家的平台，直接傳檔到 Object Storage，如下圖擷取自 AWS，本範例提供 Direct Upload to AWS-S3 and Aliyun-OSS。

![上傳 Object 流程圖](https://github.com/PhelimXue/direct-to-objectstorage/raw/master/DirectUpload.png)

### KeyPoint
- 組合出準備 Sign 的 Policy
- 對 Policy 作 Sign 的動作
- 將運算出來的 Signature 交給前端作 POST 的驗證用

### Require
* Java 1.7+
* Maven 3.0.5+

### Modify
src/main/java/com/foxconn/idsbg/cloud/directupload/controller/DirectS3Controller.java  
src/main/java/com/foxconn/idsbg/cloud/directupload/controller/DirectOSSController.java
- ACCESS_KEY_ID
- ACCESS_KEY_SECRET

### Quick Start
本範例採用 Maven + Tomcat7 模擬運行，請運行底下指令  
```sh
mvn tomcat7:run
```
接著打開 browser 訪問  
http://localhost:8080/DirectUpload/s3/upload?bucket={bucketName}&region={regionName}&key={key}  
or  
http://localhost:8080/DirectUpload/oss/upload?bucket={bucketName}&region={regionName}&key={key}  
其中在作 Direct Upload 時必須至少已經知道 bucket、region 與 key(path)

### 故障排除
遇上 Tomcat Address already in use 時可修改 pom.xml 中的 port 參數

### Reference
- S3 : http://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-UsingHTTPPOST.html
- OSS: http://bbs.aliyun.com/read/262307.html?spm=5176.730001.3.54.inGZkh
