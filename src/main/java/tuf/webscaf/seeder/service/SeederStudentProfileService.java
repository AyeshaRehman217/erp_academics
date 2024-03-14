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
import java.util.UUID;

@Service
public class SeederStudentProfileService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    public Mono<ServerResponse> seedStudent() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();


        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("studentUUID", "3a832d83-f376-4fa4-947f-c11930d027e3");
        formData.add("status", "true");
        formData.add("description", "This is first student profile");
        formData.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData.add("firstName", "Attia");
        formData.add("lastName", "Naseer");
        formData.add("email", "attia.naseer@tuf.edu.pk");
        formData.add("telephoneNo", "041-230569");
        formData.add("nic", "33100-0019703-6");
        formData.add("birthDate", "16-01-1997 00:00:00");
        formData.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");
        formData.add("religionUUID", "e7328a96-2447-4d27-bbad-74f0f62c4c85");
        formData.add("sectUUID", "c22bba30-6b53-4cf5-a17c-d3e5c4a6163e");
        formData.add("casteUUID", "63b220d1-51b7-454a-8248-fe3a752cffad");
        formData.add("genderUUID", "6ba50d82-72f1-4599-975d-e309a7c4f506");
        formData.add("maritalStatusUUID", "cf21047b-ba02-462f-81a4-49363d4d3cdf");

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("studentUUID", "10e3825f-36b5-4c7c-a1c9-a558b397d334");
        formData1.add("status", "true");
        formData1.add("description", "This is second student profile");
        formData1.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData1.add("firstName", "Iqra");
        formData1.add("lastName", "Javed");
        formData1.add("email", "iqra.javed001@gmail.com");
        formData1.add("telephoneNo", "041-230569");
        formData1.add("nic", "33100-6619703-6");
        formData1.add("birthDate", "18-06-1997 00:00:00");
        formData1.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData1.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData1.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");
        formData1.add("religionUUID", "e7328a96-2447-4d27-bbad-74f0f62c4c85");
        formData1.add("sectUUID", "c22bba30-6b53-4cf5-a17c-d3e5c4a6163e");
        formData1.add("casteUUID", "63b220d1-51b7-454a-8248-fe3a752cffad");
        formData1.add("genderUUID", "6ba50d82-72f1-4599-975d-e309a7c4f506");
        formData1.add("maritalStatusUUID", "cf21047b-ba02-462f-81a4-49363d4d3cdf");

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("studentUUID", "4e2d6d3b-028f-44d2-9dc4-194fb3033423");
        formData2.add("status", "true");
        formData2.add("description", "This is third student profile");
        formData2.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData2.add("firstName", "Mariam");
        formData2.add("lastName", "Shoukat");
        formData2.add("email", "Mariam_shoukat@gmail.com");
        formData2.add("telephoneNo", "041-230569");
        formData2.add("nic", "33100-5519703-6");
        formData2.add("birthDate", "18-06-1997 00:00:00");
        formData2.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData2.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData2.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");
        formData2.add("religionUUID", "e7328a96-2447-4d27-bbad-74f0f62c4c85");
        formData2.add("sectUUID", "c22bba30-6b53-4cf5-a17c-d3e5c4a6163e");
        formData2.add("casteUUID", "63b220d1-51b7-454a-8248-fe3a752cffad");
        formData2.add("genderUUID", "6ba50d82-72f1-4599-975d-e309a7c4f506");
        formData2.add("maritalStatusUUID", "cf21047b-ba02-462f-81a4-49363d4d3cdf");


        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/student-profiles/store", valueMap);
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

    public Mono<ServerResponse> seedStudentMother() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();


        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("studentMotherUUID", "6facd687-31d8-4fc2-b221-667b298bdf99");
        formData.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData.add("name", "Robina Shaheen");
        formData.add("age", "48");
        formData.add("email", "robina.shaheen@gmail.com");
        formData.add("noOfDependents", "6");
        formData.add("nic", "33100-7719703-6");
        formData.add("officialTel", "041912937123");
        formData.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("status", "true");
        formData1.add("studentMotherUUID", "837eb9bf-9295-488e-bdb6-92327ebdcf88");
        formData1.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData1.add("name", "Nasreen Akhtar");
        formData1.add("age", "46");
        formData1.add("email", "nasreen.akhtar@gmail.com");
        formData1.add("noOfDependents", "6");
        formData1.add("nic", "33100-7819703-6");
        formData1.add("officialTel", "041912937123");
        formData1.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData1.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData1.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("status", "true");
        formData2.add("studentMotherUUID", "ad89910a-8a44-4c21-a724-da05b6f3cff8");
        formData2.add("image", "e46798cd-55af-4c71-822b-bc5a92f1d911");
        formData2.add("name", "Abida");
        formData2.add("age", "45");
        formData2.add("email", "abida.javed@gmail.com");
        formData2.add("noOfDependents", "6");
        formData2.add("nic", "33100-9119703-6");
        formData2.add("officialTel", "041912937123");
        formData2.add("cityUUID", "dd5aea4a-39d6-4b18-adf4-35f26f8a5913");
        formData2.add("stateUUID", "030c9ac2-19d3-4aa6-8c80-8287185048e9");
        formData2.add("countryUUID", "bef09b24-37f7-4169-a220-3a55c623fd15");


        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/student-mother-profiles/store", valueMap);
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