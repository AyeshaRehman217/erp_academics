package tuf.webscaf.app.http.validationFilters.departmentRankHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tuf.webscaf.config.service.response.AppResponse;
import tuf.webscaf.config.service.response.AppResponseMessage;
import tuf.webscaf.config.service.response.CustomResponse;

import java.util.ArrayList;
import java.util.List;

public class UpdateDepartmentRankHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (!request.pathVariable("uuid").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Invalid UUID"
                                )
                        );
                    }

                    if (value.containsKey("departmentUUID")) {
                        if (value.getFirst("departmentUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Department Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("departmentUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Department"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Department Field Required"
                                )
                        );
                    }

                    if (value.containsKey("deptRankCatalogueUUID")) {
                        if (value.getFirst("deptRankCatalogueUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Department Rank Catalogue Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("deptRankCatalogueUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Department Rank Catalogue"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Department Rank Catalogue Field Required"
                                )
                        );
                    }

                    if (value.containsKey("max")) {
                        if (!value.getFirst("max").isEmpty()) {
                            if (!value.getFirst("max").matches("^[0-9]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Max must be a number"
                                        )
                                );
                            }
                        }
                    }

                    if (value.containsKey("min")) {
                        if (!value.getFirst("min").isEmpty()) {
                            if (!value.getFirst("min").matches("^[0-9]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Min must be a number"
                                        )
                                );
                            }
                        }
                    }


                    if (!messages.isEmpty()) {
                        CustomResponse appresponse = new CustomResponse();
                        return appresponse.set(
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.name(),
                                null,
                                "eng",
                                "token",
                                0L,
                                0L,
                                messages,
                                Mono.empty()
                        );
                    }

                    return next.handle(request);
                });
    }
}