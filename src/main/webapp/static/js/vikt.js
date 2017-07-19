var vals;

function answer(){
    vals = $(':checkbox').map(function (i, el) {
        if ($(el).prop('checked')) {
            return $(el).val();
        }
    }).get();
    $("#send").css('background-color','#EF1414');
    req();
}

var firstTime = function () {
    var block = true;

    $.ajax({
        url: "/viktorine",
        type: "GET",
        data:{f: "true",
                viktId: vID,
                _csrf: csrf},
        success: function (html) {
            if (html) {
                var text = html;
                if (text.split('&')[0] == "Q"){
                    putQuestion(text);
                } else if (text.split('&')[0] == "R"){
                    $("#send").hide();
                    putResults(text);
                    clearInterval(ref);
                    $("#stat").html("");
                } else if (text == "V"){
                    document.location.href = "/viktorines";
                }
            }
            block = false;
        }
    });
};

window.onload = firstTime;

var ref = setInterval(req, 3000);

function req() {

    if(!vals){ vals = ""; }

    var block = true;

    $.ajax({
        url: "/viktorine",
        type: "GET",
        data: "sa=true" + "&answerId=" + vals + "&questionNumber=" + qNumber + "&viktId=" + vID + "&_csrf=" + csrf,
        success: function (html) {
            if (html) {
                var text = html;
                if (text == 'W'){
                    return;
                } else if (text.split('&')[0] == "C"){
                    showTrueAnswer(text);
                } else if (text.split('&')[0] == "Q"){
                    $("#send").css('background-color','#08B90F');
                    vals = "";
                    putQuestion(text);
                    $("#stat").html("");
                } else if (text.split('&')[0] == "R"){
                    $("#send").hide();
                    putResults(text);
                    clearInterval(ref);
                    $("#stat").html("");
                } else if (text == "V"){
                    document.location.href = "/viktorines";
                }
            }
            block = false;
        }
    });
}

