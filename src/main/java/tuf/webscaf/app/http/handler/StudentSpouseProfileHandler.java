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
import tuf.webscaf.app.dbContext.master.entity.StudentSpouseProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSpouseProfileRepository;
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

@Tag(name = "studentSpouseProfileHandler")
@Component
public class StudentSpouseProfileHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSpouseProfileRepository studentSpouseProfileRepository;

    @Autowired
    SlaveStudentSpouseProfileRepository slaveStudentSpouseProfileRepository;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_index")
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
            Flux<SlaveStudentSpouseProfileEntity> slaveStudentSpouseProfileFlux = slaveStudentSpouseProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentSpouseProfileFlux
                    .collectList()
                    .flatMap(studentSpouseProfileEntity -> slaveStudentSpouseProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentSpouseProfileEntity> slaveStudentSpouseProfileFlux = slaveStudentSpouseProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);
            return slaveStudentSpouseProfileFlux
                    .collectList()
                    .flatMap(studentSpouseProfileEntity -> slaveStudentSpouseProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrNicContainingIgnoreCaseAndDeletedAtIsNullOrOfficialTelContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveStudentSpouseProfileRepository.findByUuidAndDeletedAtIsNull(studentSpouseProfileUUID)
                .flatMap(studentFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_student-spouse_show")
    public Mono<ServerResponse> showByStudentSpouseUUID(ServerRequest serverRequest) {
        UUID studentSpouseUUID = UUID.fromString(serverRequest.pathVariable("studentSpouseUUID"));

        return slaveStudentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseUUID)
                .flatMap(studentFinancialHistoryEntity -> responseSuccessMsg("Record Fetched Successfully", studentFinancialHistoryEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    // Show Student Profiles against Student , Student Spouse
    @AuthHasPermission(value = "academic_api_v1_student_student-spouse_student-spouse-profiles_show")
    public Mono<ServerResponse> showStudentSpouseProfile(ServerRequest serverRequest) {
        UUID studentSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentSpouseUUID = UUID.fromString(serverRequest.queryParam("studentSpouseUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentSpouseProfileRepository.showStudentSpouseProfileAgainstStudentAndStudentSpouse(studentUUID, studentSpouseUUID, studentSpouseProfileUUID)
                .flatMap(studentSpouseProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentSpouseProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists
    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSpouseProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists
    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSpouseProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists
    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentSpouseProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_store")
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

                    StudentSpouseProfileEntity entity = StudentSpouseProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .email(value.getFirst("email").trim())
                            .studentSpouseUUID(UUID.fromString(value.getFirst("studentSpouseUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .age(Integer.valueOf(value.getFirst("age")))
                            .nic(value.getFirst("nic").trim())
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents").trim()))
                            .officialTel(value.getFirst("officialTel").trim())
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

                    //checks if student spouse uuid exists
                    return studentSpouseRepository.findByUuidAndDeletedAtIsNull(entity.getStudentSpouseUUID())
                            //checks if gender uuid exists
                            .flatMap(studentSpouseEntity -> genderRepository.findByUuidAndDeletedAtIsNull(entity.getGenderUUID())
                                    //checks if contact no uuid exists
                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
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
                                                                                                    .flatMap(saveEntity -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(entity.getStudentSpouseUUID())
                                                                                                            .flatMap(studentSpouseAlreadyExists -> responseInfoMsg("Student Spouse Profile already exist"))
                                                                                                            //  check nic is unique
                                                                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.findFirstByNicAndDeletedAtIsNull(entity.getNic())
                                                                                                                    .flatMap(nicAlreadyExists -> responseInfoMsg("NIC already exist"))))
                                                                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.save(entity)
                                                                                                                    .flatMap(studentChildProfileEntity -> responseSuccessMsg("Record Stored Successfully", studentChildProfileEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Record not stored. There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. There is something wrong please try again."))
                                                                                                            ))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer")))
                                                                                    ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer")))
                                                                    ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer")))
                                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer")))
                                    ).switchIfEmpty(responseInfoMsg("Gender does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Gender does not exist. Please contact developer."))
                            ).switchIfEmpty(responseInfoMsg("Student Spouse Record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Student Spouse Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> studentSpouseProfileRepository.findByUuidAndDeletedAtIsNull(studentSpouseProfileUUID)
                        .flatMap(previousEntity -> {

                            StudentSpouseProfileEntity updatedEntity = StudentSpouseProfileEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .email(value.getFirst("email").trim())
                                    .studentSpouseUUID(previousEntity.getStudentSpouseUUID())
                                    .image(UUID.fromString(value.getFirst("image").trim()))
                                    .age(Integer.valueOf(value.getFirst("age")))
                                    .nic(value.getFirst("nic").trim())
                                    .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                                    .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                    .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                    .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents").trim()))
                                    .officialTel(value.getFirst("officialTel").trim())
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

                            //checks if gender uuid exists
                            return genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                    //checks if contact no uuid exists
                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
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
                                                                                                    .flatMap(saveEntity -> studentSpouseProfileRepository
                                                                                                            .findFirstByStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSpouseUUID(), studentSpouseProfileUUID)
                                                                                                            .flatMap(studentSpouseAlreadyExists -> responseInfoMsg("Student Spouse Profile already exist"))
                                                                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
                                                                                                                    .flatMap(nicAlreadyExists -> responseInfoMsg("NIC already exist"))))
                                                                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.save(previousEntity)
                                                                                                                    .then(studentSpouseProfileRepository.save(updatedEntity))
                                                                                                                    .flatMap(studentChildProfileEntity -> responseSuccessMsg("Record Updated Successfully", studentChildProfileEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."))

                                                                                                            ))
                                                                                                    ).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer")))
                                                                                    ).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer")))
                                                                    ).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer")))
                                                    ).switchIfEmpty(responseInfoMsg("Unable to Upload document"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Upload document. Please contact developer")))
                                    ).switchIfEmpty(responseInfoMsg("Gender does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Gender does not exist. Please contact developer."));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    return studentSpouseProfileRepository.findByUuidAndDeletedAtIsNull(studentSpouseProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentSpouseProfileEntity entity = StudentSpouseProfileEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .studentSpouseUUID(previousEntity.getStudentSpouseUUID())
                                        .image(previousEntity.getImage())
                                        .age(previousEntity.getAge())
                                        .nic(previousEntity.getNic())
                                        .genderUUID(previousEntity.getGenderUUID())
                                        .cityUUID(previousEntity.getCityUUID())
                                        .stateUUID(previousEntity.getStateUUID())
                                        .countryUUID(previousEntity.getCountryUUID())
                                        .noOfDependents(previousEntity.getNoOfDependents())
                                        .officialTel(previousEntity.getOfficialTel())
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

                                return studentSpouseProfileRepository.save(previousEntity)
                                        .then(studentSpouseProfileRepository.save(entity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status updated successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status"))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. There is something wrong please try again."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-spouse-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSpouseProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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

        return studentSpouseProfileRepository.findByUuidAndDeletedAtIsNull(studentSpouseProfileUUID)
                .flatMap(studentSpouseProfileEntity -> {

                    studentSpouseProfileEntity.setDeletedBy(UUID.fromString(userId));
                    studentSpouseProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentSpouseProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentSpouseProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentSpouseProfileEntity.setReqDeletedIP(reqIp);
                    studentSpouseProfileEntity.setReqDeletedPort(reqPort);
                    studentSpouseProfileEntity.setReqDeletedBrowser(reqBrowser);
                    studentSpouseProfileEntity.setReqDeletedOS(reqOs);
                    studentSpouseProfileEntity.setReqDeletedDevice(reqDevice);
                    studentSpouseProfileEntity.setReqDeletedReferer(reqReferer);

                    return studentSpouseProfileRepository.save(studentSpouseProfileEntity)
                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
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
