package com.watermark.service;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class GrayscaleService {
    
    public BufferedImage convertToGrayscale(BufferedImage originalImage) {
        BufferedImage grayImage = new BufferedImage(
            originalImage.getWidth(),
            originalImage.getHeight(),
            BufferedImage.TYPE_BYTE_GRAY
        );
        ColorConvertOp op = new ColorConvertOp(
            originalImage.getColorModel().getColorSpace(),
            grayImage.getColorModel().getColorSpace(),
            null
        );
        op.filter(originalImage, grayImage);
        return grayImage;
    }
}