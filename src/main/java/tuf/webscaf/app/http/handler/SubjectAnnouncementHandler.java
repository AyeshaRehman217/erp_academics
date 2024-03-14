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
import tuf.webscaf.app.dbContext.master.entity.SubjectAnnouncementEntity;
import tuf.webscaf.app.dbContext.master.repositry.SubjectAnnouncementRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherProfileRepository;
import tuf.webscaf.app.dbContext.master.repositry.TimetableCreationRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectAnnouncementEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectAnnouncementRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Tag(name = "subjectAnnouncementHandler")
@Component
public class SubjectAnnouncementHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectAnnouncementRepository subjectAnnouncementRepository;

    @Autowired
    SlaveSubjectAnnouncementRepository slaveSubjectAnnouncementRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_subject-announcements_index")
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
            Flux<SlaveSubjectAnnouncementEntity> slaveCastEntityFlux = slaveSubjectAnnouncementRepository
                    .findAllByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord,
                            Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(subjectAnnouncementEntityDB -> slaveSubjectAnnouncementRepository
                            .countByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (subjectAnnouncementEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectAnnouncementEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectAnnouncementEntity> slaveCastEntityFlux = slaveSubjectAnnouncementRepository
                    .findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveCastEntityFlux
                    .collectList()
                    .flatMap(subjectAnnouncementEntityDB -> slaveSubjectAnnouncementRepository
                            .countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrMessageContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (subjectAnnouncementEntityDB.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectAnnouncementEntityDB, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-announcements_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectAnnouncementUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectAnnouncementRepository.findByUuidAndDeletedAtIsNull(subjectAnnouncementUUID)
                .flatMap(subjectAnnouncementEntityDB -> responseSuccessMsg("Record Fetched Successfully.", subjectAnnouncementEntityDB))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-announcements_store")
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

                    SubjectAnnouncementEntity subjectAnnouncementEntity = SubjectAnnouncementEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .title(value.getFirst("title"))
                            .message(value.getFirst("message"))
                            .date(LocalDateTime.parse(value.getFirst("date"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                            .subjectUUID(UUID.fromString(value.getFirst("subjectUUID")))
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID")))
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

                    return subjectAnnouncementRepository.save(subjectAnnouncementEntity)
                            .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                            .switchIfEmpty(responseInfoMsg("Unable to store record..There is something wrong please try again."))
                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-announcements_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectAnnouncementUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectAnnouncementRepository.findByUuidAndDeletedAtIsNull(subjectAnnouncementUUID)
                        .flatMap(previousSubjectAnnouncementEntity -> {

                            SubjectAnnouncementEntity updatedSubjectAnnouncementEntity = SubjectAnnouncementEntity.builder()
                                    .uuid(previousSubjectAnnouncementEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .title(value.getFirst("title"))
                                    .message(value.getFirst("message"))
                                    .date(LocalDateTime.parse(value.getFirst("date"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                                    .subjectUUID(UUID.fromString(value.getFirst("subjectUUID")))
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID")))
                                    .createdAt(previousSubjectAnnouncementEntity.getCreatedAt())
                                    .createdBy(previousSubjectAnnouncementEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousSubjectAnnouncementEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousSubjectAnnouncementEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousSubjectAnnouncementEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousSubjectAnnouncementEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousSubjectAnnouncementEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousSubjectAnnouncementEntity.getReqCreatedReferer())
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
                            previousSubjectAnnouncementEntity.setDeletedBy(UUID.fromString(userId));
                            previousSubjectAnnouncementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousSubjectAnnouncementEntity.setReqDeletedIP(reqIp);
                            previousSubjectAnnouncementEntity.setReqDeletedPort(reqPort);
                            previousSubjectAnnouncementEntity.setReqDeletedBrowser(reqBrowser);
                            previousSubjectAnnouncementEntity.setReqDeletedOS(reqOs);
                            previousSubjectAnnouncementEntity.setReqDeletedDevice(reqDevice);
                            previousSubjectAnnouncementEntity.setReqDeletedReferer(reqReferer);

                            return subjectAnnouncementRepository.save(previousSubjectAnnouncementEntity)
                                    .then(subjectAnnouncementRepository.save(updatedSubjectAnnouncementEntity))
                                    .flatMap(saveEntity -> responseSuccessMsg("Record Updated Successfully", saveEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to update record.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-announcements_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectAnnouncementUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return subjectAnnouncementRepository.findByUuidAndDeletedAtIsNull(subjectAnnouncementUUID)
                            .flatMap(previousSubjectAnnouncementEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousSubjectAnnouncementEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectAnnouncementEntity updatedSubjectAnnouncementEntity = SubjectAnnouncementEntity.builder()
                                        .uuid(previousSubjectAnnouncementEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .title(previousSubjectAnnouncementEntity.getTitle())
                                        .message(previousSubjectAnnouncementEntity.getMessage())
                                        .date(previousSubjectAnnouncementEntity.getDate())
                                        .teacherUUID(previousSubjectAnnouncementEntity.getTeacherUUID())
                                        .subjectUUID(previousSubjectAnnouncementEntity.getSubjectUUID())
                                        .academicSessionUUID(previousSubjectAnnouncementEntity.getAcademicSessionUUID())
                                        .createdAt(previousSubjectAnnouncementEntity.getCreatedAt())
                                        .createdBy(previousSubjectAnnouncementEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousSubjectAnnouncementEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousSubjectAnnouncementEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousSubjectAnnouncementEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousSubjectAnnouncementEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousSubjectAnnouncementEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousSubjectAnnouncementEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousSubjectAnnouncementEntity.setDeletedBy(UUID.fromString(userId));
                                previousSubjectAnnouncementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousSubjectAnnouncementEntity.setReqDeletedIP(reqIp);
                                previousSubjectAnnouncementEntity.setReqDeletedPort(reqPort);
                                previousSubjectAnnouncementEntity.setReqDeletedBrowser(reqBrowser);
                                previousSubjectAnnouncementEntity.setReqDeletedOS(reqOs);
                                previousSubjectAnnouncementEntity.setReqDeletedDevice(reqDevice);
                                previousSubjectAnnouncementEntity.setReqDeletedReferer(reqReferer);

                                return subjectAnnouncementRepository.save(previousSubjectAnnouncementEntity)
                                        .then(subjectAnnouncementRepository.save(updatedSubjectAnnouncementEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-announcements_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectAnnouncementUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectAnnouncementRepository.findByUuidAndDeletedAtIsNull(subjectAnnouncementUUID)
                .flatMap(subjectAnnouncementEntity -> {

                    subjectAnnouncementEntity.setDeletedBy(UUID.fromString(userId));
                    subjectAnnouncementEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    subjectAnnouncementEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    subjectAnnouncementEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    subjectAnnouncementEntity.setReqDeletedIP(reqIp);
                    subjectAnnouncementEntity.setReqDeletedPort(reqPort);
                    subjectAnnouncementEntity.setReqDeletedBrowser(reqBrowser);
                    subjectAnnouncementEntity.setReqDeletedOS(reqOs);
                    subjectAnnouncementEntity.setReqDeletedDevice(reqDevice);
                    subjectAnnouncementEntity.setReqDeletedReferer(reqReferer);

                    return subjectAnnouncementRepository.save(subjectAnnouncementEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
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
