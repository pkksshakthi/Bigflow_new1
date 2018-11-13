package models;

public class UserDetails {
    private static int user_id;
    private static String user_name;
    private static String user_type;
    private static String user_code;
    private static String today_date;
    private static int entity_gid;


    public static void setUser_id(int user_id) {
        UserDetails.user_id = user_id;
    }

    public static void setUser_name(String user_name) {
        UserDetails.user_name = user_name;
    }

    public static void setUser_type(String user_type) {
        UserDetails.user_type = user_type;
    }

    public static void setUser_code(String user_code) {
        UserDetails.user_code = user_code;
    }

    public static void setToday_date(String today_date) {
        UserDetails.today_date = today_date;
    }

    public static void setEntity_gid(int entity_gid) {
        UserDetails.entity_gid = entity_gid;
    }


    public static int getUser_id() {
        return user_id;
    }

    public static String getUser_name() {
        return user_name;
    }

    public static String getUser_type() {
        return user_type;
    }

    public static String getUser_code() {
        return user_code;
    }

    public static String getToday_date() {
        return today_date;
    }

    public static int getEntity_gid() {
        return entity_gid;
    }

}
