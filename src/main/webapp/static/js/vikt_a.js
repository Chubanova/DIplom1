var completed = false;
var closeBtn = "<button id=\"close\" class=\"ctrl-btn\">Выход</button>";

var firstTime = function () {
    var block =  true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"questionNumber="+qNumber+"&viktId="+vID+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html.split('&')[0] == "Q") {
                    putQuestion(html);
                    if (completed == true) check();
                } else if (html.split('&')[0] == "R") {
                    $("#next").hide();
                    $("#results").hide();
                    $("#check").hide();
                    $("#stat").html("");
                    if ($("#close").length == 0) {
                        $(closeBtn).appendTo($("#control-btns"));
                    } else {$("#close").fadeIn();}
                    document.getElementById('close').onclick = close;
                    completed = true;
                    putResults(html);
                }
                block = false;
            }
        }
    });
};

window.onload = firstTime;

function next(){
    qNumber++;
    
    if(!completed){ $("#stat").html("");}
    
    var block =  true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"questionNumber="+qNumber+"&viktId="+vID+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html.split('&')[0] == "Q") {
                    putQuestion(html);
                    if (completed == true) check();
                } else if (html.split('&')[0] == "R") {
                    $("#next").hide();
                    $("#results").hide();
                    $("#check").hide();
                    if ($("#close").length == 0) {
                        $(closeBtn).appendTo($("#control-btns"));
                    } else {$("#close").fadeIn();}
                    document.getElementById('close').onclick = close;
                    putResults(html);
                    $("#stat").html("");
                    completed = true;
                }
                block = false;
            }
        }
    });
}

function back(){
    if (qNumber > 1) { qNumber--; }

    if(!completed){ $("#stat").html("");}

    var block =  true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"questionNumber="+qNumber+"&viktId="+vID+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html.split('&')[0] == "Q") {
                    $("#next").fadeIn();
                    $("#results").fadeIn();
                    $("#check").fadeIn();
                    if ($("#close").length > 0) $("#close").hide();
                    putQuestion(html);
                    if (completed == true) check();
                }
            }
            block = false;
        }
    });
}

function check(){

    var block =  true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"questionChk=true&questionNumber="+qNumber+"&viktId="+vID+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html.split('&')[0] == "C") {
                    $("#next").fadeIn();
                    $("#results").fadeIn();
                    $("#check").fadeIn();
                    showTrueAnswer(html);
                }
            }
            block = false;
        }
    });
}

function complete() {
    completed = true;

    $("#stat").html("");

    if ($("#close").length == 0) {
        $(closeBtn).appendTo($("#control-btns"));
    } else {$("#close").fadeIn();}
    document.getElementById('close').onclick = close;
    $("#next").hide();
    $("#results").hide();
    $("#check").hide();

    var block =  true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"getRes=true&viktId="+vID+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html.split('&')[0] == "R" || html == 'R') {
                    putResults(html);
                }
            }
            block = false;
        }
    });
}

function close() {

    $("#stat").html("");

    var block =  true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"stop=true&viktId="+vID+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html == "V") {
                    document.location.href = "/viktorines";
                }
            }
            block = false;
        }
    });
}