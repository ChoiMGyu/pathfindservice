// 비밀번호와 비밀번호 확인이 같은지 다른지에 따라 화면을 바꾸는 함수이다.
function comparePassword() {
    $("#passwordConfirm").on("focusout", function () {
        if ($("#password").val() !== $("#passwordConfirm").val()) {
            $("#passwordConfirmTxt").html("<span id='emconfirmchk'>비밀번호 다름</span>")
            $("#passwordConfirm").attr('class', 'form-control passwordConfirmError');
        } else {
            $("#passwordConfirmTxt").html("<span id='emconfirmchk'>비밀번호 확인</span>")
            $("#passwordConfirm").attr('class', 'form-control');
            passwordConfirm = 1;
        }
    })
}

// 아이디, 닉네임, 이메일 중복 여부 및 유효성 확인 함수
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
}

// 회원 가입 양식 제출 전 양식이 올바른지 확인하는 함수
function chkBeforeSubmit() {
    $("#registerSubmit").on('click', function () {
        return (isUserIdEmpty() && isNicknameEmpty() && isEmailEmpty() && isChkEmpty("아이디, 닉네임, 이메일 중복 확인을 해주세요.") &&
            isEmailConfirm() && isPasswordEmpty() && isPasswordConfirmEmpty() && isPasswordConfirm());
    });
}

comparePassword();
chkBeforeSubmit();
focusUserId();
focusNickname();
focusEmail()