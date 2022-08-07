package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ImageInfo {
    //主键id
    private Integer imageId;
    //图片名称
    private String imageName;
    //图片大小
    private Long size;
    //图片上传日期
    private java.util.Date uploadTime;
    //md5校验码：通过一段数据（字符串，数值，二进制）生成
    private String md5;
    //数据格式：http请求上传form-data数据格式时，图片字段还可以包含Content-Type
    private  String contentType;
    //图片路径：相对路径
    private String path;

}
