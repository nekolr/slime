package com.github.nekolr.slime.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;

/**
 * 文件处理工具类
 */
@Slf4j
public class FileUtils {

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 文件下载状态
     */
    public enum DownloadStatus {
        URL_ERROR(1, "URL 错误"),
        FILE_EXIST(2, "文件存在"),
        TIME_OUT(3, "连接超时"),
        DOWNLOAD_FAIL(4, "下载失败"),
        DOWNLOAD_SUCCESS(5, "下载成功");

        private int code;

        private String name;

        DownloadStatus(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static DownloadStatus downloadFile(String savePath, String fileUrl, boolean downNew) {
        URL urlfile = null;
        HttpURLConnection httpUrl = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        if (fileUrl.startsWith("//")) {
            fileUrl = "http:" + fileUrl;
        }
        String fileName;
        try {
            urlfile = new URL(fileUrl);
            String urlPath = urlfile.getPath();
            fileName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
        } catch (MalformedURLException e) {
            log.error("URL 异常", e);
            return DownloadStatus.URL_ERROR;
        }
        File path = new File(savePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(savePath + File.separator + fileName);
        if (file.exists()) {
            if (downNew) {
                file.delete();
            } else {
                log.info("文件已存在，不重新下载");
                return DownloadStatus.FILE_EXIST;
            }
        }
        try {
            httpUrl = (HttpURLConnection) urlfile.openConnection();
            httpUrl.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0");
            // 读取超时时间
            httpUrl.setReadTimeout(60000);
            // 连接超时时间
            httpUrl.setConnectTimeout(60000);
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int len = 2048;
            byte[] b = new byte[len];
            long readLen = 0;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            log.info("远程文件下载成功：" + fileUrl);
            bos.flush();
            bis.close();
            httpUrl.disconnect();
            return DownloadStatus.DOWNLOAD_SUCCESS;
        } catch (SocketTimeoutException e) {
            log.error("读取文件超时", e);
            return DownloadStatus.TIME_OUT;
        } catch (Exception e) {
            log.error("远程文件下载失败", e);
            return DownloadStatus.DOWNLOAD_FAIL;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                log.error("下载出错", e);
            }
        }
    }
}
