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
public class SeederClassRoomService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveCampusRepository slaveCampusRepository;

    public Mono<ServerResponse> seedClassroom() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveCampusRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull("Saleem Campus")
                .flatMap(slaveCampusEntity -> {

                    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                    formData.add("name", "CR001");
                    formData.add("description", "Class Room 001");
                    formData.add("status", "true");
                    formData.add("capacity", "50");
                    formData.add("code", "001");
                    formData.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));

                    MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
                    formData1.add("name", "CR002");
                    formData1.add("description", "Class Room 002");
                    formData1.add("status", "true");
                    formData1.add("capacity", "50");
                    formData1.add("code", "002");
                    formData1.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));

                    MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
                    formData2.add("name", "CR003");
                    formData2.add("description", "Class Room 003");
                    formData2.add("status", "true");
                    formData2.add("capacity", "50");
                    formData2.add("code", "003");
                    formData2.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));

                    MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
                    formData3.add("name", "CR004");
                    formData3.add("description", "Class Room 004");
                    formData3.add("status", "true");
                    formData3.add("capacity", "50");
                    formData3.add("code", "004");
                    formData3.add("campusUUID", String.valueOf(slaveCampusEntity.getUuid()));

                    formDataList.add(formData);
                    formDataList.add(formData1);
                    formDataList.add(formData2);
                    formDataList.add(formData3);


                    Flux<Boolean> fluxRes = Flux.just(false);

                    for (MultiValueMap<String, String> valueMap : formDataList) {
                        Mono<Boolean> res = seederService
                                .seedData(academicBaseURI + "api/v1/classrooms/store", valueMap);
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