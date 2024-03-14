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
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherProfileRepository;
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

@Tag(name = "teacherFatherProfileHandler")
@Component
public class TeacherFatherProfileHandler {


    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherFatherProfileRepository teacherFatherProfileRepository;

    @Autowired
    SlaveTeacherFatherProfileRepository slaveTeacherFatherProfileRepository;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_index")
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
            Flux<SlaveTeacherFatherProfileEntity> slaveTeacherFatherProfileFlux = slaveTeacherFatherProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveTeacherFatherProfileFlux
                    .collectList()
                    .flatMap(teacherFatherProfileEntity -> slaveTeacherFatherProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (teacherFatherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        } else {
            Flux<SlaveTeacherFatherProfileEntity> slaveTeacherFatherProfileFlux = slaveTeacherFatherProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveTeacherFatherProfileFlux
                    .collectList()
                    .flatMap(teacherFatherProfileEntity -> slaveTeacherFatherProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (teacherFatherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherFatherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherFatherProfileRepository.findByUuidAndDeletedAtIsNull(teacherFatherProfileUUID)
                .flatMap(teacherFatherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFatherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_teacher-father_show")
    public Mono<ServerResponse> showByTeacherFatherUUID(ServerRequest serverRequest) {
        UUID teacherFatherUUID = UUID.fromString((serverRequest.pathVariable("teacherFatherUUID")));

        return slaveTeacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherUUID)
                .flatMap(teacherFatherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFatherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    // Show Teacher Profiles against Teacher , Teacher Father
    @AuthHasPermission(value = "academic_api_v1_teacher_teacher-father_teacher-father-profiles_show")
    public Mono<ServerResponse> showTeacherFatherProfile(ServerRequest serverRequest) {
        UUID teacherFatherProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID teacherFatherUUID = UUID.fromString(serverRequest.queryParam("teacherFatherUUID").map(String::toString).orElse(""));
        UUID teacherUUID = UUID.fromString(serverRequest.queryParam("teacherUUID").map(String::toString).orElse(""));

        return slaveTeacherFatherProfileRepository.showTeacherFatherProfileAgainstTeacherAndTeacherFather(teacherUUID, teacherFatherUUID, teacherFatherProfileUUID)
                .flatMap(teacherFatherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", teacherFatherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If Country Exists in Teacher Father Profile
    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherFatherProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If State Exists in Teacher Father Profile
    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherFatherProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If City Exists in Teacher Father Profile
    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveTeacherFatherProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_store")
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


                    TeacherFatherProfileEntity entity = TeacherFatherProfileEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .teacherFatherUUID(UUID.fromString(value.getFirst("teacherFatherUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .name(value.getFirst("name").trim())
                            .nic(value.getFirst("nic").trim())
                            .age(Integer.valueOf(value.getFirst("age")))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                            .officialTel(value.getFirst("officialTel").trim())
//                            .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
                            .email(value.getFirst("email").trim())
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

                    //checks if teacher father uuid exists
                    return teacherFatherRepository.findByUuidAndDeletedAtIsNull(entity.getTeacherFatherUUID())
                            //checks if contact no uuid exists
                            .flatMap(teacherFatherEntity ->
//                            teacherContactNoRepository.findByUuidAndDeletedAtIsNull(entity.getContactNoUUID())
                                            //checks if document uuid exists
//                                    .flatMap(contactNoEntity ->
                                            apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //check city uuid exists
                                                            .flatMap(docId -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", entity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //  check state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", entity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                            //  check country uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", entity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(saveEntity -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(entity.getTeacherFatherUUID())
                                                                                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Father Profile already exist"))
                                                                                                                    //     check father nic is unique
                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.findFirstByNicAndTeacherFatherUUIDAndDeletedAtIsNull(entity.getNic(), entity.getTeacherFatherUUID())
                                                                                                                            .flatMap(teacherFatherProfileEntity -> responseInfoMsg("Nic already exist"))))
                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.save(entity)
                                                                                                                            .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                    .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", teacherChildProfileEntity)))
                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                            .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
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
                                                            ).switchIfEmpty(responseInfoMsg("Unable to upload document"))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload document.Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Unable to upload document"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload document.Please contact developer."))
//                                    ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Teacher Father Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Father Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherFatherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> teacherFatherProfileRepository.findByUuidAndDeletedAtIsNull(teacherFatherProfileUUID)
                                .flatMap(previousEntity -> {
                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherFatherProfileEntity updatedEntity = TeacherFatherProfileEntity.builder()
                                            .uuid(previousEntity.getUuid())
                                            .teacherFatherUUID(previousEntity.getTeacherFatherUUID())
                                            .image(UUID.fromString(value.getFirst("image").trim()))
                                            .name(value.getFirst("name").trim())
                                            .nic(value.getFirst("nic").trim())
                                            .age(Integer.valueOf(value.getFirst("age")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                                            .officialTel(value.getFirst("officialTel").trim())
//                                    .contactNoUUID(UUID.fromString(value.getFirst("contactNoUUID").trim()))
                                            .email(value.getFirst("email").trim())
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
                                    //checks if document uuid exists
//                                    .flatMap(contactNoEntity ->
                                    return apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                    // check city uuid exists
                                                    .flatMap(docId -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                    //  check state uuid exists
                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                            .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                    //  check country uuid exists
                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                    .flatMap(saveEntity -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherFatherUUID(), updatedEntity.getUuid())
                                                                                                            .flatMap(teacherFatherProfileAlreadyExists -> responseInfoMsg("Father Profile already exist"))
                                                                                                            //     check father nic is unique
                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.findFirstByNicAndTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getTeacherFatherUUID(), teacherFatherProfileUUID)
                                                                                                                    .flatMap(teacherFatherProfileEntity -> responseInfoMsg("Nic already exist"))))
                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.save(previousEntity)
                                                                                                                    .then(teacherFatherProfileRepository.save(updatedEntity))
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
                                                    ).switchIfEmpty(responseInfoMsg("Unable to upload document"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload document.Please contact developer."))
                                            ).switchIfEmpty(responseInfoMsg("Unable to upload document"))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload document.Please contact developer."));
//                                    ).switchIfEmpty(responseInfoMsg("Contact No does not exist"))
//                                    .onErrorResume(ex -> responseErrorMsg("Contact No does not exist. Please contact developer."));
                                }).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherFatherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                    return teacherFatherProfileRepository.findByUuidAndDeletedAtIsNull(teacherFatherProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherFatherProfileEntity entity = TeacherFatherProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .teacherFatherUUID(previousEntity.getTeacherFatherUUID())
                                        .image(previousEntity.getImage())
                                        .name(previousEntity.getName())
                                        .nic(previousEntity.getNic())
                                        .age(previousEntity.getAge())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .noOfDependents(previousEntity.getNoOfDependents())
                                        .officialTel(previousEntity.getOfficialTel())
//                                        .contactNoUUID(previousEntity.getContactNoUUID())
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

                                return teacherFatherProfileRepository.save(previousEntity)
                                        .then(teacherFatherProfileRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teacher-father-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherFatherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return teacherFatherProfileRepository.findByUuidAndDeletedAtIsNull(teacherFatherProfileUUID)
                .flatMap(teacherFatherProfileEntity -> {

                    teacherFatherProfileEntity.setDeletedBy(UUID.fromString(userId));
                    teacherFatherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    teacherFatherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    teacherFatherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    teacherFatherProfileEntity.setReqDeletedIP(reqIp);
                    teacherFatherProfileEntity.setReqDeletedPort(reqPort);
                    teacherFatherProfileEntity.setReqDeletedBrowser(reqBrowser);
                    teacherFatherProfileEntity.setReqDeletedOS(reqOs);
                    teacherFatherProfileEntity.setReqDeletedDevice(reqDevice);
                    teacherFatherProfileEntity.setReqDeletedReferer(reqReferer);

                    return teacherFatherProfileRepository.save(teacherFatherProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please contact developer."));
                })
                .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
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
