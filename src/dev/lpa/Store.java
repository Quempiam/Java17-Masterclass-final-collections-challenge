package dev.lpa;

import java.util.*;

public class Store {
    private NavigableSet<Cart> carts = new TreeSet<>();
    private Map<Category, Map<String, InventoryItem>> aisleInventory;
    private Map<String, InventoryItem> inventory;
    public static void main(String[] args) {
        var theStore = new Store();
        theStore.populateInventory();
        theStore.listInventory();
        theStore.populateAisles();
        theStore.listProductsByCategory();

        theStore.manageStoreCarts();
        theStore.listInventory();
    }

    private static final String exampleItems = """
            aba1120, Milk, The Company, dairy, 1.99, 200, 50
            aba1720, Cheese, The Company, dairy, 2.99, 100, 30
            agg8120, Apple, a compa, produce, 1.50, 150, 25
            agg1930, Banana, a compa, produce, 2.99, 200, 50
            abd5550, Pork, The Butcher, meat, 6.50, 50, 10
            abd5580, Beef, The Butcher, meat, 8.99, 40, 10
            kak4541, Flour, a windmillo, cereal, 0.99, 150, 50
            kan0023, Flax seeds, a windmillo, cereal, 3.50, 50, 10
            tuy2232, Soda, We sell beverage, beverage, 0.99, 200, 50
            tuy8858, Milk tea, We sell beverage, beverage, 1.99, 100, 20""";

    private void populateInventory() {
        inventory = new HashMap<>();
        for (String split : exampleItems.split("\\R")) {
            String[] item = split.split(",");
            Arrays.asList(item).replaceAll(String::trim);
            Product p = new Product(item[0].toUpperCase(), item[1], item[2], Category.valueOf(item[3].toUpperCase()));
            inventory.put(p.sku(), new InventoryItem(p,
                    Double.parseDouble(item[4]), Integer.parseInt(item[5]), Integer.parseInt(item[6])));
        }
    }

    public void listInventory() {
        System.out.println("INVENTORY:");
        inventory.values().forEach(System.out::println);
        System.out.println("=".repeat(100) + "\n");
    }

    private void populateAisles() {
        aisleInventory = new EnumMap<>(Category.class);
        for (var item : inventory.values()) {
            Category aisleCategory = item.getProduct().category();
//            see if there is a map for this category
            Map<String, InventoryItem> aisleProducts = aisleInventory.get(aisleCategory);
            if (aisleProducts == null) {
                aisleProducts = new TreeMap<>();
            }
            aisleProducts.put(item.getProduct().name(), item);
//            put if there was no map
            aisleInventory.putIfAbsent(aisleCategory, aisleProducts);
        }
    }

    public void listProductsByCategory() {
        System.out.println("AISLE INVENTORY:");
        aisleInventory.forEach((k, v) -> {
            System.out.println("** " + k + " **");
            v.keySet().forEach(System.out::println);
            System.out.println("------");
        });
        System.out.println("=".repeat(100) + "\n");
    }

//    for simulating clients actions
    public void manageStoreCarts() {
        Cart cart1 = new Cart(Cart.CartType.VIRTUAL, 0);
        Cart cart2 = new Cart(Cart.CartType.VIRTUAL, 1);
        Cart cart3 = new Cart(Cart.CartType.PHYSICAL, 0);
        Cart cart4 = new Cart(Cart.CartType.PHYSICAL, 1);

        carts.add(cart1);
        carts.add(cart2);
        carts.add(cart3);
        carts.add(cart4);

        cart1.addItem(aisleInventory.get(Category.CEREAL).get("Flour"), 2);
        cart1.addItem(aisleInventory.get(Category.DAIRY).get("Cheese"), 6);
        cart1.removeItem(aisleInventory.get(Category.DAIRY).get("Cheese"), 3);
        cart1.addItem(aisleInventory.get(Category.MEAT).get("Chicken"), 2);


        cart2.addItem(aisleInventory.get(Category.MEAT).get("Beef"), 80);
        cart2.addItem(aisleInventory.get(Category.MEAT).get("Beef"), 20);
        cart2.addItem(aisleInventory.get(Category.MEAT).get("Pork"), 35);

        cart3.addItem(inventory.get("AGG1930"), 20);
        cart3.addItem(inventory.get("AGG8120"), 110);
        cart3.addItem(inventory.get("XAXAXAX"), 20);

        cart4.addItem(inventory.get("TUY2232"), 2);
        cart4.addItem(inventory.get("ABA1120"), 3);
        cart4.addItem(aisleInventory.get(Category.CEREAL).get("Flax seed"), 1);
    }

    public void CheckoutCarts(NavigableSet<Cart> chartsSetToCheckout) {
        chartsSetToCheckout.forEach(this::CheckoutCarts);
    }

    public void CheckoutCarts(Cart cartToCheckout) {
        System.out.printf("** Checkout for cart %d: \n", cartToCheckout.getId());
        cartToCheckout.
    }
}
