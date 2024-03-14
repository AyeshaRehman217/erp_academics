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
import tuf.webscaf.app.dbContext.master.entity.CourseOfferedEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCourseOfferedDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseOfferedRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "courseOfferedHandler")
@Component
public class CourseOfferedHandler {
    @Autowired
    CustomResponse appresponse;
    @Autowired
    CourseOfferedRepository courseOfferedRepository;
    @Autowired
    SlaveCourseOfferedRepository slaveCourseOfferedRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    SlaveCourseRepository slaveCourseRepository;
    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;
    @Autowired
    CourseBatchRepository courseBatchRepository;
    @Autowired
    AcademicSessionRepository academicSessionRepository;
    @Autowired
    CampusCourseRepository campusCourseRepository;
    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_course-offered_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        String campusUUID = serverRequest.queryParam("campusUUID").map(String::toString).orElse("").trim();

        String academicSessionUUID = serverRequest.queryParam("academicSessionUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty() && !status.isEmpty()) {

            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithStatusSessionAndCampus(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllCourseOfferedWithCampusAcademicSessionAndStatusFilter(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));

        } else if (!academicSessionUUID.isEmpty() && !campusUUID.isEmpty()) {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithCampusAndSessionFilter(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllCourseOfferedWithSessionCampusFilter(UUID.fromString(campusUUID), UUID.fromString(academicSessionUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!academicSessionUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithStatusAndSession(UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllCourseOfferedWithAcademicSessionAndStatusFilter(UUID.fromString(academicSessionUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!campusUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithStatusAndCampus(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllCourseOfferedWithCampusAndStatusFilter(UUID.fromString(campusUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!academicSessionUUID.isEmpty()) {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithSessionFilter(UUID.fromString(academicSessionUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllCourseOfferedWithAcademicSessionFilter(UUID.fromString(academicSessionUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!campusUUID.isEmpty()) {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithCampusFilter(UUID.fromString(campusUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllCourseOfferedWithCampusFilter(UUID.fromString(campusUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndexWithStatus(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository
                            .countAllByDeletedAtIsNullAndStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCourseOfferedDto> slaveCourseOfferedFlux = slaveCourseOfferedRepository
                    .courseOfferedIndex(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCourseOfferedFlux
                    .collectList()
                    .flatMap(courseSubjectEntity -> slaveCourseOfferedRepository.countAllByDeletedAtIsNull(searchKeyWord)
                            .flatMap(count -> {
                                if (courseSubjectEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseSubjectEntity, count);
                                }
                            })
                    )
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }


    //Check Course Offered Exists Used By Student Financial Module in Student Group
    public Mono<ServerResponse> showCourseOfferedExistence(ServerRequest serverRequest) {
        final UUID courseOfferedUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return courseOfferedRepository.findByUuidAndDeletedAtIsNull(courseOfferedUUID)
                .flatMap(courseOfferedEntity -> responseSuccessMsg("Record Fetched Successfully", courseOfferedEntity))
                .switchIfEmpty(responseInfoMsg("Course Offered Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Course Offered Does not exist.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_course-offered_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID courseOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCourseOfferedRepository.findByUuidAndDeletedAtIsNull(courseOfferedUUID)
                .flatMap(courseOfferedEntity -> responseSuccessMsg("Record Fetched Successfully", courseOfferedEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-offered_store")
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

                    CourseOfferedEntity entity = CourseOfferedEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID")))
                            .campusCourseUUID(UUID.fromString(value.getFirst("campusCourseUUID")))
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

                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(entity.getAcademicSessionUUID())
                            .flatMap(academicSessionEntity -> campusCourseRepository.findByUuidAndDeletedAtIsNull(entity.getCampusCourseUUID())
                                    .flatMap(campusCourseEntity -> courseOfferedRepository
                                            .findFirstByAcademicSessionUUIDAndCampusCourseUUIDAndDeletedAtIsNull(entity.getAcademicSessionUUID(), entity.getCampusCourseUUID())
                                            .flatMap(academicSessionAndCampusCourse -> responseInfoMsg("Course already Offered in this Academic Session"))
                                            .switchIfEmpty(Mono.defer(() -> courseOfferedRepository.save(entity)
                                                    .flatMap(courseOfferedEntity -> responseSuccessMsg("Record Stored Successfully", courseOfferedEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Campus Course does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Campus Course does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-offered_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID courseOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> courseOfferedRepository.findByUuidAndDeletedAtIsNull(courseOfferedUUID)
                        .flatMap(entity -> {
                            CourseOfferedEntity updatedEntity = CourseOfferedEntity.builder()
                                    .uuid(entity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID")))
                                    .campusCourseUUID(UUID.fromString(value.getFirst("campusCourseUUID")))
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

                            return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAcademicSessionUUID())
                                    .flatMap(academicSessionEntity -> campusCourseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCampusCourseUUID())
                                            .flatMap(campusCourseEntity -> courseOfferedRepository
                                                    .findFirstByAcademicSessionUUIDAndCampusCourseUUIDAndDeletedAtIsNullAndUuidIsNot(entity.getAcademicSessionUUID(), entity.getCampusCourseUUID(), courseOfferedUUID)
                                                    .flatMap(academicSessionAndCampusCourse -> responseInfoMsg("Course already Offered in this Academic Session"))
                                                    .switchIfEmpty(Mono.defer(() -> courseOfferedRepository.save(entity)
                                                            .then(courseOfferedRepository.save(updatedEntity))
                                                            .flatMap(courseOfferedEntity -> responseSuccessMsg("Record Updated Successfully", courseOfferedEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))))
                                            ).switchIfEmpty(responseInfoMsg("Campus Course does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Campus Course does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-offered_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID courseOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return courseOfferedRepository.findByUuidAndDeletedAtIsNull(courseOfferedUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CourseOfferedEntity courseOfferedEntity = CourseOfferedEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .academicSessionUUID(previousEntity.getAcademicSessionUUID())
                                        .campusCourseUUID(previousEntity.getCampusCourseUUID())
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

                                return courseOfferedRepository.save(previousEntity)
                                        .then(courseOfferedRepository.save(courseOfferedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_course-offered_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID courseOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return courseOfferedRepository.findByUuidAndDeletedAtIsNull(courseOfferedUUID)
                .flatMap(courseOfferedEntity -> {

                    courseOfferedEntity.setDeletedBy(UUID.fromString(userId));
                    courseOfferedEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    courseOfferedEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    courseOfferedEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    courseOfferedEntity.setReqDeletedIP(reqIp);
                    courseOfferedEntity.setReqDeletedPort(reqPort);
                    courseOfferedEntity.setReqDeletedBrowser(reqBrowser);
                    courseOfferedEntity.setReqDeletedOS(reqOs);
                    courseOfferedEntity.setReqDeletedDevice(reqDevice);
                    courseOfferedEntity.setReqDeletedReferer(reqReferer);

                    return courseOfferedRepository.save(courseOfferedEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
