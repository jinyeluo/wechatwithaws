package lab.squirrel.bearbay;

import java.util.ArrayList;
import java.util.List;

public class Products {
    private List<Product> products;

    public Products() {
        products = new ArrayList<>();
        products.add(new Product("A", "缅因大龙虾", "15"));
        products.add(new Product("B", "路易斯安那小龙虾", "10"));
        products.add(new Product("C", "大虾", "12"));
        products.add(new Product("D", "ameripure 大个生蚝", "12"));
        products.add(new Product("E", "维吉尼亚活花蛤", "15"));
        products.add(new Product("F", "地中海活青口", "10"));
        products.add(new Product("G", "新西兰青口", "8"));
    }

    public Product getProduct(String code) {
        for (Product product : products) {
            if (product.getCode().equals(code)) {
                return product;
            }
        }
        return null;
    }
}
