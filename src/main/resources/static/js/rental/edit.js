function dateFormat(today, format) {
    format = format.replace("YYYY", today.getFullYear());
    format = format.replace("MM", ("0"+(today.getMonth() + 1)).slice(-2));
    format = format.replace("DD", ("0"+ today.getDate()).slice(-2));
    return format;
}

function setExpectedRentalOn() {
    const date = dateFormat(new Date(),'YYYY-MM-DD');
    $("#expectedRentalOn").attr("min", date);
}

function setExpectedReturnOn() {
    const date = dateFormat(new Date(),'YYYY-MM-DD');
    $("#expectedReturnOn").attr("min", date);
}


$(function() {
    // 各日付の最小値を指定（登録日当日の日付）
    // setExpectedRentalOn();
    // setExpectedReturnOn();

    // 貸出予定日が変更された場合、返却予定日の最小値を変更
    // 貸出予定日 =< 返却予定日 となるようにする
    $("#expectedRentalOn").on("change", function(){
        let value = $(this).val();
        $("#expectedReturnOn").val(null);
        if (value === null) {
            // setExpectedRentalOn();
            // setExpectedReturnOn();
        } else {
            $("#expectedReturnOn").attr("min", $(this).val());
        }
    });
})