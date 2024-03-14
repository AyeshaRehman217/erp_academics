package tuf.webscaf.router.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import tuf.webscaf.app.dbContext.master.dto.TeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherGuardianTeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeacherGuardianTeacherGuardianProfileContactNoFacadeRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherGuardianTeacherGuardianProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Get the Records With Pagination",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with firstname, lastname, teacherId, nic"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/show/{teacherGuardianUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherGuardianTeacherGuardianProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given Teacher Guardian UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherGuardianTeacherGuardianProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record not found with given uuid",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Teacher Guardian Teacher Guardian Profile Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = TeacherGuardianTeacherGuardianProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/update/{teacherGuardianUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.PUT,
                    beanClass = TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "update",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = TeacherGuardianTeacherGuardianProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID")
                            },
                            description = "Update the Record for given Teacher Guardian UUID",
                            requestBody = @RequestBody(
                                    description = "Update Teacher Guardian Profile and Contact No",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/x-www-form-urlencoded",
                                            encoding = {
                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                            },
                                            schema = @Schema(type = "object", implementation = TeacherGuardianProfileContactNoFacadeDto.class)
                                    ))
                    )
            ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/delete/{teacherGuardianUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "delete",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = SlaveCasteEntity.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            description = "Delete the Record for given Teacher Guardian UUID",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherGuardianUUID"),
                            }
                    )
            ),

            }
    )
    public RouterFunction<ServerResponse> teacherGuardianTeacherGuardianProfileContactNoFacadeRoutes(TeacherGuardianTeacherGuardianProfileContactNoFacadeHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(POST("academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store))
                .and(RouterFunctions.route(GET("academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/show/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/update/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/teacher-guardian-teacher-guardian-profile-contact-nos/delete/{teacherGuardianUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete));
    }
}
