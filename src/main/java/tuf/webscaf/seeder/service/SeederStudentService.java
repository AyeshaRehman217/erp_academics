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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveFacultyRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederStudentService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveCampusRepository slaveCampusRepository;

    public Mono<ServerResponse> seedStudent() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveCampusRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull("Saleem campus")
                .flatMap(slaveCampusEntity -> {

                    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                    formData.add("studentId", "tuf-stu-001");
                    formData.add("status", "true");
                    formData.add("officialEmail", "stu1@official.com");
                    formData.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));

                    MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
                    formData1.add("studentId", "tuf-stu-002");
                    formData1.add("status", "true");
                    formData1.add("officialEmail", "stu2@official.com");
                    formData1.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));

                    MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
                    formData2.add("studentId", "tuf-stu-003");
                    formData2.add("status", "true");
                    formData2.add("officialEmail", "stu3@official.com");
                    formData2.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));


                    formDataList.add(formData);
                    formDataList.add(formData1);
                    formDataList.add(formData2);


                    Flux<Boolean> fluxRes = Flux.just(false);

                    for (MultiValueMap<String, String> valueMap : formDataList) {
                        Mono<Boolean> res = seederService
                                .seedData(academicBaseURI + "api/v1/students/store", valueMap);
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
    }
}
