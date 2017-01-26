/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author bala
 */
/**
 * 
 * Contains variables required to display Listing and Tally Field values and Occurrence value
 */
public class BateNumbers {
    
     /** Start bates number for particular document */
     private String startBate = "";
     
     /** End bates number for  particular document */
     private String endBate = "";
     
     /** Field  value for  particular document*/
     private String fieldValue = "";
     
     /** VolumeId*/   
     private int volumeId=0;
     
     /** Volume name for corresponding Project*/     
     private String volumeName = "";
     
     /** ProjectId*/   
     private int projectId = 0;
     
     /** FieldId*/   
     private int fieldId = 0;
     
     /** DocumentId*/   
     private int childId = 0;
     
     /** Document count*/     
     private int documents;
     
     /** Word count*/    
     private int words;
     
     /** Field count*/    
     private int fields;
     
     /** Characters count*/    
     private int characters;
     
      /** Tag count*/    
     private int tags;
     
      /** Field value Occurrence id*/    
     private int listing_occurrence_id;
     
      /** Field marked for listing*/    
     private String view_marking="";
     
     
      
     public String getStartBate() {
        return startBate;
    }

    public void setStartBate(String startBate) {
        this.startBate = startBate;
    }

    public String getEndBate() {
        return endBate;
    }

    public void setEndBate(String endBate) {
        this.endBate = endBate;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    
    public int getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }
    
     public String getVolumeName() {
        return volumeName;
    }

    
    
    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    } 
     public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }
    
     public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }
    
     public int getDocuments() {
        return documents;
    }

    public void setDocuments(int documents) {
        this.documents = documents;
    }
    
     public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }
    
     public int getFields() {
        return fields;
    }

    public void setFields(int fields) {
        this.fields = fields;
    }
    
    public int getCharacters() {
        return characters;
    }

    public void setCharacters(int characters) {
        this.characters = characters;
    }
    
    public int getTags() {
        return tags;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

   public int getListing_occurrence_id()
   {
      return listing_occurrence_id;
   }

   public void setListing_occurrence_id(int listing_occurrence_id)
   {
      this.listing_occurrence_id = listing_occurrence_id;
   }

    public String getView_marking() {
        return view_marking;
    }

    public void setView_marking(String view_marking) {
        this.view_marking = view_marking;
    }
    
}
