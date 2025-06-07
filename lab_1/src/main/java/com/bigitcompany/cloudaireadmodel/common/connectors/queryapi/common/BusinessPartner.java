package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common;

import java.io.Serializable;
import java.util.List;

public record BusinessPartner(String id, String externalId, List<UdfValue> udfValues) implements Serializable {

}