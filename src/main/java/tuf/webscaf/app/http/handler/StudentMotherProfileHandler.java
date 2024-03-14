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
import tuf.webscaf.app.dbContext.master.entity.StudentMotherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherProfileRepository;
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

@Tag(name = "studentMotherProfileHandler")
@Component
public class StudentMotherProfileHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentMotherProfileRepository studentMotherProfileRepository;

    @Autowired
    SlaveStudentMotherProfileRepository slaveStudentMotherProfileRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    ApiCallService apiCallService;

    @Autowired
    StudentMotherJobHistoryRepository studentMotherJobHistoryRepository;

    @Autowired
    StudentMotherDocumentRepository studentMotherDocumentRepository;

    @Autowired
    StudentMotherAcademicHistoryRepository studentMotherAcademicHistoryRepository;

    @Autowired
    StudentMotherFamilyDoctorRepository studentMotherFamilyDoctorRepository;

    @Autowired
    StudentMotherHobbyPvtRepository studentMotherHobbyPvtRepository;

    @Autowired
    StudentMotherNationalityPvtRepository studentMotherNationalityPvtRepository;

    @Autowired
    StudentMotherAddressRepository studentMotherAddressRepository;

    @Autowired
    StudentMotherFinancialHistoryRepository studentMotherFinancialHistoryRepository;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    StudentMotherAilmentPvtRepository studentMotherAilmentPvtRepository;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_index")
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
            Flux<SlaveStudentMotherProfileEntity> slaveStudentMotherProfileFlux = slaveStudentMotherProfileRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable, searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));
            return slaveStudentMotherProfileFlux
                    .collectList()
                    .flatMap(studentMotherProfileEntity -> slaveStudentMotherProfileRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrNicContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (studentMotherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentMotherProfileEntity> slaveStudentMotherProfileFlux = slaveStudentMotherProfileRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord);
            return slaveStudentMotherProfileFlux
                    .collectList()
                    .flatMap(studentMotherProfileEntity -> slaveStudentMotherProfileRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullAndNicContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (studentMotherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherProfileEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentMotherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherProfileRepository.findByUuidAndDeletedAtIsNull(studentMotherProfileUUID)
                .flatMap(studentMotherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_student-mother_show")
    public Mono<ServerResponse> showByStudentMotherUUID(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString(serverRequest.pathVariable("studentMotherUUID"));

        return slaveStudentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherUUID)
                .flatMap(studentProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentProfileEntity))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    // Show Student Profiles against Student , Student Mother, and Student Mother Profile
    @AuthHasPermission(value = "academic_api_v1_student_student-mother_student-mother-profiles_show")
    public Mono<ServerResponse> showStudentMotherProfile(ServerRequest serverRequest) {
        UUID studentMotherProfileUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
        UUID studentMotherUUID = UUID.fromString(serverRequest.queryParam("studentMotherUUID").map(String::toString).orElse(""));
        UUID studentUUID = UUID.fromString(serverRequest.queryParam("studentUUID").map(String::toString).orElse(""));

        return slaveStudentMotherProfileRepository.showStudentMotherProfileAgainstStudentAndStudentMother(studentUUID, studentMotherUUID, studentMotherProfileUUID)
                .flatMap(studentMotherProfileEntity -> responseSuccessMsg("Record Fetched Successfully", studentMotherProfileEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
    }

    //This function is used by delete function of Country Handler in Config Module to Check If country Exists in Student Mother Profile
    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_country_show")
    public Mono<ServerResponse> getCountryUUID(ServerRequest serverRequest) {
        UUID countryUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherProfileRepository.findFirstByCountryUUIDAndDeletedAtIsNull(countryUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of State Handler in Config Module to Check If state Exists in Student Mother Profile
    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_state_show")
    public Mono<ServerResponse> getStateUUID(ServerRequest serverRequest) {
        UUID stateUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherProfileRepository.findFirstByStateUUIDAndDeletedAtIsNull(stateUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    //This function is used by delete function of City Handler in Config Module to Check If city Exists in Student Mother Profile
    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_city_show")
    public Mono<ServerResponse> getCityUUID(ServerRequest serverRequest) {
        UUID cityUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveStudentMotherProfileRepository.findFirstByCityUUIDAndDeletedAtIsNull(cityUUID)
                .flatMap(value1 -> responseInfoMsg("Unable to Delete Record as the Reference Exists."))
                .switchIfEmpty(responseErrorMsg("Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_store")
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

                    StudentMotherProfileEntity entity = StudentMotherProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentMotherUUID(UUID.fromString(value.getFirst("studentMotherUUID")))
                            .image(UUID.fromString(value.getFirst("image")))
                            .name(value.getFirst("name").trim())
                            .nic(value.getFirst("nic"))
                            .age(Integer.valueOf(value.getFirst("age")))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                            .officialTel(value.getFirst("officialTel").trim())
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


                    //  check mother nic number is unique
                    return studentMotherProfileRepository.findFirstByNicAndStudentMotherUUIDAndDeletedAtIsNull(entity.getNic(), entity.getStudentMotherUUID())
                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                            check mother profile is unique
                            .switchIfEmpty(Mono.defer(() -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(entity.getStudentMotherUUID())
                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Mother Profile already exist"))))
//                            check student mother uuid exists
                            .switchIfEmpty(Mono.defer(() -> studentMotherRepository.findByUuidAndDeletedAtIsNull(entity.getStudentMotherUUID())
                                    //checks if contactNo uuid exists
                                    .flatMap(studentProfileEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", entity.getImage())
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
                                                                                                            studentMotherProfileRepository.save(entity)
                                                                                                                    .flatMap(studentMotherProfileEntity -> responseSuccessMsg("Record Stored Successfully", studentMotherProfileEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again."))
                                                                                                                    .onErrorResume(err -> responseInfoMsg("Unable to store record. Please contact developer."))
                                                                                                    )).
                                                                                            switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                    ).
                                                                                    switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                            )).
                                                                    switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer.")))
                                                    )).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                    ).switchIfEmpty(responseInfoMsg("Mother Profile does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Mother Profile does not exist. Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentMotherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> studentMotherProfileRepository.findByUuidAndDeletedAtIsNull(studentMotherProfileUUID)
                        .flatMap(previousEntity -> {

                            StudentMotherProfileEntity updatedEntity = StudentMotherProfileEntity.builder()
                                    .uuid(previousEntity.getUuid())
                                    .studentMotherUUID(previousEntity.getStudentMotherUUID())
                                    .image(UUID.fromString(value.getFirst("image")))
                                    .name(value.getFirst("name").trim())
                                    .nic(value.getFirst("nic"))
                                    .age(Integer.valueOf(value.getFirst("age")))
                                    .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                    .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                    .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                    .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                                    .officialTel(value.getFirst("officialTel").trim())
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

                            // check nic number is unique
                            return studentMotherProfileRepository.findFirstByNicAndStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentMotherUUID(), studentMotherProfileUUID)
                                    .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                    //check mother profile is unique
                                    .switchIfEmpty(Mono.defer(() -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentMotherUUID(), studentMotherProfileUUID)
                                            .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Mother Profile already exist"))))
                                    //checks if mother uuid exists
                                    .switchIfEmpty(Mono.defer(() -> studentMotherRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getStudentMotherUUID())
                                            //checks if contactNo uuid exists
                                            .flatMap(studentProfileEntity1 -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(studentMotherDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks state uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> studentMotherProfileRepository.save(previousEntity)
                                                                                                                    .then(studentMotherProfileRepository.save(updatedEntity))
                                                                                                                    .flatMap(studentMotherProfileEntity -> responseSuccessMsg("Record Updated Successfully", studentMotherProfileEntity))
                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong Please try again."))
                                                                                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please Contact Developer."))
                                                                                                            ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                                    )).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                                    )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                                    )).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer.")))
                                            ).switchIfEmpty(responseInfoMsg("Mother Profile does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Mother Profile does not exist. Please contact developer."))
                                    ));
                        })
                        .switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID studentMotherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return studentMotherProfileRepository.findByUuidAndDeletedAtIsNull(studentMotherProfileUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                StudentMotherProfileEntity updatedEntity = StudentMotherProfileEntity
                                        .builder()
                                        .uuid(previousEntity.getUuid())
                                        .studentMotherUUID(previousEntity.getStudentMotherUUID())
                                        .image(previousEntity.getImage())
                                        .name(previousEntity.getName())
                                        .nic(previousEntity.getNic())
                                        .age(previousEntity.getAge())
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

                                return studentMotherProfileRepository.save(previousEntity)
                                        .then(studentMotherProfileRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-mother-profiles_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentMotherProfileUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return studentMotherProfileRepository.findByUuidAndDeletedAtIsNull(studentMotherProfileUUID)
                .flatMap(studentMotherProfileEntity -> {

                    studentMotherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                    studentMotherProfileEntity.setDeletedBy(UUID.fromString(userId));
                    studentMotherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                    studentMotherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                    studentMotherProfileEntity.setReqDeletedIP(reqIp);
                    studentMotherProfileEntity.setReqDeletedPort(reqPort);
                    studentMotherProfileEntity.setReqDeletedBrowser(reqBrowser);
                    studentMotherProfileEntity.setReqDeletedOS(reqOs);
                    studentMotherProfileEntity.setReqDeletedDevice(reqDevice);
                    studentMotherProfileEntity.setReqDeletedReferer(reqReferer);

                    return studentMotherProfileRepository.save(studentMotherProfileEntity)
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
