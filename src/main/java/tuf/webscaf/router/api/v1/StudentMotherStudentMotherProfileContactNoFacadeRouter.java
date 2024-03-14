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
import tuf.webscaf.app.dbContext.master.dto.StudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentMotherStudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentMotherStudentMotherProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.StudentMotherStudentMotherProfileContactNoFacadeHandler;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class StudentMotherStudentMotherProfileContactNoFacadeRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentMotherStudentMotherProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentMotherStudentMotherProfileContactNoFacadeDto.class
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
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with firstname, lastname, studentId, nic"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/show/{studentMotherUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentMotherStudentMotherProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentStudentProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given Student Mother UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentMotherUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentMotherStudentMotherProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentMotherStudentMotherProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record not found with given uuid",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Student Mother Student Mother Profile Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = StudentMotherStudentMotherProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/update/{studentMotherUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.PUT,
                    beanClass = StudentMotherStudentMotherProfileContactNoFacadeHandler.class,
                    beanMethod = "update",
                    operation = @Operation(
                            operationId = "update",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = StudentMotherProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "studentMotherUUID")
                            },
                            description = "Update the Record for given Student Mother UUID",
                            requestBody = @RequestBody(
                                    description = "Update Student Mother Profile and Contact No",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/x-www-form-urlencoded",
                                            encoding = {
                                                    @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                            },
                                            schema = @Schema(type = "object", implementation = StudentMotherProfileContactNoFacadeDto.class)
                                    ))
                    )
            ), @RouterOperation(
                    path = "/academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/delete/{studentMotherUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = StudentMotherStudentMotherProfileContactNoFacadeHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "delete",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = SlaveStudentMotherStudentMotherProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            description = "Delete the Record for given Student Mother UUID",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "studentMotherUUID"),
                            }
                    )
            ),

            }
    )
    public RouterFunction<ServerResponse> studentMotherStudentMotherProfileContactNoFacadeRoutes(StudentMotherStudentMotherProfileContactNoFacadeHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(POST("academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store))
                .and(RouterFunctions.route(GET("academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/show/{studentMotherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/update/{studentMotherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/student-mother-student-mother-profile-contact-nos/delete/{studentMotherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete));
    }
}
