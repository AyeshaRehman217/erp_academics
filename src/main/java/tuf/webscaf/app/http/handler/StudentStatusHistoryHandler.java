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
//import tuf.webscaf.app.dbContext.master.entity.StudentStatusHistoryEntity;
//import tuf.webscaf.app.dbContext.master.repositry.StudentRepository;
//import tuf.webscaf.app.dbContext.master.repositry.StudentStatusHistoryRepository;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentStatusHistoryEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentStatusHistoryRepository;
//import tuf.webscaf.config.service.response.AppResponse;
//import tuf.webscaf.config.service.response.AppResponseMessage;
//import tuf.webscaf.config.service.response.CustomResponse;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.UUID;
//
//@Tag(name = "studentStatusHistoryHandler")
//@Component
//public class StudentStatusHistoryHandler {
//    @Value("${server.zone}")
//    private String zone;
//
//    @Autowired
//    CustomResponse appresponse;
//
//    @Autowired
//    StudentStatusHistoryRepository studentStatusHistoryRepository;
//
//    @Autowired
//    SlaveStudentStatusHistoryRepository slaveStudentStatusHistoryRepository;
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
//        Flux<SlaveStudentStatusHistoryEntity> slaveStudentStatusHistoryFlux = slaveStudentStatusHistoryRepository
//                .findAllBySubjectContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord);
//        return slaveStudentStatusHistoryFlux
//                .collectList()
//                .flatMap(studentStatusHistoryEntity -> slaveStudentStatusHistoryRepository.countBySubjectContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord)
//                        .flatMap(count -> {
//                            if (studentStatusHistoryEntity.isEmpty()) {
//                                return responseIndexInfoMsg("Record does not exist", count);
//                            } else {
//                                return responseIndexSuccessMsg("All Records Fetched Successfully", studentStatusHistoryEntity, count);
//                            }
//                        })
//                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> show(ServerRequest serverRequest) {
//        final long Id = Long.parseLong(serverRequest.pathVariable("id"));
//
//        return slaveStudentStatusHistoryRepository.findByIdAndDeletedAtIsNull(Id)
//                .flatMap(studentStatusHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentStatusHistoryEntity))
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
//        return serverRequest.formData()
//                .flatMap(value -> {
//                    StudentStatusHistoryEntity entity = StudentStatusHistoryEntity.builder()
//                            .uuid(UUID.randomUUID())
//                            .status(Boolean.valueOf(value.getFirst("status")))
//                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
//                            .text(value.getFirst("text").trim())
//                            .subject(value.getFirst("subject").trim())
//                            .isDeleted(Boolean.parseBoolean(value.getFirst("isDeleted")))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .build();
//                    return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
//                            .flatMap(emailTypeEntity -> studentStatusHistoryRepository.save(entity)
//                                    .flatMap(emailEntity -> responseSuccessMsg("Record Stored Successfully", entity))
//                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
//                                    .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
//                            ).switchIfEmpty(responseInfoMsg("Student does not exist")).onErrorResume(ex -> responseErrorMsg("Student does not exist. Please contact developer"));
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
//
//        return serverRequest.formData()
//                .flatMap(value -> studentStatusHistoryRepository.findByIdAndDeletedAtIsNull(studentStatusId)
//                        .flatMap(entity -> {
//                            StudentStatusHistoryEntity updatedEntity = StudentStatusHistoryEntity.builder()
//                                    .uuid(entity.getUuid())
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
//                                    .text(value.getFirst("text").trim())
//                                    .subject(value.getFirst("subject").trim())
//                                    .isDeleted(Boolean.parseBoolean(value.getFirst("isDeleted")))
//                                    .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .createdBy(UUID.fromString(userId))
//                                    .updatedBy(UUID.fromString(userId))
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .build();
//                            entity.setDeletedBy(UUID.fromString(userId));
//                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//
//                            return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
//                                    .flatMap(emailTypeEntity -> studentStatusHistoryRepository.save(entity)
//                                            .then(studentStatusHistoryRepository.save(updatedEntity))
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
//                    return studentStatusHistoryRepository.findByIdAndDeletedAtIsNull(studentStatusId)
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
//                                StudentStatusHistoryEntity entity = StudentStatusHistoryEntity.builder()
//                                        .uuid(val.getUuid())
//                                        .status(status == true ? true : false)
//                                        .studentUUID(val.getStudentUUID())
//                                        .text(val.getText())
//                                        .subject(val.getSubject())
//                                        .isDeleted(val.isDeleted())
//                                        .createdAt(val.getCreatedAt())
//                                        .createdBy(val.getCreatedBy())
//                                        .updatedBy(UUID.fromString(userId))
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .build();
//
//                                // update status
//                                val.setDeletedBy(UUID.fromString(userId));
//                                val.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                return studentStatusHistoryRepository.save(val)
//                                        .then(studentStatusHistoryRepository.save(entity))
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
//        return studentStatusHistoryRepository.findByIdAndDeletedAtIsNull(Id)
//                .flatMap(studentStatusHistoryEntity -> {
//                    studentStatusHistoryEntity.setDeletedBy(UUID.fromString(userId));
//                    studentStatusHistoryEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                    return studentStatusHistoryRepository.save(studentStatusHistoryEntity)
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
