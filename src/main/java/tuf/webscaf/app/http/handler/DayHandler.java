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
import tuf.webscaf.app.dbContext.master.entity.DayEntity;
import tuf.webscaf.app.dbContext.master.repositry.DayRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TimetableCreationRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDayEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveDayRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "dayHandler")
@Component
public class DayHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    DayRepository dayRepository;

    @Autowired
    SlaveDayRepository slaveDayRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_days_index")
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
            Flux<SlaveDayEntity> slaveCastEntityFlux = slaveDayRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord,
                            Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(dayEntityDB -> slaveDayRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (dayEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", dayEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveDayEntity> slaveCastEntityFlux = slaveDayRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(dayEntityDB -> slaveDayRepository
                            .countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (dayEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", dayEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_days_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID dayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveDayRepository.findByUuidAndDeletedAtIsNull(dayUUID)
                .flatMap(dayEntityDB -> responseSuccessMsg("Record Fetched Successfully.", dayEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_days_store")
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

                    DayEntity dayEntity = DayEntity.builder()
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

//                    check day name is unique
                    return dayRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(dayEntity.getName())
                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> dayRepository.save(dayEntity)
                                    .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record..There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_days_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID dayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> dayRepository.findByUuidAndDeletedAtIsNull(dayUUID)
                                .flatMap(previousDayEntity -> {

                                    DayEntity updatedDayEntity = DayEntity.builder()
                                            .name(value.getFirst("name"))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .description(value.getFirst("description"))
                                            .uuid(previousDayEntity.getUuid())
                                            .createdAt(previousDayEntity.getCreatedAt())
                                            .createdBy(previousDayEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousDayEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousDayEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousDayEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousDayEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousDayEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousDayEntity.getReqCreatedReferer())
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
                                    previousDayEntity.setDeletedBy(UUID.fromString(userId));
                                    previousDayEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousDayEntity.setReqDeletedIP(reqIp);
                                    previousDayEntity.setReqDeletedPort(reqPort);
                                    previousDayEntity.setReqDeletedBrowser(reqBrowser);
                                    previousDayEntity.setReqDeletedOS(reqOs);
                                    previousDayEntity.setReqDeletedDevice(reqDevice);
                                    previousDayEntity.setReqDeletedReferer(reqReferer);

//                         check day name is unique
                                    return dayRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedDayEntity.getName(), dayUUID)
                                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> dayRepository.save(previousDayEntity)
                                                    .then(dayRepository.save(updatedDayEntity))
                                                    .flatMap(saveEntity -> responseSuccessMsg("Record Updated Successfully", saveEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.Please Contact Developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_days_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID dayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return dayRepository.findByUuidAndDeletedAtIsNull(dayUUID)
                            .flatMap(previousDayEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousDayEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                DayEntity updatedDayEntity = DayEntity.builder()
                                        .name(previousDayEntity.getName())
                                        .status(status == true ? true : false)
                                        .description(previousDayEntity.getDescription())
                                        .uuid(previousDayEntity.getUuid())
                                        .createdAt(previousDayEntity.getCreatedAt())
                                        .createdBy(previousDayEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousDayEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousDayEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousDayEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousDayEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousDayEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousDayEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousDayEntity.setDeletedBy(UUID.fromString(userId));
                                previousDayEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousDayEntity.setReqDeletedIP(reqIp);
                                previousDayEntity.setReqDeletedPort(reqPort);
                                previousDayEntity.setReqDeletedBrowser(reqBrowser);
                                previousDayEntity.setReqDeletedOS(reqOs);
                                previousDayEntity.setReqDeletedDevice(reqDevice);
                                previousDayEntity.setReqDeletedReferer(reqReferer);

                                return dayRepository.save(previousDayEntity)
                                        .then(dayRepository.save(updatedDayEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_days_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID dayUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return dayRepository.findByUuidAndDeletedAtIsNull(dayUUID)
                .flatMap(dayEntity -> timetableCreationRepository.findFirstByDayUUIDAndDeletedAtIsNull(dayEntity.getUuid())
                        .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the reference exists"))
                        .switchIfEmpty(Mono.defer(() -> {

                                    dayEntity.setDeletedBy(UUID.fromString(userId));
                                    dayEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    dayEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    dayEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    dayEntity.setReqDeletedIP(reqIp);
                                    dayEntity.setReqDeletedPort(reqPort);
                                    dayEntity.setReqDeletedBrowser(reqBrowser);
                                    dayEntity.setReqDeletedOS(reqOs);
                                    dayEntity.setReqDeletedDevice(reqDevice);
                                    dayEntity.setReqDeletedReferer(reqReferer);

                                    return dayRepository.save(dayEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                                }
                        ))
                ).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
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
