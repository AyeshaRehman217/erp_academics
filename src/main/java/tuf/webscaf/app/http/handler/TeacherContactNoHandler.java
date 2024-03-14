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
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "teacherContactNoHandler")
@Component
public class TeacherContactNoHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    SlaveTeacherContactNoRepository slaveteacherContactNoRepository;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @Autowired
    TeacherChildRepository teacherChildRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;


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

        // Optional Query Parameter of Teacher Meta UUID
        String teacherMetaUUID = serverRequest.queryParam("teacherMetaUUID").map(String::toString).orElse("").trim();

        // Optional Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !teacherMetaUUID.isEmpty()) {

            Flux<SlaveTeacherContactNoDto> slaveContactNoFlux = slaveteacherContactNoRepository
                    .indexWithStatus(UUID.fromString(teacherMetaUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveteacherContactNoRepository
                            .countTeacherContactNoRecordWithStatus(UUID.fromString(teacherMetaUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (contactNoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", contactNoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));

        } else if (!teacherMetaUUID.isEmpty()) {
            Flux<SlaveTeacherContactNoDto> slaveContactNoFlux = slaveteacherContactNoRepository
                    .indexWithoutStatus(UUID.fromString(teacherMetaUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveteacherContactNoRepository
                            .countTeacherContactNoRecordWithoutStatus(UUID.fromString(teacherMetaUUID), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (contactNoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", contactNoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));

        } else if (!status.isEmpty()) {
            Flux<SlaveTeacherContactNoDto> slaveContactNoFlux = slaveteacherContactNoRepository
                    .fetchAllRecordsWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveteacherContactNoRepository
                            .countAllRecordWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (contactNoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", contactNoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherContactNoDto> slaveContactNoFlux = slaveteacherContactNoRepository
                    .fetchAllRecordsWithoutStatusFilter(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveteacherContactNoRepository
                            .countAllRecordWithoutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (contactNoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", contactNoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID contactNoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveteacherContactNoRepository.showAllTeacherContactNo(contactNoUUID)
                .flatMap(contactNoEntity -> responseSuccessMsg("Record Fetched Successfully", contactNoEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
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

                    TeacherContactNoEntity entity = TeacherContactNoEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .contactNo(value.getFirst("contactNo").trim())
                            .contactTypeUUID(UUID.fromString(value.getFirst("contactTypeUUID").trim()))
                            .contactCategoryUUID(UUID.fromString(value.getFirst("contactCategoryUUID").trim()))
                            .teacherMetaUUID(UUID.fromString(value.getFirst("teacherMetaUUID").trim()))
//                            .description(value.getFirst("description").trim())
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


                    // check if contact type uuid exists
                    return contactTypeRepository.findByUuidAndDeletedAtIsNull(entity.getContactTypeUUID())
                            // check if contact category uuid exists
                            .flatMap(contactTypeEntity -> contactCategoryRepository.findByUuidAndDeletedAtIsNull(entity.getContactCategoryUUID())
                                    // check contact no already exists
                                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findFirstByContactNoAndTeacherMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNull(entity.getContactNo(), entity.getTeacherMetaUUID(), entity.getContactTypeUUID(), entity.getContactCategoryUUID())
                                            .flatMap(contactNoEntity -> responseInfoMsg("Contact No Already Exist Against this Category and Type"))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                // if contact category is teachers
                                                switch (contactCategoryEntity.getSlug()) {
                                                    case "teacher":
                                                        return teacherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Record does not exist. Please contact developer."));


                                                    // if contact category is teacher mothers
                                                    case "mother":
                                                        return teacherMotherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Mother Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Mother Record does not exist. Please contact developer."));


                                                    // if contact category is teacher fathers
                                                    case "father":
                                                        return teacherFatherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Father Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Father Record does not exist. Please contact developer."));


                                                    // if contact category is teacher siblings
                                                    case "sibling":
                                                        return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Sibling Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Sibling Record does not exist. Please contact developer."));


                                                    // if contact category is teacher children
                                                    case "child":
                                                        return teacherChildRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Child Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Child Record does not exist. Please contact developer."));


                                                    // if contact category is teacher spouses
                                                    case "spouse":
                                                        return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Spouse Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Spouse Record does not exist. Please contact developer."));


                                                    // if contact category is teacher guardians
                                                    case "guardian":
                                                        return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherMetaUUID())
                                                                .flatMap(teacherEntity -> teacherContactNoRepository.save(entity)
                                                                        .flatMap(TeacherContactNoEntity -> responseSuccessMsg("Record Stored Successfully", TeacherContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("teacher Guardian Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("teacher Guardian Record does not exist. Please contact developer."));


                                                    // else can't store the record
                                                    default:
                                                        return responseInfoMsg("Invalid Contact Category");
                                                }

                                            }))
                                    ).switchIfEmpty(responseInfoMsg("Contact Type does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Contact Type does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Contact Category does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Contact Category does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID contactNoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherContactNoRepository.findByUuidAndDeletedAtIsNull(contactNoUUID)
                        .flatMap(previousEntity -> {

                            TeacherContactNoEntity updatedEntity = TeacherContactNoEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .contactNo(value.getFirst("contactNo").trim())
//                                    .description(value.getFirst("description").trim())
                                    .contactTypeUUID(UUID.fromString(value.getFirst("contactTypeUUID").trim()))
                                    .contactCategoryUUID(UUID.fromString(value.getFirst("contactCategoryUUID").trim()))
                                    .teacherMetaUUID(UUID.fromString(value.getFirst("teacherMetaUUID").trim()))
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

                            // check contact type uuid exists
                            return contactTypeRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getContactTypeUUID())
                                    // check if contact no already exists
                                    .flatMap(contactTypeEntity -> contactCategoryRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getContactCategoryUUID())
                                            .flatMap(contactCategoryEntity -> teacherContactNoRepository.findFirstByContactNoAndTeacherMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getContactNo(), updatedEntity.getTeacherMetaUUID(), updatedEntity.getContactTypeUUID(), updatedEntity.getContactCategoryUUID(), updatedEntity.getUuid())
                                                    .flatMap(contactNoAlreadyExists -> responseInfoMsg("Contact No Already Exist Against this Category and Type"))
                                                    .switchIfEmpty(Mono.defer(() -> {

                                                        // if contact category is teachers
                                                        switch (contactCategoryEntity.getSlug()) {
                                                            case "teacher":
                                                                return teacherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Record does not exist. Please contact developer."));


                                                            // if contact category is teacher mothers
                                                            case "mother":
                                                                return teacherMotherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Mother Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Mother Record does not exist. Please contact developer."));


                                                            // if contact category is teacher fathers
                                                            case "father":
                                                                return teacherFatherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Father Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Father Record does not exist. Please contact developer."));


                                                            // if contact category is teacher siblings
                                                            case "sibling":
                                                                return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Sibling Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Sibling Record does not exist. Please contact developer."));


                                                            // if contact category is teacher children
                                                            case "child":
                                                                return teacherChildRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Child Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Child Record does not exist. Please contact developer."));


                                                            // if contact category is teacher spouses
                                                            case "spouse":
                                                                return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Spouse Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Spouse Record does not exist. Please contact developer."));


                                                            // if contact category is teacher guardians
                                                            case "guardian":
                                                                return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getTeacherMetaUUID())
                                                                        .flatMap(teacherEntity -> teacherContactNoRepository.save(previousEntity)
                                                                                .then(teacherContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("teacher Guardian Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("teacher Guardian Record does not exist. Please contact developer."));


                                                            // else can't update the record
                                                            default:
                                                                return responseInfoMsg("Invalid Contact Category");
                                                        }

                                                    }))
                                            ).switchIfEmpty(responseInfoMsg("Contact Category does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Contact Category does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Contact Type does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Contact Type does not exist. Please contact developer."));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist")))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID contactNoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
        String userId = serverRequest.headers().firstHeader("auid");

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        return serverRequest.formData()
                .flatMap(value -> {
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return teacherContactNoRepository.findByUuidAndDeletedAtIsNull(contactNoUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherContactNoEntity entity = TeacherContactNoEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .contactNo(previousEntity.getContactNo())
//                                        .description(previousEntity.getDescription())
                                        .contactTypeUUID(previousEntity.getContactTypeUUID())
                                        .contactCategoryUUID(previousEntity.getContactCategoryUUID())
                                        .teacherMetaUUID(previousEntity.getTeacherMetaUUID())
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

                                return teacherContactNoRepository.save(previousEntity)
                                        .then(teacherContactNoRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID contactNoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherContactNoRepository.findByUuidAndDeletedAtIsNull(contactNoUUID)
                .flatMap(TeacherContactNoEntityDB -> {
                    TeacherContactNoEntityDB.setDeletedBy(UUID.fromString(userId));
                    TeacherContactNoEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    TeacherContactNoEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    TeacherContactNoEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    TeacherContactNoEntityDB.setReqDeletedIP(reqIp);
                    TeacherContactNoEntityDB.setReqDeletedPort(reqPort);
                    TeacherContactNoEntityDB.setReqDeletedBrowser(reqBrowser);
                    TeacherContactNoEntityDB.setReqDeletedOS(reqOs);
                    TeacherContactNoEntityDB.setReqDeletedDevice(reqDevice);
                    TeacherContactNoEntityDB.setReqDeletedReferer(reqReferer);

                    return teacherContactNoRepository.save(TeacherContactNoEntityDB)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
