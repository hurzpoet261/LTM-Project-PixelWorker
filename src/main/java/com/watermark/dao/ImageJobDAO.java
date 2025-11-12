package com.watermark.dao;

import com.watermark.model.ImageJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImageJobDAO {

    public boolean createJob(ImageJob job) throws SQLException {
        String sql = "INSERT INTO image_jobs (user_id, original_filename, original_path, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, job.getUserId());
            ps.setString(2, job.getOriginalFilename());
            ps.setString(3, job.getOriginalPath());
            ps.setString(4, job.getStatus());
            ps.setTimestamp(5, job.getCreatedAt());
            return ps.executeUpdate() > 0;
        }
    }

    public List<ImageJob> getJobsByUser(int userId) throws SQLException {
        List<ImageJob> jobs = new ArrayList<>();
        String sql = "SELECT * FROM image_jobs WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImageJob job = new ImageJob();
                    job.setId(rs.getInt("id"));
                    job.setUserId(rs.getInt("user_id"));
                    job.setOriginalFilename(rs.getString("original_filename"));
                    job.setOriginalPath(rs.getString("original_path"));
                    job.setWatermarkedPath(rs.getString("watermarked_path"));
                    job.setStatus(rs.getString("status"));
                    job.setCreatedAt(rs.getTimestamp("created_at"));
                    jobs.add(job);
                }
            }
        }
        return jobs;
    }

    public synchronized ImageJob getNextPendingJob() throws SQLException {
        String sqlSelect = "SELECT * FROM image_jobs WHERE status = 'PENDING' LIMIT 1 FOR UPDATE";
        String sqlUpdate = "UPDATE image_jobs SET status = 'PROCESSING' WHERE id = ?";
        
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    ImageJob job = new ImageJob();
                    job.setId(rs.getInt("id"));
                    job.setUserId(rs.getInt("user_id"));
                    job.setOriginalFilename(rs.getString("original_filename"));
                    job.setOriginalPath(rs.getString("original_path"));
                    
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                        psUpdate.setInt(1, job.getId());
                        psUpdate.executeUpdate();
                    }
                    
                    conn.commit();
                    return job;
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
        return null;
    }

    public void updateJobStatus(int jobId, String status, String watermarkedPath) throws SQLException {
        String sql = "UPDATE image_jobs SET status = ?, watermarked_path = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, watermarkedPath);
            ps.setInt(3, jobId);
            ps.executeUpdate();
        }
    }
}