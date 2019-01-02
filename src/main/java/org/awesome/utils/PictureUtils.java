package org.awesome.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@Component
public class PictureUtils {

    /**
     * 创建固定大小缩略图
     *
     * @param srcFile  原图
     * @param destFile 目标图
     * @param height   缩略图高度
     * @param width    缩略图宽度
     * @throws IOException
     */
    public void CreateThumbnail(String srcFile, String destFile, int height, int width) throws IOException {
        Thumbnails.of(srcFile).size(height, width).toFile(destFile);
    }

    /**
     * 创建固定大小、旋转、加水印缩略图
     *
     * @param srcFile       原图
     * @param destFile      目标图
     * @param height        缩略图高度
     * @param width         缩略图宽度
     * @param rotate        旋转角度
     * @param watermarkFile 水印图
     * @throws IOException
     */
    public void CreateThumbnailWithRotationWatermark(String srcFile, String destFile, int height, int width, int rotate, String watermarkFile) throws IOException {
        Thumbnails.of(new File(srcFile))
                .size(height, width)
                .rotate(rotate)
                .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(watermarkFile)), 0.5f)
                .outputQuality(0.8)
                .toFile(destFile);
    }
}
