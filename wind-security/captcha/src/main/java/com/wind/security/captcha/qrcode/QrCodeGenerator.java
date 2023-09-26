package com.wind.security.captcha.qrcode;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.wind.security.captcha.picture.PictureGenerator;
import lombok.Data;

import java.awt.*;

/**
 * 图片二维码生成器
 *
 * @author wuxp
 * @date 2023-09-24 13:50
 **/
public interface QrCodeGenerator extends PictureGenerator {


    /**
     * 生成图片二维码
     *
     * @param content 二维码内容
     * @param size    二维码大小
     * @param margin  边距
     * @return 二维码图片
     */
    default Image generate(String content, int size, int margin) {
        return generate(content, QrCodeConfig.ofSize(size, margin));
    }

    /**
     * 生成图片二维码
     *
     * @param content      二维码内容
     * @param qrCodeConfig 二维码配置
     * @return 二维码图片
     */
    default Image generate(String content, QrCodeConfig qrCodeConfig) {
        return generate(content, qrCodeConfig, new MatrixToImageConfig());
    }

    /**
     * 生成图片二维码
     *
     * @param content      二维码内容
     * @param qrCodeConfig 二维码配置
     * @return 二维码图片 base64 编码内容
     */
    default String generateAsBas64(String content, QrCodeConfig qrCodeConfig) {
        return asBase64(generate(content, qrCodeConfig), PNG_IMAGE_FORMAT);
    }

    /**
     * 生成图片二维码
     *
     * @param content      二维码内容
     * @param qrCodeConfig 二维码配置
     * @param imageConfig  矩阵转图像配置
     * @return 二维码图片
     */
    Image generate(String content, QrCodeConfig qrCodeConfig, MatrixToImageConfig imageConfig);


    /**
     * 二维码配置
     */
    @Data
    class QrCodeConfig {

        /**
         * 二维码大小
         */
        private int size = 360;

        /**
         * 二维码边距
         */
        private int margin = 3;

        /**
         * 二维码中间的 logo 的 base64 内容
         */
        private String logo;

        /**
         * logo 的大小
         */
        private int logoSize = 72;

        /**
         * logo 边宽
         */
        private int logoBorderWith = 2;

        /**
         * logo 背景色,rbg 颜色值
         */
        private String logoBorderColor = "#ffffff";

        static QrCodeConfig ofSize(int size, int marin) {
            QrCodeConfig result = new QrCodeConfig();
            result.setSize(size);
            result.setMargin(marin);
            return result;
        }

    }
}
