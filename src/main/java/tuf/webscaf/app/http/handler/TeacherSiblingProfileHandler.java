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
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSiblingProfileRepository;
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

@Tag(name = "teacherSiblingProfileHandler")
@Component
public class TeacherSiblingProfileHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSiblingProfileRepository teacherSiblingProfileRepository;

    @Autowired
    SlaveTeacherSiblingProfileRepository slaveTeacherSiblingProfileRepository;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    TeacherRepository studentRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_index")
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

            Flux<SlaveTeacherSiblingProfileEntity> slaveTeacherSiblingProfileFlux = slaveTeacherSiblingProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveTeacherSiblingProfileFlux
                    .collectList()
                    .flatMap(teacherSiblingProfileEntity -> slaveTeacherSiblingProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingProfileEntity, count);
                                }
                            })).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));

        } else {

            Flux<SlaveTeacherSiblingProfileEntity> slaveTeacherSiblingProfileFlux = slaveTeacherSiblingProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveTeacherSiblingProfileFlux
                    .collectList()
                    .flatMap(teacherSiblingProfileEntity -> slaveTeacherSiblingProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingProfileEntity, count);
                                }
                            })).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                .flatMap(studentSiblingProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_teacher-sibling_show")
    public Mono<ServerResponse> showByTeacherSiblingUUID(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("teacherSiblingUUID")));

        return slaveTeacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(studentSiblingUUID)
                .flatMap(studentSiblingProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentSiblingProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    // Show Teacher Profiles against Teacher , Teacher Sibling
    @AuthHasPermission(value = "academic_api_v1_teacher_teacher-sibling_teacher-sibling-profiles_show")
    public Mono<ServerResponse> showTeacherSiblingProfile(ServerRequest serverRequest) {
        UUID teacherSiblingProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID teacherSiblingUUID = UUID.fromString(serverRequest.queryParam("teacherSiblingUUID").map(String::toString).orElse(""));
        UUID teacherUUID = UUID.fromString(serverRequest.queryParam("teacherUUID").map(String::toString).orElse(""));

        return slaveTeacherSiblingProfileRepository.showTeacherSiblingProfileAgainstTeacherAndTeacherSibling(teacherUUID, teacherSiblingUUID, teacherSiblingProfileUUID)
                .flatMap(teacherSiblingProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherSiblingProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }


    //This function is used by delete function of Country Handler in Config Module to Check If country Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSiblingProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSiblingProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists
    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherSiblingProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_store")
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


                    TeacherSiblingProfileEntity entity = TeacherSiblingProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .email(value.getFirst("email").trim())
                            .teacherSiblingUUID(UUID.fromString(value.getFirst("teacherSiblingUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image")))
                            .age(Integer.valueOf(value.getFirst("age")))
                            .nic(value.getFirst("nic").trim())
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
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


                    //checks if teacher sibling uuid exists
                    return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherSiblingUUID())
                            //checks if contact no uuid exists
                            .flatMap(teacherProfileEntity ->
//                                    teacherContactNoRepository.findByUuidAndDeletedAtIsNull(entity.getContactNoUUID())
//                                    .flatMap(contactNoEntity ->

                                            //checks if gender uuid exists
                                            genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                                    //checks if doc id exists
                                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                                    //check city uuid exists
                                                                    .flatMap(studentDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                    //  check state uuid exists
                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                                    //  check country uuid exists
                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                    .flatMap(saveEntity -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(entity.getTeacherSiblingUUID())
                                                                                                                            .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Sibling Profile already exist"))
                                                                                                                            //     check Sibling nic is unique
                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNull(entity.getTeacherSiblingUUID(), entity.getNic())
                                                                                                                                    .flatMap(teacherMotherProfileEntity -> responseInfoMsg("Nic already exist"))))
                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.save(entity)
                                                                                                                                    .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", teacherChildProfileEntity)))
                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                                                                            ))
                                                                                                                    ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer"))
                                                                                                            ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer"))
                                                                                                    ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                            ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                    ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                                            ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                                    ).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
//                                    ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Teacher Sibling Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Sibling Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherSiblingProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherSiblingProfileRepository.findByUuidAndDeletedAtIsNull(teacherSiblingProfileUUID)
                                .flatMap(previousEntity -> {
                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherSiblingProfileEntity updatedEntity = TeacherSiblingProfileEntity.builder()
                                            .uuid(previousEntity.getUuid())
                                            .name(value.getFirst("name").trim())
                                            .email(value.getFirst("email"))
                                            .teacherSiblingUUID(previousEntity.getTeacherSiblingUUID())
                                            .image(UUID.fromString(value.getFirst("image").trim()))
                                            .age(Integer.valueOf(value.getFirst("age")))
                                            .nic(value.getFirst("nic").trim())
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                            .officialTel(value.getFirst("officialTel").trim())
//                                    .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
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

                                    //checks if contact no uuid exists

//                                    teacherContactNoRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getContactNoUUID())
                                    //checks if gender uuid exists
//                                    .flatMap(contactNoEntity ->
                                    return genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                            //checks if document uuid exists
                                            .flatMap(genderEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            // check city uuid exists
                                                            .flatMap(studentDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //  check state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                            //  check country uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(saveEntity -> teacherSiblingProfileRepository
                                                                                                                    .findFirstByTeacherSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSiblingUUID(), teacherSiblingProfileUUID)
                                                                                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Sibling Profile already exist"))
                                                                                                                    //     check Sibling nic is unique
                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSiblingUUID(), updatedEntity.getNic(), teacherSiblingProfileUUID)
                                                                                                                            .flatMap(teacherSiblingProfileEntity -> responseInfoMsg("Nic already exist"))))
                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.save(previousEntity)
                                                                                                                            .then(teacherSiblingProfileRepository.save(updatedEntity))
                                                                                                                            .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                    .flatMap(documentUpload -> responseSuccessMsg("Record Updated Successfully", teacherChildProfileEntity)))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))
                                                                                                                    ))
                                                                                                            ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer"))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer"))
                                                                                            ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                    ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                            ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                                    ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                            ).switchIfEmpty(responseInfoMsg("Document does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Document does not exist.Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Document does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Document does not exist.Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."));
//                                    ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."));
                                })
                                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return teacherSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherSiblingProfileEntity entity = TeacherSiblingProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .teacherSiblingUUID(previousEntity.getTeacherSiblingUUID())
                                        .image(previousEntity.getImage())
                                        .age(previousEntity.getAge())
                                        .nic(previousEntity.getNic())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
//                                        .noOfDependents(previousEntity.getNoOfDependents())
                                        .officialTel(previousEntity.getOfficialTel())
//                                        .contactNoUUID(previousEntity.getContactNoUUID())
                                        .genderUUID(previousEntity.getGenderUUID())
                                        .email(previousEntity.getEmail())
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

                                return teacherSiblingProfileRepository.save(previousEntity)
                                        .then(teacherSiblingProfileRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-sibling-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherSiblingProfileRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileUUID)
                .flatMap(teacherSiblingProfileEntity -> {
                    teacherSiblingProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherSiblingProfileEntity.setDeletedBy(UUID.fromString(userId));
                    teacherSiblingProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherSiblingProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherSiblingProfileEntity.setReqDeletedIP(reqIp);
                    teacherSiblingProfileEntity.setReqDeletedPort(reqPort);
                    teacherSiblingProfileEntity.setReqDeletedBrowser(reqBrowser);
                    teacherSiblingProfileEntity.setReqDeletedOS(reqOs);
                    teacherSiblingProfileEntity.setReqDeletedDevice(reqDevice);
                    teacherSiblingProfileEntity.setReqDeletedReferer(reqReferer);

                    return teacherSiblingProfileRepository.save(teacherSiblingProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
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
