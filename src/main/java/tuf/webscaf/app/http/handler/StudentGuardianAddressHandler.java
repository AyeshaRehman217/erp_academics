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
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianAddressEntity;
import tuf.webscaf.app.dbContext.master.repositry.AddressTypeRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianAddressRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianAddressEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianAddressRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentGuardianAddressesHandler")
@Component
public class

StudentGuardianAddressHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGuardianAddressRepository studentGuardianAddressesRepository;

    @Autowired
    SlaveStudentGuardianAddressRepository slaveStudentGuardianAddressRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    AddressTypeRepository addressTypeRepository;


    @AuthHasPermission(value = "academic_api_v1_student-guardian-addresses_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Guardian UUID
        String studentGuardianUUID = serverRequest.queryParam("studentGuardianUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentGuardianUUID.isEmpty()) {

            Flux<SlaveStudentGuardianAddressEntity> slaveStudentGuardianAddressFlux = slaveStudentGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status));
            return slaveStudentGuardianAddressFlux
                    .collectList()
                    .flatMap(studentGuardianAddressEntity -> slaveStudentGuardianAddressRepository.countByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentGuardianAddressEntity> slaveStudentGuardianAddressFlux = slaveStudentGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveStudentGuardianAddressFlux
                    .collectList()
                    .flatMap(studentGuardianAddressEntity -> slaveStudentGuardianAddressRepository.countByAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentGuardianUUID.isEmpty()) {

            Flux<SlaveStudentGuardianAddressEntity> slaveStudentGuardianAddressFlux = slaveStudentGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID));
            return slaveStudentGuardianAddressFlux
                    .collectList()
                    .flatMap(studentGuardianAddressEntity -> slaveStudentGuardianAddressRepository.countByAddressContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID))
                            .flatMap(count -> {
                                if (studentGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentGuardianAddressEntity> slaveStudentGuardianAddressFlux = slaveStudentGuardianAddressRepository
                    .findAllByAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveStudentGuardianAddressFlux
                    .collectList()
                    .flatMap(studentGuardianAddressEntity -> slaveStudentGuardianAddressRepository.countByAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (studentGuardianAddressEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianAddressEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-addresses_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentGuardianAddressRepository.findByUuidAndDeletedAtIsNull(studentGuardianAddressUUID)
                .flatMap(addressEntity -> responseSuccessMsg("Record Fetched Successfully", addressEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-addresses_store")
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

                    StudentGuardianAddressEntity stdAddressEntity = StudentGuardianAddressEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentGuardianUUID(UUID.fromString(value.getFirst("studentGuardianUUID")))
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

                    // check address type is unique against student guardian 
                    return studentGuardianAddressesRepository.findFirstByStudentGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNull(stdAddressEntity.getStudentGuardianUUID(), stdAddressEntity.getAddressTypeUUID())
                            .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
//                    check student guardian  uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(stdAddressEntity.getStudentGuardianUUID())
//                            check address type uuid
                                            .flatMap(stdMsg -> addressTypeRepository.findByUuidAndDeletedAtIsNull(stdAddressEntity.getAddressTypeUUID())
                                                    .flatMap(checkAddress -> studentGuardianAddressesRepository.save(stdAddressEntity)
                                                            .flatMap(studentGuardianAddressesEntity -> responseSuccessMsg("Record Stored Successfully", studentGuardianAddressesEntity)
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again.")
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer")))))
                                            ).switchIfEmpty(responseInfoMsg("Student Guardian does not exist."))
                                            .onErrorResume(err -> responseErrorMsg("Student Guardian does not exist. Please contact developer"))
                            ));
                }).onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-addresses_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentGuardianAddressesRepository.findByUuidAndDeletedAtIsNull(studentGuardianAddressUUID)
                        .flatMap(previousStdGuardianAddressEntity -> {

                            StudentGuardianAddressEntity updatedStdAddressEntity = StudentGuardianAddressEntity.builder()
                                    .uuid(previousStdGuardianAddressEntity.getUuid())
                                    .studentGuardianUUID(previousStdGuardianAddressEntity.getStudentGuardianUUID())
                                    .addressTypeUUID(UUID.fromString(value.getFirst("addressTypeUUID")))
                                    .address(value.getFirst("address").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousStdGuardianAddressEntity.getCreatedAt())
                                    .createdBy(previousStdGuardianAddressEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousStdGuardianAddressEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStdGuardianAddressEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStdGuardianAddressEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStdGuardianAddressEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStdGuardianAddressEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStdGuardianAddressEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousStdGuardianAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStdGuardianAddressEntity.setDeletedBy(UUID.fromString(userId));
                            previousStdGuardianAddressEntity.setReqDeletedIP(reqIp);
                            previousStdGuardianAddressEntity.setReqDeletedPort(reqPort);
                            previousStdGuardianAddressEntity.setReqDeletedBrowser(reqBrowser);
                            previousStdGuardianAddressEntity.setReqDeletedOS(reqOs);
                            previousStdGuardianAddressEntity.setReqDeletedDevice(reqDevice);
                            previousStdGuardianAddressEntity.setReqDeletedReferer(reqReferer);

                            // check address type is unique against student guardian 
                            return studentGuardianAddressesRepository.findFirstByStudentGuardianUUIDAndAddressTypeUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStdAddressEntity.getStudentGuardianUUID(), updatedStdAddressEntity.getAddressTypeUUID(), studentGuardianAddressUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("Address Type already exist"))
                                    // check student guardian  uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(updatedStdAddressEntity.getStudentGuardianUUID())
                                            // check address type uuid
                                            .flatMap(studentEntity -> addressTypeRepository.findByUuidAndDeletedAtIsNull(updatedStdAddressEntity.getAddressTypeUUID())
                                                    .flatMap(addressTypeEntity ->
                                                            studentGuardianAddressesRepository.save(previousStdGuardianAddressEntity)
                                                                    .then(studentGuardianAddressesRepository.save(updatedStdAddressEntity))
                                                                    .flatMap(studentGuardianAddressesEntityDB -> responseSuccessMsg("Record Updated Successfully", studentGuardianAddressesEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong Please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Address Type Does not exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Address Type Does not exist.Please Contact Developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student  does not exist."))
                                            .onErrorResume(err -> responseErrorMsg("Student  does not exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-addresses_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGuardianAddressesRepository.findByUuidAndDeletedAtIsNull(studentGuardianAddressUUID)
                .flatMap(studentGuardianAddressesEntity -> {

                    studentGuardianAddressesEntity.setDeletedBy(UUID.fromString(userId));
                    studentGuardianAddressesEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentGuardianAddressesEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentGuardianAddressesEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentGuardianAddressesEntity.setReqDeletedIP(reqIp);
                    studentGuardianAddressesEntity.setReqDeletedPort(reqPort);
                    studentGuardianAddressesEntity.setReqDeletedBrowser(reqBrowser);
                    studentGuardianAddressesEntity.setReqDeletedOS(reqOs);
                    studentGuardianAddressesEntity.setReqDeletedDevice(reqDevice);
                    studentGuardianAddressesEntity.setReqDeletedReferer(reqReferer);

                    return studentGuardianAddressesRepository.save(studentGuardianAddressesEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .onErrorResume(err -> responseErrorMsg("Unable to Delete Record.There is something wrong please Try Again."))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-addresses_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGuardianAddressUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

                    return studentGuardianAddressesRepository.findByUuidAndDeletedAtIsNull(studentGuardianAddressUUID)
                            .flatMap(previousStdGuardianAddressEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousStdGuardianAddressEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGuardianAddressEntity updatedStdAddressEntity = StudentGuardianAddressEntity.builder()
                                        .uuid(previousStdGuardianAddressEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .studentGuardianUUID(previousStdGuardianAddressEntity.getStudentGuardianUUID())
                                        .addressTypeUUID(previousStdGuardianAddressEntity.getAddressTypeUUID())
                                        .address(previousStdGuardianAddressEntity.getAddress())
                                        .createdAt(previousStdGuardianAddressEntity.getCreatedAt())
                                        .createdBy(previousStdGuardianAddressEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousStdGuardianAddressEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousStdGuardianAddressEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousStdGuardianAddressEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousStdGuardianAddressEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousStdGuardianAddressEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousStdGuardianAddressEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                previousStdGuardianAddressEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousStdGuardianAddressEntity.setDeletedBy(UUID.fromString(userId));
                                previousStdGuardianAddressEntity.setReqDeletedIP(reqIp);
                                previousStdGuardianAddressEntity.setReqDeletedPort(reqPort);
                                previousStdGuardianAddressEntity.setReqDeletedBrowser(reqBrowser);
                                previousStdGuardianAddressEntity.setReqDeletedOS(reqOs);
                                previousStdGuardianAddressEntity.setReqDeletedDevice(reqDevice);
                                previousStdGuardianAddressEntity.setReqDeletedReferer(reqReferer);

                                return studentGuardianAddressesRepository.save(previousStdGuardianAddressEntity)
                                        .then(studentGuardianAddressesRepository.save(updatedStdAddressEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
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
