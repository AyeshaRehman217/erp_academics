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
import tuf.webscaf.app.dbContext.master.entity.StudentMotherAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentMotherAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentMotherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentMotherAddressHandler")
@Component
public class StudentMotherAddressHandler {

    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentMotherAddressRepository studentMotherAddressRepository;

    @Autowired
    SlaveStudentMotherAddressRepository slaveStudentMotherAddressRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;

    @AuthHasPermission(value = "academic_api_v1_student-mother-addresses_index")
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

            Flux<SlaveStudentMotherAddressEntity> slaveStudentMotherAddressFlux = slaveStudentMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status));
            return slaveStudentMotherAddressFlux
                    .collectList()
                    .flatMap(studentMotherAddressEntity -> slaveStudentMotherAddressRepository.countByAddressContainingIgnoreCaseAndStudentMotherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentMotherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        else if (!status.isEmpty()) {

            Flux<SlaveStudentMotherAddressEntity> slaveStudentMotherAddressFlux = slaveStudentMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveStudentMotherAddressFlux
                    .collectList()
                    .flatMap(studentMotherAddressEntity -> slaveStudentMotherAddressRepository.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        else if (!studentMotherUUID.isEmpty()) {

            Flux<SlaveStudentMotherAddressEntity> slaveStudentMotherAddressFlux = slaveStudentMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentMotherUUID));
            return slaveStudentMotherAddressFlux
                    .collectList()
                    .flatMap(studentMotherAddressEntity -> slaveStudentMotherAddressRepository.countByAddressContainingIgnoreCaseAndStudentMotherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentMotherUUID))
                            .flatMap(count -> {
                                if (studentMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
        else {

            Flux<SlaveStudentMotherAddressEntity> slaveStudentMotherAddressFlux = slaveStudentMotherAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveStudentMotherAddressFlux
                    .collectList()
                    .flatMap(studentMotherAddressEntity -> slaveStudentMotherAddressRepository.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (studentMotherAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentMotherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherAddressRepository.findByUuidAndDeletedAtIsNull(studentMotherAddressUUID)
                .flatMap(studentMotherAddressEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherAddressEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-addresses_store")
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
                    StudentMotherAddressEntity entity = StudentMotherAddressEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentMotherUUID(UUID.fromString(value.getFirst("studentMotherUUID")))
                            .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID")))
                            .address(value.getFirst("address"))
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

                    // check address type is unique against student mother profile
                    return studentMotherAddressRepository.findFirstByStudentMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNull(entity.getStudentMotherUUID(), entity.getAddressTypeUUID())
                            .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
                            //checks if student mother profile uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentMotherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMotherUUID())
                                    //checks if address type uuid exists
                                    .flatMap(studentMotherEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(entity.getAddressTypeUUID())
                                            .flatMap(addressTypeEntity -> studentMotherAddressRepository.save(entity)
                                                    .flatMap(studentMotherAddressEntity -> responseSuccessMsg("Record Stored Successfully", studentMotherAddressEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Address Type does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Student Mother  does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student Mother  does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentMotherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentMotherAddressRepository.findByUuidAndDeletedAtIsNull(studentMotherAddressUUID)
                        .flatMap(entity -> {

                            StudentMotherAddressEntity updatedEntity = StudentMotherAddressEntity.builder()
                                    .uuid(entity.getUuid())
                                    .studentMotherUUID(entity.getStudentMotherUUID())
                                    .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID")))
                                    .address(value.getFirst("address"))
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

                            // check address type is unique against student mother profile
                            return studentMotherAddressRepository.findFirstByStudentMotherUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentMotherUUID(), updatedEntity.getAddressTypeUUID(), studentMotherAddressUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
                                    //checks if student mother profile uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentMotherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMotherUUID())
                                            //checks if address type uuid exists
                                            .flatMap(studentMotherEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAddressTypeUUID())
                                                    .flatMap(addressTypeEntity -> studentMotherAddressRepository.save(entity)
                                                            .then(studentMotherAddressRepository.save(updatedEntity))
                                                            .flatMap(studentMotherAddressEntity -> responseSuccessMsg("Record Updated Successfully", studentMotherAddressEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Address Type does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Address Type does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student Mother  does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Mother  does not exist. Please contact developer."))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentMotherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentMotherAddressRepository.findByUuidAndDeletedAtIsNull(studentMotherAddressUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentMotherAddressEntity entity = StudentMotherAddressEntity.builder()
                                        .uuid(val.getUuid())
                                        .studentMotherUUID(val.getStudentMotherUUID())
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

                                return studentMotherAddressRepository.save(val)
                                        .then(studentMotherAddressRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentMotherAddressUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentMotherAddressRepository.findByUuidAndDeletedAtIsNull(studentMotherAddressUUID)
                .flatMap(studentMotherAddressEntity -> {

                    studentMotherAddressEntity.setDeletedBy(UUID.fromString(userId));
                    studentMotherAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentMotherAddressEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentMotherAddressEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentMotherAddressEntity.setReqDeletedIP(reqIp);
                    studentMotherAddressEntity.setReqDeletedPort(reqPort);
                    studentMotherAddressEntity.setReqDeletedBrowser(reqBrowser);
                    studentMotherAddressEntity.setReqDeletedOS(reqOs);
                    studentMotherAddressEntity.setReqDeletedDevice(reqDevice);
                    studentMotherAddressEntity.setReqDeletedReferer(reqReferer);

                    return studentMotherAddressRepository.save(studentMotherAddressEntity)
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
