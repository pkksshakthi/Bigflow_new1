package models;


import android.widget.EditText;

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

    public static class Sales {
        public int sodetails_product_gid;
        public double quantity;

    }

    public static class Service {
        public int product_id;
        public String product_name;

        public Service(int product_id, String product_name) {
            this.product_id = product_id;
            this.product_name = product_name;

        }
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

    // Stock Autocomplete
    public static class stockProduct {
        public String product_id;
        public String product_name;
    }

    //Stock set
    public static class Stock {
        public int prduct_id;
        public EditText current_stock_qty;
        public EditText remark;

    }

    public static class ServiceSummary_List {
        private String Customername;
        private String Productname;
        private int Productgid, servicegid;
        private String ProductSlno, Service_courierexp;
        private String Remark;
        private String Date;
        private boolean isSelected = false;

        public ServiceSummary_List(String Customername, String Productname,
                                   String Date, int servicegid, int Productgid,
                                   String ProductSlno, String Remark, String Service_courierexp) {
            this.Customername = Customername;
            this.Productname = Productname;
            this.Date = Date;
            this.servicegid = servicegid;
            this.Productgid = Productgid;
            this.ProductSlno = ProductSlno;
            this.Remark = Remark;
            this.Service_courierexp = Service_courierexp;
        }

        public String getCustomername() {
            return Customername;
        }

        public String getProductname() {
            return Productname;
        }

        public String getDate() {
            return Date;
        }

        public int getservicegid() {
            return servicegid;
        }

        public int getProductgid() {
            return Productgid;
        }

        public String getRemark() {
            return Remark;
        }

        public String getProductSlno() {
            return ProductSlno;
        }

        public String getService_courierexp() {
            return Service_courierexp;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }


    }

    public static class Courier {

        private int id;
        private String couriername;

        public Courier(int id, String couriername) {
            this.id = id;
            this.couriername = couriername;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCouriernameName() {
            return couriername;
        }

        public void setName(String couriername) {
            this.couriername = couriername;
        }


        //to display object as a string in spinner
        @Override
        public String toString() {
            return couriername;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Courier) {
                Courier c = (Courier) obj;
                if (c.getCouriernameName().equals(couriername) && c.getId() == id) return true;
            }

            return false;
        }

    }

    public static class courierset {
        public static int courier_id;
        public static String AWB_no;
        public static String Mode;
        public static String Packet;
        public static String Weight;
        public static String Date;
        public static String Send_to;

        public int getCourierid() {
            return courier_id;
        }

        public String getDate() {
            return Date;
        }

        public String getAWB_no() {
            return AWB_no;
        }

        public String getMode() {
            return Mode;
        }

        public String getPacket() {
            return Packet;
        }

        public String getWeight() {
            return Weight;
        }

        public String getSend_to() {
            return Send_to;
        }

        public String getinout_flag() {
            if (Send_to.equals("SEND TO CENTRAL OFFICE")) {
                return "SERVICE_TOCENTRAL";
            } else {
                return "SERVICE_TOBRANCH";
            }

        }

        public Boolean anyUnset() {
//            if (courier_id == null) return true;
            if (AWB_no.length() == 0) return true;
            if (Mode == null) return true;
            if (Packet.length() == 0) return true;
            if (Weight.length() == 0) return true;
            if (Send_to == null) return true;
            if (Date == null) return true;

            return false;
        }
    }

    public static class Details {
        public int gid;
        public String data;
        public int dataColor;
        public int Schedule_gid;
        public String Salestatus;
    }

    public static class History {
        public String employee_name;
        public String schedule_date;
        public String schedule_type;
        public String followup;
    }

    public static class StatusReview {
        public String customer_name;
        public String employee_name;
        public String schedule_type, schedule_date, followup_date, followup_reason;
        public String schedule_status;
        public String remark;
        public int schedule_gid, soheader_gid, schedulereview_gid;
        public String review_remarks, review_status;
        private boolean isSelected;

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

    public static class Update_sales {
        public int product_id;
        public int sodetails_gid;
        public int qty;


        public Update_sales(int product_id, int sodetails_gid, int qty) {
            this.product_id = product_id;
            this.sodetails_gid = sodetails_gid;
            this.qty = qty;
        }
    }

    public static class Comment {
        public String comment_message, comment_date, employee_name;
        public int comment_gid;
        public int employee_gid;
    }

    public static class SalesDetail {
        public String soheader_gid, sodetail_gid, product_name;
        public int product_quantity;
        public double product_price, total_price;
    }

}