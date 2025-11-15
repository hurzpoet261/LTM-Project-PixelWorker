package com.watermark.worker;

import com.watermark.dao.ImageJobDAO;
import com.watermark.model.ImageJob;
import com.watermark.service.GrayscaleService;
import com.watermark.service.ResizeService;
import com.watermark.service.WatermarkService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;

@WebListener
public class JobScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new JobWorker(), 0, 5, TimeUnit.SECONDS); 
        System.out.println(">>> JobScheduler (V2 - Full) ĐÃ KHỞI ĐỘNG <<<");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
        System.out.println(">>> JobScheduler (V2 - Full) ĐÃ TẮT <<<");
    }

    class JobWorker implements Runnable {
        private ImageJobDAO jobDAO = new ImageJobDAO();
        private WatermarkService watermarkService = new WatermarkService();
        private ResizeService resizeService = new ResizeService();
        private GrayscaleService grayscaleService = new GrayscaleService();
        
        private static final String PROCESSED_DIR = "E:/LTM-jsp/uploads/processed/";

        public JobWorker() {
             File dir = new File(PROCESSED_DIR);
             if (!dir.exists()) dir.mkdirs();
        }

        @Override
        public void run() {
            ImageJob job = null;
            try {
                job = jobDAO.getNextPendingJob();
                if (job == null) {
                    return;
                }

                System.out.println("[Worker] Đang xử lý job ID: " + job.getId() + ", Type: " + job.getJobType());
                
                String outputFilename = "processed_" + System.currentTimeMillis() + "_" + job.getInputFilename();
                String outputPath = PROCESSED_DIR + outputFilename;
                
                if(job.getJobType().startsWith("ZIP_")) {
                    outputFilename = "processed_" + System.currentTimeMillis() + ".zip";
                    outputPath = PROCESSED_DIR + outputFilename;
                }

                switch (job.getJobType()) {
                    case "WATERMARK":
                    case "RESIZE":
                    case "GRAYSCALE":
                        processSingleImage(job, outputPath, outputFilename);
                        break;
                    case "ZIP_WATERMARK":
                    case "ZIP_RESIZE":
                    case "ZIP_GRAYSCALE":
                        processZipFile(job, outputPath, outputFilename);
                        break;
                    default:
                        throw new Exception("Loại job không xác định: " + job.getJobType());
                }
                
                jobDAO.updateJobStatus(job.getId(), "COMPLETED", outputPath, outputFilename);
                System.out.println("[Worker] Job ID " + job.getId() + " đã hoàn thành.");

            } catch (Exception e) {
                System.err.println("[Worker] Lỗi khi xử lý job ID " + (job != null ? job.getId() : "UNKNOWN") + ": " + e.getMessage());
                e.printStackTrace();
                if (job != null) {
                    try {
                        jobDAO.updateJobStatus(job.getId(), "FAILED", null, null);
                    } catch (SQLException sqlEx) {
                        sqlEx.printStackTrace();
                    }
                }
            }
        }
        
        private void processSingleImage(ImageJob job, String outputPath, String outputFilename) throws IOException, NumberFormatException {
            File inputFile = new File(job.getInputPath());
            if (!inputFile.exists()) throw new IOException("File đầu vào không tồn tại: " + job.getInputPath());
            
            BufferedImage img = ImageIO.read(inputFile);
            BufferedImage processedImg = null;
            String extension = "png"; 

            switch (job.getJobType()) {
                case "WATERMARK":
                    processedImg = watermarkService.addWatermark(img, job.getJobParams());
                    break;
                case "RESIZE":
                    int width = Integer.parseInt(job.getJobParams()); 
                    processedImg = resizeService.resizeImage(img, width);
                    break;
                case "GRAYSCALE":
                    processedImg = grayscaleService.convertToGrayscale(img);
                    break;
            }
            ImageIO.write(processedImg, extension, new File(outputPath));
        }
        
        private void processZipFile(ImageJob job, String outputPath, String outputFilename) throws IOException, NumberFormatException {
            String innerJobType = job.getJobType().replace("ZIP_", "");
            
            try (ZipInputStream zin = new ZipInputStream(new FileInputStream(job.getInputPath()));
                 ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(outputPath))) {
                
                ZipEntry entry;
                while ((entry = zin.getNextEntry()) != null) {
                    if (!entry.isDirectory() && (entry.getName().toLowerCase().endsWith(".png") || entry.getName().toLowerCase().endsWith(".jpg"))) {
                        BufferedImage img = ImageIO.read(zin);
                        BufferedImage processedImg = null;
                        
                        switch (innerJobType) {
                            case "WATERMARK":
                                processedImg = watermarkService.addWatermark(img, job.getJobParams());
                                break;
                            case "RESIZE":
                                int width = Integer.parseInt(job.getJobParams());
                                processedImg = resizeService.resizeImage(img, width);
                                break;
                            case "GRAYSCALE":
                                processedImg = grayscaleService.convertToGrayscale(img);
                                break;
                        }
                        
                        ZipEntry newEntry = new ZipEntry(entry.getName().replaceAll("\\.\\w+$", ".png")); 
                        zout.putNextEntry(newEntry);
                        ImageIO.write(processedImg, "png", zout); 
                        zout.closeEntry();
                    }
                    zin.closeEntry();
                }
            }
        }
    }
}