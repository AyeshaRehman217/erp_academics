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
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingProfileRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentSiblingProfileHandler")
@Component
public class StudentSiblingProfileHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingProfileRepository studentSiblingProfileRepository;

    @Autowired
    SlaveStudentSiblingProfileRepository slaveStudentSiblingProfileRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    StudentSiblingDocumentRepository studentSiblingDocumentRepository;

    @Autowired
    StudentSiblingAcademicHistoryRepository studentSiblingAcademicHistoryRepository;

    @Autowired
    StudentSiblingFinancialHistoryRepository studentSiblingFinancialHistoryRepository;

    @Autowired
    StudentSiblingJobHistoryRepository studentSiblingJobHistoryRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    StudentSiblingAilmentPvtRepository studentSiblingAilmentPvtRepository;

    @Autowired
    StudentSiblingAddressRepository studentSiblingAddressRepository;

    @Autowired
    StudentSiblingNationalityPvtRepository studentSiblingNationalityPvtRepository;

    @Autowired
    StudentSiblingHobbyPvtRepository studentSiblingHobbyPvtRepository;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //fetching records Based on status filter
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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

        if (!status.isEmpty()) {

            Flux<SlaveStudentSiblingProfileEntity> slaveStudentSiblingProfileFlux = slaveStudentSiblingProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentSiblingProfileFlux
                    .collectList()
                    .flatMap(studentSiblingProfileEntity -> slaveStudentSiblingProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingProfileEntity, count);
                                }
                            })).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));

        } else {

            Flux<SlaveStudentSiblingProfileEntity> slaveStudentSiblingProfileFlux = slaveStudentSiblingProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveStudentSiblingProfileFlux
                    .collectList()
                    .flatMap(studentSiblingProfileEntity -> slaveStudentSiblingProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingProfileEntity, count);
                                }
                            })).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                .flatMap(studentSiblingProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_student-sibling_show")
    public Mono<ServerResponse> showByStudentSiblingUUID(ServerRequest serverRequest) {
        final UUID studentSiblingUUID = UUID.fromString(serverRequest.pathVariable("studentSiblingUUID"));

        return slaveStudentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingUUID)
                .flatMap(studentProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist Please contact developer."));
    }

    // Show Student Profiles against Student , Student Sibling, and Student Sibling Profile
    @AuthHasPermission(value = "academic_api_v1_student_student-sibling_student-sibling-profiles_show")
    public Mono<ServerResponse> showStudentSiblingProfile(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentSiblingUUID = UUID.fromString(serverRequest.queryParam("studentSiblingUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentSiblingProfileRepository.showStudentSiblingProfileAgainstStudentAndStudentSibling(studentUUID, studentSiblingUUID, studentSiblingProfileUUID)
                .flatMap(studentSiblingProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists
    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSiblingProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists
    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSiblingProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists
    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSiblingProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_store")
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


                    StudentSiblingProfileEntity entity = StudentSiblingProfileEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .email(value.getFirst("email").trim())
                            .name(value.getFirst("name").trim())
                            .description(value.getFirst("description").trim())
                            .studentSiblingUUID(UUID.fromString(value.getFirst("studentSiblingUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .age(Integer.valueOf(value.getFirst("age").trim()))
                            .nic(value.getFirst("nic").trim())
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .officialTel(value.getFirst("officialTel").trim())
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
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


                    //  check nic number is unique
                    return studentSiblingProfileRepository.findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNull(entity.getNic(), entity.getStudentSiblingUUID())
                            .flatMap(studentSiblingProfile -> responseInfoMsg("NIC already exist"))
                            // student's Sibling should have one profile
                            .switchIfEmpty(Mono.defer(() -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(entity.getStudentSiblingUUID())
                                    .flatMap(studentSiblingProfileEntity -> responseInfoMsg("Sibling Profile already exist"))))
//                          check student sibling uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSiblingUUID())
                                    //checks if document uuid exists
                                    .flatMap(studentProfileEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                    //checks if contactNo uuid exists
                                                    .flatMap(documentJsonNode -> genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                                            //checks city uuid exists
                                                            .flatMap(studentSiblingDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks state uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> studentSiblingProfileRepository.save(entity)
                                                                                                                    .flatMap(studentSiblingProfileEntity -> responseSuccessMsg("Record Stored Successfully", studentSiblingProfileEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                            )).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                            ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                                    )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                                    )).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Document does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Document does not exist. Please contact developer."))
                                            )).switchIfEmpty(responseInfoMsg("Student Sibling record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Student Sibling record does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                        .flatMap(entity -> {

                            StudentSiblingProfileEntity updatedEntity = StudentSiblingProfileEntity.builder()
                                    .uuid(entity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .description(value.getFirst("description").trim())
                                    .studentSiblingUUID(entity.getStudentSiblingUUID())
                                    .image(UUID.fromString(value.getFirst("image").trim()))
                                    .age(Integer.valueOf(value.getFirst("age")))
                                    .nic(value.getFirst("nic").trim())
                                    .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                    .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                    .officialTel(value.getFirst("officialTel"))
                                    .email(value.getFirst("email").trim())
                                    .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
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

                            // check nic number is unique
                            return studentSiblingProfileRepository.findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentSiblingUUID(), studentSiblingProfileUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                    // student's Sibling should have one profile
                                    .switchIfEmpty(Mono.defer(() -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSiblingUUID(), studentSiblingProfileUUID)
                                            .flatMap(studentSiblingProfileEntity -> responseInfoMsg("Sibling Profile already exist"))))
                                    //checks if studentProfile uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentSiblingRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentSiblingUUID())
                                            //checks if document uuid exists
                                            .flatMap(studentProfileEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks if contactNo uuid exists
                                                            .flatMap(documentJsonNode -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                                                    //checks city uuid exists
                                                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                    //checks state uuid exists
                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                                    //checks state uuid exists
                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                    .flatMap(countryJsonNode -> studentSiblingProfileRepository.save(entity)
                                                                                                                            .then(studentSiblingProfileRepository.save(updatedEntity))
                                                                                                                            .flatMap(studentSiblingProfileEntity -> responseSuccessMsg("Record Updated Successfully", studentSiblingProfileEntity))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong Please try again."))
                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer.")))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer.")))
                                                                                                    ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer.")))
                                                                                    ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer.")))
                                                                    ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Document does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Document does not exist. Please contact developer.")))
                                            ).switchIfEmpty(responseInfoMsg("Student Sibling does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Student Sibling does not exist. Please contact developer."))));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSiblingProfileEntity updatedEntity = StudentSiblingProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .description(previousEntity.getDescription())
                                        .studentSiblingUUID(previousEntity.getStudentSiblingUUID())
                                        .image(previousEntity.getImage())
                                        .age(previousEntity.getAge())
                                        .nic(previousEntity.getNic())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .officialTel(previousEntity.getOfficialTel())
                                        .email(previousEntity.getEmail())
                                        .genderUUID(previousEntity.getGenderUUID())
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

                                return studentSiblingProfileRepository.save(previousEntity)
                                        .then(studentSiblingProfileRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-sibling-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                .flatMap(studentSiblingProfileEntity -> {

                    studentSiblingProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentSiblingProfileEntity.setDeletedBy(UUID.fromString(userId));
                    studentSiblingProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentSiblingProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentSiblingProfileEntity.setReqDeletedIP(reqIp);
                    studentSiblingProfileEntity.setReqDeletedPort(reqPort);
                    studentSiblingProfileEntity.setReqDeletedBrowser(reqBrowser);
                    studentSiblingProfileEntity.setReqDeletedOS(reqOs);
                    studentSiblingProfileEntity.setReqDeletedDevice(reqDevice);
                    studentSiblingProfileEntity.setReqDeletedReferer(reqReferer);

                    return studentSiblingProfileRepository.save(studentSiblingProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to deleted record"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to deleted record. Please contact developer."));
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
