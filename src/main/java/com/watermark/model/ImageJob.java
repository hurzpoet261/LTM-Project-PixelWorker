package com.watermark.model;

import java.sql.Timestamp;

public class ImageJob {
    private int id;
    private int userId;
    private String originalFilename;
    private String originalPath;
    private String watermarkedPath;
    private String status;
    private Timestamp createdAt;

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getOriginalFilename() { return originalFilename; }
    public String getOriginalPath() { return originalPath; }
    public String getWatermarkedPath() { return watermarkedPath; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public void setOriginalPath(String originalPath) { this.originalPath = originalPath; }
    public void setWatermarkedPath(String watermarkedPath) { this.watermarkedPath = watermarkedPath; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}