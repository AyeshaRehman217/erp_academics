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
import tuf.webscaf.app.dbContext.master.entity.TeacherChildEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherChildRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherChildHandler")
@Component
public class TeacherChildHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherChildRepository teacherChildRepository;

    @Autowired
    SlaveTeacherChildRepository slaveTeacherChildRepository;

    @Autowired
    TeacherChildProfileRepository teacherChildProfileRepository;

    @Autowired
    TeacherChildDocumentRepository teacherChildDocumentRepository;

    @Autowired
    TeacherChildAcademicHistoryRepository teacherChildAcademicHistoryRepository;

    @Autowired
    TeacherChildJobHistoryRepository teacherChildJobHistoryRepository;

    @Autowired
    TeacherChildFinancialHistoryRepository teacherChildFinancialHistoryRepository;

    @Autowired
    TeacherChildAddressRepository teacherChildAddressRepository;

    @Autowired
    TeacherChildFamilyDoctorRepository teacherChildFamilyDoctorRepository;

    @Autowired
    TeacherChildAilmentPvtRepository teacherChildAilmentPvtRepository;

    @Autowired
    TeacherChildHobbyPvtRepository teacherChildHobbyPvtRepository;

    @Autowired
    TeacherChildNationalityPvtRepository teacherChildNationalityPvtRepository;

    @Autowired
    TeacherChildLanguagePvtRepository teacherChildLanguagePvtRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentRepository studentRepository;


    @AuthHasPermission(value = "academic_api_v1_teacher-childs_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher UUID
        String teacherUUID = serverRequest.queryParam("teacherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !teacherUUID.isEmpty()) {
            Flux<SlaveTeacherChildEntity> slaveTeacherChildEntityFlux = slaveTeacherChildRepository
                    .findAllByTeacherUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(teacherUUID), Boolean.valueOf(status));

            return slaveTeacherChildEntityFlux
                    .collectList()
                    .flatMap(teacherChildEntity -> slaveTeacherChildRepository.countByTeacherUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(teacherUUID), Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherChildEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveTeacherChildEntity> slaveTeacherChildEntityFlux = slaveTeacherChildRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveTeacherChildEntityFlux
                    .collectList()
                    .flatMap(teacherChildEntity -> slaveTeacherChildRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherChildEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!teacherUUID.isEmpty()) {
            Flux<SlaveTeacherChildEntity> slaveTeacherChildEntityFlux = slaveTeacherChildRepository
                    .findAllByTeacherUUIDAndDeletedAtIsNull(pageable, UUID.fromString(teacherUUID));

            return slaveTeacherChildEntityFlux
                    .collectList()
                    .flatMap(teacherChildEntity -> slaveTeacherChildRepository.countByTeacherUUIDAndDeletedAtIsNull(UUID.fromString(teacherUUID))
                            .flatMap(count ->
                            {
                                if (teacherChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherChildEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherChildEntity> slaveTeacherChildEntityFlux = slaveTeacherChildRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveTeacherChildEntityFlux
                    .collectList()
                    .flatMap(teacherChildEntity -> slaveTeacherChildRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (teacherChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherChildEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-childs_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherChildUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                .flatMap(teacherChildEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherChildEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-childs_store")
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

                    UUID studentUUID = null;
                    if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
                        studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
                    }

                    TeacherChildEntity teacherChildEntity = TeacherChildEntity.builder()
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                            .studentUUID(studentUUID)
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

                    if (teacherChildEntity.getStudentUUID() != null) {
                        return teacherChildRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherChildEntity.getTeacherUUID(), teacherChildEntity.getStudentUUID())
                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Child Record Already Exists for Given Student"))
                                // checks if student uuid exists
                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherChildEntity.getStudentUUID())
                                        // checks if teacher uuid exists
                                        .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherChildEntity.getTeacherUUID())
                                                .flatMap(teacherEntity -> teacherChildRepository.save(teacherChildEntity)
                                                        .flatMap(teacherChildEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherChildEntityDB))
                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                        ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                ));
                    } else {
                        // checks if teacher uuid exists
                        return teacherRepository.findByUuidAndDeletedAtIsNull(teacherChildEntity.getTeacherUUID())
                                .flatMap(teacherEntity -> teacherChildRepository.save(teacherChildEntity)
                                        .flatMap(teacherChildEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherChildEntityDB))
                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                    }


                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-childs_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherChildUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                        .flatMap(entity -> {

                            UUID studentUUID = null;
                            if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
                                studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
                            }

                            TeacherChildEntity updatedEntity = TeacherChildEntity.builder()
                                    .uuid(entity.getUuid())
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID")))
                                    .studentUUID(studentUUID)
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

                            if (updatedEntity.getStudentUUID() != null) {

                                return teacherChildRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Child Record Already Exists for Given Student"))
                                        // checks if student uuid exists
                                        .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                                // checks if teacher uuid exists
                                                .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                                        .flatMap(teacherEntity -> teacherChildRepository.save(entity)
                                                                .then(teacherChildRepository.save(updatedEntity))
                                                                .flatMap(teacherChildEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherChildEntityDB))
                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                        ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                        .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                                ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                        ));
                            } else {
                                // checks if teacher uuid exists
                                return teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                        .flatMap(teacherEntity -> teacherChildRepository.save(entity)
                                                .then(teacherChildRepository.save(updatedEntity))
                                                .flatMap(teacherChildEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherChildEntityDB))
                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                        ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                        .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                            }

                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-childs_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherChildUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                            .flatMap(teacherChildEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherChildEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherChildEntity updatedTeacherChildEntity = TeacherChildEntity.builder()
                                        .uuid(teacherChildEntityDB.getUuid())
                                        .teacherUUID(teacherChildEntityDB.getTeacherUUID())
                                        .studentUUID(teacherChildEntityDB.getStudentUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherChildEntityDB.getCreatedAt())
                                        .createdBy(teacherChildEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherChildEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherChildEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherChildEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherChildEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherChildEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherChildEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherChildEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherChildEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherChildEntityDB.setReqDeletedIP(reqIp);
                                teacherChildEntityDB.setReqDeletedPort(reqPort);
                                teacherChildEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherChildEntityDB.setReqDeletedOS(reqOs);
                                teacherChildEntityDB.setReqDeletedDevice(reqDevice);
                                teacherChildEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherChildRepository.save(teacherChildEntityDB)
                                        .then(teacherChildRepository.save(updatedTeacherChildEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-childs_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherChildUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                //Checks if Teacher Child Reference exists in Teacher Child Profiles
                .flatMap(teacherChildEntity -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                        .flatMap(teacherChildProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Teacher Child Reference exists in Teacher Child Documents
                        .switchIfEmpty(Mono.defer(() -> teacherChildDocumentRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Academic History
                        .switchIfEmpty(Mono.defer(() -> teacherChildAcademicHistoryRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildAcademicHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherChildFinancialHistoryRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Job History
                        .switchIfEmpty(Mono.defer(() -> teacherChildJobHistoryRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherChildAddressRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Family Doctor
                        .switchIfEmpty(Mono.defer(() -> teacherChildFamilyDoctorRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Hobbies Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherChildHobbyPvtRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Ailments Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherChildAilmentPvtRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Nationalities Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherChildNationalityPvtRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Child Reference exists in Teacher Child Language Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherChildLanguagePvtRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .flatMap(teacherChildNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
                            teacherChildEntity.setDeletedBy(UUID.fromString(userId));
                            teacherChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherChildEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherChildEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherChildEntity.setReqDeletedIP(reqIp);
                            teacherChildEntity.setReqDeletedPort(reqPort);
                            teacherChildEntity.setReqDeletedBrowser(reqBrowser);
                            teacherChildEntity.setReqDeletedOS(reqOs);
                            teacherChildEntity.setReqDeletedDevice(reqDevice);
                            teacherChildEntity.setReqDeletedReferer(reqReferer);

                            return teacherChildRepository.save(teacherChildEntity)
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
