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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveFacultyRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederDepartmentService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveFacultyRepository slaveFacultyRepository;

    public Mono<ServerResponse> seedDepartment() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveFacultyRepository.findByNameAndDeletedAtIsNull("Computer Science")
                .flatMap(slaveFacultyEntity -> {
                    return slaveFacultyRepository.findByNameAndDeletedAtIsNull("Business Administration")
                            .flatMap(slaveFacultyEntity1 -> {
                               return slaveFacultyRepository.findByNameAndDeletedAtIsNull("Electrical Engineering")
                                       .flatMap(slaveFacultyEntity2 -> {

                                           MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                                           formData.add("name", "Computer Science");
                                           formData.add("description", "Department of Computer Science");
                                           formData.add("status", "true");
                                           formData.add("code", "CS");
                                           formData.add("shortName", "CS");
                                           formData.add("location", "Saleem Campus");
                                           formData.add("slug", "computer-science");
                                           formData.add("facultyUUID", String.valueOf(slaveFacultyEntity.getUuid()));

                                           MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
                                           formData1.add("name", "Business Administration");
                                           formData1.add("description", "Department of Business Administration");
                                           formData1.add("status", "true");
                                           formData1.add("code", "BA");
                                           formData1.add("shortName", "BA");
                                           formData1.add("location", "Saleem Campus");
                                           formData1.add("slug", "business-administration");
                                           formData1.add("facultyUUID", String.valueOf(slaveFacultyEntity1.getUuid()));

                                           MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
                                           formData2.add("name", "Electrical Engineering");
                                           formData2.add("description", "Department of Electrical Engineering");
                                           formData2.add("status", "true");
                                           formData2.add("code", "EE");
                                           formData2.add("shortName", "EE");
                                           formData2.add("location", "Saleem Campus");
                                           formData2.add("slug", "electrical-engineering");
                                           formData2.add("facultyUUID", String.valueOf(slaveFacultyEntity2.getUuid()));


                                           formDataList.add(formData);
                                           formDataList.add(formData1);
                                           formDataList.add(formData2);


                                           Flux<Boolean> fluxRes = Flux.just(false);

                                           for (MultiValueMap<String, String> valueMap : formDataList) {
                                               Mono<Boolean> res = seederService
                                                       .seedData(academicBaseURI + "api/v1/departments/store", valueMap);
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

                                       });
                            });
                });
    }
}