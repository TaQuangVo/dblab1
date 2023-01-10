package kth.se.dblab1.model;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Author {
    private String name;
    private String telefonNo;
    private String personId;

    public Author(String name, String telefonNo, String personId) {
        this.name = name;
        this.telefonNo = telefonNo;
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public String getTelefonNo() {
        return telefonNo;
    }

    public String getPersonId() {
        return personId;
    }

    public static List<Author> map(ResultSet result) throws SQLException {
        ResultSetMetaData metadata = result.getMetaData();
        List<Author> list = new ArrayList<>();
        while(result.next()){
            String name = null; // should check format
            String telefonNo = null;
            Integer id = null;
            String personId = null;
            for(int i = 1; i<metadata.getColumnCount(); i++){

                switch (metadata.getColumnName(i)){
                    case "name":
                        name = result.getString(i);
                        break;
                    case "telefon_nr":
                        telefonNo = result.getString(i);
                        break;
                    case "person_id":
                        personId = result.getString(i);
                        break;
                    default:
                        System.out.println("false: "+result.getObject(i));
                }
            }
            Author tempAuthor = new Author(name, telefonNo,personId);
            list.add(tempAuthor);
        }
        return list;
    }

    public static List<Author> mapFromFind(FindIterable find){
        List<Author> authors = new ArrayList<>();
        for (MongoCursor<Document> cursor = find.iterator(); cursor.hasNext();) {
            Document doc = cursor.next();
            System.out.println(doc.getString("name"));

            Author author = new Author(doc.getString("name"),doc.getString("teleNo"),doc.getString("_id"));
            authors.add(author);
        }
            return authors;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", telefonNo='" + telefonNo + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }
}
