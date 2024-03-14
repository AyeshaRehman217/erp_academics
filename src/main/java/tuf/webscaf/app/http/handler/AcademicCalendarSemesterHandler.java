//package tuf.webscaf.app.http.handler;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import tuf.webscaf.app.dbContext.master.entity.AcademicCalendarSemesterEntity;
//import tuf.webscaf.app.dbContext.master.repositry.*;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarSemesterEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveAcademicCalendarSemesterRepository;
//import tuf.webscaf.app.service.ApiCallService;
//import tuf.webscaf.app.verification.module.AuthHasPermission;
//import tuf.webscaf.config.service.response.AppResponse;
//import tuf.webscaf.config.service.response.AppResponseMessage;
//import tuf.webscaf.config.service.response.CustomResponse;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.UUID;
//
//@Tag(name = "academicCalendarSemesterHandler")
//@Component
//public class AcademicCalendarSemesterHandler {
//    @Autowired
//    CustomResponse appresponse;
//
//    @Autowired
//    EnrollmentRepository enrollmentRepository;
//
//    @Autowired
//    RegistrationRepository registrationRepository;
//
//    @Autowired
//    FeeStructureRepository feeStructureRepository;
//
//    @Autowired
//    AcademicCalendarSemesterRepository academicCalendarSemesterRepository;
//
//    @Autowired
//    AttendanceRepository attendanceRepository;
//
//    @Autowired
//    AcademicCalendarRepository academicCalendarRepository;
//
//    @Autowired
//    CourseOfferedRepository courseOfferedRepository;
//
//    @Autowired
//    SubjectOfferedRepository subjectOfferedRepository;
//
//    @Autowired
//    SessionTypeRepository sessionTypeRepository;
//
//    @Autowired
//    SlaveAcademicCalendarSemesterRepository slaveAcademicCalendarSemesterRepository;
//
//    @Autowired
//    ApiCallService apiCallService;
//
//    @Value("${server.zone}")
//    private String zone;
//
//    @Value("${server.erp_config_module.uri}")
//    private String configUri;
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_index")
//    public Mono<ServerResponse> index(ServerRequest serverRequest) {
//
//        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();
//
//        int size = serverRequest.queryParam("s").map(Integer::parseInt).orElse(10);
//        if (size > 100) {
//            size = 100;
//        }
//        int pageRequest = serverRequest.queryParam("p").map(Integer::parseInt).orElse(1);
//        int page = pageRequest - 1;
//        if (page < 0) {
//            return responseErrorMsg("Invalid Page No");
//        }
//
//        String d = serverRequest.queryParam("d").map(String::toString).orElse("asc");
//        Sort.Direction direction;
//        switch (d.toLowerCase()) {
//            case "asc":
//                direction = Sort.Direction.ASC;
//                break;
//            case "desc":
//                direction = Sort.Direction.DESC;
//                break;
//            default:
//                direction = Sort.Direction.ASC;
//        }
//
//        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");
//
//        //  Optional status query parameter
//        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//
//         if (!status.isEmpty()) {
//            Flux<SlaveAcademicCalendarSemesterEntity> slaveAcademicCalendarSemesterFlux = slaveAcademicCalendarSemesterRepository
//                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
//                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
//            return slaveAcademicCalendarSemesterFlux
//                    .collectList()
//                    .flatMap(academicCalendarSemesterEntity -> slaveAcademicCalendarSemesterRepository
//                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
//                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
//                            .flatMap(count -> {
//                                if (academicCalendarSemesterEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarSemesterEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
//        else {
//            Flux<SlaveAcademicCalendarSemesterEntity> slaveAcademicCalendarSemesterFlux = slaveAcademicCalendarSemesterRepository
//                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
//
//            return slaveAcademicCalendarSemesterFlux
//                    .collectList()
//                    .flatMap(academicCalendarSemesterEntity -> slaveAcademicCalendarSemesterRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
//                            .flatMap(count -> {
//                                if (academicCalendarSemesterEntity.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", academicCalendarSemesterEntity, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
//    }
//
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_show")
//    public Mono<ServerResponse> show(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//
//        return slaveAcademicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                .flatMap(academicCalendarSemesterEntity -> responseSuccessMsg("Record Fetched Successfully", academicCalendarSemesterEntity))
//                .switchIfEmpty(responseInfoMsg("Record does not exist"))
//                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
//
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_store")
//    public Mono<ServerResponse> store(ServerRequest serverRequest) {
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return serverRequest.formData()
//                .flatMap(value -> {
//
//                    AcademicCalendarSemesterEntity entity = AcademicCalendarSemesterEntity
//                            .builder()
//                            .uuid(UUID.randomUUID())
//                            .academicCalendarUUID(UUID.fromString(value.getFirst("academicCalendarUUID").trim()))
//                            .sessionTypeUUID(UUID.fromString(value.getFirst("sessionTypeUUID")))
//                            .status(Boolean.valueOf(value.getFirst("status")))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                            .reqCreatedIP(reqIp)
//                            .reqCreatedPort(reqPort)
//                            .reqCreatedBrowser(reqBrowser)
//                            .reqCreatedOS(reqOs)
//                            .reqCreatedDevice(reqDevice)
//                            .reqCreatedReferer(reqReferer)
//                            .build();
//
//
//                    // check if name is unique
//                    return academicCalendarSemesterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(entity.getName())
//                            .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
//                            // check if start date uuid exists
//                            .switchIfEmpty(Mono.defer(() -> sessionTypeRepository.findByUuidAndDeletedAtIsNull(entity.getSessionTypeUUID())
//                                    .flatMap(sessionTypeEntity -> {
//
//                                        //set Academic Session Name as Fall-202X
//                                        entity.setName(sessionTypeEntity.getName() + "-" + entity.getAcademicYear().getYear());
//
//                                        // If start date is after the end date
//                                        if (entity.getStartDate().isAfter(entity.getEndDate())) {
//                                            return responseInfoMsg("Start Date Should be before the End Date");
//                                        }
//
//                                        if (entity.getEndDate().isBefore(entity.getStartDate())) {
//                                            return responseInfoMsg("End Date Should be After the Start Date");
//                                        }
//
//                                        if (!sessionTypeEntity.getIsSpecial()) {
//                                            // check if academic session's duration is overlapping
//                                            return academicCalendarSemesterRepository.findStartDateAndEndDateIsUnique(entity.getStartDate(), entity.getEndDate())
//                                                    .flatMap(checkName -> responseInfoMsg("Academic Session already exist with in this duration"))
//                                                    .switchIfEmpty(Mono.defer(() -> academicCalendarSemesterRepository.save(entity)
//                                                            .flatMap(academicCalendarSemesterEntity -> responseSuccessMsg("Record Stored Successfully", academicCalendarSemesterEntity))
//                                                            .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."))
//                                                    ));
//                                        }
//                                        // if session type is special session duration can overlap
//                                        else {
//                                            return academicCalendarSemesterRepository.save(entity)
//                                                    .flatMap(academicCalendarSemesterEntity -> responseSuccessMsg("Record Stored Successfully", academicCalendarSemesterEntity))
//                                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record.Please Contact Developer."));
//                                        }
//                                    }).switchIfEmpty(responseInfoMsg("Session Type record does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Session Type record does not exist. Please contact developer"))
//                            ));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }
//
//    public String getCalendarDate(JsonNode jsonNode) {
//        // calendar start date
//        String calendarDate = "";
//
//        final JsonNode arrNode = jsonNode.get("data");
//        if (arrNode.isArray()) {
//            for (final JsonNode objNode : arrNode) {
//                if (objNode.get("date") != null) {
//                    calendarDate = objNode.get("date").toString().replaceAll("\"", "");
//                }
//            }
//        }
//        return calendarDate;
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return serverRequest.formData()
//                .flatMap(value -> academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                        .flatMap(previousAcademicEntity -> {
//
//                            AcademicCalendarSemesterEntity updatedAcademicCalendarSemesterEntity = AcademicCalendarSemesterEntity
//                                    .builder()
//                                    .uuid(previousAcademicEntity.getUuid())
//                                    .academicYear(LocalDateTime.parse(value.getFirst("academicYear"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                                    .description(value.getFirst("description").trim())
//                                    .startDate(LocalDateTime.parse((value.getFirst("startDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                                    .endDate(LocalDateTime.parse((value.getFirst("endDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .sessionTypeUUID(UUID.fromString(value.getFirst("sessionTypeUUID")))
//                                    .createdAt(previousAcademicEntity.getCreatedAt())
//                                    .createdBy(previousAcademicEntity.getCreatedBy())
//                                    .updatedBy(UUID.fromString(userId))
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .reqCreatedIP(previousAcademicEntity.getReqCreatedIP())
//                                    .reqCreatedPort(previousAcademicEntity.getReqCreatedPort())
//                                    .reqCreatedBrowser(previousAcademicEntity.getReqCreatedBrowser())
//                                    .reqCreatedOS(previousAcademicEntity.getReqCreatedOS())
//                                    .reqCreatedDevice(previousAcademicEntity.getReqCreatedDevice())
//                                    .reqCreatedReferer(previousAcademicEntity.getReqCreatedReferer())
//                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                    .reqUpdatedIP(reqIp)
//                                    .reqUpdatedPort(reqPort)
//                                    .reqUpdatedBrowser(reqBrowser)
//                                    .reqUpdatedOS(reqOs)
//                                    .reqUpdatedDevice(reqDevice)
//                                    .reqUpdatedReferer(reqReferer)
//                                    .build();
//
//                            //Deleting Previous Record and Creating a New One Based on UUID
//                            previousAcademicEntity.setDeletedBy(UUID.fromString(userId));
//                            previousAcademicEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                            previousAcademicEntity.setReqDeletedIP(reqIp);
//                            previousAcademicEntity.setReqDeletedPort(reqPort);
//                            previousAcademicEntity.setReqDeletedBrowser(reqBrowser);
//                            previousAcademicEntity.setReqDeletedOS(reqOs);
//                            previousAcademicEntity.setReqDeletedDevice(reqDevice);
//                            previousAcademicEntity.setReqDeletedReferer(reqReferer);
//
//
//                            //  check if name is unique
//                            return academicCalendarSemesterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedAcademicCalendarSemesterEntity.getName(), academicCalendarSemesterUUID)
//                                    .flatMap(checkName -> responseInfoMsg("Name Already Exist"))
//                                    // check if year uuid exists
//                                    .switchIfEmpty(Mono.defer(() -> sessionTypeRepository.findByUuidAndDeletedAtIsNull(updatedAcademicCalendarSemesterEntity.getSessionTypeUUID())
//                                            .flatMap(sessionTypeEntity -> {
//
//                                                updatedAcademicCalendarSemesterEntity.setName(sessionTypeEntity.getName() + "-" + updatedAcademicCalendarSemesterEntity.getAcademicYear().getYear());
//
//                                                // If start date is after the end date
//                                                if (updatedAcademicCalendarSemesterEntity.getStartDate().isAfter(updatedAcademicCalendarSemesterEntity.getEndDate())) {
//                                                    return responseInfoMsg("Start Date Should be before the End Date");
//                                                }
//
//                                                if (updatedAcademicCalendarSemesterEntity.getEndDate().isBefore(updatedAcademicCalendarSemesterEntity.getStartDate())) {
//                                                    return responseInfoMsg("End Date Should be After the Start Date");
//                                                }
//
//                                                if (!sessionTypeEntity.getIsSpecial()) {
//
//                                                    // check if academic session's duration is overlapping
//                                                    return academicCalendarSemesterRepository.findStartDateAndEndDateIsUniqueAndUuidIsNot(updatedAcademicCalendarSemesterEntity.getStartDate(), updatedAcademicCalendarSemesterEntity.getEndDate(), academicCalendarSemesterUUID)
//                                                            .flatMap(checkName -> responseInfoMsg("Academic Session already exist with in this duration"))
//                                                            .switchIfEmpty(Mono.defer(() -> academicCalendarSemesterRepository.save(previousAcademicEntity)
//                                                                    .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                                                    .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
//                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
//                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
//                                                            ));
//                                                }
//                                                // if session type is special session duration can overlap
//                                                else {
//                                                    return academicCalendarSemesterRepository.save(previousAcademicEntity)
//                                                            .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                                            .flatMap(academicEntityDB -> responseSuccessMsg("Record Updated Successfully", academicEntityDB))
//                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
//                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
//                                                }
//                                            }).switchIfEmpty(responseInfoMsg("Session Type record does not exist"))
//                                            .onErrorResume(ex -> responseErrorMsg("Session Type record does not exist. Please contact developer"))
//                                    ));
//                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
//                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-open_update")
//    public Mono<ServerResponse> isOpen(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    boolean isOpen = Boolean.parseBoolean(value.getFirst("isOpen"));
//                    return academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                            .flatMap(academicCalendarSemesterEntityDB -> {
//                                // If isOpen is not Boolean value
//                                if (isOpen != false && isOpen != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same isOpen exist in database.
//                                if (((academicCalendarSemesterEntityDB.getIsOpen() ? true : false) == isOpen)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                AcademicCalendarSemesterEntity updatedAcademicCalendarSemesterEntity = AcademicCalendarSemesterEntity.builder()
//                                        .uuid(academicCalendarSemesterEntityDB.getUuid())
//                                        .name(academicCalendarSemesterEntityDB.getName())
//                                        .academicYear(academicCalendarSemesterEntityDB.getAcademicYear())
//                                        .description(academicCalendarSemesterEntityDB.getDescription())
//                                        .isOpen(isOpen == true ? true : false)
//                                        .status(academicCalendarSemesterEntityDB.getStatus())
//                                        .isEnrollmentOpen(academicCalendarSemesterEntityDB.getIsEnrollmentOpen())
//                                        .isRegistrationOpen(academicCalendarSemesterEntityDB.getIsRegistrationOpen())
//                                        .isTimetableAllow(academicCalendarSemesterEntityDB.getIsTimetableAllow())
//                                        .startDate(academicCalendarSemesterEntityDB.getStartDate())
//                                        .endDate(academicCalendarSemesterEntityDB.getEndDate())
//                                        .sessionTypeUUID(academicCalendarSemesterEntityDB.getSessionTypeUUID())
//                                        .createdAt(academicCalendarSemesterEntityDB.getCreatedAt())
//                                        .createdBy(academicCalendarSemesterEntityDB.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                        .reqCreatedIP(academicCalendarSemesterEntityDB.getReqCreatedIP())
//                                        .reqCreatedPort(academicCalendarSemesterEntityDB.getReqCreatedPort())
//                                        .reqCreatedBrowser(academicCalendarSemesterEntityDB.getReqCreatedBrowser())
//                                        .reqCreatedOS(academicCalendarSemesterEntityDB.getReqCreatedOS())
//                                        .reqCreatedDevice(academicCalendarSemesterEntityDB.getReqCreatedDevice())
//                                        .reqCreatedReferer(academicCalendarSemesterEntityDB.getReqCreatedReferer())
//                                        .reqUpdatedIP(reqIp)
//                                        .reqUpdatedPort(reqPort)
//                                        .reqUpdatedBrowser(reqBrowser)
//                                        .reqUpdatedOS(reqOs)
//                                        .reqUpdatedDevice(reqDevice)
//                                        .reqUpdatedReferer(reqReferer)
//                                        .build();
//
//                                // update isOpen
//                                academicCalendarSemesterEntityDB.setDeletedBy(UUID.fromString(userId));
//                                academicCalendarSemesterEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                academicCalendarSemesterEntityDB.setReqDeletedIP(reqIp);
//                                academicCalendarSemesterEntityDB.setReqDeletedPort(reqPort);
//                                academicCalendarSemesterEntityDB.setReqDeletedBrowser(reqBrowser);
//                                academicCalendarSemesterEntityDB.setReqDeletedOS(reqOs);
//                                academicCalendarSemesterEntityDB.setReqDeletedDevice(reqDevice);
//                                academicCalendarSemesterEntityDB.setReqDeletedReferer(reqReferer);
//
//                                return academicCalendarSemesterRepository.save(academicCalendarSemesterEntityDB)
//                                        .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                        .flatMap(isOpenUpdate -> responseSuccessMsg("Status Updated Successfully", isOpenUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the Open Academic Session.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Open Academic Session.Please Contact Developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-registration-open_update")
//    public Mono<ServerResponse> isRegistrationOpen(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    boolean isRegistrationOpen = Boolean.parseBoolean(value.getFirst("isRegistrationOpen"));
//                    return academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                            .flatMap(academicCalendarSemesterEntityDB -> {
//                                // If isRegistrationOpen is not Boolean value
//                                if (isRegistrationOpen != false && isRegistrationOpen != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same isRegistrationOpen exist in database.
//                                if (((academicCalendarSemesterEntityDB.getIsRegistrationOpen() ? true : false) == isRegistrationOpen)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                AcademicCalendarSemesterEntity updatedAcademicCalendarSemesterEntity = AcademicCalendarSemesterEntity.builder()
//                                        .uuid(academicCalendarSemesterEntityDB.getUuid())
//                                        .name(academicCalendarSemesterEntityDB.getName())
//                                        .description(academicCalendarSemesterEntityDB.getDescription())
//                                        .academicYear(academicCalendarSemesterEntityDB.getAcademicYear())
//                                        .isRegistrationOpen(isRegistrationOpen == true ? true : false)
//                                        .status(academicCalendarSemesterEntityDB.getStatus())
//                                        .isOpen(academicCalendarSemesterEntityDB.getIsOpen())
//                                        .isEnrollmentOpen(academicCalendarSemesterEntityDB.getIsEnrollmentOpen())
//                                        .isTimetableAllow(academicCalendarSemesterEntityDB.getIsTimetableAllow())
//                                        .startDate(academicCalendarSemesterEntityDB.getStartDate())
//                                        .endDate(academicCalendarSemesterEntityDB.getEndDate())
//                                        .sessionTypeUUID(academicCalendarSemesterEntityDB.getSessionTypeUUID())
//                                        .createdAt(academicCalendarSemesterEntityDB.getCreatedAt())
//                                        .createdBy(academicCalendarSemesterEntityDB.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                        .reqCreatedIP(academicCalendarSemesterEntityDB.getReqCreatedIP())
//                                        .reqCreatedPort(academicCalendarSemesterEntityDB.getReqCreatedPort())
//                                        .reqCreatedBrowser(academicCalendarSemesterEntityDB.getReqCreatedBrowser())
//                                        .reqCreatedOS(academicCalendarSemesterEntityDB.getReqCreatedOS())
//                                        .reqCreatedDevice(academicCalendarSemesterEntityDB.getReqCreatedDevice())
//                                        .reqCreatedReferer(academicCalendarSemesterEntityDB.getReqCreatedReferer())
//                                        .reqUpdatedIP(reqIp)
//                                        .reqUpdatedPort(reqPort)
//                                        .reqUpdatedBrowser(reqBrowser)
//                                        .reqUpdatedOS(reqOs)
//                                        .reqUpdatedDevice(reqDevice)
//                                        .reqUpdatedReferer(reqReferer)
//                                        .build();
//
//                                // update isRegistrationOpen
//                                academicCalendarSemesterEntityDB.setDeletedBy(UUID.fromString(userId));
//                                academicCalendarSemesterEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                academicCalendarSemesterEntityDB.setReqDeletedIP(reqIp);
//                                academicCalendarSemesterEntityDB.setReqDeletedPort(reqPort);
//                                academicCalendarSemesterEntityDB.setReqDeletedBrowser(reqBrowser);
//                                academicCalendarSemesterEntityDB.setReqDeletedOS(reqOs);
//                                academicCalendarSemesterEntityDB.setReqDeletedDevice(reqDevice);
//                                academicCalendarSemesterEntityDB.setReqDeletedReferer(reqReferer);
//
//                                return academicCalendarSemesterRepository.save(academicCalendarSemesterEntityDB)
//                                        .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                        .flatMap(isRegistrationOpenUpdate -> responseSuccessMsg("Status Updated Successfully", isRegistrationOpenUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the Registration.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Registration.Please Contact Developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-enrollment-open_update")
//    public Mono<ServerResponse> isEnrollmentOpen(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    boolean isEnrollmentOpen = Boolean.parseBoolean(value.getFirst("isEnrollmentOpen"));
//                    return academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                            .flatMap(academicCalendarSemesterEntityDB -> {
//                                // If isEnrollmentOpen is not Boolean value
//                                if (isEnrollmentOpen != false && isEnrollmentOpen != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same isEnrollmentOpen exist in database.
//                                if (((academicCalendarSemesterEntityDB.getIsEnrollmentOpen() ? true : false) == isEnrollmentOpen)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                AcademicCalendarSemesterEntity updatedAcademicCalendarSemesterEntity = AcademicCalendarSemesterEntity.builder()
//                                        .uuid(academicCalendarSemesterEntityDB.getUuid())
//                                        .name(academicCalendarSemesterEntityDB.getName())
//                                        .description(academicCalendarSemesterEntityDB.getDescription())
//                                        .academicYear(academicCalendarSemesterEntityDB.getAcademicYear())
//                                        .isEnrollmentOpen(isEnrollmentOpen == true ? true : false)
//                                        .status(academicCalendarSemesterEntityDB.getStatus())
//                                        .isOpen(academicCalendarSemesterEntityDB.getIsOpen())
//                                        .isRegistrationOpen(academicCalendarSemesterEntityDB.getIsRegistrationOpen())
//                                        .isTimetableAllow(academicCalendarSemesterEntityDB.getIsTimetableAllow())
//                                        .startDate(academicCalendarSemesterEntityDB.getStartDate())
//                                        .endDate(academicCalendarSemesterEntityDB.getEndDate())
//                                        .sessionTypeUUID(academicCalendarSemesterEntityDB.getSessionTypeUUID())
//                                        .createdAt(academicCalendarSemesterEntityDB.getCreatedAt())
//                                        .createdBy(academicCalendarSemesterEntityDB.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                        .reqCreatedIP(academicCalendarSemesterEntityDB.getReqCreatedIP())
//                                        .reqCreatedPort(academicCalendarSemesterEntityDB.getReqCreatedPort())
//                                        .reqCreatedBrowser(academicCalendarSemesterEntityDB.getReqCreatedBrowser())
//                                        .reqCreatedOS(academicCalendarSemesterEntityDB.getReqCreatedOS())
//                                        .reqCreatedDevice(academicCalendarSemesterEntityDB.getReqCreatedDevice())
//                                        .reqCreatedReferer(academicCalendarSemesterEntityDB.getReqCreatedReferer())
//                                        .reqUpdatedIP(reqIp)
//                                        .reqUpdatedPort(reqPort)
//                                        .reqUpdatedBrowser(reqBrowser)
//                                        .reqUpdatedOS(reqOs)
//                                        .reqUpdatedDevice(reqDevice)
//                                        .reqUpdatedReferer(reqReferer)
//                                        .build();
//
//                                // update isEnrollmentOpen
//                                academicCalendarSemesterEntityDB.setDeletedBy(UUID.fromString(userId));
//                                academicCalendarSemesterEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                academicCalendarSemesterEntityDB.setReqDeletedIP(reqIp);
//                                academicCalendarSemesterEntityDB.setReqDeletedPort(reqPort);
//                                academicCalendarSemesterEntityDB.setReqDeletedBrowser(reqBrowser);
//                                academicCalendarSemesterEntityDB.setReqDeletedOS(reqOs);
//                                academicCalendarSemesterEntityDB.setReqDeletedDevice(reqDevice);
//                                academicCalendarSemesterEntityDB.setReqDeletedReferer(reqReferer);
//
//                                return academicCalendarSemesterRepository.save(academicCalendarSemesterEntityDB)
//                                        .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                        .flatMap(isEnrollmentOpenUpdate -> responseSuccessMsg("Status Updated Successfully", isEnrollmentOpenUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the Enrollment.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Enrollment.Please Contact Developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_is-timetable-allow_update")
//    public Mono<ServerResponse> isTimetableAllow(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    boolean isTimetableAllow = Boolean.parseBoolean(value.getFirst("isTimetableAllow"));
//                    return academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                            .flatMap(academicCalendarSemesterEntityDB -> {
//                                // If isTimetableAllow is not Boolean value
//                                if (isTimetableAllow != false && isTimetableAllow != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same isTimetableAllow exist in database.
//                                if (((academicCalendarSemesterEntityDB.getIsTimetableAllow() ? true : false) == isTimetableAllow)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                AcademicCalendarSemesterEntity updatedAcademicCalendarSemesterEntity = AcademicCalendarSemesterEntity.builder()
//                                        .uuid(academicCalendarSemesterEntityDB.getUuid())
//                                        .name(academicCalendarSemesterEntityDB.getName())
//                                        .description(academicCalendarSemesterEntityDB.getDescription())
//                                        .academicYear(academicCalendarSemesterEntityDB.getAcademicYear())
//                                        .isTimetableAllow(isTimetableAllow == true ? true : false)
//                                        .status(academicCalendarSemesterEntityDB.getStatus())
//                                        .isOpen(academicCalendarSemesterEntityDB.getIsOpen())
//                                        .isRegistrationOpen(academicCalendarSemesterEntityDB.getIsRegistrationOpen())
//                                        .isEnrollmentOpen(academicCalendarSemesterEntityDB.getIsEnrollmentOpen())
//                                        .startDate(academicCalendarSemesterEntityDB.getStartDate())
//                                        .endDate(academicCalendarSemesterEntityDB.getEndDate())
//                                        .sessionTypeUUID(academicCalendarSemesterEntityDB.getSessionTypeUUID())
//                                        .createdAt(academicCalendarSemesterEntityDB.getCreatedAt())
//                                        .createdBy(academicCalendarSemesterEntityDB.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                        .reqCreatedIP(academicCalendarSemesterEntityDB.getReqCreatedIP())
//                                        .reqCreatedPort(academicCalendarSemesterEntityDB.getReqCreatedPort())
//                                        .reqCreatedBrowser(academicCalendarSemesterEntityDB.getReqCreatedBrowser())
//                                        .reqCreatedOS(academicCalendarSemesterEntityDB.getReqCreatedOS())
//                                        .reqCreatedDevice(academicCalendarSemesterEntityDB.getReqCreatedDevice())
//                                        .reqCreatedReferer(academicCalendarSemesterEntityDB.getReqCreatedReferer())
//                                        .reqUpdatedIP(reqIp)
//                                        .reqUpdatedPort(reqPort)
//                                        .reqUpdatedBrowser(reqBrowser)
//                                        .reqUpdatedOS(reqOs)
//                                        .reqUpdatedDevice(reqDevice)
//                                        .reqUpdatedReferer(reqReferer)
//                                        .build();
//
//                                // update isTimetableAllow
//                                academicCalendarSemesterEntityDB.setDeletedBy(UUID.fromString(userId));
//                                academicCalendarSemesterEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                academicCalendarSemesterEntityDB.setReqDeletedIP(reqIp);
//                                academicCalendarSemesterEntityDB.setReqDeletedPort(reqPort);
//                                academicCalendarSemesterEntityDB.setReqDeletedBrowser(reqBrowser);
//                                academicCalendarSemesterEntityDB.setReqDeletedOS(reqOs);
//                                academicCalendarSemesterEntityDB.setReqDeletedDevice(reqDevice);
//                                academicCalendarSemesterEntityDB.setReqDeletedReferer(reqReferer);
//
//                                return academicCalendarSemesterRepository.save(academicCalendarSemesterEntityDB)
//                                        .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                        .flatMap(isTimetableAllowUpdate -> responseSuccessMsg("Status Updated Successfully", isTimetableAllowUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the Timetable.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the Timetable.Please Contact Developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_status_update")
//    public Mono<ServerResponse> status(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
//                    return academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                            .flatMap(academicCalendarSemesterEntityDB -> {
//                                // If status is not Boolean value
//                                if (status != false && status != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same status exist in database.
//                                if (((academicCalendarSemesterEntityDB.getStatus() ? true : false) == status)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                AcademicCalendarSemesterEntity updatedAcademicCalendarSemesterEntity = AcademicCalendarSemesterEntity.builder()
//                                        .name(academicCalendarSemesterEntityDB.getName())
//                                        .description(academicCalendarSemesterEntityDB.getDescription())
//                                        .academicYear(academicCalendarSemesterEntityDB.getAcademicYear())
//                                        .status(status == true ? true : false)
//                                        .isOpen(academicCalendarSemesterEntityDB.getIsOpen())
//                                        .uuid(academicCalendarSemesterEntityDB.getUuid())
//                                        .startDate(academicCalendarSemesterEntityDB.getStartDate())
//                                        .endDate(academicCalendarSemesterEntityDB.getEndDate())
//                                        .sessionTypeUUID(academicCalendarSemesterEntityDB.getSessionTypeUUID())
//                                        .createdAt(academicCalendarSemesterEntityDB.getCreatedAt())
//                                        .createdBy(academicCalendarSemesterEntityDB.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                        .reqCreatedIP(academicCalendarSemesterEntityDB.getReqCreatedIP())
//                                        .reqCreatedPort(academicCalendarSemesterEntityDB.getReqCreatedPort())
//                                        .reqCreatedBrowser(academicCalendarSemesterEntityDB.getReqCreatedBrowser())
//                                        .reqCreatedOS(academicCalendarSemesterEntityDB.getReqCreatedOS())
//                                        .reqCreatedDevice(academicCalendarSemesterEntityDB.getReqCreatedDevice())
//                                        .reqCreatedReferer(academicCalendarSemesterEntityDB.getReqCreatedReferer())
//                                        .reqUpdatedIP(reqIp)
//                                        .reqUpdatedPort(reqPort)
//                                        .reqUpdatedBrowser(reqBrowser)
//                                        .reqUpdatedOS(reqOs)
//                                        .reqUpdatedDevice(reqDevice)
//                                        .reqUpdatedReferer(reqReferer)
//                                        .build();
//
//                                // update status
//                                academicCalendarSemesterEntityDB.setDeletedBy(UUID.fromString(userId));
//                                academicCalendarSemesterEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                academicCalendarSemesterEntityDB.setReqDeletedIP(reqIp);
//                                academicCalendarSemesterEntityDB.setReqDeletedPort(reqPort);
//                                academicCalendarSemesterEntityDB.setReqDeletedBrowser(reqBrowser);
//                                academicCalendarSemesterEntityDB.setReqDeletedOS(reqOs);
//                                academicCalendarSemesterEntityDB.setReqDeletedDevice(reqDevice);
//                                academicCalendarSemesterEntityDB.setReqDeletedReferer(reqReferer);
//
//                                return academicCalendarSemesterRepository.save(academicCalendarSemesterEntityDB)
//                                        .then(academicCalendarSemesterRepository.save(updatedAcademicCalendarSemesterEntity))
//                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the status.There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please Contact Developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
//    }
//
//    @AuthHasPermission(value = "academic_api_v1_academic-sessions_delete")
//    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
//        UUID academicCalendarSemesterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return academicCalendarSemesterRepository.findByUuidAndDeletedAtIsNull(academicCalendarSemesterUUID)
//                .flatMap(academicCalendarSemesterEntity -> feeStructureRepository.findFirstByAcademicCalendarSemesterUUIDAndDeletedAtIsNull(academicCalendarSemesterEntity.getUuid())
//                                //checking if Academic Session exists in Fee Structures
//                                .flatMap(feeStructureEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
////                        .switchIfEmpty(Mono.defer(() -> attendanceRepository.findFirstByAcademicCalendarSemesterUUIDAndDeletedAtIsNull(academicCalendarSemesterEntity.getUuid())
////                                //checking if Academic Session exists in Attendances
////                                .flatMap(attendanceEntityDB -> responseInfoMsg("Unable to delete record as the reference exists"))))
//                                //checking if Academic Session exists in Academic Calendar
//                                .switchIfEmpty(Mono.defer(() -> academicCalendarRepository.findFirstByAcademicCalendarSemesterUUIDAndDeletedAtIsNull(academicCalendarSemesterEntity.getUuid())
//                                        .flatMap(courseOfferedEntityDB -> responseInfoMsg("Unable to delete record as the reference exists"))))
//                                //checking if Academic Session exists in Subject Offered
//                                .switchIfEmpty(Mono.defer(() -> subjectOfferedRepository.findFirstByAcademicCalendarSemesterUUIDAndDeletedAtIsNull(academicCalendarSemesterEntity.getUuid())
//                                        .flatMap(courseOfferedEntityDB -> responseInfoMsg("Unable to delete record as the reference exists"))))
//                                .switchIfEmpty(Mono.defer(() -> {
//
//                                    academicCalendarSemesterEntity.setDeletedBy(UUID.fromString(userId));
//                                    academicCalendarSemesterEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                    academicCalendarSemesterEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
//                                    academicCalendarSemesterEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
//                                    academicCalendarSemesterEntity.setReqDeletedIP(reqIp);
//                                    academicCalendarSemesterEntity.setReqDeletedPort(reqPort);
//                                    academicCalendarSemesterEntity.setReqDeletedBrowser(reqBrowser);
//                                    academicCalendarSemesterEntity.setReqDeletedOS(reqOs);
//                                    academicCalendarSemesterEntity.setReqDeletedDevice(reqDevice);
//                                    academicCalendarSemesterEntity.setReqDeletedReferer(reqReferer);
//
//
//                                    return academicCalendarSemesterRepository.save(academicCalendarSemesterEntity)
//                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
//                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
//                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
//                                }))
//                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
//    }
//
//
//    public Mono<ServerResponse> responseInfoMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.INFO,
//                        msg
//                )
//        );
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//
//    public Mono<ServerResponse> responseIndexInfoMsg(String msg, Long totalDataRowsWithFilter) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.INFO,
//                        msg
//                )
//        );
//
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                totalDataRowsWithFilter,
//                0L,
//                messages,
//                Mono.empty()
//
//        );
//    }
//
//    public Mono<ServerResponse> responseIndexSuccessMsg(String msg, Object entity, Long totalDataRowsWithFilter) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.SUCCESS,
//                        msg)
//        );
//
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                totalDataRowsWithFilter,
//                0L,
//                messages,
//                Mono.just(entity)
//        );
//    }
//
//    public Mono<ServerResponse> responseErrorMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.ERROR,
//                        msg
//                )
//        );
//
//        return appresponse.set(
//                HttpStatus.BAD_REQUEST.value(),
//                HttpStatus.BAD_REQUEST.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//
//
//    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.SUCCESS,
//                        msg
//                )
//        );
//        return appresponse.set(
//                HttpStatus.OK.value(),
//                HttpStatus.OK.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.just(entity)
//        );
//    }
//
//    public Mono<ServerResponse> responseWarningMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.WARNING,
//                        msg
//                )
//        );
//
//        return appresponse.set(
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                HttpStatus.UNPROCESSABLE_ENTITY.name(),
//                null,
//                "eng",
//                "token",
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//        );
//    }
//}
