package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController //@RestController 是@controller和@ResponseBody 的结合
@RequestMapping(value = "/upload")
//跨域:
//不同的域名A 访问 域名B 的数据就是跨域
// 端口不同 也是跨域  loalhost:18081----->localhost:18082
// 协议不同 也是跨域  http://www.jd.com  --->  https://www.jd.com
// 域名不同 也是跨域  http://www.jd.com  ---> http://www.taobao.com
//协议一直,端口一致,域名一致就不是跨域  http://www.jd.com:80 --->http://www.jd.com:80 不是跨域
@CrossOrigin //跨越访问
public class FileUploadController {
    @PostMapping //文件上传一定是post方式
    public Result upload(@RequestParam(value = "file")MultipartFile file)throws Exception{
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename(),//原来的文件名  1234.jpg
                file.getBytes(),//文件本身的字节数组
                StringUtils.getFilenameExtension(file.getOriginalFilename())//获取文件拓展名
        );
//        调用FastDFSUtil工具类将文件传入到FastDFS中
//        使用uploads接收数组信息
        String[] uploads = FastDFSUtil.upload(fastDFSFile); //传入文件封装对象fastDFSFile
//3. 拼接图片的全路径返回
        // http://192.168.211.132:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
//        String url ="http://192.168.3.132:8080/"+uploads[0]+"/"+uploads[1];
        String url =FastDFSUtil.getTrackerInfo()+"/"+uploads[0]+"/"+uploads[1];
        return new Result(true, StatusCode.OK,"上传成功",url);
    }
}
