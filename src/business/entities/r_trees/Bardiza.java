package business.entities.r_trees;

public class Bardiza {
    private final String type; // SQUARE OR CIRCLE

    private final double size; // Size of the bardiza in meters

    private final double latitude;

    private final double length;

    private final String color;

    public Bardiza(String type, double size, double latitude, double length, String color) {
        this.type = type;
        this.size = size;
        this.latitude = latitude;
        this.length = length;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public double getSize() {
        return size;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLength() {
        return length;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Bardiza {" +
                "Type: " + type + ", " +
                "Size: " + size + ", " +
                "Latitude: " + latitude + ", " +
                "Length: " + length + ", " +
                "Color: " + color +
                "}";
    }
}

