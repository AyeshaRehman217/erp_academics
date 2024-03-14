package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentProfileRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "studentProfileHandler")
@Component
public class StudentProfileHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    SlaveStudentProfileRepository slaveStudentProfileRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    ReligionRepository religionRepository;

    @Autowired
    SectRepository sectRepository;

    @Autowired
    CasteRepository casteRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    MaritalStatusRepository maritalStatusRepository;

    @Autowired
    StudentDocumentRepository studentDocumentRepository;

    @Autowired
    StudentAcademicRecordRepository studentAcademicRecordRepository;

    @Autowired
    StudentSiblingProfileRepository studentSiblingProfileRepository;

    @Autowired
    StudentJobHistoryRepository studentJobHistoryRepository;

    @Autowired
    StudentMotherProfileRepository studentMotherProfileRepository;

    @Autowired
    StudentFatherProfileRepository studentFatherProfileRepository;

    @Autowired
    StudentFinancialHistoryRepository studentFinancialHistoryRepository;

    @Autowired
    StudentHobbyPvtRepository studentHobbyPvtRepository;

    @Autowired
    StudentAddressRepository studentAddressRepository;

    @Autowired
    StudentFamilyDoctorRepository studentFamilyDoctorRepository;

    @Autowired
    StudentNationalityPvtRepository studentNationalityPvtRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    StudentAilmentPvtRepository studentAilmentPvtRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-profiles_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

        //Optional Query Parameter Based of Status
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
            Flux<SlaveStudentProfileEntity> slaveStudentProfileEntityFlux = slaveStudentProfileRepository
                    .findAllByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentProfileEntityFlux
                    .collectList()
                    .flatMap(studentProfileEntity -> slaveStudentProfileRepository
                            .countByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentProfileEntity> slaveStudentProfileEntityFlux = slaveStudentProfileRepository
                    .findAllByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, pageable);
            return slaveStudentProfileEntityFlux
                    .collectList()
                    .flatMap(studentProfileEntity -> slaveStudentProfileRepository
                            .countByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return studentProfileRepository.findByUuidAndDeletedAtIsNull(studentProfileUUID)
                .flatMap(studentProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-profiles_student_show")
    public Mono<ServerResponse> showByStudentUUID(ServerRequest serverRequest) {
        final UUID studentUUID = UUID.fromString(serverRequest.pathVariable("studentUUID"));

        return slaveStudentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                .flatMap(studentProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist Please contact developer."));
    }


    //This function is used by delete function of Country Handler in Config Module to Check If country Exists in Student Profile
    @AuthHasPermission(value = "academic_api_v1_student-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }


    //This function is used by delete function of State Handler in Config Module to Check If state Exists in Student Profile
    @AuthHasPermission(value = "academic_api_v1_student-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists in Student Profile
    @AuthHasPermission(value = "academic_api_v1_student-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }


    // Used to delete Student profile entity if User record is not stored
    public Mono<ServerResponse> deleteStudentProfile(UUID stdProfileUUID) {
        return studentProfileRepository.findByUuidAndDeletedAtIsNull(stdProfileUUID)
                .flatMap(delete -> studentProfileRepository.delete(delete)
                        .flatMap(delMsg -> responseInfoMsg("Student Profile does not exist"))
                        .switchIfEmpty(responseInfoMsg("Unable to store User record.There is something wrong please try again."))
                        .onErrorResume(ex -> responseErrorMsg("Unable to store User record.Please Contact Developer.")));
    }

    @AuthHasPermission(value = "academic_api_v1_student-profiles_store")
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

                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                    StudentProfileEntity entity = StudentProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .firstName(value.getFirst("firstName").trim())
                            .lastName(value.getFirst("lastName").trim())
                            .description(value.getFirst("description").trim())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .email(value.getFirst("email").trim())
                            .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                            .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                            .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                            .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .telephoneNo(value.getFirst("telephoneNo").trim())
                            .nic(value.getFirst("nic").trim())
                            .birthDate(LocalDateTime.parse((value.getFirst("birthDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
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

                    sendFormData.add("docId", String.valueOf(entity.getImage()));

                    // check student uuid exists
                    return studentRepository.findByUuidAndDeletedAtIsNull(entity.getStudentUUID())
                            // check religion uuid exists
                            .flatMap(studentEntity -> religionRepository.findByUuidAndDeletedAtIsNull(entity.getReligionUUID())
                                    // check section uuid exists
                                    .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(entity.getSectUUID())
                                            // check caste uuid exists
                                            .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(entity.getCasteUUID())
                                                    // check gender uuid exists
                                                    .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                                            // check martial status uuid exists
                                                            .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(entity.getMaritalStatusUUID())
                                                                    // check emergency contact no uuid exists
                                                                    .flatMap(maritalStatusEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                                                            .flatMap(imageUUID -> apiCallService.checkDocId(imageUUID)
                                                                                    // check city uuid exists
                                                                                    .flatMap(imageJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                                    //  check state uuid exists
                                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                                                    //  check country uuid exists
                                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                                    // check nic is unique
                                                                                                                                    .flatMap(countryJsonNode -> studentProfileRepository.findFirstByNicAndDeletedAtIsNull(entity.getNic())
                                                                                                                                            .flatMap(checkNic -> responseInfoMsg("NIC already exist"))
                                                                                                                                            // check student profile is unique
                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(entity.getStudentUUID())
                                                                                                                                                            .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Student Profile already exist")))
                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> studentProfileRepository.save(entity))
                                                                                                                                                            .flatMap(saveStudentProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                    .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", saveStudentProfileEntity)))
                                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                                                            .onErrorResume(ex -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                                                                    )
                                                                                                                                            )).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer"))
                                                                                                                            )).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                                            )).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                                                            )).switchIfEmpty(responseInfoMsg("Unable to upload image"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload image. Please contact developer"))
                                                                            ).switchIfEmpty(responseInfoMsg("Unable to upload image"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload image. Please contact developer"))
                                                                    ).switchIfEmpty(responseInfoMsg("Marital Status record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Marital Status record does not exist. Please contact developer"))
                                                            ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer"))
                                                    ).switchIfEmpty(responseInfoMsg("Caste record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Caste record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Sect record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Sect record does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Religion record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Religion record does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Student record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student record does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentProfileRepository.findByUuidAndDeletedAtIsNull(studentProfileUUID)
                        .flatMap(previousStdEntity -> {

                            MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                            StudentProfileEntity updatedEntity = StudentProfileEntity.builder()
                                    .uuid(previousStdEntity.getUuid())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .studentUUID(previousStdEntity.getStudentUUID())
                                    .firstName(value.getFirst("firstName").trim())
                                    .lastName(value.getFirst("lastName").trim())
                                    .description(value.getFirst("description").trim())
                                    .email(value.getFirst("email").trim())
                                    .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                                    .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                                    .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                                    .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                                    .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
                                    .image(UUID.fromString(value.getFirst("image").trim()))
                                    .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                    .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                    .telephoneNo(value.getFirst("telephoneNo").trim())
                                    .nic(value.getFirst("nic").trim())
                                    .birthDate(LocalDateTime.parse((value.getFirst("birthDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                    .createdAt(previousStdEntity.getCreatedAt())
                                    .createdBy(previousStdEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousStdEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousStdEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousStdEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousStdEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousStdEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousStdEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            sendFormData.add("docId", String.valueOf(updatedEntity.getImage()));

                            previousStdEntity.setDeletedBy(UUID.fromString(userId));
                            previousStdEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousStdEntity.setReqDeletedIP(reqIp);
                            previousStdEntity.setReqDeletedPort(reqPort);
                            previousStdEntity.setReqDeletedBrowser(reqBrowser);
                            previousStdEntity.setReqDeletedOS(reqOs);
                            previousStdEntity.setReqDeletedDevice(reqDevice);
                            previousStdEntity.setReqDeletedReferer(reqReferer);

                            // check student uuid exists
                            return studentRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentUUID())
                                    // check religion uuid exists
                                    .flatMap(studentEntity -> religionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getReligionUUID())
                                            // check sect uuid exists
                                            .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSectUUID())
                                                    // check caste uuid exists
                                                    .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCasteUUID())
                                                            // check gender uuid exists
                                                            .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                                                    // check marital status uuid exists
                                                                    .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getMaritalStatusUUID())
                                                                            // check emergency contact no uuid exists
                                                                            .flatMap(maritalStatusEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                                                    .flatMap(imageUUID -> apiCallService.checkDocId(imageUUID)
                                                                                            // check city uuid exists
                                                                                            .flatMap(imageJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                                            // check city uuid exists
                                                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                                                            // check city uuid exists
                                                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                                            .flatMap(countryJsonNode -> studentProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), studentProfileUUID)
                                                                                                                                                    .flatMap(checkNic -> responseInfoMsg("Nic already exist"))
                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), studentProfileUUID)
                                                                                                                                                            .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Student Profile already exist for given this Student"))))
                                                                                                                                                    .switchIfEmpty(Mono.defer(() ->
                                                                                                                                                            studentProfileRepository.save(previousStdEntity)
                                                                                                                                                                    .then(studentProfileRepository.save(updatedEntity))
                                                                                                                                                                    .flatMap(saveStudentProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", saveStudentProfileEntity)))
                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                                                                                                    ))
                                                                                                                                            )
                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Country record does not exist."))
                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer."))
                                                                                                                            )
                                                                                                                    ).switchIfEmpty(responseInfoMsg("State record does not exist."))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer."))
                                                                                                            )
                                                                                                    ).switchIfEmpty(responseInfoMsg("City record does not exist."))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer."))
                                                                                            )
                                                                                    ).switchIfEmpty(responseInfoMsg("Unable to upload image."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload image.Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Marital Status record does not exist."))
                                                                            .onErrorResume(ex -> responseErrorMsg("Marital Status record does not exist. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Gender record does not exist."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer"))
                                                            ).switchIfEmpty(responseInfoMsg("Caste record does not exist."))
                                                            .onErrorResume(ex -> responseErrorMsg("Caste record does not exist. Please contact developer"))
                                                    ).switchIfEmpty(responseInfoMsg("Sect record does not exist."))
                                                    .onErrorResume(ex -> responseErrorMsg("Sect record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Religion record does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Religion record does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Student record does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Student record does not exist.Please contact developer"));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Record does not exist.Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentProfileRepository.findByUuidAndDeletedAtIsNull(studentProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentProfileEntity updatedEntity = StudentProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .status(status == true ? true : false)
                                        .firstName(previousEntity.getFirstName())
                                        .lastName(previousEntity.getLastName())
                                        .description(previousEntity.getDescription())
                                        .studentUUID(previousEntity.getStudentUUID())
                                        .email(previousEntity.getEmail())
                                        .religionUUID(previousEntity.getReligionUUID())
                                        .sectUUID(previousEntity.getSectUUID())
                                        .casteUUID(previousEntity.getCasteUUID())
                                        .genderUUID(previousEntity.getGenderUUID())
                                        .maritalStatusUUID(previousEntity.getMaritalStatusUUID())
                                        .image(previousEntity.getImage())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .telephoneNo(previousEntity.getTelephoneNo())
                                        .nic(previousEntity.getNic())
                                        .birthDate(previousEntity.getBirthDate())
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

                                return studentProfileRepository.save(previousEntity)
                                        .then(studentProfileRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
        return studentProfileRepository.findByUuidAndDeletedAtIsNull(studentProfileUUID)
                .flatMap(studentProfileEntityDB -> {
                    // soft delete the record
                    studentProfileEntityDB.setDeletedBy(UUID.fromString(userId));
                    studentProfileEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentProfileEntityDB.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentProfileEntityDB.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentProfileEntityDB.setReqDeletedIP(reqIp);
                    studentProfileEntityDB.setReqDeletedPort(reqPort);
                    studentProfileEntityDB.setReqDeletedBrowser(reqBrowser);
                    studentProfileEntityDB.setReqDeletedOS(reqOs);
                    studentProfileEntityDB.setReqDeletedDevice(reqDevice);
                    studentProfileEntityDB.setReqDeletedReferer(reqReferer);

                    return studentProfileRepository.save(studentProfileEntityDB)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record. There is something wrong please try again."))
                            .onErrorResume(err -> responseErrorMsg("Unable to Delete Record. Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."));
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
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg
                )
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
