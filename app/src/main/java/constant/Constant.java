package constant;

import java.util.List;

import models.Variables;

public class Constant {
    public static final String DATABASENAME = "VSOLV";
    public static final int DATABASE_VERSION = 3;
    public static String IP_ADDRESS = "174.138.120.196";
    public static String HOST_NAME = "bigflowdemo";
    public static String URL = "https://" + IP_ADDRESS + "/" + HOST_NAME + "/";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static List<Variables.Menulist> parentMenus;

    //Menu
    public static final String home = "Home";
    public static final String addSchedule = "Add Schedule";
    public static final String daySchedule = "Day's Schedule";
    public static final String employeeTracking = "Employee Tracking";
    public static final String directSchedule = "Direct Outcome";
    public static final String addLeads = "Add Leads";
    public static final String routeSummary = "Route Summary";
    public static final String serviceSummary = "Service Summary";
    public static final String fetreview = "FET Review";
    public static final String approve = "Approve";


    //table_columnName
    public static String menu_gid = "menu_gid";
    public static String menu_parent_gid = "menu_parent_gid";
    public static String menu_name = "menu_name";
    public static String menu_link = "menu_link";
    public static String menu_displayorder = "menu_displayorder";
    public static String menu_level = "menu_level";

    public static String latlong_gid = "latlong_gid";
    public static String latitude = "latlong_lat";
    public static String longitude = "latlong_long";
    public static String location_name = "latlong_locationname";
    public static String latlong_emp_gid = "latlong_emp_gid";
    public static String latlong_date = "latlong_date";
    public static String latlong_issync = "latlong_issync";
    public static String entity_gid = "entity_gid";

    //Add Schedule customer
    public static final String AStable_name = "vsolv_mst_ascustomer";
    public static final String AScustomer_gid = "ascustomer_gid";
    public static final String AScategory_gid = "ascustomer_category_gid";
    public static final String ASemployee_gid = "ascustomer_employee_gid";
    public static final String ASconstitution_gid = "ascustomer_constitution_gid";
    public static final String ASsalesmode_gid = "ascustomer_salesmode_gid";
    public static final String ASsize_gid = "ascustomer_size_gid";
    public static final String AScustomer_name = "ascustomer_name";
    public static final String ASlocation_name = "ascustomer_location_name";
    public static final String ASdisplay_name = "asdisplay_name";
    public static final String AStype_name = "astype_name";
    public static final String ASismanagement = "asis_management";
    public static final String ASstatus = "asstatus";
    public static final String ASiseditable = "asis_editable";


    //Employee
    public static final String employeetable_name = "vsolv_mst_employee";
    public static final String employee_gid = "employee_gid";
    public static final String employee_name = "employee_name";

    //Temp Table
    public static final String temptable_name = "vsolv_mst_temptable";
    public static final String temptable_gid = "temptable_gid";
    public static final String temptable_table_name = "temptable_table_name";
    public static final String temptable_table_gid = "temptable_table_gid";
    public static final String temptable_table_value = "temptable_table_value";

    //API Sales Order
    public static String emp_gid = "emp_gid";
    public static String soheader_gid = "soheader_gid";
    public static String customer_gid = "custid";
    public static String Action = "ACTION";

    public static String product_gid = "sodetails_product_gid";
    public static String quantity = "quantity";
    public static String params = "parms";
    public static String Data = "data";
    public static String sodetails = "sodetails";
    public static String Schedule_Affect = "Schedule_Affect";


    //Stock API
    public static String ACTION = "action";
    public static String stckdet = "stckdet";
    public static String todaydate = "todaydate";
    public static String FET_STOCK = "FET_STOCK";


    //Viewtask API
    public static String action = "action";
    public static String f_date = "f_date";
    public static String from_date = "from_date";
    public static String to_date = "to_date";

    //Schedule type
    public static final String st_booking = "BOOKING";
    public static final String st_Collection = "COLLECTION";
    public static final String st_service = "SERVICE";
    public static final String st_stock = "STOCK";
    public static final String st_other = "OTHERS";

    // Device Info
    public static final String Battery_Status = "Battery_Status";
    public static final String LocationMode = "LocationMode";
    public static final String AirplaneMode = "AirplaneMode";
    public static final String MobileModel = "MobileModel";
    public static final String MobileBrand = "MobileBrand";
    public static final String MobileID = "MobileID";
    public static final String Process = "Process";
    // SQL Table
    public static final String Deviceinfo_Date = "deviceinfo_date";
    public static final String Deviceinfo_Data = "deviceinfo_data";
    public static final String Deviceinfo_issync = "deviceinfo_issync";

    //page title(ToolBar Title)
    public static final String title_direct_schedule = "Direct Outcome";
    public static final String title_day_schedule = "Day's Schedule";
    public static final String title_home = "Bigflow";
    public static final String title_Approve = "Approve";
    public static final String title_employee_tracking = "Tracking";
    public static final String title_add_schedule = "Add Schedule";
    public static final String title_route_summary = "Route";
    public static final String title_service_summary = "Service";
    public static final String title_fet_review = "FET Review";
    public static final String title_collection = "Collection";
    public static final String title_sales_planning = "Sales Plan";


    public static final String status_review_pending = "PENDING";
    public static final String status_review_approved = "APPROVE";
    public static final String status_review_reject = "REJECT";

    //bundle key string
    public static final String key_employee_gid = "employee_gid";
    public static final String key_employee_name = "employee_name";
    public static final String key_customer_gid = "customer_gid";
    public static final String key_sch_review_status = "sch_review_status";
    public static final String key_sch_type_gid = "sch_type_gid";
    public static final String key_cust_group_gid = "cust_group_gid";
    public static final String key_loaction_gid = "location_gid";
    public static final String key_fdate = "from_date";
    public static final String key_tdate = "to_date";
    public static final String key_followup_fdate = "followup_fdate";
    public static final String getKey_followup_tdate = "followup_tdate";
    public static final String key_reschedule_fdate = "reschedule_fdate";
    public static final String key_reschedule_tdate = "reschedule_tdate";
    public static final String key_cust_mode = "cust_mode";
    public static final String key_cust_type = "cust_type";
    public static final String key_territory_gid = "territory_gid";
    public static final String key_route_gid = "route_gid";
    public static final String key_cust_category = "cust_category";
    public static final String key_cust_size = "cust_size";
    public static final String key_cust_constitution = "cust_constitution";
    public static final String key_cust_type_gid = "cust_type_gid";
    public static final String key_cust_size_gid = "cust_size_gid";
    public static final String key_cust_mode_gid = "cust_mode_gid";
    public static final String key_cust_category_gid = "cust_category_gid";
    public static final String key_cust_constitution_gid = "cust_constitution_gid";
    public static final String key_customer_name = "customer_name";
    public static final String key_action = "action";
    public static final String key_soheader_gid = "soheader_gid";
    public static final String key_schedule_gid = "schedule_gid";

    //Date format
    public static final String date_display_format = "dd/MM/yyyy";
}
