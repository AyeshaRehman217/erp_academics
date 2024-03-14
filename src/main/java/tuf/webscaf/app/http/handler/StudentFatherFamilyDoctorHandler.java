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
import tuf.webscaf.app.dbContext.master.entity.StudentFatherFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentContactNoRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherFamilyDoctorRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherFamilyDoctorRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentFatherFamilyDoctorHandler")
@Component
public class StudentFatherFamilyDoctorHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentFatherFamilyDoctorRepository studentFatherFamilyDoctorRepository;

    @Autowired
    SlaveStudentFatherFamilyDoctorRepository slaveStudentFatherFamilyDoctorRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @AuthHasPermission(value = "academic_api_v1_student-father-family-doctors_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Father UUID
        String studentFatherUUID = serverRequest.queryParam("studentFatherUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentFatherUUID.isEmpty()) {

            Flux<SlaveStudentFatherFamilyDoctorEntity> slaveStudentFatherFamilyDoctorFlux = slaveStudentFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status));

            return slaveStudentFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentFatherFamilyDoctorEntity -> slaveStudentFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentFatherUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentFatherFamilyDoctorEntity> slaveStudentFatherFamilyDoctorFlux = slaveStudentFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentFatherFamilyDoctorEntity -> slaveStudentFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!studentFatherUUID.isEmpty()) {

            Flux<SlaveStudentFatherFamilyDoctorEntity> slaveStudentFatherFamilyDoctorFlux = slaveStudentFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID));

            return slaveStudentFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentFatherFamilyDoctorEntity -> slaveStudentFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentFatherUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID), searchKeyWord, UUID.fromString(studentFatherUUID))
                            .flatMap(count -> {
                                if (studentFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {

            Flux<SlaveStudentFatherFamilyDoctorEntity> slaveStudentFatherFamilyDoctorFlux = slaveStudentFatherFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveStudentFatherFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentFatherFamilyDoctorEntity -> slaveStudentFatherFamilyDoctorRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {

                                if (studentFatherFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherFamilyDoctorEntity, count);
                                }

                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-father-family-doctors_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentFatherFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentFatherFamilyDoctorUUID)
                .flatMap(studentFatherFamilyDoctorEntity -> responseSuccessMsg("Record Fetched Successfully", studentFatherFamilyDoctorEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-family-doctors_store")
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

                    StudentFatherFamilyDoctorEntity entity = StudentFatherFamilyDoctorEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .contactNo(value.getFirst("contactNo").trim())
                            .studentFatherUUID(UUID.fromString(value.getFirst("studentFatherUUID")))
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

                    //check if Student , Name and Clinical Address is Unique
                    return studentFatherFamilyDoctorRepository.findFirstByStudentFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(entity.getStudentFatherUUID(), entity.getName(), entity.getClinicalAddress())
                            .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Student Father With Same Clinical Address"))
                            // Check if Student exists or not
                            .switchIfEmpty(Mono.defer(() -> studentFatherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentFatherUUID())
                                    .flatMap(checkStudent -> {

                                        // Check if Contact no is not empty
                                        if (entity.getContactNo() != "" && entity.getContactNo() != null) {
                                            //Check if Student && Name && Contact No && Clinical Address Exists
                                            return studentFatherFamilyDoctorRepository.findFirstByStudentFatherUUIDAndNameAndContactNoAndDeletedAtIsNull(entity.getStudentFatherUUID(), entity.getName(), entity.getContactNo())
                                                    .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Student With Same Contact No"))
                                                    .switchIfEmpty(Mono.defer(() -> studentFatherFamilyDoctorRepository.save(entity)
                                                            .flatMap(studentFatherFamilyDoctor -> responseSuccessMsg("Record Stored Successfully", studentFatherFamilyDoctor))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                    ));
                                        } else {
                                            return studentFatherFamilyDoctorRepository.save(entity)
                                                    .flatMap(studentFatherFamilyDoctor -> responseSuccessMsg("Record Stored Successfully", studentFatherFamilyDoctor))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                        }
                                    })
                                    .switchIfEmpty(responseInfoMsg("Student Father Record Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Student Father Record Does not Exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-family-doctors_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentFatherFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentFatherFamilyDoctorUUID)
                        .flatMap(previousEntity -> {

                            StudentFatherFamilyDoctorEntity updatedEntity = StudentFatherFamilyDoctorEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentFatherUUID(previousEntity.getStudentFatherUUID())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .contactNo(value.getFirst("contactNo").trim())
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
                            return studentFatherFamilyDoctorRepository.findFirstByStudentFatherUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentFatherUUID(), updatedEntity.getName(), updatedEntity.getClinicalAddress(), updatedEntity.getUuid())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Student Father With Same Name && Clinical Address"))
                                    // Check if Student exists or not
                                    .switchIfEmpty(Mono.defer(() -> studentFatherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentFatherUUID())
                                            .flatMap(checkStudentFather -> {

                                                // Check if Contact no is not empty
                                                if (updatedEntity.getContactNo() != "" && updatedEntity.getContactNo() != null) {
                                                    //Check if Student && Name && Contact No && Clinical Address Exists
                                                    return studentFatherFamilyDoctorRepository.findFirstByStudentFatherUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentFatherUUID(), updatedEntity.getName(), updatedEntity.getContactNo(), updatedEntity.getUuid())
                                                            .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Student Father With Same Name && Contact No"))
                                                            .switchIfEmpty(Mono.defer(() -> studentFatherFamilyDoctorRepository.save(previousEntity)
                                                                    .then(studentFatherFamilyDoctorRepository.save(updatedEntity))
                                                                    .flatMap(studentMthFamilyDoctor -> responseSuccessMsg("Record Updated Successfully", studentMthFamilyDoctor))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                            ));
                                                } else {
                                                    return studentFatherFamilyDoctorRepository.save(previousEntity)
                                                            .then(studentFatherFamilyDoctorRepository.save(updatedEntity))
                                                            .flatMap(studentMthFamilyDoctor -> responseSuccessMsg("Record Updated Successfully", studentMthFamilyDoctor))
                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."));
                                                }
                                            })
                                            .switchIfEmpty(responseInfoMsg("Student Father Record Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Student Father Record Does not Exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer.")))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request"));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-family-doctors_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentFatherFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentFatherFamilyDoctorUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentFatherFamilyDoctorEntity entity = StudentFatherFamilyDoctorEntity.builder()
                                        .uuid(val.getUuid())
                                        .status(status == true ? true : false)
                                        .name(val.getName())
                                        .description(val.getDescription())
                                        .contactNo(val.getContactNo())
                                        .studentFatherUUID(val.getStudentFatherUUID())
                                        .clinicalAddress(val.getClinicalAddress())
                                        .createdAt(val.getCreatedAt())
                                        .createdBy(val.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(val.getReqCreatedIP())
                                        .reqCreatedPort(val.getReqCreatedPort())
                                        .reqCreatedBrowser(val.getReqCreatedBrowser())
                                        .reqCreatedOS(val.getReqCreatedOS())
                                        .reqCreatedDevice(val.getReqCreatedDevice())
                                        .reqCreatedReferer(val.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                val.setDeletedBy(UUID.fromString(userId));
                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                val.setReqDeletedIP(reqIp);
                                val.setReqDeletedPort(reqPort);
                                val.setReqDeletedBrowser(reqBrowser);
                                val.setReqDeletedOS(reqOs);
                                val.setReqDeletedDevice(reqDevice);
                                val.setReqDeletedReferer(reqReferer);

                                return studentFatherFamilyDoctorRepository.save(val)
                                        .then(studentFatherFamilyDoctorRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-family-doctors_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentFatherFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentFatherFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentFatherFamilyDoctorUUID)
                .flatMap(studentFatherAcademicHistoryEntity -> {

                    studentFatherAcademicHistoryEntity.setDeletedBy(UUID.fromString(userId));
                    studentFatherAcademicHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentFatherAcademicHistoryEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentFatherAcademicHistoryEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentFatherAcademicHistoryEntity.setReqDeletedIP(reqIp);
                    studentFatherAcademicHistoryEntity.setReqDeletedPort(reqPort);
                    studentFatherAcademicHistoryEntity.setReqDeletedBrowser(reqBrowser);
                    studentFatherAcademicHistoryEntity.setReqDeletedOS(reqOs);
                    studentFatherAcademicHistoryEntity.setReqDeletedDevice(reqDevice);
                    studentFatherAcademicHistoryEntity.setReqDeletedReferer(reqReferer);

                    return studentFatherFamilyDoctorRepository.save(studentFatherAcademicHistoryEntity)
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
