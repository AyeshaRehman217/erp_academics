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
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

;

@Tag(name = "studentGuardianHandler")
@Component
public class StudentGuardianHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    SlaveStudentGuardianRepository slaveStudentGuardianRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    GuardianTypeRepository guardianTypeRepository;

    @Autowired
    StudentGuardianProfileRepository studentGuardianProfileRepository;

    @Autowired
    StudentGuardianDocumentRepository studentGuardianDocumentRepository;

    @Autowired
    StudentGuardianFinancialHistoryRepository studentGuardianFinancialHistoryRepository;

    @Autowired
    StudentGuardianJobHistoryRepository studentGuardianJobHistoryRepository;

    @Autowired
    StudentGuardianFamilyDoctorRepository studentGuardianFamilyDoctorRepository;

    @Autowired
    StudentGuardianHobbyPvtRepository studentGuardianHobbyPvtRepository;

    @Autowired
    StudentGuardianNationalityPvtRepository studentGuardianNationalityPvtRepository;

    @Autowired
    StudentGuardianAilmentPvtRepository studentGuardianAilmentPvtRepository;

    @Autowired
    StudentGuardianAddressRepository studentGuardianAddressRepository;

    @Autowired
    StudentGuardianAcademicHistoryRepository studentGuardianAcademicHistoryRepository;

    @Autowired
    StudentGuardianLanguagePvtRepository studentGuardianLanguagePvtRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_student-guardians_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
        if (size > 100) {
            size = 100;
        }
        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
        int page = pageRequest - 1;

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
            Flux<SlaveStudentGuardianEntity> slaveStudentGuardianFlux = slaveStudentGuardianRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));
            return slaveStudentGuardianFlux
                    .collectList()
                    .flatMap(studentGuardianEntity -> slaveStudentGuardianRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentGuardianEntity> slaveStudentGuardianFlux = slaveStudentGuardianRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveStudentGuardianFlux
                    .collectList()
                    .flatMap(studentGuardianEntity -> slaveStudentGuardianRepository.countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (studentGuardianEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-guardians_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                .flatMap(studentGuardianEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardians_student_show")
    public Mono<ServerResponse> showByStudentUUID(ServerRequest serverRequest) {
        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));

        return slaveStudentGuardianRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                .flatMap(studentGuardianEntityDB -> responseSuccessMsg("Record Fetched Successfully", studentGuardianEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardians_store")
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

                    StudentGuardianEntity studentGuardianEntity = StudentGuardianEntity.builder()
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .guardianTypeUUID(UUID.fromString(value.getFirst("guardianTypeUUID").trim()))
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

                    // checks if student uuid exists
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentGuardianEntity.getStudentUUID())
                            // checks if guardian type uuid exists
                            .flatMap(studentEntity -> studentGuardianRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                    .flatMap(guardianAlreadyExists -> responseInfoMsg("Student Guardian Record Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(studentGuardianEntity.getGuardianTypeUUID())
                                            .flatMap(guardianTypeEntity -> {
                                                // if guardian uuid is specified in the request
                                                if (studentGuardianEntity.getGuardianUUID() != null) {

                                                    switch (guardianTypeEntity.getSlug()) {
                                                        // if Student father is guardian
                                                        case "father":
                                                            return studentFatherRepository.findByUuidAndStudentUUIDAndDeletedAtIsNull(studentGuardianEntity.getGuardianUUID(), studentGuardianEntity.getStudentUUID())
                                                                    .flatMap(employeeFatherEntity -> studentGuardianRepository.save(studentGuardianEntity)
                                                                            .flatMap(employeeGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", employeeGuardianEntityDB))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                    .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));


                                                        // if Student mother is guardian
                                                        case "mother":
                                                            return studentMotherRepository.findByUuidAndStudentUUIDAndDeletedAtIsNull(studentGuardianEntity.getGuardianUUID(), studentGuardianEntity.getStudentUUID())
                                                                    .flatMap(employeeMotherEntity -> studentGuardianRepository.save(studentGuardianEntity)
                                                                            .flatMap(employeeGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", employeeGuardianEntityDB))
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
                                                        return studentGuardianRepository.save(studentGuardianEntity)
                                                                .flatMap(employeeGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", employeeGuardianEntityDB))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                    }
                                                }
                                            }).switchIfEmpty(responseInfoMsg("Guardian Type does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Guardian Type does not exist. Please contact developer."))
                                    ))
                            ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardians_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                        .flatMap(previousEntity -> {

                            UUID guardianUUID = null;
                            if ((value.containsKey("guardianUUID") && (value.getFirst("guardianUUID") != ""))) {
                                guardianUUID = UUID.fromString(value.getFirst("guardianUUID").trim());
                            }

                            StudentGuardianEntity updatedEntity = StudentGuardianEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                                    .guardianTypeUUID(UUID.fromString(value.getFirst("guardianTypeUUID").trim()))
                                    .guardianUUID(guardianUUID)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
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

                            //Deleting Previous Record and Creating a New One Based on UUID
                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            //Storing Deleted Previous Entity First and Then Updated Entity

                            // checks if student uuid exists
                            return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                    // checks if guardian type uuid exists
                                    .flatMap(studentEntity -> studentGuardianRepository.findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getUuid(), studentGuardianUUID)
                                            .flatMap(guardianAlreadyExists -> responseInfoMsg("Student Guardian Record Already Exists"))
                                            .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGuardianTypeUUID())
                                                    .flatMap(guardianTypeEntity -> {
                                                        // if guardian uuid is specified in the request
                                                        if (updatedEntity.getGuardianUUID() != null) {

                                                            switch (guardianTypeEntity.getSlug()) {
                                                                // if Student father is guardian
                                                                case "father":
                                                                    return studentFatherRepository.findByUuidAndStudentUUIDAndDeletedAtIsNull(updatedEntity.getGuardianUUID(), updatedEntity.getStudentUUID())
                                                                            .flatMap(employeeFatherEntity -> studentGuardianRepository.save(previousEntity)
                                                                                    .then(studentGuardianRepository.save(updatedEntity))
                                                                                    .flatMap(stdGuardianEntityDB -> responseSuccessMsg("Record Updated Successfully", stdGuardianEntityDB))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again"))
                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                            .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));


                                                                // if Student mother is guardian
                                                                case "mother":
                                                                    return studentMotherRepository.findByUuidAndStudentUUIDAndDeletedAtIsNull(updatedEntity.getGuardianUUID(), updatedEntity.getStudentUUID())
                                                                            .flatMap(employeeMotherEntity -> studentGuardianRepository.save(previousEntity)
                                                                                    .then(studentGuardianRepository.save(updatedEntity))
                                                                                    .flatMap(stdGuardianEntityDB -> responseSuccessMsg("Record Updated Successfully", stdGuardianEntityDB))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again"))
                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer."))
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
                                                                return studentGuardianRepository.save(previousEntity)
                                                                        .then(studentGuardianRepository.save(updatedEntity))
                                                                        .flatMap(stdGuardianEntityDB -> responseSuccessMsg("Record Updated Successfully", stdGuardianEntityDB))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again"))
                                                                        .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer."));
                                                            }
                                                        }
                                                    }).switchIfEmpty(responseInfoMsg("Guardian Type does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Guardian Type does not exist. Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardians_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGuardianEntity updatedEntity = StudentGuardianEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .guardianTypeUUID(previousEntity.getGuardianTypeUUID())
                                        .guardianUUID(previousEntity.getGuardianUUID())
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

                                return studentGuardianRepository.save(previousEntity)
                                        .then(studentGuardianRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardians_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                //Checks if Student Guardian Reference exists in Student Guardian Profiles
                .flatMap(studentGuardianEntity -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                        .flatMap(studentGuardianProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Student Guardian Reference exists in Student Guardian Documents
                        .switchIfEmpty(Mono.defer(() -> studentGuardianDocumentRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Financial History
                        .switchIfEmpty(Mono.defer(() -> studentGuardianFinancialHistoryRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Job History
                        .switchIfEmpty(Mono.defer(() -> studentGuardianJobHistoryRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Family Doctor
                        .switchIfEmpty(Mono.defer(() -> studentGuardianFamilyDoctorRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> studentGuardianHobbyPvtRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> studentGuardianNationalityPvtRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> studentGuardianAilmentPvtRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Address
                        .switchIfEmpty(Mono.defer(() -> studentGuardianAddressRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Academic History
                        .switchIfEmpty(Mono.defer(() -> studentGuardianAcademicHistoryRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Student Guardian Reference exists in Student Guardian Language Pvt
                        .switchIfEmpty(Mono.defer(() -> studentGuardianLanguagePvtRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .flatMap(studentGuardianDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            studentGuardianEntity.setDeletedBy(UUID.fromString(userId));
                            studentGuardianEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentGuardianEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentGuardianEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentGuardianEntity.setReqDeletedIP(reqIp);
                            studentGuardianEntity.setReqDeletedPort(reqPort);
                            studentGuardianEntity.setReqDeletedBrowser(reqBrowser);
                            studentGuardianEntity.setReqDeletedOS(reqOs);
                            studentGuardianEntity.setReqDeletedDevice(reqDevice);
                            studentGuardianEntity.setReqDeletedReferer(reqReferer);


                            return studentGuardianRepository.save(studentGuardianEntity)
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
