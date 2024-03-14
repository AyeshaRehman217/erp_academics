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
import tuf.webscaf.app.dbContext.master.entity.SubLearningTypeEntity;
import tuf.webscaf.app.dbContext.master.repositry.BloomsTaxonomyRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubLearningTypeRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubLearningTypeEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubLearningTypeRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "bloomsTaxonomySubLearningTypeHandler")
@Component
public class SubLearningTypeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubLearningTypeRepository subLearningTypesRepository;

    @Autowired
    SlaveSubLearningTypeRepository slaveSubLearningTypeRepository;

    @Autowired
    BloomsTaxonomyRepository bloomsTaxonomyRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_sub-learning-types_index")
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

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Bloom Taxonomy
        String bloomTaxonomyUUID = serverRequest.queryParam("bloomTaxonomyUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !bloomTaxonomyUUID.isEmpty()) {
            Flux<SlaveSubLearningTypeEntity> slaveLearningTypeFlux = slaveSubLearningTypeRepository
                    .findAllByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(bloomTaxonomyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(bloomTaxonomyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(bloomTaxonomyUUID), Boolean.valueOf(status));
            return slaveLearningTypeFlux
                    .collectList()
                    .flatMap(learningTypeEntity -> slaveSubLearningTypeRepository.countByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(bloomTaxonomyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(bloomTaxonomyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(bloomTaxonomyUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (learningTypeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", learningTypeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!bloomTaxonomyUUID.isEmpty()) {
            Flux<SlaveSubLearningTypeEntity> slaveLearningTypeFlux = slaveSubLearningTypeRepository
                    .findAllByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(bloomTaxonomyUUID), searchKeyWord, UUID.fromString(bloomTaxonomyUUID), searchKeyWord, UUID.fromString(bloomTaxonomyUUID));
            return slaveLearningTypeFlux
                    .collectList()
                    .flatMap(learningTypeEntity -> slaveSubLearningTypeRepository.countByNameContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndBloomTaxonomyUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(bloomTaxonomyUUID), searchKeyWord, UUID.fromString(bloomTaxonomyUUID), searchKeyWord, UUID.fromString(bloomTaxonomyUUID))
                            .flatMap(count -> {
                                if (learningTypeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", learningTypeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveSubLearningTypeEntity> slaveLearningTypeFlux = slaveSubLearningTypeRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveLearningTypeFlux
                    .collectList()
                    .flatMap(learningTypeEntity -> slaveSubLearningTypeRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (learningTypeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", learningTypeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubLearningTypeEntity> slaveLearningTypeFlux = slaveSubLearningTypeRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveLearningTypeFlux
                    .collectList()
                    .flatMap(learningTypeEntity -> slaveSubLearningTypeRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrCodeContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (learningTypeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", learningTypeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_sub-learning-types_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subLearningTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubLearningTypeRepository.findByUuidAndDeletedAtIsNull(subLearningTypeUUID)
                .flatMap(learningTypeEntity -> responseSuccessMsg("Record Fetched Successfully", learningTypeEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sub-learning-types_store")
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

                    SubLearningTypeEntity subLearningTypeDB = SubLearningTypeEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .bloomTaxonomyUUID(UUID.fromString(value.getFirst("bloomTaxonomyUUID").trim()))
                            .name(value.getFirst("name").trim())
                            .code(value.getFirst("code").trim())
                            .description(value.getFirst("description").trim())
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

                    //check if Name is Unique
                    return subLearningTypesRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(subLearningTypeDB.getName())
                            .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
                            //check if Code is Unique
                            .switchIfEmpty(Mono.defer(() -> subLearningTypesRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNull(subLearningTypeDB.getCode())
                                    .flatMap(checkCode -> responseInfoMsg("Code Already Exist"))
                            ))
                            .switchIfEmpty(Mono.defer(() -> bloomsTaxonomyRepository.findByUuidAndDeletedAtIsNull(subLearningTypeDB.getBloomTaxonomyUUID())
                                    .flatMap(bloomTaxonomy -> subLearningTypesRepository.save(subLearningTypeDB)
                                            .flatMap(subLearningTypeEntity1 -> responseSuccessMsg("Record Stored Successfully", subLearningTypeEntity1))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    )
                                    .switchIfEmpty(responseInfoMsg("Bloom's taxonomy Does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Bloom's Taxonomy Does not exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sub-learning-types_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subLearningTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subLearningTypesRepository.findByUuidAndDeletedAtIsNull(subLearningTypeUUID)
                        .flatMap(previousEntity -> {

                            SubLearningTypeEntity updatedEntity = SubLearningTypeEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .bloomTaxonomyUUID(UUID.fromString(value.getFirst("bloomTaxonomyUUID").trim()))
                                    .name(value.getFirst("name").trim())
                                    .code(value.getFirst("code").trim())
                                    .description(value.getFirst("description").trim())
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

                            //check if Name is Unique
                            return subLearningTypesRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), subLearningTypeUUID)
                                    .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
                                    //check if Code is Unique
                                    .switchIfEmpty(Mono.defer(() -> subLearningTypesRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCode(), subLearningTypeUUID)
                                            .flatMap(checkCode -> responseInfoMsg("Code Already Exist"))
                                    ))
                                    .switchIfEmpty(Mono.defer(() -> bloomsTaxonomyRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getBloomTaxonomyUUID())
                                            .flatMap(bloomTaxonomy -> subLearningTypesRepository.save(previousEntity)
                                                    .then(subLearningTypesRepository.save(updatedEntity))
                                                    .flatMap(subLearningTypeEntity1 -> responseSuccessMsg("Record Updated Successfully", subLearningTypeEntity1))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."))
                                            )
                                            .switchIfEmpty(responseInfoMsg("Bloom's taxonomy Does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Bloom's Taxonomy Does not exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sub-learning-types_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subLearningTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subLearningTypesRepository.findByUuidAndDeletedAtIsNull(subLearningTypeUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubLearningTypeEntity entity = SubLearningTypeEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .bloomTaxonomyUUID(previousEntity.getBloomTaxonomyUUID())
                                        .name(previousEntity.getName())
                                        .code(previousEntity.getCode())
                                        .description(previousEntity.getDescription())
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

                                return subLearningTypesRepository.save(previousEntity)
                                        .then(subLearningTypesRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_sub-learning-types_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subLearningTypeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subLearningTypesRepository.findByUuidAndDeletedAtIsNull(subLearningTypeUUID)
                .flatMap(subLearningType -> {

                    subLearningType.setDeletedBy(UUID.fromString(userId));
                    subLearningType.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    subLearningType.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    subLearningType.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    subLearningType.setReqDeletedIP(reqIp);
                    subLearningType.setReqDeletedPort(reqPort);
                    subLearningType.setReqDeletedBrowser(reqBrowser);
                    subLearningType.setReqDeletedOS(reqOs);
                    subLearningType.setReqDeletedDevice(reqDevice);
                    subLearningType.setReqDeletedReferer(reqReferer);

                    return subLearningTypesRepository.save(subLearningType)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
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
