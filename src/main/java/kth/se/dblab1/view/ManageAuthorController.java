package kth.se.dblab1.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import kth.se.dblab1.db.BooksDbException;
import kth.se.dblab1.db.BooksDbInterface;
import kth.se.dblab1.model.Author;

import java.util.List;

public class ManageAuthorController {
    
    private final ManageAuthor root;
    private final BooksDbInterface bookDb;


    public ManageAuthorController(BooksDbInterface bookDb, ManageAuthor root) {
        this.root = root;
        this.bookDb = bookDb;
    }

    public void onAuthorNameFieldChanged(String name){
        new Thread(()-> {
            try{
                List<Author> authorResult = bookDb.getAuthorByName(name, 10);
                Platform.runLater(()->{
                    root.updateExistingAuthor(authorResult);
                });
            } catch (BooksDbException e) {
                root.showAlertAndWait(e.getMessage(), Alert.AlertType.ERROR);
            }
        }).start();
    }

    public void onCreateNewAuthor(Author author){
        new Thread(()-> {
            try{
                bookDb.insertAuthor(author);
                onAuthorNameFieldChanged(author.getName());
            } catch (BooksDbException e) {
                root.showAlertAndWait(e.getMessage(), Alert.AlertType.ERROR);
            }
        }).start();
    }
}
