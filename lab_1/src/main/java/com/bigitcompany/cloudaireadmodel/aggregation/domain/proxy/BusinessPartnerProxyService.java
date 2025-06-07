package com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.businesspartner.BusinessPartnerClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import org.springframework.stereotype.Service;


@Service
public class BusinessPartnerProxyService {

    private final BusinessPartnerClient businessPartnerClient;

    public BusinessPartnerProxyService(BusinessPartnerClient businessPartnerClient) {
        this.businessPartnerClient = businessPartnerClient;
    }
    public Entity getBusinessPartner(String businessPartnerId) {
        return businessPartnerClient.queryBusinessPartnerById(new ReadModelRequestContext(), businessPartnerId);
    }

}
