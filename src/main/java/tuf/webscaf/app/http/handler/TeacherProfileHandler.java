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
import tuf.webscaf.app.dbContext.master.entity.TeacherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherProfileRepository;
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

@Tag(name = "teacherProfileHandler")
@Component
public class TeacherProfileHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @Autowired
    SlaveTeacherProfileRepository slaveTeacherProfileRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

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
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_index")
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
            Flux<SlaveTeacherProfileEntity> slaveTeacherProfileFlux = slaveTeacherProfileRepository
                    .findAllByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherProfileFlux
                    .collectList()
                    .flatMap(teacherProfileEntity -> slaveTeacherProfileRepository.countByFirstNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherProfileEntity> slaveTeacherProfileFlux = slaveTeacherProfileRepository
                    .findAllByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveTeacherProfileFlux
                    .collectList()
                    .flatMap(teacherProfileEntity -> slaveTeacherProfileRepository.countByFirstNameContainingIgnoreCaseAndDeletedAtIsNullOrLastNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrTelephoneNoContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherProfileRepository.findByUuidAndDeletedAtIsNull(teacherProfileUUID)
                .flatMap(teacherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_teacher_show")
    public Mono<ServerResponse> showByTeacherUUID(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("teacherUUID"));

        return slaveTeacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists in Teacher Profile
    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists in Teacher Profile
    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists in Teacher Profile
    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_store")
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

                    TeacherProfileEntity entity = TeacherProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .firstName(value.getFirst("firstName").trim())
                            .lastName(value.getFirst("lastName").trim())
                            .email(value.getFirst("email").trim())
//                            .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
                            .nic(value.getFirst("nic").trim())
                            .telephoneNo(value.getFirst("telephoneNo").trim())
                            .birthDate(LocalDateTime.parse(value.getFirst("birthDate"),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                            .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                            .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                            .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
//                            .emergencyContactPerson(value.getFirst("emergencyContactPerson").trim())
//                            .emergencyContactNoUUID(UUID.fromString(value.getFirst("emergencyContactNoUUID").trim()))
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

                    sendFormData.add("docId", String.valueOf(entity.getImage()));

                    //checks if teacher uuid exists
                    return teacherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherUUID())
                            //checks if document uuid exists
                            .flatMap(teacherEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks if contactNo uuid exists
                                                            .flatMap(documentJsonNode ->
//                                                    teacherContactNoRepository.findByUuidAndDeletedAtIsNull(entity.getContactNoUUID())
                                                                            //checks if email uuid exists
//                                                    .flatMap(contactNoEntity ->
                                                                            religionRepository.findByUuidAndDeletedAtIsNull(entity.getReligionUUID())
                                                                                    //checks if sect uuid exists
                                                                                    .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(entity.getSectUUID())
                                                                                                    //checks if caste uuid exists
                                                                                                    .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(entity.getCasteUUID())
                                                                                                                    //checks if gender uuid exists
                                                                                                                    .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                                                                                                                    //checks if marital status uuid exists
                                                                                                                                    .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(entity.getMaritalStatusUUID())
                                                                                                                                                    //checks if emergency contactNo uuid exists
                                                                                                                                                    .flatMap(maritalStatusEntity ->
//                                                                                                            teacherContactNoRepository.findByUuidAndDeletedAtIsNull(entity.getEmergencyContactNoUUID())
                                                                                                                                                                    //check city uuid exists
//                                                                                                    .flatMap(studentDocumentEntity ->
                                                                                                                                                                    apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                                                                                                                            .flatMap(cityJsonNode -> apiCallService.getUUID(cityJsonNode)
                                                                                                                                                                                    //  check state uuid exists
                                                                                                                                                                                    .flatMap(cityUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                                                                                                                            .flatMap(stateJsonNode -> apiCallService.getUUID(stateJsonNode)
                                                                                                                                                                                                    //  check country uuid exists
                                                                                                                                                                                                    .flatMap(stateUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                                                                                                                            .flatMap(countryJsonNode -> apiCallService.getUUID(countryJsonNode)
                                                                                                                                                                                                                    //  check teacher profile is unique
                                                                                                                                                                                                                    .flatMap(countryUUID -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(entity.getTeacherUUID())
                                                                                                                                                                                                                            .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Profile Already Exists"))
                                                                                                                                                                                                                            //  check nic is unique
                                                                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.findFirstByNicAndDeletedAtIsNull(entity.getNic())
                                                                                                                                                                                                                                    .flatMap(nicAlreadyExists -> responseInfoMsg("Nic Already Exists"))))
                                                                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.save(entity)
                                                                                                                                                                                                                                    .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", teacherChildProfileEntity)))
                                                                                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer"))
                                                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer"))
                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer"))
                                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer"))
                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer"))
                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer"))
//                                                                                                    ).switchIfEmpty(responseInfoMsg("Emergency Contact No does not exist"))
//                                                                                                    .onErrorResume(ex -> responseErrorMsg("Emergency Contact No does not exist. Please contact developer."))
                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Marital Status does not exist"))
                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Marital Status does not exist. Please contact developer."))
                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Gender does not exist"))
                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Gender does not exist. Please contact developer."))
                                                                                                                    ).switchIfEmpty(responseInfoMsg("Caste does not exist"))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Caste does not exist. Please contact developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Sect does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Sect does not exist. Please contact developer."))
                                                                                    ).switchIfEmpty(responseInfoMsg("Religion does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Religion does not exist. Please contact developer."))
//                                                    ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                                    .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Teacher does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherProfileRepository.findByUuidAndDeletedAtIsNull(teacherProfileUUID)
                                .flatMap(entity -> {
                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherProfileEntity updatedEntity = TeacherProfileEntity.builder()
                                            .uuid(entity.getUuid())
                                            .teacherUUID(entity.getTeacherUUID())
                                            .image(UUID.fromString(value.getFirst("image").trim()))
                                            .firstName(value.getFirst("firstName").trim())
                                            .lastName(value.getFirst("lastName").trim())
                                            .email(value.getFirst("email").trim())
//                                    .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
                                            .nic(value.getFirst("nic").trim())
                                            .telephoneNo(value.getFirst("telephoneNo").trim())
                                            .birthDate(LocalDateTime.parse(value.getFirst("birthDate"),
                                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                            .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                                            .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                                            .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                                            .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
//                                    .emergencyContactPerson(value.getFirst("emergencyContactPerson").trim())
//                                    .emergencyContactNoUUID(UUID.fromString(value.getFirst("emergencyContactNoUUID").trim()))
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

                                    sendFormData.add("docId", String.valueOf(updatedEntity.getImage()));

                                    //checks if image uuid exists
                                    return apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                            .flatMap(documentJson -> apiCallService.checkDocId(documentJson)
                                                            //checks if contactNo uuid exists
                                                            .flatMap(docId ->
//                                                    teacherContactNoRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getContactNoUUID())
                                                                            //checks if email uuid exists
//                                                    .flatMap(contactNoEntity ->
                                                                            religionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getReligionUUID())
                                                                                    //checks if sect uuid exists
                                                                                    .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSectUUID())
                                                                                                    //checks if caste uuid exists
                                                                                                    .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCasteUUID())
                                                                                                                    //checks if gender uuid exists
                                                                                                                    .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                                                                                                                    //checks if marital status uuid exists
                                                                                                                                    .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getMaritalStatusUUID())
                                                                                                                                                    //checks if emergency contact No uuid exists
                                                                                                                                                    .flatMap(maritalStatusEntity ->
//                                                                                                    teacherContactNoRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getEmergencyContactNoUUID())
                                                                                                                                                                    // check city uuid exists
//                                                                                                    .flatMap(studentDocumentEntity ->
                                                                                                                                                                    apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                                                                                                                            .flatMap(cityJsonNode -> apiCallService.getUUID(cityJsonNode)
                                                                                                                                                                                    //  check state uuid exists
                                                                                                                                                                                    .flatMap(cityUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                                                                                                                            .flatMap(stateJsonNode -> apiCallService.getUUID(stateJsonNode)
                                                                                                                                                                                                    //  check country uuid exists
                                                                                                                                                                                                    .flatMap(stateUUID -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                                                                                                                            .flatMap(countryJsonNode -> apiCallService.getUUID(countryJsonNode)
                                                                                                                                                                                                                    //  check teacher profile is unique
                                                                                                                                                                                                                    .flatMap(countryUUID -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), teacherProfileUUID)
                                                                                                                                                                                                                            .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Profile Already Exists"))
                                                                                                                                                                                                                            //  check nic is unique
                                                                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
                                                                                                                                                                                                                                    .flatMap(nicAlreadyExists -> responseInfoMsg("Nic Already Exists"))))
                                                                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.save(entity)
                                                                                                                                                                                                                                    .then(teacherProfileRepository.save(updatedEntity))
                                                                                                                                                                                                                                    .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", teacherChildProfileEntity)))
                                                                                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))))
                                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer")))
                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
//                                                                                                    ).switchIfEmpty(responseInfoMsg("Emergency Contact No does not exist"))
//                                                                                                    .onErrorResume(ex -> responseErrorMsg("Emergency Contact No does not exist. Please contact developer."))
                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Marital Status does not exist"))
                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Marital Status does not exist. Please contact developer."))
                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Gender does not exist"))
                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Gender does not exist. Please contact developer."))
                                                                                                                    ).switchIfEmpty(responseInfoMsg("Caste does not exist"))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Caste does not exist. Please contact developer."))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Sect does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Sect does not exist. Please contact developer."))
                                                                                    ).switchIfEmpty(responseInfoMsg("Religion does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Religion does not exist. Please contact developer."))
//                                                    ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                                    .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Unable to upload image"))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload image. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Unable to upload image"))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload image. Please contact developer."));
                                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        final UUID teacherProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    Boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return teacherProfileRepository.findByUuidAndDeletedAtIsNull(teacherProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherProfileEntity entity = TeacherProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .teacherUUID(previousEntity.getTeacherUUID())
                                        .image(previousEntity.getImage())
                                        .firstName(previousEntity.getFirstName())
                                        .lastName(previousEntity.getLastName())
                                        .email(previousEntity.getEmail())
//                                        .contactNoUUID(previousEntity.getContactNoUUID())
                                        .nic(previousEntity.getNic())
                                        .telephoneNo(previousEntity.getTelephoneNo())
                                        .birthDate(previousEntity.getBirthDate())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .religionUUID(previousEntity.getReligionUUID())
                                        .sectUUID(previousEntity.getSectUUID())
                                        .casteUUID(previousEntity.getCasteUUID())
                                        .genderUUID(previousEntity.getGenderUUID())
                                        .maritalStatusUUID(previousEntity.getMaritalStatusUUID())
//                                        .emergencyContactPerson(previousEntity.getEmergencyContactPerson())
//                                        .emergencyContactNoUUID(previousEntity.getEmergencyContactNoUUID())
//                                        .teacherDocumentUUID(previousEntity.getTeacherDocumentUUID())
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

                                return teacherProfileRepository.save(previousEntity)
                                        .then(teacherProfileRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID teacherProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherProfileRepository.findByUuidAndDeletedAtIsNull(teacherProfileUUID)
                //check in Teacher Documents
                .flatMap(teacherProfileEntity -> {
                    teacherProfileEntity.setDeletedBy(UUID.fromString(userId));
                    teacherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherProfileEntity.setReqDeletedIP(reqIp);
                    teacherProfileEntity.setReqDeletedPort(reqPort);
                    teacherProfileEntity.setReqDeletedBrowser(reqBrowser);
                    teacherProfileEntity.setReqDeletedOS(reqOs);
                    teacherProfileEntity.setReqDeletedDevice(reqDevice);
                    teacherProfileEntity.setReqDeletedReferer(reqReferer);

                    return teacherProfileRepository.save(teacherProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to Delete Record.There is something wrong please try again."))
                            .onErrorResume(err -> responseErrorMsg("Unable to Delete Record.Please Contact Developer."));
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
