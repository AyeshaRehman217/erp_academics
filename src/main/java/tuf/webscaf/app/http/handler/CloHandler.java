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
import tuf.webscaf.app.dbContext.master.entity.CloEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveEnrollmentDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveCloRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "cloHandler")
@Component
public class CloHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    CloRepository cloRepository;

    @Autowired
    SlaveCloRepository slaveCloRepository;

    @Autowired
    SubLearningTypeRepository subLearningTypeRepository;

    @Autowired
    BloomsTaxonomyRepository bloomsTaxonomyRepository;

    @Autowired
    SubjectObeCloPvtRepository subjectObeCloPvtRepository;

    @Autowired
    EmphasisLevelRepository emphasisLevelRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CloPloRepository cloPloRepository;

    @Value("${server.zone}")
    private String zone;


    @AuthHasPermission(value = "academic_api_v1_clos_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        String departmentUUID = serverRequest.queryParam("departmentUUID").map(String::toString).orElse("").trim();

        String emphasisUUID = serverRequest.queryParam("emphasisUUID").map(String::toString).orElse("").trim();

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

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // department & Emphasis Level & Status is present
        if (!departmentUUID.isEmpty() && !emphasisUUID.isEmpty() && !status.isEmpty()) {
            return indexClosWithEmphasisAndDepartmentWithStatus(UUID.fromString(departmentUUID), UUID.fromString(emphasisUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }
        // department & Emphasis Level is present
        else if (!departmentUUID.isEmpty() && !emphasisUUID.isEmpty()) {
            return indexClosWithEmphasisAndDepartment(UUID.fromString(departmentUUID), UUID.fromString(emphasisUUID), searchKeyWord, directionProperty, d, pageable);
        }
        // department & status is present
        else if (!departmentUUID.isEmpty() && !status.isEmpty()) {
            return indexClosWithDepartmentAndStatus(UUID.fromString(departmentUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }
        // emphasis & status is present
        else if (!emphasisUUID.isEmpty() && !status.isEmpty()) {
            return indexClosWithEmphasisAndStatus(UUID.fromString(emphasisUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }
        // if emphasis level is present
        else if (!emphasisUUID.isEmpty()) {
            return indexClosWithEmphasis(UUID.fromString(emphasisUUID), searchKeyWord, directionProperty, d, pageable);
        }
        // if department is present
        else if (!departmentUUID.isEmpty()) {
            return indexClosWithDepartment(UUID.fromString(departmentUUID), searchKeyWord, directionProperty, d, pageable);
        }
        //if Status is present Only
        else if (!status.isEmpty()) {
            Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                    .indexRecordsWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCloEntityFlux
                    .collectList()
                    .flatMap(cloEntity -> slaveCloRepository.countCloAgainstStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (cloEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                    .indexRecordWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveCloEntityFlux
                    .collectList()
                    .flatMap(cloEntity -> slaveCloRepository.countCloWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (cloEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_clos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID cloUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveCloRepository.showCloRecords(cloUUID)
                .flatMap(cloEntity -> responseSuccessMsg("Record Fetched Successfully", cloEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clos_store")
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
                    CloEntity cloEntityData = CloEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .code(value.getFirst("code").trim())
                            .description(value.getFirst("description").trim())
                            .emphasisUUID(UUID.fromString(value.getFirst("emphasisUUID").trim()))
                            .departmentUUID(UUID.fromString(value.getFirst("departmentUUID").trim()))
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

                    //check if plo name already exists
                    return cloRepository.findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(cloEntityData.getCode(), cloEntityData.getDepartmentUUID())
                            .flatMap(checkCode -> responseInfoMsg("Code Already Exist"))
                            //check learning type uuid exists
                            .switchIfEmpty(Mono.defer(() -> emphasisLevelRepository.findByUuidAndDeletedAtIsNull(cloEntityData.getEmphasisUUID())
                                    .flatMap(emphasis -> departmentRepository.findByUuidAndDeletedAtIsNull(cloEntityData.getDepartmentUUID())
                                            .flatMap(department -> {
                                                if (cloEntityData.getName() != null && !(cloEntityData.getName().equals(""))) {
                                                    return cloRepository.findFirstByNameIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(cloEntityData.getName(), cloEntityData.getDepartmentUUID())
                                                            .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
                                                            .switchIfEmpty(Mono.defer(() -> cloRepository.save(cloEntityData)
                                                                    .flatMap(saveClo -> responseSuccessMsg("Record Stored Successfully", saveClo))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                            ));
                                                } else {
                                                    return cloRepository.save(cloEntityData)
                                                            .flatMap(saveClo -> responseSuccessMsg("Record Stored Successfully", saveClo))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                }
                                            }).switchIfEmpty(responseInfoMsg("Department Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Department Does not exist.Please Contact Developer."))
                                    ).switchIfEmpty(responseInfoMsg("Emphasis level does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Emphasis level does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
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

        return serverRequest.formData()
                .flatMap(value -> cloRepository.findByUuidAndDeletedAtIsNull(cloUUID)
                        .flatMap(previousEntity -> {

                            CloEntity updatedEntity = CloEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .code(value.getFirst("code").trim())
                                    .description(value.getFirst("description").trim())
                                    .emphasisUUID(UUID.fromString(value.getFirst("emphasisUUID").trim()))
                                    .departmentUUID(UUID.fromString(value.getFirst("departmentUUID").trim()))
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

                            //check if plo name already exists
                            return cloRepository.findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCode(), updatedEntity.getDepartmentUUID(), cloUUID)
                                    .flatMap(checkCode -> responseInfoMsg("Code Already Exist"))
                                    //check learning type uuid exists
                                    .switchIfEmpty(Mono.defer(() -> emphasisLevelRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getEmphasisUUID())
                                            .flatMap(emphasis -> departmentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDepartmentUUID())
                                                    .flatMap(department -> {
                                                                if (updatedEntity.getName() != null && !(updatedEntity.getName().equals(""))) {
                                                                    return cloRepository.findFirstByNameIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), updatedEntity.getDepartmentUUID(), cloUUID)
                                                                            .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
                                                                            .switchIfEmpty(Mono.defer(() -> cloRepository.save(previousEntity)
                                                                                    .then(cloRepository.save(updatedEntity))
                                                                                    .flatMap(saveClo -> responseSuccessMsg("Record Updated Successfully", saveClo))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."))));
                                                                } else {
                                                                    return cloRepository.save(previousEntity)
                                                                            .then(cloRepository.save(updatedEntity))
                                                                            .flatMap(ploEntity -> responseSuccessMsg("Record Updated Successfully", ploEntity))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                                }
                                                            }
                                                    ).switchIfEmpty(responseInfoMsg("Department does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Department does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Emphasis level does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Emphasis level does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clos_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
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
        return serverRequest.formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return cloRepository.findByUuidAndDeletedAtIsNull(cloUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                CloEntity updatedEntity = CloEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .code(previousEntity.getCode())
                                        .description(previousEntity.getDescription())
                                        .departmentUUID(previousEntity.getDepartmentUUID())
                                        .emphasisUUID(previousEntity.getEmphasisUUID())
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


                                return cloRepository.save(previousEntity)
                                        .then(cloRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_clos_delete")
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

        return cloRepository.findByUuidAndDeletedAtIsNull(cloUUID)
                .flatMap(cloEntity -> subjectObeCloPvtRepository.findFirstByCloUUIDAndDeletedAtIsNull(cloEntity.getUuid())
                        //checking if CLO exists in CLO Subject Offered Pvt
                        .flatMap(subjectObeClo -> responseInfoMsg("Unable to delete record as the reference exists"))
                        .switchIfEmpty(Mono.defer(() -> cloPloRepository.findFirstByCloUUIDAndDeletedAtIsNull(cloEntity.getUuid())
                                //checking if CLO exists in CLO PLO pvt
                                .flatMap(cloPloPvt -> responseInfoMsg("Unable to delete record as the reference exists"))
                        ))
                        .switchIfEmpty(Mono.defer(() -> {

                            cloEntity.setDeletedBy(UUID.fromString(userId));
                            cloEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            cloEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            cloEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            cloEntity.setReqDeletedIP(reqIp);
                            cloEntity.setReqDeletedPort(reqPort);
                            cloEntity.setReqDeletedBrowser(reqBrowser);
                            cloEntity.setReqDeletedOS(reqOs);
                            cloEntity.setReqDeletedDevice(reqDevice);
                            cloEntity.setReqDeletedReferer(reqReferer);

                            return cloRepository.save(cloEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

//    ----------------------- custom functions ---------------------------------
    /**
     * fetch Clos Against Emphasis With Status
     **/
    public Mono<ServerResponse> indexClosWithEmphasisAndStatus(UUID emphasisUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                .indexRecordsAgainstEmphasisLevelWithStatus(status, emphasisUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slaveCloEntityFlux
                .collectList()
                .flatMap(cloEntity -> slaveCloRepository.countCloAgainstEmphasisAndStatus(emphasisUUID, status, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                        .flatMap(count -> {
                            if (cloEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * fetch Clos Against Emphasis Without Status
     **/
    public Mono<ServerResponse> indexClosWithEmphasis(UUID emphasisUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                .indexRecordsAgainstEmphasisLevelWithoutStatus(emphasisUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slaveCloEntityFlux
                .collectList()
                .flatMap(cloEntity -> slaveCloRepository.countCloAgainstEmphasis(emphasisUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                        .flatMap(count -> {
                            if (cloEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    /**
     * fetch Clos Against Department With Status
     **/
    public Mono<ServerResponse> indexClosWithDepartmentAndStatus(UUID departmentUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                .indexRecordsAgainstDepartmentWithStatus(status, departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slaveCloEntityFlux
                .collectList()
                .flatMap(cloEntity -> slaveCloRepository.countCloAgainstDepartmentAndStatus(departmentUUID, status, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                        .flatMap(count -> {
                            if (cloEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * fetch Clos Against Department Without Status
     **/
    public Mono<ServerResponse> indexClosWithDepartment(UUID departmentUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                .indexRecordsAgainstDepartmentWithoutStatus(departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slaveCloEntityFlux
                .collectList()
                .flatMap(cloEntity -> slaveCloRepository.countCloAgainstDepartment(departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                        .flatMap(count -> {
                            if (cloEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    /**
     * fetch Clos Against Department & Emphasis With Status
     **/
    public Mono<ServerResponse> indexClosWithEmphasisAndDepartmentWithStatus(UUID departmentUUID, UUID emphasisUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                .indexRecordsAgainstDepartmentAndEmphasisLevelWithStatus(status, departmentUUID, emphasisUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slaveCloEntityFlux
                .collectList()
                .flatMap(cloEntity -> slaveCloRepository.countCloAgainstEmphasisAndDepartmentAndStatus(emphasisUUID, departmentUUID, status, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                        .flatMap(count -> {
                            if (cloEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    /**
     * fetch Clos Against Emphasis & Department Without Status
     **/
    public Mono<ServerResponse> indexClosWithEmphasisAndDepartment(UUID departmentUUID, UUID emphasisUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlaveCloDto> slaveCloEntityFlux = slaveCloRepository
                .indexRecordsAgainstDepartmentAndEmphasisLevelWithoutStatus(departmentUUID, emphasisUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slaveCloEntityFlux
                .collectList()
                .flatMap(cloEntity -> slaveCloRepository.countCloAgainstEmphasisAndDepartment(emphasisUUID, departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                        .flatMap(count -> {
                            if (cloEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", cloEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

//    ----------------------- custom functions ---------------------------------

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
