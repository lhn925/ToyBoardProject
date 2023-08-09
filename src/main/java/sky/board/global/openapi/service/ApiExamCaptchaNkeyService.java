package sky.board.global.openapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sky.board.domain.user.model.PathDetails;
import sky.board.global.openapi.entity.OpenApi;
import sky.board.global.openapi.model.code;

/**
 * 네이버 API 로그인 2차 보안키 발급 및 인증 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiExamCaptchaNkeyService {

    private final OpenApi openApi;
    private final ObjectMapper objectMapper;

    // 키 발급시 0,  캡차 이미지 비교시 1로 세팅
    // 네이버 캡차 API 예제 - 키발급
    public Map<String, Object> getApiExamCaptchaNkey() throws JsonProcessingException {
        String responseBody = setOpenApiConfiguration("https://openapi.naver.com/v1/captcha/nkey?code=" + 0);
        return objectMapper.readValue(responseBody, HashMap.class);
    }

    // 네이버 2차 보안키 이미지 발급
    public String getApiExamCaptchaImage(String key) {
        String apiURL = "https://openapi.naver.com/v1/captcha/ncaptcha.bin?key=" + key;
        String responseBody = getImage(apiURL, getRequestHeaders());
        log.info("responseBody = {}", responseBody);
        return responseBody;
    }

    // 키 발급시 0,  캡차 이미지 비교시 1로 세팅
    // 캡차 키 발급시 받은 키값
    // 사용자가 입력한 캡차 이미지 글자값
    // 네이버 캡차 API 예제 - 키발급, 키 비교
    public Map<String, Object> getApiExamCaptchaNkeyResult(String filename, String key, String value)
        throws IOException {
        String result = setOpenApiConfiguration(
            "https://openapi.naver.com/v1/captcha/nkey?code=" + 1 + "&key=" + key + "&value=" + value);

        Map map = objectMapper.readValue(result, HashMap.class);

        // 인증코드 성공시 이미지 삭제
        if (((boolean) map.get("result"))) {
            deleteImage(filename);
        }
        return map;
    }


    private String setOpenApiConfiguration(String apiURL) {
        String responseBody = getCode(apiURL, getRequestHeaders());
        return responseBody;
    }

    private String getCode(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private String getImage(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return getReadImage(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    public void deleteImage(String filename) throws IOException {
        Path filePath = Paths.get(PathDetails.getFilePath(PathDetails.CAPTCHA_IMAGE_URL, filename, "jpg"));
        Files.delete(filePath);
    }

    private String getReadImage(InputStream is) {
        int read;
        byte[] bytes = new byte[1024];
        // 랜덤한 이름으로  파일 생성
        String filename = Long.valueOf(new Date().getTime()).toString();
        File f = new File(PathDetails.getFilePath(PathDetails.CAPTCHA_IMAGE_URL, filename, "jpg"));
        try (OutputStream outputStream = new FileOutputStream(f)) {
            f.createNewFile();
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 캡차 파일 생성에 실패 했습니다.", e);
        }
    }


    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", openApi.getClientId());
        requestHeaders.put("X-Naver-Client-Secret", openApi.getClientSecret());
        return requestHeaders;
    }
}