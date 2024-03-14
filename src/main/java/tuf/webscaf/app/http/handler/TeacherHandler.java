package tuf.webscaf.app.http.handler;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.TeacherEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherDto;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveTeacherRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "teacherHandler")
@Component
public class TeacherHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    SlaveTeacherRepository slaveTeacherRepository;

    @Autowired
    TeacherProfileRepository teacherProfileRepository;

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    TeacherFamilyDoctorRepository teacherFamilyDoctorRepository;

    @Autowired
    TeacherDocumentRepository teacherDocumentRepository;

    @Autowired
    TeacherJobHistoryRepository teacherJobHistoryRepository;

    @Autowired
    TeacherFinancialHistoryRepository teacherFinancialHistoryRepository;

    @Autowired
    TeacherAcademicRecordRepository teacherAcademicRecordRepository;

    @Autowired
    TeacherAddressRepository teacherAddressRepository;

    @Autowired
    TeacherHobbyPvtRepository teacherHobbyPvtRepository;

    @Autowired
    TeacherNationalityPvtRepository teacherNationalityPvtRepository;

    @Autowired
    TeacherLanguagePvtRepository teacherLanguagePvtRepository;

    @Autowired
    TeacherAilmentPvtRepository teacherAilmentPvtRepository;

    @Autowired
    FacultyTeacherRepository facultyTeacherRepository;

    @Autowired
    TeacherSpouseRepository teacherSpouseRepository;

    @Autowired
    TeacherMotherRepository teacherMotherRepository;

    @Autowired
    TeacherFatherRepository teacherFatherRepository;

    @Autowired
    TeacherSiblingRepository teacherSiblingRepository;

    @Autowired
    TeacherChildRepository teacherChildRepository;

    @Autowired
    TeacherGuardianRepository teacherGuardianRepository;

    @Autowired
    DepartmentRankRepository departmentRankRepository;

    @Autowired
    DepartmentRankCatalogueRepository departmentRankCatalogueRepository;

    @Autowired
    TimetableCreationRepository timetableCreationRepository;

    @Autowired
    CommencementOfClassesRepository commencementOfClassesRepository;

    @Autowired
    CampusRepository campusRepository;


    @AuthHasPermission(value = "academic_api_v1_teachers_index")
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
        if (page < 0) {
            return responseErrorMsg("Invalid Page No");
        }

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
            Flux<SlaveTeacherDto> slaveTeacherEntityFlux = slaveTeacherRepository
                    .indexWithStatus(Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherEntityFlux
                    .collectList()
                    .flatMap(teacherEntity -> slaveTeacherRepository.countIndexRecordsWithStatusFilter(Boolean.valueOf(status), searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveTeacherDto> slaveTeacherEntityFlux = slaveTeacherRepository
                    .indexWithoutStatus(searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveTeacherEntityFlux
                    .collectList()
                    .flatMap(teacherEntity -> slaveTeacherRepository.countIndexRecordsWithoutStatusFilter(searchKeyWord, searchKeyWord)
                            .flatMap(count ->
                            {
                                if (teacherEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", teacherEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }

    }

    @AuthHasPermission(value = "academic_api_v1_teachers_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));

        return slaveTeacherRepository.showByUuid(teacherUUID)
                .flatMap(teacherEntityDB -> responseSuccessMsg("Record Fetched Successfully", teacherEntityDB))
                .switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested Record does not exist. Please contact developer."));
    }

    public Mono<ServerResponse> checkDepartmentRank(TeacherEntity teacherEntity) {
        return departmentRankRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getDeptRankUUID())
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
                                                return teacherRepository.save(teacherEntity)
                                                        .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                                                        .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                        .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                            }
                                            // else record can't be stored
                                            else {
                                                return responseInfoMsg("Dept Rank is already ranked up to the maximum allowed teachers");
                                            }
                                        }
                                        // if max count is not specified in dept rank catalogue
                                        else {
                                            return teacherRepository.save(teacherEntity)
                                                    .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                        }
                                    }
                                }))
                ).switchIfEmpty(responseInfoMsg("Department Rank Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Department Rank Does not exist. Please contact developer."));
    }

    public Mono<ServerResponse> checkTeacher(TeacherEntity teacherEntity) {
        return teacherRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getReportingTo())
                .flatMap(teacher -> {
                    teacherEntity.setReportingTo(teacher.getUuid());
                    return teacherRepository.save(teacherEntity)
                            .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Reporting To Teacher Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Reporting To Teacher Record Does not exist. Please contact developer."));
    }


    public Mono<ServerResponse> checkTeacherAndDepartmentRank(TeacherEntity teacherEntity) {
        return teacherRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getReportingTo())
                .flatMap(reportingTo -> departmentRankRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getDeptRankUUID())
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
                                                        return teacherRepository.save(teacherEntity)
                                                                .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                                                                .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                                .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                    }
                                                    // else record can't be stored
                                                    else {
                                                        return responseInfoMsg("Dept Rank is already ranked up to the maximum allowed teachers");
                                                    }
                                                }
                                                // if max count is not specified in dept rank catalogue
                                                else {
                                                    return teacherRepository.save(teacherEntity)
                                                            .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                }
                                            }
                                        }))
                        ).switchIfEmpty(responseInfoMsg("Department Rank Does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Department Rank Does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Reporting To Teacher Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Reporting To Teacher Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teachers_store")
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
                    UUID teacherUUID = null;
                    UUID deptRankUUID = null;

                    if ((value.containsKey("reportingTo") && (!Objects.equals(value.getFirst("reportingTo"), "")))) {
                        teacherUUID = UUID.fromString(value.getFirst("reportingTo").trim());
                    }

                    if ((value.containsKey("deptRankUUID") && (!Objects.equals(value.getFirst("deptRankUUID"), "")))) {
                        deptRankUUID = UUID.fromString(value.getFirst("deptRankUUID").trim());
                    }
                    TeacherEntity teacherEntity = TeacherEntity.builder()
                            .employeeCode(value.getFirst("employeeCode"))
                            .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                            .reportingTo(teacherUUID)
                            .deptRankUUID(deptRankUUID)
                            .uuid(UUID.randomUUID())
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

                    // check employee code
                    return teacherRepository.findFirstByEmployeeCodeAndDeletedAtIsNull(teacherEntity.getEmployeeCode())
                            .flatMap(checkMsg -> responseInfoMsg("Employee Code Already Exists"))
                            .switchIfEmpty(Mono.defer(() -> campusRepository.findByUuidAndDeletedAtIsNull(teacherEntity.getCampusUUID())
                                    .flatMap(campusEntity -> {
                                                // if teacher and department rank uuids are given
                                                if (teacherEntity.getReportingTo() != null && teacherEntity.getDeptRankUUID() != null) {
                                                    return checkTeacherAndDepartmentRank(teacherEntity);
                                                }
                                                // if teacher uuid is given
                                                else if (teacherEntity.getReportingTo() != null) {
                                                    return checkTeacher(teacherEntity);
                                                }
                                                // if department rank uuid is given
                                                else if (teacherEntity.getDeptRankUUID() != null) {
                                                    return checkDepartmentRank(teacherEntity);
                                                }
                                                // store teacher record
                                                else {
                                                    return teacherRepository.save(teacherEntity)
                                                            .flatMap(teacherEntityDB -> responseSuccessMsg("Record Stored Successfully", teacherEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to store record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to store record. Please contact developer."));
                                                }
                                            }
                                    ).switchIfEmpty(responseInfoMsg("Campus Record does not exist"))
                                    .onErrorResume(ex -> responseErrorMsg("Campus Record does not exist. Please contact developer."))
                            ));
                }).onErrorResume(err -> responseErrorMsg("Unable to read the request"))
                .switchIfEmpty(responseInfoMsg("Unable to read the request. Please contact developer."));
    }

    public Mono<ServerResponse> updateDepartmentRank(TeacherEntity updatedTeacherEntity, TeacherEntity previousTeacher) {
        return departmentRankRepository.findByUuidAndDeletedAtIsNull(updatedTeacherEntity.getDeptRankUUID())
                .flatMap(departmentRank -> departmentRankCatalogueRepository.findByUuidAndDeletedAtIsNull(departmentRank.getDeptRankCatalogueUUID())
                        .flatMap(departmentRankCatalogueEntity -> teacherRepository.findAllByDeptRankUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherEntity.getDeptRankUUID(), updatedTeacherEntity.getUuid())
                                .collectList()
                                .flatMap(teachersWithGivenDeptRank -> {

                                    // if many is false in dept rank catalogue
                                    if (!departmentRank.getMany()) {

                                        // if some teacher is not already ranked, allow updating record
                                        if (teachersWithGivenDeptRank.isEmpty()) {
                                            return teacherRepository.save(previousTeacher)
                                                    .then(teacherRepository.save(updatedTeacherEntity))
                                                    .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again"))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
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
                                                return teacherRepository.save(previousTeacher)
                                                        .then(teacherRepository.save(updatedTeacherEntity))
                                                        .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                        .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again"))
                                                        .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
                                            }
                                            // else record can't be updated
                                            else {
                                                return responseInfoMsg("Dept Rank is already ranked up to the maximum allowed teachers");
                                            }
                                        }
                                        // if max count is not specified in dept rank catalogue
                                        else {
                                            return teacherRepository.save(previousTeacher)
                                                    .then(teacherRepository.save(updatedTeacherEntity))
                                                    .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again"))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
                                        }
                                    }
                                }))
                ).switchIfEmpty(responseInfoMsg("Department Rank Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Department Rank Does not exist. Please contact developer."));
    }

    public Mono<ServerResponse> updateTeacher(TeacherEntity updatedTeacherEntity, TeacherEntity previousTeacher) {
        return teacherRepository.findByUuidAndDeletedAtIsNull(updatedTeacherEntity.getReportingTo())
                .flatMap(teacher -> {
                    if (updatedTeacherEntity.getReportingTo().equals(previousTeacher.getUuid())) {
                        return responseInfoMsg("Teacher Cannot be Reported to its self");
                    }
                    updatedTeacherEntity.setReportingTo(teacher.getUuid());
                    return teacherRepository.save(previousTeacher)
                            .then(teacherRepository.save(updatedTeacherEntity))
                            .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Reporting To Teacher Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Reporting To Teacher Record Does not exist. Please contact developer."));
    }

    public Mono<ServerResponse> updateTeacherAndDepartmentRank(TeacherEntity updatedTeacherEntity, TeacherEntity previousTeacher) {
        return teacherRepository.findByUuidAndDeletedAtIsNull(updatedTeacherEntity.getReportingTo())
                .flatMap(reportingTo -> departmentRankRepository.findByUuidAndDeletedAtIsNull(updatedTeacherEntity.getDeptRankUUID())
                        .flatMap(departmentRank -> departmentRankCatalogueRepository.findByUuidAndDeletedAtIsNull(departmentRank.getDeptRankCatalogueUUID())
                                .flatMap(departmentRankCatalogueEntity -> teacherRepository.findAllByDeptRankUUIDAndDeletedAtIsNullAndUuidIsNot(updatedTeacherEntity.getDeptRankUUID(), updatedTeacherEntity.getUuid())
                                        .collectList()
                                        .flatMap(teachersWithGivenDeptRank -> {

                                            // if same uuid is given in teacher reporting to
                                            if (updatedTeacherEntity.getReportingTo().equals(updatedTeacherEntity.getUuid())) {
                                                return responseInfoMsg("Teacher Cannot be Reported to its self");
                                            }

                                            // if many is false in dept rank catalogue
                                            if (!departmentRank.getMany()) {

                                                // if some teacher is not already ranked, allow updating record
                                                if (teachersWithGivenDeptRank.isEmpty()) {
                                                    return teacherRepository.save(previousTeacher)
                                                            .then(teacherRepository.save(updatedTeacherEntity))
                                                            .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
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
                                                        return teacherRepository.save(previousTeacher)
                                                                .then(teacherRepository.save(updatedTeacherEntity))
                                                                .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                                .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again"))
                                                                .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                    }
                                                    // else record can't be updated
                                                    else {
                                                        return responseInfoMsg("Dept Rank is already ranked up to the maximum allowed teachers");
                                                    }
                                                }
                                                // if max count is not specified in dept rank catalogue
                                                else {
                                                    return teacherRepository.save(previousTeacher)
                                                            .then(teacherRepository.save(updatedTeacherEntity))
                                                            .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again"))
                                                            .onErrorResume(err -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                }
                                            }
                                        }))
                        ).switchIfEmpty(responseInfoMsg("Department Rank Does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Department Rank Does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Reporting To Teacher Record Does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Reporting To Teacher Record Does not exist. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teachers_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        final UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                .flatMap(value -> teacherRepository.findByUuidAndDeletedAtIsNull(teacherUUID)
                        .flatMap(entity -> {
                            UUID reportingTeacherUUID = null;
                            UUID deptRankUUID = null;

                            if ((value.containsKey("reportingTo") && (!Objects.equals(value.getFirst("reportingTo"), "")))) {
                                reportingTeacherUUID = UUID.fromString(value.getFirst("reportingTo").trim());
                            }

                            if ((value.containsKey("deptRankUUID") && (!Objects.equals(value.getFirst("deptRankUUID"), "")))) {
                                deptRankUUID = UUID.fromString(value.getFirst("deptRankUUID").trim());
                            }
                            TeacherEntity updatedTeacherEntity = TeacherEntity.builder()
                                    .uuid(entity.getUuid())
                                    .employeeCode(value.getFirst("employeeCode"))
                                    .campusUUID(UUID.fromString(value.getFirst("campusUUID")))
                                    .reportingTo(reportingTeacherUUID)
                                    .deptRankUUID(deptRankUUID)
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .createdAt(entity.getCreatedAt())
                                    .createdBy(entity.getCreatedBy())
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .updatedBy(UUID.fromString(userId))
                                    .reqCreatedIP(entity.getReqCreatedIP())
                                    .reqCreatedPort(entity.getReqCreatedPort())
                                    .reqCreatedBrowser(entity.getReqCreatedBrowser())
                                    .reqCreatedOS(entity.getReqCreatedOS())
                                    .reqCreatedDevice(entity.getReqCreatedDevice())
                                    .reqCreatedReferer(entity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            //Deleting Previous Record and Creating a New One Based on UUID
                            entity.setDeletedBy(UUID.fromString(userId));
                            entity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            entity.setReqDeletedIP(reqIp);
                            entity.setReqDeletedPort(reqPort);
                            entity.setReqDeletedBrowser(reqBrowser);
                            entity.setReqDeletedOS(reqOs);
                            entity.setReqDeletedDevice(reqDevice);
                            entity.setReqDeletedReferer(reqReferer);

                            //Storing Deleted Previous Entity First and Then Updated Entity
                            return teacherRepository.findFirstByEmployeeCodeAndDeletedAtIsNullAndUuidIsNot(updatedTeacherEntity.getEmployeeCode(), teacherUUID)
                                    .flatMap(checkCodeMsg -> responseInfoMsg("Employee Code Already Exists"))
                                    .switchIfEmpty(Mono.defer(() -> campusRepository.findByUuidAndDeletedAtIsNull(updatedTeacherEntity.getCampusUUID())
                                            .flatMap(campusEntity -> {
                                                        // update teacher record if teacher and department rank uuids exist in request
                                                        if (updatedTeacherEntity.getReportingTo() != null && updatedTeacherEntity.getDeptRankUUID() != null) {
                                                            return updateTeacherAndDepartmentRank(updatedTeacherEntity, entity);
                                                        }

                                                        // update teacher record if teacher uuid exists in request
                                                        else if (updatedTeacherEntity.getReportingTo() != null) {
                                                            return updateTeacher(updatedTeacherEntity, entity);
                                                        }

                                                        // update teacher record if department rank uuid exists in request
                                                        else if (updatedTeacherEntity.getDeptRankUUID() != null) {
                                                            return updateDepartmentRank(updatedTeacherEntity, entity);
                                                        }

                                                        // store teacher record
                                                        else {
                                                            return teacherRepository.save(entity)
                                                                    .then(teacherRepository.save(updatedTeacherEntity))
                                                                    .flatMap(teacherEntityDB -> responseSuccessMsg("Record Updated Successfully", teacherEntityDB))
                                                                    .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                        }
                                                    }
                                            ).switchIfEmpty(responseInfoMsg("Campus Record does not exist"))
                                            .onErrorResume(ex -> responseErrorMsg("Campus Record does not exist. Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read the request."))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_teachers_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                    boolean status = Boolean.parseBoolean(value.getFirst("status"));
                    return teacherRepository.findByUuidAndDeletedAtIsNull(teacherUUID)
                            .flatMap(teacherEntityDB -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((teacherEntityDB.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                TeacherEntity updatedTeacherEntity = TeacherEntity.builder()
                                        .employeeCode(teacherEntityDB.getEmployeeCode())
                                        .campusUUID(teacherEntityDB.getCampusUUID())
                                        .reportingTo(teacherEntityDB.getReportingTo())
                                        .deptRankUUID(teacherEntityDB.getDeptRankUUID())
                                        .uuid(teacherEntityDB.getUuid())
                                        .status(status == true ? true : false)
                                        .createdAt(teacherEntityDB.getCreatedAt())
                                        .createdBy(teacherEntityDB.getCreatedBy())
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .updatedBy(UUID.fromString(userId))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(teacherEntityDB.getReqCreatedIP())
                                        .reqCreatedPort(teacherEntityDB.getReqCreatedPort())
                                        .reqCreatedBrowser(teacherEntityDB.getReqCreatedBrowser())
                                        .reqCreatedOS(teacherEntityDB.getReqCreatedOS())
                                        .reqCreatedDevice(teacherEntityDB.getReqCreatedDevice())
                                        .reqCreatedReferer(teacherEntityDB.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                teacherEntityDB.setDeletedBy(UUID.fromString(userId));
                                teacherEntityDB.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                teacherEntityDB.setReqDeletedIP(reqIp);
                                teacherEntityDB.setReqDeletedPort(reqPort);
                                teacherEntityDB.setReqDeletedBrowser(reqBrowser);
                                teacherEntityDB.setReqDeletedOS(reqOs);
                                teacherEntityDB.setReqDeletedDevice(reqDevice);
                                teacherEntityDB.setReqDeletedReferer(reqReferer);

                                return teacherRepository.save(teacherEntityDB)
                                        .then(teacherRepository.save(updatedTeacherEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status. Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please Contact Developer."));
    }


    @AuthHasPermission(value = "academic_api_v1_teachers_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID teacherUUID = UUID.fromString(serverRequest.pathVariable("uuid"));
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
                //Checks if Teacher Reference exists in Teacher Profiles
                .flatMap(teacherEntity -> teacherProfileRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                        .flatMap(teacherProfileEntity -> responseInfoMsg("Unable to delete record as the reference exists"))
                        //Checks if Teacher Reference exists in Teacher Family Doctors
                        .switchIfEmpty(Mono.defer(() -> teacherFamilyDoctorRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherFamilyDoctorEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Documents
                        .switchIfEmpty(Mono.defer(() -> teacherDocumentRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherDocumentEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Job History
                        .switchIfEmpty(Mono.defer(() -> teacherJobHistoryRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherJobHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Financial History
                        .switchIfEmpty(Mono.defer(() -> teacherFinancialHistoryRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherFinancialHistoryEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Academic Record
                        .switchIfEmpty(Mono.defer(() -> teacherAcademicRecordRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherAcademicRecordEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Addresses
                        .switchIfEmpty(Mono.defer(() -> teacherAddressRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherAddressEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Ailment Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherAilmentPvtRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Language Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherLanguagePvtRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherAilmentPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Hobby Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherHobbyPvtRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherHobbyPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Nationality Pvt
                        .switchIfEmpty(Mono.defer(() -> teacherNationalityPvtRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherNationalityPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Faculty Teacher Pvt
                        .switchIfEmpty(Mono.defer(() -> facultyTeacherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(facultyTeacherPvtEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Spouses
                        .switchIfEmpty(Mono.defer(() -> teacherSpouseRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherSpouseEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Mother
                        .switchIfEmpty(Mono.defer(() -> teacherMotherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherMotherEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Father
                        .switchIfEmpty(Mono.defer(() -> teacherFatherRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherFatherEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Child
                        .switchIfEmpty(Mono.defer(() -> teacherChildRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherChildEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Sibling
                        .switchIfEmpty(Mono.defer(() -> teacherSiblingRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherSiblingEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher Guardian
                        .switchIfEmpty(Mono.defer(() -> teacherGuardianRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherGuardianEntity -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Timetable Creation
                        .switchIfEmpty(Mono.defer(() -> timetableCreationRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherEntity1 -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Commencement of Classes
                        .switchIfEmpty(Mono.defer(() -> commencementOfClassesRepository.findFirstByTeacherUUIDAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherEntity1 -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        //Checks if Teacher Reference exists in Teacher
                        .switchIfEmpty(Mono.defer(() -> teacherRepository.findFirstByReportingToAndDeletedAtIsNull(teacherEntity.getUuid())
                                .flatMap(teacherEntity1 -> responseInfoMsg("Unable to delete record as the reference exists"))))
                        .switchIfEmpty(Mono.defer(() -> {
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

                            return teacherRepository.save(teacherEntity)
                                    .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully.", entity))
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
                Mono.just(entity)
        );
    }

    public Mono<ServerResponse> responseWarningMsg(String msg) {
        var messages = List.of(
                new AppResponseMessage(
                        AppResponse.Response.WARNING,
                        msg
                )
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
