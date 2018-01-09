package com.sun.controller;

import com.alibaba.fastjson.JSON;

import com.sun.util.WendaUtil;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by TY on 2017/5/3.
 */
@Controller
public class PictureController {
    private static final Logger logger = LoggerFactory.getLogger(PictureController.class);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

//    @ResponseBody
//    @RequestMapping(value="/uploadImg",method = {RequestMethod.POST})
//    public String uploadPicture(@RequestParam(value = "file", required = false) MultipartFile file,
//                                HttpServletRequest request) {
//
//        File targetFile = null;
//        String msg = "";//返回存储路径
//        int code = 1;
//        String fileName = file.getOriginalFilename();//获取文件名加后缀
//        if (fileName != null && fileName != "") {
//           /*
//            //String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/images/upload/";//存储路径
//            String returnUrl = "/images/upload/";//存储路径
//
//            //String path = request.getSession().getServletContext().getRealPath("src/main/resources/static/images/upload"); //文件存储位置
//            String path = "D:/workspace/wenda/src/main/resources/static/images/upload";//上传至本机的绝对路径
//            String fileF = fileName.substring(fileName.lastIndexOf("."), fileName.length());//文件后缀
//            fileName = new Date().getTime() + "_" + new Random().nextInt(1000) + fileF;//新的文件名
//
//            //先判断文件是否存在
//            String fileAdd = sdf.format(new Date());
//            File file1 = new File(path + "/" + fileAdd);
//            //如果文件夹不存在则创建
//            if (!file1.exists() && !file1.isDirectory()) {
//                file1.mkdir();
//            }
//            targetFile = new File(file1, fileName);
////          targetFile = new File(path, fileName);
//            */
//            String newfilename = sdf.format(new Date()) + new Random().nextInt(1000) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//            try {
//                //得到工程目录:request.getSession().getServletContext().getRealPath()
//                String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
//                String url = request.getSession().getServletContext().getRealPath("/");
//
//                File path = new File("src/main/resources/static/images/upload/");
//                File fileSource = new File(path, newfilename);
//                if (!path.exists()) {
//                    path.mkdirs();
//                }
//                //文件写入磁盘
//                //默认创建在（/images/upload/")的路径下（transferTo与copyInputStreamToFile区别）
//                //file.transferTo(fileSource);
//                //默认创建在根目录+("src/main/resources/static/images/upload/")的路径下下
//                FileUtils.copyInputStreamToFile(file.getInputStream(), fileSource);
//                //msg=returnUrl + fileAdd+"/"+fileName;
//                //msg = "/images/upload/1493822091652_39.jpg";
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/images/upload/";
//            msg = "/images/upload/"+newfilename;
//            code = 0;
//        }
//        return WendaUtil.getJSONString(code, msg);
//    }

    @RequestMapping("/picture")
    public String index(){


        return "picture";
    }

    /**
     * 上传图片并显示，图片必须存于项目根目录下所创建的临时目录中才可回调显示，
     * 然后再把图片存于static目录资源文件中持久保存
     * @param file
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/uploadImg",method = {RequestMethod.POST})
    public String uploadPicture(@RequestParam(value = "data", required = false) MultipartFile file,
                                HttpServletRequest request) {

        int code = 1;
        String fileName = file.getOriginalFilename();//获取文件名加后缀
        String msg = "";
        if (fileName != null && fileName != "") {
            String newfilename = sdf.format(new Date()) + new Random().nextInt(1000) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            try {
                //得到工程根目录:request.getSession().getServletContext().getRealPath()
                String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                String rootPath = request.getSession().getServletContext().getRealPath("/");
                String tempPath = rootPath + "images/upload/";
                String realPath = "D:/workspace/wenda/src/main/resources/static/images/upload/";

                //只是创建了逻辑上目录，并未设置为实际目录
                File path = new File(tempPath);
                File realFile = new File(realPath);
                //把path设为实际目录
                if (!path.exists()&& !path.isDirectory()) {
                    path.mkdirs();
                }
                if (!realFile.exists()) {
                    realFile.mkdirs();
                }
                //在临时目录上创建临时文件
                File fileSource = new File(path,newfilename);
                File realFileSource = new File(realFile,newfilename);
                //将上传的文件复制到临时文件
                file.transferTo(fileSource);
                //将临时文件复制到资源库中，注意不能使用file转换到资源库
                //FileUtils.copyFile(fileSource,realFileSource);
                Files.copy(fileSource.toPath(),realFileSource.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            msg = "/images/upload/"+newfilename;
            code = 0;
        }
        return WendaUtil.getJSONString(code,msg);
    }

    @ResponseBody
    @RequestMapping(value="/uploadPic",method = {RequestMethod.POST})
    public Map<String,Object> uploadPic(@RequestParam(value = "myFileName", required = false) MultipartFile file, HttpServletRequest request) {

        Map<String,Object> map = new HashMap<>();
        int code = 1;
        String fileName = file.getOriginalFilename();//获取文件名加后缀
        String url = "";
        if (fileName != null && fileName != "") {
            String newfilename = sdf.format(new Date()) + new Random().nextInt(1000) + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            try {
                //得到工程根目录:request.getSession().getServletContext().getRealPath()
                String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                String rootPath = request.getSession().getServletContext().getRealPath("/");
                String tempPath = rootPath + "images/upload/";
                String realPath = "D:/work/workspace/wenda/src/main/resources/static/images/upload/";

                //只是创建了逻辑上目录，并未设置为实际目录
                File path = new File(tempPath);
                File realFile = new File(realPath);
                //把path设为实际目录
                if (!path.exists()&& !path.isDirectory()) {
                    path.mkdirs();
                }
                if (!realFile.exists()) {
                    realFile.mkdirs();
                }
                //在临时目录上创建临时文件
                File fileSource = new File(path,newfilename);
                File realFileSource = new File(realFile,newfilename);
                //将上传的文件复制到临时文件
                file.transferTo(fileSource);
                //将临时文件复制到资源库中，注意不能使用file转换到资源库
                //FileUtils.copyFile(fileSource,realFileSource);
                Files.copy(fileSource.toPath(),realFileSource.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
            url = "/images/upload/"+newfilename;
            code = 0;
        }
        map.put("errno",code);
        map.put("url",url);
        return map;
    }
}
