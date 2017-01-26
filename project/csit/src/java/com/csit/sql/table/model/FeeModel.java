/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.sql.table.model;

import com.avi.sql.table.servlet.model.TableServletModel;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
 
/**
 *
 * @author Admin 
 */
@Table(name = "FEE")
public class FeeModel extends TableServletModel{
    
    public static final String FEE_ID = "feeId";
    public static final String STUDENT_ID = "studentId";
    public static final String SEM_ID = "semId";
    public static final String AMOUNT = "amount"; 
    public static final String DUE_DATE = "dueDate";
    public static final String DEPOSITE_DATE = "depositeDate";
    public static final String LATE_FEE = "lateFee";

    @Column(name ="fee_id",isPK=true, autoIncrement=true, notNull=true)
    private int feeId;
    @Column(name ="student_id")
    @ForeignKey(table=StudentModel.class, column=StudentModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    private int studentId;
    @Column(name ="sem_id")
    private int semId;
    @Column 
    private double amount;
    @Column(name ="late_fee")
    private double lateFee;
    @Column(name ="due_date")
    private Date dueDate;
    @Column(name ="d_date")
    private Date depositeDate;

    public FeeModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request); 
    }

    public FeeModel() {
        
    }

    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDepositeDate() {
        return depositeDate;
    }

    public void setDepositeDate(Date depositeDate) {
        this.depositeDate = depositeDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
   
    public double getLateFee() {
        return lateFee;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }

    public int getSemId() {
        return semId;
    }

    public void setSemId(int semId) {
        this.semId = semId;
    }
 
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

}
