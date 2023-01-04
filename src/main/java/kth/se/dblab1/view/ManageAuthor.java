package kth.se.dblab1.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import kth.se.dblab1.db.BooksDbInterface;
import kth.se.dblab1.model.Author;

import java.util.ArrayList;
import java.util.List;

public class ManageAuthor extends Dialog {
    private List<Author> authorToAdd;

    private ObservableList<HBox> authorToAddObList;
    private ObservableList<HBox> existingAuthorObList;


        HBox createHBox(String labelText, String buttonText, Author author) {

            Label label = new Label();
            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            Button button = new Button();
            button.setText(buttonText);
            EventHandler<ActionEvent> addfunc = new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    boolean exist = false;
                    for(Author a : authorToAdd){
                        if(a.getPersonId().equals(author.getPersonId())){
                            exist = true;
                            break;
                        }
                    }
                    if(!exist){
                        authorToAdd.add(author);
                        updateToAddAuthor(authorToAdd);
                    }
                }
            };
            EventHandler<ActionEvent> removefunc = new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    for(Author a : authorToAdd){
                        if(a.getPersonId().equals(author.getPersonId())){
                            authorToAdd.remove(author);
                            updateToAddAuthor(authorToAdd);
                            break;
                        }
                    }
                }
            };
            if(buttonText.equals("add"))
                button.setOnAction(addfunc);
            else
                button.setOnAction(removefunc);


            HBox hb = new HBox();
            hb.getChildren().addAll(label, button);
            return hb;
        }

    public ManageAuthor(BooksDbInterface db, List<Author> authorToAdd){
        super();
        this.setTitle("Manage Authors");
        this.setHeaderText("Look, a Custom Login Dialog");
        this.authorToAdd = authorToAdd;
        authorToAddObList = FXCollections.observableArrayList();
        existingAuthorObList = FXCollections.observableArrayList();

        ManageAuthorController ctl = new ManageAuthorController(db, this);

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,loginButtonType);

        //create root grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //list view of author to add to the book
        ListView<HBox> authorToAddListView = new ListView<HBox>();
        authorToAddListView.setPrefHeight(5 * 24 + 2);
        List<HBox> list = new ArrayList<>();
        for (Author a : authorToAdd) {
            list.add(this.createHBox(a.getName() + "("+a.getPersonId()+")", "remove", a));
        }
        authorToAddObList = FXCollections.observableList(list);
        authorToAddListView.setItems(authorToAddObList);

        grid.add(new Label("Author To Add:"), 0, 0);
        grid.add(authorToAddListView, 1, 0);

        // author detail inputs
        TextField authorNameTf = new TextField();
        authorNameTf.setPromptText("Author Name");
        authorNameTf.textProperty().addListener(((observableValue, s, t1) -> {
            ctl.onAuthorNameFieldChanged(t1);
        }));
        TextField authorIDTf = new TextField();
        authorIDTf.setPromptText("Author ID");
        TextField authorTeleTf = new TextField();
        authorTeleTf.setPromptText("author Tele");
        Button createAuthorBtn = new Button("Create Author");

        createAuthorBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(true){
                    Author newAuthor = new Author(authorNameTf.getText(), authorTeleTf.getText(), authorIDTf.getText());
                    ctl.onCreateNewAuthor(newAuthor);
                }
                else{
                    showAlertAndWait("Fill in all required filed", Alert.AlertType.WARNING);
                }
            }
        });

        HBox authorInputsVbox = new HBox();
        authorInputsVbox.getChildren().addAll(authorNameTf,authorIDTf,authorTeleTf,createAuthorBtn);

        grid.add(new Label("Author Input:"), 0, 1);
        grid.add(authorInputsVbox, 1, 1);

        // existing authors
        ListView<HBox> existingAuthorsListView = new ListView<>();
        existingAuthorsListView.setPrefHeight(5 * 24 + 2);
        existingAuthorsListView.setItems(existingAuthorObList);

        grid.add(new Label("Existing author:"), 0, 2);
        grid.add(existingAuthorsListView, 1, 2);

        this.getDialogPane().setContent(grid);

        this.setResultConverter(button -> {
            if(button == loginButtonType){
                return authorToAdd;
            }
            return null;
        });
    }

    public void updateExistingAuthor(List<Author> authors){
        List<HBox> list = new ArrayList<>();
        for (Author a : authors) {
            list.add(createHBox(a.getName() + "("+a.getPersonId()+")", "add", a));
        }
        existingAuthorObList.clear();
        existingAuthorObList.addAll(list);
    }
    public void updateToAddAuthor(List<Author> authors){
        List<HBox> list = new ArrayList<>();
        for (Author a : authors) {
            list.add(createHBox(a.getName() + "("+a.getPersonId()+")", "remove", a));
        }
        authorToAddObList.clear();
        authorToAddObList.addAll(list);
    }

    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }
}