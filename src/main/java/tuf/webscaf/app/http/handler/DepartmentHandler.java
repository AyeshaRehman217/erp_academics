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
import tuf.webscaf.app.dbContext.master.entity.DepartmentEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveDepartmentRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;
import tuf.webscaf.helper.SlugifyHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "departmentHandler")
@Component
public class DepartmentHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    SlaveDepartmentRepository slaveDepartmentRepository;

    @Autowired
    FacultyRepository facultyRepository;

    @Autowired
    CampusRepository campusRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    DepartmentVisionAndMissionRepository departmentVisionAndMissionRepository;

    @Autowired
    SlugifyHelper slugifyHelper;

    @AuthHasPermission(value = "academic_api_v1_departments_index")
    public Mono<ServerResponse> index(ServerRequest serverRequest) {

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

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

        String directionProperty = serverRequest.queryParam("dp").map(String::toString).orElse("createdAt");

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        // faculty Query Parameter
        String facultyUUID = serverRequest.queryParam("facultyUUID").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));


        if (!status.isEmpty() && !facultyUUID.isEmpty()) {
            Flux<SlaveDepartmentEntity> slaveDepartmentFlux = slaveDepartmentRepository
                    .findAllByNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(facultyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(facultyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(facultyUUID), Boolean.valueOf(status));

            return slaveDepartmentFlux
                    .collectList()
                    .flatMap(departmentEntity -> slaveDepartmentRepository.countByNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndStatusAndDeletedAtIsNull(searchKeyWord, UUID.fromString(facultyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(facultyUUID), Boolean.valueOf(status), searchKeyWord, UUID.fromString(facultyUUID), Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (departmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", departmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!facultyUUID.isEmpty()) {
            Flux<SlaveDepartmentEntity> slaveDepartmentFlux = slaveDepartmentRepository
                    .findAllByNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNull(pageable,
                            searchKeyWord, UUID.fromString(facultyUUID), searchKeyWord, UUID.fromString(facultyUUID), searchKeyWord, UUID.fromString(facultyUUID));

            return slaveDepartmentFlux
                    .collectList()
                    .flatMap(departmentEntity -> slaveDepartmentRepository.countByNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndFacultyUUIDAndDeletedAtIsNull(searchKeyWord,
                                    UUID.fromString(facultyUUID), searchKeyWord, UUID.fromString(facultyUUID), searchKeyWord, UUID.fromString(facultyUUID))
                            .flatMap(count -> {
                                if (departmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", departmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else if (!status.isEmpty()) {
            Flux<SlaveDepartmentEntity> slaveDepartmentFlux = slaveDepartmentRepository
                    .findAllByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(pageable,
                            searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status));

            return slaveDepartmentFlux
                    .collectList()
                    .flatMap(departmentEntity -> slaveDepartmentRepository.countByNameContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndStatusAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndStatusAndDeletedAtIsNull(searchKeyWord,
                                    Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status), searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (departmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", departmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        } else {
            Flux<SlaveDepartmentEntity> slaveDepartmentFlux = slaveDepartmentRepository
                    .findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNull(pageable, searchKeyWord, searchKeyWord, searchKeyWord);

            return slaveDepartmentFlux
                    .collectList()
                    .flatMap(departmentEntity -> slaveDepartmentRepository.countByNameContainingIgnoreCaseAndDeletedAtIsNullOrDescriptionContainingIgnoreCaseAndDeletedAtIsNullOrShortNameContainingIgnoreCaseAndDeletedAtIsNull(searchKeyWord, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (departmentEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);
                                } else {
                                    return responseIndexSuccessMsg("All Records Fetched Successfully", departmentEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read Request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_departments_show")
    public Mono<ServerResponse> show(ServerRequest serverRequest) {
        UUID departmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));

        return slaveDepartmentRepository.findByUuidAndDeletedAtIsNull(departmentUUID)
                .flatMap(departmentEntity -> responseSuccessMsg("Record Fetched Successfully", departmentEntity))
                .switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please contact developer."));
    }

//    Mapped function to display department against Department Rank from PVT

    @AuthHasPermission(value = "academic_api_v1_department_dept-rank_mapped_show")
    public Mono<ServerResponse> showMappedDepartmentAgainstDeptRank(ServerRequest serverRequest) {

        UUID departmentRankUUID = UUID.fromString(serverRequest.pathVariable("departmentRankUUID"));

        String searchKeyWord = serverRequest.queryParam("skw").map(String::toString).orElse("").trim();

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

        // Query Parameter of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveDepartmentEntity> slaveDepartmentEntityFlux = slaveDepartmentRepository
                    .showMappedDepartmentListWithStatus(departmentRankUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveDepartmentEntityFlux
                    .collectList()
                    .flatMap(departmentListEntity -> slaveDepartmentRepository.countMappedDepartmentWithStatus(departmentRankUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (departmentListEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", departmentListEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        } else {
            Flux<SlaveDepartmentEntity> slaveDepartmentEntityFlux = slaveDepartmentRepository
                    .showMappedDepartmentList(departmentRankUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveDepartmentEntityFlux
                    .collectList()
                    .flatMap(departmentListEntity -> slaveDepartmentRepository.countMappedDepartment(departmentRankUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (departmentListEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", departmentListEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_departments_store")
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

                    DepartmentEntity departmentEntity = DepartmentEntity.builder()
                            .uuid(UUID.randomUUID())
                            .name(value.getFirst("name").trim())
                            .status(Boolean.valueOf(value.getFirst("status")))
                            .description(value.getFirst("description").trim())
                            .code(value.getFirst("code").trim())
                            .slug(slugifyHelper.slugify(value.getFirst("name").trim()))
                            .shortName(value.getFirst("shortName").trim())
                            .facultyUUID(UUID.fromString(value.getFirst("facultyUUID").trim()))
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


                    //check Name is Unique
                    return departmentRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNull(departmentEntity.getName())
                            .flatMap(CheckName -> responseInfoMsg("Name Already exist"))
                            //check Slug is Unique
                            .switchIfEmpty(Mono.defer(() -> departmentRepository.findFirstBySlugAndDeletedAtIsNull(departmentEntity.getSlug())
                                    .flatMap(CheckName -> responseInfoMsg("Slug Already Exist."))))
                            //check Short Name is Unique
                            .switchIfEmpty(Mono.defer(() -> departmentRepository.checkShortNameIsUnique(departmentEntity.getShortName())
                                    .flatMap(CheckName -> responseInfoMsg("Short Name Already Exist."))))
                            //check code is Unique
                            .switchIfEmpty(Mono.defer(() -> departmentRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNull(departmentEntity.getCode())
                                    .flatMap(CheckName -> responseInfoMsg("Code Already Exist."))))
                            // Check faculty uuid exists
                            .switchIfEmpty(Mono.defer(() -> facultyRepository.findByUuidAndDeletedAtIsNull(departmentEntity.getFacultyUUID())
                                    .flatMap(facultyEntity -> departmentRepository.save(departmentEntity)
                                            .flatMap(department -> responseSuccessMsg("Record Updated Successfully", departmentEntity))
                                            .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record.Please contact developer."))).switchIfEmpty(responseInfoMsg("Faculty does not exist."))
                                    .onErrorResume(ex -> responseErrorMsg("Faculty does not exist.Please contact developer."))
                            ));
                }).switchIfEmpty(responseInfoMsg("Unable to read Request."))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_departments_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        UUID departmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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
                .flatMap(value -> departmentRepository.findByUuidAndDeletedAtIsNull(departmentUUID)
                        .flatMap(previousEntity -> {

                            DepartmentEntity updatedEntity = DepartmentEntity
                                    .builder()
                                    .uuid(previousEntity.getUuid())
                                    .name(value.getFirst("name").trim())
                                    .status(Boolean.valueOf(value.getFirst("status")))
                                    .slug(slugifyHelper.slugify(value.getFirst("name").trim()))
                                    .description(value.getFirst("description").trim())
                                    .code(value.getFirst("code").trim())
                                    .shortName(value.getFirst("shortName").trim())
                                    .facultyUUID(UUID.fromString(value.getFirst("facultyUUID").trim()))
                                    .createdAt(previousEntity.getCreatedAt())
                                    .createdBy(previousEntity.getCreatedBy())
                                    .updatedBy(UUID.fromString(userId))
                                    .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                    .reqCreatedIP(previousEntity.getReqCreatedIP())
                                    .reqCreatedPort(previousEntity.getReqCreatedPort())
                                    .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                    .reqCreatedOS(previousEntity.getReqCreatedOS())
                                    .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                    .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                    .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                    .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                    .reqUpdatedIP(reqIp)
                                    .reqUpdatedPort(reqPort)
                                    .reqUpdatedBrowser(reqBrowser)
                                    .reqUpdatedOS(reqOs)
                                    .reqUpdatedDevice(reqDevice)
                                    .reqUpdatedReferer(reqReferer)
                                    .build();

                            previousEntity.setDeletedBy(UUID.fromString(userId));
                            previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            previousEntity.setReqDeletedIP(reqIp);
                            previousEntity.setReqDeletedPort(reqPort);
                            previousEntity.setReqDeletedBrowser(reqBrowser);
                            previousEntity.setReqDeletedOS(reqOs);
                            previousEntity.setReqDeletedDevice(reqDevice);
                            previousEntity.setReqDeletedReferer(reqReferer);

                            //Check if the Name Is Unique
                            return departmentRepository.findFirstByNameIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getName(), departmentUUID)
                                    .flatMap(checkName -> responseInfoMsg("Name Already Exist."))
                                    .switchIfEmpty(Mono.defer(() -> departmentRepository.findFirstBySlugAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getSlug(), departmentUUID)
                                            .flatMap(checkSlug -> responseInfoMsg("The Entered Slug already Exists"))))
                                    //check Short Name is Unique
                                    .switchIfEmpty(Mono.defer(() -> departmentRepository.checkShortNameIsUniqueAndUuidIsNot(updatedEntity.getShortName(), departmentUUID)
                                            .flatMap(CheckName -> responseInfoMsg("Short Name Already Exist."))))
                                    //check code is Unique
                                    .switchIfEmpty(Mono.defer(() -> departmentRepository.findFirstByCodeIgnoreCaseAndDeletedAtIsNullAndUuidIsNot(updatedEntity.getCode(),departmentUUID)
                                            .flatMap(CheckName -> responseInfoMsg("Code Already Exist."))))
                                    // Check if faculty uuid exists
                                    .switchIfEmpty(Mono.defer(() -> facultyRepository.findByUuidAndDeletedAtIsNull(updatedEntity.getFacultyUUID())
                                            .flatMap(facultyEntity -> departmentRepository.save(previousEntity)
                                                    .then(departmentRepository.save(updatedEntity))
                                                    .flatMap(departmentEntity -> responseSuccessMsg("Record Updated Successfully", departmentEntity))
                                                    .switchIfEmpty(responseInfoMsg("Unable to update record.There is something wrong please try again."))
                                                    .onErrorResume(ex -> responseErrorMsg("Unable to update record.Please contact developer."))).switchIfEmpty(responseInfoMsg("Faculty does not exist."))
                                            .onErrorResume(ex -> responseErrorMsg("Faculty does not exist.Please contact developer."))
                                    ));
                        }).switchIfEmpty(responseInfoMsg("Requested record does not exist")))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."))
                .switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request"));
    }

    @AuthHasPermission(value = "academic_api_v1_departments_status_update")
    public Mono<ServerResponse> status(ServerRequest serverRequest) {
        UUID departmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

                    return departmentRepository.findByUuidAndDeletedAtIsNull(departmentUUID)
                            .flatMap(previousEntity -> {
                                // If status is not Boolean value
                                if (status != false && status != true) {
                                    return responseInfoMsg("Status must be Active or InActive");
                                }

                                // If already same status exist in database.
                                if (((previousEntity.getStatus() ? true : false) == status)) {
                                    return responseWarningMsg("Record already exist with same status");
                                }

                                DepartmentEntity updatedEntity = DepartmentEntity.builder()
                                        .uuid(previousEntity.getUuid())
                                        .name(previousEntity.getName())
                                        .status(status == true ? true : false)
                                        .description(previousEntity.getDescription())
                                        .code(previousEntity.getCode())
                                        .slug(previousEntity.getSlug())
                                        .shortName(previousEntity.getShortName())
                                        .facultyUUID(previousEntity.getFacultyUUID())
                                        .createdAt(previousEntity.getCreatedAt())
                                        .createdBy(previousEntity.getCreatedBy())
                                        .updatedBy(UUID.fromString(userId))
                                        .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                        .reqCreatedIP(previousEntity.getReqCreatedIP())
                                        .reqCreatedPort(previousEntity.getReqCreatedPort())
                                        .reqCreatedBrowser(previousEntity.getReqCreatedBrowser())
                                        .reqCreatedOS(previousEntity.getReqCreatedOS())
                                        .reqCreatedDevice(previousEntity.getReqCreatedDevice())
                                        .reqCreatedReferer(previousEntity.getReqCreatedReferer())
                                        .reqUpdatedIP(reqIp)
                                        .reqUpdatedPort(reqPort)
                                        .reqUpdatedBrowser(reqBrowser)
                                        .reqUpdatedOS(reqOs)
                                        .reqUpdatedDevice(reqDevice)
                                        .reqUpdatedReferer(reqReferer)
                                        .build();

                                // update status
                                previousEntity.setDeletedBy(UUID.fromString(userId));
                                previousEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                previousEntity.setReqDeletedIP(reqIp);
                                previousEntity.setReqDeletedPort(reqPort);
                                previousEntity.setReqDeletedBrowser(reqBrowser);
                                previousEntity.setReqDeletedOS(reqOs);
                                previousEntity.setReqDeletedDevice(reqDevice);
                                previousEntity.setReqDeletedReferer(reqReferer);

                                return departmentRepository.save(previousEntity)
                                        .then(departmentRepository.save(updatedEntity))
                                        .flatMap(statusUpdate -> responseSuccessMsg("Status Updated Successfully", statusUpdate))
                                        .switchIfEmpty(responseInfoMsg("Unable to update the status. There is something wrong please try again."))
                                        .onErrorResume(err -> responseErrorMsg("Unable to update the status.Please contact developer."));
                            }).switchIfEmpty(responseInfoMsg("Requested Record does not exist"))
                            .onErrorResume(err -> responseErrorMsg("Requested Record does not exist.Please contact developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read the request"))
                .onErrorResume(err -> responseErrorMsg("Unable to read the request.Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_departments_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        UUID departmentUUID = UUID.fromString((serverRequest.pathVariable("uuid")));
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

        return departmentRepository.findByUuidAndDeletedAtIsNull(departmentUUID)
                //Check if Department Exists in Courses
                .flatMap(departmentEntity -> courseRepository.findFirstByDepartmentUUIDAndDeletedAtIsNull(departmentEntity.getUuid())
                                .flatMap(checkName -> responseInfoMsg("Unable to Delete Record as the Reference Exists"))
//                   Check if department Reference exists in department vision and mission
                                .switchIfEmpty(Mono.defer(() -> departmentVisionAndMissionRepository.findFirstByDepartmentUUIDAndDeletedAtIsNull(departmentEntity.getUuid())
                                        .flatMap(semesterEntity -> responseInfoMsg("Unable to delete Record as the reference Exists"))))
                                .switchIfEmpty(Mono.defer(() -> {

                                    departmentEntity.setDeletedBy(UUID.fromString(userId));
                                    departmentEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                    departmentEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                    departmentEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                    departmentEntity.setReqDeletedIP(reqIp);
                                    departmentEntity.setReqDeletedPort(reqPort);
                                    departmentEntity.setReqDeletedBrowser(reqBrowser);
                                    departmentEntity.setReqDeletedOS(reqOs);
                                    departmentEntity.setReqDeletedDevice(reqDevice);
                                    departmentEntity.setReqDeletedReferer(reqReferer);

                                    return departmentRepository.save(departmentEntity)
                                            .flatMap(entity -> responseSuccessMsg("Record Deleted Successfully", entity))
                                            .switchIfEmpty(responseInfoMsg("Unable to delete record.There is something wrong please try again."))
                                            .onErrorResume(ex -> responseErrorMsg("Unable to delete record.Please Contact Developer."));
                                }))
                ).switchIfEmpty(responseInfoMsg("Requested record does not exist"))
                .onErrorResume(ex -> responseErrorMsg("Requested record does not exist.Please contact developer."));
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
