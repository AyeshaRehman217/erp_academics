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
import tuf.webscaf.app.dbContext.master.dto.TeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.TeacherSiblingTeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.handler.TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler;
import tuf.webscaf.app.http.validationFilters.teacherSiblingTeacherSiblingProfileContactNoFacadeHandler.ShowTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSiblingTeacherSiblingProfileContactNoFacadeHandler.StoreTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSiblingTeacherSiblingProfileContactNoFacadeHandler.UpdateTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class TeacherSiblingTeacherSiblingProfileContactNoFacadeRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveTeacherSiblingTeacherSiblingProfileContactNoFacadeDto.class
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
                            path = "/academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/show/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherSiblingTeacherSiblingProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given Teacher Sibling UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = TeacherSiblingTeacherSiblingProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record not found with given uuid",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Teacher Sibling Teacher Sibling Profile Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = TeacherSiblingTeacherSiblingProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/update/{teacherSiblingUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.PUT,
                    beanClass = TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "update",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = TeacherSiblingProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID")
                            },
                            description = "Update the Record for given Teacher Sibling UUID",
                            requestBody = @RequestBody(
                                    description = "Update Teacher Sibling Profile and Contact No",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/x-www-form-urlencoded",
                                            encoding = {
                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                            },
                                            schema = @Schema(type = "object", implementation = TeacherSiblingProfileContactNoFacadeDto.class)
                                    ))
                    )
            ), @RouterOperation(
                    path = "/academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/delete/{teacherSiblingUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler.class,
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
                            description = "Delete the Record for given Teacher Sibling UUID",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
                            }
                    )
            ),

            }
    )
    public RouterFunction<ServerResponse> teacherSiblingTeacherSiblingProfileContactNoFacadeRoutes(TeacherSiblingTeacherSiblingProfileContactNoFacadeHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(POST("academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(GET("academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/show/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show).filter(new ShowTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/update/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update).filter(new UpdateTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/teacher-sibling-teacher-sibling-profile-contact-nos/delete/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new ShowTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter()));
    }
}
