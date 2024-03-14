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
import tuf.webscaf.app.dbContext.master.entity.AddressTypeEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAddressTypeEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveAddressTypeRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "addressTypeHandler")
@Component
public class AddressTypeHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    AddressTypeRepository addressTypeRepository;

    @Autowired
    SlaveAddressTypeRepository slaveAddressTypeRepository;

    @Autowired
    StudentAddressRepository studentAddressRepository;

    @Autowired
    StudentMotherAddressRepository studentMotherAddressRepository;

    @Autowired
    StudentFatherAddressRepository studentFatherAddressRepository;

    @Autowired
    StudentSiblingAddressRepository studentSiblingAddressRepository;

    @Autowired
    StudentGuardianAddressRepository studentGuardianAddressRepository;

    @Autowired
    TeacherAddressRepository teacherAddressRepository;

    @Autowired
    TeacherMotherAddressRepository teacherMotherAddressRepository;

    @Autowired
    TeacherFatherAddressRepository teacherFatherAddressRepository;

    @Autowired
    TeacherSiblingAddressRepository teacherSiblingAddressRepository;

    @Autowired
    TeacherChildAddressRepository teacherChildAddressRepository;

    @AuthHasPermission(value = "academic_api_v1_address-types_index")
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
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveAddressTypeEntity> slaveAddressTypeFlux = slaveAddressTypeRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveAddressTypeFlux
                    .collectList()
                    .flatMap(addressTypeEntity -> slaveAddressTypeRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (addressTypeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", addressTypeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveAddressTypeEntity> slaveAddressTypeFlux = slaveAddressTypeRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveAddressTypeFlux
                    .collectList()
                    .flatMap(addressTypeEntity -> slaveAddressTypeRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (addressTypeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", addressTypeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_address-types_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID addressTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveAddressTypeRepository.findByUuidAndDeletedAtIsNull(addressTypeUUID)
                .flatMap(addressTypeEntity -> responseSuccessMsg("Record Fetched Successfully", addressTypeEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_address-types_store")
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

                    AddressTypeEntity entity = AddressTypeEntity.builder()
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

//                    check address type is unique
                    return addressTypeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                            .flatMap(addressTypeEntity -> responseInfoMsg("Name Already Exist"))
                            .switchIfEmpty(Mono.defer(() -> addressTypeRepository.save(entity)
                                    .flatMap(addressTypeEntity -> responseSuccessMsg("Record Stored Successfully", addressTypeEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_address-types_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID addressTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> addressTypeRepository.findByUuidAndDeletedAtIsNull(addressTypeUUID)
                                .flatMap(entity -> {
                                    AddressTypeEntity updatedEntity = AddressTypeEntity.builder()
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

//                         check address type is unique
                                    return addressTypeRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), addressTypeUUID)
                                            .flatMap(nameExists -> responseInfoMsg("Name Already Exists"))
                                            .switchIfEmpty(Mono.defer(() ->
                                                    addressTypeRepository.save(entity)
                                                            .then(addressTypeRepository.save(updatedEntity))
                                                            .flatMap(addressTypeEntity -> responseSuccessMsg("Record Updated Successfully", addressTypeEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_address-types_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID addressTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return addressTypeRepository.findByUuidAndDeletedAtIsNull(addressTypeUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                AddressTypeEntity entity = AddressTypeEntity.builder()
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

                                return addressTypeRepository.save(val)
                                        .then(addressTypeRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_address-types_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID addressTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return addressTypeRepository.findByUuidAndDeletedAtIsNull(addressTypeUUID)
                .flatMap(addressTypeEntity -> studentAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                        //checking if Address Type exists in Student Addresses
                        .flatMap(studentAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        .switchIfEmpty(Mono.defer(() -> studentMotherAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Mother Addresses
                                .flatMap(studentMotherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> studentFatherAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Father Addresses
                                .flatMap(studentFatherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> studentSiblingAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Sibling Addresses
                                .flatMap(studentSiblingAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> studentGuardianAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Guardian Addresses
                                .flatMap(studentGuardianAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> teacherAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Addresses
                                .flatMap(teacherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> teacherMotherAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Mother Addresses
                                .flatMap(teacherMotherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> teacherFatherAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Father Addresses
                                .flatMap(teacherFatherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Sibling Addresses
                                .flatMap(teacherSiblingAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> teacherChildAddressRepository.findFirstByAddressTypeUUIDAndDeletedAtIsNull(addressTypeEntity.getUuid())
                                //checking if Address Type exists in Student Child Addresses
                                .flatMap(teacherChildAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            addressTypeEntity.setDeletedBy(UUID.fromString(userId));
                            addressTypeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            addressTypeEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            addressTypeEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            addressTypeEntity.setReqDeletedIP(reqIp);
                            addressTypeEntity.setReqDeletedPort(reqPort);
                            addressTypeEntity.setReqDeletedBrowser(reqBrowser);
                            addressTypeEntity.setReqDeletedOS(reqOs);
                            addressTypeEntity.setReqDeletedDevice(reqDevice);
                            addressTypeEntity.setReqDeletedReferer(reqReferer);

                            return addressTypeRepository.save(addressTypeEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
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
