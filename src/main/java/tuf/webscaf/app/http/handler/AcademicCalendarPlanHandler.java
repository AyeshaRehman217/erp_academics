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
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarPlanEntity;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarRepository;
import tuf.webscaf.app.dbContext.master.repositry.AcademicCalendarPlanRepository;
import tuf.webscaf.app.dbContext.master.repositry.AcademicSessionRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarPlanEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarPlanRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Tag(name = "academicCalendarPlanHandler")
@Component
public class

AcademicCalendarPlanHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    AcademicCalendarPlanRepository academicCalendarPlanRepository;

    @Autowired
    SlaveAcademicCalendarPlanRepository slaveAcademicCalendarPlanRepository;

    @Autowired
    AcademicCalendarRepository academicCalendarRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-plans_index")
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

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Academic Calendar UUID
        String academicCalendarUUID = serverRequest.queryParam("academicCalendarUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if both status and academic calendar uuids are given
        if (!status.isEmpty() && !academicCalendarUUID.isEmpty()) {
            Flux<SlaveAcademicCalendarPlanEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarPlanRepository
                    .findAllByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, UUID.fromString(academicCalendarUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(academicCalendarUUID), Boolean.valueOf(status));
            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarPlanRepository
                            .countByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, UUID.fromString(academicCalendarUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(academicCalendarUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if academic calendar uuid is given
        else if(!academicCalendarUUID.isEmpty()){
            Flux<SlaveAcademicCalendarPlanEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarPlanRepository
                    .findAllByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNull
                            (pageable, searchKeyWord, UUID.fromString(academicCalendarUUID), searchKeyWord, UUID.fromString(academicCalendarUUID));
            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarPlanRepository
                            .countByNameContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNull
                                    (searchKeyWord, UUID.fromString(academicCalendarUUID), searchKeyWord, UUID.fromString(academicCalendarUUID))
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if status is given
        else if (!status.isEmpty()) {
            Flux<SlaveAcademicCalendarPlanEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarPlanRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarPlanRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional query parameter is given
        else {
            Flux<SlaveAcademicCalendarPlanEntity> slaveAcademicCalendarFlux = slaveAcademicCalendarPlanRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveAcademicCalendarFlux
                    .collectList()
                    .flatMap(academicCalendarEntity -> slaveAcademicCalendarPlanRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull
                                    (searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-plans_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID academicCalendarPlanUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveAcademicCalendarPlanRepository.findByUuidAndDeletedAtIsNull(academicCalendarPlanUUID)
                .flatMap(academicCalendarEntity -> responseSuccessMsg("Record Fetched Successfully", academicCalendarEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));

    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-plans_store")
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

//                    int date_difference = startDate.compareTo(endDate);
                    // If start date is after the end date
                    if (endDate.isBefore(startDate)) {
                        return responseInfoMsg("Start Date is after the End Date");
                    }

                    AcademicCalendarPlanEntity entity = AcademicCalendarPlanEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .startDate(startDate)
                            .endDate(endDate)
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .academicCalendarUUID(UUID.fromString(value.getFirst("academicCalendarUUID")))
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

                    // check if name is unique
                    return academicCalendarPlanRepository.findFirstByNameIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNull(entity.getName(), entity.getAcademicCalendarUUID())
                            .flatMap(checkName -> responseInfoMsg("Name Already Exists"))
                            // check if academic calendar uuid exist
                            .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findByUuidAndDeletedAtIsNull(entity.getAcademicCalendarUUID())
                                                            // check if academic session uuid exists
                                                            .flatMap(academicCalendarEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(academicCalendarEntity.getAcademicSessionUUID())
                                                                    // check if academic session start date uuid exists
                                                                    .flatMap(academicSessionEntity ->
                                                                            {

                                                                                //If academic calendar plan start date or end date is not within the calendar start date or end date

//                                                                                int academicPlanStartDateCalenderStartDateDif = startDate.compareTo(calendarStartDate);
                                                                                if (startDate.isBefore(academicSessionEntity.getStartDate())) {
                                                                                    return responseInfoMsg("Academic Plan Start Date is before Academic Calendar Start Date");
                                                                                }

//                                                                                int academicPlanStartDateCalenderEndDateDif = startDate.compareTo(calendarEndDate);
                                                                                if (startDate.isAfter(academicSessionEntity.getEndDate())) {
                                                                                    return responseInfoMsg("Academic Plan Start Date is after Academic Calendar End Date");
                                                                                }

//                                                                                int academicPlanEndDateCalenderStartDateDif = endDate.compareTo(calendarStartDate);
                                                                                if (endDate.isBefore(academicSessionEntity.getStartDate())) {
                                                                                    return responseInfoMsg("Academic Plan End Date is before Academic Calendar Start Date");
                                                                                }

//                                                                                int academicPlanEndDateCalenderEndDateDif = endDate.compareTo(calendarEndDate);
                                                                                if (endDate.isAfter(academicSessionEntity.getEndDate()) && !(endDate.isEqual(academicSessionEntity.getEndDate()))) {
                                                                                    return responseInfoMsg("Academic Plan End Date is after Academic Calendar End Date");
                                                                                }

                                                                                return academicCalendarPlanRepository.save(entity)
                                                                                        .flatMap(academicCalendarPlanEntity -> responseSuccessMsg("Record Stored Successfully", academicCalendarPlanEntity))
                                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please Contact Developer."));
                                                                            }

                                                                    ))
                                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Academic Calendar  record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Calendar  record does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-plans_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID academicCalendarPlanUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> academicCalendarPlanRepository.findByUuidAndDeletedAtIsNull(academicCalendarPlanUUID)
                        .flatMap(previousAcademicEntity -> {

                            LocalDateTime startDate = LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            LocalDateTime endDate = LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            // If start date is after the end date
                            if (endDate.isBefore(startDate)) {
                                return responseInfoMsg("Start Date is after the End Date");
                            }

                            AcademicCalendarPlanEntity updatedAcademicCalendarPlanEntity = AcademicCalendarPlanEntity.builder()
                                    .uuid(previousAcademicEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .startDate(startDate)
                                    .endDate(endDate)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .academicCalendarUUID(UUID.fromString(value.getFirst("academicCalendarUUID")))
                                    .createdAt(previousAcademicEntity.getCreatedAt())
                                    .createdBy(previousAcademicEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousAcademicEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousAcademicEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousAcademicEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousAcademicEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousAcademicEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousAcademicEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            //Deleting Previous Record and Creating a New One Based on UUID
                            previousAcademicEntity.setDeletedBy(UUID.fromString(userId));
                            previousAcademicEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousAcademicEntity.setReqDeletedIP(reqIp);
                            previousAcademicEntity.setReqDeletedPort(reqPort);
                            previousAcademicEntity.setReqDeletedBrowser(reqBrowser);
                            previousAcademicEntity.setReqDeletedOS(reqOs);
                            previousAcademicEntity.setReqDeletedDevice(reqDevice);
                            previousAcademicEntity.setReqDeletedReferer(reqReferer);

                            // check if name is unique
                            return academicCalendarPlanRepository.findFirstByNameIgnoreCaseAndAcademicCalendarUUIDAndDeletedAtIsNullAndUuidIsNot(updatedAcademicCalendarPlanEntity.getName(), updatedAcademicCalendarPlanEntity.getAcademicCalendarUUID(), academicCalendarPlanUUID)
                                    .flatMap(checkName -> responseInfoMsg("Name Already Exists"))
                                    // check if academic calendar uuid exist
                                    .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarPlanEntity.getAcademicCalendarUUID())
                                            // check if academic session uuid exists
                                            .flatMap(academicCalendarEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(academicCalendarEntity.getAcademicSessionUUID())
                                                    // check if academic session start date uuid exists
                                                    .flatMap(academicSessionEntity -> {

                                                                //If academic calendar plan start date or end date is not within the calendar start date or end date
                                                                if (startDate.isBefore(academicSessionEntity.getStartDate())) {
                                                                    return responseInfoMsg("Academic Plan Start Date is before Academic Calendar Start Date");
                                                                }

                                                                if (startDate.isAfter(academicSessionEntity.getEndDate())) {
                                                                    return responseInfoMsg("Academic Plan Start Date is after Academic Calendar End Date");
                                                                }

                                                                if (endDate.isBefore(academicSessionEntity.getStartDate())) {
                                                                    return responseInfoMsg("Academic Plan End Date is before Academic Calendar Start Date");
                                                                }

                                                                if (endDate.isAfter(academicSessionEntity.getEndDate()) && !(endDate.isEqual(academicSessionEntity.getEndDate()))) {
                                                                    return responseInfoMsg("Academic Plan End Date is after Academic Calendar End Date");
                                                                }

                                                                return academicCalendarPlanRepository.save(previousAcademicEntity)
                                                                        .then(academicCalendarPlanRepository.save(updatedAcademicCalendarPlanEntity))
                                                                        .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                            }
                                                    ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Academic Calendar record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Academic Calendar record does not exist. Please contact developer"))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-plans_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID academicCalendarPlanUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return academicCalendarPlanRepository.findByUuidAndDeletedAtIsNull(academicCalendarPlanUUID)
                            .flatMap(academicCalendarPlanEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((academicCalendarPlanEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicCalendarPlanEntity updatedAcademicCalendarPlanEntity = AcademicCalendarPlanEntity.builder()
                                        .uuid(academicCalendarPlanEntityDB.getUuid())
                                        .name(academicCalendarPlanEntityDB.getName())
                                        .description(academicCalendarPlanEntityDB.getDescription())
                                        .status(status == true ? true : false)
                                        .startDate(academicCalendarPlanEntityDB.getStartDate())
                                        .endDate(academicCalendarPlanEntityDB.getEndDate())
                                        .academicCalendarUUID(academicCalendarPlanEntityDB.getAcademicCalendarUUID())
                                        .createdAt(academicCalendarPlanEntityDB.getCreatedAt())
                                        .createdBy(academicCalendarPlanEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicCalendarPlanEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicCalendarPlanEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicCalendarPlanEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicCalendarPlanEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicCalendarPlanEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicCalendarPlanEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                academicCalendarPlanEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicCalendarPlanEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicCalendarPlanEntityDB.setReqDeletedIP(reqIp);
                                academicCalendarPlanEntityDB.setReqDeletedPort(reqPort);
                                academicCalendarPlanEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicCalendarPlanEntityDB.setReqDeletedOS(reqOs);
                                academicCalendarPlanEntityDB.setReqDeletedDevice(reqDevice);
                                academicCalendarPlanEntityDB.setReqDeletedReferer(reqReferer);

                                return academicCalendarPlanRepository.save(academicCalendarPlanEntityDB)
                                        .then(academicCalendarPlanRepository.save(updatedAcademicCalendarPlanEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-plans_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID academicCalendarPlanUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return academicCalendarPlanRepository.findByUuidAndDeletedAtIsNull(academicCalendarPlanUUID)
                .flatMap(academicCalendarPlanEntity -> {

                    academicCalendarPlanEntity.setDeletedBy(UUID.fromString(userId));
                    academicCalendarPlanEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    academicCalendarPlanEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    academicCalendarPlanEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    academicCalendarPlanEntity.setReqDeletedIP(reqIp);
                    academicCalendarPlanEntity.setReqDeletedPort(reqPort);
                    academicCalendarPlanEntity.setReqDeletedBrowser(reqBrowser);
                    academicCalendarPlanEntity.setReqDeletedOS(reqOs);
                    academicCalendarPlanEntity.setReqDeletedDevice(reqDevice);
                    academicCalendarPlanEntity.setReqDeletedReferer(reqReferer);

                    return academicCalendarPlanRepository.save(academicCalendarPlanEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
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