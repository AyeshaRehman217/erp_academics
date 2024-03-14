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
import tuf.webscaf.app.dbContext.master.entity.RegistrationEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveRegistrationEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveRegistrationRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "registrationHandler")
@Component
public class RegistrationHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    SlaveRegistrationRepository slaveRegistrationRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    CampusCourseRepository campusCourseRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Autowired
    CampusRepository campusRepository;
    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_registrations_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveRegistrationEntity> slaveRegistrationFlux = slaveRegistrationRepository
                    .findAllByRegistrationNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status));
            return slaveRegistrationFlux
                    .collectList()
                    .flatMap(registrationEntity -> slaveRegistrationRepository
                            .countByRegistrationNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (registrationEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", registrationEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveRegistrationEntity> slaveRegistrationFlux = slaveRegistrationRepository
                    .findAllByRegistrationNoContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
            return slaveRegistrationFlux
                    .collectList()
                    .flatMap(registrationEntity -> slaveRegistrationRepository
                            .countByRegistrationNoContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (registrationEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", registrationEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_registrations_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID registrationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveRegistrationRepository.findByUuidAndDeletedAtIsNull(registrationUUID)
                .flatMap(registrationEntity -> responseSuccessMsg("Record Fetched Successfully", registrationEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_registrations_store")
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

                    RegistrationEntity registrationEntity1 = RegistrationEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                            .registrationNo(value.getFirst("registrationNo").trim())
                            .campusCourseUUID(UUID.fromString(value.getFirst("campusCourseUUID").trim()))
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

                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(registrationEntity1.getAcademicSessionUUID())
                            //check if Registration No. is Unique
                            .flatMap(session -> registrationRepository.findFirstByRegistrationNoAndDeletedAtIsNull(registrationEntity1.getRegistrationNo())
                                    .flatMap(checkUniqueNo -> responseInfoMsg("Registration No Already Exists."))
                                    //checks if student uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(registrationEntity1.getStudentUUID())
                                            //check if student is Unique against the given Campus Course
                                            .flatMap(studentEntity -> registrationRepository.findFirstByStudentUUIDAndCampusCourseUUIDAndDeletedAtIsNull(registrationEntity1.getStudentUUID(), registrationEntity1.getCampusCourseUUID())
                                                    .flatMap(studentCheck -> responseInfoMsg("The Student Against this campus Course Already Exists"))
                                                    .switchIfEmpty(Mono.defer(() -> campusRepository.findByUuidAndDeletedAtIsNull(registrationEntity1.getCampusUUID())
                                                            //check if the Campus Course Exists Against the given Campus or Not
                                                            .flatMap(campus -> campusCourseRepository.findByCampusUUIDAndDeletedAtIsNull(campus.getUuid())
                                                                    .collectList()
                                                                    .flatMap(campusCourseList -> {
                                                                        //if List is Empty then Campus Course Does not Contain record against the given Campus
                                                                        if (campusCourseList.isEmpty()) {
                                                                            return responseInfoMsg("Course Against this Campus does not exist");
                                                                        } else {
                                                                            //check if academic Session and Campus Course exists in Course Offered or not
                                                                            return courseOfferedRepository.findFirstByAcademicSessionUUIDAndCampusCourseUUIDAndDeletedAtIsNull(registrationEntity1.getAcademicSessionUUID(), registrationEntity1.getCampusCourseUUID())
                                                                                    .flatMap(courseOffered -> registrationRepository.save(registrationEntity1)
                                                                                            .flatMap(registrationEntity -> responseSuccessMsg("Record Stored Successfully", registrationEntity))
                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                    ).switchIfEmpty(responseInfoMsg("The Course Against the Given Academic Session Does not Exists"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("The Course Against the Given Academic Session Does not Exist.Please Contact Developer."));
                                                                        }
                                                                    })
                                                            )
                                                            .switchIfEmpty(responseInfoMsg("Campus Does not Exist."))
                                                            .onErrorResume(ex -> responseErrorMsg("Campus Does not exist.Please Contact Developer."))
                                                    ))
                                            ).switchIfEmpty(responseInfoMsg("Student does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student does not exist.Please contact developer."))
                                    ))).switchIfEmpty(responseInfoMsg("Academic Session Does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Session Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_registrations_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID registrationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> registrationRepository.findByUuidAndDeletedAtIsNull(registrationUUID)
                        .flatMap(previousEntity -> {
                            String reason = null;

                            if ((value.containsKey("reason") && (!Objects.equals(value.getFirst("reason"), "")))) {
                                reason = value.getFirst("reason").trim();
                            }

                            RegistrationEntity updatedEntity = RegistrationEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentUUID(previousEntity.getStudentUUID())
                                    .campusUUID(UUID.fromString(value.getFirst("campusUUID").trim()))
                                    .registrationNo(value.getFirst("registrationNo").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .campusCourseUUID(UUID.fromString(value.getFirst("campusCourseUUID").trim()))
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID").trim()))
                                    .reason(reason)
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

                            //check if Registration No. is Unique
                            return registrationRepository.findFirstByRegistrationNoAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getRegistrationNo(), registrationUUID)
                                    .flatMap(checkRegNo -> responseInfoMsg("Registration No Already Exists"))
                                    //check if entered Academic Session exists in academic session table
                                    .switchIfEmpty(Mono.defer(() -> academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAcademicSessionUUID())
                                            .flatMap(academicSession -> campusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCampusUUID())
                                                    //check if the Campus Course Exists Against the given Campus or Not
                                                    .flatMap(campus -> campusCourseRepository.findByCampusUUIDAndDeletedAtIsNull(campus.getUuid())
                                                            .collectList()
                                                            .flatMap(campusCourseList -> {
                                                                //if List is Empty then Campus Course Does not Contain record against the given Campus
                                                                if (campusCourseList.isEmpty()) {
                                                                    return responseInfoMsg("Course Against this Campus does not exist");
                                                                }

                                                                //check if academic Session and Campus Course exists in Course Offered or not
                                                                return courseOfferedRepository.findFirstByAcademicSessionUUIDAndCampusCourseUUIDAndDeletedAtIsNull(academicSession.getUuid(), updatedEntity.getCampusCourseUUID())
                                                                        .flatMap(courseOffered -> {
                                                                                    //if Campus Course offered against the student is not same as  previous one
                                                                                    if (!previousEntity.getCampusCourseUUID().equals(updatedEntity.getCampusCourseUUID())) {
                                                                                        if (updatedEntity.getReason() == null) {
                                                                                            return responseInfoMsg("Please enter reason why you want to switch your course?");
                                                                                        } else {
                                                                                            return registrationRepository.save(previousEntity)
                                                                                                    .then(registrationRepository.save(updatedEntity))
                                                                                                    .flatMap(registrationEntity -> responseSuccessMsg("Record Updated Successfully", registrationEntity))
                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                                                        }
                                                                                    }
                                                                                    //when Campus Course is same
                                                                                    else {
                                                                                        return registrationRepository.save(previousEntity)
                                                                                                .then(registrationRepository.save(updatedEntity))
                                                                                                .flatMap(registrationEntity -> responseSuccessMsg("Record Updated Successfully", registrationEntity))
                                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                                                    }
                                                                                }
                                                                        ).switchIfEmpty(responseInfoMsg("The Course Against the Given Academic Session Does not Exists"))
                                                                        .onErrorResume(ex -> responseErrorMsg("The Course Against the Given Academic Session Does not Exist.Please Contact Developer."));
                                                            })
                                                    )
                                                    .switchIfEmpty(responseInfoMsg("Campus Does not Exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Campus Does not exist.Please Contact Developer."))
                                            )
                                            .switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer"))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_registrations_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID registrationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return registrationRepository.findByUuidAndDeletedAtIsNull(registrationUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                RegistrationEntity entity = RegistrationEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .campusUUID(previousEntity.getCampusUUID())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .academicSessionUUID(previousEntity.getAcademicSessionUUID())
                                        .registrationNo(previousEntity.getRegistrationNo())
                                        .campusCourseUUID(previousEntity.getCampusCourseUUID())
                                        .reason(previousEntity.getReason())
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

                                return registrationRepository.save(previousEntity)
                                        .then(registrationRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_registrations_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID registrationUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return registrationRepository.findByUuidAndDeletedAtIsNull(registrationUUID)
                .flatMap(registrationEntity -> {

                    registrationEntity.setDeletedBy(UUID.fromString(userId));
                    registrationEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    registrationEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    registrationEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    registrationEntity.setReqDeletedIP(reqIp);
                    registrationEntity.setReqDeletedPort(reqPort);
                    registrationEntity.setReqDeletedBrowser(reqBrowser);
                    registrationEntity.setReqDeletedOS(reqOs);
                    registrationEntity.setReqDeletedDevice(reqDevice);
                    registrationEntity.setReqDeletedReferer(reqReferer);

                    return registrationRepository.save(registrationEntity)
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
