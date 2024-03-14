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
import tuf.webscaf.app.http.handler.TeacherSiblingLanguagePvtHandler;
import tuf.webscaf.app.http.validationFilters.teacherSiblingLanguagePvtHandler.DeleteTeacherSiblingLanguagePvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSiblingLanguagePvtHandler.ShowTeacherSiblingLanguagePvtHandlerFilter;
import tuf.webscaf.app.http.validationFilters.teacherSiblingLanguagePvtHandler.StoreTeacherSiblingLanguagePvtHandlerFilter;
import tuf.webscaf.springDocImpl.LanguageDocImpl;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class TeacherSiblingLanguagesPvtRouter {

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-sibling-languages/list/show/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.GET,
                            beanClass = TeacherSiblingLanguagePvtHandler.class,
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
                                    description = "Show the list of Language UUIDs that are mapped for given Teacher Sibling \n" +
                                            "This Route is used By Config Module in Language Handler",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID")
                                    }
                            )
                    ),
                    @RouterOperation(
                            path = "/academic/api/v1/teacher-sibling-languages/store/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.POST,
                            beanClass = TeacherSiblingLanguagePvtHandler.class,
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
                                            description = "Create Languages for a Teacher Sibling",
                                            required = true,
                                            content = @Content(
                                                    mediaType = "application/x-www-form-urlencoded",
                                                    encoding = {
                                                            @Encoding(name = "document", contentType = "application/x-www-form-urlencoded")
                                                    },
                                                    schema = @Schema(type = "object", implementation = LanguageDocImpl.class)
                                            )),
                                    description = "Store Languages Against a Given Teacher Sibling",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    ),

                    @RouterOperation(
                            path = "/academic/api/v1/teacher-sibling-languages/delete/{teacherSiblingUUID}",
                            produces = {
                                    MediaType.APPLICATION_FORM_URLENCODED_VALUE
                            },
                            method = RequestMethod.DELETE,
                            beanClass = TeacherSiblingLanguagePvtHandler.class,
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
                                    description = "Delete Languages Against a Given Teacher Sibling",
                                    parameters = {
                                            @Parameter(in = ParameterIn.PATH, name = "teacherSiblingUUID"),
                                            @Parameter(in = ParameterIn.QUERY, name = "languageUUID"),
//                                            @Parameter(in = ParameterIn.HEADER, name = "auid")
                                    }
                            )
                    )
            }
    )

    public RouterFunction<ServerResponse> teacherSiblingLanguagesPvtRoutes(TeacherSiblingLanguagePvtHandler handle) {
        return RouterFunctions.route(GET("academic/api/v1/teacher-sibling-languages/list/show/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::showList).filter(new ShowTeacherSiblingLanguagePvtHandlerFilter())
                .and(RouterFunctions.route(POST("academic/api/v1/teacher-sibling-languages/store/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::store).filter(new StoreTeacherSiblingLanguagePvtHandlerFilter()))
                .and(RouterFunctions.route(DELETE("academic/api/v1/teacher-sibling-languages/delete/{teacherSiblingUUID}").and(accept(APPLICATION_FORM_URLENCODED)), handle::delete).filter(new DeleteTeacherSiblingLanguagePvtHandlerFilter()));
    }

}
