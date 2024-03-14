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
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSiblingRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherSiblingHandler")
@Component
public class TeacherSiblingHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @Autowired
    SlaveTeacherSiblingRepository slaveTeacherSiblingRepository;

    @Autowired
    TeacherSiblingProfileRepository teacherSiblingProfileRepository;

    @Autowired
    TeacherSiblingDocumentRepository teacherSiblingDocumentRepository;

    @Autowired
    TeacherSiblingAcademicHistoryRepository teacherSiblingAcademicHistoryRepository;

    @Autowired
    TeacherSiblingJobHistoryRepository teacherSiblingJobHistoryRepository;

    @Autowired
    TeacherSiblingFinancialHistoryRepository teacherSiblingFinancialHistoryRepository;

    @Autowired
    TeacherSiblingAddressRepository teacherSiblingAddressRepository;

    @Autowired
    TeacherSiblingFamilyDoctorRepository teacherSiblingFamilyDoctorRepository;

    @Autowired
    TeacherSiblingAilmentPvtRepository teacherSiblingAilmentPvtRepository;

    @Autowired
    TeacherSiblingHobbyPvtRepository teacherSiblingHobbyPvtRepository;

    @Autowired
    TeacherSiblingNationalityPvtRepository teacherSiblingNationalityPvtRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;


    @AuthHasPermission(value = "academic_api_v1_teacher-siblings_index")
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

            Flux<SlaveTeacherSiblingEntity> slaveTeacherSiblingEntityFlux = slaveTeacherSiblingRepository
                    .findAllByTeacherUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(teacherUUID), Boolean.valueOf(status));

            return slaveTeacherSiblingEntityFlux
                    .collectList()
                    .flatMap(teacherSiblingEntity -> slaveTeacherSiblingRepository.countByTeacherUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(teacherUUID), Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherSiblingEntity> slaveTeacherSiblingEntityFlux = slaveTeacherSiblingRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));

            return slaveTeacherSiblingEntityFlux
                    .collectList()
                    .flatMap(teacherSiblingEntity -> slaveTeacherSiblingRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!teacherUUID.isEmpty()) {

            Flux<SlaveTeacherSiblingEntity> slaveTeacherSiblingEntityFlux = slaveTeacherSiblingRepository
                    .findAllByTeacherUUIDAndDeletedAtIsNull(pageable, UUID.fromString(teacherUUID));

            return slaveTeacherSiblingEntityFlux
                    .collectList()
                    .flatMap(teacherSiblingEntity -> slaveTeacherSiblingRepository.countByTeacherUUIDAndDeletedAtIsNull(UUID.fromString(teacherUUID))
                            .flatMap(count ->
                            {
                                if (teacherSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherSiblingEntity> slaveTeacherSiblingEntityFlux = slaveTeacherSiblingRepository
                    .findAllByDeletedAtIsNull(pageable);

            return slaveTeacherSiblingEntityFlux
                    .collectList()
                    .flatMap(teacherSiblingEntity -> slaveTeacherSiblingRepository.countByDeletedAtIsNull()
                            .flatMap(count ->
                            {
                                if (teacherSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-siblings_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                .flatMap(teacherSiblingEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherSiblingEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-siblings_store")
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

                    TeacherSiblingEntity teacherSiblingEntity = TeacherSiblingEntity.builder()
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .uuid(UUID.randomUUID())
                            .studentUUID(studentUUID)
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

                    if (teacherSiblingEntity.getStudentUUID() != null) {

                        return teacherSiblingRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherSiblingEntity.getTeacherUUID(), teacherSiblingEntity.getStudentUUID())
                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Sibling Record Already Exists for Given Student"))
                                // checks if student uuid exists
                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherSiblingEntity.getStudentUUID())
                                        // checks if teacher uuid exists
                                        .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherSiblingEntity.getTeacherUUID())
                                                .flatMap(teacherEntity -> teacherSiblingRepository.save(teacherSiblingEntity)
                                                        .flatMap(teacherSiblingEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSiblingEntityDB))
                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                        ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                ));
                    } else {
                        // checks if teacher uuid exists
                        return teacherRepository.findByUuidAndDeletedAtIsNull(teacherSiblingEntity.getTeacherUUID())
                                .flatMap(teacherEntity -> teacherSiblingRepository.save(teacherSiblingEntity)
                                        .flatMap(teacherSiblingEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherSiblingEntityDB))
                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."));
                    }


                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-siblings_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                        .flatMap(entity -> {

                            UUID studentUUID = null;
                            if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
                                studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
                            }

                            TeacherSiblingEntity updatedEntity = TeacherSiblingEntity.builder()
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

                                return teacherSiblingRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Sibling Record Already Exists for Given Student"))
                                        // checks if student uuid exists
                                        .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                                // checks if teacher uuid exists
                                                .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                                        .flatMap(teacherEntity -> teacherSiblingRepository.save(entity)
                                                                .then(teacherSiblingRepository.save(updatedEntity))
                                                                .flatMap(teacherSiblingEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherSiblingEntityDB))
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
                                        .flatMap(teacherEntity -> teacherSiblingRepository.save(entity)
                                                .then(teacherSiblingRepository.save(updatedEntity))
                                                .flatMap(teacherSiblingEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherSiblingEntityDB))
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

    @AuthHasPermission(value = "academic_api_v1_teacher-siblings_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                            .flatMap(teacherSiblingEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherSiblingEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherSiblingEntity updatedTeacherSiblingEntity = TeacherSiblingEntity.builder()
                                        .uuid(teacherSiblingEntityDB.getUuid())
                                        .teacherUUID(teacherSiblingEntityDB.getTeacherUUID())
                                        .studentUUID(teacherSiblingEntityDB.getStudentUUID())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherSiblingEntityDB.getCreatedAt())
                                        .createdBy(teacherSiblingEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherSiblingEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherSiblingEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherSiblingEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherSiblingEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherSiblingEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherSiblingEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherSiblingEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherSiblingEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherSiblingEntityDB.setReqDeletedIP(reqIp);
                                teacherSiblingEntityDB.setReqDeletedPort(reqPort);
                                teacherSiblingEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherSiblingEntityDB.setReqDeletedOS(reqOs);
                                teacherSiblingEntityDB.setReqDeletedDevice(reqDevice);
                                teacherSiblingEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherSiblingRepository.save(teacherSiblingEntityDB)
                                        .then(teacherSiblingRepository.save(updatedTeacherSiblingEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-siblings_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherSiblingUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                //Checks if Teacher Sibling Reference exists in Teacher Sibling Profiles
                .flatMap(teacherSiblingEntity -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                        .flatMap(teacherSiblingProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Documents
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingDocumentRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Academic History
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingAcademicHistoryRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingAcademicHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingFinancialHistoryRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Job History
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingJobHistoryRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingAddressRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Family Doctor
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingFamilyDoctorRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Hobbies Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingHobbyPvtRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Ailments Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingAilmentPvtRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Sibling Nationalities Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingNationalityPvtRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherSiblingNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Sibling Reference exists in Teacher Guardian
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findFirstByGuardianUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .flatMap(teacherGuardianEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
                            teacherSiblingEntity.setDeletedBy(UUID.fromString(userId));
                            teacherSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            teacherSiblingEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            teacherSiblingEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            teacherSiblingEntity.setReqDeletedIP(reqIp);
                            teacherSiblingEntity.setReqDeletedPort(reqPort);
                            teacherSiblingEntity.setReqDeletedBrowser(reqBrowser);
                            teacherSiblingEntity.setReqDeletedOS(reqOs);
                            teacherSiblingEntity.setReqDeletedDevice(reqDevice);
                            teacherSiblingEntity.setReqDeletedReferer(reqReferer);

                            return teacherSiblingRepository.save(teacherSiblingEntity)
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
