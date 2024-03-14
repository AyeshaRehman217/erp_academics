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
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingJobHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingJobHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingJobHistoryRepository;
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

@Tag(name = "studentSiblingJobHistoryHandler")
@Component
public class StudentSiblingJobHistoryHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingJobHistoryRepository studentSiblingJobHistoryRepository;

    @Autowired
    SlaveStudentSiblingJobHistoryRepository slaveStudentSiblingJobHistoryRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

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
        
        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Sibling UUID
        String studentSiblingUUID = serverRequest.queryParam("studentSiblingUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !studentSiblingUUID.isEmpty()) {

            Flux<SlaveStudentSiblingJobHistoryEntity> slaveStudentSiblingJobHistoryFlux = slaveStudentSiblingJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status));

            return slaveStudentSiblingJobHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingJobHistoryEntity -> slaveStudentSiblingJobHistoryRepository
                            .countByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(studentSiblingUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSiblingUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentSiblingJobHistoryEntity> slaveStudentSiblingJobHistoryFlux = slaveStudentSiblingJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentSiblingJobHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingJobHistoryEntity -> slaveStudentSiblingJobHistoryRepository
                            .countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!studentSiblingUUID.isEmpty()) {

            Flux<SlaveStudentSiblingJobHistoryEntity> slaveStudentSiblingJobHistoryFlux = slaveStudentSiblingJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID));

            return slaveStudentSiblingJobHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingJobHistoryEntity -> slaveStudentSiblingJobHistoryRepository
                            .countByOccupationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID))
                            .flatMap(count -> {
                                if (studentSiblingJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveStudentSiblingJobHistoryEntity> slaveStudentSiblingJobHistoryFlux = slaveStudentSiblingJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveStudentSiblingJobHistoryFlux
                    .collectList()
                    .flatMap(studentSiblingJobHistoryEntity -> slaveStudentSiblingJobHistoryRepository
                            .countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSiblingJobHistoryEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingJobHistoryEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSiblingJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingJobHistoryUUID)
                .flatMap(studentJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    // Show Student Jobs against Student , Student Sibling, and Student Sibling Job History UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-sibling_student-sibling-job-histories_show")
    public Mono<ServerResponse> showJobHistoryForStudentSiblingAndStudent(ServerRequest serverRequest) {
        UUID studentSiblingJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentSiblingUUID = UUID.fromString(serverRequest.queryParam("studentSiblingUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentSiblingJobHistoryRepository.showStudentSiblingJobHistoryAgainstStudentAndStudentSibling(studentUUID, studentSiblingUUID, studentSiblingJobHistoryUUID)
                .flatMap(studentSiblingJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Sibling Job History
    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSiblingJobHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_store")
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
                        return responseInfoMsg("Start Date is after the End Date");
                    }
                    StudentSiblingJobHistoryEntity entity = StudentSiblingJobHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentSiblingUUID(UUID.fromString(value.getFirst("studentSiblingUUID")))
                            .occupation(value.getFirst("occupation").trim())
                            .designation(value.getFirst("designation").trim())
                            .organization(value.getFirst("organization").trim())
                            .income(Long.valueOf(value.getFirst("income")))
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
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

                    //checks if studentSibling uuid exists
                    return studentSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSiblingUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentSiblingEntity -> studentSiblingJobHistoryRepository.save(entity)
                                                    .flatMap(studentSiblingJobHistoryEntity -> responseSuccessMsg("Record Stored Successfully", studentSiblingJobHistoryEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Sibling  record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Sibling  record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSiblingJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentSiblingJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingJobHistoryUUID)
                        .flatMap(entity -> {
                            LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            // If start date is after the end date
                            if (startDate.isAfter(endDate)) {
                                return responseInfoMsg("Start Date is after the End Date.");
                            }
                            StudentSiblingJobHistoryEntity updatedEntity = StudentSiblingJobHistoryEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentSiblingUUID(entity.getStudentSiblingUUID())
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
                            return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", updatedEntity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentEntity -> studentSiblingJobHistoryRepository.save(entity)
                                                    .then(studentSiblingJobHistoryRepository.save(updatedEntity))
                                                    .flatMap(studentJobHistoryEntity -> responseSuccessMsg("Record Updated Successfully", studentJobHistoryEntity))
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

    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSiblingJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentSiblingJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingJobHistoryUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSiblingJobHistoryEntity entity = StudentSiblingJobHistoryEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentSiblingUUID(val.getStudentSiblingUUID())
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

                                // update status
                                val.setDeletedBy(UUID.fromString(userId));
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return studentSiblingJobHistoryRepository.save(val)
                                        .then(studentSiblingJobHistoryRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-job-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSiblingJobHistoryRepository.findByUuidAndDeletedAtIsNull(studentSiblingJobHistoryUUID)
                .flatMap(studentSiblingJobHistory -> {

                    studentSiblingJobHistory.setDeletedBy(UUID.fromString(userId));
                    studentSiblingJobHistory.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentSiblingJobHistory.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentSiblingJobHistory.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentSiblingJobHistory.setReqDeletedIP(reqIp);
                    studentSiblingJobHistory.setReqDeletedPort(reqPort);
                    studentSiblingJobHistory.setReqDeletedBrowser(reqBrowser);
                    studentSiblingJobHistory.setReqDeletedOS(reqOs);
                    studentSiblingJobHistory.setReqDeletedDevice(reqDevice);
                    studentSiblingJobHistory.setReqDeletedReferer(reqReferer);

                    return studentSiblingJobHistoryRepository.save(studentSiblingJobHistory)
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
