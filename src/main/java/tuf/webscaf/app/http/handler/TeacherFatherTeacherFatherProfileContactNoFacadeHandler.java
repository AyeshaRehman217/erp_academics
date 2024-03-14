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
import tuf.webscaf.app.dbContext.master.dto.TeacherContactNoDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherFatherTeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherFatherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherFatherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherFatherRepository;
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

@Tag(name = "teacherFatherTeacherFatherProfileContactNoFacade")
@Component
public class TeacherFatherTeacherFatherProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    SlaveTeacherFatherRepository slaveTeacherFatherRepository;

    @Autowired
    SlaveTeacherFatherProfileRepository slaveTeacherFatherProfileRepository;

    @Autowired
    TeacherFatherProfileRepository teacherFatherProfileRepository;

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
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-father-teacher-father-profile-contact-nos_index")
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
            Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> slaveTeacherFatherTeacherFatherProfileContactNoFacadeDtoFlux = slaveTeacherFatherRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveTeacherFatherTeacherFatherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherFatherProfileEntity -> slaveTeacherFatherRepository
                            .countTeacherFatherTeacherFatherProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherFatherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> slaveTeacherFatherTeacherFatherProfileContactNoFacadeDtoFlux = slaveTeacherFatherRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherFatherTeacherFatherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherFatherProfileEntity -> slaveTeacherFatherRepository
                            .countTeacherFatherTeacherFatherProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherFatherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherFatherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-father-teacher-father-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherFatherUUID = UUID.fromString((serverRequest.pathVariable("teacherFatherUUID")));

        return slaveTeacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                .flatMap(teacherFatherEntity -> slaveTeacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                        .flatMap(teacherFatherProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherFatherContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherFatherContactNoDto);
                                    }

                                    return showFacadeDto(teacherFatherEntity, teacherFatherProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Father Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Father Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Father Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Father Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherFatherEntity slaveTeacherFatherEntity, SlaveTeacherFatherProfileEntity slaveTeacherFatherProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoFacadeDto) {

        SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto facadeDto = SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto.builder()
                .id(slaveTeacherFatherEntity.getId())
                .uuid(slaveTeacherFatherEntity.getUuid())
                .version(slaveTeacherFatherEntity.getVersion())
                .status(slaveTeacherFatherEntity.getStatus())
                .teacherUUID(slaveTeacherFatherEntity.getTeacherUUID())
                .teacherFatherUUID(slaveTeacherFatherEntity.getUuid())
                .image(slaveTeacherFatherProfileEntity.getImage())
                .name(slaveTeacherFatherProfileEntity.getName())
                .nic(slaveTeacherFatherProfileEntity.getNic())
                .age(slaveTeacherFatherProfileEntity.getAge())
                .officialTel(slaveTeacherFatherProfileEntity.getOfficialTel())
                .cityUUID(slaveTeacherFatherProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherFatherProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherFatherProfileEntity.getCountryUUID())
                .noOfDependents(slaveTeacherFatherProfileEntity.getNoOfDependents())
                .email(slaveTeacherFatherProfileEntity.getEmail())
                .teacherFatherContactNoDto(slaveTeacherContactNoFacadeDto)
                .createdAt(slaveTeacherFatherEntity.getCreatedAt())
                .createdBy(slaveTeacherFatherEntity.getCreatedBy())
                .reqCompanyUUID(slaveTeacherFatherEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherFatherEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherFatherEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherFatherEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherFatherEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherFatherEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherFatherEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherFatherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherFatherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherFatherEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherFatherEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherFatherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherFatherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherFatherEntity.getReqUpdatedReferer())
                .editable(slaveTeacherFatherEntity.getEditable())
                .deletable(slaveTeacherFatherEntity.getDeletable())
                .archived(slaveTeacherFatherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherFatherTeacherFatherProfileContactNoFacadeDto> facadeDto(TeacherFatherEntity teacherFatherEntity, TeacherFatherProfileEntity teacherFatherProfileEntity, List<TeacherContactNoDto> teacherContactNoDto) {

        TeacherFatherTeacherFatherProfileContactNoFacadeDto facadeDto = TeacherFatherTeacherFatherProfileContactNoFacadeDto.builder()
                .id(teacherFatherEntity.getId())
                .uuid(teacherFatherEntity.getUuid())
                .version(teacherFatherEntity.getVersion())
                .status(teacherFatherEntity.getStatus())
                .teacherUUID(teacherFatherEntity.getTeacherUUID())
                .teacherFatherUUID(teacherFatherEntity.getUuid())
                .image(teacherFatherProfileEntity.getImage())
                .name(teacherFatherProfileEntity.getName())
                .nic(teacherFatherProfileEntity.getNic())
                .age(teacherFatherProfileEntity.getAge())
                .officialTel(teacherFatherProfileEntity.getOfficialTel())
                .cityUUID(teacherFatherProfileEntity.getCityUUID())
                .stateUUID(teacherFatherProfileEntity.getStateUUID())
                .countryUUID(teacherFatherProfileEntity.getCountryUUID())
                .noOfDependents(teacherFatherProfileEntity.getNoOfDependents())
                .email(teacherFatherProfileEntity.getEmail())
                .teacherFatherContactNoDto(teacherContactNoDto)
                .createdAt(teacherFatherEntity.getCreatedAt())
                .createdBy(teacherFatherEntity.getCreatedBy())
                .reqCompanyUUID(teacherFatherEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherFatherEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherFatherEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherFatherEntity.getReqCreatedIP())
                .reqCreatedPort(teacherFatherEntity.getReqCreatedPort())
                .reqCreatedOS(teacherFatherEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherFatherEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherFatherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherFatherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherFatherEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherFatherEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherFatherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherFatherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherFatherEntity.getReqUpdatedReferer())
                .editable(teacherFatherEntity.getEditable())
                .deletable(teacherFatherEntity.getDeletable())
                .archived(teacherFatherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherFatherProfileContactNoFacadeDto> updatedFacadeDto(TeacherFatherEntity teacherFatherEntity, TeacherFatherProfileEntity teacherFatherProfileEntity, List<TeacherContactNoDto> teacherContactNoDto) {

        TeacherFatherProfileContactNoFacadeDto facadeDto = TeacherFatherProfileContactNoFacadeDto.builder()
                .id(teacherFatherEntity.getId())
                .uuid(teacherFatherEntity.getUuid())
                .version(teacherFatherEntity.getVersion())
                .status(teacherFatherEntity.getStatus())
                .image(teacherFatherProfileEntity.getImage())
                .name(teacherFatherProfileEntity.getName())
                .nic(teacherFatherProfileEntity.getNic())
                .age(teacherFatherProfileEntity.getAge())
                .officialTel(teacherFatherProfileEntity.getOfficialTel())
                .cityUUID(teacherFatherProfileEntity.getCityUUID())
                .stateUUID(teacherFatherProfileEntity.getStateUUID())
                .countryUUID(teacherFatherProfileEntity.getCountryUUID())
                .noOfDependents(teacherFatherProfileEntity.getNoOfDependents())
                .email(teacherFatherProfileEntity.getEmail())
                .teacherFatherContactNoDto(teacherContactNoDto)
                .updatedAt(teacherFatherEntity.getUpdatedAt())
                .updatedBy(teacherFatherEntity.getUpdatedBy())
                .reqCompanyUUID(teacherFatherProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherFatherProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherFatherProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherFatherProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherFatherProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherFatherProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherFatherProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherFatherProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherFatherProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherFatherProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherFatherProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherFatherProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherFatherProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherFatherProfileEntity.getReqUpdatedReferer())
                .editable(teacherFatherProfileEntity.getEditable())
                .deletable(teacherFatherProfileEntity.getDeletable())
                .archived(teacherFatherProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-father-teacher-father-profile-contact-nos_store")
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

                    TeacherFatherEntity teacherFatherEntity = TeacherFatherEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
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
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherFatherEntity.getTeacherUUID())
                            //check if Teacher Father Record Already Exists Against the same teacher
                            .flatMap(teacherEntity -> teacherFatherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                            .flatMap(checkMsg -> responseInfoMsg("Teacher Father Record Against the Entered Teacher Already Exist."))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                //Building Teacher Father Profile Record
                                                TeacherFatherProfileEntity teacherFatherProfileEntity = TeacherFatherProfileEntity
                                                        .builder()
                                                        .uuid(UUID.randomUUID())
                                                        .teacherFatherUUID(teacherFatherEntity.getUuid())
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

                                                sendFormData.add("docId", String.valueOf(teacherFatherProfileEntity.getImage()));

                                                //check if Teacher Father Record Exists or Not
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherFatherProfileEntity.getCityUUID())
                                                        .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                //check if State Record Exists or not
                                                                .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherFatherProfileEntity.getStateUUID())
                                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                        //check if Country Record Exists or not
                                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherFatherProfileEntity.getCountryUUID())
                                                                                                        .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                //check if NIC Is Unique Against Teacher Father
                                                                                                                .flatMap(checkNIC -> teacherFatherProfileRepository.findFirstByNicAndTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherProfileEntity.getNic(), teacherFatherProfileEntity.getTeacherFatherUUID())
                                                                                                                        .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                                                //check if Father Profile Already Exists Against Teacher Father
                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherProfileEntity.getTeacherFatherUUID())
                                                                                                                        .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Father Profile already exist"))))
                                                                                                                //check if Document Record Exists or not
                                                                                                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherFatherProfileEntity.getImage())
                                                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                //check if Contact Category is Father
                                                                                                                                                .flatMap(documentEntity -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("father")
                                                                                                                                                                .flatMap(contactCategoryEntity -> {

                                                                                                                                                                    //getting List of Contact No. From Front
                                                                                                                                                                    List<String> teacherFatherContactList = value.get("teacherFatherContactNoDto");
                                                                                                                                                                    //Creating an empty list to add teacher Contact No Records
                                                                                                                                                                    List<TeacherContactNoEntity> teacherFatherContactNoList = new ArrayList<>();

                                                                                                                                                                    // Creating an empty list to add contact Type UUID's
                                                                                                                                                                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                    // Creating an empty list to add contact No's
                                                                                                                                                                    List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                    JsonNode contactNode = null;
                                                                                                                                                                    ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                    try {
                                                                                                                                                                        contactNode = objectMapper.readTree(teacherFatherContactList.toString());
                                                                                                                                                                    } catch (JsonProcessingException e) {
                                                                                                                                                                        e.printStackTrace();
                                                                                                                                                                    }
                                                                                                                                                                    assert contactNode != null;


                                                                                                                                                                    UUID teacherMetaUUID = null;
                                                                                                                                                                    UUID contactCategoryUUID = null;

                                                                                                                                                                    //iterating over the json node from front and setting contact No's
                                                                                                                                                                    for (JsonNode fatherContact : contactNode) {

                                                                                                                                                                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                                .builder()
                                                                                                                                                                                .contactTypeUUID(UUID.fromString(fatherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                .contactNo(fatherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                .teacherMetaUUID(teacherFatherEntity.getUuid())
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

                                                                                                                                                                        teacherFatherContactNoList.add(teacherContactNoEntity);

                                                                                                                                                                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                                                                                                                                                                        contactNoList.add(teacherContactNoEntity.getContactNo());
                                                                                                                                                                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                    }

                                                                                                                                                                    //Getting Distinct Values Fom the List of Teacher Father Contact No List
                                                                                                                                                                    teacherFatherContactNoList = teacherFatherContactNoList.stream()
                                                                                                                                                                            .distinct()
                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                    //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                    contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                            .distinct()
                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                    // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                                                    if (!teacherFatherContactNoList.isEmpty()) {

                                                                                                                                                                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                                                                                                                                                                        UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherFatherContactNoList = teacherFatherContactNoList;

                                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                .collectList()
                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                        } else {
                                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher Father and Contact Type
                                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull
                                                                                                                                                                                                            (contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherFatherRepository.save(teacherFatherEntity)
                                                                                                                                                                                                            .then(teacherFatherProfileRepository.save(teacherFatherProfileEntity))
                                                                                                                                                                                                            .then(teacherContactNoRepository.saveAll(finalTeacherFatherContactNoList)
                                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                                            .flatMap(mthContactNo -> {

                                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : mthContactNo) {
                                                                                                                                                                                                                    TeacherContactNoDto teacherFatherContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                            .build();

                                                                                                                                                                                                                    teacherContactNoDto.add(teacherFatherContactNoDto);
                                                                                                                                                                                                                }

                                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherFatherEntity, teacherFatherProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                                                .flatMap(teacherFatherFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherFatherFacadeDto))
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
                                                                                                                                                                        //if Contact No List is empty then store teacher Father and Teacher Father Profile
                                                                                                                                                                        return teacherFatherRepository.save(teacherFatherEntity)
                                                                                                                                                                                //Save Teacher Father Profile Entity
                                                                                                                                                                                .then(teacherFatherProfileRepository.save(teacherFatherProfileEntity))
                                                                                                                                                                                //update Document Status After Storing record
                                                                                                                                                                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherFatherEntity, teacherFatherProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                .flatMap(teacherFatherFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherFatherFacadeDto))
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

                                                                                                                )).switchIfEmpty(responseInfoMsg("Country Record Does not exist."))
                                                                                                        .onErrorResume(ex -> responseErrorMsg("Country Record Does not Exist.Please Contact Developer."))
                                                                                        )).switchIfEmpty(responseInfoMsg("State Record Does not Exist."))
                                                                                .onErrorResume(ex -> responseErrorMsg("State Record Does not Exist.Please Contact Developer."))
                                                                )).switchIfEmpty(responseInfoMsg("City Record Does not Exist."))
                                                        .onErrorResume(ex -> responseErrorMsg("City Record Does not Exist.Please Contact Developer."));
                                            }))
                            ).switchIfEmpty(responseInfoMsg("Teacher Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-father-teacher-father-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherFatherUUID = UUID.fromString((serverRequest.pathVariable("teacherFatherUUID")));
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
                .flatMap(value -> teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                        .flatMap(teacherFatherEntity -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherFatherProfileEntity updatedEntity = TeacherFatherProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .teacherFatherUUID(previousProfileEntity.getTeacherFatherUUID())
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
                                    return teacherFatherProfileRepository.findFirstByNicAndTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getTeacherFatherUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check father profile is unique
                                            .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherFatherUUID(), updatedEntity.getUuid())
                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Father Profile already exist"))))
                                            //checks if father uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(teacherFatherDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks countries uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> {

                                                                                                                        //getting List of Contact No. From Front
                                                                                                                        List<String> teacherFatherContactList = value.get("teacherFatherContactNoDto");
                                                                                                                        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                        teacherFatherContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!teacherFatherContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("father")
                                                                                                                                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherFatherUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing teacher Father Contact No Entity
                                                                                                                                                for (TeacherContactNoEntity teacherContact : existingContactList) {
                                                                                                                                                    teacherContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                    teacherContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                    teacherContact.setReqDeletedIP(reqIp);
                                                                                                                                                    teacherContact.setReqDeletedPort(reqPort);
                                                                                                                                                    teacherContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                    teacherContact.setReqDeletedOS(reqOs);
                                                                                                                                                    teacherContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                    teacherContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                }

                                                                                                                                                //Creating an Object Node to Read Values from Front
                                                                                                                                                JsonNode contactNode = null;
                                                                                                                                                try {
                                                                                                                                                    contactNode = new ObjectMapper().readTree(teacherFatherContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<TeacherContactNoEntity> teacherFatherContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedTeacherMetaUUID = null;

                                                                                                                                                for (JsonNode fatherContact : contactNode) {

                                                                                                                                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(fatherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(fatherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .teacherMetaUUID(teacherFatherUUID)
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

                                                                                                                                                    teacherFatherContactNoList.add(teacherContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                    updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Teacher Father Contact No List
                                                                                                                                                teacherFatherContactNoList = teacherFatherContactNoList.stream()
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


                                                                                                                                                UUID finalTeacherMetaUUID = updatedTeacherMetaUUID;

                                                                                                                                                List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                List<TeacherContactNoEntity> finalTeacherFatherContactNoList1 = teacherFatherContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Teacher Father and Contact Type
                                                                                                                                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot
                                                                                                                                                                                    (finalContactNoList, finalContactTypeUUIDList,contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherFatherProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(teacherFatherProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherFatherContactNoList1)
                                                                                                                                                                                            .collectList()
                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                    TeacherContactNoDto teacherFatherContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                            .build();

                                                                                                                                                                                                    teacherContactNoDto.add(teacherFatherContactNoDto);
                                                                                                                                                                                                }

                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherFatherEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                                                .flatMap(teacherFatherFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherFatherFacadeDto))
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
                                                                                                                            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherFatherUUID)
                                                                                                                                    .collectList()
                                                                                                                                    .flatMap(previousStdContactList -> {

                                                                                                                                        for (TeacherContactNoEntity teacherContact : previousStdContactList) {
                                                                                                                                            teacherContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                            teacherContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                            teacherContact.setReqDeletedIP(reqIp);
                                                                                                                                            teacherContact.setReqDeletedPort(reqPort);
                                                                                                                                            teacherContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                            teacherContact.setReqDeletedOS(reqOs);
                                                                                                                                            teacherContact.setReqDeletedDevice(reqDevice);
                                                                                                                                            teacherContact.setReqDeletedReferer(reqReferer);
                                                                                                                                        }

                                                                                                                                        return teacherContactNoRepository.saveAll(previousStdContactList)
                                                                                                                                                .collectList()
                                                                                                                                                .flatMap(teacherContactList -> teacherFatherProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(teacherFatherProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(teacherFatherProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherFatherEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                        .flatMap(teacherFatherFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherFatherFacadeDto))
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
                                                            )).switchIfEmpty(responseInfoMsg("Unable to upload the image"))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to upload the image. Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Father Profile Against the entered Teacher Father Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Father Profile Against the entered Teacher Father Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Father Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Father Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-father-teacher-father-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherFatherUUID = UUID.fromString((serverRequest.pathVariable("teacherFatherUUID")));
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

        return teacherFatherRepository.findByUuidAndDeletedAtIsNull(teacherFatherUUID)
                .flatMap(teacherFatherEntity -> teacherFatherProfileRepository.findFirstByTeacherFatherUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                        .flatMap(teacherFatherProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherFatherEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherFatherEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherFatherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherFatherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherFatherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherFatherEntity.setReqDeletedIP(reqIp);
                                    teacherFatherEntity.setReqDeletedPort(reqPort);
                                    teacherFatherEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherFatherEntity.setReqDeletedOS(reqOs);
                                    teacherFatherEntity.setReqDeletedDevice(reqDevice);
                                    teacherFatherEntity.setReqDeletedReferer(reqReferer);

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
                                        TeacherContactNoDto teacherFatherContactNoDto = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherFatherContactNoDto);
                                    }

                                    return teacherFatherRepository.save(teacherFatherEntity)
                                            .then(teacherFatherProfileRepository.save(teacherFatherProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherFatherEntity, teacherFatherProfileEntity, teacherContactNoDto)
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
