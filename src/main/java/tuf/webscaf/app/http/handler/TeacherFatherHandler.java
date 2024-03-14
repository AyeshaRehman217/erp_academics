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
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherFatherHandler")
@Component
public class TeacherFatherHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    SlaveTeacherFatherRepository slaveTeacherFatherRepository;

    @Autowired
    TeacherFatherProfileRepository teacherFatherProfileRepository;

    @Autowired
    TeacherFatherDocumentRepository teacherFatherDocumentRepository;

    @Autowired
    TeacherFatherAcademicHistoryRepository teacherFatherAcademicHistoryRepository;

    @Autowired
    TeacherFatherJobHistoryRepository teacherFatherJobHistoryRepository;

    @Autowired
    TeacherFatherFinancialHistoryRepository teacherFatherFinancialHistoryRepository;

    @Autowired
    TeacherFatherAddressRepository teacherFatherAddressRepository;

    @Autowired
    TeacherFatherFamilyDoctorRepository teacherFatherFamilyDoctorRepository;

    @Autowired
    TeacherFatherAilmentPvtRepository teacherFatherAilmentPvtRepository;

    @Autowired
    TeacherFatherHobbyPvtRepository teacherFatherHobbyPvtRepository;

    @Autowired
    TeacherFatherNationalityPvtRepository teacherFatherNationalityPvtRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;


    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of Status
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
            Flux<SlaveTeacherFatherEntity> slaveTeacherFatherEntityFlux = slaveTeacherFatherRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveTeacherFatherEntityFlux
                    .collectList()
                    .flatMap(teacherFatherEntity -> slaveTeacherFatherRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherFatherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherFatherEntity> slaveTeacherFatherEntityFlux = slaveTeacherFatherRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveTeacherFatherEntityFlux
                    .collectList()
                    .flatMap(teacherFatherEntity -> slaveTeacherFatherRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (teacherFatherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                .flatMap(teacherFatherEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherFatherEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_teacher_show")
    public Mono<ServerResponse> showByTeacherUUID(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("teacherUUID"));

        return slaveTeacherFatherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherFatherEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherFatherEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_store")
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

                    TeacherFatherEntity teacherFatherEntity = TeacherFatherEntity.builder()
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                            .uuid(UUID.randomUUID())
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

                    // checks if teacher uuid exists
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherFatherEntity.getTeacherUUID())
                            .flatMap(teacherEntity -> teacherFatherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                    .flatMap(fatherRecordAlreadyExists -> responseInfoMsg("Teacher Father Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> teacherFatherRepository.save(teacherFatherEntity)
                                            .flatMap(teacherFatherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherFatherEntityDB))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));

                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                        .flatMap(entity -> {

                            TeacherFatherEntity updatedEntity = TeacherFatherEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(entity.getReqCreatedIP())
                                    .reqCreatedPort(entity.getReqCreatedPort())
                                    .reqCreatedBrowser(entity.getReqCreatedBrowser())
                                    .reqCreatedOS(entity.getReqCreatedOS())
                                    .reqCreatedDevice(entity.getReqCreatedDevice())
                                    .reqCreatedReferer(entity.getReqCreatedReferer())
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
                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            //Storing Deleted Previous Entity First and Then Updated Entity
                            return teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                    .flatMap(teacherEntity -> teacherFatherRepository.findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(teacherEntity.getUuid(), teacherFatherUUID)
                                            .flatMap(fatherRecordAlreadyExists -> responseInfoMsg("Teacher Father Record Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> teacherFatherRepository.save(entity)
                                                    .then(teacherFatherRepository.save(updatedEntity))
                                                    .flatMap(teacherFatherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherFatherEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                            .flatMap(teacherFatherEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherFatherEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherFatherEntity updatedTeacherFatherEntity = TeacherFatherEntity.builder()
                                        .uuid(teacherFatherEntityDB.getUuid())
                                        .teacherUUID(teacherFatherEntityDB.getTeacherUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherFatherEntityDB.getCreatedAt())
                                        .createdBy(teacherFatherEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherFatherEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherFatherEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherFatherEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherFatherEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherFatherEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherFatherEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherFatherEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherFatherEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherFatherEntityDB.setReqDeletedIP(reqIp);
                                teacherFatherEntityDB.setReqDeletedPort(reqPort);
                                teacherFatherEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherFatherEntityDB.setReqDeletedOS(reqOs);
                                teacherFatherEntityDB.setReqDeletedDevice(reqDevice);
                                teacherFatherEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherFatherRepository.save(teacherFatherEntityDB)
                                        .then(teacherFatherRepository.save(updatedTeacherFatherEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-fathers_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherFatherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                //Checks if Teacher Father Reference exists in Teacher Father Profiles
                .flatMap(teacherFatherEntity -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                        .flatMap(teacherFatherProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        // Checks if Teacher Father Reference exists in Teacher Father Documents
                        .switchIfEmpty(Mono.defer(() -> teacherFatherDocumentRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Academic History
                        .switchIfEmpty(Mono.defer(() -> teacherFatherAcademicHistoryRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherAcademicHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherFatherFinancialHistoryRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Job History
                        .switchIfEmpty(Mono.defer(() -> teacherFatherJobHistoryRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherFatherAddressRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Family Doctor
                        .switchIfEmpty(Mono.defer(() -> teacherFatherFamilyDoctorRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Hobbies Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherFatherHobbyPvtRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Ailments Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherFatherAilmentPvtRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Father Nationalities Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherFatherNationalityPvtRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherFatherNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Father Reference exists in Teacher Guardian
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findFirstByGuardianUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .flatMap(teacherGuardianEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
                            teacherFatherEntity.setDeletedBy(UUID.fromString(userId));
                            teacherFatherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherFatherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherFatherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherFatherEntity.setReqDeletedIP(reqIp);
                            teacherFatherEntity.setReqDeletedPort(reqPort);
                            teacherFatherEntity.setReqDeletedBrowser(reqBrowser);
                            teacherFatherEntity.setReqDeletedOS(reqOs);
                            teacherFatherEntity.setReqDeletedDevice(reqDevice);
                            teacherFatherEntity.setReqDeletedReferer(reqReferer);

                            return teacherFatherRepository.save(teacherFatherEntity)
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
