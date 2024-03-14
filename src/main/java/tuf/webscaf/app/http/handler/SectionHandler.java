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
import tuf.webscaf.app.dbContext.master.entity.SectionEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSectionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSectionRepository;
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

@Tag(name = "sectionHandler")
@Component
public class SectionHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SectionStudentPvtRepository sectionStudentPvtRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    SlaveSectionRepository slaveSectionRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    CourseOfferedRepository courseOfferedRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_sections_index")
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

        // courseOffered Query Parameter
        String courseOfferedUUID = serverRequest.queryParam("courseOfferedUUID").map(String::toString).orElse("").trim();

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));


        if (!status.isEmpty() && !courseOfferedUUID.isEmpty()) {
            Flux<SlaveSectionEntity> slaveSectionFlux = slaveSectionRepository
                    .findAllByNameContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, UUID.fromString(courseOfferedUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(courseOfferedUUID), Boolean.valueOf(status));
            return slaveSectionFlux
                    .collectList()
                    .flatMap(sectionEntity -> slaveSectionRepository
                            .countByNameContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, UUID.fromString(courseOfferedUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(courseOfferedUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (sectionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sectionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!courseOfferedUUID.isEmpty()) {
            Flux<SlaveSectionEntity> slaveSectionFlux = slaveSectionRepository
                    .findAllByNameContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNull
                            (pageable, searchKeyWord, UUID.fromString(courseOfferedUUID), searchKeyWord, UUID.fromString(courseOfferedUUID));
            return slaveSectionFlux
                    .collectList()
                    .flatMap(sectionEntity -> slaveSectionRepository
                            .countByNameContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNull
                                    (searchKeyWord, UUID.fromString(courseOfferedUUID), searchKeyWord, UUID.fromString(courseOfferedUUID))
                            .flatMap(count -> {
                                if (sectionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sectionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveSectionEntity> slaveSectionFlux = slaveSectionRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull
                            (pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveSectionFlux
                    .collectList()
                    .flatMap(sectionEntity -> slaveSectionRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull
                                    (searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (sectionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sectionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSectionEntity> slaveSectionFlux = slaveSectionRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveSectionFlux
                    .collectList()
                    .flatMap(sectionEntity -> slaveSectionRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (sectionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", sectionEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_sections_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID sectionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSectionRepository.findByUuidAndDeletedAtIsNull(sectionUUID)
                .flatMap(sectionEntity -> responseSuccessMsg("Record Fetched Successfully", sectionEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sections_store")
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

                    SectionEntity sectionEntity = SectionEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .courseOfferedUUID(UUID.fromString(value.getFirst("courseOfferedUUID").trim()))
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

                    //  check if section name is unique
                    return sectionRepository.findFirstByNameIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNull(sectionEntity.getName(), sectionEntity.getCourseOfferedUUID())
                            .flatMap(ailmentEntity -> responseInfoMsg("Name Already Exist"))
                            // check if course offered uuid exists
                            .switchIfEmpty(Mono.defer(() -> courseOfferedRepository.findByUuidAndDeletedAtIsNull(sectionEntity.getCourseOfferedUUID())
                                    .flatMap(checkCourse -> {

                                        //check if min and max limit both are not null
                                        if (sectionEntity.getMin() != null && sectionEntity.getMax() != null) {
                                            //check if minimum limit is greater than max limit
                                            if (sectionEntity.getMin() > sectionEntity.getMax()) {
                                                return responseInfoMsg("Minimum Limit Must not be greater than max limit");
                                            }
                                        }

                                        return sectionRepository.save(sectionEntity)
                                                .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
                                    }).switchIfEmpty(responseInfoMsg("Course Offered record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course Offered record does not exist.Please Contact Developer"))
                            ));

                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sections_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID sectionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> sectionRepository.findByUuidAndDeletedAtIsNull(sectionUUID)
                        .flatMap(previousSectionEntity -> {

                            Integer minLimit = null;
                            if ((!Objects.equals(value.getFirst("min"), "")) && (!Objects.equals(value.getFirst("min"), null))) {
                                minLimit = Integer.valueOf(value.getFirst("min"));
                            }

                            Integer maxLimit = null;
                            if ((!Objects.equals(value.getFirst("max"), "")) && (!Objects.equals(value.getFirst("max"), null))) {
                                maxLimit = Integer.valueOf(value.getFirst("max"));
                            }


                            SectionEntity updatedSectionEntity = SectionEntity.builder()
                                    .uuid(previousSectionEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .courseOfferedUUID(UUID.fromString(value.getFirst("courseOfferedUUID").trim()))
                                    .min(minLimit)
                                    .max(maxLimit)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .createdBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousSectionEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousSectionEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousSectionEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousSectionEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousSectionEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousSectionEntity.getReqCreatedReferer())
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
                            previousSectionEntity.setDeletedBy(UUID.fromString(userId));
                            previousSectionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousSectionEntity.setReqDeletedIP(reqIp);
                            previousSectionEntity.setReqDeletedPort(reqPort);
                            previousSectionEntity.setReqDeletedBrowser(reqBrowser);
                            previousSectionEntity.setReqDeletedOS(reqOs);
                            previousSectionEntity.setReqDeletedDevice(reqDevice);
                            previousSectionEntity.setReqDeletedReferer(reqReferer);

                            //  check if section name is unique
                            return sectionRepository.findFirstByNameIgnoreCaseAndCourseOfferedUUIDAndDeletedAtIsNullAndUuidIsNot(updatedSectionEntity.getName(), updatedSectionEntity.getCourseOfferedUUID(), updatedSectionEntity.getUuid())
                                    .flatMap(ailmentEntity -> responseInfoMsg("Name Already Exist"))
                                    // check if course offered uuid exists
                                    .switchIfEmpty(Mono.defer(() -> courseOfferedRepository.findByUuidAndDeletedAtIsNull(updatedSectionEntity.getCourseOfferedUUID())
                                            .flatMap(checkCourse -> {

                                                //check if min and max limit both are not null
                                                if (updatedSectionEntity.getMin() != null && updatedSectionEntity.getMax() != null) {
                                                    //check if minimum limit is greater than max limit
                                                    if (updatedSectionEntity.getMin() > updatedSectionEntity.getMax()) {
                                                        return responseInfoMsg("Minimum Limit Must not be greater than max limit");
                                                    }
                                                }

                                                return sectionRepository.save(previousSectionEntity)
                                                        .then(sectionRepository.save(updatedSectionEntity))
                                                        .flatMap(saveEntity -> responseSuccessMsg("Record Updated Successfully", saveEntity))
                                                        .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                        .onErrorResume(err -> responseErrorMsg("Unable to Update record.Please Contact Developer."));
                                            }).switchIfEmpty(responseInfoMsg("Course Offered record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Course Offered record does not exist.Please Contact Developer"))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sections_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID sectionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return sectionRepository.findByUuidAndDeletedAtIsNull(sectionUUID)
                //check if section exists in section student table
                .flatMap(sectionEntity -> sectionStudentPvtRepository.findFirstBySectionUUIDAndDeletedAtIsNull(sectionEntity.getUuid())
                        .flatMap(checkInPvt -> responseInfoMsg("Unable to Delete Record as the Reference exists."))
                        //check if section exists in timetable
                        .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findFirstBySectionUUIDAndDeletedAtIsNull(sectionEntity.getUuid())
                                .flatMap(checkTimetable -> responseInfoMsg("Unable to Delete Record as the Reference exists."))
                        )).switchIfEmpty(Mono.defer(() -> {

                            sectionEntity.setDeletedBy(UUID.fromString(userId));
                            sectionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            sectionEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            sectionEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            sectionEntity.setReqDeletedIP(reqIp);
                            sectionEntity.setReqDeletedPort(reqPort);
                            sectionEntity.setReqDeletedBrowser(reqBrowser);
                            sectionEntity.setReqDeletedOS(reqOs);
                            sectionEntity.setReqDeletedDevice(reqDevice);
                            sectionEntity.setReqDeletedReferer(reqReferer);

                            return sectionRepository.save(sectionEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sections_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID sectionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return sectionRepository.findByUuidAndDeletedAtIsNull(sectionUUID)
                            .flatMap(previousSectionEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousSectionEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SectionEntity updatedSectionEntity = SectionEntity.builder()
                                        .uuid(previousSectionEntity.getUuid())
                                        .name(previousSectionEntity.getName())
                                        .description(previousSectionEntity.getDescription())
                                        .courseOfferedUUID(previousSectionEntity.getCourseOfferedUUID())
                                        .min(previousSectionEntity.getMin())
                                        .max(previousSectionEntity.getMax())
                                        .status(status == true ? true : false)
                                        .createdAt(previousSectionEntity.getCreatedAt())
                                        .createdBy(previousSectionEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousSectionEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousSectionEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousSectionEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousSectionEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousSectionEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousSectionEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousSectionEntity.setDeletedBy(UUID.fromString(userId));
                                previousSectionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousSectionEntity.setReqDeletedIP(reqIp);
                                previousSectionEntity.setReqDeletedPort(reqPort);
                                previousSectionEntity.setReqDeletedBrowser(reqBrowser);
                                previousSectionEntity.setReqDeletedOS(reqOs);
                                previousSectionEntity.setReqDeletedDevice(reqDevice);
                                previousSectionEntity.setReqDeletedReferer(reqReferer);

                                return sectionRepository.save(previousSectionEntity)
                                        .then(sectionRepository.save(updatedSectionEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
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
