package com.koordinator.epsilon.Koordinator.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    public static final Contact DEFAULT_CONTACT = new Contact("Tihomir Stoychev Stoychev", "www.linkedin.com/in/tihomir-stoychev-stoychev", "tihomir_alcudia3@hotmail.com");
    public static final ApiInfo DEFAULT_DEMBOW = new ApiInfo("Koordinator", "Microservice which manages the prices of any Cryptocurrency", "1.0", "https://www.termsofservicegenerator.net/live.php?token=0BgpgQO9jW1OqNx2JpWR7zVev0Kklq7v",DEFAULT_CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList());
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES=new HashSet<String>(Arrays.asList("application/json","application/xml"));


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(SwaggerConfig.DEFAULT_DEMBOW)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES);
    }
}