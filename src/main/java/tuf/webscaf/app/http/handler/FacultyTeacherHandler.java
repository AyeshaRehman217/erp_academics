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
import tuf.webscaf.app.dbContext.master.entity.FacultyTeacherEntity;
import tuf.webscaf.app.dbContext.master.repositry.FacultyRepository;
import tuf.webscaf.app.dbContext.master.repositry.FacultyTeacherRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveFacultyTeacherEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveFacultyTeacherRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "facultyTeacherHandler")
@Component
public class FacultyTeacherHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    FacultyTeacherRepository facultyTeacherRepository;

    @Autowired
    SlaveFacultyTeacherRepository slaveFacultyTeacherRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_student_financial_module.uri}")
    private String studentFinancialUri;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.zone}")
    private String zone;


    @AuthHasPermission(value = "academic_api_v1_faculty-teachers_index")
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

        // faculty Query Parameter
        String facultyUUID = serverRequest.queryParam("facultyUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !facultyUUID.isEmpty()) {
            Flux<SlaveFacultyTeacherEntity> slaveFacultyTeacherPvtEntityFlux = slaveFacultyTeacherRepository
                    .findAllByFacultyUUIDAndStatusAndDeletedAtIsNull(pageable, UUID.fromString(facultyUUID), Boolean.valueOf(status));
            return slaveFacultyTeacherPvtEntityFlux
                    .collectList()
                    .flatMap(facultyTeacherPvtEntities -> slaveFacultyTeacherRepository.countAllByFacultyUUIDAndStatusAndDeletedAtIsNull(UUID.fromString(facultyUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (facultyTeacherPvtEntities.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyTeacherPvtEntities, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!facultyUUID.isEmpty()) {
            Flux<SlaveFacultyTeacherEntity> slaveFacultyTeacherPvtEntityFlux = slaveFacultyTeacherRepository
                    .findAllByFacultyUUIDAndDeletedAtIsNull(pageable, UUID.fromString(facultyUUID));
            return slaveFacultyTeacherPvtEntityFlux
                    .collectList()
                    .flatMap(facultyTeacherPvtEntities -> slaveFacultyTeacherRepository.countAllByFacultyUUIDAndDeletedAtIsNull(UUID.fromString(facultyUUID))
                            .flatMap(count -> {
                                if (facultyTeacherPvtEntities.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyTeacherPvtEntities, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveFacultyTeacherEntity> slaveFacultyTeacherPvtEntityFlux = slaveFacultyTeacherRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));
            return slaveFacultyTeacherPvtEntityFlux
                    .collectList()
                    .flatMap(facultyTeacherPvtEntities -> slaveFacultyTeacherRepository.countAllByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (facultyTeacherPvtEntities.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyTeacherPvtEntities, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveFacultyTeacherEntity> slaveFacultyTeacherPvtEntityFlux = slaveFacultyTeacherRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveFacultyTeacherPvtEntityFlux
                    .collectList()
                    .flatMap(facultyTeacherPvtEntities -> slaveFacultyTeacherRepository.countAllByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (facultyTeacherPvtEntities.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyTeacherPvtEntities, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }


    @AuthHasPermission(value = "academic_api_v1_faculty-teachers_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID facultyTeacherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveFacultyTeacherRepository.findByUuidAndDeletedAtIsNull(facultyTeacherUUID)
                .flatMap(facultyTeacherPvtEntity -> responseSuccessMsg("Record Fetched Successfully", facultyTeacherPvtEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_faculty-teachers_store")
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

                    FacultyTeacherEntity facultyTeacherEntity = FacultyTeacherEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .facultyUUID(UUID.fromString(value.getFirst("facultyUUID").trim()))
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
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

                    //check if teacher Already exists against this faculty
                    return facultyTeacherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(facultyTeacherEntity.getTeacherUUID())
                            .flatMap(checkTeacherExist -> responseInfoMsg("Teacher Against this faculty Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> facultyRepository.findByUuidAndDeletedAtIsNull(facultyTeacherEntity.getFacultyUUID())
                                    .flatMap(facultyEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(facultyTeacherEntity.getTeacherUUID())
                                            .flatMap(teacherEntity -> facultyTeacherRepository.save(facultyTeacherEntity)
                                                    .flatMap(facultyTeacher -> responseSuccessMsg("Record Stored Successfully", facultyTeacherEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Teacher does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Teacher does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Faculty does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Faculty does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_faculty-teachers_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID facultyTeacherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> facultyTeacherRepository.findByUuidAndDeletedAtIsNull(facultyTeacherUUID)
                        .flatMap(previousEntity -> {

                            FacultyTeacherEntity updatedEntity = FacultyTeacherEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .facultyUUID(UUID.fromString(value.getFirst("facultyUUID").trim()))
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
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

                            //check if teacher Already exists against this faculty
                            return facultyTeacherRepository.findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getUuid())
                                    .flatMap(checkTeacherExist -> responseInfoMsg("Teacher Against this faculty Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> facultyRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getFacultyUUID())
                                            .flatMap(facultyEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                                    .flatMap(teacherEntity -> facultyTeacherRepository.save(previousEntity)
                                                            .then(facultyTeacherRepository.save(updatedEntity))
                                                            .flatMap(facultyTeacherEntity -> responseSuccessMsg("Record Updated Successfully", facultyTeacherEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Teacher does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Teacher does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Faculty does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Faculty does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_faculty-teachers_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID facultyTeacherUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return facultyTeacherRepository.findByUuidAndDeletedAtIsNull(facultyTeacherUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                FacultyTeacherEntity updatedFacultyTeacherEntity = FacultyTeacherEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .facultyUUID(previousEntity.getFacultyUUID())
                                        .teacherUUID(previousEntity.getTeacherUUID())
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

                                return facultyTeacherRepository.save(previousEntity)
                                        .then(facultyTeacherRepository.save(updatedFacultyTeacherEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_faculty-teachers_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID cloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return facultyTeacherRepository.findByUuidAndDeletedAtIsNull(cloUUID)
                .flatMap(facultyTeacherEntity -> {

                    facultyTeacherEntity.setDeletedBy(UUID.fromString(userId));
                    facultyTeacherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    facultyTeacherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    facultyTeacherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    facultyTeacherEntity.setReqDeletedIP(reqIp);
                    facultyTeacherEntity.setReqDeletedPort(reqPort);
                    facultyTeacherEntity.setReqDeletedBrowser(reqBrowser);
                    facultyTeacherEntity.setReqDeletedOS(reqOs);
                    facultyTeacherEntity.setReqDeletedDevice(reqDevice);
                    facultyTeacherEntity.setReqDeletedReferer(reqReferer);

                    return facultyTeacherRepository.save(facultyTeacherEntity)
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
