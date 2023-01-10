package kth.se.dblab1.model;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class Book {

    private String isbn; // should check format
    private String title;
    private Date published;
    private String storyLine = "";
    private List<Author> authors;
    private List<String> genre;
    // TODO: 
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"
    
    public Book(String isbn, String title, Date published) {
        this.isbn = isbn;
        this.title = title;
        this.published = published;
    }

    public Book(String isbn, String title, Date published, List<Author> auths, List<String> genre) {
        this.isbn = isbn;
        this.title = title;
        this.published = published;
        this.authors = auths;
        this.genre = genre;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
    public String getStoryLine() { return storyLine; }
    
    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }

    public static List<Book> map(ResultSet result) throws SQLException {
        ResultSetMetaData metadata = result.getMetaData();
        List<Book> list = new ArrayList<>();
        while(result.next()){
            String t_isbn = null; // should check format
            String t_title = null;
            Date t_published = null;
            String t_storyLine = null;
            for(int i = 1; i<=metadata.getColumnCount(); i++){
                System.out.println(metadata.getColumnName(i));
                switch (metadata.getColumnName(i)){
                    case "isbn":
                        t_isbn = result.getString(i);
                        break;
                    case "title":
                        t_title = result.getString(i);
                        break;
                    case "published":
                        t_published = (Date) result.getDate(i);
                        break;
                    case "story_line":
                        t_storyLine = result.getString(i);
                        break;
                    default:
                        System.out.println("false: "+result.getObject(i));
                }
            }
            Book tempBook = new Book(t_isbn,t_title,t_published);
            tempBook.setStoryLine(t_storyLine);
            list.add(tempBook);
        }
        return list;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<String> getGenre() {
        return genre;
    }

    public static List<Book> mapFromFind(FindIterable find){
        List<Book> books = new ArrayList<>();
        System.out.println(find);
        for (MongoCursor<Document> cursor = find.iterator(); cursor.hasNext();) {
            Document doc = cursor.next();
            System.out.println(doc.getString("_id"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String d = formatter.format(doc.getDate("published"));

            Book book = new Book(doc.getString("_id"),doc.getString("title"), Date.valueOf(d), (List<Author>)doc.get("authors"), (List<String>)doc.get("genre"));
            book.setStoryLine(doc.getString("storyLine"));
            books.add(book);
        }
        return books;
    }
    @Override
    public String toString() {
        return title + ", " + isbn + ", " + published.toString() + ", " + storyLine;
    }
}
