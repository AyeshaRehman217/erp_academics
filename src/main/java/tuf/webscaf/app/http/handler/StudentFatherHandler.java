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
import tuf.webscaf.app.dbContext.master.entity.StudentFatherEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentFatherHandler")
@Component
public class StudentFatherHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    SlaveStudentFatherRepository slaveStudentFatherRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentFatherProfileRepository studentFatherProfileRepository;

    @Autowired
    StudentFatherDocumentRepository studentFatherDocumentRepository;

    @Autowired
    StudentFatherFinancialHistoryRepository studentFatherFinancialHistoryRepository;

    @Autowired
    StudentFatherJobHistoryRepository studentFatherJobHistoryRepository;

    @Autowired
    StudentFatherFamilyDoctorRepository studentFatherFamilyDoctorRepository;

    @Autowired
    StudentFatherHobbyPvtRepository studentFatherHobbyPvtRepository;

    @Autowired
    StudentFatherNationalityPvtRepository studentFatherNationalityPvtRepository;

    @Autowired
    StudentFatherAilmentPvtRepository studentFatherAilmentPvtRepository;

    @Autowired
    StudentFatherAddressRepository studentFatherAddressRepository;

    @Autowired
    StudentFatherAcademicHistoryRepository studentFatherAcademicHistoryRepository;

    @Autowired
    StudentFatherLanguagePvtRepository studentFatherLanguagePvtRepository;

    @AuthHasPermission(value = "academic_api_v1_student-fathers_index")
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

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveStudentFatherEntity> slaveStudentFatherFlux = slaveStudentFatherRepository
                    .findAllByDeletedAtIsNullAndStatus(pageable, Boolean.valueOf(status));
            return slaveStudentFatherFlux
                    .collectList()
                    .flatMap(studentFatherEntity -> slaveStudentFatherRepository
                            .countByDeletedAtIsNullAndStatus(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentFatherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentFatherEntity> slaveStudentFatherFlux = slaveStudentFatherRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveStudentFatherFlux
                    .collectList()
                    .flatMap(studentFatherEntity -> slaveStudentFatherRepository.countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (studentFatherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-fathers_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentFatherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                .flatMap(studentFatherEntity -> responseSuccessMsg("Record Fetched Successfully", studentFatherEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-fathers_student_show")
    public Mono<ServerResponse> showByStudentUUID(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));

        return slaveStudentFatherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                .flatMap(studentFatherEntity -> responseSuccessMsg("Record Fetched Successfully", studentFatherEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-fathers_store")
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

                    StudentFatherEntity entity = StudentFatherEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
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

//                    check student uuid
                    return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
                            // checks if student father profile already exists
                            .flatMap(studentEntity -> studentFatherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(entity.getStudentUUID())
                                    .flatMap(studentFatherRecordAlreadyExists -> responseInfoMsg("Student Father Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> studentFatherRepository.save(entity)
                                            .flatMap(studentFatherEntity -> responseSuccessMsg("Record Stored Successfully", studentFatherEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Student Record doses not exist"))
                            .onErrorResume(err -> responseErrorMsg("Student Record doses not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-fathers_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentFatherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> studentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                                .flatMap(previousStudentFatherEntity -> {

                                    StudentFatherEntity updatedEntity = StudentFatherEntity.builder()
                                            .uuid(previousStudentFatherEntity.getUuid())
                                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .createdAt(previousStudentFatherEntity.getCreatedAt())
                                            .createdBy(previousStudentFatherEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousStudentFatherEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousStudentFatherEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousStudentFatherEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousStudentFatherEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousStudentFatherEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousStudentFatherEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    previousStudentFatherEntity.setDeletedBy(UUID.fromString(userId));
                                    previousStudentFatherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousStudentFatherEntity.setReqDeletedIP(reqIp);
                                    previousStudentFatherEntity.setReqDeletedPort(reqPort);
                                    previousStudentFatherEntity.setReqDeletedBrowser(reqBrowser);
                                    previousStudentFatherEntity.setReqDeletedOS(reqOs);
                                    previousStudentFatherEntity.setReqDeletedDevice(reqDevice);
                                    previousStudentFatherEntity.setReqDeletedReferer(reqReferer);

//                            check student uuid
                                    return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                            .flatMap(studentEntity -> studentFatherRepository.findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentFatherRecordAlreadyExists -> responseInfoMsg("Student Father Record Already Exists"))
                                                    .switchIfEmpty(Mono.defer(() -> studentFatherRepository.save(previousStudentFatherEntity)
                                                    .then(studentFatherRepository.save(updatedEntity))
                                                    .flatMap(studentFatherEntity -> responseSuccessMsg("Record Updated Successfully", studentFatherEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                    ))
                                            ).switchIfEmpty(responseInfoMsg("Student record doses not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Student record doses not exist. Please contact developer."));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-fathers_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentFatherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentFatherEntity studentFatherEntity = StudentFatherEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .status(status == true ? true : false)
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

                                return studentFatherRepository.save(previousEntity)
                                        .then(studentFatherRepository.save(studentFatherEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-fathers_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentFatherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                //Checks if Student Father Reference exists in Student Father Profiles
                .flatMap(studentFatherEntity -> studentFatherProfileRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                        .flatMap(studentFatherProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Student Father Reference exists in Student Father Documents
                        .switchIfEmpty(Mono.defer(() -> studentFatherDocumentRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Financial History
                        .switchIfEmpty(Mono.defer(() -> studentFatherFinancialHistoryRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Job History
                        .switchIfEmpty(Mono.defer(() -> studentFatherJobHistoryRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Family Doctor
                        .switchIfEmpty(Mono.defer(() -> studentFatherFamilyDoctorRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> studentFatherHobbyPvtRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> studentFatherNationalityPvtRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> studentFatherAilmentPvtRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Address
                        .switchIfEmpty(Mono.defer(() -> studentFatherAddressRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Academic History
                        .switchIfEmpty(Mono.defer(() -> studentFatherAcademicHistoryRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Father Reference exists in Student Father Language Pvt
                        .switchIfEmpty(Mono.defer(() -> studentFatherLanguagePvtRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .flatMap(studentFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            studentFatherEntity.setDeletedBy(UUID.fromString(userId));
                            studentFatherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentFatherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentFatherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentFatherEntity.setReqDeletedIP(reqIp);
                            studentFatherEntity.setReqDeletedPort(reqPort);
                            studentFatherEntity.setReqDeletedBrowser(reqBrowser);
                            studentFatherEntity.setReqDeletedOS(reqOs);
                            studentFatherEntity.setReqDeletedDevice(reqDevice);
                            studentFatherEntity.setReqDeletedReferer(reqReferer);


                            return studentFatherRepository.save(studentFatherEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
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
