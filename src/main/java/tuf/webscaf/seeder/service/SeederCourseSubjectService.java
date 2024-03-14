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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSemesterRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederCourseSubjectService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveCourseRepository slaveCourseRepository;

    @Autowired
    private SlaveSubjectRepository slaveSubjectRepository;

    @Autowired
    private SlaveSemesterRepository slaveSemesterRepository;

    public Mono<ServerResponse> seedCourseSubject() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveCourseRepository.findByNameAndDeletedAtIsNull("Programming Fundamentals")
                .flatMap(slaveCourseEntity -> {
                    return slaveSubjectRepository.findByNameAndDeletedAtIsNull("Introduction to Programming")
                            .flatMap(slaveSubjectEntity -> {
                                return slaveSemesterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull("First Semester")
                                        .flatMap(slaveSemesterEntity -> {

                                            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                                            formData.add("totalCreditHours", "4");
                                            formData.add("status", "true");
                                            formData.add("obe", "true");
                                            formData.add("electiveSubject", "false");
                                            formData.add("deficiencySubject", "false");
                                            formData.add("courseUUID", String.valueOf(slaveCourseEntity.getUuid()));
                                            formData.add("subjectUUID", String.valueOf(slaveSubjectEntity.getUuid()));
                                            formData.add("semesterUUID", String.valueOf(slaveSemesterEntity.getUuid()));

                                            formDataList.add(formData);


                                            Flux<Boolean> fluxRes = Flux.just(false);

                                            for (MultiValueMap<String, String> valueMap : formDataList) {
                                                Mono<Boolean> res = seederService
                                                        .seedData(academicBaseURI + "api/v1/course-subjects/store", valueMap);
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