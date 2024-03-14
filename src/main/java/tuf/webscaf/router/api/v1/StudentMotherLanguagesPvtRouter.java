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
import tuf.webscaf.app.dbContext.master.dto.LanguageDto;
import tuf.webscaf.app.http.handler.StudentMotherLanguagePvtHandler;
import tuf.webscaf.app.http.validationFilters.studentMotherLanguagePvtHandler.DeleteStudentMotherLanguagePvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentMotherLanguagePvtHandler.ShowStudentMotherLanguagePvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.studentMotherLanguagePvtHandler.StoreStudentMotherLanguagePvtHandlerFilter;
import tuf.webscaf.springDocImpl.LanguageDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class StudentMotherLanguagesPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/student-mother-languages/list/show/{studentMotherUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = StudentMotherLanguagePvtHandler.class,
                            beanMethod = "showList",
                            operation = @Operation(
                                    operationId = "showList",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = LanguageDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Show the list of Language UUIDs that are mapped for given Student Mother \n" +
                                            "This Route is used By Config Module in Language Handler",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentMotherUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/student-mother-languages/store/{studentMotherUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = StudentMotherLanguagePvtHandler.class,
                            beanMethod = "store",
                            operation = @Operation(
                                    operationId = "store",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = LanguageDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    requestBody = @RequestBody(
                                            description = "Create Languages for a Student Mother",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = LanguageDocImpl.class)
                                            )),
                                    description = "Store Languages Against a Given Student Mother",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentMotherUUID"),
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/student-mother-languages/delete/{studentMotherUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = StudentMotherLanguagePvtHandler.class,
                            beanMethod = "delete",
                            operation = @Operation(
                                    operationId = "delete",
                                    security = {@SecurityRequirement(name = "bearer")},
                                    responses = {
                                            @ApiResponse(
                                                    responseCode = "200",
                                                    description = "successful operation",
                                                    content = @Content(schema = @Schema(
                                                            implementation = LanguageDto.class
                                                    ))
                                            ),
                                            @ApiResponse(responseCode = "404", description = "Records not found!",
                                                    content = @Content(schema = @Schema(hidden = true))
                                            )
                                    },
                                    description = "Delete Languages Against a Given Student Mother",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "studentMotherUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "languageUUID"),
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> studentMotherLanguagesPvtRoutes(StudentMotherLanguagePvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/student-mother-languages/list/show/{studentMotherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showList).filter(new ShowStudentMotherLanguagePvtHandlerFilter())
                .and(RouterFunctions.route(POST("academic/api/v1/student-mother-languages/store/{studentMotherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreStudentMotherLanguagePvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/student-mother-languages/delete/{studentMotherUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteStudentMotherLanguagePvtHandlerFilter()));
    }

}
