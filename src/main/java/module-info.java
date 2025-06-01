module com.physicalapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    exports com.physicalapp;
    exports com.physicalapp.view;
    exports com.physicalapp.model;
    exports com.physicalapp.controller;
    exports com.physicalapp.simulation;
    
    opens com.physicalapp to javafx.fxml;
    opens com.physicalapp.view to javafx.fxml;
    opens com.physicalapp.controller to javafx.fxml;
}