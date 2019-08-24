package component;

import strategy.ResultSetStrategy;
import strategy.StatementStrategy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcContext {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy strategy) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = dataSource.getConnection();
            ps = strategy.makePreparedStatement(c);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if(ps!=null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
            }
            if(c!=null) {
                try {
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

    public <T> T workWithResultSetStrategy(StatementStrategy statement, ResultSetStrategy resultSet) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = statement.makePreparedStatement(c);
            rs = ps.executeQuery();
            return resultSet.getResult(rs);
        } catch (SQLException e) {
            throw e;
        } finally {
            if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {}
            }
            if(ps!=null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
            }
            if(c!=null) {
                try {
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

    public void executeSql(final String sql, final Object... items) throws SQLException {
        workWithStatementStrategy(getStrategy(sql, items));
    }

    public <T> T executeSql(ResultSetStrategy resultSetStrategy, final String sql, final Object... items) throws SQLException {
        return workWithResultSetStrategy(getStrategy(sql, items), resultSetStrategy);
    }

    private StatementStrategy getStrategy(final String sql, final Object[] items) throws SQLException {
        return new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement(sql);
                for(int i=0; i<items.length; i++) {
                    Object item = items[i];
                    if(item instanceof Integer) {
                        ps.setInt(i+1, (Integer) item);
                    } else if (item instanceof Long) {
                        ps.setLong(i+1, (Long) item);
                    } else if (item instanceof Float) {
                        ps.setFloat(i+1, (Float) item);
                    } else if (item instanceof Double) {
                        ps.setDouble(i+1, (Double) item);
                    } else if (item instanceof Boolean) {
                        ps.setBoolean(i+1, (Boolean) item);
                    } else if (item instanceof String) {
                        ps.setString(i+1, (String) item);
                    }
                }
                return ps;
            }
        };
    }
}
