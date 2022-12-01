package kth.se.dblab1.model;

import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class BookDbMySqlImpl implements BooksDbInterface{
    private Connection conn = null;

    @Override
    public boolean connect(String database) throws BooksDbException {
        String user = System.getenv("env_user"); // username
        String pwd = System.getenv("env_pwd"); // password
        String endpoint = System.getenv("env_endpoint"); //link to database, database endpoint
        database = "test"; // the name of the specific database
        String server = "jdbc:mysql://" + endpoint + ":3306/" + database + "?UseClientEnc=UTF8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (conn == null)
                throw new BooksDbException("No connection");
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new BooksDbException("someting gone wtrong");
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String title) throws BooksDbException {
        return null;
    }
}
