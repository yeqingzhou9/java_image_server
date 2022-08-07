package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class WebUtil {

    private static final ObjectMapper MAPPER=new ObjectMapper();

    static{
        //设置序列化的日期格式
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MAPPER.setDateFormat(df);
    }

    //json序列化
    public static void serialize(HttpServletResponse resp,Object o){
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String json=MAPPER.writeValueAsString(o);
            resp.getWriter().write(json);
        } catch (IOException e) {
            //这里是序列化就返回响应了，捕获到异常，就自行处理
            e.printStackTrace();
            resp.setStatus(500);
//            Map<String,Object> body=new HashMap<>();
//            body.put("ok",false);
//            body.put("msg","json序列化失败");
//            resp.getWriter().write(MAPPER.writeValueAsString(body));
        }
    }

    //反序列化
    public  static <T> T deserialize(HttpServletRequest req,Class<T> clazz){
        try {
            return MAPPER.readValue(req.getInputStream(),clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("反序列化失败",e);
        }
    }

}
