package tuf.webscaf.app.http.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.dto.LanguageDto;
import tuf.webscaf.app.dbContext.master.entity.StudentChildLanguagePvtEntity;
import tuf.webscaf.app.dbContext.master.repositry.StudentChildLanguagePvtRepository;
import tuf.webscaf.app.dbContext.master.repositry.StudentChildRepository;
import tuf.webscaf.app.dbContext.slave.repositry.SlaveStudentChildLanguagePvtRepository;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Tag(name = "studentChildLanguagePvtHandler")
public class StudentChildLanguagePvtHandler {
    @Value("${server.zone}")
    private String zone;

    @Autowired
    StudentChildLanguagePvtRepository studentChildLanguagePvtRepository;

    @Autowired
    SlaveStudentChildLanguagePvtRepository slaveStudentChildLanguagePvtRepository;

    @Autowired
    StudentChildRepository studentChildRepository;

    @Autowired
    CustomResponse appresponse;

    @Autowired
    ApiCallService apiCallService;

    @Value("${server.erp_config_module.uri}")
    private String configUri;

    //This Function Fetch Mapped Language UUID's against Student Child from Pvt
    @AuthHasPermission(value = "academic_api_v1_student-child-languages_list_show")
    public Mono<ServerResponse> showList(ServerRequest serverRequest) {
        final UUID studentChildUUID = UUID.fromString(serverRequest.pathVariable("studentChildUUID"));

        return slaveStudentChildLanguagePvtRepository.getAllMappedLanguageUUIDAgainstStudentChild(studentChildUUID)
                .flatMap(uuids -> {
                    List<String> listOfIds = Arrays.asList(uuids.split("\\s*,\\s*"));
                    return responseSuccessMsg("Records Fetched Successfully", listOfIds);
                }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                .onErrorResume(err -> responseErrorMsg("Record does not exist. Please Contact Developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-languages_store")
    public Mono<ServerResponse> store(ServerRequest serverRequest) {

        String userId = serverRequest.headers().firstHeader("auid");
        final UUID studentChildUUID = UUID.fromString(serverRequest.pathVariable("studentChildUUID"));

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
            return responseWarningMsg("Unknown User");
        }

        return serverRequest.formData()
                .flatMap(value -> studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                        .flatMap(studentChildEntity -> {

                            //getting List of Languages From Front
                            List<String> listOfLanguageUUID = new ArrayList<>(value.get("languageUUID"));

                            listOfLanguageUUID.removeIf(s -> s.equals(""));

                            if (!listOfLanguageUUID.isEmpty()) {
                                return apiCallService.getListWithQueryParams(configUri + "api/v1/languages/list/show", "uuid", listOfLanguageUUID)
                                        .flatMap(languageJsonNode -> {
                                            // Language UUID List
                                            List<UUID> languageList = new ArrayList<>(apiCallService.getListOfUUIDs(languageJsonNode));

                                            if (!languageList.isEmpty()) {

                                                // language uuid list to show in response
                                                List<UUID> languageRecords = new ArrayList<>(languageList);

                                                List<StudentChildLanguagePvtEntity> listPvt = new ArrayList<>();

                                                return studentChildLanguagePvtRepository.findAllByStudentChildUUIDAndLanguageUUIDInAndDeletedAtIsNull(studentChildUUID, languageList)
                                                        .collectList()
                                                        .flatMap(studentChildPvtEntity -> {
                                                            for (StudentChildLanguagePvtEntity pvtEntity : studentChildPvtEntity) {
                                                                //Removing Existing Language UUID in Language Final List to be saved that does not contain already mapped values
                                                                languageList.remove(pvtEntity.getLanguageUUID());
                                                            }

                                                            // iterate Language UUIDs for given Student Child
                                                            for (UUID languageUUID : languageList) {

                                                                StudentChildLanguagePvtEntity voucherLanguagePvtEntity = StudentChildLanguagePvtEntity
                                                                        .builder()
                                                                        .languageUUID(languageUUID)
                                                                        .uuid(UUID.randomUUID())
                                                                        .studentChildUUID(studentChildUUID)
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

                                                                listPvt.add(voucherLanguagePvtEntity);
                                                            }

                                                            return studentChildLanguagePvtRepository.saveAll(listPvt)
                                                                    .collectList()
                                                                    .flatMap(groupList -> {

                                                                        if (!languageList.isEmpty()) {
                                                                            return responseSuccessMsg("Record Stored Successfully", languageRecords)
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record,There is something wrong please try again!"))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        } else {
                                                                            return responseSuccessMsg("Record Already Exists", languageRecords);
                                                                        }

                                                                    }).switchIfEmpty(responseInfoMsg("Unable to store Record.There is something wrong please try again."))
                                                                    .onErrorResume(err -> responseErrorMsg("Unable to store Record.Please Contact Developer."));
                                                        });
                                            } else {
                                                return responseInfoMsg("Language Record does not exist");
                                            }
                                        }).switchIfEmpty(responseInfoMsg("Language Does not exist."))
                                        .onErrorResume(ex -> responseErrorMsg("Language Does not exist.Please Contact Developer."));
                            } else {
                                return responseInfoMsg("Select Language First");
                            }
                        }).switchIfEmpty(responseInfoMsg("Student Child Record does not exist"))
                        .onErrorResume(err -> responseInfoMsg("Student Child Record does not exist. Please contact developer."))
                ).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-child-languages_delete")
    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        final UUID studentChildUUID = UUID.fromString(serverRequest.pathVariable("studentChildUUID"));
        UUID languageUUID = UUID.fromString(serverRequest.queryParam("languageUUID").map(String::toString).orElse("").trim());
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
            return responseWarningMsg("Unknown User");
        }

        return studentChildRepository.findByUuidAndDeletedAtIsNull(studentChildUUID)
                .flatMap(studentChildEntity -> apiCallService.getDataWithUUID(configUri + "api/v1/languages/show/", languageUUID)
                        .flatMap(languageJson -> apiCallService.getUUID(languageJson)
                                .flatMap(language -> studentChildLanguagePvtRepository.findFirstByStudentChildUUIDAndLanguageUUIDAndDeletedAtIsNull(studentChildUUID, languageUUID)
                                        .flatMap(studentChildLanguagePvtEntity -> {

                                            studentChildLanguagePvtEntity.setDeletedAt(LocalDateTime.now());
                                            studentChildLanguagePvtEntity.setDeletedBy(UUID.fromString(userId));
                                            studentChildLanguagePvtEntity.setReqCompanyUUID(UUID.fromString(reqCompanyUUID));
                                            studentChildLanguagePvtEntity.setReqBranchUUID(UUID.fromString(reqBranchUUID));
                                            studentChildLanguagePvtEntity.setReqDeletedIP(reqIp);
                                            studentChildLanguagePvtEntity.setReqDeletedPort(reqPort);
                                            studentChildLanguagePvtEntity.setReqDeletedBrowser(reqBrowser);
                                            studentChildLanguagePvtEntity.setReqDeletedOS(reqOs);
                                            studentChildLanguagePvtEntity.setReqDeletedDevice(reqDevice);
                                            studentChildLanguagePvtEntity.setReqDeletedReferer(reqReferer);

                                            return studentChildLanguagePvtRepository.save(studentChildLanguagePvtEntity)
                                                    .flatMap(deleteEntity -> responseSuccessMsg("Record Deleted Successfully", languageDtoMapper(languageJson)))
                                                    .switchIfEmpty(responseInfoMsg("Unable to delete the record.There is something wrong please try again."))
                                                    .onErrorResume(err -> responseErrorMsg("Unable to delete the record.Please Contact Developer."));
                                        }).switchIfEmpty(responseInfoMsg("Record does not exist"))
                                        .onErrorResume(err -> responseErrorMsg("Record does not exist.Please Contact Developer.")))
                        ).switchIfEmpty(responseInfoMsg("Language Does not exist"))
                        .onErrorResume(ex -> responseErrorMsg("Language Does not Exist.Please Contact Developer."))
                ).switchIfEmpty(responseInfoMsg("Student Child Record does not exist."))
                .onErrorResume(ex -> responseErrorMsg("Student Child Record does not exist.Please Contact Developer."));

    }

    public LanguageDto languageDtoMapper(JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        final JsonNode arrNode = jsonNode.get("data");
        JsonNode objectNode = null;
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                objectNode = objNode;
            }
        }
        ObjectReader reader = mapper.readerFor(new TypeReference<LanguageDto>() {
        });
        LanguageDto languageDto = null;
        if (!jsonNode.get("data").isEmpty()) {
            try {
                languageDto = reader.readValue(objectNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return languageDto;
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