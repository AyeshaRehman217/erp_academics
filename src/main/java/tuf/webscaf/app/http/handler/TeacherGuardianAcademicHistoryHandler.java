package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianAcademicHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.DegreeRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianAcademicHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianAcademicHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianAcademicHistoryRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherGuardianAcademicHistoryHandler")
@Component
public class TeacherGuardianAcademicHistoryHandler {

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianAcademicHistoryRepository teacherGuardianAcademicHistoryRepository;

    @Autowired
    SlaveTeacherGuardianAcademicHistoryRepository slaveTeacherGuardianAcademicHistoryRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    DegreeRepository degreeRepository;

    @Autowired
    ApiCallService apiCallService;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Guardian UUID
        String teacherGuardianUUID = serverRequest.queryParam("teacherGuardianUUID").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
        Sort.Direction direction;
        switch (d.toLowerCase()) {
            case "asc":
                direction = Sort.Direction.ASC;
                break;
            case "desc":
                direction = Sort.Direction.DESC;
                break;
            default:
                direction = Sort.Direction.ASC;
        }

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));


        if (!status.isEmpty() && !teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianAcademicHistoryEntity> slaveTeacherGuardianAcademicHistoryFlux = slaveTeacherGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status));
            return slaveTeacherGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianAcademicHistoryEntity -> slaveTeacherGuardianAcademicHistoryRepository
                            .countByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherGuardianAcademicHistoryEntity> slaveTeacherGuardianAcademicHistoryFlux = slaveTeacherGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianAcademicHistoryEntity -> slaveTeacherGuardianAcademicHistoryRepository
                            .countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));

        } else if (!teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianAcademicHistoryEntity> slaveTeacherGuardianAcademicHistoryFlux = slaveTeacherGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID));
            return slaveTeacherGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianAcademicHistoryEntity -> slaveTeacherGuardianAcademicHistoryRepository
                            .countByGradeContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID))
                            .flatMap(count -> {
                                if (teacherGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherGuardianAcademicHistoryEntity> slaveTeacherGuardianAcademicHistoryFlux = slaveTeacherGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveTeacherGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(teacherGuardianAcademicHistoryEntity -> slaveTeacherGuardianAcademicHistoryRepository.countByGradeContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (teacherGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherGuardianAcademicHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAcademicHistoryUUID)
                .flatMap(teacherGuardianAcademicHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", teacherGuardianAcademicHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherGuardianAcademicHistoryRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherGuardianAcademicHistoryRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherGuardianAcademicHistoryRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                //checks if teacher guardian uuid exists
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                        .flatMap(teacherGuardianEntity -> {

                            // get encoded value of grade from request
                            String encodedGrade = value.getFirst("grade");

                            String decodedGrade = "";

                            // decode the value of grade
                            try {
                                decodedGrade = URLDecoder.decode(encodedGrade, "UTF-8");
                            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                                e.printStackTrace();
                            }

                            // if teacher guardian uuid is already set
                            if (teacherGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Academic History. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
                                LocalDateTime startDate = null;
                                if ((value.containsKey("startDate") && (value.getFirst("startDate") != ""))) {
                                    startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                                }

                                LocalDateTime endDate = null;
                                if ((value.containsKey("endDate") && (value.getFirst("endDate") != ""))) {
                                    endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                                }

                                LocalDateTime passOutYear = null;
                                if ((value.containsKey("passOutYear") && (value.getFirst("passOutYear") != ""))) {
                                    passOutYear = LocalDateTime.parse((value.getFirst("passOutYear")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                                }

                                Integer totalMarks = null;
                                if ((value.containsKey("totalMarks") && (value.getFirst("totalMarks") != ""))) {
                                    totalMarks = Integer.parseInt(value.getFirst("totalMarks"));
                                }

                                Integer obtainedMarks = null;
                                if ((value.containsKey("obtainedMarks") && (value.getFirst("obtainedMarks") != ""))) {
                                    obtainedMarks = Integer.parseInt(value.getFirst("obtainedMarks"));
                                }


                                Float percentage = null;
                                if ((value.containsKey("percentage") && (value.getFirst("percentage") != ""))) {
                                    percentage = Float.parseFloat(value.getFirst("percentage"));
                                }

                                Float totalCgpa = null;
                                if ((value.containsKey("totalCgpa") && (value.getFirst("totalCgpa") != ""))) {
                                    totalCgpa = Float.parseFloat(value.getFirst("totalCgpa"));
                                }

                                Float obtainedCgpa = null;
                                if ((value.containsKey("obtainedCgpa") && (value.getFirst("obtainedCgpa") != ""))) {
                                    obtainedCgpa = Float.parseFloat(value.getFirst("obtainedCgpa"));
                                }

                                if (startDate != null && endDate != null) {
                                    // If start date is after the end date
                                    if (startDate.isAfter(endDate)) {
                                        return responseInfoMsg("Start Date is after the End Date.");
                                    }
                                }

                                if (startDate != null && passOutYear != null) {
                                    // If passOutYear is before the start date
                                    if ((passOutYear.getYear()) < startDate.getYear()) {
                                        return responseInfoMsg("Pass Out Year is before the Starting year.");
                                    }
                                }

                                // when isCgpa is true
                                if (Boolean.parseBoolean(value.getFirst("isCgpa"))) {

                                    // if any of total or obtained marks are given
                                    if (totalMarks != null || obtainedMarks != null) {
                                        return responseInfoMsg("Unable to enter marks as CGPA is selected");
                                    }
                                    // if only total cgpa is given
                                    else if (totalCgpa != null && obtainedCgpa == null) {
                                        return responseInfoMsg("Enter Obtained CGPA");
                                    }

                                    // if only obtained cgpa is given
                                    else if (totalCgpa == null && obtainedCgpa != null) {
                                        return responseInfoMsg("Enter Total CGPA");
                                    }

                                    // if both total and obtained cgpa is given
                                    else if (totalCgpa != null && obtainedCgpa != null) {
                                        // If obtained cgpa is greater than total cgpa
                                        if (obtainedCgpa > totalCgpa) {
                                            return responseInfoMsg("Obtained cgpa is greater than total cgpa");
                                        }
                                    }
                                    // if none of total or obtained cgpa are given
                                    else {
                                        return responseInfoMsg("Academic record must have total and obtained CGPA");
                                    }
                                }


                                // when isCgpa is false
                                else {

                                    // if any of total and obtained cgp is given
                                    if (obtainedCgpa != null || totalCgpa != null) {
                                        return responseInfoMsg("Unable to enter CGPA as CGPA is not selected");
                                    }

                                    // if total marks are provided but obtained marks are not given
                                    else if (totalMarks != null && obtainedMarks == null) {
                                        return responseInfoMsg("Enter Obtained Marks");
                                    }

                                    // if obtained marks are provided but total marks are not given
                                    else if (totalMarks == null && obtainedMarks != null) {
                                        return responseInfoMsg("Enter Total Marks");
                                    }

                                    // if both total are obtained marks are given
                                    else {
                                        if (totalMarks != null && obtainedMarks != null) {
                                            if (obtainedMarks > totalMarks) {
                                                return responseInfoMsg("Obtained marks is greater than total marks");
                                            }
                                        }
                                    }
                                }

                                if (percentage != null) {
                                    // If percentage is greater than 100
                                    if (percentage > 100) {
                                        return responseInfoMsg("Percentage can't be grater than 100%");
                                    }
                                }

                                TeacherGuardianAcademicHistoryEntity entity = TeacherGuardianAcademicHistoryEntity.builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherGuardianUUID(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                                        .degreeUUID(UUID.fromString(value.getFirst("degreeUUID").trim()))
                                        .totalMarks(totalMarks)
                                        .obtainedMarks(obtainedMarks)
                                        .isCgpa(Boolean.parseBoolean(value.getFirst("isCgpa")))
                                        .totalCgpa(totalCgpa)
                                        .obtainedCgpa(obtainedCgpa)
                                        .percentage(percentage)
                                        .grade(decodedGrade)
                                        .startDate(startDate)
                                        .endDate(endDate)
                                        .passOutYear(passOutYear)
                                        .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                        .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                        .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                        .status(Boolean.valueOf(value.getFirst("status")))
                                        .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .createdBy(UUID.fromString(userId))
                                        .build();

                                return teacherGuardianAcademicHistoryRepository.findFirstByDegreeUUIDAndTeacherGuardianUUIDAndDeletedAtIsNull(entity.getDegreeUUID(), entity.getTeacherGuardianUUID())
                                        .flatMap(teacherGuardianAcademicHistoryEntity -> responseInfoMsg("Record already exist with same degree"))
                                        //checks if degree uuid exists
                                        .switchIfEmpty(Mono.defer(() -> degreeRepository.findByUuidAndDeletedAtIsNull(entity.getDegreeUUID())
                                                //checks city uuid exists
                                                .flatMap(degreeEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                        .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                //checks state uuid exists
                                                                .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                        .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                //checks state uuid exists
                                                                                .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                        .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                .flatMap(countryJsonNode -> teacherGuardianAcademicHistoryRepository.save(entity)
                                                                                                        .flatMap(teacherGuardianAcademicHistoryEntity -> responseSuccessMsg("Record Stored Successfully", getResponseEntity(teacherGuardianAcademicHistoryEntity, encodedGrade)))
                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                        ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                        .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                        .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                        ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                        .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                ).switchIfEmpty(responseInfoMsg("Degree does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Degree does not exist. Please contact developer."))
                                        ));
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Guardian Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    // This method is used to return response entity with encoded value of grade
    public TeacherGuardianAcademicHistoryEntity getResponseEntity(TeacherGuardianAcademicHistoryEntity entity, String encodedGrade) {
        // set the encoded value
        entity.setGrade(encodedGrade);
        return entity;
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherGuardianAcademicHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> teacherGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAcademicHistoryUUID)
                        .flatMap(entity -> {

                            // get encoded value of grade from request
                            String encodedGrade = value.getFirst("grade");

                            String decodedGrade = "";

                            // decode the value of grade
                            try {
                                decodedGrade = URLDecoder.decode(encodedGrade, "UTF-8");
                            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                                e.printStackTrace();
                            }

                            LocalDateTime startDate = null;
                            if ((value.containsKey("startDate") && (value.getFirst("startDate") != ""))) {
                                startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            }

                            LocalDateTime endDate = null;
                            if ((value.containsKey("endDate") && (value.getFirst("endDate") != ""))) {
                                endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            }

                            LocalDateTime passOutYear = null;
                            if ((value.containsKey("passOutYear") && (value.getFirst("passOutYear") != ""))) {
                                passOutYear = LocalDateTime.parse((value.getFirst("passOutYear")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            }

                            Integer totalMarks = null;
                            if ((value.containsKey("totalMarks") && (value.getFirst("totalMarks") != ""))) {
                                totalMarks = Integer.parseInt(value.getFirst("totalMarks"));
                            }

                            Integer obtainedMarks = null;
                            if ((value.containsKey("obtainedMarks") && (value.getFirst("obtainedMarks") != ""))) {
                                obtainedMarks = Integer.parseInt(value.getFirst("obtainedMarks"));
                            }


                            Float percentage = null;
                            if ((value.containsKey("percentage") && (value.getFirst("percentage") != ""))) {
                                percentage = Float.parseFloat(value.getFirst("percentage"));
                            }

                            Float totalCgpa = null;
                            if ((value.containsKey("totalCgpa") && (value.getFirst("totalCgpa") != ""))) {
                                totalCgpa = Float.parseFloat(value.getFirst("totalCgpa"));
                            }

                            Float obtainedCgpa = null;
                            if ((value.containsKey("obtainedCgpa") && (value.getFirst("obtainedCgpa") != ""))) {
                                obtainedCgpa = Float.parseFloat(value.getFirst("obtainedCgpa"));
                            }

                            if (startDate != null && endDate != null) {
                                // If start date is after the end date
                                if (startDate.isAfter(endDate)) {
                                    return responseInfoMsg("Start Date is after the End Date.");
                                }
                            }

                            if (startDate != null && passOutYear != null) {
                                // If passOutYear is before the start date
                                if ((passOutYear.getYear()) < startDate.getYear()) {
                                    return responseInfoMsg("Pass Out Year is before the Starting year.");
                                }
                            }

                            // when isCgpa is true
                            if (Boolean.parseBoolean(value.getFirst("isCgpa"))) {

                                // if any of total or obtained marks are given
                                if (totalMarks != null || obtainedMarks != null) {
                                    return responseInfoMsg("Unable to enter marks as CGPA is selected");
                                }
                                // if only total cgpa is given
                                else if (totalCgpa != null && obtainedCgpa == null) {
                                    return responseInfoMsg("Enter Obtained CGPA");
                                }

                                // if only obtained cgpa is given
                                else if (totalCgpa == null && obtainedCgpa != null) {
                                    return responseInfoMsg("Enter Total CGPA");
                                }

                                // if both total and obtained cgpa is given
                                else if (totalCgpa != null && obtainedCgpa != null) {
                                    // If obtained cgpa is greater than total cgpa
                                    if (obtainedCgpa > totalCgpa) {
                                        return responseInfoMsg("Obtained cgpa is greater than total cgpa");
                                    }
                                }
                                // if none of total or obtained cgpa are given
                                else {
                                    return responseInfoMsg("Academic record must have total and obtained CGPA");
                                }
                            }


                            // when isCgpa is false
                            else {

                                // if any of total and obtained cgp is given
                                if (obtainedCgpa != null || totalCgpa != null) {
                                    return responseInfoMsg("Unable to enter CGPA as CGPA is not selected");
                                }

                                // if total marks are provided but obtained marks are not given
                                else if (totalMarks != null && obtainedMarks == null) {
                                    return responseInfoMsg("Enter Obtained Marks");
                                }

                                // if obtained marks are provided but total marks are not given
                                else if (totalMarks == null && obtainedMarks != null) {
                                    return responseInfoMsg("Enter Total Marks");
                                }

                                // if both total are obtained marks are given
                                else {
                                    if (totalMarks != null && obtainedMarks != null) {
                                        if (obtainedMarks > totalMarks) {
                                            return responseInfoMsg("Obtained marks is greater than total marks");
                                        }
                                    }
                                }
                            }

                            if (percentage != null) {
                                // If percentage is greater than 100
                                if (percentage > 100) {
                                    return responseInfoMsg("Percentage can't be grater than 100%");
                                }
                            }

                            TeacherGuardianAcademicHistoryEntity updatedEntity = TeacherGuardianAcademicHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherGuardianUUID(entity.getTeacherGuardianUUID())
                                    .degreeUUID(UUID.fromString(value.getFirst("degreeUUID").trim()))
                                    .totalMarks(totalMarks)
                                    .obtainedMarks(obtainedMarks)
                                    .isCgpa(Boolean.parseBoolean(value.getFirst("isCgpa")))
                                    .totalCgpa(totalCgpa)
                                    .obtainedCgpa(obtainedCgpa)
                                    .percentage(percentage)
                                    .grade(decodedGrade)
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .passOutYear(passOutYear)
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                    .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                    .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(entity.getReqCreatedIP())
                                    .reqCreatedPort(entity.getReqCreatedPort())
                                    .reqCreatedBrowser(entity.getReqCreatedBrowser())
                                    .reqCreatedOS(entity.getReqCreatedOS())
                                    .reqCreatedDevice(entity.getReqCreatedDevice())
                                    .reqCreatedReferer(entity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            return teacherGuardianAcademicHistoryRepository
                                    .findFirstByDegreeUUIDAndTeacherGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getDegreeUUID(), updatedEntity.getTeacherGuardianUUID(), teacherGuardianAcademicHistoryUUID)
                                    .flatMap(nameExists -> responseInfoMsg("Record already exist with same degree"))
                                    //checks if degree uuid exists
                                    .switchIfEmpty(Mono.defer(() -> degreeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDegreeUUID())
                                            //checks city uuid exists
                                            .flatMap(degreeEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                            //checks state uuid exists
                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                            .flatMap(countryJsonNode -> teacherGuardianAcademicHistoryRepository.save(entity)
                                                                                                    .then(teacherGuardianAcademicHistoryRepository.save(updatedEntity))
                                                                                                    .flatMap(teacherGuardianAcademicHistoryEntity -> responseSuccessMsg("Record Stored Successfully", getResponseEntity(teacherGuardianAcademicHistoryEntity, encodedGrade)))
                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong Please try again."))
                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer."))
                                                                                            ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                    ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Degree does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Degree does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherGuardianAcademicHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }
        return serverRequest.formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return teacherGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAcademicHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherGuardianAcademicHistoryEntity entity = TeacherGuardianAcademicHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .teacherGuardianUUID(val.getTeacherGuardianUUID())
                                        .degreeUUID(val.getDegreeUUID())
                                        .totalMarks(val.getTotalMarks())
                                        .obtainedMarks(val.getObtainedMarks())
                                        .isCgpa(val.getIsCgpa())
                                        .totalCgpa(val.getTotalCgpa())
                                        .obtainedCgpa(val.getObtainedCgpa())
                                        .percentage(val.getPercentage())
                                        .grade(val.getGrade())
                                        .startDate(val.getStartDate())
                                        .endDate(val.getEndDate())
                                        .passOutYear(val.getPassOutYear())
                                        .countryUUID(val.getCountryUUID())
                                        .stateUUID(val.getStateUUID())
                                        .cityUUID(val.getCityUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(val.getCreatedAt())
                                        .createdBy(val.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(val.getReqCreatedIP())
                                        .reqCreatedPort(val.getReqCreatedPort())
                                        .reqCreatedBrowser(val.getReqCreatedBrowser())
                                        .reqCreatedOS(val.getReqCreatedOS())
                                        .reqCreatedDevice(val.getReqCreatedDevice())
                                        .reqCreatedReferer(val.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                val.setDeletedBy(UUID.fromString(userId));
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return teacherGuardianAcademicHistoryRepository.save(val)
                                        .then(teacherGuardianAcademicHistoryRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-academic-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianAcademicHistoryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return teacherGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAcademicHistoryUUID)
                .flatMap(teacherGuardianAcademicHistoryEntity -> {
                    teacherGuardianAcademicHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    teacherGuardianAcademicHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherGuardianAcademicHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherGuardianAcademicHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherGuardianAcademicHistoryEntity.setReqDeletedIP(reqIp);
                    teacherGuardianAcademicHistoryEntity.setReqDeletedPort(reqPort);
                    teacherGuardianAcademicHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    teacherGuardianAcademicHistoryEntity.setReqDeletedOS(reqOs);
                    teacherGuardianAcademicHistoryEntity.setReqDeletedDevice(reqDevice);
                    teacherGuardianAcademicHistoryEntity.setReqDeletedReferer(reqReferer);

                    return teacherGuardianAcademicHistoryRepository.save(teacherGuardianAcademicHistoryEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    public Mono<ServerResponse> responseInfoMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );


        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()

        );
    }

    public Mono<ServerResponse> responseIndexInfoMsg(String msg, Long totalDataRowsWithFilter) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.INFO,
                        msg
                )
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                totalDataRowsWithFilter,
                0L,
                messages,
                Mono.empty()

        );
    }


    public Mono<ServerResponse> responseErrorMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.ERROR,
                        msg
                )
        );

        return appresponse.set(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()
        );
    }

    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        msg)
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseIndexSuccessMsg(String msg, Object entity, Long totalDataRowsWithFilter) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.SUCCESS,
                        msg)
        );

        return appresponse.set(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                null,
                "eng",
                "token",
                totalDataRowsWithFilter,
                0L,
                messages,
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg)
        );


        return appresponse.set(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.name(),
                null,
                "eng",
                "token",
                0L,
                0L,
                messages,
                Mono.empty()
        );
    }
}
