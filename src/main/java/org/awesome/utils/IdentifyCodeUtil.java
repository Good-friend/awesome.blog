package org.awesome.utils;

import org.awesome.models.IdentifyCode;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

/**
 * 验证码生成工具
 */
@Component
public class IdentifyCodeUtil {

    /**
     * 图片的宽度
     */
    private int width = 160;

    /**
     * 图片的高度
     */
    private int height = 40;

    /**
     * 验证码字符个数
     */
    private int codeCount = 4;

    /**
     * 验证码干扰线数
     */
    private int lineCount = 20;

    /**
     * 随机数
     */
    SecureRandom random = new SecureRandom();

    /**
     * 生成默认定义的图片
     */
    public IdentifyCodeUtil() {
    }

    /**
     * 自定义图片宽和高
     *
     * @param width
     * @param height
     */
    public IdentifyCodeUtil(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 自定义图片宽、高和字符个数
     *
     * @param width
     * @param height
     * @param codeCount
     */
    public IdentifyCodeUtil(int width, int height, int codeCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
    }

    /**
     * 自定义宽、高、字符个数和干扰线条数
     *
     * @param width
     * @param height
     * @param codeCount
     * @param lineCount
     */
    public IdentifyCodeUtil(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
    }

    /**
     * 生成图片
     */
    public BufferedImage creatImage(IdentifyCode identifyCode) {
        int fontWidth = width / codeCount;// 字体的宽度
        int fontHeight = height - 5;// 字体的高度
        int codeY = height - 8;

        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImg.getGraphics();
        // 设置背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        // 设置字体
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        g.setFont(font);

        // 设置干扰线
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }

        // 添加噪点
        float yawpRate = 0.01f;// 噪声率
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            buffImg.setRGB(x, y, random.nextInt(255));
        }

        g.setColor(getRandColor(1, 255));
        g.drawString(identifyCode.getOp1() + "", 0 * fontWidth + 3, codeY);
        g.drawString(identifyCode.getOp() + "", 1 * fontWidth + 3, codeY);
        g.drawString(identifyCode.getOp2() + "", 2 * fontWidth + 3, codeY);
        g.drawString("=", 3 * fontWidth + 3, codeY);

        return buffImg;
    }


    // 得到随机颜色
    private Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 产生随机字体
     */
    private Font getFont(int size) {
        Random random = new Random();
        Font font[] = new Font[5];
        font[0] = new Font("Ravie", Font.PLAIN, size);
        font[1] = new Font("Antique Olive Compact", Font.PLAIN, size);
        font[2] = new Font("Fixedsys", Font.PLAIN, size);
        font[3] = new Font("Wide Latin", Font.PLAIN, size);
        font[4] = new Font("Gill Sans Ultra Bold", Font.PLAIN, size);
        return font[random.nextInt(5)];
    }

    /**
     * 扭曲方法
     *
     * @param g
     * @param w1
     * @param h1
     * @param color
     */
    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private void shearX(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(2);

        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);

        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }

    }

    private void shearY(Graphics g, int w1, int h1, Color color) {

        int period = random.nextInt(40) + 10; // 50;

        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }

        }

    }
}
