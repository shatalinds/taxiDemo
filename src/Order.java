package ru.tbstaxi.tbstaxidrive.struct;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Order implements Serializable {
    private String orderId;
    private String status;
    private OrderDateTime dateTime;
    private OrderAddress from;
    private OrderAddress to;
    private OrderTariff tariff;
    private String extraServices;
    private String cost;
    private String name;
    private String phone;

    public final static String P_ORDER_ID = "id";
    public final static String P_STATUS = "status";
    public final static String P_DATETIME = "datetime";
    public final static String P_FROM = "address_from";
    public final static String P_TO = "address_to";
    public final static String P_TARIFF = "tariff";
    public final static String P_COST = "cost";
    public final static String P_NAME = "name";
    public final static String P_PHONE = "phone";

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(P_ORDER_ID, orderId);
            obj.put(P_STATUS, status);
            obj.put(P_DATETIME, dateTime.toJSON());
            obj.put(P_FROM, from.toJSON());
            obj.put(P_TO, to.toJSON());
            obj.put(P_TARIFF, tariff.toJSON());
            obj.put(P_COST, cost);
            obj.put(P_NAME, name);
            obj.put(P_PHONE, phone);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return obj;
    }

    private String hasGet(JSONObject obj, String name) throws JSONException {
        if (obj.has(name)) {
            return obj.get(name).toString();
        }
        return "";
    }

    private Object hasGetObject(JSONObject obj, String name) throws JSONException {
        if (obj.has(name)) {
            return obj.get(name);
        }
        return null;
    }

    public void fromJSON(JSONObject jsonObject) {
        try {
            orderId = hasGet(jsonObject, P_ORDER_ID);
            status = hasGet(jsonObject, P_STATUS);

            JSONObject json = null;

            json = jsonObject.getJSONObject(P_DATETIME);
            if (json != null) {
                dateTime = new OrderDateTime();
                dateTime.fromJSON(json);
            }

            json = jsonObject.getJSONObject(P_FROM);
            if (json != null) {
                from = new OrderAddress();
                from.fromJSON(json);
            }

            json = jsonObject.getJSONObject(P_TO);
            if (json != null) {
                to = new OrderAddress();
                to.fromJSON(json);
            }

            json = jsonObject.getJSONObject(P_TARIFF);
            if (json != null) {
                tariff = new OrderTariff();
                tariff.fromJSON(json);
            }

            cost = hasGet(jsonObject, P_COST);
            name = hasGet(jsonObject, P_NAME);
            phone = hasGet(jsonObject, P_PHONE);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public String getExtraServices() {
        return extraServices;
    }

    public void setExtraServices(String extraServices) {
        this.extraServices = extraServices;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderDateTime getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(OrderDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public OrderAddress getFrom() {
        return this.from;
    }

    public void setFrom(OrderAddress from) {
        this.from = from;
    }

    public OrderAddress getTo() {
        return this.to;
    }

    public void setTo(OrderAddress to) {
        this.to = to;
    }

    public OrderTariff getTariff() {
        return this.tariff;
    }

    public void setTariff(OrderTariff tariff) {
        this.tariff = tariff;
    }

    public String getCost() {
        return this.cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public class OrderAddress {
        private String region;
        private String city;
        private String street;
        private String house;
        private String housing;
        private String building;
        private String porch;
        private String apartment;
        private String lat;
        private String lon;

        public static final String P_REGION = "region";
        public static final String P_CITY = "city";
        public static final String P_STREET = "street";
        public static final String P_HOUSE = "house";
        public static final String P_HOUSING = "housing";
        public static final String P_BUILDING = "building";
        public static final String P_PORCH = "porch";
        public static final String P_APARTMENT = "apartment";
        public static final String P_LAT = "lat";
        public static final String P_LON = "lon";

        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            try {
                obj.put(P_REGION, this.region);
                obj.put(P_CITY, this.city);
                obj.put(P_STREET, this.street);
                obj.put(P_HOUSE, this.house);
                obj.put(P_HOUSING, this.housing);
                obj.put(P_BUILDING, this.building);
                obj.put(P_PORCH, this.porch);
                obj.put(P_APARTMENT, this.apartment);
                obj.put(P_LAT, this.lat);
                obj.put(P_LON, this.lon);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return obj;
        }

        public void fromJSON(JSONObject jsonObject) {
            try {
                this.region = hasGet(jsonObject, P_REGION);
                this.city = hasGet(jsonObject, P_CITY);
                this.street = hasGet(jsonObject, P_STREET);
                this.house = hasGet(jsonObject, P_HOUSE);
                this.housing = hasGet(jsonObject, P_HOUSING);
                this.building = hasGet(jsonObject, P_BUILDING);
                this.porch = hasGet(jsonObject, P_PORCH);
                this.apartment = hasGet(jsonObject, P_APARTMENT);
                this.lat = hasGet(jsonObject, P_LAT);
                this.lon = hasGet(jsonObject, P_LON);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getHouse() {
            return house;
        }

        public void setHouse(String house) {
            this.house = house;
        }

        public String getHousing() {
            return housing;
        }

        public void setHousing(String housing) {
            this.housing = housing;
        }

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
        }

        public String getPorch() {
            return porch;
        }

        public void setPorch(String porch) {
            this.porch = porch;
        }

        public String getApartment() {
            return apartment;
        }

        public void setApartment(String apartment) {
            this.apartment = apartment;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }
    }

    public class OrderTariff {
        private String id;
        private String name;

        public static final String P_TARIFF_ID = "id";
        public static final String P_NAME = "name";

        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            try {
                obj.put(P_TARIFF_ID, this.id);
                obj.put(P_NAME, this.name);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return obj;
        }

        public void fromJSON(JSONObject jsonObject) {
            try {
                this.id = hasGet(jsonObject, P_TARIFF_ID);
                this.name = hasGet(jsonObject, P_NAME);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }


        public String getTariffId() {
            return id;
        }

        public void setTariffId(String tariffId) {
            this.id = tariffId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class OrderDateTime {
        private String date;
        private String time;

        public static final String P_DATE = "date";
        public static final String P_TIME = "time";

        public JSONObject toJSON() {
            JSONObject obj = new JSONObject();
            try {
                obj.put(P_DATE, this.date);
                obj.put(P_TIME, this.time);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return obj;
        }

        public void fromJSON(JSONObject jsonObject) {
            try {
                this.date = hasGet(jsonObject, P_DATE);
                this.time = hasGet(jsonObject, P_TIME);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDateTime() {
            return new StringBuilder().append(date).append(" ").append(time).toString();
        }
    }
}
