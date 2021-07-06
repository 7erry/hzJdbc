package com.example.jdbc;

import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.render.SelectRenderContext;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

public class CustomJdbcDialectProvider implements DialectResolver.JdbcDialectProvider {

    @Override
    public Optional<Dialect> getDialect(JdbcOperations operations) {
        return Optional.ofNullable(
                operations.execute((ConnectionCallback<Dialect>) CustomJdbcDialectProvider::getDialect));
    }

    private static Dialect getDialect(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ROOT);
        if (name.contains("hazelcast")) {
            return HazelcastDialect.INSTANCE;
        }
        return null;
    }

    private static class HazelcastDialect implements Dialect {
        private static final HazelcastDialect INSTANCE = new HazelcastDialect();

        @Override
        public LimitClause limit() {
            return new LimitClause() {
                public String getLimit(long limit) {
                    return "LIMIT " + limit;
                }
                public String getOffset(long offset) {
                    return "OFFSET " + offset;
                }
                public String getLimitOffset(long limit, long offset) {
                    return String.format("LIMIT %d OFFSET %d", limit, offset);
                }
                public Position getClausePosition() {
                    return Position.AFTER_ORDER_BY;
                }
            };
        }

        @Override
        public LockClause lock() {
            return null;
        }

        @Override
        public SelectRenderContext getSelectContext() {
            return new SelectRenderContext() {};
        }
    }
}