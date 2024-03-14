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
import tuf.webscaf.app.dbContext.master.entity.FeeStructureEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveFeeStructureEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveFeeStructureRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Tag(name = "feeStructureHandler")
@Component
public class FeeStructureHandler {
    @Value("${server.zone}")
    private String zone;
    
    @Autowired
    CustomResponse appresponse;

    @Autowired
    FeeStructureRepository feeStructureRepository;

    @Autowired
    SlaveFeeStructureRepository slaveFeeStructureRepository;

    @Autowired
    AcademicSessionRepository academicSessionRepository;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;


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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
        Flux<SlaveFeeStructureEntity> slaveFeeStructureFlux = slaveFeeStructureRepository
                .findAllByDeletedAtIsNull(pageable);
        return slaveFeeStructureFlux
                .collectList()
                .flatMap(feeStructureEntity -> slaveFeeStructureRepository.countByDeletedAtIsNull()
                        .flatMap(count -> {
                            if (feeStructureEntity.isEmpty()) {
                                return responseIndexInfoMsg("Record does not exist", count);
                            } else {
                                return responseIndexSuccessMsg("All Records Fetched Successfully", feeStructureEntity, count);
                            }
                        })
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final long Id = Long.parseLong(serverRequest.pathVariable("id"));

        return slaveFeeStructureRepository.findByIdAndDeletedAtIsNull(Id)
                .flatMap(feeStructureEntity -> responseSuccessMsg("Record Fetched Successfully", feeStructureEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

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
                    FeeStructureEntity entity = FeeStructureEntity.builder()
                            .uuid(UUID.randomUUID())
                            .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID")))
                            .semesterUUID(UUID.fromString(value.getFirst("semesterUUID")))
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                            .courseUUID(UUID.fromString(value.getFirst("courseUUID")))
                            .creditHours(Integer.valueOf(value.getFirst("creditHours")))
                            .creditHoursRate(Long.valueOf(value.getFirst("creditHoursRate")))
                            .amount(Long.valueOf(value.getFirst("amount")))
                            .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                            .payableDate(LocalDateTime.parse(value.getFirst("payableDate"),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .dueDate(LocalDateTime.parse(value.getFirst("dueDate"),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .receiptDate(LocalDateTime.parse(value.getFirst("receiptDate"),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
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

                    //checks if academic session uuid exists
                    return academicSessionRepository.findByUuidAndDeletedAtIsNull(entity.getAcademicSessionUUID())
                            //checks if semester uuid exists
                            .flatMap(academicSessionEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(entity.getSemesterUUID())
                                    //checks if campus uuid exists
                                    .flatMap(semesterEntity -> campusRepository.findByUuidAndDeletedAtIsNull(entity.getCampusUUID())
                                            //checks if course uuid exists
                                            .flatMap(campusEntity -> courseRepository.findByUuidAndDeletedAtIsNull(entity.getCourseUUID())
                                                    //checks if currency uuid exists
                                                    .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                                            .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                                                    .flatMap(currencyEntity -> feeStructureRepository.save(entity)
                                                                            .flatMap(feeStructureEntity -> responseSuccessMsg("Record Stored Successfully", feeStructureEntity))
                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer.")))
                                                            ).switchIfEmpty(responseInfoMsg("Currency does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Currency does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Campus does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Campus does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Semester does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Semester does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final long feeStructureId = Long.parseLong(serverRequest.pathVariable("id"));
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
                .flatMap(value -> feeStructureRepository.findByIdAndDeletedAtIsNull(feeStructureId)
                        .flatMap(entity -> {
                            FeeStructureEntity updatedEntity = FeeStructureEntity.builder()
                                    .uuid(entity.getUuid())
                                    .academicSessionUUID(UUID.fromString(value.getFirst("academicSessionUUID")))
                                    .semesterUUID(UUID.fromString(value.getFirst("semesterUUID")))
                                    .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                                    .courseUUID(UUID.fromString(value.getFirst("courseUUID")))
                                    .creditHours(Integer.valueOf(value.getFirst("creditHours")))
                                    .creditHoursRate(Long.valueOf(value.getFirst("creditHoursRate")))
                                    .amount(Long.valueOf(value.getFirst("amount")))
                                    .currencyUUID(UUID.fromString(value.getFirst("currencyUUID")))
                                    .payableDate(LocalDateTime.parse(value.getFirst("payableDate"),
                                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .dueDate(LocalDateTime.parse(value.getFirst("dueDate"),
                                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .receiptDate(LocalDateTime.parse(value.getFirst("receiptDate"),
                                            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
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

                            //checks if academic session uuid exists
                            return academicSessionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getAcademicSessionUUID())
                                    //checks if semester uuid exists
                                    .flatMap(academicSessionEntity -> semesterRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSemesterUUID())
                                            //checks if campus uuid exists
                                            .flatMap(semesterEntity -> campusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCampusUUID())
                                                    //checks if course uuid exists
                                                    .flatMap(campusEntity -> courseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCourseUUID())
                                                            //checks if currency uuid exists
                                                            .flatMap(courseEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/currencies/show/", entity.getCurrencyUUID())
                                                                    .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                                                            .flatMap(currencyEntity -> feeStructureRepository.save(entity)
                                                                                    .then(feeStructureRepository.save(updatedEntity))
                                                                                    .flatMap(feeStructureEntity -> responseSuccessMsg("Record Updated Successfully", feeStructureEntity))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer.")))
                                                                    ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Course does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Course does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Campus does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Campus does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Semester does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Semester does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Academic Session does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Academic Session does not exist. Please contact developer."));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        final long feeStructureId = Long.parseLong(serverRequest.pathVariable("id"));
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
                    Boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return feeStructureRepository.findByIdAndDeletedAtIsNull(feeStructureId)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                FeeStructureEntity entity = FeeStructureEntity.builder()
                                        .uuid(val.getUuid())
                                        .academicSessionUUID(val.getAcademicSessionUUID())
                                        .semesterUUID(val.getSemesterUUID())
                                        .campusUUID(val.getSemesterUUID())
                                        .courseUUID(val.getCourseUUID())
                                        .creditHours(val.getCreditHours())
                                        .creditHoursRate(val.getCreditHoursRate())
                                        .amount(val.getAmount())
                                        .currencyUUID(val.getCurrencyUUID())
                                        .payableDate(val.getPayableDate())
                                        .dueDate(val.getDueDate())
                                        .receiptDate(val.getReceiptDate())
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

                                return feeStructureRepository.save(val)
                                        .then(feeStructureRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final long Id = Long.parseLong(serverRequest.pathVariable("id"));
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

        return feeStructureRepository.findByIdAndDeletedAtIsNull(Id)
                .flatMap(feeStructureEntity -> {

                    feeStructureEntity.setDeletedBy(UUID.fromString(userId));
                    feeStructureEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    feeStructureEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    feeStructureEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    feeStructureEntity.setReqDeletedIP(reqIp);
                    feeStructureEntity.setReqDeletedPort(reqPort);
                    feeStructureEntity.setReqDeletedBrowser(reqBrowser);
                    feeStructureEntity.setReqDeletedOS(reqOs);
                    feeStructureEntity.setReqDeletedDevice(reqDevice);
                    feeStructureEntity.setReqDeletedReferer(reqReferer);

                    return feeStructureRepository.save(feeStructureEntity)
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
