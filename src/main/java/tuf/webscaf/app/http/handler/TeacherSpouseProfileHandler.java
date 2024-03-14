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
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSpouseProfileRepository;
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

@Tag(name = "teacherSpouseProfileHandler")
@Component
public class TeacherSpouseProfileHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSpouseProfileRepository teacherSpouseProfileRepository;

    @Autowired
    SlaveTeacherSpouseProfileRepository slaveTeacherSpouseProfileRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    TeacherSpouseJobHistoryRepository teacherSpouseJobHistoryRepository;

    @Autowired
    TeacherSpouseDocumentRepository teacherSpouseDocumentRepository;

    @Autowired
    TeacherSpouseAcademicHistoryRepository teacherSpouseAcademicHistoryRepository;

    @Autowired
    TeacherSpouseFamilyDoctorRepository teacherSpouseFamilyDoctorRepository;

    @Autowired
    TeacherSpouseHobbyPvtRepository teacherSpouseHobbyPvtRepository;

    @Autowired
    TeacherSpouseNationalityPvtRepository teacherSpouseNationalityPvtRepository;

    @Autowired
    TeacherSpouseAddressRepository teacherSpouseAddressRepository;

    @Autowired
    TeacherSpouseFinancialHistoryRepository teacherSpouseFinancialHistoryRepository;

    @Autowired
    TeacherSpouseAilmentPvtRepository teacherSpouseAilmentPvtRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_index")
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
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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
            Flux<SlaveTeacherSpouseProfileEntity> slaveTeacherSpouseProfileFlux = slaveTeacherSpouseProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherSpouseProfileFlux
                    .collectList()
                    .flatMap(teacherSpouseProfileEntity -> slaveTeacherSpouseProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherSpouseProfileEntity> slaveTeacherSpouseProfileFlux = slaveTeacherSpouseProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveTeacherSpouseProfileFlux
                    .collectList()
                    .flatMap(teacherSpouseProfileEntity -> slaveTeacherSpouseProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherSpouseProfileRepository.findByUuidAndDeletedAtIsNull(teacherSpouseProfileUUID)
                .flatMap(teacherFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_teacher-spouse_show")
    public Mono<ServerResponse> showByTeacherSpouseUUID(ServerRequest serverRequest) {
        UUID teacherSpouseUUID = UUID.fromString(serverRequest.pathVariable("teacherSpouseUUID"));

        return slaveTeacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseUUID)
                .flatMap(teacherFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    // Show Teacher Profiles against Teacher , Teacher Spouse
    @AuthHasPermission(value = "academic_api_v1_teacher_teacher-spouse_teacher-spouse-profiles_show")
    public Mono<ServerResponse> showTeacherSpouseProfile(ServerRequest serverRequest) {
        UUID teacherSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID teacherSpouseUUID = UUID.fromString(serverRequest.queryParam("teacherSpouseUUID").map(String::toString).orElse(""));
        UUID teacherUUID = UUID.fromString(serverRequest.queryParam("teacherUUID").map(String::toString).orElse(""));

        return slaveTeacherSpouseProfileRepository.showTeacherSpouseProfileAgainstTeacherAndTeacherSpouse(teacherUUID, teacherSpouseUUID, teacherSpouseProfileUUID)
                .flatMap(teacherSpouseProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherSpouseProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSpouseProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSpouseProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSpouseProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_store")
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

                    TeacherSpouseProfileEntity entity = TeacherSpouseProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .email(value.getFirst("email").trim())
                            .teacherSpouseUUID(UUID.fromString(value.getFirst("teacherSpouseUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .age(Integer.valueOf(value.getFirst("age")))
                            .nic(value.getFirst("nic").trim())
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents").trim()))
                            .officialTel(value.getFirst("officialTel").trim())
//                            .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
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

                    //checks if teacher profile uuid exists
                    return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherSpouseUUID())
                            //checks if gender uuid exists
                            .flatMap(teacherSpouseEntity -> genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                            //checks if contact no uuid exists
                                            .flatMap(genderEntity ->
//                                                    teacherContactNoRepository.findByUuidAndDeletedAtIsNull(entity.getContactNoUUID())
                                                            //checks if document uuid exists
//                                            .flatMap(contactNoEntity ->
                                                            apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                                            //check city uuid exists
                                                                            .flatMap(documentJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //  check state uuid exists
                                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                                            //  check country uuid exists
                                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                            .flatMap(saveEntity -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(entity.getTeacherSpouseUUID())
                                                                                                                                    .flatMap(teacherSpouseProfileAlreadyExists -> responseInfoMsg("Teacher Spouse Profile already exist"))
                                                                                                                                    //  check nic is unique
                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.findFirstByNicAndDeletedAtIsNull(entity.getNic())
                                                                                                                                            .flatMap(nicAlreadyExists -> responseInfoMsg("NIC already exist"))))
                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.save(entity)
                                                                                                                                            .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                    .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", teacherChildProfileEntity)))
                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))))
                                                                                                                            ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer")))
                                                                                                            ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer")))
                                                                                            ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer")))
                                                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                                                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
//                                            ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                            .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Teacher Spouse Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Spouse Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherSpouseProfileRepository.findByUuidAndDeletedAtIsNull(teacherSpouseProfileUUID)
                                .flatMap(previousEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();


                                    TeacherSpouseProfileEntity updatedEntity = TeacherSpouseProfileEntity.builder()
                                            .uuid(previousEntity.getUuid())
                                            .name(value.getFirst("name").trim())
                                            .email(value.getFirst("email").trim())
                                            .teacherSpouseUUID(previousEntity.getTeacherSpouseUUID())
                                            .image(UUID.fromString(value.getFirst("image").trim()))
                                            .age(Integer.valueOf(value.getFirst("age")))
                                            .nic(value.getFirst("nic").trim())
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents").trim()))
                                            .officialTel(value.getFirst("officialTel").trim())
//                                    .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
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

                                    sendFormData.add("docId", String.valueOf(updatedEntity.getImage()));

                                    //checks if gender uuid exists
                                    return genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                            //checks if contact no uuid exists
                                            .flatMap(genderEntity ->
//                                            teacherContactNoRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getContactNoUUID())
                                                            // checks if document uuid exists
//                                            .flatMap(contactNoEntity ->
                                                            apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                                            // check city uuid exists
                                                                            .flatMap(documentJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //  check state uuid exists
                                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                                            //  check country uuid exists
                                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                            .flatMap(saveEntity -> teacherSpouseProfileRepository
                                                                                                                                    .findFirstByTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSpouseUUID(), teacherSpouseProfileUUID)
                                                                                                                                    .flatMap(teacherSpouseProfileAlreadyExists -> responseInfoMsg("Teacher Spouse Profile already exist"))
                                                                                                                                    //  check nic is unique
                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), teacherSpouseProfileUUID)
                                                                                                                                            .flatMap(nicAlreadyExists -> responseInfoMsg("NIC already exist"))))
                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.save(previousEntity)
                                                                                                                                            .then(teacherSpouseProfileRepository.save(updatedEntity))
                                                                                                                                            .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                    .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", teacherChildProfileEntity)))
                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))))
                                                                                                                            ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer")))
                                                                                                            ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer")))
                                                                                            ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer")))
                                                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
                                                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer"))
//                                            ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                            .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."));
                                })
                                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return teacherSpouseProfileRepository.findByUuidAndDeletedAtIsNull(teacherSpouseProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherSpouseProfileEntity entity = TeacherSpouseProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .email(previousEntity.getEmail())
                                        .teacherSpouseUUID(previousEntity.getTeacherSpouseUUID())
                                        .image(previousEntity.getImage())
                                        .age(previousEntity.getAge())
                                        .nic(previousEntity.getNic())
                                        .genderUUID(previousEntity.getGenderUUID())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .noOfDependents(previousEntity.getNoOfDependents())
                                        .officialTel(previousEntity.getOfficialTel())
//                                        .contactNoUUID(previousEntity.getContactNoUUID())
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

                                return teacherSpouseProfileRepository.save(previousEntity)
                                        .then(teacherSpouseProfileRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseInfoMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-spouse-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return teacherSpouseProfileRepository.findByUuidAndDeletedAtIsNull(teacherSpouseProfileUUID)
                .flatMap(teacherSpouseProfileEntity -> {

                    teacherSpouseProfileEntity.setDeletedBy(UUID.fromString(userId));
                    teacherSpouseProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherSpouseProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherSpouseProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherSpouseProfileEntity.setReqDeletedIP(reqIp);
                    teacherSpouseProfileEntity.setReqDeletedPort(reqPort);
                    teacherSpouseProfileEntity.setReqDeletedBrowser(reqBrowser);
                    teacherSpouseProfileEntity.setReqDeletedOS(reqOs);
                    teacherSpouseProfileEntity.setReqDeletedDevice(reqDevice);
                    teacherSpouseProfileEntity.setReqDeletedReferer(reqReferer);

                    return teacherSpouseProfileRepository.save(teacherSpouseProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again"))
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
