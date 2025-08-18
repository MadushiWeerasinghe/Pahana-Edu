package edu.pahana.service.model;

public class Item {
    private int id;
    private String code;
    private String description;
    private double unitPrice;

    public Item(){}
    public Item(int id, String code, String description, double unitPrice){
        this.id=id; this.code=code; this.description=description; this.unitPrice=unitPrice;
    }

    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }
    public String getCode(){ return code; }
    public void setCode(String code){ this.code=code; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description=description; }
    public double getUnitPrice(){ return unitPrice; }
    public void setUnitPrice(double unitPrice){ this.unitPrice=unitPrice; }
}
