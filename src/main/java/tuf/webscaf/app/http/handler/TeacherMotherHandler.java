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
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherMotherRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherMotherHandler")
@Component
public class TeacherMotherHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    SlaveTeacherMotherRepository slaveTeacherMotherRepository;

    @Autowired
    TeacherMotherProfileRepository teacherMotherProfileRepository;

    @Autowired
    TeacherMotherDocumentRepository teacherMotherDocumentRepository;

    @Autowired
    TeacherMotherAcademicHistoryRepository teacherMotherAcademicHistoryRepository;

    @Autowired
    TeacherMotherJobHistoryRepository teacherMotherJobHistoryRepository;

    @Autowired
    TeacherMotherFinancialHistoryRepository teacherMotherFinancialHistoryRepository;

    @Autowired
    TeacherMotherAddressRepository teacherMotherAddressRepository;

    @Autowired
    TeacherMotherFamilyDoctorRepository teacherMotherFamilyDoctorRepository;

    @Autowired
    TeacherMotherAilmentPvtRepository teacherMotherAilmentPvtRepository;

    @Autowired
    TeacherMotherHobbyPvtRepository teacherMotherHobbyPvtRepository;

    @Autowired
    TeacherMotherNationalityPvtRepository teacherMotherNationalityPvtRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;


    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_index")
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
            Flux<SlaveTeacherMotherEntity> slaveTeacherMotherEntityFlux = slaveTeacherMotherRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveTeacherMotherEntityFlux
                    .collectList()
                    .flatMap(teacherMotherEntity -> slaveTeacherMotherRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherMotherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherMotherEntity> slaveTeacherMotherEntityFlux = slaveTeacherMotherRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveTeacherMotherEntityFlux
                    .collectList()
                    .flatMap(teacherMotherEntity -> slaveTeacherMotherRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (teacherMotherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherMotherEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherMotherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                .flatMap(teacherMotherEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherMotherEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_teacher_show")
    public Mono<ServerResponse> showByTeacherUUID(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("teacherUUID"));

        return slaveTeacherMotherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherMotherEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherMotherEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_store")
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

                    TeacherMotherEntity teacherMotherEntity = TeacherMotherEntity.builder()
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
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherMotherEntity.getTeacherUUID())
                            .flatMap(teacherEntity -> teacherMotherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                    .flatMap(motherRecordAlreadyExists -> responseInfoMsg("Teacher Mother Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> teacherMotherRepository.save(teacherMotherEntity)
                                            .flatMap(teacherMotherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherMotherEntityDB))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherMotherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                        .flatMap(entity -> {

                            TeacherMotherEntity updatedEntity = TeacherMotherEntity.builder()
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
                                    .flatMap(teacherEntity -> teacherMotherRepository.findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(teacherEntity.getUuid(), teacherMotherUUID)
                                            .flatMap(motherRecordAlreadyExists -> responseInfoMsg("Teacher Mother Record Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> teacherMotherRepository.save(entity)
                                                    .then(teacherMotherRepository.save(updatedEntity))
                                                    .flatMap(teacherMotherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherMotherEntityDB))
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

    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherMotherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                            .flatMap(teacherMotherEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherMotherEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherMotherEntity updatedTeacherMotherEntity = TeacherMotherEntity.builder()
                                        .uuid(teacherMotherEntityDB.getUuid())
                                        .teacherUUID(teacherMotherEntityDB.getTeacherUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherMotherEntityDB.getCreatedAt())
                                        .createdBy(teacherMotherEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherMotherEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherMotherEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherMotherEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherMotherEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherMotherEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherMotherEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherMotherEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherMotherEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherMotherEntityDB.setReqDeletedIP(reqIp);
                                teacherMotherEntityDB.setReqDeletedPort(reqPort);
                                teacherMotherEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherMotherEntityDB.setReqDeletedOS(reqOs);
                                teacherMotherEntityDB.setReqDeletedDevice(reqDevice);
                                teacherMotherEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherMotherRepository.save(teacherMotherEntityDB)
                                        .then(teacherMotherRepository.save(updatedTeacherMotherEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-mothers_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherMotherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                //Checks if Teacher Mother Reference exists in Teacher Mother Profiles
                .flatMap(teacherMotherEntity -> teacherMotherProfileRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                        .flatMap(teacherMotherProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        // Checks if Teacher Mother Reference exists in Teacher Mother Documents
                        .switchIfEmpty(Mono.defer(() -> teacherMotherDocumentRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Academic History
                        .switchIfEmpty(Mono.defer(() -> teacherMotherAcademicHistoryRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherAcademicHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherMotherFinancialHistoryRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Job History
                        .switchIfEmpty(Mono.defer(() -> teacherMotherJobHistoryRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherMotherAddressRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Family Doctor
                        .switchIfEmpty(Mono.defer(() -> teacherMotherFamilyDoctorRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Hobbies Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherMotherHobbyPvtRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Ailments Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherMotherAilmentPvtRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Mother Nationalities Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherMotherNationalityPvtRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherMotherNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Mother Reference exists in Teacher Guardian
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findFirstByGuardianUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .flatMap(teacherGuardianEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
                            teacherMotherEntity.setDeletedBy(UUID.fromString(userId));
                            teacherMotherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherMotherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherMotherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherMotherEntity.setReqDeletedIP(reqIp);
                            teacherMotherEntity.setReqDeletedPort(reqPort);
                            teacherMotherEntity.setReqDeletedBrowser(reqBrowser);
                            teacherMotherEntity.setReqDeletedOS(reqOs);
                            teacherMotherEntity.setReqDeletedDevice(reqDevice);
                            teacherMotherEntity.setReqDeletedReferer(reqReferer);

                            return teacherMotherRepository.save(teacherMotherEntity)
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
