package com.iexec.sms.iexecsms.cas;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;

@FeignClient(name = "CasClient",
        url = "#{casConfigurationService.casUrl}", configuration = CasClientFeignConfiguration.class)
public interface CasClient {

    @PostMapping(value = "/session")
    ResponseEntity generateSecureSessionWithPalaemonFile(@RequestBody byte[] data) throws FeignException;
}
