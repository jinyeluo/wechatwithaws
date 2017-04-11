package lab.squirrel.bearbay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Order {
    public final static String DINE_IN = "1";
    public final static String TO_GO = "2";

    private String type;
    private List<OrderItem> items = new ArrayList<>();
    private Calendar deliveryTime;
    private final DateFormat format;

    public Order() {
        format = new SimpleDateFormat("M/dd hh:mm");
        format.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String printItems(Products products) {
        StringBuilder builder = new StringBuilder();
        for (OrderItem item : items) {
            Product product = products.getProduct(item.getCode());
            if (product == null) {
                builder.append("No more available:").append(item.getName());
            } else {
                builder.append(product.getName()).append(" ").append(item.getAmount())
                    .append("磅 辣度 ").append(item.getSpicyLevel()).append("\n");
            }
        }

        if (deliveryTime != null) {
            DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);
            dateInstance.setTimeZone(TimeZone.getTimeZone("CST"));
            String timeInStr = format.format(deliveryTime.getTime());
            builder.append("取货时间:").append(timeInStr).append("\n");
        }
        if (type.equals(TO_GO)) {
            builder.append("外卖\n");
        } else if (type.equals(DINE_IN)) {
            builder.append("堂吃\n");
        }

        return builder.toString();
    }

    public void setDeliveryTime(Calendar deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Calendar getDeliveryTime() {
        return deliveryTime;
    }
}
