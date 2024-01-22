var timeId;

// id가 message와 notice인 속성을 사용해 메시지를 웹페이지에 표시하는 함수이다.
function showMessage(message, noticeClass) {
    $("#message").text(message);
    $("#notice").attr('class', noticeClass);
    $("#notice").show();
    clearTimeout(timeId);
    timeId = setTimeout(function () { $("#notice").hide(); }, 5000); // 5000 milliseconds (5 seconds)
}
// 이메일 인증번호 전송 함수
function checkEmail(message) {
/*    if(!isEmailEmpty() || !isChkEmpty(message)) return false;
    $.ajax({
        type: "post",
        url: "/mailSend",
        dataType: "json",
        async: false,
        contentType: "application/json; charset-utf-8",
        data: JSON.stringify({"email": $("#email").val()}),
        success: function (data) {
            showMessage('해당 이메일로 인증번호 발송이 완료되었습니다. 확인 부탁드립니다.');
            //console.log("data : " + data);
            emailConfirm = 0;
            //$("#emailNumberTxt").html("<span id='emconfirmchk'>인증번호</span>")
            $("#emailNumber").val(null).attr('class', 'form-control');
            $("#numberSend").attr('value', 'Y').text('재전송');
            $("#emailNumberSend").prop('checked', true);
            //chkEmailNumber(data);
            //$("#numberSend").text('재전송');

            clearInterval(countdown);
            seconds = 60 * 30; // 30분(1800초)

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
        },
        error: function(e) {
            alert("인증번호 전송에 실패했습니다. 다시 시도해 주시기 바랍니다.");
        }
    })*/
    let form = document.getElementById("submitForm");
    form.action = "/members/emailNumberSend";
    form.submit();
}

function countDown() {
    document.addEventListener("DOMContentLoaded", function () {
        if ($("#emailNumberSend").is(":checked") === true) {
            $("#numberSend").text('재전송');

            clearInterval(countdown);
            seconds = $("#timeCount").val();//60 * 30; // 30분(1800초)

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
        }
    });
}

// 이메일 인증 번호와 사용자가 기입한 번호가 일치하는지 확인하는데 쓰이는 함수이다.
/*function chkEmailNumber(data) {
    $("#numberCheck").on("click", function () {
/!*        if (data != $("#emailNumber").val()) {
            //$("#emailconfirmTxt").html("<span id='emconfirmchk'>인증 실패</span>");
            showMessage('인증번호가 다릅니다.', 'alert alert-danger alert-dismissible fade show mt-3');
            $("#emailNumber").attr('class', 'form-control fieldError');
        } else {
            //$("#emailconfirmTxt").html("<span id='emconfirmchk'>인증 완료</span>");
            showMessage('인증 완료되었습니다.', 'alert alert-info alert-dismissible fade show mt-3');
            $("#emailNumber").attr('class', 'form-control');
            $("#emailNumberCheck").prop('checked', true);
            $('#time').text("");
            //emailConfirm = 1;
            document.getElementById("emailForm").submit();
        }*!/
        emailNumberChk();
    })
}*/

var seconds; // 남은 시간 변수
var countdown; // 카운트다운을 관리하는 변수

// 시간을 업데이트하고 화면에 표시하는 함수
function updateCountdown() {
/*    if ($("#emailNumberCheck").is(":checked")) {
        return;
    }*/
    if (seconds >= 0) {
        //console.log(seconds);
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        if(!$("#emailNumberCheck").is(":checked")) $('#time').text(`${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`);
        document.getElementById("timeCount").value = seconds--;
    } else {
        clearInterval(countdown);
        showMessage('인증번호 유효시간이 만료되었습니다.', 'alert alert-danger alert-dismissible fade show mt-3')
    }
}

// 인증번호 확인 전에 인증번호를 발급 받았는지 여부를 묻는 함수이다.
function beforeNumberCheck() {
    $("#numberCheck").on('click', function () {
        if ($("#numberSend").val() == "N") {
            //showMessage('먼저 인증번호 발급을 해주세요.', 'alert alert-warning alert-dismissible fade show mt-3');
            $("#numberSend").focus();
            return false;
        }
    });
}

// id가 userId인 속성의 값이 변하면 아이디 중복 확인을 false로 바꾼다. N일 때는 중복 검사 혹은 확인 검사를 먼저 하도록 하는데 쓰인다.
function changeUserId() {
    $('#userId').on('change', function () {
        $('#Chk').attr("value", "N");
        $('#userIdCheck').prop('checked', false);
    });
}

// id가 nickname인 속성의 값이 변하면 닉네임 중복 확인을 false로 바꾼다. N일 때는 중복 검사 혹은 확인 검사를 먼저 하도록 하는데 쓰인다.
function changeNickname() {
    $('#nickname').on('change', function () {
        $('#Chk').attr("value", "N");
        $('#nicknameCheck').prop('checked', false);
    });
}

// id가 email인 속성의 값이 변하면 이메일 중복 확인, 이메일 인증 번호 발급, 이메일 인증 번호 검증 여부를 false로 바꾼다. N일 때는 중복 검사 혹은 확인 검사를 먼저 하도록 하는데 쓰인다.
function changeEmail() {
    $('#email').on('change', function () {
        $('#Chk').attr("value", "N");
        $('#emailCheck').prop('checked', false);
        $('#emailNumberCheck').prop('checked', false);
        $('#emailNumberSend').prop('checked', false);
    });
}

// id가 emailNumber인 속성의 값이 변하면 이메일 인증 번호 확인을 false로 바꾼다.
function changeEmailNumber() {
    $('#emailNumber').on('change', function () {
        $('#emailNumberCheck').prop('checked', false);
    });
}

function isUserIdEmpty() {
    if ($("#userId").val() == "") {
        showMessage('아이디를 입력해 주세요.', 'alert alert-warning alert-dismissible fade show mt-3')
        $("#userId").focus();
        return false;
    }
    return true;
}

function isNicknameEmpty() {
    if ($("#nickname").val() == "") {
        showMessage('닉네임을 입력해 주세요.','alert alert-warning alert-dismissible fade show mt-3');
        $("#nickname").focus();
        return false;
    }
    return true;
}

function isEmailEmpty() {
    if ($("#email").val() == "") {
        showMessage('이메일을 입력해 주세요.', 'alert alert-warning alert-dismissible fade show mt-3');
        $("#email").focus();
        return false;
    }
    return true;
}

function isChkEmpty(message) {
    if (!$('#emailCheck').is(':checked')) {
        showMessage(message, 'alert alert-warning alert-dismissible fade show mt-3');
        return false;
    }
    return true;
}

var emailConfirm = 0;
function isEmailConfirm() {
    if (emailConfirm === 0) {
        showMessage('이메일 인증을 진행해 주세요.', 'alert alert-warning alert-dismissible fade show mt-3');
        $("#numberSend").focus();
        return false;
    }
    return true;
}

function isPasswordEmpty() {
    if ($("#password").val() == "") {
        showMessage('비밀번호를 입력해 주세요.', 'alert alert-warning alert-dismissible fade show mt-3');
        $("#password").focus();
        return false;
    }
    return true;
}

function isPasswordConfirmEmpty() {
    if ($("#passwordConfirm").val() == "") {
        showMessage('비밀번호 확인을 입력해 주세요.', 'alert alert-warning alert-dismissible fade show mt-3');
        $("#passwordConfirm").focus();
        return false;
    }
    return true;
}

var passwordConfirm = 0;
function isPasswordConfirm() {
    if (passwordConfirm === 0) {
        showMessage('확인 비밀번호가 비밀번호와 다릅니다.', 'alert alert-danger alert-dismissible fade show mt-3');
        $("#passwordConfirm").focus();
        return false;
    }
    return true;
}

//beforeNumberCheck();
countDown();