package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common;

import java.io.Serializable;
import java.util.List;

public record Equipment(String id, String externalId, List<UdfValue> udfValues) implements Serializable {

}