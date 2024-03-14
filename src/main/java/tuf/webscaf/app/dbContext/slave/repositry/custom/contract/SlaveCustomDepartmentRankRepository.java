package tuf.webscaf.app.dbContext.slave.repositry.custom.contract;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tuf.webscaf.app.dbContext.slave.dto.SlaveDepartmentRankDto;
import tuf.webscaf.app.dbContext.slave.entity.SlaveDepartmentRankCatalogueEntity;

import java.util.UUID;

public interface SlaveCustomDepartmentRankRepository {

//    Flux<SlaveDepartmentRankCatalogueEntity> showUnmappedDepartmentRankCatalogueList(UUID departmentUUID, String catalogueName, String catalogueDescription, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveDepartmentRankCatalogueEntity> showUnmappedDepartmentRankCatalogueListWithStatus(UUID departmentUUID, String catalogueName, String catalogueDescription, Boolean status, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveDepartmentRankCatalogueEntity> showMappedDepartmentRankCatalogueList(UUID departmentUUID, String catalogueName, String catalogueDescription, String dp, String d, Integer size, Long page);
//
//    Flux<SlaveDepartmentRankCatalogueEntity> showMappedDepartmentRankCatalogueListWithStatus(UUID departmentUUID, String catalogueName, String catalogueDescription, Boolean status, String dp, String d, Integer size, Long page);

    Flux<SlaveDepartmentRankDto> showAllRecordsWithName(String name, String dp, String d, Integer size, Long page);

    Flux<SlaveDepartmentRankDto> showAllRecordsWithNameAndManyFilter(String name, Boolean many, String dp, String d, Integer size, Long page);

    Mono<SlaveDepartmentRankDto> showRecordWithName(UUID uuid);
}
