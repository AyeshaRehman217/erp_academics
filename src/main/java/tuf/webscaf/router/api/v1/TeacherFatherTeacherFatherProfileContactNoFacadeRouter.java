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
import tuf.webscaf.app.dbContext.master.dto.TeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherFatherTeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.TeacherFatherTeacherFatherProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.handler.TeacherFatherTeacherFatherProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.validationFilters.teacherFatherTeacherFatherProfileContactNoFacadeHandler.ShowTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherFatherTeacherFatherProfileContactNoFacadeHandler.StoreTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherFatherTeacherFatherProfileContactNoFacadeHandler.UpdateTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeacherFatherTeacherFatherProfileContactNoFacadeRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherFatherTeacherFatherProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherFatherTeacherFatherProfileContactNoFacadeDto.class
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
                            path = "/academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/show/{teacherFatherUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherFatherTeacherFatherProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherFatherTeacherFatherProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given Teacher Father UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherFatherUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherFatherTeacherFatherProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherFatherTeacherFatherProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record not found with given uuid",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Teacher Father Teacher Father Profile Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = TeacherFatherTeacherFatherProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/update/{teacherFatherUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.PUT,
                    beanClass = TeacherFatherTeacherFatherProfileContactNoFacadeHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "update",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = TeacherFatherProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherFatherUUID")
                            },
                            description = "Update the Record for given Teacher Father UUID",
                            requestBody = @RequestBody(
                                    description = "Update Teacher Father Profile and Contact No",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/x-www-form-urlencoded",
                                            encoding = {
                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                            },
                                            schema = @Schema(type = "object", implementation = TeacherFatherProfileContactNoFacadeDto.class)
                                    ))
                    )
            ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/delete/{teacherFatherUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = TeacherFatherTeacherFatherProfileContactNoFacadeHandler.class,
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
                            description = "Delete the Record for given Teacher Father UUID",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherFatherUUID"),
                            }
                    )
            ),

            }
    )
    public RouterFunction<ServerResponse> teacherFatherTeacherFatherProfileContactNoFacadeRoutes(TeacherFatherTeacherFatherProfileContactNoFacadeHandler handle) {
        return  RouterFunctions.route(GET("academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(POST("academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/show/{teacherFatherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/update/{teacherFatherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/teacher-father-teacher-father-profile-contact-nos/delete/{teacherFatherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowTeacherFatherTeacherFatherProfileContactNoFacadeHandlerFilter()));
    }
}
