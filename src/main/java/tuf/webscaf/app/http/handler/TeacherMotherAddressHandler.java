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
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherMotherAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherMotherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherMotherAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherMotherAddressHandler")
@Component
public class TeacherMotherAddressHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherMotherAddressRepository teacherMotherAddressRepository;

    @Autowired
    SlaveTeacherMotherAddressRepository slaveTeacherMotherAddressRepository;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-addresses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher UUID
        String teacherMotherUUID = serverRequest.queryParam("teacherMotherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !teacherMotherUUID.isEmpty()) {

            Flux<SlaveTeacherMotherAddressEntity> slaveTeacherMotherAddressFlux = slaveTeacherMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherMotherUUID), Boolean.valueOf(status));
            return slaveTeacherMotherAddressFlux
                    .collectList()
                    .flatMap(teacherMotherAddressEntity -> slaveTeacherMotherAddressRepository.countByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherMotherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        else if (!status.isEmpty()) {

            Flux<SlaveTeacherMotherAddressEntity> slaveTeacherMotherAddressFlux = slaveTeacherMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherMotherAddressFlux
                    .collectList()
                    .flatMap(teacherMotherAddressEntity -> slaveTeacherMotherAddressRepository.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        else if (!teacherMotherUUID.isEmpty()) {

            Flux<SlaveTeacherMotherAddressEntity> slaveTeacherMotherAddressFlux = slaveTeacherMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherMotherUUID));
            return slaveTeacherMotherAddressFlux
                    .collectList()
                    .flatMap(teacherMotherAddressEntity -> slaveTeacherMotherAddressRepository.countByAddressContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherMotherUUID))
                            .flatMap(count -> {
                                if (teacherMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        else {

            Flux<SlaveTeacherMotherAddressEntity> slaveTeacherMotherAddressFlux = slaveTeacherMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveTeacherMotherAddressFlux
                    .collectList()
                    .flatMap(teacherMotherAddressEntity -> slaveTeacherMotherAddressRepository.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (teacherMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherMotherAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherMotherAddressRepository.findByUuidAndDeletedAtIsNull(teacherMotherAddressUUID)
                .flatMap(teacherMotherAddressEntity -> responseSuccessMsg("Record Fetched Successfully", teacherMotherAddressEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-addresses_store")
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

                    TeacherMotherAddressEntity entity = TeacherMotherAddressEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherMotherUUID(UUID.fromString(value.getFirst("teacherMotherUUID").trim()))
                            .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID").trim()))
                            .address(value.getFirst("address").trim())
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

                    //checks if teacher mother uuid exists
                    return teacherMotherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMotherUUID())
                            //checks if address type uuid exists
                            .flatMap(teacherMotherEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(entity.getAddressTypeUUID())
                                    .flatMap(addressTypeEntity -> teacherMotherAddressRepository.findFirstByTeacherMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(entity.getTeacherMotherUUID(), entity.getAddressTypeUUID())
                                            .flatMap(checkMsg -> responseInfoMsg("Address Type already exists against teacher Mother Record"))
                                            .switchIfEmpty(Mono.defer(() -> teacherMotherAddressRepository.save(entity)
                                                    .flatMap(teacherMotherAddressEntity -> responseSuccessMsg("Record Stored Successfully", teacherMotherAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist.Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Teacher Mother Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Mother Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherMotherAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherMotherAddressRepository.findByUuidAndDeletedAtIsNull(teacherMotherAddressUUID)
                        .flatMap(entity -> {

                            TeacherMotherAddressEntity updatedEntity = TeacherMotherAddressEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherMotherUUID(entity.getTeacherMotherUUID())
                                    .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID").trim()))
                                    .address(value.getFirst("address").trim())
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

                            //checks if address type uuid exists
                            return addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAddressTypeUUID())
                                    .flatMap(addressTypeEntity -> teacherMotherAddressRepository.findFirstByTeacherMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(entity.getTeacherMotherUUID(), entity.getAddressTypeUUID(), teacherMotherAddressUUID)
                                            .flatMap(checkMsg -> responseInfoMsg("Address Type already exists against teacher Mother Record"))
                                            .switchIfEmpty(Mono.defer(() -> teacherMotherAddressRepository.save(entity)
                                                    .then(teacherMotherAddressRepository.save(updatedEntity))
                                                    .flatMap(teacherMotherAddressEntity -> responseSuccessMsg("Record Updated Successfully", teacherMotherAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist.Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherMotherAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherMotherAddressRepository.findByUuidAndDeletedAtIsNull(teacherMotherAddressUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherMotherAddressEntity entity = TeacherMotherAddressEntity.builder()
                                        .uuid(val.getUuid())
                                        .teacherMotherUUID(val.getTeacherMotherUUID())
                                        .addressTypeUUID(val.getAddressTypeUUID())
                                        .address(val.getAddress())
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

                                return teacherMotherAddressRepository.save(val)
                                        .then(teacherMotherAddressRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherMotherAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherMotherAddressRepository.findByUuidAndDeletedAtIsNull(teacherMotherAddressUUID)
                .flatMap(teacherMotherAddressEntity -> {
                    teacherMotherAddressEntity.setDeletedBy(UUID.fromString(userId));
                    teacherMotherAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherMotherAddressEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherMotherAddressEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherMotherAddressEntity.setReqDeletedIP(reqIp);
                    teacherMotherAddressEntity.setReqDeletedPort(reqPort);
                    teacherMotherAddressEntity.setReqDeletedBrowser(reqBrowser);
                    teacherMotherAddressEntity.setReqDeletedOS(reqOs);
                    teacherMotherAddressEntity.setReqDeletedDevice(reqDevice);
                    teacherMotherAddressEntity.setReqDeletedReferer(reqReferer);

                    return teacherMotherAddressRepository.save(teacherMotherAddressEntity)
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
