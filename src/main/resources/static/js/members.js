//@Valid message를 보여주는 함수
function displayErrors(errors) {
    // 모든 오류 메시지 초기화
    $(".text-danger").hide();

    // 오류를 필드에 표시
    errors.forEach(function(error) {
        // 필드에 따라 ID를 설정 (예: emailError, userIdError 등)
        let fieldErrorId = error.code+ "Error";
        $("#" + fieldErrorId).text(error.description).show();
    });
}

// 이메일 인증번호 전송 함수
function checkEmail() {
    if(location.pathname.includes('Password')) {
        if (!isEmailEmpty() || !isUserIdEmailCheck()) return false;
    }
    else {
        if (!isEmailEmpty() || !isEmailCheck()) return false;
    }

    const email = $("#email").val();
    const emailCheck = $("#emailCheck").is(":checked");

    console.log("email : " + email);
    console.log("emailCheck : " + emailCheck);

    if (!emailCheck) {
        $("#emailError").text("이메일 중복 확인을 해주세요.").show();
        return;
    }

    $.ajax({
        url: '/api/registration/emailNumberSend',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email: email, emailCheck: emailCheck }),
        success: function(response) {
            if (response.authNumber) {
                console.log('인증번호가 발송되었습니다.');
                console.log('Verification Code:', response.authNumber);
                console.log("response.timeCount : " + response.timeCount);
                $("#emailNumberSend").prop("checked", true); //이메일 인증번호 전송 완료
                $("#timeCount").val(response.timeCount); //인증번호 유효시간 설정
                countDown(); //타이머를 시작
                bodyAlert(response.message);
                resetError();
            }
        },
        error: function(xhr, status, error) {
            //console.error('Error:', error);
            if (xhr.responseJSON) {
                //console.log("xhr.responseJSON");
                // 오류 메시지가 배열 형태로 되어 있는지 확인
                if (Array.isArray(xhr.responseJSON)) {
                    //console.log("isArray");
                    // 여러 오류 메시지가 있는 경우, 첫 번째 메시지만 표시하거나 모든 메시지를 표시
                    if (xhr.responseJSON.length > 0) {
                        //console.log("length");
                        let firstError = xhr.responseJSON[0]; // 첫 번째 오류 메시지
                        console.log(firstError);
                        $("#verificationCodeError").text(firstError.message).show();
                    }
                } else {
                    // 오류 메시지가 배열이 아닌 경우, 단일 객체로 가정하고 처리
                    //console.log("not isArray");
                    let err = xhr.responseJSON;
                    $("#verificationCodeError").text(err.message).show();
                }
            } else {
                //console.log("else");
                // xhr.responseJSON이 없는 경우 기본 오류 메시지 표시
                $("#verificationCodeError").text("서버 오류가 발생했습니다.").show();
            }
        }
    });
}

var seconds; // 남은 시간 변수
var countdown; // 카운트다운을 관리하는 변수

function countDown() {
    if ($("#emailNumberSend").is(":checked") === true) {
        $("#numberSend").text('재전송');

        clearInterval(countdown);
        seconds = $("#timeCount").val();//60 * 30; // 30분(1800초)

        updateCountdown();
        // 1초마다 카운트다운 업데이트
        countdown = setInterval(updateCountdown, 1000);
    }
}

// 시간을 업데이트하고 화면에 표시하는 함수
function updateCountdown() {
    if (seconds >= 0) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        if (!$("#emailNumberCheck").is(":checked")) $('#time').text(`${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`);
        document.getElementById("timeCount").value = seconds--;
    } else {
        clearInterval(countdown);
        $("#emailNumber").attr('class', "form-control fieldError").focus();
        $('#verificationCodeError').text('인증 번호 유효 시간이 초과되어 다시 인증 번호를 발급해 주세요.').show();
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
    if (document.getElementById("verificationCodeError")) $('#verificationCodeError').hide();
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
        $('#verificationCodeError').text('먼저 인증번호 발급을 해주세요.').show();
        return false;
    }
    return true;
}

// function isEmailNumberEmpty() {
//     if ($("#emailNumber").val() === "") {
//         resetError();
//         $("#emailNumber").attr('class', "form-control fieldError").focus();
//         $('#emailNumberError').text('인증번호를 입력해 주세요.').show();
//         return false;
//     }
//     return true;
// }

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
    if(!isEmailNumberSend()){
        //console.log("return됨");
        return false;
    }
    if(!isEmailNumberSend()){
        //console.log("return됨 2");
        return false;
    }
    if(seconds <= 0) {
        console.log("여기 진입");
        updateCountdown();
        return false;
    }
    // 이메일과 인증 번호 값 가져오기
    const email = $("#email").val();
    const authNum = $("#emailNumber").val();
    console.log("인증번호 확인 : " + authNum);

    // AJAX 요청 보내기
    $.ajax({
        url: '/api/registration/emailNumberChk', // 요청할 URL
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email: email, authNum: authNum }), // 요청 데이터
        success: function(response) {
            console.log("success 인증번호 일치 여부 : " + response.emailNumberCheck);
            if (response.emailNumberCheck) {
                bodyAlert(response.message);
                resetError();
                console.log('인증 번호가 일치합니다.');
            } else {
                console.log('인증 번호가 일치하지 않습니다.');
            }
        },
        error: function(xhr, status, error) {
            //console.error('Error:', error);

            // xhr.responseJSON이 있는지 확인
            if (xhr.responseJSON) {
                //console.log("xhr.responseJSON");
                // 오류 메시지가 배열 형태로 되어 있는지 확인
                if (Array.isArray(xhr.responseJSON)) {
                    //console.log("isArray");
                    // 여러 오류 메시지가 있는 경우, 첫 번째 메시지만 표시하거나 모든 메시지를 표시
                    if (xhr.responseJSON.length > 0) {
                        //console.log("length");
                        let firstError = xhr.responseJSON[0]; // 첫 번째 오류 메시지
                        console.log(firstError);
                        $("#verificationCodeError").text(firstError.message).show();
                    }
                } else {
                    // 오류 메시지가 배열이 아닌 경우, 단일 객체로 가정하고 처리
                    //console.log("not isArray");
                    let err = xhr.responseJSON;
                    $("#verificationCodeError").text(err.message).show();
                }
            } else {
                //console.log("else");
                // xhr.responseJSON이 없는 경우 기본 오류 메시지 표시
                $("#verificationCodeError").text("서버 오류가 발생했습니다.").show();
            }
        }
    });
}