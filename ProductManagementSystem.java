import java.io.*;
import java.util.*;

/**
 * E-Commerce Product Management System (PS No. 13)
 * Concepts: HashMap, ArrayList, Sorting, Searching, Methods, File Handling
 * Run: javac ProductManagementSystem.java && java ProductManagementSystem
 */
public class ProductManagementSystem {

    // ---------- Product class ----------
    static class Product {
        String id;
        String name;
        String category;
        int price;
        int stock;

        Product(String id, String name, String category, int price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        String details() {
            return id + " | " + name + " | " + category + " | " + price + " | Stock: " + stock;
        }
    }

    // ---------- Data ----------
    private static final String FILE_NAME = "products.txt";
    private static final int FIRST_ID = 101;
    private static HashMap<String, Product> products = new HashMap<>();
    private static int nextIdNumber = FIRST_ID;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        loadFromFile();

        while (true) {
            printMenu();
            int option = readInt(sc, "Enter option: ");
            if (option == 6) {
                saveToFile();
                System.out.println("Products saved. Goodbye!");
                break;
            }
            switch (option) {
                case 1: addProduct(sc); break;
                case 2: displayAll(); break;
                case 3: searchProduct(sc); break;
                case 4: sortByPrice(); break;
                case 5: updateStock(sc); break;
                default: System.out.println("Invalid option. Choose 1 to 6.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n===== E-Commerce Product Management System =====");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. Search Product");
        System.out.println("4. Sort by Price (Low to High)");
        System.out.println("5. Update Stock");
        System.out.println("6. Exit");
    }


    private static void addProduct(Scanner sc) {
        System.out.print("Product name: ");
        String name = clean(sc.nextLine());
        System.out.print("Category: ");
        String category = clean(sc.nextLine());
        if (name.isEmpty() || category.isEmpty()) {
            System.out.println("Name and category cannot be empty.");
            return;
        }
        int price = readInt(sc, "Price: ");
        if (price <= 0) {
            System.out.println("Invalid price. Price must be greater than 0.");
            return;
        }
        int stock = readInt(sc, "Stock: ");
        if (stock < 0) {
            System.out.println("Invalid stock. Stock must not be negative.");
            return;
        }
        String id = "P" + nextIdNumber++;
        Product p = new Product(id, name, category, price, stock);
        products.put(id, p);
        System.out.println("Product added. ID: " + p.details());
    }

    private static void displayAll() {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        for (Product p : sortedById()) {
            System.out.println(p.details());
        }
    }

    private static void searchProduct(Scanner sc) {
        System.out.println("Search by: 1. ID  2. Category");
        int type = readInt(sc, "Enter choice: ");
        if (type == 1) {
            searchById(sc);
        } else if (type == 2) {
            searchByCategory(sc);
        } else {
            System.out.println("Invalid choice.");
        }
    }


    private static void searchById(Scanner sc) {
        System.out.print("Product ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        Product p = products.get(id);
        if (p == null) {
            System.out.println("Product " + id + " not found.");
        } else {
            System.out.println("Found: " + p.details());
        }
    }


    private static void searchByCategory(Scanner sc) {
        System.out.print("Category: ");
        String category = sc.nextLine().trim();
        boolean found = false;
        for (Product p : sortedById()) {
            if (p.category.equalsIgnoreCase(category)) {
                System.out.println(p.id + " | " + p.name + " | " + p.price);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No products found in category " + category + ".");
        }
    }

    private static void sortByPrice() {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        ArrayList<Product> list = sortedById();
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product a, Product b) {
                return Integer.compare(a.price, b.price);
            }
        });
        int rank = 1;
        for (Product p : list) {
            System.out.println(rank++ + ". " + p.id + " | " + p.price);
        }
    }

    private static void updateStock(Scanner sc) {
        System.out.print("Product ID: ");
        String id = sc.nextLine().trim().toUpperCase();
        Product p = products.get(id);
        if (p == null) {
            System.out.println("Product " + id + " not found.");
            return;
        }
        int newStock = readInt(sc, "New stock: ");
        if (newStock < 0) {
            System.out.println("Invalid stock. Stock must not be negative.");
            return;
        }
        p.stock = newStock;
        if (newStock == 0) {
            System.out.println("Stock updated. " + id + " is Out of Stock.");
        } else {
            System.out.println("Stock updated. " + id + " | Stock: " + newStock);
        }
    }

    private static ArrayList<Product> sortedById() {
        ArrayList<Product> list = new ArrayList<>(products.values());
        Collections.sort(list, new Comparator<Product>() {
            @Override
            public int compare(Product a, Product b) {
                return Integer.compare(Integer.parseInt(a.id.substring(1)),
                                       Integer.parseInt(b.id.substring(1)));
            }
        });
        return list;
    }

    private static String clean(String s) {
        return s.replace("|", " ").trim();
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;   // first run: start with an empty store
        }
        int maxId = FIRST_ID - 1;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;                 // skip malformed line
                try {
                    String id = parts[0].trim();
                    int price = Integer.parseInt(parts[3].trim());
                    int stock = Integer.parseInt(parts[4].trim());
                    int num = Integer.parseInt(id.substring(1));
                    if (price <= 0 || stock < 0) continue;       // skip invalid data
                    products.put(id, new Product(id, parts[1].trim(), parts[2].trim(), price, stock));
                    if (num > maxId) maxId = num;
                } catch (RuntimeException e) {
                    // malformed line: ignore it
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
        nextIdNumber = maxId + 1;
    }

    private static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Product p : sortedById()) {
                pw.println(p.id + "|" + p.name + "|" + p.category + "|" + p.price + "|" + p.stock);
            }
        } catch (IOException e) {
            System.out.println("Could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }
}

