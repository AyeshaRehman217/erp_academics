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
import tuf.webscaf.app.dbContext.master.entity.SubjectFeeEntity;
import tuf.webscaf.app.dbContext.master.repositry.CourseSubjectRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectFeeRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectOfferedRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectFeeEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectFeeRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "subjectFeeHandler")
@Component
public class SubjectFeeHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectFeeRepository subjectFeeRepository;

    @Autowired
    SlaveSubjectFeeRepository slaveSubjectFeeRepository;

    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @AuthHasPermission(value = "academic_api_v1_subject-fees_index")
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

        //fetching records Based on status filter
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveSubjectFeeEntity> slaveSubjectFeeFlux = slaveSubjectFeeRepository
                    .findAllByStatusAndDeletedAtIsNull(pageable, Boolean.valueOf(status));
            return slaveSubjectFeeFlux
                    .collectList()
                    .flatMap(subjectFeeEntity -> slaveSubjectFeeRepository.countByStatusAndDeletedAtIsNull(Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectFeeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectFeeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectFeeEntity> slaveSubjectFeeFlux = slaveSubjectFeeRepository
                    .findAllByDeletedAtIsNull(pageable);
            return slaveSubjectFeeFlux
                    .collectList()
                    .flatMap(subjectFeeEntity -> slaveSubjectFeeRepository.countByDeletedAtIsNull()
                            .flatMap(count -> {
                                if (subjectFeeEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectFeeEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-fees_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectFeeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectFeeRepository.findByUuidAndDeletedAtIsNull(subjectFeeUUID)
                .flatMap(subjectFeeEntity -> responseSuccessMsg("Record Fetched Successfully", subjectFeeEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    //This function is used by delete function of Currency Handler in Config Module to Check If Currency Exists in Subject Fee
    @AuthHasPermission(value = "academic_api_v1_subject-fees_currency_show")
    public Mono<ServerResponse> getCurrencyUUID(ServerRequest serverRequest) {
        UUID currencyUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectFeeRepository.findFirstByCurrencyUUIDAndDeletedAtIsNull(currencyUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-fees_store")
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

                    double creditHourRate = 0.0;
                    if (!Objects.equals(value.getFirst("creditHoursRate"), "") && value.getFirst("creditHoursRate") != null) {
                        creditHourRate = Double.parseDouble(value.getFirst("creditHoursRate"));
                    }

                    double amount = 0.0;
                    if (!Objects.equals(value.getFirst("amount"), "") && value.getFirst("amount") != null) {
                        amount = Double.parseDouble(value.getFirst("amount"));
                    }


                    SubjectFeeEntity subjectFeeEntity = SubjectFeeEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .subjectOfferedUUID(UUID.fromString(value.getFirst("subjectOfferedUUID").trim()))
                            .creditHoursRate(creditHourRate)
                            .subjectEnrollmentFee(Double.parseDouble(value.getFirst("subjectEnrollmentFee")))
                            .amount(amount)
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                            .isAmount(Boolean.valueOf(value.getFirst("isAmount")))
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

                    //checks if subject Offered uuid exists
                    return subjectOfferedRepository.findByUuidAndDeletedAtIsNull(subjectFeeEntity.getSubjectOfferedUUID())
                            //checks if currency uuid exists
                            .flatMap(subjectOffered -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOffered.getCourseSubjectUUID())
                                    .flatMap(courseSubject -> {
                                        //check if user left credit hour rate and amount empty at the same time
                                        if (subjectFeeEntity.getCreditHoursRate().equals(0.0) && subjectFeeEntity.getAmount().equals(0.0)) {
                                            return responseInfoMsg("Credit Hour Rate or Amount cannot be empty at the same time.");
                                        }

                                        if (!subjectFeeEntity.getCreditHoursRate().equals(0.0) && !subjectFeeEntity.getAmount().equals(0.0)) {
                                            return responseInfoMsg("Please Enter Credit Hour Rate or Amount.Both cannot be entered at the same time.");
                                        }

                                        if (subjectFeeEntity.getIsAmount()) {
                                            if (subjectFeeEntity.getAmount().equals(0.0)) {
                                                return responseInfoMsg("Please Enter Amount");
                                            } else {
                                                subjectFeeEntity.setAmount(subjectFeeEntity.getAmount());
                                            }

                                            //check if the user does not enter credit hour rate
                                            if (subjectFeeEntity.getCreditHoursRate().equals(0.0)) {
                                                Double calculatedHourRate = subjectFeeEntity.getAmount() / courseSubject.getTotalCreditHours();
                                                subjectFeeEntity.setCreditHoursRate(calculatedHourRate);
                                            }

                                        } else {
                                            if (subjectFeeEntity.getCreditHoursRate().equals(0.0)) {
                                                return responseInfoMsg("Please Enter Credit Hour Rate");
                                            } else {
                                                subjectFeeEntity.setCreditHoursRate(subjectFeeEntity.getCreditHoursRate());
                                            }
                                            //check if the user does not enter amount
                                            if (subjectFeeEntity.getAmount().equals(0.0)) {
                                                Double calculatedAmount = courseSubject.getTotalCreditHours() * subjectFeeEntity.getCreditHoursRate();
                                                subjectFeeEntity.setAmount(calculatedAmount);
                                            }
                                        }
                                        //check if user enters both credit hour rate and Amount
                                        return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", subjectFeeEntity.getCurrencyUUID())
                                                .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                                        .flatMap(currencyEntity -> subjectFeeRepository.save(subjectFeeEntity)
                                                                .flatMap(feeStructureEntity -> responseSuccessMsg("Record Stored Successfully", feeStructureEntity))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                                ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));

                                    })
                            ).switchIfEmpty(responseInfoMsg("Subject Offered does not exist in offered Subjects"))
                            .onErrorResume(ex -> responseErrorMsg("Subject Offered does not exist in offered Subjects.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-fees_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectFeeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectFeeRepository.findByUuidAndDeletedAtIsNull(subjectFeeUUID)
                        .flatMap(previousEntity -> {

                            double creditHourRate = 0.0;
                            if (!Objects.equals(value.getFirst("creditHoursRate"), "") && value.getFirst("creditHoursRate") != null) {
                                creditHourRate = Double.parseDouble(value.getFirst("creditHoursRate"));
                            }

                            double amount = 0.0;
                            if (!Objects.equals(value.getFirst("amount"), "") && value.getFirst("amount") != null) {
                                amount = Double.parseDouble(value.getFirst("amount"));
                            }

                            SubjectFeeEntity updatedEntity = SubjectFeeEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .subjectOfferedUUID(UUID.fromString(value.getFirst("subjectOfferedUUID").trim()))
                                    .creditHoursRate(creditHourRate)
                                    .subjectEnrollmentFee(Double.parseDouble(value.getFirst("subjectEnrollmentFee")))
                                    .amount(amount)
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID").trim()))
                                    .isAmount(Boolean.valueOf(value.getFirst("isAmount")))
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

                            //checks if subject Offered uuid exists
                            return subjectOfferedRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSubjectOfferedUUID())
                                    //checks if currency uuid exists
                                    .flatMap(subjectOffered -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOffered.getCourseSubjectUUID())
                                            .flatMap(courseSubject -> {
                                                //check if user left credit hour rate and amount empty at the same time
                                                if (updatedEntity.getCreditHoursRate().equals(0.0) && updatedEntity.getAmount().equals(0.0)) {
                                                    return responseInfoMsg("Credit Hour Rate or Amount cannot be empty at the same time.");
                                                }

                                                if (updatedEntity.getIsAmount()) {
                                                    if (updatedEntity.getAmount().equals(0.0)) {
                                                        return responseInfoMsg("Please Enter Amount");
                                                    } else {
                                                        updatedEntity.setAmount(updatedEntity.getAmount());
                                                    }

                                                    //check if the user does not enter credit hour rate
                                                    if (updatedEntity.getCreditHoursRate().equals(0.0)) {
                                                        Double calculatedHourRate = updatedEntity.getAmount() / courseSubject.getTotalCreditHours();
                                                        updatedEntity.setCreditHoursRate(calculatedHourRate);
                                                    }

                                                } else {
                                                    if (updatedEntity.getCreditHoursRate().equals(0.0)) {
                                                        return responseInfoMsg("Please Enter Credit Hour Rate");
                                                    } else {
                                                        updatedEntity.setCreditHoursRate(updatedEntity.getCreditHoursRate());
                                                    }
                                                    //check if the user does not enter amount
                                                    if (updatedEntity.getAmount().equals(0.0)) {
                                                        Double calculatedAmount = courseSubject.getTotalCreditHours() * updatedEntity.getCreditHoursRate();
                                                        updatedEntity.setAmount(calculatedAmount);
                                                    }
                                                }
                                                //check if user enters both credit hour rate and Amount
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", updatedEntity.getCurrencyUUID())
                                                        .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                                                .flatMap(currencyEntity -> subjectFeeRepository.save(previousEntity)
                                                                        .then(subjectFeeRepository.save(updatedEntity))
                                                                        .flatMap(feeStructureEntity -> responseSuccessMsg("Record Updated Successfully", feeStructureEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                        .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer.")))
                                                        ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                                        .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."));

                                            })
                                    ).switchIfEmpty(responseInfoMsg("Subject Offered does not exist in offered Subjects"))
                                    .onErrorResume(ex -> responseErrorMsg("Subject Offered does not exist in offered Subjects.Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-fees_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectFeeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectFeeRepository.findByUuidAndDeletedAtIsNull(subjectFeeUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectFeeEntity updatedEntity = SubjectFeeEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .subjectOfferedUUID(previousEntity.getSubjectOfferedUUID())
                                        .creditHoursRate(previousEntity.getCreditHoursRate())
                                        .subjectEnrollmentFee(previousEntity.getSubjectEnrollmentFee())
                                        .amount(previousEntity.getAmount())
                                        .currencyUUID(previousEntity.getCurrencyUUID())
                                        .isAmount(previousEntity.getIsAmount())
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

                                return subjectFeeRepository.save(previousEntity)
                                        .then(subjectFeeRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-fees_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectFeeUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectFeeRepository.findByUuidAndDeletedAtIsNull(subjectFeeUUID)
                .flatMap(subjectFeeEntity -> {

                    subjectFeeEntity.setDeletedBy(UUID.fromString(userId));
                    subjectFeeEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    subjectFeeEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    subjectFeeEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    subjectFeeEntity.setReqDeletedIP(reqIp);
                    subjectFeeEntity.setReqDeletedPort(reqPort);
                    subjectFeeEntity.setReqDeletedBrowser(reqBrowser);
                    subjectFeeEntity.setReqDeletedOS(reqOs);
                    subjectFeeEntity.setReqDeletedDevice(reqDevice);
                    subjectFeeEntity.setReqDeletedReferer(reqReferer);

                    return subjectFeeRepository.save(subjectFeeEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
