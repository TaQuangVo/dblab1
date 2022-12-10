package kth.se.dblab1.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kth.se.dblab1.model.Author;
import kth.se.dblab1.model.Book;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BookCustom extends Dialog {
    List<Author> authorToAdd = new ArrayList<>();
    Controller controller;

    public BookCustom(Controller c){
        super();
        controller = c;
        // Create the custom dialog.

        this.setTitle("Login Dialog");
        this.setHeaderText("Look, a Custom Login Dialog");

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,loginButtonType);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        DatePicker publishedField = new DatePicker(LocalDate.now());
        publishedField.setPromptText("Published");
        TextField storyLineField = new TextField();
        storyLineField.setPromptText("Storyline");
        TextField genreField = new TextField();
        genreField.setPromptText("Genres(Coma separated)");

        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Published:"), 0, 2);
        grid.add(publishedField, 1, 2);
        grid.add(new Label("Storyline:"), 0, 3);
        grid.add(storyLineField, 1, 3);
        grid.add(new Label("Genres:"), 0, 4);
        grid.add(genreField, 1, 4);


        Button manageAuthorBtn = new Button("Manage Authors");
       manageAuthorBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                ManageAuthor ma = new ManageAuthor();
                ma.showAndWait();
            }
        });
        grid.add(new Label("Author:"), 0, 5);
        Label authorsText = new Label("duklas, omelet");
        grid.add(authorsText,1,5);
        grid.add(manageAuthorBtn, 1, 6);


        this.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                Date d = Date.valueOf(publishedField.getEditor().getText());
                Book newBook = new Book(isbnField.getText(),titleField.getText(), d);
                newBook.setStoryLine(storyLineField.getText());

                String genreString = genreField.getText();
                List<String> genre = Arrays.asList(genreString.split(","));

                controller.onCreateBook(newBook, authorToAdd, genre);

                return titleField.getText();
            }
            return null;
        });
    }


}