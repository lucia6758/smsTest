package com.sbs.untactTeacher.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.RequestParam;

public class Sms {

	public static void sendSms(@RequestParam Map<String, Object> param) {
		try {

			final String encodingType = "utf-8";
			final String boundary = "____boundary____";

			/**************** 문자전송하기 예제 ******************/
			/* "result_code":결과코드,"message":결과문구, */
			/* "msg_id":메세지ID,"error_cnt":에러갯수,"success_cnt":성공갯수 */
			
			/******************** 인증정보 *********************/
			String sms_url = "https://apis.aligo.in/send/"; // 전송요청 URL

			Map<String, String> sms = new HashMap<String, String>();

			sms.put("user_id", "lsy6758"); // SMS 아이디
			sms.put("key", "8iz7qqxsg7coa154zf52u3ghxzgglc7b"); // 인증키

			/******************** 인증정보 ********************/

			/******************** 전송정보 ********************/
			sms.put("msg", String.format("%s님 가입을 환영합니다. TEST용", param.get("name"))); // 메세지 내용
			sms.put("receiver", (String) param.get("cellphoneNo")); // 수신번호
			sms.put("sender", "01033339180"); // 발신번호
			sms.put("testmode_yn", "Y"); // Y 인경우 실제문자 전송가지않음

			String image = "";
			// image = "/tmp/pic_57f358af08cf7_sms_.jpg"; // MMS 이미지 파일 위치

			/******************** 전송정보 ********************/

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();

			builder.setBoundary(boundary);
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.setCharset(Charset.forName(encodingType));

			for (Iterator<String> i = sms.keySet().iterator(); i.hasNext();) {
				String key = i.next();
				builder.addTextBody(key, sms.get(key), ContentType.create("Multipart/related", encodingType));
			}

			File imageFile = new File(image);
			if (image != null && image.length() > 0 && imageFile.exists()) {

				builder.addPart("image", new FileBody(imageFile, ContentType.create("application/octet-stream"),
						URLEncoder.encode(imageFile.getName(), encodingType)));
			}

			HttpEntity entity = builder.build();

			HttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(sms_url);
			post.setEntity(entity);

			HttpResponse res = client.execute(post);

			String result = "";
			if (res != null) {
				BufferedReader in = new BufferedReader(
						new InputStreamReader(res.getEntity().getContent(), encodingType));
				String buffer = null;
				while ((buffer = in.readLine()) != null) {
					result += buffer;
				}
				in.close();
			}

			System.out.println(result); // 메세지 전송 성공여부(콘솔에 출력)
			System.out.println(sms); // 메세지내용이나 수신자 잘 입력됐는지 확인용(콘솔에 출력)

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}

}
