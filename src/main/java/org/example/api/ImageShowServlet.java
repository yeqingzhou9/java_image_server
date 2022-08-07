package org.example.api;

import org.example.dao.ImageDao;
import org.example.model.ImageInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@WebServlet("/imageShow")
public class ImageShowServlet extends HttpServlet {

    //白名单列表（本机）
    private static final List<String> WHITE_LIST= Arrays.asList(
            "http://localhost:8080/java_image_server/",
            "http://localhost:8080/java_image_server/index.html",
            "http://106.52.212.221:8080/java_image_server/",
            "http://106.52.212.221:8080/java_image_server/index.html"
    );

    //获取图片内容接口：GET /imageShow?imageId=1
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //防盗链：先获取Referer请求头，在白名单列表中，才允许访问，否则返回403
        String referer=req.getHeader("Referer");
        //不在白名单中
        if(!WHITE_LIST.contains(referer)){
            resp.setStatus(403);
            return;
        }

        //1.获取请求数据：获取图片id，获取queryString，是getParameter获取
        String imageId=req.getParameter("imageId");
        //2.根据图片id，在数据库查询图片数据（包含path字段：路径后缀）
        ImageInfo imageInfo= ImageDao.selectOne(Integer.parseInt(imageId));
        //3.返回响应：读取本地图片文件，把二进制数据设置到响应体
        //读取本地图片：完整路径=路径前缀+路径后缀（path字段）
        String path=ImageServlet.LOCAL_PATH_PREFIX+imageInfo.getPath();
        //读取这个路径的文件
        File pic=new File(path);
        byte[] data=Files.readAllBytes(pic.toPath());

        //把二进制图片数据，写入到响应正文
        //严格来说要设置响应数据格式Content-Type，但前段是<img>使用，所以没有也可以
        resp.getOutputStream().write(data);
    }
}
