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
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.repositry.TeacherContactNoRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherFatherFamilyDoctorRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherFamilyDoctorRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherFatherFamilyDoctorHandler")
@Component
public class TeacherFatherFamilyDoctorHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherFatherFamilyDoctorRepository teacherFatherFamilyDoctorRepository;

    @Autowired
    SlaveTeacherFatherFamilyDoctorRepository slaveTeacherFatherFamilyDoctorRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @AuthHasPermission(value = "academic_api_v1_teacher-father-family-doctors_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Teacher Father UUID
        String teacherFatherUUID = serverRequest.queryParam("teacherFatherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !teacherFatherUUID.isEmpty()) {

            Flux<SlaveTeacherFatherFamilyDoctorEntity> slaveTeacherFatherFamilyDoctorFlux = slaveTeacherFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status));

            return slaveTeacherFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherFatherFamilyDoctorEntity -> slaveTeacherFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(teacherFatherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveTeacherFatherFamilyDoctorEntity> slaveTeacherFatherFamilyDoctorFlux = slaveTeacherFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveTeacherFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherFatherFamilyDoctorEntity -> slaveTeacherFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!teacherFatherUUID.isEmpty()) {

            Flux<SlaveTeacherFatherFamilyDoctorEntity> slaveTeacherFatherFamilyDoctorFlux = slaveTeacherFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(teacherFatherUUID), searchKeyWord, UUID.fromString(teacherFatherUUID), searchKeyWord, UUID.fromString(teacherFatherUUID));

            return slaveTeacherFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherFatherFamilyDoctorEntity -> slaveTeacherFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndTeacherFatherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(teacherFatherUUID), searchKeyWord, UUID.fromString(teacherFatherUUID), searchKeyWord, UUID.fromString(teacherFatherUUID))
                            .flatMap(count -> {
                                if (teacherFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveTeacherFatherFamilyDoctorEntity> slaveTeacherFatherFamilyDoctorFlux = slaveTeacherFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveTeacherFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(teacherFatherFamilyDoctorEntity -> slaveTeacherFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-family-doctors_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherFatherFamilyDoctorUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherFatherFamilyDoctorUUID)
                .flatMap(teacherFatherFamilyDoctorEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFatherFamilyDoctorEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-family-doctors_store")
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

                    TeacherFatherFamilyDoctorEntity entity = TeacherFatherFamilyDoctorEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .contactNo(value.getFirst("contactNo").trim())
                            .teacherFatherUUID(UUID.fromString(value.getFirst("teacherFatherUUID").trim()))
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

                    //check if Teacher Father, Name and Clinical Address is Unique
                    return teacherFatherFamilyDoctorRepository.findFirstByTeacherFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(entity.getTeacherFatherUUID(), entity.getName(), entity.getClinicalAddress())
                            .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Teacher Father With Same Clinical Address"))
                            // Check if Teacher Father exists or not
                            .switchIfEmpty(Mono.defer(() -> teacherFatherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherFatherUUID())
                                    .flatMap(checkTeacher -> {

                                        // Check if Contact no is not empty
                                        if (entity.getContactNo() != "" && entity.getContactNo() != null) {
                                            //Check if Teacher && Name && Contact No && Clinical Address Exists
                                            return teacherFatherFamilyDoctorRepository.findFirstByTeacherFatherUUIDAndNameAndContactNoAndDeletedAtIsNull(entity.getTeacherFatherUUID(), entity.getName(), entity.getContactNo())
                                                    .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Teacher Father With Same Contact No"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherFatherFamilyDoctorRepository.save(entity)
                                                            .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", teacherFamilyDoctorEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                    ));
                                        } else {
                                            return teacherFatherFamilyDoctorRepository.save(entity)
                                                    .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", teacherFamilyDoctorEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                        }
                                    })
                                    .switchIfEmpty(responseInfoMsg("Teacher Father Record Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Teacher Father Record Does not Exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-family-doctors_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherFatherFamilyDoctorUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherFatherFamilyDoctorUUID)
                        .flatMap(previousEntity -> {

                            TeacherFatherFamilyDoctorEntity updatedEntity = TeacherFatherFamilyDoctorEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .contactNo(value.getFirst("contactNo").trim())
                                    .teacherFatherUUID(previousEntity.getTeacherFatherUUID())
                                    .clinicalAddress(value.getFirst("clinicalAddress").trim())
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

                            //check if teacher , Name and Clinical Address is Unique
                            return teacherFatherFamilyDoctorRepository.findFirstByTeacherFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherFatherUUID(), updatedEntity.getName(), updatedEntity.getClinicalAddress(), updatedEntity.getUuid())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against teacher With Same Name && Clinical Address"))
                                    // Check if teacher exists or not
                                    .switchIfEmpty(Mono.defer(() -> teacherFatherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherFatherUUID())
                                            .flatMap(checkTeacher -> {

                                                // Check if Contact no is not empty
                                                if (updatedEntity.getContactNo() != "" && updatedEntity.getContactNo() != null) {
                                                    //Check if teacher && Name && Contact No && Clinical Address Exists
                                                    return teacherFatherFamilyDoctorRepository.findFirstByTeacherFatherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherFatherUUID(), updatedEntity.getName(), updatedEntity.getContactNo(), updatedEntity.getUuid())
                                                            .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against teacher Father With Same Name && Contact No"))
                                                            .switchIfEmpty(Mono.defer(() -> teacherFatherFamilyDoctorRepository.save(previousEntity)
                                                                    .then(teacherFatherFamilyDoctorRepository.save(updatedEntity))
                                                                    .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Updated Successfully", teacherFamilyDoctorEntity))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                            ));
                                                } else {
                                                    return teacherFatherFamilyDoctorRepository.save(previousEntity)
                                                            .then(teacherFatherFamilyDoctorRepository.save(updatedEntity))
                                                            .flatMap(teacherFamilyDoctorEntity -> responseSuccessMsg("Record Updated Successfully", teacherFamilyDoctorEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."));
                                                }
                                            })
                                            .switchIfEmpty(responseInfoMsg("Teacher Father Record Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Teacher Father Record Does not Exist.Please Contact Developer."))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-family-doctors_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherFatherFamilyDoctorUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return teacherFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherFatherFamilyDoctorUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherFatherFamilyDoctorEntity entity = TeacherFatherFamilyDoctorEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .description(previousEntity.getDescription())
                                        .contactNo(previousEntity.getContactNo())
                                        .teacherFatherUUID(previousEntity.getTeacherFatherUUID())
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

                                return teacherFatherFamilyDoctorRepository.save(previousEntity)
                                        .then(teacherFatherFamilyDoctorRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-family-doctors_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherFatherFamilyDoctorUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(teacherFatherFamilyDoctorUUID)
                .flatMap(teacherFatherFamilyDoctorEntity -> {
                    teacherFatherFamilyDoctorEntity.setDeletedBy(UUID.fromString(userId));
                    teacherFatherFamilyDoctorEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherFatherFamilyDoctorEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherFatherFamilyDoctorEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherFatherFamilyDoctorEntity.setReqDeletedIP(reqIp);
                    teacherFatherFamilyDoctorEntity.setReqDeletedPort(reqPort);
                    teacherFatherFamilyDoctorEntity.setReqDeletedBrowser(reqBrowser);
                    teacherFatherFamilyDoctorEntity.setReqDeletedOS(reqOs);
                    teacherFatherFamilyDoctorEntity.setReqDeletedDevice(reqDevice);
                    teacherFatherFamilyDoctorEntity.setReqDeletedReferer(reqReferer);

                    return teacherFatherFamilyDoctorRepository.save(teacherFatherFamilyDoctorEntity)
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
