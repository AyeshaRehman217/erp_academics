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
import tuf.webscaf.app.dbContext.master.dto.StudentChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentChildStudentChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentContactNoDto;
import tuf.webscaf.app.dbContext.master.entity.StudentChildEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentChildProfileEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentChildStudentChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentChildProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentChildProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentChildRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentRepository;
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

@Tag(name = "studentChildStudentChildProfileContactNoFacade")
@Component
public class StudentChildStudentChildProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentChildRepository studentChildRepository;

    @Autowired
    SlaveStudentChildProfileRepository slaveStudentChildProfileRepository;

    @Autowired
    StudentChildProfileRepository studentChildProfileRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    SlaveStudentContactNoRepository slaveStudentContactNoRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    SlaveStudentChildRepository slaveStudentChildRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_student-child-student-child-profile-contact-nos_index")
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
            Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> slaveStudentChildStudentChildProfileContactNoFacadeDtoFlux = slaveStudentChildRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentChildStudentChildProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentChildProfileEntity -> slaveStudentChildRepository
                            .countStudentChildStudentChildProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentChildProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentChildStudentChildProfileContactNoFacadeDto> slaveStudentChildStudentChildProfileContactNoFacadeDtoFlux = slaveStudentChildRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentChildStudentChildProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentChildProfileEntity -> slaveStudentChildRepository
                            .countStudentChildStudentChildProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentChildProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentChildProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-child-student-child-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("studentChildUUID")));

        return slaveStudentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                .flatMap(slaveStudentChildEntity -> slaveStudentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(slaveStudentChildEntity.getUuid())
                        .flatMap(slaveStudentChildProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(slaveStudentChildEntity.getUuid())
                                .collectList()
                                .flatMap(slaveStudentContactNoEntities -> {
                                    List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoDto = new ArrayList<>();

                                    for (SlaveStudentContactNoEntity studentContact : slaveStudentContactNoEntities) {
                                        SlaveStudentContactNoFacadeDto studentChildContactNoDto = SlaveStudentContactNoFacadeDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        slaveStudentContactNoDto.add(studentChildContactNoDto);
                                    }

                                    return showFacadeDto(slaveStudentChildEntity, slaveStudentChildProfileEntity, slaveStudentContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Child Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Child Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Child Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Child Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentChildStudentChildProfileContactNoFacadeDto> showFacadeDto(SlaveStudentChildEntity slaveStudentChildEntity, SlaveStudentChildProfileEntity slaveStudentChildProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoDto) {

        SlaveStudentChildStudentChildProfileContactNoFacadeDto facadeDto = SlaveStudentChildStudentChildProfileContactNoFacadeDto.builder()
                .id(slaveStudentChildEntity.getId())
                .uuid(slaveStudentChildEntity.getUuid())
                .version(slaveStudentChildEntity.getVersion())
                .status(slaveStudentChildEntity.getStatus())
                .studentUUID(slaveStudentChildEntity.getStudentUUID())
                .studentChildAsStudentUUID(slaveStudentChildEntity.getStudentUUID())
                .studentChildUUID(slaveStudentChildEntity.getUuid())
                .image(slaveStudentChildProfileEntity.getImage())
                .name(slaveStudentChildProfileEntity.getName())
                .nic(slaveStudentChildProfileEntity.getNic())
                .age(slaveStudentChildProfileEntity.getAge())
                .officialTel(slaveStudentChildProfileEntity.getOfficialTel())
                .cityUUID(slaveStudentChildProfileEntity.getCityUUID())
                .stateUUID(slaveStudentChildProfileEntity.getStateUUID())
                .countryUUID(slaveStudentChildProfileEntity.getCountryUUID())
                .genderUUID(slaveStudentChildProfileEntity.getGenderUUID())
                .email(slaveStudentChildProfileEntity.getEmail())
                .studentChildContactNoDto(slaveStudentContactNoDto)
                .createdAt(slaveStudentChildEntity.getCreatedAt())
                .createdBy(slaveStudentChildEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentChildEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentChildEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentChildEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentChildEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentChildEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentChildEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentChildEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentChildEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentChildEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentChildEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentChildEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentChildEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentChildEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentChildEntity.getReqUpdatedReferer())
                .editable(slaveStudentChildEntity.getEditable())
                .deletable(slaveStudentChildEntity.getDeletable())
                .archived(slaveStudentChildEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentChildStudentChildProfileContactNoFacadeDto> facadeDto(StudentChildEntity studentChildEntity, StudentChildProfileEntity studentChildProfileEntity, List<StudentContactNoDto> studentChildContactNoDto) {

        StudentChildStudentChildProfileContactNoFacadeDto facadeDto = StudentChildStudentChildProfileContactNoFacadeDto.builder()
                .id(studentChildEntity.getId())
                .uuid(studentChildEntity.getUuid())
                .version(studentChildEntity.getVersion())
                .status(studentChildEntity.getStatus())
                .studentUUID(studentChildEntity.getStudentUUID())
                .studentChildAsStudentUUID(studentChildEntity.getStudentChildUUID())
                .studentChildUUID(studentChildEntity.getUuid())
                .image(studentChildProfileEntity.getImage())
                .name(studentChildProfileEntity.getName())
                .nic(studentChildProfileEntity.getNic())
                .age(studentChildProfileEntity.getAge())
                .officialTel(studentChildProfileEntity.getOfficialTel())
                .cityUUID(studentChildProfileEntity.getCityUUID())
                .stateUUID(studentChildProfileEntity.getStateUUID())
                .countryUUID(studentChildProfileEntity.getCountryUUID())
                .genderUUID(studentChildProfileEntity.getGenderUUID())
                .email(studentChildProfileEntity.getEmail())
                .studentChildContactNoDto(studentChildContactNoDto)
                .createdAt(studentChildEntity.getCreatedAt())
                .createdBy(studentChildEntity.getCreatedBy())
                .reqCompanyUUID(studentChildEntity.getReqCompanyUUID())
                .reqBranchUUID(studentChildEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentChildEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentChildEntity.getReqCreatedIP())
                .reqCreatedPort(studentChildEntity.getReqCreatedPort())
                .reqCreatedOS(studentChildEntity.getReqCreatedOS())
                .reqCreatedDevice(studentChildEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentChildEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentChildEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentChildEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentChildEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentChildEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentChildEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentChildEntity.getReqUpdatedReferer())
                .editable(studentChildEntity.getEditable())
                .deletable(studentChildEntity.getDeletable())
                .archived(studentChildEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentChildProfileContactNoFacadeDto> updatedFacadeDto(StudentChildEntity studentChildEntity, StudentChildProfileEntity studentChildProfileEntity, List<StudentContactNoDto> studentChildContactNoDto) {

        StudentChildProfileContactNoFacadeDto facadeDto = StudentChildProfileContactNoFacadeDto.builder()
                .id(studentChildEntity.getId())
                .uuid(studentChildEntity.getUuid())
                .version(studentChildEntity.getVersion())
                .status(studentChildEntity.getStatus())
                .studentChildUUID(studentChildProfileEntity.getStudentChildUUID())
                .image(studentChildProfileEntity.getImage())
                .name(studentChildProfileEntity.getName())
                .nic(studentChildProfileEntity.getNic())
                .age(studentChildProfileEntity.getAge())
                .officialTel(studentChildProfileEntity.getOfficialTel())
                .cityUUID(studentChildProfileEntity.getCityUUID())
                .stateUUID(studentChildProfileEntity.getStateUUID())
                .countryUUID(studentChildProfileEntity.getCountryUUID())
                .genderUUID(studentChildProfileEntity.getGenderUUID())
                .email(studentChildProfileEntity.getEmail())
                .studentChildContactNoDto(studentChildContactNoDto)
                .createdAt(studentChildEntity.getCreatedAt())
                .createdBy(studentChildEntity.getCreatedBy())
                .updatedAt(studentChildEntity.getUpdatedAt())
                .updatedBy(studentChildEntity.getUpdatedBy())
                .reqCompanyUUID(studentChildProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(studentChildProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentChildProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentChildProfileEntity.getReqCreatedIP())
                .reqCreatedPort(studentChildProfileEntity.getReqCreatedPort())
                .reqCreatedOS(studentChildProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(studentChildProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentChildProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentChildProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentChildProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentChildProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentChildProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentChildProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentChildProfileEntity.getReqUpdatedReferer())
                .editable(studentChildProfileEntity.getEditable())
                .deletable(studentChildProfileEntity.getDeletable())
                .archived(studentChildProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-child-student-child-profile-contact-nos_store")
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

                    UUID studentChildUUID = null;
                    if ((value.containsKey("studentChildUUID") && (value.getFirst("studentChildUUID") != ""))) {
                        studentChildUUID = UUID.fromString(value.getFirst("studentChildUUID").trim());
                    }

                    StudentChildEntity studentChildEntity = StudentChildEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .studentChildUUID(studentChildUUID)
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
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentChildEntity.getStudentUUID())
                            //check if Student Child Record Already Exists Against the same student
                            .flatMap(studentEntity -> {

                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                //Building Student Child Profile Record
                                StudentChildProfileEntity studentChildProfileEntity = StudentChildProfileEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .studentChildUUID(studentChildEntity.getUuid())
                                        .image(UUID.fromString(value.getFirst("image")))
                                        .name(value.getFirst("name").trim())
                                        .nic(value.getFirst("nic"))
                                        .age(Integer.valueOf(value.getFirst("age")))
                                        .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                        .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                        .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                        .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
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

                                sendFormData.add("docId", String.valueOf(studentChildProfileEntity.getImage()));

                                //check if Gender Record Exists or Not
                                return genderRepository.findByUuidAndDeletedAtIsNull(studentChildProfileEntity.getGenderUUID())
                                        //check if City Record Exists or Not
                                        .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentChildProfileEntity.getCityUUID())
                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                        //check if State Record Exists or not
                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentChildProfileEntity.getStateUUID())
                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                        //check if Country Record Exists or not
                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentChildProfileEntity.getCountryUUID())
                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                        //check if NIC Is Unique Against Student Child
                                                                                        .flatMap(checkNIC -> studentChildProfileRepository.findFirstByNicAndStudentChildUUIDAndDeletedAtIsNull(studentChildProfileEntity.getNic(), studentChildProfileEntity.getStudentChildUUID())
                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                        //check if Child Profile Already Exists Against Student Child
                                                                                        .switchIfEmpty(Mono.defer(() -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildProfileEntity.getStudentChildUUID())
                                                                                                .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Child Profile already exist"))))
                                                                                        //check if Document Record Exists or not
                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentChildProfileEntity.getImage())
                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                        .flatMap(documentEntity -> {
                                                                                                                    // if student child uuid is given
                                                                                                                    if (studentChildEntity.getStudentChildUUID() != null) {

                                                                                                                        // checks if record already exists for student
                                                                                                                        return studentChildRepository.findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getStudentUUID(), studentChildEntity.getStudentChildUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Student Child Record Already Exists for Given Student"))
                                                                                                                                // checks if student uuid exists
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(studentChildEntity.getStudentUUID())
                                                                                                                                        .flatMap(saveStudentEntity -> storeFacadeRecord(studentChildEntity, studentChildProfileEntity, value.get("studentChildContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // else store the record
                                                                                                                    else {
                                                                                                                        return storeFacadeRecord(studentChildEntity, studentChildProfileEntity, value.get("studentChildContactNoDto"), sendFormData);
                                                                                                                    }
                                                                                                                }
                                                                                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Image."))
                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Image.Please Contact Developer."))
                                                                                                ))

                                                                                        )).switchIfEmpty(responseInfoMsg("Country Record Does not exist."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Country Record Does not Exist.Please Contact Developer."))
                                                                        )).switchIfEmpty(responseInfoMsg("State Record Does not Exist."))
                                                                .onErrorResume(ex -> responseErrorMsg("State Record Does not Exist.Please Contact Developer."))
                                                        )).switchIfEmpty(responseInfoMsg("City Record Does not Exist."))
                                                .onErrorResume(ex -> responseErrorMsg("City Record Does not Exist.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Gender Record Does not Exist."))
                                        .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist.Please Contact Developer."));

                            }).switchIfEmpty(responseInfoMsg("Student Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Student Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    public Mono<ServerResponse> storeFacadeRecord(StudentChildEntity studentChildEntity, StudentChildProfileEntity studentChildProfileEntity, List<String> studentChildContactList, MultiValueMap<String, String> sendFormData) {

        //check if Contact Category is Child
        return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("child")
                .flatMap(contactCategoryEntity -> {
                    //Creating an empty list to add student Contact No Records
                    List<StudentContactNoEntity> studentChildContactNoList = new ArrayList<>();

                    // Creating an empty list to add contact Type UUID's
                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                    // Creating an empty list to add contact No's
                    List<String> contactNoList = new ArrayList<>();


                    JsonNode contactNode = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contactNode = objectMapper.readTree(studentChildContactList.toString());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert contactNode != null;


                    UUID studentMetaUUID = null;
                    UUID contactCategoryUUID = null;

                    //iterating over the json node from front and setting contact No's
                    for (JsonNode childContact : contactNode) {

                        StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                .builder()
                                .contactTypeUUID(UUID.fromString(childContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                .contactNo(childContact.get("contactNo").toString().replaceAll("\"", ""))
                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                .studentMetaUUID(studentChildEntity.getUuid())
                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                .createdBy(studentChildEntity.getCreatedBy())
                                .reqCompanyUUID(studentChildEntity.getReqCompanyUUID())
                                .reqBranchUUID(studentChildEntity.getReqBranchUUID())
                                .reqCreatedIP(studentChildEntity.getReqCreatedIP())
                                .reqCreatedPort(studentChildEntity.getReqCreatedPort())
                                .reqCreatedBrowser(studentChildEntity.getReqCreatedBrowser())
                                .reqCreatedOS(studentChildEntity.getReqCreatedOS())
                                .reqCreatedDevice(studentChildEntity.getReqCreatedDevice())
                                .reqCreatedReferer(studentChildEntity.getReqCreatedReferer())
                                .build();

                        studentChildContactNoList.add(studentContactNoEntity);

                        contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                        contactNoList.add(studentContactNoEntity.getContactNo());
                        studentMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                        contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();
                    }

                    //Getting Distinct Values Fom the List of Student Child Contact No List
                    studentChildContactNoList = studentChildContactNoList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    //Getting Distinct Values Fom the List of Contact Type UUID
                    contactTypeUUIDList = contactTypeUUIDList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    // Creating an empty list to add contact No's and returning dto with response
                    List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                    if (!studentChildContactNoList.isEmpty()) {

                        UUID finalStdMetaUUID = studentMetaUUID;

                        UUID finalContactCategoryUUID = contactCategoryUUID;

                        List<StudentContactNoEntity> finalStudentChildContactNoList = studentChildContactNoList;

                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                .collectList()
                                .flatMap(contactTypeEntityList -> {

                                    if (!contactTypeEntityList.isEmpty()) {

                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                            return responseInfoMsg("Contact Type Does not Exist");
                                        } else {
                                            //check if Contact No Record Already Exists against Student Child and Contact Type
                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStdMetaUUID)
                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                    .switchIfEmpty(Mono.defer(() -> studentChildRepository.save(studentChildEntity)
                                                            .then(studentChildProfileRepository.save(studentChildProfileEntity))
                                                            .then(studentContactNoRepository.saveAll(finalStudentChildContactNoList)
                                                                    .collectList())
                                                            .flatMap(mthContactNo -> {

                                                                for (StudentContactNoEntity studentContact : mthContactNo) {
                                                                    StudentContactNoDto studentChildContactNoDto = StudentContactNoDto.builder()
                                                                            .contactNo(studentContact.getContactNo())
                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                            .build();

                                                                    studentContactNoDto.add(studentChildContactNoDto);
                                                                }

                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", studentChildEntity.getCreatedBy().toString(),
                                                                                studentChildEntity.getReqCompanyUUID().toString(), studentChildEntity.getReqBranchUUID().toString())
                                                                        .flatMap(docUpdate -> facadeDto(studentChildEntity, studentChildProfileEntity, studentContactNoDto)
                                                                                .flatMap(studentChildFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentChildFacadeDto))
                                                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."));
                                                            })
                                                            .switchIfEmpty(responseInfoMsg("Unable to Store Record There is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
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
                        //if Contact No List is empty then store student Child and Student Child Profile
                        return studentChildRepository.save(studentChildEntity)
                                //Save Student Child Profile Entity
                                .then(studentChildProfileRepository.save(studentChildProfileEntity))
                                //update Document Status After Storing record
                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", studentChildEntity.getCreatedBy().toString(),
                                                studentChildEntity.getReqCompanyUUID().toString(), studentChildEntity.getReqBranchUUID().toString())
                                        .flatMap(docUpdate -> facadeDto(studentChildEntity, studentChildProfileEntity, studentContactNoDto)
                                                .flatMap(studentChildFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentChildFacadeDto))
                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                    }
                });

    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-child-student-child-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("studentChildUUID")));
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
                .flatMap(value -> studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                        .flatMap(studentChildEntity -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentChildProfileEntity updatedEntity = StudentChildProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentChildUUID(previousProfileEntity.getStudentChildUUID())
                                            .image(UUID.fromString(value.getFirst("image")))
                                            .name(value.getFirst("name").trim())
                                            .nic(value.getFirst("nic"))
                                            .age(Integer.valueOf(value.getFirst("age")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
                                            .officialTel(value.getFirst("officialTel").trim())
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
                                    return studentChildProfileRepository.findFirstByNicAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentChildUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check child profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentChildUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Child Profile already exist"))))
                                            //checks if child uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //check if Gender Record Exists or Not
                                                            .flatMap(studentChildDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                                                    //check if City Record Exists or Not
                                                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                    //checks state uuid exists
                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                                    //checks countries uuid exists
                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                    .flatMap(countryJsonNode -> {

                                                                                                                                //getting List of Contact No. From Front
                                                                                                                                List<String> studentChildContactList = value.get("studentChildContactNoDto");
                                                                                                                                List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                                studentChildContactList.removeIf(s -> s.equals(""));

                                                                                                                                if (!studentChildContactList.isEmpty()) {
                                                                                                                                    return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("child")
                                                                                                                                            .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentChildUUID)
                                                                                                                                                    .collectList()
                                                                                                                                                    .flatMap(existingContactList -> {

                                                                                                                                                        //Removing Already existing Student Child Contact No Entity
                                                                                                                                                        for (StudentContactNoEntity StudentContact : existingContactList) {
                                                                                                                                                            StudentContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                            StudentContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                            StudentContact.setReqDeletedIP(reqIp);
                                                                                                                                                            StudentContact.setReqDeletedPort(reqPort);
                                                                                                                                                            StudentContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                            StudentContact.setReqDeletedOS(reqOs);
                                                                                                                                                            StudentContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                            StudentContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                        }

                                                                                                                                                        //Creating an Object Node to Read Values from Front
                                                                                                                                                        JsonNode contactNode = null;
                                                                                                                                                        try {
                                                                                                                                                            contactNode = new ObjectMapper().readTree(studentChildContactList.toString());
                                                                                                                                                        } catch (JsonProcessingException e) {
                                                                                                                                                            e.printStackTrace();
                                                                                                                                                        }

                                                                                                                                                        //New Contact No list for adding values after building entity
                                                                                                                                                        List<StudentContactNoEntity> stdChildContactNoList = new ArrayList<>();

                                                                                                                                                        List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                        List<String> contactNoList = new ArrayList<>();

                                                                                                                                                        UUID updatedStdMetaUUID = null;

                                                                                                                                                        assert contactNode != null;
                                                                                                                                                        for (JsonNode childContact : contactNode) {

                                                                                                                                                            StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                    .builder()
                                                                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                                                                    .contactTypeUUID(UUID.fromString(childContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                    .contactNo(childContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                    .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                    .studentMetaUUID(studentChildUUID)
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

                                                                                                                                                            stdChildContactNoList.add(studentContactNoEntity);

                                                                                                                                                            contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                            contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                            updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                        }

                                                                                                                                                        //Getting Distinct Values Fom the List of Student Child Contact No List
                                                                                                                                                        stdChildContactNoList = stdChildContactNoList.stream()
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


                                                                                                                                                        UUID finalStdMetaUUID = updatedStdMetaUUID;

                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                        List<StudentContactNoEntity> finalStudentChildContactNoList1 = stdChildContactNoList;

                                                                                                                                                        List<String> finalContactNoList = contactNoList;

                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                .collectList()
                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                        } else {

                                                                                                                                                                            //check if Contact No Record Already Exists against Student Child and Contact Type
                                                                                                                                                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> studentChildProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                            .then(studentChildProfileRepository.save(updatedEntity))
                                                                                                                                                                                            .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                            .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentChildContactNoList1)
                                                                                                                                                                                                    .collectList()
                                                                                                                                                                                                    .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                        for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
                                                                                                                                                                                                            StudentContactNoDto studentChildContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                    .contactNo(studentContact.getContactNo())
                                                                                                                                                                                                                    .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                            studentContactNoDto.add(studentChildContactNoDto);
                                                                                                                                                                                                        }

                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                .flatMap(docUpdate -> updatedFacadeDto(studentChildEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                                                                        .flatMap(stdFacadeDto -> responseSuccessMsg("Record Updated Successfully", stdFacadeDto))
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
                                                                                                                                    return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentChildUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(previousStdContactList -> {

                                                                                                                                                for (StudentContactNoEntity StudentContact : previousStdContactList) {
                                                                                                                                                    StudentContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                    StudentContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                    StudentContact.setReqDeletedIP(reqIp);
                                                                                                                                                    StudentContact.setReqDeletedPort(reqPort);
                                                                                                                                                    StudentContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                    StudentContact.setReqDeletedOS(reqOs);
                                                                                                                                                    StudentContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                    StudentContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                }

                                                                                                                                                return studentContactNoRepository.saveAll(previousStdContactList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(studentContactList -> studentChildProfileRepository.save(previousProfileEntity)
                                                                                                                                                                .then(studentChildProfileRepository.save(updatedEntity))
                                                                                                                                                                .flatMap(StudentChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                        .flatMap(docUpdateEntity -> updatedFacadeDto(studentChildEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                                .flatMap(StudentChildFacadeDto -> responseSuccessMsg("Record Updated Successfully", StudentChildFacadeDto))
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
                                                                                                                    )).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                                    )).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                                    )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Gender Record Does not Exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist. Please contact developer."))
                                                            )).switchIfEmpty(responseInfoMsg("Unable to upload the image"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload the image. Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Child Profile Against the entered Student Child Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Child Profile Against the entered Student Child Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Child Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Child Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

//    public Mono<ServerResponse> updateFacadeRecord(StudentChildEntity studentChildEntity, StudentChildEntity updatedStudentChildEntity, StudentChildProfileEntity previousProfileEntity, StudentChildProfileEntity updatedEntity, List<String> studentChildContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();
//
//        studentChildContactList.removeIf(s -> s.equals(""));
//
//        if (!studentChildContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("child")
//                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Student Child Contact No Entity
//                                for (StudentContactNoEntity studentContact : existingContactList) {
//                                    studentContact.setDeletedBy(updatedStudentChildEntity.getUpdatedBy());
//                                    studentContact.setDeletedAt(updatedStudentChildEntity.getUpdatedAt());
//                                    studentContact.setReqDeletedIP(updatedStudentChildEntity.getReqUpdatedIP());
//                                    studentContact.setReqDeletedPort(updatedStudentChildEntity.getReqUpdatedPort());
//                                    studentContact.setReqDeletedBrowser(updatedStudentChildEntity.getReqUpdatedBrowser());
//                                    studentContact.setReqDeletedOS(updatedStudentChildEntity.getReqUpdatedOS());
//                                    studentContact.setReqDeletedDevice(updatedStudentChildEntity.getReqUpdatedDevice());
//                                    studentContact.setReqDeletedReferer(updatedStudentChildEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(studentChildContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<StudentContactNoEntity> studentChildContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedStdMetaUUID = null;
//
//                                for (JsonNode childContact : contactNode) {
//
//                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(childContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(childContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .studentMetaUUID(studentChildEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedStudentChildEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedStudentChildEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedStudentChildEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedStudentChildEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedStudentChildEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedStudentChildEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedStudentChildEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedStudentChildEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedStudentChildEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    studentChildContactNoList.add(studentContactNoEntity);
//
//                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(studentContactNoEntity.getContactNo());
//
//                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Student Child Contact No List
//                                studentChildContactNoList = studentChildContactNoList.stream()
//                                        .distinct()
//                                        .collect(Collectors.toList());
//
//                                //Getting Distinct Values Fom the List of Contact Type UUID
//                                contactTypeUUIDList = contactTypeUUIDList.stream()
//                                        .distinct()
//                                        .collect(Collectors.toList());
//
//                                //Getting Distinct Values Fom the List of Contact No
//                                contactNoList = contactNoList.stream()
//                                        .distinct()
//                                        .collect(Collectors.toList());
//
//
//                                UUID finalStdMetaUUID = updatedStdMetaUUID;
//
//                                List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;
//
//                                List<StudentContactNoEntity> finalStudentChildContactNoList1 = studentChildContactNoList;
//
//                                List<String> finalContactNoList = contactNoList;
//
//                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
//                                        .collectList()
//                                        .flatMap(contactTypeEntityList -> {
//
//                                            if (!contactTypeEntityList.isEmpty()) {
//
//                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
//                                                    return responseInfoMsg("Contact Type Does not Exist");
//                                                } else {
//
//                                                    //check if Contact No Record Already Exists against Student Child and Contact Type
//                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> studentChildRepository.save(studentChildEntity)
//                                                                    .then(studentChildRepository.save(updatedStudentChildEntity))
//                                                                    .then(studentChildProfileRepository.save(previousProfileEntity))
//                                                                    .then(studentChildProfileRepository.save(updatedEntity))
//                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentChildContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
//                                                                                    StudentContactNoDto studentChildContactNoDto = StudentContactNoDto.builder()
//                                                                                            .contactNo(studentContact.getContactNo())
//                                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    studentContactNoDto.add(studentChildContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedStudentChildEntity.getUpdatedBy().toString(),
//                                                                                                updatedStudentChildEntity.getReqCompanyUUID().toString(), updatedStudentChildEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(studentChildEntity, updatedEntity, studentContactNoDto)
//                                                                                                .flatMap(studentChildFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentChildFacadeDto))
//                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
//                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
//                                                                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.There is something wrong please try again."))
//                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."));
//                                                                            }))
//                                                            ));
//                                                }
//                                            }
//                                            // when list is empty
//                                            else {
//                                                return responseInfoMsg("Contact Type Does not exist.");
//                                            }
//                                        }).switchIfEmpty(responseInfoMsg("Contact Type Does not Exist."))
//                                        .onErrorResume(ex -> responseErrorMsg("Contact Type does not exist.Please Contact Developer."));
//                            })).switchIfEmpty(responseInfoMsg("Contact Category Does not exist."))
//                    .onErrorResume(ex -> responseErrorMsg("Contact Category Does not exist.PLease Contact Developer."));
//        } else {
//            //If Contact No is not empty then store profile only
//            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousStdContactList -> {
//
//                        for (StudentContactNoEntity studentContact : previousStdContactList) {
//                            studentContact.setDeletedBy(updatedStudentChildEntity.getUpdatedBy());
//                            studentContact.setDeletedAt(updatedStudentChildEntity.getUpdatedAt());
//                            studentContact.setReqDeletedIP(updatedStudentChildEntity.getReqUpdatedIP());
//                            studentContact.setReqDeletedPort(updatedStudentChildEntity.getReqUpdatedPort());
//                            studentContact.setReqDeletedBrowser(updatedStudentChildEntity.getReqUpdatedBrowser());
//                            studentContact.setReqDeletedOS(updatedStudentChildEntity.getReqUpdatedOS());
//                            studentContact.setReqDeletedDevice(updatedStudentChildEntity.getReqUpdatedDevice());
//                            studentContact.setReqDeletedReferer(updatedStudentChildEntity.getReqUpdatedReferer());
//                        }
//
//                        return studentContactNoRepository.saveAll(previousStdContactList)
//                                .collectList()
//                                .flatMap(studentContactList -> studentChildRepository.save(studentChildEntity)
//                                        .then(studentChildRepository.save(updatedStudentChildEntity))
//                                        .then(studentChildProfileRepository.save(previousProfileEntity))
//                                        .then(studentChildProfileRepository.save(updatedEntity))
//                                        .flatMap(studentChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedStudentChildEntity.getUpdatedBy().toString(),
//                                                        updatedStudentChildEntity.getReqCompanyUUID().toString(), updatedStudentChildEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(studentChildEntity, updatedEntity, studentContactNoDto)
//                                                        .flatMap(studentChildFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentChildFacadeDto))
//                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
//                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
//                                                ).switchIfEmpty(responseInfoMsg("Unable to update Document. There is something wrong Please try again."))
//                                                .onErrorResume(err -> responseErrorMsg("Unable to Update Document. Please Contact Developer."))
//                                        )
//                                ).switchIfEmpty(responseInfoMsg("Unable to Update Contact No Records. There is something wrong please try again"))
//                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Contact No Records.Please Contact Developer."));
//                    });
//        }
//    }


//    @AuthHasPermission(value = "academic_api_v1_facade_student-child-student-child-profile-contact-nos_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("studentChildUUID")));
//        String userId = serverRequest.headers().firstHeader("auid");
//
//        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
//        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
//        String reqIp = serverRequest.headers().firstHeader("reqIp");
//        String reqPort = serverRequest.headers().firstHeader("reqPort");
//        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
//        String reqOs = serverRequest.headers().firstHeader("reqOs");
//        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
//        String reqReferer = serverRequest.headers().firstHeader("reqReferer");
//
//        return serverRequest.formData()
//                .flatMap(value -> studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
//                        .flatMap(studentChildEntity -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildUUID)
//                                .flatMap(previousProfileEntity -> {
//
//                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();
//
//                                    UUID studentChildAsChildUUID = null;
//                                    if ((value.containsKey("studentChildUUID") && (value.getFirst("studentChildUUID") != ""))) {
//                                        studentChildAsChildUUID = UUID.fromString(value.getFirst("studentChildUUID").trim());
//                                    }
//
//
//                                    StudentChildEntity updatedStudentChildEntity = StudentChildEntity.builder()
//                                            .uuid(studentChildEntity.getUuid())
//                                            .studentUUID(studentChildEntity.getUuid())
//                                            .studentChildUUID(studentChildAsChildUUID)
//                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                            .createdAt(studentChildEntity.getCreatedAt())
//                                            .createdBy(studentChildEntity.getCreatedBy())
//                                            .updatedBy(UUID.fromString(userId))
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(studentChildEntity.getReqCreatedIP())
//                                            .reqCreatedPort(studentChildEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(studentChildEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(studentChildEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(studentChildEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(studentChildEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                            .reqUpdatedIP(reqIp)
//                                            .reqUpdatedPort(reqPort)
//                                            .reqUpdatedBrowser(reqBrowser)
//                                            .reqUpdatedOS(reqOs)
//                                            .reqUpdatedDevice(reqDevice)
//                                            .reqUpdatedReferer(reqReferer)
//                                            .build();
//
//                                    studentChildEntity.setDeletedBy(UUID.fromString(userId));
//                                    studentChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                    studentChildEntity.setReqDeletedIP(reqIp);
//                                    studentChildEntity.setReqDeletedPort(reqPort);
//                                    studentChildEntity.setReqDeletedBrowser(reqBrowser);
//                                    studentChildEntity.setReqDeletedOS(reqOs);
//                                    studentChildEntity.setReqDeletedDevice(reqDevice);
//                                    studentChildEntity.setReqDeletedReferer(reqReferer);
//
//                                    StudentChildProfileEntity updatedEntity = StudentChildProfileEntity.builder()
//                                            .uuid(previousProfileEntity.getUuid())
//                                            .studentChildUUID(previousProfileEntity.getStudentChildUUID())
//                                            .image(UUID.fromString(value.getFirst("image")))
//                                            .name(value.getFirst("name").trim())
//                                            .nic(value.getFirst("nic"))
//                                            .age(Integer.valueOf(value.getFirst("age")))
//                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
//                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
//                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
//                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
//                                            .officialTel(value.getFirst("officialTel").trim())
//                                            .email(value.getFirst("email").trim())
//                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(UUID.fromString(userId))
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                            .reqUpdatedIP(reqIp)
//                                            .reqUpdatedPort(reqPort)
//                                            .reqUpdatedBrowser(reqBrowser)
//                                            .reqUpdatedOS(reqOs)
//                                            .reqUpdatedDevice(reqDevice)
//                                            .reqUpdatedReferer(reqReferer)
//                                            .build();
//
//                                    previousProfileEntity.setDeletedBy(UUID.fromString(userId));
//                                    previousProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                    previousProfileEntity.setReqDeletedIP(reqIp);
//                                    previousProfileEntity.setReqDeletedPort(reqPort);
//                                    previousProfileEntity.setReqDeletedBrowser(reqBrowser);
//                                    previousProfileEntity.setReqDeletedOS(reqOs);
//                                    previousProfileEntity.setReqDeletedDevice(reqDevice);
//                                    previousProfileEntity.setReqDeletedReferer(reqReferer);
//
//                                    sendFormData.add("docId", String.valueOf(updatedEntity.getImage()));
//
//                                    // check nic number is unique
//                                    return studentChildProfileRepository.findFirstByNicAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentChildUUID(), updatedEntity.getUuid())
//                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                                            //check child profile is unique
//                                            .switchIfEmpty(Mono.defer(() -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentChildUUID(), updatedEntity.getUuid())
//                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Child Profile already exist"))))
//                                            //checks if child uuid exists
//                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
//                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
//                                                            //check if Gender Record Exists or Not
//                                                            .flatMap(studentChildDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
//                                                                    //check if City Record Exists or Not
//                                                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
//                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
//                                                                                    //checks state uuid exists
//                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
//                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
//                                                                                                    //checks countries uuid exists
//                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
//                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
//                                                                                                                    .flatMap(countryJsonNode -> {
//
//                                                                                                                                if (updatedStudentChildEntity.getStudentChildUUID() != null) {
//                                                                                                                                    // checks if record already exists for student
//                                                                                                                                    return studentChildRepository.findFirstByStudentUUIDAndStudentChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStudentChildEntity.getStudentUUID(), updatedStudentChildEntity.getStudentUUID(), updatedStudentChildEntity.getUuid())
//                                                                                                                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Child Record Already Exists for Given Student"))
//                                                                                                                                            // checks if student uuid exists
//                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedStudentChildEntity.getStudentUUID())
//                                                                                                                                                    .flatMap(studentEntity -> updateFacadeRecord(studentChildEntity, updatedStudentChildEntity, previousProfileEntity, updatedEntity, value.get("studentChildContactNoDto"), sendFormData))
//                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
//                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
//                                                                                                                                            ));
//                                                                                                                                }
//
//                                                                                                                                // else update the record
//                                                                                                                                else {
//                                                                                                                                    return updateFacadeRecord(studentChildEntity, updatedStudentChildEntity, previousProfileEntity, updatedEntity, value.get("studentChildContactNoDto"), sendFormData);
//                                                                                                                                }
//
//                                                                                                                            }
//                                                                                                                    )).switchIfEmpty(responseInfoMsg("Country does not exist"))
//                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
//                                                                                                    )).switchIfEmpty(responseInfoMsg("State does not exist"))
//                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
//                                                                                    )).switchIfEmpty(responseInfoMsg("City does not exist"))
//                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
//                                                                    ).switchIfEmpty(responseInfoMsg("Gender Record Does not Exist"))
//                                                                    .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist. Please contact developer."))
//                                                            )).switchIfEmpty(responseInfoMsg("Unable to upload the image"))
//                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload the image. Please contact developer."))
//                                            ));
//                                }).switchIfEmpty(responseInfoMsg("Child Profile Against the entered Student Child Record Does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Child Profile Against the entered Student Child Record Does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Student Child Record Does not Exist."))
//                        .onErrorResume(ex -> responseErrorMsg("Student Child Record Does not Exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
//    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-child-student-child-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentChildUUID = UUID.fromString((serverRequest.pathVariable("studentChildUUID")));
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

        return studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                .flatMap(studentChildEntity -> studentChildProfileRepository.findFirstByStudentChildUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                        .flatMap(studentChildProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentChildEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentChildEntity.setDeletedBy(UUID.fromString(userId));
                                    studentChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentChildEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentChildEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentChildEntity.setReqDeletedIP(reqIp);
                                    studentChildEntity.setReqDeletedPort(reqPort);
                                    studentChildEntity.setReqDeletedBrowser(reqBrowser);
                                    studentChildEntity.setReqDeletedOS(reqOs);
                                    studentChildEntity.setReqDeletedDevice(reqDevice);
                                    studentChildEntity.setReqDeletedReferer(reqReferer);

                                    studentChildProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    studentChildProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentChildProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentChildProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentChildProfileEntity.setReqDeletedIP(reqIp);
                                    studentChildProfileEntity.setReqDeletedPort(reqPort);
                                    studentChildProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    studentChildProfileEntity.setReqDeletedOS(reqOs);
                                    studentChildProfileEntity.setReqDeletedDevice(reqDevice);
                                    studentChildProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        StudentContactNoDto studentChildContactNoDto = StudentContactNoDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentChildContactNoDto);
                                    }

                                    return studentChildRepository.save(studentChildEntity)
                                            .then(studentChildProfileRepository.save(studentChildProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentChildEntity, studentChildProfileEntity, studentContactNoDto)
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
