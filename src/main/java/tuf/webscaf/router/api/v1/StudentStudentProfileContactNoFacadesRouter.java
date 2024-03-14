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
import tuf.webscaf.app.dbContext.master.dto.StudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.master.dto.StudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveStudentStudentProfileContactNoFacadeDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAcademicCalendarEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveAilmentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCasteEntity;
import tuf.webscaf.app.http.handler.*;
import tuf.webscaf.app.http.validationFilters.academicCalendarHandler.ShowAcademicCalendarHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ailmentHandler.IndexAilmentHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ailmentHandler.ShowAilmentHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ailmentHandler.StoreAilmentHandlerFilter;
import tuf.webscaf.app.http.validationFilters.ailmentHandler.UpdateAilmentHandlerFilter;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class StudentStudentProfileContactNoFacadesRouter {
    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/facade/student-student-profile-contact-nos/index",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentStudentProfileContactNoFacadeHandler.class,
                            beanMethod = "index",
                            operation = @Operation(
                                    operationId = "index",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentStudentProfileContactNoFacadeDto.class
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
                            path = "/academic/api/v1/facade/student-student-profile-contact-nos/show/{studentUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentStudentProfileContactNoFacadeHandler.class,
                            beanMethod = "show",
                            operation = @Operation(
                                    operationId = "show",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentStudentProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record Does Not exist.",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the Record for given student UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/student-student-profile-contact-nos/store",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentStudentProfileContactNoFacadeHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentStudentProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Store the Record",
                                    requestBody = @RequestBody(
                                            description = "Create Student-Student-Profile-ContactNo",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = StudentStudentProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/facade/student-student-profile-contact-nos/update/{studentUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.PUT,
                            beanClass = StudentStudentProfileContactNoFacadeHandler.class,
                            beanMethod = "update",
                            operation = @Operation(
                                    operationId = "update",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentProfileContactNoFacadeDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID")
                                    },
                                    description = "Update the Record for given Student UUID",
                                    requestBody = @RequestBody(
                                            description = "Update Student Profile and Contact No",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = StudentProfileContactNoFacadeDto.class)
                                            ))
                            )
                    ), @RouterOperation(
                    path = "/academic/api/v1/facade/student-student-profile-contact-nos/delete/{studentUUID}",
                    produces = {
                            MediaType.APPLICATION_FORM_URLENCODED_VALUE
                    },
                    method = RequestMethod.DELETE,
                    beanClass = StudentStudentProfileContactNoFacadeHandler.class,
                    beanMethod = "delete",
                    operation = @Operation(
                            operationId = "delete",
                            security = {@SecurityRequirement(name = "bearer")},
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "successful operation",
                                            content = @Content(schema = @Schema(
                                                    implementation = SlaveStudentStudentProfileContactNoFacadeDto.class
                                            ))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Record does not exist",
                                            content = @Content(schema = @Schema(hidden = true))
                                    )
                            },
                            description = "Delete the Record for given uuid",
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
                            }
                    )
            ),


            }
    )
    public RouterFunction<ServerResponse> studentStudentProfileContactNoFacadeRoutes(StudentStudentProfileContactNoFacadeHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/facade/student-student-profile-contact-nos/index").and(accept(APPLICATION_FORM_URLENCODED)), handle::index)
                .and(RouterFunctions.route(GET("academic/api/v1/facade/student-student-profile-contact-nos/show/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::show))
                .and(RouterFunctions.route(POST("academic/api/v1/facade/student-student-profile-contact-nos/store").and(accept(APPLICATION_FORM_URLENCODED)), handle::store))
                .and(RouterFunctions.route(PUT("academic/api/v1/facade/student-student-profile-contact-nos/update/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::update))
                .and(RouterFunctions.route(DELETE("academic/api/v1/facade/student-student-profile-contact-nos/delete/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete));
    }
}
