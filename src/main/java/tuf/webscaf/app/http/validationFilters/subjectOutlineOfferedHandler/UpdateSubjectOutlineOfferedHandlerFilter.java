package tuf.webscaf.app.http.validationFilters.subjectOutlineOfferedHandler;

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
import java.util.Objects;

public class UpdateSubjectOutlineOfferedHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
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

                    if (value.containsKey("subjectOfferedUUID")) {
                        if (value.getFirst("subjectOfferedUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Subject Offered Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("subjectOfferedUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Subject Offered"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Subject Offered UUID Field Required"
                                )
                        );
                    }

                    if (value.containsKey("teacherUUID")) {
                        if (value.getFirst("teacherUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Teacher Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("teacherUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Teacher"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Teacher UUID Field Required"
                                )
                        );
                    }


                    if ((value.containsKey("subjectOutlineUUID") && (!Objects.equals(value.getFirst("subjectOutlineUUID"), "")))) {
                        if (!value.getFirst("subjectOutlineUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Subject Outline"
                                    )
                            );
                        }
                    }

                    if ((value.containsKey("SubjectObeUUID") && (!Objects.equals(value.getFirst("SubjectObeUUID"), "")))) {
                        if (!value.getFirst("SubjectObeUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Subject OBE Outline"
                                    )
                            );
                        }
                    }

                    if ((value.containsKey("subjectOutlineUUID") && (!Objects.equals(value.getFirst("subjectOutlineUUID"), "")))) {
                        if (!value.getFirst("subjectOutlineUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Subject Outline"
                                    )
                            );
                        }
                    }

                    if ((value.containsKey("subjectObeUUID") && (!Objects.equals(value.getFirst("subjectObeUUID"), "")))) {
                        if (!value.getFirst("subjectObeUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Subject OBE Outline"
                                    )
                            );
                        }
                    }


                    if (messages.isEmpty() != true) {
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
