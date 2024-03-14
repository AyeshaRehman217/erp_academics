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
import tuf.webscaf.app.dbContext.master.entity.NationalityEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveNationalityEntity;
import tuf.webscaf.app.http.handler.StudentNationalityPvtHandler;
import tuf.webscaf.app.http.validationFilters.studentNationalityPvtHandler.DeleteStudentNationalityPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentNationalityPvtHandler.ShowStudentNationalityPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentNationalityPvtHandler.StoreStudentNationalityPvtHandlerFilter;
import tuf.webscaf.springDocImpl.NationalityDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class StudentNationalitiesPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-nationalities/existing/show/{studentUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentNationalityPvtHandler.class,
                            beanMethod = "showNationalitiesAgainstStudent",
                            operation = @Operation(
                                    operationId = "showNationalitiesAgainstStudent",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveNationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Nationalities That Are Not Mapped With Given Student",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with name or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-nationalities/mapped/show/{studentUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentNationalityPvtHandler.class,
                            beanMethod = "showMappedNationalitiesAgainstStudent",
                            operation = @Operation(
                                    operationId = "showMappedNationalitiesAgainstStudent",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveNationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Nationalities That Are Mapped With Given Student",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "s"),
                                            @Parameter(in = ParameterIn.QUERY, name = "p"),
                                            @Parameter(in = ParameterIn.QUERY, name = "d"),
                                            @Parameter(in = ParameterIn.QUERY, name = "dp", description = "Sorting can be based on all columns. Default sort is in ascending order by created_at"),
                                            @Parameter(in = ParameterIn.QUERY, name = "skw", description = "Search with name or description"),
                                            @Parameter(in = ParameterIn.QUERY, name = "status")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-nationalities/store/{studentUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentNationalityPvtHandler.class,
                            beanMethod = "store",
//                            consumes = { "APPLICATION_FORM_URLENCODED" },
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = NationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Nationalities for a Student",
                                            required = true,
                                            content = @Content(
//                                                    mediaType = "multipart/form-data",
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = NationalityDocImpl.class)
                                            )),
                                    description = "Store Nationalities Against a Given Student",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/student-nationalities/delete/{studentUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentNationalityPvtHandler.class,
                            beanMethod = "delete",
//                            consumes = { "APPLICATION_FORM_URLENCODED" },
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = NationalityEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Nationalities Against a Given Student",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "nationalityUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> studentNationalitiesPvtRoutes(StudentNationalityPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-nationalities/existing/show/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showNationalitiesAgainstStudent).filter(new ShowStudentNationalityPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-nationalities/mapped/show/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedNationalitiesAgainstStudent).filter(new ShowStudentNationalityPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-nationalities/store/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentNationalityPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-nationalities/delete/{studentUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentNationalityPvtHandlerFilter()));
    }

}
