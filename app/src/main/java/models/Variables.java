package models;


import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import constant.Constant;

public class Variables {
    public static class Menulist {
        public int Menu_gid;
        public int Menu_Parent_gid;
        public String Menu_Name;
        public String Menu_Link;
        public int Menu_Display_Order;
        public int Menu_Level;
        public int Entity_gid;

    }

    public static class Employee {
        public String employee_name;
        public int employee_gid;

    }

    public static class Customer {

        public String customer_name, customer_location, isEditable, customer_sch_status;
        public int customer_gid;
        public boolean isSelected;

    }

    public static class ScheduleType {
        public String schedule_type_name;
        public int schedule_type_id;
        public int schedule_gid;
        public String schedule_status;
        public Object schedule_details;

        public String getSchedule_type_name() {
            return schedule_type_name;
        }

        public int getSchedule_type_id() {
            return schedule_type_id;
        }
    }

    public static class FollowupReason {
        public String followup_name;
        public int followup_id;

        public String getFollowup_name() {
            return followup_name;
        }

        public int getFollowup_id() {
            return followup_id;
        }


    }

    public static class Location {
        public double latlong_lat;
        public double latlong_long;
        public String latlong_locationname;
        public int emp_gid;
        public String latlong_date;
        public int latlong_gid;
        public int entity_gid;
    }

    public static class DeviceInfo {
        public String DeviceInfo_Date;
        public String DeviceInfo_Data;
        public int DeviceInfo_Gid;
    }

    public static class Product {
        public int product_id;
        public String product_name;
        public String product_code;
    }


    public static class Service {
        public int product_gid;
        public String product_name, product_serial_no, product_remark;
    }

    public static class Timeline {
        public enum Status {
            COMPLETED,
            ACTIVE,
            INACTIVE, REJECTED;
        }

        public String title;
        public String subtitle;
        public Status status;
    }

    //Stock set
    public static class Stock {
        public int prduct_id;
        public EditText current_stock_qty;
        public EditText remark;

    }

    public static class ServiceSummary {
        public String customer_name;
        public String product_name;
        public int product_gid, service_gid;
        public String product_Slno, service_courierexp;
        public String service_remark;
        public String service_date;
        public boolean is_selected;

    }

    public static class Details {
        public int gid;
        public String data;
        public int dataColor;
        public String status;
    }

    public static class History {
        public String employee_name;
        public String schedule_date;
        public String schedule_type, schedule_status;
        public String followup, reschedule_date, followup_date;
    }

    public static class StatusReview {
        public String customer_name;
        public String employee_name;
        public String schedule_type, schedule_date, followup_date, followup_reason;
        public String schedule_status;
        public String remark;
        public int schedule_gid, soheader_gid, schedulereview_gid, employee_gid;
        public String review_remarks, review_status;
        public boolean isSelected, isAdmin;

    }

    //Viewtask
    public static class Viewtask_customer {

        public String cust_name, date, type, complete_for, status;
        int Schedule_ref_gid, customer_gid, schedule_gid;

        public Viewtask_customer(String cust_name, String date, String type, String complete_for, String status, int Schedule_ref_gid,
                                 int customer_gid, int schedule_gid) {
            this.cust_name = cust_name;
            this.date = date;
            this.type = type;
            this.complete_for = complete_for;
            this.status = status;
            this.Schedule_ref_gid = Schedule_ref_gid;
            this.customer_gid = customer_gid;
            this.schedule_gid = schedule_gid;
        }

        public String getCust_name() {
            return cust_name;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }

        public String getComplete_for() {
            return complete_for;
        }

        public String getStatus() {
            return status;
        }

        public int getSchedule_ref_gid() {
            return Schedule_ref_gid;
        }

        public int getCustomer_gid() {
            return customer_gid;
        }

        public int getSchedule_gid() {
            return schedule_gid;
        }
    }

    public static class Approval_List {
        private String Customername;
        private String Employeename;
        private Object jsonsalesdetail;
        private Object jsonoutstngdetail;
        private Object jsonpdcsdetail;
        private String soheader;


        public Approval_List(String Customername, String Employeename, Object jsonsalesdetail,
                             Object jsonoutstngdetail,
                             Object jsonpdcsdetail,
                             String soheader) {
            this.Customername = Customername;
            this.Employeename = Employeename;
            this.jsonsalesdetail = jsonsalesdetail;
            this.jsonoutstngdetail = jsonoutstngdetail;
            this.jsonpdcsdetail = jsonpdcsdetail;
            this.soheader = soheader;
        }

        public String getCustomername() {
            return Customername;
        }

        public String getEmployeename() {
            return Employeename;
        }

        public Object getJsonsalesdetail() {
            return jsonsalesdetail;
        }

        public Object getJsonoutstngdetail() {
            return jsonoutstngdetail;
        }

        public Object getJsonpdcsdetail() {
            return jsonpdcsdetail;
        }

        public String getSoheader() {
            return soheader;
        }

    }

    public static class LatLong {
        public double latitude, longitude;
        public String title;
    }


    public static class Comment {
        public String comment_message, comment_date, employee_name;
        public int comment_gid;
        public int employee_gid;
    }

    public static class SalesDetail {
        public String  product_name,sales_date,soheader_no;
        public int product_quantity,order_quantity,product_gid,sodetail_gid,soheader_gid;
        public double product_price, total_price;
    }

    public static class paramsStatusReview {
        public int employee_gid, customer_gid, scheduletype_gid, custgroup_gid, location_gid;
        public String employee_name, from_date, todate, sch_from_data, sch_to_date, resch_from_date, resch_to_date, sch_review_status;
    }

    public static class calendarDate {
        private final Date date1;
        private int year, dayofmonth, month;

        public calendarDate(String date) {

            if (date == null || date.equals("")) {
                date1 = new Date();
            } else {
                date1 = Common.convertDate(date, Constant.date_display_format);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayofmonth = calendar.get(Calendar.DAY_OF_MONTH);

        }

        public int getDayofmonth() {
            return dayofmonth;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }
    }

    public static class paramsAddSchedule {
        public int cust_mode_gid, cust_size_gid, cust_category_gid, cust_constitution_gid;
        public String cust_type;
        public List<Integer> employee_gid, cluster_gid, route_gid;
    }


}