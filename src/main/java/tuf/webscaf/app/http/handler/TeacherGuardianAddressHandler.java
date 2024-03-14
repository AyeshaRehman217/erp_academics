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
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherGuardianAddressHandler")
@Component
public class TeacherGuardianAddressHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianAddressRepository teacherGuardianAddressRepository;

    @Autowired
    SlaveTeacherGuardianAddressRepository slaveTeacherGuardianAddressRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-addresses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Guardian UUID
        String teacherGuardianUUID = serverRequest.queryParam("teacherGuardianUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianAddressEntity> slaveTeacherGuardianAddressFlux = slaveTeacherGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status));
            return slaveTeacherGuardianAddressFlux
                    .collectList()
                    .flatMap(teacherGuardianAddressEntity -> slaveTeacherGuardianAddressRepository.countByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherGuardianAddressEntity> slaveTeacherGuardianAddressFlux = slaveTeacherGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherGuardianAddressFlux
                    .collectList()
                    .flatMap(teacherGuardianAddressEntity -> slaveTeacherGuardianAddressRepository.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianAddressEntity> slaveTeacherGuardianAddressFlux = slaveTeacherGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID));
            return slaveTeacherGuardianAddressFlux
                    .collectList()
                    .flatMap(teacherGuardianAddressEntity -> slaveTeacherGuardianAddressRepository.countByAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID))
                            .flatMap(count -> {
                                if (teacherGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherGuardianAddressEntity> slaveTeacherGuardianAddressFlux = slaveTeacherGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveTeacherGuardianAddressFlux
                    .collectList()
                    .flatMap(teacherGuardianAddressEntity -> slaveTeacherGuardianAddressRepository.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (teacherGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherGuardianAddressRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAddressUUID)
                .flatMap(teacherGuardianAddressEntity -> responseSuccessMsg("Record Fetched Successfully", teacherGuardianAddressEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-addresses_store")
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
                //checks if teacher guardian uuid exists
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                        .flatMap(teacherGuardianEntity -> {

                            // if teacher guardian uuid is already set
                            if (teacherGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Address. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
                                TeacherGuardianAddressEntity entity = TeacherGuardianAddressEntity.builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherGuardianUUID(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
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

                                //checks if address type uuid exists
                                return addressTypeRepository.findByUuidAndDeletedAtIsNull(entity.getAddressTypeUUID())
                                        .flatMap(addressTypeEntity -> teacherGuardianAddressRepository.findFirstByTeacherGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNull(entity.getTeacherGuardianUUID(), entity.getAddressTypeUUID())
                                                .flatMap(checkMsg -> responseInfoMsg("Address Type already exists against Teacher Guardian"))
                                                .switchIfEmpty(Mono.defer(() -> teacherGuardianAddressRepository.save(entity)
                                                        .flatMap(teacherGuardianAddressEntity -> responseSuccessMsg("Record Stored Successfully", teacherGuardianAddressEntity))
                                                        .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record.Please contact developer."))
                                                ))
                                        ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                        .onErrorResume(ex -> responseErrorMsg("Address Type does not exist.Please contact developer."));
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Guardian does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Guardian does not exist.Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherGuardianAddressRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAddressUUID)
                        .flatMap(entity -> {

                            TeacherGuardianAddressEntity updatedEntity = TeacherGuardianAddressEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherGuardianUUID(entity.getTeacherGuardianUUID())
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

                            // checks if address type uuid exists
                            return addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAddressTypeUUID())
                                    .flatMap(addressTypeEntity -> teacherGuardianAddressRepository.findFirstByTeacherGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(entity.getTeacherGuardianUUID(), entity.getAddressTypeUUID(), teacherGuardianAddressUUID)
                                            .flatMap(checkMsg -> responseInfoMsg("Address Type already exists against Teacher Guardian"))
                                            .switchIfEmpty(Mono.defer(() -> teacherGuardianAddressRepository.save(entity)
                                                    .then(teacherGuardianAddressRepository.save(updatedEntity))
                                                    .flatMap(teacherGuardianAddressEntity -> responseSuccessMsg("Record Updated Successfully", teacherGuardianAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.There is something wrong please try again."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist.Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherGuardianAddressRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAddressUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherGuardianAddressEntity entity = TeacherGuardianAddressEntity.builder()
                                        .uuid(val.getUuid())
                                        .teacherGuardianUUID(val.getTeacherGuardianUUID())
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

                                return teacherGuardianAddressRepository.save(val)
                                        .then(teacherGuardianAddressRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherGuardianAddressRepository.findByUuidAndDeletedAtIsNull(teacherGuardianAddressUUID)
                .flatMap(teacherGuardianAddressEntity -> {
                    teacherGuardianAddressEntity.setDeletedBy(UUID.fromString(userId));
                    teacherGuardianAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherGuardianAddressEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherGuardianAddressEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherGuardianAddressEntity.setReqDeletedIP(reqIp);
                    teacherGuardianAddressEntity.setReqDeletedPort(reqPort);
                    teacherGuardianAddressEntity.setReqDeletedBrowser(reqBrowser);
                    teacherGuardianAddressEntity.setReqDeletedOS(reqOs);
                    teacherGuardianAddressEntity.setReqDeletedDevice(reqDevice);
                    teacherGuardianAddressEntity.setReqDeletedReferer(reqReferer);

                    return teacherGuardianAddressRepository.save(teacherGuardianAddressEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
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
