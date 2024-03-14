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
public class SeederEnrollmentService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;


    public Mono<ServerResponse> seedStudent() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("extra", "false");
        formData.add("isOpenLMS", "true");
        formData.add("studentUUID", "3a832d83-f376-4fa4-947f-c11930d027e3");
        formData.add("academicSessionUUID", "e2db0d26-7888-4846-9980-9166693b95cf");
        formData.add("campusUUID", "64cde770-30a7-4656-8a12-2773b67dccab");
        formData.add("courseUUID", "cdd7c256-f607-4ccc-9fea-0d493224b591");
        formData.add("subjectOfferedUUID", "7375aa17-2bec-4572-bec6-8e14f1a9cd8e");
        formData.add("semesterUUID", "bdcec9c3-a9db-40e6-acb6-f23f8e4e6c5a");

        formDataList.add(formData);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/enrollments/store", valueMap);
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
