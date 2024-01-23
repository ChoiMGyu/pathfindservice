// 이메일 인증번호 전송 함수
function checkEmail() {
    if(location.pathname.includes('Password')) {
        //console.log(location.pathname);
        if (!isEmailEmpty() || !isUserIdEmailCheck()) return false;
    }
    else {
        if (!isEmailEmpty() || !isEmailCheck()) return false;
    }
    let form = document.getElementById("submitForm");
    form.action = "/members/emailNumberSend";
    form.submit();
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
        if (!$("#emailNumberCheck").is(":checked")) $('#time').text(`${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`);
        document.getElementById("timeCount").value = seconds--;
    } else {
        clearInterval(countdown);
        $("#emailNumber").attr('class', "form-control fieldError").focus();
        if (!document.getElementById("thEmailNumberError")) $('#emailNumberError').text('인증번호 유효시간이 만료되었습니다.').show();
    }
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

// 프론트에서 에러 메시지를 출력하기 전에 기존에 있던 에러를 페이지에서 숨기는 함수이다.
function resetError() {
    $("#userId").attr('class', "form-control");
    if (document.getElementById("thUserIdError")) $('#thUserIdError').hide();
    $("#nickname").attr('class', "form-control");
    if (document.getElementById("thNicknameError")) $('#thNicknameError').hide();
    $("#email").attr('class', "form-control");
    if (document.getElementById("thEmailError")) $('#thEmailError').hide();
    if (document.getElementById("globalError")) $('#globalError').hide();
    if (document.getElementById("thGlobalError")) $('#thGlobalError').hide();
    $("#emailNumber").attr('class', "form-control");
    if (document.getElementById("thEmailNumberError")) $('#thEmailNumberError').hide();
    $("#passwordConfirm").attr('class', "form-control");
    if (document.getElementById("thPasswordConfirmError")) $('#thPasswordConfirmError').hide();
    if (document.getElementById("userIdError")) $('#userIdError').hide();
    if (document.getElementById("nicknameError")) $('#nicknameError').hide();
    if (document.getElementById("emailError")) $('#emailError').hide();
    if (document.getElementById("emailNumberError")) $('#emailNumberError').hide();
    if (document.getElementById("passwordError")) $('#passwordError').hide();
    if (document.getElementById("thPasswordError")) $('#thPasswordError').hide();
    if (document.getElementById("passwordConfirmError")) $('#passwordConfirmError').hide();
}
function isUserIdEmpty() {
    if ($("#userId").val() === "") {
        resetError();
        $("#userId").attr('class', "form-control fieldError").focus();
        $('#userIdError').text('아이디는 필수입니다.').show();
        return false;
    }
    return true;
}

function isNicknameEmpty() {
    if ($("#nickname").val() === "") {
        resetError();
        $("#nickname").attr('class', "form-control fieldError").focus();
        $('#nicknameError').text('닉네임은 필수입니다.').show();
        return false;
    }
    return true;
}

function isEmailEmpty() {
    if ($("#email").val() === "") {
        resetError();
        $("#email").attr('class', "form-control fieldError").focus();
        $('#emailError').text('이메일은 필수입니다.').show();
        return false;
    }
    return true;
}

function isUserIdCheck() {
    if ($("#userIdCheck").is(":checked") === false) {
        resetError();
        $('#userIdError').text('아이디 중복 확인을 해주세요.').show();
        return false;
    }
    return true;
}

function isNicknameCheck() {
    if ($("#nicknameCheck").is(":checked") === false) {
        resetError();
        $('#nicknameError').text('닉네임 중복 확인을 해주세요.').show();
        return false;
    }
    return true;
}

function isEmailCheck() {
    if ($("#emailCheck").is(":checked") === false) {
        resetError();
        $('#emailError').text('이메일 중복 확인을 해주세요.').show();
        return false;
    }
    return true;
}

function isEmailNumberSend() {
    if ($("#emailNumberSend").is(":checked") === false) {
        resetError();
        $('#emailNumberError').text('먼저 인증번호 발급을 해주세요.').show();
        return false;
    }
    return true;
}

function isEmailNumberEmpty() {
    if ($("#emailNumber").val() === "") {
        resetError();
        $("#emailNumber").attr('class', "form-control fieldError").focus();
        $('#emailNumberError').text('인증번호를 입력해 주세요.').show();
        return false;
    }
    return true;
}

function isEmailNumberCheck() {
    if ($("#emailNumberCheck").is(":checked") === false) {
        resetError();
        $('#emailNumberError').text('인증번호 확인을 해주세요.').show();
        return false;
    }
    return true;
}

function isPasswordEmpty() {
    if ($("#password").val() === "") {
        resetError();
        $("#password").attr('class', "form-control fieldError").focus();
        $('#passwordError').text('비밀번호는 필수입니다.').show();
        return false;
    }
    return true;
}

function isPasswordConfirmEmpty() {
    if ($("#passwordConfirm").val() === "") {
        resetError();
        $("#passwordConfirm").attr('class', "form-control fieldError").focus();
        $('#passwordConfirmError').text('비밀번호 확인은 필수입니다.').show();
        return false;
    }
    return true;
}

var passwordConfirm = 0;

// 비밀번호와 비밀번호 확인이 같은지 다른지에 따라 화면을 바꾸는 함수이다.
function comparePassword() {
    $("#passwordConfirm").on("focusout", function () {
        if ($("#password").val() !== $("#passwordConfirm").val()) {
            //$("#passwordConfirmTxt").html("<span id='emconfirmchk'>비밀번호 다름</span>")
            $("#passwordConfirm").attr('class', 'form-control passwordConfirmError');
        } else {
            //$("#passwordConfirmTxt").html("<span id='emconfirmchk'>비밀번호 확인</span>")
            $("#passwordConfirm").attr('class', 'form-control');
            passwordConfirm = 1;
        }
    })
}

function isPasswordSame() {
    if (passwordConfirm === 0) {
        resetError();
        $("#passwordConfirm").attr('class', "form-control fieldError").focus();
        $('#passwordConfirmError').text('비밀번호와 비밀번호 확인이 서로 일치하지 않습니다.').show();
        return false;
    }
    return true;
}

// 이메일 인증 번호 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function emailNumberChk() {
    if($("#emailNumberCheck").is(":checked") === true || !isEmailNumberSend() || !isEmailNumberEmpty()) return false;
    let form = document.getElementById("submitForm");
    document.getElementById("timeCount").value = seconds;
    form.action = "/members/emailNumberChk";
    form.submit();
}

countDown();