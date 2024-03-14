package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianDocumentEntity;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianAcademicHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianDocumentRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianDocumentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianDocumentRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherGuardianDocumentHandler")
@Component
public class TeacherGuardianDocumentHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianDocumentRepository teacherGuardianDocumentRepository;

    @Autowired
    SlaveTeacherGuardianDocumentRepository slaveTeacherGuardianDocumentRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    TeacherGuardianAcademicHistoryRepository teacherGuardianAcademicHistoryRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-documents_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Guardian UUID
        String teacherGuardianUUID = serverRequest.queryParam("teacherGuardianUUID").map(String::toString).orElse("").trim();

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

        if (!teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianDocumentEntity> slaveTeacherGuardianDocumentFlux = slaveTeacherGuardianDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID));

            return slaveTeacherGuardianDocumentFlux
                    .collectList()
                    .flatMap(teacherGuardianDocumentEntity -> slaveTeacherGuardianDocumentRepository.countByTitleContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID))
                            .flatMap(count -> {
                                if (teacherGuardianDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherGuardianDocumentEntity> slaveTeacherGuardianDocumentFlux = slaveTeacherGuardianDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveTeacherGuardianDocumentFlux
                    .collectList()
                    .flatMap(teacherGuardianDocumentEntity -> slaveTeacherGuardianDocumentRepository.countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherGuardianDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-documents_index")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherGuardianDocumentUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherGuardianDocumentRepository.findByUuidAndDeletedAtIsNull(teacherGuardianDocumentUUID)
                .flatMap(teacherFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    //Show Teacher Guardian Document Against Teacher UUID , Teacher Guardian UUID and Guardian Document UUID
    @AuthHasPermission(value = "academic_api_v1_teacher_teacher-guardian_teacher-guardian-documents_show")
    public Mono<ServerResponse> showByTeacherGuardianAndTeacher(ServerRequest serverRequest) {
        UUID teacherGuardianDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        UUID teacherGuardianUUID = UUID.fromString(serverRequest.queryParam("teacherGuardianUUID").map(String::toString).orElse(""));
        UUID teacherUUID = UUID.fromString(serverRequest.queryParam("teacherUUID").map(String::toString).orElse(""));

        return slaveTeacherGuardianDocumentRepository.showDocsAgainstTeacherAndTeacherGuardian(teacherUUID, teacherGuardianUUID, teacherGuardianDocumentUUID)
                .flatMap(teacherGuardianDocEntity -> responseSuccessMsg("Record Fetched Successfully", teacherGuardianDocEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-documents_store")
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
                // checks if teacher guardian uuid exists
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                        .flatMap(teacherGuardianEntity -> {
                            // if teacher guardian uuid is already set
                            if (teacherGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Documents. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                TeacherGuardianDocumentEntity teacherGrdDocumentEntity = TeacherGuardianDocumentEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherGuardianUUID(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                                        .docId(UUID.fromString(value.getFirst("docId").trim()))
                                        .title(value.getFirst("title").trim())
                                        .description(value.getFirst("description").trim())
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

                                sendFormData.add("docId", String.valueOf(teacherGrdDocumentEntity.getDocId()));

                                // checks if doc id exists
                                return apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherGrdDocumentEntity.getDocId())
                                        .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                                .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                        .flatMap(extension -> {

                                                            teacherGrdDocumentEntity.setExtension(extension);

                                                            return teacherGuardianDocumentRepository.save(teacherGrdDocumentEntity)
                                                                    .flatMap(saveSiblingDocumentEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", saveSiblingDocumentEntity)))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to Stored Record.There is something wrong please try again"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                        })
                                                        .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                                )
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"));
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Guardian Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-documents_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherGuardianDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherGuardianDocumentRepository.findByUuidAndDeletedAtIsNull(teacherGuardianDocumentUUID)
                        .flatMap(previousEntity -> {

                            MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                            TeacherGuardianDocumentEntity updatedEntity = TeacherGuardianDocumentEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .teacherGuardianUUID(previousEntity.getTeacherGuardianUUID())
                                    .docId(UUID.fromString(value.getFirst("docId").trim()))
                                    .title(value.getFirst("title").trim())
                                    .description(value.getFirst("description").trim())
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            sendFormData.add("docId", String.valueOf(updatedEntity.getDocId()));

                            return apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getDocId())
                                    .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                            .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                    .flatMap(extension -> {

                                                        updatedEntity.setExtension(extension);

                                                        return teacherGuardianDocumentRepository.save(previousEntity)
                                                                .then(teacherGuardianDocumentRepository.save(updatedEntity))
                                                                .flatMap(saveteacherGuardDocEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                        .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", saveteacherGuardDocEntity)))
                                                                .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again"))
                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                    })
                                                    .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                            )
                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-documents_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherGuardianDocumentRepository.findByUuidAndDeletedAtIsNull(teacherGuardianDocumentUUID)
                .flatMap(teacherGuardianDocumentEntity -> {

                    teacherGuardianDocumentEntity.setDeletedBy(UUID.fromString(userId));
                    teacherGuardianDocumentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherGuardianDocumentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherGuardianDocumentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherGuardianDocumentEntity.setReqDeletedIP(reqIp);
                    teacherGuardianDocumentEntity.setReqDeletedPort(reqPort);
                    teacherGuardianDocumentEntity.setReqDeletedBrowser(reqBrowser);
                    teacherGuardianDocumentEntity.setReqDeletedOS(reqOs);
                    teacherGuardianDocumentEntity.setReqDeletedDevice(reqDevice);
                    teacherGuardianDocumentEntity.setReqDeletedReferer(reqReferer);

                    return teacherGuardianDocumentRepository.save(teacherGuardianDocumentEntity)
                            .flatMap(siblingDocEntity -> responseSuccessMsg("Record Deleted Successfully.", siblingDocEntity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
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
