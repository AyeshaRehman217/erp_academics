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
import tuf.webscaf.app.dbContext.master.entity.ReligionEntity;
import tuf.webscaf.app.dbContext.master.repositry.ReligionRepository;
import tuf.webscaf.app.dbContext.master.repositry.SectRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherProfileRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveReligionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveReligionRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "religionHandler")
@Component
public class ReligionHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    ReligionRepository religionRepository;

    @Autowired
    SlaveReligionRepository slaveReligionRepository;

    @Autowired
    SectRepository sectRepository;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_religions_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveReligionEntity> slaveReligionEntityFlux = slaveReligionRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveReligionEntityFlux
                    .collectList()
                    .flatMap(religionEntity -> slaveReligionRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (religionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", religionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveReligionEntity> slaveReligionEntityFlux = slaveReligionRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveReligionEntityFlux
                    .collectList()
                    .flatMap(religionEntity -> slaveReligionRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (religionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", religionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_religions_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID religionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveReligionRepository.findByUuidAndDeletedAtIsNull(religionUUID)
                .flatMap(religionEntity -> responseSuccessMsg("Record Fetched Successfully", religionEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_religions_store")
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

                    ReligionEntity religionEntity = ReligionEntity.builder()
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .uuid(UUID.randomUUID())
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

//                    check religion name is unique
                    return religionRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(religionEntity.getName())
                            .flatMap(checkName -> responseInfoMsg("Name Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> religionRepository.save(religionEntity)
                                    .flatMap(religionDB -> responseSuccessMsg("Record Stored Successfully", religionDB))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_religions_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID religionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> religionRepository.findByUuidAndDeletedAtIsNull(religionUUID)
                                .flatMap(previousReligionEntity -> {

                                    ReligionEntity updatedEntity = ReligionEntity.builder()
                                            .name(value.getFirst("name").trim())
                                            .description(value.getFirst("description").trim())
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .uuid(previousReligionEntity.getUuid())
                                            .createdAt(previousReligionEntity.getCreatedAt())
                                            .createdBy(previousReligionEntity.getCreatedBy())
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .updatedBy(UUID.fromString(userId))
                                            .reqCreatedIP(previousReligionEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousReligionEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousReligionEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousReligionEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousReligionEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousReligionEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    previousReligionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousReligionEntity.setDeletedBy(UUID.fromString(userId));
                                    previousReligionEntity.setReqDeletedIP(reqIp);
                                    previousReligionEntity.setReqDeletedPort(reqPort);
                                    previousReligionEntity.setReqDeletedBrowser(reqBrowser);
                                    previousReligionEntity.setReqDeletedOS(reqOs);
                                    previousReligionEntity.setReqDeletedDevice(reqDevice);
                                    previousReligionEntity.setReqDeletedReferer(reqReferer);

//                        check religion name is unique
                                    return religionRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), religionUUID)
                                            .flatMap(checkName -> responseInfoMsg("Name Already Exists."))
                                            .switchIfEmpty(Mono.defer(() ->
                                                    religionRepository.save(previousReligionEntity)
                                                            .then(religionRepository.save(updatedEntity))
                                                            .flatMap(religionEntity -> responseSuccessMsg("Record Updated Successfully", religionEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to Update Record.Please Contact Developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_religions_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID religionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return religionRepository.findByUuidAndDeletedAtIsNull(religionUUID)
                //Checks if Religion Reference exists in Sects
                .flatMap(religionEntity -> sectRepository.findFirstByReligionUUIDAndDeletedAtIsNull(religionEntity.getUuid())
                        .flatMap(checkSect -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Religion Reference exists in Teacher Profile
                        .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.findFirstByReligionUUIDAndDeletedAtIsNull(religionEntity.getUuid())
                                .flatMap(checkSect -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Religion Reference exists in Student Profile
                        .switchIfEmpty(Mono.defer(() -> studentProfileRepository.findFirstByReligionUUIDAndDeletedAtIsNull(religionEntity.getUuid())
                                .flatMap(checkSect -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            religionEntity.setDeletedBy(UUID.fromString(userId));
                            religionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            religionEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            religionEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            religionEntity.setReqDeletedIP(reqIp);
                            religionEntity.setReqDeletedPort(reqPort);
                            religionEntity.setReqDeletedBrowser(reqBrowser);
                            religionEntity.setReqDeletedOS(reqOs);
                            religionEntity.setReqDeletedDevice(reqDevice);
                            religionEntity.setReqDeletedReferer(reqReferer);

                            return religionRepository.save(religionEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_religions_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID religionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return religionRepository.findByUuidAndDeletedAtIsNull(religionUUID)
                            .flatMap(previousReligionEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousReligionEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                ReligionEntity updatedReligionEntity = ReligionEntity.builder()
                                        .name(previousReligionEntity.getName())
                                        .status(status == true ? true : false)
                                        .description(previousReligionEntity.getDescription())
                                        .uuid(previousReligionEntity.getUuid())
                                        .createdAt(previousReligionEntity.getCreatedAt())
                                        .createdBy(previousReligionEntity.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousReligionEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousReligionEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousReligionEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousReligionEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousReligionEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousReligionEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousReligionEntity.setDeletedBy(UUID.fromString(userId));
                                previousReligionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousReligionEntity.setReqDeletedIP(reqIp);
                                previousReligionEntity.setReqDeletedPort(reqPort);
                                previousReligionEntity.setReqDeletedBrowser(reqBrowser);
                                previousReligionEntity.setReqDeletedOS(reqOs);
                                previousReligionEntity.setReqDeletedDevice(reqDevice);
                                previousReligionEntity.setReqDeletedReferer(reqReferer);

                                return religionRepository.save(previousReligionEntity)
                                        .then(religionRepository.save(updatedReligionEntity))
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
