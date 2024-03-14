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
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingDocumentEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingDocumentRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSiblingRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingDocumentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingDocumentRepository;
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

@Tag(name = "studentSiblingDocumentHandler")
@Component
public class StudentSiblingDocumentHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingDocumentRepository studentSiblingDocumentRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    SlaveStudentSiblingDocumentRepository slaveStudentSiblingDocumentRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-sibling-documents_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Sibling UUID
        String studentSiblingUUID = serverRequest.queryParam("studentSiblingUUID").map(String::toString).orElse("").trim();

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

        if (!studentSiblingUUID.isEmpty()) {

            Flux<SlaveStudentSiblingDocumentEntity> slaveStudentSiblingDocumentFlux = slaveStudentSiblingDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID));

            return slaveStudentSiblingDocumentFlux
                    .collectList()
                    .flatMap(studentSiblingDocumentEntity -> slaveStudentSiblingDocumentRepository.countByTitleContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSiblingUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentSiblingUUID), searchKeyWord, UUID.fromString(studentSiblingUUID))
                            .flatMap(count -> {
                                if (studentSiblingDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentSiblingDocumentEntity> slaveStudentSiblingDocumentFlux = slaveStudentSiblingDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveStudentSiblingDocumentFlux
                    .collectList()
                    .flatMap(studentSiblingDocumentEntity -> slaveStudentSiblingDocumentRepository.countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSiblingDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-documents_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingDocumentUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSiblingDocumentRepository.findByUuidAndDeletedAtIsNull(studentSiblingDocumentUUID)
                .flatMap(studentSiblingDocumentEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingDocumentEntity))
                .switchIfEmpty(responseInfoMsg("Record Does not exist."))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    //Show Student Sibling Document Against Student UUID , Student Sibling UUID and Sibling Document UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-sibling_student-sibling-documents_show")
    public Mono<ServerResponse> showByStudentSiblingAndStudent(ServerRequest serverRequest) {
        UUID studentSiblingDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        UUID studentSiblingUUID = UUID.fromString(serverRequest.queryParam("studentSiblingUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentSiblingDocumentRepository.showDocsAgainstStudentAndStudentSibling(studentUUID, studentSiblingUUID, studentSiblingDocumentUUID)
                .flatMap(studentSiblingDocEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingDocEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-documents_store")
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

                    StudentSiblingDocumentEntity entity = StudentSiblingDocumentEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentSiblingUUID(UUID.fromString(value.getFirst("studentSiblingUUID")))
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

                    sendFormData.add("docId", String.valueOf(entity.getDocId()));

                    // check student sibling uuid
                    return studentSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSiblingUUID())
                            // check doc id from drive api
                            .flatMap(studentSiblingEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getDocId())
                                    .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                            .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                    .flatMap(extension -> {
                                                        entity.setExtension(extension);
                                                        return studentSiblingDocumentRepository.save(entity)
                                                                .flatMap(studentDocumentEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                        .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", studentDocumentEntity)))
                                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again"))
                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                    })
                                                    .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Student Sibling  does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Sibling  does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-documents_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSiblingDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentSiblingDocumentRepository.findByUuidAndDeletedAtIsNull(studentSiblingDocumentUUID)
                        .flatMap(previousEntity -> {
                            MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                            StudentSiblingDocumentEntity updatedEntity = StudentSiblingDocumentEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentSiblingUUID(previousEntity.getStudentSiblingUUID())
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

                            // check student sibling uuid
                            return studentSiblingRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentSiblingUUID())
                                    // check doc id from drive api
                                    .flatMap(studentSiblingEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getDocId())
                                            .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                                    .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                            .flatMap(extension -> {
                                                                updatedEntity.setExtension(extension);
                                                                return studentSiblingDocumentRepository.save(previousEntity)
                                                                        .then(studentSiblingDocumentRepository.save(updatedEntity))
                                                                        .flatMap(studentDocumentEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", studentDocumentEntity)))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again"))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                            })
                                                            .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                                            )).switchIfEmpty(responseInfoMsg("Student record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-documents_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentSiblingDocumentRepository.findByUuidAndDeletedAtIsNull(studentSiblingDocumentUUID)
                .flatMap(studentSiblingDocumentEntity -> {

                    studentSiblingDocumentEntity.setDeletedBy(UUID.fromString(userId));
                    studentSiblingDocumentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentSiblingDocumentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentSiblingDocumentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentSiblingDocumentEntity.setReqDeletedIP(reqIp);
                    studentSiblingDocumentEntity.setReqDeletedPort(reqPort);
                    studentSiblingDocumentEntity.setReqDeletedBrowser(reqBrowser);
                    studentSiblingDocumentEntity.setReqDeletedOS(reqOs);
                    studentSiblingDocumentEntity.setReqDeletedDevice(reqDevice);
                    studentSiblingDocumentEntity.setReqDeletedReferer(reqReferer);


                    return studentSiblingDocumentRepository.save(studentSiblingDocumentEntity)
                            .flatMap(siblingDocEntity -> responseSuccessMsg("Record Deleted Successfully.", siblingDocEntity))
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
