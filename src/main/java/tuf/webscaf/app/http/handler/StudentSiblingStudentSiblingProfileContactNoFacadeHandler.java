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
import tuf.webscaf.app.dbContext.master.dto.StudentSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentSiblingStudentSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentSiblingProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSiblingProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSiblingRepository;
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

@Tag(name = "studentSiblingStudentSiblingProfileContactNoFacade")
@Component
public class StudentSiblingStudentSiblingProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSiblingRepository studentSiblingRepository;

    @Autowired
    SlaveStudentSiblingRepository slaveStudentSiblingRepository;

    @Autowired
    SlaveStudentSiblingProfileRepository slaveStudentSiblingProfileRepository;

    @Autowired
    StudentSiblingProfileRepository studentSiblingProfileRepository;

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
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_student-sibling-student-sibling-profile-contact-nos_index")
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
            Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> slaveStudentSiblingStudentSiblingProfileContactNoFacadeDtoFlux = slaveStudentSiblingRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentSiblingStudentSiblingProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentSiblingProfileEntity -> slaveStudentSiblingRepository
                            .countStudentSiblingStudentSiblingProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> slaveStudentSiblingStudentSiblingProfileContactNoFacadeDtoFlux = slaveStudentSiblingRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentSiblingStudentSiblingProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentSiblingProfileEntity -> slaveStudentSiblingRepository
                            .countStudentSiblingStudentSiblingProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentSiblingProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSiblingProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-sibling-student-sibling-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("studentSiblingUUID")));

        return slaveStudentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                .flatMap(studentSiblingEntity -> slaveStudentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                        .flatMap(studentSiblingProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNo -> {
                                    List<SlaveStudentContactNoFacadeDto> studentContactNoDto = new ArrayList<>();

                                    for (SlaveStudentContactNoEntity studentContact : studentContactNo) {
                                        SlaveStudentContactNoFacadeDto studentSiblingContactNoDto = SlaveStudentContactNoFacadeDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentSiblingContactNoDto);
                                    }

                                    return showFacadeDto(studentSiblingEntity, studentSiblingProfileEntity, studentContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Sibling Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Sibling Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Sibling Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Sibling Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto> showFacadeDto(SlaveStudentSiblingEntity slaveStudentSiblingEntity, SlaveStudentSiblingProfileEntity slaveStudentSiblingProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoFacadeDto) {

        SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto facadeDto = SlaveStudentSiblingStudentSiblingProfileContactNoFacadeDto.builder()
                .id(slaveStudentSiblingEntity.getId())
                .uuid(slaveStudentSiblingEntity.getUuid())
                .version(slaveStudentSiblingEntity.getVersion())
                .status(slaveStudentSiblingEntity.getStatus())
                .studentUUID(slaveStudentSiblingEntity.getStudentUUID())
                .studentSiblingAsStudentUUID(slaveStudentSiblingEntity.getStudentUUID())
                .studentSiblingUUID(slaveStudentSiblingEntity.getUuid())
                .image(slaveStudentSiblingProfileEntity.getImage())
                .name(slaveStudentSiblingProfileEntity.getName())
                .nic(slaveStudentSiblingProfileEntity.getNic())
                .age(slaveStudentSiblingProfileEntity.getAge())
                .officialTel(slaveStudentSiblingProfileEntity.getOfficialTel())
                .cityUUID(slaveStudentSiblingProfileEntity.getCityUUID())
                .stateUUID(slaveStudentSiblingProfileEntity.getStateUUID())
                .countryUUID(slaveStudentSiblingProfileEntity.getCountryUUID())
                .genderUUID(slaveStudentSiblingProfileEntity.getGenderUUID())
                .email(slaveStudentSiblingProfileEntity.getEmail())
                .studentSiblingContactNoDto(slaveStudentContactNoFacadeDto)
                .createdAt(slaveStudentSiblingEntity.getCreatedAt())
                .createdBy(slaveStudentSiblingEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentSiblingEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentSiblingEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentSiblingEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentSiblingEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentSiblingEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentSiblingEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentSiblingEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentSiblingEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentSiblingEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentSiblingEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentSiblingEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentSiblingEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentSiblingEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentSiblingEntity.getReqUpdatedReferer())
                .editable(slaveStudentSiblingEntity.getEditable())
                .deletable(slaveStudentSiblingEntity.getDeletable())
                .archived(slaveStudentSiblingEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentSiblingStudentSiblingProfileContactNoFacadeDto> facadeDto(StudentSiblingEntity studentSiblingEntity, StudentSiblingProfileEntity studentSiblingProfileEntity, List<StudentContactNoDto> studentSiblingContactNoDto) {

        StudentSiblingStudentSiblingProfileContactNoFacadeDto facadeDto = StudentSiblingStudentSiblingProfileContactNoFacadeDto.builder()
                .id(studentSiblingEntity.getId())
                .uuid(studentSiblingEntity.getUuid())
                .version(studentSiblingEntity.getVersion())
                .status(studentSiblingEntity.getStatus())
                .studentUUID(studentSiblingEntity.getStudentUUID())
                .studentSiblingAsStudentUUID(studentSiblingEntity.getStudentUUID())
                .studentSiblingUUID(studentSiblingEntity.getUuid())
                .image(studentSiblingProfileEntity.getImage())
                .name(studentSiblingProfileEntity.getName())
                .nic(studentSiblingProfileEntity.getNic())
                .age(studentSiblingProfileEntity.getAge())
                .officialTel(studentSiblingProfileEntity.getOfficialTel())
                .cityUUID(studentSiblingProfileEntity.getCityUUID())
                .stateUUID(studentSiblingProfileEntity.getStateUUID())
                .countryUUID(studentSiblingProfileEntity.getCountryUUID())
                .genderUUID(studentSiblingProfileEntity.getGenderUUID())
                .email(studentSiblingProfileEntity.getEmail())
                .studentSiblingContactNoDto(studentSiblingContactNoDto)
                .createdAt(studentSiblingEntity.getCreatedAt())
                .createdBy(studentSiblingEntity.getCreatedBy())
                .reqCompanyUUID(studentSiblingEntity.getReqCompanyUUID())
                .reqBranchUUID(studentSiblingEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentSiblingEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentSiblingEntity.getReqCreatedIP())
                .reqCreatedPort(studentSiblingEntity.getReqCreatedPort())
                .reqCreatedOS(studentSiblingEntity.getReqCreatedOS())
                .reqCreatedDevice(studentSiblingEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentSiblingEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentSiblingEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentSiblingEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentSiblingEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentSiblingEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentSiblingEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentSiblingEntity.getReqUpdatedReferer())
                .editable(studentSiblingEntity.getEditable())
                .deletable(studentSiblingEntity.getDeletable())
                .archived(studentSiblingEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentSiblingProfileContactNoFacadeDto> updatedFacadeDto(StudentSiblingEntity studentSiblingEntity, StudentSiblingProfileEntity studentSiblingProfileEntity, List<StudentContactNoDto> studentSiblingContactNoDto) {

        StudentSiblingProfileContactNoFacadeDto facadeDto = StudentSiblingProfileContactNoFacadeDto.builder()
                .id(studentSiblingEntity.getId())
                .uuid(studentSiblingEntity.getUuid())
                .version(studentSiblingEntity.getVersion())
                .status(studentSiblingEntity.getStatus())
                .studentSiblingAsStudentUUID(studentSiblingEntity.getStudentUUID())
                .studentSiblingUUID(studentSiblingEntity.getUuid())
                .image(studentSiblingProfileEntity.getImage())
                .name(studentSiblingProfileEntity.getName())
                .nic(studentSiblingProfileEntity.getNic())
                .age(studentSiblingProfileEntity.getAge())
                .officialTel(studentSiblingProfileEntity.getOfficialTel())
                .cityUUID(studentSiblingProfileEntity.getCityUUID())
                .stateUUID(studentSiblingProfileEntity.getStateUUID())
                .countryUUID(studentSiblingProfileEntity.getCountryUUID())
                .genderUUID(studentSiblingProfileEntity.getGenderUUID())
                .email(studentSiblingProfileEntity.getEmail())
                .studentSiblingContactNoDto(studentSiblingContactNoDto)
                .createdAt(studentSiblingEntity.getCreatedAt())
                .createdBy(studentSiblingEntity.getCreatedBy())
                .updatedAt(studentSiblingEntity.getUpdatedAt())
                .updatedBy(studentSiblingEntity.getUpdatedBy())
                .reqCompanyUUID(studentSiblingProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(studentSiblingProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentSiblingProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentSiblingProfileEntity.getReqCreatedIP())
                .reqCreatedPort(studentSiblingProfileEntity.getReqCreatedPort())
                .reqCreatedOS(studentSiblingProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(studentSiblingProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentSiblingProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentSiblingProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentSiblingProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentSiblingProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentSiblingProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentSiblingProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentSiblingProfileEntity.getReqUpdatedReferer())
                .editable(studentSiblingProfileEntity.getEditable())
                .deletable(studentSiblingProfileEntity.getDeletable())
                .archived(studentSiblingProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-sibling-student-sibling-profile-contact-nos_store")
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

//                   Student's sibling is also a student
                    UUID studentSiblingUUID = null;
                    if ((value.containsKey("studentSiblingUUID") && (value.getFirst("studentSiblingUUID") != ""))) {
                        studentSiblingUUID = UUID.fromString(value.getFirst("studentSiblingUUID").trim());
                    }

                    StudentSiblingEntity studentSiblingEntity = StudentSiblingEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .studentSiblingUUID(studentSiblingUUID)
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
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentSiblingEntity.getStudentUUID())
                            //check if Student Sibling Record Already Exists Against the same student
                            .flatMap(studentEntity -> {

                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                //Building Student Sibling Profile Record
                                StudentSiblingProfileEntity studentSiblingProfileEntity = StudentSiblingProfileEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .studentSiblingUUID(studentSiblingEntity.getUuid())
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

                                sendFormData.add("docId", String.valueOf(studentSiblingProfileEntity.getImage()));

                                //check if Gender Record Exists or Not
                                return genderRepository.findByUuidAndDeletedAtIsNull(studentSiblingProfileEntity.getGenderUUID())
                                        //check if City Record Exists or Not
                                        .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentSiblingProfileEntity.getCityUUID())
                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                        //check if State Record Exists or not
                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentSiblingProfileEntity.getStateUUID())
                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                        //check if Country Record Exists or not
                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentSiblingProfileEntity.getCountryUUID())
                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                        //check if NIC Is Unique Against Student Sibling
                                                                                        .flatMap(checkNIC -> studentSiblingProfileRepository.findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingProfileEntity.getNic(), studentSiblingProfileEntity.getStudentSiblingUUID())
                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                        //check if Sibling Profile Already Exists Against Student Sibling
                                                                                        .switchIfEmpty(Mono.defer(() -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingProfileEntity.getStudentSiblingUUID())
                                                                                                .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Sibling Profile already exist"))))
                                                                                        //check if Document Record Exists or not
                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentSiblingProfileEntity.getImage())
                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                        .flatMap(documentEntity -> {

                                                                                                                    // if student uuid is given
                                                                                                                    if (studentSiblingEntity.getStudentSiblingUUID() != null) {

                                                                                                                        // checks if record already exists for student
                                                                                                                        return studentSiblingRepository.findFirstByStudentUUIDAndStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getStudentUUID(), studentSiblingEntity.getStudentUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Student Sibling Record Already Exists for Given Student"))
                                                                                                                                // checks if student uuid exists
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(studentSiblingEntity.getStudentUUID())
                                                                                                                                        .flatMap(saveStudentEntity -> storeFacadeRecord(studentSiblingEntity, studentSiblingProfileEntity, value.get("studentSiblingContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // else store the record
                                                                                                                    else {
                                                                                                                        return storeFacadeRecord(studentSiblingEntity, studentSiblingProfileEntity, value.get("studentSiblingContactNoDto"), sendFormData);
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


    public Mono<ServerResponse> storeFacadeRecord(StudentSiblingEntity studentSiblingEntity, StudentSiblingProfileEntity studentSiblingProfileEntity, List<String> studentSiblingContactList, MultiValueMap<String, String> sendFormData) {

        //check if Contact Category is Sibling
        return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("sibling")
                .flatMap(contactCategoryEntity -> {
                    //Creating an empty list to add student Contact No Records
                    List<StudentContactNoEntity> studentSiblingContactNoList = new ArrayList<>();

                    // Creating an empty list to add contact Type UUID's
                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                    // Creating an empty list to add contact No's
                    List<String> contactNoList = new ArrayList<>();


                    JsonNode contactNode = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contactNode = objectMapper.readTree(studentSiblingContactList.toString());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert contactNode != null;


                    UUID studentMetaUUID = null;
                    UUID contactCategoryUUID = null;

                    //iterating over the json node from front and setting contact No's
                    for (JsonNode siblingContact : contactNode) {

                        StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                .builder()
                                .contactTypeUUID(UUID.fromString(siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                .contactNo(siblingContact.get("contactNo").toString().replaceAll("\"", ""))
                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                .studentMetaUUID(studentSiblingEntity.getUuid())
                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                .createdBy(studentSiblingEntity.getCreatedBy())
                                .reqCompanyUUID(studentSiblingEntity.getReqCompanyUUID())
                                .reqBranchUUID(studentSiblingEntity.getReqBranchUUID())
                                .reqCreatedIP(studentSiblingEntity.getReqCreatedIP())
                                .reqCreatedPort(studentSiblingEntity.getReqCreatedPort())
                                .reqCreatedBrowser(studentSiblingEntity.getReqCreatedBrowser())
                                .reqCreatedOS(studentSiblingEntity.getReqCreatedOS())
                                .reqCreatedDevice(studentSiblingEntity.getReqCreatedDevice())
                                .reqCreatedReferer(studentSiblingEntity.getReqCreatedReferer())
                                .build();

                        studentSiblingContactNoList.add(studentContactNoEntity);

                        contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                        contactNoList.add(studentContactNoEntity.getContactNo());
                        studentMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                        contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();
                    }

                    //Getting Distinct Values Fom the List of Student Sibling Contact No List
                    studentSiblingContactNoList = studentSiblingContactNoList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    //Getting Distinct Values Fom the List of Contact Type UUID
                    contactTypeUUIDList = contactTypeUUIDList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    // Creating an empty list to add contact No's and returning dto with response
                    List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                    if (!studentSiblingContactNoList.isEmpty()) {

                        UUID finalStdMetaUUID = studentMetaUUID;

                        UUID finalContactCategoryUUID = contactCategoryUUID;

                        List<StudentContactNoEntity> finalStudentSiblingContactNoList = studentSiblingContactNoList;

                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                .collectList()
                                .flatMap(contactTypeEntityList -> {

                                    if (!contactTypeEntityList.isEmpty()) {

                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                            return responseInfoMsg("Contact Type Does not Exist");
                                        } else {
                                            //check if Contact No Record Already Exists against Student Sibling and Contact Type
                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStdMetaUUID)
                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                    .switchIfEmpty(Mono.defer(() -> studentSiblingRepository.save(studentSiblingEntity)
                                                            .then(studentSiblingProfileRepository.save(studentSiblingProfileEntity))
                                                            .then(studentContactNoRepository.saveAll(finalStudentSiblingContactNoList)
                                                                    .collectList())
                                                            .flatMap(mthContactNo -> {

                                                                for (StudentContactNoEntity studentContact : mthContactNo) {
                                                                    StudentContactNoDto studentSiblingContactNoDto = StudentContactNoDto.builder()
                                                                            .contactNo(studentContact.getContactNo())
                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                            .build();

                                                                    studentContactNoDto.add(studentSiblingContactNoDto);
                                                                }

                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", studentSiblingEntity.getCreatedBy().toString(),
                                                                                studentSiblingEntity.getReqCompanyUUID().toString(), studentSiblingEntity.getReqBranchUUID().toString())
                                                                        .flatMap(docUpdate -> facadeDto(studentSiblingEntity, studentSiblingProfileEntity, studentContactNoDto)
                                                                                .flatMap(studentSiblingFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentSiblingFacadeDto))
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
                        //if Contact No List is empty then store student Sibling and Student Sibling Profile
                        return studentSiblingRepository.save(studentSiblingEntity)
                                //Save Student Sibling Profile Entity
                                .then(studentSiblingProfileRepository.save(studentSiblingProfileEntity))
                                //update Document Status After Storing record
                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", studentSiblingEntity.getCreatedBy().toString(),
                                                studentSiblingEntity.getReqCompanyUUID().toString(), studentSiblingEntity.getReqBranchUUID().toString())
                                        .flatMap(docUpdate -> facadeDto(studentSiblingEntity, studentSiblingProfileEntity, studentContactNoDto)
                                                .flatMap(studentSiblingFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentSiblingFacadeDto))
                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                    }
                });

    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-sibling-student-sibling-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("studentSiblingUUID")));
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
                .flatMap(value -> studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                        .flatMap(studentSiblingEntity -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentSiblingProfileEntity updatedEntity = StudentSiblingProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentSiblingUUID(previousProfileEntity.getStudentSiblingUUID())
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
                                    return studentSiblingProfileRepository.findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentSiblingUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check sibling profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSiblingUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Sibling Profile already exist"))))
                                            //checks if sibling uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //check if Gender Record Exists or Not
                                                            .flatMap(studentSiblingDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
                                                                                                                                List<String> studentSiblingContactList = value.get("studentSiblingContactNoDto");
                                                                                                                                List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                                studentSiblingContactList.removeIf(s -> s.equals(""));

                                                                                                                                if (!studentSiblingContactList.isEmpty()) {
                                                                                                                                    return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("sibling")
                                                                                                                                            .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSiblingUUID)
                                                                                                                                                    .collectList()
                                                                                                                                                    .flatMap(existingContactList -> {

                                                                                                                                                        //Removing Already existing Student Sibling Contact No Entity
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
                                                                                                                                                            contactNode = new ObjectMapper().readTree(studentSiblingContactList.toString());
                                                                                                                                                        } catch (JsonProcessingException e) {
                                                                                                                                                            e.printStackTrace();
                                                                                                                                                        }

                                                                                                                                                        //New Contact No list for adding values after building entity
                                                                                                                                                        List<StudentContactNoEntity> stdSiblingContactNoList = new ArrayList<>();

                                                                                                                                                        List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                        List<String> contactNoList = new ArrayList<>();

                                                                                                                                                        UUID updatedStdMetaUUID = null;

                                                                                                                                                        assert contactNode != null;
                                                                                                                                                        for (JsonNode siblingContact : contactNode) {

                                                                                                                                                            StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                    .builder()
                                                                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                                                                    .contactTypeUUID(UUID.fromString(siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                    .contactNo(siblingContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                    .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                    .studentMetaUUID(studentSiblingUUID)
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

                                                                                                                                                            stdSiblingContactNoList.add(studentContactNoEntity);

                                                                                                                                                            contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                            contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                            updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                        }

                                                                                                                                                        //Getting Distinct Values Fom the List of Student Sibling Contact No List
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

                                                                                                                                                        List<StudentContactNoEntity> finalStudentSiblingContactNoList1 = stdSiblingContactNoList;

                                                                                                                                                        List<String> finalContactNoList = contactNoList;

                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                .collectList()
                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                        } else {

                                                                                                                                                                            //check if Contact No Record Already Exists against Student Sibling and Contact Type
                                                                                                                                                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> studentSiblingProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                            .then(studentSiblingProfileRepository.save(updatedEntity))
                                                                                                                                                                                            .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                            .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentSiblingContactNoList1)
                                                                                                                                                                                                    .collectList()
                                                                                                                                                                                                    .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                        for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
                                                                                                                                                                                                            StudentContactNoDto studentSiblingContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                    .contactNo(studentContact.getContactNo())
                                                                                                                                                                                                                    .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                            studentContactNoDto.add(studentSiblingContactNoDto);
                                                                                                                                                                                                        }

                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                .flatMap(docUpdate -> updatedFacadeDto(studentSiblingEntity, updatedEntity, studentContactNoDto)
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
                                                                                                                                    return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSiblingUUID)
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
                                                                                                                                                        .flatMap(studentContactList -> studentSiblingProfileRepository.save(previousProfileEntity)
                                                                                                                                                                .then(studentSiblingProfileRepository.save(updatedEntity))
                                                                                                                                                                .flatMap(StudentSiblingProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                        .flatMap(docUpdateEntity -> updatedFacadeDto(studentSiblingEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                                .flatMap(StudentSiblingFacadeDto -> responseSuccessMsg("Record Updated Successfully", StudentSiblingFacadeDto))
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
                                }).switchIfEmpty(responseInfoMsg("Sibling Profile Against the entered Student Sibling Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Sibling Profile Against the entered Student Sibling Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Sibling Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Sibling Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

//    @AuthHasPermission(value = "academic_api_v1_facade_student-sibling-student-sibling-profile-contact-nos_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("studentSiblingUUID")));
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
//                .flatMap(value -> studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
//                        .flatMap(studentSiblingEntity -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingUUID)
//                                .flatMap(previousProfileEntity -> {
//
//                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();
//
////                                    Student's sibling is also a student
//                                    
//                                    UUID studentSiblingAsStudentUUID = null;
//                                    if ((value.containsKey("studentSiblingUUID") && (value.getFirst("studentSiblingUUID") != ""))) {
//                                        studentSiblingAsStudentUUID = UUID.fromString(value.getFirst("studentSiblingUUID").trim());
//                                    }
//
//                                    StudentSiblingEntity updatedStudentSiblingEntity = StudentSiblingEntity.builder()
//                                            .uuid(studentSiblingEntity.getUuid())
//                                            .studentUUID(studentSiblingEntity.getStudentSiblingUUID())
//                                            .studentSiblingUUID(studentSiblingAsStudentUUID)
//                                            .status(Boolean.valueOf(value.getFirst("status")))
//                                            .createdAt(studentSiblingEntity.getCreatedAt())
//                                            .createdBy(studentSiblingEntity.getCreatedBy())
//                                            .updatedBy(UUID.fromString(userId))
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(studentSiblingEntity.getReqCreatedIP())
//                                            .reqCreatedPort(studentSiblingEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(studentSiblingEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(studentSiblingEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(studentSiblingEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(studentSiblingEntity.getReqCreatedReferer())
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
//                                    studentSiblingEntity.setDeletedBy(UUID.fromString(userId));
//                                    studentSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                    studentSiblingEntity.setReqDeletedIP(reqIp);
//                                    studentSiblingEntity.setReqDeletedPort(reqPort);
//                                    studentSiblingEntity.setReqDeletedBrowser(reqBrowser);
//                                    studentSiblingEntity.setReqDeletedOS(reqOs);
//                                    studentSiblingEntity.setReqDeletedDevice(reqDevice);
//                                    studentSiblingEntity.setReqDeletedReferer(reqReferer);
//
//                                    StudentSiblingProfileEntity updatedEntity = StudentSiblingProfileEntity.builder()
//                                            .uuid(previousProfileEntity.getUuid())
//                                            .studentSiblingUUID(previousProfileEntity.getStudentSiblingUUID())
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
//                                    return studentSiblingProfileRepository.findFirstByNicAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentSiblingUUID(), updatedEntity.getUuid())
//                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                                            //check sibling profile is unique
//                                            .switchIfEmpty(Mono.defer(() -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSiblingUUID(), updatedEntity.getUuid())
//                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Sibling Profile already exist"))))
//                                            //checks if sibling uuid exists
//                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
//                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
//                                                            //check if Gender Record Exists or Not
//                                                            .flatMap(studentSiblingDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
//                                                                                                                                if (updatedStudentSiblingEntity.getStudentSiblingUUID() != null) {
//                                                                                                                                    // checks if record already exists for student
//                                                                                                                                    return studentSiblingRepository.findFirstByStudentUUIDAndStudentSiblingUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStudentSiblingEntity.getStudentUUID(), updatedStudentSiblingEntity.getStudentUUID(), updatedStudentSiblingEntity.getUuid())
//                                                                                                                                            .flatMap(recordAlreadyExists -> responseInfoMsg("Student Sibling Record Already Exists for Given Student"))
//                                                                                                                                            // checks if student uuid exists
//                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedStudentSiblingEntity.getStudentUUID())
//                                                                                                                                                    .flatMap(studentEntity -> updateFacadeRecord(studentSiblingEntity, updatedStudentSiblingEntity, previousProfileEntity, updatedEntity, value.get("studentSiblingContactNoDto"), sendFormData))
//                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
//                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
//                                                                                                                                            ));
//                                                                                                                                }
//
//                                                                                                                                // else update the record
//                                                                                                                                else {
//                                                                                                                                    return updateFacadeRecord(studentSiblingEntity, updatedStudentSiblingEntity, previousProfileEntity, updatedEntity, value.get("studentSiblingContactNoDto"), sendFormData);
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
//                                }).switchIfEmpty(responseInfoMsg("Sibling Profile Against the entered Student Sibling Record Does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Sibling Profile Against the entered Student Sibling Record Does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Student Sibling Record Does not Exist."))
//                        .onErrorResume(ex -> responseErrorMsg("Student Sibling Record Does not Exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
//    }


//    public Mono<ServerResponse> updateFacadeRecord(StudentSiblingEntity studentSiblingEntity, StudentSiblingEntity updatedStudentSiblingEntity, StudentSiblingProfileEntity previousProfileEntity, StudentSiblingProfileEntity updatedEntity, List<String> studentSiblingContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();
//
//        studentSiblingContactList.removeIf(s -> s.equals(""));
//
//        if (!studentSiblingContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("sibling")
//                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Student Sibling Contact No Entity
//                                for (StudentContactNoEntity studentContact : existingContactList) {
//                                    studentContact.setDeletedBy(updatedStudentSiblingEntity.getUpdatedBy());
//                                    studentContact.setDeletedAt(updatedStudentSiblingEntity.getUpdatedAt());
//                                    studentContact.setReqDeletedIP(updatedStudentSiblingEntity.getReqUpdatedIP());
//                                    studentContact.setReqDeletedPort(updatedStudentSiblingEntity.getReqUpdatedPort());
//                                    studentContact.setReqDeletedBrowser(updatedStudentSiblingEntity.getReqUpdatedBrowser());
//                                    studentContact.setReqDeletedOS(updatedStudentSiblingEntity.getReqUpdatedOS());
//                                    studentContact.setReqDeletedDevice(updatedStudentSiblingEntity.getReqUpdatedDevice());
//                                    studentContact.setReqDeletedReferer(updatedStudentSiblingEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(studentSiblingContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<StudentContactNoEntity> studentSiblingContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedStdMetaUUID = null;
//
//                                for (JsonNode siblingContact : contactNode) {
//
//                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(siblingContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .studentMetaUUID(studentSiblingEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedStudentSiblingEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedStudentSiblingEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedStudentSiblingEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedStudentSiblingEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedStudentSiblingEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedStudentSiblingEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedStudentSiblingEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedStudentSiblingEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedStudentSiblingEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    studentSiblingContactNoList.add(studentContactNoEntity);
//
//                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(studentContactNoEntity.getContactNo());
//
//                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Student Sibling Contact No List
//                                studentSiblingContactNoList = studentSiblingContactNoList.stream()
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
//                                List<StudentContactNoEntity> finalStudentSiblingContactNoList1 = studentSiblingContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Student Sibling and Contact Type
//                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> studentSiblingRepository.save(studentSiblingEntity)
//                                                                    .then(studentSiblingRepository.save(updatedStudentSiblingEntity))
//                                                                    .then(studentSiblingProfileRepository.save(previousProfileEntity))
//                                                                    .then(studentSiblingProfileRepository.save(updatedEntity))
//                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentSiblingContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
//                                                                                    StudentContactNoDto studentSiblingContactNoDto = StudentContactNoDto.builder()
//                                                                                            .contactNo(studentContact.getContactNo())
//                                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    studentContactNoDto.add(studentSiblingContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedStudentSiblingEntity.getUpdatedBy().toString(),
//                                                                                                updatedStudentSiblingEntity.getReqCompanyUUID().toString(), updatedStudentSiblingEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(studentSiblingEntity, updatedEntity, studentContactNoDto)
//                                                                                                .flatMap(studentSiblingFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentSiblingFacadeDto))
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
//            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousStdContactList -> {
//
//                        for (StudentContactNoEntity studentContact : previousStdContactList) {
//                            studentContact.setDeletedBy(updatedStudentSiblingEntity.getUpdatedBy());
//                            studentContact.setDeletedAt(updatedStudentSiblingEntity.getUpdatedAt());
//                            studentContact.setReqDeletedIP(updatedStudentSiblingEntity.getReqUpdatedIP());
//                            studentContact.setReqDeletedPort(updatedStudentSiblingEntity.getReqUpdatedPort());
//                            studentContact.setReqDeletedBrowser(updatedStudentSiblingEntity.getReqUpdatedBrowser());
//                            studentContact.setReqDeletedOS(updatedStudentSiblingEntity.getReqUpdatedOS());
//                            studentContact.setReqDeletedDevice(updatedStudentSiblingEntity.getReqUpdatedDevice());
//                            studentContact.setReqDeletedReferer(updatedStudentSiblingEntity.getReqUpdatedReferer());
//                        }
//
//                        return studentContactNoRepository.saveAll(previousStdContactList)
//                                .collectList()
//                                .flatMap(studentContactList -> studentSiblingRepository.save(studentSiblingEntity)
//                                        .then(studentSiblingRepository.save(updatedStudentSiblingEntity))
//                                        .then(studentSiblingProfileRepository.save(previousProfileEntity))
//                                        .then(studentSiblingProfileRepository.save(updatedEntity))
//                                        .flatMap(studentSiblingProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedStudentSiblingEntity.getUpdatedBy().toString(),
//                                                        updatedStudentSiblingEntity.getReqCompanyUUID().toString(), updatedStudentSiblingEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(studentSiblingEntity, updatedEntity, studentContactNoDto)
//                                                        .flatMap(studentSiblingFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentSiblingFacadeDto))
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


    @AuthHasPermission(value = "academic_api_v1_facade_student-sibling-student-sibling-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSiblingUUID = UUID.fromString((serverRequest.pathVariable("studentSiblingUUID")));
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

        return studentSiblingRepository.findByUuidAndDeletedAtIsNull(studentSiblingUUID)
                .flatMap(studentSiblingEntity -> studentSiblingProfileRepository.findFirstByStudentSiblingUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                        .flatMap(studentSiblingProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSiblingEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentSiblingEntity.setDeletedBy(UUID.fromString(userId));
                                    studentSiblingEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentSiblingEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentSiblingEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentSiblingEntity.setReqDeletedIP(reqIp);
                                    studentSiblingEntity.setReqDeletedPort(reqPort);
                                    studentSiblingEntity.setReqDeletedBrowser(reqBrowser);
                                    studentSiblingEntity.setReqDeletedOS(reqOs);
                                    studentSiblingEntity.setReqDeletedDevice(reqDevice);
                                    studentSiblingEntity.setReqDeletedReferer(reqReferer);

                                    studentSiblingProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    studentSiblingProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentSiblingProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentSiblingProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentSiblingProfileEntity.setReqDeletedIP(reqIp);
                                    studentSiblingProfileEntity.setReqDeletedPort(reqPort);
                                    studentSiblingProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    studentSiblingProfileEntity.setReqDeletedOS(reqOs);
                                    studentSiblingProfileEntity.setReqDeletedDevice(reqDevice);
                                    studentSiblingProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        StudentContactNoDto studentSiblingContactNoDto = StudentContactNoDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentSiblingContactNoDto);
                                    }

                                    return studentSiblingRepository.save(studentSiblingEntity)
                                            .then(studentSiblingProfileRepository.save(studentSiblingProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentSiblingEntity, studentSiblingProfileEntity, studentContactNoDto)
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
