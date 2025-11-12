package com.watermark.worker;

import com.watermark.model.ImageJob;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WatermarkService {
    
    // *** ĐẢM BẢO THƯ MỤC NÀY TỒN TẠI TRÊN MÁY BẠN ***
    private static final String WATERMARKED_DIR = "E:/LTM-jsp/uploads/watermarked/";
    
    public WatermarkService() {
        File dir = new File(WATERMARKED_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public String addWatermark(ImageJob job) throws IOException {
        File sourceFile = new File(job.getOriginalPath());
        BufferedImage image = ImageIO.read(sourceFile);
        
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        
        String watermarkText = "Copyright by " + job.getUserId();
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        
        int x = image.getWidth() - g2d.getFontMetrics().stringWidth(watermarkText) - 20;
        int y = image.getHeight() - 20;
        
        g2d.drawString(watermarkText, x, y);
        g2d.dispose();
        
        String outputFilename = "wm_" + new File(job.getOriginalPath()).getName();
        String outputPath = WATERMARKED_DIR + outputFilename;
        File destFile = new File(outputPath);
        
        ImageIO.write(image, "png", destFile);
        
        return outputPath;
    }
}