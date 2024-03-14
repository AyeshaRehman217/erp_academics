package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;


import reactor.core.publisher.Flux;
import tuf.webscaf.app.dbContext.slave.dto.SlaveCloDto;
import tuf.webscaf.app.dbContext.slave.dto.SlavePloPeoPvtDto;
import tuf.webscaf.app.dbContext.slave.dto.SlaveSubjectObeCloPvtDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveCloEntity;

import java.util.UUID;

// This interface wil extends in  Subject Offered Clos Pvt Repository
public interface SlaveCustomSubjectObeCloPvtRepository {

    Flux<SlaveCloDto> unMappedClosList(UUID subjectObeUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> unMappedClosListWithStatus(UUID subjectObeUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> showClosList(UUID subjectObeUUID, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveCloDto> showClosListWithStatus(UUID subjectObeUUID, Boolean status, String title, String name, String code, String description, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectObeCloPvtDto> index(String key, String dp, String d, Integer size, Long page);

    Flux<SlaveSubjectObeCloPvtDto> indexWithDepartment(UUID departmentUUID, String key, String dp, String d, Integer size, Long page);
}
