import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public Node getContent() {
   Pane pane = new Pane();
   String path = context.getCSS("pane.css");
   pane.getStylesheets().add(path);
   pane.getStyleClass().add("paneStyle");
   return pane;
}
