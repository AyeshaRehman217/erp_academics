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
public class SeederAcademicCalendarEventService {


    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;


    public Mono<ServerResponse> seedAcademicCalendarEvent() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("name", "Commencement of Classes");
        formData.add("description", "Academic Calendar Event");
        formData.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("status", "true");
        formData1.add("name", "Last date to Add or Drop");
        formData1.add("description", "Academic Calendar Event");
        formData1.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("status", "true");
        formData2.add("name", "20 years of excellence");
        formData2.add("description", "Academic Calendar Event");
        formData2.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData3.add("status", "true");
        formData3.add("name", "Co Curricular Activities");
        formData3.add("description", "Academic Calendar Event");
        formData3.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData4 = new LinkedMultiValueMap<>();
        formData4.add("status", "true");
        formData4.add("name", "Eid Milad un Nabi S A W");
        formData4.add("description", "Calendar Event");
        formData4.add("academicCalendarEventTypeUUID", "d358feda-9910-4cdf-9d53-2bff0f7b7741");

        MultiValueMap<String, String> formData5 = new LinkedMultiValueMap<>();
        formData5.add("status", "true");
        formData5.add("name", "Faculty Training Program");
        formData5.add("description", "Academic Calendar Event");
        formData5.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData6 = new LinkedMultiValueMap<>();
        formData6.add("status", "true");
        formData6.add("name", "Mid Semester Examination");
        formData6.add("description", "Academic Calendar Event");
        formData6.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData7 = new LinkedMultiValueMap<>();
        formData7.add("status", "true");
        formData7.add("name", "Deadline for submission of Results of Mid Semester Examination");
        formData7.add("description", "Academic Calendar Event");
        formData7.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData8 = new LinkedMultiValueMap<>();
        formData8.add("status", "true");
        formData8.add("name", "Faculty Training Program");
        formData8.add("description", "Academic Calendar Event");
        formData8.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData9 = new LinkedMultiValueMap<>();
        formData9.add("status", "true");
        formData9.add("name", "Deadline for submission of Assignment or Quizzes or Sessional results");
        formData9.add("description", "Academic Calendar Event");
        formData9.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData10 = new LinkedMultiValueMap<>();
        formData10.add("status", "true");
        formData10.add("name", "Attendance lock");
        formData10.add("description", "Academic Calendar Event");
        formData10.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData11 = new LinkedMultiValueMap<>();
        formData11.add("status", "true");
        formData11.add("name", "Commencement of Practical Exam");
        formData11.add("description", "Academic Calendar Event");
        formData11.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData12 = new LinkedMultiValueMap<>();
        formData12.add("status", "true");
        formData12.add("name", "Collection of Admit Cards");
        formData12.add("description", "Academic Calendar Event");
        formData12.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData13 = new LinkedMultiValueMap<>();
        formData13.add("status", "true");
        formData13.add("name", "Theory Examination");
        formData13.add("description", "Academic Calendar Event");
        formData13.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        MultiValueMap<String, String> formData14 = new LinkedMultiValueMap<>();
        formData14.add("status", "true");
        formData14.add("name", "Semester Termination");
        formData14.add("description", "Academic Calendar Event");
        formData14.add("academicCalendarEventTypeUUID", "17f94bd7-93b7-47a5-9f22-405b5248bd30");

        formDataList.add(formData);
        formDataList.add(formData1);
        formDataList.add(formData2);
        formDataList.add(formData3);
        formDataList.add(formData4);
        formDataList.add(formData5);
        formDataList.add(formData6);
        formDataList.add(formData7);
        formDataList.add(formData8);
        formDataList.add(formData9);
        formDataList.add(formData10);
        formDataList.add(formData11);
        formDataList.add(formData12);
        formDataList.add(formData13);
        formDataList.add(formData14);


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/academic-calendar-events/store", valueMap);
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
