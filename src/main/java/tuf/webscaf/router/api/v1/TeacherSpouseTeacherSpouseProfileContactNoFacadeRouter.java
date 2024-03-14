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
import tuf.webscaf.app.dbContext.master.dto.TeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherSpouseTeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.validationFilters.teacherSpouseTeacherSpouseProfileContactNoFacadeHandler.ShowTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSpouseTeacherSpouseProfileContactNoFacadeHandler.StoreTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSpouseTeacherSpouseProfileContactNoFacadeHandler.UpdateTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeacherSpouseTeacherSpouseProfileContactNoFacadeRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherSpouseTeacherSpouseProfileContactNoFacadeDto.class
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
                            path = "/academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/show/{teacherSpouseUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherSpouseTeacherSpouseProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given Teacher Spouse UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSpouseUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherSpouseTeacherSpouseProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record not found with given uuid",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Teacher Spouse Teacher Spouse Profile Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = TeacherSpouseTeacherSpouseProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/update/{teacherSpouseUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.PUT,
                    beanClass = TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "update",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = TeacherSpouseProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherSpouseUUID")
                            },
                            description = "Update the Record for given Teacher Spouse UUID",
                            requestBody = @RequestBody(
                                    description = "Update Teacher Spouse Profile and Contact No",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/x-www-form-urlencoded",
                                            encoding = {
                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                            },
                                            schema = @Schema(type = "object", implementation = TeacherSpouseProfileContactNoFacadeDto.class)
                                    ))
                    )
            ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/delete/{teacherSpouseUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler.class,
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
                            description = "Delete the Record for given Teacher Spouse UUID",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherSpouseUUID"),
                            }
                    )
            ),

            }
    )
    public RouterFunction<ServerResponse> teacherSpouseTeacherSpouseProfileContactNoFacadeRoutes(TeacherSpouseTeacherSpouseProfileContactNoFacadeHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(POST("academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/show/{teacherSpouseUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/update/{teacherSpouseUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/teacher-spouse-teacher-spouse-profile-contact-nos/delete/{teacherSpouseUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowTeacherSpouseTeacherSpouseProfileContactNoFacadeHandlerFilter()));
    }
}
