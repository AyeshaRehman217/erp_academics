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
public class SeederDepartmentRankService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    public Mono<ServerResponse> seedDepartmentRank() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("departmentUUID", "e1326d54-c8ab-45f4-9597-61f57f4fa997");
        formData.add("deptRankCatalogueUUID", "a6ff3719-8cb8-4b85-9059-418776e8be20");
        formData.add("many", "true");
        formData.add("max", "12");
        formData.add("min", "1");

        formDataList.add(formData);

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("status", "true");
        formData1.add("departmentUUID", "a5776ff6-e48e-41c6-bf4a-e3bde7a12685");
        formData1.add("deptRankCatalogueUUID", "a6ff3719-8cb8-4b85-9059-418776e8be20");
        formData1.add("many", "true");
        formData1.add("max", "12");
        formData1.add("min", "1");

        formDataList.add(formData1);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/department-ranks/store", valueMap);
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
