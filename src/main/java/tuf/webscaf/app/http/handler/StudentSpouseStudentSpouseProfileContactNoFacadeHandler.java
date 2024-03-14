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
import tuf.webscaf.app.dbContext.master.dto.*;
import tuf.webscaf.app.dbContext.master.dto.StudentContactNoDto;
import tuf.webscaf.app.dbContext.master.entity.*;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentSpouseProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSpouseProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentSpouseRepository;
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

@Tag(name = "studentSpouseStudentSpouseProfileContactNoFacade")
@Component
public class StudentSpouseStudentSpouseProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentSpouseRepository studentSpouseRepository;

    @Autowired
    SlaveStudentSpouseProfileRepository slaveStudentSpouseProfileRepository;

    @Autowired
    StudentSpouseProfileRepository studentSpouseProfileRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

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
    SlaveStudentSpouseRepository slaveStudentSpouseRepository;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_student-spouse-student-spouse-profile-contact-nos_index")
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
            Flux<SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto> slaveStudentSpouseStudentSpouseProfileContactNoFacadeDtoFlux = slaveStudentSpouseRepository
                    .indexWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());
            return slaveStudentSpouseStudentSpouseProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentSpouseProfileEntity -> slaveStudentSpouseRepository
                            .countStudentSpouseStudentSpouseProfileContactNoWithStatus(searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count ->
                            {
                                if (studentSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto> slaveStudentSpouseStudentSpouseProfileContactNoFacadeDtoFlux = slaveStudentSpouseRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveStudentSpouseStudentSpouseProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(studentSpouseProfileEntity -> slaveStudentSpouseRepository
                            .countStudentSpouseStudentSpouseProfileContactNoWithOutStatus(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (studentSpouseProfileEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", studentSpouseProfileEntity.stream().distinct(), count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-spouse-student-spouse-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID studentSpouseUUID = UUID.fromString((serverRequest.pathVariable("studentSpouseUUID")));

        return slaveStudentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                .flatMap(studentSpouseEntity -> slaveStudentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                        .flatMap(studentSpouseProfileEntity -> slaveStudentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNo -> {
                                    List<SlaveStudentContactNoFacadeDto> studentContactNoDto = new ArrayList<>();

                                    for (SlaveStudentContactNoEntity studentContact : studentContactNo) {
                                        SlaveStudentContactNoFacadeDto studentSpouseContactNoDto = SlaveStudentContactNoFacadeDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentSpouseContactNoDto);
                                    }

                                    return showFacadeDto(studentSpouseEntity, studentSpouseProfileEntity, studentContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Student Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Student Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Spouse Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Student Spouse Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Spouse Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Spouse Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto> showFacadeDto(SlaveStudentSpouseEntity slaveStudentSpouseEntity, SlaveStudentSpouseProfileEntity slaveStudentSpouseProfileEntity, List<SlaveStudentContactNoFacadeDto> slaveStudentContactNoFacadeDto) {

        SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto facadeDto = SlaveStudentSpouseStudentSpouseProfileContactNoFacadeDto.builder()
                .id(slaveStudentSpouseEntity.getId())
                .uuid(slaveStudentSpouseEntity.getUuid())
                .version(slaveStudentSpouseEntity.getVersion())
                .status(slaveStudentSpouseEntity.getStatus())
                .studentUUID(slaveStudentSpouseEntity.getStudentUUID())
                .studentSpouseAsStudentUUID(slaveStudentSpouseEntity.getStudentUUID())
                .studentSpouseUUID(slaveStudentSpouseEntity.getUuid())
                .noOfDependents(slaveStudentSpouseProfileEntity.getNoOfDependents())
                .image(slaveStudentSpouseProfileEntity.getImage())
                .name(slaveStudentSpouseProfileEntity.getName())
                .nic(slaveStudentSpouseProfileEntity.getNic())
                .age(slaveStudentSpouseProfileEntity.getAge())
                .officialTel(slaveStudentSpouseProfileEntity.getOfficialTel())
                .cityUUID(slaveStudentSpouseProfileEntity.getCityUUID())
                .stateUUID(slaveStudentSpouseProfileEntity.getStateUUID())
                .countryUUID(slaveStudentSpouseProfileEntity.getCountryUUID())
                .genderUUID(slaveStudentSpouseProfileEntity.getGenderUUID())
                .email(slaveStudentSpouseProfileEntity.getEmail())
                .studentSpouseContactNoDto(slaveStudentContactNoFacadeDto)
                .createdAt(slaveStudentSpouseEntity.getCreatedAt())
                .createdBy(slaveStudentSpouseEntity.getCreatedBy())
                .reqCompanyUUID(slaveStudentSpouseEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveStudentSpouseEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveStudentSpouseEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveStudentSpouseEntity.getReqCreatedIP())
                .reqCreatedPort(slaveStudentSpouseEntity.getReqCreatedPort())
                .reqCreatedOS(slaveStudentSpouseEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveStudentSpouseEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveStudentSpouseEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveStudentSpouseEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveStudentSpouseEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveStudentSpouseEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveStudentSpouseEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveStudentSpouseEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveStudentSpouseEntity.getReqUpdatedReferer())
                .editable(slaveStudentSpouseEntity.getEditable())
                .deletable(slaveStudentSpouseEntity.getDeletable())
                .archived(slaveStudentSpouseEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }


    public Mono<StudentSpouseStudentSpouseProfileContactNoFacadeDto> facadeDto(StudentSpouseEntity studentSpouseEntity, StudentSpouseProfileEntity studentSpouseProfileEntity, List<StudentContactNoDto> studentSpouseContactNoDto) {

        StudentSpouseStudentSpouseProfileContactNoFacadeDto facadeDto = StudentSpouseStudentSpouseProfileContactNoFacadeDto.builder()
                .id(studentSpouseEntity.getId())
                .uuid(studentSpouseEntity.getUuid())
                .version(studentSpouseEntity.getVersion())
                .status(studentSpouseEntity.getStatus())
                .studentUUID(studentSpouseEntity.getStudentUUID())
                .studentSpouseAsStudentUUID(studentSpouseEntity.getStudentUUID())
                .studentSpouseUUID(studentSpouseEntity.getUuid())
                .noOfDependents(studentSpouseProfileEntity.getNoOfDependents())
                .image(studentSpouseProfileEntity.getImage())
                .name(studentSpouseProfileEntity.getName())
                .nic(studentSpouseProfileEntity.getNic())
                .age(studentSpouseProfileEntity.getAge())
                .officialTel(studentSpouseProfileEntity.getOfficialTel())
                .cityUUID(studentSpouseProfileEntity.getCityUUID())
                .stateUUID(studentSpouseProfileEntity.getStateUUID())
                .countryUUID(studentSpouseProfileEntity.getCountryUUID())
                .genderUUID(studentSpouseProfileEntity.getGenderUUID())
                .email(studentSpouseProfileEntity.getEmail())
                .studentSpouseContactNoDto(studentSpouseContactNoDto)
                .createdAt(studentSpouseEntity.getCreatedAt())
                .createdBy(studentSpouseEntity.getCreatedBy())
                .reqCompanyUUID(studentSpouseEntity.getReqCompanyUUID())
                .reqBranchUUID(studentSpouseEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentSpouseEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentSpouseEntity.getReqCreatedIP())
                .reqCreatedPort(studentSpouseEntity.getReqCreatedPort())
                .reqCreatedOS(studentSpouseEntity.getReqCreatedOS())
                .reqCreatedDevice(studentSpouseEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentSpouseEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentSpouseEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentSpouseEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentSpouseEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentSpouseEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentSpouseEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentSpouseEntity.getReqUpdatedReferer())
                .editable(studentSpouseEntity.getEditable())
                .deletable(studentSpouseEntity.getDeletable())
                .archived(studentSpouseEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<StudentSpouseProfileContactNoFacadeDto> updatedFacadeDto(StudentSpouseEntity studentSpouseEntity, StudentSpouseProfileEntity studentSpouseProfileEntity, List<StudentContactNoDto> studentSpouseContactNoDto) {

        StudentSpouseProfileContactNoFacadeDto facadeDto = StudentSpouseProfileContactNoFacadeDto.builder()
                .id(studentSpouseEntity.getId())
                .uuid(studentSpouseEntity.getUuid())
                .version(studentSpouseEntity.getVersion())
                .status(studentSpouseEntity.getStatus())
                .studentSpouseAsStudentUUID(studentSpouseEntity.getStudentUUID())
                .image(studentSpouseProfileEntity.getImage())
                .name(studentSpouseProfileEntity.getName())
                .nic(studentSpouseProfileEntity.getNic())
                .age(studentSpouseProfileEntity.getAge())
                .noOfDependents(studentSpouseProfileEntity.getNoOfDependents())
                .officialTel(studentSpouseProfileEntity.getOfficialTel())
                .cityUUID(studentSpouseProfileEntity.getCityUUID())
                .stateUUID(studentSpouseProfileEntity.getStateUUID())
                .countryUUID(studentSpouseProfileEntity.getCountryUUID())
                .genderUUID(studentSpouseProfileEntity.getGenderUUID())
                .email(studentSpouseProfileEntity.getEmail())
                .studentSpouseContactNoDto(studentSpouseContactNoDto)
                .createdAt(studentSpouseEntity.getCreatedAt())
                .createdBy(studentSpouseEntity.getCreatedBy())
                .updatedAt(studentSpouseEntity.getUpdatedAt())
                .updatedBy(studentSpouseEntity.getUpdatedBy())
                .reqCompanyUUID(studentSpouseProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(studentSpouseProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(studentSpouseProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(studentSpouseProfileEntity.getReqCreatedIP())
                .reqCreatedPort(studentSpouseProfileEntity.getReqCreatedPort())
                .reqCreatedOS(studentSpouseProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(studentSpouseProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(studentSpouseProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(studentSpouseProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(studentSpouseProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(studentSpouseProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(studentSpouseProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(studentSpouseProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(studentSpouseProfileEntity.getReqUpdatedReferer())
                .editable(studentSpouseProfileEntity.getEditable())
                .deletable(studentSpouseProfileEntity.getDeletable())
                .archived(studentSpouseProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-spouse-student-spouse-profile-contact-nos_store")
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

                    UUID studentSpouseAsStudentUUID = null;
                    if ((value.containsKey("studentSpouseAsStudentUUID") && (value.getFirst("studentSpouseAsStudentUUID") != ""))) {
                        studentSpouseAsStudentUUID = UUID.fromString(value.getFirst("studentSpouseAsStudentUUID").trim());
                    }

                    UUID studentSpouseAsTeacherUUID = null;
                    if ((value.containsKey("studentSpouseAsTeacherUUID") && (value.getFirst("studentSpouseAsTeacherUUID") != ""))) {
                        studentSpouseAsTeacherUUID = UUID.fromString(value.getFirst("studentSpouseAsTeacherUUID").trim());
                    }

                    StudentSpouseEntity studentSpouseEntity = StudentSpouseEntity.builder()
                            .uuid(UUID.randomUUID())
                            .studentUUID(UUID.fromString(value.getFirst("studentUUID").trim()))
                            .teacherUUID(studentSpouseAsTeacherUUID)
                            .studentSpouseUUID(studentSpouseAsStudentUUID)
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
                    return studentRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID())
                            //check if Student Spouse Record Already Exists Against the same student
                            .flatMap(studentEntity -> {

                                MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                //Building Student Spouse Profile Record
                                StudentSpouseProfileEntity studentSpouseProfileEntity = StudentSpouseProfileEntity
                                        .builder()
                                        .uuid(UUID.randomUUID())
                                        .studentSpouseUUID(studentSpouseEntity.getUuid())
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

                                sendFormData.add("docId", String.valueOf(studentSpouseProfileEntity.getImage()));

                                //check if Gender Record Exists or Not
                                return genderRepository.findByUuidAndDeletedAtIsNull(studentSpouseProfileEntity.getGenderUUID())
                                        //check if City Record Exists or Not
                                        .flatMap(genderEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", studentSpouseProfileEntity.getCityUUID())
                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                        //check if State Record Exists or not
                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", studentSpouseProfileEntity.getStateUUID())
                                                                .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                        //check if Country Record Exists or not
                                                                        .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", studentSpouseProfileEntity.getCountryUUID())
                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                        //check if NIC Is Unique Against Student Spouse
                                                                                        .flatMap(checkNIC -> studentSpouseProfileRepository.findFirstByNicAndDeletedAtIsNull(studentSpouseProfileEntity.getNic())
                                                                                                .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist")))
                                                                                        //check if Spouse Profile Already Exists Against Student Spouse
                                                                                        .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseProfileEntity.getStudentSpouseUUID())
                                                                                                .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Spouse Profile already exist"))))
                                                                                        //check if Document Record Exists or not
                                                                                        .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", studentSpouseProfileEntity.getImage())
                                                                                                .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                        .flatMap(documentEntity -> {

                                                                                                                    // if student uuid is same as studentSpouseAsStudentUUID uuid
                                                                                                                    if (studentSpouseEntity.getStudentUUID().equals(studentSpouseEntity.getStudentSpouseUUID())) {
                                                                                                                        return responseInfoMsg("The student spouse cannot be the same as the given student");
                                                                                                                    }

                                                                                                                    // if student spouse is teacher and student
                                                                                                                    else if (studentSpouseEntity.getTeacherUUID() != null && studentSpouseEntity.getStudentSpouseUUID() != null) {
                                                                                                                        return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getStudentSpouseUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getTeacherUUID())
                                                                                                                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getTeacherUUID())
                                                                                                                                        .flatMap(teacherEntity -> studentRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getStudentSpouseUUID())
                                                                                                                                                .flatMap(studentRecord -> storeFacadeRecord(studentSpouseEntity, studentSpouseProfileEntity, value.get("studentSpouseContactNoDto"), sendFormData)
                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // if student spouse is teacher
                                                                                                                    else if (studentSpouseEntity.getTeacherUUID() != null) {
                                                                                                                        return studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getTeacherUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getTeacherUUID())
                                                                                                                                        .flatMap(teacherEntity -> storeFacadeRecord(studentSpouseEntity, studentSpouseProfileEntity, value.get("studentSpouseContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // if student spouse is student
                                                                                                                    else if (studentSpouseEntity.getStudentSpouseUUID() != null) {
                                                                                                                        return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getStudentUUID(), studentSpouseEntity.getStudentSpouseUUID())
                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
                                                                                                                                .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(studentSpouseEntity.getStudentSpouseUUID())
                                                                                                                                        .flatMap(studentRecord -> storeFacadeRecord(studentSpouseEntity, studentSpouseProfileEntity, value.get("studentSpouseContactNoDto"), sendFormData))
                                                                                                                                        .switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
                                                                                                                                ));
                                                                                                                    }

                                                                                                                    // else store the record
                                                                                                                    else {
                                                                                                                        return storeFacadeRecord(studentSpouseEntity, studentSpouseProfileEntity, value.get("studentSpouseContactNoDto"), sendFormData);
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


    public Mono<ServerResponse> storeFacadeRecord(StudentSpouseEntity studentSpouseEntity, StudentSpouseProfileEntity studentSpouseProfileEntity, List<String> studentSpouseContactList, MultiValueMap<String, String> sendFormData) {

        //check if Contact Category is Spouse
        return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
                .flatMap(contactCategoryEntity -> {
                    //Creating an empty list to add student Contact No Records
                    List<StudentContactNoEntity> studentSpouseContactNoList = new ArrayList<>();

                    // Creating an empty list to add contact Type UUID's
                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                    // Creating an empty list to add contact No's
                    List<String> contactNoList = new ArrayList<>();


                    JsonNode contactNode = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        contactNode = objectMapper.readTree(studentSpouseContactList.toString());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    assert contactNode != null;


                    UUID studentMetaUUID = null;
                    UUID contactCategoryUUID = null;

                    //iterating over the json node from front and setting contact No's
                    for (JsonNode spouseContact : contactNode) {

                        StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                .builder()
                                .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                .studentMetaUUID(studentSpouseEntity.getUuid())
                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                .createdBy(studentSpouseEntity.getCreatedBy())
                                .reqCompanyUUID(studentSpouseEntity.getReqCompanyUUID())
                                .reqBranchUUID(studentSpouseEntity.getReqBranchUUID())
                                .reqCreatedIP(studentSpouseEntity.getReqCreatedIP())
                                .reqCreatedPort(studentSpouseEntity.getReqCreatedPort())
                                .reqCreatedBrowser(studentSpouseEntity.getReqCreatedBrowser())
                                .reqCreatedOS(studentSpouseEntity.getReqCreatedOS())
                                .reqCreatedDevice(studentSpouseEntity.getReqCreatedDevice())
                                .reqCreatedReferer(studentSpouseEntity.getReqCreatedReferer())
                                .build();

                        studentSpouseContactNoList.add(studentContactNoEntity);

                        contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
                        contactNoList.add(studentContactNoEntity.getContactNo());
                        studentMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                        contactCategoryUUID = studentContactNoEntity.getContactCategoryUUID();
                    }

                    //Getting Distinct Values Fom the List of Student Spouse Contact No List
                    studentSpouseContactNoList = studentSpouseContactNoList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    //Getting Distinct Values Fom the List of Contact Type UUID
                    contactTypeUUIDList = contactTypeUUIDList.stream()
                            .distinct()
                            .collect(Collectors.toList());

                    // Creating an empty list to add contact No's and returning dto with response
                    List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                    if (!studentSpouseContactNoList.isEmpty()) {

                        UUID finalStdMetaUUID = studentMetaUUID;

                        UUID finalContactCategoryUUID = contactCategoryUUID;

                        List<StudentContactNoEntity> finalStudentSpouseContactNoList = studentSpouseContactNoList;

                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                .collectList()
                                .flatMap(contactTypeEntityList -> {

                                    if (!contactTypeEntityList.isEmpty()) {

                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                            return responseInfoMsg("Contact Type Does not Exist");
                                        } else {
                                            //check if Contact No Record Already Exists against Student Spouse and Contact Type
                                            return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndStudentMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalStdMetaUUID)
                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                    .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.save(studentSpouseEntity)
                                                            .then(studentSpouseProfileRepository.save(studentSpouseProfileEntity))
                                                            .then(studentContactNoRepository.saveAll(finalStudentSpouseContactNoList)
                                                                    .collectList())
                                                            .flatMap(mthContactNo -> {

                                                                for (StudentContactNoEntity studentContact : mthContactNo) {
                                                                    StudentContactNoDto studentSpouseContactNoDto = StudentContactNoDto.builder()
                                                                            .contactNo(studentContact.getContactNo())
                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                            .build();

                                                                    studentContactNoDto.add(studentSpouseContactNoDto);
                                                                }

                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", studentSpouseEntity.getCreatedBy().toString(),
                                                                                studentSpouseEntity.getReqCompanyUUID().toString(), studentSpouseEntity.getReqBranchUUID().toString())
                                                                        .flatMap(docUpdate -> facadeDto(studentSpouseEntity, studentSpouseProfileEntity, studentContactNoDto)
                                                                                .flatMap(studentSpouseFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentSpouseFacadeDto))
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
                        //if Contact No List is empty then store student Spouse and Student Spouse Profile
                        return studentSpouseRepository.save(studentSpouseEntity)
                                //Save Student Spouse Profile Entity
                                .then(studentSpouseProfileRepository.save(studentSpouseProfileEntity))
                                //update Document Status After Storing record
                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", studentSpouseEntity.getCreatedBy().toString(),
                                                studentSpouseEntity.getReqCompanyUUID().toString(), studentSpouseEntity.getReqBranchUUID().toString())
                                        .flatMap(docUpdate -> facadeDto(studentSpouseEntity, studentSpouseProfileEntity, studentContactNoDto)
                                                .flatMap(studentSpouseFacadeDto -> responseSuccessMsg("Record Stored Successfully", studentSpouseFacadeDto))
                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                    }
                });

    }

    @AuthHasPermission(value = "academic_api_v1_facade_student-spouse-student-spouse-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID studentSpouseUUID = UUID.fromString((serverRequest.pathVariable("studentSpouseUUID")));
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
                .flatMap(value -> studentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                        .flatMap(studentSpouseEntity -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    StudentSpouseProfileEntity updatedEntity = StudentSpouseProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .studentSpouseUUID(previousProfileEntity.getStudentSpouseUUID())
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
                                    return studentSpouseProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
                                            //check spouse profile is unique
                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSpouseUUID(), updatedEntity.getUuid())
                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Spouse Profile already exist"))))
                                            //checks if spouse uuid exists
                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                            //check if Gender Record Exists or Not
                                                            .flatMap(studentSpouseDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
                                                                                                                        List<String> studentSpouseContactList = value.get("studentSpouseContactNoDto");
                                                                                                                        List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();

                                                                                                                        studentSpouseContactList.removeIf(s -> s.equals(""));

                                                                                                                        if (!studentSpouseContactList.isEmpty()) {
                                                                                                                            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
                                                                                                                                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseUUID)
                                                                                                                                            .collectList()
                                                                                                                                            .flatMap(existingContactList -> {

                                                                                                                                                //Removing Already existing Student Spouse Contact No Entity
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
                                                                                                                                                    contactNode = new ObjectMapper().readTree(studentSpouseContactList.toString());
                                                                                                                                                } catch (JsonProcessingException e) {
                                                                                                                                                    e.printStackTrace();
                                                                                                                                                }

                                                                                                                                                //New Contact No list for adding values after building entity
                                                                                                                                                List<StudentContactNoEntity> stdSpouseContactNoList = new ArrayList<>();

                                                                                                                                                List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                List<String> contactNoList = new ArrayList<>();

                                                                                                                                                UUID updatedStdMetaUUID = null;

                                                                                                                                                assert contactNode != null;
                                                                                                                                                for (JsonNode spouseContact : contactNode) {

                                                                                                                                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
                                                                                                                                                            .builder()
                                                                                                                                                            .uuid(UUID.randomUUID())
                                                                                                                                                            .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                            .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                            .studentMetaUUID(studentSpouseUUID)
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

                                                                                                                                                    stdSpouseContactNoList.add(studentContactNoEntity);

                                                                                                                                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());

                                                                                                                                                    contactNoList.add(studentContactNoEntity.getContactNo());

                                                                                                                                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
                                                                                                                                                }

                                                                                                                                                //Getting Distinct Values Fom the List of Student Spouse Contact No List
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

                                                                                                                                                List<StudentContactNoEntity> finalStudentSpouseContactNoList1 = stdSpouseContactNoList;

                                                                                                                                                List<String> finalContactNoList = contactNoList;

                                                                                                                                                return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                        .collectList()
                                                                                                                                                        .flatMap(contactTypeEntityList -> {

                                                                                                                                                            if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                    return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                } else {

                                                                                                                                                                    //check if Contact No Record Already Exists against Student Spouse and Contact Type
                                                                                                                                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
                                                                                                                                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                    .then(studentSpouseProfileRepository.save(updatedEntity))
                                                                                                                                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                            .collectList())
                                                                                                                                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentSpouseContactNoList1)
                                                                                                                                                                                            .collectList()
                                                                                                                                                                                            .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
                                                                                                                                                                                                    StudentContactNoDto studentSpouseContactNoDto = StudentContactNoDto.builder()
                                                                                                                                                                                                            .contactNo(studentContact.getContactNo())
                                                                                                                                                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
                                                                                                                                                                                                            .build();

                                                                                                                                                                                                    studentContactNoDto.add(studentSpouseContactNoDto);
                                                                                                                                                                                                }

                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdate -> updatedFacadeDto(studentSpouseEntity, updatedEntity, studentContactNoDto)
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
                                                                                                                            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseUUID)
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
                                                                                                                                                .flatMap(studentContactList -> studentSpouseProfileRepository.save(previousProfileEntity)
                                                                                                                                                        .then(studentSpouseProfileRepository.save(updatedEntity))
                                                                                                                                                        .flatMap(StudentSpouseProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                .flatMap(docUpdateEntity -> updatedFacadeDto(studentSpouseEntity, updatedEntity, studentContactNoDto)
                                                                                                                                                                        .flatMap(StudentSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", StudentSpouseFacadeDto))
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
                                }).switchIfEmpty(responseInfoMsg("Spouse Profile Against the entered Student Spouse Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg("Spouse Profile Against the entered Student Spouse Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Student Spouse Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Student Spouse Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }

//    @AuthHasPermission(value = "academic_api_v1_facade_student-spouse-student-spouse-profile-contact-nos_update")
//    public Mono<ServerResponse> update(ServerRequest serverRequest) {
//        UUID studentSpouseUUID = UUID.fromString((serverRequest.pathVariable("studentSpouseUUID")));
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
//                .flatMap(value -> studentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
//                        .flatMap(studentSpouseEntity -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseUUID)
//                                .flatMap(previousProfileEntity -> {
//
//                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();
//
//                                    StudentSpouseProfileEntity updatedEntity = StudentSpouseProfileEntity.builder()
//                                            .uuid(previousProfileEntity.getUuid())
//                                            .studentSpouseUUID(previousProfileEntity.getStudentSpouseUUID())
//                                            .image(UUID.fromString(value.getFirst("image")))
//                                            .name(value.getFirst("name").trim())
//                                            .nic(value.getFirst("nic"))
//                                            .age(Integer.valueOf(value.getFirst("age")))
//                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID")))
//                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID")))
//                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID")))
//                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID")))
//                                            .noOfDependents(Integer.valueOf(value.getFirst("noOfDependents")))
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
//                                    return studentSpouseProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
//                                            .flatMap(checkNicMsg -> responseInfoMsg("NIC already exist"))
//                                            //check spouse profile is unique
//                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getStudentSpouseUUID(), updatedEntity.getUuid())
//                                                    .flatMap(studentProfileAlreadyExists -> responseInfoMsg("Spouse Profile already exist"))))
//                                            //checks if spouse uuid exists
//                                            .switchIfEmpty(Mono.defer(() -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
//                                                    .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
//                                                            //check if Gender Record Exists or Not
//                                                            .flatMap(studentSpouseDocumentEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
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
//                                                                                                                        UUID studentSpouseAsTeacherUUID = null;
//                                                                                                                        UUID studentSpouseAsStudentUUID = null;
//
//                                                                                                                        if ((value.containsKey("studentSpouseAsTeacherUUID") && (value.getFirst("studentSpouseAsTeacherUUID") != ""))) {
//                                                                                                                            studentSpouseAsTeacherUUID = UUID.fromString(value.getFirst("studentSpouseAsTeacherUUID").trim());
//                                                                                                                        }
//
//                                                                                                                        if ((value.containsKey("studentSpouseAsStudentUUID") && (value.getFirst("studentSpouseAsStudentUUID") != ""))) {
//                                                                                                                            studentSpouseAsStudentUUID = UUID.fromString(value.getFirst("studentSpouseAsStudentUUID").trim());
//                                                                                                                        }
//
//
//                                                                                                                        // if student spouse entity isn't updated
//                                                                                                                        if (studentSpouseEntity.getTeacherUUID() == studentSpouseAsTeacherUUID && studentSpouseEntity.getStudentSpouseUUID() == studentSpouseAsStudentUUID) {
//                                                                                                                            return updateFacadeRecord(studentSpouseEntity, previousProfileEntity, updatedEntity, value.get("studentSpouseContactNoDto"), sendFormData);
//                                                                                                                        }
//
//
//                                                                                                                        // else update all entities
//                                                                                                                        else {
//                                                                                                                            StudentSpouseEntity updatedStudentSpouseEntity = StudentSpouseEntity.builder()
//                                                                                                                                    .uuid(studentSpouseEntity.getUuid())
//                                                                                                                                    .studentUUID(studentSpouseEntity.getStudentUUID())
//                                                                                                                                    .teacherUUID(studentSpouseAsTeacherUUID)
//                                                                                                                                    .studentSpouseUUID(studentSpouseAsStudentUUID)
//                                                                                                                                    .status(Boolean.valueOf(value.getFirst("status")))
//                                                                                                                                    .createdAt(studentSpouseEntity.getCreatedAt())
//                                                                                                                                    .createdBy(studentSpouseEntity.getCreatedBy())
//                                                                                                                                    .updatedBy(UUID.fromString(userId))
//                                                                                                                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                                                                                                                    .reqCreatedIP(studentSpouseEntity.getReqCreatedIP())
//                                                                                                                                    .reqCreatedPort(studentSpouseEntity.getReqCreatedPort())
//                                                                                                                                    .reqCreatedBrowser(studentSpouseEntity.getReqCreatedBrowser())
//                                                                                                                                    .reqCreatedOS(studentSpouseEntity.getReqCreatedOS())
//                                                                                                                                    .reqCreatedDevice(studentSpouseEntity.getReqCreatedDevice())
//                                                                                                                                    .reqCreatedReferer(studentSpouseEntity.getReqCreatedReferer())
//                                                                                                                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
//                                                                                                                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
//                                                                                                                                    .reqUpdatedIP(reqIp)
//                                                                                                                                    .reqUpdatedPort(reqPort)
//                                                                                                                                    .reqUpdatedBrowser(reqBrowser)
//                                                                                                                                    .reqUpdatedOS(reqOs)
//                                                                                                                                    .reqUpdatedDevice(reqDevice)
//                                                                                                                                    .reqUpdatedReferer(reqReferer)
//                                                                                                                                    .build();
//
//                                                                                                                            studentSpouseEntity.setDeletedBy(UUID.fromString(userId));
//                                                                                                                            studentSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
//                                                                                                                            studentSpouseEntity.setReqDeletedIP(reqIp);
//                                                                                                                            studentSpouseEntity.setReqDeletedPort(reqPort);
//                                                                                                                            studentSpouseEntity.setReqDeletedBrowser(reqBrowser);
//                                                                                                                            studentSpouseEntity.setReqDeletedOS(reqOs);
//                                                                                                                            studentSpouseEntity.setReqDeletedDevice(reqDevice);
//                                                                                                                            studentSpouseEntity.setReqDeletedReferer(reqReferer);
//
//                                                                                                                            // if same student uuid is given as both student and student spouse's student uuid
//                                                                                                                            if (updatedStudentSpouseEntity.getStudentUUID().equals(updatedStudentSpouseEntity.getStudentSpouseUUID())) {
//                                                                                                                                return responseInfoMsg("The student spouse cannot be the same as the given student");
//                                                                                                                            }
//
//                                                                                                                            // if student spouse is teacher and student
//                                                                                                                            else if (updatedStudentSpouseEntity.getTeacherUUID() != null && updatedStudentSpouseEntity.getStudentSpouseUUID() != null) {
//                                                                                                                                return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStudentSpouseEntity.getStudentUUID(), updatedStudentSpouseEntity.getStudentSpouseUUID(), updatedEntity.getUuid())
//                                                                                                                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
//                                                                                                                                        .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStudentSpouseEntity.getStudentUUID(), updatedStudentSpouseEntity.getTeacherUUID(), updatedStudentSpouseEntity.getUuid())
//                                                                                                                                                .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))))
//                                                                                                                                        .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedStudentSpouseEntity.getTeacherUUID())
//                                                                                                                                                .flatMap(studentEntity -> studentRepository.findByUuidAndDeletedAtIsNull(updatedStudentSpouseEntity.getStudentSpouseUUID())
//                                                                                                                                                        .flatMap(studentRecord -> updateFacadeRecordWithSpouse(studentSpouseEntity, updatedStudentSpouseEntity, previousProfileEntity, updatedEntity, value.get("studentSpouseContactNoDto"), sendFormData))
//                                                                                                                                                        .switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
//                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
//                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
//                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
//                                                                                                                                        ));
//                                                                                                                            }
//
//                                                                                                                            // if student spouse is teacher
//                                                                                                                            else if (updatedStudentSpouseEntity.getTeacherUUID() != null) {
//                                                                                                                                return studentSpouseRepository.findFirstByStudentUUIDAndTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStudentSpouseEntity.getStudentUUID(), updatedStudentSpouseEntity.getTeacherUUID(), updatedStudentSpouseEntity.getUuid())
//                                                                                                                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Teacher"))
//                                                                                                                                        .switchIfEmpty(Mono.defer(() -> teacherRepository.findByUuidAndDeletedAtIsNull(updatedStudentSpouseEntity.getTeacherUUID())
//                                                                                                                                                .flatMap(studentEntity -> updateFacadeRecordWithSpouse(studentSpouseEntity, updatedStudentSpouseEntity, previousProfileEntity, updatedEntity, value.get("studentSpouseContactNoDto"), sendFormData))
//                                                                                                                                                .switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
//                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Teacher Record does not exist. Please contact developer."))
//                                                                                                                                        ));
//                                                                                                                            }
//
//
//                                                                                                                            // if student spouse is student
//                                                                                                                            else if (updatedStudentSpouseEntity.getStudentSpouseUUID() != null) {
//                                                                                                                                return studentSpouseRepository.findFirstByStudentUUIDAndStudentSpouseUUIDAndDeletedAtIsNullAndUuidIsNot(updatedStudentSpouseEntity.getStudentUUID(), updatedStudentSpouseEntity.getStudentSpouseUUID(), updatedStudentSpouseEntity.getUuid())
//                                                                                                                                        .flatMap(recordAlreadyExists -> responseInfoMsg("Student Spouse Record Already Exists for Given Student"))
//                                                                                                                                        .switchIfEmpty(Mono.defer(() -> studentRepository.findByUuidAndDeletedAtIsNull(updatedStudentSpouseEntity.getStudentSpouseUUID())
//                                                                                                                                                .flatMap(studentRecord -> updateFacadeRecordWithSpouse(studentSpouseEntity, updatedStudentSpouseEntity, previousProfileEntity, updatedEntity, value.get("studentSpouseContactNoDto"), sendFormData))
//                                                                                                                                                .switchIfEmpty(responseInfoMsg("Spouse Student Record does not exist"))
//                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Spouse Student Record does not exist. Please contact developer."))
//                                                                                                                                        ));
//                                                                                                                            }
//
//
//                                                                                                                            // else update the record
//                                                                                                                            else {
//                                                                                                                                return updateFacadeRecordWithSpouse(studentSpouseEntity, updatedStudentSpouseEntity, previousProfileEntity, updatedEntity, value.get("studentSpouseContactNoDto"), sendFormData);
//                                                                                                                            }
//                                                                                                                        }
//
//                                                                                                                    })).switchIfEmpty(responseInfoMsg("Country does not exist"))
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
//                                }).switchIfEmpty(responseInfoMsg("Spouse Profile Against the entered Student Spouse Record Does not exist"))
//                                .onErrorResume(ex -> responseErrorMsg("Spouse Profile Against the entered Student Spouse Record Does not exist.Please Contact Developer."))
//                        ).switchIfEmpty(responseInfoMsg("Student Spouse Record Does not Exist."))
//                        .onErrorResume(ex -> responseErrorMsg("Student Spouse Record Does not Exist.Please Contact Developer."))
//                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
//                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
//    }


//    public Mono<ServerResponse> updateFacadeRecord(StudentSpouseEntity studentSpouseEntity, StudentSpouseProfileEntity previousProfileEntity, StudentSpouseProfileEntity updatedEntity, List<String> studentSpouseContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();
//
//        studentSpouseContactList.removeIf(s -> s.equals(""));
//
//        if (!studentSpouseContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
//                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Student Spouse Contact No Entity
//                                for (StudentContactNoEntity studentContact : existingContactList) {
//                                    studentContact.setDeletedBy(updatedEntity.getUpdatedBy());
//                                    studentContact.setDeletedAt(updatedEntity.getUpdatedAt());
//                                    studentContact.setReqDeletedIP(updatedEntity.getReqUpdatedIP());
//                                    studentContact.setReqDeletedPort(updatedEntity.getReqUpdatedPort());
//                                    studentContact.setReqDeletedBrowser(updatedEntity.getReqUpdatedBrowser());
//                                    studentContact.setReqDeletedOS(updatedEntity.getReqUpdatedOS());
//                                    studentContact.setReqDeletedDevice(updatedEntity.getReqUpdatedDevice());
//                                    studentContact.setReqDeletedReferer(updatedEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(studentSpouseContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<StudentContactNoEntity> studentSpouseContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedStdMetaUUID = null;
//
//                                for (JsonNode spouseContact : contactNode) {
//
//                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .studentMetaUUID(studentSpouseEntity.getUuid())
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
//                                    studentSpouseContactNoList.add(studentContactNoEntity);
//
//                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(studentContactNoEntity.getContactNo());
//
//                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Student Spouse Contact No List
//                                studentSpouseContactNoList = studentSpouseContactNoList.stream()
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
//                                List<StudentContactNoEntity> finalStudentSpouseContactNoList1 = studentSpouseContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Student Spouse and Contact Type
//                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseProfileRepository.save(previousProfileEntity)
//                                                                    .then(studentSpouseProfileRepository.save(updatedEntity))
//                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentSpouseContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
//                                                                                    StudentContactNoDto studentSpouseContactNoDto = StudentContactNoDto.builder()
//                                                                                            .contactNo(studentContact.getContactNo())
//                                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    studentContactNoDto.add(studentSpouseContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedEntity.getUpdatedBy().toString(),
//                                                                                                updatedEntity.getReqCompanyUUID().toString(), updatedEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(studentSpouseEntity, updatedEntity, studentContactNoDto)
//                                                                                                .flatMap(studentSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentSpouseFacadeDto))
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
//            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousStdContactList -> {
//
//                        for (StudentContactNoEntity studentContact : previousStdContactList) {
//                            studentContact.setDeletedBy(updatedEntity.getUpdatedBy());
//                            studentContact.setDeletedAt(updatedEntity.getUpdatedAt());
//                            studentContact.setReqDeletedIP(updatedEntity.getReqUpdatedIP());
//                            studentContact.setReqDeletedPort(updatedEntity.getReqUpdatedPort());
//                            studentContact.setReqDeletedBrowser(updatedEntity.getReqUpdatedBrowser());
//                            studentContact.setReqDeletedOS(updatedEntity.getReqUpdatedOS());
//                            studentContact.setReqDeletedDevice(updatedEntity.getReqUpdatedDevice());
//                            studentContact.setReqDeletedReferer(updatedEntity.getReqUpdatedReferer());
//                        }
//
//                        return studentContactNoRepository.saveAll(previousStdContactList)
//                                .collectList()
//                                .flatMap(studentContactList -> studentSpouseProfileRepository.save(previousProfileEntity)
//                                        .then(studentSpouseProfileRepository.save(updatedEntity))
//                                        .flatMap(studentSpouseProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedEntity.getUpdatedBy().toString(),
//                                                        updatedEntity.getReqCompanyUUID().toString(), updatedEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(studentSpouseEntity, updatedEntity, studentContactNoDto)
//                                                        .flatMap(studentSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentSpouseFacadeDto))
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
//    public Mono<ServerResponse> updateFacadeRecordWithSpouse(StudentSpouseEntity studentSpouseEntity, StudentSpouseEntity updatedStudentSpouseEntity, StudentSpouseProfileEntity previousProfileEntity, StudentSpouseProfileEntity updatedEntity, List<String> studentSpouseContactList, MultiValueMap<String, String> sendFormData) {
//
//        List<StudentContactNoDto> studentContactNoDto = new ArrayList<>();
//
//        studentSpouseContactList.removeIf(s -> s.equals(""));
//
//        if (!studentSpouseContactList.isEmpty()) {
//            return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("spouse")
//                    .flatMap(contactCategoryEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
//                            .collectList()
//                            .flatMap(existingContactList -> {
//
//                                //Removing Already existing Student Spouse Contact No Entity
//                                for (StudentContactNoEntity studentContact : existingContactList) {
//                                    studentContact.setDeletedBy(updatedStudentSpouseEntity.getUpdatedBy());
//                                    studentContact.setDeletedAt(updatedStudentSpouseEntity.getUpdatedAt());
//                                    studentContact.setReqDeletedIP(updatedStudentSpouseEntity.getReqUpdatedIP());
//                                    studentContact.setReqDeletedPort(updatedStudentSpouseEntity.getReqUpdatedPort());
//                                    studentContact.setReqDeletedBrowser(updatedStudentSpouseEntity.getReqUpdatedBrowser());
//                                    studentContact.setReqDeletedOS(updatedStudentSpouseEntity.getReqUpdatedOS());
//                                    studentContact.setReqDeletedDevice(updatedStudentSpouseEntity.getReqUpdatedDevice());
//                                    studentContact.setReqDeletedReferer(updatedStudentSpouseEntity.getReqUpdatedReferer());
//                                }
//
//                                //Creating an Object Node to Read Values from Front
//                                JsonNode contactNode = null;
//                                try {
//                                    contactNode = new ObjectMapper().readTree(studentSpouseContactList.toString());
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                //New Contact No list for adding values after building entity
//                                List<StudentContactNoEntity> studentSpouseContactNoList = new ArrayList<>();
//
//                                List<UUID> contactTypeUUIDList = new ArrayList<>();
//
//                                List<String> contactNoList = new ArrayList<>();
//
//                                UUID updatedStdMetaUUID = null;
//
//                                for (JsonNode spouseContact : contactNode) {
//
//                                    StudentContactNoEntity studentContactNoEntity = StudentContactNoEntity
//                                            .builder()
//                                            .uuid(UUID.randomUUID())
//                                            .contactTypeUUID(UUID.fromString(spouseContact.get("contactTypeUUID").toString().replaceAll("\"", "")))
//                                            .contactNo(spouseContact.get("contactNo").toString().replaceAll("\"", ""))
//                                            .contactCategoryUUID(contactCategoryEntity.getUuid())
//                                            .studentMetaUUID(studentSpouseEntity.getUuid())
//                                            .createdAt(previousProfileEntity.getCreatedAt())
//                                            .createdBy(previousProfileEntity.getCreatedBy())
//                                            .updatedBy(updatedStudentSpouseEntity.getUpdatedBy())
//                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
//                                            .reqCreatedIP(previousProfileEntity.getReqCreatedIP())
//                                            .reqCreatedPort(previousProfileEntity.getReqCreatedPort())
//                                            .reqCreatedBrowser(previousProfileEntity.getReqCreatedBrowser())
//                                            .reqCreatedOS(previousProfileEntity.getReqCreatedOS())
//                                            .reqCreatedDevice(previousProfileEntity.getReqCreatedDevice())
//                                            .reqCreatedReferer(previousProfileEntity.getReqCreatedReferer())
//                                            .reqCompanyUUID(updatedStudentSpouseEntity.getReqCompanyUUID())
//                                            .reqBranchUUID(updatedStudentSpouseEntity.getReqBranchUUID())
//                                            .reqUpdatedIP(updatedStudentSpouseEntity.getReqUpdatedIP())
//                                            .reqUpdatedPort(updatedStudentSpouseEntity.getReqUpdatedPort())
//                                            .reqUpdatedBrowser(updatedStudentSpouseEntity.getReqUpdatedBrowser())
//                                            .reqUpdatedOS(updatedStudentSpouseEntity.getReqUpdatedOS())
//                                            .reqUpdatedDevice(updatedStudentSpouseEntity.getReqUpdatedDevice())
//                                            .reqUpdatedReferer(updatedStudentSpouseEntity.getReqUpdatedReferer())
//                                            .build();
//
//                                    studentSpouseContactNoList.add(studentContactNoEntity);
//
//                                    contactTypeUUIDList.add(studentContactNoEntity.getContactTypeUUID());
//
//                                    contactNoList.add(studentContactNoEntity.getContactNo());
//
//                                    updatedStdMetaUUID = studentContactNoEntity.getStudentMetaUUID();
//                                }
//
//                                //Getting Distinct Values Fom the List of Student Spouse Contact No List
//                                studentSpouseContactNoList = studentSpouseContactNoList.stream()
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
//                                List<StudentContactNoEntity> finalStudentSpouseContactNoList1 = studentSpouseContactNoList;
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
//                                                    //check if Contact No Record Already Exists against Student Spouse and Contact Type
//                                                    return studentContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndStudentMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalStdMetaUUID)
//                                                            .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
//                                                            .switchIfEmpty(Mono.defer(() -> studentSpouseRepository.save(studentSpouseEntity)
//                                                                    .then(studentSpouseRepository.save(updatedStudentSpouseEntity))
//                                                                    .then(studentSpouseProfileRepository.save(previousProfileEntity))
//                                                                    .then(studentSpouseProfileRepository.save(updatedEntity))
//                                                                    .then(studentContactNoRepository.saveAll(existingContactList)
//                                                                            .collectList())
//                                                                    .flatMap(previousContactNoListEntity -> studentContactNoRepository.saveAll(finalStudentSpouseContactNoList1)
//                                                                            .collectList()
//                                                                            .flatMap(updatedContactNoEntity -> {
//
//                                                                                for (StudentContactNoEntity studentContact : updatedContactNoEntity) {
//                                                                                    StudentContactNoDto studentSpouseContactNoDto = StudentContactNoDto.builder()
//                                                                                            .contactNo(studentContact.getContactNo())
//                                                                                            .contactTypeUUID(studentContact.getContactTypeUUID())
//                                                                                            .build();
//
//                                                                                    studentContactNoDto.add(studentSpouseContactNoDto);
//                                                                                }
//
//                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedStudentSpouseEntity.getUpdatedBy().toString(),
//                                                                                                updatedStudentSpouseEntity.getReqCompanyUUID().toString(), updatedStudentSpouseEntity.getReqBranchUUID().toString())
//                                                                                        .flatMap(docUpdate -> updatedFacadeDto(updatedStudentSpouseEntity, updatedEntity, studentContactNoDto)
//                                                                                                .flatMap(studentSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentSpouseFacadeDto))
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
//            return studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
//                    .collectList()
//                    .flatMap(previousStdContactList -> {
//
//                        for (StudentContactNoEntity studentContact : previousStdContactList) {
//                            studentContact.setDeletedBy(updatedStudentSpouseEntity.getUpdatedBy());
//                            studentContact.setDeletedAt(updatedStudentSpouseEntity.getUpdatedAt());
//                            studentContact.setReqDeletedIP(updatedStudentSpouseEntity.getReqUpdatedIP());
//                            studentContact.setReqDeletedPort(updatedStudentSpouseEntity.getReqUpdatedPort());
//                            studentContact.setReqDeletedBrowser(updatedStudentSpouseEntity.getReqUpdatedBrowser());
//                            studentContact.setReqDeletedOS(updatedStudentSpouseEntity.getReqUpdatedOS());
//                            studentContact.setReqDeletedDevice(updatedStudentSpouseEntity.getReqUpdatedDevice());
//                            studentContact.setReqDeletedReferer(updatedStudentSpouseEntity.getReqUpdatedReferer());
//                        }
//
//                        return studentContactNoRepository.saveAll(previousStdContactList)
//                                .collectList()
//                                .flatMap(studentContactList -> studentSpouseRepository.save(studentSpouseEntity)
//                                        .then(studentSpouseRepository.save(updatedStudentSpouseEntity))
//                                        .then(studentSpouseProfileRepository.save(previousProfileEntity))
//                                        .then(studentSpouseProfileRepository.save(updatedEntity))
//                                        .flatMap(studentSpouseProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", updatedStudentSpouseEntity.getUpdatedBy().toString(),
//                                                        updatedStudentSpouseEntity.getReqCompanyUUID().toString(), updatedStudentSpouseEntity.getReqBranchUUID().toString())
//                                                .flatMap(docUpdateEntity -> updatedFacadeDto(updatedStudentSpouseEntity, updatedEntity, studentContactNoDto)
//                                                        .flatMap(studentSpouseFacadeDto -> responseSuccessMsg("Record Updated Successfully", studentSpouseFacadeDto))
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

    @AuthHasPermission(value = "academic_api_v1_facade_student-spouse-student-spouse-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID studentSpouseUUID = UUID.fromString((serverRequest.pathVariable("studentSpouseUUID")));
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

        return studentSpouseRepository.findByUuidAndDeletedAtIsNull(studentSpouseUUID)
                .flatMap(studentSpouseEntity -> studentSpouseProfileRepository.findFirstByStudentSpouseUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                        .flatMap(studentSpouseProfileEntity -> studentContactNoRepository.findAllByStudentMetaUUIDAndDeletedAtIsNull(studentSpouseEntity.getUuid())
                                .collectList()
                                .flatMap(studentContactNoEntity -> {

                                    List<StudentContactNoEntity> studentContactNoEntityList = new ArrayList<>();

                                    studentSpouseEntity.setDeletedBy(UUID.fromString(userId));
                                    studentSpouseEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentSpouseEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentSpouseEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentSpouseEntity.setReqDeletedIP(reqIp);
                                    studentSpouseEntity.setReqDeletedPort(reqPort);
                                    studentSpouseEntity.setReqDeletedBrowser(reqBrowser);
                                    studentSpouseEntity.setReqDeletedOS(reqOs);
                                    studentSpouseEntity.setReqDeletedDevice(reqDevice);
                                    studentSpouseEntity.setReqDeletedReferer(reqReferer);

                                    studentSpouseProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    studentSpouseProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    studentSpouseProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    studentSpouseProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    studentSpouseProfileEntity.setReqDeletedIP(reqIp);
                                    studentSpouseProfileEntity.setReqDeletedPort(reqPort);
                                    studentSpouseProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    studentSpouseProfileEntity.setReqDeletedOS(reqOs);
                                    studentSpouseProfileEntity.setReqDeletedDevice(reqDevice);
                                    studentSpouseProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        StudentContactNoDto studentSpouseContactNoDto = StudentContactNoDto.builder()
                                                .contactNo(studentContact.getContactNo())
                                                .contactTypeUUID(studentContact.getContactTypeUUID())
                                                .build();

                                        studentContactNoDto.add(studentSpouseContactNoDto);
                                    }

                                    return studentSpouseRepository.save(studentSpouseEntity)
                                            .then(studentSpouseProfileRepository.save(studentSpouseProfileEntity))
                                            .then(studentContactNoRepository.saveAll(studentContactNoEntityList)
                                                    .collectList())
                                            .flatMap(studentContactNoEntities -> facadeDto(studentSpouseEntity, studentSpouseProfileEntity, studentContactNoDto)
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
