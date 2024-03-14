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
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianAcademicHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.DegreeRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianAcademicHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianDocumentRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianAcademicHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianAcademicHistoryRepository;
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

@Tag(name = "studentGuardianAcademicHistoryHandler")
@Component
public class StudentGuardianAcademicHistoryHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGuardianAcademicHistoryRepository studentGuardianAcademicHistoryRepository;

    @Autowired
    SlaveStudentGuardianAcademicHistoryRepository slaveStudentGuardianAcademicHistoryRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    StudentGuardianDocumentRepository studentGuardianDocumentRepository;

    @Autowired
    DegreeRepository degreeRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Guardian UUID
        String studentGuardianUUID = serverRequest.queryParam("studentGuardianUUID").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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

        if (!status.isEmpty() && !studentGuardianUUID.isEmpty()) {

            Flux<SlaveStudentGuardianAcademicHistoryEntity> slaveStudentGuardianAcademicHistoryFlux = slaveStudentGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status));
            return slaveStudentGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianAcademicHistoryEntity -> slaveStudentGuardianAcademicHistoryRepository
                            .countByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentGuardianAcademicHistoryEntity> slaveStudentGuardianAcademicHistoryFlux = slaveStudentGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveStudentGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianAcademicHistoryEntity -> slaveStudentGuardianAcademicHistoryRepository
                            .countByGradeContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));

        } else if (!studentGuardianUUID.isEmpty()) {

            Flux<SlaveStudentGuardianAcademicHistoryEntity> slaveStudentGuardianAcademicHistoryFlux = slaveStudentGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID));
            return slaveStudentGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianAcademicHistoryEntity -> slaveStudentGuardianAcademicHistoryRepository
                            .countByGradeContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID))
                            .flatMap(count -> {
                                if (studentGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentGuardianAcademicHistoryEntity> slaveStudentGuardianAcademicHistoryFlux = slaveStudentGuardianAcademicHistoryRepository
                    .findAllByGradeContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveStudentGuardianAcademicHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianAcademicHistoryEntity -> slaveStudentGuardianAcademicHistoryRepository.countByGradeContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (studentGuardianAcademicHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAcademicHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianAcademicHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianAcademicHistoryUUID)
                .flatMap(academicSessionEntity -> responseSuccessMsg("Record Fetched Successfully", academicSessionEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists in Student Guardian history
    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianAcademicHistoryRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists in Student Guardian history
    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianAcademicHistoryRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists in Student Guardian history
    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianAcademicHistoryRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_store")
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
                .flatMap(value -> {

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

                    StudentGuardianAcademicHistoryEntity entity = StudentGuardianAcademicHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .studentGuardianUUID(UUID.fromString(value.getFirst("studentGuardianUUID").trim()))
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
                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                            .createdBy(UUID.fromString(userId))
                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                            .reqCreatedIP(reqIp)
                            .reqCreatedPort(reqPort)
                            .reqCreatedBrowser(reqBrowser)
                            .reqCreatedOS(reqOs)
                            .reqCreatedDevice(reqDevice)
                            .reqCreatedReferer(reqReferer)
                            .build();

                    //checks if student guardian academic history record already exists
                    return studentGuardianAcademicHistoryRepository
                            .findFirstByDegreeUUIDAndStudentGuardianUUIDAndDeletedAtIsNull(entity.getDegreeUUID(), entity.getStudentGuardianUUID())
                            .flatMap(recordAlreadyExists -> responseInfoMsg("Record already exist with same degree"))
                            //checks if student guardian  uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getStudentGuardianUUID())
                                    .flatMap(studentGuardianEntity -> degreeRepository.findByUuidAndDeletedAtIsNull(entity.getDegreeUUID())
                                            //checks city uuid exists
                                            .flatMap(studentGuardianDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                            //checks state uuid exists
                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                            .flatMap(countryJsonNode -> studentGuardianAcademicHistoryRepository.save(entity)
                                                                                                    .flatMap(studentGuardianAcademicHistoryEntity -> responseSuccessMsg("Record Stored Successfully", getResponseEntity(studentGuardianAcademicHistoryEntity, encodedGrade)))
                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer.")))
                                                                                    ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer.")))
                                                            ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer.")))
                                            ).switchIfEmpty(responseInfoMsg("Degree record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Degree record  does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Student Guardian  record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student Guardian  record  does not exist. Please contact developer"))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    // This method is used to return response entity with encoded value of grade
    public StudentGuardianAcademicHistoryEntity getResponseEntity(StudentGuardianAcademicHistoryEntity entity, String encodedGrade) {
        // set the encoded value
        entity.setGrade(encodedGrade);
        return entity;
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGuardianAcademicHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianAcademicHistoryUUID)
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


                            StudentGuardianAcademicHistoryEntity updatedEntity = StudentGuardianAcademicHistoryEntity.builder()
                                    .uuid(UUID.randomUUID())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .studentGuardianUUID(entity.getStudentGuardianUUID())
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

                            //checks if student guardian academic history record already exists
                            return studentGuardianAcademicHistoryRepository
                                    .findFirstByDegreeUUIDAndStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getDegreeUUID(), updatedEntity.getStudentGuardianUUID(), studentGuardianAcademicHistoryUUID)
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record already exist with same degree"))
                                    //checks if student guardian uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentGuardianUUID())
                                            .flatMap(studentMotherEntity -> degreeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDegreeUUID())
                                                    //checks city uuid exists
                                                    .flatMap(studentGuardianDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                    //checks state uuid exists
                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                    //checks state uuid exists
                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                    .flatMap(countryJsonNode -> studentGuardianAcademicHistoryRepository.save(entity)
                                                                                                            .then(studentGuardianAcademicHistoryRepository.save(updatedEntity))
                                                                                                            .flatMap(studentGuardianAcademicHistoryEntity -> responseSuccessMsg("Record Updated Successfully", getResponseEntity(studentGuardianAcademicHistoryEntity, encodedGrade)))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong Please try again."))
                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer.")))
                                                                                    ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer.")))
                                                                    ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer.")))
                                                    ).switchIfEmpty(responseInfoMsg("Degree record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Degree record  does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Student Guardian  record does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Student Guardian  record does not exist. Please contact developer"))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist")))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGuardianAcademicHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianAcademicHistoryUUID)
                            .flatMap(previousStdGuardEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousStdGuardEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGuardianAcademicHistoryEntity stdGrdAcademicHistoryEntity = StudentGuardianAcademicHistoryEntity.builder()
                                        .uuid(previousStdGuardEntity.getUuid())
                                        .studentGuardianUUID(previousStdGuardEntity.getStudentGuardianUUID())
                                        .degreeUUID(previousStdGuardEntity.getDegreeUUID())
                                        .totalMarks(previousStdGuardEntity.getTotalMarks())
                                        .obtainedMarks(previousStdGuardEntity.getObtainedMarks())
                                        .isCgpa(previousStdGuardEntity.getIsCgpa())
                                        .totalCgpa(previousStdGuardEntity.getTotalCgpa())
                                        .obtainedCgpa(previousStdGuardEntity.getObtainedCgpa())
                                        .percentage(previousStdGuardEntity.getPercentage())
                                        .grade(previousStdGuardEntity.getGrade())
                                        .startDate(previousStdGuardEntity.getStartDate())
                                        .endDate(previousStdGuardEntity.getEndDate())
                                        .passOutYear(previousStdGuardEntity.getPassOutYear())
                                        .countryUUID(previousStdGuardEntity.getCountryUUID())
                                        .stateUUID(previousStdGuardEntity.getStateUUID())
                                        .cityUUID(previousStdGuardEntity.getCityUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(previousStdGuardEntity.getCreatedAt())
                                        .createdBy(previousStdGuardEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousStdGuardEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousStdGuardEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousStdGuardEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousStdGuardEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousStdGuardEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousStdGuardEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousStdGuardEntity.setDeletedBy(UUID.fromString(userId));
                                previousStdGuardEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousStdGuardEntity.setReqDeletedIP(reqIp);
                                previousStdGuardEntity.setReqDeletedPort(reqPort);
                                previousStdGuardEntity.setReqDeletedBrowser(reqBrowser);
                                previousStdGuardEntity.setReqDeletedOS(reqOs);
                                previousStdGuardEntity.setReqDeletedDevice(reqDevice);
                                previousStdGuardEntity.setReqDeletedReferer(reqReferer);

                                return studentGuardianAcademicHistoryRepository.save(previousStdGuardEntity)
                                        .then(studentGuardianAcademicHistoryRepository.save(stdGrdAcademicHistoryEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-academic-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianAcademicHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGuardianAcademicHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianAcademicHistoryUUID)
                .flatMap(studentMotherAcademicHistoryEntity -> {

                    studentMotherAcademicHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    studentMotherAcademicHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentMotherAcademicHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentMotherAcademicHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentMotherAcademicHistoryEntity.setReqDeletedIP(reqIp);
                    studentMotherAcademicHistoryEntity.setReqDeletedPort(reqPort);
                    studentMotherAcademicHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    studentMotherAcademicHistoryEntity.setReqDeletedOS(reqOs);
                    studentMotherAcademicHistoryEntity.setReqDeletedDevice(reqDevice);
                    studentMotherAcademicHistoryEntity.setReqDeletedReferer(reqReferer);

                    return studentGuardianAcademicHistoryRepository.save(studentMotherAcademicHistoryEntity)
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
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg
                )
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
