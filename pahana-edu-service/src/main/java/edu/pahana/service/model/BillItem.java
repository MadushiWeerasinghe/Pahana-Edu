package edu.pahana.service.model;

public class BillItem {
    private int itemId;
    private int quantity;
    private double unitPrice;
    private String description;

    public BillItem(){}
    public BillItem(int itemId, int quantity, double unitPrice, String description){
        this.itemId=itemId; this.quantity=quantity; this.unitPrice=unitPrice; this.description=description;
    }
    public int getItemId(){ return itemId; }
    public void setItemId(int itemId){ this.itemId=itemId; }
    public int getQuantity(){ return quantity; }
    public void setQuantity(int quantity){ this.quantity=quantity; }
    public double getUnitPrice(){ return unitPrice; }
    public void setUnitPrice(double unitPrice){ this.unitPrice=unitPrice; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description=description; }

    public double getLineTotal(){ return unitPrice * quantity; }
}
