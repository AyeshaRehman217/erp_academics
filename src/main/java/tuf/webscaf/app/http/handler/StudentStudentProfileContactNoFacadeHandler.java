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
import tuf.webscaf.app.dbContext.master.dto.StudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.StudentContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "studentStudentProfileContactNoFacadeHandler")
@Component
public class StudentStudentProfileContactNoFacadeHandler {

    @Autowired
    CustomResponse appresponse;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    StudentContactNoRepository studentContactNoRepository;

    @Autowired
    SlaveStudentContactNoRepository slaveStudentContactNoRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ReligionRepository religionRepository;

    @Autowired
    SectRepository sectRepository;

    @Autowired
    CasteRepository casteRepository;

    @Autowired
    GenderRepository genderRepository;

    @Autowired
    MaritalStatusRepository maritalStatusRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @Autowired
    SlaveStudentProfileRepository slaveStudentProfileRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    SlaveStudentRepository slaveStudentRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_student-student-profile-contact-nos_index")
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
            Flux<SlaveStudentStudentProfileContactNoFacadeDto> slaveStudentStudentProfileContactNoFacadeDtoFlux = slaveStudentRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentStudentProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentProfileEntity -> slaveStudentRepository
                            .countStudentStudentProfileContactNoWithStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentStudentProfileContactNoFacadeDto> slaveStudentStudentProfileContactNoFacadeDtoFlux = slaveStudentRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentStudentProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentProfileEntity -> slaveStudentRepository
                            .countStudentStudentProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-student-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));

        return slaveStudentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                .flatMap(studentEntity -> slaveStudentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                        .flatMap(studentProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentEntity.getUuid())
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

                                    return showFacadeDto(studentEntity, studentProfileEntity, stdContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentStudentProfileContactNoFacadeDto> showFacadeDto(SlaveStudentEntity slaveStudentEntity, SlaveStudentProfileEntity slaveStudentProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoFacadeDto) {

        SlaveStudentStudentProfileContactNoFacadeDto facadeDto = SlaveStudentStudentProfileContactNoFacadeDto.builder()
                .id(slaveStudentEntity.getId())
                .uuid(slaveStudentEntity.getUuid())
                .version(slaveStudentEntity.getVersion())
                .studentId(slaveStudentEntity.getStudentId())
                .status(slaveStudentEntity.getStatus())
                .campusUUID(slaveStudentEntity.getCampusUUID())
                .officialEmail(slaveStudentEntity.getOfficialEmail())
                .description(slaveStudentProfileEntity.getDescription())
                .studentUUID(slaveStudentProfileEntity.getStudentUUID())
                .image(slaveStudentProfileEntity.getImage())
                .firstName(slaveStudentProfileEntity.getFirstName())
                .lastName(slaveStudentProfileEntity.getLastName())
                .email(slaveStudentProfileEntity.getEmail())
                .telephoneNo(slaveStudentProfileEntity.getTelephoneNo())
                .nic(slaveStudentProfileEntity.getNic())
                .birthDate(slaveStudentProfileEntity.getBirthDate())
                .cityUUID(slaveStudentProfileEntity.getCityUUID())
                .stateUUID(slaveStudentProfileEntity.getStateUUID())
                .countryUUID(slaveStudentProfileEntity.getCountryUUID())
                .religionUUID(slaveStudentProfileEntity.getReligionUUID())
                .sectUUID(slaveStudentProfileEntity.getSectUUID())
                .casteUUID(slaveStudentProfileEntity.getCasteUUID())
                .genderUUID(slaveStudentProfileEntity.getGenderUUID())
                .maritalStatusUUID(slaveStudentProfileEntity.getMaritalStatusUUID())
                .studentContactNoDto(slaveStudentContactNoFacadeDto)
                .createdAt(slaveStudentEntity.getCreatedAt())
                .createdBy(slaveStudentEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentEntity.getReqUpdatedReferer())
                .editable(slaveStudentEntity.getEditable())
                .deletable(slaveStudentEntity.getDeletable())
                .archived(slaveStudentEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-student-profile-contact-nos_store")
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

                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                    //  build Student Entity
                    StudentEntity studentEntity = StudentEntity
                            .builder()
                            .uuid(UUID.randomUUID())
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                            .studentId(value.getFirst("studentId"))
                            .officialEmail(value.getFirst("officialEmail").trim())
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

                    //  build Student Profile Entity
                    StudentProfileEntity studentProfileEntity = StudentProfileEntity.builder()
                            .uuid(UUID.randomUUID())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .firstName(value.getFirst("firstName").trim())
                            .lastName(value.getFirst("lastName").trim())
                            .description(value.getFirst("description").trim())
                            .studentUUID(studentEntity.getUuid())
                            .email(value.getFirst("email").trim())
                            .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                            .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                            .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                            .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
                            .image(UUID.fromString(value.getFirst("image").trim()))
                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                            .telephoneNo(value.getFirst("telephoneNo").trim())
                            .nic(value.getFirst("nic").trim())
                            .birthDate(LocalDateTime.parse((value.getFirst("birthDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
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

                    sendFormData.add("docId", String.valueOf(studentProfileEntity.getImage()));

                    // check campus uuid exists
                    return campusRepository.findByUuidAndDeletedAtIsNull(studentEntity.getCampusUUID())
                            // check religion uuid exists
                            .flatMap(campusEntity -> religionRepository.findByUuidAndDeletedAtIsNull(studentProfileEntity.getReligionUUID())
                                    // check section uuid exists
                                    .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(studentProfileEntity.getSectUUID())
                                            // check caste uuid exists
                                            .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(studentProfileEntity.getCasteUUID())
                                                    // check gender uuid exists
                                                    .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(studentProfileEntity.getGenderUUID())
                                                            // check martial status uuid exists
                                                            .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(studentProfileEntity.getMaritalStatusUUID())
                                                                    // check doc id no uuid exists
                                                                    .flatMap(maritalStatusEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentProfileEntity.getImage())
                                                                            .flatMap(imageUUID -> apiCallService.checkDocId(imageUUID)
                                                                                    // check city uuid exists
                                                                                    .flatMap(imageJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentProfileEntity.getCityUUID())
                                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                                    //  check state uuid exists
                                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentProfileEntity.getStateUUID())
                                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(stateEntity)
                                                                                                                    //  check country uuid exists
                                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentProfileEntity.getCountryUUID())
                                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                                    // find contact category exist
                                                                                                                                    .flatMap(countryJson -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("student")
                                                                                                                                            .flatMap(contactCategoryEntity -> studentRepository.findFirstByStudentIdAndDeletedAtIsNull(studentEntity.getStudentId())
                                                                                                                                                    // check student id is unique
                                                                                                                                                    .flatMap(checkStudentIdIsUnique -> responseInfoMsg("Student ID already exist")
                                                                                                                                                            // check official email is unique
                                                                                                                                                    ).switchIfEmpty(Mono.defer(() -> studentRepository.findFirstByOfficialEmailAndDeletedAtIsNull(studentEntity.getOfficialEmail())
                                                                                                                                                                    .flatMap(checkOfficialEmailIsUnique -> responseInfoMsg("Official Email already exist"))
                                                                                                                                                            // check Nic is unique
                                                                                                                                                    )).switchIfEmpty(Mono.defer(() -> studentProfileRepository.findFirstByNicAndDeletedAtIsNull(studentProfileEntity.getNic())
                                                                                                                                                                    .flatMap(checkNicIsUnique -> responseInfoMsg("NIC already exist"))
                                                                                                                                                            // check Student profile is unique
                                                                                                                                                    )).switchIfEmpty(Mono.defer(() -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentProfileEntity.getStudentUUID())
                                                                                                                                                            .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Student Profile already exist"))
                                                                                                                                                    )).switchIfEmpty(Mono.defer(() -> {

                                                                                                                                                                //getting List of Contact No. From Front
                                                                                                                                                                List<String> studentContactNoDtoList = new LinkedList<>(value.get("studentContactNoDto"));

                                                                                                                                                                //remove empty space from list
                                                                                                                                                                studentContactNoDtoList.removeIf(s -> s.equals(""));

                                                                                                                                                                //create student Contact Entity list
                                                                                                                                                                List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                                                                                                                                                //create Contact type list
                                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                //create Contact No list
                                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                                //create Contact No Dto list
                                                                                                                                                                List<StudentContactNoDto> stdContactNoDto = new ArrayList<>();

                                                                                                                                                                UUID stdMetaUUID = null;
                                                                                                                                                                UUID contactCategoryUUID = null;

                                                                                                                                                                //create Contact Node
                                                                                                                                                                JsonNode contactNode = null;
                                                                                                                                                                ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                try {
                                                                                                                                                                    contactNode = objectMapper.readTree(studentContactNoDtoList.toString());
                                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                                    e.printStackTrace();
                                                                                                                                                                }
                                                                                                                                                                assert contactNode != null;
                                                                                                                                                                for (JsonNode studentContactNoDto : contactNode) {

                                                                                                                                                                    //  build Student Contact Entity
                                                                                                                                                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                                            .builder()
                                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                                            .status(Boolean.valueOf(value.getFirst("status")))
                                                                                                                                                                            .contactNo(studentContactNoDto.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                            .contactTypeUUID(UUID.fromString(studentContactNoDto.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                            .studentMetaUUID(studentEntity.getUuid())
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

                                                                                                                                                                    studentContactNoEntityList.add(studentContactNoEntity);
                                                                                                                                                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                                                                                                                                                                    contactNoList.add(studentContactNoEntity.getContactNo());
                                                                                                                                                                    stdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                                    contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();

                                                                                                                                                                }

                                                                                                                                                                //Getting Distinct Values Fom the List of Student Contact No List
                                                                                                                                                                studentContactNoEntityList = studentContactNoEntityList.stream()
                                                                                                                                                                        .distinct()
                                                                                                                                                                        .collect(Collectors.toList());

                                                                                                                                                                //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                        .distinct()
                                                                                                                                                                        .collect(Collectors.toList());

                                                                                                                                                                // if contact no list is empty
                                                                                                                                                                if (!studentContactNoEntityList.isEmpty()) {

                                                                                                                                                                    UUID finalStdMetaUUID = stdMetaUUID;
                                                                                                                                                                    UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                    List<StudentContactNoEntity> finalStudentContactNoList = studentContactNoEntityList;

                                                                                                                                                                    List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;
                                                                                                                                                                    return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                            .collectList()
                                                                                                                                                                            .flatMap(contactTypeEntityList -> {


                                                                                                                                                                                if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                    if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                        return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                    } else {

                                                                                                                                                                                        //check if Contact No Record Already Exists against Student and Contact Type
                                                                                                                                                                                        return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStdMetaUUID)
                                                                                                                                                                                                .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.save(studentEntity)
                                                                                                                                                                                                        .then(studentProfileRepository.save(studentProfileEntity))
                                                                                                                                                                                                        .then(studentContactNoRepository.saveAll(finalStudentContactNoList)
                                                                                                                                                                                                                .collectList())
                                                                                                                                                                                                        .flatMap(stdContactNo -> {

                                                                                                                                                                                                            for (StudentContactNoEntity stdContact : stdContactNo) {
                                                                                                                                                                                                                StudentContactNoDto studentContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                                        .contactNo(stdContact.getContactNo())
                                                                                                                                                                                                                        .contactTypeUUID(stdContact.getContactTypeUUID())
                                                                                                                                                                                                                        .build();

                                                                                                                                                                                                                stdContactNoDto.add(studentContactNoDto);
                                                                                                                                                                                                            }

                                                                                                                                                                                                            return facadeDto(studentEntity, studentProfileEntity, stdContactNoDto)
                                                                                                                                                                                                                    .flatMap(facadeDto -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", facadeDto)))
                                                                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                                                                                                                                        }).switchIfEmpty(responseInfoMsg("Unable to Store Record There is something wrong please try again."))
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
                                                                                                                                                                    //if Contact No List is empty then store student and Student Profile
                                                                                                                                                                    return studentRepository.save(studentEntity)
                                                                                                                                                                            .then(studentProfileRepository.save(studentProfileEntity))
                                                                                                                                                                            .flatMap(saveEntity -> facadeDto(studentEntity, studentProfileEntity, stdContactNoDto)
                                                                                                                                                                                    .flatMap(facadeDto -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                            .flatMap(documentUpload -> responseSuccessMsg("Record Stored Successfully", facadeDto)))
                                                                                                                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                            ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                    ))
                                                                                                                                            ).switchIfEmpty(responseInfoMsg("Contact Category record does not exist"))
                                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Contact Category record does not exist. Please contact developer"))
                                                                                                                                    )).switchIfEmpty(responseInfoMsg("Country record does not exist"))
                                                                                                                            .onErrorResume(ex -> responseErrorMsg("Country record does not exist. Please contact developer"))
                                                                                                                    )).switchIfEmpty(responseInfoMsg("State record does not exist"))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("State record does not exist. Please contact developer"))
                                                                                                    )).switchIfEmpty(responseInfoMsg("City record does not exist"))
                                                                                            .onErrorResume(ex -> responseErrorMsg("City record does not exist. Please contact developer"))
                                                                                    )).switchIfEmpty(responseInfoMsg("Unable to upload image"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to upload image. Please contact developer"))
                                                                    ).switchIfEmpty(responseInfoMsg("Marital Status record does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Marital Status record does not exist. Please contact developer"))
                                                            ).switchIfEmpty(responseInfoMsg("Gender record does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Gender record does not exist. Please contact developer"))
                                                    ).switchIfEmpty(responseInfoMsg("Caste record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Caste record does not exist. Please contact developer"))
                                            ).switchIfEmpty(responseInfoMsg("Sect record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Sect record does not exist. Please contact developer"))
                                    ).switchIfEmpty(responseInfoMsg("Religion record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Religion record does not exist. Please contact developer"))
                            ).switchIfEmpty(responseInfoMsg("Campus record does not exist"))
                            .onErrorResume(ex -> responseErrorMsg("Campus record does not exist. Please contact developer"));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-student-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));
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
                .flatMap(value -> studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                        .flatMap(studentEntity -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentProfileEntity updatedEntity = StudentProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentUUID(previousProfileEntity.getStudentUUID())
                                            .status(Boolean.valueOf(value.getFirst("status")))
                                            .description((value.getFirst("description")))
                                            .image(UUID.fromString(value.getFirst("image")))
                                            .firstName(value.getFirst("firstName").trim())
                                            .lastName(value.getFirst("lastName").trim())
                                            .email(value.getFirst("email").trim())
                                            .nic(value.getFirst("nic"))
                                            .telephoneNo(value.getFirst("telephoneNo"))
                                            .birthDate(LocalDateTime.parse((value.getFirst("birthDate")), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
                                            .religionUUID(UUID.fromString(value.getFirst("religionUUID")))
                                            .sectUUID(UUID.fromString(value.getFirst("sectUUID")))
                                            .casteUUID(UUID.fromString(value.getFirst("casteUUID")))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
                                            .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID")))
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
                                    return studentProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check student profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Student Profile already exist"))))
                                            //checks if doc id uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //checks city uuid exists
                                                            .flatMap(studentDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                    .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                            //checks state uuid exists
                                                                            .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                    .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                            //checks countries uuid exists
                                                                                            .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                    .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                            .flatMap(countryJsonNode -> {

                                                                                                                        //getting List of Contact No. From Front
                                                                                                                        List<String> studentContactList = value.get("studentContactNoDto");
                                                                                                                        List<StudentContactNoDto> stdContactNoDto = new ArrayList<>();

                                                                                                                        studentContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!studentContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("student")
                                                                                                                                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing student Contact No Entity
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
                                                                                                                                                    contactNode = new ObjectMapper().readTree(studentContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<StudentContactNoEntity> studentContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedStdMetaUUID = null;

                                                                                                                                                assert contactNode != null;
                                                                                                                                                for (JsonNode motherContact : contactNode) {

                                                                                                                                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(motherContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(motherContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .studentMetaUUID(studentUUID)
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

                                                                                                                                                    studentContactNoList.add(studentContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Student Contact No List
                                                                                                                                                studentContactNoList = studentContactNoList.stream()
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

                                                                                                                                                List<StudentContactNoEntity> finalStudentContactNoList1 = studentContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Student and Contact Type
                                                                                                                                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(studentProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentContactNoList1)
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
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(studentEntity, updatedEntity, stdContactNoDto)
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
                                                                                                                            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentUUID)
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
                                                                                                                                                .flatMap(stdContactList -> studentProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(studentProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(studentProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(studentEntity, updatedEntity, stdContactNoDto)
                                                                                                                                                                        .flatMap(stdFacadeDto -> responseSuccessMsg("Record Updated Successfully", stdFacadeDto))
                                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Unable to Update Record.There is something wrong please try again."))
                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer.")))
                                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to update Document. There is something wrong Please try again."))
                                                                                                                                                                .onErrorResume(err -> responseErrorMsg("Unable to Update Document. Please Contact Developer."))
                                                                                                                                                        )).switchIfEmpty(responseInfoMsg("Unable to Update Document There is something wrong please try again"))
                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update Document.Please Contact Developer."));
                                                                                                                                    });
                                                                                                                        }
                                                                                                                    }
                                                                                                            )).switchIfEmpty(responseInfoMsg("Country does not exist"))
                                                                                                    .onErrorResume(ex -> responseErrorMsg("Country does not exist. Please contact developer."))
                                                                                            )).switchIfEmpty(responseInfoMsg("State does not exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("State does not exist. Please contact developer."))
                                                                            )).switchIfEmpty(responseInfoMsg("City does not exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("City does not exist. Please contact developer."))
                                                            )).switchIfEmpty(responseInfoMsg("Document record does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Document record does not exist. Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg("Student Profile Against the entered student Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Student Profile Against the entered student Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-student-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));
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

        return studentRepository.findByUuidAndDeletedAtIsNull(studentUUID)
                .flatMap(studentEntity -> studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                        .flatMap(studentProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentEntity.setDeletedBy(UUID.fromString(userId));
                                    studentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentEntity.setReqDeletedIP(reqIp);
                                    studentEntity.setReqDeletedPort(reqPort);
                                    studentEntity.setReqDeletedBrowser(reqBrowser);
                                    studentEntity.setReqDeletedOS(reqOs);
                                    studentEntity.setReqDeletedDevice(reqDevice);
                                    studentEntity.setReqDeletedReferer(reqReferer);

                                    studentProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    studentProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentProfileEntity.setReqDeletedIP(reqIp);
                                    studentProfileEntity.setReqDeletedPort(reqPort);
                                    studentProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    studentProfileEntity.setReqDeletedOS(reqOs);
                                    studentProfileEntity.setReqDeletedDevice(reqDevice);
                                    studentProfileEntity.setReqDeletedReferer(reqReferer);


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

                                    return studentRepository.save(studentEntity)
                                            .then(studentProfileRepository.save(studentProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentEntity, studentProfileEntity, stdContactNoDto)
                                                    .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto)))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record. There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record. Please contact developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist. Please contact developer."));
    }

    public Mono<StudentStudentProfileContactNoFacadeDto> facadeDto(StudentEntity studentEntity, StudentProfileEntity studentProfileEntity, List<StudentContactNoDto> studentContactNoDto) {

        StudentStudentProfileContactNoFacadeDto facadeDto = StudentStudentProfileContactNoFacadeDto.builder()
                .id(studentEntity.getId())
                .uuid(studentEntity.getUuid())
                .version(studentEntity.getVersion())
                .studentId(studentEntity.getStudentId())
                .status(studentEntity.getStatus())
                .campusUUID(studentEntity.getCampusUUID())
                .officialEmail(studentEntity.getOfficialEmail())
                .description(studentProfileEntity.getDescription())
                .studentUUID(studentProfileEntity.getStudentUUID())
                .image(studentProfileEntity.getImage())
                .firstName(studentProfileEntity.getFirstName())
                .lastName(studentProfileEntity.getLastName())
                .email(studentProfileEntity.getEmail())
                .telephoneNo(studentProfileEntity.getTelephoneNo())
                .nic(studentProfileEntity.getNic())
                .birthDate(studentProfileEntity.getBirthDate())
                .cityUUID(studentProfileEntity.getCityUUID())
                .stateUUID(studentProfileEntity.getStateUUID())
                .countryUUID(studentProfileEntity.getCountryUUID())
                .religionUUID(studentProfileEntity.getReligionUUID())
                .sectUUID(studentProfileEntity.getSectUUID())
                .casteUUID(studentProfileEntity.getCasteUUID())
                .genderUUID(studentProfileEntity.getGenderUUID())
                .maritalStatusUUID(studentProfileEntity.getMaritalStatusUUID())
                .studentContactNoDto(studentContactNoDto)
                .createdAt(studentEntity.getCreatedAt())
                .createdBy(studentEntity.getCreatedBy())
                .reqCompanyUUID(studentEntity.getReqCompanyUUID())
                .reqBranchUUID(studentEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentEntity.getReqCreatedIP())
                .reqCreatedPort(studentEntity.getReqCreatedPort())
                .reqCreatedOS(studentEntity.getReqCreatedOS())
                .reqCreatedDevice(studentEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentEntity.getReqUpdatedReferer())
                .editable(studentEntity.getEditable())
                .deletable(studentEntity.getDeletable())
                .archived(studentEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentProfileContactNoFacadeDto> updatedFacadeDto(StudentEntity studentEntity, StudentProfileEntity studentProfileEntity, List<StudentContactNoDto> studentContactNoDto) {


        StudentProfileContactNoFacadeDto facadeDto = StudentProfileContactNoFacadeDto.builder()
                .id(studentEntity.getId())
                .uuid(studentEntity.getUuid())
                .version(studentEntity.getVersion())
                .status(studentProfileEntity.getStatus())
                .description(studentProfileEntity.getDescription())
                .studentUUID(studentProfileEntity.getStudentUUID())
                .image(studentProfileEntity.getImage())
                .firstName(studentProfileEntity.getFirstName())
                .lastName(studentProfileEntity.getLastName())
                .email(studentProfileEntity.getEmail())
                .telephoneNo(studentProfileEntity.getTelephoneNo())
                .nic(studentProfileEntity.getNic())
                .birthDate(studentProfileEntity.getBirthDate())
                .cityUUID(studentProfileEntity.getCityUUID())
                .stateUUID(studentProfileEntity.getStateUUID())
                .countryUUID(studentProfileEntity.getCountryUUID())
                .religionUUID(studentProfileEntity.getReligionUUID())
                .sectUUID(studentProfileEntity.getSectUUID())
                .casteUUID(studentProfileEntity.getCasteUUID())
                .genderUUID(studentProfileEntity.getGenderUUID())
                .maritalStatusUUID(studentProfileEntity.getMaritalStatusUUID())
                .studentContactNoDto(studentContactNoDto)
                .createdAt(studentEntity.getCreatedAt())
                .createdBy(studentEntity.getCreatedBy())
                .updatedAt(studentEntity.getUpdatedAt())
                .updatedBy(studentEntity.getUpdatedBy())
                .reqCompanyUUID(studentEntity.getReqCompanyUUID())
                .reqBranchUUID(studentEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentEntity.getReqCreatedIP())
                .reqCreatedPort(studentEntity.getReqCreatedPort())
                .reqCreatedOS(studentEntity.getReqCreatedOS())
                .reqCreatedDevice(studentEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentEntity.getReqUpdatedReferer())
                .editable(studentEntity.getEditable())
                .deletable(studentEntity.getDeletable())
                .archived(studentEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
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
