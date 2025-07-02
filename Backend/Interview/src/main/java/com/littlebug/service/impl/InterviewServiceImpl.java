package com.littlebug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlebug.pojo.*;
import com.littlebug.service.InterviewService;
import com.littlebug.mapper.InterviewMapper;
import com.littlebug.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private MongoTemplate mongoTemplate;
    @Autowired
    private ApiService apiService;

    @Override
    public Result createInterview(String token, String position ,MultipartFile PdfFile, MultipartFile TxtFile) throws IOException {

        //获取token对应的用户
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId,userId).and(q-> q.ne(Interview::getStatus,"completed").or().ne(Interview::getStatus,"cancle"));
        Long count = interviewMapper.selectCount(queryWrapper);
        if(count > 0) {
            return Result.build("Unfinished interview existing",ResultCodeEnum.PROCESS_ERROR);
        }
        //mongodb创建文档，并插入数据库
        try{
            Interview interview = new Interview();
            interview.setPositionType(position);
            interview.setCreatedAt(new Date());
            interview.setCurrentQuestionSeq(0);
            interview.setUserId((long)userId);
            int rows = interviewMapper.insert(interview);
            //调用AI传递职位和简历
            apiService.uploadPdfAndTxt(PdfFile,TxtFile);
            String matchingMessage = apiService.callMatchingAnalysisApi();
            //获取interview 唯一标识
            LambdaQueryWrapper<Interview> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(Interview::getUserId,userId);
            queryWrapper2.eq(Interview::getStatus,"created");
            interview = interviewMapper.selectOne(queryWrapper2);

            //更新Mongodb
            InterviewDocument interviewDocument = new InterviewDocument();
            interviewDocument.setStudentId(userId);
            interviewDocument.setInterviewId(interview.getId());
            interviewDocument.setCreatedAt(new Date());
            interviewDocument.setUpdatedAt(new Date());
            interviewDocument.setStatus("created");
            interviewDocument.setMatchingMessage(matchingMessage);
            interviewDocument.setPositionType(position);


            mongoTemplate.insert(interviewDocument);
            return Result.ok("Inster In to row :" + rows);
        }
        catch (Exception e){
            return  Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
        }
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
            try{
                interview.setStatus("started");
                interview.setUpdatedAt(new Date());
                interview.setCurrentQuestionSeq(1);
                interviewMapper.updateById(interview);
                //mongodb更新字段
                Query query = new Query(Criteria.where("interviewId").is(interview.getId()));
                Update update = new Update().set("status", "started");
                //获取初始问题
                String question = apiService.getInitQuestions(interview.getId().toString());
                TimelineEvent timelineEvent = new TimelineEvent("getQuestion",new Date(),question);
                Question qs = new Question(1,question,null);
                update.push("timeline",timelineEvent);
                update.push("questions",qs);
                update.set("update_at" ,new Date());
                mongoTemplate.updateFirst(query, update, InterviewDocument.class);

                return  Result.ok(question);

            }catch (Exception e) {
                return Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
            }
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

                    VoiceEmotionAnalysisResponse ans = apiService.callThirdPartyApiWithAudio(mp3File);
                    return  Result.ok(ans.getData());
                } catch (Exception e) {
                    return  Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
                }
            }
            else{
                return  Result.build("mptVideo  Is Ey", ResultCodeEnum.PROCESS_ERROR);
            }
        } else {
            return  Result.build("Started Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);

        }

    }
}




