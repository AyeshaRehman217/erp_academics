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
import tuf.webscaf.app.dbContext.master.entity.StudentAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentAddressHandler")
@Component
public class StudentAddressHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentAddressRepository studentAddressRepository;

    @Autowired
    SlaveStudentAddressRepository slaveStudentAddressRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;

    @AuthHasPermission(value = "academic_api_v1_student-addresses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Student UUID Query Parameter
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentUUID.isEmpty()) {

            Flux<SlaveStudentAddressEntity> slaveStudentEntityFlux = slaveStudentAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentUUID), Boolean.valueOf(status));

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntityDB -> slaveStudentAddressRepository.countByAddressContainingIgnoreCaseAndStudentUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentAddressEntity> slaveStudentEntityFlux = slaveStudentAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntityDB -> slaveStudentAddressRepository.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentUUID.isEmpty()) {

            Flux<SlaveStudentAddressEntity> slaveStudentEntityFlux = slaveStudentAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentUUID));

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntityDB -> slaveStudentAddressRepository.countByAddressContainingIgnoreCaseAndStudentUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentUUID))
                            .flatMap(count -> {
                                if (studentEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentAddressEntity> slaveStudentEntityFlux = slaveStudentAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);

            return slaveStudentEntityFlux
                    .collectList()
                    .flatMap(studentEntityDB -> slaveStudentAddressRepository.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (studentEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentAddressRepository.findByUuidAndDeletedAtIsNull(studentAddressUUID)
                .flatMap(addressEntity -> responseSuccessMsg("Record Fetched Successfully", addressEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-addresses_store")
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

                    StudentAddressEntity stdAddressEntity = StudentAddressEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
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

                    // check address type is unique against student
                    return studentAddressRepository.findFirstByStudentUUIDAndAddressTypeUUIDAndDeletedAtIsNull(stdAddressEntity.getStudentUUID(), stdAddressEntity.getAddressTypeUUID())
                            .flatMap(checkAddressTypeMsg -> responseInfoMsg("Address Type already exist"))
//                            check student uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(stdAddressEntity.getStudentUUID())
//                          check address type uuid exists
                                            .flatMap(studentEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(stdAddressEntity.getAddressTypeUUID())
                                                    .flatMap(checkAddress -> studentAddressRepository.save(stdAddressEntity)
                                                            .flatMap(studentAddressEntity -> responseSuccessMsg("Record Stored Successfully", studentAddressEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong Please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                                    ).switchIfEmpty(responseInfoMsg("Address Type Does not Exist."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Student record does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Student record does not exist.Please contact developer"))
                            ));
                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_student-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentAddressRepository.findByUuidAndDeletedAtIsNull(studentAddressUUID)
                        .flatMap(previousStdAddressEntity -> {

                            StudentAddressEntity updatedStdAddressEntity = StudentAddressEntity.builder()
                                    .uuid(previousStdAddressEntity.getUuid())
                                    .studentUUID(previousStdAddressEntity.getStudentUUID())
                                    .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID").trim()))
                                    .address(value.getFirst("address").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousStdAddressEntity.getCreatedAt())
                                    .createdBy(previousStdAddressEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousStdAddressEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStdAddressEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStdAddressEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStdAddressEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStdAddressEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStdAddressEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousStdAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStdAddressEntity.setDeletedBy(UUID.fromString(userId));
                            previousStdAddressEntity.setReqDeletedIP(reqIp);
                            previousStdAddressEntity.setReqDeletedPort(reqPort);
                            previousStdAddressEntity.setReqDeletedBrowser(reqBrowser);
                            previousStdAddressEntity.setReqDeletedOS(reqOs);
                            previousStdAddressEntity.setReqDeletedDevice(reqDevice);
                            previousStdAddressEntity.setReqDeletedReferer(reqReferer);

                            // check address type is unique against student
                            return studentAddressRepository.findFirstByStudentUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStdAddressEntity.getStudentUUID(), updatedStdAddressEntity.getAddressTypeUUID(), studentAddressUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
                                    // check student uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedStdAddressEntity.getStudentUUID())
                                            //check address type uuid exists
                                            .flatMap(studentEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedStdAddressEntity.getAddressTypeUUID())
                                                    .flatMap(addressTypeEntity ->
                                                            studentAddressRepository.save(previousStdAddressEntity)
                                                                    .then(studentAddressRepository.save(updatedStdAddressEntity))
                                                                    .flatMap(studentAddressEntityDB -> responseSuccessMsg("Record Updated Successfully", studentAddressEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong Please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Address Type Does not exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Address Type Does not exist.Please Contact Developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student record does not exist."))
                                            .onErrorResume(err -> responseErrorMsg("Student record does not exist. Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

                    return studentAddressRepository.findByUuidAndDeletedAtIsNull(studentAddressUUID)
                            .flatMap(previousStdAddressEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousStdAddressEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentAddressEntity updatedStdAddressEntity = StudentAddressEntity.builder()
                                        .uuid(previousStdAddressEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .studentUUID(previousStdAddressEntity.getStudentUUID())
                                        .addressTypeUUID(previousStdAddressEntity.getAddressTypeUUID())
                                        .address(previousStdAddressEntity.getAddress())
                                        .createdAt(previousStdAddressEntity.getCreatedAt())
                                        .createdBy(previousStdAddressEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousStdAddressEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousStdAddressEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousStdAddressEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousStdAddressEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousStdAddressEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousStdAddressEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                previousStdAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousStdAddressEntity.setDeletedBy(UUID.fromString(userId));
                                previousStdAddressEntity.setReqDeletedIP(reqIp);
                                previousStdAddressEntity.setReqDeletedPort(reqPort);
                                previousStdAddressEntity.setReqDeletedBrowser(reqBrowser);
                                previousStdAddressEntity.setReqDeletedOS(reqOs);
                                previousStdAddressEntity.setReqDeletedDevice(reqDevice);
                                previousStdAddressEntity.setReqDeletedReferer(reqReferer);

                                return studentAddressRepository.save(previousStdAddressEntity)
                                        .then(studentAddressRepository.save(updatedStdAddressEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentAddressRepository.findByUuidAndDeletedAtIsNull(studentAddressUUID)
                .flatMap(studentAddressEntity -> {

                    studentAddressEntity.setDeletedBy(UUID.fromString(userId));
                    studentAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentAddressEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentAddressEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentAddressEntity.setReqDeletedIP(reqIp);
                    studentAddressEntity.setReqDeletedPort(reqPort);
                    studentAddressEntity.setReqDeletedBrowser(reqBrowser);
                    studentAddressEntity.setReqDeletedOS(reqOs);
                    studentAddressEntity.setReqDeletedDevice(reqDevice);
                    studentAddressEntity.setReqDeletedReferer(reqReferer);

                    return studentAddressRepository.save(studentAddressEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record.There is something wrong please try again."))
                            .onErrorResume(err -> responseErrorMsg("Unable to Delete Record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
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
