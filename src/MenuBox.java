import javafx.scene.layout.Pane;

class MenuBox extends Pane {
    private static SubMenu subMenu;

    MenuBox(SubMenu subMenu) {
        MenuBox.subMenu = subMenu;
        getChildren().addAll(subMenu);
    }

    void setSubMenu(SubMenu subMenu) {
        getChildren().remove(MenuBox.subMenu);
        MenuBox.subMenu = subMenu;
        getChildren().add(MenuBox.subMenu);
    }
}