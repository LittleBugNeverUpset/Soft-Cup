package com.littlebug.utils;

import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class VideoToMp3Converter {

    /**
     * 从视频文件中提取 MP3 音频
     * @param videoFile 上传的视频文件
     * @return 包含 MP3 音频的 MultipartFile
     * @throws IOException 如果文件操作失败
     * @throws InterruptedException 如果 FFmpeg 进程被中断
     * @throws IllegalArgumentException 如果输入文件不是视频
     */
    public static MultipartFile extractMp3FromVideo(MultipartFile videoFile)
            throws IOException, InterruptedException, IllegalArgumentException {

        // 验证输入文件
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("视频文件不能为空");
        }

        // 检查文件是否是视频 (简单检查)
        String contentType = videoFile.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("输入文件不是视频类型");
        }

        // 创建临时目录
        Path tempDir = Files.createTempDirectory("video_to_mp3_");

        try {
            // 保存上传的视频到临时文件
            File inputVideoFile = new File(tempDir.toFile(), "input_" + videoFile.getOriginalFilename());
            videoFile.transferTo(inputVideoFile);

            // 生成输出 MP3 文件名
            String outputMp3Filename = UUID.randomUUID() + ".mp3";
            File outputMp3File = new File(tempDir.toFile(), outputMp3Filename);

            // 使用 FFmpeg 提取音频
            extractAudioWithFfmpeg(inputVideoFile, outputMp3File);

            // 将生成的 MP3 文件转换为 MultipartFile
            byte[] mp3Bytes = FileUtils.readFileToByteArray(outputMp3File);

            return new InMemoryMultipartFile(
                    "audioFile",
                    outputMp3Filename,
                    "audio/mpeg",
                    mp3Bytes
            );

        } finally {
            // 清理临时文件
            FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    /**
     * 使用 FFmpeg 从视频中提取 MP3 音频
     */
    private static void extractAudioWithFfmpeg(File inputFile, File outputFile)
            throws IOException, InterruptedException {

        // 构建 FFmpeg 命令
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile.getAbsolutePath(),
                "-vn",                  // 禁用视频
                "-acodec", "libmp3lame", // 使用 MP3 编码器
                "-q:a", "2",            // 音频质量 (0-9, 0是最高质量)
                "-y",                   // 覆盖输出文件
                outputFile.getAbsolutePath()
        );

        // 重定向错误流以查看 FFmpeg 输出
        pb.redirectErrorStream(true);

        Process process = pb.start();

        // 读取 FFmpeg 输出 (用于调试)
        try (InputStream is = process.getInputStream()) {
            String output = IOUtils.toString(is, "UTF-8");
            System.out.println("FFmpeg output: " + output);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg 处理失败，退出码: " + exitCode);
        }

        if (!outputFile.exists() || outputFile.length() == 0) {
            throw new IOException("FFmpeg 未能生成有效的 MP3 文件");
        }
    }

    /**
     * 自定义内存中的 MultipartFile 实现
     */
    private static class InMemoryMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public InMemoryMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            FileUtils.writeByteArrayToFile(dest, content);
        }
    }
}