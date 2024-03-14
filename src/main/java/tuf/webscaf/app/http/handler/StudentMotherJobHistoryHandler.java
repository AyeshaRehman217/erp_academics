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
import tuf.webscaf.app.dbContext.master.entity.StudentMotherJobHistoryEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentMotherJobHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentMotherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherJobHistoryEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherJobHistoryRepository;
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

@Tag(name = "studentMotherJobHistoryHandler")
@Component
public class StudentMotherJobHistoryHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentMotherJobHistoryRepository studentMotherJobHistoryRepository;

    @Autowired
    SlaveStudentMotherJobHistoryRepository slaveStudentMotherJobHistoryRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student UUID
        String studentMotherUUID = serverRequest.queryParam("studentMotherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentMotherUUID.isEmpty()) {
            Flux<SlaveStudentMotherJobHistoryEntity> slaveMotherJobHistoryFlux = slaveStudentMotherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status));

            return slaveMotherJobHistoryFlux
                    .collectList()
                    .flatMap(studentMotherJobHistoryEntityDB -> slaveStudentMotherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        else if (!status.isEmpty()) {

            Flux<SlaveStudentMotherJobHistoryEntity> slaveMotherJobHistoryFlux = slaveStudentMotherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveMotherJobHistoryFlux
                    .collectList()
                    .flatMap(studentMotherJobHistoryEntityDB -> slaveStudentMotherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        else if (!studentMotherUUID.isEmpty()) {
            Flux<SlaveStudentMotherJobHistoryEntity> slaveMotherJobHistoryFlux = slaveStudentMotherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentMotherUUID), searchKeyWord, UUID.fromString(studentMotherUUID), searchKeyWord, UUID.fromString(studentMotherUUID));

            return slaveMotherJobHistoryFlux
                    .collectList()
                    .flatMap(studentMotherJobHistoryEntityDB -> slaveStudentMotherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentMotherUUID), searchKeyWord, UUID.fromString(studentMotherUUID), searchKeyWord, UUID.fromString(studentMotherUUID))
                            .flatMap(count -> {
                                if (studentMotherJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        else {

            Flux<SlaveStudentMotherJobHistoryEntity> slaveMotherJobHistoryFlux = slaveStudentMotherJobHistoryRepository
                    .findAllByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveMotherJobHistoryFlux
                    .collectList()
                    .flatMap(studentMotherJobHistoryEntityDB -> slaveStudentMotherJobHistoryRepository.countByOccupationContainingIgnoreCaseAndDeletedAtIsNullOrDesignationContainingIgnoreCaseAndDeletedAtIsNullOrOrganizationContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentMotherJobHistoryEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherJobHistoryEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID stdMthJobUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherJobHistoryRepository.findByUuidAndDeletedAtIsNull(stdMthJobUUID)
                .flatMap(stdMotherJobHistoryEntityDB -> responseSuccessMsg("Record Fetched Successfully", stdMotherJobHistoryEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    // Show Student Jobs against Student , Student Mother, and Student Mother Job History UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-mother_student-mother-job-histories_show")
    public Mono<ServerResponse> showJobHistoryForStudentMotherAndStudent(ServerRequest serverRequest) {
        UUID studentMotherJobHistoryUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentMotherUUID = UUID.fromString(serverRequest.queryParam("studentMotherUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentMotherJobHistoryRepository.showStudentMotherJobHistoryAgainstStudentAndStudentMother(studentUUID, studentMotherUUID, studentMotherJobHistoryUUID)
                .flatMap(studentMotherJobHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherJobHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Student Mother Job History
    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherJobHistoryRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_store")
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
                        return responseInfoMsg("Start Date is after the End Date.");
                    }

                    StudentMotherJobHistoryEntity entity = StudentMotherJobHistoryEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentMotherUUID(UUID.fromString(value.getFirst("studentMotherUUID").trim()))
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
                    return studentMotherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMotherUUID())
                            //checks if currency uuid exists
                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(studentEntity -> studentMotherJobHistoryRepository.save(entity)
                                                    .flatMap(stdMotherJobHistoryEntityDB -> responseSuccessMsg("Record Stored Successfully", stdMotherJobHistoryEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                    ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Mother  does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Mother  does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID stdMthJobUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentMotherJobHistoryRepository.findByUuidAndDeletedAtIsNull(stdMthJobUUID)
                        .flatMap(previousMthJobEntity -> {
                            LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                            LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            int date_difference = startDate.compareTo(endDate);
                            // If start date is after the end date
                            if (date_difference > 0) {
                                return responseInfoMsg("Start Date is after the End Date.");
                            }

                            StudentMotherJobHistoryEntity updatedEntity = StudentMotherJobHistoryEntity.builder()
                                    .uuid(previousMthJobEntity.getUuid())
                                    .studentMotherUUID(previousMthJobEntity.getStudentMotherUUID())
                                    .occupation(value.getFirst("occupation").trim())
                                    .designation(value.getFirst("designation").trim())
                                    .organization(value.getFirst("organization").trim())
                                    .income(Long.valueOf(value.getFirst("income")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousMthJobEntity.getCreatedAt())
                                    .createdBy(previousMthJobEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousMthJobEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousMthJobEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousMthJobEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousMthJobEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousMthJobEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousMthJobEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousMthJobEntity.setDeletedBy(UUID.fromString(userId));
                            previousMthJobEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousMthJobEntity.setReqDeletedIP(reqIp);
                            previousMthJobEntity.setReqDeletedPort(reqPort);
                            previousMthJobEntity.setReqDeletedBrowser(reqBrowser);
                            previousMthJobEntity.setReqDeletedOS(reqOs);
                            previousMthJobEntity.setReqDeletedDevice(reqDevice);
                            previousMthJobEntity.setReqDeletedReferer(reqReferer);

                            //checks if currency uuid exists
                            return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", updatedEntity.getCurrencyUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(stdMth -> studentMotherJobHistoryRepository.save(previousMthJobEntity)
                                                    .then(studentMotherJobHistoryRepository.save(updatedEntity))
                                                    .flatMap(stdMotherJobHistoryEntityDB -> responseSuccessMsg("Record Updated Successfully", stdMotherJobHistoryEntityDB))
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

    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID stdMthJobUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentMotherJobHistoryRepository.findByUuidAndDeletedAtIsNull(stdMthJobUUID)
                            .flatMap(previousMthJobEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousMthJobEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentMotherJobHistoryEntity updatedMthEntity = StudentMotherJobHistoryEntity.builder()
                                        .uuid(previousMthJobEntity.getUuid())
                                        .studentMotherUUID(previousMthJobEntity.getStudentMotherUUID())
                                        .occupation(previousMthJobEntity.getOccupation())
                                        .designation(previousMthJobEntity.getDesignation())
                                        .organization(previousMthJobEntity.getOrganization())
                                        .income(previousMthJobEntity.getIncome())
                                        .currencyUUID(previousMthJobEntity.getCurrencyUUID())
                                        .startDate(previousMthJobEntity.getStartDate())
                                        .endDate(previousMthJobEntity.getEndDate())
                                        .status(status == true ? true : false)
                                        .createdAt(previousMthJobEntity.getCreatedAt())
                                        .createdBy(previousMthJobEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousMthJobEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousMthJobEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousMthJobEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousMthJobEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousMthJobEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousMthJobEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                previousMthJobEntity.setDeletedBy(UUID.fromString(userId));
                                previousMthJobEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousMthJobEntity.setReqDeletedIP(reqIp);
                                previousMthJobEntity.setReqDeletedPort(reqPort);
                                previousMthJobEntity.setReqDeletedBrowser(reqBrowser);
                                previousMthJobEntity.setReqDeletedOS(reqOs);
                                previousMthJobEntity.setReqDeletedDevice(reqDevice);
                                previousMthJobEntity.setReqDeletedReferer(reqReferer);

                                return studentMotherJobHistoryRepository.save(previousMthJobEntity)
                                        .then(studentMotherJobHistoryRepository.save(updatedMthEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-job-histories_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID stdMthJobUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentMotherJobHistoryRepository.findByUuidAndDeletedAtIsNull(stdMthJobUUID)
                .flatMap(stdMotherJobHistoryEntityDB -> {

                    stdMotherJobHistoryEntityDB.setDeletedBy(UUID.fromString(userId));
                    stdMotherJobHistoryEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    stdMotherJobHistoryEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    stdMotherJobHistoryEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    stdMotherJobHistoryEntityDB.setReqDeletedIP(reqIp);
                    stdMotherJobHistoryEntityDB.setReqDeletedPort(reqPort);
                    stdMotherJobHistoryEntityDB.setReqDeletedBrowser(reqBrowser);
                    stdMotherJobHistoryEntityDB.setReqDeletedOS(reqOs);
                    stdMotherJobHistoryEntityDB.setReqDeletedDevice(reqDevice);
                    stdMotherJobHistoryEntityDB.setReqDeletedReferer(reqReferer);

                    return studentMotherJobHistoryRepository.save(stdMotherJobHistoryEntityDB)
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
