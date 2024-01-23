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

// 아이디 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function userIdChk() {
    if(!isUserIdEmpty()) return false;
    let form = document.getElementById("submitForm");
    form.action = "/members/userIdChk";
    form.submit();
}

// 닉네임 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function nicknameChk() {
    if(!isNicknameEmpty()) return false;
    let form = document.getElementById("submitForm");
    form.action = "/members/nicknameChk";
    form.submit();
}

// 이메일 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function emailChk() {
    if(!isEmailEmpty()) return false;
    let form = document.getElementById("submitForm");
    form.action = "/members/emailChk";
    form.submit();
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
