package tuf.webscaf.app.http.validationFilters.studentChildLanguagePvtHandler;

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

public class StoreStudentChildLanguagePvtHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (!request.pathVariable("studentChildUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Invalid Student Child UUID"
                                )
                        );
                    }

                    List<String> listOfLanguages = value.get("languageUUID");
                    boolean notValid = false;
                    if (value.containsKey("languageUUID")) {
                        listOfLanguages.removeIf(s -> s.equals(""));
                        if (listOfLanguages.isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Select Languages First"
                                    )
                            );
                        } else {
                            for (String val : listOfLanguages) {
                                if (!val.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                    notValid = true;
                                    break;
                                }
                            }
                            if (notValid) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Language must be valid"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Select Languages First"
                                )
                        );
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
