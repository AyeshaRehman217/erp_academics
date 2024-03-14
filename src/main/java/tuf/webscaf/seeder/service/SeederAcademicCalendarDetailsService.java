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
public class SeederAcademicCalendarDetailsService {

    @Autowired
    SeederService seederService;

    @Autowired
    CustomResponse customResponse;

    @Value("${server.erp_academic_module.uri}")
    private String academicBaseURI;

    public Mono<ServerResponse> seedAcademicCalendarDetail() {

        List<MultiValueMap<String, String>> formDataList = new ArrayList<>();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData.add("calendarDate", "05-09-2022 00:00:00");
        formData.add("status", "true");
        formData.add("comments", "Commencement of classes");
        formData.add("isWorkingDay", "true");
        formData.add("isLectureAllowed", "true");

        formDataList.add(formData);


        MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
        formData1.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData1.add("calendarDate", "06-09-2022 00:00:00");
        formData1.add("status", "true");
        formData1.add("comments", "");
        formData1.add("isWorkingDay", "true");
        formData1.add("isLectureAllowed", "true");

        formDataList.add(formData1);

        MultiValueMap<String, String> formData2 = new LinkedMultiValueMap<>();
        formData2.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData2.add("calendarDate", "07-09-2022 00:00:00");
        formData2.add("status", "true");
        formData2.add("comments", "");
        formData2.add("isWorkingDay", "true");
        formData2.add("isLectureAllowed", "true");

        formDataList.add(formData2);

        MultiValueMap<String, String> formData3 = new LinkedMultiValueMap<>();
        formData3.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData3.add("calendarDate", "08-09-2022 00:00:00");
        formData3.add("status", "true");
        formData3.add("comments", "");
        formData3.add("isWorkingDay", "true");
        formData3.add("isLectureAllowed", "true");

        formDataList.add(formData3);

        MultiValueMap<String, String> formData4 = new LinkedMultiValueMap<>();
        formData4.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData4.add("calendarDate", "09-09-2022 00:00:00");
        formData4.add("status", "true");
        formData4.add("comments", "Last date to Add or Drop");
        formData4.add("isWorkingDay", "true");
        formData4.add("isLectureAllowed", "true");

        formDataList.add(formData4);

        MultiValueMap<String, String> formData5 = new LinkedMultiValueMap<>();
        formData5.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData5.add("calendarDate", "10-09-2022 00:00:00");
        formData5.add("status", "true");
        formData5.add("comments", "");
        formData5.add("isWorkingDay", "false");
        formData5.add("isLectureAllowed", "false");

        formDataList.add(formData5);

        MultiValueMap<String, String> formData6 = new LinkedMultiValueMap<>();
        formData6.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData6.add("calendarDate", "11-09-2022 00:00:00");
        formData6.add("status", "true");
        formData6.add("comments", "");
        formData6.add("isWorkingDay", "false");
        formData6.add("isLectureAllowed", "false");

        formDataList.add(formData6);

        MultiValueMap<String, String> formData7 = new LinkedMultiValueMap<>();
        formData7.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData7.add("calendarDate", "12-09-2022 00:00:00");
        formData7.add("status", "true");
        formData7.add("comments", "");
        formData7.add("isWorkingDay", "true");
        formData7.add("isLectureAllowed", "true");

        formDataList.add(formData7);

        MultiValueMap<String, String> formData8 = new LinkedMultiValueMap<>();
        formData8.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData8.add("calendarDate", "13-09-2022 00:00:00");
        formData8.add("status", "true");
        formData8.add("comments", "");
        formData8.add("isWorkingDay", "true");
        formData8.add("isLectureAllowed", "true");

        formDataList.add(formData8);

        MultiValueMap<String, String> formData9 = new LinkedMultiValueMap<>();
        formData9.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData9.add("calendarDate", "14-09-2022 00:00:00");
        formData9.add("status", "true");
        formData9.add("comments", "");
        formData9.add("isWorkingDay", "true");
        formData9.add("isLectureAllowed", "true");

        formDataList.add(formData9);

        MultiValueMap<String, String> formData10 = new LinkedMultiValueMap<>();
        formData10.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData10.add("calendarDate", "15-09-2022 00:00:00");
        formData10.add("status", "true");
        formData10.add("comments", "");
        formData10.add("isWorkingDay", "true");
        formData10.add("isLectureAllowed", "true");

        formDataList.add(formData10);

        MultiValueMap<String, String> formData11 = new LinkedMultiValueMap<>();
        formData11.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData11.add("calendarDate", "16-09-2022 00:00:00");
        formData11.add("status", "true");
        formData11.add("comments", "");
        formData11.add("isWorkingDay", "true");
        formData11.add("isLectureAllowed", "true");

        formDataList.add(formData11);

        MultiValueMap<String, String> formData12 = new LinkedMultiValueMap<>();
        formData12.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData12.add("calendarDate", "17-09-2022 00:00:00");
        formData12.add("status", "true");
        formData12.add("comments", "");
        formData12.add("isWorkingDay", "false");
        formData12.add("isLectureAllowed", "false");

        formDataList.add(formData12);


        MultiValueMap<String, String> formData13 = new LinkedMultiValueMap<>();
        formData13.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData13.add("calendarDate", "18-09-2022 00:00:00");
        formData13.add("status", "true");
        formData13.add("comments", "");
        formData13.add("isWorkingDay", "false");
        formData13.add("isLectureAllowed", "false");

        formDataList.add(formData13);

        MultiValueMap<String, String> formData14 = new LinkedMultiValueMap<>();
        formData14.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData14.add("calendarDate", "19-09-2022 00:00:00");
        formData14.add("status", "true");
        formData14.add("comments", "");
        formData14.add("isWorkingDay", "true");
        formData14.add("isLectureAllowed", "true");

        formDataList.add(formData14);

        MultiValueMap<String, String> formData15 = new LinkedMultiValueMap<>();
        formData15.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData15.add("calendarDate", "20-09-2022 00:00:00");
        formData15.add("status", "true");
        formData15.add("comments", "");
        formData15.add("isWorkingDay", "true");
        formData15.add("isLectureAllowed", "true");

        formDataList.add(formData15);

        MultiValueMap<String, String> formData16 = new LinkedMultiValueMap<>();
        formData16.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData16.add("calendarDate", "21-09-2022 00:00:00");
        formData16.add("status", "true");
        formData16.add("comments", "");
        formData16.add("isWorkingDay", "true");
        formData16.add("isLectureAllowed", "true");

        formDataList.add(formData16);

        MultiValueMap<String, String> formData17 = new LinkedMultiValueMap<>();
        formData17.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData17.add("calendarDate", "22-09-2022 00:00:00");
        formData17.add("status", "true");
        formData17.add("comments", "");
        formData17.add("isWorkingDay", "true");
        formData17.add("isLectureAllowed", "true");

        formDataList.add(formData17);

        MultiValueMap<String, String> formData18 = new LinkedMultiValueMap<>();
        formData18.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData18.add("calendarDate", "23-09-2022 00:00:00");
        formData18.add("status", "true");
        formData18.add("comments", "");
        formData18.add("isWorkingDay", "true");
        formData18.add("isLectureAllowed", "true");

        formDataList.add(formData18);

        MultiValueMap<String, String> formData19 = new LinkedMultiValueMap<>();
        formData19.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData19.add("calendarDate", "24-09-2022 00:00:00");
        formData19.add("status", "true");
        formData19.add("comments", "");
        formData19.add("isWorkingDay", "false");
        formData19.add("isLectureAllowed", "false");

        formDataList.add(formData19);

        MultiValueMap<String, String> formData20 = new LinkedMultiValueMap<>();
        formData20.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData20.add("calendarDate", "25-09-2022 00:00:00");
        formData20.add("status", "true");
        formData20.add("comments", "");
        formData20.add("isWorkingDay", "false");
        formData20.add("isLectureAllowed", "false");

        formDataList.add(formData20);

        MultiValueMap<String, String> formData21 = new LinkedMultiValueMap<>();
        formData21.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData21.add("calendarDate", "26-09-2022 00:00:00");
        formData21.add("status", "true");
        formData21.add("comments", "20 years of excellence");
        formData21.add("isWorkingDay", "true");
        formData21.add("isLectureAllowed", "false");

        formDataList.add(formData21);

        MultiValueMap<String, String> formData22 = new LinkedMultiValueMap<>();
        formData22.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData22.add("calendarDate", "27-09-2022 00:00:00");
        formData22.add("status", "true");
        formData22.add("comments", "");
        formData22.add("isWorkingDay", "true");
        formData22.add("isLectureAllowed", "false");

        formDataList.add(formData22);

        MultiValueMap<String, String> formData23 = new LinkedMultiValueMap<>();
        formData23.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData23.add("calendarDate", "28-09-2022 00:00:00");
        formData23.add("status", "true");
        formData23.add("comments", "");
        formData23.add("isWorkingDay", "true");
        formData23.add("isLectureAllowed", "false");

        formDataList.add(formData23);

        MultiValueMap<String, String> formData24 = new LinkedMultiValueMap<>();
        formData24.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData24.add("calendarDate", "29-09-2022 00:00:00");
        formData24.add("status", "true");
        formData24.add("comments", "");
        formData24.add("isWorkingDay", "true");
        formData24.add("isLectureAllowed", "false");

        formDataList.add(formData24);

        MultiValueMap<String, String> formData25 = new LinkedMultiValueMap<>();
        formData25.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData25.add("calendarDate", "30-09-2022 00:00:00");
        formData25.add("status", "true");
        formData25.add("comments", "");
        formData25.add("isWorkingDay", "true");
        formData25.add("isLectureAllowed", "false");

        formDataList.add(formData25);

        MultiValueMap<String, String> formData26 = new LinkedMultiValueMap<>();
        formData26.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData26.add("calendarDate", "01-10-2022 00:00:00");
        formData26.add("status", "true");
        formData26.add("comments", "");
        formData26.add("isWorkingDay", "false");
        formData26.add("isLectureAllowed", "false");

        formDataList.add(formData26);

        MultiValueMap<String, String> formData27 = new LinkedMultiValueMap<>();
        formData27.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData27.add("calendarDate", "02-10-2022 00:00:00");
        formData27.add("status", "true");
        formData27.add("comments", "Co Curricular Activities");
        formData27.add("isWorkingDay", "false");
        formData27.add("isLectureAllowed", "false");

        formDataList.add(formData27);

        MultiValueMap<String, String> formData28 = new LinkedMultiValueMap<>();
        formData28.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData28.add("calendarDate", "03-10-2022 00:00:00");
        formData28.add("status", "true");
        formData28.add("comments", "");
        formData28.add("isWorkingDay", "true");
        formData28.add("isLectureAllowed", "true");

        formDataList.add(formData28);

        MultiValueMap<String, String> formData29 = new LinkedMultiValueMap<>();
        formData29.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData29.add("calendarDate", "04-10-2022 00:00:00");
        formData29.add("status", "true");
        formData29.add("comments", "");
        formData29.add("isWorkingDay", "true");
        formData29.add("isLectureAllowed", "true");

        formDataList.add(formData29);

        MultiValueMap<String, String> formData30 = new LinkedMultiValueMap<>();
        formData30.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData30.add("calendarDate", "05-10-2022 00:00:00");
        formData30.add("status", "true");
        formData30.add("comments", "");
        formData30.add("isWorkingDay", "true");
        formData30.add("isLectureAllowed", "true");

        formDataList.add(formData30);

        MultiValueMap<String, String> formData31 = new LinkedMultiValueMap<>();
        formData31.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData31.add("calendarDate", "06-10-2022 00:00:00");
        formData31.add("status", "true");
        formData31.add("comments", "");
        formData31.add("isWorkingDay", "true");
        formData31.add("isLectureAllowed", "true");

        formDataList.add(formData31);

        MultiValueMap<String, String> formData32 = new LinkedMultiValueMap<>();
        formData32.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData32.add("calendarDate", "07-10-2022 00:00:00");
        formData32.add("status", "true");
        formData32.add("comments", "");
        formData32.add("isWorkingDay", "true");
        formData32.add("isLectureAllowed", "true");

        formDataList.add(formData32);

        MultiValueMap<String, String> formData33 = new LinkedMultiValueMap<>();
        formData33.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData33.add("calendarDate", "08-10-2022 00:00:00");
        formData33.add("status", "true");
        formData33.add("comments", "Eid Milad un Nabi S A W");
        formData33.add("isWorkingDay", "false");
        formData33.add("isLectureAllowed", "false");

        formDataList.add(formData33);

        MultiValueMap<String, String> formData34 = new LinkedMultiValueMap<>();
        formData34.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData34.add("calendarDate", "09-10-2022 00:00:00");
        formData34.add("status", "true");
        formData34.add("comments", "");
        formData34.add("isWorkingDay", "false");
        formData34.add("isLectureAllowed", "false");

        formDataList.add(formData34);

        MultiValueMap<String, String> formData35 = new LinkedMultiValueMap<>();
        formData35.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData35.add("calendarDate", "10-10-2022 00:00:00");
        formData35.add("status", "true");
        formData35.add("comments", "");
        formData35.add("isWorkingDay", "true");
        formData35.add("isLectureAllowed", "true");

        formDataList.add(formData35);

        MultiValueMap<String, String> formData36 = new LinkedMultiValueMap<>();
        formData36.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData36.add("calendarDate", "11-10-2022 00:00:00");
        formData36.add("status", "true");
        formData36.add("comments", "");
        formData36.add("isWorkingDay", "true");
        formData36.add("isLectureAllowed", "true");

        formDataList.add(formData36);

        MultiValueMap<String, String> formData37 = new LinkedMultiValueMap<>();
        formData37.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData37.add("calendarDate", "12-10-2022 00:00:00");
        formData37.add("status", "true");
        formData37.add("comments", "");
        formData37.add("isWorkingDay", "true");
        formData37.add("isLectureAllowed", "true");

        formDataList.add(formData37);

        MultiValueMap<String, String> formData38 = new LinkedMultiValueMap<>();
        formData38.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData38.add("calendarDate", "13-10-2022 00:00:00");
        formData38.add("status", "true");
        formData38.add("comments", "");
        formData38.add("isWorkingDay", "true");
        formData38.add("isLectureAllowed", "true");

        formDataList.add(formData38);

        MultiValueMap<String, String> formData39 = new LinkedMultiValueMap<>();
        formData39.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData39.add("calendarDate", "14-10-2022 00:00:00");
        formData39.add("status", "true");
        formData39.add("comments", "");
        formData39.add("isWorkingDay", "true");
        formData39.add("isLectureAllowed", "true");

        formDataList.add(formData39);

        MultiValueMap<String, String> formData40 = new LinkedMultiValueMap<>();
        formData40.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData40.add("calendarDate", "15-10-2022 00:00:00");
        formData40.add("status", "true");
        formData40.add("comments", "Faculty Training Program");
        formData40.add("isWorkingDay", "false");
        formData40.add("isLectureAllowed", "false");

        formDataList.add(formData40);

        MultiValueMap<String, String> formData41 = new LinkedMultiValueMap<>();
        formData41.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData41.add("calendarDate", "16-10-2022 00:00:00");
        formData41.add("status", "true");
        formData41.add("comments", "");
        formData41.add("isWorkingDay", "false");
        formData41.add("isLectureAllowed", "false");

        formDataList.add(formData41);

        MultiValueMap<String, String> formData42 = new LinkedMultiValueMap<>();
        formData42.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData42.add("calendarDate", "17-10-2022 00:00:00");
        formData42.add("status", "true");
        formData42.add("comments", "");
        formData42.add("isWorkingDay", "true");
        formData42.add("isLectureAllowed", "true");

        formDataList.add(formData42);

        MultiValueMap<String, String> formData43 = new LinkedMultiValueMap<>();
        formData43.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData43.add("calendarDate", "18-10-2022 00:00:00");
        formData43.add("status", "true");
        formData43.add("comments", "");
        formData43.add("isWorkingDay", "true");
        formData43.add("isLectureAllowed", "true");

        formDataList.add(formData43);

        MultiValueMap<String, String> formData44 = new LinkedMultiValueMap<>();
        formData44.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData44.add("calendarDate", "19-10-2022 00:00:00");
        formData44.add("status", "true");
        formData44.add("comments", "");
        formData44.add("isWorkingDay", "true");
        formData44.add("isLectureAllowed", "true");

        formDataList.add(formData44);

        MultiValueMap<String, String> formData45 = new LinkedMultiValueMap<>();
        formData45.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData45.add("calendarDate", "20-10-2022 00:00:00");
        formData45.add("status", "true");
        formData45.add("comments", "");
        formData45.add("isWorkingDay", "true");
        formData45.add("isLectureAllowed", "true");

        formDataList.add(formData45);

        MultiValueMap<String, String> formData46 = new LinkedMultiValueMap<>();
        formData46.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData46.add("calendarDate", "21-10-2022 00:00:00");
        formData46.add("status", "true");
        formData46.add("comments", "");
        formData46.add("isWorkingDay", "true");
        formData46.add("isLectureAllowed", "true");

        formDataList.add(formData46);

        MultiValueMap<String, String> formData47 = new LinkedMultiValueMap<>();
        formData47.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData47.add("calendarDate", "22-10-2022 00:00:00");
        formData47.add("status", "true");
        formData47.add("comments", "");
        formData47.add("isWorkingDay", "false");
        formData47.add("isLectureAllowed", "false");

        formDataList.add(formData47);

        MultiValueMap<String, String> formData48 = new LinkedMultiValueMap<>();
        formData48.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData48.add("calendarDate", "23-10-2022 00:00:00");
        formData48.add("status", "true");
        formData48.add("comments", "");
        formData48.add("isWorkingDay", "false");
        formData48.add("isLectureAllowed", "false");

        formDataList.add(formData48);

        MultiValueMap<String, String> formData49 = new LinkedMultiValueMap<>();
        formData49.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData49.add("calendarDate", "24-10-2022 00:00:00");
        formData49.add("status", "true");
        formData49.add("comments", "");
        formData49.add("isWorkingDay", "true");
        formData49.add("isLectureAllowed", "true");

        formDataList.add(formData49);

        MultiValueMap<String, String> formData50 = new LinkedMultiValueMap<>();
        formData50.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData50.add("calendarDate", "25-10-2022 00:00:00");
        formData50.add("status", "true");
        formData50.add("comments", "");
        formData50.add("isWorkingDay", "true");
        formData50.add("isLectureAllowed", "true");

        formDataList.add(formData50);

        MultiValueMap<String, String> formData51 = new LinkedMultiValueMap<>();
        formData51.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData51.add("calendarDate", "26-10-2022 00:00:00");
        formData51.add("status", "true");
        formData51.add("comments", "");
        formData51.add("isWorkingDay", "true");
        formData51.add("isLectureAllowed", "true");

        formDataList.add(formData51);

        MultiValueMap<String, String> formData52 = new LinkedMultiValueMap<>();
        formData52.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData52.add("calendarDate", "27-10-2022 00:00:00");
        formData52.add("status", "true");
        formData52.add("comments", "");
        formData52.add("isWorkingDay", "true");
        formData52.add("isLectureAllowed", "true");

        formDataList.add(formData52);

        MultiValueMap<String, String> formData53 = new LinkedMultiValueMap<>();
        formData53.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData53.add("calendarDate", "28-10-2022 00:00:00");
        formData53.add("status", "true");
        formData53.add("comments", "");
        formData53.add("isWorkingDay", "true");
        formData53.add("isLectureAllowed", "true");

        formDataList.add(formData53);

        MultiValueMap<String, String> formData54 = new LinkedMultiValueMap<>();
        formData54.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData54.add("calendarDate", "29-10-2022 00:00:00");
        formData54.add("status", "true");
        formData54.add("comments", "");
        formData54.add("isWorkingDay", "false");
        formData54.add("isLectureAllowed", "false");

        formDataList.add(formData54);


        MultiValueMap<String, String> formData55 = new LinkedMultiValueMap<>();
        formData55.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData55.add("calendarDate", "30-10-2022 00:00:00");
        formData55.add("status", "true");
        formData55.add("comments", "");
        formData55.add("isWorkingDay", "false");
        formData55.add("isLectureAllowed", "false");

        formDataList.add(formData55);

        MultiValueMap<String, String> formData56 = new LinkedMultiValueMap<>();
        formData56.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData56.add("calendarDate", "31-10-2022 00:00:00");
        formData56.add("status", "true");
        formData56.add("comments", "Mid Semester Examination");
        formData56.add("isWorkingDay", "true");
        formData56.add("isLectureAllowed", "false");

        formDataList.add(formData56);


        MultiValueMap<String, String> formData57 = new LinkedMultiValueMap<>();
        formData57.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData57.add("calendarDate", "01-11-2022 00:00:00");
        formData57.add("status", "true");
        formData57.add("comments", "");
        formData57.add("isWorkingDay", "true");
        formData57.add("isLectureAllowed", "false");

        formDataList.add(formData57);

        MultiValueMap<String, String> formData58 = new LinkedMultiValueMap<>();
        formData58.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData58.add("calendarDate", "02-11-2022 00:00:00");
        formData58.add("status", "true");
        formData58.add("comments", "");
        formData58.add("isWorkingDay", "true");
        formData58.add("isLectureAllowed", "false");

        formDataList.add(formData58);

        MultiValueMap<String, String> formData59 = new LinkedMultiValueMap<>();
        formData59.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData59.add("calendarDate", "03-11-2022 00:00:00");
        formData59.add("status", "true");
        formData59.add("comments", "");
        formData59.add("isWorkingDay", "true");
        formData59.add("isLectureAllowed", "false");

        formDataList.add(formData59);

        MultiValueMap<String, String> formData60 = new LinkedMultiValueMap<>();
        formData60.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData60.add("calendarDate", "04-11-2022 00:00:00");
        formData60.add("status", "true");
        formData60.add("comments", "");
        formData60.add("isWorkingDay", "true");
        formData60.add("isLectureAllowed", "false");

        formDataList.add(formData60);

        MultiValueMap<String, String> formData61 = new LinkedMultiValueMap<>();
        formData61.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData61.add("calendarDate", "05-11-2022 00:00:00");
        formData61.add("status", "true");
        formData61.add("comments", "");
        formData61.add("isWorkingDay", "false");
        formData61.add("isLectureAllowed", "false");

        formDataList.add(formData61);

        MultiValueMap<String, String> formData62 = new LinkedMultiValueMap<>();
        formData62.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData62.add("calendarDate", "06-11-2022 00:00:00");
        formData62.add("status", "true");
        formData62.add("comments", "");
        formData62.add("isWorkingDay", "false");
        formData62.add("isLectureAllowed", "false");

        formDataList.add(formData62);

        MultiValueMap<String, String> formData63 = new LinkedMultiValueMap<>();
        formData63.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData63.add("calendarDate", "07-11-2022 00:00:00");
        formData63.add("status", "true");
        formData63.add("comments", "");
        formData63.add("isWorkingDay", "true");
        formData63.add("isLectureAllowed", "true");

        formDataList.add(formData63);
        MultiValueMap<String, String> formData64 = new LinkedMultiValueMap<>();
        formData64.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData64.add("calendarDate", "08-11-2022 00:00:00");
        formData64.add("status", "true");
        formData64.add("comments", "");
        formData64.add("isWorkingDay", "true");
        formData64.add("isLectureAllowed", "true");

        formDataList.add(formData64);

        MultiValueMap<String, String> formData65 = new LinkedMultiValueMap<>();
        formData65.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData65.add("calendarDate", "09-11-2022 00:00:00");
        formData65.add("status", "true");
        formData65.add("comments", "");
        formData65.add("isWorkingDay", "true");
        formData65.add("isLectureAllowed", "true");

        formDataList.add(formData65);

        MultiValueMap<String, String> formData66 = new LinkedMultiValueMap<>();
        formData66.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData66.add("calendarDate", "10-11-2022 00:00:00");
        formData66.add("status", "true");
        formData66.add("comments", "");
        formData66.add("isWorkingDay", "true");
        formData66.add("isLectureAllowed", "true");

        formDataList.add(formData66);

        MultiValueMap<String, String> formData67 = new LinkedMultiValueMap<>();
        formData67.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData67.add("calendarDate", "11-11-2022 00:00:00");
        formData67.add("status", "true");
        formData67.add("comments", "Deadline for submission of Results of Mid Semester Examination");
        formData67.add("isWorkingDay", "true");
        formData67.add("isLectureAllowed", "true");

        formDataList.add(formData67);

        MultiValueMap<String, String> formData68 = new LinkedMultiValueMap<>();
        formData68.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData68.add("calendarDate", "12-11-2022 00:00:00");
        formData68.add("status", "true");
        formData68.add("comments", "");
        formData68.add("isWorkingDay", "false");
        formData68.add("isLectureAllowed", "false");

        formDataList.add(formData68);

        MultiValueMap<String, String> formData69 = new LinkedMultiValueMap<>();
        formData69.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData69.add("calendarDate", "13-11-2022 00:00:00");
        formData69.add("status", "true");
        formData69.add("comments", "");
        formData69.add("isWorkingDay", "false");
        formData69.add("isLectureAllowed", "false");

        formDataList.add(formData69);

        MultiValueMap<String, String> formData70 = new LinkedMultiValueMap<>();
        formData70.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData70.add("calendarDate", "14-11-2022 00:00:00");
        formData70.add("status", "true");
        formData70.add("comments", "");
        formData70.add("isWorkingDay", "true");
        formData70.add("isLectureAllowed", "true");

        formDataList.add(formData70);

        MultiValueMap<String, String> formData71 = new LinkedMultiValueMap<>();
        formData71.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData71.add("calendarDate", "15-11-2022 00:00:00");
        formData71.add("status", "true");
        formData71.add("comments", "");
        formData71.add("isWorkingDay", "true");
        formData71.add("isLectureAllowed", "true");

        formDataList.add(formData71);

        MultiValueMap<String, String> formData72 = new LinkedMultiValueMap<>();
        formData72.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData72.add("calendarDate", "16-11-2022 00:00:00");
        formData72.add("status", "true");
        formData72.add("comments", "");
        formData72.add("isWorkingDay", "true");
        formData72.add("isLectureAllowed", "true");

        formDataList.add(formData72);

        MultiValueMap<String, String> formData73 = new LinkedMultiValueMap<>();
        formData73.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData73.add("calendarDate", "17-11-2022 00:00:00");
        formData73.add("status", "true");
        formData73.add("comments", "");
        formData73.add("isWorkingDay", "true");
        formData73.add("isLectureAllowed", "true");

        formDataList.add(formData73);

        MultiValueMap<String, String> formData74 = new LinkedMultiValueMap<>();
        formData74.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData74.add("calendarDate", "18-11-2022 00:00:00");
        formData74.add("status", "true");
        formData74.add("comments", "");
        formData74.add("isWorkingDay", "true");
        formData74.add("isLectureAllowed", "true");

        formDataList.add(formData74);

        MultiValueMap<String, String> formData75 = new LinkedMultiValueMap<>();
        formData75.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData75.add("calendarDate", "19-11-2022 00:00:00");
        formData75.add("status", "true");
        formData75.add("comments", "");
        formData75.add("isWorkingDay", "false");
        formData75.add("isLectureAllowed", "false");

        formDataList.add(formData75);

        MultiValueMap<String, String> formData76 = new LinkedMultiValueMap<>();
        formData76.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData76.add("calendarDate", "20-11-2022 00:00:00");
        formData76.add("status", "true");
        formData76.add("comments", "");
        formData76.add("isWorkingDay", "false");
        formData76.add("isLectureAllowed", "false");

        formDataList.add(formData76);

        MultiValueMap<String, String> formData77 = new LinkedMultiValueMap<>();
        formData77.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData77.add("calendarDate", "21-11-2022 00:00:00");
        formData77.add("status", "true");
        formData77.add("comments", "");
        formData77.add("isWorkingDay", "true");
        formData77.add("isLectureAllowed", "true");

        formDataList.add(formData77);

        MultiValueMap<String, String> formData78 = new LinkedMultiValueMap<>();
        formData78.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData78.add("calendarDate", "22-11-2022 00:00:00");
        formData78.add("status", "true");
        formData78.add("comments", "");
        formData78.add("isWorkingDay", "true");
        formData78.add("isLectureAllowed", "true");

        formDataList.add(formData78);

        MultiValueMap<String, String> formData79 = new LinkedMultiValueMap<>();
        formData79.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData79.add("calendarDate", "23-11-2022 00:00:00");
        formData79.add("status", "true");
        formData79.add("comments", "");
        formData79.add("isWorkingDay", "true");
        formData79.add("isLectureAllowed", "true");

        formDataList.add(formData79);

        MultiValueMap<String, String> formData80 = new LinkedMultiValueMap<>();
        formData80.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData80.add("calendarDate", "24-11-2022 00:00:00");
        formData80.add("status", "true");
        formData80.add("comments", "");
        formData80.add("isWorkingDay", "true");
        formData80.add("isLectureAllowed", "true");

        formDataList.add(formData80);

        MultiValueMap<String, String> formData81 = new LinkedMultiValueMap<>();
        formData81.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData81.add("calendarDate", "25-11-2022 00:00:00");
        formData81.add("status", "true");
        formData81.add("comments", "");
        formData81.add("isWorkingDay", "true");
        formData81.add("isLectureAllowed", "true");

        formDataList.add(formData81);

        MultiValueMap<String, String> formData82 = new LinkedMultiValueMap<>();
        formData82.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData82.add("calendarDate", "26-11-2022 00:00:00");
        formData82.add("status", "true");
        formData82.add("comments", "");
        formData82.add("isWorkingDay", "false");
        formData82.add("isLectureAllowed", "false");

        formDataList.add(formData82);

        MultiValueMap<String, String> formData83 = new LinkedMultiValueMap<>();
        formData83.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData83.add("calendarDate", "27-11-2022 00:00:00");
        formData83.add("status", "true");
        formData83.add("comments", "");
        formData83.add("isWorkingDay", "false");
        formData83.add("isLectureAllowed", "false");

        formDataList.add(formData83);

        MultiValueMap<String, String> formData84 = new LinkedMultiValueMap<>();
        formData84.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData84.add("calendarDate", "28-11-2022 00:00:00");
        formData84.add("status", "true");
        formData84.add("comments", "");
        formData84.add("isWorkingDay", "true");
        formData84.add("isLectureAllowed", "true");

        formDataList.add(formData84);

        MultiValueMap<String, String> formData85 = new LinkedMultiValueMap<>();
        formData85.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData85.add("calendarDate", "29-11-2022 00:00:00");
        formData85.add("status", "true");
        formData85.add("comments", "");
        formData85.add("isWorkingDay", "true");
        formData85.add("isLectureAllowed", "true");

        formDataList.add(formData85);

        MultiValueMap<String, String> formData86 = new LinkedMultiValueMap<>();
        formData86.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData86.add("calendarDate", "30-11-2022 00:00:00");
        formData86.add("status", "true");
        formData86.add("comments", "");
        formData86.add("isWorkingDay", "true");
        formData86.add("isLectureAllowed", "true");

        formDataList.add(formData86);

        MultiValueMap<String, String> formData87 = new LinkedMultiValueMap<>();
        formData87.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData87.add("calendarDate", "01-12-2022 00:00:00");
        formData87.add("status", "true");
        formData87.add("comments", "");
        formData87.add("isWorkingDay", "true");
        formData87.add("isLectureAllowed", "true");

        formDataList.add(formData87);

        MultiValueMap<String, String> formData88 = new LinkedMultiValueMap<>();
        formData88.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData88.add("calendarDate", "02-12-2022 00:00:00");
        formData88.add("status", "true");
        formData88.add("comments", "");
        formData88.add("isWorkingDay", "true");
        formData88.add("isLectureAllowed", "true");

        formDataList.add(formData88);

        MultiValueMap<String, String> formData89 = new LinkedMultiValueMap<>();
        formData89.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData89.add("calendarDate", "03-12-2022 00:00:00");
        formData89.add("status", "true");
        formData89.add("comments", "");
        formData89.add("isWorkingDay", "false");
        formData89.add("isLectureAllowed", "false");

        formDataList.add(formData89);

        MultiValueMap<String, String> formData90 = new LinkedMultiValueMap<>();
        formData90.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData90.add("calendarDate", "04-12-2022 00:00:00");
        formData90.add("status", "true");
        formData90.add("comments", "");
        formData90.add("isWorkingDay", "false");
        formData90.add("isLectureAllowed", "false");

        formDataList.add(formData90);

        MultiValueMap<String, String> formData91 = new LinkedMultiValueMap<>();
        formData91.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData91.add("calendarDate", "05-12-2022 00:00:00");
        formData91.add("status", "true");
        formData91.add("comments", "");
        formData91.add("isWorkingDay", "true");
        formData91.add("isLectureAllowed", "true");

        formDataList.add(formData91);

        MultiValueMap<String, String> formData92 = new LinkedMultiValueMap<>();
        formData92.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData92.add("calendarDate", "06-12-2022 00:00:00");
        formData92.add("status", "true");
        formData92.add("comments", "");
        formData92.add("isWorkingDay", "true");
        formData92.add("isLectureAllowed", "true");

        formDataList.add(formData92);

        MultiValueMap<String, String> formData93 = new LinkedMultiValueMap<>();
        formData93.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData93.add("calendarDate", "07-12-2022 00:00:00");
        formData93.add("status", "true");
        formData93.add("comments", "");
        formData93.add("isWorkingDay", "true");
        formData93.add("isLectureAllowed", "true");

        formDataList.add(formData93);

        MultiValueMap<String, String> formData94 = new LinkedMultiValueMap<>();
        formData94.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData94.add("calendarDate", "08-12-2022 00:00:00");
        formData94.add("status", "true");
        formData94.add("comments", "");
        formData94.add("isWorkingDay", "true");
        formData94.add("isLectureAllowed", "true");

        formDataList.add(formData94);

        MultiValueMap<String, String> formData95 = new LinkedMultiValueMap<>();
        formData95.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData95.add("calendarDate", "09-12-2022 00:00:00");
        formData95.add("status", "true");
        formData95.add("comments", "");
        formData95.add("isWorkingDay", "true");
        formData95.add("isLectureAllowed", "true");

        formDataList.add(formData95);

        MultiValueMap<String, String> formData96 = new LinkedMultiValueMap<>();
        formData96.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData96.add("calendarDate", "10-12-2022 00:00:00");
        formData96.add("status", "true");
        formData96.add("comments", "Faculty Training Program");
        formData96.add("isWorkingDay", "false");
        formData96.add("isLectureAllowed", "false");

        formDataList.add(formData96);

        MultiValueMap<String, String> formData97 = new LinkedMultiValueMap<>();
        formData97.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData97.add("calendarDate", "11-12-2022 00:00:00");
        formData97.add("status", "true");
        formData97.add("comments", "");
        formData97.add("isWorkingDay", "false");
        formData97.add("isLectureAllowed", "false");

        formDataList.add(formData97);

        MultiValueMap<String, String> formData98 = new LinkedMultiValueMap<>();
        formData98.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData98.add("calendarDate", "12-12-2022 00:00:00");
        formData98.add("status", "true");
        formData98.add("comments", "");
        formData98.add("isWorkingDay", "true");
        formData98.add("isLectureAllowed", "true");

        formDataList.add(formData98);


        MultiValueMap<String, String> formData99 = new LinkedMultiValueMap<>();
        formData99.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData99.add("calendarDate", "13-12-2022 00:00:00");
        formData99.add("status", "true");
        formData99.add("comments", "");
        formData99.add("isWorkingDay", "true");
        formData99.add("isLectureAllowed", "true");

        formDataList.add(formData99);

        MultiValueMap<String, String> formData100 = new LinkedMultiValueMap<>();
        formData100.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData100.add("calendarDate", "14-12-2022 00:00:00");
        formData100.add("status", "true");
        formData100.add("comments", "");
        formData100.add("isWorkingDay", "true");
        formData100.add("isLectureAllowed", "true");

        formDataList.add(formData100);

        MultiValueMap<String, String> formData101 = new LinkedMultiValueMap<>();
        formData101.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData101.add("calendarDate", "15-12-2022 00:00:00");
        formData101.add("status", "true");
        formData101.add("comments", "");
        formData101.add("isWorkingDay", "true");
        formData101.add("isLectureAllowed", "true");

        formDataList.add(formData101);

        MultiValueMap<String, String> formData102 = new LinkedMultiValueMap<>();
        formData102.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData102.add("calendarDate", "16-12-2022 00:00:00");
        formData102.add("status", "true");
        formData102.add("comments", "Deadline for submission of Assignment or Quizzes or Sessional results");
        formData102.add("isWorkingDay", "true");
        formData102.add("isLectureAllowed", "true");

        formDataList.add(formData102);

        MultiValueMap<String, String> formData103 = new LinkedMultiValueMap<>();
        formData103.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData103.add("calendarDate", "17-12-2022 00:00:00");
        formData103.add("status", "true");
        formData103.add("comments", "");
        formData103.add("isWorkingDay", "false");
        formData103.add("isLectureAllowed", "false");

        formDataList.add(formData103);

        MultiValueMap<String, String> formData104 = new LinkedMultiValueMap<>();
        formData104.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData104.add("calendarDate", "18-12-2022 00:00:00");
        formData104.add("status", "true");
        formData104.add("comments", "");
        formData104.add("isWorkingDay", "false");
        formData104.add("isLectureAllowed", "false");

        formDataList.add(formData104);

        MultiValueMap<String, String> formData105 = new LinkedMultiValueMap<>();
        formData105.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData105.add("calendarDate", "19-12-2022 00:00:00");
        formData105.add("status", "true");
        formData105.add("comments", "");
        formData105.add("isWorkingDay", "true");
        formData105.add("isLectureAllowed", "true");

        formDataList.add(formData105);

        MultiValueMap<String, String> formData106 = new LinkedMultiValueMap<>();
        formData106.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData106.add("calendarDate", "20-12-2022 00:00:00");
        formData106.add("status", "true");
        formData106.add("comments", "");
        formData106.add("isWorkingDay", "true");
        formData106.add("isLectureAllowed", "true");

        formDataList.add(formData106);

        MultiValueMap<String, String> formData107 = new LinkedMultiValueMap<>();
        formData107.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData107.add("calendarDate", "21-12-2022 00:00:00");
        formData107.add("status", "true");
        formData107.add("comments", "");
        formData107.add("isWorkingDay", "true");
        formData107.add("isLectureAllowed", "true");

        formDataList.add(formData107);


        MultiValueMap<String, String> formData108 = new LinkedMultiValueMap<>();
        formData108.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData108.add("calendarDate", "22-12-2022 00:00:00");
        formData108.add("status", "true");
        formData108.add("comments", "");
        formData108.add("isWorkingDay", "true");
        formData108.add("isLectureAllowed", "true");

        formDataList.add(formData108);

        MultiValueMap<String, String> formData109 = new LinkedMultiValueMap<>();
        formData109.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData109.add("calendarDate", "23-12-2022 00:00:00");
        formData109.add("status", "true");
        formData109.add("comments", "Attendance lock");
        formData109.add("isWorkingDay", "true");
        formData109.add("isLectureAllowed", "true");

        formDataList.add(formData109);

        MultiValueMap<String, String> formData110 = new LinkedMultiValueMap<>();
        formData110.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData110.add("calendarDate", "24-12-2022 00:00:00");
        formData110.add("status", "true");
        formData110.add("comments", "");
        formData110.add("isWorkingDay", "false");
        formData110.add("isLectureAllowed", "false");

        formDataList.add(formData110);

        MultiValueMap<String, String> formData111 = new LinkedMultiValueMap<>();
        formData111.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData111.add("calendarDate", "25-12-2022 00:00:00");
        formData111.add("status", "true");
        formData111.add("comments", "");
        formData111.add("isWorkingDay", "false");
        formData111.add("isLectureAllowed", "false");

        formDataList.add(formData111);

        MultiValueMap<String, String> formData112 = new LinkedMultiValueMap<>();
        formData112.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData112.add("calendarDate", "26-12-2022 00:00:00");
        formData112.add("status", "true");
        formData112.add("comments", "Commencement of Practical Exam");
        formData112.add("isWorkingDay", "true");
        formData112.add("isLectureAllowed", "false");

        formDataList.add(formData112);

        MultiValueMap<String, String> formData113 = new LinkedMultiValueMap<>();
        formData113.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData113.add("calendarDate", "27-12-2022 00:00:00");
        formData113.add("status", "true");
        formData113.add("comments", "");
        formData113.add("isWorkingDay", "true");
        formData113.add("isLectureAllowed", "false");

        formDataList.add(formData113);

        MultiValueMap<String, String> formData114 = new LinkedMultiValueMap<>();
        formData114.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData114.add("calendarDate", "28-12-2022 00:00:00");
        formData114.add("status", "true");
        formData114.add("comments", "Collection of Admit Cards");
        formData114.add("isWorkingDay", "true");
        formData114.add("isLectureAllowed", "false");

        formDataList.add(formData114);

        MultiValueMap<String, String> formData115 = new LinkedMultiValueMap<>();
        formData115.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData115.add("calendarDate", "29-12-2022 00:00:00");
        formData115.add("status", "true");
        formData115.add("comments", "");
        formData115.add("isWorkingDay", "true");
        formData115.add("isLectureAllowed", "false");

        formDataList.add(formData115);

        MultiValueMap<String, String> formData116 = new LinkedMultiValueMap<>();
        formData116.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData116.add("calendarDate", "30-12-2022 00:00:00");
        formData116.add("status", "true");
        formData116.add("comments", "");
        formData116.add("isWorkingDay", "true");
        formData116.add("isLectureAllowed", "false");

        formDataList.add(formData116);

        MultiValueMap<String, String> formData117 = new LinkedMultiValueMap<>();
        formData117.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData117.add("calendarDate", "31-12-2022 00:00:00");
        formData117.add("status", "true");
        formData117.add("comments", "");
        formData117.add("isWorkingDay", "false");
        formData117.add("isLectureAllowed", "false");

        formDataList.add(formData117);

        MultiValueMap<String, String> formData118 = new LinkedMultiValueMap<>();
        formData118.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData118.add("calendarDate", "01-01-2023 00:00:00");
        formData118.add("status", "true");
        formData118.add("comments", "");
        formData118.add("isWorkingDay", "false");
        formData118.add("isLectureAllowed", "false");

        formDataList.add(formData118);

        MultiValueMap<String, String> formData119 = new LinkedMultiValueMap<>();
        formData119.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData119.add("calendarDate", "02-01-2023 00:00:00");
        formData119.add("status", "true");
        formData119.add("comments", "Theory Examination");
        formData119.add("isWorkingDay", "true");
        formData119.add("isLectureAllowed", "false");

        formDataList.add(formData119);

        MultiValueMap<String, String> formData120 = new LinkedMultiValueMap<>();
        formData120.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData120.add("calendarDate", "03-01-2023 00:00:00");
        formData120.add("status", "true");
        formData120.add("comments", "");
        formData120.add("isWorkingDay", "true");
        formData120.add("isLectureAllowed", "false");

        formDataList.add(formData120);

        MultiValueMap<String, String> formData121 = new LinkedMultiValueMap<>();
        formData121.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData121.add("calendarDate", "04-01-2023 00:00:00");
        formData121.add("status", "true");
        formData121.add("comments", "");
        formData121.add("isWorkingDay", "true");
        formData121.add("isLectureAllowed", "false");

        formDataList.add(formData121);

        MultiValueMap<String, String> formData122 = new LinkedMultiValueMap<>();
        formData122.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData122.add("calendarDate", "05-01-2023 00:00:00");
        formData122.add("status", "true");
        formData122.add("comments", "");
        formData122.add("isWorkingDay", "true");
        formData122.add("isLectureAllowed", "false");

        formDataList.add(formData122);

        MultiValueMap<String, String> formData123 = new LinkedMultiValueMap<>();
        formData123.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData123.add("calendarDate", "06-01-2023 00:00:00");
        formData123.add("status", "true");
        formData123.add("comments", "");
        formData123.add("isWorkingDay", "true");
        formData123.add("isLectureAllowed", "false");

        formDataList.add(formData123);

        MultiValueMap<String, String> formData124 = new LinkedMultiValueMap<>();
        formData124.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData124.add("calendarDate", "07-01-2023 00:00:00");
        formData124.add("status", "true");
        formData124.add("comments", "Semester Termination");
        formData124.add("isWorkingDay", "false");
        formData124.add("isLectureAllowed", "false");

        formDataList.add(formData124);

        MultiValueMap<String, String> formData125 = new LinkedMultiValueMap<>();
        formData125.add("academicCalendarUUID", "2e8851cc-e44a-4540-b38e-166a0bf990d7");
        formData125.add("calendarDate", "08-01-2023 00:00:00");
        formData125.add("status", "true");
        formData125.add("comments", "");
        formData125.add("isWorkingDay", "false");
        formData125.add("isLectureAllowed", "false");

        formDataList.add(formData125
        );


        Flux<Boolean> fluxRes = Flux.just(false);

        for (MultiValueMap<String, String> valueMap : formDataList) {
            Mono<Boolean> res = seederService
                    .seedData(academicBaseURI + "api/v1/academic-calendar-details/store", valueMap);
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