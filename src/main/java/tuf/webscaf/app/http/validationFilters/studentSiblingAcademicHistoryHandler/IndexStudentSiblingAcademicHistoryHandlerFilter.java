package tuf.webscaf.app.http.validationFilters.studentSiblingAcademicHistoryHandler;

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
import java.util.Optional;

public class IndexStudentSiblingAcademicHistoryHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        int size = request.queryParam("s").map(Integer::parseInt).orElse(10);
        int pageRequest = request.queryParam("p").map(Integer::parseInt).orElse(1);

        //Optional Query Parameter
        String studentSiblingUUID = request.queryParam("studentSiblingUUID").map(String::toString).orElse("").trim();

        int page = pageRequest - 1;

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();
                    if(page < 0) {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.INFO,
                                        "Page index must not be less than zero!"
                                )
                        );
                    }
                    else {
                        if (size < 1) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.INFO,
                                            "Page size must not be less than one!"
                                    )
                            );
                        }
                    }

                    if (!studentSiblingUUID.isEmpty()) {
                        if (!studentSiblingUUID.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Student Sibling UUID"
                                    )
                            );
                        }
                    }

                    if (messages.isEmpty() != true){
                        CustomResponse appresponse = new CustomResponse();
                        return appresponse.set(
                                HttpStatus.OK.value(),
                                HttpStatus.OK.name(),
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
