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
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.repositry.TeacherContactNoRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianFamilyDoctorRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherGuardianRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianFamilyDoctorRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherGuardianFamilyDoctorHandler")
@Component
public class TeacherGuardianFamilyDoctorHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianFamilyDoctorRepository teacherGuardianFamilyDoctorRepository;

    @Autowired
    SlaveTeacherGuardianFamilyDoctorRepository slaveTeacherGuardianFamilyDoctorRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-family-doctors_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Guardian UUID
        String teacherGuardianUUID = serverRequest.queryParam("teacherGuardianUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianFamilyDoctorEntity> slaveTeacherGuardianFamilyDoctorFlux = slaveTeacherGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status));
            return slaveTeacherGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherGuardianFamilyDoctorEntity -> slaveTeacherGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherGuardianUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherGuardianFamilyDoctorEntity> slaveTeacherGuardianFamilyDoctorFlux = slaveTeacherGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherGuardianFamilyDoctorEntity -> slaveTeacherGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!teacherGuardianUUID.isEmpty()) {

            Flux<SlaveTeacherGuardianFamilyDoctorEntity> slaveTeacherGuardianFamilyDoctorFlux = slaveTeacherGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID));
            return slaveTeacherGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherGuardianFamilyDoctorEntity -> slaveTeacherGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherGuardianUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID), searchKeyWord, UUID.fromString(teacherGuardianUUID))
                            .flatMap(count -> {
                                if (teacherGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveTeacherGuardianFamilyDoctorEntity> slaveTeacherGuardianFamilyDoctorFlux = slaveTeacherGuardianFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveTeacherGuardianFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherGuardianFamilyDoctorEntity -> slaveTeacherGuardianFamilyDoctorRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherGuardianFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-family-doctors_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFamilyDoctorUUID)
                .flatMap(teacherFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-family-doctors_store")
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
                // checks if teacher guardian uuid exists
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                        .flatMap(teacherGuardianEntity -> {

                            // if teacher guardian uuid is already set
                            if (teacherGuardianEntity.getGuardianUUID() != null) {
                                return responseInfoMsg("Unable to Create Guardian Family Doctor. Guardian Records Already Exists");
                            }
                            // else store the record
                            else {
                                TeacherGuardianFamilyDoctorEntity entity = TeacherGuardianFamilyDoctorEntity.builder()
                                        .uuid(UUID.randomUUID())
                                        .name(value.getFirst("name").trim())
                                        .description(value.getFirst("description").trim())
                                        .contactNo(value.getFirst("contactNo").trim())
                                        .teacherGuardianUUID(UUID.fromString(value.getFirst("teacherGuardianUUID").trim()))
                                        .clinicalAddress(value.getFirst("clinicalAddress").trim())
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

                                //check if Teacher Guardian Name and Clinical Address is Unique
                                return teacherGuardianFamilyDoctorRepository.findFirstByTeacherGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(entity.getTeacherGuardianUUID(), entity.getName(), entity.getClinicalAddress())
                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Teacher Guardian With Same Clinical Address"))
                                        // Check if Teacher Guardian exists or not
                                        .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherGuardianUUID())
                                                .flatMap(checkTeacher -> {

                                                    // Check if Contact no is not empty
                                                    if (entity.getContactNo() != "" && entity.getContactNo() != null) {
                                                        //Check if Teacher Guardian && Name && Contact No && Clinical Address Exists
                                                        return teacherGuardianFamilyDoctorRepository.findFirstByTeacherGuardianUUIDAndNameAndContactNoAndDeletedAtIsNull(entity.getTeacherGuardianUUID(), entity.getName(), entity.getContactNo())
                                                                .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Teacher Guardian With Same Contact No"))
                                                                .switchIfEmpty(Mono.defer(() -> teacherGuardianFamilyDoctorRepository.save(entity)
                                                                        .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", teacherFamilyDoctorEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                ));
                                                    } else {
                                                        return teacherGuardianFamilyDoctorRepository.save(entity)
                                                                .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", teacherFamilyDoctorEntity))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                                    }
                                                })
                                                .switchIfEmpty(responseInfoMsg("Teacher Guardian Record Does not exist."))
                                                .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record Does not Exist.Please Contact Developer."))
                                        ));
                            }
                        }).switchIfEmpty(responseInfoMsg("Teacher Guardian record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Guardian record does not exist. Please contact developer"))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-family-doctors_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFamilyDoctorUUID)
                        .flatMap(entity -> {

                            TeacherGuardianFamilyDoctorEntity updatedEntity = TeacherGuardianFamilyDoctorEntity.builder()
                                    .uuid(entity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .contactNo(value.getFirst("contactNo").trim())
                                    .teacherGuardianUUID(entity.getTeacherGuardianUUID())
                                    .clinicalAddress(value.getFirst("clinicalAddress").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
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

                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            //check if teacher , Name and Clinical Address is Unique
                            return teacherGuardianFamilyDoctorRepository.findFirstByTeacherGuardianUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherGuardianUUID(), updatedEntity.getName(), updatedEntity.getClinicalAddress(), updatedEntity.getUuid())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against teacher Guardian With Same Name && Clinical Address"))
                                    // Check if teacher exists or not
                                    .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherGuardianUUID())
                                            .flatMap(checkteacher -> {

                                                // Check if Contact no is not empty
                                                if (updatedEntity.getContactNo() != "" && updatedEntity.getContactNo() != null) {
                                                    //Check if teacher && Name && Contact No && Clinical Address Exists
                                                    return teacherGuardianFamilyDoctorRepository.findFirstByTeacherGuardianUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherGuardianUUID(), updatedEntity.getName(), updatedEntity.getContactNo(), updatedEntity.getUuid())
                                                            .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against teacher With Same Name && Contact No"))
                                                            .switchIfEmpty(Mono.defer(() -> teacherGuardianFamilyDoctorRepository.save(entity)
                                                                    .then(teacherGuardianFamilyDoctorRepository.save(updatedEntity))
                                                                    .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Updated Successfully", teacherFamilyDoctorEntity))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                            ));
                                                } else {
                                                    return teacherGuardianFamilyDoctorRepository.save(entity)
                                                            .then(teacherGuardianFamilyDoctorRepository.save(updatedEntity))
                                                            .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Updated Successfully", teacherFamilyDoctorEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."));
                                                }
                                            })
                                            .switchIfEmpty(responseInfoMsg("Teacher Guardian Record Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record Does not Exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist")))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-family-doctors_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFamilyDoctorUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherGuardianFamilyDoctorEntity entity = TeacherGuardianFamilyDoctorEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .name(previousEntity.getName())
                                        .description(previousEntity.getDescription())
                                        .contactNo(previousEntity.getContactNo())
                                        .teacherGuardianUUID(previousEntity.getTeacherGuardianUUID())
                                        .clinicalAddress(previousEntity.getClinicalAddress())
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

                                return teacherGuardianFamilyDoctorRepository.save(previousEntity)
                                        .then(teacherGuardianFamilyDoctorRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-guardian-family-doctors_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherGuardianFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherGuardianFamilyDoctorUUID)
                .flatMap(teacherGuardianFamilyDoctorEntity -> {
                    teacherGuardianFamilyDoctorEntity.setDeletedBy(UUID.fromString(userId));
                    teacherGuardianFamilyDoctorEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherGuardianFamilyDoctorEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherGuardianFamilyDoctorEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherGuardianFamilyDoctorEntity.setReqDeletedIP(reqIp);
                    teacherGuardianFamilyDoctorEntity.setReqDeletedPort(reqPort);
                    teacherGuardianFamilyDoctorEntity.setReqDeletedBrowser(reqBrowser);
                    teacherGuardianFamilyDoctorEntity.setReqDeletedOS(reqOs);
                    teacherGuardianFamilyDoctorEntity.setReqDeletedDevice(reqDevice);
                    teacherGuardianFamilyDoctorEntity.setReqDeletedReferer(reqReferer);

                    return teacherGuardianFamilyDoctorRepository.save(teacherGuardianFamilyDoctorEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
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
