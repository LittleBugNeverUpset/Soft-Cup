package com.littlebug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlebug.pojo.EmotionAnalysisResponse;
import com.littlebug.pojo.Interview;
import com.littlebug.service.InterviewService;
import com.littlebug.mapper.InterviewMapper;
import com.littlebug.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.littlebug.utils.AudioExtractor.extractAudioToMp3;

/**
* @author 种昊阳
* @description 针对表【interview】的数据库操作Service实现
* @createDate 2025-06-14 12:13:52
*/
@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview>
    implements InterviewService{
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private InterviewMapper interviewMapper;
    @Autowired
    private VideoToMp3Service videoToMp3Service;

    @Autowired
    private AudioAnalysisService audioAnalysisService;

    @Override
    public Result createInterview(String token, String position) {

        //获取token对应的用户
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId,userId).and(q-> q.ne(Interview::getStatus,"completed").or().ne(Interview::getStatus,"cancle"));
        Long count = interviewMapper.selectCount(queryWrapper);
        if(count > 0) {
            return Result.build("Unfinished interview existing",ResultCodeEnum.PROCESS_ERROR);
        }

        Interview interview = new Interview();
        interview.setPositionType(position);
        interview.setCreatedAt(new Date());
        interview.setCurrentQuestionSeq(0);
        interview.setUserId((long)userId);
        int rows = interviewMapper.insert(interview);
        return Result.ok("Inster In to row :" + rows);

    }

    @Override
    public Result startInterview(String token) {
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId,userId);
        queryWrapper.eq(Interview::getStatus,"created");
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if( interview != null) {
            interview.setStatus("started");
            interview.setUpdatedAt(new Date());
            interviewMapper.updateById(interview);
            return  Result.ok("Interview Started At " + new Date());
        }
        return Result.build("Created Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);
    }

    @Override
    public Result generateInterviewQuestion(String token) {
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId,userId);
        queryWrapper.eq(Interview::getStatus,"started");
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if(interview != null){
            interview.setCurrentQuestionSeq(interview.getCurrentQuestionSeq() + 1);
            interview.setUpdatedAt(new Date());
            interviewMapper.updateById(interview);
            return Result.ok("Question:" +interview.getCurrentQuestionSeq() + ": 请解释RESTful API的设计原则");
        }
        return Result.build("Started Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);
    }

    @Override
    public Result answerInterviewquestion(String token, MultipartFile videoFile) {
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId, userId);
        queryWrapper.eq(Interview::getStatus, "started");
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if (interview != null) {
            try {
                File mp3File = null;
                try {
                    // 1. 转换为MP3
                    mp3File = videoToMp3Service.convertToMp3(videoFile);

                    // 2. 调用分析服务
                    AudioAnalysisService.AudioAnalysisResult result = audioAnalysisService.analyzeAudio(mp3File);

                    return Result.ok(result);

                } catch (Exception e) {
                    return Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
                } finally {
                    // 3. 确保删除临时文件
                    if (mp3File != null && mp3File.exists()) {
                        mp3File.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.build("Video Handle Error", ResultCodeEnum.PROCESS_ERROR);
            }
        }
        return  Result.build("Started Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);
    }
}




