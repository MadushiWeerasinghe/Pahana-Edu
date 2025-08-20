package edu.pahana.service.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int id;
    private int customerId;
    private LocalDateTime createdAt;
    private List<BillItem> items = new ArrayList<>();
    private double subTotal;
    private double tax;
    private double grandTotal;

    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }
    public int getCustomerId(){ return customerId; }
    public void setCustomerId(int customerId){ this.customerId=customerId; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt=createdAt; }
    public List<BillItem> getItems(){ return items; }
    public void setItems(List<BillItem> items){ this.items=items; }
    public double getSubTotal(){ return subTotal; }
    public void setSubTotal(double subTotal){ this.subTotal=subTotal; }
    public double getTax(){ return tax; }
    public void setTax(double tax){ this.tax=tax; }
    public double getGrandTotal(){ return grandTotal; }
    public void setGrandTotal(double grandTotal){ this.grandTotal=grandTotal; }
}
