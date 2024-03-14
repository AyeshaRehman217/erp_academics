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
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianProfileRepository;
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

@Tag(name = "studentGuardianProfileHandler")
@Component
public class StudentGuardianProfileHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGuardianProfileRepository studentGuardianProfileRepository;

    @Autowired
    SlaveStudentGuardianProfileRepository slaveStudentGuardianProfileRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    StudentGuardianDocumentRepository studentGuardianDocumentRepository;

    @Autowired
    StudentGuardianFinancialHistoryRepository studentGuardianFinancialHistoryRepository;

    @Autowired
    StudentGuardianAcademicHistoryRepository studentGuardianAcademicHistoryRepository;

    @Autowired
    StudentGuardianJobHistoryRepository studentGuardianJobHistoryRepository;

    @Autowired
    StudentGuardianFamilyDoctorRepository studentGuardianFamilyDoctorRepository;

    @Autowired
    StudentGuardianAddressRepository studentGuardianAddressRepository;

    @Autowired
    StudentGuardianAilmentPvtRepository studentGuardianAilmentPvtRepository;

    @Autowired
    StudentGuardianHobbyPvtRepository studentGuardianHobbyPvtRepository;

    @Autowired
    StudentGuardianNationalityPvtRepository studentGuardianNationalityPvtRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_index")
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

            Flux<SlaveStudentGuardianProfileEntity> slaveStudentGuardianProfileFlux = slaveStudentGuardianProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveStudentGuardianProfileFlux
                    .collectList()
                    .flatMap(studentGuardianProfileEntity -> slaveStudentGuardianProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentGuardianProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianProfileEntity, count);
                                }
                            })).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));

        } else {

            Flux<SlaveStudentGuardianProfileEntity> slaveStudentGuardianProfileFlux = slaveStudentGuardianProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveStudentGuardianProfileFlux
                    .collectList()
                    .flatMap(studentGuardianProfileEntity -> slaveStudentGuardianProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentGuardianProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianProfileEntity, count);
                                }
                            })).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentGuardianProfileRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                .flatMap(studentGuardianProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_student-guardian_show")
    public Mono<ServerResponse> showByStudentGuardianUUID(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("studentGuardianUUID"));

        return slaveStudentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianUUID)
                .flatMap(studentProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    // Show Student Profiles against Student , Student Guardian, and Student Guardian Profile
    @AuthHasPermission(value = "academic_api_v1_student_student-guardian_student-guardian-profiles_show")
    public Mono<ServerResponse> showStudentGuardianProfile(ServerRequest serverRequest) {
        UUID studentGuardianProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentGuardianUUID = UUID.fromString(serverRequest.queryParam("studentGuardianUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentGuardianProfileRepository.showStudentGuardianProfileAgainstStudentAndStudentGuardian(studentUUID, studentGuardianUUID, studentGuardianProfileUUID)
                .flatMap(studentGuardianProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentGuardianProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists in Student Guardian Profile
    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists in Student Guardian Profile
    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists in Student Guardian Profile
    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentGuardianProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_store")
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

                    StudentGuardianProfileEntity entity = StudentGuardianProfileEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .studentGuardianUUID(UUID.fromString(value.getFirst("studentGuardianUUID")))
                            .name(value.getFirst("name").trim())
                            .relation(value.getFirst("relation"))
                            .age(Integer.valueOf(value.getFirst("age")))
                            .description(value.getFirst("description"))
                            .nic(value.getFirst("nic"))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                            .officialTel(value.getFirst("officialTel"))
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
                            .email(value.getFirst("email").trim())
                            .image(UUID.fromString(value.getFirst("image")))
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


                    return studentGuardianRepository.findByUuidAndDeletedAtIsNull(entity.getStudentGuardianUUID())
                            .flatMap(studentGuardian -> {
                                if (studentGuardian.getGuardianUUID() != null) {
                                    return responseInfoMsg("Unable to Create Guardian Profile. Guardian Records Already Exists");
                                } else {
                                    return studentGuardianProfileRepository.findFirstByNicAndStudentGuardianUUIDAndDeletedAtIsNull(entity.getNic(), entity.getStudentGuardianUUID())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            // student's Guardian should have one profile
                                            .switchIfEmpty(Mono.defer(() -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(entity.getStudentGuardianUUID())
                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Guardian Profile already exist"))))
                                            //checks if contact no uuid exists
                                            .switchIfEmpty(Mono.defer(() -> genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                                    //checks if document uuid exists
                                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                                    //checks city uuid exists
                                                                    .flatMap(documentJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                    //checks state uuid exists
                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                                    //checks state uuid exists
                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                    .flatMap(countryJsonNode ->
                                                                                                                            studentGuardianProfileRepository.save(entity)
                                                                                                                                    .flatMap(studentGuardianProfileEntity -> responseSuccessMsg("Record Stored Successfully", studentGuardianProfileEntity))
                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                                    )).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                                            )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                                            )).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                                            )).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
                                            ));
                                }
                            }).switchIfEmpty(responseInfoMsg("Student Guardian record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Guardian record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer"));

    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentGuardianProfileRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                        .flatMap(previousEntity -> {

                            StudentGuardianProfileEntity updatedEntity = StudentGuardianProfileEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentGuardianUUID(previousEntity.getStudentGuardianUUID())
                                    .name(value.getFirst("name"))
                                    .relation(value.getFirst("relation"))
                                    .age(Integer.valueOf(value.getFirst("age")))
                                    .description(value.getFirst("description"))
                                    .nic(value.getFirst("nic"))
                                    .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                    .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                    .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                                    .officialTel(value.getFirst("officialTel"))
                                    .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
                                    .email(value.getFirst("email").trim())
                                    .image(UUID.fromString(value.getFirst("image")))
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
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

                            // check nic number is unique
                            return studentGuardianProfileRepository.findFirstByNicAndStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentGuardianUUID(), studentGuardianUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                    // student's Guardian should have one profile
                                    .switchIfEmpty(Mono.defer(() -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentGuardianUUID(), studentGuardianUUID)
                                            .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Guardian Profile already exist"))))
                                    // check contact no uuid exists
                                    .switchIfEmpty(Mono.defer(() -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                            //checks if document uuid exists
                                            .flatMap(genderEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(studentGuardianDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks state uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> studentGuardianProfileRepository.save(previousEntity)
                                                                                                                    .then(studentGuardianProfileRepository.save(updatedEntity))
                                                                                                                    .flatMap(studentGuardianProfileEntity -> responseSuccessMsg("Record Updated Successfully", studentGuardianProfileEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong Please try again."))
                                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer."))
                                                                                                            ).
                                                                                                            switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer.")))
                                                                                            ).
                                                                                            switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer.")))
                                                                            ).
                                                                            switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer.")))
                                                            ).
                                                            switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                                    )).
                                            switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentGuardianProfileRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentGuardianProfileEntity updatedEntity = StudentGuardianProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .studentGuardianUUID(previousEntity.getStudentGuardianUUID())
                                        .relation(previousEntity.getRelation())
                                        .age(previousEntity.getAge())
                                        .description(previousEntity.getDescription())
                                        .nic(previousEntity.getNic())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .noOfDependents(previousEntity.getNoOfDependents())
                                        .officialTel(previousEntity.getOfficialTel())
                                        .email(previousEntity.getEmail())
                                        .genderUUID(previousEntity.getGenderUUID())
                                        .image(previousEntity.getImage())
                                        .status(status == true ? true : false)
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

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return studentGuardianProfileRepository.save(previousEntity)
                                        .then(studentGuardianProfileRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-guardian-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentGuardianProfileRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                .flatMap(studentGuardianProfileEntity -> {

                    studentGuardianProfileEntity.setDeletedBy(UUID.fromString(userId));
                    studentGuardianProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentGuardianProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentGuardianProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentGuardianProfileEntity.setReqDeletedIP(reqIp);
                    studentGuardianProfileEntity.setReqDeletedPort(reqPort);
                    studentGuardianProfileEntity.setReqDeletedBrowser(reqBrowser);
                    studentGuardianProfileEntity.setReqDeletedOS(reqOs);
                    studentGuardianProfileEntity.setReqDeletedDevice(reqDevice);
                    studentGuardianProfileEntity.setReqDeletedReferer(reqReferer);

                    return studentGuardianProfileRepository.save(studentGuardianProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

//    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
//        UUID studentGuardianUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
//        return studentGuardianProfileRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
//                //Checks if Student Guardian Reference exists in Student Guardians
//                .flatMap(studentGuardianProfileEntity -> studentGuardianRepository.findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                        .flatMap(studentGuardianEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists in Student Guardians"))
//                        //Checks if Student Guardian Reference exists in Student Guardian's Financial History
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianFinancialHistoryRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianFinancialHistoryEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists in Student Guardian Financial History"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian's Academic History
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianAcademicHistoryRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianAcademicHistoryEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists in Student Guardian Academic History"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian's Job History
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianJobHistoryRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianJobHistoryEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists in Student Guardian Job History"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian Family Doctors
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianFamilyDoctorRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianFamilyDoctorEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists in Student Guardian Family Doctors"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian Addresses
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianAddressRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianAddressEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists in Student Guardian Addresses"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian Hobby Pvt
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianHobbyPvtRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianProfileHobbyPvtEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists With Hobbies"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian Nationality Pvt
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianNationalityPvtRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianProfileNationalityPvtEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists With Nationalities"))))
//                        //Checks if Student Guardian Reference exists in Student Guardian Ailment Pvt
//                        .switchIfEmpty(Mono.defer(() -> studentGuardianAilmentPvtRepository
//                                .findFirstByStudentGuardianProfileUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getUuid())
//                                .flatMap(studentGuardianProfileAilmentPvtEntity -> responseInfoMsg("Unable to delete Record as the Reference Exists With Ailments"))))
//                        .switchIfEmpty(Mono.defer(() -> {
//                            studentGuardianProfileEntity.setDeletedBy(UUID.fromString(userId));
//                            studentGuardianProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                            return studentGuardianProfileRepository.save(studentGuardianProfileEntity)
//                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
//                                    .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
//                                    .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
//                        }))
//                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
//                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
//    }

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
