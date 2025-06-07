package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlProvider;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BaseStatementProvider implements PreparedStatementCreator, SqlProvider {

    final List<Object> args;

    private final String tenant;

    private String sql;

    protected BaseStatementProvider(String tenant) {
        this(tenant, "");
    }

    public BaseStatementProvider(String tenant, String sql) {
        this.tenant = tenant;
        this.sql = sql;
        args = new ArrayList<>();
    }

    protected PreparedStatement setArguments(PreparedStatement statement, List<Object> args) throws SQLException {
        var i = 1;

        // Append new argument types as needed
        for (Object arg : args) {

            if (arg instanceof Boolean booleanArg) {
                statement.setBoolean(i++, booleanArg);
            } else if (arg instanceof Integer integerArg) {
                statement.setInt(i++, integerArg);
            } else if (arg instanceof Date dateArg) {
                statement.setDate(i++, dateArg);
            } else if (arg instanceof Instant instantArg) {
                statement.setTimestamp(i++, Timestamp.from(instantArg));
            } else if (arg.getClass().isArray() && arg instanceof UUID[] uuidsArg) {
                var uuids = statement.getConnection().createArrayOf("uuid", uuidsArg);
                statement.setArray(i++, uuids);
            } else if (arg instanceof String stringArg) {
                statement.setString(i++, stringArg);
            } else if (arg instanceof UUID stringArg) {
                statement.setString(i++, stringArg.toString());
            } else if (arg.getClass().isArray() && arg instanceof String[] stringArs) {
                var strings = statement.getConnection().createArrayOf("varchar", stringArs);
                statement.setArray(i++, strings);
            } else {
                // disallow anything that doesn't have a pre-defined structure (and cannot be validated at API level)
                throw new IllegalArgumentException("Unsupported argument type provided");
            }
        }

        return statement;
    }

    public BaseStatementProvider appendWithSingleArg(String query, Object arg) {
        return arg == null ? append(query) : append(query, Collections.singletonList(arg));
    }

    public BaseStatementProvider appendWithMultipleArgs(String query, Object... args) {
        return append(query, Arrays.stream(args).filter(Objects::nonNull).toList());
    }

    public BaseStatementProvider appendOptional(String query, Object arg) {
        if (arg != null) {
            return appendWithSingleArg(query, arg);
        }
        return this;
    }

    public BaseStatementProvider append(String query) {
        return append(query, Collections.emptyList());
    }

    public BaseStatementProvider append(String query, List<Object> args) {
        // prepare data
        long paramCount = query.chars().filter(ch -> ch == '?').count();
        List<Object> argList = args.stream().filter(Objects::nonNull).toList();

        // check arguments for correct length
        if (paramCount == 0 && !argList.isEmpty()) {
            throw new IllegalArgumentException("Incorrect number of arguments provided: query does not require arguments");
        } else if (argList.size() != paramCount) {
            var message = String.format("Incorrect number of arguments provided: query requires %s argument(s)", paramCount);
            throw new IllegalArgumentException(message);
        }

        // append to query and args
        sql = String.format("%s %s", sql, query);
        this.args.addAll(argList);

        // return object for chaining
        return this;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        connection.setSchema(tenant);
        var preparedStatement = connection.prepareStatement(sql);
        return setArguments(preparedStatement, args);
    }

    @Override
    public String getSql() {
        return sql;
    }
}
