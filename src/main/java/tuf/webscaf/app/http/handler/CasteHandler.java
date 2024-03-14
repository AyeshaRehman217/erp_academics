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
import tuf.webscaf.app.dbContext.master.entity.CasteEntity;
import tuf.webscaf.app.dbContext.master.repositry.CasteRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherProfileRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCasteRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "casteHandler")
@Component
public class CasteHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    CasteRepository casteRepository;

    @Autowired
    SlaveCasteRepository slaveCasteRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @AuthHasPermission(value = "academic_api_v1_castes_index")
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
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveCasteEntity> slaveCastEntityFlux = slaveCasteRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                            Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), pageable);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(casteEntityDB -> slaveCasteRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (casteEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", casteEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCasteEntity> slaveCastEntityFlux = slaveCasteRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, pageable);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(casteEntityDB -> slaveCasteRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (casteEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", casteEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_castes_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID casteUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCasteRepository.findByUuidAndDeletedAtIsNull(casteUUID)
                .flatMap(casteEntityDB -> responseSuccessMsg("Record Fetched Successfully.", casteEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_castes_store")
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

                    CasteEntity casteEntity = CasteEntity.builder()
                            .name(value.getFirst("name"))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .description(value.getFirst("description"))
                            .uuid(UUID.randomUUID())
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

//                    check caste name is unique
                    return casteRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(casteEntity.getName())
                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> casteRepository.save(casteEntity)
                                    .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record..There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_castes_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID casteUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> casteRepository.findByUuidAndDeletedAtIsNull(casteUUID)
                                .flatMap(previousCasteEntity -> {

                                    CasteEntity updatedCasteEntity = CasteEntity.builder()
                                            .name(value.getFirst("name"))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .description(value.getFirst("description"))
                                            .uuid(previousCasteEntity.getUuid())
                                            .createdAt(previousCasteEntity.getCreatedAt())
                                            .createdBy(previousCasteEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousCasteEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousCasteEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousCasteEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousCasteEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousCasteEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousCasteEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    //Deleting Previous Record and Creating a New One Based on UUID
                                    previousCasteEntity.setDeletedBy(UUID.fromString(userId));
                                    previousCasteEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousCasteEntity.setReqDeletedIP(reqIp);
                                    previousCasteEntity.setReqDeletedPort(reqPort);
                                    previousCasteEntity.setReqDeletedBrowser(reqBrowser);
                                    previousCasteEntity.setReqDeletedOS(reqOs);
                                    previousCasteEntity.setReqDeletedDevice(reqDevice);
                                    previousCasteEntity.setReqDeletedReferer(reqReferer);

//                         check caste name is unique
                                    return casteRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedCasteEntity.getName(), casteUUID)
                                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> casteRepository.save(previousCasteEntity)
                                                    .then(casteRepository.save(updatedCasteEntity))
                                                    .flatMap(saveEntity -> responseSuccessMsg("Record Updated Successfully", saveEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.Please Contact Developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_castes_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID casteUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return casteRepository.findByUuidAndDeletedAtIsNull(casteUUID)
                            .flatMap(previousCasteEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousCasteEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CasteEntity updatedCasteEntity = CasteEntity.builder()
                                        .name(previousCasteEntity.getName())
                                        .status(status == true ? true : false)
                                        .description(previousCasteEntity.getDescription())
                                        .uuid(previousCasteEntity.getUuid())
                                        .createdAt(previousCasteEntity.getCreatedAt())
                                        .createdBy(previousCasteEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousCasteEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousCasteEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousCasteEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousCasteEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousCasteEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousCasteEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousCasteEntity.setDeletedBy(UUID.fromString(userId));
                                previousCasteEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousCasteEntity.setReqDeletedIP(reqIp);
                                previousCasteEntity.setReqDeletedPort(reqPort);
                                previousCasteEntity.setReqDeletedBrowser(reqBrowser);
                                previousCasteEntity.setReqDeletedOS(reqOs);
                                previousCasteEntity.setReqDeletedDevice(reqDevice);
                                previousCasteEntity.setReqDeletedReferer(reqReferer);

                                return casteRepository.save(previousCasteEntity)
                                        .then(casteRepository.save(updatedCasteEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_castes_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID casteUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return casteRepository.findByUuidAndDeletedAtIsNull(casteUUID)
                .flatMap(casteEntity -> studentProfileRepository.findFirstByCasteUUIDAndDeletedAtIsNull(casteEntity.getUuid())
                        //Checks if Caste Reference exists in Student Profiles
                        .flatMap(studentProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.findFirstByCasteUUIDAndDeletedAtIsNull(casteEntity.getUuid())
                                //Checks if Caste Reference exists in Teacher Profiles
                                .flatMap(teacherProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            casteEntity.setDeletedBy(UUID.fromString(userId));
                            casteEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            casteEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            casteEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            casteEntity.setReqDeletedIP(reqIp);
                            casteEntity.setReqDeletedPort(reqPort);
                            casteEntity.setReqDeletedBrowser(reqBrowser);
                            casteEntity.setReqDeletedOS(reqOs);
                            casteEntity.setReqDeletedDevice(reqDevice);
                            casteEntity.setReqDeletedReferer(reqReferer);

                            return casteRepository.save(casteEntity)
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
