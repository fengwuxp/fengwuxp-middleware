package com.wind.security.captcha.picture;

import org.apache.commons.lang3.RandomUtils;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

/**
 * @author wuxp
 * @date 2023-09-24 12:49
 **/
public class SimplePictureGenerator implements PictureGenerator {

    /**
     * 常用颜色
     */
    private static final int[][] RGB_COLORS = {
            {0, 135, 255},
            {51, 153, 51},
            {255, 102, 102},
            {255, 153, 0},
            {153, 102, 0},
            {153, 102, 153},
            {51, 153, 153},
            {102, 102, 255},
            {0, 102, 204},
            {204, 51, 51},
            {0, 153, 204},
            {0, 51, 102}
    };

    /**
     * 默认字体
     */
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 32);

    /**
     * 默认的中文字体
     */
    private static final Font DEFAULT_CHINESE_FONT = new Font("楷体", Font.PLAIN, 28);

    /**
     * 判断是否为中文的正则
     */
    private static final Pattern CHINESE_REGX = Pattern.compile("[一-龥]");


    @Override
    public Image generate(String content, int width, int height) {
        char[] chars = content.toCharArray();
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) bi.getGraphics();
        // 填充背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画干扰圆
        drawOval(g2d, width, height);
        // 画干扰线
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        drawBesselLine(g2d, width, height);
        // 画字符串
        g2d.setFont(captchaTextFont(content));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        // 每一个字符所占的宽度
        int fW = width / chars.length;
        // 字符的左右边距
        int fSp = (fW - (int) fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;
        for (int i = 0; i < chars.length; i++) {
            g2d.setColor(captchaTextColor());
            // 文字的纵坐标
            int fY = height - ((height - (int) fontMetrics.getStringBounds(String.valueOf(chars[i]), g2d).getHeight()) >> 1);
            g2d.drawString(String.valueOf(chars[i]), i * fW + fSp + 3, fY - 3);
        }
        g2d.dispose();
        return bi;
    }


    /**
     * 随机画干扰圆
     *
     * @param g      Graphics2D
     * @param width  图片宽度
     * @param height 图片高度
     */
    private void drawOval(Graphics2D g, int width, int height) {
        int num = nextInt(6, 9);
        for (int i = 0; i < num; i++) {
            g.setColor(interferenceLineColor());
            int w = 5 + nextInt(8);
            g.drawOval(
                    nextInt(width - 25),
                    nextInt(height - 15),
                    w,
                    w);
        }
    }

    /**
     * 随机画贝塞尔曲线
     *
     * @param g      Graphics2D
     * @param width  宽度
     * @param height 高度
     */
    private void drawBesselLine(Graphics2D g, int width, int height) {
        int num = nextInt(1, 2);
        for (int i = 0; i < num; i++) {
            g.setColor(bezierCurveColor());
            int x1 = 5, y1 = nextInt(5, height / 2);
            int x2 = width - 5, y2 = nextInt(height / 2, height - 5);
            int ctrlX = nextInt(width / 4, width / 4 * 3), ctrlY = nextInt(5, height - 5);
            if (nextInt(2) == 0) {
                int ty = y1;
                y1 = y2;
                y2 = ty;
            }
            if (nextInt(2) == 0) {
                // 二阶贝塞尔曲线
                QuadCurve2D shape = new QuadCurve2D.Double();
                shape.setCurve(x1, y1, ctrlX, ctrlY, x2, y2);
                g.draw(shape);
            } else {
                // 三阶贝塞尔曲线
                int ctrlX1 = nextInt(width / 4, width / 4 * 3), ctrlY1 = nextInt(5, height - 5);
                CubicCurve2D shape = new CubicCurve2D.Double(x1, y1, ctrlX, ctrlY, ctrlX1, ctrlY1, x2, y2);
                g.draw(shape);
            }
        }
    }

    private Color backgroundColor() {
        int[] color = RGB_COLORS[nextInt(RGB_COLORS.length)];
        return new Color(color[0], color[1], color[2]);
    }


    private Color captchaTextColor() {
        return backgroundColor();
    }


    private Color interferenceLineColor() {
        return backgroundColor();
    }


    private Color bezierCurveColor() {
        return backgroundColor();
    }

    /**
     * @param text 内容
     * @return 是否为中文
     */
    private boolean isChinese(String text) {
        return CHINESE_REGX.matcher(text).find();
    }

    private Font captchaTextFont(String text) {
        if (this.isChinese(text)) {
            return DEFAULT_CHINESE_FONT;
        }
        return DEFAULT_FONT;
    }

    /**
     * 产生 min ~ max 的随机数,不包括 max
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    private int nextInt(int min, int max) {
        return RandomUtils.nextInt(min, max);
    }

    /**
     * 产生 0 ~ max 的随机数,不包括 num
     *
     * @param max 最大值
     * @return 随机数
     */
    private int nextInt(int max) {
        return nextInt(0, max);
    }

}
