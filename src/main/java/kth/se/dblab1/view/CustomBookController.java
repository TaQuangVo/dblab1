package kth.se.dblab1.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import kth.se.dblab1.db.BooksDbInterface;
import kth.se.dblab1.db.InputExeption;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;

import java.util.List;

public class CustomBookController {
    private final BookCustom root;
    private final BooksDbInterface booksDb;

    public CustomBookController(BookCustom root, BooksDbInterface db) {
        this.root = root;
        this.booksDb = db;
    }

    public void onCreateBook(Book book, List<Author> authors , List<String> genre) {
        new Thread(() -> {
            try {
                this.booksDb.insertBookFullDetail(book, authors, genre);
            } catch (Exception e) {
                Platform.runLater(()->{
                    root.showAlertAndWait(e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }

    public void onUpdateBook(Book book, List<Author> authors , List<String> genre){
        new Thread(() -> {
            try {
                this.booksDb.updateBookFullDetail(book, authors, genre);
            } catch (Exception e) {
                Platform.runLater(()->{
                    root.showAlertAndWait(e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();
    }


}
