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
public class SeederTeacherProfileService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;


    public Mono<ServerResponse> seedTeacherProfile() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();


        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("teacherUUID", "5c2dc539-0fc8-4f35-9fc5-4bcd3ab4d5b0");
        formData.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData.add("firstName", "Haseeb");
        formData.add("lastName", "Tariq");
        formData.add("email", "ht.teacher@gmail.com");
        formData.add("telephoneNo", "041111111");
        formData.add("nic", "33333-3333333-3");
        formData.add("birthDate", "16-01-1997 00:00:00");
        formData.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");
        formData.add("religionUUID", "e7328a96-2447-4d27-bbad-74f0f62c4c85");
        formData.add("sectUUID", "c22bba30-6b53-4cf5-a17c-d3e5c4a6163e");
        formData.add("casteUUID", "63b220d1-51b7-454a-8248-fe3a752cffad");
        formData.add("genderUUID", "818779f0-81cb-4961-894c-7bb309251903");
        formData.add("maritalStatusUUID", "cf21047b-ba02-462f-81a4-49363d4d3cdf");


        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("status", "true");
        formData1.add("teacherUUID", "2b2beaec-b84d-4ec4-8cd0-8e1b21338a66");
        formData1.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData1.add("firstName", "Waqar");
        formData1.add("lastName", "Ahmed");
        formData1.add("email", "wa.teacher@gmail.com");
        formData1.add("telephoneNo", "041111111");
        formData1.add("nic", "33333-2333333-3");
        formData1.add("birthDate", "08-01-1996 00:00:00");
        formData1.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData1.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData1.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");
        formData1.add("religionUUID", "e7328a96-2447-4d27-bbad-74f0f62c4c85");
        formData1.add("sectUUID", "c22bba30-6b53-4cf5-a17c-d3e5c4a6163e");
        formData1.add("casteUUID", "63b220d1-51b7-454a-8248-fe3a752cffad");
        formData1.add("genderUUID", "818779f0-81cb-4961-894c-7bb309251903");
        formData1.add("maritalStatusUUID", "cf21047b-ba02-462f-81a4-49363d4d3cdf");

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("status", "true");
        formData2.add("teacherUUID", "b6f7775b-235e-4976-bad4-6d23bdbd6080");
        formData2.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData2.add("firstName", "Imran");
        formData2.add("lastName", "Ayub");
        formData2.add("email", "ia.teacher@gmail.com");
        formData2.add("telephoneNo", "041111111");
        formData2.add("nic", "33333-1333333-3");
        formData2.add("birthDate", "21-07-1997 00:00:00");
        formData2.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData2.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData2.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");
        formData2.add("religionUUID", "e7328a96-2447-4d27-bbad-74f0f62c4c85");
        formData2.add("sectUUID", "c22bba30-6b53-4cf5-a17c-d3e5c4a6163e");
        formData2.add("casteUUID", "63b220d1-51b7-454a-8248-fe3a752cffad");
        formData2.add("genderUUID", "818779f0-81cb-4961-894c-7bb309251903");
        formData2.add("maritalStatusUUID", "cf21047b-ba02-462f-81a4-49363d4d3cdf");


        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/teacher-profiles/store", valueMap);
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
