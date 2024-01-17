// 이메일 인증번호 전송 함수
function checkEmail(message) {
    if(!isUserIdEmpty() || !isEmailEmpty() || !isNicknameEmpty() || !isChkEmpty(message)) return false;
    $.ajax({
        type: "post",
        url: "/mailSend",
        dataType: "json",
        async: false,
        contentType: "application/json; charset-utf-8",
        data: JSON.stringify({"email": $("#email").val()}),
        success: function (data) {
            alert("해당 이메일로 인증번호 발송이 완료되었습니다. \n 확인부탁드립니다.")
            //console.log("data : " + data);
            emailConfirm = 0;
            $("#emailconfirmTxt").html("<span id='emconfirmchk'>인증번호</span>")
            $("#emailconfirm").val(null).attr('class', 'form-control');
            $("#numberSend").attr('value', 'Y');
            chkEmailConfirm(data);
            $("#numberSend").text('재전송');

            clearInterval(countdown);
            seconds = 60 * 30; // 30분(1800초)

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
        },
        error: function(e) {
            alert("인증번호 전송에 실패했습니다. 다시 시도해 주시기 바랍니다.");
        }
    })
}

// 이메일 인증 번호와 사용자가 기입한 번호가 일치하는지 확인하는데 쓰이는 함수이다.
function chkEmailConfirm(data) {
    $("#numberCheck").on("click", function () {
        if (data != $("#emailconfirm").val()) {
            $("#emailconfirmTxt").html("<span id='emconfirmchk'>인증 실패</span>")
            $("#emailconfirm").attr('class', 'form-control fieldError');
        } else {
            $("#emailconfirmTxt").html("<span id='emconfirmchk'>인증 완료</span>")
            $("#emailconfirm").attr('class', 'form-control');
            $('#time').text("");
            emailConfirm = 1;
            document.getElementById("emailForm").submit();
        }
    })
}

var seconds; // 남은 시간 변수
var countdown; // 카운트다운을 관리하는 변수

// 시간을 업데이트하고 화면에 표시하는 함수
function updateCountdown() {
    if (emailConfirm === 1) {
        return;
    }
    if (seconds >= 0) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        $('#time').text(`${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`);
        seconds--;
    } else {
        clearInterval(countdown);
        alert('인증번호 유효시간이 만료되었습니다.');
    }
};

// 인증번호 확인 전에 인증번호를 발급 받았는지 여부를 묻는 함수이다.
function beforeNumberCheck() {
    $("#numberCheck").on('click', function () {
        if ($("#numberSend").val() == "N") {
            alert("먼저 인증번호 발급을 해주세요.");
            $("#numberSend").focus();
            return false;
        }
    });
}

// id가 userId인 속성이 포커스 되면 id가 Chk인 곳의 value를 N으로 바꾼다. N일 때는 중복 검사 혹은 확인 검사를 먼저 하도록 하는데 쓰인다.
function focusUserId() {
    $('#userId').on('focus', function () {
        $('#Chk').attr("value", "N");
    });
}

// id가 nickname인 속성이 포커스 되면 id가 Chk인 곳의 value를 N으로 바꾼다. N일 때는 중복 검사 혹은 확인 검사를 먼저 하도록 하는데 쓰인다.
function focusNickname() {
    $('#nickname').on('focus', function () {
        $('#Chk').attr("value", "N");
    });
}

// id가 email인 속성이 포커스 되면 id가 Chk인 곳의 value를 N으로 바꾼다. N일 때는 중복 검사 혹은 확인 검사를 먼저 하도록 하는데 쓰인다.
function focusEmail() {
    $('#email').on('focus', function () {
        $('#Chk').attr("value", "N");
    });
}

function isUserIdEmpty() {
    if ($("#userId").val() == "") {
        alert("아이디를 입력해 주세요.");
        $("#userId").focus();
        return false;
    }
    return true;
}

function isNicknameEmpty() {
    if ($("#nickname").val() == "") {
        alert("닉네임을 입력해 주세요.");
        $("#nickname").focus();
        return false;
    }
    return true;
}

function isEmailEmpty() {
    if ($("#email").val() == "") {
        alert("이메일을 입력해 주세요.");
        $("#email").focus();
        return false;
    }
    return true;
}

function isChkEmpty(message) {
    if ($('#Chk').val() == "N") {
        alert(message);
        return false;
    }
    return true;
}

var emailConfirm = 0;
function isEmailConfirm() {
    if (emailConfirm === 0) {
        alert("이메일 인증을 진행해 주세요.");
        $("#numberSend").focus();
        return false;
    }
    return true;
}

function isPasswordEmpty() {
    if ($("#password").val() == "") {
        alert("비밀번호를 입력해 주세요.");
        $("#password").focus();
        return false;
    }
    return true;
}

function isPasswordConfirmEmpty() {
    if ($("#passwordConfirm").val() == "") {
        alert("비밀번호 확인을 입력해 주세요.");
        $("#passwordConfirm").focus();
        return false;
    }
    return true;
}

var passwordConfirm = 0;
function isPasswordConfirm() {
    if (passwordConfirm === 0) {
        alert("확인 비밀번호가 비밀번호와 다릅니다.");
        $("#passwordConfirm").focus();
        return false;
    }
    return true;
}

beforeNumberCheck();