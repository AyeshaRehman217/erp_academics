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
import tuf.webscaf.app.dbContext.master.entity.HobbyEntity;
import tuf.webscaf.app.dbContext.master.entity.StudentFatherHobbyPvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.HobbyRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherHobbyPvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentFatherRepository;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveHobbyRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentFatherHobbyPvtRepository;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Tag(name = "studentFatherHobbyPvtHandler")
@Component
public class StudentFatherHobbyPvtHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    StudentFatherHobbyPvtRepository studentFatherHobbyPvtRepository;

    @Autowired
    SlaveStudentFatherHobbyPvtRepository slaveStudentFatherHobbyPvtRepository;

    @Autowired
    SlaveHobbyRepository slaveHobbyRepository;

    @Autowired
    HobbyRepository hobbyRepository;

    @Autowired
    StudentFatherRepository studentFatherRepository;

    @AuthHasPermission(value = "academic_api_v1_student-father-hobbies_existing_show")
    public Mono<ServerResponse> showHobbiesAgainstStudentFather(ServerRequest serverRequest) {

        final UUID studentFatherUUID = UUID.fromString(serverRequest.pathVariable("studentFatherUUID"));

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

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
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .existingStudentFatherHobbiesListWithStatusCheck(studentFatherUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status), directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countExistingStudentFatherHobbiesRecordsWithStatus(studentFatherUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .existingStudentFatherHobbiesList(studentFatherUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countExistingStudentFatherHobbiesRecords(studentFatherUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records Fetched Successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-hobbies_mapped_show")
    public Mono<ServerResponse> showMappedHobbiesAgainstStudentFather(ServerRequest serverRequest) {

        final UUID studentFatherUUID = UUID.fromString(serverRequest.pathVariable("studentFatherUUID"));

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

        //Optional Query Parameter Based of Status
        String status = serverRequest.queryParam("status").map(String::toString).orElse("").trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, directionProperty));

        if (!status.isEmpty()) {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .showStudentFatherHobbiesListWithStatus(studentFatherUUID, Boolean.valueOf(status), searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countMappedStudentFatherHobbiesWithStatus(studentFatherUUID, searchKeyWord, searchKeyWord, Boolean.valueOf(status))
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        } else {
            Flux<SlaveHobbyEntity> slaveHobbiesFlux = slaveHobbyRepository
                    .showStudentFatherHobbiesList(studentFatherUUID, searchKeyWord, searchKeyWord, directionProperty, d, pageable.getPageSize(), pageable.getOffset());

            return slaveHobbiesFlux
                    .collectList()
                    .flatMap(hobbyEntity -> slaveHobbyRepository.countMappedStudentFatherHobbies(studentFatherUUID, searchKeyWord, searchKeyWord)
                            .flatMap(count -> {
                                if (hobbyEntity.isEmpty()) {
                                    return responseIndexInfoMsg("Record does not exist", count);

                                } else {

                                    return responseIndexSuccessMsg("All Records fetched successfully", hobbyEntity, count);
                                }
                            })
                    ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                    .onErrorResume(ex -> responseErrorMsg("Unable to read request Please contact developer."));
        }
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-hobbies_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");

        final UUID studentFatherUUID = UUID.fromString(serverRequest.pathVariable("studentFatherUUID"));

        String reqCompanyUUID = serverRequest.headers().firstHeader("reqCompanyUUID");
        String reqBranchUUID = serverRequest.headers().firstHeader("reqBranchUUID");
        String reqIp = serverRequest.headers().firstHeader("reqIp");
        String reqPort = serverRequest.headers().firstHeader("reqPort");
        String reqBrowser = serverRequest.headers().firstHeader("reqBrowser");
        String reqOs = serverRequest.headers().firstHeader("reqOs");
        String reqDevice = serverRequest.headers().firstHeader("reqDevice");
        String reqReferer = serverRequest.headers().firstHeader("reqReferer");

        if (userId == null) {
            return responseWarningMsg("Unknown user");
        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return responseWarningMsg("Unknown user");
        }

        return serverRequest.formData()
                .flatMap(value -> studentFatherRepository.findByUuidAndDeletedAtIsNull(studentFatherUUID)
                        .flatMap(studentFatherEntity -> {

                            //getting List of Hobbies From Front
                            List<String> listOfHobbyUUID = new LinkedList<>(value.get("hobbyUUID"));

                            listOfHobbyUUID.removeIf(s -> s.equals(""));

                            List<UUID> l_list = new ArrayList<>();
                            for (String getHobbyUUID : listOfHobbyUUID) {
                                l_list.add(UUID.fromString(getHobbyUUID));
                            }

                            if (!l_list.isEmpty()) {
                                return hobbyRepository.findAllByUuidInAndDeletedAtIsNull(l_list)
                                        .collectList()
                                        .flatMap(existingHobbies -> {
                                            // Hobby UUID List
                                            List<UUID> hobbyList = new ArrayList<>();

                                            for (HobbyEntity hobby : existingHobbies) {
                                                hobbyList.add(hobby.getUuid());
                                            }

                                            if (!hobbyList.isEmpty()) {

                                                // hobby uuid list to show in response
                                                List<UUID> hobbyRecords = new ArrayList<>(hobbyList);

                                                List<StudentFatherHobbyPvtEntity> listPvt = new ArrayList<>();

                                                return studentFatherHobbyPvtRepository.findAllByStudentFatherUUIDAndHobbyUUIDInAndDeletedAtIsNull(studentFatherUUID, hobbyList)
                                                        .collectList()
                                                        .flatMap(studentFatherPvtEntity -> {
                                                            for (StudentFatherHobbyPvtEntity pvtEntity : studentFatherPvtEntity) {
                                                                //Removing Existing Hobby UUID in Hobby Final List to be saved that does not contain already mapped values
                                                                hobbyList.remove(pvtEntity.getHobbyUUID());
                                                            }

                                                            // iterate Hobby UUIDs for given Student Father
                                                            for (UUID hobbyUUID : hobbyList) {

                                                                StudentFatherHobbyPvtEntity studentFatherHobbyPvtEntity = StudentFatherHobbyPvtEntity
                                                                        .builder()
                                                                        .hobbyUUID(hobbyUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .studentFatherUUID(studentFatherUUID)
                                                                        .createdBy(UUID.fromString(userId))
                                                                        .createdAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                        .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                                                        .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                                                        .reqCreatedIP(reqIp)
                                                                        .reqCreatedPort(reqPort)
                                                                        .reqCreatedBrowser(reqBrowser)
                                                                        .reqCreatedOS(reqOs)
                                                                        .reqCreatedDevice(reqDevice)
                                                                        .reqCreatedReferer(reqReferer)
                                                                        .build();

                                                                listPvt.add(studentFatherHobbyPvtEntity);
                                                            }

                                                            return studentFatherHobbyPvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!hobbyList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", hobbyRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseSuccessMsg("Record Already Exists", hobbyRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Hobby Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("The Entered Hobby Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("The Entered Hobby Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Hobby First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Student Father Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Student Father Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-father-hobbies_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID studentFatherUUID = UUID.fromString(serverRequest.pathVariable("studentFatherUUID"));
        UUID hobbyUUID = UUID.fromString(serverRequest.queryParam("hobbyUUID").map(String::toString).orElse(""));
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
            return responseWarningMsg("Unknown user");
        } else if (!userId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return responseWarningMsg("Unknown user");
        }

        return hobbyRepository.findByUuidAndDeletedAtIsNull(hobbyUUID)
                .flatMap(hobbyEntity -> studentFatherHobbyPvtRepository
                        .findFirstByStudentFatherUUIDAndHobbyUUIDAndDeletedAtIsNull(studentFatherUUID, hobbyUUID)
                        .flatMap(studentFatherHobbyPvtEntity -> {

                            studentFatherHobbyPvtEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                            studentFatherHobbyPvtEntity.setDeletedBy(UUID.fromString(userId));
                            studentFatherHobbyPvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                            studentFatherHobbyPvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                            studentFatherHobbyPvtEntity.setReqDeletedIP(reqIp);
                            studentFatherHobbyPvtEntity.setReqDeletedPort(reqPort);
                            studentFatherHobbyPvtEntity.setReqDeletedBrowser(reqBrowser);
                            studentFatherHobbyPvtEntity.setReqDeletedOS(reqOs);
                            studentFatherHobbyPvtEntity.setReqDeletedDevice(reqDevice);
                            studentFatherHobbyPvtEntity.setReqDeletedReferer(reqReferer);

                            return studentFatherHobbyPvtRepository.save(studentFatherHobbyPvtEntity)
                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", hobbyEntity))
                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record. Please Contact Developer."));
                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                        .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Father  record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Student Father  record does not exist. Please Contact Developer."));
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
