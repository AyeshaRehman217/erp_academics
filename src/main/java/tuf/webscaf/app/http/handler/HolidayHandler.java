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
import tuf.webscaf.app.dbContext.master.entity.HolidayEntity;
import tuf.webscaf.app.dbContext.master.repositry.HolidayRepository;
import tuf.webscaf.app.dbContext.master.repositry.HolidayTypeRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHolidayEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHolidayRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "holidayHandler")
@Component
public class HolidayHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    SlaveHolidayRepository slaveHolidayRepository;

    @Autowired
    HolidayTypeRepository holidayTypeRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_holidays_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty()) {
            Flux<SlaveHolidayEntity> slaveHolidayEntityFlux = slaveHolidayRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                            Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), pageable);
            return slaveHolidayEntityFlux
                    .collectList()
                    .flatMap(holidayEntityDB -> slaveHolidayRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (holidayEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", holidayEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseErrorMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveHolidayEntity> slaveHolidayEntityFlux = slaveHolidayRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, pageable);
            return slaveHolidayEntityFlux
                    .collectList()
                    .flatMap(holidayEntityDB -> slaveHolidayRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (holidayEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", holidayEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseErrorMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_holidays_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID holidayUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveHolidayRepository.findByUuidAndDeletedAtIsNull(holidayUUID)
                .flatMap(holidayEntityDB -> responseSuccessMsg("Record Fetched Successfully.", holidayEntityDB))
                .switchIfEmpty(Mono.defer(() -> responseInfoMsg("Record does not exist")))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_holidays_store")
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
                return responseWarningMsg("Unknown User!");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> {

                    HolidayEntity holidayEntity = HolidayEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name"))
                            .description(value.getFirst("description"))
                            .holidayTypeUUID(UUID.fromString(value.getFirst("holidayTypeUUID")))
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

                    //check holiday type exists
                    return holidayTypeRepository.findByUuidAndDeletedAtIsNull(holidayEntity.getHolidayTypeUUID())
//                    check holiday name is unique
                            .flatMap(holidayTypeEntity -> holidayRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(holidayEntity.getName())
                                    .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> holidayRepository.save(holidayEntity)
                                            .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully.", saveEntity))
                                            .switchIfEmpty(Mono.defer(() -> responseErrorMsg("Unable to store record. There is something wrong please try again.")))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Holiday Type record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Holiday Type record does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_holidays_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID holidayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                return responseWarningMsg("Unknown User!");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> holidayRepository.findByUuidAndDeletedAtIsNull(holidayUUID)
                                .flatMap(previousHolidayEntity -> {

                                    HolidayEntity updatedHolidayEntity = HolidayEntity.builder()
                                            .uuid(previousHolidayEntity.getUuid())
                                            .name(value.getFirst("name"))
                                            .description(value.getFirst("description"))
                                            .holidayTypeUUID(UUID.fromString(value.getFirst("holidayTypeUUID")))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .createdAt(previousHolidayEntity.getCreatedAt())
                                            .createdBy(previousHolidayEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousHolidayEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousHolidayEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousHolidayEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousHolidayEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousHolidayEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousHolidayEntity.getReqCreatedReferer())
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
                                    previousHolidayEntity.setDeletedBy(UUID.fromString(userId));
                                    previousHolidayEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousHolidayEntity.setReqDeletedIP(reqIp);
                                    previousHolidayEntity.setReqDeletedPort(reqPort);
                                    previousHolidayEntity.setReqDeletedBrowser(reqBrowser);
                                    previousHolidayEntity.setReqDeletedOS(reqOs);
                                    previousHolidayEntity.setReqDeletedDevice(reqDevice);
                                    previousHolidayEntity.setReqDeletedReferer(reqReferer);

//                                    check holiday type exists
                                    return holidayTypeRepository.findByUuidAndDeletedAtIsNull(updatedHolidayEntity.getHolidayTypeUUID())
//                                  check holiday name is unique
                                            .flatMap(holidayTypeEntity -> holidayRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedHolidayEntity.getName(), holidayUUID)
                                                    .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                                                    .switchIfEmpty(Mono.defer(() -> holidayRepository.save(previousHolidayEntity)
                                                            .then(holidayRepository.save(updatedHolidayEntity))
                                                            .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully.", saveEntity))
                                                            .switchIfEmpty(Mono.defer(() -> responseErrorMsg("Unable to store record.")))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                                    ))
                                            ).switchIfEmpty(responseInfoMsg("Holiday Type record does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Holiday Type record does not exist. Please contact developer"));
                                }).switchIfEmpty(Mono.defer(() -> responseInfoMsg("Record does not exist")))
                                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer"))
                ).switchIfEmpty(Mono.defer(() -> responseInfoMsg("Unable to read the request.")))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_holidays_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID holidayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                return responseWarningMsg("Unknown User!");
            }
        }
        return serverRequest.formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));

                    return holidayRepository.findByUuidAndDeletedAtIsNull(holidayUUID)
                            .flatMap(previousHolidayEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousHolidayEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                HolidayEntity updatedHolidayEntity = HolidayEntity.builder()
                                        .uuid(previousHolidayEntity.getUuid())
                                        .name(previousHolidayEntity.getName())
                                        .description(previousHolidayEntity.getDescription())
                                        .status(status == true ? true : false)
                                        .holidayTypeUUID(previousHolidayEntity.getHolidayTypeUUID())
                                        .createdAt(previousHolidayEntity.getCreatedAt())
                                        .createdBy(previousHolidayEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousHolidayEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousHolidayEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousHolidayEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousHolidayEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousHolidayEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousHolidayEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousHolidayEntity.setDeletedBy(UUID.fromString(userId));
                                previousHolidayEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousHolidayEntity.setReqDeletedIP(reqIp);
                                previousHolidayEntity.setReqDeletedPort(reqPort);
                                previousHolidayEntity.setReqDeletedBrowser(reqBrowser);
                                previousHolidayEntity.setReqDeletedOS(reqOs);
                                previousHolidayEntity.setReqDeletedDevice(reqDevice);
                                previousHolidayEntity.setReqDeletedReferer(reqReferer);

                                return holidayRepository.save(previousHolidayEntity)
                                        .then(holidayRepository.save(updatedHolidayEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status updated successfully", statusUpdate))
                                        .switchIfEmpty(responseErrorMsg("Unable to update the status"))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. There is something wrong please try again."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseErrorMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_holidays_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID holidayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                return responseWarningMsg("Unknown User!");
            }
        }

        return holidayRepository.findByUuidAndDeletedAtIsNull(holidayUUID)
                .flatMap(holidayEntity -> {

                    holidayEntity.setDeletedBy(UUID.fromString(userId));
                    holidayEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    holidayEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    holidayEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    holidayEntity.setReqDeletedIP(reqIp);
                    holidayEntity.setReqDeletedPort(reqPort);
                    holidayEntity.setReqDeletedBrowser(reqBrowser);
                    holidayEntity.setReqDeletedOS(reqOs);
                    holidayEntity.setReqDeletedDevice(reqDevice);
                    holidayEntity.setReqDeletedReferer(reqReferer);

                    return holidayRepository.save(holidayEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
                            .switchIfEmpty(responseErrorMsg("Unable to delete record. There is something wrong please try again."))
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