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
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianJobHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianJobHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianJobHistoryRepository;
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

@Tag(name = "studentGuardianJobHistoryHandler")
@Component
public class StudentGuardianJobHistoryHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;
    
    @Autowired
    StudentGuardianJobHistoryRepository studentGuardianJobHistoryRepository;
    
    @Autowired
    SlaveStudentGuardianJobHistoryRepository slaveStudentGuardianJobHistoryRepository;
    
    @Autowired
    StudentGuardianRepository studentGuardianRepository;
    
    @Autowired
    ApiCallService apiCallService;
    
    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

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

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Guardian UUID
        String studentGuardianUUID = serverRequest.queryParam("studentGuardianUUID").map(String::toString).orElse("").trim();

        if (!status.isEmpty() && !studentGuardianUUID.isEmpty()) {
            Flux<SlaveStudentGuardianJobHistoryEntity> slaveStudentGuardianJobHistoryFlux = slaveStudentGuardianJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status));

            return slaveStudentGuardianJobHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianJobHistoryEntity -> slaveStudentGuardianJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentGuardianJobHistoryEntity> slaveStudentGuardianJobHistoryFlux = slaveStudentGuardianJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentGuardianJobHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianJobHistoryEntityD -> slaveStudentGuardianJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianJobHistoryEntityD.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianJobHistoryEntityD, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentGuardianUUID.isEmpty()) {
            Flux<SlaveStudentGuardianJobHistoryEntity> slaveStudentGuardianJobHistoryFlux = slaveStudentGuardianJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID));

            return slaveStudentGuardianJobHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianJobHistoryEntity -> slaveStudentGuardianJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID))
                            .flatMap(count -> {
                                if (studentGuardianJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentGuardianJobHistoryEntity> slaveStudentGuardianJobHistoryFlux = slaveStudentGuardianJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveStudentGuardianJobHistoryFlux
                    .collectList()
                    .flatMap(studentGuardianJobHistoryEntity -> slaveStudentGuardianJobHistoryRepository.countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentGuardianJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentGuardianJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianJobHistoryUUID)
                .flatMap(studentGuardianJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Guardian Job History
    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianJobHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }


    // Show Student Jobs against Student , Student Guardian, and Student Guardian Job History UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-guardian_student-guardian-job-histories_show")
    public Mono<ServerResponse> showJobHistoryForStudentGuardianAndStudent(ServerRequest serverRequest) {
        UUID studentGuardianJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentGuardianUUID = UUID.fromString(serverRequest.queryParam("studentGuardianUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentGuardianJobHistoryRepository.showStudentGuardianJobHistoryAgainstStudentAndStudentGuardian(studentUUID, studentGuardianUUID, studentGuardianJobHistoryUUID)
                .flatMap(studentGuardianJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_store")
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

                    StudentGuardianJobHistoryEntity entity = StudentGuardianJobHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentGuardianUUID(UUID.fromString(value.getFirst("studentGuardianUUID")))
                            .occupation(value.getFirst("occupation").trim())
                            .designation(value.getFirst("designation").trim())
                            .organization(value.getFirst("organization").trim())
                            .income(Long.valueOf(value.getFirst("income").trim()))
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

                    //checks if student guardian uuid exists
                    return studentGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getStudentGuardianUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentGuardianEntity -> studentGuardianJobHistoryRepository.save(entity)
                                                    .flatMap(studentGuardianJobHistoryEntity -> responseSuccessMsg("Record Stored Successfully", studentGuardianJobHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Guardian does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Guardian does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGuardianJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentGuardianJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianJobHistoryUUID)
                        .flatMap(entity -> {

                            LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            // If start date is after the end date
                            if (startDate.isAfter(endDate)) {
                                return responseInfoMsg("Start Date is after the End Date.");
                            }

                            StudentGuardianJobHistoryEntity updatedEntity = StudentGuardianJobHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentGuardianUUID(entity.getStudentGuardianUUID())
                                    .occupation(value.getFirst("occupation").trim())
                                    .designation(value.getFirst("designation").trim())
                                    .organization(value.getFirst("organization").trim())
                                    .income(Long.valueOf(value.getFirst("income")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                                    .startDate(startDate)
                                    .endDate(endDate)
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

                            //checks if currency uuid exists
                            return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentGuardianEntity -> studentGuardianJobHistoryRepository.save(entity)
                                                    .then(studentGuardianJobHistoryRepository.save(updatedEntity))
                                                    .flatMap(studentGuardianJobHistoryEntity -> responseSuccessMsg("Record Updated Successfully", studentGuardianJobHistoryEntity))
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

    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGuardianJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentGuardianJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianJobHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGuardianJobHistoryEntity entity = StudentGuardianJobHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentGuardianUUID(val.getStudentGuardianUUID())
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
                                        .updatedBy(UUID.fromString(userId))
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

                                return studentGuardianJobHistoryRepository.save(val)
                                        .then(studentGuardianJobHistoryRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-job-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGuardianJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentGuardianJobHistoryUUID)
                .flatMap(studentGuardianJobHistoryEntity -> {

                    studentGuardianJobHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    studentGuardianJobHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentGuardianJobHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentGuardianJobHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentGuardianJobHistoryEntity.setReqDeletedIP(reqIp);
                    studentGuardianJobHistoryEntity.setReqDeletedPort(reqPort);
                    studentGuardianJobHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    studentGuardianJobHistoryEntity.setReqDeletedOS(reqOs);
                    studentGuardianJobHistoryEntity.setReqDeletedDevice(reqDevice);
                    studentGuardianJobHistoryEntity.setReqDeletedReferer(reqReferer);

                    return studentGuardianJobHistoryRepository.save(studentGuardianJobHistoryEntity)
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
