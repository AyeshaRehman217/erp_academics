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
import tuf.webscaf.app.dbContext.master.dto.TeacherMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherMotherTeacherMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherMotherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherMotherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherMotherProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherMotherRepository;
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

@Tag(name = "teacherMotherTeacherMotherProfileContactNoFacade")
@Component
public class TeacherMotherTeacherMotherProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    SlaveTeacherMotherRepository slaveTeacherMotherRepository;

    @Autowired
    SlaveTeacherMotherProfileRepository slaveTeacherMotherProfileRepository;

    @Autowired
    TeacherMotherProfileRepository teacherMotherProfileRepository;

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

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-mother-teacher-mother-profile-contact-nos_index")
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
            Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> slaveTeacherTeacherProfileContactNoFacadeDtoFlux = slaveTeacherMotherRepository
                    .indexTeacherMotherAndTeacherMotherProfileAndContactNoWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherTeacherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherProfileEntity -> slaveTeacherMotherRepository
                            .countTeacherMotherTeacherMotherProfileContactNoWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> slaveTeacherTeacherProfileContactNoFacadeDtoFlux = slaveTeacherMotherRepository
                    .indexTeacherMotherAndTeacherMotherProfileAndContactNoWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherTeacherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherProfileEntity -> slaveTeacherMotherRepository
                            .countTeacherMotherTeacherMotherProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-mother-teacher-mother-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherMotherUUID = UUID.fromString((serverRequest.pathVariable("teacherMotherUUID")));

        return slaveTeacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                .flatMap(teacherMotherEntity -> slaveTeacherMotherProfileRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                        .flatMap(teacherMotherProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherMotherContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherMotherContactNoDto);
                                    }

                                    return showFacadeDto(teacherMotherEntity, teacherMotherProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Mother Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Mother Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Mother Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Mother Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherMotherEntity slaveTeacherMotherEntity, SlaveTeacherMotherProfileEntity slaveTeacherMotherProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoFacadeDto) {

        SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto facadeDto = SlaveTeacherMotherTeacherMotherProfileContactNoFacadeDto.builder()
                .id(slaveTeacherMotherEntity.getId())
                .uuid(slaveTeacherMotherEntity.getUuid())
                .version(slaveTeacherMotherEntity.getVersion())
                .status(slaveTeacherMotherEntity.getStatus())
                .teacherUUID(slaveTeacherMotherEntity.getTeacherUUID())
                .teacherMotherUUID(slaveTeacherMotherEntity.getUuid())
                .image(slaveTeacherMotherProfileEntity.getImage())
                .name(slaveTeacherMotherProfileEntity.getName())
                .nic(slaveTeacherMotherProfileEntity.getNic())
                .age(slaveTeacherMotherProfileEntity.getAge())
                .officialTel(slaveTeacherMotherProfileEntity.getOfficialTel())
                .cityUUID(slaveTeacherMotherProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherMotherProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherMotherProfileEntity.getCountryUUID())
                .noOfDependents(slaveTeacherMotherProfileEntity.getNoOfDependents())
                .email(slaveTeacherMotherProfileEntity.getEmail())
                .createdAt(slaveTeacherMotherEntity.getCreatedAt())
                .createdBy(slaveTeacherMotherEntity.getCreatedBy())
                .teacherContactNoDto(slaveTeacherContactNoFacadeDto)
                .reqCompanyUUID(slaveTeacherMotherEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherMotherEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherMotherEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherMotherEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherMotherEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherMotherEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherMotherEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherMotherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherMotherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherMotherEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherMotherEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherMotherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherMotherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherMotherEntity.getReqUpdatedReferer())
                .editable(slaveTeacherMotherEntity.getEditable())
                .deletable(slaveTeacherMotherEntity.getDeletable())
                .archived(slaveTeacherMotherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }


    public Mono<TeacherMotherTeacherMotherProfileContactNoFacadeDto> facadeDto(TeacherMotherEntity teacherMotherEntity, TeacherMotherProfileEntity teacherMotherProfileEntity, List<TeacherContactNoDto> teacherMotherContactNoDto) {

        TeacherMotherTeacherMotherProfileContactNoFacadeDto facadeDto = TeacherMotherTeacherMotherProfileContactNoFacadeDto.builder()
                .id(teacherMotherEntity.getId())
                .uuid(teacherMotherEntity.getUuid())
                .version(teacherMotherEntity.getVersion())
                .status(teacherMotherEntity.getStatus())
                .teacherUUID(teacherMotherEntity.getTeacherUUID())
                .teacherMotherUUID(teacherMotherEntity.getUuid())
                .image(teacherMotherProfileEntity.getImage())
                .name(teacherMotherProfileEntity.getName())
                .nic(teacherMotherProfileEntity.getNic())
                .age(teacherMotherProfileEntity.getAge())
                .officialTel(teacherMotherProfileEntity.getOfficialTel())
                .cityUUID(teacherMotherProfileEntity.getCityUUID())
                .stateUUID(teacherMotherProfileEntity.getStateUUID())
                .countryUUID(teacherMotherProfileEntity.getCountryUUID())
                .noOfDependents(teacherMotherProfileEntity.getNoOfDependents())
                .email(teacherMotherProfileEntity.getEmail())
                .createdAt(teacherMotherEntity.getCreatedAt())
                .createdBy(teacherMotherEntity.getCreatedBy())
                .teacherMotherContactNoDto(teacherMotherContactNoDto)
                .reqCompanyUUID(teacherMotherEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherMotherEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherMotherEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherMotherEntity.getReqCreatedIP())
                .reqCreatedPort(teacherMotherEntity.getReqCreatedPort())
                .reqCreatedOS(teacherMotherEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherMotherEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherMotherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherMotherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherMotherEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherMotherEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherMotherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherMotherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherMotherEntity.getReqUpdatedReferer())
                .editable(teacherMotherEntity.getEditable())
                .deletable(teacherMotherEntity.getDeletable())
                .archived(teacherMotherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherMotherProfileContactNoFacadeDto> updatedFacadeDto(TeacherMotherEntity teacherMotherEntity, TeacherMotherProfileEntity teacherMotherProfileEntity, List<TeacherContactNoDto> teacherMotherContactNoDto) {

        TeacherMotherProfileContactNoFacadeDto facadeDto = TeacherMotherProfileContactNoFacadeDto.builder()
                .id(teacherMotherEntity.getId())
                .uuid(teacherMotherEntity.getUuid())
                .version(teacherMotherEntity.getVersion())
                .status(teacherMotherEntity.getStatus())
                .image(teacherMotherProfileEntity.getImage())
                .name(teacherMotherProfileEntity.getName())
                .nic(teacherMotherProfileEntity.getNic())
                .age(teacherMotherProfileEntity.getAge())
                .officialTel(teacherMotherProfileEntity.getOfficialTel())
                .cityUUID(teacherMotherProfileEntity.getCityUUID())
                .stateUUID(teacherMotherProfileEntity.getStateUUID())
                .countryUUID(teacherMotherProfileEntity.getCountryUUID())
                .noOfDependents(teacherMotherProfileEntity.getNoOfDependents())
                .email(teacherMotherProfileEntity.getEmail())
                .teacherMotherContactNoDto(teacherMotherContactNoDto)
                .updatedAt(teacherMotherEntity.getUpdatedAt())
                .updatedBy(teacherMotherEntity.getUpdatedBy())
                .reqCompanyUUID(teacherMotherProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherMotherProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherMotherProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherMotherProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherMotherProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherMotherProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherMotherProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherMotherProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherMotherProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherMotherProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherMotherProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherMotherProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherMotherProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherMotherProfileEntity.getReqUpdatedReferer())
                .editable(teacherMotherProfileEntity.getEditable())
                .deletable(teacherMotherProfileEntity.getDeletable())
                .archived(teacherMotherProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-mother-teacher-mother-profile-contact-nos_store")
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

                    TeacherMotherEntity teacherMotherEntity = TeacherMotherEntity.builder()
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
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherMotherEntity.getTeacherUUID())
                            //check if Teacher Mother Record Already Exists Against the same teacher
                            .flatMap(teacherEntity -> teacherMotherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                            .flatMap(checkMsg -> responseInfoMsg("Teacher Mother Record Against the Entered Teacher Already Exist."))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                //Building Teacher Mother Profile Record
                                                TeacherMotherProfileEntity teacherMotherProfileEntity = TeacherMotherProfileEntity
                                                        .builder()
                                                        .uuid(UUID.randomUUID())
                                                        .teacherMotherUUID(teacherMotherEntity.getUuid())
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

                                                sendFormData.add("docId", String.valueOf(teacherMotherProfileEntity.getImage()));

                                                //check if Teacher Mother Record Exists or Not
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherMotherProfileEntity.getCityUUID())
                                                        .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                //check if State Record Exists or not
                                                                .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherMotherProfileEntity.getStateUUID())
                                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                        //check if Country Record Exists or not
                                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherMotherProfileEntity.getCountryUUID())
                                                                                                        .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                //check if NIC Is Unique Against Teacher Mother
                                                                                                                .flatMap(checkNIC -> teacherMotherProfileRepository.findFirstByNicAndTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherProfileEntity.getNic(), teacherMotherProfileEntity.getTeacherMotherUUID())
                                                                                                                        .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                                                //check if Mother Profile Already Exists Against Teacher Mother
                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherMotherProfileRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherProfileEntity.getTeacherMotherUUID())
                                                                                                                        .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Mother Profile already exist"))))
                                                                                                                //check if Document Record Exists or not
                                                                                                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherMotherProfileEntity.getImage())
                                                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                //check if Contact Category is Mother
                                                                                                                                                .flatMap(documentEntity -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("mother")
                                                                                                                                                                .flatMap(contactCategoryEntity -> {

                                                                                                                                                                    //getting List of Contact No. From Front
                                                                                                                                                                    List<String> teacherMotherContactList = value.get("teacherMotherContactNoDto");
                                                                                                                                                                    //Creating an empty list to add teacher Contact No Records
                                                                                                                                                                    List<TeacherContactNoEntity> teacherMotherContactNoList = new ArrayList<>();

                                                                                                                                                                    // Creating an empty list to add contact Type UUID's
                                                                                                                                                                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                    // Creating an empty list to add contact No's
                                                                                                                                                                    List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                    JsonNode contactNode = null;
                                                                                                                                                                    ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                    try {
                                                                                                                                                                        contactNode = objectMapper.readTree(teacherMotherContactList.toString());
                                                                                                                                                                    } catch (JsonProcessingException e) {
                                                                                                                                                                        e.printStackTrace();
                                                                                                                                                                    }
                                                                                                                                                                    assert contactNode != null;


                                                                                                                                                                    UUID teacherMetaUUID = null;
                                                                                                                                                                    UUID contactCategoryUUID = null;

                                                                                                                                                                    //iterating over the json node from front and setting contact No's
                                                                                                                                                                    for (JsonNode motherContact : contactNode) {

                                                                                                                                                                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                                .builder()
                                                                                                                                                                                .contactTypeUUID(UUID.fromString(motherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                .contactNo(motherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                .teacherMetaUUID(teacherMotherEntity.getUuid())
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

                                                                                                                                                                        teacherMotherContactNoList.add(teacherContactNoEntity);

                                                                                                                                                                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                                                                                                                                                                        contactNoList.add(teacherContactNoEntity.getContactNo());
                                                                                                                                                                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                    }

                                                                                                                                                                    //Getting Distinct Values Fom the List of Teacher Mother Contact No List
                                                                                                                                                                    teacherMotherContactNoList = teacherMotherContactNoList.stream()
                                                                                                                                                                            .distinct()
                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                    //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                    contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                            .distinct()
                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                    // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                                                    if (!teacherMotherContactNoList.isEmpty()) {

                                                                                                                                                                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                                                                                                                                                                        UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherMotherContactNoList = teacherMotherContactNoList;

                                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                .collectList()
                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                        } else {
                                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher Mother and Contact Type
                                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherMotherRepository.save(teacherMotherEntity)
                                                                                                                                                                                                            .then(teacherMotherProfileRepository.save(teacherMotherProfileEntity))
                                                                                                                                                                                                            .then(teacherContactNoRepository.saveAll(finalTeacherMotherContactNoList)
                                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                                            .flatMap(mthContactNo -> {

                                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : mthContactNo) {
                                                                                                                                                                                                                    TeacherContactNoDto teacherMotherContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                            .build();

                                                                                                                                                                                                                    teacherContactNoDto.add(teacherMotherContactNoDto);
                                                                                                                                                                                                                }

                                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherMotherEntity, teacherMotherProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                                                .flatMap(teacherMotherFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherMotherFacadeDto))
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
                                                                                                                                                                        //if Contact No List is empty then store teacher Mother and Teacher Mother Profile
                                                                                                                                                                        return teacherMotherRepository.save(teacherMotherEntity)
                                                                                                                                                                                //Save Teacher Mother Profile Entity
                                                                                                                                                                                .then(teacherMotherProfileRepository.save(teacherMotherProfileEntity))
                                                                                                                                                                                //update Document Status After Storing record
                                                                                                                                                                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherMotherEntity, teacherMotherProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                .flatMap(teacherMotherFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherMotherFacadeDto))
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

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-mother-teacher-mother-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherMotherUUID = UUID.fromString((serverRequest.pathVariable("teacherMotherUUID")));
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
                .flatMap(value -> teacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                        .flatMap(teacherMotherEntity -> teacherMotherProfileRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherMotherProfileEntity updatedEntity = TeacherMotherProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .teacherMotherUUID(previousProfileEntity.getTeacherMotherUUID())
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
                                    return teacherMotherProfileRepository.findFirstByNicAndTeacherMotherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getTeacherMotherUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check mother profile is unique
                                            .switchIfEmpty(Mono.defer(() -> teacherMotherProfileRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherMotherUUID(), updatedEntity.getUuid())
                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Mother Profile already exist"))))
                                            //checks if mother uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(teacherMotherDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks countries uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> {

                                                                                                                        //getting List of Contact No. From Front
                                                                                                                        List<String> teacherMotherContactList = value.get("teacherMotherContactNoDto");
                                                                                                                        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                        teacherMotherContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!teacherMotherContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("mother")
                                                                                                                                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherMotherUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing Teacher Mother Contact No Entity
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
                                                                                                                                                    contactNode = new ObjectMapper().readTree(teacherMotherContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<TeacherContactNoEntity> teacherMotherContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedTeacherMetaUUID = null;

                                                                                                                                                for (JsonNode motherContact : contactNode) {

                                                                                                                                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(motherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(motherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .teacherMetaUUID(teacherMotherUUID)
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

                                                                                                                                                    teacherMotherContactNoList.add(teacherContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                    updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Teacher Mother Contact No List
                                                                                                                                                teacherMotherContactNoList = teacherMotherContactNoList.stream()
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

                                                                                                                                                List<TeacherContactNoEntity> finalTeacherMotherContactNoList1 = teacherMotherContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Teacher Mother and Contact Type
                                                                                                                                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherMotherProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(teacherMotherProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherMotherContactNoList1)
                                                                                                                                                                                            .collectList()
                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                    TeacherContactNoDto teacherMotherContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                            .build();

                                                                                                                                                                                                    teacherContactNoDto.add(teacherMotherContactNoDto);
                                                                                                                                                                                                }

                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherMotherEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                                                .flatMap(teacherMotherFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherMotherFacadeDto))
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
                                                                                                                            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherMotherUUID)
                                                                                                                                    .collectList()
                                                                                                                                    .flatMap(previousTeacherContactList -> {

                                                                                                                                        for (TeacherContactNoEntity teacherContact : previousTeacherContactList) {
                                                                                                                                            teacherContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                            teacherContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                            teacherContact.setReqDeletedIP(reqIp);
                                                                                                                                            teacherContact.setReqDeletedPort(reqPort);
                                                                                                                                            teacherContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                            teacherContact.setReqDeletedOS(reqOs);
                                                                                                                                            teacherContact.setReqDeletedDevice(reqDevice);
                                                                                                                                            teacherContact.setReqDeletedReferer(reqReferer);
                                                                                                                                        }

                                                                                                                                        return teacherContactNoRepository.saveAll(previousTeacherContactList)
                                                                                                                                                .collectList()
                                                                                                                                                .flatMap(teacherContactList -> teacherMotherProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(teacherMotherProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(teacherMotherProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherMotherEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                        .flatMap(teacherMotherFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherMotherFacadeDto))
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
                                }).switchIfEmpty(responseInfoMsg("Mother Profile Against the entered Teacher Mother Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Mother Profile Against the entered Teacher Mother Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Mother Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Mother Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-mother-teacher-mother-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherMotherUUID = UUID.fromString((serverRequest.pathVariable("teacherMotherUUID")));
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

        return teacherMotherRepository.findByUuidAndDeletedAtIsNull(teacherMotherUUID)
                .flatMap(teacherMotherEntity -> teacherMotherProfileRepository.findFirstByTeacherMotherUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                        .flatMap(teacherMotherProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherMotherEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherMotherEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherMotherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherMotherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherMotherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherMotherEntity.setReqDeletedIP(reqIp);
                                    teacherMotherEntity.setReqDeletedPort(reqPort);
                                    teacherMotherEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherMotherEntity.setReqDeletedOS(reqOs);
                                    teacherMotherEntity.setReqDeletedDevice(reqDevice);
                                    teacherMotherEntity.setReqDeletedReferer(reqReferer);

                                    teacherMotherProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherMotherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherMotherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherMotherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherMotherProfileEntity.setReqDeletedIP(reqIp);
                                    teacherMotherProfileEntity.setReqDeletedPort(reqPort);
                                    teacherMotherProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherMotherProfileEntity.setReqDeletedOS(reqOs);
                                    teacherMotherProfileEntity.setReqDeletedDevice(reqDevice);
                                    teacherMotherProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        TeacherContactNoDto teacherMotherContactNoDto = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherMotherContactNoDto);
                                    }

                                    return teacherMotherRepository.save(teacherMotherEntity)
                                            .then(teacherMotherProfileRepository.save(teacherMotherProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherMotherEntity, teacherMotherProfileEntity, teacherContactNoDto)
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
