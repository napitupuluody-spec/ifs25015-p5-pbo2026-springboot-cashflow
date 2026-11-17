package org.delcom.ifs25015springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.sql.DataSource;
import org.delcom.ifs25015springbootcashflow.domain.entity.Role;
import org.delcom.ifs25015springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25015springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

class JdbcGapTest {

    @Test
    void parseRoleIsTolerant() {
        assertEquals(Role.USER, JdbcUserRepository.parseRole(null));
        assertEquals(Role.ADMIN, JdbcUserRepository.parseRole(" role_admin "));
        assertEquals(Role.ADMIN, JdbcUserRepository.parseRole("admin"));
        assertEquals(Role.USER, JdbcUserRepository.parseRole("ngawur"));
    }

    @Test
    void parseTypeIsTolerant() {
        assertEquals(TransactionType.INCOME, JdbcTransactionRepository.parseType(null));
        assertEquals(TransactionType.EXPENSE, JdbcTransactionRepository.parseType(" expense "));
        assertEquals(TransactionType.INCOME, JdbcTransactionRepository.parseType("ngawur"));
    }

    @Test
    void rowWithoutCreatedAtGivesNullTimestamp() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection c = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(ds.getConnection()).thenReturn(c);
        when(c.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getTimestamp("created_at")).thenReturn(null);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getInt("user_id")).thenReturn(2);
        when(rs.getString("description")).thenReturn("x");
        when(rs.getLong("amount")).thenReturn(10L);
        when(rs.getString("type")).thenReturn("INCOME");
        when(rs.getString("proof_path")).thenReturn(null);

        List<Transaction> rows = new JdbcTransactionRepository(ds).findByUserId(2);
        assertEquals(1, rows.size());
        assertNull(rows.get(0).createdAt());
    }
}
