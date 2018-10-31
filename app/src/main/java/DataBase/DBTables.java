package DataBase;

import constant.Constant;

public class DBTables {

    public static final String CREATE_TABLE_Menu = "CREATE TABLE gal_mst_tmenu ( " +
            "  menu_gid int  NOT NULL," +
            "  menu_parent_gid integer NOT NULL," +
            "  menu_name varchar(64) NOT NULL," +
            "  menu_link varchar(128) DEFAULT NULL," +
            "  menu_displayorder integer NOT NULL DEFAULT '0'," +
            "  menu_level integer NOT NULL DEFAULT '0'" +
            ") ";

    public static final String CREATE_TABLE_LatLong = "CREATE TABLE fet_trn_tlatlong ( " +
            "  latlong_gid INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "  latlong_lat double NOT NULL, " +
            "  latlong_long double NOT NULL, " +
            "  latlong_emp_gid INT NOT NULL, " +
            "  latlong_date DATETIME NOT NULL, " +
            "  latlong_issync varchar(1) NOT NULL DEFAULT 'N', " +
            "  entity_gid INT NOT NULL " +
            ") ";
    public static final String CUSTOMER_ADD_SCHEDULE = "create table " + Constant.AStable_name + " ( " +
            Constant.AScustomer_gid + " integer, " +
            Constant.AScategory_gid + " integer," +
            Constant.ASemployee_gid + " integer," +
            Constant.ASconstitution_gid + " integer," +
            Constant.ASsalesmode_gid + " integer," +
            Constant.ASsize_gid + " integer," +
            Constant.AScustomer_name + " varchar(128)," +
            Constant.ASlocation_name + " varchar(128)," +
            Constant.ASdisplay_name + " varchar(128)," +
            Constant.ASismanagement + " integer," +
            Constant.ASstatus + " varchar(64)," +
            Constant.ASiseditable + " varchar(10)" +
            ")";
    public static final String EMPLOYEE = "create table " + Constant.employeetable_name + " ( " +
            Constant.employee_gid + " integer, " +
            Constant.employee_name + " varchar(64)" +
            ")";

    public static final String TEMPTABLE = "create table " + Constant.temptable_name + " ( " +
            Constant.temptable_gid + " integer, " +
            Constant.temptable_table_name + " varchar(64)," +
            Constant.temptable_table_gid + " integer," +
            Constant.temptable_table_value + " varchar(64)" +
            ")";

    public static final String CREATE_TABLE_Customer = "CREATE TABLE fet_mst_tcustomer ( " +
            " customer_gid INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " customer_custgroup_gid INT NOT NULL, " +
            " location_gid INT NOT NULL, " +
            " customer_name varchar(128), " +
            " display_name varchar(128), " +
            " location_name varchar(64)," +
            " customer_isactive varchar(1)" +
            ") ";

    public static final String CREATE_TABLE_Customergroup = "CREATE TABLE fet_mst_tcustomergroup ( " +
            " customergroup_gid INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " customergroup_name varchar(128) " +
            ") ";
    public static final String CREATE_TABLE_Product = "CREATE TABLE fet_mst_tproduct( " +
            " product_gid INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " uom_gid INT NOT NULL, " +
            " product_code varchar(16), " +
            " product_name varchar(64), " +
            " product_displayname varchar(64), " +
            " uom_name varchar(16) " +
            ") ";


}
