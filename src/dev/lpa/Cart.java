package dev.lpa;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Comparable<Cart>{

    @Override
    public int compareTo(Cart c) {
        return id - c.id;
    }

    enum CartType {PHYSICAL, VIRTUAL};
    private static int lastId = 1;
    private int id;
    private LocalDate cartDate;
    private CartType type;
    private Map<String, Integer> products;

    public Cart(CartType type, int days) {
        this.type = type;
        id = lastId++;
        cartDate = LocalDate.now().minusDays(days);
        products = new HashMap<>();
    }

    public Cart(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDate getCartDate() {
        return cartDate;
    }

    public void addItem(InventoryItem item, int qty) {
        if (item != null) {
            if (!item.reserveItem(qty)) {
                System.out.println("Ouch, something went wrong, could not add item");
            }
            else {
                products.merge(item.getProduct().sku(), qty, Integer::sum);
            }
        }
        else {
            System.out.println("Ouch, something went wrong, there is not such an item");
        }
    }

    public void removeItem(InventoryItem item, int qty) {

        int current = products.get(item.getProduct().sku());
        if (current <= qty) {
            qty = current;
            products.remove(item.getProduct().sku());
            System.out.printf("Item [%s] removed from basket%n",
                    item.getProduct().name());

        } else {
            products.merge(item.getProduct().sku(), qty,
                    (oldVal, newVal) -> oldVal - newVal);
            System.out.printf("%d [%s]s removed%n", qty, item.getProduct().name());
        }
        item.releaseItem(qty);
    }

    public void printSalesSlip(Map<String, InventoryItem> inventory) {

        double total = 0;
        System.out.println("------------------------------------");
        System.out.println("Thank you for your sale: ");
        boolean sellError = false;
        for (var cartItem : products.entrySet()) {
            var item = inventory.get(cartItem.getKey());
            int qty = cartItem.getValue();
            if (item.sellItem(qty)) {
                double itemizedPrice = (item.getPrice() * qty);
                total += itemizedPrice;
                System.out.printf("\t%s %-10s (%d)@ $%.2f = $%.2f%n",
                        cartItem.getKey(), item.getProduct().name(), qty,
                        item.getPrice(), itemizedPrice);
            }
            else {
                sellError = true;
                System.out.printf("\t%s %-10s cannot sell item%n",
                        cartItem.getKey(), item.getProduct().name());
            }
        }
        System.out.printf("Total Sale: $%.2f%n", total);
        if (sellError) System.out.println("Error occurred while selling some items.\n" +
                "They won't be added to total prize");
        System.out.println("------------------------------------");
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", cartDate=" + cartDate +
                ", products=" + products +
                '}';
    }
}
