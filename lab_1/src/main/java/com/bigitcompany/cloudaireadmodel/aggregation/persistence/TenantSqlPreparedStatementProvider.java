package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import java.util.Collections;

public class TenantSqlPreparedStatementProvider extends BaseStatementProvider {

    public TenantSqlPreparedStatementProvider(String sql, String tenant) {
        super(tenant, sql);
    }

    public TenantSqlPreparedStatementProvider(String tenant) {
        super(tenant);
    }

    public TenantSqlPreparedStatementProvider setArg(Object arg) {
        if (arg != null) {
            args.addAll(Collections.singletonList(arg));
        }
        return this;
    }

    public TenantSqlPreparedStatementProvider addSingleArg(Object arg) {
        args.add(arg);
        return this;
    }

    public TenantSqlPreparedStatementProvider appendSingleArgWithQuery(String query, Object arg) {
        appendWithSingleArg(query, arg);
        return this;
    }

    public TenantSqlPreparedStatementProvider appendOptionalArg(String query, Object arg) {
        appendOptional(query, arg);
        return this;
    }
}
