/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.sql.table.model;

import com.avi.sql.table.servlet.model.TableServletModel;
import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import com.avi.util.DateFormatter;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Admin
 */

@Table(name = "COMMENT")
public class CommentModel extends TableServletModel {

    public static final String COMMENT_ID = "commentId";
    public static final String USER_ID = "userId";
    public static final String SUBJECT = "subject";
    public static final String COMMENT = "comment";
    public static final String COMMENT_DATE = "commentDate";

    @Column(name = "comment_id",isPK=true, autoIncrement=true)
    private int commentId;
    @Column(name = "user_id")
    @ForeignKey(table=UserModel.class, column=UserModel.ID, onDelete=ForeignKey.CASCADE, onUpdate=ForeignKey.CASCADE)
    private int userId;
    @Column
    private String subject;
    @Column
    private String comment;
    @Column(name = "comment_date")
    private Date commentDate;

    public CommentModel(HttpServletRequest request) throws IllegalArgumentException, Exception {
        super(request);
    }
    
    public CommentModel() {
        
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    } 

    public String getComment_date() {
        return DateFormatter.getCurrentDate();
    } 

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}




