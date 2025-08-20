package edu.pahana.service.dto;
public class AggregatePoint {
    private String label;
    private String periodStart;
    private String periodEnd;
    private double total;
    public AggregatePoint(){}
    public AggregatePoint(String label, String periodStart, String periodEnd, double total){
        this.label=label; this.periodStart=periodStart; this.periodEnd=periodEnd; this.total=total;
    }
    public String getLabel(){ return label; }
    public void setLabel(String label){ this.label=label; }
    public String getPeriodStart(){ return periodStart; }
    public void setPeriodStart(String periodStart){ this.periodStart=periodStart; }
    public String getPeriodEnd(){ return periodEnd; }
    public void setPeriodEnd(String periodEnd){ this.periodEnd=periodEnd; }
    public double getTotal(){ return total; }
    public void setTotal(double total){ this.total=total; }
}
