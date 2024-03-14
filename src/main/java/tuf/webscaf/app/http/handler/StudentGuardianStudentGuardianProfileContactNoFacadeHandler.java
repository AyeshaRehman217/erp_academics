package tuf.webscaf.app.http.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import tuf.webscaf.app.dbContext.master.dto.StudentContactNoDto;
import tuf.webscaf.app.dbContext.master.dto.StudentGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentGuardianStudentGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentGuardianProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentGuardianProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentGuardianRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "studentGuardianStudentGuardianProfileContactNoFacade")
@Component
public class StudentGuardianStudentGuardianProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentGuardianRepository studentGuardianRepository;

    @Autowired
    SlaveStudentGuardianRepository slaveStudentGuardianRepository;

    @Autowired
    SlaveStudentGuardianProfileRepository slaveStudentGuardianProfileRepository;

    @Autowired
    StudentGuardianProfileRepository studentGuardianProfileRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    GuardianTypeRepository guardianTypeRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    SlaveStudentContactNoRepository slaveStudentContactNoRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_student-guardian-student-guardian-profile-contact-nos_index")
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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("created_at");
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> slaveStudentGuardianStudentGuardianProfileContactNoFacadeDtoFlux = slaveStudentGuardianRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentGuardianStudentGuardianProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentGuardianProfileEntity -> slaveStudentGuardianRepository
                            .countStudentGuardianStudentGuardianProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentGuardianProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> slaveStudentGuardianStudentGuardianProfileContactNoFacadeDtoFlux = slaveStudentGuardianRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentGuardianStudentGuardianProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentGuardianProfileEntity -> slaveStudentGuardianRepository
                            .countStudentGuardianStudentGuardianProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentGuardianProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentGuardianProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-guardian-student-guardian-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString((serverRequest.pathVariable("studentGuardianUUID")));

        return slaveStudentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                .flatMap(studentGuardianEntity -> slaveStudentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                        .flatMap(studentGuardianProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNo -> {
                                    List<SlaveStudentContactNoFacadeDto> studentContactNoDto = new ArrayList<>();

                                    for (SlaveStudentContactNoEntity studentContact : studentContactNo) {
                                        SlaveStudentContactNoFacadeDto studentGuardianContactNoDto = SlaveStudentContactNoFacadeDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentGuardianContactNoDto);
                                    }

                                    return showFacadeDto(studentGuardianEntity, studentGuardianProfileEntity, studentContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Guardian Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Guardian Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Guardian Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Guardian Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto> showFacadeDto(SlaveStudentGuardianEntity slaveStudentGuardianEntity, SlaveStudentGuardianProfileEntity slaveStudentGuardianProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoFacadeDto) {

        SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto facadeDto = SlaveStudentGuardianStudentGuardianProfileContactNoFacadeDto.builder()
                .id(slaveStudentGuardianEntity.getId())
                .uuid(slaveStudentGuardianEntity.getUuid())
                .version(slaveStudentGuardianEntity.getVersion())
                .status(slaveStudentGuardianEntity.getStatus())
                .studentUUID(slaveStudentGuardianEntity.getStudentUUID())
                .guardianUUID(slaveStudentGuardianEntity.getGuardianUUID())
                .guardianTypeUUID(slaveStudentGuardianEntity.getGuardianTypeUUID())
                .studentGuardianUUID(slaveStudentGuardianEntity.getUuid())
                .description(slaveStudentGuardianProfileEntity.getDescription())
                .relation(slaveStudentGuardianProfileEntity.getRelation())
                .genderUUID(slaveStudentGuardianProfileEntity.getGenderUUID())
                .image(slaveStudentGuardianProfileEntity.getImage())
                .name(slaveStudentGuardianProfileEntity.getName())
                .nic(slaveStudentGuardianProfileEntity.getNic())
                .age(slaveStudentGuardianProfileEntity.getAge())
                .officialTel(slaveStudentGuardianProfileEntity.getOfficialTel())
                .cityUUID(slaveStudentGuardianProfileEntity.getCityUUID())
                .stateUUID(slaveStudentGuardianProfileEntity.getStateUUID())
                .countryUUID(slaveStudentGuardianProfileEntity.getCountryUUID())
                .noOfDependents(slaveStudentGuardianProfileEntity.getNoOfDependents())
                .email(slaveStudentGuardianProfileEntity.getEmail())
                .studentGuardianContactNoDto(slaveStudentContactNoFacadeDto)
                .createdAt(slaveStudentGuardianEntity.getCreatedAt())
                .createdBy(slaveStudentGuardianEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentGuardianEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentGuardianEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentGuardianEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentGuardianEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentGuardianEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentGuardianEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentGuardianEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentGuardianEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentGuardianEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentGuardianEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentGuardianEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentGuardianEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentGuardianEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentGuardianEntity.getReqUpdatedReferer())
                .editable(slaveStudentGuardianEntity.getEditable())
                .deletable(slaveStudentGuardianEntity.getDeletable())
                .archived(slaveStudentGuardianEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentGuardianStudentGuardianProfileContactNoFacadeDto> facadeDto(StudentGuardianEntity studentGuardianEntity, StudentGuardianProfileEntity studentGuardianProfileEntity, List<StudentContactNoDto> studentContactNoDto) {

        StudentGuardianStudentGuardianProfileContactNoFacadeDto facadeDto = StudentGuardianStudentGuardianProfileContactNoFacadeDto.builder()
                .id(studentGuardianEntity.getId())
                .uuid(studentGuardianEntity.getUuid())
                .version(studentGuardianEntity.getVersion())
                .status(studentGuardianEntity.getStatus())
                .studentUUID(studentGuardianEntity.getStudentUUID())
                .guardianUUID(studentGuardianEntity.getGuardianUUID())
                .guardianTypeUUID(studentGuardianEntity.getGuardianTypeUUID())
                .studentGuardianUUID(studentGuardianEntity.getUuid())
                .image(studentGuardianProfileEntity.getImage())
                .name(studentGuardianProfileEntity.getName())
                .nic(studentGuardianProfileEntity.getNic())
                .age(studentGuardianProfileEntity.getAge())
                .description(studentGuardianProfileEntity.getDescription())
                .relation(studentGuardianProfileEntity.getRelation())
                .genderUUID(studentGuardianProfileEntity.getGenderUUID())
                .officialTel(studentGuardianProfileEntity.getOfficialTel())
                .cityUUID(studentGuardianProfileEntity.getCityUUID())
                .stateUUID(studentGuardianProfileEntity.getStateUUID())
                .countryUUID(studentGuardianProfileEntity.getCountryUUID())
                .noOfDependents(studentGuardianProfileEntity.getNoOfDependents())
                .email(studentGuardianProfileEntity.getEmail())
                .studentGuardianContactNoDto(studentContactNoDto)
                .createdAt(studentGuardianEntity.getCreatedAt())
                .createdBy(studentGuardianEntity.getCreatedBy())
                .reqCompanyUUID(studentGuardianEntity.getReqCompanyUUID())
                .reqBranchUUID(studentGuardianEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentGuardianEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentGuardianEntity.getReqCreatedIP())
                .reqCreatedPort(studentGuardianEntity.getReqCreatedPort())
                .reqCreatedOS(studentGuardianEntity.getReqCreatedOS())
                .reqCreatedDevice(studentGuardianEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentGuardianEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentGuardianEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentGuardianEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentGuardianEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentGuardianEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentGuardianEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentGuardianEntity.getReqUpdatedReferer())
                .editable(studentGuardianEntity.getEditable())
                .deletable(studentGuardianEntity.getDeletable())
                .archived(studentGuardianEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentGuardianProfileContactNoFacadeDto> updatedFacadeDto(StudentGuardianEntity studentGuardianEntity, StudentGuardianProfileEntity studentGuardianProfileEntity, List<StudentContactNoDto> studentContactNoDto) {

        StudentGuardianProfileContactNoFacadeDto facadeDto = StudentGuardianProfileContactNoFacadeDto.builder()
                .id(studentGuardianEntity.getId())
                .uuid(studentGuardianEntity.getUuid())
                .version(studentGuardianEntity.getVersion())
                .status(studentGuardianEntity.getStatus())
                .image(studentGuardianProfileEntity.getImage())
                .name(studentGuardianProfileEntity.getName())
                .nic(studentGuardianProfileEntity.getNic())
                .age(studentGuardianProfileEntity.getAge())
                .officialTel(studentGuardianProfileEntity.getOfficialTel())
                .cityUUID(studentGuardianProfileEntity.getCityUUID())
                .stateUUID(studentGuardianProfileEntity.getStateUUID())
                .countryUUID(studentGuardianProfileEntity.getCountryUUID())
                .noOfDependents(studentGuardianProfileEntity.getNoOfDependents())
                .email(studentGuardianProfileEntity.getEmail())
                .description(studentGuardianProfileEntity.getDescription())
                .relation(studentGuardianProfileEntity.getRelation())
                .genderUUID(studentGuardianProfileEntity.getGenderUUID())
                .studentGuardianContactNoDto(studentContactNoDto)
                .updatedAt(studentGuardianEntity.getUpdatedAt())
                .updatedBy(studentGuardianEntity.getUpdatedBy())
                .reqCompanyUUID(studentGuardianProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(studentGuardianProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentGuardianProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentGuardianProfileEntity.getReqCreatedIP())
                .reqCreatedPort(studentGuardianProfileEntity.getReqCreatedPort())
                .reqCreatedOS(studentGuardianProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(studentGuardianProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentGuardianProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentGuardianProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentGuardianProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentGuardianProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentGuardianProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentGuardianProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentGuardianProfileEntity.getReqUpdatedReferer())
                .editable(studentGuardianProfileEntity.getEditable())
                .deletable(studentGuardianProfileEntity.getDeletable())
                .archived(studentGuardianProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-guardian-student-guardian-profile-contact-nos_store")
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

                    UUID guardianUUID = null;
                    if ((value.containsKey("guardianUUID") && (value.getFirst("guardianUUID") != ""))) {
                        guardianUUID = UUID.fromString(value.getFirst("guardianUUID").trim());
                    }

                    StudentGuardianEntity studentGuardianEntity = StudentGuardianEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .guardianTypeUUID(UUID.fromString(value.getFirst("guardianTypeUUID").trim()))
                            .guardianUUID(guardianUUID)
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

                    //check if Student Record exists or not
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentGuardianEntity.getStudentUUID())
                            //check if Student Guardian Record Already Exists Against the same student
                            .flatMap(studentEntity -> studentGuardianRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                            .flatMap(checkMsg -> responseInfoMsg("Student Guardian Record Against the Entered Student Already Exist."))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                //Building Student Guardian Profile Record
                                                StudentGuardianProfileEntity studentGuardianProfileEntity = StudentGuardianProfileEntity
                                                        .builder()
                                                        .uuid(UUID.randomUUID())
                                                        .studentGuardianUUID(studentGuardianEntity.getUuid())
                                                        .image(UUID.fromString(value.getFirst("image")))
                                                        .name(value.getFirst("name").trim())
                                                        .nic(value.getFirst("nic").trim())
                                                        .relation(value.getFirst("relation").trim())
                                                        .email(value.getFirst("email").trim())
                                                        .age(Integer.valueOf(value.getFirst("age")))
                                                        .description(value.getFirst("description"))
                                                        .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
                                                        .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                                        .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                                        .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                                        .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
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

                                                sendFormData.add("docId", String.valueOf(studentGuardianProfileEntity.getImage()));

                                                //check if City Record Exists or not
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentGuardianProfileEntity.getCityUUID())
                                                        .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                //check if State Record Exists or not
                                                                .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentGuardianProfileEntity.getStateUUID())
                                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                        //check if Country Record Exists or not
                                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentGuardianProfileEntity.getCountryUUID())
                                                                                                        .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                //check if Gender Record Exists or not
                                                                                                                .flatMap(countryUUID -> genderRepository.findByUuidAndDeletedAtIsNull(studentGuardianProfileEntity.getGenderUUID())
                                                                                                                                //check if NIC Is Unique Against Student Guardian
                                                                                                                                .flatMap(checkNIC -> studentGuardianProfileRepository.findFirstByNicAndStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getNic(), studentGuardianProfileEntity.getStudentGuardianUUID())
                                                                                                                                        .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                                                                //check if Guardian Profile Already Exists Against Student Guardian
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianProfileEntity.getStudentGuardianUUID())
                                                                                                                                        .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Guardian Profile already exist"))))
                                                                                                                                //check if Document Record Exists or not
                                                                                                                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentGuardianProfileEntity.getImage())
                                                                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                                //check if Contact Category is Guardian
                                                                                                                                                                .flatMap(documentEntity -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("guardian")
                                                                                                                                                                                .flatMap(contactCategoryEntity -> {

                                                                                                                                                                                    //getting List of Contact No. From Front
                                                                                                                                                                                    List<String> studentGuardianContactList = value.get("studentGuardianContactNoDto");
                                                                                                                                                                                    //Creating an empty list to add student Contact No Records
                                                                                                                                                                                    List<StudentContactNoEntity> studentGuardianContactNoList = new ArrayList<>();

                                                                                                                                                                                    // Creating an empty list to add contact Type UUID's
                                                                                                                                                                                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                                    // Creating an empty list to add contact No's
                                                                                                                                                                                    List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                                    JsonNode contactNode = null;
                                                                                                                                                                                    ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                                    try {
                                                                                                                                                                                        contactNode = objectMapper.readTree(studentGuardianContactList.toString());
                                                                                                                                                                                    } catch (JsonProcessingException e) {
                                                                                                                                                                                        e.printStackTrace();
                                                                                                                                                                                    }
                                                                                                                                                                                    assert contactNode != null;


                                                                                                                                                                                    UUID studentMetaUUID = null;
                                                                                                                                                                                    UUID contactCategoryUUID = null;

                                                                                                                                                                                    //iterating over the json node from front and setting contact No's
                                                                                                                                                                                    for (JsonNode guardianContact : contactNode) {

                                                                                                                                                                                        StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                                                .builder()
                                                                                                                                                                                                .contactTypeUUID(UUID.fromString(guardianContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                                .contactNo(guardianContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                                .studentMetaUUID(studentGuardianEntity.getUuid())
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

                                                                                                                                                                                        studentGuardianContactNoList.add(studentContactNoEntity);

                                                                                                                                                                                        contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                                                                                                                                                                                        contactNoList.add(studentContactNoEntity.getContactNo());
                                                                                                                                                                                        studentMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                                                        contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                                    }

                                                                                                                                                                                    //Getting Distinct Values Fom the List of Student Guardian Contact No List
                                                                                                                                                                                    studentGuardianContactNoList = studentGuardianContactNoList.stream()
                                                                                                                                                                                            .distinct()
                                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                                    //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                                    contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                                            .distinct()
                                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                                    // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                                    List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                                                                                    if (!studentGuardianContactNoList.isEmpty()) {

                                                                                                                                                                                        UUID finalStudentMetaUUID = studentMetaUUID;

                                                                                                                                                                                        UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                                        List<StudentContactNoEntity> finalStudentGuardianContactNoList = studentGuardianContactNoList;

                                                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                                .collectList()
                                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                                        } else {
                                                                                                                                                                                                            //check if Contact No Record Already Exists against Student Guardian and Contact Type
                                                                                                                                                                                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStudentMetaUUID)
                                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(studentGuardianEntity.getGuardianTypeUUID())
                                                                                                                                                                                                                            .flatMap(guardianTypeEntity -> {

                                                                                                                                                                                                                                // if guardian uuid is specified in the request
                                                                                                                                                                                                                                if (studentGuardianEntity.getGuardianUUID() != null) {

                                                                                                                                                                                                                                    // if student father is guardian
                                                                                                                                                                                                                                    switch (guardianTypeEntity.getSlug()) {
                                                                                                                                                                                                                                        case "father":
                                                                                                                                                                                                                                            return studentFatherRepository.findByUuidAndStudentUUIDAndDeletedAtIsNull(studentGuardianEntity.getGuardianUUID(), studentGuardianEntity.getStudentUUID())
                                                                                                                                                                                                                                                    .flatMap(studentFatherEntity -> studentGuardianRepository.save(studentGuardianEntity)
                                                                                                                                                                                                                                                            .flatMap(studentGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", studentGuardianEntityDB))
                                                                                                                                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                                                                                                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                                                                                                                                                                                                    .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));

                                                                                                                                                                                                                                        // if student mother is guardian
                                                                                                                                                                                                                                        case "mother":
                                                                                                                                                                                                                                            return studentMotherRepository.findByUuidAndStudentUUIDAndDeletedAtIsNull(studentGuardianEntity.getGuardianUUID(), studentGuardianEntity.getStudentUUID())
                                                                                                                                                                                                                                                    .flatMap(studentMotherEntity -> studentGuardianRepository.save(studentGuardianEntity)
                                                                                                                                                                                                                                                            .flatMap(studentGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", studentGuardianEntityDB))
                                                                                                                                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                                                                                                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                                                                                                                                                                                                    .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));

                                                                                                                                                                                                                                        // if guardian type is other but guardian uuid is given
                                                                                                                                                                                                                                        case "other":
                                                                                                                                                                                                                                            return responseInfoMsg("Guardian is not valid for given Guardian Type");
                                                                                                                                                                                                                                        default:
                                                                                                                                                                                                                                            return responseInfoMsg("Guardian Type is not valid. Unable to store record.");
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                // if guardian uuid is not in the request
                                                                                                                                                                                                                                else {
                                                                                                                                                                                                                                    if (guardianTypeEntity.getSlug().equals("father")) {
                                                                                                                                                                                                                                        return responseInfoMsg("Enter the Father Record First.");
                                                                                                                                                                                                                                    } else if (guardianTypeEntity.getSlug().equals("mother")) {
                                                                                                                                                                                                                                        return responseInfoMsg("Enter the Mother Record First.");
                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                        return studentGuardianRepository.save(studentGuardianEntity)
                                                                                                                                                                                                                                                .then(studentGuardianProfileRepository.save(studentGuardianProfileEntity))
                                                                                                                                                                                                                                                .then(studentContactNoRepository.saveAll(finalStudentGuardianContactNoList)
                                                                                                                                                                                                                                                        .collectList())
                                                                                                                                                                                                                                                .flatMap(studentContactNoEntities -> {
                                                                                                                                                                                                                                                    for (StudentContactNoEntity studentContact : studentContactNoEntities) {
                                                                                                                                                                                                                                                        StudentContactNoDto studentGuardianContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                                                                .contactNo(studentContact.getContactNo())
                                                                                                                                                                                                                                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                                                                                                                                                                                                                .build();

                                                                                                                                                                                                                                                        studentContactNoDto.add(studentGuardianContactNoDto);
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                    return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                                            .flatMap(docUpdate -> facadeDto(studentGuardianEntity, studentGuardianProfileEntity, studentContactNoDto)
                                                                                                                                                                                                                                                                    .flatMap(studentGuardianFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentGuardianFacadeDto))
                                                                                                                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                                                                                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."));
                                                                                                                                                                                                                                                }).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                            }).switchIfEmpty(responseInfoMsg("Guardian Type does not exist"))
                                                                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Guardian Type does not exist. Please contact developer."))
                                                                                                                                                                                                                    ));
                                                                                                                                                                                                        }
                                                                                                                                                                                                    }
                                                                                                                                                                                                    // when list is empty
                                                                                                                                                                                                    else {
                                                                                                                                                                                                        return responseInfoMsg("Contact Type Does not exist");
                                                                                                                                                                                                    }

                                                                                                                                                                                                }).switchIfEmpty(responseInfoMsg("Contact Type Does not Exist"))
                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Contact Type does not exist.Please Contact Developer."));
                                                                                                                                                                                    } else {
                                                                                                                                                                                        //if Contact No List is empty then store student Guardian and Student Guardian Profile
                                                                                                                                                                                        return studentGuardianRepository.save(studentGuardianEntity)
                                                                                                                                                                                                //Save Student Guardian Profile Entity
                                                                                                                                                                                                .then(studentGuardianProfileRepository.save(studentGuardianProfileEntity))
                                                                                                                                                                                                //update Document Status After Storing record
                                                                                                                                                                                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(studentGuardianEntity, studentGuardianProfileEntity, studentContactNoDto)
                                                                                                                                                                                                                .flatMap(studentGuardianFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentGuardianFacadeDto))
                                                                                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                                                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                                                                                                                    }
//
                                                                                                                                                                                })
                                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Upload Image."))
                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Upload Image.Please Contact Developer."))
                                                                                                                                                ))
                                                                                                                                ).switchIfEmpty(responseInfoMsg("Gender Record Does not exist."))
                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist.Please Contact Developer."))
                                                                                                                )).switchIfEmpty(responseInfoMsg("Country Record Does not exist."))
                                                                                                        .onErrorResume(ex -> responseErrorMsg("Country Record Does not Exist.Please Contact Developer."))
                                                                                        )).switchIfEmpty(responseInfoMsg("State Record Does not Exist."))
                                                                                .onErrorResume(ex -> responseErrorMsg("State Record Does not Exist.Please Contact Developer."))
                                                                )).switchIfEmpty(responseInfoMsg("City Record Does not Exist."))
                                                        .onErrorResume(ex -> responseErrorMsg("City Record Does not Exist.Please Contact Developer."));
                                            }))
                            ).switchIfEmpty(responseInfoMsg("Student Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Student Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-guardian-student-guardian-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString((serverRequest.pathVariable("studentGuardianUUID")));
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
                .flatMap(value -> studentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                        .flatMap(previousStudentGuardianEntity -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentGuardianProfileEntity updatedEntity = StudentGuardianProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentGuardianUUID(previousProfileEntity.getStudentGuardianUUID())
                                            .image(UUID.fromString(value.getFirst("image")))
                                            .name(value.getFirst("name").trim())
                                            .nic(value.getFirst("nic").trim())
                                            .age(Integer.valueOf(value.getFirst("age")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
                                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
                                            .officialTel(value.getFirst("officialTel").trim())
                                            .relation(value.getFirst("relation").trim())
                                            .description(value.getFirst("description"))
                                            .email(value.getFirst("email").trim())
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .createdAt(previousProfileEntity.getCreatedAt())
                                            .createdBy(previousProfileEntity.getCreatedBy())
                                            .updatedBy(UUID.fromString(userId))
                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                            .reqUpdatedIP(reqIp)
                                            .reqUpdatedPort(reqPort)
                                            .reqUpdatedBrowser(reqBrowser)
                                            .reqUpdatedOS(reqOs)
                                            .reqUpdatedDevice(reqDevice)
                                            .reqUpdatedReferer(reqReferer)
                                            .build();

                                    previousProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    previousProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    previousProfileEntity.setReqDeletedIP(reqIp);
                                    previousProfileEntity.setReqDeletedPort(reqPort);
                                    previousProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    previousProfileEntity.setReqDeletedOS(reqOs);
                                    previousProfileEntity.setReqDeletedDevice(reqDevice);
                                    previousProfileEntity.setReqDeletedReferer(reqReferer);

                                    sendFormData.add("docId", String.valueOf(updatedEntity.getImage()));

                                    // check nic number is unique
                                    return studentGuardianProfileRepository.findFirstByNicAndStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentGuardianUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check guardian profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentGuardianUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Guardian Profile already exist"))))
                                            //checks if guardian uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(studentGuardianDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks countries uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                                                                                                    .flatMap(genderEntity -> {

                                                                                                                                //getting List of Contact No. From Front
                                                                                                                                List<String> studentGuardianContactList = value.get("studentGuardianContactNoDto");
                                                                                                                                List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                                studentGuardianContactList.removeIf(s -> s.equals(""));

                                                                                                                                if (!studentGuardianContactList.isEmpty()) {
                                                                                                                                    return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("guardian")
                                                                                                                                            .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentGuardianUUID)
                                                                                                                                                    .collectList()
                                                                                                                                                    .flatMap(existingContactList -> {

                                                                                                                                                        //Removing Already existing student Guardian Contact No Entity
                                                                                                                                                        for (StudentContactNoEntity studentContact : existingContactList) {
                                                                                                                                                            studentContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                            studentContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                            studentContact.setReqDeletedIP(reqIp);
                                                                                                                                                            studentContact.setReqDeletedPort(reqPort);
                                                                                                                                                            studentContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                            studentContact.setReqDeletedOS(reqOs);
                                                                                                                                                            studentContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                            studentContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                        }

                                                                                                                                                        //Creating an Object Node to Read Values from Front
                                                                                                                                                        JsonNode contactNode = null;
                                                                                                                                                        try {
                                                                                                                                                            contactNode = new ObjectMapper().readTree(studentGuardianContactList.toString());
                                                                                                                                                        } catch (JsonProcessingException e) {
                                                                                                                                                            e.printStackTrace();
                                                                                                                                                        }

                                                                                                                                                        //New Contact No list for adding values after building entity
                                                                                                                                                        List<StudentContactNoEntity> studentGuardianContactNoList = new ArrayList<>();

                                                                                                                                                        List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                        List<String> contactNoList = new ArrayList<>();

                                                                                                                                                        UUID updatedStudentMetaUUID = null;

                                                                                                                                                        for (JsonNode guardianContact : contactNode) {

                                                                                                                                                            StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                    .builder()
                                                                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                                                                    .contactTypeUUID(UUID.fromString(guardianContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                    .contactNo(guardianContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                    .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                    .studentMetaUUID(studentGuardianUUID)
                                                                                                                                                                    .createdAt(previousProfileEntity.getCreatedAt())
                                                                                                                                                                    .createdBy(previousProfileEntity.getCreatedBy())
                                                                                                                                                                    .updatedBy(UUID.fromString(userId))
                                                                                                                                                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                                                                                                                    .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
                                                                                                                                                                    .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
                                                                                                                                                                    .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
                                                                                                                                                                    .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
                                                                                                                                                                    .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
                                                                                                                                                                    .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
                                                                                                                                                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                                                                                                                                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                                                                                                                                                    .reqUpdatedIP(reqIp)
                                                                                                                                                                    .reqUpdatedPort(reqPort)
                                                                                                                                                                    .reqUpdatedBrowser(reqBrowser)
                                                                                                                                                                    .reqUpdatedOS(reqOs)
                                                                                                                                                                    .reqUpdatedDevice(reqDevice)
                                                                                                                                                                    .reqUpdatedReferer(reqReferer)
                                                                                                                                                                    .build();

                                                                                                                                                            studentGuardianContactNoList.add(studentContactNoEntity);

                                                                                                                                                            contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                            contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                            updatedStudentMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                        }

                                                                                                                                                        //Getting Distinct Values Fom the List of Student Guardian Contact No List
                                                                                                                                                        studentGuardianContactNoList = studentGuardianContactNoList.stream()
                                                                                                                                                                .distinct()
                                                                                                                                                                .collect(Collectors.toList());

                                                                                                                                                        //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                        contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                .distinct()
                                                                                                                                                                .collect(Collectors.toList());

                                                                                                                                                        //Getting Distinct Values Fom the List of Contact No
                                                                                                                                                        contactNoList = contactNoList.stream()
                                                                                                                                                                .distinct()
                                                                                                                                                                .collect(Collectors.toList());


                                                                                                                                                        UUID finalStudentMetaUUID = updatedStudentMetaUUID;

                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                        List<StudentContactNoEntity> finalStudentGuardianContactNoList1 = studentGuardianContactNoList;

                                                                                                                                                        List<String> finalContactNoList = contactNoList;

                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                .collectList()
                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                        } else {

                                                                                                                                                                            //check if Contact No Record Already Exists against Student Guardian and Contact Type
                                                                                                                                                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStudentMetaUUID)
                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> studentGuardianProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                            .then(studentGuardianProfileRepository.save(updatedEntity))
                                                                                                                                                                                            .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                            .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentGuardianContactNoList1)
                                                                                                                                                                                                    .collectList()
                                                                                                                                                                                                    .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                        for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
                                                                                                                                                                                                            StudentContactNoDto studentGuardianContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                    .contactNo(studentContact.getContactNo())
                                                                                                                                                                                                                    .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                            studentContactNoDto.add(studentGuardianContactNoDto);
                                                                                                                                                                                                        }

                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                .flatMap(docUpdate -> updatedFacadeDto(previousStudentGuardianEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                                                                        .flatMap(studentGuardianFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentGuardianFacadeDto))
                                                                                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."));
                                                                                                                                                                                                    }))
                                                                                                                                                                                    ));
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                    // when list is empty
                                                                                                                                                                    else {
                                                                                                                                                                        return responseInfoMsg("Contact Type Does not exist.");
                                                                                                                                                                    }
                                                                                                                                                                }).switchIfEmpty(responseInfoMsg("Contact Type Does not Exist."))
                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Contact Type does not exist.Please Contact Developer."));
                                                                                                                                                    })).switchIfEmpty(responseInfoMsg("Contact Category Does not exist."))
                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Contact Category Does not exist.PLease Contact Developer."));
                                                                                                                                } else {
                                                                                                                                    //If Contact No is not empty then store profile only
                                                                                                                                    return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentGuardianUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(previousStudentContactList -> {

                                                                                                                                                for (StudentContactNoEntity studentContact : previousStudentContactList) {
                                                                                                                                                    studentContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                    studentContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                    studentContact.setReqDeletedIP(reqIp);
                                                                                                                                                    studentContact.setReqDeletedPort(reqPort);
                                                                                                                                                    studentContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                    studentContact.setReqDeletedOS(reqOs);
                                                                                                                                                    studentContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                    studentContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                }

                                                                                                                                                return studentContactNoRepository.saveAll(previousStudentContactList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(studentContactList -> studentGuardianProfileRepository.save(previousProfileEntity)
                                                                                                                                                                .then(studentGuardianProfileRepository.save(updatedEntity))
                                                                                                                                                                .flatMap(studentGuardianProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                        .flatMap(docUpdateEntity -> updatedFacadeDto(previousStudentGuardianEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                                .flatMap(studentGuardianFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentGuardianFacadeDto))
                                                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Unable to update Document. There is something wrong Please try again."))
                                                                                                                                                                        .onErrorResume(err -> responseErrorMsg("Unable to Update Document. Please Contact Developer."))
                                                                                                                                                                )
                                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Unable to Update Contact No Records. There is something wrong please try again"))
                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Update Contact No Records.Please Contact Developer."));
                                                                                                                                            });
                                                                                                                                }
                                                                                                                            }
                                                                                                                    ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer."))
                                                                                                            )).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                            )).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                            )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                            )).switchIfEmpty(responseInfoMsg("Unable to upload the image"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload the image. Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Guardian Profile Against the entered Student Guardian Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Guardian Profile Against the entered Student Guardian Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Guardian Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Guardian Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_student-guardian-student-guardian-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentGuardianUUID = UUID.fromString((serverRequest.pathVariable("studentGuardianUUID")));
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

        return studentGuardianRepository.findByUuidAndDeletedAtIsNull(studentGuardianUUID)
                .flatMap(studentGuardianEntity -> studentGuardianProfileRepository.findFirstByStudentGuardianUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                        .flatMap(studentGuardianProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentGuardianEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentGuardianEntity.setDeletedBy(UUID.fromString(userId));
                                    studentGuardianEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentGuardianEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentGuardianEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentGuardianEntity.setReqDeletedIP(reqIp);
                                    studentGuardianEntity.setReqDeletedPort(reqPort);
                                    studentGuardianEntity.setReqDeletedBrowser(reqBrowser);
                                    studentGuardianEntity.setReqDeletedOS(reqOs);
                                    studentGuardianEntity.setReqDeletedDevice(reqDevice);
                                    studentGuardianEntity.setReqDeletedReferer(reqReferer);

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

                                    for (StudentContactNoEntity studentContact : studentContactNoEntity) {

                                        studentContact.setDeletedBy(UUID.fromString(userId));
                                        studentContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                        studentContact.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                        studentContact.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                        studentContact.setReqDeletedIP(reqIp);
                                        studentContact.setReqDeletedPort(reqPort);
                                        studentContact.setReqDeletedBrowser(reqBrowser);
                                        studentContact.setReqDeletedOS(reqOs);
                                        studentContact.setReqDeletedDevice(reqDevice);
                                        studentContact.setReqDeletedReferer(reqReferer);

                                        studentContactNoEntityList.add(studentContact);

                                    }

                                    List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                    for (StudentContactNoEntity studentContact : studentContactNoEntity) {
                                        StudentContactNoDto studentGuardianContactNoDto = StudentContactNoDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentGuardianContactNoDto);
                                    }

                                    return studentGuardianRepository.save(studentGuardianEntity)
                                            .then(studentGuardianProfileRepository.save(studentGuardianProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentGuardianEntity, studentGuardianProfileEntity, studentContactNoDto)
                                                    .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto)))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
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
