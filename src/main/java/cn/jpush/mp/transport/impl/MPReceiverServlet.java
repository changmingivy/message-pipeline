package cn.jpush.mp.transport.impl;


import org.apache.commons.lang.ArrayUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by elvin on 16/8/15.
 */

public class MPReceiverServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("Service UP");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        byte [] data = extractData(req);

        System.out.println(data);
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
