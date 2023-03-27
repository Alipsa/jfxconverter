import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public Node getContent() {
   Pane pane = new Pane();
   String path = context.getCSS("dropShadow.css");
   pane.getStylesheets().add(path);
   Rectangle rect = new Rectangle(0, 0, 200, 200);
   rect.setFill(Color.YELLOW);
   rect.getStyleClass().add("dropshadow");
   pane.getChildren().add(rect);
   return pane;
}
