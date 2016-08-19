package cn.jpush.mp.transport.impl;


import cn.jpush.mp.datatarget.MPProviderManager;
import cn.jpush.mp.utils.SpringContextUtil;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by elvin on 16/8/15.
 */

public class MPReceiverServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MPReceiverServlet.class);

    private MPProviderManager providerManager;

    @Override
    public void init() throws ServletException {
        super.init();
        providerManager = (MPProviderManager) SpringContextUtil.getBean("providerManager");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Service UP");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String targetMQ = req.getHeader("Target-MQ");
        String routingKey = req.getHeader("Routing-Key");
        byte [] data = extractData(req);

        int statusCode = 500;
        String responseContent = "fail";

        logger.info("Http receive request, targetMQ: {}", targetMQ);

        if (!StringUtils.isEmpty(targetMQ) && !ArrayUtils.isEmpty(data) && targetMQ.contains(".")) {
            logger.info("Http receive request, targetMQ: {} data length", targetMQ, data.length);
            try {
                providerManager.publishMessage(targetMQ, routingKey, data);
                statusCode = 200;
                responseContent = "ok";
            } catch (Exception e) {
                logger.error("Internal Server Error", e);
                statusCode = 500;
                responseContent = e.getMessage();
            }
        } else {
            statusCode = 400;
            responseContent = "Parameter Error";
        }

        resp.setStatus(statusCode);
        resp.getWriter().write(responseContent);
    }

    private byte[] extractData(HttpServletRequest req) throws IOException {
        byte [] data = null;
        int len = 0;
        byte [] buf = new byte[1024];
        InputStream inputStream = new BufferedInputStream(req.getInputStream());
        while ((len = inputStream.read(buf)) > 0) {
            if (len < buf.length) {
                buf = Arrays.copyOfRange(buf, 0, len);
            }
            data = ArrayUtils.addAll(data, buf);
        }
        return data;
    }
}
