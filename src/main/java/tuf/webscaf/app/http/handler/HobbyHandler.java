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
import tuf.webscaf.app.dbContext.master.entity.HobbyEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHobbyRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "hobbyHandler")
@Component
public class HobbyHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    HobbyRepository hobbyRepository;

    @Autowired
    SlaveHobbyRepository slaveHobbyRepository;

    @Autowired
    StudentHobbyPvtRepository studentHobbyPvtRepository;

    @Autowired
    StudentMotherHobbyPvtRepository studentMotherHobbyPvtRepository;

    @Autowired
    StudentFatherHobbyPvtRepository studentFatherHobbyPvtRepository;

    @Autowired
    StudentGuardianHobbyPvtRepository studentGuardianHobbyPvtRepository;

    @Autowired
    TeacherHobbyPvtRepository teacherHobbyPvtRepository;

    @Autowired
    TeacherMotherHobbyPvtRepository teacherMotherHobbyPvtRepository;

    @Autowired
    TeacherFatherHobbyPvtRepository teacherFatherHobbyPvtRepository;

    @Autowired
    TeacherSiblingHobbyPvtRepository teacherSiblingHobbyPvtRepository;

    @Autowired
    TeacherChildHobbyPvtRepository teacherChildHobbyPvtRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_hobbies_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveHobbyEntity> slaveHobbyFlux = slaveHobbyRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveHobbyFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbyFlux = slaveHobbyRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveHobbyFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_hobbies_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID HobbyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveHobbyRepository.findByUuidAndDeletedAtIsNull(HobbyUUID)
                .flatMap(hobbyEntity -> responseSuccessMsg("Record Fetched Successfully", hobbyEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_hobbies_store")
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

                    HobbyEntity entity = HobbyEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
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

                    //check name is unique
                    return hobbyRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                            .flatMap(hobbyEntity -> responseInfoMsg("Name Already Exist"))
                            .switchIfEmpty(Mono.defer(() -> hobbyRepository.save(entity)
                                    .flatMap(hobbyEntity -> responseSuccessMsg("Record Stored Successfully", hobbyEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_hobbies_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID HobbyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> hobbyRepository.findByUuidAndDeletedAtIsNull(HobbyUUID)
                                .flatMap(entity -> {

                                    HobbyEntity updatedEntity = HobbyEntity.builder()
                                            .uuid(entity.getUuid())
                                            .name(value.getFirst("name").trim())
                                            .description(value.getFirst("description").trim())
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

//                            check name is unique
                                    return hobbyRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), HobbyUUID)
                                            .flatMap(nameExists -> responseInfoMsg("Name Already Exists"))
                                            .switchIfEmpty(Mono.defer(() ->
                                                    hobbyRepository.save(entity)
                                                            .then(hobbyRepository.save(updatedEntity))
                                                            .flatMap(hobbyEntity -> responseSuccessMsg("Record Updated Successfully", hobbyEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ));
                                })
                                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_hobbies_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID HobbyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return hobbyRepository.findByUuidAndDeletedAtIsNull(HobbyUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                HobbyEntity entity = HobbyEntity.builder()
                                        .uuid(val.getUuid())
                                        .name(val.getName())
                                        .description(val.getDescription())
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

                                return hobbyRepository.save(val)
                                        .then(hobbyRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_hobbies_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID HobbyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return hobbyRepository.findByUuidAndDeletedAtIsNull(HobbyUUID)
//        If hobby Reference exists in studentProfileHobbyPvt
                .flatMap(hobbyEntity -> studentHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                .flatMap(studentProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))
//                        If hobby Reference exists in studentMotherProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> studentMotherHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(studentMotherProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in studentFatherProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> studentFatherHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(studentFatherProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in studentGuardianProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> studentGuardianHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(studentGuardianProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in teacherProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> teacherHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(teacherProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in teacherMotherProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> teacherMotherHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(teacherMotherProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in teacherFatherProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> teacherFatherHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(teacherFatherProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in teacherSiblingProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> teacherSiblingHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(teacherSiblingProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
//                        If hobby Reference exists in teacherChildProfileHobbyPvt
                                .switchIfEmpty(Mono.defer(() -> teacherChildHobbyPvtRepository.findFirstByHobbyUUIDAndDeletedAtIsNull(hobbyEntity.getUuid())
                                        .flatMap(teacherChildProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete! Reference of record exists."))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    hobbyEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    hobbyEntity.setDeletedBy(UUID.fromString(userId));
                                    hobbyEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    hobbyEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    hobbyEntity.setReqDeletedIP(reqIp);
                                    hobbyEntity.setReqDeletedPort(reqPort);
                                    hobbyEntity.setReqDeletedBrowser(reqBrowser);
                                    hobbyEntity.setReqDeletedOS(reqOs);
                                    hobbyEntity.setReqDeletedDevice(reqDevice);
                                    hobbyEntity.setReqDeletedReferer(reqReferer);

                                    return hobbyRepository.save(hobbyEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to deleted record")).onErrorResume(ex -> responseErrorMsg("Unable to deleted record"));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
