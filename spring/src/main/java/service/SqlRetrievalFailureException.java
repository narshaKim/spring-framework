package service;

import sql.SqlNotFoundException;

public class SqlRetrievalFailureException extends RuntimeException {

    public SqlRetrievalFailureException(String message) {
        super(message);
    }

    public SqlRetrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlRetrievalFailureException(SqlNotFoundException e) {super(e.getMessage(), e.getCause());}
}
