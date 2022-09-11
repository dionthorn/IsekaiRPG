module org.dionthorn.isekairpg {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.dionthorn.isekairpg to javafx.fxml;
    exports org.dionthorn.isekairpg;
    exports org.dionthorn.isekairpg.worlds;
    opens org.dionthorn.isekairpg.worlds to javafx.fxml;
    exports org.dionthorn.isekairpg.controllers;
    opens org.dionthorn.isekairpg.controllers to javafx.fxml;
    exports org.dionthorn.isekairpg.characters;
    opens org.dionthorn.isekairpg.characters to javafx.fxml;
    exports org.dionthorn.isekairpg.utility;
    opens org.dionthorn.isekairpg.utility to javafx.fxml;
}