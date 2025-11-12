package com.watermark.worker;

import com.watermark.dao.ImageJobDAO;
import com.watermark.model.ImageJob;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class JobScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Lên lịch chạy JobWorker mỗi 10 giây
        scheduler.scheduleAtFixedRate(new JobWorker(), 0, 10, TimeUnit.SECONDS);
        System.out.println(">>> JobScheduler ĐÃ KHỞI ĐỘNG <<<");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdown();
        System.out.println(">>> JobScheduler ĐÃ TẮT <<<");
    }

    // Tác vụ chạy ngầm
    class JobWorker implements Runnable {
        private ImageJobDAO jobDAO = new ImageJobDAO();
        private WatermarkService watermarkService = new WatermarkService();

        @Override
        public void run() {
            try {
                System.out.println("[Worker] Đang tìm job...");
                ImageJob job = jobDAO.getNextPendingJob();
                
                if (job != null) {
                    System.out.println("[Worker] Đã tìm thấy job ID: " + job.getId());
                    try {
                        String watermarkedPath = watermarkService.addWatermark(job);
                        jobDAO.updateJobStatus(job.getId(), "COMPLETED", watermarkedPath);
                        System.out.println("[Worker] Job ID " + job.getId() + " đã hoàn thành.");
                        
                    } catch (Exception e) {
                        System.err.println("[Worker] Lỗi khi xử lý job ID " + job.getId() + ": " + e.getMessage());
                        jobDAO.updateJobStatus(job.getId(), "FAILED", null);
                    }
                } else {
                    // System.out.println("[Worker] Không có job nào đang chờ.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}