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
import tuf.webscaf.app.dbContext.master.entity.TimetableChangeReasonEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTimetableChangeReasonEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTimetableChangeReasonRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "timetableChangeReasonHandler")
@Component
public class TimetableChangeReasonHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TimetableChangeReasonRepository timetableChangeReasonRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    SlaveTimetableChangeReasonRepository slaveTimetableChangeReasonRepository;

    @Autowired
    StudentFatherAcademicHistoryRepository studentFatherAcademicHistoryRepository;

    @Autowired
    StudentAcademicRecordRepository studentAcademicRecordRepository;

    @Autowired
    TeacherAcademicRecordRepository teacherAcademicRecordRepository;

    @Autowired
    StudentMotherAcademicHistoryRepository studentMotherAcademicHistoryRepository;

    @Autowired
    StudentSiblingAcademicHistoryRepository studentSiblingAcademicHistoryRepository;

    @Autowired
    StudentGuardianAcademicHistoryRepository studentGuardianAcademicHistoryRepository;

    @Autowired
    TeacherMotherAcademicHistoryRepository teacherMotherAcademicHistoryRepository;

    @Autowired
    TeacherFatherAcademicHistoryRepository teacherFatherAcademicHistoryRepository;

    @Autowired
    TeacherSiblingAcademicHistoryRepository teacherSiblingAcademicHistoryRepository;

    @Autowired
    TeacherChildAcademicHistoryRepository teacherChildAcademicHistoryRepository;

    @AuthHasPermission(value = "academic_api_v1_timetable-change-reasons_index")
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        String timetableUUID = serverRequest.queryParam("timetableUUID").map(String::toString).orElse("").trim();

        if (!timetableUUID.isEmpty()) {
            Flux<SlaveTimetableChangeReasonEntity> slaveTimetableChangeReasonFlux = slaveTimetableChangeReasonRepository
                    .findAllByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNullAndTimetableUUID(pageable, UUID.fromString(timetableUUID), searchKeyWord);
            return slaveTimetableChangeReasonFlux
                    .collectList()
                    .flatMap(timetableChangeReasonEntity -> slaveTimetableChangeReasonRepository
                            .countByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNullAndTimetableUUID(UUID.fromString(timetableUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (timetableChangeReasonEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableChangeReasonEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTimetableChangeReasonEntity> slaveTimetableChangeReasonFlux = slaveTimetableChangeReasonRepository
                    .findAllByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveTimetableChangeReasonFlux
                    .collectList()
                    .flatMap(timetableChangeReasonEntity -> slaveTimetableChangeReasonRepository
                            .countByTimetableChangeReasonContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (timetableChangeReasonEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", timetableChangeReasonEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-change-reasons_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID timetableChangeReasonUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTimetableChangeReasonRepository.findByUuidAndDeletedAtIsNull(timetableChangeReasonUUID)
                .flatMap(timetableChangeReasonEntity -> responseSuccessMsg("Record Fetched Successfully", timetableChangeReasonEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-change-reasons_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> {
                    TimetableChangeReasonEntity entity = TimetableChangeReasonEntity.builder()
                            .uuid(UUID.randomUUID())
                            .timetableUUID(UUID.fromString(value.getFirst("timetableUUID")))
                            .timetableChangeReason(value.getFirst("timetableChangeReason").trim())
                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                            .createdBy(UUID.fromString(userId))
                            .build();

                    return timetableCreationRepository.findByUuidAndDeletedAtIsNull(entity.getTimetableUUID())
                            .flatMap(timetable -> timetableChangeReasonRepository.save(entity)
                                    .flatMap(timetableChangeReasonEntity -> responseSuccessMsg("Record Stored Successfully", timetableChangeReasonEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                            )
                            .switchIfEmpty(responseInfoMsg("Timetable Creation Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Timetable Creation Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_timetable-change-reasons_status_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID timetableChangeReasonUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return serverRequest.formData()
                .flatMap(value -> timetableChangeReasonRepository.findByUuidAndDeletedAtIsNull(timetableChangeReasonUUID)
                        .flatMap(previousEntity -> {

                            TimetableChangeReasonEntity updatedEntity = TimetableChangeReasonEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .timetableUUID(previousEntity.getTimetableUUID())
                                    .timetableChangeReason(value.getFirst("timetableChangeReason").trim())
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .build();

                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));

                            return timetableCreationRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTimetableUUID())
                                    .flatMap(timetable -> timetableChangeReasonRepository.save(previousEntity)
                                            .then(timetableChangeReasonRepository.save(updatedEntity))
                                            .flatMap(timetableChangeReasonEntity -> responseSuccessMsg("Record Updated Successfully", timetableChangeReasonEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                    )
                                    .switchIfEmpty(responseInfoMsg("Timetable Creation Record Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Timetable Creation Record Does not exist.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_timetable-change-reasons_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID timetableChangeReasonUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        String userId = serverRequest.headers().firstHeader("auid");

        if (userId == null) {
            return responseWarningMsg("Unknown User");
        } else {
            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                return responseWarningMsg("Unknown User");
            }
        }

        return timetableChangeReasonRepository.findByUuidAndDeletedAtIsNull(timetableChangeReasonUUID)
                .flatMap(timetableChangeReasonEntity -> {
                    timetableChangeReasonEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    timetableChangeReasonEntity.setDeletedBy(UUID.fromString(userId));
                    return timetableChangeReasonRepository.save(timetableChangeReasonEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to deleted record"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to deleted record. Please contact developer."));
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
