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
import tuf.webscaf.app.dbContext.master.entity.NationalityEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveNationalityRepository;
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

@Tag(name = "nationalityHandler")
@Component
public class NationalityHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    NationalityRepository nationalityRepository;

    @Autowired
    SlaveNationalityRepository slaveNationalityRepository;

    @Autowired
    StudentNationalityPvtRepository studentNationalityPvtRepository;

    @Autowired
    StudentFatherNationalityPvtRepository studentFatherNationalityPvtRepository;

    @Autowired
    StudentMotherNationalityPvtRepository studentMotherProfileNationalityRepository;

    @Autowired
    StudentSiblingNationalityPvtRepository studentSiblingNationalityPvtRepository;

    @Autowired
    StudentGuardianNationalityPvtRepository studentGuardianNationalityPvtRepository;

    @Autowired
    TeacherNationalityPvtRepository teacherNationalityPvtRepository;

    @Autowired
    TeacherChildNationalityPvtRepository teacherChildNationalityPvtRepository;

    @Autowired
    TeacherFatherNationalityPvtRepository teacherFatherNationalityPvtRepository;

    @Autowired
    TeacherMotherNationalityPvtRepository teacherMotherNationalityPvtRepository;

    @Autowired
    TeacherSiblingNationalityPvtRepository teacherSiblingNationalityPvtRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;


    @AuthHasPermission(value = "academic_api_v1_nationalities_index")
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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveNationalityEntity> slaveNationalityFlux = slaveNationalityRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveNationalityFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository
                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (nationalityEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveNationalityEntity> slaveNationalityFlux = slaveNationalityRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveNationalityFlux
                    .collectList()
                    .flatMap(nationalityEntity -> slaveNationalityRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (nationalityEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", nationalityEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_nationalities_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID nationalityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveNationalityRepository.findByUuidAndDeletedAtIsNull(nationalityUUID)
                .flatMap(nationalityEntity -> responseSuccessMsg("Record Fetched Successfully", nationalityEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_nationalities_country_show")
//This function is used by delete function of Country Handler in Config Module to Check If country Exists in nationality
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveNationalityRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_nationalities_store")
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

                    NationalityEntity nationalityEntity = NationalityEntity.builder()
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
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

//                    check name is unique
                    return nationalityRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(nationalityEntity.getName())
                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", nationalityEntity.getCountryUUID())
                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                            .flatMap(saveEntity -> nationalityRepository.save(nationalityEntity)
                                                    .flatMap(nationalityDB -> responseSuccessMsg("Record Stored Successfully", nationalityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                            )
                                    )
                                    .switchIfEmpty(responseInfoMsg("Country does not exist"))
                                    .onErrorResume(err -> responseErrorMsg("Country does not exist. Please contact developer"))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer"));
    }

    @AuthHasPermission(value = "academic_api_v1_nationalities_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID nationalityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> nationalityRepository.findByUuidAndDeletedAtIsNull(nationalityUUID)
                        .flatMap(previousNationalityDB -> {

                            NationalityEntity updatedNationalityEntity = NationalityEntity.builder()
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .uuid(previousNationalityDB.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                    .createdAt(previousNationalityDB.getCreatedAt())
                                    .createdBy(previousNationalityDB.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(previousNationalityDB.getReqCreatedIP())
                                    .reqCreatedPort(previousNationalityDB.getReqCreatedPort())
                                    .reqCreatedBrowser(previousNationalityDB.getReqCreatedBrowser())
                                    .reqCreatedOS(previousNationalityDB.getReqCreatedOS())
                                    .reqCreatedDevice(previousNationalityDB.getReqCreatedDevice())
                                    .reqCreatedReferer(previousNationalityDB.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousNationalityDB.setDeletedBy(UUID.fromString(userId));
                            previousNationalityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousNationalityDB.setReqDeletedIP(reqIp);
                            previousNationalityDB.setReqDeletedPort(reqPort);
                            previousNationalityDB.setReqDeletedBrowser(reqBrowser);
                            previousNationalityDB.setReqDeletedOS(reqOs);
                            previousNationalityDB.setReqDeletedDevice(reqDevice);
                            previousNationalityDB.setReqDeletedReferer(reqReferer);

                            // check name is unique
                            return nationalityRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedNationalityEntity.getName(), nationalityUUID)
                                    .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exists."))
                                    .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedNationalityEntity.getCountryUUID())
                                            .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                                    .flatMap(saveEntity -> nationalityRepository.save(previousNationalityDB)
                                                            .then(nationalityRepository.save(updatedNationalityEntity))
                                                            .flatMap(nationalityDB -> responseSuccessMsg("Record Updated Successfully", nationalityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer"))
                                                    ))
                                            .switchIfEmpty(responseInfoMsg("Country does not exist"))
                                            .onErrorResume(err -> responseErrorMsg("Country does not exist. Please contact developer"))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_nationalities_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID nationalityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return nationalityRepository.findByUuidAndDeletedAtIsNull(nationalityUUID)
                .flatMap(nationalityEntityDB -> studentNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                        //Checks if Nationality Reference exists in Student Profile Nationality Pvt
                        .flatMap(checkStdProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Student Profile Nationality Pvt"))
                        .switchIfEmpty(Mono.defer(() -> studentFatherNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Student Father Profile Nationality Pvt
                                .flatMap(checkFthProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Student Father Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> studentMotherProfileNationalityRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Student Mother Profile Nationality Pvt
                                .flatMap(checkMthProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Student Mother Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> studentSiblingNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Student Sibling Profile Nationality Pvt
                                .flatMap(checkGuardProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Student Sibling Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> studentGuardianNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Student Guardian Nationality Pvt
                                .flatMap(checkGuardProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Student Guardian Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> teacherNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Teacher Profile Nationality Pvt
                                .flatMap(checkTeacherProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Teacher Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> teacherChildNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Teacher Child Profile Nationality Pvt
                                .flatMap(checkTeacherChildProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Teacher Child Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> teacherFatherNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Teacher Father Profile Nationality Pvt
                                .flatMap(checkTeacherFthProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Teacher Father Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> teacherMotherNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Teacher Mother Profile Nationality Pvt
                                .flatMap(checkTeacherMthProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Teacher Mother Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingNationalityPvtRepository.findFirstByNationalityUUIDAndDeletedAtIsNull(nationalityEntityDB.getUuid())
                                //Checks if Nationality Reference exists in Teacher Sibling Profile Nationality Pvt
                                .flatMap(checkTeacherMthProfileMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists in Teacher Sibling Profile Nationality Pvt"))))
                        .switchIfEmpty(Mono.defer(() -> {

                            nationalityEntityDB.setDeletedBy(UUID.fromString(userId));
                            nationalityEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            nationalityEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            nationalityEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            nationalityEntityDB.setReqDeletedIP(reqIp);
                            nationalityEntityDB.setReqDeletedPort(reqPort);
                            nationalityEntityDB.setReqDeletedBrowser(reqBrowser);
                            nationalityEntityDB.setReqDeletedOS(reqOs);
                            nationalityEntityDB.setReqDeletedDevice(reqDevice);
                            nationalityEntityDB.setReqDeletedReferer(reqReferer);

                            return nationalityRepository.save(nationalityEntityDB)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                        }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_nationalities_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID nationalityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return nationalityRepository.findByUuidAndDeletedAtIsNull(nationalityUUID)
                            .flatMap(previousNationalityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousNationalityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                NationalityEntity updatedNationalityEntity = NationalityEntity.builder()
                                        .name(previousNationalityDB.getName())
                                        .description(previousNationalityDB.getDescription())
                                        .uuid(previousNationalityDB.getUuid())
                                        .status(status == true ? true : false)
                                        .countryUUID(previousNationalityDB.getCountryUUID())
                                        .createdAt(previousNationalityDB.getCreatedAt())
                                        .createdBy(previousNationalityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousNationalityDB.getReqCreatedIP())
                                        .reqCreatedPort(previousNationalityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(previousNationalityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(previousNationalityDB.getReqCreatedOS())
                                        .reqCreatedDevice(previousNationalityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(previousNationalityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousNationalityDB.setDeletedBy(UUID.fromString(userId));
                                previousNationalityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousNationalityDB.setReqDeletedIP(reqIp);
                                previousNationalityDB.setReqDeletedPort(reqPort);
                                previousNationalityDB.setReqDeletedBrowser(reqBrowser);
                                previousNationalityDB.setReqDeletedOS(reqOs);
                                previousNationalityDB.setReqDeletedDevice(reqDevice);
                                previousNationalityDB.setReqDeletedReferer(reqReferer);

                                return nationalityRepository.save(previousNationalityDB)
                                        .then(nationalityRepository.save(updatedNationalityEntity))
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
