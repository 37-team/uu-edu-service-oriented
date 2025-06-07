package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourcePartitionRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Resource;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceIdsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceOptions;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.ResourceDataRepository;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.ResourceIdsFilterRepository;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.ResourcePartitionRepository;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ResourceService {

    private final ResourceDataRepository resourceDataRepository;

    private final ResourceIdsFilterRepository resourceIdsFilterRepository;

    private final ResourcePartitionRepository resourcePartitionRepository;

    public ResourceService(ResourceDataRepository resourceDataRepository,
                           ResourceIdsFilterRepository resourceIdsFilterRepository,
                           ResourcePartitionRepository resourcePartitionRepository) {
        this.resourceDataRepository = resourceDataRepository;
        this.resourceIdsFilterRepository = resourceIdsFilterRepository;
        this.resourcePartitionRepository = resourcePartitionRepository;
    }

    public List<Resource> getResources(List<UUID> personIds, ResourceOptions options, BookingsFilter bookingFilter, FetchRequest fetchUdfRequest, FetchRequest fetchSkillRequest) {
        return resourceDataRepository.findResourcesByIds(personIds, options, bookingFilter, fetchUdfRequest, fetchSkillRequest);
    }

    public List<UUID> filterResourceIds(ResourceIdsFilter filter) {
        return resourceIdsFilterRepository.filterAndLimitResourceIds(filter);
    }

    public Map<String, List<String>> getPartitions(ResourcePartitionRequestDto resourcePartitionRequestDto) {
        return this.resourcePartitionRepository.getPartitions(
                resourcePartitionRequestDto.ids().stream().map(UuidMapper::toUUID).toList(),
                resourcePartitionRequestDto.skillNames()
        );
    }
}
