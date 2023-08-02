package lovely.jia.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j @Getter @Setter
@Service
@ConfigurationProperties(prefix = "jia.hanyang.login")
public class LoginService {

    @Autowired
    private WebClient webClient;

    private String cryptKey;

    private String loginType;


    private static final String LOGIN_URL = "/core/module/membership/loginCheck.php";

    /**
     * 로그인 후 세션아이디 값을 리턴한다.
     * @param id
     * @param password
     * @return
     */
    public String login(String id, String password){

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("ANYSECUREENCODE_cryptKey", cryptKey);
        formData.add("ANYSECUREENCODE_loginType", loginType);
        formData.add("ANYSECUREENCODE_id", convertLoginParameter(id));
        formData.add("ANYSECUREENCODE_password", convertLoginParameter(password));
        formData.add("ANYSECURE_ENCODETYPE", "2");

        ClientResponse block = webClient.mutate()
                .build()
                .post()
                .uri(LOGIN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                .block();
        log.info("Login 결과 :: {}", block.statusCode());
        Mono<String> body = block.bodyToMono(String.class);
        if(!isSuccess(body.block())){
            return null;
        }
        MultiValueMap<String, ResponseCookie> cookies = block
                .cookies();

        List<ResponseCookie> phpsessid = cookies.get("PHPSESSID");
        ResponseCookie responseCookie = phpsessid.get(0);
        log.info("Session Cookie [{}]", responseCookie.getValue());
        return responseCookie.getValue();
    }
    private boolean isSuccess(String html){
        return html.contains("parent.document.location.reload();");
    }

    /**
     * 해당 사이트 아이디 패스워드 Encode 방법 가져옴
     * @param value
     * @return
     */
    public String convertLoginParameter(String value){
        String data = Base64Utils.encodeToString(value.getBytes());
        data = Base64Utils.encodeToString(data.getBytes());
        data = Base64Utils.encodeToString(data.getBytes());
        data = Base64Utils.encodeToString(data.getBytes());

        String str1 = data.substring(0, 1);
        String str2 = data.substring(1, 2+1);
        String str3 = data.substring(3, 3+3);
        String str4 = data.substring(6, 4+6);
        String str5 = data.substring(10);
        String str6 = str4 + str2 + str3 + str1 + str5;
        String rtnStr = Base64Utils.encodeToString(str6.getBytes());
        return rtnStr;
    }

}
