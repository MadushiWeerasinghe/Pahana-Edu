package edu.pahana.service.dto;
import java.util.List;
public class BillSummaryResponse {
    private int count;
    private double totalSubTotal;
    private double totalTax;
    private double totalGrandTotal;
    private List<AggregatePoint> daily;
    public BillSummaryResponse(){}
    public BillSummaryResponse(int count, double totalSubTotal, double totalTax, double totalGrandTotal, List<AggregatePoint> daily){
        this.count=count; this.totalSubTotal=totalSubTotal; this.totalTax=totalTax; this.totalGrandTotal=totalGrandTotal; this.daily=daily;
    }
    public int getCount(){ return count; }
    public void setCount(int count){ this.count=count; }
    public double getTotalSubTotal(){ return totalSubTotal; }
    public void setTotalSubTotal(double totalSubTotal){ this.totalSubTotal=totalSubTotal; }
    public double getTotalTax(){ return totalTax; }
    public void setTotalTax(double totalTax){ this.totalTax=totalTax; }
    public double getTotalGrandTotal(){ return totalGrandTotal; }
    public void setTotalGrandTotal(double totalGrandTotal){ this.totalGrandTotal=totalGrandTotal; }
    public List<AggregatePoint> getDaily(){ return daily; }
    public void setDaily(List<AggregatePoint> daily){ this.daily=daily; }
}
