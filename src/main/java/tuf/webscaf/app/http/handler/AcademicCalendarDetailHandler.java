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
import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarDetailEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveAcademicCalendarDetailDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarDetailEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarDetailRepository;
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

@Tag(name = "academicCalendarDetailHandler")
@Component
public class AcademicCalendarDetailHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    AcademicCalendarDetailRepository academicCalendarDetailRepository;

    @Autowired
    SlaveAcademicCalendarDetailRepository slaveAcademicCalendarDetailRepository;

    @Autowired
    AcademicCalendarRepository academicCalendarRepository;

    @Autowired
    AcademicCalendarDetailHolidayPvtRepository academicCalendarDetailHolidayPvtRepository;

    @Autowired
    AcademicCalendarDetailEventPvtRepository academicCalendarDetailEventPvtRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    ApiCallService apiCallService;

    @Autowired
    HolidayRepository holidayRepository;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-details_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

//        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

        String academicCalendarUUID = serverRequest.queryParam("academicCalendarUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

//        if (!academicSessionUUID.isEmpty() && !academicCalendarUUID.isEmpty() && !status.isEmpty()) {
//            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
//                    .indexAgainstCalendarAndSessionWithStatus(UUID.fromString(academicCalendarUUID), UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveAcademicCalendarDetailFlux
//                    .collectList()
//                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
//                            .countDetailsAgainstAcademicSessionAndCalendarWithStatus(Boolean.valueOf(status), UUID.fromString(academicSessionUUID), UUID.fromString(academicCalendarUUID), searchKeyWord, searchKeyWord)
//                            .flatMap(count -> {
//                                if (academicCalendarDetailEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        } else if (!academicSessionUUID.isEmpty() && !academicCalendarUUID.isEmpty()) {
//            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
//                    .indexAgainstCalendarAndSessionWithoutStatus(UUID.fromString(academicCalendarUUID), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveAcademicCalendarDetailFlux
//                    .collectList()
//                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
//                            .countDetailsAgainstAcademicSessionAndCalendarWithoutStatus(UUID.fromString(academicSessionUUID), UUID.fromString(academicCalendarUUID), searchKeyWord, searchKeyWord)
//                            .flatMap(count -> {
//                                if (academicCalendarDetailEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        } else if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
//            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
//                    .indexAgainstSessionWithStatus(UUID.fromString(academicSessionUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveAcademicCalendarDetailFlux
//                    .collectList()
//                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
//                            .countDetailsAgainstAcademicSessionWithStatus(Boolean.valueOf(status), UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord)
//                            .flatMap(count -> {
//                                if (academicCalendarDetailEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
        if (!academicCalendarUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
                    .indexAgainstCalendarWithStatus(UUID.fromString(academicCalendarUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAcademicCalendarDetailFlux
                    .collectList()
                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
                            .countDetailsAgainstAcademicCalendarWithStatus(Boolean.valueOf(status), UUID.fromString(academicCalendarUUID), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarDetailEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!academicCalendarUUID.isEmpty()) {
            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
                    .indexAgainstCalendarWithoutStatus(UUID.fromString(academicCalendarUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAcademicCalendarDetailFlux
                    .collectList()
                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
                            .countDetailsAgainstAcademicCalendarWithoutStatus(UUID.fromString(academicCalendarUUID), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarDetailEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
//        else if (!academicSessionUUID.isEmpty()) {
//            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
//                    .indexAgainstSessionWithoutStatus(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
//
//            return slaveAcademicCalendarDetailFlux
//                    .collectList()
//                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
//                            .countDetailsAgainstAcademicSessionWithoutStatus(UUID.fromString(academicSessionUUID), searchKeyWord, searchKeyWord)
//                            .flatMap(count -> {
//                                if (academicCalendarDetailEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
        else if (!status.isEmpty()) {
            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
                    .indexWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAcademicCalendarDetailFlux
                    .collectList()
                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
                            .countDetailsWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarDetailEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveAcademicCalendarDetailDto> slaveAcademicCalendarDetailFlux = slaveAcademicCalendarDetailRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveAcademicCalendarDetailFlux
                    .collectList()
                    .flatMap(academicCalendarDetailEntity -> slaveAcademicCalendarDetailRepository
                            .countDetailsWithoutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (academicCalendarDetailEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarDetailEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-details_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID academicCalendarDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveAcademicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                .flatMap(academicCalendarDetailEntity -> responseSuccessMsg("Record Fetched Successfully", academicCalendarDetailEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-details_store")
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

                    AcademicCalendarDetailEntity entity = AcademicCalendarDetailEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .academicCalendarUUID(UUID.fromString(value.getFirst("academicCalendarUUID").trim()))
                            .calendarDate(LocalDateTime.parse(value.getFirst("calendarDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .comments(value.getFirst("comments"))
                            .isWorkingDay(Boolean.valueOf(value.getFirst("isWorkingDay")))
                            .isLectureAllowed(Boolean.valueOf(value.getFirst("isLectureAllowed")))
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

                    // check if record already exists with given calendar date
                    return academicCalendarDetailRepository.findFirstByAcademicCalendarUUIDAndCalendarDateAndDeletedAtIsNull(entity.getAcademicCalendarUUID(), entity.getCalendarDate())
                            .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists"))
                            // check if academic calendar uuid exists
                            .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findByUuidAndDeletedAtIsNull(entity.getAcademicCalendarUUID())
                                    // check if academic session uuid exists
                                    .flatMap(academicCalendarEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(academicCalendarEntity.getAcademicSessionUUID())
                                            // check if academic session start date uuid exists
                                            .flatMap(academicSessionEntity ->
                                                    {
                                                        if (entity.getCalendarDate().isBefore(academicSessionEntity.getStartDate())) {
                                                            return responseInfoMsg("Calendar Date is before Academic Calendar Start Date");
                                                        }

                                                        if (entity.getCalendarDate().isAfter(academicSessionEntity.getEndDate())) {
                                                            return responseInfoMsg("Calendar Date is after Academic Calendar End Date");
                                                        }

                                                        return academicCalendarDetailRepository.save(entity)
                                                                .flatMap(academicCalendarDetailEntity -> responseSuccessMsg("Record Stored Successfully", academicCalendarDetailEntity))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                                    }
                                            ).switchIfEmpty(responseInfoMsg("Academic Session record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Academic Session record does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Academic Calendar record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Academic Calendar record does not exist. Please contact developer"))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-details_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID academicCalendarDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                        .flatMap(previousAcademicEntity -> {

                            AcademicCalendarDetailEntity updatedAcademicCalendarDetailEntity = AcademicCalendarDetailEntity
                                    .builder()
                                    .uuid(previousAcademicEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .academicCalendarUUID(UUID.fromString(value.getFirst("academicCalendarUUID").trim()))
                                    .calendarDate(LocalDateTime.parse(value.getFirst("calendarDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .comments(value.getFirst("comments"))
                                    .isWorkingDay(Boolean.valueOf(value.getFirst("isWorkingDay")))
                                    .isLectureAllowed(Boolean.valueOf(value.getFirst("isLectureAllowed")))
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

                            // check if record already exists with given calendar date
                            return academicCalendarDetailRepository.findFirstByAcademicCalendarUUIDAndCalendarDateAndDeletedAtIsNullAndUuidIsNot(updatedAcademicCalendarDetailEntity.getAcademicCalendarUUID(), updatedAcademicCalendarDetailEntity.getCalendarDate(), updatedAcademicCalendarDetailEntity.getUuid())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists"))
                                    // check if academic calendar uuid exists
                                    .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarDetailEntity.getAcademicCalendarUUID())
                                            // check if academic session uuid exists
                                            .flatMap(academicCalendarEntity -> academicSessionRepository.findByUuidAndDeletedAtIsNull(academicCalendarEntity.getAcademicSessionUUID())
                                                    // check if academic session start date uuid exists
                                                    .flatMap(academicSessionEntity -> {

                                                                if (updatedAcademicCalendarDetailEntity.getCalendarDate().isBefore(academicSessionEntity.getStartDate())) {
                                                                    return responseInfoMsg("Calendar Date is before Academic Calendar Start Date");
                                                                }

                                                                if (updatedAcademicCalendarDetailEntity.getCalendarDate().isAfter(academicSessionEntity.getEndDate())) {
                                                                    return responseInfoMsg("Calendar Date is after Academic Calendar End Date");
                                                                }

                                                                return academicCalendarDetailRepository.save(previousAcademicEntity)
                                                                        .then(academicCalendarDetailRepository.save(updatedAcademicCalendarDetailEntity))
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
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-details_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID academicCalendarDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                            .flatMap(academicCalendarDetailEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((academicCalendarDetailEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AcademicCalendarDetailEntity updatedAcademicCalendarDetailEntity = AcademicCalendarDetailEntity
                                        .builder()
                                        .uuid(academicCalendarDetailEntityDB.getUuid())
                                        .status(status == true ? true : false)
                                        .academicCalendarUUID(academicCalendarDetailEntityDB.getAcademicCalendarUUID())
                                        .calendarDate(academicCalendarDetailEntityDB.getCalendarDate())
                                        .comments(academicCalendarDetailEntityDB.getComments())
                                        .isWorkingDay(academicCalendarDetailEntityDB.getIsWorkingDay())
                                        .isLectureAllowed(academicCalendarDetailEntityDB.getIsLectureAllowed())
                                        .createdAt(academicCalendarDetailEntityDB.getCreatedAt())
                                        .createdBy(academicCalendarDetailEntityDB.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(academicCalendarDetailEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(academicCalendarDetailEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(academicCalendarDetailEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(academicCalendarDetailEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(academicCalendarDetailEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(academicCalendarDetailEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                academicCalendarDetailEntityDB.setDeletedBy(UUID.fromString(userId));
                                academicCalendarDetailEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                academicCalendarDetailEntityDB.setReqDeletedIP(reqIp);
                                academicCalendarDetailEntityDB.setReqDeletedPort(reqPort);
                                academicCalendarDetailEntityDB.setReqDeletedBrowser(reqBrowser);
                                academicCalendarDetailEntityDB.setReqDeletedOS(reqOs);
                                academicCalendarDetailEntityDB.setReqDeletedDevice(reqDevice);
                                academicCalendarDetailEntityDB.setReqDeletedReferer(reqReferer);

                                return academicCalendarDetailRepository.save(academicCalendarDetailEntityDB)
                                        .then(academicCalendarDetailRepository.save(updatedAcademicCalendarDetailEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_academic-calendar-details_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID academicCalendarDetailUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return academicCalendarDetailRepository.findByUuidAndDeletedAtIsNull(academicCalendarDetailUUID)
                .flatMap(academicCalendarDetailEntity -> {

                    academicCalendarDetailEntity.setDeletedBy(UUID.fromString(userId));
                    academicCalendarDetailEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    academicCalendarDetailEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    academicCalendarDetailEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    academicCalendarDetailEntity.setReqDeletedIP(reqIp);
                    academicCalendarDetailEntity.setReqDeletedPort(reqPort);
                    academicCalendarDetailEntity.setReqDeletedBrowser(reqBrowser);
                    academicCalendarDetailEntity.setReqDeletedOS(reqOs);
                    academicCalendarDetailEntity.setReqDeletedDevice(reqDevice);
                    academicCalendarDetailEntity.setReqDeletedReferer(reqReferer);

                    // if academic calendar detail reference exists in academic calendar detail holiday pvt
                    return academicCalendarDetailHolidayPvtRepository.findFirstByAcademicCalendarDetailUUIDAndDeletedAtIsNull(academicCalendarDetailEntity.getUuid())
                            .flatMap(academicCalendarDetailHolidayPvt -> responseInfoMsg("Unable to delete record as the reference exists"))
                            // if academic calendar detail reference exists in academic calendar detail event pvt
                            .switchIfEmpty(Mono.defer(() -> academicCalendarDetailEventPvtRepository.findFirstByAcademicCalendarDetailUUIDAndDeletedAtIsNull(academicCalendarDetailEntity.getUuid())
                                    .flatMap(academicCalendarDetailEventPvt -> responseInfoMsg("Unable to delete record as the reference exists"))))
                            .switchIfEmpty(Mono.defer(() -> academicCalendarDetailRepository.save(academicCalendarDetailEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."))
                            ));
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