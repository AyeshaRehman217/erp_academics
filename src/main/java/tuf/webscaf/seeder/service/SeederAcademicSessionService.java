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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSessionTypeRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederAcademicSessionService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveSessionTypeRepository slaveSessionTypeRepository;

    public Mono<ServerResponse> seedAcademicSession() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveSessionTypeRepository.findByNameAndDeletedAtIsNull("Fall")
                .flatMap(slaveSessionEntity -> {

                    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                    formData.add("name", "Fall-2022");
                    formData.add("description", "This is a Fall-2022 session");
                    formData.add("status", "true");
                    formData.add("year", "01-01-2022 00:00:00");
                    formData.add("startDate", "01-09-2022 09:01:01");
                    formData.add("endDate", "31-01-2023 09:01:01");
                    formData.add("sessionUUID", String.valueOf(slaveSessionEntity.getUuid()));
                    formDataList.add(formData);

                    return slaveSessionTypeRepository.findByNameAndDeletedAtIsNull("Spring")
                            .flatMap(slaveSessionEntity1 -> {

                                MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
                                formData1.add("name", "Spring-2023");
                                formData1.add("description", "This is a Spring-2023 Session");
                                formData1.add("status", "true");
                                formData1.add("year", "01-01-2023 00:00:00");
                                formData1.add("startDate", "01-02-2023 09:01:01");
                                formData1.add("endDate", "30-06-2023 09:01:01");
                                formData1.add("sessionUUID", String.valueOf(slaveSessionEntity1.getUuid()));
                                formDataList.add(formData1);



                                Flux<Boolean> fluxRes = Flux.just(false);

                                for (MultiValueMap<String, String> valueMap : formDataList) {
                                    Mono<Boolean> res = seederService
                                            .seedData(academicBaseURI + "api/v1/academic-sessions/store", valueMap);
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