package com.watermark.controller;

import com.watermark.dao.ImageJobDAO;
import com.watermark.model.ImageJob;
import com.watermark.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
    
    private ImageJobDAO jobDAO = new ImageJobDAO();
    private static final String UPLOAD_DIR = "E:/LTM-jsp/uploads/input/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            User user = (User) session.getAttribute("user");
            String jobType = request.getParameter("job_type");
            String jobParams = request.getParameter("job_params");

            String uploadMode = request.getParameter("upload_mode");

            List<Part> fileParts = request.getParts().stream()
                    .filter(part -> "imageFile".equals(part.getName()) && part.getSize() > 0)
                    .collect(Collectors.toList());

            if (fileParts.isEmpty()) {
                throw new ServletException("Không có file nào được upload.");
            }

            switch (uploadMode) {
                case "single_image":
                case "single_zip":
                    processSingleFile(fileParts.get(0), user, jobType, jobParams);
                    break;
                case "multi_image":
                    processMultipleFiles(fileParts, user, jobType, jobParams);
                    break;
                default:
                    throw new ServletException("Chế độ upload không hợp lệ.");
            }

            response.sendRedirect("history");

        } catch (Exception e) {
            throw new ServletException("Lỗi nghiêm trọng khi upload file", e);
        }
    }

    private void processSingleFile(Part filePart, User user, String jobType, String jobParams) throws IOException, SQLException {
        String originalFilename = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        String savedPath = UPLOAD_DIR + uniqueFilename;
        
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();
        
        filePart.write(savedPath); 

        ImageJob job = new ImageJob();
        job.setUserId(user.getId());
        job.setInputFilename(originalFilename);
        job.setInputPath(savedPath);
        
        if (originalFilename.toLowerCase().endsWith(".zip")) {
            job.setJobType("ZIP_" + jobType); 
        } else {
            job.setJobType(jobType); 
        }
        
        job.setJobParams(jobParams);
        job.setStatus("PENDING");
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        jobDAO.createJob(job);
    }

   
    private void processMultipleFiles(List<Part> fileParts, User user, String jobType, String jobParams) throws IOException, SQLException {
        
        String zipFilename = "batch_" + System.currentTimeMillis() + ".zip";
        String savedZipPath = UPLOAD_DIR + zipFilename;

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(savedZipPath))) {
            for (Part part : fileParts) {
                String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                
                zout.putNextEntry(new ZipEntry(filename));
                try (InputStream in = part.getInputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        zout.write(buffer, 0, bytesRead);
                    }
                }
                zout.closeEntry();
            }
        }

        ImageJob job = new ImageJob();
        job.setUserId(user.getId());
        job.setInputFilename(zipFilename); 
        job.setInputPath(savedZipPath);   
        job.setJobType("ZIP_" + jobType); 
        job.setJobParams(jobParams);      
        job.setStatus("PENDING");
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        jobDAO.createJob(job);
    }
}