package com.jeffrey.example.demospringjwtvalidator.controller;

import com.nimbusds.jose.util.JSONObjectUtils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

@RestController
public class DemoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @SuppressWarnings("unused")
    @Value("${com.jeffrey.example.jwtValidation.remote-jwk-set-uri:#{null}}")
    private String remotePublicKeyUri;

    /**
     * curl -i http://localhost:8080/test1 -X GET -H 'Content-Type: application/json' -H "Authorization: Bearer eyJraWQiOiIxIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhY2Nlc3NfdG9rZW4iOiJTS3F0NHlHUlQ0d0FCeENSQkJtd2tKQXRneDlEIiwiYXVkaWVuY2UiOiJtaWNyb2dhdGV3YXkiLCJhcGlfcHJvZHVjdF9saXN0IjpbImF0by1kZW1vLWFwaS1kZXYtaW50LXByb2R1Y3QiXSwiYXBwbGljYXRpb25fbmFtZSI6ImF0by1kZW1vLWFwaS1kZXYtaW50LWFwcCIsIm5iZiI6MTU4MDUzMzcyMywiaXNzIjoiaHR0cHM6XC9cL21hbnVsaWZlLWRldmVsb3BtZW50LWRldi5hcGlnZWUubmV0XC92MVwvbWdcL29hdXRoMlwvdG9rZW4iLCJzY29wZXMiOlsiUkVBRCIsIkRFTEVURSIsIldSSVRFIl0sImV4cCI6MTU4MDUzNDA4MywiaWF0IjoxNTgwNTMzNzgzLCJjbGllbnRfaWQiOiIwRzhRVzdFTHBCVmhxYzlabnF3WUZ3WHJhSzVTV2FqYSIsImp0aSI6ImQ4OThiZjQ4LWE4MGEtNGNmNC1hYTQ1LWNlYmYyMzIyZGRmOCJ9.YLj5KYS3fXlitMKUOvEsjtbObtKXwIlakdBQkVqwpdN4_P-1P7KzTITkU7A2UnuTJYm5Y26ImekgEuPuYuYYBzLYz8Ea6As8kwNckXZxCadbULoaOUri8r6wvctjxvtxkKtKI0ZBpxC1KmUvwjKcgulo0vQrZyhaYB_x4a8uHAiHZgtu1TvZvEZqjfP-7mZRnWBhNGi0cpHw7k6vpe7lx2aB_CF9tMp-tgUE6G4m2kmHHiznFXGIqJ9sx-wnTBVFlYFE-QigTtgvKngSCZ8SY1pHpXKapjivq0oNCldiBKeUJecA4UuCYb_g4NKQcHVznnCiWIX9T-be0M0nSfx5ng"
     */
    @GetMapping(path="/test1")
    public @ResponseBody String test1() {
        return "test1";
    }

    @GetMapping(path="/jwk")
    public ResponseEntity<String> jwk() {
//        return "{\"keys\":[{\"kty\":\"RSA\",\"n\":\"1nUR3oWTTjoFW_T2RvQVQTSM6MYXxiMDdGrgiD-hnDo24qFoP3Zx4deuscZ4-5Cx2k5RjLUAxyLx50YKSLmoQmt9uGCYEMEu2GYUP32Jvql-Zu5Dnq-wMjVaWH2mQq1j0cBADkUbz6Wh-WmUnWIpuGz5HBvK5YgAWSZzfQMvjpp_GDhaR2uWgtRyt3j9ZEKNtsUqd6TY3vwWLZn_MpE4tNR7VE5tz4zzebzyGr5KX5Qvh6T4DMN_jRTdfQSJKFGP41P1yco0KDEX4E6CzmR2pENyMwP9FlvwaVKTyOUnrRlWmxklbqVYO-kfrTUpLxziVQKR8O4e5a-8rvWgRkgnUQ\",\"e\":\"AQAB\",\"kid\":\"1\",\"alg\":\"RS256\",\"use\":\"sig\"}]}";
        try {
            return ResponseEntity.ok(fetchRemoteJwk());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Autowired
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    public String fetchRemoteJwk() throws Exception {
        String jsonString = restTemplate.getForObject(URI.create(remotePublicKeyUri), String.class);

        // workaround: remove any null object from the keyset
        JSONObject jsonObject = JSONObjectUtils.parse(jsonString);
        JSONArray jsonArray = JSONObjectUtils.getJSONArray(jsonObject, "keys");
        jsonArray.removeIf(Objects::isNull);
        jsonObject.replace("keys", jsonArray);

        String outputJsonString = jsonObject.toJSONString();
        LOGGER.debug("output json string: {}", outputJsonString);
        return outputJsonString;
    }

}
