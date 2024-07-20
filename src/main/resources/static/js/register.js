/*// 아이디, 닉네임, 이메일 중복 여부 및 유효성 확인 함수
function validationChk() {
    let form = document.createElement("form");
    form.name = "memberForm";
    form.action = "/members/validationChk";
    form.method = "post";
    form.style.display = 'none';
    let input1 = document.createElement("input");
    input1.value = document.getElementById("userId").value;
    input1.name = "userId"
    form.appendChild(input1);
    let input2 = document.createElement("input");
    input2.value = document.getElementById("nickname").value;
    input2.name = "nickname"
    form.appendChild(input2);
    let input3 = document.createElement("input");
    input3.value = document.getElementById("email").value;
    input3.name = "email"
    form.appendChild(input3);
    let input4 = document.createElement("input");
    input4.value = "dummyPassword!234";
    input4.name = "password"
    form.appendChild(input4);
    document.body.appendChild(form);
    form.submit();
}*/

// 아이디 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 ajax를 사용해 userId를 전송하는 함수
function userIdChk() {
    resetError();
    if (!isUserIdEmpty()) return false;
    let userId = $("#userId").serialize();
    console.log(userId);
    $.ajax({
        type: "GET",
        url: "/api/registration/check/user-id?" + userId,
        success: function (response) {
            console.log(response);
            $("#userIdCheck").prop('checked',true);
            bodyAlert(response.message);
        },
        error: function (error) {
            // 에러 처리
            //console.error("에러 발생:", error);
            error.responseJSON.find(function (err) {
                console.log(err);
                $("#userIdError").text(err.message).show();
                $("#userId").attr('class', "form-control fieldError").focus();
            });
        }
    });
}

// 닉네임 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function nicknameChk() {
    resetError();
    if (!isNicknameEmpty()) return false;
    let nickname = $("#nickname").serialize();
    console.log(nickname);
    $.ajax({
        type: "GET",
        url: "/api/registration/check/nickname?" + nickname,
        success: function (response) {
            console.log(response);
            $("#nicknameCheck").prop('checked',true);
            bodyAlert(response.message);
        },
        error: function (error) {
            // 에러 처리
            //console.error("에러 발생:", error);
            error.responseJSON.find(function (err) {
                console.log(err);
                $("#nicknameError").text(err.message).show();
                $("#nickname").attr('class', "form-control fieldError").focus();
            });
        }
    });
}

// 이메일 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function emailChk() {
    resetError();
    if (!isEmailEmpty()) return false;
    let email = $("#email").serialize();
    console.log(email);
    $.ajax({
        type: "GET",
        url: "/api/registration/check/email?" + email,
        success: function (response) {
            console.log(response);
            $("#emailCheck").prop('checked',true);
            bodyAlert(response.message);
        },
        error: function (error) {
            // 에러 처리
            //console.error("에러 발생:", error);
            error.responseJSON.find(function (err) {
                console.log(err);
                $("#emailError").text(err.message).show();
                $("#email").attr('class', "form-control fieldError").focus();
            });
        }
    });
}

// 회원 가입 양식 제출 전 양식이 올바른지 확인하는 함수
function checkBeforeSubmit() {
    $("#registerSubmit").on('click', function () {
        return (isUserIdEmpty() && isUserIdCheck() && isNicknameEmpty() && isNicknameCheck()
            && isEmailEmpty() && isEmailCheck() && isEmailNumberSend() && isEmailNumberEmpty()
            && isEmailNumberCheck() && isPasswordEmpty() && isPasswordConfirmEmpty() && isPasswordSame());
    });
}

changeUserId();
changeNickname();
changeEmail();
changeEmailNumber();
comparePassword();
checkBeforeSubmit();
