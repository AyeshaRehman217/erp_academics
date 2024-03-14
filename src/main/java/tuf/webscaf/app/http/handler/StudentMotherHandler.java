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
import tuf.webscaf.app.dbContext.master.entity.StudentMotherEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentMotherHandler")
@Component
public class

StudentMotherHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    SlaveStudentMotherRepository slaveStudentMotherRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentMotherProfileRepository studentMotherProfileRepository;

    @Autowired
    StudentMotherDocumentRepository studentMotherDocumentRepository;

    @Autowired
    StudentMotherFinancialHistoryRepository studentMotherFinancialHistoryRepository;

    @Autowired
    StudentMotherJobHistoryRepository studentMotherJobHistoryRepository;

    @Autowired
    StudentMotherFamilyDoctorRepository studentMotherFamilyDoctorRepository;

    @Autowired
    StudentMotherHobbyPvtRepository studentMotherHobbyPvtRepository;

    @Autowired
    StudentMotherNationalityPvtRepository studentMotherNationalityPvtRepository;

    @Autowired
    StudentMotherAilmentPvtRepository studentMotherAilmentPvtRepository;

    @Autowired
    StudentMotherAddressRepository studentMotherAddressRepository;

    @Autowired
    StudentMotherAcademicHistoryRepository studentMotherAcademicHistoryRepository;

    @Autowired
    StudentMotherLanguagePvtRepository studentMotherLanguagePvtRepository;

    @AuthHasPermission(value = "academic_api_v1_student-mothers_index")
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
            Flux<SlaveStudentMotherEntity> slaveStudentMotherFlux = slaveStudentMotherRepository
                    .findAllByDeletedAtIsNullAndStatus(pageable, Boolean.valueOf(status));
            return slaveStudentMotherFlux
                    .collectList()
                    .flatMap(studentMotherEntity -> slaveStudentMotherRepository
                            .countByDeletedAtIsNullAndStatus( Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentMotherEntity> slaveStudentMotherFlux = slaveStudentMotherRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveStudentMotherFlux
                    .collectList()
                    .flatMap(studentMotherEntity -> slaveStudentMotherRepository.countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (studentMotherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-mothers_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                .flatMap(studentMotherEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mothers_student_show")
    public Mono<ServerResponse> showByStudentUUID(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));

        return slaveStudentMotherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                .flatMap(studentMotherEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mothers_store")
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

                    StudentMotherEntity entity = StudentMotherEntity.builder()
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

                    // checks if student uuid exists
                    return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
                            .flatMap(studentEntity -> studentMotherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                    .flatMap(motherRecordAlreadyExists -> responseInfoMsg("Student Mother Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> studentMotherRepository.save(entity)
                                            .flatMap(studentMotherEntityDB -> responseSuccessMsg("Record Stored Successfully", studentMotherEntityDB))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mothers_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> studentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                                .flatMap(previousStudentMotherEntity -> {

                                    StudentMotherEntity updatedEntity = StudentMotherEntity.builder()
                                            .uuid(previousStudentMotherEntity.getUuid())
                                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .createdAt(previousStudentMotherEntity.getCreatedAt())
                                            .createdBy(previousStudentMotherEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousStudentMotherEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousStudentMotherEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousStudentMotherEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousStudentMotherEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousStudentMotherEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousStudentMotherEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    previousStudentMotherEntity.setDeletedBy(UUID.fromString(userId));
                                    previousStudentMotherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousStudentMotherEntity.setReqDeletedIP(reqIp);
                                    previousStudentMotherEntity.setReqDeletedPort(reqPort);
                                    previousStudentMotherEntity.setReqDeletedBrowser(reqBrowser);
                                    previousStudentMotherEntity.setReqDeletedOS(reqOs);
                                    previousStudentMotherEntity.setReqDeletedDevice(reqDevice);
                                    previousStudentMotherEntity.setReqDeletedReferer(reqReferer);

                                    //Storing Deleted Previous Entity First and Then Updated Entity
                                    return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                            .flatMap(studentEntity -> studentMotherRepository.findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(studentEntity.getUuid(), studentMotherUUID)
                                                    .flatMap(motherRecordAlreadyExists -> responseInfoMsg("Student Mother Record Already Exists"))
                                                    .switchIfEmpty(Mono.defer(() -> studentMotherRepository.save(previousStudentMotherEntity)
                                                            .then(studentMotherRepository.save(updatedEntity))
                                                            .flatMap(studentMotherEntityDB -> responseSuccessMsg("Record Updated Successfully", studentMotherEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                    ))
                                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mothers_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentMotherEntity studentMotherEntity = StudentMotherEntity.builder()
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

                                return studentMotherRepository.save(previousEntity)
                                        .then(studentMotherRepository.save(studentMotherEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mothers_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                //Checks if Student Mother Reference exists in Student Mother Profiles
                .flatMap(studentMotherEntity -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                        .flatMap(studentMotherProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Student Mother Reference exists in Student Mother Documents
                        .switchIfEmpty(Mono.defer(() -> studentMotherDocumentRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Financial History
                        .switchIfEmpty(Mono.defer(() -> studentMotherFinancialHistoryRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Job History
                        .switchIfEmpty(Mono.defer(() -> studentMotherJobHistoryRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Family Doctor
                        .switchIfEmpty(Mono.defer(() -> studentMotherFamilyDoctorRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> studentMotherHobbyPvtRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> studentMotherNationalityPvtRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> studentMotherAilmentPvtRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Address
                        .switchIfEmpty(Mono.defer(() -> studentMotherAddressRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Academic History
                        .switchIfEmpty(Mono.defer(() -> studentMotherAcademicHistoryRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Mother Reference exists in Student Mother Language Pvt
                        .switchIfEmpty(Mono.defer(() -> studentMotherLanguagePvtRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .flatMap(studentMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            studentMotherEntity.setDeletedBy(UUID.fromString(userId));
                            studentMotherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentMotherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentMotherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentMotherEntity.setReqDeletedIP(reqIp);
                            studentMotherEntity.setReqDeletedPort(reqPort);
                            studentMotherEntity.setReqDeletedBrowser(reqBrowser);
                            studentMotherEntity.setReqDeletedOS(reqOs);
                            studentMotherEntity.setReqDeletedDevice(reqDevice);
                            studentMotherEntity.setReqDeletedReferer(reqReferer);


                            return studentMotherRepository.save(studentMotherEntity)
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
