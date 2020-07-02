package com.iexec.sms.tee.session;

import com.iexec.sms.config.FeignConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TeeSessionService {

    private TeeSessionClient teeSessionClient;
    private TeeCasConfiguration teeCasConfiguration;
    private FeignConfig feignConfig;
    private TeeSessionHelper teeSessionHelper;

    public TeeSessionService(
            TeeSessionHelper teeSessionHelper,
            TeeSessionClient teeSessionClient,
            TeeCasConfiguration teeCasConfiguration,
            FeignConfig feignConfig) {
        this.teeSessionHelper = teeSessionHelper;
        this.teeSessionClient = teeSessionClient;
        this.teeCasConfiguration = teeCasConfiguration;
        this.feignConfig = feignConfig;
    }

    public String generateTeeSession(String taskId, String workerAddress, String teeChallenge){
        String sessionId = String.format("%s0000%s", RandomStringUtils.randomAlphanumeric(10), taskId);
        String sessionYmlAsString = teeSessionHelper.getPalaemonSessionYmlAsString(sessionId, taskId, workerAddress, teeChallenge);
        if (sessionYmlAsString.isEmpty()) {
            log.error("Failed to get session yml [taskId:{}, workerAddress:{}]", taskId, workerAddress);
            return "";
        }

        log.info("## Palaemon session YML ##"); //dev logs, lets keep them for now
        log.info(sessionYmlAsString);
        log.info("#####################");

        /*
        ResponseEntity<String> response = teeSessionClient.generateSecureSession(sessionYmlAsString.getBytes());
        ResponseEntity<String> response2 = teeSessionClient.generateSecureSession(sessionYmlAsString.replace("0000", "1111").getBytes());

         */

        ResponseEntity<String> response = generateSecureSessionWithRestTemplate(sessionYmlAsString.getBytes());
        log.info("Response of generateSecureSession [taskId:{}, getStatusCode:{}, httpBody:{}]",
                taskId, response.getStatusCode(), response.getBody());
        ResponseEntity<String> response2 = generateSecureSessionWithRestTemplate(sessionYmlAsString.replace("0000", "1111").getBytes());
        log.info("Response of generateSecureSession [taskId:{}, getStatusCode:{}, httpBody:{}]",
                taskId, response2.getStatusCode(), response2.getBody());

        return (response != null && response.getStatusCode().is2xxSuccessful()) ? sessionId : "";
    }

    /*
     * RestTemplate used for generating session
     * */
    public ResponseEntity<String> generateSecureSessionWithRestTemplate(byte[] palaemonFile) {
        HttpHeaders headers = new HttpHeaders();
        //headers.set("Content-Type", "application/x-www-form-urlencoded");
        //headers.set("Expect", "100-continue");
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(palaemonFile, headers);
        ResponseEntity<String> response = null;
        try {
            response = feignConfig.getRestTemplate().exchange(teeCasConfiguration.getCasUrl() + "/session",
                    HttpMethod.POST, httpEntity, String.class);
        } catch (Exception e) {
            log.error("Why?");
        }

        log.info("[responseStatus:{}]", response != null ? response.getStatusCode(): "");
        log.info("[responseBody:{}]", response != null ? response.getBody(): "");

        return response;
    }
}
