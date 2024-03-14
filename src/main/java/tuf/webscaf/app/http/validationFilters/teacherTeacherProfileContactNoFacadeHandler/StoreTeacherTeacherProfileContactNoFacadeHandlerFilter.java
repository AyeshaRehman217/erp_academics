package tuf.webscaf.app.http.validationFilters.teacherTeacherProfileContactNoFacadeHandler;

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

public class StoreTeacherTeacherProfileContactNoFacadeHandlerFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {
    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.formData()
                .flatMap(value -> {
                    List<AppResponseMessage> messages = new ArrayList<>();

                    if (value.containsKey("employeeCode")) {
                        if (value.getFirst("employeeCode").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Employee Code Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("employeeCode").matches("^[a-zA-Z0-9-]+$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Employee Code"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Employee Code Field Required"
                                )
                        );
                    }

                    if (value.containsKey("campusUUID")) {
                        if (value.getFirst("campusUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Campus Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("campusUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Campus"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Campus Field Required"
                                )
                        );
                    }

                    if (value.containsKey("reportingTo") && (!Objects.equals(value.getFirst("reportingTo"), ""))) {
                        if (!value.getFirst("reportingTo").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Reporting To UUID"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("deptRankUUID") && (!Objects.equals(value.getFirst("deptRankUUID"), ""))) {
                        if (!value.getFirst("deptRankUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid department Rank"
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

                    if (value.containsKey("firstName")) {
                        if (value.getFirst("firstName").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "First Name Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("firstName").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid First Name"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "First Name Required"
                                )
                        );
                    }

                    if (value.containsKey("lastName")) {
                        if (value.getFirst("lastName").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Last Name Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("lastName").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Last Name"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Last Name Required"
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

                    if (value.containsKey("telephoneNo")) {
                        if (!value.getFirst("telephoneNo").matches("^[\\sa-zA-Z0-9*.,!@#$&()_-]*$")) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Invalid Telephone No"
                                    )
                            );
                        }
                    }

                    if (value.containsKey("birthDate")) {
                        if (value.getFirst("birthDate").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Birth Date Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("birthDate").matches("^(0[1-9]|[1-2][0-9]|3[0-1])-(0[1-9]|1[0-2])-[0-9]{4}\\s(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Birth Date must be in dd-MM-yyyy HH:mm:ss format"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Birth Date Field Required"
                                )
                        );
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

                    if (value.containsKey("religionUUID")) {
                        if (value.getFirst("religionUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Religion Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("religionUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Religion"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Religion Field Required"
                                )
                        );
                    }

                    if (value.containsKey("sectUUID")) {
                        if (value.getFirst("sectUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Sect Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("sectUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Sect"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Sect Field Required"
                                )
                        );
                    }

                    if (value.containsKey("casteUUID")) {
                        if (value.getFirst("casteUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Caste Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("casteUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Caste"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Caste Field Required"
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

                    if (value.containsKey("maritalStatusUUID")) {
                        if (value.getFirst("maritalStatusUUID").isEmpty()) {
                            messages.add(
                                    new AppResponseMessage(
                                            AppResponse.Response.ERROR,
                                            "Marital Status Field Required"
                                    )
                            );
                        } else {
                            if (!value.getFirst("maritalStatusUUID").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                                messages.add(
                                        new AppResponseMessage(
                                                AppResponse.Response.ERROR,
                                                "Invalid Marital Status"
                                        )
                                );
                            }
                        }
                    } else {
                        messages.add(
                                new AppResponseMessage(
                                        AppResponse.Response.ERROR,
                                        "Marital Status Field Required"
                                )
                        );
                    }

                    if (!value.get("teacherContactNoDto").isEmpty()){
                        List<String> teacherContactList = value.get("teacherContactNoDto");

                        JsonNode contactNode = null;
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            contactNode = objectMapper.readTree(teacherContactList.toString());
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        assert contactNode != null;

                        for (JsonNode motherContact : contactNode) {
                            // Contact Type
                            String contactTypeUUID= motherContact.get("contactTypeUUID").toString().replaceAll("\"", "");

                            // Contact No
                            String contactNo= motherContact.get("contactNo").toString().replaceAll("\"", "");

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
