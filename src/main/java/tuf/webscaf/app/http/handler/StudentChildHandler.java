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
import tuf.webscaf.app.dbContext.master.entity.StudentChildEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentChildRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentChildHandler")
@Component
public class StudentChildHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentChildRepository studentChildRepository;

    @Autowired
    SlaveStudentChildRepository slaveStudentChildRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentChildProfileRepository studentChildProfileRepository;

    @Autowired
    StudentChildDocumentRepository studentChildDocumentRepository;

    @Autowired
    StudentChildFinancialHistoryRepository studentChildFinancialHistoryRepository;

    @Autowired
    StudentChildJobHistoryRepository studentChildJobHistoryRepository;

    @Autowired
    StudentChildFamilyDoctorRepository studentChildFamilyDoctorRepository;

    @Autowired
    StudentChildHobbyPvtRepository studentChildHobbyPvtRepository;

    @Autowired
    StudentChildNationalityPvtRepository studentChildNationalityPvtRepository;

    @Autowired
    StudentChildAilmentPvtRepository studentChildAilmentPvtRepository;

    @Autowired
    StudentChildAddressRepository studentChildAddressRepository;

    @Autowired
    StudentChildAcademicHistoryRepository studentChildAcademicHistoryRepository;

    @Autowired
    StudentChildLanguagePvtRepository studentChildLanguagePvtRepository;

    @AuthHasPermission(value = "academic_api_v1_student-childs_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student UUID
        String studentUUID = serverRequest.queryParam("studentUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if both student uuid and status are present in optional query params
        if (!status.isEmpty() && !studentUUID.isEmpty()) {
            Flux<SlaveStudentChildEntity> slaveStudentChildFlux = slaveStudentChildRepository
                    .findAllByStudentUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(studentUUID), Boolean.valueOf(status));
            return slaveStudentChildFlux
                    .collectList()
                    .flatMap(studentChildEntity -> slaveStudentChildRepository
                            .countByStudentUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(studentUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only student uuid is present in optional query params
        else if (!studentUUID.isEmpty()) {
            Flux<SlaveStudentChildEntity> slaveStudentChildFlux = slaveStudentChildRepository
                    .findAllByStudentUUIDAndDeletedAtIsNull(pageable, UUID.fromString(studentUUID));
            return slaveStudentChildFlux
                    .collectList()
                    .flatMap(studentChildEntity -> slaveStudentChildRepository
                            .countByStudentUUIDAndDeletedAtIsNull(UUID.fromString(studentUUID))
                            .flatMap(count -> {
                                if (studentChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if only status is present in optional query params
        else if (!status.isEmpty()) {
            Flux<SlaveStudentChildEntity> slaveStudentChildFlux = slaveStudentChildRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));
            return slaveStudentChildFlux
                    .collectList()
                    .flatMap(studentChildEntity -> slaveStudentChildRepository
                            .countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if no optional query params are present
        else {
            Flux<SlaveStudentChildEntity> slaveStudentChildFlux = slaveStudentChildRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveStudentChildFlux
                    .collectList()
                    .flatMap(studentChildEntity -> slaveStudentChildRepository.countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (studentChildEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-childs_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                .flatMap(studentChildEntity -> responseSuccessMsg("Record Fetched Successfully", studentChildEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-childs_store")
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

                    UUID studentChildUUID = null;
                    if ((value.containsKey("studentChildUUID") && (value.getFirst("studentChildUUID") != ""))) {
                        studentChildUUID = UUID.fromString(value.getFirst("studentChildUUID").trim());
                    }

                    StudentChildEntity entity = StudentChildEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID")))
                            .studentChildUUID(studentChildUUID)
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
                    if (entity.getStudentChildUUID() != null) {

                        // if same student uuid is given as both student and student child's student uuid
                        if (entity.getStudentChildUUID().equals(entity.getStudentUUID())) {
                            return responseInfoMsg("The student child cannot be the same as the given student");
                        } else {
                            // checks if student uuid exists
                            return studentChildRepository.findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNull(entity.getStudentUUID(), entity.getStudentChildUUID())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Student Child Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> studentChildRepository.findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNull(entity.getStudentChildUUID(), entity.getStudentUUID())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("The given Student Child is already Parent of given Student"))))
                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
                                            // checks if student's Child is student
                                            .flatMap(studentEntity -> studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentChildUUID())
                                                    .flatMap(studentEntity1 -> studentChildRepository.save(entity)
                                                            .flatMap(studentChildEntityDB -> responseSuccessMsg("Record Stored Successfully", studentChildEntityDB))
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
                        return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
                                .flatMap(studentEntity -> studentChildRepository.save(entity)
                                        .flatMap(studentChildEntityDB -> responseSuccessMsg("Record Stored Successfully", studentChildEntityDB))
                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                    }
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-childs_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                        .flatMap(previousStudentChildEntity -> {

                            UUID studentUUID = null;
                            if ((value.containsKey("studentChildUUID") && (value.getFirst("studentChildUUID") != ""))) {
                                studentUUID = UUID.fromString(value.getFirst("studentChildUUID").trim());
                            }

                            StudentChildEntity updatedEntity = StudentChildEntity.builder()
                                    .uuid(previousStudentChildEntity.getUuid())
                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                                    .studentChildUUID(studentUUID)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousStudentChildEntity.getCreatedAt())
                                    .createdBy(previousStudentChildEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousStudentChildEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStudentChildEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStudentChildEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStudentChildEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStudentChildEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStudentChildEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousStudentChildEntity.setDeletedBy(UUID.fromString(userId));
                            previousStudentChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStudentChildEntity.setReqDeletedIP(reqIp);
                            previousStudentChildEntity.setReqDeletedPort(reqPort);
                            previousStudentChildEntity.setReqDeletedBrowser(reqBrowser);
                            previousStudentChildEntity.setReqDeletedOS(reqOs);
                            previousStudentChildEntity.setReqDeletedDevice(reqDevice);
                            previousStudentChildEntity.setReqDeletedReferer(reqReferer);

                            // if student uuid is given in request
                            if (updatedEntity.getStudentChildUUID() != null) {
                                // if same student uuid is given as both student and student child's student uuid
                                if (updatedEntity.getStudentChildUUID().equals(updatedEntity.getStudentUUID())) {
                                    return responseInfoMsg("The student child cannot be the same as the given student");
                                } else {
                                    // checks if student uuid exists
                                    return studentChildRepository.findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getStudentChildUUID(), updatedEntity.getUuid())
                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Child Record Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> studentChildRepository.findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentChildUUID(), updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                                    .flatMap(recordAlreadyExists -> responseInfoMsg("The given Student Child is already Parent of given Student"))))
                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                                    // checks if student's Child is student
                                                    .flatMap(studentEntity -> studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentChildUUID())
                                                            .flatMap(studentChild -> studentChildRepository.save(previousStudentChildEntity)
                                                                    .then(studentChildRepository.save(updatedEntity))
                                                                    .flatMap(studentChildEntityDB -> responseSuccessMsg("Record Updated Successfully", studentChildEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                            ));
                                }
                            }

                            // if student uuid is not in request
                            else {
                                return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                        .flatMap(studentEntity -> studentChildRepository.save(previousStudentChildEntity)
                                                .then(studentChildRepository.save(updatedEntity))
                                                .flatMap(studentChildEntityDB -> responseSuccessMsg("Record Updated Successfully", studentChildEntityDB))
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

    @AuthHasPermission(value = "academic_api_v1_student-childs_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentChildEntity studentChildEntity = StudentChildEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .studentChildUUID(previousEntity.getStudentChildUUID())
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

                                return studentChildRepository.save(previousEntity)
                                        .then(studentChildRepository.save(studentChildEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-childs_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                //Checks if Student Child Reference exists in Student Child Profiles
                .flatMap(studentChildEntity -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                        .flatMap(studentChildProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Student Child Reference exists in Student Child Documents
                        .switchIfEmpty(Mono.defer(() -> studentChildDocumentRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Financial History
                        .switchIfEmpty(Mono.defer(() -> studentChildFinancialHistoryRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Job History
                        .switchIfEmpty(Mono.defer(() -> studentChildJobHistoryRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Family Doctor
                        .switchIfEmpty(Mono.defer(() -> studentChildFamilyDoctorRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> studentChildHobbyPvtRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> studentChildNationalityPvtRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> studentChildAilmentPvtRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Address
                        .switchIfEmpty(Mono.defer(() -> studentChildAddressRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Academic History
                        .switchIfEmpty(Mono.defer(() -> studentChildAcademicHistoryRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Child Reference exists in Student Child Language Pvt
                        .switchIfEmpty(Mono.defer(() -> studentChildLanguagePvtRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .flatMap(studentChildDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            studentChildEntity.setDeletedBy(UUID.fromString(userId));
                            studentChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentChildEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentChildEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentChildEntity.setReqDeletedIP(reqIp);
                            studentChildEntity.setReqDeletedPort(reqPort);
                            studentChildEntity.setReqDeletedBrowser(reqBrowser);
                            studentChildEntity.setReqDeletedOS(reqOs);
                            studentChildEntity.setReqDeletedDevice(reqDevice);
                            studentChildEntity.setReqDeletedReferer(reqReferer);

                            return studentChildRepository.save(studentChildEntity)
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
