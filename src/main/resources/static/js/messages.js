document.addEventListener("DOMContentLoaded", function () {
    var messageElement = document.querySelector('.alert');
    if (messageElement) {
        setTimeout(function () {
            messageElement.style.display = 'none';
        }, 5000); // 5000 milliseconds (5 seconds)
    }
});

$(".messageBtn").click(function () {
    //$(".alert").hide();
    var messageElement = document.querySelector('.alert');
    if (messageElement) {
        messageElement.style.display = 'none';
    }
});
