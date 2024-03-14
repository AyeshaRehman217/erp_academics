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
import tuf.webscaf.app.dbContext.master.dto.StudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentMotherStudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentMotherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentMotherStudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentMotherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentMotherRepository;
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

@Tag(name = "studentMotherStudentMotherProfileContactNoFacade")
@Component
public class StudentMotherStudentMotherProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentMotherRepository studentMotherRepository;

    @Autowired
    SlaveStudentMotherRepository slaveStudentMotherRepository;

    @Autowired
    SlaveStudentMotherProfileRepository slaveStudentMotherProfileRepository;

    @Autowired
    StudentMotherProfileRepository studentMotherProfileRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    SlaveStudentContactNoRepository slaveStudentContactNoRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    SlaveStudentRepository slaveStudentRepository;

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

    @AuthHasPermission(value = "academic_api_v1_facade_student-mother-student-mother-profile-contact-nos_index")
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
            Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> slaveStudentMotherStudentMotherProfileContactNoFacadeDtoFlux = slaveStudentMotherRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentMotherStudentMotherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentMotherProfileEntity -> slaveStudentMotherRepository
                            .countStudentMotherStudentMotherProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentMotherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> slaveStudentMotherStudentMotherProfileContactNoFacadeDtoFlux = slaveStudentMotherRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentMotherStudentMotherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentMotherProfileEntity -> slaveStudentMotherRepository
                            .countStudentMotherStudentMotherProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentMotherProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentMotherProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-mother-student-mother-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString((serverRequest.pathVariable("studentMotherUUID")));

        return slaveStudentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                .flatMap(stdMotherEntity -> slaveStudentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(stdMotherEntity.getUuid())
                        .flatMap(studentMotherProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(stdMotherEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNo -> {
                                    List<SlaveStudentContactNoFacadeDto> stdContactNoDto = new ArrayList<>();

                                    for (SlaveStudentContactNoEntity stdContact : studentContactNo) {
                                        SlaveStudentContactNoFacadeDto studentContactNoDto = SlaveStudentContactNoFacadeDto.builder()
                                                .contactNo(stdContact.getContactNo())
                                                .contactTypeUUID(stdContact.getContactTypeUUID())
                                                .build();

                                        stdContactNoDto.add(studentContactNoDto);
                                    }

                                    return showFacadeDto(stdMotherEntity, studentMotherProfileEntity, stdContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Mother Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Mother Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Mother Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Mother Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentMotherStudentMotherProfileContactNoFacadeDto> showFacadeDto(SlaveStudentMotherEntity slaveStudentMotherEntity, SlaveStudentMotherProfileEntity slaveStudentMotherProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoFacadeDto) {

        SlaveStudentMotherStudentMotherProfileContactNoFacadeDto facadeDto = SlaveStudentMotherStudentMotherProfileContactNoFacadeDto.builder()
                .id(slaveStudentMotherEntity.getId())
                .uuid(slaveStudentMotherEntity.getUuid())
                .version(slaveStudentMotherEntity.getVersion())
                .status(slaveStudentMotherEntity.getStatus())
                .studentUUID(slaveStudentMotherEntity.getStudentUUID())
                .studentMotherUUID(slaveStudentMotherEntity.getUuid())
                .image(slaveStudentMotherProfileEntity.getImage())
                .name(slaveStudentMotherProfileEntity.getName())
                .nic(slaveStudentMotherProfileEntity.getNic())
                .age(slaveStudentMotherProfileEntity.getAge())
                .officialTel(slaveStudentMotherProfileEntity.getOfficialTel())
                .cityUUID(slaveStudentMotherProfileEntity.getCityUUID())
                .stateUUID(slaveStudentMotherProfileEntity.getStateUUID())
                .countryUUID(slaveStudentMotherProfileEntity.getCountryUUID())
                .noOfDependents(slaveStudentMotherProfileEntity.getNoOfDependents())
                .email(slaveStudentMotherProfileEntity.getEmail())
                .studentContactNoDto(slaveStudentContactNoFacadeDto)
                .createdAt(slaveStudentMotherEntity.getCreatedAt())
                .createdBy(slaveStudentMotherEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentMotherEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentMotherEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentMotherEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentMotherEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentMotherEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentMotherEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentMotherEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentMotherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentMotherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentMotherEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentMotherEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentMotherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentMotherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentMotherEntity.getReqUpdatedReferer())
                .editable(slaveStudentMotherEntity.getEditable())
                .deletable(slaveStudentMotherEntity.getDeletable())
                .archived(slaveStudentMotherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentMotherStudentMotherProfileContactNoFacadeDto> facadeDto(StudentMotherEntity studentMotherEntity, StudentMotherProfileEntity studentMotherProfileEntity, List<StudentContactNoDto> studentContactNoDto) {

        StudentMotherStudentMotherProfileContactNoFacadeDto facadeDto = StudentMotherStudentMotherProfileContactNoFacadeDto.builder()
                .id(studentMotherEntity.getId())
                .uuid(studentMotherEntity.getUuid())
                .version(studentMotherEntity.getVersion())
                .status(studentMotherEntity.getStatus())
                .studentUUID(studentMotherEntity.getStudentUUID())
                .studentMotherUUID(studentMotherEntity.getUuid())
                .image(studentMotherProfileEntity.getImage())
                .name(studentMotherProfileEntity.getName())
                .nic(studentMotherProfileEntity.getNic())
                .age(studentMotherProfileEntity.getAge())
                .officialTel(studentMotherProfileEntity.getOfficialTel())
                .cityUUID(studentMotherProfileEntity.getCityUUID())
                .stateUUID(studentMotherProfileEntity.getStateUUID())
                .countryUUID(studentMotherProfileEntity.getCountryUUID())
                .noOfDependents(studentMotherProfileEntity.getNoOfDependents())
                .email(studentMotherProfileEntity.getEmail())
                .studentContactNoDto(studentContactNoDto)
                .createdAt(studentMotherEntity.getCreatedAt())
                .createdBy(studentMotherEntity.getCreatedBy())
                .reqCompanyUUID(studentMotherEntity.getReqCompanyUUID())
                .reqBranchUUID(studentMotherEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentMotherEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentMotherEntity.getReqCreatedIP())
                .reqCreatedPort(studentMotherEntity.getReqCreatedPort())
                .reqCreatedOS(studentMotherEntity.getReqCreatedOS())
                .reqCreatedDevice(studentMotherEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentMotherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentMotherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentMotherEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentMotherEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentMotherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentMotherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentMotherEntity.getReqUpdatedReferer())
                .editable(studentMotherEntity.getEditable())
                .deletable(studentMotherEntity.getDeletable())
                .archived(studentMotherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentMotherProfileContactNoFacadeDto> updatedFacadeDto(StudentMotherEntity studentMotherEntity, StudentMotherProfileEntity studentMotherProfileEntity, List<StudentContactNoDto> studentContactNoDto) {

        StudentMotherProfileContactNoFacadeDto facadeDto = StudentMotherProfileContactNoFacadeDto.builder()
                .id(studentMotherEntity.getId())
                .uuid(studentMotherEntity.getUuid())
                .version(studentMotherEntity.getVersion())
                .status(studentMotherEntity.getStatus())
                .image(studentMotherProfileEntity.getImage())
                .name(studentMotherProfileEntity.getName())
                .nic(studentMotherProfileEntity.getNic())
                .age(studentMotherProfileEntity.getAge())
                .officialTel(studentMotherProfileEntity.getOfficialTel())
                .cityUUID(studentMotherProfileEntity.getCityUUID())
                .stateUUID(studentMotherProfileEntity.getStateUUID())
                .countryUUID(studentMotherProfileEntity.getCountryUUID())
                .noOfDependents(studentMotherProfileEntity.getNoOfDependents())
                .email(studentMotherProfileEntity.getEmail())
                .studentContactNoDto(studentContactNoDto)
                .createdAt(studentMotherEntity.getCreatedAt())
                .createdBy(studentMotherEntity.getCreatedBy())
                .updatedAt(studentMotherEntity.getUpdatedAt())
                .updatedBy(studentMotherEntity.getUpdatedBy())
                .reqCompanyUUID(studentMotherEntity.getReqCompanyUUID())
                .reqBranchUUID(studentMotherEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentMotherEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentMotherEntity.getReqCreatedIP())
                .reqCreatedPort(studentMotherEntity.getReqCreatedPort())
                .reqCreatedOS(studentMotherEntity.getReqCreatedOS())
                .reqCreatedDevice(studentMotherEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentMotherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentMotherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentMotherEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentMotherEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentMotherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentMotherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentMotherEntity.getReqUpdatedReferer())
                .editable(studentMotherEntity.getEditable())
                .deletable(studentMotherEntity.getDeletable())
                .archived(studentMotherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-mother-student-mother-profile-contact-nos_store")
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

                    StudentMotherEntity studentMotherEntity = StudentMotherEntity.builder()
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
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentMotherEntity.getStudentUUID())
                            //check if Student Mother Record Already Exists Against the same student
                            .flatMap(stdEntity -> studentMotherRepository.findFirstByStudentUUIDAndDeletedAtIsNull(stdEntity.getUuid())
                                            .flatMap(checkMsg -> responseInfoMsg("Student Mother Record Against the Entered Student Already Exist."))
                                            .switchIfEmpty(Mono.defer(() -> {

                                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                                //Building Student Mother Profile Record
                                                StudentMotherProfileEntity studentMotherProfileEntity = StudentMotherProfileEntity
                                                        .builder()
                                                        .uuid(UUID.randomUUID())
                                                        .studentMotherUUID(studentMotherEntity.getUuid())
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

                                                sendFormData.add("docId", String.valueOf(studentMotherProfileEntity.getImage()));

                                                //check if Student Mother Record Exists or Not
                                                return apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentMotherProfileEntity.getCityUUID())
                                                        .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                //check if State Record Exists or not
                                                                .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentMotherProfileEntity.getStateUUID())
                                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                                //check if Country Record Exists or not
                                                                                                .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentMotherProfileEntity.getCountryUUID())
                                                                                                        .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                        //check if NIC Is Unique Against Student Mother
                                                                                                                        .flatMap(checkNIC -> studentMotherProfileRepository.findFirstByNicAndStudentMotherUUIDAndDeletedAtIsNull(studentMotherProfileEntity.getNic(), studentMotherProfileEntity.getStudentMotherUUID())
                                                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                                                        //check if Mother Profile Already Exists Against Student Mother
                                                                                                                        .switchIfEmpty(Mono.defer(() -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherProfileEntity.getStudentMotherUUID())
                                                                                                                                .flatMap(studentMotherProfileAlreadyExists -> responseInfoMsg("Mother Profile already exist"))))
                                                                                                                        //check if Document Record Exists or not
                                                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentMotherProfileEntity.getImage())
                                                                                                                                        .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                        //check if Contact Category is Mother
                                                                                                                                                        .flatMap(documentEntity -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("mother")
                                                                                                                                                                        .flatMap(contactCategoryEntity -> {

                                                                                                                                                                            //getting List of Contact No. From Front
                                                                                                                                                                            List<String> studentMotherContactList = value.get("studentContactNoDto");
                                                                                                                                                                            //Creating an empty list to add student Contact No Records
                                                                                                                                                                            List<StudentContactNoEntity> studentMotherContactNoList = new ArrayList<>();

                                                                                                                                                                            // Creating an empty list to add contact Type UUID's
                                                                                                                                                                            List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                            // Creating an empty list to add contact No's
                                                                                                                                                                            List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                            JsonNode contactNode = null;
                                                                                                                                                                            ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                            try {
                                                                                                                                                                                contactNode = objectMapper.readTree(studentMotherContactList.toString());
                                                                                                                                                                            } catch (JsonProcessingException e) {
                                                                                                                                                                                e.printStackTrace();
                                                                                                                                                                            }
                                                                                                                                                                            assert contactNode != null;


                                                                                                                                                                            UUID stdMetaUUID = null;
                                                                                                                                                                            UUID contactCategoryUUID = null;

                                                                                                                                                                            //iterating over the json node from front and setting contact No's
                                                                                                                                                                            for (JsonNode motherContact : contactNode) {

                                                                                                                                                                                StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                                        .builder()
                                                                                                                                                                                        .contactTypeUUID(UUID.fromString(motherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                        .contactNo(motherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                        .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                        .studentMetaUUID(studentMotherEntity.getUuid())
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

                                                                                                                                                                                studentMotherContactNoList.add(studentContactNoEntity);

                                                                                                                                                                                contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                                                                                                                                                                                contactNoList.add(studentContactNoEntity.getContactNo());
                                                                                                                                                                                stdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                                                contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                            }

                                                                                                                                                                            //Getting Distinct Values Fom the List of Student Mother Contact No List
                                                                                                                                                                            studentMotherContactNoList = studentMotherContactNoList.stream()
                                                                                                                                                                                    .distinct()
                                                                                                                                                                                    .collect(Collectors.toList());

                                                                                                                                                                            //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                            contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                                    .distinct()
                                                                                                                                                                                    .collect(Collectors.toList());

                                                                                                                                                                            // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                            List<StudentContactNoDto> stdContactNoDto = new ArrayList<>();

                                                                                                                                                                            if (!studentMotherContactNoList.isEmpty()) {

                                                                                                                                                                                UUID finalStdMetaUUID = stdMetaUUID;

                                                                                                                                                                                UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                                List<StudentContactNoEntity> finalStudentMotherContactNoList = studentMotherContactNoList;

                                                                                                                                                                                List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                        .collectList()
                                                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                                } else {
                                                                                                                                                                                                    //check if Contact No Record Already Exists against Student Mother and Contact Type
                                                                                                                                                                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStdMetaUUID)
                                                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentMotherRepository.save(studentMotherEntity)
                                                                                                                                                                                                                    .then(studentMotherProfileRepository.save(studentMotherProfileEntity))
                                                                                                                                                                                                                    .then(studentContactNoRepository.saveAll(finalStudentMotherContactNoList)
                                                                                                                                                                                                                            .collectList())
                                                                                                                                                                                                                    .flatMap(mthContactNo -> {

                                                                                                                                                                                                                        for (StudentContactNoEntity stdContact : mthContactNo) {
                                                                                                                                                                                                                            StudentContactNoDto studentContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                                    .contactNo(stdContact.getContactNo())
                                                                                                                                                                                                                                    .contactTypeUUID(stdContact.getContactTypeUUID())
                                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                                            stdContactNoDto.add(studentContactNoDto);
                                                                                                                                                                                                                        }

                                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                .flatMap(docUpdate -> facadeDto(studentMotherEntity, studentMotherProfileEntity, stdContactNoDto)
                                                                                                                                                                                                                                        .flatMap(stdMotherFacadeDto -> responseSuccessMsg("Record Stored Successfully", stdMotherFacadeDto))
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
                                                                                                                                                                                //if Contact No List is empty then store student Mother and Student Mother Profile
                                                                                                                                                                                return studentMotherRepository.save(studentMotherEntity)
                                                                                                                                                                                        //Save Student Mother Profile Entity
                                                                                                                                                                                        .then(studentMotherProfileRepository.save(studentMotherProfileEntity))
                                                                                                                                                                                        //update Document Status After Storing record
                                                                                                                                                                                        .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                .flatMap(docUpdate -> facadeDto(studentMotherEntity, studentMotherProfileEntity, stdContactNoDto)
                                                                                                                                                                                                        .flatMap(stdMotherFacadeDto -> responseSuccessMsg("Record Stored Successfully", stdMotherFacadeDto))
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

                                                                                                                        ).switchIfEmpty(responseInfoMsg("Country Record Does not exist."))
                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Country Record Does not Exist.Please Contact Developer."))
                                                                                                        ))
                                                                                ).switchIfEmpty(responseInfoMsg("State Record Does not Exist."))
                                                                                .onErrorResume(ex -> responseErrorMsg("State Record Does not Exist.Please Contact Developer."))
                                                                )).switchIfEmpty(responseInfoMsg("City Record Does not Exist."))
                                                        .onErrorResume(ex -> responseErrorMsg("City Record Does not Exist.Please Contact Developer."));
                                            }))
                            ).switchIfEmpty(responseInfoMsg("Student Record Does not exist."))
                            .onErrorResume(ex -> responseErrorMsg("Student Record Does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-mother-student-mother-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString((serverRequest.pathVariable("studentMotherUUID")));
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
                .flatMap(value -> studentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                        .flatMap(studentMotherEntity -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentMotherProfileEntity updatedEntity = StudentMotherProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentMotherUUID(previousProfileEntity.getStudentMotherUUID())
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
                                    return studentMotherProfileRepository.findFirstByNicAndStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getStudentMotherUUID(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check mother profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentMotherUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentMotherProfileAlreadyExists -> responseInfoMsg("Mother Profile already exist"))))
                                            //checks if mother uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(studentMotherDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks countries uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> {

                                                                                                                        //getting List of Contact No. From Front
                                                                                                                        List<String> studentMotherContactList = value.get("studentContactNoDto");
                                                                                                                        List<StudentContactNoDto> stdContactNoDto = new ArrayList<>();

                                                                                                                        studentMotherContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!studentMotherContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("mother")
                                                                                                                                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentMotherUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing student Mother Contact No Entity
                                                                                                                                                for (StudentContactNoEntity stdContact : existingContactList) {
                                                                                                                                                    stdContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                                    stdContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                                    stdContact.setReqDeletedIP(reqIp);
                                                                                                                                                    stdContact.setReqDeletedPort(reqPort);
                                                                                                                                                    stdContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                                    stdContact.setReqDeletedOS(reqOs);
                                                                                                                                                    stdContact.setReqDeletedDevice(reqDevice);
                                                                                                                                                    stdContact.setReqDeletedReferer(reqReferer);
                                                                                                                                                }

                                                                                                                                                //Creating an Object Node to Read Values from Front
                                                                                                                                                JsonNode contactNode = null;
                                                                                                                                                try {
                                                                                                                                                    contactNode = new ObjectMapper().readTree(studentMotherContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<StudentContactNoEntity> studentMotherContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedStdMetaUUID = null;

                                                                                                                                                for (JsonNode motherContact : contactNode) {

                                                                                                                                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(motherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(motherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .studentMetaUUID(studentMotherUUID)
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

                                                                                                                                                    studentMotherContactNoList.add(studentContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Student Mother Contact No List
                                                                                                                                                studentMotherContactNoList = studentMotherContactNoList.stream()
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

                                                                                                                                                List<StudentContactNoEntity> finalStudentMotherContactNoList1 = studentMotherContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Student Mother and Contact Type
                                                                                                                                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentMotherProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(studentMotherProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentMotherContactNoList1)
                                                                                                                                                                                            .collectList()
                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                for (StudentContactNoEntity stdContact : updatedContactNoEntity) {
                                                                                                                                                                                                    StudentContactNoDto studentContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                            .contactNo(stdContact.getContactNo())
                                                                                                                                                                                                            .contactTypeUUID(stdContact.getContactTypeUUID())
                                                                                                                                                                                                            .build();

                                                                                                                                                                                                    stdContactNoDto.add(studentContactNoDto);
                                                                                                                                                                                                }

                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(studentMotherEntity, updatedEntity, stdContactNoDto)
                                                                                                                                                                                                                .flatMap(stdMotherFacadeDto -> responseSuccessMsg("Record Updated Successfully", stdMotherFacadeDto))
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
                                                                                                                            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentMotherUUID)
                                                                                                                                    .collectList()
                                                                                                                                    .flatMap(previousStdContactList -> {

                                                                                                                                        for (StudentContactNoEntity stdContact : previousStdContactList) {
                                                                                                                                            stdContact.setDeletedBy(UUID.fromString(userId));
                                                                                                                                            stdContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                                                            stdContact.setReqDeletedIP(reqIp);
                                                                                                                                            stdContact.setReqDeletedPort(reqPort);
                                                                                                                                            stdContact.setReqDeletedBrowser(reqBrowser);
                                                                                                                                            stdContact.setReqDeletedOS(reqOs);
                                                                                                                                            stdContact.setReqDeletedDevice(reqDevice);
                                                                                                                                            stdContact.setReqDeletedReferer(reqReferer);
                                                                                                                                        }

                                                                                                                                        return studentContactNoRepository.saveAll(previousStdContactList)
                                                                                                                                                .collectList()
                                                                                                                                                .flatMap(stdContactList -> studentMotherProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(studentMotherProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(studentMotherProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(studentMotherEntity, updatedEntity, stdContactNoDto)
                                                                                                                                                                        .flatMap(stdMotherFacadeDto -> responseSuccessMsg("Record Updated Successfully", stdMotherFacadeDto))
                                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer.")))
                                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to update Document. There is something wrong Please try again."))
                                                                                                                                                                .onErrorResume(err -> responseErrorMsg("Unable to Update Document. Please Contact Developer."))
                                                                                                                                                        )
                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Update Document There is something wrong please try again"))
                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Document.Please Contact Developer."));
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    }
                                                                                                            ).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                                    )).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                                    )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                                    )).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                                    )
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Mother Profile Against the entered student Mother Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Mother Profile Against the entered student Mother Record Does not exist.Please Contact Developer."))
                        )
                        .switchIfEmpty(responseInfoMsg("Student Mother Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Mother Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_student-mother-student-mother-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentMotherUUID = UUID.fromString((serverRequest.pathVariable("studentMotherUUID")));
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

        return studentMotherRepository.findByUuidAndDeletedAtIsNull(studentMotherUUID)
                .flatMap(studentMotherEntity -> studentMotherProfileRepository.findFirstByStudentMotherUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                        .flatMap(studentMotherProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentMotherEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentMotherEntity.setDeletedBy(UUID.fromString(userId));
                                    studentMotherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentMotherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentMotherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentMotherEntity.setReqDeletedIP(reqIp);
                                    studentMotherEntity.setReqDeletedPort(reqPort);
                                    studentMotherEntity.setReqDeletedBrowser(reqBrowser);
                                    studentMotherEntity.setReqDeletedOS(reqOs);
                                    studentMotherEntity.setReqDeletedDevice(reqDevice);
                                    studentMotherEntity.setReqDeletedReferer(reqReferer);

                                    studentMotherProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    studentMotherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentMotherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentMotherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentMotherProfileEntity.setReqDeletedIP(reqIp);
                                    studentMotherProfileEntity.setReqDeletedPort(reqPort);
                                    studentMotherProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    studentMotherProfileEntity.setReqDeletedOS(reqOs);
                                    studentMotherProfileEntity.setReqDeletedDevice(reqDevice);
                                    studentMotherProfileEntity.setReqDeletedReferer(reqReferer);

                                    for (StudentContactNoEntity stdContact : studentContactNoEntity) {

                                        stdContact.setDeletedBy(UUID.fromString(userId));
                                        stdContact.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                        stdContact.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                        stdContact.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                        stdContact.setReqDeletedIP(reqIp);
                                        stdContact.setReqDeletedPort(reqPort);
                                        stdContact.setReqDeletedBrowser(reqBrowser);
                                        stdContact.setReqDeletedOS(reqOs);
                                        stdContact.setReqDeletedDevice(reqDevice);
                                        stdContact.setReqDeletedReferer(reqReferer);

                                        studentContactNoEntityList.add(stdContact);

                                    }

                                    List<StudentContactNoDto> stdContactNoDto = new ArrayList<>();

                                    for (StudentContactNoEntity stdContact : studentContactNoEntity) {
                                        StudentContactNoDto studentContactNoDto = StudentContactNoDto.builder()
                                                .contactNo(stdContact.getContactNo())
                                                .contactTypeUUID(stdContact.getContactTypeUUID())
                                                .build();

                                        stdContactNoDto.add(studentContactNoDto);
                                    }

                                    return studentMotherRepository.save(studentMotherEntity)
                                            .then(studentMotherProfileRepository.save(studentMotherProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentMotherEntity, studentMotherProfileEntity, stdContactNoDto)
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
