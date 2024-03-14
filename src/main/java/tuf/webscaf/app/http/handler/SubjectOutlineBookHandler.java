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
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineBookEntity;
import tuf.webscaf.app.dbContext.master.repositry.SubjectOutlineBookRepository;
import tuf.webscaf.app.dbContext.master.repositry.SubjectOutlineRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineBookEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveSubjectOutlineBookEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectOutlineBookRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "subjectOutlineBookHandler")
@Component
public class SubjectOutlineBookHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectOutlineBookRepository subjectOutlineBookRepository;

    @Autowired
    SlaveSubjectOutlineBookRepository slaveSubjectOutlineBookRepository;

    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;

    @AuthHasPermission(value = "academic_api_v1_subject-outline-books_index")
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

        String subjectOutlineUUID = serverRequest.queryParam("subjectOutlineUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !subjectOutlineUUID.isEmpty()) {
            Flux<SlaveSubjectOutlineBookEntity> slaveSubjectOutlineBookFlux = slaveSubjectOutlineBookRepository
                    .findAllByTitleContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID));

            return slaveSubjectOutlineBookFlux
                    .collectList()
                    .flatMap(subjectOutlineBookEntity -> slaveSubjectOutlineBookRepository.countByTitleContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndSubjectOutlineUUIDAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID), searchKeyWord, Boolean.valueOf(status), UUID.fromString(subjectOutlineUUID))
                            .flatMap(count -> {
                                if (subjectOutlineBookEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineBookEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveSubjectOutlineBookEntity> slaveSubjectOutlineBookFlux = slaveSubjectOutlineBookRepository
                    .findAllByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveSubjectOutlineBookFlux
                    .collectList()
                    .flatMap(subjectOutlineBookEntity -> slaveSubjectOutlineBookRepository.countByTitleContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (subjectOutlineBookEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineBookEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!subjectOutlineUUID.isEmpty()) {
            Flux<SlaveSubjectOutlineBookEntity> slaveSubjectOutlineBookFlux = slaveSubjectOutlineBookRepository
                    .findAllByTitleContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(pageable, searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID));

            return slaveSubjectOutlineBookFlux
                    .collectList()
                    .flatMap(subjectOutlineBookEntity -> slaveSubjectOutlineBookRepository.countByTitleContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndSubjectOutlineUUIDAndDeletedAtIsNull(searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID), searchKeyWord, UUID.fromString(subjectOutlineUUID))
                            .flatMap(count -> {
                                if (subjectOutlineBookEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineBookEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectOutlineBookEntity> slaveSubjectOutlineBookFlux = slaveSubjectOutlineBookRepository
                    .findAllByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveSubjectOutlineBookFlux
                    .collectList()
                    .flatMap(subjectOutlineBookEntity -> slaveSubjectOutlineBookRepository.countByTitleContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrEditionContainingIgnoreCaseAndDeletedAtIsNullOrIsbnContainingIgnoreCaseAndDeletedAtIsNullOrPublisherNameContainingIgnoreCaseAndDeletedAtIsNullOrAuthorContainingIgnoreCaseAndDeletedAtIsNullOrUrlContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (subjectOutlineBookEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", subjectOutlineBookEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-books_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectOutlineBookUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveSubjectOutlineBookRepository.findByUuidAndDeletedAtIsNull(subjectOutlineBookUUID)
                .flatMap(subjectOutlineBookEntity -> responseSuccessMsg("Record Fetched Successfully", subjectOutlineBookEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-books_store")
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

                    SubjectOutlineBookEntity entity = SubjectOutlineBookEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .title(value.getFirst("title").trim())
                            .edition(value.getFirst("edition").trim())
                            .isbn(value.getFirst("isbn").trim())
                            .url(value.getFirst("url").trim())
                            .publisherName(value.getFirst("publisherName").trim())
                            .description(value.getFirst("description").trim())
                            .author(value.getFirst("author").trim())
                            .subjectOutlineUUID(UUID.fromString(value.getFirst("subjectOutlineUUID").trim()))
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

                    return subjectOutlineBookRepository.findFirstByTitleAndAuthorAndEditionAndDeletedAtIsNull(entity.getTitle(), entity.getAuthor(), entity.getEdition())
                            .flatMap(checkUniquenessName -> responseInfoMsg("Record Against this Title Author and Edition Already exists"))
                            //checks if subject outline uuid exists
                            .switchIfEmpty(Mono.defer(() -> subjectOutlineRepository.findByUuidAndDeletedAtIsNull(entity.getSubjectOutlineUUID())
                                    .flatMap(subjectOutlineEntity -> subjectOutlineBookRepository.save(entity)
                                            .flatMap(subjectOutlineBookEntity -> responseSuccessMsg("Record Stored Successfully", subjectOutlineBookEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Subject Outline does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Subject Outline does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-books_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectOutlineBookUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectOutlineBookRepository.findByUuidAndDeletedAtIsNull(subjectOutlineBookUUID)
                        .flatMap(previousEntity -> {

                            SubjectOutlineBookEntity updatedEntity = SubjectOutlineBookEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .title(value.getFirst("title").trim())
                                    .edition(value.getFirst("edition").trim())
                                    .isbn(value.getFirst("isbn").trim())
                                    .url(value.getFirst("url").trim())
                                    .publisherName(value.getFirst("publisherName").trim())
                                    .description(value.getFirst("description").trim())
                                    .author(value.getFirst("author").trim())
                                    .subjectOutlineUUID(UUID.fromString(value.getFirst("subjectOutlineUUID")))
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

                            return subjectOutlineBookRepository.findFirstByTitleAndAuthorAndEditionAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTitle(), updatedEntity.getAuthor(), updatedEntity.getEdition(), subjectOutlineBookUUID)
                                    .flatMap(checkUniquenessName -> responseInfoMsg("Record Against this Title Author and Edition Already exists"))
                                    //checks if subject outline uuid exists
                                    .switchIfEmpty(Mono.defer(() -> subjectOutlineRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSubjectOutlineUUID())
                                            .flatMap(subjectOutlineEntity -> subjectOutlineBookRepository.save(previousEntity)
                                                    .then(subjectOutlineBookRepository.save(updatedEntity))
                                                    .flatMap(subjectOutlineBookEntity -> responseSuccessMsg("Record Updated Successfully", subjectOutlineBookEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to Update record. There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to Update record. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Subject Outline does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Subject Outline does not exist. Please contact developer."))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-books_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectOutlineBookUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectOutlineBookRepository.findByUuidAndDeletedAtIsNull(subjectOutlineBookUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectOutlineBookEntity entity = SubjectOutlineBookEntity.builder()
                                        .uuid(val.getUuid())
                                        .title(val.getTitle())
                                        .isbn(val.getIsbn())
                                        .author(val.getAuthor())
                                        .url(val.getUrl())
                                        .edition(val.getEdition())
                                        .description(val.getDescription())
                                        .publisherName(val.getPublisherName())
                                        .subjectOutlineUUID(val.getSubjectOutlineUUID())
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

                                return subjectOutlineBookRepository.save(val)
                                        .then(subjectOutlineBookRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-books_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectOutlineBookUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectOutlineBookRepository.findByUuidAndDeletedAtIsNull(subjectOutlineBookUUID)
                .flatMap(subjectOutlineBookEntity -> {

                    subjectOutlineBookEntity.setDeletedBy(UUID.fromString(userId));
                    subjectOutlineBookEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    subjectOutlineBookEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    subjectOutlineBookEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    subjectOutlineBookEntity.setReqDeletedIP(reqIp);
                    subjectOutlineBookEntity.setReqDeletedPort(reqPort);
                    subjectOutlineBookEntity.setReqDeletedBrowser(reqBrowser);
                    subjectOutlineBookEntity.setReqDeletedOS(reqOs);
                    subjectOutlineBookEntity.setReqDeletedDevice(reqDevice);
                    subjectOutlineBookEntity.setReqDeletedReferer(reqReferer);

                    return subjectOutlineBookRepository.save(subjectOutlineBookEntity)
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
