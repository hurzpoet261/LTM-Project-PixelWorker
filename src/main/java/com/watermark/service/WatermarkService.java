package com.watermark.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class WatermarkService {
    
    public BufferedImage addWatermark(BufferedImage image, String text) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        
        g2d.setColor(new Color(255, 0, 0, 80)); 
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        
        int x = image.getWidth() - g2d.getFontMetrics().stringWidth(text) - 20;
        int y = image.getHeight() - 20;
        
        g2d.drawString(text, x, y);
        g2d.dispose();
        
        return image;
    }
}