package com.littlebug.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class VideoConversionService {

    @Value("${ffmpeg.path:ffmpeg}")
    private String ffmpegPath;
    // FFmpeg 可执行文件路径（根据系统安装路径修改）
//    private String ffmpegPath = "ffmpeg"; // 如果已添加到系统PATH，直接使用"ffmpeg"

    public File convertVideoToMp3(MultipartFile videoFile) throws Exception {
        // 创建临时文件保存上传的视频
        File ffmpegExecutable = new File(ffmpegPath);
        if (!ffmpegExecutable.exists()) {
            // 尝试检查系统PATH中是否存在ffmpeg
            if (isCommandAvailable("ffmpeg")) {
                ffmpegPath = "ffmpeg"; // 使用系统PATH中的ffmpeg
            } else {
                throw new IOException("FFmpeg 可执行文件不存在于路径: " + ffmpegPath);
            }
        }
        File tempVideoFile = createTempFile(videoFile, "input-video");

        try {
            // 生成输出 MP3 文件
            File outputMp3File = createTempFile(null, "output-audio", ".mp3");

            // 构建 FFmpeg 命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath,
                "-i", tempVideoFile.getAbsolutePath(), // 输入视频文件
                "-vn",                                 // 不处理视频流
                "-acodec", "libmp3lame",              // 使用 MP3 编码器
                "-q:a", "2",                           // 音频质量参数（0-9，0 为最佳）
                outputMp3File.getAbsolutePath()        // 输出 MP3 文件
            );

            // 设置错误输出和标准输出合并
            processBuilder.redirectErrorStream(true);

            // 执行命令
            Process process = processBuilder.start();

            // 读取命令输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 可记录到日志
                }
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg 命令执行失败，退出代码: " + exitCode);
            }

            return outputMp3File;
        } finally {
            // 删除临时视频文件（保留转换后的 MP3）
            if (tempVideoFile.exists()) {
                tempVideoFile.delete();
            }
        }
    }

    // 创建临时文件
    private File createTempFile(MultipartFile multipartFile, String prefix, String suffix) throws IOException {
        String fileName = prefix + "-" + UUID.randomUUID().toString();
        if (suffix == null && multipartFile != null) {
            String originalFileName = multipartFile.getOriginalFilename();
            if (originalFileName != null) {
                int extIndex = originalFileName.lastIndexOf('.');
                if (extIndex > 0) {
                    suffix = originalFileName.substring(extIndex);
                }
            }
        }
        if (suffix == null) suffix = "";

        File tempFile = File.createTempFile(fileName, suffix);

        // 如果有文件内容，复制到临时文件
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }

        return tempFile;
    }

    // 重载方法，使用默认后缀
    private File createTempFile(MultipartFile multipartFile, String prefix) throws IOException {
        return createTempFile(multipartFile, prefix, null);
    }
    private boolean isCommandAvailable(String command) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", command});
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}