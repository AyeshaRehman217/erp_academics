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
import tuf.webscaf.app.dbContext.master.entity.StudentFatherJobHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherJobHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherJobHistoryRepository;
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

@Tag(name = "studentFatherJobHistoryHandler")
@Component
public class

StudentFatherJobHistoryHandler {

    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentFatherJobHistoryRepository studentFatherJobHistoryRepository;

    @Autowired
    SlaveStudentFatherJobHistoryRepository slaveStudentFatherJobHistoryRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Father UUID
        String studentFatherUUID = serverRequest.queryParam("studentFatherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentFatherUUID.isEmpty()) {

            Flux<SlaveStudentFatherJobHistoryEntity> slaveStudentFatherJobHistoryFlux = slaveStudentFatherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status));
            return slaveStudentFatherJobHistoryFlux
                    .collectList()
                    .flatMap(studentFatherJobHistoryEntity -> slaveStudentFatherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentFatherJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentFatherJobHistoryEntity> slaveStudentFatherJobHistoryFlux = slaveStudentFatherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentFatherJobHistoryFlux
                    .collectList()
                    .flatMap(studentFatherJobHistoryEntity -> slaveStudentFatherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentFatherJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentFatherUUID.isEmpty()) {

            Flux<SlaveStudentFatherJobHistoryEntity> slaveStudentFatherJobHistoryFlux = slaveStudentFatherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID));
            return slaveStudentFatherJobHistoryFlux
                    .collectList()
                    .flatMap(studentFatherJobHistoryEntity -> slaveStudentFatherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID))
                            .flatMap(count -> {
                                if (studentFatherJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentFatherJobHistoryEntity> slaveStudentFatherJobHistoryFlux = slaveStudentFatherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveStudentFatherJobHistoryFlux
                    .collectList()
                    .flatMap(studentFatherJobHistoryEntity -> slaveStudentFatherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentFatherJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentFatherJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentFatherJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentFatherJobHistoryUUID)
                .flatMap(stdFatherJobHistoryEntityDB -> responseSuccessMsg("Record Fetched Successfully", stdFatherJobHistoryEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    // Show Student Jobs against Student , Student Father, and Student Father Job History UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-father_student-father-job-histories_show")
    public Mono<ServerResponse> showJobHistoryForStudentFatherAndStudent(ServerRequest serverRequest) {
        UUID studentFatherJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentFatherUUID = UUID.fromString(serverRequest.queryParam("studentFatherUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentFatherJobHistoryRepository.showStudentFatherJobHistoryAgainstStudentAndStudentFather(studentUUID, studentFatherUUID, studentFatherJobHistoryUUID)
                .flatMap(studentFatherJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentFatherJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Father Job History
    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentFatherJobHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_store")
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

                    // If start date is after the end date
                    if (startDate.isAfter(endDate)) {
                        return responseInfoMsg("Start Date is after the End Date.");
                    }

                    StudentFatherJobHistoryEntity entity = StudentFatherJobHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentFatherUUID(UUID.fromString(value.getFirst("studentFatherUUID").trim()))
                            .occupation(value.getFirst("occupation").trim())
                            .designation(value.getFirst("designation").trim())
                            .organization(value.getFirst("organization").trim())
                            .income(Long.valueOf(value.getFirst("income")))
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                            .startDate(startDate)
                            .endDate(endDate)
                            .status(Boolean.valueOf(value.getFirst("status")))
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

                    //checks if student uuid exists
                    return studentFatherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentFatherUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentEntity -> studentFatherJobHistoryRepository.save(entity)
                                                    .flatMap(stdFatherJobHistoryEntityDB -> responseSuccessMsg("Record Stored Successfully.", stdFatherJobHistoryEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Father record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Father record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentFatherJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentFatherJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentFatherJobHistoryUUID)
                        .flatMap(previousFthEntity -> {
                            LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            // If start date is after the end date
                            if (startDate.isAfter(endDate)) {
                                return responseInfoMsg("Start Date is after the End Date.");
                            }

                            StudentFatherJobHistoryEntity updatedEntity = StudentFatherJobHistoryEntity.builder()
                                    .uuid(previousFthEntity.getUuid())
                                    .studentFatherUUID(previousFthEntity.getStudentFatherUUID())
                                    .occupation(value.getFirst("occupation").trim())
                                    .designation(value.getFirst("designation").trim())
                                    .organization(value.getFirst("organization").trim())
                                    .income(Long.valueOf(value.getFirst("income")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousFthEntity.getCreatedAt())
                                    .createdBy(previousFthEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousFthEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousFthEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousFthEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousFthEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousFthEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousFthEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousFthEntity.setDeletedBy(UUID.fromString(userId));
                            previousFthEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousFthEntity.setReqDeletedIP(reqIp);
                            previousFthEntity.setReqDeletedPort(reqPort);
                            previousFthEntity.setReqDeletedBrowser(reqBrowser);
                            previousFthEntity.setReqDeletedOS(reqOs);
                            previousFthEntity.setReqDeletedDevice(reqDevice);
                            previousFthEntity.setReqDeletedReferer(reqReferer);

                            //checks if currency uuid exists
                            return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", updatedEntity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(stdMth -> studentFatherJobHistoryRepository.save(previousFthEntity)
                                                    .then(studentFatherJobHistoryRepository.save(updatedEntity))
                                                    .flatMap(stdFatherJobHistoryEntityDB -> responseSuccessMsg("Record Updated Successfully", stdFatherJobHistoryEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentFatherJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    Boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return studentFatherJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentFatherJobHistoryUUID)
                            .flatMap(previousFthEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousFthEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentFatherJobHistoryEntity updatedMthEntity = StudentFatherJobHistoryEntity.builder()
                                        .uuid(previousFthEntity.getUuid())
                                        .studentFatherUUID(previousFthEntity.getStudentFatherUUID())
                                        .occupation(previousFthEntity.getOccupation())
                                        .designation(previousFthEntity.getDesignation())
                                        .organization(previousFthEntity.getOrganization())
                                        .income(previousFthEntity.getIncome())
                                        .currencyUUID(previousFthEntity.getCurrencyUUID())
                                        .startDate(previousFthEntity.getStartDate())
                                        .endDate(previousFthEntity.getEndDate())
                                        .status(status == true ? true : false)
                                        .createdAt(previousFthEntity.getCreatedAt())
                                        .createdBy(previousFthEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousFthEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousFthEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousFthEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousFthEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousFthEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousFthEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                previousFthEntity.setDeletedBy(UUID.fromString(userId));
                                previousFthEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousFthEntity.setReqDeletedIP(reqIp);
                                previousFthEntity.setReqDeletedPort(reqPort);
                                previousFthEntity.setReqDeletedBrowser(reqBrowser);
                                previousFthEntity.setReqDeletedOS(reqOs);
                                previousFthEntity.setReqDeletedDevice(reqDevice);
                                previousFthEntity.setReqDeletedReferer(reqReferer);

                                return studentFatherJobHistoryRepository.save(previousFthEntity)
                                        .then(studentFatherJobHistoryRepository.save(updatedMthEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-job-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentFatherJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentFatherJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentFatherJobHistoryUUID)
                .flatMap(stdFatherJobHistoryEntityDB -> {

                    stdFatherJobHistoryEntityDB.setDeletedBy(UUID.fromString(userId));
                    stdFatherJobHistoryEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    stdFatherJobHistoryEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    stdFatherJobHistoryEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    stdFatherJobHistoryEntityDB.setReqDeletedIP(reqIp);
                    stdFatherJobHistoryEntityDB.setReqDeletedPort(reqPort);
                    stdFatherJobHistoryEntityDB.setReqDeletedBrowser(reqBrowser);
                    stdFatherJobHistoryEntityDB.setReqDeletedOS(reqOs);
                    stdFatherJobHistoryEntityDB.setReqDeletedDevice(reqDevice);
                    stdFatherJobHistoryEntityDB.setReqDeletedReferer(reqReferer);

                    return studentFatherJobHistoryRepository.save(stdFatherJobHistoryEntityDB)
                            .flatMap(saveEntity -> responseSuccessMsg("Record Deleted Successfully.", saveEntity))
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
}
