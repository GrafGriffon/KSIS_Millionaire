import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

class MenuItem extends StackPane {

    MenuItem(String name) {
        Rectangle bg = new Rectangle(380, 40, Color.BLACK);
        bg.setOpacity(0.45);
        Text text = new Text(name);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("BROADWEY", FontWeight.BOLD, 20));
        setAlignment(Pos.CENTER);
        getChildren().addAll(bg, text);
        FillTransition st = new FillTransition(Duration.seconds(0.5), bg);
        setOnMouseEntered(event -> {
            st.setFromValue(Color.BLACK);
            st.setToValue(Color.DARKGOLDENROD);
            st.setCycleCount(Animation.INDEFINITE);
            st.setAutoReverse(true);
            st.play();
        });
        setOnMouseExited(event -> {
            st.stop();
            bg.setFill(Color.BLACK);
        });
    }
}
