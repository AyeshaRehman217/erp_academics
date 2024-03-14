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
public class SeederCourseTypeService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    public Mono<ServerResponse> seedCourseType(){

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("name", "BS");
        formData.add("description", "Bachelor of Science");
        formData.add("duration", "04 year");
        formData.add("isSemester", "true");
        formData.add("noOfSemester", "08");
        formData.add("noOfAnnuals", "04");
        formData.add("isAnnual", "false");
        formData.add("status", "true");

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("name", "MS");
        formData1.add("description", "Master of Science");
        formData1.add("duration", "02 year");
        formData1.add("isSemester", "true");
        formData1.add("noOfSemester", "04");
        formData1.add("noOfAnnuals", "02");
        formData1.add("isAnnual", "false");
        formData1.add("status", "true");


        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("name", "Phd");
        formData2.add("description", "Doctor of Philosophy");
        formData2.add("duration", "5 year");
        formData2.add("isSemester", "true");
        formData2.add("noOfSemester", "10");
        formData2.add("noOfAnnuals", "05");
        formData2.add("isAnnual", "false");
        formData2.add("status", "true");


        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);

        Flux<Boolean> fluxRes = Flux.just(false);

        for (int i = 0; i < formDataList.size(); i++) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI+"api/v1/course-types/store", formDataList.get(i));
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