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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCampusRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederTeacherService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;


    public Mono<ServerResponse> seedTeacher() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();


        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("employeeCode", "tuf-emp-001");
        formData.add("campusUUID", "64cde770-30a7-4656-8a12-2773b67dccab");
        formData.add("deptRankUUID", "39521748-278c-42d5-a9de-113713e5bdae");
//                    formData.add("reportingTo", "");
        formData.add("status", "true");

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("employeeCode", "tuf-emp-002");
        formData1.add("campusUUID", "64cde770-30a7-4656-8a12-2773b67dccab");
        formData1.add("deptRankUUID", "39521748-278c-42d5-a9de-113713e5bdae");
//                    formData1.add("reportingTo", "");
        formData1.add("status", "true");

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("employeeCode", "tuf-emp-003");
        formData2.add("campusUUID", "64cde770-30a7-4656-8a12-2773b67dccab");
        formData2.add("deptRankUUID", "39521748-278c-42d5-a9de-113713e5bdae");
//                    formData2.add("reportingTo", "");
        formData2.add("status", "true");


        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/teachers/store", valueMap);
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
