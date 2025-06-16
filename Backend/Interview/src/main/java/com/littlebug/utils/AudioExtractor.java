package com.littlebug.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class AudioExtractor {
    
    public static File extractAudioToMp3(File videoFile) throws IOException, InterruptedException {
        // 创建临时MP3文件
        File outputFile = File.createTempFile("extracted-audio-", ".mp3");
        
        // 构建FFmpeg命令
        String[] cmd = {
            "ffmpeg",
            "-i", videoFile.getAbsolutePath(),
            "-vn",              // 禁用视频流
            "-ac", "2",         // 立体声
            "-ar", "44100",     // 采样率
            "-ab", "192k",      // 比特率
            "-f", "mp3",        // 输出格式
            "-y",               // 覆盖输出文件
            outputFile.getAbsolutePath()
        };
        
        // 执行命令
        Process process = Runtime.getRuntime().exec(cmd);
        
        // 读取错误流（FFmpeg的输出通常到错误流）
        try (BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                System.out.println(line); // 打印FFmpeg输出
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg处理失败，退出码: " + exitCode);
        }
        
        return outputFile;
    }
}