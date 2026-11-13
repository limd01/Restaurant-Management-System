/** One food or drink option on the restaurant menu. */
public class MenuItem {
    private String name;
    private double price;
    private int waitTime;

    public MenuItem(String name, double price, int waitTime) {
        setName(name);
        setPrice(price);
        setWaitTime(waitTime);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()
                || !name.trim().matches("[A-Za-z0-9 ]+")) {
            throw new IllegalArgumentException(
                "Menu item name must use letters, numbers, and spaces.");
        }
        this.name = name.trim();
    }

    public void setPrice(double price) {
        if (price <= 0 || Double.isNaN(price) || Double.isInfinite(price)) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        this.price = price;
    }

    public void setWaitTime(int waitTime) {
        if (waitTime <= 0) {
            throw new IllegalArgumentException("Prep time must be greater than zero.");
        }
        this.waitTime = waitTime;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price)
            + " (" + waitTime + " min)";
    }
}
