package kth.se.dblab1.db;

import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;
import kth.se.dblab1.model.Review;

import java.sql.*;
import java.util.ArrayList;
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
    public List<Book> searchBooksByTitle(String title, int limit) throws BooksDbException {
        String queryString = "Select * from book where title like ?  limit ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, title+"%");
            pstmt.setInt(2,limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }

    }

    @Override
    public List<Book> searchBooksByIsbn(String isbn, int limit) throws BooksDbException {
        String queryString = "Select * from book where isbn like ? limit ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, isbn+"%");
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Book> searchBooksByAuthorName(String authorName, int limit) throws BooksDbException {
        String queryString = "select * from book where isbn in (select isbn from writen_by where person_id in (select person_id from author where name like ?)) limit ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, authorName+"%");
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            List<Book> bs = Book.map(rs);
            return bs;
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Book> searchBooksByRate(int rate, int limit) throws BooksDbException {
        String queryString = "select * from book where id in (select book_id from rate where rate = ?) limit ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setInt(1, rate);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Book> searchBooksByGenre(String genre, int limit) throws BooksDbException {
        String queryString = "select * from book where id in (select book_id from genre where genre_title like ?) limit +?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1,genre+"%");
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Book.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Author> getAuthorByPersonId(String personId, int limit) throws BooksDbException{
        String queryString = "select * from author where person_id = ? limit ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, personId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Author.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public  List<Author> getBookAuthors(Book book, int limit) throws BooksDbException {
        String queryString = "select * from author where person_id in(select person_id from writen_by where isbn = ?) limit ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Author.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public  List<String> getBookGenres(Book book, int limit) throws BooksDbException {
        String queryString = "select genre_title from genre where isbn = ? limit ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            List<String> rt = new ArrayList<>();
            while(rs.next())
                rt.add(rs.getString(1));

            return rt;
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

    public int insertReview(Book book, int rate, String review) throws BooksDbException {
        String queryString = "insert into rate(isbn,rate,review) values (?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setInt(2, rate);
            pstmt.setString(3, review);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public List<Review> getBookReview(Book book) throws BooksDbException {
        String queryString = "select * from rate where isbn = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, book.getIsbn());
            ResultSet rs = pstmt.executeQuery();
            return Review.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public int deleteGenreFromBook(Book book, String genreTitle) throws BooksDbException {
        String queryString = "DELETE FROM genre WHERE isbn = ? and genre_title = ?;";
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
        try{
            conn.setAutoCommit(false);

            //insert book
            insertBook(book);

            //insert author that are not exist
            for (Author author : authors){
                List<Author> auList = getAuthorByPersonId(author.getPersonId(), 100);
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

    public int deleteBookByIsbn(String isbn) throws BooksDbException {
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

    public List<Author> getAuthorByName(String name, int limit) throws BooksDbException{
        String queryString = "select * from author where name like ? limit ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, name+"%");
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            // Get the attribute names
            return Author.map(rs);
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    public int deleteAuthorfromBook(Author a, Book b) throws BooksDbException{
        String queryString = "DELETE FROM writen_by WHERE isbn = ? and person_id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(queryString);) {
            pstmt.setString(1, b.getIsbn());
            pstmt.setString(2, a.getPersonId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException(e.getMessage());
        }
    }

    //DELETE FROM writen_by WHERE isbn = "44444444" and person_id = "2000421343";

    public void updateBookFullDetail(Book book, List<Author> updatedAuthors , List<String> updatedGenre) throws BooksDbException, SQLException {
        try{
            conn.setAutoCommit(false);

            updateBookInfo(book);

            List<Author> originAuthors = getBookAuthors(book, 100);
            List<String> originGenre = getBookGenres(book, 100);

            for(Author updated : updatedAuthors) {
                boolean exist = false;
                for (Author origin : originAuthors) {
                    if (updated.getPersonId().equals(origin.getPersonId())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    insetWritenBy(book, updated);
                }
            }

            for(Author origin : originAuthors) {
                boolean exist = false;
                for (Author updated : updatedAuthors) {
                    if (updated.getPersonId().equals(origin.getPersonId())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    deleteAuthorfromBook(origin, book);
                }
            }

            for(String updated : updatedGenre) {
                boolean exist = false;
                for (String origin : originGenre) {
                    if (updated.equals(origin)){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    insertGenre(book, updated);
                }
            }

            for(String origin : originGenre) {
                boolean exist = false;
                for (String updated : updatedGenre) {
                    if (updated.equals(origin)){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    deleteGenreFromBook(book, origin);
                }
            }
            conn.commit();
        } catch (BooksDbException | SQLException e) {
            if(conn != null) conn.rollback();
            System.out.println(e.getMessage());
            throw new BooksDbException(e.getMessage());
        } finally {
            if(conn != null) conn.setAutoCommit(true);
        }
    }


}
