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
import tuf.webscaf.app.http.handler.SectionStudentPvtHandler;
import tuf.webscaf.app.http.validationFilters.sectionStudentPvtHandler.DeleteSectionStudentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.sectionStudentPvtHandler.ShowSectionStudentPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.sectionStudentPvtHandler.StoreSectionStudentPvtHandlerFilter;
import tuf.webscaf.springDocImpl.SectionStudentDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class SectionStudentsPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/section-students/un-mapped/show",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = SectionStudentPvtHandler.class,
                            beanMethod = "showUnMappedStudentsAgainstSection",
                            operation = @Operation(
                                    operationId = "showUnMappedStudentsAgainstSection",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveStudentEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Students That Are Un-Mapped for given courseOffered",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with studentId"),
//                                            @Parameter(in = ParameterIn.PATH, name = "sectionUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "courseOfferedUUID", required = true),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/section-students/mapped/show/{sectionUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = SectionStudentPvtHandler.class,
                            beanMethod = "showMappedStudentsAgainstSection",
                            operation = @Operation(
                                    operationId = "showMappedStudentsAgainstSection",
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
                                    description = "Show Students That Are Mapped for given Section UUID and courseOffered UUID",
                                    parameters = {
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with studentId"),
                                            @Parameter(in = ParameterIn.PATH, name = "sectionUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "courseOfferedUUID", required = true),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/section-students/store/{sectionUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = SectionStudentPvtHandler.class,
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
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Students for a Section",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = SectionStudentDocImpl.class)
                                            )),
                                    description = "Store Students Against a Given Section",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "sectionUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/section-students/delete/{sectionUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = SectionStudentPvtHandler.class,
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
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Students Against a Given Section",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "sectionUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "studentUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> sectionStudentPvtRoutes(SectionStudentPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/section-students/un-mapped/show").and(accept(APPLICATION_FORM_URLENCODED)), handle::showUnMappedStudentsAgainstSection).filter(new ShowSectionStudentPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/section-students/mapped/show/{sectionUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedStudentsAgainstSection).filter(new ShowSectionStudentPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/section-students/store/{sectionUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreSectionStudentPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/section-students/delete/{sectionUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteSectionStudentPvtHandlerFilter()));
    }

}
