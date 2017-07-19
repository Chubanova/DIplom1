package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.*;
import com.mesaeva.viktorines.domain.dao.impl.*;
import com.mesaeva.viktorines.domain.entities.Answer;
import com.mesaeva.viktorines.domain.entities.Question;
import com.mesaeva.viktorines.domain.entities.User;
import com.mesaeva.viktorines.domain.entities.Viktorine;
import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import com.mesaeva.viktorines.template.TemplateTags;
import org.springframework.security.web.csrf.CsrfToken;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(Pages.CREATE_VIKTORINE)
public class CreateViktorineServlet extends AppHttpServlet {
    private static final String CONTENT_DELEGATE = "newviktorine";
    private static final String PAGE_TITLE = "Добавление викторины";

    private final UserDAO userDAO = new HibernateUserDAO();
    private final DisciplineDAO disciplineDAO = new HibernateDisciplineDAO();
    private final ViktorineDAO viktorineDAO = new HibernateViktorineDAO();
    private final QuestionDAO questionDAO = new HibernateQuestionDAO();
    private final AnswerDAO answerDAO = new HibernateAnswerDAO();

    public CreateViktorineServlet() throws NamingException {
        //default constructor
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer role = TemplateTags.ROLE_STUDENT;
        try {
            role = userDAO.get(getAuthUsername()).getRoleId();
        } catch (Exception e) {
            resp.sendError(403);
        }
        HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
        if (req.getParameter(TemplateTags.VIKT_EDIT) != null) {
            Viktorine v = viktorineDAO.get(Integer.parseInt(req.getParameter(TemplateTags.VIKT_ID)));
            User user = userDAO.get(v.getIdUser());
            writer.putData(TemplateTags.TEACHER_NAME, user.getUsername())
                    .putData(TemplateTags.DISCIPLINE_NAME, disciplineDAO.getNameById(v.getIdDicipline()))
                    .putData(TemplateTags.VIKT_NAME, v.getName())
                    .putData(TemplateTags.VIKT_ID, v.getIdVikt());
        }
        List<String> disciplines = disciplineDAO.list();
        switch (role) {
            case TemplateTags.ROLE_STUDENT:
                resp.sendRedirect(Pages.VIKTORINES);
                break;
            case TemplateTags.ROLE_TEACHER:
                writer.setContentDelegate(CONTENT_DELEGATE)
                        .setTitle(PAGE_TITLE)
                        .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                        .putData(TemplateTags.DISCIPLINES, disciplines)
                        .write();
                break;
            case TemplateTags.ROLE_ADMIN:
                List<String> teacher = userDAO.listTeachers();
                writer.setContentDelegate(CONTENT_DELEGATE)
                        .setTitle(PAGE_TITLE)
                        .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                        .putData(TemplateTags.DISCIPLINES, disciplines)
                        .putData(TemplateTags.TEACHERS, teacher)
                        .write();
                break;
            default:
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer role = userDAO.get(getAuthUsername()).getRoleId();
        if (role == TemplateTags.ROLE_STUDENT) {
            resp.sendError(403);
            return;
        }

        if (req.getParameter(TemplateTags.VIKT_CREATE) != null) {
            String name = req.getParameter(TemplateTags.VIKT_NAME);
            String teacherName;
            if (req.getParameter(TemplateTags.TEACHER_NAME) != null &&
                    !req.getParameter(TemplateTags.TEACHER_NAME).equals("")) {
                teacherName = req.getParameter(TemplateTags.TEACHER_NAME);
            } else {
                teacherName = userDAO.get(getAuthUsername()).getUsername();
            }
            Integer teacherId = userDAO.get(teacherName, 1).getIdUser();

            Viktorine v = viktorineDAO.get(name, teacherId);
            if (v != null && v.getIdUser().intValue() == teacherId.intValue()) {
                resp.getWriter().print("E&viktExist=true");
                return;
            }
            viktorineDAO.create(
                    new Viktorine(
                            name,
                            disciplineDAO.getIdByName(req.getParameter(TemplateTags.DISCIPLINE_NAME)),
                            teacherId));
            resp.getWriter().print("S&" + TemplateTags.VIKT_ID + "=" + viktorineDAO.get(name, teacherId).getIdVikt());
            return;
        } else if (req.getParameter(TemplateTags.VIKT_EDIT) != null) {
            String name = req.getParameter(TemplateTags.VIKT_NAME);
            Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
            String teacherName;
            if (req.getParameter(TemplateTags.TEACHER_NAME) != null &&
                    !req.getParameter(TemplateTags.TEACHER_NAME).equals("")) {
                teacherName = req.getParameter(TemplateTags.TEACHER_NAME);
            } else {
                teacherName = userDAO.get(getAuthUsername()).getUsername();
            }
            Integer teacherId = userDAO.get(teacherName, 1).getIdUser();
            Viktorine v = new Viktorine(name,
                    disciplineDAO.getIdByName(req.getParameter(TemplateTags.DISCIPLINE_NAME)),
                    teacherId);
            v.setIdVikt(viktId);
            viktorineDAO.update(v);
            resp.getWriter().print(questionInfo(viktId, 1));
            return;
        } else if (req.getParameter(TemplateTags.VIKT_END) != null) {
            Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
            String answers = req.getParameter(TemplateTags.ANSWERS);
            Integer numberQ = Integer.valueOf(req.getParameter(TemplateTags.QUESTION_NUMBER));
            String qText = req.getParameter(TemplateTags.QUESTION_TEXT);

            Question q = questionDAO.question(viktId, numberQ);
            if (q == null) {
                questionDAO.create(new Question(viktId, qText, numberQ));
                Integer qId = questionDAO.question(viktId, numberQ).getIdQuestion();
                createAnswersFromReq(answers, qId);
                resp.getWriter().print("ok");
            } else {
                q.setTextQuestion(qText);
                questionDAO.update(q);
                answerDAO.clean(q.getIdQuestion());
                createAnswersFromReq(answers, q.getIdQuestion());
                resp.getWriter().print("ok");
            }
        } else if (req.getParameter(TemplateTags.PREV_QUESTION) != null) {
            Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
            Integer numberQ = Integer.valueOf(req.getParameter(TemplateTags.QUESTION_NUMBER));
            resp.getWriter().print(questionInfo(viktId, numberQ));
            return;
        } else if (req.getParameter(TemplateTags.EDIT_QUESTION) != null || req.getParameter(TemplateTags.NEXT_QUESTION) != null) {
            Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
            String answers = req.getParameter(TemplateTags.ANSWERS);
            Integer numberQ = Integer.valueOf(req.getParameter(TemplateTags.QUESTION_NUMBER));
            String qText = req.getParameter(TemplateTags.QUESTION_TEXT);

            if (req.getParameter(TemplateTags.EDIT_QUESTION) == null) {
                questionDAO.create(new Question(viktId, qText, numberQ));

                Integer qId = questionDAO.question(viktId, numberQ).getIdQuestion();

                createAnswersFromReq(answers, qId);

                resp.getWriter().print("ok");
                return;
            } else {
                Question q = questionDAO.question(viktId, numberQ);
                if (q == null) {
                    q = new Question(viktId, qText, numberQ);
                    questionDAO.create(q);
                } else {
                    q.setTextQuestion(qText);
                    questionDAO.update(q);
                }
                Integer qId = questionDAO.question(viktId, numberQ).getIdQuestion();
                answerDAO.clean(qId);
                createAnswersFromReq(answers, qId);

                Question nq = questionDAO.question(viktId, numberQ + 1);
                if (nq == null) {
                    resp.getWriter().print(TemplateTags.NEXT_QUESTION);
                    return;
                } else {
                    resp.getWriter().print(questionInfo(viktId, numberQ + 1));
                    return;
                }
            }
        }
    }

    private String questionInfo(Integer viktId, Integer numberQ) {
//        "S&QTEXT=<QTEXT>&ANSWERS=<0ANS1><A-SPL><1ANS2><..>"
        StringBuilder result = new StringBuilder();
        result.append("S").append(TemplateTags.AMP);
        Question q = questionDAO.question(viktId, numberQ);
        List<Answer> answers = answerDAO.getAnswersForQuestion(q.getIdQuestion());
        result.append(TemplateTags.QUESTION_TEXT)
                .append("=")
                .append(q.getTextQuestion())
                .append(TemplateTags.AMP)
                .append(TemplateTags.ANSWERS)
                .append("=")
                .append(answers.get(0).isFlag() ? "1" : "0")
                .append(answers.get(0).getTextAnswer());
        answers.stream().filter(a -> a.getIdAnswer().intValue() != answers.get(0).getIdAnswer().intValue()).forEach(a -> {
            result.append(TemplateTags.ANSWERS_SPL);
            if (a.isFlag()) {
                result.append("1");
            } else {
                result.append("0");
            }
            result.append(a.getTextAnswer());
        });
        return result.toString();
    }

    private void createAnswersFromReq(String answers, int qId) {
        for (String s : answers.split(TemplateTags.ANSWERS_SPL)) {
            String answerText = s.substring(1);
            Answer a = new Answer();
            a.setTextAnswer(answerText);
            a.setFlag(s.startsWith("1"));
            a.setIdQuestion(qId);
            answerDAO.create(a);
        }
    }
}
