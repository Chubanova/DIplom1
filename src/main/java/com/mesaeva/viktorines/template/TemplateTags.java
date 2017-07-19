package com.mesaeva.viktorines.template;

public class TemplateTags {
    private TemplateTags(){
        //Default constructor
    }
    public static final String FORM_DATA = "formData";
    public static final String USERNAME = "username";
    public static final String USERS = "users";

    public static final String USER = "user";
    public static final String USER_ID = "userid";
    public static final String USER_LOGIN = "login";
    public static final String USER_ENABLED = "enabled";
    public static final String USER_ROLE = "role";
    public static final String USER_OWNER_ID = "ownerId";

    public static final String VIKTS = "vikts";
    public static final String VIKT_ID = "viktId";
    public static final String VIKT_NAME = "viktName";
    public static final String VIKT_DISCIPLINE = "viktDiscipline";
    public static final String VIKT_ID_USER = "viktIdUser";
    public static final String VIKT_END = "stop";
    public static final String VIKT_RES = "getRes";
    public static final String VIKT_CREATE = "create";
    public static final String VIKT_EDIT = "edit";

    public static final String ANSWERS = "answers";
    public static final String ANSWER_ID = "answerId";
    public static final String ANSWER_QUESTION_ID = "answerQuestionId";
    public static final String ANSWER_TEXT = "answerText";
    public static final String ANSWER_FLAG = "answerFlag";
    public static final String STUDENT_ANSWER = "sa";
    public static final String ANSWERS_SPL = "<<A-SPL>>";

    public static final String QUESTION = "question";
    public static final String QUESTION_CHK = "questionChk";
    public static final String QUESTION_ID = "questionId";
    public static final String QUESTION_VIKT_ID = "questionViktId";
    public static final String QUESTION_TEXT = "questionText";
    public static final String QUESTION_NUMBER = "questionNumber";
    public static final String CURRENT_QUESTION_NUMBER = "currentQuestion";
    public static final String NEXT_QUESTION = "nq";
    public static final String EDIT_QUESTION = "eq";
    public static final String PREV_QUESTION = "pq";

    public static final String DISCIPLINES = "disciplines";
    public static final String DISCIPLINE_NAME = "disciplineName";
    public static final String DISCIPLINE_NAME_FOR_REMOVE = "disciplineNameR";
    public static final String TEACHER_NAME = "teacherName";
    public static final String TEACHERS = "teachers";
    public static final String ROLES = "roles";
    public static final String ROLE = "role";
    public static final String IS_ADMIN = "isAdmin";

    public static final String AMP = "&A#M38P;";

    public static final int ROLE_STUDENT = 1;
    public static final int ROLE_TEACHER = 2;
    public static final int ROLE_ADMIN = 3;

    public static class FormTags {
        private FormTags() {
            //Default constructor
        }
        public static final String FAILED = "failed";
        public static final String ACTION = "action";
        public static final String MESSAGE = "message";
        public static final String SUBMIT_LABEL = "submitLabel";
        public static final String FIELDS = "fields";

        public static final String NAME = "name";
        public static final String VALUE = "value";
        public static final String VALUES = "values";
        public static final String LABEL = "label";
        public static final String TYPE = "type";
        public static final String OPTIONS = "options";
        public static final String OPTION_LABEL = "label";
        public static final String OPTION_VALUE = "value";
        public static final String ERROR = "error";
    }
}
