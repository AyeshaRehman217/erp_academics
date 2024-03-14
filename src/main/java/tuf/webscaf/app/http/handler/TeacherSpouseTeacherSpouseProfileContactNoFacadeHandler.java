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
import tuf.webscaf.app.dbContext.master.dto.TeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherSpouseTeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherSpouseProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSpouseProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherSpouseRepository;
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

@Tag(name = "teacherSpouseTeacherSpouseProfileContactNoFacade")
@Component
public class TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    SlaveTeacherSpouseRepository slaveTeacherSpouseRepository;

    @Autowired
    SlaveTeacherSpouseProfileRepository slaveTeacherSpouseProfileRepository;

    @Autowired
    TeacherSpouseProfileRepository teacherSpouseProfileRepository;

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

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-spouse-teacher-spouse-profile-contact-nos_index")
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
            Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDtoFlux = slaveTeacherSpouseRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherSpouseProfileEntity -> slaveTeacherSpouseRepository
                            .countTeacherSpouseTeacherSpouseProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (teacherSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDtoFlux = slaveTeacherSpouseRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherSpouseProfileEntity -> slaveTeacherSpouseRepository
                            .countTeacherSpouseTeacherSpouseProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherSpouseProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-spouse-teacher-spouse-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherSpouseUUID = UUID.fromString((serverRequest.pathVariable("teacherSpouseUUID")));

        return slaveTeacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                .flatMap(teacherSpouseEntity -> slaveTeacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                        .flatMap(teacherSpouseProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherSpouseContactNoDto = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherSpouseContactNoDto);
                                    }

                                    return showFacadeDto(teacherSpouseEntity, teacherSpouseProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Spouse Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Spouse Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Spouse Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Spouse Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherSpouseEntity slaveTeacherSpouseEntity, SlaveTeacherSpouseProfileEntity slaveTeacherSpouseProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoFacadeDto) {

        SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto facadeDto = SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto.builder()
                .id(slaveTeacherSpouseEntity.getId())
                .uuid(slaveTeacherSpouseEntity.getUuid())
                .version(slaveTeacherSpouseEntity.getVersion())
                .status(slaveTeacherSpouseEntity.getStatus())
                .teacherUUID(slaveTeacherSpouseEntity.getTeacherUUID())
                .studentUUID(slaveTeacherSpouseEntity.getStudentUUID())
                .teacherSpouseAsTeacherUUID(slaveTeacherSpouseEntity.getTeacherSpouseUUID())
                .teacherSpouseUUID(slaveTeacherSpouseEntity.getUuid())
                .image(slaveTeacherSpouseProfileEntity.getImage())
                .name(slaveTeacherSpouseProfileEntity.getName())
                .nic(slaveTeacherSpouseProfileEntity.getNic())
                .age(slaveTeacherSpouseProfileEntity.getAge())
                .officialTel(slaveTeacherSpouseProfileEntity.getOfficialTel())
                .cityUUID(slaveTeacherSpouseProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherSpouseProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherSpouseProfileEntity.getCountryUUID())
                .genderUUID(slaveTeacherSpouseProfileEntity.getGenderUUID())
                .email(slaveTeacherSpouseProfileEntity.getEmail())
                .teacherSpouseContactNoDto(slaveTeacherContactNoFacadeDto)
                .createdAt(slaveTeacherSpouseEntity.getCreatedAt())
                .createdBy(slaveTeacherSpouseEntity.getCreatedBy())
                .reqCompanyUUID(slaveTeacherSpouseEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherSpouseEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherSpouseEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherSpouseEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherSpouseEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherSpouseEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherSpouseEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherSpouseEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherSpouseEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherSpouseEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherSpouseEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherSpouseEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherSpouseEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherSpouseEntity.getReqUpdatedReferer())
                .editable(slaveTeacherSpouseEntity.getEditable())
                .deletable(slaveTeacherSpouseEntity.getDeletable())
                .archived(slaveTeacherSpouseEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherSpouseTeacherSpouseProfileContactNoFacadeDto> facadeDto(TeacherSpouseEntity teacherSpouseEntity, TeacherSpouseProfileEntity teacherSpouseProfileEntity, List<TeacherContactNoDto> teacherSpouseContactNoDto) {

        TeacherSpouseTeacherSpouseProfileContactNoFacadeDto facadeDto = TeacherSpouseTeacherSpouseProfileContactNoFacadeDto.builder()
                .id(teacherSpouseEntity.getId())
                .uuid(teacherSpouseEntity.getUuid())
                .version(teacherSpouseEntity.getVersion())
                .status(teacherSpouseEntity.getStatus())
                .teacherUUID(teacherSpouseEntity.getTeacherUUID())
                .studentUUID(teacherSpouseEntity.getStudentUUID())
                .teacherSpouseAsTeacherUUID(teacherSpouseEntity.getTeacherSpouseUUID())
                .teacherSpouseUUID(teacherSpouseEntity.getUuid())
                .image(teacherSpouseProfileEntity.getImage())
                .name(teacherSpouseProfileEntity.getName())
                .nic(teacherSpouseProfileEntity.getNic())
                .age(teacherSpouseProfileEntity.getAge())
                .officialTel(teacherSpouseProfileEntity.getOfficialTel())
                .cityUUID(teacherSpouseProfileEntity.getCityUUID())
                .stateUUID(teacherSpouseProfileEntity.getStateUUID())
                .countryUUID(teacherSpouseProfileEntity.getCountryUUID())
                .genderUUID(teacherSpouseProfileEntity.getGenderUUID())
                .email(teacherSpouseProfileEntity.getEmail())
                .teacherSpouseContactNoDto(teacherSpouseContactNoDto)
                .createdAt(teacherSpouseEntity.getCreatedAt())
                .createdBy(teacherSpouseEntity.getCreatedBy())
                .reqCompanyUUID(teacherSpouseEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherSpouseEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherSpouseEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherSpouseEntity.getReqCreatedIP())
                .reqCreatedPort(teacherSpouseEntity.getReqCreatedPort())
                .reqCreatedOS(teacherSpouseEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherSpouseEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherSpouseEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherSpouseEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherSpouseEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherSpouseEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherSpouseEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherSpouseEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherSpouseEntity.getReqUpdatedReferer())
                .editable(teacherSpouseEntity.getEditable())
                .deletable(teacherSpouseEntity.getDeletable())
                .archived(teacherSpouseEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherSpouseProfileContactNoFacadeDto> updatedFacadeDto(TeacherSpouseEntity teacherSpouseEntity, TeacherSpouseProfileEntity teacherSpouseProfileEntity, List<TeacherContactNoDto> teacherSpouseContactNoDto) {

        TeacherSpouseProfileContactNoFacadeDto facadeDto = TeacherSpouseProfileContactNoFacadeDto.builder()
                .id(teacherSpouseEntity.getId())
                .uuid(teacherSpouseEntity.getUuid())
                .version(teacherSpouseEntity.getVersion())
                .status(teacherSpouseEntity.getStatus())
                .studentUUID(teacherSpouseEntity.getStudentUUID())
                .teacherSpouseAsTeacherUUID(teacherSpouseEntity.getTeacherSpouseUUID())
                .image(teacherSpouseProfileEntity.getImage())
                .name(teacherSpouseProfileEntity.getName())
                .nic(teacherSpouseProfileEntity.getNic())
                .age(teacherSpouseProfileEntity.getAge())
                .officialTel(teacherSpouseProfileEntity.getOfficialTel())
                .cityUUID(teacherSpouseProfileEntity.getCityUUID())
                .stateUUID(teacherSpouseProfileEntity.getStateUUID())
                .countryUUID(teacherSpouseProfileEntity.getCountryUUID())
                .genderUUID(teacherSpouseProfileEntity.getGenderUUID())
                .email(teacherSpouseProfileEntity.getEmail())
                .teacherSpouseContactNoDto(teacherSpouseContactNoDto)
                .updatedAt(teacherSpouseEntity.getUpdatedAt())
                .updatedBy(teacherSpouseEntity.getUpdatedBy())
                .reqCompanyUUID(teacherSpouseProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherSpouseProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherSpouseProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherSpouseProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherSpouseProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherSpouseProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherSpouseProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherSpouseProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherSpouseProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherSpouseProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherSpouseProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherSpouseProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherSpouseProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherSpouseProfileEntity.getReqUpdatedReferer())
                .editable(teacherSpouseProfileEntity.getEditable())
                .deletable(teacherSpouseProfileEntity.getDeletable())
                .archived(teacherSpouseProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-spouse-teacher-spouse-profile-contact-nos_store")
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

                    UUID teacherSpouseAsTeacherUUID = null;
                    if ((value.containsKey("teacherSpouseAsTeacherUUID") && (value.getFirst("teacherSpouseAsTeacherUUID") != ""))) {
                        teacherSpouseAsTeacherUUID = UUID.fromString(value.getFirst("teacherSpouseAsTeacherUUID").trim());
                    }

                    TeacherSpouseEntity teacherSpouseEntity = TeacherSpouseEntity.builder()
                            .uuid(UUID.randomUUID())
                            .teacherUUID(UUID.fromString(value.getFirst("teacherUUID").trim()))
                            .studentUUID(studentUUID)
                            .teacherSpouseUUID(teacherSpouseAsTeacherUUID)
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
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID())
                            //check if Teacher Spouse Record Already Exists Against the same teacher
                            .flatMap(teacherEntity -> {

                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                //Building Teacher Spouse Profile Record
                                TeacherSpouseProfileEntity teacherSpouseProfileEntity = TeacherSpouseProfileEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .teacherSpouseUUID(teacherSpouseEntity.getUuid())
                                        .image(UUID.fromString(value.getFirst("image")))
                                        .name(value.getFirst("name").trim())
                                        .nic(value.getFirst("nic"))
                                        .age(Integer.valueOf(value.getFirst("age")))
                                        .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                        .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                        .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                        .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
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

                                sendFormData.add("docId", String.valueOf(teacherSpouseProfileEntity.getImage()));

                                //check if Gender Record Exists or Not
                                return genderRepository.findByUuidAndDeletedAtIsNull(teacherSpouseProfileEntity.getGenderUUID())
                                        //check if City Record Exists or Not
                                        .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherSpouseProfileEntity.getCityUUID())
                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                        //check if State Record Exists or not
                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherSpouseProfileEntity.getStateUUID())
                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                        //check if Country Record Exists or not
                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherSpouseProfileEntity.getCountryUUID())
                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                        //check if NIC Is Unique Against Teacher Spouse
                                                                                        .flatMap(checkNIC -> teacherSpouseProfileRepository.findFirstByNicAndDeletedAtIsNull(teacherSpouseProfileEntity.getNic())
                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                        //check if Spouse Profile Already Exists Against Teacher Spouse
                                                                                        .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseProfileEntity.getTeacherSpouseUUID())
                                                                                                .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Spouse Profile already exist"))))
                                                                                        //check if Document Record Exists or not
                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherSpouseProfileEntity.getImage())
                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                        .flatMap(documentEntity -> {

                                                                                                                    // if same teacher uuid is given as both teacher and teacher spouse's teacher uuid
                                                                                                                    if (teacherSpouseEntity.getTeacherUUID().equals(teacherSpouseEntity.getTeacherSpouseUUID())) {
                                                                                                                        return responseInfoMsg("The teacher spouse cannot be the same as the given teacher");
                                                                                                                    }

                                                                                                                    // if teacher spouse is student and teacher
                                                                                                                    else if (teacherSpouseEntity.getStudentUUID() != null && teacherSpouseEntity.getTeacherSpouseUUID() != null) {
                                                                                                                        return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getTeacherSpouseUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getStudentUUID())
                                                                                                                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getStudentUUID())
                                                                                                                                        .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getTeacherSpouseUUID())
                                                                                                                                                .flatMap(teacherRecord -> storeFacadeRecord(teacherSpouseEntity, teacherSpouseProfileEntity, value.get("teacherSpouseContactNoDto"), sendFormData))
                                                                                                                                                .switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // if teacher spouse is student
                                                                                                                    else if (teacherSpouseEntity.getStudentUUID() != null) {
                                                                                                                        return teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getStudentUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getStudentUUID())
                                                                                                                                        .flatMap(studentEntity -> storeFacadeRecord(teacherSpouseEntity, teacherSpouseProfileEntity, value.get("teacherSpouseContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // if teacher spouse is teacher
                                                                                                                    else if (teacherSpouseEntity.getTeacherSpouseUUID() != null) {
                                                                                                                        return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getTeacherUUID(), teacherSpouseEntity.getTeacherSpouseUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherSpouseEntity.getTeacherSpouseUUID())
                                                                                                                                        .flatMap(teacherRecord -> storeFacadeRecord(teacherSpouseEntity, teacherSpouseProfileEntity, value.get("teacherSpouseContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // else store the record
                                                                                                                    else {
                                                                                                                        return storeFacadeRecord(teacherSpouseEntity, teacherSpouseProfileEntity, value.get("teacherSpouseContactNoDto"), sendFormData);
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


    public Mono<ServerResponse> storeFacadeRecord(TeacherSpouseEntity teacherSpouseEntity, TeacherSpouseProfileEntity teacherSpouseProfileEntity, List<String> teacherSpouseContactList, MultiValueMap<String, String> sendFormData) {

        //check if Contact Category is Spouse
        return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
                .flatMap(contactCategoryEntity -> {
                    //Creating an empty list to add teacher Contact No Records
                    List<TeacherContactNoEntity> teacherSpouseContactNoList = new ArrayList<>();

                    // Creating an empty list to add contact Type UUID's
                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                    // Creating an empty list to add contact No's
                    List<String> contactNoList = new ArrayList<>();


                    JsonNode contactNode = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contactNode = objectMapper.readTree(teacherSpouseContactList.toString());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert contactNode != null;


                    UUID teacherMetaUUID = null;
                    UUID contactCategoryUUID = null;

                    //iterating over the json node from front and setting contact No's
                    for (JsonNode spouseContact : contactNode) {

                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                .builder()
                                .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                .teacherMetaUUID(teacherSpouseEntity.getUuid())
                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                .createdBy(teacherSpouseEntity.getCreatedBy())
                                .reqCompanyUUID(teacherSpouseEntity.getReqCompanyUUID())
                                .reqBranchUUID(teacherSpouseEntity.getReqBranchUUID())
                                .reqCreatedIP(teacherSpouseEntity.getReqCreatedIP())
                                .reqCreatedPort(teacherSpouseEntity.getReqCreatedPort())
                                .reqCreatedBrowser(teacherSpouseEntity.getReqCreatedBrowser())
                                .reqCreatedOS(teacherSpouseEntity.getReqCreatedOS())
                                .reqCreatedDevice(teacherSpouseEntity.getReqCreatedDevice())
                                .reqCreatedReferer(teacherSpouseEntity.getReqCreatedReferer())
                                .build();

                        teacherSpouseContactNoList.add(teacherContactNoEntity);

                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                        contactNoList.add(teacherContactNoEntity.getContactNo());
                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                    }

                    //Getting Distinct Values Fom the List of Teacher Spouse Contact No List
                    teacherSpouseContactNoList = teacherSpouseContactNoList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    //Getting Distinct Values Fom the List of Contact Type UUID
                    contactTypeUUIDList = contactTypeUUIDList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    // Creating an empty list to add contact No's and returning dto with response
                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                    if (!teacherSpouseContactNoList.isEmpty()) {

                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                        UUID finalContactCategoryUUID = contactCategoryUUID;

                        List<TeacherContactNoEntity> finalTeacherSpouseContactNoList = teacherSpouseContactNoList;

                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                .collectList()
                                .flatMap(contactTypeEntityList -> {

                                    if (!contactTypeEntityList.isEmpty()) {

                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                            return responseInfoMsg("Contact Type Does not Exist");
                                        } else {
                                            //check if Contact No Record Already Exists against Teacher Spouse and Contact Type
                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                    .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.save(teacherSpouseEntity)
                                                            .then(teacherSpouseProfileRepository.save(teacherSpouseProfileEntity))
                                                            .then(teacherContactNoRepository.saveAll(finalTeacherSpouseContactNoList)
                                                                    .collectList())
                                                            .flatMap(mthContactNo -> {

                                                                for (TeacherContactNoEntity teacherContact : mthContactNo) {
                                                                    TeacherContactNoDto teacherSpouseContactNoDto = TeacherContactNoDto.builder()
                                                                            .contactNo(teacherContact.getContactNo())
                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                            .build();

                                                                    teacherContactNoDto.add(teacherSpouseContactNoDto);
                                                                }

                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherSpouseEntity.getCreatedBy().toString(),
                                                                                teacherSpouseEntity.getReqCompanyUUID().toString(), teacherSpouseEntity.getReqBranchUUID().toString())
                                                                        .flatMap(docUpdate -> facadeDto(teacherSpouseEntity, teacherSpouseProfileEntity, teacherContactNoDto)
                                                                                .flatMap(teacherSpouseFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherSpouseFacadeDto))
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
                        //if Contact No List is empty then store teacher Spouse and Teacher Spouse Profile
                        return teacherSpouseRepository.save(teacherSpouseEntity)
                                //Save Teacher Spouse Profile Entity
                                .then(teacherSpouseProfileRepository.save(teacherSpouseProfileEntity))
                                //update Document Status After Storing record
                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherSpouseEntity.getCreatedBy().toString(),
                                                teacherSpouseEntity.getReqCompanyUUID().toString(), teacherSpouseEntity.getReqBranchUUID().toString())
                                        .flatMap(docUpdate -> facadeDto(teacherSpouseEntity, teacherSpouseProfileEntity, teacherContactNoDto)
                                                .flatMap(teacherSpouseFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherSpouseFacadeDto))
                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                    }
                });

    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-spouse-teacher-spouse-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherSpouseUUID = UUID.fromString((serverRequest.pathVariable("teacherSpouseUUID")));
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
                .flatMap(value -> teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                        .flatMap(teacherSpouseEntity -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherSpouseProfileEntity updatedEntity = TeacherSpouseProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .teacherSpouseUUID(previousProfileEntity.getTeacherSpouseUUID())
                                            .image(UUID.fromString(value.getFirst("image")))
                                            .name(value.getFirst("name").trim())
                                            .nic(value.getFirst("nic"))
                                            .age(Integer.valueOf(value.getFirst("age")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
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
                                    return teacherSpouseProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check spouse profile is unique
                                            .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSpouseUUID(), updatedEntity.getUuid())
                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Spouse Profile already exist"))))
                                            //checks if spouse uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //check if Gender Record Exists or Not
                                                            .flatMap(teacherSpouseDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
                                                                                                                        List<String> teacherSpouseContactList = value.get("teacherSpouseContactNoDto");
                                                                                                                        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                        teacherSpouseContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!teacherSpouseContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
                                                                                                                                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing Teacher Spouse Contact No Entity
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
                                                                                                                                                    contactNode = new ObjectMapper().readTree(teacherSpouseContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<TeacherContactNoEntity> stdSpouseContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedStdMetaUUID = null;

                                                                                                                                                assert contactNode != null;
                                                                                                                                                for (JsonNode spouseContact : contactNode) {

                                                                                                                                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .teacherMetaUUID(teacherSpouseUUID)
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

                                                                                                                                                    stdSpouseContactNoList.add(teacherContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                    updatedStdMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Teacher Spouse Contact No List
                                                                                                                                                stdSpouseContactNoList = stdSpouseContactNoList.stream()
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

                                                                                                                                                List<TeacherContactNoEntity> finalTeacherSpouseContactNoList1 = stdSpouseContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Teacher Spouse and Contact Type
                                                                                                                                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(teacherSpouseProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherSpouseContactNoList1)
                                                                                                                                                                                            .collectList()
                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                    TeacherContactNoDto teacherSpouseContactNoDto = TeacherContactNoDto.builder()
                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                            .build();

                                                                                                                                                                                                    teacherContactNoDto.add(teacherSpouseContactNoDto);
                                                                                                                                                                                                }

                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherSpouseEntity, updatedEntity, teacherContactNoDto)
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
                                                                                                                            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseUUID)
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
                                                                                                                                                .flatMap(teacherContactList -> teacherSpouseProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(teacherSpouseProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(TeacherSpouseProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherSpouseEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                        .flatMap(TeacherSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", TeacherSpouseFacadeDto))
                                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to update Document. There is something wrong Please try again."))
                                                                                                                                                                .onErrorResume(err -> responseErrorMsg("Unable to Update Document. Please Contact Developer."))
                                                                                                                                                        )
                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Update Contact No Records. There is something wrong please try again"))
                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Contact No Records.Please Contact Developer."));
                                                                                                                                    });
                                                                                                                        }

                                                                                                                    })).switchIfEmpty(responseInfoMsg("Country does not exist"))
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
                                }).switchIfEmpty(responseInfoMsg("Spouse Profile Against the entered Teacher Spouse Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Spouse Profile Against the entered Teacher Spouse Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Spouse Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Spouse Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

//    @AuthHasPermission(value = "academic_api_v1_facade_teacher-spouse-teacher-spouse-profile-contact-nos_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID teacherSpouseUUID = UUID.fromString((serverRequest.pathVariable("teacherSpouseUUID")));
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
//                .flatMap(value -> teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
//                                .flatMap(teacherSpouseEntity -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseUUID)
//                                                .flatMap(previousProfileEntity -> {
//
//                                                    UUID studentUUID = null;
//                                                    UUID teacherSpouseAsTeacherUUID = null;
//
//                                                    if ((value.containsKey("studentUUID") && (value.getFirst("studentUUID") != ""))) {
//                                                        studentUUID = UUID.fromString(value.getFirst("studentUUID").trim());
//                                                    }
//
//                                                    if ((value.containsKey("teacherSpouseAsTeacherUUID") && (value.getFirst("teacherSpouseAsTeacherUUID") != ""))) {
//                                                        teacherSpouseAsTeacherUUID = UUID.fromString(value.getFirst("teacherSpouseAsTeacherUUID").trim());
//                                                    }
//
//                                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();
//
//                                                    TeacherSpouseProfileEntity updatedEntity = TeacherSpouseProfileEntity.builder()
//                                                            .uuid(previousProfileEntity.getUuid())
//                                                            .teacherSpouseUUID(previousProfileEntity.getTeacherSpouseUUID())
//                                                            .image(UUID.fromString(value.getFirst("image")))
//                                                            .name(value.getFirst("name").trim())
//                                                            .nic(value.getFirst("nic"))
//                                                            .age(Integer.valueOf(value.getFirst("age")))
//                                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
//                                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
//                                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
//                                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
//                                                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
//                                                            .officialTel(value.getFirst("officialTel").trim())
//                                                            .email(value.getFirst("email").trim())
//                                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                                            .updatedBy(UUID.fromString(userId))
//                                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                                            .reqUpdatedIP(reqIp)
//                                                            .reqUpdatedPort(reqPort)
//                                                            .reqUpdatedBrowser(reqBrowser)
//                                                            .reqUpdatedOS(reqOs)
//                                                            .reqUpdatedDevice(reqDevice)
//                                                            .reqUpdatedReferer(reqReferer)
//                                                            .build();
//
//                                                    previousProfileEntity.setDeletedBy(UUID.fromString(userId));
//                                                    previousProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                                    previousProfileEntity.setReqDeletedIP(reqIp);
//                                                    previousProfileEntity.setReqDeletedPort(reqPort);
//                                                    previousProfileEntity.setReqDeletedBrowser(reqBrowser);
//                                                    previousProfileEntity.setReqDeletedOS(reqOs);
//                                                    previousProfileEntity.setReqDeletedDevice(reqDevice);
//                                                    previousProfileEntity.setReqDeletedReferer(reqReferer);
//
//                                                    sendFormData.add("docId", String.valueOf(updatedEntity.getImage()));
//
//                                                    // check nic number is unique
//                                                    UUID finalStudentUUID = studentUUID;
//                                                    UUID finalTeacherSpouseAsTeacherUUID = teacherSpouseAsTeacherUUID;
//                                                    return teacherSpouseProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
//                                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                                                            //check spouse profile is unique
//                                                            .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherSpouseUUID(), updatedEntity.getUuid())
//                                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Spouse Profile already exist"))))
//                                                            //checks if spouse uuid exists
//                                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
//                                                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
//                                                                                    //check if Gender Record Exists or Not
//                                                                                    .flatMap(teacherSpouseDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
//                                                                                                    //check if City Record Exists or Not
//                                                                                                    .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
//                                                                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
//                                                                                                                            //checks state uuid exists
//                                                                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
//                                                                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
//                                                                                                                                                    //checks countries uuid exists
//                                                                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
//                                                                                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
//                                                                                                                                                                            .flatMap(countryJsonNode -> {
//
//                                                                                                                                                                                // if teacher spouse entity isn't updated
////
//                                                                                                                                                                                if (teacherSpouseEntity.getStudentUUID() == finalStudentUUID && teacherSpouseEntity.getTeacherSpouseUUID() == finalTeacherSpouseAsTeacherUUID) {
//                                                                                                                                                                                    return updateFacadeRecord(teacherSpouseEntity, previousProfileEntity, updatedEntity, value.get("teacherSpouseContactNoDto"), sendFormData);
//                                                                                                                                                                                }
//
//                                                                                                                                                                                // else update all entities
//                                                                                                                                                                                else {
//                                                                                                                                                                                    TeacherSpouseEntity updatedTeacherSpouseEntity = TeacherSpouseEntity.builder()
//                                                                                                                                                                                            .uuid(teacherSpouseEntity.getUuid())
//                                                                                                                                                                                            .teacherUUID(teacherSpouseEntity.getTeacherUUID())
//                                                                                                                                                                                            .studentUUID(finalStudentUUID)
//                                                                                                                                                                                            .teacherSpouseUUID(finalTeacherSpouseAsTeacherUUID)
//                                                                                                                                                                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                                                                                                                                                                            .createdAt(teacherSpouseEntity.getCreatedAt())
//                                                                                                                                                                                            .createdBy(teacherSpouseEntity.getCreatedBy())
//                                                                                                                                                                                            .updatedBy(UUID.fromString(userId))
//                                                                                                                                                                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                                                                                                                                                                            .reqCreatedIP(teacherSpouseEntity.getReqCreatedIP())
//                                                                                                                                                                                            .reqCreatedPort(teacherSpouseEntity.getReqCreatedPort())
//                                                                                                                                                                                            .reqCreatedBrowser(teacherSpouseEntity.getReqCreatedBrowser())
//                                                                                                                                                                                            .reqCreatedOS(teacherSpouseEntity.getReqCreatedOS())
//                                                                                                                                                                                            .reqCreatedDevice(teacherSpouseEntity.getReqCreatedDevice())
//                                                                                                                                                                                            .reqCreatedReferer(teacherSpouseEntity.getReqCreatedReferer())
//                                                                                                                                                                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                                                                                                                                                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                                                                                                                                                                            .reqUpdatedIP(reqIp)
//                                                                                                                                                                                            .reqUpdatedPort(reqPort)
//                                                                                                                                                                                            .reqUpdatedBrowser(reqBrowser)
//                                                                                                                                                                                            .reqUpdatedOS(reqOs)
//                                                                                                                                                                                            .reqUpdatedDevice(reqDevice)
//                                                                                                                                                                                            .reqUpdatedReferer(reqReferer)
//                                                                                                                                                                                            .build();
//
//                                                                                                                                                                                    teacherSpouseEntity.setDeletedBy(UUID.fromString(userId));
//                                                                                                                                                                                    teacherSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                                                                                                                                                                    teacherSpouseEntity.setReqDeletedIP(reqIp);
//                                                                                                                                                                                    teacherSpouseEntity.setReqDeletedPort(reqPort);
//                                                                                                                                                                                    teacherSpouseEntity.setReqDeletedBrowser(reqBrowser);
//                                                                                                                                                                                    teacherSpouseEntity.setReqDeletedOS(reqOs);
//                                                                                                                                                                                    teacherSpouseEntity.setReqDeletedDevice(reqDevice);
//                                                                                                                                                                                    teacherSpouseEntity.setReqDeletedReferer(reqReferer);
//
//                                                                                                                                                                                    // if same teacher uuid is given as both teacher and teacher spouse's teacher uuid
//                                                                                                                                                                                    if (updatedTeacherSpouseEntity.getTeacherUUID().equals(updatedTeacherSpouseEntity.getTeacherSpouseUUID())) {
//                                                                                                                                                                                        return responseInfoMsg("The teacher spouse cannot be the same as the given teacher");
//                                                                                                                                                                                    }
//
//                                                                                                                                                                                    // if teacher spouse is student and teacher
//                                                                                                                                                                                    else if (updatedTeacherSpouseEntity.getStudentUUID() != null && updatedTeacherSpouseEntity.getTeacherSpouseUUID() != null) {
//                                                                                                                                                                                        return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherSpouseEntity.getTeacherUUID(), updatedTeacherSpouseEntity.getTeacherSpouseUUID(), updatedEntity.getUuid())
//                                                                                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
//                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherSpouseEntity.getTeacherUUID(), updatedTeacherSpouseEntity.getStudentUUID(), updatedTeacherSpouseEntity.getUuid())
//                                                                                                                                                                                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))))
//                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedTeacherSpouseEntity.getStudentUUID())
//                                                                                                                                                                                                        .flatMap(studentEntity -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedTeacherSpouseEntity.getTeacherSpouseUUID())
//                                                                                                                                                                                                                .flatMap(teacherRecord -> updateFacadeRecordWithSpouse(teacherSpouseEntity, updatedTeacherSpouseEntity, previousProfileEntity, updatedEntity, value.get("teacherSpouseContactNoDto"), sendFormData))
//                                                                                                                                                                                                                .switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
//                                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
//                                                                                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Student Record does not exist"))
//                                                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
//                                                                                                                                                                                                ));
//                                                                                                                                                                                    }
//
//                                                                                                                                                                                    // if teacher spouse is student
//                                                                                                                                                                                    else if (updatedTeacherSpouseEntity.getStudentUUID() != null) {
//                                                                                                                                                                                        return teacherSpouseRepository.findFirstByTeacherUUIDAndStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherSpouseEntity.getTeacherUUID(), updatedTeacherSpouseEntity.getStudentUUID(), updatedTeacherSpouseEntity.getUuid())
//                                                                                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Student"))
//                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedTeacherSpouseEntity.getStudentUUID())
//                                                                                                                                                                                                        .flatMap(studentEntity -> updateFacadeRecordWithSpouse(teacherSpouseEntity, updatedTeacherSpouseEntity, previousProfileEntity, updatedEntity, value.get("teacherSpouseContactNoDto"), sendFormData))
//                                                                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
//                                                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
//                                                                                                                                                                                                ));
//                                                                                                                                                                                    }
//
//
//                                                                                                                                                                                    // if teacher spouse is teacher
//                                                                                                                                                                                    else if (updatedTeacherSpouseEntity.getTeacherSpouseUUID() != null) {
//                                                                                                                                                                                        return teacherSpouseRepository.findFirstByTeacherUUIDAndTeacherSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherSpouseEntity.getTeacherUUID(), updatedTeacherSpouseEntity.getTeacherSpouseUUID(), updatedTeacherSpouseEntity.getUuid())
//                                                                                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Teacher Spouse Record Already Exists for Given Teacher"))
//                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedTeacherSpouseEntity.getTeacherSpouseUUID())
//                                                                                                                                                                                                        .flatMap(teacherRecord -> updateFacadeRecordWithSpouse(teacherSpouseEntity, updatedTeacherSpouseEntity, previousProfileEntity, updatedEntity, value.get("teacherSpouseContactNoDto"), sendFormData))
//                                                                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Spouse Teacher Record does not exist"))
//                                                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Spouse Teacher Record does not exist. Please contact developer."))
//                                                                                                                                                                                                ));
//                                                                                                                                                                                    }
//
//
//                                                                                                                                                                                    // else update the record
//                                                                                                                                                                                    else {
//                                                                                                                                                                                        return updateFacadeRecordWithSpouse(teacherSpouseEntity, updatedTeacherSpouseEntity, previousProfileEntity, updatedEntity, value.get("teacherSpouseContactNoDto"), sendFormData);
//                                                                                                                                                                                    }
//                                                                                                                                                                                }
//
//                                                                                                                                                                            })).switchIfEmpty(responseInfoMsg("Country does not exist"))
//                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
//                                                                                                                                                    )).switchIfEmpty(responseInfoMsg("State does not exist"))
//                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
//                                                                                                                            )).switchIfEmpty(responseInfoMsg("City does not exist"))
//                                                                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
//                                                                                                    ).switchIfEmpty(responseInfoMsg("Gender Record Does not Exist"))
//                                                                                                    .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist. Please contact developer."))
//                                                                                    )).switchIfEmpty(responseInfoMsg("Unable to upload the image"))
//                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload the image. Please contact developer."))
//                                                            ));
//                                                }).switchIfEmpty(responseInfoMsg("Spouse Profile Against the entered Teacher Spouse Record Does not exist"))
//                                                .onErrorResume(ex -> responseErrorMsg("Spouse Profile Against the entered Teacher Spouse Record Does not exist.Please Contact Developer."))
//                                ).switchIfEmpty(responseInfoMsg("Teacher Spouse Record Does not Exist."))
//                                .onErrorResume(ex -> responseErrorMsg("Teacher Spouse Record Does not Exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
//    }
//
//
//    public Mono<ServerResponse> updateFacadeRecord(TeacherSpouseEntity teacherSpouseEntity, TeacherSpouseProfileEntity previousProfileEntity, TeacherSpouseProfileEntity updatedEntity, List<String> teacherSpouseContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();
//
//        teacherSpouseContactList.removeIf(s -> s.equals(""));
//
//        if (!teacherSpouseContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
//                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Teacher Spouse Contact No Entity
//                                for (TeacherContactNoEntity teacherContact : existingContactList) {
//                                    teacherContact.setDeletedBy(updatedEntity.getUpdatedBy());
//                                    teacherContact.setDeletedAt(updatedEntity.getUpdatedAt());
//                                    teacherContact.setReqDeletedIP(updatedEntity.getReqUpdatedIP());
//                                    teacherContact.setReqDeletedPort(updatedEntity.getReqUpdatedPort());
//                                    teacherContact.setReqDeletedBrowser(updatedEntity.getReqUpdatedBrowser());
//                                    teacherContact.setReqDeletedOS(updatedEntity.getReqUpdatedOS());
//                                    teacherContact.setReqDeletedDevice(updatedEntity.getReqUpdatedDevice());
//                                    teacherContact.setReqDeletedReferer(updatedEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(teacherSpouseContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<TeacherContactNoEntity> teacherSpouseContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedTeacherMetaUUID = null;
//
//                                for (JsonNode spouseContact : contactNode) {
//
//                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .teacherMetaUUID(teacherSpouseEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    teacherSpouseContactNoList.add(teacherContactNoEntity);
//
//                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(teacherContactNoEntity.getContactNo());
//
//                                    updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Teacher Spouse Contact No List
//                                teacherSpouseContactNoList = teacherSpouseContactNoList.stream()
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
//                                List<TeacherContactNoEntity> finalTeacherSpouseContactNoList1 = teacherSpouseContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Teacher Spouse and Contact Type
//                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> teacherSpouseProfileRepository.save(previousProfileEntity)
//                                                                    .then(teacherSpouseProfileRepository.save(updatedEntity))
//                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherSpouseContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
//                                                                                    TeacherContactNoDto teacherSpouseContactNoDto = TeacherContactNoDto.builder()
//                                                                                            .contactNo(teacherContact.getContactNo())
//                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    teacherContactNoDto.add(teacherSpouseContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedEntity.getUpdatedBy().toString(),
//                                                                                                updatedEntity.getReqCompanyUUID().toString(), updatedEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(teacherSpouseEntity, updatedEntity, teacherContactNoDto)
//                                                                                                .flatMap(teacherSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherSpouseFacadeDto))
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
//            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousTeacherContactList -> {
//
//                        for (TeacherContactNoEntity teacherContact : previousTeacherContactList) {
//                            teacherContact.setDeletedBy(updatedEntity.getUpdatedBy());
//                            teacherContact.setDeletedAt(updatedEntity.getUpdatedAt());
//                            teacherContact.setReqDeletedIP(updatedEntity.getReqUpdatedIP());
//                            teacherContact.setReqDeletedPort(updatedEntity.getReqUpdatedPort());
//                            teacherContact.setReqDeletedBrowser(updatedEntity.getReqUpdatedBrowser());
//                            teacherContact.setReqDeletedOS(updatedEntity.getReqUpdatedOS());
//                            teacherContact.setReqDeletedDevice(updatedEntity.getReqUpdatedDevice());
//                            teacherContact.setReqDeletedReferer(updatedEntity.getReqUpdatedReferer());
//                        }
//
//                        return teacherContactNoRepository.saveAll(previousTeacherContactList)
//                                .collectList()
//                                .flatMap(teacherContactList -> teacherSpouseProfileRepository.save(previousProfileEntity)
//                                        .then(teacherSpouseProfileRepository.save(updatedEntity))
//                                        .flatMap(teacherSpouseProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedEntity.getUpdatedBy().toString(),
//                                                        updatedEntity.getReqCompanyUUID().toString(), updatedEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(teacherSpouseEntity, updatedEntity, teacherContactNoDto)
//                                                        .flatMap(teacherSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherSpouseFacadeDto))
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
//
//
//    public Mono<ServerResponse> updateFacadeRecordWithSpouse(TeacherSpouseEntity teacherSpouseEntity, TeacherSpouseEntity updatedTeacherSpouseEntity, TeacherSpouseProfileEntity previousProfileEntity, TeacherSpouseProfileEntity updatedEntity, List<String> teacherSpouseContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();
//
//        teacherSpouseContactList.removeIf(s -> s.equals(""));
//
//        if (!teacherSpouseContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
//                    .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Teacher Spouse Contact No Entity
//                                for (TeacherContactNoEntity teacherContact : existingContactList) {
//                                    teacherContact.setDeletedBy(updatedTeacherSpouseEntity.getUpdatedBy());
//                                    teacherContact.setDeletedAt(updatedTeacherSpouseEntity.getUpdatedAt());
//                                    teacherContact.setReqDeletedIP(updatedTeacherSpouseEntity.getReqUpdatedIP());
//                                    teacherContact.setReqDeletedPort(updatedTeacherSpouseEntity.getReqUpdatedPort());
//                                    teacherContact.setReqDeletedBrowser(updatedTeacherSpouseEntity.getReqUpdatedBrowser());
//                                    teacherContact.setReqDeletedOS(updatedTeacherSpouseEntity.getReqUpdatedOS());
//                                    teacherContact.setReqDeletedDevice(updatedTeacherSpouseEntity.getReqUpdatedDevice());
//                                    teacherContact.setReqDeletedReferer(updatedTeacherSpouseEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(teacherSpouseContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<TeacherContactNoEntity> teacherSpouseContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedTeacherMetaUUID = null;
//
//                                for (JsonNode spouseContact : contactNode) {
//
//                                    TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .teacherMetaUUID(teacherSpouseEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedTeacherSpouseEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedTeacherSpouseEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedTeacherSpouseEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedTeacherSpouseEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedTeacherSpouseEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedTeacherSpouseEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedTeacherSpouseEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedTeacherSpouseEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedTeacherSpouseEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    teacherSpouseContactNoList.add(teacherContactNoEntity);
//
//                                    contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(teacherContactNoEntity.getContactNo());
//
//                                    updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Teacher Spouse Contact No List
//                                teacherSpouseContactNoList = teacherSpouseContactNoList.stream()
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
//                                List<TeacherContactNoEntity> finalTeacherSpouseContactNoList1 = teacherSpouseContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Teacher Spouse and Contact Type
//                                                    return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.save(teacherSpouseEntity)
//                                                                    .then(teacherSpouseRepository.save(updatedTeacherSpouseEntity))
//                                                                    .then(teacherSpouseProfileRepository.save(previousProfileEntity))
//                                                                    .then(teacherSpouseProfileRepository.save(updatedEntity))
//                                                                    .then(teacherContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherSpouseContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
//                                                                                    TeacherContactNoDto teacherSpouseContactNoDto = TeacherContactNoDto.builder()
//                                                                                            .contactNo(teacherContact.getContactNo())
//                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    teacherContactNoDto.add(teacherSpouseContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedTeacherSpouseEntity.getUpdatedBy().toString(),
//                                                                                                updatedTeacherSpouseEntity.getReqCompanyUUID().toString(), updatedTeacherSpouseEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(updatedTeacherSpouseEntity, updatedEntity, teacherContactNoDto)
//                                                                                                .flatMap(teacherSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherSpouseFacadeDto))
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
//            return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousTeacherContactList -> {
//
//                        for (TeacherContactNoEntity teacherContact : previousTeacherContactList) {
//                            teacherContact.setDeletedBy(updatedTeacherSpouseEntity.getUpdatedBy());
//                            teacherContact.setDeletedAt(updatedTeacherSpouseEntity.getUpdatedAt());
//                            teacherContact.setReqDeletedIP(updatedTeacherSpouseEntity.getReqUpdatedIP());
//                            teacherContact.setReqDeletedPort(updatedTeacherSpouseEntity.getReqUpdatedPort());
//                            teacherContact.setReqDeletedBrowser(updatedTeacherSpouseEntity.getReqUpdatedBrowser());
//                            teacherContact.setReqDeletedOS(updatedTeacherSpouseEntity.getReqUpdatedOS());
//                            teacherContact.setReqDeletedDevice(updatedTeacherSpouseEntity.getReqUpdatedDevice());
//                            teacherContact.setReqDeletedReferer(updatedTeacherSpouseEntity.getReqUpdatedReferer());
//                        }
//
//                        return teacherContactNoRepository.saveAll(previousTeacherContactList)
//                                .collectList()
//                                .flatMap(teacherContactList -> teacherSpouseRepository.save(teacherSpouseEntity)
//                                        .then(teacherSpouseRepository.save(updatedTeacherSpouseEntity))
//                                        .then(teacherSpouseProfileRepository.save(previousProfileEntity))
//                                        .then(teacherSpouseProfileRepository.save(updatedEntity))
//                                        .flatMap(teacherSpouseProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedTeacherSpouseEntity.getUpdatedBy().toString(),
//                                                        updatedTeacherSpouseEntity.getReqCompanyUUID().toString(), updatedTeacherSpouseEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(updatedTeacherSpouseEntity, updatedEntity, teacherContactNoDto)
//                                                        .flatMap(teacherSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherSpouseFacadeDto))
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


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-spouse-teacher-spouse-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherSpouseUUID = UUID.fromString((serverRequest.pathVariable("teacherSpouseUUID")));
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

        return teacherSpouseRepository.findByUuidAndDeletedAtIsNull(teacherSpouseUUID)
                .flatMap(teacherSpouseEntity -> teacherSpouseProfileRepository.findFirstByTeacherSpouseUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                        .flatMap(teacherSpouseProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherSpouseEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherSpouseEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherSpouseEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherSpouseEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherSpouseEntity.setReqDeletedIP(reqIp);
                                    teacherSpouseEntity.setReqDeletedPort(reqPort);
                                    teacherSpouseEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherSpouseEntity.setReqDeletedOS(reqOs);
                                    teacherSpouseEntity.setReqDeletedDevice(reqDevice);
                                    teacherSpouseEntity.setReqDeletedReferer(reqReferer);

                                    teacherSpouseProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherSpouseProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherSpouseProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherSpouseProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherSpouseProfileEntity.setReqDeletedIP(reqIp);
                                    teacherSpouseProfileEntity.setReqDeletedPort(reqPort);
                                    teacherSpouseProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherSpouseProfileEntity.setReqDeletedOS(reqOs);
                                    teacherSpouseProfileEntity.setReqDeletedDevice(reqDevice);
                                    teacherSpouseProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        TeacherContactNoDto teacherSpouseContactNoDto = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherSpouseContactNoDto);
                                    }

                                    return teacherSpouseRepository.save(teacherSpouseEntity)
                                            .then(teacherSpouseProfileRepository.save(teacherSpouseProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherSpouseEntity, teacherSpouseProfileEntity, teacherContactNoDto)
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
