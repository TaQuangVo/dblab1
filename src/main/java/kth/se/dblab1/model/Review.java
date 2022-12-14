package kth.se.dblab1.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Review {

    private int id; // should check format
    private String isbn;
    private int rate;
    private String review = "";
    private Date rate_at;
    private String rate_by;
    // TODO:
    // Add authors, as a separate class(!), and corresponding methods, to your implementation
    // as well, i.e. "private ArrayList<Author> authors;"


    public Review(int id, String isbn, String review, Date rate_at, String rate_by) {
        this.id = id;
        this.isbn = isbn;
        this.rate = rate;
        this.review = review;
        this.rate_at = rate_at;
        this.rate_by = rate_by;
    }
    public static List<Review> map(ResultSet result) throws SQLException {
        ResultSetMetaData metadata = result.getMetaData();
        List<Review> list = new ArrayList<>();
        while(result.next()){
            Integer id = null; // should check format
            String isbn = "";
            Integer rate = null;
            String review = "";
            Date rate_at = null;
            String rate_by = "";
            for(int i = 1; i<=metadata.getColumnCount(); i++){
                System.out.println(metadata.getColumnName(i));
                switch (metadata.getColumnName(i)){
                    case "id":
                        id = result.getInt(i);
                        break;
                    case "isbn":
                        isbn = result.getString(i);
                        break;
                    case "rate":
                        rate = result.getInt(i);
                        break;
                    case "review":
                        review = result.getString(i);
                        break;
                    case "rate_at":
                        rate_at = result.getDate(i);
                        break;
                    case "rate_by":
                        rate_by = result.getString(i);
                        break;
                    default:
                        System.out.println("false: "+result.getObject(i));
                }
            }
            Review tempReview = new Review(id, isbn, review, rate_at, rate_by);
            list.add(tempReview);
        }
        return list;
    }
}
