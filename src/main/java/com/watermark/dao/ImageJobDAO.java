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

    public ImageJob getJobById(int jobId) throws SQLException {
        String sql = "SELECT * FROM image_jobs WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToImageJob(rs);
                }
            }
        }
        return null;
    }

    public boolean createJob(ImageJob job) throws SQLException {
        String sql = "INSERT INTO image_jobs (user_id, job_type, job_params, input_filename, input_path, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, job.getUserId());
            ps.setString(2, job.getJobType());
            ps.setString(3, job.getJobParams());
            ps.setString(4, job.getInputFilename());
            ps.setString(5, job.getInputPath());
            ps.setString(6, job.getStatus());
            ps.setTimestamp(7, job.getCreatedAt());
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
                    jobs.add(mapResultSetToImageJob(rs));
                }
            }
        }
        return jobs;
    }

    public synchronized ImageJob getNextPendingJob() throws SQLException {
        String sqlSelect = "SELECT * FROM image_jobs WHERE status = 'PENDING' LIMIT 1 FOR UPDATE";
        String sqlUpdate = "UPDATE image_jobs SET status = 'PROCESSING' WHERE id = ?";
        
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSelect = conn.prepareStatement(sqlSelect)) {
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    ImageJob job = mapResultSetToImageJob(rs);
                    
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

    public void updateJobStatus(int jobId, String status, String outputPath, String outputFilename) throws SQLException {
        String sql = "UPDATE image_jobs SET status = ?, output_path = ?, output_filename = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, outputPath);
            ps.setString(3, outputFilename);
            ps.setInt(4, jobId);
            ps.executeUpdate();
        }
    }

    private ImageJob mapResultSetToImageJob(ResultSet rs) throws SQLException {
        ImageJob job = new ImageJob();
        job.setId(rs.getInt("id"));
        job.setUserId(rs.getInt("user_id"));
        job.setJobType(rs.getString("job_type"));
        job.setJobParams(rs.getString("job_params"));
        job.setInputFilename(rs.getString("input_filename"));
        job.setInputPath(rs.getString("input_path"));
        job.setOutputPath(rs.getString("output_path"));
        job.setOutputFilename(rs.getString("output_filename"));
        job.setStatus(rs.getString("status"));
        job.setCreatedAt(rs.getTimestamp("created_at"));
        return job;
    }
}