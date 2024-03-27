// 서버 측으로부터 message가 있으면 해당 메시지를 담은 알림창을 띄워준다.
document.addEventListener("DOMContentLoaded", function () {
    var messageElement = document.querySelector('.alert');
    if (messageElement) {
        setTimeout(function () {
            messageElement.style.display = 'none';
        }, 5000); // 5000 milliseconds (5 seconds)
    }
});

function bodyAlert(message = "") {
    $("#message").text(message);
    let messageElement = document.querySelector('.alert');
    messageElement.style.display = 'block';
    if (messageElement) {
        setTimeout(function () {
            messageElement.style.display = 'none';
        }, 5000); // 5000 milliseconds (5 seconds)
    }
}

// 중복 확인이나 이메일 인증 번호 확인을 완료했을 때 뜨는 알림창에서 x 표시를 누르면 해당 알림창이 꺼지게 하는 함수이다.
$(".messageBtn").click(function () {
    var messageElement = document.querySelector('.alert');
    if (messageElement) {
        messageElement.style.display = 'none';
    }
});
