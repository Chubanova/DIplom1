function submit(){

    var viktId;

    var o = document.getElementsByName('viktId');
    for (var i = 0; i < o.length; i++) {
        if (o[i].checked) {
            viktId = o[i].value;
            break;
        }
    }

    var block = true;

    $.ajax({
        url:"/viktorine",
        type:"GET",
        data:"viktId="+viktId+"&_csrf="+csrf,
        success:function(html) {
            if (html) {
                if (html == "V") {
                    alert("Викторина ещё не началась.")
                } else {
                    document.location.href = "/viktorine?viktId="+viktId+"&_csrf="+csrf;
                }
            }
            block = false;
        }
    });
}