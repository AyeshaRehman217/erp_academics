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
import tuf.webscaf.app.dbContext.master.dto.TeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.entity.TeacherContactNoEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherEntity;
import tuf.webscaf.app.dbContext.master.entity.TeacherProfileEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherContactNoEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveTeacherProfileEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherContactNoRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherProfileRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "teacherTeacherProfileContactNoFacade")
@Component
public class TeacherTeacherProfileContactNoFacadeHandler {
    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    SlaveTeacherRepository slaveTeacherRepository;

    @Autowired
    SlaveTeacherProfileRepository slaveTeacherProfileRepository;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @Autowired
    TeacherContactNoRepository teacherContactNoRepository;

    @Autowired
    SlaveTeacherContactNoRepository slaveTeacherContactNoRepository;

    @Autowired
    ContactCategoryRepository contactCategoryRepository;

    @Autowired
    ContactTypeRepository contactTypeRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    DepartmentRankRepository departmentRankRepository;

    @Autowired
    DepartmentRankCatalogueRepository departmentRankCatalogueRepository;

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
    ApiCallService apiCallService;

    @Value("${server.zone}")
    private String zone;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    @Value("${server.erp_drive_module.uri}")
    private String driveUri;

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-teacher-profile-contact-nos_index")
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
            Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> slaveTeacherTeacherProfileContactNoFacadeDtoFlux = slaveTeacherRepository
                    .indexTeacherAndTeacherProfileAndContactNoWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherTeacherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherProfileEntity -> slaveTeacherRepository
                            .countIndexTeacherTeacherProfileContactNoWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
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
            Flux<SlaveTeacherTeacherProfileContactNoFacadeDto> slaveTeacherTeacherProfileContactNoFacadeDtoFlux = slaveTeacherRepository
                    .indexTeacherAndTeacherProfileAndContactNoWithoutStatus(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherTeacherProfileContactNoFacadeDtoFlux
                    .collectList()
                    .flatMap(teacherProfileEntity -> slaveTeacherRepository
                            .countIndexTeacherTeacherProfileContactNoWithoutStatusFilter(searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord, searchKeyWord)
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

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-teacher-profile-contact-nos_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID teacherUUID = UUID.fromString((serverRequest.pathVariable("teacherUUID")));

        return slaveTeacherRepository.findByUuidAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherEntity -> slaveTeacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                        .flatMap(teacherProfileEntity -> slaveTeacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNo -> {
                                    List<SlaveTeacherContactNoFacadeDto> teacherContactNoDto = new ArrayList<>();

                                    for (SlaveTeacherContactNoEntity teacherContact : teacherContactNo) {
                                        SlaveTeacherContactNoFacadeDto teacherContactNoRecord = SlaveTeacherContactNoFacadeDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherContactNoRecord);
                                    }

                                    return showFacadeDto(teacherEntity, teacherProfileEntity, teacherContactNoDto)
                                            .flatMap(facadeDto -> responseSuccessMsg("Record Fetched Successfully", facadeDto));

                                }).switchIfEmpty(responseInfoMsg("Teacher Contact No record does not exist"))
                                .onErrorResume(err -> responseErrorMsg("Teacher Contact No record does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Profile record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Teacher Profile record does not exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Teacher Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Teacher Record does not exist.Please Contact Developer."));
    }

    public Mono<SlaveTeacherTeacherProfileContactNoFacadeDto> showFacadeDto(SlaveTeacherEntity slaveTeacherEntity, SlaveTeacherProfileEntity slaveTeacherProfileEntity, List<SlaveTeacherContactNoFacadeDto> slaveTeacherContactNoFacadeDto) {

        SlaveTeacherTeacherProfileContactNoFacadeDto facadeDto = SlaveTeacherTeacherProfileContactNoFacadeDto.builder()
                .id(slaveTeacherEntity.getId())
                .uuid(slaveTeacherEntity.getUuid())
                .version(slaveTeacherEntity.getVersion())
                .status(slaveTeacherEntity.getStatus())
                .employeeCode(slaveTeacherEntity.getEmployeeCode())
                .campusUUID(slaveTeacherEntity.getCampusUUID())
                .deptRankUUID(slaveTeacherEntity.getDeptRankUUID())
                .reportingTo(slaveTeacherEntity.getReportingTo())
                .teacherUUID(slaveTeacherProfileEntity.getTeacherUUID())
                .image(slaveTeacherProfileEntity.getImage())
                .firstName(slaveTeacherProfileEntity.getFirstName())
                .lastName(slaveTeacherProfileEntity.getLastName())
                .nic(slaveTeacherProfileEntity.getNic())
                .email(slaveTeacherProfileEntity.getEmail())
                .telephoneNo(slaveTeacherProfileEntity.getTelephoneNo())
                .nic(slaveTeacherProfileEntity.getNic())
                .birthDate(slaveTeacherProfileEntity.getBirthDate())
                .cityUUID(slaveTeacherProfileEntity.getCityUUID())
                .stateUUID(slaveTeacherProfileEntity.getStateUUID())
                .countryUUID(slaveTeacherProfileEntity.getCountryUUID())
                .religionUUID(slaveTeacherProfileEntity.getReligionUUID())
                .sectUUID(slaveTeacherProfileEntity.getSectUUID())
                .casteUUID(slaveTeacherProfileEntity.getCasteUUID())
                .genderUUID(slaveTeacherProfileEntity.getGenderUUID())
                .maritalStatusUUID(slaveTeacherProfileEntity.getMaritalStatusUUID())
                .teacherContactNoDto(slaveTeacherContactNoFacadeDto)
                .createdAt(slaveTeacherEntity.getCreatedAt())
                .createdBy(slaveTeacherEntity.getCreatedBy())
                .reqCompanyUUID(slaveTeacherEntity.getReqCompanyUUID())
                .reqBranchUUID(slaveTeacherEntity.getReqBranchUUID())
                .reqCreatedBrowser(slaveTeacherEntity.getReqCreatedBrowser())
                .reqCreatedIP(slaveTeacherEntity.getReqCreatedIP())
                .reqCreatedPort(slaveTeacherEntity.getReqCreatedPort())
                .reqCreatedOS(slaveTeacherEntity.getReqCreatedOS())
                .reqCreatedDevice(slaveTeacherEntity.getReqCreatedDevice())
                .reqCreatedReferer(slaveTeacherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(slaveTeacherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(slaveTeacherEntity.getReqUpdatedIP())
                .reqUpdatedPort(slaveTeacherEntity.getReqUpdatedPort())
                .reqUpdatedOS(slaveTeacherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(slaveTeacherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(slaveTeacherEntity.getReqUpdatedReferer())
                .editable(slaveTeacherEntity.getEditable())
                .deletable(slaveTeacherEntity.getDeletable())
                .archived(slaveTeacherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherTeacherProfileContactNoFacadeDto> facadeDto(TeacherEntity teacherEntity, TeacherProfileEntity teacherProfileEntity, List<TeacherContactNoDto> teacherContactNoDto) {

        TeacherTeacherProfileContactNoFacadeDto facadeDto = TeacherTeacherProfileContactNoFacadeDto.builder()
                .id(teacherEntity.getId())
                .uuid(teacherEntity.getUuid())
                .version(teacherEntity.getVersion())
                .status(teacherEntity.getStatus())
                .employeeCode(teacherEntity.getEmployeeCode())
                .campusUUID(teacherEntity.getCampusUUID())
                .deptRankUUID(teacherEntity.getDeptRankUUID())
                .reportingTo(teacherEntity.getReportingTo())
                .teacherUUID(teacherProfileEntity.getTeacherUUID())
                .image(teacherProfileEntity.getImage())
                .firstName(teacherProfileEntity.getFirstName())
                .lastName(teacherProfileEntity.getLastName())
                .nic(teacherProfileEntity.getNic())
                .email(teacherProfileEntity.getEmail())
                .telephoneNo(teacherProfileEntity.getTelephoneNo())
                .nic(teacherProfileEntity.getNic())
                .birthDate(teacherProfileEntity.getBirthDate())
                .cityUUID(teacherProfileEntity.getCityUUID())
                .stateUUID(teacherProfileEntity.getStateUUID())
                .countryUUID(teacherProfileEntity.getCountryUUID())
                .religionUUID(teacherProfileEntity.getReligionUUID())
                .sectUUID(teacherProfileEntity.getSectUUID())
                .casteUUID(teacherProfileEntity.getCasteUUID())
                .genderUUID(teacherProfileEntity.getGenderUUID())
                .maritalStatusUUID(teacherProfileEntity.getMaritalStatusUUID())
                .teacherContactNoDto(teacherContactNoDto)
                .createdAt(teacherEntity.getCreatedAt())
                .createdBy(teacherEntity.getCreatedBy())
                .reqCompanyUUID(teacherEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherEntity.getReqCreatedIP())
                .reqCreatedPort(teacherEntity.getReqCreatedPort())
                .reqCreatedOS(teacherEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherEntity.getReqUpdatedReferer())
                .editable(teacherEntity.getEditable())
                .deletable(teacherEntity.getDeletable())
                .archived(teacherEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    public Mono<TeacherProfileContactNoFacadeDto> updatedFacadeDto(TeacherEntity teacherEntity, TeacherProfileEntity teacherProfileEntity, List<TeacherContactNoDto> teacherContactNoDto) {

        TeacherProfileContactNoFacadeDto facadeDto = TeacherProfileContactNoFacadeDto.builder()
                .id(teacherEntity.getId())
                .uuid(teacherEntity.getUuid())
                .version(teacherEntity.getVersion())
                .status(teacherEntity.getStatus())
                .teacherUUID(teacherProfileEntity.getTeacherUUID())
                .image(teacherProfileEntity.getImage())
                .firstName(teacherProfileEntity.getFirstName())
                .lastName(teacherProfileEntity.getLastName())
                .nic(teacherProfileEntity.getNic())
                .email(teacherProfileEntity.getEmail())
                .telephoneNo(teacherProfileEntity.getTelephoneNo())
                .nic(teacherProfileEntity.getNic())
                .birthDate(teacherProfileEntity.getBirthDate())
                .cityUUID(teacherProfileEntity.getCityUUID())
                .stateUUID(teacherProfileEntity.getStateUUID())
                .countryUUID(teacherProfileEntity.getCountryUUID())
                .religionUUID(teacherProfileEntity.getReligionUUID())
                .sectUUID(teacherProfileEntity.getSectUUID())
                .casteUUID(teacherProfileEntity.getCasteUUID())
                .genderUUID(teacherProfileEntity.getGenderUUID())
                .maritalStatusUUID(teacherProfileEntity.getMaritalStatusUUID())
                .teacherContactNoDto(teacherContactNoDto)
                .updatedAt(teacherEntity.getUpdatedAt())
                .updatedBy(teacherEntity.getUpdatedBy())
                .reqCompanyUUID(teacherProfileEntity.getReqCompanyUUID())
                .reqBranchUUID(teacherProfileEntity.getReqBranchUUID())
                .reqCreatedBrowser(teacherProfileEntity.getReqCreatedBrowser())
                .reqCreatedIP(teacherProfileEntity.getReqCreatedIP())
                .reqCreatedPort(teacherProfileEntity.getReqCreatedPort())
                .reqCreatedOS(teacherProfileEntity.getReqCreatedOS())
                .reqCreatedDevice(teacherProfileEntity.getReqCreatedDevice())
                .reqCreatedReferer(teacherProfileEntity.getReqCreatedReferer())
                .reqUpdatedBrowser(teacherProfileEntity.getReqUpdatedBrowser())
                .reqUpdatedIP(teacherProfileEntity.getReqUpdatedIP())
                .reqUpdatedPort(teacherProfileEntity.getReqUpdatedPort())
                .reqUpdatedOS(teacherProfileEntity.getReqUpdatedOS())
                .reqUpdatedDevice(teacherProfileEntity.getReqUpdatedDevice())
                .reqUpdatedReferer(teacherProfileEntity.getReqUpdatedReferer())
                .editable(teacherProfileEntity.getEditable())
                .deletable(teacherProfileEntity.getDeletable())
                .archived(teacherProfileEntity.getArchived())
                .build();

        return Mono.just(facadeDto);
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-teacher-profile-contact-nos_store")
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

                    UUID reportingTo = null;
                    UUID deptRankUUID = null;

                    if ((value.containsKey("reportingTo") && (!Objects.equals(value.getFirst("reportingTo"), "")))) {
                        reportingTo = UUID.fromString(value.getFirst("reportingTo").trim());
                    }

                    if ((value.containsKey("deptRankUUID") && (!Objects.equals(value.getFirst("deptRankUUID"), "")))) {
                        deptRankUUID = UUID.fromString(value.getFirst("deptRankUUID").trim());
                    }

                    TeacherEntity teacherEntity = TeacherEntity.builder()
                            .uuid(UUID.randomUUID())
                            .employeeCode(value.getFirst("employeeCode"))
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                            .reportingTo(reportingTo)
                            .deptRankUUID(deptRankUUID)
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

                    // Checks if employee code already exists
                    return teacherRepository.findFirstByEmployeeCodeAndDeletedAtIsNull(teacherEntity.getEmployeeCode())
                            .flatMap(checkMsg -> responseInfoMsg("Employee Code Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> campusRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getCampusUUID())
                                    .flatMap(campusEntity -> {

                                        MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                        //Building Teacher Profile Record
                                        TeacherProfileEntity teacherProfileEntity = TeacherProfileEntity
                                                .builder()
                                                .uuid(UUID.randomUUID())
                                                .teacherUUID(teacherEntity.getUuid())
                                                .image(UUID.fromString(value.getFirst("image")))
                                                .firstName(value.getFirst("firstName").trim())
                                                .lastName(value.getFirst("lastName").trim())
                                                .email(value.getFirst("email").trim())
                                                .nic(value.getFirst("nic").trim())
                                                .telephoneNo(value.getFirst("telephoneNo").trim())
                                                .birthDate(LocalDateTime.parse(value.getFirst("birthDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                                .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                                .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                                .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                                .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                                                .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                                                .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                                                .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                                                .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
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

                                        sendFormData.add("docId", String.valueOf(teacherProfileEntity.getImage()));

                                        // if teacher and department rank uuids are given
                                        if (teacherEntity.getReportingTo() != null && teacherEntity.getDeptRankUUID() != null) {

                                            return teacherRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getReportingTo())
                                                    .flatMap(reportingToEntity -> departmentRankRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getDeptRankUUID())
                                                            .flatMap(departmentRank -> departmentRankCatalogueRepository.findByUuidAndDeletedAtIsNull(departmentRank.getDeptRankCatalogueUUID())
                                                                    .flatMap(departmentRankCatalogueEntity -> teacherRepository.findAllByDeptRankUUIDAndDeletedAtIsNull(teacherEntity.getDeptRankUUID())
                                                                            .collectList()
                                                                            .flatMap(teachersWithGivenDeptRank -> {

                                                                                // if many is false in dept rank catalogue
                                                                                if (!departmentRank.getMany()) {

                                                                                    // if some teacher is not already ranked, allow storing record
                                                                                    if (teachersWithGivenDeptRank.isEmpty()) {
                                                                                        return teacherRepository.save(teacherEntity)
                                                                                                .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                                                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                                                    }

                                                                                    // if some teacher is already ranked
                                                                                    else {
                                                                                        return responseInfoMsg("Teacher Record Already Exists With " + departmentRankCatalogueEntity.getName() + " Department Rank");
                                                                                    }
                                                                                }


                                                                                // if many is true in dept rank catalogue
                                                                                else {

                                                                                    // if max count is specified in dept rank catalogue
                                                                                    if (departmentRank.getMax() != null) {
                                                                                        // if already ranked teachers count is less than max, allow storing record
                                                                                        if (teachersWithGivenDeptRank.size() < departmentRank.getMax()) {
                                                                                            return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                                                                        }
                                                                                        // else record can't be stored
                                                                                        else {
                                                                                            return responseInfoMsg("Dept Rank is already ranked up to the maximum allowed teachers");
                                                                                        }
                                                                                    }
                                                                                    // if max count is not specified in dept rank catalogue
                                                                                    else {
                                                                                        return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                                                                    }
                                                                                }
                                                                            }))
                                                            ).switchIfEmpty(responseInfoMsg("Department Rank Does not exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Department Rank Does not exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Reporting To Teacher Record Does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Reporting To Teacher Record Does not exist. Please contact developer."));
                                        }


                                        // if teacher uuid is given
                                        else if (teacherEntity.getReportingTo() != null) {
                                            return teacherRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getReportingTo())
                                                    .flatMap(teacher -> {
                                                        teacherEntity.setReportingTo(teacher.getUuid());
                                                        return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                                    }).switchIfEmpty(responseInfoMsg("Reporting To Teacher Record Does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Reporting To Teacher Record Does not exist. Please contact developer."));
                                        }


                                        // if department rank uuid is given
                                        else if (teacherEntity.getDeptRankUUID() != null) {
                                            return departmentRankRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getDeptRankUUID())
                                                    .flatMap(departmentRank -> departmentRankCatalogueRepository.findByUuidAndDeletedAtIsNull(departmentRank.getDeptRankCatalogueUUID())
                                                            .flatMap(departmentRankCatalogueEntity -> teacherRepository.findAllByDeptRankUUIDAndDeletedAtIsNull(teacherEntity.getDeptRankUUID())
                                                                    .collectList()
                                                                    .flatMap(teachersWithGivenDeptRank -> {

                                                                        // if many is false in dept rank catalogue
                                                                        if (!departmentRank.getMany()) {

                                                                            // if some teacher is not already ranked, allow storing record
                                                                            if (teachersWithGivenDeptRank.isEmpty()) {
                                                                                return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                                                            }

                                                                            // if some teacher is already ranked
                                                                            else {
                                                                                return responseInfoMsg("Teacher Record Already Exists With " + departmentRankCatalogueEntity.getName() + " Department Rank");
                                                                            }
                                                                        }


                                                                        // if many is true in dept rank catalogue
                                                                        else {

                                                                            // if max count is specified in dept rank catalogue
                                                                            if (departmentRank.getMax() != null) {
                                                                                // if already ranked teachers count is less than max, allow storing record
                                                                                if (teachersWithGivenDeptRank.size() < departmentRank.getMax()) {
                                                                                    return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                                                                }
                                                                                // else record can't be stored
                                                                                else {
                                                                                    return responseInfoMsg("Dept Rank is already ranked up to the maximum allowed teachers");
                                                                                }
                                                                            }
                                                                            // if max count is not specified in dept rank catalogue
                                                                            else {
                                                                                return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                                                            }
                                                                        }
                                                                    }))
                                                    ).switchIfEmpty(responseInfoMsg("Department Rank Does not exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Department Rank Does not exist. Please contact developer."));
                                        }


                                        // store teacher record
                                        else {
                                            return storeFacadeRecord(teacherEntity, teacherProfileEntity, value.get("teacherContactNoDto"), sendFormData);
                                        }


                                    }).switchIfEmpty(responseInfoMsg("Campus Record Does not Exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Campus Record Does not Exist.Please Contact Developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    public Mono<ServerResponse> storeFacadeRecord(TeacherEntity teacherEntity, TeacherProfileEntity teacherProfileEntity, List<String> teacherContactList, MultiValueMap<String, String> sendFormData) {

        //check if religion uuid exists
        return religionRepository.findByUuidAndDeletedAtIsNull(teacherProfileEntity.getReligionUUID())
                //checks if sect uuid exists
                .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(teacherProfileEntity.getSectUUID())
                                //checks if caste uuid exists
                                .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(teacherProfileEntity.getCasteUUID())
                                                //checks if gender uuid exists
                                                .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(teacherProfileEntity.getGenderUUID())
                                                                //checks if marital status uuid exists
                                                                .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(teacherProfileEntity.getMaritalStatusUUID())
                                                                                //checks if emergency contactNo uuid exists
                                                                                .flatMap(maritalStatusEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", teacherProfileEntity.getCityUUID())
                                                                                                .flatMap(cityJson -> apiCallService.getUUID(cityJson)
                                                                                                        //check if State Record Exists or not
                                                                                                        .flatMap(cityUuid -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", teacherProfileEntity.getStateUUID())
                                                                                                                        .flatMap(stateJson -> apiCallService.getUUID(stateJson)
                                                                                                                                //check if Country Record Exists or not
                                                                                                                                .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", teacherProfileEntity.getCountryUUID())
                                                                                                                                                .flatMap(countryJson -> apiCallService.getUUID(countryJson)
                                                                                                                                                        //check if Document Record Exists or not
                                                                                                                                                        .flatMap(countryUUID -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", teacherProfileEntity.getImage())
                                                                                                                                                                        .flatMap(docJson -> apiCallService.checkDocId(docJson)
                                                                                                                                                                                //check if NIC Is Unique Against Teacher
                                                                                                                                                                                .flatMap(documentEntity -> teacherProfileRepository.findFirstByNicAndDeletedAtIsNull(teacherProfileEntity.getNic())
                                                                                                                                                                                                .flatMap(nicAlreadyExists -> responseInfoMsg("Nic Already Exists"))
                                                                                                                                                                                                //check if Profile Already Exists Against Teacher
                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherProfileEntity.getTeacherUUID())
                                                                                                                                                                                                        .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Profile already exist"))))
                                                                                                                                                                                                //check if Contact Category is
                                                                                                                                                                                                .switchIfEmpty(Mono.defer(() -> contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("teacher")
                                                                                                                                                                                                                .flatMap(contactCategoryEntity -> {

                                                                                                                                                                                                                    //Creating an empty list to add teacher Contact No Records
                                                                                                                                                                                                                    List<TeacherContactNoEntity> teacherContactNoList = new ArrayList<>();

                                                                                                                                                                                                                    // Creating an empty list to add contact Type UUID's
                                                                                                                                                                                                                    List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                                                                    // Creating an empty list to add contact No's
                                                                                                                                                                                                                    List<String> contactNoList = new ArrayList<>();


                                                                                                                                                                                                                    JsonNode contactNode = null;
                                                                                                                                                                                                                    ObjectMapper objectMapper = new ObjectMapper();
                                                                                                                                                                                                                    try {
                                                                                                                                                                                                                        contactNode = objectMapper.readTree(teacherContactList.toString());
                                                                                                                                                                                                                    } catch (JsonProcessingException e) {
                                                                                                                                                                                                                        e.printStackTrace();
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                    assert contactNode != null;


                                                                                                                                                                                                                    UUID teacherMetaUUID = null;
                                                                                                                                                                                                                    UUID contactCategoryUUID = null;

                                                                                                                                                                                                                    //iterating over the json node from front and setting contact No's
                                                                                                                                                                                                                    for (JsonNode Contact : contactNode) {

                                                                                                                                                                                                                        TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                                                                                .builder()
                                                                                                                                                                                                                                .contactTypeUUID(UUID.fromString(Contact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                                                                .contactNo(Contact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                                                                .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                                                                .teacherMetaUUID(teacherEntity.getUuid())
                                                                                                                                                                                                                                .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                                                                                                                                                                                .createdBy(teacherEntity.getCreatedBy())
                                                                                                                                                                                                                                .reqCompanyUUID(teacherEntity.getReqCompanyUUID())
                                                                                                                                                                                                                                .reqBranchUUID(teacherEntity.getReqBranchUUID())
                                                                                                                                                                                                                                .reqCreatedIP(teacherEntity.getReqCreatedIP())
                                                                                                                                                                                                                                .reqCreatedPort(teacherEntity.getReqCreatedPort())
                                                                                                                                                                                                                                .reqCreatedBrowser(teacherEntity.getReqCreatedBrowser())
                                                                                                                                                                                                                                .reqCreatedOS(teacherEntity.getReqCreatedOS())
                                                                                                                                                                                                                                .reqCreatedDevice(teacherEntity.getReqCreatedDevice())
                                                                                                                                                                                                                                .reqCreatedReferer(teacherEntity.getReqCreatedReferer())
                                                                                                                                                                                                                                .build();

                                                                                                                                                                                                                        teacherContactNoList.add(teacherContactNoEntity);

                                                                                                                                                                                                                        contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());
                                                                                                                                                                                                                        contactNoList.add(teacherContactNoEntity.getContactNo());
                                                                                                                                                                                                                        teacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                                                                                        contactCategoryUUID = teacherContactNoEntity.getContactCategoryUUID();
                                                                                                                                                                                                                    }

                                                                                                                                                                                                                    //Getting Distinct Values Fom the List of Teacher Contact No List
                                                                                                                                                                                                                    teacherContactNoList = teacherContactNoList.stream()
                                                                                                                                                                                                                            .distinct()
                                                                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                                                                    //Getting Distinct Values Fom the List of Contact Type UUID
                                                                                                                                                                                                                    contactTypeUUIDList = contactTypeUUIDList.stream()
                                                                                                                                                                                                                            .distinct()
                                                                                                                                                                                                                            .collect(Collectors.toList());

                                                                                                                                                                                                                    // Creating an empty list to add contact No's and returning dto with response
                                                                                                                                                                                                                    List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                                                                                                    if (!teacherContactNoList.isEmpty()) {

                                                                                                                                                                                                                        UUID finalTeacherMetaUUID = teacherMetaUUID;

                                                                                                                                                                                                                        UUID finalContactCategoryUUID = contactCategoryUUID;

                                                                                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherContactNoList = teacherContactNoList;

                                                                                                                                                                                                                        List<UUID> finalContactTypeUUIDList = contactTypeUUIDList;

                                                                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                                                                .collectList()
                                                                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher and Contact Type
                                                                                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndTeacherMetaUUIDAndDeletedAtIsNull(contactNoList, finalContactTypeUUIDList, finalContactCategoryUUID, finalTeacherMetaUUID)
                                                                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherRepository.save(teacherEntity)
                                                                                                                                                                                                                                                            .then(teacherProfileRepository.save(teacherProfileEntity))
                                                                                                                                                                                                                                                            .then(teacherContactNoRepository.saveAll(finalTeacherContactNoList)
                                                                                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                                                                                            .flatMap(mthContactNo -> {

                                                                                                                                                                                                                                                                for (TeacherContactNoEntity teacherContact : mthContactNo) {
                                                                                                                                                                                                                                                                    TeacherContactNoDto teacherContactNoRecord = TeacherContactNoDto.builder()
                                                                                                                                                                                                                                                                            .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                                                                            .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                                                                            .build();

                                                                                                                                                                                                                                                                    teacherContactNoDto.add(teacherContactNoRecord);
                                                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                                                return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherEntity.getCreatedBy().toString(), teacherEntity.getReqCompanyUUID().toString(), teacherEntity.getReqBranchUUID().toString())
                                                                                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherEntity, teacherProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                                                                                                .flatMap(teacherFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherFacadeDto))
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
                                                                                                                                                                                                                        //if Contact No List is empty then store teacher and Teacher Profile
                                                                                                                                                                                                                        return teacherRepository.save(teacherEntity)
                                                                                                                                                                                                                                //Save Teacher Profile Entity
                                                                                                                                                                                                                                .then(teacherProfileRepository.save(teacherProfileEntity))
                                                                                                                                                                                                                                //update Document Status After Storing record
                                                                                                                                                                                                                                .flatMap(saveEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", teacherEntity.getCreatedBy().toString(), teacherEntity.getReqCompanyUUID().toString(), teacherEntity.getReqBranchUUID().toString())
                                                                                                                                                                                                                                        .flatMap(docUpdate -> facadeDto(teacherEntity, teacherProfileEntity, teacherContactNoDto)
                                                                                                                                                                                                                                                .flatMap(teacherFacadeDto -> responseSuccessMsg("Record Stored Successfully", teacherFacadeDto))
                                                                                                                                                                                                                                                .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."))
                                                                                                                                                                                                                                        ).switchIfEmpty(responseInfoMsg("Unable to Upload Document.there is something wrong please try again."))
                                                                                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Document.Please Contact Developer."))
                                                                                                                                                                                                                                ).switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                                                                                                                                                                    }
//
                                                                                                                                                                                                                })
                                                                                                                                                                                                ))
                                                                                                                                                                                )).switchIfEmpty(responseInfoMsg("Unable to Upload Image."))
                                                                                                                                                                        .onErrorResume(ex -> responseErrorMsg("Unable to Upload Image.Please Contact Developer."))
                                                                                                                                                        )).switchIfEmpty(responseInfoMsg("Country Record Does not exist."))
                                                                                                                                                .onErrorResume(ex -> responseErrorMsg("Country Record Does not Exist.Please Contact Developer."))
                                                                                                                                )).switchIfEmpty(responseInfoMsg("State Record Does not Exist."))
                                                                                                                        .onErrorResume(ex -> responseErrorMsg("State Record Does not Exist.Please Contact Developer."))
                                                                                                        )).switchIfEmpty(responseInfoMsg("City Record Does not Exist."))
                                                                                                .onErrorResume(ex -> responseErrorMsg("City Record Does not Exist.Please Contact Developer."))
                                                                                ).switchIfEmpty(responseInfoMsg("Marital Status Record Does not Exist"))
                                                                                .onErrorResume(ex -> responseErrorMsg("Marital Status Record Does not Exist. Please contact developer."))
                                                                ).switchIfEmpty(responseInfoMsg("Gender Record Does not Exist"))
                                                                .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist. Please contact developer."))
                                                ).switchIfEmpty(responseInfoMsg("Caste Record Does not Exist"))
                                                .onErrorResume(ex -> responseErrorMsg("Caste Record Does not Exist. Please contact developer."))
                                ).switchIfEmpty(responseInfoMsg("Sect Record Does not Exist"))
                                .onErrorResume(ex -> responseErrorMsg("Sect Record Does not Exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Religion Record Does not Exist"))
                .onErrorResume(ex -> responseErrorMsg("Religion Record Does not Exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_facade_teacher-teacher-profile-contact-nos_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID teacherUUID = UUID.fromString((serverRequest.pathVariable("teacherUUID")));
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
                .flatMap(value -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherUUID)
                        .flatMap(teacherEntity -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherUUID)
                                .flatMap(previousProfileEntity -> {

                                    MultiValueMap<String, String> sendFormData = new LinkedMultiValueMap<>();

                                    TeacherProfileEntity updatedEntity = TeacherProfileEntity.builder()
                                            .uuid(previousProfileEntity.getUuid())
                                            .teacherUUID(previousProfileEntity.getTeacherUUID())
                                            .image(UUID.fromString(value.getFirst("image")))
                                            .firstName(value.getFirst("firstName").trim())
                                            .lastName(value.getFirst("lastName").trim())
                                            .email(value.getFirst("email").trim())
                                            .nic(value.getFirst("nic").trim())
                                            .telephoneNo(value.getFirst("telephoneNo").trim())
                                            .birthDate(LocalDateTime.parse(value.getFirst("birthDate"), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                                            .cityUUID(UUID.fromString(value.getFirst("cityUUID").trim()))
                                            .stateUUID(UUID.fromString(value.getFirst("stateUUID").trim()))
                                            .countryUUID(UUID.fromString(value.getFirst("countryUUID").trim()))
                                            .religionUUID(UUID.fromString(value.getFirst("religionUUID").trim()))
                                            .sectUUID(UUID.fromString(value.getFirst("sectUUID").trim()))
                                            .casteUUID(UUID.fromString(value.getFirst("casteUUID").trim()))
                                            .genderUUID(UUID.fromString(value.getFirst("genderUUID").trim()))
                                            .maritalStatusUUID(UUID.fromString(value.getFirst("maritalStatusUUID").trim()))
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
                                    return teacherProfileRepository.findFirstByNicAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getNic(), updatedEntity.getUuid())
                                            .flatMap(nicAlreadyExists -> responseInfoMsg("Nic Already Exists"))
                                            //check teacher profile is unique
                                            .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getTeacherUUID(), updatedEntity.getUuid())
                                                    .flatMap(teacherProfileAlreadyExists -> responseInfoMsg("Teacher Profile already exist"))))
                                            //checks if religion uuid exists
                                            .switchIfEmpty(Mono.defer(() -> religionRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getReligionUUID())
                                                    //checks if sect uuid exists
                                                    .flatMap(religionEntity -> sectRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getSectUUID())
                                                            //checks if caste uuid exists
                                                            .flatMap(sectEntity -> casteRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getCasteUUID())
                                                                    //checks if gender uuid exists
                                                                    .flatMap(casteEntity -> genderRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getGenderUUID())
                                                                            //checks if marital status uuid exists
                                                                            .flatMap(genderEntity -> maritalStatusRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getMaritalStatusUUID())
                                                                                    //checks if doc id exists
                                                                                    .flatMap(maritalStatusEntity -> apiCallService.getDataWithUUID(driveUri + "api/v1/documents/show/", updatedEntity.getImage())
                                                                                            .flatMap(documentEntity -> apiCallService.checkDocId(documentEntity)
                                                                                                    //checks city uuid exists
                                                                                                    .flatMap(teacherDocumentEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/cities/show/", updatedEntity.getCityUUID())
                                                                                                            .flatMap(cityEntity -> apiCallService.getUUID(cityEntity)
                                                                                                                    //checks state uuid exists
                                                                                                                    .flatMap(cityJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/states/show/", updatedEntity.getStateUUID())
                                                                                                                            .flatMap(stateEntity -> apiCallService.getUUID(cityEntity)
                                                                                                                                    //checks countries uuid exists
                                                                                                                                    .flatMap(stateJsonNode -> apiCallService.getDataWithUUID(configUri + "api/v1/countries/show/", updatedEntity.getCountryUUID())
                                                                                                                                            .flatMap(countryEntity -> apiCallService.getUUID(countryEntity)
                                                                                                                                                    .flatMap(countryJsonNode -> {

                                                                                                                                                                //getting List of Contact No. From Front
                                                                                                                                                                List<String> teacherContactList = value.get("teacherContactNoDto");
                                                                                                                                                                List<TeacherContactNoDto> teacherContactNoDto = new ArrayList<>();

                                                                                                                                                                teacherContactList.removeIf(s -> s.equals(""));

                                                                                                                                                                if (!teacherContactList.isEmpty()) {
                                                                                                                                                                    return contactCategoryRepository.findFirstBySlugAndDeletedAtIsNull("teacher")
                                                                                                                                                                            .flatMap(contactCategoryEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherUUID)
                                                                                                                                                                                    .collectList()
                                                                                                                                                                                    .flatMap(existingContactList -> {

                                                                                                                                                                                        //Removing Already existing Teacher Contact No Entity
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
                                                                                                                                                                                            contactNode = new ObjectMapper().readTree(teacherContactList.toString());
                                                                                                                                                                                        } catch (JsonProcessingException e) {
                                                                                                                                                                                            e.printStackTrace();
                                                                                                                                                                                        }

                                                                                                                                                                                        //New Contact No list for adding values after building entity
                                                                                                                                                                                        List<TeacherContactNoEntity> teacherContactNoList = new ArrayList<>();

                                                                                                                                                                                        List<UUID> contactTypeUUIDList = new ArrayList<>();

                                                                                                                                                                                        List<String> contactNoList = new ArrayList<>();

                                                                                                                                                                                        UUID updatedTeacherMetaUUID = null;

                                                                                                                                                                                        for (JsonNode Contact : contactNode) {

                                                                                                                                                                                            TeacherContactNoEntity teacherContactNoEntity = TeacherContactNoEntity
                                                                                                                                                                                                    .builder()
                                                                                                                                                                                                    .uuid(UUID.randomUUID())
                                                                                                                                                                                                    .contactTypeUUID(UUID.fromString(Contact.get("contactTypeUUID").toString().replaceAll("\"", "")))
                                                                                                                                                                                                    .contactNo(Contact.get("contactNo").toString().replaceAll("\"", ""))
                                                                                                                                                                                                    .contactCategoryUUID(contactCategoryEntity.getUuid())
                                                                                                                                                                                                    .teacherMetaUUID(teacherUUID)
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

                                                                                                                                                                                            teacherContactNoList.add(teacherContactNoEntity);

                                                                                                                                                                                            contactTypeUUIDList.add(teacherContactNoEntity.getContactTypeUUID());

                                                                                                                                                                                            contactNoList.add(teacherContactNoEntity.getContactNo());

                                                                                                                                                                                            updatedTeacherMetaUUID = teacherContactNoEntity.getTeacherMetaUUID();
                                                                                                                                                                                        }

                                                                                                                                                                                        //Getting Distinct Values Fom the List of Teacher Contact No List
                                                                                                                                                                                        teacherContactNoList = teacherContactNoList.stream()
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

                                                                                                                                                                                        List<TeacherContactNoEntity> finalTeacherContactNoList1 = teacherContactNoList;

                                                                                                                                                                                        List<String> finalContactNoList = contactNoList;

                                                                                                                                                                                        return contactTypeRepository.findAllByUuidInAndDeletedAtIsNull(contactTypeUUIDList)
                                                                                                                                                                                                .collectList()
                                                                                                                                                                                                .flatMap(contactTypeEntityList -> {

                                                                                                                                                                                                    if (!contactTypeEntityList.isEmpty()) {

                                                                                                                                                                                                        if (contactTypeEntityList.size() != finalContactTypeUUIDList.size()) {
                                                                                                                                                                                                            return responseInfoMsg("Contact Type Does not Exist");
                                                                                                                                                                                                        } else {

                                                                                                                                                                                                            //check if Contact No Record Already Exists against Teacher and Contact Type
                                                                                                                                                                                                            return teacherContactNoRepository.findFirstByContactNoInAndContactTypeUUIDInAndContactCategoryUUIDAndDeletedAtIsNullAndTeacherMetaUUIDIsNot(finalContactNoList, finalContactTypeUUIDList, contactCategoryEntity.getUuid(), finalTeacherMetaUUID)
                                                                                                                                                                                                                    .flatMap(checkMsg -> responseInfoMsg("Contact No Already Exists Against the Contact Type"))
                                                                                                                                                                                                                    .switchIfEmpty(Mono.defer(() -> teacherProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                                                            .then(teacherProfileRepository.save(updatedEntity))
                                                                                                                                                                                                                            .then(teacherContactNoRepository.saveAll(existingContactList)
                                                                                                                                                                                                                                    .collectList())
                                                                                                                                                                                                                            .flatMap(previousContactNoListEntity -> teacherContactNoRepository.saveAll(finalTeacherContactNoList1)
                                                                                                                                                                                                                                    .collectList()
                                                                                                                                                                                                                                    .flatMap(updatedContactNoEntity -> {

                                                                                                                                                                                                                                        for (TeacherContactNoEntity teacherContact : updatedContactNoEntity) {
                                                                                                                                                                                                                                            TeacherContactNoDto teacherContactNoRecord = TeacherContactNoDto.builder()
                                                                                                                                                                                                                                                    .contactNo(teacherContact.getContactNo())
                                                                                                                                                                                                                                                    .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                                                                                                                                                                                                                    .build();

                                                                                                                                                                                                                                            teacherContactNoDto.add(teacherContactNoRecord);
                                                                                                                                                                                                                                        }

                                                                                                                                                                                                                                        return apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                                                                .flatMap(docUpdate -> updatedFacadeDto(teacherEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                                                                                        .flatMap(teacherFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherFacadeDto))
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
                                                                                                                                                                    return teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherUUID)
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
                                                                                                                                                                                        .flatMap(teacherContactNoList -> teacherProfileRepository.save(previousProfileEntity)
                                                                                                                                                                                                .then(teacherProfileRepository.save(updatedEntity))
                                                                                                                                                                                                .flatMap(teacherProfileEntity -> apiCallService.updateDataList(sendFormData, driveUri + "api/v1/documents/submitted/update", userId, reqCompanyUUID, reqBranchUUID)
                                                                                                                                                                                                        .flatMap(docUpdateEntity -> updatedFacadeDto(teacherEntity, updatedEntity, teacherContactNoDto)
                                                                                                                                                                                                                .flatMap(teacherFacadeDto -> responseSuccessMsg("Record Updated Successfully", teacherFacadeDto))
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
                                                                                    ).switchIfEmpty(responseInfoMsg("Marital Status Record Does not Exist"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Marital Status Record Does not Exist. Please contact developer."))
                                                                            ).switchIfEmpty(responseInfoMsg("Gender Record Does not Exist"))
                                                                            .onErrorResume(ex -> responseErrorMsg("Gender Record Does not Exist. Please contact developer."))
                                                                    ).switchIfEmpty(responseInfoMsg("Caste Record Does not Exist"))
                                                                    .onErrorResume(ex -> responseErrorMsg("Caste Record Does not Exist. Please contact developer."))
                                                            ).switchIfEmpty(responseInfoMsg("Sect Record Does not Exist"))
                                                            .onErrorResume(ex -> responseErrorMsg("Sect Record Does not Exist. Please contact developer."))
                                                    ).switchIfEmpty(responseInfoMsg("Religion Record Does not Exist"))
                                                    .onErrorResume(ex -> responseErrorMsg("Religion Record Does not Exist. Please contact developer."))
                                            ));
                                }).switchIfEmpty(responseInfoMsg(" Profile Against the entered Teacher Record Does not exist"))
                                .onErrorResume(ex -> responseErrorMsg(" Profile Against the entered Teacher Record Does not exist.Please Contact Developer."))
                        ).switchIfEmpty(responseInfoMsg("Teacher Record Does not Exist."))
                        .onErrorResume(ex -> responseErrorMsg("Teacher Record Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_facade_teacher-teacher-profile-contact-nos_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherUUID = UUID.fromString((serverRequest.pathVariable("teacherUUID")));
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

        return teacherRepository.findByUuidAndDeletedAtIsNull(teacherUUID)
                .flatMap(teacherEntity -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                        .flatMap(teacherProfileEntity -> teacherContactNoRepository.findAllByTeacherMetaUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .collectList()
                                .flatMap(teacherContactNoEntity -> {

                                    List<TeacherContactNoEntity> teacherContactNoEntityList = new ArrayList<>();

                                    teacherEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherEntity.setReqDeletedIP(reqIp);
                                    teacherEntity.setReqDeletedPort(reqPort);
                                    teacherEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherEntity.setReqDeletedOS(reqOs);
                                    teacherEntity.setReqDeletedDevice(reqDevice);
                                    teacherEntity.setReqDeletedReferer(reqReferer);

                                    teacherProfileEntity.setDeletedBy(UUID.fromString(userId));
                                    teacherProfileEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    teacherProfileEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    teacherProfileEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    teacherProfileEntity.setReqDeletedIP(reqIp);
                                    teacherProfileEntity.setReqDeletedPort(reqPort);
                                    teacherProfileEntity.setReqDeletedBrowser(reqBrowser);
                                    teacherProfileEntity.setReqDeletedOS(reqOs);
                                    teacherProfileEntity.setReqDeletedDevice(reqDevice);
                                    teacherProfileEntity.setReqDeletedReferer(reqReferer);

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
                                        TeacherContactNoDto teacherContactNoRecord = TeacherContactNoDto.builder()
                                                .contactNo(teacherContact.getContactNo())
                                                .contactTypeUUID(teacherContact.getContactTypeUUID())
                                                .build();

                                        teacherContactNoDto.add(teacherContactNoRecord);
                                    }

                                    return teacherRepository.save(teacherEntity)
                                            .then(teacherProfileRepository.save(teacherProfileEntity))
                                            .then(teacherContactNoRepository.saveAll(teacherContactNoEntityList)
                                                    .collectList())
                                            .flatMap(teacherContactNoEntities -> facadeDto(teacherEntity, teacherProfileEntity, teacherContactNoDto)
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
