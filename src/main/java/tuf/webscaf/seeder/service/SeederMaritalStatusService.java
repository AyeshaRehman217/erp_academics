package tuf.webscaf.seeder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederMaritalStatusService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;


    public Mono<ServerResponse> seedMaritalStatus(){

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", "Single");
        formData.add("description", "This is a marital status");
        formData.add("status", "true");

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("name", "Married");
        formData1.add("description", "This is a marital status");
        formData1.add("status", "true");

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("name", "Divorced");
        formData2.add("description", "This is a marital status");
        formData2.add("status", "true");

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData2.add("name", "Separated");
        formData2.add("description", "This is a marital status");
        formData2.add("status", "true");

        MultiValueMap<String, String> formData4 = new LinkedMultiValueMap<>();
        formData2.add("name", "Widowed");
        formData2.add("description", "This is a marital status");
        formData2.add("status", "true");

        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);
        formDataList.add(formData3);
        formDataList.add(formData4);

        Flux<Boolean> fluxRes = Flux.just(false);

        for (int i = 0; i < formDataList.size(); i++) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI+"api/v1/marital-statuses/store", formDataList.get(i));
            fluxRes = fluxRes.concatWith(res);
        }
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        "Successful"
                )
        );

        return customResponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                fluxRes.last()
        );
    }
}