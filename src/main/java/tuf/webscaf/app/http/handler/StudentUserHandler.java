package tuf.webscaf.app.http.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.master.entity.StudentUserEntity;
import tuf.webscaf.app.dbContext.master.repositry.*;
import tuf.webscaf.app.service.ApiCallService;
import tuf.webscaf.app.verification.module.AuthHasPermission;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
@Tag(name = "studentUserHandler")
public class StudentUserHandler {

    @Value("${server.zone}")
    private String zone;

    @Autowired
    CustomResponse appresponse;

    @Value("${server.erp_auth_module.uri}")
    private String authModuleUri;

    @Autowired
    ApiCallService apiCallService;

    @Autowired
    StudentUserRepository studentUserRepository;

    @Autowired
    StudentProfileRepository studentProfileRepository;

    @AuthHasPermission(value = "academic_api_v1_student-users_store")
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

                    MultiValueMap<String, String> saveUserPostFormData = new LinkedMultiValueMap<>();

                    //Taking Values userTypeUUID (Student UUID) to Create user record
                    saveUserPostFormData.add("userTypeUUID", value.getFirst("userTypeUUID").trim());
                    saveUserPostFormData.add("password", value.getFirst("password").trim());

                    return apiCallService.getDataWithString(authModuleUri + "api/v1/accesslevel/show-by-slug/", "student")
                            .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                    .flatMap(accessLevelUUID -> {

                                        saveUserPostFormData.add("accessLevelUUID", accessLevelUUID.toString());

                                        return studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(UUID.fromString(value.getFirst("userTypeUUID").trim()))
                                                .flatMap(stdProfile -> {

                                                    saveUserPostFormData.add("fname", stdProfile.getFirstName());
                                                    saveUserPostFormData.add("lname", stdProfile.getLastName());
                                                    saveUserPostFormData.add("email", stdProfile.getEmail());
                                                    saveUserPostFormData.add("phone", stdProfile.getTelephoneNo());
                                                    saveUserPostFormData.add("desc", stdProfile.getDescription());

                                                    return apiCallService.postDataList(saveUserPostFormData, authModuleUri + "api/v1/users/with-user-type/store", userId, reqCompanyUUID, reqBranchUUID)
                                                            .flatMap(userJsonNode -> {

                                                                JsonNode returnUserJsonNode = null;

                                                                for (JsonNode userNode : userJsonNode.get("data")) {
                                                                    returnUserJsonNode = userNode;
                                                                }

                                                                JsonNode finalReturnUserJsonNode = returnUserJsonNode;
                                                                return apiCallService.getUUID(userJsonNode)
                                                                        .flatMap(user -> {

                                                                            StudentUserEntity studentUserEntity = StudentUserEntity.builder()
                                                                                    .uuid(UUID.randomUUID())
                                                                                    .userUUID(user)
                                                                                    .studentUUID(UUID.fromString(value.getFirst("userTypeUUID").trim()))
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

                                                                            return studentUserRepository.save(studentUserEntity)
                                                                                    .flatMap(stdUserEntity -> responseSuccessMsg("Record Stored Successfully!", finalReturnUserJsonNode))
                                                                                    .switchIfEmpty(responseInfoMsg("Unable to Store Record.There is something wrong please try again."))
                                                                                    .onErrorResume(ex -> responseErrorMsg("Unable to Store Record.Please Contact Developer."));
                                                                        }).switchIfEmpty(responseInfoMsg(apiCallService.getResponseMsg(userJsonNode)))
                                                                        .onErrorResume(ex -> responseErrorMsg(apiCallService.getResponseMsg(userJsonNode)));
                                                            }).switchIfEmpty(responseInfoMsg("Unable to Create User there is something wrong please try again."))
                                                            .onErrorResume(ex -> responseErrorMsg("Unable to Create User.Please Contact Developer."));
                                                }).switchIfEmpty(responseInfoMsg("Student Profile Does not Exist Against the Entered Student"))
                                                .onErrorResume(ex -> responseErrorMsg("Student Profile Does not Exist Against the Entered Student.Please Contact Developer."));
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to fetch Student Access Level.Something went wrong please try again"))
                            .onErrorResume(ex -> responseErrorMsg("Unable to fetch Student Access Level.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to read request"))
                .onErrorResume(ex -> responseErrorMsg("Unable to read Request. Please contact developer."));
    }

    @AuthHasPermission(value = "academic_api_v1_student-users_update")
    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        String userId = serverRequest.headers().firstHeader("auid");

        UUID studentUUID = UUID.fromString((serverRequest.pathVariable("studentUUID")));

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

                    MultiValueMap<String, String> updateFormData = new LinkedMultiValueMap<>();

                    return apiCallService.getDataWithString(authModuleUri + "api/v1/accesslevel/show-by-slug/", "student")
                            .flatMap(jsonNode -> apiCallService.getUUID(jsonNode)
                                    .flatMap(studentAccessUUID -> {

                                        updateFormData.add("accessLevelUUID", studentAccessUUID.toString());
                                        updateFormData.add("password", value.getFirst("password"));


                                        return studentUserRepository.findFirstByStudentUUIDAndDeletedAtIsNull(studentUUID)
                                                .flatMap(previousStdUserEntity -> {

                                                    return studentProfileRepository.findFirstByStudentUUIDAndDeletedAtIsNull(previousStdUserEntity.getStudentUUID())
                                                            .flatMap(studentProfileEntity -> {
                                                                        // set campus fields to request form data
                                                                        updateFormData.add("fname", studentProfileEntity.getFirstName());
                                                                        updateFormData.add("lname", studentProfileEntity.getLastName());
                                                                        updateFormData.add("email", studentProfileEntity.getEmail());
                                                                        updateFormData.add("phone", studentProfileEntity.getTelephoneNo());
                                                                        updateFormData.add("desc", studentProfileEntity.getDescription());
                                                                        updateFormData.add("userTypeUUID", studentProfileEntity.getStudentUUID().toString());

                                                                        return apiCallService.putDataList(updateFormData, previousStdUserEntity.getUserUUID(), authModuleUri + "api/v1/users/with-user-type/update/", userId, reqCompanyUUID, reqBranchUUID)
                                                                                .flatMap(updateUserJson -> apiCallService.getUUID(updateUserJson)
                                                                                        .flatMap(user -> {

                                                                                                    JsonNode returnUserJsonNode = null;

                                                                                                    for (JsonNode userNode : updateUserJson.get("data")) {
                                                                                                        returnUserJsonNode = userNode;
                                                                                                    }

                                                                                                    JsonNode finalReturnUserJsonNode1 = returnUserJsonNode;

                                                                                                    StudentUserEntity updatedStdUserEntity = StudentUserEntity.builder()
                                                                                                            .uuid(previousStdUserEntity.getUuid())
                                                                                                            .userUUID(user)
                                                                                                            .studentUUID(studentProfileEntity.getStudentUUID())
                                                                                                            .createdAt(previousStdUserEntity.getCreatedAt())
                                                                                                            .createdBy(previousStdUserEntity.getCreatedBy())
                                                                                                            .updatedBy(UUID.fromString(userId))
                                                                                                            .updatedAt(LocalDateTime.now(ZoneId.of(zone)))
                                                                                                            .reqCreatedIP(previousStdUserEntity.getReqCreatedIP())
                                                                                                            .reqCreatedPort(previousStdUserEntity.getReqCreatedPort())
                                                                                                            .reqCreatedBrowser(previousStdUserEntity.getReqCreatedBrowser())
                                                                                                            .reqCreatedOS(previousStdUserEntity.getReqCreatedOS())
                                                                                                            .reqCreatedDevice(previousStdUserEntity.getReqCreatedDevice())
                                                                                                            .reqCreatedReferer(previousStdUserEntity.getReqCreatedReferer())
                                                                                                            .reqCompanyUUID(UUID.fromString(reqCompanyUUID))
                                                                                                            .reqBranchUUID(UUID.fromString(reqBranchUUID))
                                                                                                            .reqUpdatedIP(reqIp)
                                                                                                            .reqUpdatedPort(reqPort)
                                                                                                            .reqUpdatedBrowser(reqBrowser)
                                                                                                            .reqUpdatedOS(reqOs)
                                                                                                            .reqUpdatedDevice(reqDevice)
                                                                                                            .reqUpdatedReferer(reqReferer)
                                                                                                            .build();

                                                                                                    previousStdUserEntity.setDeletedBy(UUID.fromString(userId));
                                                                                                    previousStdUserEntity.setDeletedAt(LocalDateTime.now(ZoneId.of(zone)));
                                                                                                    previousStdUserEntity.setReqDeletedIP(reqIp);
                                                                                                    previousStdUserEntity.setReqDeletedPort(reqPort);
                                                                                                    previousStdUserEntity.setReqDeletedBrowser(reqBrowser);
                                                                                                    previousStdUserEntity.setReqDeletedOS(reqOs);
                                                                                                    previousStdUserEntity.setReqDeletedDevice(reqDevice);
                                                                                                    previousStdUserEntity.setReqDeletedReferer(reqReferer);


                                                                                                    return studentUserRepository.save(previousStdUserEntity)
                                                                                                            .then(studentUserRepository.save(updatedStdUserEntity))
                                                                                                            .flatMap(saveStdUserEntity -> responseSuccessMsg("Record Updated Successfully", finalReturnUserJsonNode1))
                                                                                                            .switchIfEmpty(responseInfoMsg("Unable to update record. There is something wrong please try again."))
                                                                                                            .onErrorResume(ex -> responseErrorMsg("Unable to update record. Please contact developer."));
                                                                                                }
                                                                                        )
                                                                                        .switchIfEmpty(responseInfoMsg(apiCallService.getResponseMsg(updateUserJson)))
                                                                                        .onErrorResume(ex -> responseErrorMsg(apiCallService.getResponseMsg(updateUserJson)))
                                                                                )
                                                                                .switchIfEmpty(responseInfoMsg("Unable tp Update user.There is something wrong please try again."))
                                                                                .onErrorResume(ex -> responseErrorMsg("Unable to Update user.Please Contact Developer."));
                                                                    }
                                                            ).switchIfEmpty(responseInfoMsg("Student Profile Does not exist against the entered student."))
                                                            .onErrorResume(ex -> responseErrorMsg("Student Profile Does not exist.Please Contact Developer."));

                                                }).switchIfEmpty(responseInfoMsg("Student User Does not exist against the entered Student"))
                                                .onErrorResume(ex -> responseErrorMsg("Student User Does not exist against the entered Student.Please Contact Developer"));
                                    })
                            ).switchIfEmpty(responseInfoMsg("Unable to fetch Student Access Level."))
                            .onErrorResume(ex -> responseErrorMsg("Unable to fetch Student Access Level.Please Contact Developer."));
                }).switchIfEmpty(responseInfoMsg("Unable to Read Request.There is something wrong please try again."))
                .onErrorResume(ex -> responseErrorMsg("Unable to Read Request.Please Contact Developer."));
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

    public Mono<ServerResponse> responseInfoMsg(String msg, Object entity) {
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
                entity

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
