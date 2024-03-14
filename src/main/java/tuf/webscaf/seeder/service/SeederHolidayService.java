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
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHolidayTypeRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

@Service
public class SeederHolidayService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    @Autowired
    private SlaveHolidayTypeRepository slaveHolidayTypeRepository;

    public Mono<ServerResponse> seedHoliday() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        return slaveHolidayTypeRepository.findByNameAndDeletedAtIsNull("Company")
                .flatMap(slaveHolidayTypeEntity -> {
                    return slaveHolidayTypeRepository.findByNameAndDeletedAtIsNull("National")
                            .flatMap(slaveHolidayTypeEntity1 -> {
                                return slaveHolidayTypeRepository.findByNameAndDeletedAtIsNull("International")
                                        .flatMap(slaveHolidayTypeEntity2 -> {

                                            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                                            formData.add("name", "Pakistan Day");
                                            formData.add("description", "Pakistan Day Holiday");
                                            formData.add("status", "true");
                                            formData.add("holidayTypeUUID", String.valueOf(slaveHolidayTypeEntity.getUuid()));

                                            MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
                                            formData1.add("name", "Independence Day");
                                            formData1.add("description", "Independence Day Holiday");
                                            formData1.add("status", "true");
                                            formData1.add("holidayTypeUUID", String.valueOf(slaveHolidayTypeEntity1.getUuid()));

                                            MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
                                            formData2.add("name", "Labour Day");
                                            formData2.add("description", "International Holiday of Labour Day");
                                            formData2.add("status", "true");
                                            formData2.add("holidayTypeUUID", String.valueOf(slaveHolidayTypeEntity2.getUuid()));


                                            formDataList.add(formData);
                                            formDataList.add(formData1);
                                            formDataList.add(formData2);


                                            Flux<Boolean> fluxRes = Flux.just(false);

                                            for (MultiValueMap<String, String> valueMap : formDataList) {
                                                Mono<Boolean> res = seederService
                                                        .seedData(academicBaseURI + "api/v1/holidays/store", valueMap);
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