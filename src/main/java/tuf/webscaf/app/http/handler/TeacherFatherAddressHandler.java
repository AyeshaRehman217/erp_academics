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
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherFatherAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherFatherAddressHandler")
@Component
public class TeacherFatherAddressHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherFatherAddressRepository teacherFatherAddressRepository;

    @Autowired
    SlaveTeacherFatherAddressRepository slaveTeacherFatherAddressRepo;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;


    @AuthHasPermission(value = "academic_api_v1_teacher-father-addresses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Father UUID
        String teacherFatherUUID = serverRequest.queryParam("teacherFatherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !teacherFatherUUID.isEmpty()) {

            Flux<SlaveTeacherFatherAddressEntity> slaveTeacherFatherEntityFlux = slaveTeacherFatherAddressRepo
                    .findAllByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status));

            return slaveTeacherFatherEntityFlux
                    .collectList()
                    .flatMap(teacherFatherEntityDB -> slaveTeacherFatherAddressRepo.countByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherFatherEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherFatherAddressEntity> slaveTeacherFatherEntityFlux = slaveTeacherFatherAddressRepo
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));

            return slaveTeacherFatherEntityFlux
                    .collectList()
                    .flatMap(teacherFatherEntityDB -> slaveTeacherFatherAddressRepo.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherFatherEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!teacherFatherUUID.isEmpty()) {

            Flux<SlaveTeacherFatherAddressEntity> slaveTeacherFatherEntityFlux = slaveTeacherFatherAddressRepo
                    .findAllByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherFatherUUID));

            return slaveTeacherFatherEntityFlux
                    .collectList()
                    .flatMap(teacherFatherEntityDB -> slaveTeacherFatherAddressRepo.countByAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherFatherUUID))
                            .flatMap(count -> {
                                if (teacherFatherEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherFatherAddressEntity> slaveTeacherFatherEntityFlux = slaveTeacherFatherAddressRepo
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);

            return slaveTeacherFatherEntityFlux
                    .collectList()
                    .flatMap(teacherFatherEntityDB -> slaveTeacherFatherAddressRepo.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (teacherFatherEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherFatherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherFatherAddressRepo.findByUuidAndDeletedAtIsNull(teacherFatherAddressUUID)
                .flatMap(teacherFatherAddressEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFatherAddressEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-addresses_store")
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

                    TeacherFatherAddressEntity entity = TeacherFatherAddressEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherFatherUUID(UUID.fromString(value.getFirst("teacherFatherUUID").trim()))
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

                    //checks if teacher father uuid exists
                    return teacherFatherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherFatherUUID())
                            //checks if address type uuid exists
                            .flatMap(teacherFatherEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(entity.getAddressTypeUUID())
                                    .flatMap(addressTypeEntity -> teacherFatherAddressRepository.findFirstByTeacherFatherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(entity.getTeacherFatherUUID(), entity.getAddressTypeUUID())
                                            .flatMap(checkMsg -> responseInfoMsg("Address Type already exists against teacher Father "))
                                            .switchIfEmpty(Mono.defer(() -> teacherFatherAddressRepository.save(entity)
                                                    .flatMap(teacherMotherAddressEntity -> responseSuccessMsg("Record Stored Successfully", teacherMotherAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to Stored record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to Stored record. Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist.Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Teacher Father Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Father Father Record does not exist. Please contact developer."));
                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherFatherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherFatherAddressRepository.findByUuidAndDeletedAtIsNull(teacherFatherAddressUUID)
                        .flatMap(entity -> {

                            TeacherFatherAddressEntity updatedEntity = TeacherFatherAddressEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherFatherUUID(entity.getTeacherFatherUUID())
                                    .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID").trim()))
                                    .address(value.getFirst("address").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
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

                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            //checks if address type uuid exists
                            return addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAddressTypeUUID())
                                    .flatMap(addressTypeEntity -> teacherFatherAddressRepository.findFirstByTeacherFatherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(entity.getTeacherFatherUUID(), entity.getAddressTypeUUID(), teacherFatherAddressUUID)
                                            .flatMap(checkMsg -> responseInfoMsg("Address Type already exists against teacher Father "))
                                            .switchIfEmpty(Mono.defer(() -> teacherFatherAddressRepository.save(entity)
                                                    .then(teacherFatherAddressRepository.save(updatedEntity))
                                                    .flatMap(teacherMotherAddressEntity -> responseSuccessMsg("Record Updated Successfully", teacherMotherAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.here is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist.Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherFatherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return teacherFatherAddressRepository.findByUuidAndDeletedAtIsNull(teacherFatherAddressUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherFatherAddressEntity updatedEntity = TeacherFatherAddressEntity.builder()
                                        .uuid(val.getUuid())
                                        .status(status == true ? true : false)
                                        .teacherFatherUUID(val.getTeacherFatherUUID())
                                        .addressTypeUUID(val.getAddressTypeUUID())
                                        .address(val.getAddress())
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
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setDeletedBy(UUID.fromString(userId));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return teacherFatherAddressRepository.save(val)
                                        .then(teacherFatherAddressRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherFatherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherFatherAddressRepository.findByUuidAndDeletedAtIsNull(teacherFatherAddressUUID)
                .flatMap(teacherAddressEntity -> {

                    teacherAddressEntity.setDeletedBy(UUID.fromString(userId));
                    teacherAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherAddressEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherAddressEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherAddressEntity.setReqDeletedIP(reqIp);
                    teacherAddressEntity.setReqDeletedPort(reqPort);
                    teacherAddressEntity.setReqDeletedBrowser(reqBrowser);
                    teacherAddressEntity.setReqDeletedOS(reqOs);
                    teacherAddressEntity.setReqDeletedDevice(reqDevice);

                    return teacherFatherAddressRepository.save(teacherAddressEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .onErrorResume(err -> responseErrorMsg("Unable to Delete Record.There is something wrong please Try Again."))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
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
