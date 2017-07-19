package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.AnswerDAO;
import com.mesaeva.viktorines.domain.dao.QuestionDAO;
import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.ViktorineDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateAnswerDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateQuestionDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateViktorineDAO;
import com.mesaeva.viktorines.domain.entities.Answer;
import com.mesaeva.viktorines.domain.entities.CurrentVikt;
import com.mesaeva.viktorines.domain.entities.Question;
import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import com.mesaeva.viktorines.template.TemplateTags;
import org.apache.log4j.Logger;
import org.springframework.security.web.csrf.CsrfToken;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet(Pages.VIKTORINE)
public class ViktorineServlet extends AppHttpServlet {
    private static final String VIKTORINE = "Викторина";
    private static final String ADMIN_TEACHER_CONTENT_DELEGATE = "My_viktorine";
    private static final String STUDENT_CONTENT_DELEGATE = "viktorine";
    static Logger logger = Logger.getLogger(ViktorineServlet.class);


    private final UserDAO userDAO = new HibernateUserDAO();
    private final ViktorineDAO viktorineDAO = new HibernateViktorineDAO();
    private final QuestionDAO questionDAO = new HibernateQuestionDAO();
    private final AnswerDAO answerDAO = new HibernateAnswerDAO();

    private ViktorinesCleaner viktorinesCleaner = new ViktorinesCleaner();

    static volatile List<CurrentVikt> current = new ArrayList<>();
    /* Мапа, содержащая в себе ID викторины и мапу со всеми на неё ответами.
    *  Параметры - Integer - ИД викторины, String - login ответившего,
    *  Integer - номер вопроса, List - ответы
    */
    static volatile Map<Integer, Map<String, Map<Integer, List<Answer>>>> ansvers = new HashMap<>();
    /* Мапа, содержащая в себе ID викторины и мапу с правильными ответами,
    *  данными студентами. То есть, содержит в себе результаты. Флаг boolean говорит о готовности отрисовать результаты
     */
    static volatile Map<Integer, Map<String, Integer>> trueAnswersCount = new HashMap<>();
    /*  Тут флаг с готовностью отрисовать результаты
     */
    static volatile Map<Integer, Boolean> completed = new HashMap<>();

    public ViktorineServlet() throws NamingException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int role = userDAO.get(getAuthUsername()).getRoleId();
        Integer viktId = 0;
        viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));

        // AJAX ---->
        //Блок управления викториной ---->
        if (role == TemplateTags.ROLE_TEACHER || role == TemplateTags.ROLE_ADMIN) {
            //first time
            if (viktId != 0 && req.getParameter(TemplateTags.QUESTION_NUMBER) == null &&
                    req.getParameter(TemplateTags.VIKT_RES) == null && req.getParameter(TemplateTags.VIKT_END) == null) {
                current.add(new CurrentVikt(viktId, getAuthUsername(), 1));
                ansvers.put(viktId, new HashMap<>());
                trueAnswersCount.put(viktId, new HashMap<>());
                completed.put(viktId, false);
                viktorinesCleaner.add(viktId);
            }
            //Проверяем запрос на проверку вопроса
            if (req.getParameter(TemplateTags.QUESTION_CHK) != null) {
                checkQuestion(req, resp);
                return;
            }
            //Проверяем на запрос нового вопроса (next или back)
            if (req.getParameter(TemplateTags.QUESTION_NUMBER) != null && req.getParameter(TemplateTags.VIKT_ID) != null) {
                newQuestion(req, resp);
                return;
            }
            //Запрос завершения викторины
            if (req.getParameter(TemplateTags.VIKT_END) != null) {
                Iterator iterator = current.iterator();
                while (iterator.hasNext()) {
                    CurrentVikt c = (CurrentVikt) iterator.next();
                    if (c.getViktId().intValue() == viktId.intValue()) {
                        iterator.remove();
                        break;
                    }
                }
                ansvers.remove(viktId);
                trueAnswersCount.remove(viktId);
                completed.remove(viktId);
                resp.getWriter().print("V");
                return;
            }
            //Запрос результатов викторины
            if (req.getParameter(TemplateTags.VIKT_RES) != null) {
                getResult(viktId);
                printResults(resp, viktId);
                return;
            }
        }
        //Блок управления викториной <----
        //Блок студентов ---->
        if (role == TemplateTags.ROLE_STUDENT) {
            if (userDAO.get(getAuthUsername()).getOwnerId().intValue() != viktorineDAO.get(viktId).getIdUser()) {
                resp.getWriter().print("403 Forbidden.");
                return;
            } else if (!ansvers.keySet().contains(viktId)) {
                resp.getWriter().print("V");
                return;
            } else if (req.getParameter(TemplateTags.STUDENT_ANSWER) != null) {
                if (!ansvers.get(viktId).keySet().contains(getAuthUsername())) {
                    ansvers.get(viktId).put(getAuthUsername(), new HashMap<>());
                }
                studentsLogic(req, resp);
                return;
            } else if (req.getParameter("f") != null) {
                Integer numberQ = 1;
                for (CurrentVikt c : current) {
                    if (c.getViktId().intValue() == viktId) {
                        numberQ = c.getCurrentQ();
                    }
                }
                printNewQuestion(resp, viktId, numberQ);
                return;
            }
        }
        //Блок студентов <----
        // AJAX <----

        //Если викторина ещё не началась, то нарисовать шаблон
        if (req.getParameter(TemplateTags.CURRENT_QUESTION_NUMBER) == null) {
            if (role == TemplateTags.ROLE_STUDENT) {
                try {
                    HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
                    writer.setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                            .setTitle(VIKTORINE)
                            .setContentDelegate(STUDENT_CONTENT_DELEGATE)
                            .putData(TemplateTags.QUESTION, question(questionDAO.question(viktId, 1)))
                            .write();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
                    writer.setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                            .setTitle(VIKTORINE)
                            .setContentDelegate(ADMIN_TEACHER_CONTENT_DELEGATE)
                            .putData(TemplateTags.QUESTION, question(questionDAO.question(viktId, 1)))
                            .write();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Map<String, Object> question(Question q) {
        Map<String, Object> tmpQuestion = new HashMap<>();
        tmpQuestion.put(TemplateTags.QUESTION_VIKT_ID, q.getIdVikt());
        tmpQuestion.put(TemplateTags.QUESTION_NUMBER, q.getNumberOfQuestion());
        return tmpQuestion;
    }

    private void newQuestion(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer number = Integer.valueOf(req.getParameter(TemplateTags.QUESTION_NUMBER));
        current.stream().filter(v -> v.getLogin().equals(getAuthUsername())).forEach(v -> {
            v.setqIsChecked(false);
            v.setCurrentQ(number);
        });
        Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
        int i = printNewQuestion(resp, viktId, number);
        if (i == -1) {
            getResult(viktId);
            printResults(resp, viktId);
        }
    }

    private void checkQuestion(HttpServletRequest req, HttpServletResponse resp) {
        Integer number = Integer.valueOf(req.getParameter(TemplateTags.QUESTION_NUMBER));
        Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
        current.stream().filter(c -> c.getLogin().equals(getAuthUsername())).forEach(c -> {
            c.setqIsChecked(true);
        });
        printTrueAnswer(resp, viktId, number);
    }

    private void studentsLogic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer number = 0;
        number = Integer.valueOf(req.getParameter(TemplateTags.QUESTION_NUMBER));

        Integer viktId = Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID));
        if (!ansvers.get(viktId).get(getAuthUsername()).keySet().contains(number)) {
            ansvers.get(viktId).get(getAuthUsername()).put(number, new ArrayList<>());
        }
        //Засчитываем ответ на вопрос. Если он, конечно, есть.
        if (!req.getParameter(TemplateTags.ANSWER_ID).equals("")) {
            String answersFromReq[] = req.getParameter(TemplateTags.ANSWER_ID).split(",");
            List<Answer> a = new ArrayList<>();
            if (answersFromReq.length > 0)
                for (String s : answersFromReq)
                    a.add(answerDAO.get(Integer.valueOf(s)));
            Integer finalNumber = number;
            current.stream().filter(c -> c.getViktId().intValue() == viktId.intValue())
                    .filter(c -> finalNumber.intValue() == c.getCurrentQ()).forEach(c -> {
                List<Answer> answered = ansvers.get(viktId).get(getAuthUsername()).get(finalNumber);
                if (answered.isEmpty()) {
                    answered.addAll(a);
                }
            });
        }
        for (CurrentVikt c : current) {
            if (viktId.intValue() == c.getViktId().intValue()) {
                if (completed.get(viktId)) {
                    printResults(resp, viktId);
                    return;
                }
                if (number.intValue() == c.getCurrentQ() && !c.isqIsChecked()) {
                    resp.getWriter().print("W");
                } else if (number.intValue() == c.getCurrentQ() && c.isqIsChecked()) {
                    printTrueAnswer(resp, viktId, number);
                } else if (number == 0 || number.intValue() != c.getCurrentQ().intValue()) {
                    int i = printNewQuestion(resp, viktId, c.getCurrentQ());
                    if (i == -1) {
                        if (!completed.get(viktId)) {
                            resp.getWriter().print("W");
                        } else {
                            printResults(resp, viktId);
                            return;
                        }
                    }
                }
                break;
            }
        }
    }

    private void printTrueAnswer(HttpServletResponse resp, Integer viktId, Integer number) {
        try {
            List<Answer> alist = answerDAO.getAnswersForQuestion(questionDAO.question(viktId, number).getIdQuestion());
            StringBuilder result = new StringBuilder();
            result.append("C");
            alist.stream().filter(Answer::isFlag).forEach(a ->   {
                result.append(TemplateTags.AMP).append(TemplateTags.ANSWER_ID).append("=").append(a.getIdAnswer());
            });
            //Считаем процентики
            Integer count = 0;
            List<Integer> countPerAnswer = alist.stream().map(a -> 0).collect(Collectors.toList());
            countPerAnswer.add(0);

            for (String user : ansvers.get(viktId).keySet()) {
                if (ansvers.get(viktId).get(user).get(number).isEmpty()) {
                    count = count + 1;
                }
                count = count + ansvers.get(viktId).get(user).get(number).size();
            }
            //Считаем для первого, второго и тд вопроса
            for (int i = 0; i < alist.size(); i++) {
                for (String user : ansvers.get(viktId).keySet()) {
                    if (ansvers.get(viktId).get(user).get(number).isEmpty()) {
                        for (Answer a : ansvers.get(viktId).get(user).get(number)) {
                            if (a.getIdAnswer().intValue() == alist.get(i).getIdAnswer().intValue()) {
                                countPerAnswer.set(i, countPerAnswer.get(i) + 1);
                            }
                        }
                    }
                }
            }
            //А это те, кто воздержался.
            boolean exit = false;
            for (int i = 0; i < alist.size(); i++) {
                if (exit) break;
                for (String user : ansvers.get(viktId).keySet()) {
                    if (ansvers.get(viktId).get(user).get(number).isEmpty()) {
                        countPerAnswer.set(alist.size(), countPerAnswer.get(alist.size()) + 1);
                        exit = true;
                        break;
                    }
                }
            }

            List<Integer> percentPerAnswer = countPerAnswer.stream().map(a -> 0).collect(Collectors.toList());
            if (count != 0) {
                for (int i = 0; i < percentPerAnswer.size(); i++) {
                    String percent = String.valueOf((countPerAnswer.get(i)) / count * 100);
                    if (percent.contains(".")) {
                        percent = percent.substring(0, percent.indexOf('.'));
                    }
                    percentPerAnswer.set(i, Integer.valueOf(percent));
                }
            } else percentPerAnswer.set(percentPerAnswer.size() - 1, 100);

            result.append("<STAT>");

            for (int i = 0; i < percentPerAnswer.size(); i++) {
                result.append(TemplateTags.AMP).append(i == percentPerAnswer.size() - 1 ? "NOA" : TemplateTags.ANSWER_ID)
                        .append("=").append(percentPerAnswer.get(i));
            }

            //Результат прилетит в форме строки "C&answerId=<ID>{&answerId=..}<STAT><ID>=<%>{<ID>=}"
            resp.getWriter().print(result.toString());
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private int printNewQuestion(HttpServletResponse resp, Integer viktId, Integer number) throws IOException {
        try {
            Question q = questionDAO.question(viktId, number);
            List<Answer> answersForQuestion = answerDAO.getAnswersForQuestion(q.getIdQuestion());
            StringBuilder result = new StringBuilder();
            result.append("Q").append(TemplateTags.AMP).append(q.getNumberOfQuestion()).append("=").append(q.getTextQuestion());
            for (Answer a : answersForQuestion) {
                result.append(TemplateTags.AMP).append(TemplateTags.ANSWER_TEXT).append("=").append(a.getTextAnswer()).append("<AND>")
                        .append(TemplateTags.ANSWER_ID).append("=").append(a.getIdAnswer());
            }
            //Результат прилетит в форме строки "Q&question=<TEXT>&answerText=<TEXT>{&answerText=..}"
            resp.getWriter().print(result.toString());
        } catch (Exception e) {
            //Если такого вопроса нет - рисуем результаты
            return -1;
        }
        return 0;
    }

    private void printResults(HttpServletResponse resp, Integer viktId) throws IOException {
        //Формат строки для выхлопа клиенту : "R&<LOGIN>=<COUNT>{&<LOGIN>=..}"
        StringBuilder result = new StringBuilder();
        result.append("R");
        for (String user : trueAnswersCount.get(viktId).keySet()) {
            result.append(TemplateTags.AMP).append(user).append("=").append(trueAnswersCount.get(viktId).get(user));
        }
        resp.getWriter().print(result.toString());
    }

    private void getResult(Integer viktId) {
        for (String user : ansvers.get(viktId).keySet()) {
            trueAnswersCount.get(viktId).put(user, 0);
            Map<Integer, List<Answer>> answers = ansvers.get(viktId).get(user);
            answers.keySet().stream().filter(i -> answers.get(i).isEmpty()).forEach(i -> {
                List<Answer> trueAnswers = answerDAO.getAnswersForQuestion(
                        questionDAO.question(viktId, i).getIdQuestion());
                Iterator iterator = trueAnswers.iterator();
                while (iterator.hasNext()) {
                    Answer a = (Answer) iterator.next();
                    if (!a.isFlag()) iterator.remove();
                }
                List<Integer> answeredIds = answers.get(i).stream()
                        .map(Answer::getIdAnswer).collect(Collectors.toList());
                List<Integer> trueIds = trueAnswers.stream()
                        .map(Answer::getIdAnswer).collect(Collectors.toList());

                if (answeredIds.size() == trueIds.size() && answeredIds.containsAll(trueIds)) {
                    Integer count = trueAnswersCount.get(viktId).get(user) + 1;
                    trueAnswersCount.get(viktId).put(user, count);
                }
            });
        }
        trueAnswersCount.put(viktId, sortByValue(trueAnswersCount.get(viktId)));
        completed.put(viktId, true);
        viktorinesCleaner.completed(viktId);
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}