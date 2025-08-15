package com.fatebug.base.core.constants;

/**
 * 媒体类型工具类
 */
public interface MimeTypeUtils {
    String IMAGE_PNG = "image/png";

    String IMAGE_JPG = "image/jpg";

    String IMAGE_JPEG = "image/jpeg";

    String IMAGE_BMP = "image/bmp";

    String IMAGE_GIF = "image/gif";

    String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

    String[] FLASH_EXTENSION = {"swf", "flv"};

    String[] MEDIA_EXTENSION = {"swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf", "rm", "rmvb"};

    String[] VIDEO_EXTENSION = {"mp4", "avi", "rmvb"};

    String[] DEFAULT_ALLOWED_EXTENSION = {
            // 图片
            "bmp", "gif", "jpg", "jpeg", "png",
            // word excel powerpoint
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "html", "htm", "txt",
            // 压缩文件
            "rar", "zip", "gz", "bz2",
            // 视频格式
            "mp4", "avi", "rmvb",
            // pdf
            "pdf"};

    static String getExtension(String prefix) {
        return switch (prefix) {
            case IMAGE_PNG -> "png";
            case IMAGE_JPG -> "jpg";
            case IMAGE_JPEG -> "jpeg";
            case IMAGE_BMP -> "bmp";
            case IMAGE_GIF -> "gif";
            default -> "";
        };
    }
}
