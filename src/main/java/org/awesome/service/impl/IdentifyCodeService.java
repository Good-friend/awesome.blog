package org.awesome.service.impl;

import org.awesome.models.IdentifyCode;
import org.awesome.service.IIdentifyCodeService;
import org.awesome.utils.CommonUtils;
import org.awesome.utils.IdentifyCodeUtil;
import org.awesome.vo.RestResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdentifyCodeService implements IIdentifyCodeService {

    private static final Logger LOG = LoggerFactory.getLogger(IdentifyCodeService.class);

    @Autowired
    private IdentifyCodeUtil identifyCodeUtil;


    private SecureRandom random = new SecureRandom();

    private static Map<String, String> map = new ConcurrentHashMap<String, String>();


    @Override
    public RestResultVo checkIdentifyCode(HttpServletRequest request){
        String key =  CommonUtils.getIpAddress(request) +"_"+ request.getParameter("str");
        LOG.info("map取出验证码 KEY：[{}] .", key);
        String redisCode = map.get(key);
        map.remove(key);
        LOG.info("map取出验证码 ：[{}] .", redisCode);
        String code = request.getParameter("code");
        LOG.info("需要验证的code ：[{}] .", code);
       if(code.equals(redisCode)){
           return new RestResultVo(RestResultVo.RestResultCode.SUCCESS,"","");
       }else{
           return new RestResultVo(RestResultVo.RestResultCode.FAILED,"","");
       }
    }

    @Override
    public RestResultVo generateIdentifyCodeAndImage(String param) {
        final IdentifyCode identifyCode = createIdentifyCode();
        final String identifyCodeImage = createIdentifyCodeImage(identifyCode);
        StringBuffer key = new StringBuffer(param + "_" + identifyCodeImage.substring(identifyCodeImage.length()-9,identifyCodeImage.length()).replace("+","/"));
        LOG.info("存入map KEY：[{}] .", key.toString());
        map.put(key.toString(),identifyCode.getResult()+"");
        LOG.info("存入map验证码：[{}] .", identifyCode.getResult());
        return new RestResultVo(RestResultVo.RestResultCode.SUCCESS, null, identifyCodeImage);
    }

    private IdentifyCode createIdentifyCode() {
        LOG.info("Create identifyCode.");

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

        LOG.info("Create identifyCode finish.");

        return identifyCode;
    }

    private String createIdentifyCodeImage(IdentifyCode identifyCode) {
        LOG.info("Create identifyCode image.");

        String result = "";
        BufferedImage image = identifyCodeUtil.creatImage(identifyCode);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            Base64.Encoder encoder = Base64.getEncoder();
            result = encoder.encodeToString(outputStream.toByteArray());

        } catch (IOException _ignore) {
            LOG.error("Generate identifyCode image error. [{}]", _ignore.getMessage());
        }

        LOG.info("Create identifyCode image finish.");

        return result;
    }
}
