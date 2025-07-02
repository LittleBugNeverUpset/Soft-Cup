package com.littlebug.service.impl;

import com.littlebug.pojo.Answer;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlebug.pojo.*;
import com.littlebug.service.InterviewService;
import com.littlebug.mapper.InterviewMapper;
import com.littlebug.utils.*;
import com.mongodb.WriteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
    public Result createInterview(String token, String position, MultipartFile PdfFile, MultipartFile TxtFile) throws IOException {
        int userId = jwtHelper.getUserId(token).intValue();

        // 检查是否有未完成的面试
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId, userId)
                .and(q -> q.ne(Interview::getStatus, "completed")
                        .or()
                        .ne(Interview::getStatus, "cancle"));
        Long count = interviewMapper.selectCount(queryWrapper);
        if (count > 0) {
            return Result.build("Unfinished interview existing", ResultCodeEnum.PROCESS_ERROR);
        }

        Interview interview = null;
        try {
            // 1. 先插入 MySQL（但可以回滚）
            interview = new Interview();
            interview.setPositionType(position);
            interview.setCreatedAt(new Date());
            interview.setCurrentQuestionSeq(0);
            interview.setUserId((long) userId);
            interviewMapper.insert(interview);

            // 2. 调用 AI 服务（如果失败，会抛出异常）
            apiService.uploadPdfAndTxt(PdfFile, TxtFile);
            String matchingMessage = apiService.callMatchingAnalysisApi();

            // 3. 插入 MongoDB（如果失败，回滚 MySQL）
            InterviewDocument interviewDocument = new InterviewDocument();
            interviewDocument.setStudentId(userId);
            interviewDocument.setInterviewId(interview.getId());
            interviewDocument.setCreatedAt(new Date());
            interviewDocument.setUpdatedAt(new Date());
            interviewDocument.setStatus("created");
            interviewDocument.setMatchingMessage(matchingMessage);
            interviewDocument.setPositionType(position);
            mongoTemplate.insert(interviewDocument);

            return Result.ok(matchingMessage);
        } catch (Exception e) {
            // 如果 MongoDB 或 AI 调用失败，回滚 MySQL
            if (interview != null && interview.getId() != null) {
                interviewMapper.deleteById(interview.getId());
            }
            return Result.build(e.getMessage(), ResultCodeEnum.PROCESS_ERROR);
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
                Question qs = new Question(1,question,0,null);
                update.push("timeline",timelineEvent);
                update.push("questions",qs);
                update.set("update_at" ,new Date());
                UpdateResult result = mongoTemplate.updateFirst(query, update, InterviewDocument.class);
                System.out.println("Matched: " + result.getMatchedCount()); // 检查是否匹配到文档
                System.out.println("Modified: " + result.getModifiedCount()); // 检查是否成功修改
                return  Result.ok(question);

            }catch (Exception e) {
                return Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
            }
        }
        return Result.build("Created Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);
    }

    @Override
    public Result generateInterviewQuestion(String token) throws IOException {
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId,userId);
        queryWrapper.eq(Interview::getStatus,"started");
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if(interview != null){
            InterviewDocument interviewDocument = mongoTemplate.findOne(
                    new Query(Criteria.where("interviewId").is(interview.getId())),
                    InterviewDocument.class
            );
            String answer  = mergeAnswersText(interviewDocument,"");
            interview.setCurrentQuestionSeq(interview.getCurrentQuestionSeq() + 1);
            interview.setUpdatedAt(new Date());
            interviewMapper.updateById(interview);

            // mongodb字段更新
            String newQuestion = apiService.getNextQuestion(interview.getId().toString(),answer);
            Question question = new Question(interview.getCurrentQuestionSeq(),newQuestion,0,null);
            interviewDocument.getQuestions().add(question);
            mongoTemplate.save(interviewDocument);

            return Result.ok("Question:" +interview.getCurrentQuestionSeq() + ": " + newQuestion);
        }
        return Result.build("Started Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);
    }

    @Override
    public Result answerInterviewquestion(String token, MultipartFile videoFile, String answer) {
        int userId = jwtHelper.getUserId(token).intValue();

        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId, userId);
        queryWrapper.eq(Interview::getStatus, "started");
        Interview interview = interviewMapper.selectOne(queryWrapper);


        if (interview != null ) {
            InterviewDocument interviewDocument = mongoTemplate.findOne(
                    new Query(Criteria.where("interviewId").is(interview.getId())),
                    InterviewDocument.class
            );
            if(interviewDocument == null){
                System.out.println("interviewDocument is empty");
                return Result.build("interviewDocument is empty",ResultCodeEnum.PROCESS_ERROR);
            }
            else if (videoFile != null) {
                try {
                    MultipartFile mp3File = VideoToMp3Converter.extractMp3FromVideo(videoFile);
                    // 处理返回的 MP3 文件...
                    VoiceEmotionAnalysisResponse ans = apiService.callThirdPartyApiWithAudio(mp3File);
                    //mongodb更新字段
                    List<Question> questions = interviewDocument.getQuestions();
                    if (!questions.isEmpty() ) {
                        Answer interviewAnswer = new Answer(interview.getId().toString(),answer,ans,new Date(),interview.getCurrentQuestionSeq().toString());
                        questions.get(interview.getCurrentQuestionSeq() - 1).getAnswers().add(interviewAnswer); // 直接修改
                        Integer ansId = questions.get(interview.getCurrentQuestionSeq() - 1).getAnswerId();
                        questions.get(interview.getCurrentQuestionSeq() - 1).setAnswerId(ansId + 1);

                        // 3. 存回 MongoDB（覆盖更新）
                        mongoTemplate.save(interviewDocument);
                    }
                    return  Result.ok(ans.getData());
                } catch (Exception e) {
                    return  Result.build(e.getMessage(),ResultCodeEnum.PROCESS_ERROR);
                }
            }
            else{
                return  Result.build("mp3Video  Is Empty", ResultCodeEnum.PROCESS_ERROR);
            }
        }
        return  Result.build("Started Interview Not Exist",ResultCodeEnum.PROCESS_ERROR);

    }

    @Override
    public Result completeInterview(String token) throws IOException {
        int userId = jwtHelper.getUserId(token).intValue();
        //查询数据
        LambdaQueryWrapper<Interview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Interview::getUserId, userId);
        queryWrapper.eq(Interview::getStatus, "started");
        Interview interview = interviewMapper.selectOne(queryWrapper);
        String summery = apiService.summeryInterview(interview.getId().toString());
        if (interview != null ) {
            InterviewDocument interviewDocument = mongoTemplate.findOne(
                    new Query(Criteria.where("interviewId").is(interview.getId())),
                    InterviewDocument.class
            );
            interviewDocument.setSummery(summery);
            interviewDocument.setStatus("completed");
            mongoTemplate.save(interviewDocument);
            interview.setStatus("completed");
            interview.setUpdatedAt(new Date());
            interview.setCurrentQuestionSeq(1);
            interviewMapper.updateById(interview);
            return Result.ok(interviewDocument);
        }
        return  null;
    }

    private String mergeAnswersText(InterviewDocument interview, String existingAnswer) {
        // 获取当前问题的索引（注意：根据实际情况可能需要减 1，因为列表索引从 0 开始）
        Integer currentQuestionIndex = interview.getCurrentQuestion();
        if (currentQuestionIndex == null) {
            return existingAnswer; // 若当前问题为空，直接返回现有答案
        }

        // 转换为列表索引（假设 getCurrentQuestion 返回的是从 1 开始的序号）
        int questionListIndex = currentQuestionIndex - 1;

        // 检查索引是否有效
        List<Question> questions = interview.getQuestions();
        if (questions == null || questions.isEmpty() ||
                questionListIndex < 0 || questionListIndex >= questions.size()) {
            return existingAnswer; // 索引无效时返回现有答案
        }

        // 获取指定问题
        Question currentQuestion = questions.get(questionListIndex);
        List<Answer> answers = currentQuestion.getAnswers();
        if (answers == null || answers.isEmpty()) {
            return existingAnswer; // 无答案时返回现有答案
        }

        // 拼接所有答案的文本
        StringBuilder mergedText = new StringBuilder(existingAnswer);
        for (Answer answer : answers) {
            if (answer.getText() != null) {
                mergedText.append(answer.getText());
            }
        }

        return mergedText.toString();
    }
}






