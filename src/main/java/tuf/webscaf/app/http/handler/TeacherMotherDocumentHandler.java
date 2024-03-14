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
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherDocumentEntity;
import tuf.webscaf.app.dbContext.master.repositry.TeacherMotherAcademicHistoryRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherMotherDocumentRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherMotherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherDocumentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherMotherDocumentRepository;
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

@Tag(name = "teacherMotherDocumentHandler")
@Component
public class TeacherMotherDocumentHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherMotherDocumentRepository teacherMotherDocumentRepository;

    @Autowired
    SlaveTeacherMotherDocumentRepository slaveTeacherMotherDocumentRepository;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    TeacherMotherAcademicHistoryRepository teacherMotherAcademicHistoryRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-documents_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher UUID
        String teacherMotherUUID = serverRequest.queryParam("teacherMotherUUID").map(String::toString).orElse("").trim();

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

        if (!teacherMotherUUID.isEmpty()) {

            Flux<SlaveTeacherMotherDocumentEntity> slaveTeacherMotherDocumentFlux = slaveTeacherMotherDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherMotherUUID), searchKeyWord, UUID.fromString(teacherMotherUUID));

            return slaveTeacherMotherDocumentFlux
                    .collectList()
                    .flatMap(teacherMotherDocumentEntity -> slaveTeacherMotherDocumentRepository.countByTitleContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherMotherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherMotherUUID), searchKeyWord, UUID.fromString(teacherMotherUUID))
                            .flatMap(count -> {
                                if (teacherMotherDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherMotherDocumentEntity> slaveTeacherMotherDocumentFlux = slaveTeacherMotherDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveTeacherMotherDocumentFlux
                    .collectList()
                    .flatMap(teacherMotherDocumentEntity -> slaveTeacherMotherDocumentRepository.countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherMotherDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-documents_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherMotherDocumentUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherMotherDocumentRepository.findByUuidAndDeletedAtIsNull(teacherMotherDocumentUUID)
                .flatMap(teacherFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    //Show Teacher Mother Document Against Teacher UUID , Teacher Mother UUID and Mother Document UUID
    @AuthHasPermission(value = "academic_api_v1_teacher_teacher-mother_teacher-mother-documents_index")
    public Mono<ServerResponse> showByTeacherMotherAndTeacher(ServerRequest serverRequest) {
        UUID teacherMotherDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        UUID teacherMotherUUID = UUID.fromString(serverRequest.queryParam("teacherMotherUUID").map(String::toString).orElse(""));
        UUID teacherUUID = UUID.fromString(serverRequest.queryParam("teacherUUID").map(String::toString).orElse(""));

        return slaveTeacherMotherDocumentRepository.showDocsAgainstTeacherAndTeacherMother(teacherUUID, teacherMotherUUID, teacherMotherDocumentUUID)
                .flatMap(teacherMotherDocEntity -> responseSuccessMsg("Record Fetched Successfully", teacherMotherDocEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-documents_store")
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
                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                    TeacherMotherDocumentEntity teacherMotherDocEntity = TeacherMotherDocumentEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .teacherMotherUUID(UUID.fromString(value.getFirst("teacherMotherUUID")))
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

                    sendFormData.add("docId", String.valueOf(teacherMotherDocEntity.getDocId()));

                    // check if teacher mother uuid exists
                    return teacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherDocEntity.getTeacherMotherUUID())
                            // check if doc Id exists
                            .flatMap(teacherMotherEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherMotherDocEntity.getDocId())
                                    .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                            .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                    .flatMap(extension -> {

                                                        teacherMotherDocEntity.setExtension(extension);

                                                        return teacherMotherDocumentRepository.save(teacherMotherDocEntity)
                                                                .flatMap(saveteacherMotherDocEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                        .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", saveteacherMotherDocEntity)))
                                                                .switchIfEmpty(responseInfoMsg("Unable to Stored Record.There is something wrong please try again"))
                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                    })
                                                    .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                            )
                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Teacher Mother Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Mother Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-documents_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherMotherDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherMotherDocumentRepository.findByUuidAndDeletedAtIsNull(teacherMotherDocumentUUID)
                        .flatMap(previousEntity -> {
                            MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                            TeacherMotherDocumentEntity updatedEntity = TeacherMotherDocumentEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .teacherMotherUUID(previousEntity.getTeacherMotherUUID())
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

                            // checks if doc Id exists
                            return apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getDocId())
                                    .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                            .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                    .flatMap(extension -> {

                                                        updatedEntity.setExtension(extension);

                                                        return teacherMotherDocumentRepository.save(previousEntity)
                                                                .then(teacherMotherDocumentRepository.save(updatedEntity))
                                                                .flatMap(saveteacherMthDocumentEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                        .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", saveteacherMthDocumentEntity)))
                                                                .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again"))
                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                    })
                                                    .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                            )
                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mother-documents_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherMotherDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherMotherDocumentRepository.findByUuidAndDeletedAtIsNull(teacherMotherDocumentUUID)
                .flatMap(teacherMotherDocumentEntity -> {
                    teacherMotherDocumentEntity.setDeletedBy(UUID.fromString(userId));
                    teacherMotherDocumentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherMotherDocumentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherMotherDocumentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherMotherDocumentEntity.setReqDeletedIP(reqIp);
                    teacherMotherDocumentEntity.setReqDeletedPort(reqPort);
                    teacherMotherDocumentEntity.setReqDeletedBrowser(reqBrowser);
                    teacherMotherDocumentEntity.setReqDeletedOS(reqOs);
                    teacherMotherDocumentEntity.setReqDeletedDevice(reqDevice);
                    teacherMotherDocumentEntity.setReqDeletedReferer(reqReferer);

                    return teacherMotherDocumentRepository.save(teacherMotherDocumentEntity)
                            .flatMap(siblingDocEntity -> responseSuccessMsg("Record Deleted Successfully", siblingDocEntity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
