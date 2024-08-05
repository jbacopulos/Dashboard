module org.dashboard.dashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.desktop;

    opens org.dashboard.dashboard to javafx.fxml;
    exports org.dashboard.dashboard;
}