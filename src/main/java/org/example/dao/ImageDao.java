package org.example.dao;

import com.mysql.jdbc.Connection;
import org.example.model.ImageInfo;
import org.example.util.DBUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ImageDao {
    public static int insert(ImageInfo image) {
        Connection c=null;
        PreparedStatement ps=null;
        try {
            c= DBUtil.getConnection();
            //全列插入，主键插入null，其实还是自增插入
            String sql="insert into image_info values(null,?,?,?,?,?,?)";
            //创建操作命令对象
            ps=c.prepareStatement(sql);
            //替换占位符
            ps.setString(1, image.getImageName());
            ps.setLong(2,image.getSize());
            long time=image.getUploadTime().getTime();
            ps.setTimestamp(3,new Timestamp(time));
            ps.setString(4,image.getMd5());
            ps.setString(5,image.getContentType());
            ps.setString(6,image.getPath());
            //执行插入操作，并把jdbc的返回值，作为当前方法的返回
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("数据库保存图片出错",e);
        }finally {
            DBUtil.close(c,ps,null);
        }
    }

    public static List<ImageInfo> selectAll() {
        Connection c=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            c=DBUtil.getConnection();
            String sql="select * from image_info";
            ps=c.prepareStatement(sql);
            //查询返回结果集
            rs=ps.executeQuery();
            //先准备一个返回的List
            List<ImageInfo> images=new ArrayList<>();
            //处理结果集
            while(rs.next()){
                //处理下一条数据：转换为一个ImageInfo对象，再放到list
                ImageInfo imageInfo=new ImageInfo();
                //前端只需要两个字段imageId，imageName
                imageInfo.setImageId(rs.getInt("image_id"));
                imageInfo.setImageName(rs.getString("image_name"));
                //添加到list
                images.add(imageInfo);
            }
            return images;
        } catch (SQLException e) {
            throw new RuntimeException("数据库获取图片列表出错",e);
        }finally {
            DBUtil.close(c,ps,rs);
        }
    }

    //根据图片id查询一条数据
    public static ImageInfo selectOne(int id) {
        Connection c=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            c=DBUtil.getConnection();
            String sql="select * from image_info where image_id=?";
            ps=c.prepareStatement(sql);
            //替换占位符
            ps.setInt(1,id);
            //查询返回结果集
            rs=ps.executeQuery();
            //处理结果集
            while(rs.next()){
                //处理下一条数据：转换为一个ImageInfo对象
                ImageInfo imageInfo=new ImageInfo();
                //调用的地方，其实只使用到path字段，所以至少返回有path
                imageInfo.setImageId(rs.getInt("image_id"));
                imageInfo.setImageName(rs.getString("image_name"));
                imageInfo.setPath(rs.getString("path"));
                return imageInfo;
            }
            //如果没有进入while循环，表示没有查到数据，返回null
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("数据库查询图片详情出错",e);
        }finally {
            DBUtil.close(c,ps,rs);
        }
    }

    //根据图片id删除数据
    public static int delete(Integer id) {
        Connection c=null;
        PreparedStatement ps=null;
        try {
            c=DBUtil.getConnection();
            String sql="delete from image_info where image_id=?";
            ps=c.prepareStatement(sql);
            //替换占位符
            ps.setInt(1,id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("数据库删除图片出错",e);
        }finally {
            DBUtil.close(c,ps,null);
        }
    }

    //根据md5查询数据
    public static ImageInfo selectByMd5(String md5) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBUtil.getConnection();
            String sql = "select * from image_info where md5=?";
            ps = c.prepareStatement(sql);
            //替换占位符
            ps.setString(1, md5);
            //查询返回结果集
            rs = ps.executeQuery();
            //处理结果集
            while (rs.next()) {
                //处理下一条数据：转换为一个ImageInfo对象
                ImageInfo imageInfo = new ImageInfo();
                //调用的地方，其实只使用到path字段，所以至少返回有path
                imageInfo.setImageId(rs.getInt("image_id"));
                imageInfo.setImageName(rs.getString("image_name"));
                imageInfo.setPath(rs.getString("path"));
                return imageInfo;
            }
            //如果没有进入while循环，表示没有查到数据，返回null
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("根据md5查询图片出错", e);
        } finally {
            DBUtil.close(c, ps, rs);
        }
    }
}
