package lab.squirrel.bearbay;

public class OrderItem {
    private String name;
    private String code;
    private String amount;
    private int spicyLevel;
    private String price;

    public OrderItem(String name, String code, String amount,  int spicyLevel) {
        this.name = name;
        this.code = code;
        this.amount = amount;
        this.spicyLevel = spicyLevel;
    }

    public void isValid(Products products) throws OrderHandlerException {
        if (products.getProduct(code) == null) {
            throw new OrderHandlerException("code is out of range");
        } else {
            double amountDb = Double.parseDouble(amount);
            double floor = Math.floor(amountDb);
            double remainder = amountDb - floor;
            if ((Math.abs(remainder - 0.0) > Double.MIN_VALUE
                && Math.abs(remainder - 0.5) > Double.MIN_VALUE) //must end with full pound or half
                || amountDb < 0.99){
                throw new OrderHandlerException("order can be at least one pound, increased by half pound");
            } else if (spicyLevel <1 || spicyLevel > 10){
                throw new OrderHandlerException("spicy level can only be 1 - 10");
            }
        }
    }

    public String getCode() {
        return code;
    }

    public String getAmount() {
        return amount;
    }

    public int getSpicyLevel() {
        return spicyLevel;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
