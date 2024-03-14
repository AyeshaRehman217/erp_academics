package tuf.webscaf.app.http.validationFilters.teacherSiblingTeacherSiblingProfileContactNoFacadeHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class StoreTeacherSiblingTeacherSiblingProfileContactNoFacadeHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

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
                                        "Teacher Field Required"
                                )
                        );
                    }

                    if ((value.containsKey("studentUUID") && (!Objects.equals(value.getFirst("studentUUID"), "")))) {
                        if (!value.getFirst("studentUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Student UUID"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("image")) {
                        if (value.getFirst("image").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Image Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("image").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Image"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Image Field Required"
                                )
                        );
                    }

                    if (value.containsKey("name")) {
                        if (value.getFirst("name").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Name Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("name").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Name"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Name Required"
                                )
                        );
                    }

                    if (value.containsKey("nic")) {
                        if (value.getFirst("nic").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "NIC Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("nic").matches("^[0-9]{5}-[0-9]{7}-[0-9]$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid NIC"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "NIC Required"
                                )
                        );
                    }

                    if (value.containsKey("age")) {
                        if (value.getFirst("age").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Age Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("age").matches("^[0-9]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Age"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Age Required"
                                )
                        );
                    }

                    if (value.containsKey("officialTel")) {
                        if (!value.getFirst("officialTel").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Official Telephone"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("cityUUID")) {
                        if (value.getFirst("cityUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "City Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("cityUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid City"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "City Field Required"
                                )
                        );
                    }

                    if (value.containsKey("stateUUID")) {
                        if (value.getFirst("stateUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "State Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("stateUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid State"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "State Field Required"
                                )
                        );
                    }

                    if (value.containsKey("countryUUID")) {
                        if (value.getFirst("countryUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Country Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("countryUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Country"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Country Field Required"
                                )
                        );
                    }

                    if (value.containsKey("genderUUID")) {
                        if (value.getFirst("genderUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Gender Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("genderUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Gender"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Gender Field Required"
                                )
                        );
                    }

                    if (value.containsKey("email")) {
                        if (!value.getFirst("email").matches("^(.+)@(.+)$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Email"
                                    )
                            );
                        }
                    }

                    if (!value.get("teacherSiblingContactNoDto").isEmpty()){
                        List<String> teacherSiblingContactList = value.get("teacherSiblingContactNoDto");

                        JsonNode contactNode = null;
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            contactNode = objectMapper.readTree(teacherSiblingContactList.toString());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        assert contactNode != null;

                        for (JsonNode siblingContact : contactNode) {
                            // Contact Type
                            String contactTypeUUID= siblingContact.get("contactTypeUUID").toString().replaceAll("\"", "");

                            // Contact No
                            String contactNo= siblingContact.get("contactNo").toString().replaceAll("\"", "");

                            if (!contactTypeUUID.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Contact Type"
                                        )
                                );
                                break;
                            }

                            if (!contactNo.matches("^((\\+92)?(0092)?(92)?(0)?)(3)([0-9]{9})$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Contact No"
                                        )
                                );
                                break;
                            }
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
