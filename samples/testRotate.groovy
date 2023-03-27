import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public Node getContent() {
      Pane pane = new Pane();
      Rectangle rect = new Rectangle(0, 0, 200, 200);
      rect.setFill(Color.YELLOW);
      rect.setStyle("-fx-fill: green; -fx-rotate: 45;");
      Line line = new Line(0, 0, 200, 200);
      line.setStroke(Color.RED);
      pane.getChildren().add(rect);
      pane.getChildren().add(line);
      return pane;
}
