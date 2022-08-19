package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

//实现文件管理，文件的上传、下载、删除、文件信息获取、storage信息获取、tracker信息获取
public class FastDFSUtil {
    static {
        try {
//        加载配置我呢见fdfs_client.conf,查找classpath下的文件路径
        String filename = new ClassPathResource("fdfs_client.conf").getPath();
//        加载trcker路径
            ClientGlobal.init(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //图片上传,上传到的信息封装
    public static String[] upload(FastDFSFile fastDFSFile) {//静态static方便被调用
        try {
//            创建Tracker的客户端访问对象TrackerClient
            TrackerClient trackerClient = new TrackerClient();
//            通过TrackerClient访问trackerServer服务
            TrackerServer trackerServer = trackerClient.getConnection();
//            通过trackerServer的链接信息可以获取Storage的链接信息，创建storageclient对象存储storage的链接信息
            StorageClient storageClient = new StorageClient(trackerServer, null);
//            storageClient.get//查看获取文件信息的方法和参数get_file_info(),用于文件信息方法中获取参数
//            附加参数
            NameValuePair[] meta_list = new NameValuePair[1];//NameValuePair类型长度是1的数组meta_list，注意一个元素的下标是0meta_list[0]
           meta_list[0] = new NameValuePair("nuthor",fastDFSFile.getAuthor());//从上传的文件信息取出作者getAuthor
//调用upload_file()实现文件上传
            String[] uploads = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), meta_list);
//            查看upload_file源码，可以看到有数组String返回，两个参数一个Storage的group name一个是filename
            return uploads;// strings[0]==group1  strings[1]=M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
//            返回的strings[0]和strings[1]在前端controller中接收，uploads[0] uploads[1]
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return null;
        return new String[0];
    }
    //根据文件名和组名获取文件的信息
    //参数1 指定组名
    //参数2 指定文件的路径
    public static FileInfo getFile(String groupName, String remoteFileName)throws Exception {
//        创建TrackerClient对象，通过TrackerClient对象访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
//        通过TrackerClient获取TrackerServer的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
//        通过TrackerServer获取Storage信息封装到StorageClient，创建StorageClient对象存储Storage信息
        StorageClient storageClient = new StorageClient(trackerServer,null);
//        获取文件信息
        return storageClient.get_file_info(groupName,remoteFileName);
    }

    /**
     *文件下载
     * @param groupName
     * @throws Exception
     * 希望返回文件输入流
     */
    public static InputStream downloadFile(String groupName, String remoteFileName)throws Exception{
        //创建TrackerClient对象，通过TrackerClient对象访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过TrackerServer获取Storage信息封装到StorageClient，创建StorageClient对象存储Storage信息
        StorageClient storageClient = new StorageClient(trackerServer,null);
        //        文件下载，返回的是字节数组buffer，但是我们下载文件获取的是文件的字节输入流InputStream
//        所以我们需要把buffer转成字节输入流，InputStream是抽象类不能直接使用，我们查看InputStream哪个子类与字节数组有关，
//        使用ByteArrayInputStream转成流的形式
        byte[] buffer = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(buffer);
    }

    /**
     * 文件下载
     * @param groupName remoteFileName
     * @throws Exception
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        /*//创建TrackerClient对象，通过TrackerClient对象访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过TrackerServer获取Storage信息封装到StorageClient，创建StorageClient对象存储Storage信息
        StorageClient storageClient = new StorageClient(trackerServer,null);*/
//        调用共同代码块
//        获取trackerServer
        TrackerServer trackerServer = getTrackerServer();
//        获取StorageClient
        StorageClient storageClient = getStorageClient(trackerServer);

//        删除文件
        storageClient.delete_file(groupName, remoteFileName);
    }

    /**
     * 获取Storage组信息
     */
    public static StorageServer getStorages() throws Exception {
        //创建TrackerClient对象，通过TrackerClient对象访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过TrackerServer获取Storage信息封装到StorageClient，创建StorageClient对象存储Storage信息
//        StorageClient storageClient = new StorageClient(trackerServer,null);
        StorageClient storageClient = getStorageClient(trackerServer);
//        获取Storage信息
        return trackerClient.getStoreStorage(trackerServer);
    }
    /***
     * 根据文件组名和文件存储路径获取Storage服务的IP、端口信息
     * @param groupName :组名
     * @param remoteFileName ：文件存储完整名
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) throws Exception {
        //创建TrackerClient对象，通过TrackerClient对象访问TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer的链接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //通过TrackerServer获取Storage信息封装到StorageClient，创建StorageClient对象存储Storage信息
//        StorageClient storageClient = new StorageClient(trackerServer, null);
        return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
    }
    /***
     * 获取Tracker服务地址
     */
    public static String getTrackerInfo() throws Exception {
        /*//创建TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();*/
        //        获取trackerServer
        TrackerServer trackerServer = getTrackerServer();
        //获取Tracker地址
//      return "http://"+trackerServer.getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port();
        String ip = trackerServer.getInetSocketAddress().getHostString();
        int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
        String url = "http://"+ip+":"+g_tracker_http_port;
        return url;
    }

    /**
     * 共同代码块
     * 获取StorageClient
     */
    public static StorageClient getStorageClient(TrackerServer trackerServer){
        StorageClient storageClient = new StorageClient(trackerServer,null);
        return storageClient;
    }
    /**
     * 共同代码块
     * 获取trackerServer
     * @return
     * @throws Exception
     */
    public static TrackerServer getTrackerServer() throws Exception {
        //创建TrackerClient对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient获取TrackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }
    //测试
    public static void main(String[] args) throws Exception {
//        看能否获取文件信息
        /*FileInfo fileInfo = getFile("group1", "M00/00/00/wKjThF0DBzaAP23MAAXz2mMp9oM26.jpeg");
        System.out.println(fileInfo.getSourceIpAddr());//文件ip地址
        System.out.println(fileInfo.getFileSize());//文件的端口号*/
//        测试文件下载，读取文件字节数组，将文件存储到本地磁盘
//        InputStream is = downloadFile("group1", "M00/00/00/wKjThGLjmuSAFHPOAADfLHLHZi4105.png");
//        服务器中有图片http://192.168.211.132:8080/group1/M00/00/00/wKjThGLjmuSAFHPOAADfLHLHZi4105.png
//        测试文件下载
        /*InputStream is = downloadFile("group1", "M00/00/00/wKjThGLjmuSAFHPOAADfLHLHZi4105.png");
//        将文件写入本地磁盘
        FileOutputStream os = new FileOutputStream(
                "d:\\00.jpg");
//        定义缓冲区
        byte[] bytes = new byte[1024];
//        读数据
        while (is.read(bytes)!=-1){//读到缓冲区bytes
//            写到缓冲区
            os.write(bytes);
        }
        os.flush();
        os.close();
        is.close();
    */
//        测试文件删除,注意如果需要删除图片立即能看到效果，需要配置nginx禁止浏览器缓存
//        deleteFile("group1","M00/00/00/wKgDhGLnbr6AbRp4AAGT-RbVp3I83.jpeg");

//        获取Storage信息
        /*StorageServer storageServer = getStorages();
        System.out.println(getStorages().getStorePathIndex());
        System.out.println(getStorages().getInetSocketAddress().getHostString());*/
//获取组信息
       /* ServerInfo[] groups = getServerInfo("group1", "M00/00/00/wKgDhGLmU-WAbIjgAAI7I0cRhHA027.jpg");
//        文件可能存在组内的多台机器任意一台机器上,使用for循环遍历寻找
        for (ServerInfo group : groups) {
            System.out.println(group.getIpAddr());
            System.out.println(group.getPort());
        }*/
//       获取Tracker的信息
        System.out.println(getTrackerInfo());
    }
}
