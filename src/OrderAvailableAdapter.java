package ru.tbstaxi.tbstaxidrive.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ru.tbstaxi.tbstaxidrive.R;
import ru.tbstaxi.tbstaxidrive.Utils;
import ru.tbstaxi.tbstaxidrive.network.Network;
import ru.tbstaxi.tbstaxidrive.struct.Order;

public class OrderAvailableAdapter extends BaseAdapter {
    public static final String TAG = OrderAvailableAdapter.class.getSimpleName();

    private static Network network;
    private static Context context;
    private static Activity activity;
    private static List<Order> orders;

    public OrderAvailableAdapter(final Activity activity, final List<Order> orders) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.orders = new ArrayList<>(orders);
        this.network = Network.getInstance();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        Collections.sort(orders, new Comparator<Order>(){
            public int compare(Order emp1, Order emp2) {
                Date date1, date2;
                int i = 0;
                try {
                    date1 = dateFormat.parse(emp1.getDateTime().getDateTime());
                    date2 = dateFormat.parse(emp2.getDateTime().getDateTime());
                    i = date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return i;
            }
        });
    }
    private static long lastClickTime = 0;
    private static boolean bFirstClick = true;
    private static Handler handler = new Handler();

    private interface TickTimer {
        public void onTick();
    }

    static class ViewHolder {
        public String status;
        public TickTimer tickTimer;
        public Button btnRequest;
        public TextView tvOrderNumber;
        public TextView tvOrderTariff;
        public TextView tvOrderDateTime;
        public TextView tvOrderLeft;
        public TextView tvOrderFrom;
        public TextView tvOrderTo;
        public TextView tvOrderAdditions;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Order currentOrder = orders.get(position);
        final ViewHolder holder;

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.order_available_item, null, true);
            holder = new ViewHolder();

            holder.tvOrderNumber = (TextView) rowView.findViewById(R.id.tvOrderNumber);
            holder.tvOrderTariff = (TextView) rowView.findViewById(R.id.tvOrderTariff);
            holder.tvOrderDateTime = (TextView) rowView.findViewById(R.id.tvOrderDateTime);
            holder.tvOrderLeft = (TextView) rowView.findViewById(R.id.tvOrderLeft);
            holder.tvOrderFrom = (TextView) rowView.findViewById(R.id.tvOrderFrom);
            holder.tvOrderTo = (TextView) rowView.findViewById(R.id.tvOrderTo);
            holder.tvOrderAdditions = (TextView) rowView.findViewById(R.id.tvOrderAdditions);
            holder.btnRequest = (Button) rowView.findViewById(R.id.btnRequest);

            network.orderStatus(activity, currentOrder.getOrderId(), new Network.MapCallBack() {
                @Override
                public void onSuccess(Map map) {
                    if (map.containsKey(Network.RESULT)) {
                        final String result = map.get(Network.RESULT).toString();

                        if (result.equalsIgnoreCase(Network.AVAILABLE)) {
                            holder.btnRequest.setText(context.getString(R.string.request));
                            holder.status = Network.AVAILABLE;
                        }
                        if (result.equalsIgnoreCase(Network.REQUEST)) {
                            holder.btnRequest.setText(context.getString(R.string.reject));
                            holder.status = Network.REQUEST;
                        }
                        if (result.equalsIgnoreCase(Network.REJECT)) {
                            holder.btnRequest.setText(context.getString(R.string.available));
                            holder.status = Network.REJECT;
                        }
                        if (result.equalsIgnoreCase(Network.ACCEPTED)) {
                            holder.btnRequest.setText(context.getString(R.string.accepted));
                            holder.status = Network.ACCEPTED;
                        }
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, new StringBuilder()
                            .append(context.getString(R.string.error_get_status))
                            .append(":")
                            .append(error)
                            .toString(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bFirstClick) {
                        lastClickTime = SystemClock.elapsedRealtime();
                        bFirstClick = false;
                        return;
                    }

                    long cmpTime = (SystemClock.elapsedRealtime() - lastClickTime);
                    if (cmpTime > 2000) {
                        lastClickTime = SystemClock.elapsedRealtime();
                        return;
                    }

                    bFirstClick = true;
                    final String orderId = currentOrder.getOrderId();

                    holder.btnRequest.setVisibility(View.INVISIBLE);
                    if (holder.status.equalsIgnoreCase(Network.AVAILABLE)) {
                        Utils.startProgress(activity);
                        network.takeOrder(activity, orderId, new Network.MapCallBack() {
                            @Override
                            public void onSuccess(Map map) {
                                Utils.stopProgress();
                                holder.btnRequest.setText(context.getString(R.string.reject));
                                holder.status = Network.REQUEST;
                            }

                            @Override
                            public void onError(String error) {
                                Utils.stopProgress();
                                Toast.makeText(context,
                                        new StringBuilder()
                                                .append(context.getString(R.string.error_change_status))
                                                .append(":")
                                                .append(error)
                                                .toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (holder.status.equalsIgnoreCase(Network.REQUEST)) {
                        Utils.startProgress(activity);
                        network.reject(activity, orderId, context.getString(R.string.reject_reason),
                                new Network.MapCallBack() {
                                    @Override
                                    public void onSuccess(Map map) {
                                        Utils.stopProgress();
                                        holder.btnRequest.setText(context.getString(R.string.available));
                                        holder.status = Network.REJECT;
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Utils.stopProgress();
                                        Toast.makeText(context,
                                                new StringBuilder()
                                                        .append(context.getString(R.string.error_change_status))
                                                        .append(":")
                                                        .append(error)
                                                        .toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else if (holder.status.equalsIgnoreCase(Network.REJECT)) {
                        Utils.startProgress(activity);
                        network.statusClear(activity, orderId, new Network.MapCallBack() {
                            @Override
                            public void onSuccess(Map map) {
                                Utils.stopProgress();
                                holder.btnRequest.setText(context.getString(R.string.request));
                                holder.status = Network.AVAILABLE;
                            }

                            @Override
                            public void onError(String error) {
                                Utils.stopProgress();
                                Toast.makeText(context,
                                        new StringBuilder()
                                                .append(context.getString(R.string.error_change_status))
                                                .append(":")
                                                .append(error)
                                                .toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    holder.btnRequest.setVisibility(View.VISIBLE);
                }
            });

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.tickTimer.onTick();
                            }
                        });
                    }
                }
            }, 0, 1000);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.tvOrderNumber.setText(currentOrder.getOrderId());
        holder.tvOrderTariff.setText(currentOrder.getTariff().getName());
        holder.tvOrderDateTime.setText(Utils.getDateTimeFromOrder(currentOrder.getDateTime()));
        holder.tvOrderFrom.setText(Utils.getFullAddress(currentOrder.getFrom()));
        holder.tvOrderTo.setText(Utils.getFullAddress(currentOrder.getTo()));
        holder.tvOrderAdditions.setText(context.getString(R.string.no));

        holder.tickTimer = new TickTimer() {
            @Override
            public void onTick() {
                if (holder.tvOrderLeft != null) {
                    holder.tvOrderLeft.setText(
                            Utils.getDiffDates(context, currentOrder.getDateTime().getDateTime()));
                }
            }
        };

        return rowView;
    }
}
