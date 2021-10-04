import javafx.scene.layout.VBox;

class SubMenu extends VBox {
    SubMenu(MenuItem... items) {
        setSpacing(15);
        setTranslateY(245);
        setTranslateX(740);
        for (MenuItem item : items) {
            getChildren().addAll(item);
        }
    }
}