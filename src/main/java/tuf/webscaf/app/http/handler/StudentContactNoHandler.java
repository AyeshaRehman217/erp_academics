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
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Tag(name = "studentContactNoHandler")
@Component
public class StudentContactNoHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    SlaveStudentContactNoRepository slaveStudentContactNoRepository;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    StudentChildRepository studentChildRepository;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;


    @AuthHasPermission(value = "academic_api_v1_student-contact-nos_index")
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

        // Optional Query Parameter of Student Meta UUID
        String studentMetaUUID = serverRequest.queryParam("studentMetaUUID").map(String::toString).orElse("").trim();

        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty() && !studentMetaUUID.isEmpty()) {

            Flux<SlaveStudentContactNoDto> slaveContactNoFlux = slaveStudentContactNoRepository
                    .indexWithStatus(UUID.fromString(studentMetaUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveStudentContactNoRepository
                            .countStudentContactNoRecordWithStatus(UUID.fromString(studentMetaUUID), Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (contactNoEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", contactNoEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));

        } else if (!studentMetaUUID.isEmpty()) {
            Flux<SlaveStudentContactNoDto> slaveContactNoFlux = slaveStudentContactNoRepository
                    .indexWithoutStatus(UUID.fromString(studentMetaUUID), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveStudentContactNoRepository
                            .countStudentContactNoRecordWithoutStatus(UUID.fromString(studentMetaUUID), searchKeyWord, searchKeyWord)
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
            Flux<SlaveStudentContactNoDto> slaveContactNoFlux = slaveStudentContactNoRepository
                    .fetchAllRecordsWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveStudentContactNoRepository
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
            Flux<SlaveStudentContactNoDto> slaveContactNoFlux = slaveStudentContactNoRepository
                    .fetchAllRecordsWithoutStatusFilter(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveContactNoFlux
                    .collectList()
                    .flatMap(contactNoEntity -> slaveStudentContactNoRepository
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

    @AuthHasPermission(value = "academic_api_v1_student-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID contactNoUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentContactNoRepository.showAllStudentContactNo(contactNoUUID)
                .flatMap(contactNoEntity -> responseSuccessMsg("Record Fetched Successfully", contactNoEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-contact-nos_store")
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

                    StudentContactNoEntity entity = StudentContactNoEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .contactNo(value.getFirst("contactNo").trim())
                            .contactTypeUUID(UUID.fromString(value.getFirst("contactTypeUUID").trim()))
                            .contactCategoryUUID(UUID.fromString(value.getFirst("contactCategoryUUID").trim()))
                            .studentMetaUUID(UUID.fromString(value.getFirst("studentMetaUUID").trim()))
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
                                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findFirstByContactNoAndStudentMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNull(entity.getContactNo(), entity.getStudentMetaUUID(), entity.getContactTypeUUID(), entity.getContactCategoryUUID())
                                            .flatMap(contactNoEntity -> responseInfoMsg("Contact No Already Exist Against this Category and Type"))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                // if contact category is students
                                                switch (contactCategoryEntity.getSlug()) {
                                                    case "student":
                                                        return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Record does not exist. Please contact developer."));


                                                    // if contact category is student mothers
                                                    case "mother":
                                                        return studentMotherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Mother Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Mother Record does not exist. Please contact developer."));


                                                    // if contact category is student fathers
                                                    case "father":
                                                        return studentFatherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Father Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Father Record does not exist. Please contact developer."));


                                                    // if contact category is student siblings
                                                    case "sibling":
                                                        return studentSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Sibling Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Sibling Record does not exist. Please contact developer."));


                                                    // if contact category is student children
                                                    case "child":
                                                        return studentChildRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Child Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Child Record does not exist. Please contact developer."));


                                                    // if contact category is student spouses
                                                    case "spouse":
                                                        return studentSpouseRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Spouse Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Spouse Record does not exist. Please contact developer."));


                                                    // if contact category is student guardians
                                                    case "guardian":
                                                        return studentGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMetaUUID())
                                                                .flatMap(studentEntity -> studentContactNoRepository.save(entity)
                                                                        .flatMap(studentContactNoEntity -> responseSuccessMsg("Record Stored Successfully", studentContactNoEntity))
                                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Student Guardian Record does not exist"))
                                                                .onErrorResume(err -> responseErrorMsg("Student Guardian Record does not exist. Please contact developer."));


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

    @AuthHasPermission(value = "academic_api_v1_student-contact-nos_update")
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
                .flatMap(value -> studentContactNoRepository.findByUuidAndDeletedAtIsNull(contactNoUUID)
                        .flatMap(previousEntity -> {

                            StudentContactNoEntity updatedEntity = StudentContactNoEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .contactNo(value.getFirst("contactNo").trim())
                                    .contactTypeUUID(UUID.fromString(value.getFirst("contactTypeUUID").trim()))
                                    .contactCategoryUUID(UUID.fromString(value.getFirst("contactCategoryUUID").trim()))
                                    .studentMetaUUID(UUID.fromString(value.getFirst("studentMetaUUID").trim()))
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
                                            .flatMap(contactCategoryEntity -> studentContactNoRepository.findFirstByContactNoAndStudentMetaUUIDAndContactTypeUUIDAndContactCategoryUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getContactNo(), updatedEntity.getStudentMetaUUID(), updatedEntity.getContactTypeUUID(), updatedEntity.getContactCategoryUUID(), updatedEntity.getUuid())
                                                    .flatMap(contactNoAlreadyExists -> responseInfoMsg("Contact No Already Exist Against this Category and Type"))
                                                    .switchIfEmpty(Mono.defer(() -> {

                                                        // if contact category is students
                                                        switch (contactCategoryEntity.getSlug()) {
                                                            case "student":
                                                                return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Record does not exist. Please contact developer."));


                                                            // if contact category is student mothers
                                                            case "mother":
                                                                return studentMotherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Mother Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Mother Record does not exist. Please contact developer."));


                                                            // if contact category is student fathers
                                                            case "father":
                                                                return studentFatherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Father Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Father Record does not exist. Please contact developer."));


                                                            // if contact category is student siblings
                                                            case "sibling":
                                                                return studentSiblingRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Sibling Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Sibling Record does not exist. Please contact developer."));


                                                            // if contact category is student children
                                                            case "child":
                                                                return studentChildRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Child Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Child Record does not exist. Please contact developer."));


                                                            // if contact category is student spouses
                                                            case "spouse":
                                                                return studentSpouseRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Spouse Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Spouse Record does not exist. Please contact developer."));


                                                            // if contact category is student guardians
                                                            case "guardian":
                                                                return studentGuardianRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMetaUUID())
                                                                        .flatMap(studentEntity -> studentContactNoRepository.save(previousEntity)
                                                                                .then(studentContactNoRepository.save(updatedEntity))
                                                                                .flatMap(courseTypeEntity -> responseSuccessMsg("Record Updated Successfully", courseTypeEntity))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Student Guardian Record does not exist"))
                                                                        .onErrorResume(err -> responseErrorMsg("Student Guardian Record does not exist. Please contact developer."));


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

    @AuthHasPermission(value = "academic_api_v1_student-contact-nos_status_update")
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
                    return studentContactNoRepository.findByUuidAndDeletedAtIsNull(contactNoUUID)
                            .flatMap(val -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((val.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentContactNoEntity entity = StudentContactNoEntity.builder()
                                        .uuid(val.getUuid())
                                        .status(status == true ? true : false)
                                        .contactNo(val.getContactNo())
                                        .contactTypeUUID(val.getContactTypeUUID())
                                        .contactCategoryUUID(val.getContactCategoryUUID())
                                        .studentMetaUUID(val.getStudentMetaUUID())
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

                                return studentContactNoRepository.save(val)
                                        .then(studentContactNoRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-contact-nos_delete")
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

        return studentContactNoRepository.findByUuidAndDeletedAtIsNull(contactNoUUID)
                .flatMap(studentContactNoEntityDB -> {

                    studentContactNoEntityDB.setDeletedBy(UUID.fromString(userId));
                    studentContactNoEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentContactNoEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentContactNoEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentContactNoEntityDB.setReqDeletedIP(reqIp);
                    studentContactNoEntityDB.setReqDeletedPort(reqPort);
                    studentContactNoEntityDB.setReqDeletedBrowser(reqBrowser);
                    studentContactNoEntityDB.setReqDeletedOS(reqOs);
                    studentContactNoEntityDB.setReqDeletedDevice(reqDevice);
                    studentContactNoEntityDB.setReqDeletedReferer(reqReferer);


                    return studentContactNoRepository.save(studentContactNoEntityDB)
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
