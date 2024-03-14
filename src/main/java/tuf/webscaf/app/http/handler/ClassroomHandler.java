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
import tuf.webscaf.app.dbContext.master.entity.ClassroomEntity;
import tuf.webscaf.app.dbContext.master.repositry.CampusRepository;
import tuf.webscaf.app.dbContext.master.repositry.ClassroomRepository;
import tuf.webscaf.app.dbContext.master.repositry.TimetableCreationRepository;
import tuf.webscaf.app.dbContext.slave.dto.SlaveClassroomDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveClassroomRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "classroomHandler")
@Component
public class ClassroomHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    ClassroomRepository classroomRepository;

    @Autowired
    SlaveClassroomRepository slaveClassroomRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_classrooms_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        // Optional Query Parameter of Campus UUID
        String campusUUID = serverRequest.queryParam("campusUUID").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if optional parameters of status and campus uuid are present
        if (!status.isEmpty() && !campusUUID.isEmpty()) {
            Flux<SlaveClassroomDto> slaveClassroomFlux = slaveClassroomRepository
                    .indexWithStatusAndCampus(UUID.fromString(campusUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClassroomFlux
                    .collectList()
                    .flatMap(classroomEntityDB -> slaveClassroomRepository
                            .countIndexRecordsCampusWithStatusFilter(UUID.fromString(campusUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (classroomEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", classroomEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter of campus uuid is present
        else if (!campusUUID.isEmpty()) {
            Flux<SlaveClassroomDto> slaveClassroomFlux = slaveClassroomRepository
                    .indexWithCampusWithoutStatusFilter(UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClassroomFlux
                    .collectList()
                    .flatMap(classroomEntityDB -> slaveClassroomRepository
                            .countIndexRecordsCampusWithoutStatusFilter(UUID.fromString(campusUUID), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (classroomEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", classroomEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if optional parameter of status is present
        else if (!status.isEmpty()) {
            Flux<SlaveClassroomDto> slaveClassroomFlux = slaveClassroomRepository
                    .indexWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClassroomFlux
                    .collectList()
                    .flatMap(classroomEntityDB -> slaveClassroomRepository
                            .countIndexRecordsWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (classroomEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", classroomEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional parameter is present
        else {
            Flux<SlaveClassroomDto> slaveClassroomFlux = slaveClassroomRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveClassroomFlux
                    .collectList()
                    .flatMap(classroomEntityDB -> slaveClassroomRepository
                            .countIndexRecordsWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (classroomEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", classroomEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_classrooms_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID classroomUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveClassroomRepository.showByUuid(classroomUUID)
                .flatMap(classroomEntity -> responseSuccessMsg("Record Fetched Successfully", classroomEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_classrooms_store")
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

                    Integer capacity = null;
                    if ((value.containsKey("capacity") && (!Objects.equals(value.getFirst("capacity"), "")))) {
                        capacity = Integer.valueOf(value.getFirst("capacity"));
                    }

                    ClassroomEntity entity = ClassroomEntity.builder()
                            .uuid(UUID.randomUUID())
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .code(value.getFirst("code").trim())
                            .capacity(capacity)
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


                    return campusRepository.findByUuidAndDeletedAtIsNull(entity.getCampusUUID())
                            .flatMap(campusEntity -> classroomRepository.findFirstByNameIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(entity.getName(), entity.getCampusUUID())
                                    .flatMap(classroomName -> responseInfoMsg("Name already exist"))
                                    .switchIfEmpty(Mono.defer(() -> classroomRepository.findFirstByCodeIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(entity.getCode(), entity.getCampusUUID())
                                            .flatMap(classroomCode -> responseInfoMsg("Code already Exist"))))
                                    .switchIfEmpty(Mono.defer(() -> classroomRepository.save(entity)
                                            .flatMap(classroomEntity -> responseSuccessMsg("Record Stored Successfully", classroomEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please Contact Developer"))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Campus Does not Exist."))
                            .onErrorResume(ex -> responseErrorMsg("Campus Does not exist. Please Contact Developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_classrooms_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID classroomUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> classroomRepository.findByUuidAndDeletedAtIsNull(classroomUUID)
                        .flatMap(entity -> {
                            Integer capacity = null;
                            if ((value.containsKey("capacity") && (!Objects.equals(value.getFirst("capacity"), "")))) {
                                capacity = Integer.valueOf(value.getFirst("capacity"));
                            }
                            ClassroomEntity updatedEntity = ClassroomEntity.builder()
                                    .uuid(entity.getUuid())
                                    .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .code(value.getFirst("code").trim())
                                    .capacity(capacity)
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

                            return campusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCampusUUID())
                                    .flatMap(campusEntity -> classroomRepository.findFirstByNameIgnoreCaseAndCampusUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), updatedEntity.getCampusUUID(), classroomUUID)
                                            .flatMap(classroomName -> responseInfoMsg("Name already exist"))
                                            .switchIfEmpty(Mono.defer(() -> classroomRepository.findFirstByCodeIgnoreCaseAndCampusUUIDAndDeletedAtIsNullAndUuidIsNot
                                                            (updatedEntity.getCode(), updatedEntity.getCampusUUID(), classroomUUID)
                                                    .flatMap(classroomCode -> responseInfoMsg("Code already Exist"))))
                                            .switchIfEmpty(Mono.defer(() -> classroomRepository.save(entity)
                                                    .then(classroomRepository.save(updatedEntity))
                                                    .flatMap(classroomEntity -> responseSuccessMsg("Record Updated Successfully", classroomEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer"))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Campus Does not Exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Campus Does not exist. Please Contact Developer"));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_classrooms_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID classroomUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return classroomRepository.findByUuidAndDeletedAtIsNull(classroomUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                ClassroomEntity entity = ClassroomEntity.builder()
                                        .uuid(val.getUuid())
                                        .campusUUID(val.getCampusUUID())
                                        .name(val.getName())
                                        .description(val.getDescription())
                                        .capacity(val.getCapacity())
                                        .code(val.getCode())
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

                                return classroomRepository.save(val)
                                        .then(classroomRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_classrooms_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID classroomUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return classroomRepository.findByUuidAndDeletedAtIsNull(classroomUUID)
                //check if classroom exists in timetable handler
                .flatMap(classroomEntity -> timetableCreationRepository.findFirstByClassroomUUIDAndDeletedAtIsNull(classroomEntity.getUuid())
                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists"))
                        .switchIfEmpty(Mono.defer(() ->
                        {
                            classroomEntity.setDeletedBy(UUID.fromString(userId));
                            classroomEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            classroomEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            classroomEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            classroomEntity.setReqDeletedIP(reqIp);
                            classroomEntity.setReqDeletedPort(reqPort);
                            classroomEntity.setReqDeletedBrowser(reqBrowser);
                            classroomEntity.setReqDeletedOS(reqOs);
                            classroomEntity.setReqDeletedDevice(reqDevice);
                            classroomEntity.setReqDeletedReferer(reqReferer);

                            return classroomRepository.save(classroomEntity)
                                    .flatMap(saveClassroomEntity -> responseSuccessMsg("Record Deleted Successfully", saveClassroomEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
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
