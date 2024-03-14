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
import tuf.webscaf.app.dbContext.master.entity.StudentEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveStudentEntity;
import tuf.webscaf.app.http.handler.StudentGroupStudentPvtHandler;
import tuf.webscaf.app.http.validationFilters.studentGroupStudentPvtHandler.DeleteStudentGroupStudentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentGroupStudentPvtHandler.ShowStudentGroupStudentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentGroupStudentPvtHandler.StoreStudentGroupStudentPvtHandlerFilter;
import tuf.webscaf.springDocImpl.SectionStudentDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class StudentGroupStudentsPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-group-students/un-mapped/show/{studentGroupUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentGroupStudentPvtHandler.class,
                            beanMethod = "showUnMappedStudentsAgainstStudentGroup",
                            operation = @Operation(
                                    operationId = "showUnMappedStudentsAgainstStudentGroup",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Students That Are Not Mapped With Given Student Group",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGroupUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with student Id or official Email"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-group-students/mapped/show/{studentGroupUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentGroupStudentPvtHandler.class,
                            beanMethod = "showMappedStudentsAgainstStudentGroup",
                            operation = @Operation(
                                    operationId = "showMappedStudentsAgainstStudentGroup",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Students That Are Mapped With Given Student Group",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGroupUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with student Id or official Email"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-group-students/store/{studentGroupUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentGroupStudentPvtHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Students for a Student Group",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = SectionStudentDocImpl.class)
                                            )),
                                    description = "Store Students Against a Given Student Group",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGroupUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/student-group-students/delete/{studentGroupUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentGroupStudentPvtHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = StudentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Record does not exist",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Students Against a Given Student Group",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentGroupUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "studentUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> studentGroupStudentsPvtRoutes(StudentGroupStudentPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-group-students/un-mapped/show/{studentGroupUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showUnMappedStudentsAgainstStudentGroup).filter(new ShowStudentGroupStudentPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-group-students/mapped/show/{studentGroupUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedStudentsAgainstStudentGroup).filter(new ShowStudentGroupStudentPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-group-students/store/{studentGroupUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentGroupStudentPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-group-students/delete/{studentGroupUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentGroupStudentPvtHandlerFilter()));
    }

}
