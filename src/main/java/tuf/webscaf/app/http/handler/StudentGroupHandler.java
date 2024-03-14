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
import tuf.webscaf.app.dbContext.master.entity.StudentGroupEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGroupEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGroupRepository;
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

@Tag(name = "studentGroupHandler")
@Component
public class StudentGroupHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGroupRepository studentGroupRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    SlaveStudentGroupRepository slaveStudentGroupRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    SectionStudentPvtRepository sectionStudentPvtRepository;

    @AuthHasPermission(value = "academic_api_v1_student-groups_index")
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

        // Optional Query Param of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveStudentGroupEntity> slaveStudentGroupFlux = slaveStudentGroupRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentGroupFlux
                    .collectList()
                    .flatMap(studentGroupEntity -> slaveStudentGroupRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGroupEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGroupEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentGroupEntity> slaveStudentGroupFlux = slaveStudentGroupRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveStudentGroupFlux
                    .collectList()
                    .flatMap(studentGroupEntity -> slaveStudentGroupRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentGroupEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGroupEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-groups_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGroupUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                .flatMap(studentGroupEntity -> responseSuccessMsg("Record Fetched Successfully", studentGroupEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-groups_store")
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

                    Integer minLimit = null;
                    if ((!Objects.equals(value.getFirst("min"), "")) && (!Objects.equals(value.getFirst("min"), null))) {
                        minLimit = Integer.valueOf(value.getFirst("min"));
                    }

                    Integer maxLimit = null;
                    if ((!Objects.equals(value.getFirst("max"), "")) && (!Objects.equals(value.getFirst("max"), null))) {
                        maxLimit = Integer.valueOf(value.getFirst("max"));
                    }

                    StudentGroupEntity studentGroupEntity = StudentGroupEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .min(minLimit)
                            .max(maxLimit)
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

                    //  check if student group name is unique
                    return studentGroupRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(studentGroupEntity.getName())
                            .flatMap(ailmentEntity -> responseInfoMsg("Name Already Exist"))
                            .switchIfEmpty(Mono.defer(() -> {

                                //check if min and max limit both are not null
                                if (studentGroupEntity.getMin() != null && studentGroupEntity.getMax() != null) {
                                    //check if minimum limit is greater than max limit
                                    if (studentGroupEntity.getMin() > studentGroupEntity.getMax()) {
                                        return responseInfoMsg("Minimum Limit Must not be greater than max limit");
                                    }
                                }

                                return studentGroupRepository.save(studentGroupEntity)
                                        .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                            }));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-groups_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGroupUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                        .flatMap(previousStudentGroupEntity -> {

                            Integer minLimit = null;
                            if ((!Objects.equals(value.getFirst("min"), "")) && (!Objects.equals(value.getFirst("min"), null))) {
                                minLimit = Integer.valueOf(value.getFirst("min"));
                            }

                            Integer maxLimit = null;
                            if ((!Objects.equals(value.getFirst("max"), "")) && (!Objects.equals(value.getFirst("max"), null))) {
                                maxLimit = Integer.valueOf(value.getFirst("max"));
                            }


                            StudentGroupEntity updatedStudentGroupEntity = StudentGroupEntity.builder()
                                    .uuid(previousStudentGroupEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .min(minLimit)
                                    .max(maxLimit)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousStudentGroupEntity.getCreatedAt())
                                    .createdBy(previousStudentGroupEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousStudentGroupEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStudentGroupEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStudentGroupEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStudentGroupEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStudentGroupEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStudentGroupEntity.getReqCreatedReferer())
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
                            previousStudentGroupEntity.setDeletedBy(UUID.fromString(userId));
                            previousStudentGroupEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStudentGroupEntity.setReqDeletedIP(reqIp);
                            previousStudentGroupEntity.setReqDeletedPort(reqPort);
                            previousStudentGroupEntity.setReqDeletedBrowser(reqBrowser);
                            previousStudentGroupEntity.setReqDeletedOS(reqOs);
                            previousStudentGroupEntity.setReqDeletedDevice(reqDevice);
                            previousStudentGroupEntity.setReqDeletedReferer(reqReferer);

                            //  check student group name is unique
                            return studentGroupRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedStudentGroupEntity.getName(), updatedStudentGroupEntity.getUuid())
                                    .flatMap(ailmentEntity -> responseInfoMsg("Name Already Exist"))
                                    .switchIfEmpty(Mono.defer(() -> {

                                        //check if min and max limit both are not null
                                        if (updatedStudentGroupEntity.getMin() != null && updatedStudentGroupEntity.getMax() != null) {
                                            //check if minimum limit is greater than max limit
                                            if (updatedStudentGroupEntity.getMin() > updatedStudentGroupEntity.getMax()) {
                                                return responseInfoMsg("Minimum Limit Must not be greater than max limit");
                                            }
                                        }

                                        return studentGroupRepository.save(previousStudentGroupEntity)
                                                .then(studentGroupRepository.save(updatedStudentGroupEntity))
                                                .flatMap(studentGroupEntity -> responseSuccessMsg("Record Updated Successfully", studentGroupEntity))
                                                .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                .onErrorResume(err -> responseErrorMsg("Unable to Update record.Please Contact Developer."));
                                    }));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-groups_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGroupUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return studentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                            .flatMap(previousStudentGroupEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousStudentGroupEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGroupEntity updatedStudentGroupEntity = StudentGroupEntity.builder()
                                        .uuid(previousStudentGroupEntity.getUuid())
                                        .name(previousStudentGroupEntity.getName())
                                        .description(previousStudentGroupEntity.getDescription())
                                        .min(previousStudentGroupEntity.getMin())
                                        .max(previousStudentGroupEntity.getMax())
                                        .status(status == true ? true : false)
                                        .createdAt(previousStudentGroupEntity.getCreatedAt())
                                        .createdBy(previousStudentGroupEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousStudentGroupEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousStudentGroupEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousStudentGroupEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousStudentGroupEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousStudentGroupEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousStudentGroupEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousStudentGroupEntity.setDeletedBy(UUID.fromString(userId));
                                previousStudentGroupEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousStudentGroupEntity.setReqDeletedIP(reqIp);
                                previousStudentGroupEntity.setReqDeletedPort(reqPort);
                                previousStudentGroupEntity.setReqDeletedBrowser(reqBrowser);
                                previousStudentGroupEntity.setReqDeletedOS(reqOs);
                                previousStudentGroupEntity.setReqDeletedDevice(reqDevice);
                                previousStudentGroupEntity.setReqDeletedReferer(reqReferer);

                                return studentGroupRepository.save(previousStudentGroupEntity)
                                        .then(studentGroupRepository.save(updatedStudentGroupEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-groups_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {

        final UUID studentGroupUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGroupRepository.findByUuidAndDeletedAtIsNull(studentGroupUUID)
                .flatMap(studentGroupEntity -> {

                    studentGroupEntity.setDeletedBy(UUID.fromString(userId));
                    studentGroupEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentGroupEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentGroupEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentGroupEntity.setReqDeletedIP(reqIp);
                    studentGroupEntity.setReqDeletedPort(reqPort);
                    studentGroupEntity.setReqDeletedBrowser(reqBrowser);
                    studentGroupEntity.setReqDeletedOS(reqOs);
                    studentGroupEntity.setReqDeletedDevice(reqDevice);
                    studentGroupEntity.setReqDeletedReferer(reqReferer);

                    return studentGroupRepository.save(studentGroupEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
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
