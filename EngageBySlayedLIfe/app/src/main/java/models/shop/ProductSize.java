package models.shop;

import java.io.Serializable;

public class ProductSize implements Serializable {
    public int id;
    public int productId;
    public String size;
    public boolean active;
    public int quantity;
}
