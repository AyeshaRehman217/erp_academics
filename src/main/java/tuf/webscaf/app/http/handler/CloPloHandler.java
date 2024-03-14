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
import tuf.webscaf.app.dbContext.master.entity.*;
import tuf.webscaf.app.dbContext.master.entity.CloPloEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloPloDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloPloEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlavePloEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCloPloRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlavePloRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
@Tag(name = "cloPloHandler")
public class CloPloHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    CloPloRepository cloPloRepository;

    @Autowired
    SlaveCloPloRepository slaveCloPloRepository;

    @Autowired
    CloRepository cloRepository;

    @Autowired
    PloRepository ploRepository;

    @Autowired
    BloomsTaxonomyRepository bloomsTaxonomyRepository;

    @Autowired
    SubLearningTypeRepository subLearningTypeRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_clo-plos_index")
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

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Query Parameter of department UUID
        String departmentUUID = serverRequest.queryParam("departmentUUID").map(String::toString).orElse("").trim();


        if (!status.isEmpty() && !departmentUUID.isEmpty()) {
            Flux<SlaveCloPloDto> slaveCloPloFlux = slaveCloPloRepository
                    .indexWithDepartmentAndStatusFilter(UUID.fromString(departmentUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCloPloFlux
                    .collectList()
                    .flatMap(cloPloEntity -> slaveCloPloRepository.countCloPloRecordsWithDepartmentAndStatus
                                    (UUID.fromString(departmentUUID), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (cloPloEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", cloPloEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!departmentUUID.isEmpty()) {
            Flux<SlaveCloPloDto> slaveCloPloFlux = slaveCloPloRepository
                    .indexWithDepartmentFilter(UUID.fromString(departmentUUID), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCloPloFlux
                    .collectList()
                    .flatMap(cloPloEntity -> slaveCloPloRepository.countCloPloRecordsWithDepartmentUUID(UUID.fromString(departmentUUID), searchKeyWord)
                            .flatMap(count -> {
                                if (cloPloEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", cloPloEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveCloPloDto> slaveCloPloFlux = slaveCloPloRepository
                    .indexWithStatusFilter(Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCloPloFlux
                    .collectList()
                    .flatMap(cloPloEntity -> slaveCloPloRepository
                            .countCloPloRecordsAndStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (cloPloEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", cloPloEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCloPloDto> slaveCloPloFlux = slaveCloPloRepository
                    .index(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveCloPloFlux
                    .collectList()
                    .flatMap(cloPloEntity -> slaveCloPloRepository.countCloPloRecords(searchKeyWord)
                            .flatMap(count -> {
                                if (cloPloEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", cloPloEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_clo-plos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID cloPloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCloPloRepository.showByUUID(cloPloUUID)
                .flatMap(cloPloEntity -> responseSuccessMsg("Record Fetched Successfully", cloPloEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clo-plos_store")
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

                    CloPloEntity entity = CloPloEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .cloUUID(UUID.fromString(value.getFirst("cloUUID")))
                            .ploUUID(UUID.fromString(value.getFirst("ploUUID")))
                            .bloomTaxonomyUUID(UUID.fromString(value.getFirst("bloomTaxonomyUUID")))
                            .subLearningTypeUUID(UUID.fromString(value.getFirst("subLearningTypeUUID")))
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

                    //check clo exists
                    return cloRepository.findByUuidAndDeletedAtIsNull(entity.getCloUUID())
                            //check plo exists
                            .flatMap(cloEntity -> ploRepository.findByUuidAndDeletedAtIsNull(entity.getPloUUID())
                                    //check bloomsTaxonomy exists
                                    .flatMap(ploEntity -> bloomsTaxonomyRepository.findByUuidAndDeletedAtIsNull(entity.getBloomTaxonomyUUID())
                                            //check subLearningType exists
                                            .flatMap(bloomTaxonomyEntity -> subLearningTypeRepository.findByUuidAndDeletedAtIsNull(entity.getSubLearningTypeUUID())
                                                    .flatMap(subLearningTypeEntity -> cloPloRepository.findFirstByCloUUIDAndPloUUIDAndBloomTaxonomyUUIDAndSubLearningTypeUUIDAndDeletedAtIsNull
                                                                    (entity.getCloUUID(), entity.getPloUUID(), entity.getBloomTaxonomyUUID(), entity.getSubLearningTypeUUID())
                                                            .flatMap(checkPloCloBloomTaxonomyUnique -> responseInfoMsg("Sub Learning already exist against CLO, PLO and Blooms Taxonomy"))
                                                            .switchIfEmpty(Mono.defer(() -> {
                                                                if (!cloEntity.getDepartmentUUID().equals(ploEntity.getDepartmentUUID())) {
                                                                    return responseInfoMsg("Clo's department does not matches with Plo's Department");
                                                                } else {
                                                                    return cloPloRepository.save(entity)
                                                                            .flatMap(cloPloEntity -> responseSuccessMsg("Record Stored Successfully", cloPloEntity))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                                }
                                                            }))
                                                    ).switchIfEmpty(responseInfoMsg("SubLearningType record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("SubLearningType record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("BloomTaxonomy record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("BloomTaxonomy record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("PLO record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("PLO record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("CLO record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("CLO record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clo-plos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID cloPloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> cloPloRepository.findByUuidAndDeletedAtIsNull(cloPloUUID)
                        .flatMap(previousEntity -> {

                            CloPloEntity updatedEntity = CloPloEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .cloUUID(UUID.fromString(value.getFirst("cloUUID")))
                                    .ploUUID(UUID.fromString(value.getFirst("ploUUID")))
                                    .bloomTaxonomyUUID(UUID.fromString(value.getFirst("bloomTaxonomyUUID")))
                                    .subLearningTypeUUID(UUID.fromString(value.getFirst("subLearningTypeUUID")))
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

                            //check clo exists
                            return cloRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCloUUID())
                                    //check plo exists
                                    .flatMap(cloEntity -> ploRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getPloUUID())
                                            //check bloomsTaxonomy exists
                                            .flatMap(ploEntity -> bloomsTaxonomyRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getBloomTaxonomyUUID())
                                                    //check subLearningType exists
                                                    .flatMap(bloomTaxonomyEntity -> subLearningTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSubLearningTypeUUID())
                                                            .flatMap(subLearningTypeEntity -> cloPloRepository.findFirstByCloUUIDAndPloUUIDAndBloomTaxonomyUUIDAndSubLearningTypeUUIDAndDeletedAtIsNullAndUuidIsNot
                                                                            (updatedEntity.getCloUUID(), updatedEntity.getPloUUID(), updatedEntity.getBloomTaxonomyUUID(), updatedEntity.getSubLearningTypeUUID(), cloPloUUID)
                                                                    .flatMap(checkPloCloBloomTaxonomyUnique -> responseInfoMsg("Sub Learning already exist against CLO, PLO and Bloom Taxonomy"))
                                                                    .switchIfEmpty(Mono.defer(() -> {
                                                                                if (!cloEntity.getDepartmentUUID().equals(ploEntity.getDepartmentUUID())) {
                                                                                    return responseInfoMsg("Clo's department does not matches with Plo's Department");
                                                                                } else {
                                                                                    return cloPloRepository.save(previousEntity)
                                                                                            .then(cloPloRepository.save(updatedEntity))
                                                                                            .flatMap(cloPloEntity -> responseSuccessMsg("Record Updated Successfully", cloPloEntity))
                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                                                }
                                                                            }
                                                                    ))
                                                            ).switchIfEmpty(responseInfoMsg("SubLearningType record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("SubLearningType record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("BloomTaxonomy record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("BloomTaxonomy record does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("PLO record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("PLO record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("CLO record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("CLO record does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clo-plos_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID cloPloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return cloPloRepository.findByUuidAndDeletedAtIsNull(cloPloUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CloPloEntity entity = CloPloEntity.builder()
                                        .uuid(val.getUuid())
                                        .cloUUID(val.getCloUUID())
                                        .ploUUID(val.getPloUUID())
                                        .bloomTaxonomyUUID(val.getBloomTaxonomyUUID())
                                        .subLearningTypeUUID(val.getSubLearningTypeUUID())
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

                                return cloPloRepository.save(val)
                                        .then(cloPloRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clo-plos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID cloPloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return cloPloRepository.findByUuidAndDeletedAtIsNull(cloPloUUID)
                .flatMap(cloPloEntity -> {

                    cloPloEntity.setDeletedBy(UUID.fromString(userId));
                    cloPloEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    cloPloEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    cloPloEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    cloPloEntity.setReqDeletedIP(reqIp);
                    cloPloEntity.setReqDeletedPort(reqPort);
                    cloPloEntity.setReqDeletedBrowser(reqBrowser);
                    cloPloEntity.setReqDeletedOS(reqOs);
                    cloPloEntity.setReqDeletedDevice(reqDevice);
                    cloPloEntity.setReqDeletedReferer(reqReferer);

                    return cloPloRepository.save(cloPloEntity)
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
