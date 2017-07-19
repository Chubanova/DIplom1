var numberQ;
var count = 2;
var i;
var addABtn = "<div class='addAButton' id='addAButton'><button onclick='addAnswer()' id='addA' class='addABtn'>+</button></div>";
var remABtn = "<div class='remAButton' id='remAButton'><button onclick='remAns()' id='remA' class='remABtn'>-</button></div>";

var closeBtn = "<button id=\"complete\" onclick='complete()' class=\"ctrl-btn\">Завершить</button>";
var nextQBtn = "<button id=\"next\" onclick='next()' class=\"ctrl-btn\">Следующий</button>";
var backQBtn = "<button id=\"back\" onclick='back()' class=\"ctrl-btn\">Предыдущий</button>";

var nv = true;

function submit(){
    if(viktId != 0){
        nv = false;
    }

    var viktName = document.getElementById("name").value;
    if (viktName == ""){
        alert("Введите название викторины.");
        return;
    }

    var teacherName = null;
    if ($("#teacherName").length > 0) {
       teacherName = document.getElementById("teacherName").value;
    }

    var block =  true;
    if (nv) {
        $.ajax({
            url: "/new",
            type: "POST",
            data: {
                create: "true",
                viktName: viktName,
                disciplineName: document.getElementById("disciplineName").value,
                teacherName: teacherName,
                _csrf: csrf
            },
            success: function (html) {
                if (html) {
                    if (html.split('&')[0] == "S") {
                        viktId = html.split('&')[1].split('=')[1];
                        numberQ = 1;
                        printFormQuestion();
                        printControlBtns();
                    } else if (html.split('&')[0] == "E") {
                        alert("Викторина с таким именем уже существует.");
                        return;
                    }
                }
                block = false;
            }
        });
    } else {
        $.ajax({
            url: "/new",
            type: "POST",
            data: {
                edit: "true",
                viktName: viktName,
                viktId: viktId,
                disciplineName: document.getElementById("disciplineName").value,
                teacherName: teacherName,
                _csrf: csrf
            },
            success: function (html) {
                if (html) {
                    //Тут прилетит строка "S&QTEXT=<TEXT>&ANSWERS=.."
                    if (html.split('&A#M38P;')[0] == "S") {
                        numberQ = 1;
                        fillFormQuestion(html);
                        printControlBtns();
                    }
                }
                block = false;
            }
        });
    }
}

function printFormQuestion() {
    var doc = "<div class=\"page-header\">" +
                "<h2>" +
                    "<textarea placeholder='Вопрос' id='question' class='form-control' name='question' style='resize: none' rows='5'/>" +
                "</h2>" +
                "</div>" +
                "<div id='answers' class='row answers'>";

    for (i = 0; i < count; i++){
        var num = i+1;
        doc +=  "<p id='id"+i+"'>" +
                    "<input type='checkbox' name='answerId' value='"+num+"' style='margin-top: 20px;'> "+num+") " +
                    "<input placeholder='Введите вариант ответа' id='answer' class='form-control a-textfield' type='text' name='answer'/>" +
                "</p>";
    }
    doc += "</div>";
    doc += addABtn;

    $("#newviktall").html(doc);
}

function fillFormQuestion(html) {
    //html "S&QTEXT=<QTEXT>&ANSWERS=<0ANS1><A-SPL><1ANS2><..>"
    var question = html.split('&A#M38P;')[1].substring(html.split('&A#M38P;')[1].indexOf('=') + 1);
    var answers = html.split('&A#M38P;')[2].substring(html.split('&A#M38P;')[2].indexOf('=') + 1).split('<<A-SPL>>');
    count = answers.length;
    var trueAnswers = "";
    for (i = 0; i < count; i++){
        if (answers[i].split('')[0] == 1){
            if (trueAnswers.split('').length == 0){
                trueAnswers += i;
            } else {
                trueAnswers += ","+i;
            }
        }
        answers[i] = answers[i].substring(1);
    }

    var tal = trueAnswers.split(',').length;

    var doc = "<div class=\"page-header\">" +
        "<h2>" +
        "<textarea placeholder='Вопрос' id='question' class='form-control' name='question' style='resize: none' rows='5'>" + question + "</textarea>" +
        "</h2>" +
        "</div>" +
        "<div id='answers' class='row answers'>";
    for (i = 0; i < count; i++){
        var trueA = false;
        if (tal > 1) {
            for (var j = 0; j < trueAnswers.split(',').length; j++) {
                if (trueAnswers.split(',')[j] == i) {
                    trueA = true;
                    break;
                }
            }
        } else if (trueAnswers == i){trueA = true;}

        var num = i+1;
        doc +=  "<p id='id"+i+"'>";
        if (trueA) {
            doc += "<input type='checkbox' name='answerId' value='" + num + "' style='margin-top: 20px;' checked> " + num + ") ";
        } else {
            doc += "<input type='checkbox' name='answerId' value='" + num + "' style='margin-top: 20px;' > " + num + ") ";
        }
        doc +=  "<input placeholder='Введите вариант ответа' id='answer' class='form-control a-textfield' type='text' name='answer' value='"+answers[i]+"'/>" +
            "</p>";
    }
    doc += "</div>";
    doc += addABtn;

    $("#newviktall").html(doc);

    if (count > 2) {
        if ($("#remAButton").length == 0) {
            $(remABtn).appendTo($("#newviktall"));
        }
        else {
            $("#remAButton").fadeIn();
        }
    }
}

function addAnswer() {
    var num = count + 1;
    var answer = "<p id='id"+count+"'>" +
                    "<input type='checkbox' name='answerId' value='"+num+"' style='margin-top: 20px;'> "+num+") " +
                    "<input placeholder='Введите вариант ответа' id='answer' class='form-control a-textfield' type='text' name='answer' value=''/>" +
                 "</p>";
    count++;

    $(answer).appendTo($("#answers"));

    if (count > 2) {
        if ($("#remAButton").length == 0) {
            $(remABtn).appendTo($("#newviktall"));
        }
        else {
            $("#remAButton").fadeIn();
        }
    }
}

function remAns() {
    count--;
    $("#id"+count).remove();
    if (count == 2){
        $("#remAButton").hide();
    }
}

function printControlBtns() {
    $("#control-btns").html(backQBtn+nextQBtn+closeBtn);
}

function next() {

    var data = getData();

    var block =  true;
    if (nv) {
        count = 2;

        $.ajax({
            url: "/new",
            type: "POST",
            data: {
                nq: "true",
                questionNumber: numberQ,
                questionText: data.split('<QSPLITA>')[0],
                viktId: viktId,
                answers: data.split('<QSPLITA>')[1],
                _csrf: csrf
            },
            success: function (html) {
                if (html == "ok") {
                    printFormQuestion();
                }
                block = false;
            }
        });
    } else {
        $.ajax({
            url: "/new",
            type: "POST",
            data: {
                eq: "true",
                questionNumber: numberQ,
                questionText: data.split('<QSPLITA>')[0],
                viktId: viktId,
                answers: data.split('<QSPLITA>')[1],
                _csrf: csrf
            },
            success: function (html) {
                if (html.split('&A#M38P;')[0] == "S") {
                    fillFormQuestion(html);
                } if (html == "nq"){
                    printFormQuestion();
                }
                block = false;
            }
        });
    }
    numberQ++;
}

function back() {
    if (numberQ == 1) {return;}
    
    numberQ--;
    
    var block = true;

    $.ajax({
        url: "/new",
        type: "POST",
        header: 'Content-type: text/html; charset=UTF-8',
        data: {
            pq: "true",
            questionNumber: numberQ,
            viktId: viktId,
            _csrf: csrf
        },
        success: function (html) {
            if (html.split('&A#M38P;')[0] == "S") {
                fillFormQuestion(html);
            }
            block = false;
        }
    });
}

function complete () {
    var data = getData();

    var block = true;

    $.ajax({
        url: "/new",
        type: "POST",
        header: 'Content-type: text/html; charset=UTF-8',
        data: {
            stop: "true",
            questionNumber: numberQ,
            questionText: data.split('<QSPLITA>')[0],
            viktId: viktId,
            answers: data.split('<QSPLITA>')[1],
            _csrf: csrf
        },
        success: function (html) {
            if (html == "ok") {
                document.location.href = "/viktorines";
            }
            block = false;
        }
    });
}

function getData() {
    
    var question = $("#question").val();
    if (question == "") {
        alert("Введите вопрос.");
        return;
    }
    
    var trueAnswers = $(':checkbox').map(function (i, el) {
        if ($(el).prop('checked')) {
            return $(el).val();
        }
    }).get();
    
    if (trueAnswers.length == 0) {
        alert("Выберите хотя-бы 1 правильный ответ.");
        return;
    }
    
    var ok = true;
    var answersDefArray = $(':text').map(function (i, el) {
        if(ok) {
            if ($(el).val() != "") {
                return "0"+$(el).val();
            } else {
                alert("Заполните все поля.");
                ok = false;
            }
        }
    }).get();
    
    if (ok) {
        if (answersDefArray.length < 2) {
            alert("Введите хотя-бы 2 варианта ответа");
            return;
        }

        for (i = 0; i < count; i++){
            for (var j = 0; j < trueAnswers.length; j++){
                if (trueAnswers[j] == i+1){
                    answersDefArray[i] = "1"+answersDefArray[i].substring(1);
                    break;
                }
            }
        }
        
        var answers = answersDefArray[0];
        for (i = 1; i < answersDefArray.length; i++){
            answers += "<<A-SPL>>" + answersDefArray[i];
        }
        
        return question+"<QSPLITA>"+answers;
    }
}