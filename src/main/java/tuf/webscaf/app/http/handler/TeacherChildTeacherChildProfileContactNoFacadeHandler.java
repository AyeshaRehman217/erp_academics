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
import tuf.webscaf.app.dbContext.master.dto.TeacherContactNoDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherChildTeacherChildProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherChildProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherChildProfileEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherChildProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherChildRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
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

@Tag(name = "teacherChildTeacherChildProfileContactNoFacade")
@Component
public class TeacherChildTeacherChildProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherChildRepository teacherChildRepository;

    @Autowired
    SlaveTeacherChildRepository slaveTeacherChildRepository;

    @Autowired
    SlaveTeacherChildProfileRepository slaveTeacherChildProfileRepository;

    @Autowired
    TeacherChildProfileRepository teacherChildProfileRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    SlaveTeacherContactNoRepository slaveTeacherContactNoRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-child-teacher-child-profile-contact-nos_index")
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
            Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> slaveTeacherChildTeacherChildProfileContactNoFacadeDtoFlux = slaveTeacherChildRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveTeacherChildTeacherChildProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherChildProfileEntity -> slaveTeacherChildRepository
                            .countTeacherChildTeacherChildProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherChildProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherChildProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> slaveTeacherChildTeacherChildProfileContactNoFacadeDtoFlux = slaveTeacherChildRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherChildTeacherChildProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherChildProfileEntity -> slaveTeacherChildRepository
                            .countTeacherChildTeacherChildProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherChildProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherChildProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-child-teacher-child-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherChildUUID = UUID.fromString((serverRequest.pathVariable("teacherChildUUID")));

        return slaveTeacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                .flatMap(teacherChildEntity -> slaveTeacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                        .flatMap(teacherChildProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherChildContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherChildContactNoDto);
                                    }

                                    return showFacadeDto(teacherChildEntity, teacherChildProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Child Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Child Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Child Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Child Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherChildTeacherChildProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherChildEntity slaveTeacherChildEntity, SlaveTeacherChildProfileEntity slaveTeacherChildProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoFacadeDto) {

        SlaveTeacherChildTeacherChildProfileContactNoFacadeDto facadeDto = SlaveTeacherChildTeacherChildProfileContactNoFacadeDto.builder()
                .id(slaveTeacherChildEntity.getId())
                .uuid(slaveTeacherChildEntity.getUuid())
                .version(slaveTeacherChildEntity.getVersion())
                .status(slaveTeacherChildEntity.getStatus())
                .teacherUUID(slaveTeacherChildEntity.getTeacherUUID())
                .studentUUID(slaveTeacherChildEntity.getStudentUUID())
                .teacherChildUUID(slaveTeacherChildEntity.getUuid())
                .image(slaveTeacherChildProfileEntity.getImage())
                .name(slaveTeacherChildProfileEntity.getName())
                .nic(slaveTeacherChildProfileEntity.getNic())
                .age(slaveTeacherChildProfileEntity.getAge())
                .officialTel(slaveTeacherChildProfileEntity.getOfficialTel())
                .cityUUID(slaveTeacherChildProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherChildProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherChildProfileEntity.getCountryUUID())
                .genderUUID(slaveTeacherChildProfileEntity.getGenderUUID())
                .email(slaveTeacherChildProfileEntity.getEmail())
                .teacherChildContactNoDto(slaveTeacherContactNoFacadeDto)
                .createdAt(slaveTeacherChildEntity.getCreatedAt())
                .createdBy(slaveTeacherChildEntity.getCreatedBy())
                .reqCompanyUUID(slaveTeacherChildEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherChildEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherChildEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherChildEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherChildEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherChildEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherChildEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherChildEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherChildEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherChildEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherChildEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherChildEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherChildEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherChildEntity.getReqUpdatedReferer())
                .editable(slaveTeacherChildEntity.getEditable())
                .deletable(slaveTeacherChildEntity.getDeletable())
                .archived(slaveTeacherChildEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherChildTeacherChildProfileContactNoFacadeDto> facadeDto(TeacherChildEntity teacherChildEntity, TeacherChildProfileEntity teacherChildProfileEntity, List<TeacherContactNoDto> teacherChildContactNoDto) {

        TeacherChildTeacherChildProfileContactNoFacadeDto facadeDto = TeacherChildTeacherChildProfileContactNoFacadeDto.builder()
                .id(teacherChildEntity.getId())
                .uuid(teacherChildEntity.getUuid())
                .version(teacherChildEntity.getVersion())
                .status(teacherChildEntity.getStatus())
                .teacherUUID(teacherChildEntity.getTeacherUUID())
                .studentUUID(teacherChildEntity.getStudentUUID())
                .teacherChildUUID(teacherChildEntity.getUuid())
                .image(teacherChildProfileEntity.getImage())
                .name(teacherChildProfileEntity.getName())
                .nic(teacherChildProfileEntity.getNic())
                .age(teacherChildProfileEntity.getAge())
                .officialTel(teacherChildProfileEntity.getOfficialTel())
                .cityUUID(teacherChildProfileEntity.getCityUUID())
                .stateUUID(teacherChildProfileEntity.getStateUUID())
                .countryUUID(teacherChildProfileEntity.getCountryUUID())
                .genderUUID(teacherChildProfileEntity.getGenderUUID())
                .email(teacherChildProfileEntity.getEmail())
                .teacherChildContactNoDto(teacherChildContactNoDto)
                .createdAt(teacherChildEntity.getCreatedAt())
                .createdBy(teacherChildEntity.getCreatedBy())
                .reqCompanyUUID(teacherChildEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherChildEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherChildEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherChildEntity.getReqCreatedIP())
                .reqCreatedPort(teacherChildEntity.getReqCreatedPort())
                .reqCreatedOS(teacherChildEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherChildEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherChildEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherChildEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherChildEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherChildEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherChildEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherChildEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherChildEntity.getReqUpdatedReferer())
                .editable(teacherChildEntity.getEditable())
                .deletable(teacherChildEntity.getDeletable())
                .archived(teacherChildEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherChildProfileContactNoFacadeDto> updatedFacadeDto(TeacherChildEntity teacherChildEntity, TeacherChildProfileEntity teacherChildProfileEntity, List<TeacherContactNoDto> teacherChildContactNoDto) {

        TeacherChildProfileContactNoFacadeDto facadeDto = TeacherChildProfileContactNoFacadeDto.builder()
                .id(teacherChildEntity.getId())
                .uuid(teacherChildEntity.getUuid())
                .version(teacherChildEntity.getVersion())
                .status(teacherChildEntity.getStatus())
                .image(teacherChildProfileEntity.getImage())
                .name(teacherChildProfileEntity.getName())
                .nic(teacherChildProfileEntity.getNic())
                .age(teacherChildProfileEntity.getAge())
                .officialTel(teacherChildProfileEntity.getOfficialTel())
                .cityUUID(teacherChildProfileEntity.getCityUUID())
                .stateUUID(teacherChildProfileEntity.getStateUUID())
                .countryUUID(teacherChildProfileEntity.getCountryUUID())
                .genderUUID(teacherChildProfileEntity.getGenderUUID())
                .email(teacherChildProfileEntity.getEmail())
                .teacherChildContactNoDto(teacherChildContactNoDto)
                .updatedAt(teacherChildEntity.getUpdatedAt())
                .updatedBy(teacherChildEntity.getUpdatedBy())
                .reqCompanyUUID(teacherChildProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherChildProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherChildProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherChildProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherChildProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherChildProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherChildProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherChildProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherChildProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherChildProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherChildProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherChildProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherChildProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherChildProfileEntity.getReqUpdatedReferer())
                .editable(teacherChildProfileEntity.getEditable())
                .deletable(teacherChildProfileEntity.getDeletable())
                .archived(teacherChildProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-child-teacher-child-profile-contact-nos_store")
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

                    UUID studentUUID = null;
                    if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
                        studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
                    }

                    TeacherChildEntity teacherChildEntity = TeacherChildEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .studentUUID(studentUUID)
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

                    //check if Teacher Record exists or not
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherChildEntity.getTeacherUUID())
                            //check if Teacher Child Record Already Exists Against the same teacher
                            .flatMap(teacherEntity -> {

                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                //Building Teacher Child Profile Record
                                TeacherChildProfileEntity teacherChildProfileEntity = TeacherChildProfileEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherChildUUID(teacherChildEntity.getUuid())
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

                                sendFormData.add("docId", String.valueOf(teacherChildProfileEntity.getImage()));

                                //check if Gender Record Exists or Not
                                return genderRepository.findByUuidAndDeletedAtIsNull(teacherChildProfileEntity.getGenderUUID())
                                        //check if City Record Exists or Not
                                        .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherChildProfileEntity.getCityUUID())
                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                        //check if State Record Exists or not
                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherChildProfileEntity.getStateUUID())
                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                        //check if Country Record Exists or not
                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherChildProfileEntity.getCountryUUID())
                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                        //check if NIC Is Unique Against Teacher Child
                                                                                        .flatMap(checkNIC -> teacherChildProfileRepository.findFirstByNicAndTeacherChildUUIDAndDeletedAtIsNull(teacherChildProfileEntity.getNic(), teacherChildProfileEntity.getTeacherChildUUID())
                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                        //check if Child Profile Already Exists Against Teacher Child
                                                                                        .switchIfEmpty(Mono.defer(() -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildProfileEntity.getTeacherChildUUID())
                                                                                                .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Child Profile already exist"))))
                                                                                        //check if Document Record Exists or not
                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherChildProfileEntity.getImage())
                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                        .flatMap(documentEntity -> {

                                                                                                                    // if student uuid is given
                                                                                                                    if (teacherChildEntity.getStudentUUID() != null) {

                                                                                                                        // checks if record already exists for student
                                                                                                                        return teacherChildRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherChildEntity.getTeacherUUID(), teacherChildEntity.getStudentUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Child Record Already Exists for Given Student"))
                                                                                                                                // checks if student uuid exists
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherChildEntity.getStudentUUID())
                                                                                                                                        .flatMap(studentEntity -> storeFacadeRecord(teacherChildEntity, teacherChildProfileEntity, value.get("teacherChildContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // else store the record
                                                                                                                    else {
                                                                                                                        return storeFacadeRecord(teacherChildEntity, teacherChildProfileEntity, value.get("teacherChildContactNoDto"), sendFormData);
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

                            }).switchIfEmpty(responseInfoMsg("Teacher Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }


    public Mono<ServerResponse> storeFacadeRecord(TeacherChildEntity teacherChildEntity, TeacherChildProfileEntity teacherChildProfileEntity, List<String> teacherChildContactList, MultiValueMap<String, String> sendFormData) {

        //check if Contact Category is Child
        return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("child")
                .flatMap(contactCategoryEntity -> {
                    //Creating an empty list to add teacher Contact No Records
                    List<TeacherContactNoEntity> teacherChildContactNoList = new ArrayList<>();

                    // Creating an empty list to add contact Type UUID's
                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                    // Creating an empty list to add contact No's
                    List<String> contactNoList = new ArrayList<>();


                    JsonNode contactNode = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contactNode = objectMapper.readTree(teacherChildContactList.toString());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert contactNode != null;


                    UUID teacherMetaUUID = null;
                    UUID contactCategoryUUID = null;

                    //iterating over the json node from front and setting contact No's
                    for (JsonNode childContact : contactNode) {

                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                .builder()
                                .contactTypeUUID(UUID.fromString(childContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                .contactNo(childContact.get("contactNo").toString().replaceAll("\"", ""))
                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                .teacherMetaUUID(teacherChildEntity.getUuid())
                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                .createdBy(teacherChildEntity.getCreatedBy())
                                .reqCompanyUUID(teacherChildEntity.getReqCompanyUUID())
                                .reqBranchUUID(teacherChildEntity.getReqBranchUUID())
                                .reqCreatedIP(teacherChildEntity.getReqCreatedIP())
                                .reqCreatedPort(teacherChildEntity.getReqCreatedPort())
                                .reqCreatedBrowser(teacherChildEntity.getReqCreatedBrowser())
                                .reqCreatedOS(teacherChildEntity.getReqCreatedOS())
                                .reqCreatedDevice(teacherChildEntity.getReqCreatedDevice())
                                .reqCreatedReferer(teacherChildEntity.getReqCreatedReferer())
                                .build();

                        teacherChildContactNoList.add(teacherContactNoEntity);

                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                        contactNoList.add(teacherContactNoEntity.getContactNo());
                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                    }

                    //Getting Distinct Values Fom the List of Teacher Child Contact No List
                    teacherChildContactNoList = teacherChildContactNoList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    //Getting Distinct Values Fom the List of Contact Type UUID
                    contactTypeUUIDList = contactTypeUUIDList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    // Creating an empty list to add contact No's and returning dto with response
                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                    if (!teacherChildContactNoList.isEmpty()) {

                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                        UUID finalContactCategoryUUID = contactCategoryUUID;

                        List<TeacherContactNoEntity> finalTeacherChildContactNoList = teacherChildContactNoList;

                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                .collectList()
                                .flatMap(contactTypeEntityList -> {

                                    if (!contactTypeEntityList.isEmpty()) {

                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                            return responseInfoMsg("Contact Type Does not Exist");
                                        } else {
                                            //check if Contact No Record Already Exists against Teacher Child and Contact Type
                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherChildRepository.save(teacherChildEntity)
                                                            .then(teacherChildProfileRepository.save(teacherChildProfileEntity))
                                                            .then(teacherContactNoRepository.saveAll(finalTeacherChildContactNoList)
                                                                    .collectList())
                                                            .flatMap(mthContactNo -> {

                                                                for (TeacherContactNoEntity teacherContact : mthContactNo) {
                                                                    TeacherContactNoDto teacherChildContactNoDto = TeacherContactNoDto.builder()
                                                                            .contactNo(teacherContact.getContactNo())
                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                            .build();

                                                                    teacherContactNoDto.add(teacherChildContactNoDto);
                                                                }

                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherChildEntity.getCreatedBy().toString(),
                                                                                teacherChildEntity.getReqCompanyUUID().toString(), teacherChildEntity.getReqBranchUUID().toString())
                                                                        .flatMap(docUpdate -> facadeDto(teacherChildEntity, teacherChildProfileEntity, teacherContactNoDto)
                                                                                .flatMap(teacherChildFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherChildFacadeDto))
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
                        //if Contact No List is empty then store teacher Child and Teacher Child Profile
                        return teacherChildRepository.save(teacherChildEntity)
                                //Save Teacher Child Profile Entity
                                .then(teacherChildProfileRepository.save(teacherChildProfileEntity))
                                //update Document Status After Storing record
                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherChildEntity.getCreatedBy().toString(),
                                                teacherChildEntity.getReqCompanyUUID().toString(), teacherChildEntity.getReqBranchUUID().toString())
                                        .flatMap(docUpdate -> facadeDto(teacherChildEntity, teacherChildProfileEntity, teacherContactNoDto)
                                                .flatMap(teacherChildFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherChildFacadeDto))
                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                    }
                });

    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-child-teacher-child-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherChildUUID = UUID.fromString((serverRequest.pathVariable("teacherChildUUID")));
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
                .flatMap(value -> teacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                        .flatMap(teacherChildEntity -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherChildProfileEntity updatedEntity = TeacherChildProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .teacherChildUUID(previousProfileEntity.getTeacherChildUUID())
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
                                    return teacherChildProfileRepository.findFirstByNicAndTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getTeacherChildUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check child profile is unique
                                            .switchIfEmpty(Mono.defer(() -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherChildUUID(), updatedEntity.getUuid())
                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Child Profile already exist"))))
                                            //checks if child uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //check if Gender Record Exists or Not
                                                            .flatMap(teacherChildDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
                                                                                                                                List<String> teacherChildContactList = value.get("teacherChildContactNoDto");
                                                                                                                                List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                teacherChildContactList.removeIf(s -> s.equals(""));

                                                                                                                                if (!teacherChildContactList.isEmpty()) {
                                                                                                                                    return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("child")
                                                                                                                                            .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherChildUUID)
                                                                                                                                                    .collectList()
                                                                                                                                                    .flatMap(existingContactList -> {

                                                                                                                                                        //Removing Already existing Teacher Child Contact No Entity
                                                                                                                                                        for (TeacherContactNoEntity TeacherContact : existingContactList) {
                                                                                                                                                            TeacherContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                            TeacherContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                            TeacherContact.setReqDeletedIP(reqIp);
                                                                                                                                                            TeacherContact.setReqDeletedPort(reqPort);
                                                                                                                                                            TeacherContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                            TeacherContact.setReqDeletedOS(reqOs);
                                                                                                                                                            TeacherContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                            TeacherContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                        }

                                                                                                                                                        //Creating an Object Node to Read Values from Front
                                                                                                                                                        JsonNode contactNode = null;
                                                                                                                                                        try {
                                                                                                                                                            contactNode = new ObjectMapper().readTree(teacherChildContactList.toString());
                                                                                                                                                        } catch (JsonProcessingException e) {
                                                                                                                                                            e.printStackTrace();
                                                                                                                                                        }

                                                                                                                                                        //New Contact No list for adding values after building entity
                                                                                                                                                        List<TeacherContactNoEntity> stdChildContactNoList = new ArrayList<>();

                                                                                                                                                        List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                        List<String> contactNoList = new ArrayList<>();

                                                                                                                                                        UUID updatedStdMetaUUID = null;

                                                                                                                                                        assert contactNode != null;
                                                                                                                                                        for (JsonNode childContact : contactNode) {

                                                                                                                                                            TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                    .builder()
                                                                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                                                                    .contactTypeUUID(UUID.fromString(childContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                    .contactNo(childContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                    .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                    .teacherMetaUUID(teacherChildUUID)
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

                                                                                                                                                            stdChildContactNoList.add(teacherContactNoEntity);

                                                                                                                                                            contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                            contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                            updatedStdMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                        }

                                                                                                                                                        //Getting Distinct Values Fom the List of Teacher Child Contact No List
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

                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherChildContactNoList1 = stdChildContactNoList;

                                                                                                                                                        List<String> finalContactNoList = contactNoList;

                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                .collectList()
                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                        } else {

                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher Child and Contact Type
                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherChildProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                            .then(teacherChildProfileRepository.save(updatedEntity))
                                                                                                                                                                                            .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                            .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherChildContactNoList1)
                                                                                                                                                                                                    .collectList()
                                                                                                                                                                                                    .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                        for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                            TeacherContactNoDto teacherChildContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                                    .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                    .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                            teacherContactNoDto.add(teacherChildContactNoDto);
                                                                                                                                                                                                        }

                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                .flatMap(docUpdate -> updatedFacadeDto(teacherChildEntity, updatedEntity, teacherContactNoDto)
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
                                                                                                                                    return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherChildUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(previousStdContactList -> {

                                                                                                                                                for (TeacherContactNoEntity TeacherContact : previousStdContactList) {
                                                                                                                                                    TeacherContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                    TeacherContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                    TeacherContact.setReqDeletedIP(reqIp);
                                                                                                                                                    TeacherContact.setReqDeletedPort(reqPort);
                                                                                                                                                    TeacherContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                    TeacherContact.setReqDeletedOS(reqOs);
                                                                                                                                                    TeacherContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                    TeacherContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                }

                                                                                                                                                return teacherContactNoRepository.saveAll(previousStdContactList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(teacherContactList -> teacherChildProfileRepository.save(previousProfileEntity)
                                                                                                                                                                .then(teacherChildProfileRepository.save(updatedEntity))
                                                                                                                                                                .flatMap(TeacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                        .flatMap(docUpdateEntity -> updatedFacadeDto(teacherChildEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                .flatMap(TeacherChildFacadeDto -> responseSuccessMsg("Record Updated Successfully", TeacherChildFacadeDto))
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
                                }).switchIfEmpty(responseInfoMsg("Child Profile Against the entered Teacher Child Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Child Profile Against the entered Teacher Child Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Child Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Child Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

//    @AuthHasPermission(value = "academic_api_v1_facade_teacher-child-teacher-child-profile-contact-nos_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID teacherChildUUID = UUID.fromString((serverRequest.pathVariable("teacherChildUUID")));
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
//                .flatMap(value -> teacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
//                        .flatMap(teacherChildEntity -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildUUID)
//                                .flatMap(previousProfileEntity -> {
//
//                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();
//
//                                    UUID studentUUID = null;
//                                    if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
//                                        studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
//                                    }
//
//                                    TeacherChildEntity updatedTeacherChildEntity = TeacherChildEntity.builder()
//                                            .uuid(teacherChildEntity.getUuid())
//                                            .teacherUUID(teacherChildEntity.getTeacherUUID())
//                                            .studentUUID(studentUUID)
//                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                            .createdAt(teacherChildEntity.getCreatedAt())
//                                            .createdBy(teacherChildEntity.getCreatedBy())
//                                            .updatedBy(UUID.fromString(userId))
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(teacherChildEntity.getReqCreatedIP())
//                                            .reqCreatedPort(teacherChildEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(teacherChildEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(teacherChildEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(teacherChildEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(teacherChildEntity.getReqCreatedReferer())
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
//                                    teacherChildEntity.setDeletedBy(UUID.fromString(userId));
//                                    teacherChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                    teacherChildEntity.setReqDeletedIP(reqIp);
//                                    teacherChildEntity.setReqDeletedPort(reqPort);
//                                    teacherChildEntity.setReqDeletedBrowser(reqBrowser);
//                                    teacherChildEntity.setReqDeletedOS(reqOs);
//                                    teacherChildEntity.setReqDeletedDevice(reqDevice);
//                                    teacherChildEntity.setReqDeletedReferer(reqReferer);
//
//                                    TeacherChildProfileEntity updatedEntity = TeacherChildProfileEntity.builder()
//                                            .uuid(previousProfileEntity.getUuid())
//                                            .teacherChildUUID(previousProfileEntity.getTeacherChildUUID())
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
//                                    return teacherChildProfileRepository.findFirstByNicAndTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getTeacherChildUUID(), updatedEntity.getUuid())
//                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                                            //check child profile is unique
//                                            .switchIfEmpty(Mono.defer(() -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherChildUUID(), updatedEntity.getUuid())
//                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Child Profile already exist"))))
//                                            //checks if child uuid exists
//                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
//                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
//                                                            //check if Gender Record Exists or Not
//                                                            .flatMap(teacherChildDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
//                                                                                                                                if (updatedTeacherChildEntity.getStudentUUID() != null) {
//                                                                                                                                    // checks if record already exists for student
//                                                                                                                                    return teacherChildRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherChildEntity.getTeacherUUID(), updatedTeacherChildEntity.getStudentUUID(), updatedTeacherChildEntity.getUuid())
//                                                                                                                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Child Record Already Exists for Given Student"))
//                                                                                                                                            // checks if student uuid exists
//                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedTeacherChildEntity.getStudentUUID())
//                                                                                                                                                    .flatMap(studentEntity -> updateFacadeRecord(teacherChildEntity, updatedTeacherChildEntity, previousProfileEntity, updatedEntity, value.get("teacherChildContactNoDto"), sendFormData))
//                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
//                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
//                                                                                                                                            ));
//                                                                                                                                }
//
//                                                                                                                                // else update the record
//                                                                                                                                else {
//                                                                                                                                    return updateFacadeRecord(teacherChildEntity, updatedTeacherChildEntity, previousProfileEntity, updatedEntity, value.get("teacherChildContactNoDto"), sendFormData);
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
//                                }).switchIfEmpty(responseInfoMsg("Child Profile Against the entered Teacher Child Record Does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Child Profile Against the entered Teacher Child Record Does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Teacher Child Record Does not Exist."))
//                        .onErrorResume(ex -> responseErrorMsg("Teacher Child Record Does not Exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
//    }
//
//
//    public Mono<ServerResponse> updateFacadeRecord(TeacherChildEntity teacherChildEntity, TeacherChildEntity updatedTeacherChildEntity, TeacherChildProfileEntity previousProfileEntity, TeacherChildProfileEntity updatedEntity, List<String> teacherChildContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();
//
//        teacherChildContactList.removeIf(s -> s.equals(""));
//
//        if (!teacherChildContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("child")
//                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Teacher Child Contact No Entity
//                                for (TeacherContactNoEntity teacherContact : existingContactList) {
//                                    teacherContact.setDeletedBy(updatedTeacherChildEntity.getUpdatedBy());
//                                    teacherContact.setDeletedAt(updatedTeacherChildEntity.getUpdatedAt());
//                                    teacherContact.setReqDeletedIP(updatedTeacherChildEntity.getReqUpdatedIP());
//                                    teacherContact.setReqDeletedPort(updatedTeacherChildEntity.getReqUpdatedPort());
//                                    teacherContact.setReqDeletedBrowser(updatedTeacherChildEntity.getReqUpdatedBrowser());
//                                    teacherContact.setReqDeletedOS(updatedTeacherChildEntity.getReqUpdatedOS());
//                                    teacherContact.setReqDeletedDevice(updatedTeacherChildEntity.getReqUpdatedDevice());
//                                    teacherContact.setReqDeletedReferer(updatedTeacherChildEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(teacherChildContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<TeacherContactNoEntity> teacherChildContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedTeacherMetaUUID = null;
//
//                                for (JsonNode childContact : contactNode) {
//
//                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(childContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(childContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .teacherMetaUUID(teacherChildEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedTeacherChildEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedTeacherChildEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedTeacherChildEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedTeacherChildEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedTeacherChildEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedTeacherChildEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedTeacherChildEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedTeacherChildEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedTeacherChildEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    teacherChildContactNoList.add(teacherContactNoEntity);
//
//                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(teacherContactNoEntity.getContactNo());
//
//                                    updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Teacher Child Contact No List
//                                teacherChildContactNoList = teacherChildContactNoList.stream()
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
//                                UUID finalTeacherMetaUUID = updatedTeacherMetaUUID;
//
//                                List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;
//
//                                List<TeacherContactNoEntity> finalTeacherChildContactNoList1 = teacherChildContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Teacher Child and Contact Type
//                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> teacherChildRepository.save(teacherChildEntity)
//                                                                    .then(teacherChildRepository.save(updatedTeacherChildEntity))
//                                                                    .then(teacherChildProfileRepository.save(previousProfileEntity))
//                                                                    .then(teacherChildProfileRepository.save(updatedEntity))
//                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherChildContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
//                                                                                    TeacherContactNoDto teacherChildContactNoDto = TeacherContactNoDto.builder()
//                                                                                            .contactNo(teacherContact.getContactNo())
//                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    teacherContactNoDto.add(teacherChildContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedTeacherChildEntity.getUpdatedBy().toString(),
//                                                                                                updatedTeacherChildEntity.getReqCompanyUUID().toString(), updatedTeacherChildEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherChildEntity, updatedEntity, teacherContactNoDto)
//                                                                                                .flatMap(teacherChildFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherChildFacadeDto))
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
//            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousTeacherContactList -> {
//
//                        for (TeacherContactNoEntity teacherContact : previousTeacherContactList) {
//                            teacherContact.setDeletedBy(updatedTeacherChildEntity.getUpdatedBy());
//                            teacherContact.setDeletedAt(updatedTeacherChildEntity.getUpdatedAt());
//                            teacherContact.setReqDeletedIP(updatedTeacherChildEntity.getReqUpdatedIP());
//                            teacherContact.setReqDeletedPort(updatedTeacherChildEntity.getReqUpdatedPort());
//                            teacherContact.setReqDeletedBrowser(updatedTeacherChildEntity.getReqUpdatedBrowser());
//                            teacherContact.setReqDeletedOS(updatedTeacherChildEntity.getReqUpdatedOS());
//                            teacherContact.setReqDeletedDevice(updatedTeacherChildEntity.getReqUpdatedDevice());
//                            teacherContact.setReqDeletedReferer(updatedTeacherChildEntity.getReqUpdatedReferer());
//                        }
//
//                        return teacherContactNoRepository.saveAll(previousTeacherContactList)
//                                .collectList()
//                                .flatMap(teacherContactList -> teacherChildRepository.save(teacherChildEntity)
//                                        .then(teacherChildRepository.save(updatedTeacherChildEntity))
//                                        .then(teacherChildProfileRepository.save(previousProfileEntity))
//                                        .then(teacherChildProfileRepository.save(updatedEntity))
//                                        .flatMap(teacherChildProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedTeacherChildEntity.getUpdatedBy().toString(),
//                                                        updatedTeacherChildEntity.getReqCompanyUUID().toString(), updatedTeacherChildEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherChildEntity, updatedEntity, teacherContactNoDto)
//                                                        .flatMap(teacherChildFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherChildFacadeDto))
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


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-child-teacher-child-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherChildUUID = UUID.fromString((serverRequest.pathVariable("teacherChildUUID")));
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

        return teacherChildRepository.findByUuidAndDeletedAtIsNull(teacherChildUUID)
                .flatMap(teacherChildEntity -> teacherChildProfileRepository.findFirstByTeacherChildUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                        .flatMap(teacherChildProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherChildEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherChildEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherChildEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherChildEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherChildEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherChildEntity.setReqDeletedIP(reqIp);
                                    teacherChildEntity.setReqDeletedPort(reqPort);
                                    teacherChildEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherChildEntity.setReqDeletedOS(reqOs);
                                    teacherChildEntity.setReqDeletedDevice(reqDevice);
                                    teacherChildEntity.setReqDeletedReferer(reqReferer);

                                    teacherChildProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherChildProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherChildProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherChildProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherChildProfileEntity.setReqDeletedIP(reqIp);
                                    teacherChildProfileEntity.setReqDeletedPort(reqPort);
                                    teacherChildProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherChildProfileEntity.setReqDeletedOS(reqOs);
                                    teacherChildProfileEntity.setReqDeletedDevice(reqDevice);
                                    teacherChildProfileEntity.setReqDeletedReferer(reqReferer);

                                    for (TeacherContactNoEntity teacherContact : teacherContactNoEntity) {

                                        teacherContact.setDeletedBy(UUID.fromString(userId));
                                        teacherContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                        teacherContact.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                        teacherContact.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                        teacherContact.setReqDeletedIP(reqIp);
                                        teacherContact.setReqDeletedPort(reqPort);
                                        teacherContact.setReqDeletedBrowser(reqBrowser);
                                        teacherContact.setReqDeletedOS(reqOs);
                                        teacherContact.setReqDeletedDevice(reqDevice);
                                        teacherContact.setReqDeletedReferer(reqReferer);

                                        teacherContactNoEntityList.add(teacherContact);

                                    }

                                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                    for (TeacherContactNoEntity teacherContact : teacherContactNoEntity) {
                                        TeacherContactNoDto teacherChildContactNoDto = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherChildContactNoDto);
                                    }

                                    return teacherChildRepository.save(teacherChildEntity)
                                            .then(teacherChildProfileRepository.save(teacherChildProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherChildEntity, teacherChildProfileEntity, teacherContactNoDto)
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
