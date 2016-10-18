package ru.tbstaxi.tbstaxidrive.network;

import android.app.Activity;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.joshdholtz.sentry.Sentry;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.tbstaxi.tbstaxidrive.AppConfig;
import ru.tbstaxi.tbstaxidrive.R;
import ru.tbstaxi.tbstaxidrive.activity.CustomActivity;
import ru.tbstaxi.tbstaxidrive.fragment.MyGroupFragment;
import ru.tbstaxi.tbstaxidrive.struct.Order;

import static ru.tbstaxi.tbstaxidrive.R.string.error;
import static ru.tbstaxi.tbstaxidrive.fragment.MyGroupFragment.FRAGMENT_AVAILABLE_ORDERS;
import static ru.tbstaxi.tbstaxidrive.fragment.MyGroupFragment.FRAGMENT_FUTURE_ORDERS;
import static ru.tbstaxi.tbstaxidrive.fragment.MyGroupFragment.FRAGMENT_HISTORY_ORDERS;

public class Network {
    public static final String TAG = Network.class.getSimpleName();

    private static final String urlBase = "???";
    private static final boolean DEBUG = false;

    private static final String urlAuth = "???";
    private static final String urlDispatcherSettings = "???";
    private static final String urlSetDriverPosition = "???";
    private static final String urlSetDriverStatus = "???";
    private static final String urlSetDriverSettings = "???";

....

    public static final String ENABLE = "enable"; //Включен/Выключен робот
    public static final String METERS = "distance_meters"; //Настройка удаленности от заказа в км
    public static final String MINUTES = "distance_minutes"; //Настройка удаленности от заказа в часах

    public static final String DRIVER_STATUS_NOT_WORK = "not_work";//не работает
    public static final String DRIVER_STATUS_FREE = "free";    //свободен
    public static final String DRIVER_STATUS_BUSY = "busy";    //занят
    public static final String DRIVER_STATUS_ON_ORDER = "on_order";//на заказе

    public static final String AVAILABLE = "available"; //Доступен
    public static final String PROCESSED = "processed"; //Обработка
    public static final String REJECT = "reject";    //Отклоненные
    public static final String DRIVE = "drive";     //В пути
    public static final String ACCEPTED = "accepted";  //Принятый
    public static final String DEPARTED = "departed";  //Выехал
    public static final String IN_PLACE = "in_place";  //На месте

    private static final String P_LAT = "lat";
    private static final String P_LON = "lon";

    private static Network instance = null;

    public static Network getInstance() {
        if (instance == null) {
            synchronized (Network.class) {
                if (instance == null) {
                    instance = new Network();
                }
            }
        }
        return instance;
    }

    public interface ErrorCallBack {
        public void onError(String error);
    }

    public interface ListCallBack extends ErrorCallBack {
        public void onSuccess(List<Order> orders);
    }

    public interface MapCallBack<T> extends ErrorCallBack {
        public void onSuccess(Map<String, T> map);
    }

    public interface OrderCallBack extends ErrorCallBack {
        public void onSuccess(Order order);
    }

    public interface MapResponseCallBack extends ErrorCallBack {
        public void onResponse(JSONObject json, Map<String, String> map);
    }

    /**
     * Выполнить GET запрос с проверкой результата по полю STATUS
     *
     * @param activity
     * @param url
     * @param callBack
     */
    private void doGetMapRequest(final Activity activity, final String url,
                                 final MapResponseCallBack callBack) {
        HttpsTrustManager.allowAllSSL();
        Request<JSONObject> jsObjRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Map<String, String> map = new HashMap<>();
                try {
                    if (response != null) {
                        JSONObject json = new JSONObject(response.toString());
                        final Boolean bStatus = Boolean.parseBoolean(putInMap(map, json, STATUS));
                        if (bStatus) {
                            callBack.onResponse(json, map);
                        } else {
                            if (json.has(RESULT)) {
                                JSONObject jsonResult = new JSONObject(json.get(RESULT).toString());
                                putInMap(map, jsonResult, MESSAGE);
                                putInMap(map, jsonResult, ORDER_ID);
                                callBack.onResponse(json, map);
                            } else {
                                Sentry.captureMessage(json.toString());
                                callBack.onError(json.toString());
                            }
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Sentry.captureException(ex);
                    callBack.onError(ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Sentry.captureException(error);
                callBack.onError(error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(jsObjRequest);
    }

    /**
     * @param map
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    private String putInMap(Map<String, String> map, JSONObject json, final String key) throws JSONException {
        String val = null;
        if (json.has(key)) {
            val = json.get(key).toString();
            map.put(key, val);
        }
        return val;
    }

    /**
     * @param json
     * @param key
     * @return
     * @throws JSONException
     */
    private String getKeyFromJSON(JSONObject json, final String key) throws JSONException {
        String val = null;
        if (json.has(key)) {
            val = json.get(key).toString();
        }
        return val;
    }


....
}