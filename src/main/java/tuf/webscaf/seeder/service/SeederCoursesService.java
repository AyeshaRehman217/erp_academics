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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseLevelRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveDepartmentRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederCoursesService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveDepartmentRepository slaveDepartmentRepository;

    @Autowired
    private SlaveCourseLevelRepository slaveCourseLevelRepository;


    public Mono<ServerResponse> seedCourse() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveDepartmentRepository.findByNameAndDeletedAtIsNull("Computer Science")
                .flatMap(slaveDepartmentEntity -> {
                    return slaveCourseLevelRepository.findByNameAndDeletedAtIsNull("Bachelors degree")
                            .flatMap(slaveCourseLevelEntity -> {

                                System.out.println("====================="+slaveCourseLevelEntity.getUuid());
                                System.out.println("====================="+slaveDepartmentEntity.getUuid());

                                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                                formData.add("name", "Programming Fundamentals");
                                formData.add("description", "Course of Computer Science");
                                formData.add("status", "true");
                                formData.add("code", "PF");
                                formData.add("shortName", "PF");
                                formData.add("slug", "programming-fundamentals");
                                formData.add("maximumAgeLimit", "30");
                                formData.add("minimumAgeLimit", "20");
                                formData.add("eligibilityCriteria", "xyz");
                                formData.add("duration", "04 year");
                                formData.add("isSemester", "true");
                                formData.add("noOfSemester", "08");
//                                formData.add("noOfAnnuals", "04");
                                formData.add("isAnnual", "false");
                                formData.add("courseLevelUUID", String.valueOf(slaveCourseLevelEntity.getUuid()));
                                formData.add("departmentUUID", String.valueOf(slaveDepartmentEntity.getUuid()));

                                MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
                                formData1.add("name", "Computer Science");
                                formData1.add("description", "Department of Computer Science");
                                formData1.add("status", "true");
                                formData1.add("code", "CS");
                                formData1.add("shortName", "CS");
                                formData1.add("slug", "computer-science");
                                formData1.add("maximumAgeLimit", "30");
                                formData1.add("minimumAgeLimit", "20");
                                formData1.add("eligibilityCriteria", "xyz");
                                formData1.add("duration", "04 year");
                                formData1.add("isSemester", "true");
                                formData1.add("noOfSemester", "08");
//                                formData1.add("noOfAnnuals", "04");
                                formData1.add("isAnnual", "false");
                                formData1.add("courseLevelUUID", String.valueOf(slaveCourseLevelEntity.getUuid()));
                                formData1.add("departmentUUID", String.valueOf(slaveDepartmentEntity.getUuid()));

                                MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
                                formData2.add("name", "Object Oriented Programming");
                                formData2.add("description", "Course of Computer Science");
                                formData2.add("status", "true");
                                formData2.add("code", "OOP");
                                formData2.add("shortName", "OOP");
                                formData2.add("slug", "object-oriented-programming");
                                formData2.add("maximumAgeLimit", "30");
                                formData2.add("minimumAgeLimit", "20");
                                formData2.add("eligibilityCriteria", "xyz");
                                formData2.add("duration", "04 year");
                                formData2.add("isSemester", "true");
                                formData2.add("noOfSemester", "08");
//                                formData2.add("noOfAnnuals", "04");
                                formData2.add("isAnnual", "false");
                                formData2.add("courseLevelUUID", String.valueOf(slaveCourseLevelEntity.getUuid()));
                                formData2.add("departmentUUID", String.valueOf(slaveDepartmentEntity.getUuid()));


                                formDataList.add(formData);
                                formDataList.add(formData1);
                                formDataList.add(formData2);


                                Flux<Boolean> fluxRes = Flux.just(false);

                                for (MultiValueMap<String, String> valueMap : formDataList) {
                                    Mono<Boolean> res = seederService
                                            .seedData(academicBaseURI + "api/v1/courses/store", valueMap);
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
    }
}