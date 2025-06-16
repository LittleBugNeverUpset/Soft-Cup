package com.littlebug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.littlebug.pojo.Interview;
import com.littlebug.service.InterviewService;
import com.littlebug.mapper.InterviewMapper;
import com.littlebug.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

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
    private ApiService apiService;

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
            if(videoFile != null){
                try {
                    MultipartFile mp3File = VideoToMp3Converter.extractMp3FromVideo(videoFile);
                    // 处理返回的 MP3 文件...
                    String ans = apiService.callThirdPartyApiWithAudio(mp3File);
                    return  Result.ok(ans);
                } catch (Exception e) {
                    return  Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
                }
            }
            else{
                return  Result.build("Video  Is Empty", ResultCodeEnum.PROCESS_ERROR);
            }
        } else {
            return  Result.build("Started Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);

        }

    }
}




