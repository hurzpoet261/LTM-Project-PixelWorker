package com.watermark.model;

import java.sql.Timestamp;

public class ImageJob {
    private int id;
    private int userId;
    private String jobType; 
    private String jobParams; 
    private String inputFilename; 
    private String inputPath; 
    private String outputPath; 
    private String outputFilename; 
    private String status;
    private Timestamp createdAt;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getJobType() { return jobType; }
    public String getJobParams() { return jobParams; }
    public String getInputFilename() { return inputFilename; }
    public String getInputPath() { return inputPath; }
    public String getOutputPath() { return outputPath; }
    public String getOutputFilename() { return outputFilename; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public void setJobParams(String jobParams) { this.jobParams = jobParams; }
    public void setInputFilename(String inputFilename) { this.inputFilename = inputFilename; }
    public void setInputPath(String inputPath) { this.inputPath = inputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
    public void setOutputFilename(String outputFilename) { this.outputFilename = outputFilename; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}