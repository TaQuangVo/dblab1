module kth.se.dblab1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;


    opens kth.se.dblab1 to javafx.fxml;
    exports kth.se.dblab1;
}