package org.example.api;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.dao.ImageDao;
import org.example.model.ImageInfo;
import org.example.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/image")
@MultipartConfig
public class ImageServlet extends HttpServlet {

    //图片上传，服务端保存在本地硬盘的路径前缀
//    public static final String LOCAL_PATH_PREFIX="E:/TMP";
    public static final String LOCAL_PATH_PREFIX="/root/TMP";

    //图片上传接口
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求数据:uploadImage=图片数据
        Part p=req.getPart("uploadImage");

        //1.保存在服务端本地硬盘:完整路径为路径前缀+后缀
        //用md5值作为文件名（后缀）
        //md5操作，可以基于byte[],String,InputStream
        //先根据上传的图片，生成md5值
        String md5= DigestUtils.md5Hex(p.getInputStream());
        //先验证md5值，如果存在（报错），就是重复的，不保存
        ImageInfo imageInfo=ImageDao.selectByMd5(md5);
        if(imageInfo!=null){
            //返回响应：{ok:boolean,msg:String}
            Map<String,Object> data=new HashMap<>();
            data.put("ok",false);
            data.put("msg","上传图片重复");
            WebUtil.serialize(resp,data);
            return;
        }

        //保存在服务端本地硬盘：路径前缀（常量）+后缀（/md5值）
        p.write(LOCAL_PATH_PREFIX+"/"+md5);

        //2.保存在数据库
        //先构造一个ImageInfo对象，来保存要插入数据库的数据
        ImageInfo image=new ImageInfo();
        //设置图片名称：上传的文件名
        image.setImageName(p.getSubmittedFileName());
        //设置图片大小：上传的文件大小
        image.setSize(p.getSize());
        //设置上传日期：当前日期
        image.setUploadTime(new java.util.Date());
        //设置md5
        image.setMd5(md5);
        //设置数据格式/类型：上传的文件格式（注：不是请求的数据格式，是form-data上传的图片字段的格式）
        image.setContentType(p.getContentType());
        //设置路径：设置为路径后缀（/md5值）
        image.setPath("/"+md5);

        //插入数据库图片数据
        int n= ImageDao.insert(image);

        //返回响应：{ok:boolean,msg:String}
        Map<String,Object> data=new HashMap<>();
        data.put("ok",true);//不返回错误，出错就让tomcat返回500状态码
        WebUtil.serialize(resp,data);
    }

    //获取图片列表接口：返回[{imageID:1,imageName:""}]
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //查询数据库所有图片，并返回(List<ImageInfo>)
        List<ImageInfo> images=ImageDao.selectAll();
        //返回响应
        WebUtil.serialize(resp,images);
    }

    //删除图片接口：DELETE /image?imageId=1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取请求数据：获取图片id
        String imageId=req.getParameter("imageId");
        Integer id=Integer.parseInt(imageId);

        //2.删除本地文件（先根据图片id查询到数据（包含path），拼接完整路径再删除）
        ImageInfo imageInfo=ImageDao.selectOne(id);
        String path=LOCAL_PATH_PREFIX+imageInfo.getPath();
        File pic=new File(path);
        //删除文件：没有权限可能会失败
        pic.delete();

        //3.删除数据库图片数据
        int n=ImageDao.delete(id);

        //4.返回响应数据
        Map<String,Object> data=new HashMap<>();
        data.put("ok",true);
        WebUtil.serialize(resp,data);
    }
}
