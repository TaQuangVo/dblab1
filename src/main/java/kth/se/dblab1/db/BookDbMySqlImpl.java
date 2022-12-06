package kth.se.dblab1.db;

import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;

import java.sql.*;
import java.util.List;

public class BookDbMySqlImpl implements BooksDbInterface{
    private Connection conn = null;

    @Override
    public void connect(String database) throws BooksDbException {
        String user = System.getenv("env_user"); // username
        String pwd = System.getenv("env_pwd"); // password
        String endpoint = System.getenv("env_endpoint"); //link to database, database endpoint
        String server = "jdbc:mysql://" + endpoint + ":3306/" + database + "?UseClientEnc=UTF8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    @Override
    public void disconnect() throws BooksDbException {
        try {
            if (conn == null)
                throw new BooksDbException("No connection");
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            throw new BooksDbException("someting gone wtrong");
        }
    }

    @Override
    public List<Book> searchBooksByTitle(String title) throws BooksDbException {
        String queryString = "Select * from book where title like ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, title+"%");
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }

    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn) throws BooksDbException {
        String queryString = "Select * from book where isbn like ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, isbn+"%");
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Book> searchBooksByAuthorName(String authorName) throws BooksDbException {
        String queryString = "select * from book where id in (select book_id from writen_by where author_id = (select id from author where name like ?));";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, authorName+"%");
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            List<Book> bs = Book.map(rs);
            return bs;
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Book> searchBooksByRate(int rate) throws BooksDbException {
        String queryString = "select * from book where id in (select book_id from rate where rate = ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setInt(1, rate);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Book> searchBooksByGenre(String genre) throws BooksDbException {
        String queryString = "select * from book where id in (select book_id from genre where genre_title like ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1,genre+"%");
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Author> getAuthorByPersonId(String personId) throws BooksDbException{
        String queryString = "select * from author where person_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, personId);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Author.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public int insetWritenBy(Book book, Author author) throws BooksDbException {
        String queryString = "insert into writen_by(isbn,person_id) values (?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, author.getPersonId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }

    }

    public int insertAuthor(Author author) throws BooksDbException {
        String queryString = "insert into author(name,telefon_nr,person_id) values (?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, author.getName());
            pstmt.setString(2, author.getTelefonNo());
            pstmt.setString(3, author.getPersonId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public int insertGenre(Book book, String genreTitle) throws BooksDbException {
        String queryString = "insert into genre(isbn, genre_title) values (?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, genreTitle);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public int insertBook(Book book) throws BooksDbException {
        String queryString = "insert into book(isbn, title, published, story_line) values(?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1,book.getIsbn());
            pstmt.setString(2,book.getTitle());
            pstmt.setDate(3, book.getPublished());
            pstmt.setString(4,book.getStoryLine());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public void insertBookFullDetail(Book book, List<Author> authors, List<String> genre) throws BooksDbException, SQLException {
        String insertBookQuery = "insert into book(isbn, title, published, story_line) values(?,?,?,?)";
        try{
            conn.setAutoCommit(false);

            //insert book
            insertBook(book);

            //insert author that are not exist
            for (Author author : authors){
                List<Author> auList = getAuthorByPersonId(author.getPersonId());
                if (auList.isEmpty()) {
                    insertAuthor(author);
                }
            }

            for (Author author : authors){
                insetWritenBy(book,author);
            }

            for (String g : genre){
                insertGenre(book,g);
            }

            conn.commit();
        } catch (BooksDbException e) {
            if(conn != null) conn.rollback();
            System.out.println(e.getMessage());
            throw new BooksDbException(e.getMessage());
        } finally {
            if(conn != null) conn.setAutoCommit(true);
        }
    }

    public int deleteByIsbn(String isbn) throws BooksDbException {
        String queryString = "delete from book where isbn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, isbn);
            return pstmt.executeUpdate();
            // Get the attribute names
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }

    }

    public int updateBookInfo(Book book) throws BooksDbException {
        String queryString = "update book set title = ?, published = ?, story_line = ? where isbn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getTitle());
            pstmt.setDate(2,book.getPublished());
            pstmt.setString(3,book.getStoryLine());
            pstmt.setString(4,book.getIsbn());
            return pstmt.executeUpdate();
            // Get the attribute names
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new BooksDbException(e.getMessage());
        }

    }


}
