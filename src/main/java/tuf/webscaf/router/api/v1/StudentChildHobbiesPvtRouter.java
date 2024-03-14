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
import tuf.webscaf.app.dbContext.master.entity.HobbyEntity;
import tuf.webscaf.app.dbContext.slave.entity.SlaveHobbyEntity;
import tuf.webscaf.app.http.handler.StudentChildHobbyPvtHandler;
import tuf.webscaf.app.http.validationFilters.studentChildHobbyPvtHandler.DeleteStudentChildHobbyPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentChildHobbyPvtHandler.ShowStudentChildHobbyPvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentChildHobbyPvtHandler.StoreStudentChildHobbyPvtHandlerFilter;
import tuf.webscaf.springDocImpl.HobbyDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class StudentChildHobbiesPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-child-hobbies/existing/show/{studentChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentChildHobbyPvtHandler.class,
                            beanMethod = "showHobbiesAgainstStudentChild",
                            operation = @Operation(
                                    operationId = "showHobbiesAgainstStudentChild",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveHobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Hobbies That Are Not Mapped With Given Student Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentChildUUID"),
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
                            path = "/academic/api/v1/student-child-hobbies/mapped/show/{studentChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentChildHobbyPvtHandler.class,
                            beanMethod = "showMappedHobbiesAgainstStudentChild",
                            operation = @Operation(
                                    operationId = "showMappedHobbiesAgainstStudentChild",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = SlaveHobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show Hobbies That Are Mapped With Given Student Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentChildUUID"),
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
                            path = "/academic/api/v1/student-child-hobbies/store/{studentChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentChildHobbyPvtHandler.class,
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
                                                            implementation = HobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Hobbies for a Student Child",
                                            required = true,
                                            content = @Content(
//                                                    mediaType = "multipart/form-data",
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = HobbyDocImpl.class)
                                            )),
                                    description = "Store Hobbies Against a Given Student Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentChildUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/student-child-hobbies/delete/{studentChildUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentChildHobbyPvtHandler.class,
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
                                                            implementation = HobbyEntity.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Hobbies Against a Given Student Child",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentChildUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "hobbyUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> studentChildHobbiesPvtRoutes(StudentChildHobbyPvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-child-hobbies/existing/show/{studentChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showHobbiesAgainstStudentChild).filter(new ShowStudentChildHobbyPvtHandlerFilter())
                .and(RouterFunctions.route(GET("academic/api/v1/student-child-hobbies/mapped/show/{studentChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showMappedHobbiesAgainstStudentChild).filter(new ShowStudentChildHobbyPvtHandlerFilter()))
                .and(RouterFunctions.route(POST("academic/api/v1/student-child-hobbies/store/{studentChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentChildHobbyPvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-child-hobbies/delete/{studentChildUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentChildHobbyPvtHandlerFilter()));
    }

}
