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
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentSiblingHandler")
@Component
public class

StudentSiblingHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    SlaveStudentSiblingRepository slaveStudentSiblingRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentSiblingProfileRepository studentSiblingProfileRepository;

    @Autowired
    StudentSiblingDocumentRepository studentSiblingDocumentRepository;

    @Autowired
    StudentSiblingFinancialHistoryRepository studentSiblingFinancialHistoryRepository;

    @Autowired
    StudentSiblingJobHistoryRepository studentSiblingJobHistoryRepository;

    @Autowired
    StudentSiblingFamilyDoctorRepository studentSiblingFamilyDoctorRepository;

    @Autowired
    StudentSiblingHobbyPvtRepository studentSiblingHobbyPvtRepository;

    @Autowired
    StudentSiblingNationalityPvtRepository studentSiblingNationalityPvtRepository;

    @Autowired
    StudentSiblingAilmentPvtRepository studentSiblingAilmentPvtRepository;

    @Autowired
    StudentSiblingAddressRepository studentSiblingAddressRepository;

    @Autowired
    StudentSiblingAcademicHistoryRepository studentSiblingAcademicHistoryRepository;

    @Autowired
    StudentSiblingLanguagePvtRepository studentSiblingLanguagePvtRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_student-siblings_index")
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

        //Optional Query Parameter of Student UUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if both student uuid and status are present in optional query params
        if (!status.isEmpty() && !studentUUID.isEmpty()) {
            Flux<SlaveStudentSiblingEntity> slaveStudentSiblingFlux = slaveStudentSiblingRepository
                    .findAllByStudentUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(studentUUID), Boolean.valueOf(status));
            return slaveStudentSiblingFlux
                    .collectList()
                    .flatMap(studentSiblingEntity -> slaveStudentSiblingRepository
                            .countByStudentUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(studentUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only student uuid is present in optional query params
        else if (!studentUUID.isEmpty()) {
            Flux<SlaveStudentSiblingEntity> slaveStudentSiblingFlux = slaveStudentSiblingRepository
                    .findAllByStudentUUIDAndDeletedAtIsNull(pageable, UUID.fromString(studentUUID));
            return slaveStudentSiblingFlux
                    .collectList()
                    .flatMap(studentSiblingEntity -> slaveStudentSiblingRepository
                            .countByStudentUUIDAndDeletedAtIsNull(UUID.fromString(studentUUID))
                            .flatMap(count -> {
                                if (studentSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only status is present in optional query params
        else if (!status.isEmpty()) {
            Flux<SlaveStudentSiblingEntity> slaveStudentSiblingFlux = slaveStudentSiblingRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));
            return slaveStudentSiblingFlux
                    .collectList()
                    .flatMap(studentSiblingEntity -> slaveStudentSiblingRepository
                            .countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional query params are present
        else {
            Flux<SlaveStudentSiblingEntity> slaveStudentSiblingFlux = slaveStudentSiblingRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveStudentSiblingFlux
                    .collectList()
                    .flatMap(studentSiblingEntity -> slaveStudentSiblingRepository.countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (studentSiblingEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-siblings_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                .flatMap(studentSiblingEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-siblings_store")
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
                    UUID studentSiblingUUID = null;
                    if ((value.containsKey("studentSiblingUUID") && (value.getFirst("studentSiblingUUID") != ""))) {
                        studentSiblingUUID = UUID.fromString(value.getFirst("studentSiblingUUID").trim());
                    }

                    StudentSiblingEntity studentSiblingEntity = StudentSiblingEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID")))
                            .studentSiblingUUID(studentSiblingUUID)
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


                    // if student uuid is given in request
                    if (studentSiblingEntity.getStudentSiblingUUID() != null) {

                        // if same student uuid is given as both student and student sibling's student uuid
                        if (studentSiblingEntity.getStudentSiblingUUID().equals(studentSiblingEntity.getStudentUUID())) {
                            return responseInfoMsg("The student sibling cannot be the same as the given student");
                        } else {
                            // checks if student uuid exists
                            return studentSiblingRepository.findFirstByStudentUUIDAndStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getStudentUUID(), studentSiblingEntity.getStudentSiblingUUID())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Student Sibling Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(studentSiblingEntity.getStudentUUID())
                                            // checks if student's Sibling is student
                                            .flatMap(studentEntity -> studentRepository.findByUuidAndDeletedAtIsNull(studentSiblingEntity.getStudentSiblingUUID())
                                                    .flatMap(studentSibling -> studentSiblingRepository.save(studentSiblingEntity)
                                                            .flatMap(studentSiblingEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSiblingEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                    ));
                        }
                    }

                    // if student uuid is not in request
                    else {
                        return studentRepository.findByUuidAndDeletedAtIsNull(studentSiblingEntity.getStudentUUID())
                                .flatMap(studentEntity -> studentSiblingRepository.save(studentSiblingEntity)
                                        .flatMap(studentSiblingEntityDB -> responseSuccessMsg("Record Stored Successfully", studentSiblingEntityDB))
                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                    }
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-siblings_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                        .flatMap(previousStudentSiblingEntity -> {

                            UUID studentUUID = null;
                            if ((value.containsKey("studentSiblingUUID") && (value.getFirst("studentSiblingUUID") != ""))) {
                                studentUUID = UUID.fromString(value.getFirst("studentSiblingUUID").trim());
                            }
                            StudentSiblingEntity updatedEntity = StudentSiblingEntity.builder()
                                    .uuid(previousStudentSiblingEntity.getUuid())
                                    .studentSiblingUUID(studentUUID)
                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousStudentSiblingEntity.getCreatedAt())
                                    .createdBy(previousStudentSiblingEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousStudentSiblingEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStudentSiblingEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStudentSiblingEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStudentSiblingEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStudentSiblingEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStudentSiblingEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousStudentSiblingEntity.setDeletedBy(UUID.fromString(userId));
                            previousStudentSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStudentSiblingEntity.setReqDeletedIP(reqIp);
                            previousStudentSiblingEntity.setReqDeletedPort(reqPort);
                            previousStudentSiblingEntity.setReqDeletedBrowser(reqBrowser);
                            previousStudentSiblingEntity.setReqDeletedOS(reqOs);
                            previousStudentSiblingEntity.setReqDeletedDevice(reqDevice);
                            previousStudentSiblingEntity.setReqDeletedReferer(reqReferer);

                            // if student uuid is given in request
                            if (updatedEntity.getStudentSiblingUUID() != null) {

                                // if same student uuid is given as both student and student sibling's student uuid
                                if (updatedEntity.getStudentSiblingUUID().equals(updatedEntity.getStudentUUID())) {
                                    return responseInfoMsg("The student sibling cannot be the same as the given student");
                                } else {
                                    // checks if student uuid exists
                                    return studentSiblingRepository.findFirstByStudentUUIDAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getStudentSiblingUUID(), updatedEntity.getUuid())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Sibling Record Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                                    // checks if student's Sibling is student
                                                    .flatMap(studentEntity -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentSiblingUUID())
                                                            .flatMap(studentEntity1 -> studentSiblingRepository.save(previousStudentSiblingEntity)
                                                                    .then(studentSiblingRepository.save(updatedEntity))
                                                                    .flatMap(studentSiblingEntityDB -> responseSuccessMsg("Record Updated Successfully", studentSiblingEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                            ));
                                }
                            }

                            // if student sibling uuid is not in request
                            else {
                                return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                        .flatMap(studentEntity -> studentSiblingRepository.save(previousStudentSiblingEntity)
                                                .then(studentSiblingRepository.save(updatedEntity))
                                                .flatMap(studentSiblingEntityDB -> responseSuccessMsg("Record Updated Successfully", studentSiblingEntityDB))
                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                        ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                            }
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-siblings_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSiblingEntity studentSiblingEntity = StudentSiblingEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .studentSiblingUUID(previousEntity.getStudentSiblingUUID())
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

                                return studentSiblingRepository.save(previousEntity)
                                        .then(studentSiblingRepository.save(studentSiblingEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-siblings_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                //Checks if Student Sibling Reference exists in Student Sibling Profiles
                .flatMap(studentSiblingEntity -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                        .flatMap(studentSiblingProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Student Sibling Reference exists in Student Sibling Documents
                        .switchIfEmpty(Mono.defer(() -> studentSiblingDocumentRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Financial History
                        .switchIfEmpty(Mono.defer(() -> studentSiblingFinancialHistoryRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Job History
                        .switchIfEmpty(Mono.defer(() -> studentSiblingJobHistoryRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Family Doctor
                        .switchIfEmpty(Mono.defer(() -> studentSiblingFamilyDoctorRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSiblingHobbyPvtRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSiblingNationalityPvtRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSiblingAilmentPvtRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Address
                        .switchIfEmpty(Mono.defer(() -> studentSiblingAddressRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Academic History
                        .switchIfEmpty(Mono.defer(() -> studentSiblingAcademicHistoryRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Sibling Reference exists in Student Sibling Language Pvt
                        .switchIfEmpty(Mono.defer(() -> studentSiblingLanguagePvtRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .flatMap(studentSiblingDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            studentSiblingEntity.setDeletedBy(UUID.fromString(userId));
                            studentSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentSiblingEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentSiblingEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentSiblingEntity.setReqDeletedIP(reqIp);
                            studentSiblingEntity.setReqDeletedPort(reqPort);
                            studentSiblingEntity.setReqDeletedBrowser(reqBrowser);
                            studentSiblingEntity.setReqDeletedOS(reqOs);
                            studentSiblingEntity.setReqDeletedDevice(reqDevice);
                            studentSiblingEntity.setReqDeletedReferer(reqReferer);

                            return studentSiblingRepository.save(studentSiblingEntity)
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
