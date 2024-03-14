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
//import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineChapterEntity;
//import tuf.webscaf.app.dbContext.master.repositry.SubjectOutlineChapterRepository;
//import tuf.webscaf.app.dbContext.master.repositry.SubjectOutlineRepository;
//import tuf.webscaf.app.dbContext.master.repositry.SubjectOutlineTopicRepository;
//import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineChapterEntity;
//import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectOutlineChapterRepository;
//import tuf.webscaf.config.service.response.AppResponse;
//import tuf.webscaf.config.service.response.AppResponseMessage;
//import tuf.webscaf.config.service.response.CustomResponse;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Tag(name = "subjectOutlineChapterHandler")
//@Component
//public class SubjectOutlineChapterHandler {
//
//    @Value("${server.zone}")
//    private String zone;
//
//    @Autowired
//    CustomResponse appresponse;
//
//    @Autowired
//    SubjectOutlineChapterRepository subjectOutlineChapterRepository;
//
//    @Autowired
//    SlaveSubjectOutlineChapterRepository slaveSubjectOutlineChapterRepository;
//
//    @Autowired
//    SubjectOutlineRepository subjectOutlineRepository;
//
//    @Autowired
//    SubjectOutlineTopicRepository subjectOutlineTopicRepository;
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
//        Optional<String> status = serverRequest.queryParam("status");
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));
//
//        if (status.isPresent()) {
//            Flux<SlaveSubjectOutlineChapterEntity> slaveSubjectOutlineChapterFlux = slaveSubjectOutlineChapterRepository
//                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
//                            searchKeyWord, Boolean.valueOf(status.get()), searchKeyWord, Boolean.valueOf(status.get()));
//
//            return slaveSubjectOutlineChapterFlux
//                    .collectList()
//                    .flatMap(subjectOutlineChapterEntityDB -> slaveSubjectOutlineChapterRepository
//                            .countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
//                                    Boolean.valueOf(status.get()), searchKeyWord, Boolean.valueOf(status.get()))
//                            .flatMap(count -> {
//                                if (subjectOutlineChapterEntityDB.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineChapterEntityDB, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        } else {
//            Flux<SlaveSubjectOutlineChapterEntity> slaveSubjectOutlineChapterFlux = slaveSubjectOutlineChapterRepository
//                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
//
//            return slaveSubjectOutlineChapterFlux
//                    .collectList()
//                    .flatMap(subjectOutlineChapterEntityDB -> slaveSubjectOutlineChapterRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
//                            .flatMap(count -> {
//                                if (subjectOutlineChapterEntityDB.isEmpty()) {
//                                    return responseIndexInfoMsg("Record does not exist", count);
//                                } else {
//                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineChapterEntityDB, count);
//                                }
//                            })
//                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
//                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
//        }
//    }
//
//    public Mono<ServerResponse> show(ServerRequest serverRequest) {
//        UUID subOutlineChapterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
//
//        return slaveSubjectOutlineChapterRepository.findByUuidAndDeletedAtIsNull(subOutlineChapterUUID)
//                .flatMap(subjectOutLineChapterEntityDB -> responseSuccessMsg("Record Fetched Successfully", subjectOutLineChapterEntityDB))
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
//
//                    SubjectOutlineChapterEntity subjectOutlineChapEntity = SubjectOutlineChapterEntity.builder()
//                            .uuid(UUID.randomUUID())
//                            .chapterNo(Integer.valueOf(value.getFirst("chapterNo").trim()))
//                            .name(value.getFirst("name").trim())
//                            .description(value.getFirst("description").trim())
//                            .subjectOutlineUUID(UUID.fromString(value.getFirst("subjectOutlineUUID").trim()))
//                            .status(Boolean.valueOf(value.getFirst("status")))
//                            .createdAt(LocalDateTime.now(ZoneId.of(zone)))
//                            .createdBy(UUID.fromString(userId))
//                            .build();
//
////                    check subject outline chapter uuid exists
//                    return subjectOutlineRepository.findByUuidAndDeletedAtIsNull(subjectOutlineChapEntity.getSubjectOutlineUUID())
//                            // check subject outline chapter name is unique
//                            .flatMap(checkSubOutlineMsg -> subjectOutlineChapterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(subjectOutlineChapEntity.getName())
//                                    .flatMap(checkMsg -> responseInfoMsg("Name Already Exists"))
//                                    .switchIfEmpty(Mono.defer(() -> subjectOutlineChapterRepository.save(subjectOutlineChapEntity)
//                                            .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity)
//                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
//                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
//                                            )))
//                            ).switchIfEmpty(responseInfoMsg("Subject Outline Does not exist."))
//                            .onErrorResume(ex -> responseErrorMsg("Subject Outline Does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID subOutlineChapterId = UUID.fromString((serverRequest.pathVariable("uuid")));
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
//                .flatMap(value -> subjectOutlineChapterRepository.findByUuidAndDeletedAtIsNull(subOutlineChapterId)
//                        .flatMap(previousSubjectOutlineEntity -> {
//
//                            SubjectOutlineChapterEntity updatedSubjectOutlineChapEntity = SubjectOutlineChapterEntity.builder()
//                                    .uuid(previousSubjectOutlineEntity.getUuid())
//                                    .chapterNo(Integer.valueOf(value.getFirst("chapterNo")))
//                                    .name(value.getFirst("name").trim())
//                                    .description(value.getFirst("description").trim())
//                                    .subjectOutlineUUID(UUID.fromString(value.getFirst("subjectOutlineUUID").trim()))
//                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                    .createdAt(previousSubjectOutlineEntity.getCreatedAt())
//                                    .createdBy(previousSubjectOutlineEntity.getCreatedBy())
//                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                    .updatedBy(UUID.fromString(userId))
//                                    .build();
//
//                            previousSubjectOutlineEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                            previousSubjectOutlineEntity.setDeletedBy(UUID.fromString(userId));
//
//                            //check subject outline chapter uuid exists
//                            return subjectOutlineRepository.findByUuidAndDeletedAtIsNull(updatedSubjectOutlineChapEntity.getSubjectOutlineUUID())
//                                    // check subject outline chapter name is unique
//                                    .flatMap(subjectOutline -> subjectOutlineChapterRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedSubjectOutlineChapEntity.getName(), subOutlineChapterId)
//                                            .flatMap(checkNameMsg -> responseInfoMsg("Name Already Exist."))
//                                            .switchIfEmpty(Mono.defer(() -> subjectOutlineChapterRepository.save(previousSubjectOutlineEntity)
//                                                    .then(subjectOutlineChapterRepository.save(updatedSubjectOutlineChapEntity))
//                                                    .flatMap(saveChapterEntity -> responseSuccessMsg("Record Updated Successfully", saveChapterEntity)
//                                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
//                                                            .onErrorResume(err -> responseErrorMsg("Unable to Update Record.Please Contact Developer."))
//                                                    )))
//                                    ).switchIfEmpty(responseInfoMsg("Subject Outline Does not exist."))
//                                    .onErrorResume(ex -> responseErrorMsg("Subject Outline Does not exist.Please Contact Developer."));
//                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
//                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
//        UUID subOutlineChapterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
//                .flatMap(value -> subjectOutlineChapterRepository.findByUuidAndDeletedAtIsNull(subOutlineChapterUUID)
//                        //Check if Subject Outline Chapter Exists in Subject Outline Topic
//                        .flatMap(subOutlineChapEntityDB -> subjectOutlineTopicRepository.findFirstBySubjectOutlineChapterUUIDAndDeletedAtIsNull(subOutlineChapEntityDB.getUuid())
//                                .flatMap(topicMsg -> responseInfoMsg("Unable to Delete Record as the Reference Exists"))
//                                .switchIfEmpty(Mono.defer(() -> {
//                                    subOutlineChapEntityDB.setDeletedBy(UUID.fromString(userId));
//                                    subOutlineChapEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//
//                                    return subjectOutlineChapterRepository.save(subOutlineChapEntityDB)
//                                            .flatMap(saveChapterEntity -> responseSuccessMsg("Record Deleted Successfully", saveChapterEntity))
//                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
//                                            .onErrorResume(err -> responseErrorMsg("Unable to delete record.Please contact developer."));
//                                }))
//                        ).switchIfEmpty(responseInfoMsg("Record does not exist"))
//                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
//    }
//
//    public Mono<ServerResponse> status(ServerRequest serverRequest) {
//        UUID subOutlineChapterUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
//                    return subjectOutlineChapterRepository.findByUuidAndDeletedAtIsNull(subOutlineChapterUUID)
//                            .flatMap(previousSubjectOutlineEntity -> {
//                                // If status is not Boolean value
//                                if (status != false && status != true) {
//                                    return responseInfoMsg("Status must be Active or InActive");
//                                }
//
//                                // If already same status exist in database.
//                                if (((previousSubjectOutlineEntity.getStatus() ? true : false) == status)) {
//                                    return responseWarningMsg("Record already exist with same status");
//                                }
//
//                                SubjectOutlineChapterEntity updatedSubjectOutlineChapEntity = SubjectOutlineChapterEntity.builder()
//                                        .uuid(previousSubjectOutlineEntity.getUuid())
//                                        .chapterNo(previousSubjectOutlineEntity.getChapterNo())
//                                        .name(previousSubjectOutlineEntity.getName())
//                                        .description(previousSubjectOutlineEntity.getDescription())
//                                        .subjectOutlineUUID(previousSubjectOutlineEntity.getSubjectOutlineUUID())
//                                        .status(status == true ? true : false)
//                                        .createdAt(previousSubjectOutlineEntity.getCreatedAt())
//                                        .createdBy(previousSubjectOutlineEntity.getCreatedBy())
//                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                        .updatedBy(UUID.fromString(userId))
//                                        .build();
//
//                                previousSubjectOutlineEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                previousSubjectOutlineEntity.setDeletedBy(UUID.fromString(userId));
//
//                                return subjectOutlineChapterRepository.save(previousSubjectOutlineEntity)
//                                        .then(subjectOutlineChapterRepository.save(updatedSubjectOutlineChapEntity))
//                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
//                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
//                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
//                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
//                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
//                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
//                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
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
