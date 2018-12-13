package org.awesome.service.impl;

import org.awesome.models.IdentifyCode;
import org.awesome.service.IIdentifyCodeService;
import org.awesome.utils.IdentifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

@Service
public class IdentifyCodeService implements IIdentifyCodeService {

    private static final Logger LOG = LoggerFactory.getLogger(IdentifyCodeService.class);

    @Autowired
    private IdentifyCodeUtil identifyCodeUtil;

    private SecureRandom random = new SecureRandom();

    @Override
    public IdentifyCode generateIdentifyCode() {
        LOG.info("Generate identifyCode.");

        int op1 = random.nextInt(100);
        int op2 = random.nextInt(100);
        int opInt = random.nextInt(2);
        String opString;
        int result;
        if (opInt == 1) {
            opString = "+";
            result = op1 + op2;
        } else {
            opString = "-";
            result = op1 - op2;
            if (result < 0) {
                opString = "+";
                result = op1 + op2;
            }
        }

        IdentifyCode identifyCode = new IdentifyCode();
        identifyCode.setOp1(op1);
        identifyCode.setOp2(op2);
        identifyCode.setOp(opString);
        identifyCode.setResult(result);

        LOG.info("Generate identifyCode finish.");

        return identifyCode;
    }

    @Override
    public String generateIdentifyCodeImage(IdentifyCode identifyCode, HttpServletResponse response) {
        LOG.info("Generate identifyCode image.");

        BufferedImage image = identifyCodeUtil.creatImage(identifyCode);

        response.setContentType("image/jpeg");
        response.setDateHeader("expries", -1);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        String base64Img = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            BASE64Encoder encoder = new BASE64Encoder();
            base64Img = encoder.encode(outputStream.toByteArray());

        } catch (IOException _ignore) {
            LOG.error("Generate identifyCode image error. [{}]", _ignore.getMessage());
        }

        LOG.info("Generate identifyCode image finish.");
        return base64Img;
    }
}
