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
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentContactNoRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSpouseFamilyDoctorRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentSpouseRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseFamilyDoctorEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSpouseFamilyDoctorRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentSpouseFamilyDoctorHandler")
@Component
public class StudentSpouseFamilyDoctorHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSpouseFamilyDoctorRepository studentSpouseFamilyDoctorRepository;

    @Autowired
    SlaveStudentSpouseFamilyDoctorRepository slaveStudentSpouseFamilyDoctorRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @AuthHasPermission(value = "academic_api_v1_student-spouse-family-doctors_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        //Optional Query Parameter of Student Spouse UUID
        String studentSpouseUUID = serverRequest.queryParam("studentSpouseUUID").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty() && !studentSpouseUUID.isEmpty()) {

            Flux<SlaveStudentSpouseFamilyDoctorEntity> slaveStudentSpouseFamilyDoctorFlux = slaveStudentSpouseFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status));
            return slaveStudentSpouseFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentSpouseFamilyDoctorEntity -> slaveStudentSpouseFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSpouseUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(studentSpouseUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSpouseFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!status.isEmpty()) {

            Flux<SlaveStudentSpouseFamilyDoctorEntity> slaveStudentSpouseFamilyDoctorFlux = slaveStudentSpouseFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentSpouseFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentSpouseFamilyDoctorEntity -> slaveStudentSpouseFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSpouseFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else if (!studentSpouseUUID.isEmpty()) {

            Flux<SlaveStudentSpouseFamilyDoctorEntity> slaveStudentSpouseFamilyDoctorFlux = slaveStudentSpouseFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID));
            return slaveStudentSpouseFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentSpouseFamilyDoctorEntity -> slaveStudentSpouseFamilyDoctorRepository.countByNameContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndStudentSpouseUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID), searchKeyWord, UUID.fromString(studentSpouseUUID))
                            .flatMap(count -> {
                                if (studentSpouseFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {

            Flux<SlaveStudentSpouseFamilyDoctorEntity> slaveStudentSpouseFamilyDoctorFlux = slaveStudentSpouseFamilyDoctorRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveStudentSpouseFamilyDoctorFlux
                    .collectList()
                    .flatMap(studentSpouseFamilyDoctorEntity -> slaveStudentSpouseFamilyDoctorRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrClinicalAddressContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSpouseFamilyDoctorEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseFamilyDoctorEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-family-doctors_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSpouseFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSpouseFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentSpouseFamilyDoctorUUID)
                .flatMap(studentFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-family-doctors_store")
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

                    StudentSpouseFamilyDoctorEntity entity = StudentSpouseFamilyDoctorEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .contactNo(value.getFirst("contactNo").trim())
                            .studentSpouseUUID(UUID.fromString(value.getFirst("studentSpouseUUID").trim()))
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


                    return studentSpouseFamilyDoctorRepository.findFirstByStudentSpouseUUIDAndNameAndClinicalAddressAndDeletedAtIsNull(entity.getStudentSpouseUUID(), entity.getName(), entity.getClinicalAddress())
                            .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Student Spouse With Same Clinical Address"))
                            // Check if Student exists or not
                            .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSpouseUUID())
                                    .flatMap(checkStudent -> {

                                        // Check if Contact no is not empty
                                        if (entity.getContactNo() != "" && entity.getContactNo() != null) {
                                            //Check if Student && Name && Contact No && Clinical Address Exists
                                            return studentSpouseFamilyDoctorRepository.findFirstByStudentSpouseUUIDAndNameAndContactNoAndDeletedAtIsNull(entity.getStudentSpouseUUID(), entity.getName(), entity.getContactNo())
                                                    .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Student Spouse With Same Contact No"))
                                                    .switchIfEmpty(Mono.defer(() -> studentSpouseFamilyDoctorRepository.save(entity)
                                                            .flatMap(studentFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", studentFamilyDoctorEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                    ));
                                        } else {
                                            return studentSpouseFamilyDoctorRepository.save(entity)
                                                    .flatMap(studentFamilyDoctorEntity -> responseSuccessMsg("Record Stored Successfully", studentFamilyDoctorEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."));
                                        }
                                    })
                                    .switchIfEmpty(responseInfoMsg("Student Spouse Record Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Student Spouse Record Does not Exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-family-doctors_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSpouseFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentSpouseFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentSpouseFamilyDoctorUUID)
                        .flatMap(previousEntity -> {

                            StudentSpouseFamilyDoctorEntity updatedEntity = StudentSpouseFamilyDoctorEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .contactNo(value.getFirst("contactNo").trim())
                                    .studentSpouseUUID(previousEntity.getStudentSpouseUUID())
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


                            //check if Student , Name and Clinical Address is Unique
                            return studentSpouseFamilyDoctorRepository.findFirstByStudentSpouseUUIDAndNameAndClinicalAddressAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSpouseUUID(), updatedEntity.getName(), updatedEntity.getClinicalAddress(), updatedEntity.getUuid())
                                    .flatMap(recordAlreadyExists -> responseInfoMsg("Record Already Exists Against Student Spouse With Same Name && Clinical Address"))
                                    // Check if Student exists or not
                                    .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentSpouseUUID())
                                            .flatMap(checkStudentSpouse -> {

                                                // Check if Contact no is not empty
                                                if (updatedEntity.getContactNo() != "" && updatedEntity.getContactNo() != null) {
                                                    //Check if Student Spouse && Name && Contact No && Clinical Address Exists
                                                    return studentSpouseFamilyDoctorRepository.findFirstByStudentSpouseUUIDAndNameAndContactNoAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSpouseUUID(), updatedEntity.getName(), updatedEntity.getContactNo(), updatedEntity.getUuid())
                                                            .flatMap(checkMsg -> responseInfoMsg("Record Already Exists Against Student Spouse With Same Name && Contact No"))
                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseFamilyDoctorRepository.save(previousEntity)
                                                                    .then(studentSpouseFamilyDoctorRepository.save(updatedEntity))
                                                                    .flatMap(studentMthFamilyDoctor -> responseSuccessMsg("Record Updated Successfully", studentMthFamilyDoctor))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."))
                                                            ));
                                                } else {
                                                    return studentSpouseFamilyDoctorRepository.save(previousEntity)
                                                            .then(studentSpouseFamilyDoctorRepository.save(updatedEntity))
                                                            .flatMap(studentMthFamilyDoctor -> responseSuccessMsg("Record Updated Successfully", studentMthFamilyDoctor))
                                                            .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseInfoMsg("Unable to Update record. Please contact developer."));
                                                }
                                            })
                                            .switchIfEmpty(responseInfoMsg("Student Spouse Record Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Student Spouse Record Does not Exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist")))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-family-doctors_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSpouseFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentSpouseFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentSpouseFamilyDoctorUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSpouseFamilyDoctorEntity entity = StudentSpouseFamilyDoctorEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .name(previousEntity.getName())
                                        .description(previousEntity.getDescription())
                                        .contactNo(previousEntity.getContactNo())
                                        .studentSpouseUUID(previousEntity.getStudentSpouseUUID())
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

                                return studentSpouseFamilyDoctorRepository.save(previousEntity)
                                        .then(studentSpouseFamilyDoctorRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-family-doctors_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSpouseFamilyDoctorUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSpouseFamilyDoctorRepository.findByUuidAndDeletedAtIsNull(studentSpouseFamilyDoctorUUID)
                .flatMap(studentSpouseFamilyDoctorEntity -> {

                    studentSpouseFamilyDoctorEntity.setDeletedBy(UUID.fromString(userId));
                    studentSpouseFamilyDoctorEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentSpouseFamilyDoctorEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentSpouseFamilyDoctorEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentSpouseFamilyDoctorEntity.setReqDeletedIP(reqIp);
                    studentSpouseFamilyDoctorEntity.setReqDeletedPort(reqPort);
                    studentSpouseFamilyDoctorEntity.setReqDeletedBrowser(reqBrowser);
                    studentSpouseFamilyDoctorEntity.setReqDeletedOS(reqOs);
                    studentSpouseFamilyDoctorEntity.setReqDeletedDevice(reqDevice);
                    studentSpouseFamilyDoctorEntity.setReqDeletedReferer(reqReferer);

                    return studentSpouseFamilyDoctorRepository.save(studentSpouseFamilyDoctorEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
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
