package models;

import android.content.Context;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import DataBase.DataBaseHandler;
import constant.Constant;

/**
 * Created by sakthivel
 */
public class ExpandableListDataSource {


    public static Map<String, List<String>> getData(Context context) {

        DataBaseHandler dataBaseHandler = new DataBaseHandler(context);
        Constant.parentMenus = dataBaseHandler.Read_Menu();
        Map<String, List<String>> expandableListData = new LinkedHashMap<>();
        for (int i = 0; i < Constant.parentMenus.size(); i++) {
            int s =Constant.parentMenus.get(i).Menu_gid;
            expandableListData.put(Constant.parentMenus.get(i).Menu_Name, DataBaseHandler.getSubMenus(Constant.parentMenus.get(i).Menu_gid, context));
        }
        return expandableListData;
    }


}
