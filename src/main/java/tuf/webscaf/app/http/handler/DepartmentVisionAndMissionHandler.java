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
import tuf.webscaf.app.dbContext.master.entity.DepartmentVisionAndMissionEntity;
import tuf.webscaf.app.dbContext.master.repositry.DepartmentRepository;
import tuf.webscaf.app.dbContext.master.repositry.DepartmentVisionAndMissionRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentVisionAndMissionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveDepartmentVisionAndMissionRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "departmentVisionAndMissionHandler")
@Component
public class

DepartmentVisionAndMissionHandler {
    @Autowired
    CustomResponse appresponse;
    @Autowired
    DepartmentVisionAndMissionRepository departmentVisionAndMissionRepository;
    @Autowired
    SlaveDepartmentVisionAndMissionRepository slaveDepartmentVisionAndMissionRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_department-vision-and-missions_index")
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
            Flux<SlaveDepartmentVisionAndMissionEntity> slaveDepartmentVisionAndMissionFlux = slaveDepartmentVisionAndMissionRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveDepartmentVisionAndMissionFlux
                    .collectList()
                    .flatMap(departmentVisionAndMissionEntity -> slaveDepartmentVisionAndMissionRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (departmentVisionAndMissionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", departmentVisionAndMissionEntity, count);
                                }
                            }))
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveDepartmentVisionAndMissionEntity> slaveDepartmentVisionAndMissionFlux = slaveDepartmentVisionAndMissionRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveDepartmentVisionAndMissionFlux
                    .collectList()
                    .flatMap(departmentVisionAndMissionEntity -> slaveDepartmentVisionAndMissionRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (departmentVisionAndMissionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", departmentVisionAndMissionEntity, count);
                                }
                            }))
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_department-vision-and-missions_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID departmentVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveDepartmentVisionAndMissionRepository.showDepartmentVisionAndMissionAgainstUUID(departmentVisionAndMissionUUID)
                .flatMap(departmentVisionAndMissionEntity -> responseSuccessMsg("Record Fetched Successfully", departmentVisionAndMissionEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-vision-and-missions_store")
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

                    DepartmentVisionAndMissionEntity entity = DepartmentVisionAndMissionEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .vision(value.getFirst("vision").trim())
                            .mission(value.getFirst("mission").trim())
                            .departmentUUID(UUID.fromString(value.getFirst("departmentUUID")))
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

                    return departmentRepository.findByUuidAndDeletedAtIsNull(entity.getDepartmentUUID())
                            .flatMap(departmentEntity -> departmentVisionAndMissionRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                                    .flatMap(nameAlreadyExists -> responseInfoMsg("Name already exist"))
                                    .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.findFirstByVisionAndDepartmentUUIDAndDeletedAtIsNull(entity.getVision(), entity.getDepartmentUUID())
                                            .flatMap(checkVision -> responseInfoMsg("Vision already exist"))))
                                    .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.findFirstByMissionAndDepartmentUUIDAndDeletedAtIsNull(entity.getMission(), entity.getDepartmentUUID())
                                            .flatMap(checkMission -> responseInfoMsg("Mission already exist"))))
                                    .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.save(entity)
                                            .flatMap(departmentVisionAndMissionEntity -> responseSuccessMsg("Record Stored Successfully", departmentVisionAndMissionEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                            ).switchIfEmpty(responseInfoMsg("Department does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Department does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-vision-and-missions_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID departmentVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> departmentVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(departmentVisionAndMissionUUID)
                        .flatMap(previousDepartmentVisionAndMissionEntity -> {
                            DepartmentVisionAndMissionEntity updatedEntity = DepartmentVisionAndMissionEntity.builder()
                                    .uuid(previousDepartmentVisionAndMissionEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .vision(value.getFirst("vision").trim())
                                    .mission(value.getFirst("mission").trim())
                                    .departmentUUID(UUID.fromString(value.getFirst("departmentUUID")))
                                    .createdAt(previousDepartmentVisionAndMissionEntity.getCreatedAt())
                                    .createdBy(previousDepartmentVisionAndMissionEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousDepartmentVisionAndMissionEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousDepartmentVisionAndMissionEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousDepartmentVisionAndMissionEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousDepartmentVisionAndMissionEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousDepartmentVisionAndMissionEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousDepartmentVisionAndMissionEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousDepartmentVisionAndMissionEntity.setDeletedBy(UUID.fromString(userId));
                            previousDepartmentVisionAndMissionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousDepartmentVisionAndMissionEntity.setReqDeletedIP(reqIp);
                            previousDepartmentVisionAndMissionEntity.setReqDeletedPort(reqPort);
                            previousDepartmentVisionAndMissionEntity.setReqDeletedBrowser(reqBrowser);
                            previousDepartmentVisionAndMissionEntity.setReqDeletedOS(reqOs);
                            previousDepartmentVisionAndMissionEntity.setReqDeletedDevice(reqDevice);
                            previousDepartmentVisionAndMissionEntity.setReqDeletedReferer(reqReferer);

                            return departmentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDepartmentUUID())
                                    .flatMap(departmentEntity -> departmentVisionAndMissionRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), updatedEntity.getUuid())
                                            .flatMap(nameAlreadyExists -> responseInfoMsg("Name already exist"))
                                            .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.findFirstByVisionAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getVision(), updatedEntity.getDepartmentUUID(), departmentVisionAndMissionUUID)
                                                    .flatMap(checkVision -> responseInfoMsg("Vision already exist"))))
                                            .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.findFirstByMissionAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getMission(), updatedEntity.getDepartmentUUID(), departmentVisionAndMissionUUID)
                                                    .flatMap(checkMission -> responseInfoMsg("Mission already exist"))))
                                            .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.save(previousDepartmentVisionAndMissionEntity)
                                                    .then(departmentVisionAndMissionRepository.save(updatedEntity))
                                                    .flatMap(departmentVisionAndMissionEntity -> responseSuccessMsg("Record Stored Successfully", departmentVisionAndMissionEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                    ).switchIfEmpty(responseInfoMsg("Department does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Department does not exist. Please contact developer"));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-vision-and-missions_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID departmentVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return departmentVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(departmentVisionAndMissionUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                DepartmentVisionAndMissionEntity departmentVisionAndMissionEntity = DepartmentVisionAndMissionEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .description(previousEntity.getDescription())
                                        .vision(previousEntity.getVision())
                                        .mission(previousEntity.getMission())
                                        .status(status == true ? true : false)
                                        .departmentUUID(previousEntity.getDepartmentUUID())
                                        .createdAt(previousEntity.getCreatedAt())
                                        .createdBy(previousEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return departmentVisionAndMissionRepository.save(previousEntity)
                                        .then(departmentVisionAndMissionRepository.save(departmentVisionAndMissionEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-vision-and-missions_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID departmentVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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

        return departmentVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(departmentVisionAndMissionUUID)
                //check If DepartmentVisionAndMission UUID Exists in peoDepartmentVisionAndMission
                .flatMap(departmentVisionAndMissionEntity -> {

                    departmentVisionAndMissionEntity.setDeletedBy(UUID.fromString(userId));
                    departmentVisionAndMissionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    departmentVisionAndMissionEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    departmentVisionAndMissionEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    departmentVisionAndMissionEntity.setReqDeletedIP(reqIp);
                    departmentVisionAndMissionEntity.setReqDeletedPort(reqPort);
                    departmentVisionAndMissionEntity.setReqDeletedBrowser(reqBrowser);
                    departmentVisionAndMissionEntity.setReqDeletedOS(reqOs);
                    departmentVisionAndMissionEntity.setReqDeletedDevice(reqDevice);
                    departmentVisionAndMissionEntity.setReqDeletedReferer(reqReferer);

                    return departmentVisionAndMissionRepository.save(departmentVisionAndMissionEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
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
