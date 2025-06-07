package com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.equipment.EquipmentClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import org.springframework.stereotype.Service;


@Service
public class EquipmentProxyService {

    private final EquipmentClient client;

    public EquipmentProxyService(EquipmentClient client) {
        this.client = client;
    }

    public Entity getEquipment(String id) {
        return client.queryEquipmentById(new ReadModelRequestContext(), id);
    }

}
