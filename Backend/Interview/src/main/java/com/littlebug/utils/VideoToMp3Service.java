package com.littlebug.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

@Service
public class VideoToMp3Service {

    private static final int CONVERSION_TIMEOUT_SECONDS = 120;
    
    /**
     * 将MultipartFile视频转换为MP3文件
     * @param videoFile 上传的视频文件
     * @return 转换后的MP3临时文件（使用后需要手动删除）
     * @throws IOException 文件操作异常
     * @throws InterruptedException 进程中断异常
     * @throws FFmpegException 转换失败异常
     */
    public File convertToMp3(MultipartFile videoFile) throws IOException, InterruptedException, FFmpegException {

        // 1. 创建临时工作目录
        File tempDir = Files.createTempDirectory("video_conversion_").toFile();
        
        try {
            // 2. 将MultipartFile转存为临时视频文件
            File inputVideoFile = new File(tempDir, "input_" + System.currentTimeMillis() + getFileExtension(videoFile.getOriginalFilename()));
            Files.copy(videoFile.getInputStream(), inputVideoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // 3. 创建输出MP3文件
            File outputMp3File = new File(tempDir, "output_" + System.currentTimeMillis() + ".mp3");
            
            // 4. 执行FFmpeg转换
            convertVideoToMp3(inputVideoFile, outputMp3File);
            
            // 5. 返回转换后的文件（调用方需要负责删除）
            return outputMp3File;
            
        } catch (Exception e) {
            // 6. 发生异常时清理临时目录
            deleteDirectory(tempDir);
            throw e;
        }
    }
    
    private void convertVideoToMp3(File inputFile, File outputFile) throws IOException, InterruptedException, FFmpegException {
        String ffmpegPath = "D:\\Program File (x86)\\ffmpeg\\bin\\ffmpeg.exe";
        ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath, // 使用绝对路径
                "-i", inputFile.getAbsolutePath(),
                "-vn",
                "-acodec", "libmp3lame",
                "-q:a", "2",
                "-y",
                outputFile.getAbsolutePath()
        );

        // 重定向错误流以便调试
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // 读取进程输出（防止缓冲区满导致阻塞）
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 可以记录日志或处理输出
            }
        }
        
        // 设置超时
        if (!process.waitFor(CONVERSION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new FFmpegException("转换超时，耗时超过 " + CONVERSION_TIMEOUT_SECONDS + " 秒");
        }
        
        // 检查转换结果
        if (process.exitValue() != 0 || !outputFile.exists()) {
            throw new FFmpegException("FFmpeg转换失败，退出码: " + process.exitValue());
        }
    }
    
    // 辅助方法：获取文件扩展名
    private String getFileExtension(String filename) {
        return filename != null && filename.contains(".") 
            ? filename.substring(filename.lastIndexOf('.')) 
            : "";
    }
    
    // 辅助方法：递归删除目录
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
    
    // 自定义异常
    public static class FFmpegException extends Exception {
        public FFmpegException(String message) {
            super(message);
        }
    }
}