package models.shop;

import java.io.Serializable;

public class Service implements Serializable {
    public int id;
    public int userId;
    public String name;
    public String description;
    public String images;
    public Float price;
    public StateTax tax;
    public int duration;
    public int taxId;
    public String discountType;
    public boolean active;
    public Float discount;
    public boolean deleted;
}
