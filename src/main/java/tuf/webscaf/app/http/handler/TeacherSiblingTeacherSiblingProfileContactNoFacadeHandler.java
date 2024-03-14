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
import tuf.webscaf.app.dbContext.master.dto.TeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherSiblingTeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSiblingProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSiblingProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSiblingRepository;
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

@Tag(name = "teacherSiblingTeacherSiblingProfileContactNoFacade")
@Component
public class TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @Autowired
    SlaveTeacherSiblingRepository slaveTeacherSiblingRepository;

    @Autowired
    SlaveTeacherSiblingProfileRepository slaveTeacherSiblingProfileRepository;

    @Autowired
    TeacherSiblingProfileRepository teacherSiblingProfileRepository;

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

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-sibling-teacher-sibling-profile-contact-nos_index")
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
            Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDtoFlux = slaveTeacherSiblingRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherSiblingProfileEntity -> slaveTeacherSiblingRepository
                            .countTeacherSiblingTeacherSiblingProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDtoFlux = slaveTeacherSiblingRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherSiblingProfileEntity -> slaveTeacherSiblingRepository
                            .countTeacherSiblingTeacherSiblingProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSiblingProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-sibling-teacher-sibling-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherSiblingUUID = UUID.fromString((serverRequest.pathVariable("teacherSiblingUUID")));

        return slaveTeacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                .flatMap(teacherSiblingEntity -> slaveTeacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                        .flatMap(teacherSiblingProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherSiblingContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherSiblingContactNoDto);
                                    }

                                    return showFacadeDto(teacherSiblingEntity, teacherSiblingProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Sibling Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Sibling Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Sibling Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Sibling Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherSiblingEntity slaveTeacherSiblingEntity, SlaveTeacherSiblingProfileEntity slaveTeacherSiblingProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoDto) {

        SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto facadeDto = SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto.builder()
                .id(slaveTeacherSiblingEntity.getId())
                .uuid(slaveTeacherSiblingEntity.getUuid())
                .version(slaveTeacherSiblingEntity.getVersion())
                .status(slaveTeacherSiblingEntity.getStatus())
                .teacherUUID(slaveTeacherSiblingEntity.getTeacherUUID())
                .teacherSiblingUUID(slaveTeacherSiblingEntity.getUuid())
                .image(slaveTeacherSiblingProfileEntity.getImage())
                .name(slaveTeacherSiblingProfileEntity.getName())
                .nic(slaveTeacherSiblingProfileEntity.getNic())
                .age(slaveTeacherSiblingProfileEntity.getAge())
                .officialTel(slaveTeacherSiblingProfileEntity.getOfficialTel())
                .cityUUID(slaveTeacherSiblingProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherSiblingProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherSiblingProfileEntity.getCountryUUID())
                .genderUUID(slaveTeacherSiblingProfileEntity.getGenderUUID())
                .email(slaveTeacherSiblingProfileEntity.getEmail())
                .teacherSiblingContactNoDto(slaveTeacherContactNoDto)
                .createdAt(slaveTeacherSiblingEntity.getCreatedAt())
                .createdBy(slaveTeacherSiblingEntity.getCreatedBy())
                .reqCompanyUUID(slaveTeacherSiblingEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherSiblingEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherSiblingEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherSiblingEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherSiblingEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherSiblingEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherSiblingEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherSiblingEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherSiblingEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherSiblingEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherSiblingEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherSiblingEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherSiblingEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherSiblingEntity.getReqUpdatedReferer())
                .editable(slaveTeacherSiblingEntity.getEditable())
                .deletable(slaveTeacherSiblingEntity.getDeletable())
                .archived(slaveTeacherSiblingEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherSiblingTeacherSiblingProfileContactNoFacadeDto> facadeDto(TeacherSiblingEntity teacherSiblingEntity, TeacherSiblingProfileEntity teacherSiblingProfileEntity, List<TeacherContactNoDto> teacherSiblingContactNoDto) {

        TeacherSiblingTeacherSiblingProfileContactNoFacadeDto facadeDto = TeacherSiblingTeacherSiblingProfileContactNoFacadeDto.builder()
                .id(teacherSiblingEntity.getId())
                .uuid(teacherSiblingEntity.getUuid())
                .version(teacherSiblingEntity.getVersion())
                .status(teacherSiblingEntity.getStatus())
                .teacherUUID(teacherSiblingEntity.getTeacherUUID())
                .teacherSiblingUUID(teacherSiblingEntity.getUuid())
                .image(teacherSiblingProfileEntity.getImage())
                .name(teacherSiblingProfileEntity.getName())
                .nic(teacherSiblingProfileEntity.getNic())
                .age(teacherSiblingProfileEntity.getAge())
                .officialTel(teacherSiblingProfileEntity.getOfficialTel())
                .cityUUID(teacherSiblingProfileEntity.getCityUUID())
                .stateUUID(teacherSiblingProfileEntity.getStateUUID())
                .countryUUID(teacherSiblingProfileEntity.getCountryUUID())
                .genderUUID(teacherSiblingProfileEntity.getGenderUUID())
                .email(teacherSiblingProfileEntity.getEmail())
                .teacherSiblingContactNoDto(teacherSiblingContactNoDto)
                .createdAt(teacherSiblingEntity.getCreatedAt())
                .createdBy(teacherSiblingEntity.getCreatedBy())
                .reqCompanyUUID(teacherSiblingEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherSiblingEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherSiblingEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherSiblingEntity.getReqCreatedIP())
                .reqCreatedPort(teacherSiblingEntity.getReqCreatedPort())
                .reqCreatedOS(teacherSiblingEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherSiblingEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherSiblingEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherSiblingEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherSiblingEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherSiblingEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherSiblingEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherSiblingEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherSiblingEntity.getReqUpdatedReferer())
                .editable(teacherSiblingEntity.getEditable())
                .deletable(teacherSiblingEntity.getDeletable())
                .archived(teacherSiblingEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherSiblingProfileContactNoFacadeDto> updatedFacadeDto(TeacherSiblingEntity teacherSiblingEntity, TeacherSiblingProfileEntity teacherSiblingProfileEntity, List<TeacherContactNoDto> teacherSiblingContactNoDto) {

        TeacherSiblingProfileContactNoFacadeDto facadeDto = TeacherSiblingProfileContactNoFacadeDto.builder()
                .id(teacherSiblingEntity.getId())
                .uuid(teacherSiblingEntity.getUuid())
                .version(teacherSiblingEntity.getVersion())
                .status(teacherSiblingEntity.getStatus())
                .image(teacherSiblingProfileEntity.getImage())
                .name(teacherSiblingProfileEntity.getName())
                .nic(teacherSiblingProfileEntity.getNic())
                .age(teacherSiblingProfileEntity.getAge())
                .officialTel(teacherSiblingProfileEntity.getOfficialTel())
                .cityUUID(teacherSiblingProfileEntity.getCityUUID())
                .stateUUID(teacherSiblingProfileEntity.getStateUUID())
                .countryUUID(teacherSiblingProfileEntity.getCountryUUID())
                .genderUUID(teacherSiblingProfileEntity.getGenderUUID())
                .email(teacherSiblingProfileEntity.getEmail())
                .teacherSiblingContactNoDto(teacherSiblingContactNoDto)
                .updatedAt(teacherSiblingEntity.getUpdatedAt())
                .updatedBy(teacherSiblingEntity.getUpdatedBy())
                .reqCompanyUUID(teacherSiblingProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherSiblingProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherSiblingProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherSiblingProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherSiblingProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherSiblingProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherSiblingProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherSiblingProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherSiblingProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherSiblingProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherSiblingProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherSiblingProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherSiblingProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherSiblingProfileEntity.getReqUpdatedReferer())
                .editable(teacherSiblingProfileEntity.getEditable())
                .deletable(teacherSiblingProfileEntity.getDeletable())
                .archived(teacherSiblingProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-sibling-teacher-sibling-profile-contact-nos_store")
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

                    TeacherSiblingEntity teacherSiblingEntity = TeacherSiblingEntity.builder()
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
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherSiblingEntity.getTeacherUUID())
                            //check if Teacher Sibling Record Already Exists Against the same teacher
                            .flatMap(teacherEntity -> {

                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                //Building Teacher Sibling Profile Record
                                TeacherSiblingProfileEntity teacherSiblingProfileEntity = TeacherSiblingProfileEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherSiblingUUID(teacherSiblingEntity.getUuid())
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

                                sendFormData.add("docId", String.valueOf(teacherSiblingProfileEntity.getImage()));

                                //check if Gender Record Exists or Not
                                return genderRepository.findByUuidAndDeletedAtIsNull(teacherSiblingProfileEntity.getGenderUUID())
                                        //check if City Record Exists or Not
                                        .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherSiblingProfileEntity.getCityUUID())
                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                        //check if State Record Exists or not
                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherSiblingProfileEntity.getStateUUID())
                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                        //check if Country Record Exists or not
                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherSiblingProfileEntity.getCountryUUID())
                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                        //check if NIC Is Unique Against Teacher Sibling
                                                                                        .flatMap(checkNIC -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNull(teacherSiblingProfileEntity.getTeacherSiblingUUID(), teacherSiblingProfileEntity.getNic())
                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                        //check if Sibling Profile Already Exists Against Teacher Sibling
                                                                                        .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingProfileEntity.getTeacherSiblingUUID())
                                                                                                .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Sibling Profile already exist"))))
                                                                                        //check if Document Record Exists or not
                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherSiblingProfileEntity.getImage())
                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                        .flatMap(documentEntity -> {

                                                                                                                    // if student uuid is given
                                                                                                                    if (teacherSiblingEntity.getStudentUUID() != null) {

                                                                                                                        // checks if record already exists for student
                                                                                                                        return teacherSiblingRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherSiblingEntity.getTeacherUUID(), teacherSiblingEntity.getStudentUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Sibling Record Already Exists for Given Student"))
                                                                                                                                // checks if student uuid exists
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherSiblingEntity.getStudentUUID())
                                                                                                                                        .flatMap(studentEntity -> storeFacadeRecord(teacherSiblingEntity, teacherSiblingProfileEntity, value.get("teacherSiblingContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // else store the record
                                                                                                                    else {
                                                                                                                        return storeFacadeRecord(teacherSiblingEntity, teacherSiblingProfileEntity, value.get("teacherSiblingContactNoDto"), sendFormData);
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


    public Mono<ServerResponse> storeFacadeRecord(TeacherSiblingEntity teacherSiblingEntity, TeacherSiblingProfileEntity teacherSiblingProfileEntity, List<String> teacherSiblingContactList, MultiValueMap<String, String> sendFormData) {

        //check if Contact Category is Sibling
        return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("sibling")
                .flatMap(contactCategoryEntity -> {
                    //Creating an empty list to add teacher Contact No Records
                    List<TeacherContactNoEntity> teacherSiblingContactNoList = new ArrayList<>();

                    // Creating an empty list to add contact Type UUID's
                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                    // Creating an empty list to add contact No's
                    List<String> contactNoList = new ArrayList<>();


                    JsonNode contactNode = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contactNode = objectMapper.readTree(teacherSiblingContactList.toString());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert contactNode != null;


                    UUID teacherMetaUUID = null;
                    UUID contactCategoryUUID = null;

                    //iterating over the json node from front and setting contact No's
                    for (JsonNode siblingContact : contactNode) {

                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                .builder()
                                .contactTypeUUID(UUID.fromString(siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                .contactNo(siblingContact.get("contactNo").toString().replaceAll("\"", ""))
                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                .teacherMetaUUID(teacherSiblingEntity.getUuid())
                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                .createdBy(teacherSiblingEntity.getCreatedBy())
                                .reqCompanyUUID(teacherSiblingEntity.getReqCompanyUUID())
                                .reqBranchUUID(teacherSiblingEntity.getReqBranchUUID())
                                .reqCreatedIP(teacherSiblingEntity.getReqCreatedIP())
                                .reqCreatedPort(teacherSiblingEntity.getReqCreatedPort())
                                .reqCreatedBrowser(teacherSiblingEntity.getReqCreatedBrowser())
                                .reqCreatedOS(teacherSiblingEntity.getReqCreatedOS())
                                .reqCreatedDevice(teacherSiblingEntity.getReqCreatedDevice())
                                .reqCreatedReferer(teacherSiblingEntity.getReqCreatedReferer())
                                .build();

                        teacherSiblingContactNoList.add(teacherContactNoEntity);

                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                        contactNoList.add(teacherContactNoEntity.getContactNo());
                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                    }

                    //Getting Distinct Values Fom the List of Teacher Sibling Contact No List
                    teacherSiblingContactNoList = teacherSiblingContactNoList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    //Getting Distinct Values Fom the List of Contact Type UUID
                    contactTypeUUIDList = contactTypeUUIDList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    // Creating an empty list to add contact No's and returning dto with response
                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                    if (!teacherSiblingContactNoList.isEmpty()) {

                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                        UUID finalContactCategoryUUID = contactCategoryUUID;

                        List<TeacherContactNoEntity> finalTeacherSiblingContactNoList = teacherSiblingContactNoList;

                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                .collectList()
                                .flatMap(contactTypeEntityList -> {

                                    if (!contactTypeEntityList.isEmpty()) {

                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                            return responseInfoMsg("Contact Type Does not Exist");
                                        } else {
                                            //check if Contact No Record Already Exists against Teacher Sibling and Contact Type
                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherSiblingRepository.save(teacherSiblingEntity)
                                                            .then(teacherSiblingProfileRepository.save(teacherSiblingProfileEntity))
                                                            .then(teacherContactNoRepository.saveAll(finalTeacherSiblingContactNoList)
                                                                    .collectList())
                                                            .flatMap(mthContactNo -> {

                                                                for (TeacherContactNoEntity teacherContact : mthContactNo) {
                                                                    TeacherContactNoDto teacherSiblingContactNoDto = TeacherContactNoDto.builder()
                                                                            .contactNo(teacherContact.getContactNo())
                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                            .build();

                                                                    teacherContactNoDto.add(teacherSiblingContactNoDto);
                                                                }

                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherSiblingEntity.getCreatedBy().toString(),
                                                                                teacherSiblingEntity.getReqCompanyUUID().toString(), teacherSiblingEntity.getReqBranchUUID().toString())
                                                                        .flatMap(docUpdate -> facadeDto(teacherSiblingEntity, teacherSiblingProfileEntity, teacherContactNoDto)
                                                                                .flatMap(teacherSiblingFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherSiblingFacadeDto))
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
                        //if Contact No List is empty then store teacher Sibling and Teacher Sibling Profile
                        return teacherSiblingRepository.save(teacherSiblingEntity)
                                //Save Teacher Sibling Profile Entity
                                .then(teacherSiblingProfileRepository.save(teacherSiblingProfileEntity))
                                //update Document Status After Storing record
                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherSiblingEntity.getCreatedBy().toString(),
                                                teacherSiblingEntity.getReqCompanyUUID().toString(), teacherSiblingEntity.getReqBranchUUID().toString())
                                        .flatMap(docUpdate -> facadeDto(teacherSiblingEntity, teacherSiblingProfileEntity, teacherContactNoDto)
                                                .flatMap(teacherSiblingFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherSiblingFacadeDto))
                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                    }
                });

    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-sibling-teacher-sibling-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherSiblingUUID = UUID.fromString((serverRequest.pathVariable("teacherSiblingUUID")));
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
                .flatMap(value -> teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                                .flatMap(teacherSiblingEntity -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingUUID)
                                                .flatMap(previousProfileEntity -> {

                                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                    TeacherSiblingProfileEntity updatedEntity = TeacherSiblingProfileEntity.builder()
                                                            .uuid(previousProfileEntity.getUuid())
                                                            .teacherSiblingUUID(previousProfileEntity.getTeacherSiblingUUID())
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
                                                    return teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSiblingUUID(), updatedEntity.getNic(), updatedEntity.getUuid())
                                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                                            //check sibling profile is unique
                                                            .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSiblingUUID(), updatedEntity.getUuid())
                                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Sibling Profile already exist"))))
                                                            //checks if sibling uuid exists
                                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                                                    //check if Gender Record Exists or Not
                                                                                    .flatMap(teacherSiblingDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
                                                                                                                                                                                        List<String> teacherSiblingContactList = value.get("teacherSiblingContactNoDto");
                                                                                                                                                                                        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                                                                        teacherSiblingContactList.removeIf(s -> s.equals(""));

                                                                                                                                                                                        if (!teacherSiblingContactList.isEmpty()) {
                                                                                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("sibling")
                                                                                                                                                                                                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSiblingUUID)
                                                                                                                                                                                                            .collectList()
                                                                                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                                                                                //Removing Already existing Teacher Sibling Contact No Entity
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
                                                                                                                                                                                                                    contactNode = new ObjectMapper().readTree(teacherSiblingContactList.toString());
                                                                                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                                                                                    e.printStackTrace();
                                                                                                                                                                                                                }

                                                                                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                                                                                List<TeacherContactNoEntity> stdSiblingContactNoList = new ArrayList<>();

                                                                                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                                                                                UUID updatedStdMetaUUID = null;

                                                                                                                                                                                                                assert contactNode != null;
                                                                                                                                                                                                                for (JsonNode siblingContact : contactNode) {

                                                                                                                                                                                                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                                                                            .builder()
                                                                                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                                                                                            .contactTypeUUID(UUID.fromString(siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                                                            .contactNo(siblingContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                                                            .teacherMetaUUID(teacherSiblingUUID)
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

                                                                                                                                                                                                                    stdSiblingContactNoList.add(teacherContactNoEntity);

                                                                                                                                                                                                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                                                                                    contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                                                                                    updatedStdMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                                                                                }

                                                                                                                                                                                                                //Getting Distinct Values Fom the List of Teacher Sibling Contact No List
                                                                                                                                                                                                                stdSiblingContactNoList = stdSiblingContactNoList.stream()
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

                                                                                                                                                                                                                List<TeacherContactNoEntity> finalTeacherSiblingContactNoList1 = stdSiblingContactNoList;

                                                                                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                                                        .collectList()
                                                                                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                                                                } else {

                                                                                                                                                                                                                                    //check if Contact No Record Already Exists against Teacher Sibling and Contact Type
                                                                                                                                                                                                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                                                                                    .then(teacherSiblingProfileRepository.save(updatedEntity))
                                                                                                                                                                                                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                                                                            .collectList())
                                                                                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherSiblingContactNoList1)
                                                                                                                                                                                                                                                            .collectList()
                                                                                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                                                                                    TeacherContactNoDto teacherSiblingContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                                                                            .build();

                                                                                                                                                                                                                                                                    teacherContactNoDto.add(teacherSiblingContactNoDto);
                                                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherSiblingEntity, updatedEntity, teacherContactNoDto)
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
                                                                                                                                                                                            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSiblingUUID)
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
                                                                                                                                                                                                                .flatMap(teacherContactList -> teacherSiblingProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                                                        .then(teacherSiblingProfileRepository.save(updatedEntity))
                                                                                                                                                                                                                        .flatMap(TeacherSiblingProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherSiblingEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                                                                        .flatMap(TeacherSiblingFacadeDto -> responseSuccessMsg("Record Updated Successfully", TeacherSiblingFacadeDto))
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
                                                }).switchIfEmpty(responseInfoMsg("Sibling Profile Against the entered Teacher Sibling Record Does not exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Sibling Profile Against the entered Teacher Sibling Record Does not exist.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Teacher Sibling Record Does not Exist."))
                                .onErrorResume(ex -> responseErrorMsg("Teacher Sibling Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

//    @AuthHasPermission(value = "academic_api_v1_facade_teacher-sibling-teacher-sibling-profile-contact-nos_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID teacherSiblingUUID = UUID.fromString((serverRequest.pathVariable("teacherSiblingUUID")));
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
//                .flatMap(value -> teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
//                        .flatMap(teacherSiblingEntity -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingUUID)
//                                .flatMap(previousProfileEntity -> {
//
//                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();
//
//                                    UUID studentUUID = null;
//                                    if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
//                                        studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
//                                    }
//
//                                    TeacherSiblingEntity updatedTeacherSiblingEntity = TeacherSiblingEntity.builder()
//                                            .uuid(teacherSiblingEntity.getUuid())
//                                            .teacherUUID(teacherSiblingEntity.getTeacherUUID())
//                                            .studentUUID(studentUUID)
//                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                            .createdAt(teacherSiblingEntity.getCreatedAt())
//                                            .createdBy(teacherSiblingEntity.getCreatedBy())
//                                            .updatedBy(UUID.fromString(userId))
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(teacherSiblingEntity.getReqCreatedIP())
//                                            .reqCreatedPort(teacherSiblingEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(teacherSiblingEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(teacherSiblingEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(teacherSiblingEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(teacherSiblingEntity.getReqCreatedReferer())
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
//                                    teacherSiblingEntity.setDeletedBy(UUID.fromString(userId));
//                                    teacherSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                    teacherSiblingEntity.setReqDeletedIP(reqIp);
//                                    teacherSiblingEntity.setReqDeletedPort(reqPort);
//                                    teacherSiblingEntity.setReqDeletedBrowser(reqBrowser);
//                                    teacherSiblingEntity.setReqDeletedOS(reqOs);
//                                    teacherSiblingEntity.setReqDeletedDevice(reqDevice);
//                                    teacherSiblingEntity.setReqDeletedReferer(reqReferer);
//
//                                    TeacherSiblingProfileEntity updatedEntity = TeacherSiblingProfileEntity.builder()
//                                            .uuid(previousProfileEntity.getUuid())
//                                            .teacherSiblingUUID(previousProfileEntity.getTeacherSiblingUUID())
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
//                                    return teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSiblingUUID(), updatedEntity.getNic(), updatedEntity.getUuid())
//                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                                            //check sibling profile is unique
//                                            .switchIfEmpty(Mono.defer(() -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSiblingUUID(), updatedEntity.getUuid())
//                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Sibling Profile already exist"))))
//                                            //checks if sibling uuid exists
//                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
//                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
//                                                            //check if Gender Record Exists or Not
//                                                            .flatMap(teacherSiblingDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
//                                                                                                                                if (updatedTeacherSiblingEntity.getStudentUUID() != null) {
//                                                                                                                                    // checks if record already exists for student
//                                                                                                                                    return teacherSiblingRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherSiblingEntity.getTeacherUUID(), updatedTeacherSiblingEntity.getStudentUUID(), updatedTeacherSiblingEntity.getUuid())
//                                                                                                                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Sibling Record Already Exists for Given Student"))
//                                                                                                                                            // checks if student uuid exists
//                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedTeacherSiblingEntity.getStudentUUID())
//                                                                                                                                                    .flatMap(studentEntity -> updateFacadeRecord(teacherSiblingEntity, updatedTeacherSiblingEntity, previousProfileEntity, updatedEntity, value.get("teacherSiblingContactNoDto"), sendFormData))
//                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
//                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
//                                                                                                                                            ));
//                                                                                                                                }
//
//                                                                                                                                // else update the record
//                                                                                                                                else {
//                                                                                                                                    return updateFacadeRecord(teacherSiblingEntity, updatedTeacherSiblingEntity, previousProfileEntity, updatedEntity, value.get("teacherSiblingContactNoDto"), sendFormData);
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
//                                }).switchIfEmpty(responseInfoMsg("Sibling Profile Against the entered Teacher Sibling Record Does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Sibling Profile Against the entered Teacher Sibling Record Does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Teacher Sibling Record Does not Exist."))
//                        .onErrorResume(ex -> responseErrorMsg("Teacher Sibling Record Does not Exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
//    }
//
//
//    public Mono<ServerResponse> updateFacadeRecord(TeacherSiblingEntity teacherSiblingEntity, TeacherSiblingEntity updatedTeacherSiblingEntity, TeacherSiblingProfileEntity previousProfileEntity, TeacherSiblingProfileEntity updatedEntity, List<String> teacherSiblingContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();
//
//        teacherSiblingContactList.removeIf(s -> s.equals(""));
//
//        if (!teacherSiblingContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("sibling")
//                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Teacher Sibling Contact No Entity
//                                for (TeacherContactNoEntity teacherContact : existingContactList) {
//                                    teacherContact.setDeletedBy(updatedTeacherSiblingEntity.getUpdatedBy());
//                                    teacherContact.setDeletedAt(updatedTeacherSiblingEntity.getUpdatedAt());
//                                    teacherContact.setReqDeletedIP(updatedTeacherSiblingEntity.getReqUpdatedIP());
//                                    teacherContact.setReqDeletedPort(updatedTeacherSiblingEntity.getReqUpdatedPort());
//                                    teacherContact.setReqDeletedBrowser(updatedTeacherSiblingEntity.getReqUpdatedBrowser());
//                                    teacherContact.setReqDeletedOS(updatedTeacherSiblingEntity.getReqUpdatedOS());
//                                    teacherContact.setReqDeletedDevice(updatedTeacherSiblingEntity.getReqUpdatedDevice());
//                                    teacherContact.setReqDeletedReferer(updatedTeacherSiblingEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(teacherSiblingContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<TeacherContactNoEntity> teacherSiblingContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedTeacherMetaUUID = null;
//
//                                for (JsonNode siblingContact : contactNode) {
//
//                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(siblingContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .teacherMetaUUID(teacherSiblingEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedTeacherSiblingEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedTeacherSiblingEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedTeacherSiblingEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedTeacherSiblingEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedTeacherSiblingEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedTeacherSiblingEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedTeacherSiblingEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedTeacherSiblingEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedTeacherSiblingEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    teacherSiblingContactNoList.add(teacherContactNoEntity);
//
//                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(teacherContactNoEntity.getContactNo());
//
//                                    updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Teacher Sibling Contact No List
//                                teacherSiblingContactNoList = teacherSiblingContactNoList.stream()
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
//                                List<TeacherContactNoEntity> finalTeacherSiblingContactNoList1 = teacherSiblingContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Teacher Sibling and Contact Type
//                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> teacherSiblingRepository.save(teacherSiblingEntity)
//                                                                    .then(teacherSiblingRepository.save(updatedTeacherSiblingEntity))
//                                                                    .then(teacherSiblingProfileRepository.save(previousProfileEntity))
//                                                                    .then(teacherSiblingProfileRepository.save(updatedEntity))
//                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherSiblingContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
//                                                                                    TeacherContactNoDto teacherSiblingContactNoDto = TeacherContactNoDto.builder()
//                                                                                            .contactNo(teacherContact.getContactNo())
//                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    teacherContactNoDto.add(teacherSiblingContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedTeacherSiblingEntity.getUpdatedBy().toString(),
//                                                                                                updatedTeacherSiblingEntity.getReqCompanyUUID().toString(), updatedTeacherSiblingEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherSiblingEntity, updatedEntity, teacherContactNoDto)
//                                                                                                .flatMap(teacherSiblingFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherSiblingFacadeDto))
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
//            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousTeacherContactList -> {
//
//                        for (TeacherContactNoEntity teacherContact : previousTeacherContactList) {
//                            teacherContact.setDeletedBy(updatedTeacherSiblingEntity.getUpdatedBy());
//                            teacherContact.setDeletedAt(updatedTeacherSiblingEntity.getUpdatedAt());
//                            teacherContact.setReqDeletedIP(updatedTeacherSiblingEntity.getReqUpdatedIP());
//                            teacherContact.setReqDeletedPort(updatedTeacherSiblingEntity.getReqUpdatedPort());
//                            teacherContact.setReqDeletedBrowser(updatedTeacherSiblingEntity.getReqUpdatedBrowser());
//                            teacherContact.setReqDeletedOS(updatedTeacherSiblingEntity.getReqUpdatedOS());
//                            teacherContact.setReqDeletedDevice(updatedTeacherSiblingEntity.getReqUpdatedDevice());
//                            teacherContact.setReqDeletedReferer(updatedTeacherSiblingEntity.getReqUpdatedReferer());
//                        }
//
//                        return teacherContactNoRepository.saveAll(previousTeacherContactList)
//                                .collectList()
//                                .flatMap(teacherContactList -> teacherSiblingRepository.save(teacherSiblingEntity)
//                                        .then(teacherSiblingRepository.save(updatedTeacherSiblingEntity))
//                                        .then(teacherSiblingProfileRepository.save(previousProfileEntity))
//                                        .then(teacherSiblingProfileRepository.save(updatedEntity))
//                                        .flatMap(teacherSiblingProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedTeacherSiblingEntity.getUpdatedBy().toString(),
//                                                        updatedTeacherSiblingEntity.getReqCompanyUUID().toString(), updatedTeacherSiblingEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherSiblingEntity, updatedEntity, teacherContactNoDto)
//                                                        .flatMap(teacherSiblingFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherSiblingFacadeDto))
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


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-sibling-teacher-sibling-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherSiblingUUID = UUID.fromString((serverRequest.pathVariable("teacherSiblingUUID")));
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

        return teacherSiblingRepository.findByUuidAndDeletedAtIsNull(teacherSiblingUUID)
                .flatMap(teacherSiblingEntity -> teacherSiblingProfileRepository.findFirstByTeacherSiblingUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                        .flatMap(teacherSiblingProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSiblingEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherSiblingEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherSiblingEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherSiblingEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherSiblingEntity.setReqDeletedIP(reqIp);
                                    teacherSiblingEntity.setReqDeletedPort(reqPort);
                                    teacherSiblingEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherSiblingEntity.setReqDeletedOS(reqOs);
                                    teacherSiblingEntity.setReqDeletedDevice(reqDevice);
                                    teacherSiblingEntity.setReqDeletedReferer(reqReferer);

                                    teacherSiblingProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherSiblingProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherSiblingProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherSiblingProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherSiblingProfileEntity.setReqDeletedIP(reqIp);
                                    teacherSiblingProfileEntity.setReqDeletedPort(reqPort);
                                    teacherSiblingProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherSiblingProfileEntity.setReqDeletedOS(reqOs);
                                    teacherSiblingProfileEntity.setReqDeletedDevice(reqDevice);
                                    teacherSiblingProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        TeacherContactNoDto teacherSiblingContactNoDto = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherSiblingContactNoDto);
                                    }

                                    return teacherSiblingRepository.save(teacherSiblingEntity)
                                            .then(teacherSiblingProfileRepository.save(teacherSiblingProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherSiblingEntity, teacherSiblingProfileEntity, teacherContactNoDto)
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
