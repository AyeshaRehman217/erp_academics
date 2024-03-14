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
import tuf.webscaf.app.dbContext.master.entity.StudentFatherDocumentEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherDocumentRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherDocumentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherDocumentRepository;
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

@Tag(name = "studentFatherDocumentHandler")
@Component
public class StudentFatherDocumentHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentFatherDocumentRepository studentFatherDocumentRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    SlaveStudentFatherDocumentRepository slaveStudentFatherDocumentRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-father-documents_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Father UUID
        String studentFatherUUID = serverRequest.queryParam("studentFatherUUID").map(String::toString).orElse("").trim();

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

        if (!studentFatherUUID.isEmpty()) {

            Flux<SlaveStudentFatherDocumentEntity> slaveStudentFatherDocumentFlux = slaveStudentFatherDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID));

            return slaveStudentFatherDocumentFlux
                    .collectList()
                    .flatMap(studentFatherDocumentEntity -> slaveStudentFatherDocumentRepository.countByTitleContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID))
                            .flatMap(count -> {
                                if (studentFatherDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentFatherDocumentEntity> slaveStudentFatherDocumentFlux = slaveStudentFatherDocumentRepository
                    .findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveStudentFatherDocumentFlux
                    .collectList()
                    .flatMap(studentFatherDocumentEntity -> slaveStudentFatherDocumentRepository.countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentFatherDocumentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherDocumentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-father-documents_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentFatherDocumentUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentFatherDocumentRepository.findByUuidAndDeletedAtIsNull(studentFatherDocumentUUID)
                .flatMap(studentFatherDocumentEntity -> responseSuccessMsg("Record Fetched Successfully", studentFatherDocumentEntity))
                .switchIfEmpty(responseInfoMsg("Record Does not exist."))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //Show Student Father Document Against Student UUID , Student Father UUID and Father Document UUID
    @AuthHasPermission(value = "academic_api_v1_student_student-father_student-father-documents_show")
    public Mono<ServerResponse> showByStudentFatherAndStudent(ServerRequest serverRequest) {
        UUID studentFatherDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        UUID studentFatherUUID = UUID.fromString(serverRequest.queryParam("studentFatherUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentFatherDocumentRepository.showDocsAgainstStudentAndStudentFather(studentUUID, studentFatherUUID, studentFatherDocumentUUID)
                .flatMap(studentFatherDocEntity -> responseSuccessMsg("Record Fetched Successfully", studentFatherDocEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-documents_store")
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

                    StudentFatherDocumentEntity stdFatherDocumentEntity = StudentFatherDocumentEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentFatherUUID(UUID.fromString(value.getFirst("studentFatherUUID").trim()))
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

                    sendFormData.add("docId", String.valueOf(stdFatherDocumentEntity.getDocId()));

                    return studentFatherRepository.findByUuidAndDeletedAtIsNull(stdFatherDocumentEntity.getStudentFatherUUID())
                            .flatMap(studentFatherEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", stdFatherDocumentEntity.getDocId())
                                    .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                            .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                    .flatMap(extension -> {

                                                        stdFatherDocumentEntity.setExtension(extension);

                                                        return studentFatherDocumentRepository.save(stdFatherDocumentEntity)
                                                                .flatMap(stdFatherDocEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                        .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", stdFatherDocEntity)))
                                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again"))
                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                    })
                                                    .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                            )
                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Student Father record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Father record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-documents_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentFatherDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentFatherDocumentRepository.findByUuidAndDeletedAtIsNull(studentFatherDocumentUUID)
                        .flatMap(previousEntity -> {

                            MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                            StudentFatherDocumentEntity updatedEntity = StudentFatherDocumentEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentFatherUUID(previousEntity.getStudentFatherUUID())
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

                            return studentFatherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentFatherUUID())
                                    .flatMap(studentFatherEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getDocId())
                                            .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                                    .flatMap(docId -> apiCallService.getDocumentExtension(documentJson)
                                                            .flatMap(extension -> {

                                                                updatedEntity.setExtension(extension);

                                                                return studentFatherDocumentRepository.save(previousEntity)
                                                                        .then(studentFatherDocumentRepository.save(updatedEntity))
                                                                        .flatMap(stdFatherDocEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", stdFatherDocEntity)))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again"))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                            })
                                                            .switchIfEmpty(responseInfoMsg("Unable to Fetch extension.There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Fetch extension.Please Contact Developer."))
                                                    )
                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Student father record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student father record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-documents_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentFatherDocumentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentFatherDocumentRepository.findByUuidAndDeletedAtIsNull(studentFatherDocumentUUID)
                .flatMap(studentFatherDocumentEntity -> {

                    studentFatherDocumentEntity.setDeletedBy(UUID.fromString(userId));
                    studentFatherDocumentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentFatherDocumentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentFatherDocumentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentFatherDocumentEntity.setReqDeletedIP(reqIp);
                    studentFatherDocumentEntity.setReqDeletedPort(reqPort);
                    studentFatherDocumentEntity.setReqDeletedBrowser(reqBrowser);
                    studentFatherDocumentEntity.setReqDeletedOS(reqOs);
                    studentFatherDocumentEntity.setReqDeletedDevice(reqDevice);
                    studentFatherDocumentEntity.setReqDeletedReferer(reqReferer);

                    return studentFatherDocumentRepository.save(studentFatherDocumentEntity)
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
