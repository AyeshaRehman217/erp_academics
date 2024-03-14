package tuf.webscaf.app.http.validationFilters.teacherSpouseProfileHandler;

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

public class ShowTeacherSpouseProfileAgainstTeacherAndSpouseHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        String teacherSpouseUUID = request.queryParam("teacherSpouseUUID").map(String::toString).orElse("");

        String teacherUUID = request.queryParam("teacherUUID").map(String::toString).orElse("");

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (!request.pathVariable("uuid").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Invalid Teacher Spouse Profile UUID"
                                )
                        );
                    }

                    if (teacherSpouseUUID.isEmpty()) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Teacher Spouse Field Required"
                                )
                        );
                    } else {
                        if (!teacherSpouseUUID.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Teacher Spouse UUID"
                                    )
                            );
                        }
                    }


                    if (teacherUUID.isEmpty()) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Teacher Field Required"
                                )
                        );
                    } else {
                        if (!teacherUUID.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Teacher UUID"
                                    )
                            );
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
