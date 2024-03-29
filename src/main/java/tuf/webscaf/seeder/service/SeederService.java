package tuf.webscaf.seeder.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Service
public class SeederService {


    @Value("${server.ssl-status}")
    private String sslStatus;

    @Value("${webclient.backend.token}")
    private String token;

    public Mono<Boolean> seedData(String url, MultiValueMap<String, String> formData) {

        WebClient webClient = initWebClient();

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_FORM_URLENCODED))
                .header(HttpHeaders.AUTHORIZATION, "Bearer "+token)
                .header("auid", "4647bdc2-0a93-4728-b092-3e04a304593d")
                .header("reqCompanyUUID", "4647bdc2-0a93-4728-b092-3e04a304593d")
                .header("reqBranchUUID", "4647bdc2-0a93-4728-b092-3e04a304593d")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(JsonNode.class).flatMap(value -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = null;
                    try {
                        jsonNode = objectMapper.readTree(value.toString());
                        Integer status = Integer.valueOf(jsonNode.get("status").toString());
                        if (status.equals(200)) {
                            JsonNode objectNode = jsonNode.get("appResponse");
                            JsonNode arrNode = objectNode.get("message");
                            Integer msgCode = Integer.valueOf(arrNode.get(0).get("messageCode").toString());
                            if (msgCode == 99200) {
                                return Mono.just(true);
                            }
                        }
                    }  catch (JsonProcessingException e) {
                        System.out.println("======================================== "+e.getMessage());
                        return Mono.just(false);
                    }
                    System.out.println("------------------------------------ "+value.toString());
                    return Mono.just(false);
                });

    }


    public WebClient initWebClient() {
        try {
            SslContext context = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(t -> t.sslContext(context));

            if (sslStatus.equals("enable")) {
                return WebClient.builder()
                        .clientConnector(
                                new ReactorClientHttpConnector(httpClient)
                        )
                        .build();
            } else {
                return WebClient.builder()
                        .build();
            }
        } catch (SSLException e) {
            return WebClient.builder()
                    .build();
        }
    }

}