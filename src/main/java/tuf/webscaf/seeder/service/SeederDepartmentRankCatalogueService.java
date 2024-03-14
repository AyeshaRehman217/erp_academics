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
public class SeederDepartmentRankCatalogueService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    public Mono<ServerResponse> seedDepartmentRankCatalogue() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("name", "Rector");
        formData.add("description", "The highest-ranked administrative and educational leader for an academic institution");

        formDataList.add(formData);

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("status", "true");
        formData1.add("name", "Vice Rector");
        formData1.add("description", "Appointed by the rector to carry out a sub-set of the rector’s tasks");

        formDataList.add(formData1);

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("status", "true");
        formData2.add("name", "Dean");
        formData2.add("description", "Dean is a title employed in academic administrations such as colleges or universities for a person with significant authority over a specific academic unit, over a specific area of concern, or both");

        formDataList.add(formData2);

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData3.add("status", "true");
        formData3.add("name", "Vice Dean");
        formData3.add("description", "Head of studies in a faculty");

        formDataList.add(formData3);

        MultiValueMap<String, String> formData4 = new LinkedMultiValueMap<>();
        formData4.add("status", "true");
        formData4.add("name", "Head of department");
        formData4.add("description", "An academic leader with academic, programmatic, managerial, and fiscal responsibilities for a designated department");

        formDataList.add(formData4);



        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/department-rank-catalogues/store", valueMap);
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
