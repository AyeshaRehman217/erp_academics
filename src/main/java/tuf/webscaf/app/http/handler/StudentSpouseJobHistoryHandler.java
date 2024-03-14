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
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseJobHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentSpouseJobHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSpouseRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSpouseJobHistoryRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentSpouseJobHistoryHandler")
@Component
public class StudentSpouseJobHistoryHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSpouseJobHistoryRepository studentSpouseJobHistoryRepository;

    @Autowired
    SlaveStudentSpouseJobHistoryRepository slaveSpouseJobHistoryRepository;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Spouse UUID
        String studentSpouseUUID = serverRequest.queryParam("studentSpouseUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentSpouseUUID.isEmpty()) {

            Flux<SlaveStudentSpouseJobHistoryEntity> slaveSpouseJobHistoryFlux = slaveSpouseJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status));

            return slaveSpouseJobHistoryFlux
                    .collectList()
                    .flatMap(studentSpouseJobHistoryEntityDB -> slaveSpouseJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSpouseJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentSpouseJobHistoryEntity> slaveSpouseJobHistoryFlux = slaveSpouseJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveSpouseJobHistoryFlux
                    .collectList()
                    .flatMap(studentSpouseJobHistoryEntityDB -> slaveSpouseJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSpouseJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentSpouseUUID.isEmpty()) {

            Flux<SlaveStudentSpouseJobHistoryEntity> slaveSpouseJobHistoryFlux = slaveSpouseJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID));

            return slaveSpouseJobHistoryFlux
                    .collectList()
                    .flatMap(studentSpouseJobHistoryEntityDB -> slaveSpouseJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID))
                            .flatMap(count -> {
                                if (studentSpouseJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentSpouseJobHistoryEntity> slaveSpouseJobHistoryFlux = slaveSpouseJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveSpouseJobHistoryFlux
                    .collectList()
                    .flatMap(studentSpouseJobHistoryEntityDB -> slaveSpouseJobHistoryRepository.countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSpouseJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSpouseJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveSpouseJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSpouseJobHistoryUUID)
                .flatMap(studentSpouseJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentSpouseJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }


    // Show Student Jobs against Student , Student Spouse, and Student Spouse Job History UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-spouse_student-spouse-job-histories_show")
    public Mono<ServerResponse> showJobHistoryForStudentSpouseAndStudent(ServerRequest serverRequest) {
        UUID studentSpouseJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentSpouseUUID = UUID.fromString(serverRequest.queryParam("studentSpouseUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveSpouseJobHistoryRepository.showStudentSpouseJobHistoryAgainstStudentAndStudentSpouse(studentUUID, studentSpouseUUID, studentSpouseJobHistoryUUID)
                .flatMap(studentSpouseJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentSpouseJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Spouse Job History
    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSpouseJobHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_store")
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

                    LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                    LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                    int date_difference = startDate.compareTo(endDate);
                    // If start date is after the end date
                    if (date_difference > 0) {
                        return responseInfoMsg("Start Date is after the End Date");
                    }
                    StudentSpouseJobHistoryEntity entity = StudentSpouseJobHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .occupation(value.getFirst("occupation").trim())
                            .designation(value.getFirst("designation").trim())
                            .organization(value.getFirst("organization").trim())
                            .income(Long.valueOf(value.getFirst("income")))
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                            .startDate(startDate)
                            .endDate(endDate)
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .studentSpouseUUID(UUID.fromString(value.getFirst("studentSpouseUUID").trim()))
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

                    //checks if student spouse uuid exists
                    return studentSpouseRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSpouseUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(currencyUUID -> studentSpouseJobHistoryRepository.save(entity)
                                                    .flatMap(studentSpouseJobHistoryEntity -> responseSuccessMsg("Record Stored Successfully", studentSpouseJobHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student Spouse  does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Spouse  does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).onErrorResume(err -> responseErrorMsg("Student Spouse record does not exist"))
                            .switchIfEmpty(responseInfoMsg("Student Spouse record does not exist. Please contact developer."));
                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSpouseJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentSpouseJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSpouseJobHistoryUUID)
                        .flatMap(entity -> {
                            LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            int date_difference = startDate.compareTo(endDate);
                            // If start date is after the end date
                            if (date_difference > 0) {
                                return responseInfoMsg("Start Date is after the End Date");
                            }

                            StudentSpouseJobHistoryEntity updatedEntity = StudentSpouseJobHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentSpouseUUID(entity.getStudentSpouseUUID())
                                    .occupation(value.getFirst("occupation").trim())
                                    .designation(value.getFirst("designation").trim())
                                    .organization(value.getFirst("organization").trim())
                                    .income(Long.valueOf(value.getFirst("income")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
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

                            //checks if currency uuid exists
                            return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", updatedEntity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(currencyUUID -> studentSpouseJobHistoryRepository.save(entity)
                                                    .then(studentSpouseJobHistoryRepository.save(updatedEntity))
                                                    .flatMap(studentSpouseJobHistoryEntity -> responseSuccessMsg("Record Updated Successfully", studentSpouseJobHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student Spouse  does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Spouse  does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSpouseJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentSpouseJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSpouseJobHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSpouseJobHistoryEntity updatedMthEntity = StudentSpouseJobHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentSpouseUUID(val.getStudentSpouseUUID())
                                        .occupation(val.getOccupation())
                                        .designation(val.getDesignation())
                                        .organization(val.getOrganization())
                                        .income(val.getIncome())
                                        .currencyUUID(val.getCurrencyUUID())
                                        .startDate(val.getStartDate())
                                        .endDate(val.getEndDate())
                                        .status(status == true ? true : false)
                                        .createdAt(val.getCreatedAt())
                                        .createdBy(val.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
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

                                val.setDeletedBy(UUID.fromString(userId));
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return studentSpouseJobHistoryRepository.save(val)
                                        .then(studentSpouseJobHistoryRepository.save(updatedMthEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-job-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSpouseJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSpouseJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSpouseJobHistoryUUID)
                .flatMap(studentSpouseJobHistoryEntity -> {

                    studentSpouseJobHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    studentSpouseJobHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentSpouseJobHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentSpouseJobHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentSpouseJobHistoryEntity.setReqDeletedIP(reqIp);
                    studentSpouseJobHistoryEntity.setReqDeletedPort(reqPort);
                    studentSpouseJobHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    studentSpouseJobHistoryEntity.setReqDeletedOS(reqOs);
                    studentSpouseJobHistoryEntity.setReqDeletedDevice(reqDevice);
                    studentSpouseJobHistoryEntity.setReqDeletedReferer(reqReferer);

                    return studentSpouseJobHistoryRepository.save(studentSpouseJobHistoryEntity)
                            .flatMap(saveEntity -> responseSuccessMsg("Record Deleted Successfully", saveEntity))
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
