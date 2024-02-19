package models.shop;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    public int id;
    public int userId;
    public String name;
    public String description;
    public String sku;
    public String images;
    public Float price;
    public StateTax tax;
    public int taxId;
    public String shippingType;
    public String discountType;
    public Float shipping;
    public boolean active;
    public Float discount;
    public boolean deleted;
    public List<ProductSize> productSize;
}
