package com.tsk.quizapp.service;

import com.tsk.quizapp.dao.QuestionDAO;
import com.tsk.quizapp.dao.QuizDAO;
import com.tsk.quizapp.model.Question;
import com.tsk.quizapp.model.QuestionWrapper;
import com.tsk.quizapp.model.Quiz;
import com.tsk.quizapp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDAO quizDAO;
    @Autowired
    QuestionDAO questionDAO;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions=questionDAO.findRandomQuestionsByCategory(category,numQ);

        Quiz quiz=new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDAO.save(quiz);

        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz= quizDAO.findById(id);
        List<Question> questionsFromDB=quiz.get().getQuestions();
        // convert Question into QuestionWraper
        List<QuestionWrapper> questionsForUser=new ArrayList<>();

        for (Question q:questionsFromDB){
            QuestionWrapper qw=new QuestionWrapper(q.getId(),q.getQuestionTitle(),
                    q.getOption1(),q.getOption2(),q.getOption3(),q.getOption4());
            questionsForUser.add(qw);
        }

        return new ResponseEntity<>(questionsForUser,HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz=quizDAO.findById(id).get();
        List<Question> questions=quiz.getQuestions();
        int correctAnswer=0;
        int i=0;
        for (Response r:responses){
            if (r.getResponse().equals(questions.get(i).getRightAnswer())){
                correctAnswer++;
            }
                i++;
        }
        return new ResponseEntity<>(correctAnswer,HttpStatus.OK);
    }
}
