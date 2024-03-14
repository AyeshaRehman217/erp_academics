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
import tuf.webscaf.app.dbContext.master.entity.PeoEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlavePeoDto;
import tuf.webscaf.app.dbContext.slave.entity.SlavePeoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlavePeoRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "peoHandler")
@Component
public class PeoHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    PeoRepository peoRepository;

    @Autowired
    SlavePeoRepository slavePeoRepository;

    @Autowired
    PloPeoPvtRepository ploPeoPvtRepository;

    @Value("${server.zone}")
    private String zone;


    @AuthHasPermission(value = "academic_api_v1_peos_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // department Query Parameter
        String departmentUUID = serverRequest.queryParam("departmentUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        // department & status is present
        if (!departmentUUID.isEmpty() && !status.isEmpty()) {
            return indexPeosWithDepartmentAndStatus(UUID.fromString(departmentUUID), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable);
        }
        // if department is present
        else if (!departmentUUID.isEmpty()) {
            return indexPeosWithDepartment(UUID.fromString(departmentUUID), searchKeyWord, directionProperty, d, pageable);
        }
        //if Status is present Only
        else if (!status.isEmpty()) {
            Flux<SlavePeoDto> slavePeoEntityFlux = slavePeoRepository
                    .indexRecordsWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoEntityFlux
                    .collectList()
                    .flatMap(cloEntity -> slavePeoRepository.countPeoAgainstStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord)
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
            Flux<SlavePeoDto> slavePeoEntityFlux = slavePeoRepository
                    .indexRecordWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slavePeoEntityFlux
                    .collectList()
                    .flatMap(cloEntity -> slavePeoRepository.countPeoWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord)
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

    @AuthHasPermission(value = "academic_api_v1_peos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID peoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slavePeoRepository.showPeoRecords(peoUUID)
                .flatMap(peoEntity -> responseSuccessMsg("Record Fetched Successfully", peoEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_peos_store")
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

                    PeoEntity peoEntity = PeoEntity.builder()
                            .uuid(UUID.randomUUID())
                            .departmentUUID(UUID.fromString(value.getFirst("departmentUUID").trim()))
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

                    //check if Code is Unique
                    return peoRepository.findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNull(peoEntity.getCode(), peoEntity.getDepartmentUUID())
                            .flatMap(checkCodeMsg -> responseInfoMsg("Code Already Exist"))
                            //check if Department UUID exists in Departments
                            .switchIfEmpty(Mono.defer(() -> departmentRepository.findByUuidAndDeletedAtIsNull(peoEntity.getDepartmentUUID())
                                    .flatMap(deptMsg -> peoRepository.save(peoEntity)
                                            .flatMap(peoEntityDB -> responseSuccessMsg("Record Stored Successfully", peoEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                    .switchIfEmpty(responseInfoMsg("Department Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Department Does not exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_peos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID peoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> peoRepository.findByUuidAndDeletedAtIsNull(peoUUID)
                        .flatMap(previousEntity -> {

                            PeoEntity updatedEntity = PeoEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .departmentUUID(UUID.fromString(value.getFirst("departmentUUID").trim()))
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

                            //check if Code is Unique
                            return peoRepository.findFirstByCodeIgnoreCaseAndDepartmentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCode(), updatedEntity.getDepartmentUUID(), peoUUID)
                                    .flatMap(checkCodeMsg -> responseInfoMsg("Code Already Exist"))
                                    //check if Department UUID exists in Departments
                                    .switchIfEmpty(Mono.defer(() -> departmentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getDepartmentUUID())
                                            .flatMap(deptMsg -> peoRepository.save(previousEntity)
                                                    .then(peoRepository.save(updatedEntity))
                                                    .flatMap(peoEntityDB -> responseSuccessMsg("Record Updated Successfully", peoEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer.")))
                                            .switchIfEmpty(responseInfoMsg("Department Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Department Does not exist.Please Contact Developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_peos_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID peoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return peoRepository.findByUuidAndDeletedAtIsNull(peoUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                PeoEntity entity = PeoEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .departmentUUID(previousEntity.getDepartmentUUID())
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

                                return peoRepository.save(previousEntity)
                                        .then(peoRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_peos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID peoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return peoRepository.findByUuidAndDeletedAtIsNull(peoUUID)
                .flatMap(peoEntity -> ploPeoPvtRepository.findFirstByPeoUUIDAndDeletedAtIsNull(peoEntity.getUuid())
                        //checking if PEO exists in PLO PEO Pvt
                        .flatMap(ploEntity -> responseInfoMsg("Unable to delete record as the reference exists in PLOs"))
                        .switchIfEmpty(Mono.defer(() -> {

                            peoEntity.setDeletedBy(UUID.fromString(userId));
                            peoEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            peoEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            peoEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            peoEntity.setReqDeletedIP(reqIp);
                            peoEntity.setReqDeletedPort(reqPort);
                            peoEntity.setReqDeletedBrowser(reqBrowser);
                            peoEntity.setReqDeletedOS(reqOs);
                            peoEntity.setReqDeletedDevice(reqDevice);
                            peoEntity.setReqDeletedReferer(reqReferer);

                            return peoRepository.save(peoEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                        })))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."));
    }

//    ------------------ custom functions-----------------------------------

    /**
     * fetch Peos Against Department With Status
     **/
    public Mono<ServerResponse> indexPeosWithDepartmentAndStatus(UUID departmentUUID, Boolean status, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlavePeoDto> slavePeoEntityFlux = slavePeoRepository
                .indexRecordsAgainstDepartmentWithStatus(status, departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slavePeoEntityFlux
                .collectList()
                .flatMap(cloEntity -> slavePeoRepository.countPeoAgainstDepartmentAndStatus(departmentUUID, status, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
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
     * fetch Peos Against Department Without Status
     **/
    public Mono<ServerResponse> indexPeosWithDepartment(UUID departmentUUID, String searchKeyWord, String directionProperty, String direction, Pageable pageable) {

        Flux<SlavePeoDto> slavePeoEntityFlux = slavePeoRepository
                .indexRecordsAgainstDepartmentWithoutStatus(departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, direction, pageable.getPageSize(), pageable.getOffset());

        return slavePeoEntityFlux
                .collectList()
                .flatMap(cloEntity -> slavePeoRepository.countPeoAgainstDepartment(departmentUUID, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
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

//    ------------------ custom functions-----------------------------------
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
