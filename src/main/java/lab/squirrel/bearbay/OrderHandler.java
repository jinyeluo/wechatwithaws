package lab.squirrel.bearbay;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderHandler {
    public static final Pattern ORDER_PATTERN = Pattern.compile("\\A([A-Z])#(\\d{1,2}(\\.\\d)?)#(\\d{1,2})\\Z");
    public static final Pattern WAIT_TIME_PATTERN = Pattern.compile("\\A(\\d{1,2})H(\\d{1,2})?\\Z");
    static final char[] EMOJI_DISAPPOINTED = Character.toChars(128542);
    static final char[] EMOJI_CHECK = Character.toChars(0x2714);
    private int orderId = 1;
    private Map<String, Order> orders = new HashMap<>();

    public String order(String userId, String msg, Products products) {
        Order order = orders.get(userId);
        if (order == null) {
            return generateDisappointedMsg("pls use menu to select 堂吃 or 外卖");
        }

        StringBuilder builder = new StringBuilder(msg);
        int spacePos = builder.indexOf(" ");
        while (spacePos > -1) {
            builder.deleteCharAt(spacePos);
            spacePos = builder.indexOf(" ");
        }

        String normalizedCmd = builder.toString().toUpperCase();
        String message = processOrder(products, order, normalizedCmd);
        if (message == null) {
            message = processWaitTime(order, normalizedCmd, products);
            if (message == null) {
                return generateDisappointedMsg("I don't understand");
            } else return message;
        } else return message;
    }

    private String processWaitTime(Order order, String normalizedCmd, Products products) {
        Matcher matcher = WAIT_TIME_PATTERN.matcher(normalizedCmd);
        if (matcher.find()) {
            int hh = convertNumber(matcher.group(1));
            int mm = convertNumber(matcher.group(2));

            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR, hh);
            c.add(Calendar.MINUTE, mm);
            order.setDeliveryTime(c);

            return order.printItems(products);
        } else
            return null;
    }

    private int convertNumber(String hours) {
        int hh = 0;
        if (hours != null && !hours.isEmpty()) {
            hh = Integer.parseInt(hours);
        }
        return hh;
    }

    private String processOrder(Products products, Order order, String src) {
        Matcher matcher = ORDER_PATTERN.matcher(src);
        if (matcher.find()) {
            Product product = products.getProduct(matcher.group(1));
            if (product == null) {
                return generateDisappointedMsg("product doesn't exist");
            }

            OrderItem orderItem = new OrderItem(product.getName(),
                matcher.group(1), matcher.group(2), Integer.parseInt(matcher.group(4)));
            try {
                orderItem.isValid(products);
                order.getItems().add(orderItem);
                return order.printItems(products);
            } catch (OrderHandlerException e) {
                return generateDisappointedMsg(e.getMessage());
            }
        }
        return null;
    }

    private String generateDisappointedMsg(String str) {
        return new StringBuilder().append(EMOJI_DISAPPOINTED).append(str).toString();
    }

    public boolean newOrder(String userId, String dineInOrTogo, StringBuilder answer) throws OrderHandlerException {
        Order order = orders.get(userId);
        if (order == null) {
            Order newOrder = new Order();
            newOrder.setType(dineInOrTogo);
            orders.put(userId, newOrder);
            return true;
        } else {
            answer.append("order exists");
            return false;
        }
    }

    public boolean confirm(String userId, Products products, StringBuilder answer) {
        Order order = orders.get(userId);
        if (order == null) {
            answer.append("Hah???");
            return false;
        } else {
            if (order.getItems().isEmpty()) {
                answer.append("nothing to confirm");
                return false;
            }
            String productDesc = order.printItems(products);
            int confirmedId = orderId++;
            if (orderId == 1000) {
                orderId = 1;
            }
            orders.remove(userId);
            answer.append(productDesc)
                .append(EMOJI_CHECK).append("Order# ").append(confirmedId);
            return true;
        }
    }

    public String cancel(String userId) {
        Order removed = orders.remove(userId);
        if (removed == null) {
            return "Hah???";
        } else {
            return "cancelled";
        }

    }
}
