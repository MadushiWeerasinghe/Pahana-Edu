package edu.pahana.service.dto;
public class CustomerReportRow {
    private int id;
    private String accountNumber;
    private String name;
    private String address;
    private String phone;
    private String createdAt; // ISO string
    private String lastBillAt; // may be null
    public CustomerReportRow(){}
    public CustomerReportRow(int id, String accountNumber, String name, String address, String phone, String createdAt, String lastBillAt){
        this.id=id; this.accountNumber=accountNumber; this.name=name; this.address=address; this.phone=phone; this.createdAt=createdAt; this.lastBillAt=lastBillAt;
    }
    public int getId(){ return id; }
    public void setId(int id){ this.id=id; }
    public String getAccountNumber(){ return accountNumber; }
    public void setAccountNumber(String accountNumber){ this.accountNumber=accountNumber; }
    public String getName(){ return name; }
    public void setName(String name){ this.name=name; }
    public String getAddress(){ return address; }
    public void setAddress(String address){ this.address=address; }
    public String getPhone(){ return phone; }
    public void setPhone(String phone){ this.phone=phone; }
    public String getCreatedAt(){ return createdAt; }
    public void setCreatedAt(String createdAt){ this.createdAt=createdAt; }
    public String getLastBillAt(){ return lastBillAt; }
    public void setLastBillAt(String lastBillAt){ this.lastBillAt=lastBillAt; }
}
