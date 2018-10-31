package models;

public class UserMenu {

    private static int entity_gid;
    public static int menu_gid;
    public static int menu_parent_gid;
    public static String menu_name;
    public static String menu_link;
    public static int menu_displayorder;
    public static String menu_level;

    public static int getEntity_gid() {
        return entity_gid;
    }

    public static void setEntity_gid(int entity_gid) {
        UserMenu.entity_gid = entity_gid;
    }

    public static int getMenu_gid() {
        return menu_gid;
    }

    public static void setMenu_gid(int menu_gid) {
        UserMenu.menu_gid = menu_gid;
    }

    public static int getMenu_parent_gid() {
        return menu_parent_gid;
    }

    public static void setMenu_parent_gid(int menu_parent_gid) {
        UserMenu.menu_parent_gid = menu_parent_gid;
    }

    public static String getMenu_name() {
        return menu_name;
    }

    public static void setMenu_name(String menu_name) {
        UserMenu.menu_name = menu_name;
    }

    public static String getMenu_link() {
        return menu_link;
    }

    public static void setMenu_link(String menu_link) {
        UserMenu.menu_link = menu_link;
    }

    public static int getMenu_displayorder() {
        return menu_displayorder;
    }

    public static void setMenu_displayorder(int menu_displayorder) {
        UserMenu.menu_displayorder = menu_displayorder;
    }

    public static String getMenu_level() {
        return menu_level;
    }

    public static void setMenu_level(String menu_level) {
        UserMenu.menu_level = menu_level;
    }


}
