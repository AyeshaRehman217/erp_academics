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
import tuf.webscaf.app.dbContext.master.dto.StudentFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentFatherStudentFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentFatherStudentFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentFatherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherRepository;
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

@Tag(name = "studentFatherStudentFatherProfileContactNoFacade")
@Component
public class StudentFatherStudentFatherProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @Autowired
    SlaveStudentFatherRepository slaveStudentFatherRepository;

    @Autowired
    SlaveStudentFatherProfileRepository slaveStudentFatherProfileRepository;

    @Autowired
    StudentFatherProfileRepository studentFatherProfileRepository;

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
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;


    @AuthHasPermission(value = "academic_api_v1_facade_student-father-student-father-profile-contact-nos_index")
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
            Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> slaveStudentFatherStudentFatherProfileContactNoFacadeDtoFlux = slaveStudentFatherRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentFatherStudentFatherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentFatherProfileEntity -> slaveStudentFatherRepository
                            .countStudentFatherStudentFatherProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentFatherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> slaveStudentFatherStudentFatherProfileContactNoFacadeDtoFlux = slaveStudentFatherRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentFatherStudentFatherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentFatherProfileEntity -> slaveStudentFatherRepository
                            .countStudentFatherStudentFatherProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentFatherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentFatherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }
    
    @AuthHasPermission(value = "academic_api_v1_facade_student-father-student-father-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentFatherUUID = UUID.fromString((serverRequest.pathVariable("studentFatherUUID")));

        return slaveStudentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                .flatMap(StudentFatherEntity -> slaveStudentFatherProfileRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(StudentFatherEntity.getUuid())
                        .flatMap(StudentFatherProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(StudentFatherEntity.getUuid())
                                .collectList()
                                .flatMap(StudentContactNo -> {
                                    List<SlaveStudentContactNoFacadeDto> studentContactNoDto = new ArrayList<>();

                                    for (SlaveStudentContactNoEntity studentContact : StudentContactNo) {
                                        SlaveStudentContactNoFacadeDto StudentFatherContactNoDto = SlaveStudentContactNoFacadeDto
                                                .builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(StudentFatherContactNoDto);
                                    }

                                    return showFacadeDto(StudentFatherEntity, StudentFatherProfileEntity, studentContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Father Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Father Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Father Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Father Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentFatherStudentFatherProfileContactNoFacadeDto> showFacadeDto(SlaveStudentFatherEntity slaveStudentFatherEntity, SlaveStudentFatherProfileEntity slaveStudentFatherProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoFacadeDto) {

        SlaveStudentFatherStudentFatherProfileContactNoFacadeDto facadeDto = SlaveStudentFatherStudentFatherProfileContactNoFacadeDto.builder()
                .id(slaveStudentFatherEntity.getId())
                .uuid(slaveStudentFatherEntity.getUuid())
                .version(slaveStudentFatherEntity.getVersion())
                .status(slaveStudentFatherEntity.getStatus())
                .studentUUID(slaveStudentFatherEntity.getStudentUUID())
                .studentFatherUUID(slaveStudentFatherEntity.getUuid())
                .image(slaveStudentFatherProfileEntity.getImage())
                .name(slaveStudentFatherProfileEntity.getName())
                .nic(slaveStudentFatherProfileEntity.getNic())
                .age(slaveStudentFatherProfileEntity.getAge())
                .officialTel(slaveStudentFatherProfileEntity.getOfficialTel())
                .cityUUID(slaveStudentFatherProfileEntity.getCityUUID())
                .stateUUID(slaveStudentFatherProfileEntity.getStateUUID())
                .countryUUID(slaveStudentFatherProfileEntity.getCountryUUID())
                .noOfDependents(slaveStudentFatherProfileEntity.getNoOfDependents())
                .email(slaveStudentFatherProfileEntity.getEmail())
                .studentFatherContactNoDto(slaveStudentContactNoFacadeDto)
                .createdAt(slaveStudentFatherEntity.getCreatedAt())
                .createdBy(slaveStudentFatherEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentFatherEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentFatherEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentFatherEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentFatherEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentFatherEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentFatherEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentFatherEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentFatherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentFatherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentFatherEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentFatherEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentFatherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentFatherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentFatherEntity.getReqUpdatedReferer())
                .editable(slaveStudentFatherEntity.getEditable())
                .deletable(slaveStudentFatherEntity.getDeletable())
                .archived(slaveStudentFatherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentFatherStudentFatherProfileContactNoFacadeDto> facadeDto(StudentFatherEntity studentFatherEntity, StudentFatherProfileEntity studentFatherProfileEntity, List<StudentContactNoDto> studentContactNoDto) {

        StudentFatherStudentFatherProfileContactNoFacadeDto facadeDto = StudentFatherStudentFatherProfileContactNoFacadeDto.builder()
                .id(studentFatherEntity.getId())
                .uuid(studentFatherEntity.getUuid())
                .version(studentFatherEntity.getVersion())
                .status(studentFatherEntity.getStatus())
                .studentUUID(studentFatherEntity.getStudentUUID())
                .studentFatherUUID(studentFatherEntity.getUuid())
                .image(studentFatherProfileEntity.getImage())
                .name(studentFatherProfileEntity.getName())
                .nic(studentFatherProfileEntity.getNic())
                .age(studentFatherProfileEntity.getAge())
                .officialTel(studentFatherProfileEntity.getOfficialTel())
                .cityUUID(studentFatherProfileEntity.getCityUUID())
                .stateUUID(studentFatherProfileEntity.getStateUUID())
                .countryUUID(studentFatherProfileEntity.getCountryUUID())
                .noOfDependents(studentFatherProfileEntity.getNoOfDependents())
                .email(studentFatherProfileEntity.getEmail())
                .studentFatherContactNoDto(studentContactNoDto)
                .createdAt(studentFatherEntity.getCreatedAt())
                .createdBy(studentFatherEntity.getCreatedBy())
                .reqCompanyUUID(studentFatherEntity.getReqCompanyUUID())
                .reqBranchUUID(studentFatherEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentFatherEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentFatherEntity.getReqCreatedIP())
                .reqCreatedPort(studentFatherEntity.getReqCreatedPort())
                .reqCreatedOS(studentFatherEntity.getReqCreatedOS())
                .reqCreatedDevice(studentFatherEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentFatherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentFatherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentFatherEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentFatherEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentFatherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentFatherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentFatherEntity.getReqUpdatedReferer())
                .editable(studentFatherEntity.getEditable())
                .deletable(studentFatherEntity.getDeletable())
                .archived(studentFatherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentFatherProfileContactNoFacadeDto> updatedFacadeDto(StudentFatherEntity studentFatherEntity, StudentFatherProfileEntity StudentFatherProfileEntity, List<StudentContactNoDto> StudentContactNoDto) {

        StudentFatherProfileContactNoFacadeDto facadeDto = StudentFatherProfileContactNoFacadeDto.builder()
                .id(studentFatherEntity.getId())
                .uuid(studentFatherEntity.getUuid())
                .version(studentFatherEntity.getVersion())
                .status(studentFatherEntity.getStatus())
                .image(StudentFatherProfileEntity.getImage())
                .name(StudentFatherProfileEntity.getName())
                .nic(StudentFatherProfileEntity.getNic())
                .age(StudentFatherProfileEntity.getAge())
                .officialTel(StudentFatherProfileEntity.getOfficialTel())
                .cityUUID(StudentFatherProfileEntity.getCityUUID())
                .stateUUID(StudentFatherProfileEntity.getStateUUID())
                .countryUUID(StudentFatherProfileEntity.getCountryUUID())
                .noOfDependents(StudentFatherProfileEntity.getNoOfDependents())
                .email(StudentFatherProfileEntity.getEmail())
                .studentFatherContactNoDto(StudentContactNoDto)
                .createdAt(studentFatherEntity.getCreatedAt())
                .createdBy(studentFatherEntity.getCreatedBy())
                .updatedAt(studentFatherEntity.getUpdatedAt())
                .updatedBy(studentFatherEntity.getUpdatedBy())
                .reqCompanyUUID(StudentFatherProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(StudentFatherProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(StudentFatherProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(StudentFatherProfileEntity.getReqCreatedIP())
                .reqCreatedPort(StudentFatherProfileEntity.getReqCreatedPort())
                .reqCreatedOS(StudentFatherProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(StudentFatherProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(StudentFatherProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(StudentFatherProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(StudentFatherProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(StudentFatherProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(StudentFatherProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(StudentFatherProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(StudentFatherProfileEntity.getReqUpdatedReferer())
                .editable(StudentFatherProfileEntity.getEditable())
                .deletable(StudentFatherProfileEntity.getDeletable())
                .archived(StudentFatherProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-father-student-father-profile-contact-nos_store")
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

                    StudentFatherEntity stdFatherEntity = StudentFatherEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
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
                    return studentRepository.findByUuidAndDeletedAtIsNull(stdFatherEntity.getStudentUUID())
                            //check if Student Father Record Already Exists Against the same Student
                            .flatMap(studentEntity -> studentFatherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                            .flatMap(checkMsg -> responseInfoMsg("Student Father Record Against the Entered Student Already Exist."))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                //Building Student Father Profile Record
                                                StudentFatherProfileEntity studentFatherProfileEntity = StudentFatherProfileEntity
                                                        .builder()
                                                        .uuid(UUID.randomUUID())
                                                        .studentFatherUUID(stdFatherEntity.getUuid())
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

                                                sendFormData.add("docId", String.valueOf(studentFatherProfileEntity.getImage()));

                                                //check if Student Father Record Exists or Not
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentFatherProfileEntity.getCityUUID())
                                                        .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                //check if State Record Exists or not
                                                                .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentFatherProfileEntity.getStateUUID())
                                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                        //check if Country Record Exists or not
                                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentFatherProfileEntity.getCountryUUID())
                                                                                                        .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                //check if NIC Is Unique Against Student Father
                                                                                                                .flatMap(checkNIC -> studentFatherProfileRepository.findFirstByNicAndStudentFatherUUIDAndDeletedAtIsNull(studentFatherProfileEntity.getNic(), studentFatherProfileEntity.getStudentFatherUUID())
                                                                                                                        .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                                                //check if Father Profile Already Exists Against Student Father
                                                                                                                .switchIfEmpty(Mono.defer(() -> studentFatherProfileRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherProfileEntity.getStudentFatherUUID())
                                                                                                                        .flatMap(StudentProfileAlreadyExists -> responseInfoMsg("Father Profile already exist"))))
                                                                                                                //check if Document Record Exists or not
                                                                                                                .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentFatherProfileEntity.getImage())
                                                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                //check if Contact Category is Father
                                                                                                                                                .flatMap(documentEntity -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("father")
                                                                                                                                                                .flatMap(contactCategoryEntity -> {

                                                                                                                                                                    //getting List of Contact No. From Front
                                                                                                                                                                    List<String> StudentFatherContactList = value.get("StudentFatherContactNoDto");
                                                                                                                                                                    //Creating an empty list to add Student Contact No Records
                                                                                                                                                                    List<StudentContactNoEntity> StudentFatherContactNoList = new ArrayList<>();

                                                                                                                                                                    // Creating an empty list to add contact Type UUID's
                                                                                                                                                                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                    // Creating an empty list to add contact No's
                                                                                                                                                                    List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                    JsonNode contactNode = null;
                                                                                                                                                                    ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                    try {
                                                                                                                                                                        contactNode = objectMapper.readTree(StudentFatherContactList.toString());
                                                                                                                                                                    } catch (JsonProcessingException e) {
                                                                                                                                                                        e.printStackTrace();
                                                                                                                                                                    }
                                                                                                                                                                    assert contactNode != null;


                                                                                                                                                                    UUID StudentMetaUUID = null;
                                                                                                                                                                    UUID contactCategoryUUID = null;

                                                                                                                                                                    //iterating over the json node from front and setting contact No's
                                                                                                                                                                    for (JsonNode fatherContact : contactNode) {

                                                                                                                                                                        StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                                .builder()
                                                                                                                                                                                .contactTypeUUID(UUID.fromString(fatherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                .contactNo(fatherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                .studentMetaUUID(stdFatherEntity.getUuid())
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

                                                                                                                                                                        StudentFatherContactNoList.add(studentContactNoEntity);

                                                                                                                                                                        contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                                                                                                                                                                        contactNoList.add(studentContactNoEntity.getContactNo());
                                                                                                                                                                        StudentMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                                        contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                    }

                                                                                                                                                                    //Getting Distinct Values Fom the List of Student Father Contact No List
                                                                                                                                                                    StudentFatherContactNoList = StudentFatherContactNoList.stream()
                                                                                                                                                                            .distinct()
                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                    //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                    contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                            .distinct()
                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                    // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                    List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                                                                    if (!StudentFatherContactNoList.isEmpty()) {

                                                                                                                                                                        UUID finalStdMetaUUID = StudentMetaUUID;

                                                                                                                                                                        UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                        List<StudentContactNoEntity> finalStudentFatherContactNoList = StudentFatherContactNoList;

                                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                .collectList()
                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                        } else {
                                                                                                                                                                                            //check if Contact No Record Already Exists against Student Father and Contact Type
                                                                                                                                                                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStdMetaUUID)
                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> studentFatherRepository.save(stdFatherEntity)
                                                                                                                                                                                                            .then(studentFatherProfileRepository.save(studentFatherProfileEntity))
                                                                                                                                                                                                            .then(studentContactNoRepository.saveAll(finalStudentFatherContactNoList)
                                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                                            .flatMap(mthContactNo -> {

                                                                                                                                                                                                                for (StudentContactNoEntity StudentContact : mthContactNo) {
                                                                                                                                                                                                                    StudentContactNoDto studentFatherContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                            .contactNo(StudentContact.getContactNo())
                                                                                                                                                                                                                            .contactTypeUUID(StudentContact.getContactTypeUUID())
                                                                                                                                                                                                                            .build();

                                                                                                                                                                                                                    studentContactNoDto.add(studentFatherContactNoDto);
                                                                                                                                                                                                                }

                                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(stdFatherEntity, studentFatherProfileEntity, studentContactNoDto)
                                                                                                                                                                                                                                .flatMap(studentFatherFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentFatherFacadeDto))
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
                                                                                                                                                                        //if Contact No List is empty then store Student Father and Student Father Profile
                                                                                                                                                                        return studentFatherRepository.save(stdFatherEntity)
                                                                                                                                                                                //Save Student Father Profile Entity
                                                                                                                                                                                .then(studentFatherProfileRepository.save(studentFatherProfileEntity))
                                                                                                                                                                                //update Document Status After Storing record
                                                                                                                                                                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(stdFatherEntity, studentFatherProfileEntity, studentContactNoDto)
                                                                                                                                                                                                .flatMap(studentFatherFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentFatherFacadeDto))
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
                            ).switchIfEmpty(responseInfoMsg("Student Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Student Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-father-student-father-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentFatherUUID = UUID.fromString((serverRequest.pathVariable("studentFatherUUID")));
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
                .flatMap(value -> studentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                        .flatMap(stdFatherEntity -> studentFatherProfileRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentFatherProfileEntity updatedEntity = StudentFatherProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentFatherUUID(previousProfileEntity.getStudentFatherUUID())
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
                                    return studentFatherProfileRepository.findFirstByNicAndStudentFatherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentFatherUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check father profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentFatherProfileRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentFatherUUID(), updatedEntity.getUuid())
                                                    .flatMap(checkMsg -> responseInfoMsg("Father Profile already exist"))))
                                            //checks if father uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(StudentFatherDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks countries uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> {

                                                                                                                        //getting List of Contact No. From Front
                                                                                                                        List<String> studentFatherContactList = value.get("studentFatherContactNoDto");
                                                                                                                        List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                        studentFatherContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!studentFatherContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("father")
                                                                                                                                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentFatherUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing Student Father Contact No Entity
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
                                                                                                                                                    contactNode = new ObjectMapper().readTree(studentFatherContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<StudentContactNoEntity> stdFatherContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedStdMetaUUID = null;

                                                                                                                                                for (JsonNode fatherContact : contactNode) {

                                                                                                                                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(fatherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(fatherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .studentMetaUUID(studentFatherUUID)
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

                                                                                                                                                    stdFatherContactNoList.add(studentContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Student Father Contact No List
                                                                                                                                                stdFatherContactNoList = stdFatherContactNoList.stream()
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

                                                                                                                                                List<StudentContactNoEntity> finalStudentFatherContactNoList1 = stdFatherContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Student Father and Contact Type
                                                                                                                                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentFatherProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(studentFatherProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentFatherContactNoList1)
                                                                                                                                                                                            .collectList()
                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
                                                                                                                                                                                                    StudentContactNoDto studentFatherContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                            .contactNo(studentContact.getContactNo())
                                                                                                                                                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                                                                                                                                                            .build();

                                                                                                                                                                                                    studentContactNoDto.add(studentFatherContactNoDto);
                                                                                                                                                                                                }

                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(stdFatherEntity, updatedEntity, studentContactNoDto)
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
                                                                                                                            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentFatherUUID)
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
                                                                                                                                                .flatMap(studentContactList -> studentFatherProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(studentFatherProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(StudentFatherProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(stdFatherEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                        .flatMap(StudentFatherFacadeDto -> responseSuccessMsg("Record Updated Successfully", StudentFatherFacadeDto))
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
                                }).switchIfEmpty(responseInfoMsg("Father Profile Against the entered Student Father Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Father Profile Against the entered Student Father Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Father Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Father Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_student-father-student-father-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID StudentFatherUUID = UUID.fromString((serverRequest.pathVariable("studentFatherUUID")));
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

        return studentFatherRepository.findByUuidAndDeletedAtIsNull(StudentFatherUUID)
                .flatMap(studentFatherEntity -> studentFatherProfileRepository.findFirstByStudentFatherUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                        .flatMap(studentFatherProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentFatherEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentFatherEntity.setDeletedBy(UUID.fromString(userId));
                                    studentFatherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentFatherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentFatherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentFatherEntity.setReqDeletedIP(reqIp);
                                    studentFatherEntity.setReqDeletedPort(reqPort);
                                    studentFatherEntity.setReqDeletedBrowser(reqBrowser);
                                    studentFatherEntity.setReqDeletedOS(reqOs);
                                    studentFatherEntity.setReqDeletedDevice(reqDevice);
                                    studentFatherEntity.setReqDeletedReferer(reqReferer);

                                    studentFatherProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    studentFatherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentFatherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentFatherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentFatherProfileEntity.setReqDeletedIP(reqIp);
                                    studentFatherProfileEntity.setReqDeletedPort(reqPort);
                                    studentFatherProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    studentFatherProfileEntity.setReqDeletedOS(reqOs);
                                    studentFatherProfileEntity.setReqDeletedDevice(reqDevice);
                                    studentFatherProfileEntity.setReqDeletedReferer(reqReferer);

                                    for (StudentContactNoEntity studentContactNoEntity1 : studentContactNoEntityList) {

                                        studentContactNoEntity1.setDeletedBy(UUID.fromString(userId));
                                        studentContactNoEntity1.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                        studentContactNoEntity1.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                        studentContactNoEntity1.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                        studentContactNoEntity1.setReqDeletedIP(reqIp);
                                        studentContactNoEntity1.setReqDeletedPort(reqPort);
                                        studentContactNoEntity1.setReqDeletedBrowser(reqBrowser);
                                        studentContactNoEntity1.setReqDeletedOS(reqOs);
                                        studentContactNoEntity1.setReqDeletedDevice(reqDevice);
                                        studentContactNoEntity1.setReqDeletedReferer(reqReferer);

                                        studentContactNoEntityList.add(studentContactNoEntity1);

                                    }

                                    List<StudentContactNoDto> studentContactNoDtoList = new ArrayList<>();

                                    for (StudentContactNoEntity studentContactNos : studentContactNoEntity) {
                                        StudentContactNoDto studentFatherContactNoDto = StudentContactNoDto.builder()
                                                .contactNo(studentContactNos.getContactNo())
                                                .contactTypeUUID(studentContactNos.getContactTypeUUID())
                                                .build();

                                        studentContactNoDtoList.add(studentFatherContactNoDto);
                                    }

                                    return studentFatherRepository.save(studentFatherEntity)
                                            .then(studentFatherProfileRepository.save(studentFatherProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentFatherEntity, studentFatherProfileEntity, studentContactNoDtoList)
                                                    .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto)))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Record does not exist. Please contact developer."));
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
