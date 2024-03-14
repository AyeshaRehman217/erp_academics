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
import tuf.webscaf.app.dbContext.master.dto.TeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherGuardianTeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherGuardianProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherGuardianProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherGuardianRepository;
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

@Tag(name = "teacherGuardianTeacherGuardianProfileContactNoFacade")
@Component
public class TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    SlaveTeacherGuardianRepository slaveTeacherGuardianRepository;

    @Autowired
    SlaveTeacherGuardianProfileRepository slaveTeacherGuardianProfileRepository;

    @Autowired
    TeacherGuardianProfileRepository teacherGuardianProfileRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    GuardianTypeRepository guardianTypeRepository;

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

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-guardian-teacher-guardian-profile-contact-nos_index")
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
            Flux<SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto> slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDtoFlux = slaveTeacherGuardianRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherGuardianProfileEntity -> slaveTeacherGuardianRepository
                            .countTeacherGuardianTeacherGuardianProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherGuardianProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto> slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDtoFlux = slaveTeacherGuardianRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherGuardianProfileEntity -> slaveTeacherGuardianRepository
                            .countTeacherGuardianTeacherGuardianProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherGuardianProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherGuardianProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-guardian-teacher-guardian-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherGuardianUUID = UUID.fromString((serverRequest.pathVariable("teacherGuardianUUID")));

        return slaveTeacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                .flatMap(teacherGuardianEntity -> slaveTeacherGuardianProfileRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                        .flatMap(teacherGuardianProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherGuardianContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherGuardianContactNoDto);
                                    }

                                    return showFacadeDto(teacherGuardianEntity, teacherGuardianProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Guardian Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Guardian Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Guardian Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Guardian Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherGuardianEntity slaveTeacherGuardianEntity, SlaveTeacherGuardianProfileEntity slaveTeacherGuardianProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoFacadeDto) {

        SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto facadeDto = SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto.builder()
                .id(slaveTeacherGuardianEntity.getId())
                .uuid(slaveTeacherGuardianEntity.getUuid())
                .version(slaveTeacherGuardianEntity.getVersion())
                .status(slaveTeacherGuardianEntity.getStatus())
                .guardianTypeUUID(slaveTeacherGuardianEntity.getGuardianTypeUUID())
                .guardianUUID(slaveTeacherGuardianEntity.getGuardianUUID())
                .teacherGuardianUUID(slaveTeacherGuardianEntity.getUuid())
                .description(slaveTeacherGuardianProfileEntity.getDescription())
                .relation(slaveTeacherGuardianProfileEntity.getRelation())
                .genderUUID(slaveTeacherGuardianProfileEntity.getGenderUUID())
                .teacherUUID(slaveTeacherGuardianEntity.getTeacherUUID())
                .image(slaveTeacherGuardianProfileEntity.getImage())
                .name(slaveTeacherGuardianProfileEntity.getName())
                .nic(slaveTeacherGuardianProfileEntity.getNic())
                .age(slaveTeacherGuardianProfileEntity.getAge())
                .officialTel(slaveTeacherGuardianProfileEntity.getOfficialTel())
                .cityUUID(slaveTeacherGuardianProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherGuardianProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherGuardianProfileEntity.getCountryUUID())
                .noOfDependents(slaveTeacherGuardianProfileEntity.getNoOfDependents())
                .email(slaveTeacherGuardianProfileEntity.getEmail())
                .teacherGuardianContactNoDto(slaveTeacherContactNoFacadeDto)
                .createdAt(slaveTeacherGuardianEntity.getCreatedAt())
                .createdBy(slaveTeacherGuardianEntity.getCreatedBy())
                .reqCompanyUUID(slaveTeacherGuardianEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherGuardianEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherGuardianEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherGuardianEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherGuardianEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherGuardianEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherGuardianEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherGuardianEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherGuardianEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherGuardianEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherGuardianEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherGuardianEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherGuardianEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherGuardianEntity.getReqUpdatedReferer())
                .editable(slaveTeacherGuardianEntity.getEditable())
                .deletable(slaveTeacherGuardianEntity.getDeletable())
                .archived(slaveTeacherGuardianEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherGuardianTeacherGuardianProfileContactNoFacadeDto> facadeDto(TeacherGuardianEntity teacherGuardianEntity, TeacherGuardianProfileEntity teacherGuardianProfileEntity, List<TeacherContactNoDto> teacherContactNoDto) {

        TeacherGuardianTeacherGuardianProfileContactNoFacadeDto facadeDto = TeacherGuardianTeacherGuardianProfileContactNoFacadeDto.builder()
                .id(teacherGuardianEntity.getId())
                .uuid(teacherGuardianEntity.getUuid())
                .version(teacherGuardianEntity.getVersion())
                .status(teacherGuardianEntity.getStatus())
                .teacherUUID(teacherGuardianEntity.getTeacherUUID())
                .guardianTypeUUID(teacherGuardianEntity.getGuardianTypeUUID())
                .guardianUUID(teacherGuardianEntity.getGuardianUUID())
                .teacherGuardianUUID(teacherGuardianEntity.getUuid())
                .description(teacherGuardianProfileEntity.getDescription())
                .genderUUID(teacherGuardianProfileEntity.getGenderUUID())
                .image(teacherGuardianProfileEntity.getImage())
                .name(teacherGuardianProfileEntity.getName())
                .nic(teacherGuardianProfileEntity.getNic())
                .age(teacherGuardianProfileEntity.getAge())
                .relation(teacherGuardianProfileEntity.getRelation())
                .genderUUID(teacherGuardianProfileEntity.getGenderUUID())
                .officialTel(teacherGuardianProfileEntity.getOfficialTel())
                .cityUUID(teacherGuardianProfileEntity.getCityUUID())
                .stateUUID(teacherGuardianProfileEntity.getStateUUID())
                .countryUUID(teacherGuardianProfileEntity.getCountryUUID())
                .noOfDependents(teacherGuardianProfileEntity.getNoOfDependents())
                .email(teacherGuardianProfileEntity.getEmail())
                .teacherGuardianContactNoDto(teacherContactNoDto)
                .createdAt(teacherGuardianEntity.getCreatedAt())
                .createdBy(teacherGuardianEntity.getCreatedBy())
                .reqCompanyUUID(teacherGuardianEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherGuardianEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherGuardianEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherGuardianEntity.getReqCreatedIP())
                .reqCreatedPort(teacherGuardianEntity.getReqCreatedPort())
                .reqCreatedOS(teacherGuardianEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherGuardianEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherGuardianEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherGuardianEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherGuardianEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherGuardianEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherGuardianEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherGuardianEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherGuardianEntity.getReqUpdatedReferer())
                .editable(teacherGuardianEntity.getEditable())
                .deletable(teacherGuardianEntity.getDeletable())
                .archived(teacherGuardianEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherGuardianProfileContactNoFacadeDto> updatedFacadeDto(TeacherGuardianEntity teacherGuardianEntity, TeacherGuardianProfileEntity teacherGuardianProfileEntity, List<TeacherContactNoDto> teacherContactNoDto) {

        TeacherGuardianProfileContactNoFacadeDto facadeDto = TeacherGuardianProfileContactNoFacadeDto.builder()
                .id(teacherGuardianEntity.getId())
                .uuid(teacherGuardianEntity.getUuid())
                .version(teacherGuardianEntity.getVersion())
                .status(teacherGuardianEntity.getStatus())
                .image(teacherGuardianProfileEntity.getImage())
                .name(teacherGuardianProfileEntity.getName())
                .nic(teacherGuardianProfileEntity.getNic())
                .age(teacherGuardianProfileEntity.getAge())
                .relation(teacherGuardianProfileEntity.getRelation())
                .description(teacherGuardianProfileEntity.getDescription())
                .genderUUID(teacherGuardianProfileEntity.getGenderUUID())
                .officialTel(teacherGuardianProfileEntity.getOfficialTel())
                .cityUUID(teacherGuardianProfileEntity.getCityUUID())
                .stateUUID(teacherGuardianProfileEntity.getStateUUID())
                .countryUUID(teacherGuardianProfileEntity.getCountryUUID())
                .noOfDependents(teacherGuardianProfileEntity.getNoOfDependents())
                .email(teacherGuardianProfileEntity.getEmail())
                .teacherGuardianContactNoDto(teacherContactNoDto)
                .updatedAt(teacherGuardianEntity.getUpdatedAt())
                .updatedBy(teacherGuardianEntity.getUpdatedBy())
                .reqCompanyUUID(teacherGuardianProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherGuardianProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherGuardianProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherGuardianProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherGuardianProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherGuardianProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherGuardianProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherGuardianProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherGuardianProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherGuardianProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherGuardianProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherGuardianProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherGuardianProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherGuardianProfileEntity.getReqUpdatedReferer())
                .editable(teacherGuardianProfileEntity.getEditable())
                .deletable(teacherGuardianProfileEntity.getDeletable())
                .archived(teacherGuardianProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-guardian-teacher-guardian-profile-contact-nos_store")
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

                    TeacherGuardianEntity teacherGuardianEntity = TeacherGuardianEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
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

                    //check if Teacher Record exists or not
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherGuardianEntity.getTeacherUUID())
                            //check if Teacher Guardian Record Already Exists Against the same teacher
                            .flatMap(teacherEntity -> teacherGuardianRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                            .flatMap(checkMsg -> responseInfoMsg("Teacher Guardian Record Against the Entered Teacher Already Exist."))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                //Building Teacher Guardian Profile Record
                                                TeacherGuardianProfileEntity teacherGuardianProfileEntity = TeacherGuardianProfileEntity
                                                        .builder()
                                                        .uuid(UUID.randomUUID())
                                                        .teacherGuardianUUID(teacherGuardianEntity.getUuid())
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

                                                sendFormData.add("docId", String.valueOf(teacherGuardianProfileEntity.getImage()));

                                                //check if City Record Exists or not
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherGuardianProfileEntity.getCityUUID())
                                                        .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                //check if State Record Exists or not
                                                                .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherGuardianProfileEntity.getStateUUID())
                                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                        //check if Country Record Exists or not
                                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherGuardianProfileEntity.getCountryUUID())
                                                                                                        .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                //check if Gender Record Exists or not
                                                                                                                .flatMap(countryUUID -> genderRepository.findByUuidAndDeletedAtIsNull(teacherGuardianProfileEntity.getGenderUUID())
                                                                                                                                //check if NIC Is Unique Against Teacher Guardian
                                                                                                                                .flatMap(checkNIC -> teacherGuardianProfileRepository.findFirstByNicAndTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianProfileEntity.getNic(), teacherGuardianProfileEntity.getTeacherGuardianUUID())
                                                                                                                                        .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                                                                //check if Guardian Profile Already Exists Against Teacher Guardian
                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherGuardianProfileRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianProfileEntity.getTeacherGuardianUUID())
                                                                                                                                        .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Guardian Profile already exist"))))
                                                                                                                                //check if Document Record Exists or not
                                                                                                                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherGuardianProfileEntity.getImage())
                                                                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                                //check if Contact Category is Guardian
                                                                                                                                                                .flatMap(documentEntity -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("guardian")
                                                                                                                                                                                .flatMap(contactCategoryEntity -> {

                                                                                                                                                                                    //getting List of Contact No. From Front
                                                                                                                                                                                    List<String> teacherGuardianContactList = value.get("teacherGuardianContactNoDto");
                                                                                                                                                                                    //Creating an empty list to add teacher Contact No Records
                                                                                                                                                                                    List<TeacherContactNoEntity> teacherGuardianContactNoList = new ArrayList<>();

                                                                                                                                                                                    // Creating an empty list to add contact Type UUID's
                                                                                                                                                                                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                                    // Creating an empty list to add contact No's
                                                                                                                                                                                    List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                                    JsonNode contactNode = null;
                                                                                                                                                                                    ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                                    try {
                                                                                                                                                                                        contactNode = objectMapper.readTree(teacherGuardianContactList.toString());
                                                                                                                                                                                    } catch (JsonProcessingException e) {
                                                                                                                                                                                        e.printStackTrace();
                                                                                                                                                                                    }
                                                                                                                                                                                    assert contactNode != null;


                                                                                                                                                                                    UUID teacherMetaUUID = null;
                                                                                                                                                                                    UUID contactCategoryUUID = null;

                                                                                                                                                                                    //iterating over the json node from front and setting contact No's
                                                                                                                                                                                    for (JsonNode guardianContact : contactNode) {

                                                                                                                                                                                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                                                .builder()
                                                                                                                                                                                                .contactTypeUUID(UUID.fromString(guardianContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                                .contactNo(guardianContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                                .teacherMetaUUID(teacherGuardianEntity.getUuid())
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

                                                                                                                                                                                        teacherGuardianContactNoList.add(teacherContactNoEntity);

                                                                                                                                                                                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                                                                                                                                                                                        contactNoList.add(teacherContactNoEntity.getContactNo());
                                                                                                                                                                                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                                                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                                    }

                                                                                                                                                                                    //Getting Distinct Values Fom the List of Teacher Guardian Contact No List
                                                                                                                                                                                    teacherGuardianContactNoList = teacherGuardianContactNoList.stream()
                                                                                                                                                                                            .distinct()
                                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                                    //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                                    contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                                            .distinct()
                                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                                    // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                                                                    if (!teacherGuardianContactNoList.isEmpty()) {

                                                                                                                                                                                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                                                                                                                                                                                        UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherGuardianContactNoList = teacherGuardianContactNoList;

                                                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                                .collectList()
                                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                                        } else {
                                                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher Guardian and Contact Type
                                                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> guardianTypeRepository.findByUuidAndDeletedAtIsNull(teacherGuardianEntity.getGuardianTypeUUID())
                                                                                                                                                                                                                            .flatMap(guardianTypeEntity -> {

                                                                                                                                                                                                                                // if guardian uuid is specified in the request
                                                                                                                                                                                                                                if (teacherGuardianEntity.getGuardianUUID() != null) {

                                                                                                                                                                                                                                    // if teacher father is guardian
                                                                                                                                                                                                                                    switch (guardianTypeEntity.getSlug()) {
                                                                                                                                                                                                                                        case "father":
                                                                                                                                                                                                                                            return teacherFatherRepository.findByUuidAndTeacherUUIDAndDeletedAtIsNull(teacherGuardianEntity.getGuardianUUID(), teacherGuardianEntity.getTeacherUUID())
                                                                                                                                                                                                                                                    .flatMap(teacherFatherEntity -> teacherGuardianRepository.save(teacherGuardianEntity)
                                                                                                                                                                                                                                                            .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
                                                                                                                                                                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                                                                                                                                                                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."))
                                                                                                                                                                                                                                                    ).switchIfEmpty(responseInfoMsg("Guardian is not valid."))
                                                                                                                                                                                                                                                    .onErrorResume(err -> responseErrorMsg("Guardian is not valid. Please contact developer."));

                                                                                                                                                                                                                                        // if teacher mother is guardian
                                                                                                                                                                                                                                        case "mother":
                                                                                                                                                                                                                                            return teacherMotherRepository.findByUuidAndTeacherUUIDAndDeletedAtIsNull(teacherGuardianEntity.getGuardianUUID(), teacherGuardianEntity.getTeacherUUID())
                                                                                                                                                                                                                                                    .flatMap(teacherMotherEntity -> teacherGuardianRepository.save(teacherGuardianEntity)
                                                                                                                                                                                                                                                            .flatMap(teacherGuardianEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherGuardianEntityDB))
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
                                                                                                                                                                                                                                        return teacherGuardianRepository.save(teacherGuardianEntity)
                                                                                                                                                                                                                                                .then(teacherGuardianProfileRepository.save(teacherGuardianProfileEntity))
                                                                                                                                                                                                                                                .then(teacherContactNoRepository.saveAll(finalTeacherGuardianContactNoList)
                                                                                                                                                                                                                                                        .collectList())
                                                                                                                                                                                                                                                .flatMap(teacherContactNoEntities -> {
                                                                                                                                                                                                                                                    for (TeacherContactNoEntity teacherContact : teacherContactNoEntities) {
                                                                                                                                                                                                                                                        TeacherContactNoDto teacherGuardianContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                                                                                .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                                                                .build();

                                                                                                                                                                                                                                                        teacherContactNoDto.add(teacherGuardianContactNoDto);
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                    return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                                            .flatMap(docUpdate -> facadeDto(teacherGuardianEntity, teacherGuardianProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                                                                                    .flatMap(teacherGuardianFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherGuardianFacadeDto))
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
                                                                                                                                                                                        //if Contact No List is empty then store teacher Guardian and Teacher Guardian Profile
                                                                                                                                                                                        return teacherGuardianRepository.save(teacherGuardianEntity)
                                                                                                                                                                                                //Save Teacher Guardian Profile Entity
                                                                                                                                                                                                .then(teacherGuardianProfileRepository.save(teacherGuardianProfileEntity))
                                                                                                                                                                                                //update Document Status After Storing record
                                                                                                                                                                                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherGuardianEntity, teacherGuardianProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                                .flatMap(teacherGuardianFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherGuardianFacadeDto))
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
                            ).switchIfEmpty(responseInfoMsg("Teacher Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Teacher Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-guardian-teacher-guardian-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherGuardianUUID = UUID.fromString((serverRequest.pathVariable("teacherGuardianUUID")));
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
                .flatMap(value -> teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                        .flatMap(previousTeacherGuardianEntity -> teacherGuardianProfileRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherGuardianProfileEntity updatedEntity = TeacherGuardianProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .teacherGuardianUUID(previousProfileEntity.getTeacherGuardianUUID())
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
                                    return teacherGuardianProfileRepository.findFirstByNicAndTeacherGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getTeacherGuardianUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check guardian profile is unique
                                            .switchIfEmpty(Mono.defer(() -> teacherGuardianProfileRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherGuardianUUID(), updatedEntity.getUuid())
                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Guardian Profile already exist"))))
                                            //checks if guardian uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(teacherGuardianDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
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
                                                                                                                                List<String> teacherGuardianContactList = value.get("teacherGuardianContactNoDto");
                                                                                                                                List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                teacherGuardianContactList.removeIf(s -> s.equals(""));

                                                                                                                                if (!teacherGuardianContactList.isEmpty()) {
                                                                                                                                    return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("guardian")
                                                                                                                                            .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherGuardianUUID)
                                                                                                                                                    .collectList()
                                                                                                                                                    .flatMap(existingContactList -> {

                                                                                                                                                        //Removing Already existing teacher Guardian Contact No Entity
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
                                                                                                                                                            contactNode = new ObjectMapper().readTree(teacherGuardianContactList.toString());
                                                                                                                                                        } catch (JsonProcessingException e) {
                                                                                                                                                            e.printStackTrace();
                                                                                                                                                        }

                                                                                                                                                        //New Contact No list for adding values after building entity
                                                                                                                                                        List<TeacherContactNoEntity> teacherGuardianContactNoList = new ArrayList<>();

                                                                                                                                                        List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                        List<String> contactNoList = new ArrayList<>();

                                                                                                                                                        UUID updatedTeacherMetaUUID = null;

                                                                                                                                                        for (JsonNode guardianContact : contactNode) {

                                                                                                                                                            TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                    .builder()
                                                                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                                                                    .contactTypeUUID(UUID.fromString(guardianContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                    .contactNo(guardianContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                    .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                    .teacherMetaUUID(teacherGuardianUUID)
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

                                                                                                                                                            teacherGuardianContactNoList.add(teacherContactNoEntity);

                                                                                                                                                            contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                            contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                            updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                        }

                                                                                                                                                        //Getting Distinct Values Fom the List of Teacher Guardian Contact No List
                                                                                                                                                        teacherGuardianContactNoList = teacherGuardianContactNoList.stream()
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

                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherGuardianContactNoList1 = teacherGuardianContactNoList;

                                                                                                                                                        List<String> finalContactNoList = contactNoList;

                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                .collectList()
                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                        } else {

                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher Guardian and Contact Type
                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherGuardianProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                            .then(teacherGuardianProfileRepository.save(updatedEntity))
                                                                                                                                                                                            .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                            .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherGuardianContactNoList1)
                                                                                                                                                                                                    .collectList()
                                                                                                                                                                                                    .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                        for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                            TeacherContactNoDto teacherGuardianContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                                    .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                    .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                            teacherContactNoDto.add(teacherGuardianContactNoDto);
                                                                                                                                                                                                        }

                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                .flatMap(docUpdate -> updatedFacadeDto(previousTeacherGuardianEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                                                        .flatMap(teacherGuardianFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherGuardianFacadeDto))
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
                                                                                                                                    return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherGuardianUUID)
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
                                                                                                                                                        .flatMap(teacherContactList -> teacherGuardianProfileRepository.save(previousProfileEntity)
                                                                                                                                                                .then(teacherGuardianProfileRepository.save(updatedEntity))
                                                                                                                                                                .flatMap(teacherGuardianProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                        .flatMap(docUpdateEntity -> updatedFacadeDto(previousTeacherGuardianEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                .flatMap(teacherGuardianFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherGuardianFacadeDto))
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
                                }).switchIfEmpty(responseInfoMsg("Guardian Profile Against the entered Teacher Guardian Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Guardian Profile Against the entered Teacher Guardian Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Guardian Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Guardian Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-guardian-teacher-guardian-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherGuardianUUID = UUID.fromString((serverRequest.pathVariable("teacherGuardianUUID")));
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

        return teacherGuardianRepository.findByUuidAndDeletedAtIsNull(teacherGuardianUUID)
                .flatMap(teacherGuardianEntity -> teacherGuardianProfileRepository.findFirstByTeacherGuardianUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                        .flatMap(teacherGuardianProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherGuardianEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherGuardianEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherGuardianEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherGuardianEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherGuardianEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherGuardianEntity.setReqDeletedIP(reqIp);
                                    teacherGuardianEntity.setReqDeletedPort(reqPort);
                                    teacherGuardianEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherGuardianEntity.setReqDeletedOS(reqOs);
                                    teacherGuardianEntity.setReqDeletedDevice(reqDevice);
                                    teacherGuardianEntity.setReqDeletedReferer(reqReferer);

                                    teacherGuardianProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherGuardianProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherGuardianProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherGuardianProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherGuardianProfileEntity.setReqDeletedIP(reqIp);
                                    teacherGuardianProfileEntity.setReqDeletedPort(reqPort);
                                    teacherGuardianProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherGuardianProfileEntity.setReqDeletedOS(reqOs);
                                    teacherGuardianProfileEntity.setReqDeletedDevice(reqDevice);
                                    teacherGuardianProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        TeacherContactNoDto teacherGuardianContactNoDto = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherGuardianContactNoDto);
                                    }

                                    return teacherGuardianRepository.save(teacherGuardianEntity)
                                            .then(teacherGuardianProfileRepository.save(teacherGuardianProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherGuardianEntity, teacherGuardianProfileEntity, teacherContactNoDto)
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
