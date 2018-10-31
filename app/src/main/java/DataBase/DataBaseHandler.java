package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import constant.Constant;
import models.Variables;
import DataBase.DBTables;


public class DataBaseHandler extends SQLiteOpenHelper {
    private static DataBaseHandler dataBaseHandler;

    public DataBaseHandler(Context context) {
        super(context, Constant.DATABASENAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        String query = "CREATE TABLE gal_mst_tmenu (" +
//                "  menu_gid int  NOT NULL," +
//                "  menu_parent_gid integer NOT NULL," +
//                "  menu_name varchar(64) NOT NULL," +
//                "  menu_link varchar(128) DEFAULT NULL," +
//                "  menu_displayorder integer NOT NULL DEFAULT '0'," +
//                "  menu_level integer NOT NULL DEFAULT '0'" +
//                ") ";
//        sqLiteDatabase.execSQL(query);


        sqLiteDatabase.execSQL(DBTables.CREATE_TABLE_Menu.toString());
        sqLiteDatabase.execSQL(DBTables.CREATE_TABLE_LatLong.toString());
        sqLiteDatabase.execSQL(DBTables.CUSTOMER_ADD_SCHEDULE);
        sqLiteDatabase.execSQL(DBTables.EMPLOYEE);
        sqLiteDatabase.execSQL(DBTables.TEMPTABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String Insert(String TableName, ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long i = sqLiteDatabase.insert(TableName, null, contentValues);

        if (i >= 1) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }

    }

    public String Update(String TableName, ContentValues contentValues, String WhereClause) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long i = sqLiteDatabase.update(TableName, contentValues, WhereClause, null);

        if (i >= 1) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }


    public List<Variables.Menulist> Read_Menu() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from gal_mst_tmenu where menu_parent_gid=0 order by " + Constant.menu_displayorder, null);
        List<Variables.Menulist> list = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {

                Variables.Menulist variables = new Variables.Menulist();
                variables.Menu_gid = cursor.getInt(cursor.getColumnIndex("menu_gid"));
                variables.Menu_Parent_gid = cursor.getInt(cursor.getColumnIndex("menu_parent_gid"));
                variables.Menu_Name = cursor.getString(cursor.getColumnIndex("menu_name"));
                variables.Menu_Link = cursor.getString(cursor.getColumnIndex("menu_link"));
                variables.Menu_Display_Order = cursor.getInt(cursor.getColumnIndex("menu_displayorder"));
                variables.Menu_Level = cursor.getInt(cursor.getColumnIndex("menu_level"));
                list.add(variables);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public String Table_Truncate(String Table_Name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(Table_Name, null, null);
        return "";
    }

    public static List<String> getSubMenus(int menu_name, Context context) {
        if (dataBaseHandler == null) {
            dataBaseHandler = new DataBaseHandler(context);
        }
        ArrayList<String> strings = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = dataBaseHandler.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select menu_name from gal_mst_tmenu where menu_parent_gid=" + menu_name + "", null);

        if (cursor.moveToFirst()) {
            do {
                strings.add(cursor.getString(cursor.getColumnIndex("menu_name")));
            } while (cursor.moveToNext());
        }
        cursor.close();


        return strings;
    }

    public List<Variables.Location> getLatLong(Context context) {
        if (dataBaseHandler == null) {
            dataBaseHandler = new DataBaseHandler(context);
        }

        SQLiteDatabase sqLiteDatabase = dataBaseHandler.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select latlong_gid,latlong_lat,latlong_long,latlong_date,latlong_emp_gid,entity_gid " +
                "from fet_trn_tlatlong where latlong_issync = 'N';", null);
        List<Variables.Location> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Variables.Location location = new Variables.Location();
                location.latlong_gid = cursor.getInt(cursor.getColumnIndex("latlong_gid"));
                location.latlong_lat = cursor.getDouble(cursor.getColumnIndex("latlong_lat"));
                location.latlong_long = cursor.getDouble(cursor.getColumnIndex("latlong_long"));
                location.latlong_date = cursor.getString(cursor.getColumnIndex("latlong_date"));
                location.emp_gid = cursor.getInt(cursor.getColumnIndex("latlong_emp_gid"));
                location.entity_gid = cursor.getInt(cursor.getColumnIndex("entity_gid"));
                list.add(location);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;

    }


}
