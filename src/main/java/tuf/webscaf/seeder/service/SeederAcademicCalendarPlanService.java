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
public class SeederAcademicCalendarPlanService {


    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;


    public Mono<ServerResponse> seedAcademicCalendarPlan() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("status", "true");
        formData.add("name", "Completion of Enrollment Process");
        formData.add("description", "Academic Plan for Fall 2022");
        formData.add("startDate", "29-08-2022 00:00:00");
        formData.add("endDate", "02-09-2022 00:00:00");
        formData.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData);

        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("status", "true");
        formData1.add("name", "Commencement of Classes");
        formData1.add("description", "Academic Plan for Fall 2022");
        formData1.add("startDate", "05-09-2022 00:00:00");
        formData1.add("endDate", "05-09-2022 00:00:00");
        formData1.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData1);

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("status", "true");
        formData2.add("name", "Last Date of Add or Drop");
        formData2.add("description", "Academic Plan for Fall 2022");
        formData2.add("startDate", "09-09-2022 00:00:00");
        formData2.add("endDate", "09-09-2022 00:00:00");
        formData2.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData2);

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData3.add("status", "true");
        formData3.add("name", "Mid Semester Examination");
        formData3.add("description", "Academic Plan for Fall 2022");
        formData3.add("startDate", "31-10-2022 00:00:00");
        formData3.add("endDate", "31-10-2022 00:00:00");
        formData3.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData3);


        MultiValueMap<String, String> formData4 = new LinkedMultiValueMap<>();
        formData4.add("status", "true");
        formData4.add("name", "Deadline for submission of Results of Mid Semester Examination");
        formData4.add("description", "Academic Plan for Fall 2022");
        formData4.add("startDate", "11-11-2022 00:00:00");
        formData4.add("endDate", "11-11-2022 00:00:00");
        formData4.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData4);

        MultiValueMap<String, String> formData5 = new LinkedMultiValueMap<>();
        formData5.add("status", "true");
        formData5.add("name", "Deadline for submission of Assignment or Quizzes or sessional results");
        formData5.add("description", "Academic Plan for Fall 2022");
        formData5.add("startDate", "16-12-2022 00:00:00");
        formData5.add("endDate", "16-12-2022 00:00:00");
        formData5.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData5);

        MultiValueMap<String, String> formData6 = new LinkedMultiValueMap<>();
        formData6.add("status", "true");
        formData6.add("name", "Attendance Lock");
        formData6.add("description", "Academic Plan for Fall 2022");
        formData6.add("startDate", "23-12-2022 00:00:00");
        formData6.add("endDate", "23-12-2022 00:00:00");
        formData6.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData6);

        MultiValueMap<String, String> formData7 = new LinkedMultiValueMap<>();
        formData7.add("status", "true");
        formData7.add("name", "Attendance Lock");
        formData7.add("description", "Academic Plan for Fall 2022");
        formData7.add("startDate", "23-12-2022 00:00:00");
        formData7.add("endDate", "23-12-2022 00:00:00");
        formData7.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData7);


        MultiValueMap<String, String> formData8 = new LinkedMultiValueMap<>();
        formData8.add("status", "true");
        formData8.add("name", "Commencement of Practical Examination");
        formData8.add("description", "Academic Plan for Fall 2022");
        formData8.add("startDate", "26-12-2022 00:00:00");
        formData8.add("endDate", "26-12-2022 00:00:00");
        formData8.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData8);

        MultiValueMap<String, String> formData9 = new LinkedMultiValueMap<>();
        formData9.add("status", "true");
        formData9.add("name", "Collection of Admit Cards");
        formData9.add("description", "Academic Plan for Fall 2022");
        formData9.add("startDate", "28-12-2022 00:00:00");
        formData9.add("endDate", "28-12-2022 00:00:00");
        formData9.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData9);

        MultiValueMap<String, String> formData10 = new LinkedMultiValueMap<>();
        formData10.add("status", "true");
        formData10.add("name", "Final Semester Examination");
        formData10.add("description", "Academic Plan for Fall 2022");
        formData10.add("startDate", "02-01-2023 00:00:00");
        formData10.add("endDate", "02-01-2023 00:00:00");
        formData10.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData10);

        MultiValueMap<String, String> formData11 = new LinkedMultiValueMap<>();
        formData11.add("status", "true");
        formData11.add("name", "Semester Termination Date");
        formData11.add("description", "Academic Plan for Fall 2022");
        formData11.add("startDate", "07-01-2023 00:00:00");
        formData11.add("endDate", "07-01-2023 00:00:00");
        formData11.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData11);

        MultiValueMap<String, String> formData12 = new LinkedMultiValueMap<>();
        formData12.add("status", "true");
        formData12.add("name", "Submission of Results to the Controller of Examinations");
        formData12.add("description", "Academic Plan for Fall 2022");
        formData12.add("startDate", "13-01-2023 00:00:00");
        formData12.add("endDate", "13-01-2023 00:00:00");
        formData12.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData12);

        MultiValueMap<String, String> formData13 = new LinkedMultiValueMap<>();
        formData13.add("status", "true");
        formData13.add("name", "Declaration of Results by the Controller of Examinations");
        formData13.add("description", "Academic Plan for Fall 2022");
        formData13.add("startDate", "18-01-2023 00:00:00");
        formData13.add("endDate", "19-01-2023 00:00:00");
        formData13.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");

        formDataList.add(formData13);

        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/academic-calendar-plans/store", valueMap);
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