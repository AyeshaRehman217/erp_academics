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
import tuf.webscaf.app.dbContext.master.entity.FacultyEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveFacultyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveFacultyRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.SlugifyHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "facultyHandler")
@Component
public class FacultyHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    SlaveFacultyRepository slaveFacultyRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    FacultyVisionAndMissionRepository facultyVisionAndMissionRepository;

    @Autowired
    SlugifyHelper slugifyHelper;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_faculties_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID").trim();

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!reqBranchUUID.isEmpty() && !status.isEmpty()) {
            Flux<SlaveFacultyEntity> slaveFacultyFlux = slaveFacultyRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), UUID.fromString(reqBranchUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(reqBranchUUID));

            return slaveFacultyFlux
                    .collectList()
                    .flatMap(facultyEntity -> slaveFacultyRepository
                            .countByNameContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndCampusUUIDAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), UUID.fromString(reqBranchUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(reqBranchUUID))
                            .flatMap(count -> {
                                if (facultyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!reqBranchUUID.isEmpty()) {
            Flux<SlaveFacultyEntity> slaveFacultyFlux = slaveFacultyRepository
                    .findAllByNameContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(reqBranchUUID), searchKeyWord, UUID.fromString(reqBranchUUID));

            return slaveFacultyFlux
                    .collectList()
                    .flatMap(facultyEntity -> slaveFacultyRepository
                            .countByNameContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndCampusUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(reqBranchUUID), searchKeyWord, UUID.fromString(reqBranchUUID))
                            .flatMap(count -> {
                                if (facultyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveFacultyEntity> slaveFacultyFlux = slaveFacultyRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveFacultyFlux
                    .collectList()
                    .flatMap(facultyEntity -> slaveFacultyRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (facultyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveFacultyEntity> slaveFacultyFlux = slaveFacultyRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);

            return slaveFacultyFlux
                    .collectList()
                    .flatMap(facultyEntity -> slaveFacultyRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (facultyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", facultyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_faculties_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID facultyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveFacultyRepository.findByUuidAndDeletedAtIsNull(facultyUUID)
                .flatMap(facultyEntity -> responseSuccessMsg("Record Fetched Successfully", facultyEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_faculties_store")
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

                    FacultyEntity entity = FacultyEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description"))
                            .companyUUID(UUID.fromString(reqCompanyUUID))
                            .campusUUID(UUID.fromString(reqBranchUUID))
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

                    return facultyRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
                            .flatMap(name -> responseInfoMsg("Name already exist"))
                            .switchIfEmpty(Mono.defer(() -> facultyRepository.save(entity)
                                    .flatMap(facultyEntity -> responseSuccessMsg("Record Stored Successfully", facultyEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseInfoMsg("Unable to store record. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please contact developer."));

    }

    @AuthHasPermission(value = "academic_api_v1_faculties_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID facultyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> facultyRepository.findByUuidAndDeletedAtIsNull(facultyUUID)
                                .flatMap(entity -> {

                                    FacultyEntity updatedEntity = FacultyEntity.builder()
                                            .uuid(entity.getUuid())
                                            .name(value.getFirst("name").trim())
                                            .description(value.getFirst("description").trim())
                                            .companyUUID(UUID.fromString(reqCompanyUUID))
                                            .campusUUID(UUID.fromString(reqBranchUUID))
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

                                    return facultyRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), facultyUUID)
                                            .flatMap(Name -> responseInfoMsg("Name already exist"))
                                            .switchIfEmpty(Mono.defer(() -> facultyRepository.save(entity)
                                                            .then(facultyRepository.save(updatedEntity))
                                                            .flatMap(facultyEntity -> responseSuccessMsg("Record Updated Successfully", facultyEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record.Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request"));
    }

    @AuthHasPermission(value = "academic_api_v1_faculties_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID facultyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return facultyRepository.findByUuidAndDeletedAtIsNull(facultyUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                FacultyEntity entity = FacultyEntity.builder()
                                        .uuid(val.getUuid())
                                        .name(val.getName())
                                        .description(val.getDescription())
                                        .companyUUID(val.getCompanyUUID())
                                        .campusUUID(val.getCampusUUID())
                                        .status(status == true ? true : false)
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

                                return facultyRepository.save(val)
                                        .then(facultyRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_faculties_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID facultyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

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

        return facultyRepository.findByUuidAndDeletedAtIsNull(facultyUUID)
                //Check if faculty is used by Departments
                .flatMap(facultyEntity -> departmentRepository.findFirstByFacultyUUIDAndDeletedAtIsNull(facultyEntity.getUuid())
                                .flatMap(checkMsg -> responseInfoMsg("Unable to Delete Record as the Reference"))
//                 Check if faculty Reference exists in faculty vision and mission
                                .switchIfEmpty(Mono.defer(() -> facultyVisionAndMissionRepository.findFirstByFacultyUUIDAndDeletedAtIsNull(facultyEntity.getUuid())
                                        .flatMap(semesterEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists"))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    facultyEntity.setDeletedBy(UUID.fromString(userId));
                                    facultyEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    facultyEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    facultyEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    facultyEntity.setReqDeletedIP(reqIp);
                                    facultyEntity.setReqDeletedPort(reqPort);
                                    facultyEntity.setReqDeletedBrowser(reqBrowser);
                                    facultyEntity.setReqDeletedOS(reqOs);
                                    facultyEntity.setReqDeletedDevice(reqDevice);
                                    facultyEntity.setReqDeletedReferer(reqReferer);

                                    return facultyRepository.save(facultyEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                                }))

                )
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
