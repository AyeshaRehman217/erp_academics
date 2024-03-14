//package tuf.webscaf.app.http.handler;
//
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
//import tuf.webscaf.app.dbContext.master.entity.StudentStatusEntity;
//import tuf.webscaf.app.dbContext.master.repositry.StudentRepository;
//import tuf.webscaf.app.dbContext.master.repositry.StudentStatusRepository;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentStatusEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentStatusRepository;
//import tuf.webscaf.config.service.response.AppResponse;
//import tuf.webscaf.config.service.response.AppResponseMessage;
//import tuf.webscaf.config.service.response.CustomResponse;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//@Tag(name = "studentStatusHandler")
//@Component
//public class StudentStatusHandler {
//    @Value("${server.zone}")
//    private String zone;
//
//    @Autowired
//    CustomResponse appresponse;
//
//    @Autowired
//    StudentStatusRepository studentStatusRepository;
//
//    @Autowired
//    SlaveStudentStatusRepository slaveStudentStatusRepository;
//
//    @Autowired
//    StudentRepository studentRepository;
//
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
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//        Flux<SlaveStudentStatusEntity> slaveStudentStatusFlux = slaveStudentStatusRepository
//                .findAllByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, pageable);
//        return slaveStudentStatusFlux
//                .collectList()
//                .flatMap(studentStatusEntity -> slaveStudentStatusRepository.countByDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
//                        .flatMap(count -> {
//                            if (studentStatusEntity.isEmpty()) {
//                                return responseIndexInfoMsg("Record does not exist", count);
//                            } else {
//                                return responseIndexSuccessMsg("All Records Fetched Successfully", studentStatusEntity, count);
//                            }
//                        })
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> show(ServerRequest serverRequest) {
//        final long Id = Long.parseLong(serverRequest.pathVariable("id"));
//
//        return slaveStudentStatusRepository.findByIdAndDeletedAtIsNull(Id)
//                .flatMap(studentStatusEntity -> responseSuccessMsg("Record Fetched Successfully", studentStatusEntity))
//                .switchIfEmpty(responseInfoMsg("Record does not exist"))
//                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> store(ServerRequest serverRequest) {
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        //Creating an Empty Status List to Add All the Boolean Values of Status
//        List<Boolean> statusList = new ArrayList<>();
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    StudentStatusEntity entity = StudentStatusEntity.builder()
//                            .uuid(UUID.randomUUID())
//                            .status(Boolean.valueOf(value.getFirst("status")))
//                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
//                            .description(value.getFirst("description").trim())
//                            .isApplicant(Boolean.valueOf(value.getFirst("isApplicant")))
//                            .isCandidate(Boolean.valueOf(value.getFirst("isCandidate")))
//                            .isStudent(Boolean.valueOf(value.getFirst("isStudent")))
//                            .isPassedOut(Boolean.valueOf(value.getFirst("isPassedOut")))
//                            .isDroppedOut(Boolean.valueOf(value.getFirst("isDroppedOut")))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .build();
//
//                    Boolean applicantStudent = Boolean.parseBoolean(value.getFirst("isApplicant"));
//
//                    Boolean candidateStudent = Boolean.parseBoolean(value.getFirst("isCandidate"));
//
//                    Boolean student = Boolean.parseBoolean(value.getFirst("isStudent"));
//
//                    Boolean passOutStudent = Boolean.parseBoolean(value.getFirst("isPassedOut"));
//
//                    Boolean dropOutStudent = Boolean.parseBoolean(value.getFirst("isDroppedOut"));
//
//                    //Adding All the Boolean values in Status List
//                    statusList.add(applicantStudent);
//                    statusList.add(candidateStudent);
//                    statusList.add(student);
//                    statusList.add(passOutStudent);
//                    statusList.add(dropOutStudent);
//
//
//                    //Checking the Occurrence of Status where Status is True
//                    int statusTrueOccurrences = Collections.frequency(statusList, Boolean.TRUE);
//
//                    //Check if User Selects True More than once than return response
//                    if (statusTrueOccurrences > 1) {
//                        return responseInfoMsg("Please Select One Status Only");
//                    }
//
//                    return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
//                            .flatMap(emailTypeEntity -> studentStatusRepository.save(entity)
//                                    .flatMap(emailEntity -> responseSuccessMsg("Record Stored Successfully", entity))
//                                    .switchIfEmpty(responseInfoMsg("Unable to store record.There is something wrong please try again."))
//                                    .onErrorResume(ex -> responseInfoMsg("Unable to store record. Please contact developer."))
//                            ).switchIfEmpty(responseInfoMsg("Student does not exist"))
//                            .onErrorResume(ex -> responseErrorMsg("Student does not exist. Please contact developer"));
//                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        final long studentStatusId = Long.parseLong(serverRequest.pathVariable("id"));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//        //Creating an Empty Status List to Add All the Boolean Values of Status
//        List<Boolean> statusList = new ArrayList<>();
//
//        return serverRequest.formData()
//                .flatMap(value -> studentStatusRepository.findByIdAndDeletedAtIsNull(studentStatusId)
//                        .flatMap(entity -> {
//                            StudentStatusEntity updatedEntity = StudentStatusEntity.builder()
//                                    .uuid(entity.getUuid())
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID")))
//                                    .description(value.getFirst("description").trim())
//                                    .isApplicant(Boolean.valueOf(value.getFirst("isApplicant")))
//                                    .isCandidate(Boolean.valueOf(value.getFirst("isCandidate")))
//                                    .isStudent(Boolean.valueOf(value.getFirst("isStudent")))
//                                    .isPassedOut(Boolean.valueOf(value.getFirst("isPassedOut")))
//                                    .isDroppedOut(Boolean.valueOf(value.getFirst("isDroppedOut")))
//                                    .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .createdBy(UUID.fromString(userId))
//                                    .updatedBy(UUID.fromString(userId))
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .build();
//
//                            entity.setDeletedBy(UUID.fromString(userId));
//                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//
//                            Boolean applicantStudent = Boolean.parseBoolean(value.getFirst("isApplicant"));
//
//                            Boolean candidateStudent = Boolean.parseBoolean(value.getFirst("isCandidate"));
//
//                            Boolean student = Boolean.parseBoolean(value.getFirst("isStudent"));
//
//                            Boolean passOutStudent = Boolean.parseBoolean(value.getFirst("isPassedOut"));
//
//                            Boolean dropOutStudent = Boolean.parseBoolean(value.getFirst("isDroppedOut"));
//
//                            //Adding All the Boolean values in Status List
//                            statusList.add(applicantStudent);
//                            statusList.add(candidateStudent);
//                            statusList.add(student);
//                            statusList.add(passOutStudent);
//                            statusList.add(dropOutStudent);
//
//
//                            //Checking the Occurrence of Status where Status is True
//                            int statusTrueOccurrences = Collections.frequency(statusList, Boolean.TRUE);
//
//                            //Check if User Selects True More than once than return response
//                            if (statusTrueOccurrences > 1) {
//                                return responseInfoMsg("Please Select One Status Only");
//                            }
//
//                            return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
//                                    .flatMap(emailTypeEntity -> studentStatusRepository.save(entity)
//                                            .then(studentStatusRepository.save(updatedEntity))
//                                            .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
//                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
//                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
//                                    ).switchIfEmpty(responseInfoMsg("Student does not exist.")).onErrorResume(ex -> responseErrorMsg("Student does not exist. Please contact developer"));
//                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))).onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
//                .switchIfEmpty(responseInfoMsg("Unable to read request")).onErrorResume(ex -> responseErrorMsg("Unable to read request"));
//    }
//
//    public Mono<ServerResponse> status(ServerRequest serverRequest) {
//        final long studentStatusId = Long.parseLong(serverRequest.pathVariable("id"));
//        String userId = serverRequest.headers().firstHeader("auid");
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
//                    return studentStatusRepository.findByIdAndDeletedAtIsNull(studentStatusId)
//                            .flatMap(val -> {
//                                // If status is not Boolean value
//                                if (status != false && status != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same status exist in database.
//                                if (((val.getStatus() ? true : false) == status)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                StudentStatusEntity entity = StudentStatusEntity.builder()
//                                        .uuid(val.getUuid())
//                                        .status(status == true ? true : false)
//                                        .studentUUID(val.getStudentUUID())
//                                        .description(val.getDescription())
//                                        .isApplicant(val.getIsApplicant())
//                                        .isCandidate(val.getIsCandidate())
//                                        .isStudent(val.getIsStudent())
//                                        .isPassedOut(val.getIsPassedOut())
//                                        .isDroppedOut(val.getIsDroppedOut())
//                                        .createdAt(val.getCreatedAt())
//                                        .createdBy(val.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .build();
//
//                                // update status
//                                val.setDeletedBy(UUID.fromString(userId));
//                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                return studentStatusRepository.save(val)
//                                        .then(studentStatusRepository.save(entity))
//                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
//        final long Id = Long.parseLong(serverRequest.pathVariable("id"));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        if (userId == null) {
//            return responseWarningMsg("Unknown User");
//        } else {
//            if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
//                return responseWarningMsg("Unknown User");
//            }
//        }
//
//        return studentStatusRepository.findByIdAndDeletedAtIsNull(Id)
//                .flatMap(studentStatusEntity -> {
//                    studentStatusEntity.setDeletedBy(UUID.fromString(userId));
//                    studentStatusEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                    return studentStatusRepository.save(studentStatusEntity)
//                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
//                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
//                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
//                })
//                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> responseInfoMsg(String msg) {
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
//                0L,
//                0L,
//                messages,
//                Mono.empty()
//
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
//    public Mono<ServerResponse> responseSuccessMsg(String msg, Object entity) {
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
//                0L,
//                0L,
//                messages,
//                Mono.just(entity)
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
//    public Mono<ServerResponse> responseWarningMsg(String msg) {
//        var messages = List.of(
//                new AppResponseMessage(
//                        AppResponse.Response.WARNING,
//                        msg)
//        );
//
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
