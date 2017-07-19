var i;

function showTrueAnswer(html) {
    //Парсим строку "C&A#M38P;answerId=<ID>{&A#M38P;answerId=..}<STAT><ID>=<%>{<ID>=}"
    var ids = [];
    for (i = 1; i < html.split('<STAT>')[0].split('&A#M38P;').length; i++){
        ids[i-1] = html.split('<STAT>')[0].split('&A#M38P;')[i].split('=')[1];
    }
    for (i = 0; i < ids.length; i++){
        var id = "#id"+ids[i];
        $(id).css('color', 'green');
        $(id).css('font-weight', 'bold');
    }

    var doc = "";

    for (i = 0; i < html.split('<STAT>&A#M38P;')[1].split('&A#M38P;').length; i++){
        var num = i+1;
        var per = html.split('<STAT>&A#M38P;')[1].split('&A#M38P;')[i].split('=')[1];
        if (i == html.split('<STAT>&A#M38P;')[1].split('&A#M38P;').length - 1){
            doc = doc + "<div class='stat-group'><p class='stat-row'>" +
                "N) "+per+"% </p>" +
                "<div class='stat-line' style='width: "+per+"%;'>%</div></div>";
        } else {
            doc = doc + "<div class='stat-group'><p style='color: black; position: absolute'>"
                + num +") "+per+"% </p>" +
                "<div class='stat-line' style='width: "+per+"%;'>%</div></div>";
        }
    }

    $("#stat").html(doc);
}

function putQuestion(html) {


    //Парсим строку "Q&A#M38P;<Number>=<TEXT>&A#M38P;answerText=<TEXT>&A#M38P;answerId=<ID>{&A#M38P;answerText=..}"
    qNumber = html.split('&A#M38P;')[1].split('=')[0];
    var question = html.split('&A#M38P;')[1].substring(html.split('&A#M38P;')[1].indexOf('=')+1);

    var answers = [{}];
    for (i = 2; i < html.split('&A#M38P;').length; i++){
        var a = {};
        a.answerText = html.split('&A#M38P;')[i].split('<AND>')[0].substring(html.split('&A#M38P;')[i].split('<AND>')[0].indexOf('=')+1);
        a.answerId = html.split('&A#M38P;')[i].split('<AND>')[1].substring(html.split('&A#M38P;')[i].split('<AND>')[1].indexOf('=')+1);
        answers[i-2] = a;
    }

    var doc = "<div class=\"page-header\">" +
        "<h2>"+question+"</h2>" +
        "</div>" +
        "<div class='row answers'>";
    for (i = 0; i < answers.length; i++){
        var answer = answers[i];
        var num = i + 1;
        doc = doc + "<p id=\"id"+answer.answerId+"\"><input type=\"checkbox\" name=\"answerId\" value=\""+answer.answerId+"\"> "+num+") "+answer.answerText+"</p>";
    }
    doc = doc + "</div>";

    $("#question").html(doc).hide().fadeIn(1000);
}

function putResults(html) {
    //Парсим строку "R&A#M38P;<LOGIN>=<COUNT>{&A#M38P;<LOGIN>=..}"
    var results = "<div class=\"page-header\">" +
        "<h2>Результаты</h2>" +
        "</div>" +
        "<div class='row results'>";

    for (i = 1; i < html.split('&A#M38P;').length; i++){
        results = results + "<p class=\"resrow\">" + html.split('&A#M38P;')[i].substring(0,html.split('&A#M38P;')[i].lastIndexOf('=')) + ": " +
            html.split('&A#M38P;')[i].split('=')[html.split('&A#M38P;')[i].split('=').length - 1] + "</p>";
    }
    results = results + "</div>";

    $("#question").html(results).hide().fadeIn(1000);
}