package com.jiawa.train.business.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.jiawa.train.business.util.RedisUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
@RestController
@RequestMapping("/kaptcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final RedisUtil redisUtil;
    private final DefaultKaptcha defaultCaptcha;

    @GetMapping("/image-code/{imageCodeToken}")
    public void imageCode(@PathVariable(value = "imageCodeToken") String imageCodeToken, HttpServletResponse httpServletResponse) throws Exception{
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            // 生成验证码字符串
            String createText = defaultCaptcha.createText();

            // 将生成的验证码放入redis缓存中，后续验证的时候用到
            redisUtil.set(imageCodeToken, createText, 300);

            // 使用验证码字符串生成验证码图片
            BufferedImage challenge = defaultCaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
