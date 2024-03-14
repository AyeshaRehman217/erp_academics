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
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentContactNoRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianFamilyDoctorRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianFamilyDoctorRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentGuardianFamilyDoctorHandler")
@Component
public class

StudentGuardianFamilyDoctorHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGuardianFamilyDoctorRepository studentGuardianFamilyDoctorRepository;

    @Autowired
    SlaveStudentGuardianFamilyDoctorRepository slaveStudentGuardianFamilyDoctorRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @AuthHasPermission(value = "academic_api_v1_student-guardian-family-doctors_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Guardian UUID
        String studentGuardianUUID = serverRequest.queryParam("studentGuardianUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentGuardianUUID.isEmpty()) {

            Flux<SlaveStudentGuardianFamilyDoctorEntity> slaveStudentGuardianFamilyDoctorFlux = slaveStudentGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status));
            return slaveStudentGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentGuardianFamilyDoctorEntity -> slaveStudentGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentGuardianFamilyDoctorEntity> slaveStudentGuardianFamilyDoctorFlux = slaveStudentGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentGuardianFamilyDoctorEntity -> slaveStudentGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!studentGuardianUUID.isEmpty()) {

            Flux<SlaveStudentGuardianFamilyDoctorEntity> slaveStudentGuardianFamilyDoctorFlux = slaveStudentGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID));
            return slaveStudentGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentGuardianFamilyDoctorEntity -> slaveStudentGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID), searchKeyWord, UUID.fromString(studentGuardianUUID))
                            .flatMap(count -> {
                                if (studentGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveStudentGuardianFamilyDoctorEntity> slaveStudentGuardianFamilyDoctorFlux = slaveStudentGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveStudentGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentGuardianFamilyDoctorEntity -> slaveStudentGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-family-doctors_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentGuardianFamilyDoctorUUID)
                .flatMap(studentGuardianFamilyDoctorEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianFamilyDoctorEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-family-doctors_store")
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
                .flatMap(value -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(UUID.fromString(value.getFirst("studentGuardianUUID").trim()))
                        .flatMap(stdGuardianEntity -> {
                            // if teacher guardian uuid is already set
                            if (stdGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Family Doctor. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
                                StudentGuardianFamilyDoctorEntity entity = StudentGuardianFamilyDoctorEntity.builder()
                                        .uuid(UUID.randomUUID())
                                        .name(value.getFirst("name").trim())
                                        .description(value.getFirst("description").trim())
                                        .contactNo(value.getFirst("contactNo").trim())
                                        .studentGuardianUUID(UUID.fromString(value.getFirst("studentGuardianUUID")))
                                        .clinicalAddress(value.getFirst("clinicalAddress"))
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

                                return studentGuardianFamilyDoctorRepository.findFirstByStudentGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(entity.getStudentGuardianUUID(), entity.getName(), entity.getClinicalAddress())
                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Student Guardian With Same Clinical Address"))
                                        // Check if Student exists or not
                                        .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getStudentGuardianUUID())
                                                .flatMap(checkStudent -> {

                                                    // Check if Contact no is not empty
                                                    if (entity.getContactNo() != "" && entity.getContactNo() != null) {
                                                        //Check if Student && Name && Contact No && Clinical Address Exists
                                                        return studentGuardianFamilyDoctorRepository.findFirstByStudentGuardianUUIDAndNameAndContactNoAndDeletedAtIsNull(entity.getStudentGuardianUUID(), entity.getName(), entity.getContactNo())
                                                                .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Student Guardian With Same Contact No"))
                                                                .switchIfEmpty(Mono.defer(() -> studentGuardianFamilyDoctorRepository.save(entity)
                                                                        .flatMap(studentFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", studentFamilyDoctorEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                ));
                                                    } else {
                                                        return studentGuardianFamilyDoctorRepository.save(entity)
                                                                .flatMap(studentFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", studentFamilyDoctorEntity))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                                    }
                                                })
                                                .switchIfEmpty(responseInfoMsg("Student Guardian Record Does not exist."))
                                                .onErrorResume(ex -> responseErrorMsg("Student Guardian Record Does not Exist.Please Contact Developer."))
                                        ));
                            }
                        })
                        .switchIfEmpty(responseInfoMsg("Student Guardian Record Does not exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Guardian Record Does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-family-doctors_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentGuardianFamilyDoctorUUID)
                        .flatMap(previousEntity -> {

                            StudentGuardianFamilyDoctorEntity updatedEntity = StudentGuardianFamilyDoctorEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .contactNo(value.getFirst("contactNo").trim())
                                    .studentGuardianUUID(previousEntity.getStudentGuardianUUID())
                                    .clinicalAddress(value.getFirst("clinicalAddress"))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
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

                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            //check if Student , Name and Clinical Address is Unique
                            return studentGuardianFamilyDoctorRepository.findFirstByStudentGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentGuardianUUID(), updatedEntity.getName(), updatedEntity.getClinicalAddress(), updatedEntity.getUuid())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Student Guardian With Same Name && Clinical Address"))
                                    // Check if Student exists or not
                                    .switchIfEmpty(Mono.defer(() -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentGuardianUUID())
                                            .flatMap(checkStudentGuardian -> {

                                                // Check if Contact no is not empty
                                                if (updatedEntity.getContactNo() != "" && updatedEntity.getContactNo() != null) {
                                                    //Check if Student Guardian && Name && Contact No && Clinical Address Exists
                                                    return studentGuardianFamilyDoctorRepository.findFirstByStudentGuardianUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentGuardianUUID(), updatedEntity.getName(), updatedEntity.getContactNo(), updatedEntity.getUuid())
                                                            .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Student Guardian With Same Name && Contact No"))
                                                            .switchIfEmpty(Mono.defer(() -> studentGuardianFamilyDoctorRepository.save(previousEntity)
                                                                    .then(studentGuardianFamilyDoctorRepository.save(updatedEntity))
                                                                    .flatMap(studentMthFamilyDoctor -> responseSuccessMsg("Record Updated Successfully", studentMthFamilyDoctor))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                            ));
                                                } else {
                                                    return studentGuardianFamilyDoctorRepository.save(previousEntity)
                                                            .then(studentGuardianFamilyDoctorRepository.save(updatedEntity))
                                                            .flatMap(studentMthFamilyDoctor -> responseSuccessMsg("Record Updated Successfully", studentMthFamilyDoctor))
                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."));
                                                }
                                            })
                                            .switchIfEmpty(responseInfoMsg("Student Guardian Record Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Student Guardian Record Does not Exist.Please Contact Developer."))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-family-doctors_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentGuardianFamilyDoctorUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGuardianFamilyDoctorEntity entity = StudentGuardianFamilyDoctorEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .description(previousEntity.getDescription())
                                        .contactNo(previousEntity.getContactNo())
                                        .studentGuardianUUID(previousEntity.getStudentGuardianUUID())
                                        .clinicalAddress(previousEntity.getClinicalAddress())
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

                                return studentGuardianFamilyDoctorRepository.save(previousEntity)
                                        .then(studentGuardianFamilyDoctorRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-family-doctors_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentGuardianFamilyDoctorUUID)
                .flatMap(studentGuardianFamilyDoctorEntity -> {

                    studentGuardianFamilyDoctorEntity.setDeletedBy(UUID.fromString(userId));
                    studentGuardianFamilyDoctorEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentGuardianFamilyDoctorEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentGuardianFamilyDoctorEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentGuardianFamilyDoctorEntity.setReqDeletedIP(reqIp);
                    studentGuardianFamilyDoctorEntity.setReqDeletedPort(reqPort);
                    studentGuardianFamilyDoctorEntity.setReqDeletedBrowser(reqBrowser);
                    studentGuardianFamilyDoctorEntity.setReqDeletedOS(reqOs);
                    studentGuardianFamilyDoctorEntity.setReqDeletedDevice(reqDevice);
                    studentGuardianFamilyDoctorEntity.setReqDeletedReferer(reqReferer);

                    return studentGuardianFamilyDoctorRepository.save(studentGuardianFamilyDoctorEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
