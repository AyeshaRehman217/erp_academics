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
import tuf.webscaf.app.dbContext.master.entity.SubjectOutlineOfferedEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectOutlineOfferedDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveSubjectOutlineOfferedRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "subjectOutlineOfferedHandler")
@Component
public class SubjectOutlineOfferedHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    SubjectOutlineOfferedRepository subjectOutlineOfferedRepository;

    @Autowired
    SlaveSubjectOutlineOfferedRepository slaveSubjectOutlineOfferedRepository;

    @Autowired
    SubjectOfferedRepository subjectOfferedRepository;

    @Autowired
    CourseSubjectRepository courseSubjectRepository;

    @Autowired
    SubjectOutlineRepository subjectOutlineRepository;

    @Autowired
    SubjectObeRepository subjectObeRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Value("${server.zone}")
    private String zone;

    @AuthHasPermission(value = "academic_api_v1_subject-outline-offered_index")
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

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // Optional Query Parameter of OBE
        String obe = serverRequest.queryParam("obe").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !obe.isEmpty()) {
            if (Boolean.parseBoolean(obe)) {
                Flux<SlaveSubjectOutlineOfferedDto> slaveSubjectOutlineOfferedFlux = slaveSubjectOutlineOfferedRepository
                        .indexSubjectOutlineOfferedWithStatusAndOBETrue(Boolean.valueOf(obe), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

                return slaveSubjectOutlineOfferedFlux
                        .collectList()
                        .flatMap(outlineOfferedEntity -> slaveSubjectOutlineOfferedRepository
                                .countSubjectOutlineOfferedWithOBETrueAndStatus(Boolean.valueOf(status), Boolean.valueOf(obe), searchKeyWord)
                                .flatMap(count -> {
                                    if (outlineOfferedEntity.isEmpty()) {
                                        return responseIndexInfoMsg("Record does not exist", count);
                                    } else {
                                        return responseIndexSuccessMsg("All Records Fetched Successfully", outlineOfferedEntity, count);
                                    }
                                })
                        ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                        .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
            } else {
                Flux<SlaveSubjectOutlineOfferedDto> slaveSubjectOutlineOfferedFlux = slaveSubjectOutlineOfferedRepository
                        .indexSubjectOutlineOfferedWithStatusAndOBEFalse(Boolean.valueOf(obe), Boolean.valueOf(status), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

                return slaveSubjectOutlineOfferedFlux
                        .collectList()
                        .flatMap(outlineOfferedEntity -> slaveSubjectOutlineOfferedRepository
                                .countSubjectOutlineOfferedWithOBEFalseAndStatus(Boolean.valueOf(status), Boolean.valueOf(obe), searchKeyWord)
                                .flatMap(count -> {
                                    if (outlineOfferedEntity.isEmpty()) {
                                        return responseIndexInfoMsg("Record does not exist", count);
                                    } else {
                                        return responseIndexSuccessMsg("All Records Fetched Successfully", outlineOfferedEntity, count);
                                    }
                                })
                        ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                        .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
            }

        } else if (!obe.isEmpty()) {
            if (Boolean.parseBoolean(obe)) {
                Flux<SlaveSubjectOutlineOfferedDto> slaveSubjectOutlineOfferedFlux = slaveSubjectOutlineOfferedRepository
                        .indexSubjectOutlineOfferedAgainstOBETrue(Boolean.valueOf(obe), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

                return slaveSubjectOutlineOfferedFlux
                        .collectList()
                        .flatMap(outlineOfferedEntity -> slaveSubjectOutlineOfferedRepository
                                .countSubjectOutlineOfferedWithOBETrue(Boolean.valueOf(obe), searchKeyWord)
                                .flatMap(count -> {
                                    if (outlineOfferedEntity.isEmpty()) {
                                        return responseIndexInfoMsg("Record does not exist", count);
                                    } else {
                                        return responseIndexSuccessMsg("All Records Fetched Successfully", outlineOfferedEntity, count);
                                    }
                                })
                        ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                        .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
            } else {
                Flux<SlaveSubjectOutlineOfferedDto> slaveSubjectOutlineOfferedFlux = slaveSubjectOutlineOfferedRepository
                        .indexSubjectOutlineOfferedAgainstOBEFalse(Boolean.valueOf(obe), searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

                return slaveSubjectOutlineOfferedFlux
                        .collectList()
                        .flatMap(outlineOfferedEntity -> slaveSubjectOutlineOfferedRepository
                                .countSubjectOutlineOfferedWithOBEFalse(Boolean.valueOf(obe), searchKeyWord)
                                .flatMap(count -> {
                                    if (outlineOfferedEntity.isEmpty()) {
                                        return responseIndexInfoMsg("Record does not exist", count);
                                    } else {
                                        return responseIndexSuccessMsg("All Records Fetched Successfully", outlineOfferedEntity, count);
                                    }
                                })
                        ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                        .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
            }
        } else if (!status.isEmpty()) {
            Flux<SlaveSubjectOutlineOfferedDto> slaveSubjectOutlineOfferedFlux = slaveSubjectOutlineOfferedRepository
                    .indexSubjectOutlineOfferedWithStatusCheck(searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectOutlineOfferedFlux
                    .collectList()
                    .flatMap(outlineOfferedEntity -> slaveSubjectOutlineOfferedRepository
                            .countSubjectOutlineOfferedWithStatus(searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (outlineOfferedEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", outlineOfferedEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveSubjectOutlineOfferedDto> slaveSubjectOutlineOfferedFlux = slaveSubjectOutlineOfferedRepository
                    .indexSubjectOutlineOffered(searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveSubjectOutlineOfferedFlux
                    .collectList()
                    .flatMap(outlineOfferedEntity -> slaveSubjectOutlineOfferedRepository.countSubjectOutlineOffered(searchKeyWord)
                            .flatMap(count -> {
                                if (outlineOfferedEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", outlineOfferedEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-offered_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID subjectOutlineOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return subjectOutlineOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedUUID)
                .flatMap(subjectOutlineOffered -> {

                    // if subject offered is obe
                    if (subjectOutlineOffered.getObe()) {
                        return slaveSubjectOutlineOfferedRepository.showSubjectOutlineOfferedAgainstOBE(subjectOutlineOfferedUUID, subjectOutlineOffered.getObe())
                                .flatMap(subjectOutlineOfferedEntity -> responseSuccessMsg("Record Fetched Successfully", subjectOutlineOfferedEntity))
                                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
                    }

                    // id subject offered is non-obe
                    else {
                        return slaveSubjectOutlineOfferedRepository.showSubjectOutlineOffered(subjectOutlineOfferedUUID)
                                .flatMap(subjectOutlineOfferedEntity -> responseSuccessMsg("Record Fetched Successfully", subjectOutlineOfferedEntity))
                                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
                    }
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-offered_store")
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

                    UUID subjectOutlineUUID = null;
                    if (value.getFirst("subjectOutlineUUID") != null && value.getFirst("subjectOutlineUUID") != "") {
                        subjectOutlineUUID = UUID.fromString(value.getFirst("subjectOutlineUUID").trim());
                    }

                    UUID subjectObeUUID = null;
                    if (value.getFirst("subjectObeUUID") != null && value.getFirst("subjectObeUUID") != "") {
                        subjectObeUUID = UUID.fromString(value.getFirst("subjectObeUUID").trim());
                    }

                    SubjectOutlineOfferedEntity subjectOutlineOfferedEntity = SubjectOutlineOfferedEntity.builder()
                            .uuid(UUID.randomUUID())
                            .subjectOfferedUUID(UUID.fromString(value.getFirst("subjectOfferedUUID").trim()))
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .subjectOutlineUUID(subjectOutlineUUID)
                            .subjectObeUUID(subjectObeUUID)
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


                    if (subjectOutlineUUID != null && subjectObeUUID != null) {
                        return responseInfoMsg("Subject Outline OBE and Subject Outline Both cannot be entered at the same time.");
                    }

                    UUID finalSubjectObeUUID = subjectObeUUID;
                    //check academic Session exist

                    // check if subject offered uuid exist
                    return subjectOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedEntity.getSubjectOfferedUUID())
                            .flatMap(subjectOfferedEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedEntity.getTeacherUUID())
                                    // get course subject record from subject offered
                                    .flatMap(teacherEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
                                            .flatMap(courseSubject -> {
                                                // If Subject OBE is given
                                                if (finalSubjectObeUUID != null) {
                                                    //check given course subject is obe or not
                                                    if (!courseSubject.getObe()) {
                                                        return responseInfoMsg("Given Course Subject is Non-OBE.");
                                                    } else {

                                                        return subjectObeRepository.findByUuidAndDeletedAtIsNull(finalSubjectObeUUID)
                                                                .flatMap(subjectOutlineObe -> subjectOutlineOfferedRepository.findFirstBySubjectObeUUIDAndSubjectOfferedUUIDAndDeletedAtIsNull(finalSubjectObeUUID, subjectOutlineOfferedEntity.getSubjectOfferedUUID())
                                                                        .flatMap(checkMsg -> responseInfoMsg("The Entered Subject Outline OBE Already Exist against this Subject Offered"))
                                                                        .switchIfEmpty(Mono.defer(() -> {

                                                                            // if course subject is different from outline
                                                                            if (!subjectOfferedEntity.getCourseSubjectUUID().equals(subjectOutlineObe.getCourseSubjectUUID())) {
                                                                                return responseInfoMsg("This Subject Offered can't be Mapped against this Subject OBE Outline");
                                                                            }

                                                                            // else store the record
                                                                            else {
                                                                                subjectOutlineOfferedEntity.setObe(true);
                                                                                return subjectOutlineOfferedRepository.save(subjectOutlineOfferedEntity)
                                                                                        .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                                                                        .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                            }
                                                                        }))
                                                                ).switchIfEmpty(responseInfoMsg("Subject OBE  record does not exist."))
                                                                .onErrorResume(ex -> responseErrorMsg("Subject OBE  record does not exist. Please Contact Developer."));
                                                    }
                                                }
                                                // If Subject Outline is given
                                                else {
                                                    //check given course subject is obe or not
                                                    if (courseSubject.getObe()) {
                                                        return responseInfoMsg("Given Course Subject is OBE.");
                                                    } else {
                                                        return subjectOutlineRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedEntity.getSubjectOutlineUUID())
                                                                .flatMap(subjectOutline -> subjectOutlineOfferedRepository.findFirstBySubjectOutlineUUIDAndSubjectOfferedUUIDAndDeletedAtIsNull(subjectOutlineOfferedEntity.getSubjectOutlineUUID(), subjectOutlineOfferedEntity.getSubjectOfferedUUID())
                                                                        .flatMap(checkMsg -> responseInfoMsg("The Entered Subject Outline Already Exist against this Subject Offered"))
                                                                        .switchIfEmpty(Mono.defer(() -> {

                                                                            // if course subject is different from outline
                                                                            if (!subjectOfferedEntity.getCourseSubjectUUID().equals(subjectOutline.getCourseSubjectUUID())) {
                                                                                return responseInfoMsg("This Subject Offered can't be Mapped against this Subject Outline");
                                                                            }

                                                                            // else store the record
                                                                            else {
                                                                                subjectOutlineOfferedEntity.setObe(false);
                                                                                return subjectOutlineOfferedRepository.save(subjectOutlineOfferedEntity)
                                                                                        .flatMap(saveEntity -> responseSuccessMsg("Record Stored Successfully", saveEntity))
                                                                                        .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                            }
                                                                        }))
                                                                ).switchIfEmpty(responseInfoMsg("Subject Outline Does not exist."))
                                                                .onErrorResume(ex -> responseErrorMsg("Subject Outline Does not exist. Please Contact Developer."));
                                                    }
                                                }
                                            })
                                    ).switchIfEmpty(responseInfoMsg("Teacher Record Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Teacher Record Does not exist.Please Contact Developer."))
                            ).switchIfEmpty(responseInfoMsg("Subject Offered Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Subject Offered Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-offered_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID subjectOutlineOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> subjectOutlineOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedUUID)
                        .flatMap(previousEntity -> {

                            UUID subjectOutlineUUID = null;
                            if (value.getFirst("subjectOutlineUUID") != null && value.getFirst("subjectOutlineUUID") != "") {
                                subjectOutlineUUID = UUID.fromString(value.getFirst("subjectOutlineUUID").trim());
                            }

                            UUID subjectObeUUID = null;
                            if (value.getFirst("subjectObeUUID") != null && value.getFirst("subjectObeUUID") != "") {
                                subjectObeUUID = UUID.fromString(value.getFirst("subjectObeUUID").trim());
                            }


                            SubjectOutlineOfferedEntity updatedEntity = SubjectOutlineOfferedEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .subjectOfferedUUID(UUID.fromString(value.getFirst("subjectOfferedUUID").trim()))
                                    .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                                    .subjectOutlineUUID(subjectOutlineUUID)
                                    .subjectObeUUID(subjectObeUUID)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
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

                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            if (subjectOutlineUUID != null && subjectObeUUID != null) {
                                return responseInfoMsg("Subject Outline OBE and Subject Outline Both cannot be entered at the same time.");
                            }

                            UUID finalSubjectOutlineUUID = subjectOutlineUUID;
                            UUID finalSubjectObeUUID = subjectObeUUID;

                            // check if subject offered uuid exist
                            return subjectOfferedRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSubjectOfferedUUID())
                                    .flatMap(subjectOfferedEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherUUID())
                                            // get course subject record from subject offered
                                            .flatMap(teacherEntity -> courseSubjectRepository.findByUuidAndDeletedAtIsNull(subjectOfferedEntity.getCourseSubjectUUID())
                                                    .flatMap(courseSubject -> {
                                                        // If Subject OBE is given
                                                        if (finalSubjectObeUUID != null) {
                                                            //check given course subject is obe or not
                                                            if (!courseSubject.getObe()) {
                                                                return responseInfoMsg("Given Course Subject is Non-OBE.");
                                                            } else {
                                                                return subjectObeRepository.findByUuidAndDeletedAtIsNull(finalSubjectObeUUID)
                                                                        .flatMap(subjectOutlineObe -> subjectOutlineOfferedRepository.findFirstBySubjectObeUUIDAndSubjectOfferedUUIDAndDeletedAtIsNullAndUuidIsNot(finalSubjectObeUUID, updatedEntity.getSubjectOfferedUUID(), subjectOutlineOfferedUUID)
                                                                                .flatMap(checkMsg -> responseInfoMsg("The Entered Subject Outline OBE Already Exist against this Subject Offered"))
                                                                                .switchIfEmpty(Mono.defer(() -> {

                                                                                    // if course subject is different from outline
                                                                                    if (!subjectOfferedEntity.getCourseSubjectUUID().equals(subjectOutlineObe.getCourseSubjectUUID())) {
                                                                                        return responseInfoMsg("This Subject Offered can't be Mapped against this Subject OBE Outline");
                                                                                    }

                                                                                    // else update the record
                                                                                    else {

                                                                                        updatedEntity.setSubjectObeUUID(finalSubjectObeUUID);
                                                                                        updatedEntity.setObe(true);
                                                                                        return subjectOutlineOfferedRepository.save(previousEntity)
                                                                                                .then(subjectOutlineOfferedRepository.save(updatedEntity))
                                                                                                .flatMap(saveEntity -> responseSuccessMsg("Record Updated Successfully", saveEntity))
                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                                                    }
                                                                                }))
                                                                        ).switchIfEmpty(responseInfoMsg("Subject Outline OBE Does not exist."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Subject Outline OBE Does not exist.Please Contact Developer."));
                                                            }
                                                        }
                                                        // If Subject Outline is given
                                                        else {
                                                            //check given course subject is obe or not
                                                            if (courseSubject.getObe()) {
                                                                return responseInfoMsg("Given Course Subject is OBE.");
                                                            } else {
                                                                return subjectOutlineRepository.findByUuidAndDeletedAtIsNull(finalSubjectOutlineUUID)
                                                                        .flatMap(subjectOutlineObe -> subjectOutlineOfferedRepository.findFirstBySubjectOutlineUUIDAndSubjectOfferedUUIDAndDeletedAtIsNullAndUuidIsNot(finalSubjectOutlineUUID, updatedEntity.getSubjectOfferedUUID(), subjectOutlineOfferedUUID)
                                                                                .flatMap(checkMsg -> responseInfoMsg("The Entered Subject Outline Already Exist against this Subject Offered"))
                                                                                .switchIfEmpty(Mono.defer(() -> {

                                                                                    // if course subject is different from outline
                                                                                    if (!subjectOfferedEntity.getCourseSubjectUUID().equals(subjectOutlineObe.getCourseSubjectUUID())) {
                                                                                        return responseInfoMsg("This Subject Offered can't be Mapped against this Subject Outline");
                                                                                    }

                                                                                    // else update the record
                                                                                    else {
                                                                                        updatedEntity.setSubjectOutlineUUID(finalSubjectOutlineUUID);
                                                                                        updatedEntity.setObe(false);

                                                                                        return subjectOutlineOfferedRepository.save(previousEntity)
                                                                                                .then(subjectOutlineOfferedRepository.save(updatedEntity))
                                                                                                .flatMap(saveEntity -> responseSuccessMsg("Record Updated Successfully", saveEntity))
                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Record.Please Contact Developer."));
                                                                                    }
                                                                                }))
                                                                        ).switchIfEmpty(responseInfoMsg("Subject Outline Does not exist."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Subject Outline Does not exist.Please Contact Developer."));
                                                            }
                                                        }
                                                    }).switchIfEmpty(responseInfoMsg("Course Subject record does not exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Course Subject record does not exist.Please Contact Developer."))
                                            ).switchIfEmpty(responseInfoMsg("Teacher Does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Teacher Does not exist.Please Contact Developer."))
                                    ).switchIfEmpty(responseInfoMsg("Subject Offered Does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Subject Offered Does not exist.Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-offered_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID subjectOutlineOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return subjectOutlineOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                SubjectOutlineOfferedEntity entity = SubjectOutlineOfferedEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .obe(previousEntity.getObe())
                                        .subjectOfferedUUID(previousEntity.getSubjectOfferedUUID())
                                        .teacherUUID(previousEntity.getTeacherUUID())
                                        .subjectOutlineUUID(previousEntity.getSubjectOutlineUUID())
                                        .subjectObeUUID(previousEntity.getSubjectObeUUID())
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

                                return subjectOutlineOfferedRepository.save(previousEntity)
                                        .then(subjectOutlineOfferedRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_subject-outline-offered_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID subjectOutlineOfferedUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return subjectOutlineOfferedRepository.findByUuidAndDeletedAtIsNull(subjectOutlineOfferedUUID)
                .flatMap(subjectEntity -> {

                    subjectEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    subjectEntity.setDeletedBy(UUID.fromString(userId));
                    subjectEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    subjectEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    subjectEntity.setReqDeletedIP(reqIp);
                    subjectEntity.setReqDeletedPort(reqPort);
                    subjectEntity.setReqDeletedBrowser(reqBrowser);
                    subjectEntity.setReqDeletedOS(reqOs);
                    subjectEntity.setReqDeletedDevice(reqDevice);
                    subjectEntity.setReqDeletedReferer(reqReferer);

                    return subjectOutlineOfferedRepository.save(subjectEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record.There is something wrong please try again!"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to Delete Record.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
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

    public Mono<ServerResponse> responseInfoMsg(String msg, Object entity) {
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
                entity

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
