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
import tuf.webscaf.app.dbContext.master.entity.AddressTypeEntity;
import tuf.webscaf.app.dbContext.master.entity.DepartmentRankCatalogueEntity;
import tuf.webscaf.app.dbContext.master.entity.DepartmentRankEntity;
import tuf.webscaf.app.dbContext.master.repositry.DepartmentRankCatalogueRepository;
import tuf.webscaf.app.dbContext.master.repositry.DepartmentRankRepository;
import tuf.webscaf.app.dbContext.master.repositry.DepartmentRepository;
import tuf.webscaf.app.dbContext.master.repositry.TeacherRepository;
import tuf.webscaf.app.dbContext.slave.dto.SlaveDepartmentRankDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentRankCatalogueEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveDepartmentRankCatalogueRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveDepartmentRankRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "departmentRankHandler")
@Component
public class DepartmentRankHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    DepartmentRankRepository departmentRankRepository;

    @Autowired
    SlaveDepartmentRankRepository slaveDepartmentRankRepository;

    @Autowired
    DepartmentRankCatalogueRepository departmentRankCatalogueRepository;

    @Autowired
    SlaveDepartmentRankCatalogueRepository slaveDepartmentRankCatalogueRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_department-ranks_index")
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        //Optional Query Parameter of Many
        String many = serverRequest.queryParam("many").map(String::toString).orElse("").trim();

        if (!many.isEmpty()) {
            Flux<SlaveDepartmentRankDto> slaveDeptRankFlux = slaveDepartmentRankRepository
                    .showAllRecordsWithNameAndManyFilter(searchKeyWord, Boolean.valueOf(many), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveDeptRankFlux
                    .collectList()
                    .flatMap(deptRankEntity -> slaveDepartmentRankRepository.countAllRecordsWithNameAndManyFilter(searchKeyWord, Boolean.valueOf(many))
                            .flatMap(count -> {
                                if (deptRankEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", deptRankEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        } else {
            Flux<SlaveDepartmentRankDto> slaveDeptRankFlux = slaveDepartmentRankRepository
                    .showAllRecordsWithName(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveDeptRankFlux
                    .collectList()
                    .flatMap(deptRankEntity -> slaveDepartmentRankRepository.countAllRecordsWithName(searchKeyWord)
                            .flatMap(count -> {
                                if (deptRankEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", deptRankEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_department-ranks_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID deptRankUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveDepartmentRankRepository.showRecordWithName(deptRankUUID)
                .flatMap(departmentRank -> responseSuccessMsg("Record Fetched Successfully", departmentRank))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-ranks_store")
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

                    // max count of teachers to be ranked
                    Integer max = null;
                    if (value.containsKey("max")) {
                        // if max is not empty, then parse the value to integer
                        if (!value.getFirst("max").isEmpty()) {
                            max = Integer.valueOf(value.getFirst("max"));
                        }
                    }

                    // min count of teachers to be ranked
                    Integer min = null;
                    if (value.containsKey("min")) {
                        // if max is not empty, then parse the value to integer
                        if (!value.getFirst("min").isEmpty()) {
                            min = Integer.valueOf(value.getFirst("min"));
                        }
                    }

                    DepartmentRankEntity entity = DepartmentRankEntity.builder()
                            .uuid(UUID.randomUUID())
                            .departmentUUID(UUID.fromString(value.getFirst("departmentUUID").trim()))
                            .deptRankCatalogueUUID(UUID.fromString(value.getFirst("deptRankCatalogueUUID").trim()))
                            .many(Boolean.valueOf(value.getFirst("many")))
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .max(max)
                            .min(min)
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


                    // if max and min are given but many is false
                    if (entity.getMax() != null && entity.getMin() != null) {
                        if (!entity.getMany()) {
                            return responseInfoMsg("Max and Min Fields are allowed only when many is true");
                        } else {
                            // if min is greater than max
                            if (entity.getMax() < entity.getMin()) {
                                return responseInfoMsg("Max must be greater than Min");
                            }
                        }
                    }

                    // check if department uuid exists
                    return departmentRepository.findByUuidAndDeletedAtIsNull(entity.getDepartmentUUID())
                            // check if department rank catalogue uuid exists
                            .flatMap(departmentEntity -> departmentRankCatalogueRepository.findByUuidAndDeletedAtIsNull(entity.getDeptRankCatalogueUUID())
                                    // check if record already exists
                                    .flatMap(departmentRankCatalogueEntity -> departmentRankRepository.findFirstByDepartmentUUIDAndDeptRankCatalogueUUIDAndDeletedAtIsNull(entity.getDepartmentUUID(), entity.getDeptRankCatalogueUUID())
                                            .flatMap(departmentRankEntity -> responseInfoMsg("Record Already Exist"))
                                            .switchIfEmpty(Mono.defer(() -> departmentRankRepository.save(entity)
                                                    .flatMap(departmentRankEntity -> responseSuccessMsg("Record Stored Successfully", departmentRankEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.  Please contact developer."))
                                            ))
                                    ).switchIfEmpty(responseInfoMsg("Department Rank Catalogue Record does not exist"))
                                    .onErrorResume(err -> responseInfoMsg("Department Rank Catalogue Record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Department Record does not exist"))
                            .onErrorResume(err -> responseInfoMsg("Department Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-ranks_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID departmentRankUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> departmentRankRepository.findByUuidAndDeletedAtIsNull(departmentRankUUID)
                        .flatMap(entity -> {

                            // max count of teachers to be ranked
                            Integer max = null;
                            if (value.containsKey("max")) {
                                // if max is not empty, then parse the value to integer
                                if (!value.getFirst("max").isEmpty()) {
                                    max = Integer.valueOf(value.getFirst("max"));
                                }
                            }

                            // min count of teachers to be ranked
                            Integer min = null;
                            if (value.containsKey("min")) {
                                // if max is not empty, then parse the value to integer
                                if (!value.getFirst("min").isEmpty()) {
                                    min = Integer.valueOf(value.getFirst("min"));
                                }
                            }

                            DepartmentRankEntity updatedEntity = DepartmentRankEntity.builder()
                                    .uuid(entity.getUuid())
                                    .departmentUUID(UUID.fromString(value.getFirst("departmentUUID").trim()))
                                    .deptRankCatalogueUUID(UUID.fromString(value.getFirst("deptRankCatalogueUUID").trim()))
                                    .many(Boolean.valueOf(value.getFirst("many")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .max(max)
                                    .min(min)
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

                            // if max and min are given but many is false
                            if (updatedEntity.getMax() != null && updatedEntity.getMin() != null) {
                                if (!updatedEntity.getMany()) {
                                    return responseInfoMsg("Max and Min Fields are allowed only when many is true");
                                } else {
                                    // if min is greater than max
                                    if (updatedEntity.getMax() < updatedEntity.getMin()) {
                                        return responseInfoMsg("Max must be greater than Min");
                                    }
                                }
                            }

                            // check if department uuid exists
                            return departmentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDepartmentUUID())
                                    // check if department rank catalogue uuid exists
                                    .flatMap(departmentEntity -> departmentRankCatalogueRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDeptRankCatalogueUUID())
                                            // check if record already exists
                                            .flatMap(departmentRankCatalogueEntity -> departmentRankRepository.findFirstByDepartmentUUIDAndDeptRankCatalogueUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getDepartmentUUID(), updatedEntity.getDeptRankCatalogueUUID(), departmentRankUUID)
                                                    .flatMap(nameExists -> responseInfoMsg("Record Already Exists"))
                                                    .switchIfEmpty(Mono.defer(() -> departmentRankRepository.save(entity)
                                                            .then(departmentRankRepository.save(updatedEntity))
                                                            .flatMap(genderEntity -> responseSuccessMsg("Record Updated Successfully", genderEntity))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                    ))
                                            ).switchIfEmpty(responseInfoMsg("Department Rank Catalogue Record does not exist"))
                                            .onErrorResume(err -> responseInfoMsg("Department Rank Catalogue Record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Department Record does not exist"))
                                    .onErrorResume(err -> responseInfoMsg("Department Record does not exist. Please contact developer."));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-ranks_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID departmentRankUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return departmentRankRepository.findByUuidAndDeletedAtIsNull(departmentRankUUID)
                .flatMap(deptRankEntity -> teacherRepository.findFirstByDeptRankUUIDAndDeletedAtIsNull(deptRankEntity.getUuid())
                        .flatMap(checkMessage -> responseInfoMsg("Unable To Delete Record as the reference exist"))
                        .switchIfEmpty(Mono.defer(() -> {

                            deptRankEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            deptRankEntity.setDeletedBy(UUID.fromString(userId));
                            deptRankEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            deptRankEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            deptRankEntity.setReqDeletedIP(reqIp);
                            deptRankEntity.setReqDeletedPort(reqPort);
                            deptRankEntity.setReqDeletedBrowser(reqBrowser);
                            deptRankEntity.setReqDeletedOS(reqOs);
                            deptRankEntity.setReqDeletedDevice(reqDevice);
                            deptRankEntity.setReqDeletedReferer(reqReferer);

                            return departmentRankRepository.save(deptRankEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record"))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer"));
                        }))
                )
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_department-ranks_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID deptRankUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

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

                    return departmentRankRepository.findByUuidAndDeletedAtIsNull(deptRankUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                DepartmentRankEntity updatedEntity = DepartmentRankEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .departmentUUID(previousEntity.getDepartmentUUID())
                                        .deptRankCatalogueUUID(previousEntity.getDeptRankCatalogueUUID())
                                        .many(previousEntity.getMany())
                                        .status(status == true ? true : false)
                                        .max(previousEntity.getMax())
                                        .min(previousEntity.getMin())
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

                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return departmentRankRepository.save(previousEntity)
                                        .then(departmentRankRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
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

    public Mono<ServerResponse> responseInfoMsg(String msg, Object entity) {
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
                entity
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
