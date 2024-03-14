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
import tuf.webscaf.app.dbContext.master.entity.CourseVisionAndMissionEntity;
import tuf.webscaf.app.dbContext.master.repositry.CourseRepository;
import tuf.webscaf.app.dbContext.master.repositry.CourseVisionAndMissionRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCourseVisionAndMissionEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCourseVisionAndMissionRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "courseVisionAndMissionHandler")
@Component
public class

CourseVisionAndMissionHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CourseVisionAndMissionRepository courseVisionAndMissionRepository;

    @Autowired
    SlaveCourseVisionAndMissionRepository slaveCourseVisionAndMissionRepository;

    @Autowired
    CourseRepository courseRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_course-vision-and-missions_index")
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

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();


        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // if status is present
        if (!status.isEmpty()) {
            Flux<SlaveCourseVisionAndMissionEntity> slaveCourseVisionAndMissionFlux = slaveCourseVisionAndMissionRepository
                    .findAllByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveCourseVisionAndMissionFlux
                    .collectList()
                    .flatMap(courseVisionAndMissionEntity -> slaveCourseVisionAndMissionRepository
                            .countByDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (courseVisionAndMissionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseVisionAndMissionEntity, count);
                                }
                            }))
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

        // if status is not present
        else {
            Flux<SlaveCourseVisionAndMissionEntity> slaveCourseVisionAndMissionFlux = slaveCourseVisionAndMissionRepository
                    .findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveCourseVisionAndMissionFlux
                    .collectList()
                    .flatMap(courseVisionAndMissionEntity -> slaveCourseVisionAndMissionRepository
                            .countByDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrVisionContainingIgnoreCaseAndDeletedAtIsNullOrMissionContainingIgnoreCaseAndDeletedAtIsNullOrObjectivesContainingIgnoreCaseAndDeletedAtIsNullOrOutcomesContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (courseVisionAndMissionEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", courseVisionAndMissionEntity, count);
                                }
                            }))
                    .switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_course-vision-and-missions_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID courseVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCourseVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(courseVisionAndMissionUUID)
                .flatMap(courseVisionAndMissionEntity -> responseSuccessMsg("Record Fetched Successfully", courseVisionAndMissionEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-vision-and-missions_store")
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

                    CourseVisionAndMissionEntity entity = CourseVisionAndMissionEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .description(value.getFirst("description").trim())
                            .vision(value.getFirst("vision").trim())
                            .mission(value.getFirst("mission").trim())
                            .objectives(value.getFirst("objectives").trim())
                            .outcomes(value.getFirst("outcomes").trim())
                            .courseUUID(UUID.fromString(value.getFirst("courseUUID")))
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

                    return courseRepository.findByUuidAndDeletedAtIsNull(entity.getCourseUUID())
                            .flatMap(courseEntity -> courseVisionAndMissionRepository.findFirstByVisionAndCourseUUIDAndDeletedAtIsNull(entity.getVision(), entity.getCourseUUID())
                                    .flatMap(checkVision -> responseInfoMsg("Vision already exist"))
                                    .switchIfEmpty(Mono.defer(() -> courseVisionAndMissionRepository.findFirstByMissionAndCourseUUIDAndDeletedAtIsNull(entity.getMission(), entity.getCourseUUID())
                                            .flatMap(checkMission -> responseInfoMsg("Mission already exist"))))
                                    .switchIfEmpty(Mono.defer(() -> courseVisionAndMissionRepository.save(entity)
                                            .flatMap(courseVisionAndMissionEntity -> responseSuccessMsg("Record Stored Successfully", courseVisionAndMissionEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                            ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-vision-and-missions_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID courseVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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
                .flatMap(value -> courseVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(courseVisionAndMissionUUID)
                        .flatMap(previousCourseVisionAndMissionEntity -> {
                            CourseVisionAndMissionEntity updatedEntity = CourseVisionAndMissionEntity.builder()
                                    .uuid(previousCourseVisionAndMissionEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .description(value.getFirst("description").trim())
                                    .vision(value.getFirst("vision").trim())
                                    .mission(value.getFirst("mission").trim())
                                    .objectives(value.getFirst("objectives").trim())
                                    .outcomes(value.getFirst("outcomes").trim())
                                    .courseUUID(UUID.fromString(value.getFirst("courseUUID")))
                                    .createdAt(previousCourseVisionAndMissionEntity.getCreatedAt())
                                    .createdBy(previousCourseVisionAndMissionEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousCourseVisionAndMissionEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousCourseVisionAndMissionEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousCourseVisionAndMissionEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousCourseVisionAndMissionEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousCourseVisionAndMissionEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousCourseVisionAndMissionEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousCourseVisionAndMissionEntity.setDeletedBy(UUID.fromString(userId));
                            previousCourseVisionAndMissionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousCourseVisionAndMissionEntity.setReqDeletedIP(reqIp);
                            previousCourseVisionAndMissionEntity.setReqDeletedPort(reqPort);
                            previousCourseVisionAndMissionEntity.setReqDeletedBrowser(reqBrowser);
                            previousCourseVisionAndMissionEntity.setReqDeletedOS(reqOs);
                            previousCourseVisionAndMissionEntity.setReqDeletedDevice(reqDevice);
                            previousCourseVisionAndMissionEntity.setReqDeletedReferer(reqReferer);

                            return courseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseUUID())
                                    .flatMap(courseEntity -> courseVisionAndMissionRepository.findFirstByVisionAndCourseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getVision(), updatedEntity.getCourseUUID(), courseVisionAndMissionUUID)
                                            .flatMap(checkVision -> responseInfoMsg("Vision already exist"))
                                            .switchIfEmpty(Mono.defer(() -> courseVisionAndMissionRepository.findFirstByMissionAndCourseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getMission(), updatedEntity.getCourseUUID(), courseVisionAndMissionUUID)
                                                    .flatMap(checkMission -> responseInfoMsg("Mission already exist"))))
                                            .switchIfEmpty(Mono.defer(() -> courseVisionAndMissionRepository.save(previousCourseVisionAndMissionEntity)
                                                    .then(courseVisionAndMissionRepository.save(updatedEntity))
                                                    .flatMap(courseVisionAndMissionEntity -> responseSuccessMsg("Record Stored Successfully", courseVisionAndMissionEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                    ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer"));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-vision-and-missions_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID courseVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return courseVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(courseVisionAndMissionUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CourseVisionAndMissionEntity courseVisionAndMissionEntity = CourseVisionAndMissionEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .vision(previousEntity.getVision())
                                        .mission(previousEntity.getMission())
                                        .description(previousEntity.getDescription())
                                        .objectives(previousEntity.getObjectives())
                                        .outcomes(previousEntity.getOutcomes())
                                        .courseUUID(previousEntity.getCourseUUID())
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

                                return courseVisionAndMissionRepository.save(previousEntity)
                                        .then(courseVisionAndMissionRepository.save(courseVisionAndMissionEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_course-vision-and-missions_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID courseVisionAndMissionUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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

        return courseVisionAndMissionRepository.findByUuidAndDeletedAtIsNull(courseVisionAndMissionUUID)
                //check If CourseVisionAndMission UUID Exists in peoCourseVisionAndMission
                .flatMap(courseVisionAndMissionEntity -> {

                            courseVisionAndMissionEntity.setDeletedBy(UUID.fromString(userId));
                            courseVisionAndMissionEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            courseVisionAndMissionEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            courseVisionAndMissionEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            courseVisionAndMissionEntity.setReqDeletedIP(reqIp);
                            courseVisionAndMissionEntity.setReqDeletedPort(reqPort);
                            courseVisionAndMissionEntity.setReqDeletedBrowser(reqBrowser);
                            courseVisionAndMissionEntity.setReqDeletedOS(reqOs);
                            courseVisionAndMissionEntity.setReqDeletedDevice(reqDevice);
                            courseVisionAndMissionEntity.setReqDeletedReferer(reqReferer);

                            return courseVisionAndMissionRepository.save(courseVisionAndMissionEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }
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
