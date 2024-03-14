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
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherGuardianHandler")
@Component
public class TeacherGuardianHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    SlaveTeacherGuardianRepository slaveTeacherGuardianRepository;

    @Autowired
    TeacherGuardianProfileRepository teacherGuardianProfileRepository;

    @Autowired
    TeacherGuardianDocumentRepository teacherGuardianDocumentRepository;

    @Autowired
    TeacherGuardianAcademicHistoryRepository teacherGuardianAcademicHistoryRepository;

    @Autowired
    TeacherGuardianJobHistoryRepository teacherGuardianJobHistoryRepository;

    @Autowired
    TeacherGuardianFinancialHistoryRepository teacherGuardianFinancialHistoryRepository;

    @Autowired
    TeacherGuardianAddressRepository teacherGuardianAddressRepository;

    @Autowired
    TeacherGuardianFamilyDoctorRepository teacherGuardianFamilyDoctorRepository;

    @Autowired
    TeacherGuardianAilmentPvtRepository teacherGuardianAilmentPvtRepository;

    @Autowired
    TeacherGuardianHobbyPvtRepository teacherGuardianHobbyPvtRepository;

    @Autowired
    TeacherGuardianNationalityPvtRepository teacherGuardianNationalityPvtRepository;

    @Autowired
    TeacherGuardianLanguagePvtRepository teacherGuardianLanguagePvtRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    GuardianTypeRepository guardianTypeRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_index")
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
            Flux<SlaveTeacherGuardianEntity> slaveTeacherGuardianEntityFlux = slaveTeacherGuardianRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveTeacherGuardianEntityFlux
                    .collectList()
                    .flatMap(teacherGuardianEntity -> slaveTeacherGuardianRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherGuardianEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherGuardianEntity> slaveTeacherGuardianEntityFlux = slaveTeacherGuardianRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveTeacherGuardianEntityFlux
                    .collectList()
                    .flatMap(teacherGuardianEntity -> slaveTeacherGuardianRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (teacherGuardianEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherGuardianEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_teacher_show")
    public Mono<ServerResponse> showByTeacherUUID(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("teacherUUID"));

        return slaveTeacherGuardianRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherGuardianEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_store")
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

                    UUID guardianUUID = null;
                    if ((value.containsKey("guardianUUID") && (value.getFirst("guardianUUID") != ""))) {
                        guardianUUID = UUID.fromString(value.getFirst("guardianUUID").trim());
                    }

                    TeacherGuardianEntity teacherGuardianEntity = TeacherGuardianEntity.builder()
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                            .guardianTypeUUID(UUID.fromString(value.getFirst("guardianTypeUUID")))
                            .guardianUUID(guardianUUID)
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
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherGuardianEntity.getTeacherUUID())
                            // checks if guardian type uuid exists
                            .flatMap(teacherEntity -> teacherGuardianRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                    .flatMap(guardianAlreadyExists -> responseInfoMsg("Teacher Guardian Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(teacherGuardianEntity.getGuardianTypeUUID())
                                            .flatMap(guardianTypeEntity -> {

                                                // if guardian uuid is specified in the request
                                                if (teacherGuardianEntity.getGuardianUUID() != null) {

                                                    // if teacher father is guardian
                                                    switch (guardianTypeEntity.getSlug()) {
                                                        case "father":
                                                            return teacherFatherRepository.findByUuidAndTeacherUUIDAndDeletedAtIsNull(teacherGuardianEntity.getGuardianUUID(), teacherGuardianEntity.getTeacherUUID())
                                                                    .flatMap(teacherFatherEntity -> teacherGuardianRepository.save(teacherGuardianEntity)
                                                                            .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                    .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));


                                                        // if teacher mother is guardian
                                                        case "mother":
                                                            return teacherMotherRepository.findByUuidAndTeacherUUIDAndDeletedAtIsNull(teacherGuardianEntity.getGuardianUUID(), teacherGuardianEntity.getTeacherUUID())
                                                                    .flatMap(teacherMotherEntity -> teacherGuardianRepository.save(teacherGuardianEntity)
                                                                            .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                    .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));

                                                        // if guardian type is other but guardian uuid is given
                                                        case "other":
                                                            return responseInfoMsg("Guardian is not valid for given Guardian Type");
                                                        default:
                                                            return responseInfoMsg("Guardian Type is not valid. Unable to store record.");
                                                    }

                                                }

                                                // if guardian uuid is not in the request
                                                else {
                                                    if (guardianTypeEntity.getSlug().equals("father")) {
                                                        return responseInfoMsg("Enter the Father Record First.");
                                                    } else if (guardianTypeEntity.getSlug().equals("mother")) {
                                                        return responseInfoMsg("Enter the Mother Record First.");
                                                    } else {
                                                        return teacherGuardianRepository.save(teacherGuardianEntity)
                                                                .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                    }
                                                }
                                            }).switchIfEmpty(responseInfoMsg("Guardian Type does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Guardian Type does not exist. Please contact developer."))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                        .flatMap(entity -> {

                            UUID guardianUUID = null;
                            if ((value.containsKey("guardianUUID") && (value.getFirst("guardianUUID") != ""))) {
                                guardianUUID = UUID.fromString(value.getFirst("guardianUUID").trim());
                            }

                            TeacherGuardianEntity updatedEntity = TeacherGuardianEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                                    .guardianTypeUUID(UUID.fromString(value.getFirst("guardianTypeUUID")))
                                    .guardianUUID(guardianUUID)
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

                            // checks if teacher uuid exists
                            return teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                    // checks if guardian type uuid exists
                                    .flatMap(teacherEntity -> teacherGuardianRepository.findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(teacherEntity.getUuid(), teacherGuardianUUID)
                                            .flatMap(guardianAlreadyExists -> responseInfoMsg("Teacher Guardian Record Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGuardianTypeUUID())
                                                    .flatMap(guardianTypeEntity -> {
                                                        // if guardian uuid is specified in the request
                                                        if (updatedEntity.getGuardianUUID() != null) {

                                                            // if teacher father is guardian
                                                            switch (guardianTypeEntity.getSlug()) {
                                                                case "father":
                                                                    return teacherFatherRepository.findByUuidAndTeacherUUIDAndDeletedAtIsNull(updatedEntity.getGuardianUUID(), updatedEntity.getTeacherUUID())
                                                                            .flatMap(teacherFatherEntity -> teacherGuardianRepository.save(entity)
                                                                                    .then(teacherGuardianRepository.save(updatedEntity))
                                                                                    .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                            .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));


                                                                // if teacher mother is guardian
                                                                case "mother":
                                                                    return teacherMotherRepository.findByUuidAndTeacherUUIDAndDeletedAtIsNull(updatedEntity.getGuardianUUID(), updatedEntity.getTeacherUUID())
                                                                            .flatMap(teacherMotherEntity -> teacherGuardianRepository.save(entity)
                                                                                    .then(teacherGuardianRepository.save(updatedEntity))
                                                                                    .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                            .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));

                                                                // if guardian type is other but guardian uuid is given
                                                                case "other":
                                                                    return responseInfoMsg("Guardian is not valid for given Guardian Type");
                                                                default:
                                                                    return responseInfoMsg("Guardian Type is not valid. Unable to store record.");
                                                            }

                                                        }

                                                        // if guardian uuid is not in the request
                                                        else {
                                                            if (guardianTypeEntity.getSlug().equals("father")) {
                                                                return responseInfoMsg("Enter the Father Record First.");
                                                            } else if (guardianTypeEntity.getSlug().equals("mother")) {
                                                                return responseInfoMsg("Enter the Mother Record First.");
                                                            } else {
                                                                return teacherGuardianRepository.save(entity)
                                                                        .then(teacherGuardianRepository.save(updatedEntity))
                                                                        .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                            }
                                                        }
                                                    }).switchIfEmpty(responseInfoMsg("Guardian Type does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Guardian Type does not exist. Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                            .flatMap(teacherGuardianEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherGuardianEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherGuardianEntity updatedTeacherGuardianEntity = TeacherGuardianEntity.builder()
                                        .uuid(teacherGuardianEntityDB.getUuid())
                                        .teacherUUID(teacherGuardianEntityDB.getTeacherUUID())
                                        .guardianTypeUUID(teacherGuardianEntityDB.getGuardianTypeUUID())
                                        .guardianUUID(teacherGuardianEntityDB.getGuardianUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherGuardianEntityDB.getCreatedAt())
                                        .createdBy(teacherGuardianEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherGuardianEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherGuardianEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherGuardianEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherGuardianEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherGuardianEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherGuardianEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherGuardianEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherGuardianEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherGuardianEntityDB.setReqDeletedIP(reqIp);
                                teacherGuardianEntityDB.setReqDeletedPort(reqPort);
                                teacherGuardianEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherGuardianEntityDB.setReqDeletedOS(reqOs);
                                teacherGuardianEntityDB.setReqDeletedDevice(reqDevice);
                                teacherGuardianEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherGuardianRepository.save(teacherGuardianEntityDB)
                                        .then(teacherGuardianRepository.save(updatedTeacherGuardianEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-guardians_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                //Checks if Teacher Guardian Reference exists in Teacher Guardian Profiles
                .flatMap(teacherGuardianEntity -> teacherGuardianProfileRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                        .flatMap(teacherGuardianProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        // Checks if Teacher Guardian Reference exists in Teacher Guardian Documents
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianDocumentRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Academic History
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianAcademicHistoryRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianAcademicHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianFinancialHistoryRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Job History
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianJobHistoryRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianAddressRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Family Doctor
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianFamilyDoctorRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Hobbies Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianHobbyPvtRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Ailments Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianAilmentPvtRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Nationalities Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianNationalityPvtRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Guardian Reference exists in Teacher Guardian Language Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianLanguagePvtRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .flatMap(teacherGuardianNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
                            teacherGuardianEntity.setDeletedBy(UUID.fromString(userId));
                            teacherGuardianEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherGuardianEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherGuardianEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherGuardianEntity.setReqDeletedIP(reqIp);
                            teacherGuardianEntity.setReqDeletedPort(reqPort);
                            teacherGuardianEntity.setReqDeletedBrowser(reqBrowser);
                            teacherGuardianEntity.setReqDeletedOS(reqOs);
                            teacherGuardianEntity.setReqDeletedDevice(reqDevice);
                            teacherGuardianEntity.setReqDeletedReferer(reqReferer);

                            return teacherGuardianRepository.save(teacherGuardianEntity)
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
