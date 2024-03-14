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
import tuf.webscaf.app.dbContext.master.dto.TeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherTeacherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.StudentStudentProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.handler.TeacherTeacherProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.validationFilters.teacherTeacherProfileContactNoFacadeHandler.ShowTeacherTeacherProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherTeacherProfileContactNoFacadeHandler.StoreTeacherTeacherProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherTeacherProfileContactNoFacadeHandler.UpdateTeacherTeacherProfileContactNoFacadeHandlerFilter;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeacherTeacherProfileContactNoFacadeRouter {
    @Bean
    @RouterOperations(
            {

                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-teacher-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherTeacherProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherTeacherProfileContactNoFacadeDto.class
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
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with firstname, lastname, employeeCode, nic, Telephone No , Email"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-teacher-profile-contact-nos/show/{teacherUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherTeacherProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherTeacherProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given Teacher UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-teacher-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherTeacherProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherTeacherProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record not found with given uuid",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Teacher Teacher Profile Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = TeacherTeacherProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-teacher-profile-contact-nos/update/{teacherUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.PUT,
                    beanClass = TeacherTeacherProfileContactNoFacadeHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "update",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = TeacherProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherUUID")
                            },
                            description = "Update the Record for given Teacher UUID",
                            requestBody = @RequestBody(
                                    description = "Update Teacher Profile and Contact No",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/x-www-form-urlencoded",
                                            encoding = {
                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                            },
                                            schema = @Schema(type = "object", implementation = TeacherProfileContactNoFacadeDto.class)
                                    ))
                    )
            ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-teacher-profile-contact-nos/delete/{teacherUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = TeacherTeacherProfileContactNoFacadeHandler.class,
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
                            description = "Delete the Record for given Teacher UUID",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherUUID")
                            }
                    )
            ),

            }
    )
    public RouterFunction<ServerResponse> teacherTeacherProfileContactNoFacadeRoutes(TeacherTeacherProfileContactNoFacadeHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/facade/teacher-teacher-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(POST("academic/api/v1/facade/teacher-teacher-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherTeacherProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/facade/teacher-teacher-profile-contact-nos/show/{teacherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowTeacherTeacherProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/teacher-teacher-profile-contact-nos/update/{teacherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateTeacherTeacherProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/teacher-teacher-profile-contact-nos/delete/{teacherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowTeacherTeacherProfileContactNoFacadeHandlerFilter()));
    }
}
