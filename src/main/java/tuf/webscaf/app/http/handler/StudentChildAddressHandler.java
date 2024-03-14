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
import tuf.webscaf.app.dbContext.master.entity.StudentChildAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentChildAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentChildRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentChildAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentChildAddressHandler")
@Component
public class StudentChildAddressHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentChildAddressRepository studentChildAddressRepository;

    @Autowired
    SlaveStudentChildAddressRepository slaveStudentChildAddressRepository;

    @Autowired
    StudentChildRepository studentChildRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;

    @AuthHasPermission(value = "academic_api_v1_student-child-addresses_index")
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

        // studentChild UUID Query Parameter
        String studentChildUUID = serverRequest.queryParam("studentChildUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() &&! studentChildUUID.isEmpty()) {

            Flux<SlaveStudentChildAddressEntity> slaveStudentChildAddressFlux = slaveStudentChildAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentChildUUID), Boolean.valueOf(status));
            return slaveStudentChildAddressFlux
                    .collectList()
                    .flatMap(studentChildAddressEntity -> slaveStudentChildAddressRepository.countByAddressContainingIgnoreCaseAndStudentChildUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentChildUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentChildAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        else if (!status.isEmpty()) {

            Flux<SlaveStudentChildAddressEntity> slaveStudentChildAddressFlux = slaveStudentChildAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveStudentChildAddressFlux
                    .collectList()
                    .flatMap(studentChildAddressEntity -> slaveStudentChildAddressRepository.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentChildAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        else if (!studentChildUUID.isEmpty()) {

            Flux<SlaveStudentChildAddressEntity> slaveStudentChildAddressFlux = slaveStudentChildAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentChildUUID));
            return slaveStudentChildAddressFlux
                    .collectList()
                    .flatMap(studentChildAddressEntity -> slaveStudentChildAddressRepository.countByAddressContainingIgnoreCaseAndStudentChildUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentChildUUID))
                            .flatMap(count -> {
                                if (studentChildAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        else {

            Flux<SlaveStudentChildAddressEntity> slaveStudentChildAddressFlux = slaveStudentChildAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveStudentChildAddressFlux
                    .collectList()
                    .flatMap(studentChildAddressEntity -> slaveStudentChildAddressRepository.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (studentChildAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentChildAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentChildAddressRepository.findByUuidAndDeletedAtIsNull(studentChildAddressUUID)
                .flatMap(studentChildAddressEntity -> responseSuccessMsg("Record Fetched Successfully", studentChildAddressEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-addresses_store")
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
                    StudentChildAddressEntity entity = StudentChildAddressEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentChildUUID(UUID.fromString(value.getFirst("studentChildUUID").trim()))
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

                    // check address type is unique against student child 
                    return studentChildAddressRepository.findFirstByStudentChildUUIDAndAddressTypeUUIDAndDeletedAtIsNull(entity.getStudentChildUUID(), entity.getAddressTypeUUID())
                            .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
                            //checks if student child  uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentChildRepository.findByUuidAndDeletedAtIsNull(entity.getStudentChildUUID())
                                    //checks if address type uuid exists
                                    .flatMap(studentChildEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(entity.getAddressTypeUUID())
                                            .flatMap(addressTypeEntity -> studentChildAddressRepository.save(entity)
                                                    .flatMap(studentChildAddressEntity -> responseSuccessMsg("Record Stored Successfully", studentChildAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Address Type does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Student Child record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student Child record does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentChildAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentChildAddressRepository.findByUuidAndDeletedAtIsNull(studentChildAddressUUID)
                        .flatMap(entity -> {

                            StudentChildAddressEntity updatedEntity = StudentChildAddressEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentChildUUID(entity.getStudentChildUUID())
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

                            // check address type is unique against student child 
                            return studentChildAddressRepository.findFirstByStudentChildUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentChildUUID(), updatedEntity.getAddressTypeUUID(), studentChildAddressUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
                                    //checks if student child  uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentChildRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentChildUUID())
                                            //checks if address type uuid exists
                                            .flatMap(studentChildEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAddressTypeUUID())
                                                    .flatMap(addressTypeEntity -> studentChildAddressRepository.save(entity)
                                                            .then(studentChildAddressRepository.save(updatedEntity))
                                                            .flatMap(studentChildAddressEntity -> responseSuccessMsg("Record Updated Successfully", studentChildAddressEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student Child record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Child record does not exist. Please contact developer."))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentChildAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentChildAddressRepository.findByUuidAndDeletedAtIsNull(studentChildAddressUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentChildAddressEntity entity = StudentChildAddressEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentChildUUID(val.getStudentChildUUID())
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

                                return studentChildAddressRepository.save(val)
                                        .then(studentChildAddressRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentChildAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentChildAddressRepository.findByUuidAndDeletedAtIsNull(studentChildAddressUUID)
                .flatMap(studentChildAddressEntity -> {

                    studentChildAddressEntity.setDeletedBy(UUID.fromString(userId));
                    studentChildAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentChildAddressEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentChildAddressEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentChildAddressEntity.setReqDeletedIP(reqIp);
                    studentChildAddressEntity.setReqDeletedPort(reqPort);
                    studentChildAddressEntity.setReqDeletedBrowser(reqBrowser);
                    studentChildAddressEntity.setReqDeletedOS(reqOs);
                    studentChildAddressEntity.setReqDeletedDevice(reqDevice);
                    studentChildAddressEntity.setReqDeletedReferer(reqReferer);

                    return studentChildAddressRepository.save(studentChildAddressEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
