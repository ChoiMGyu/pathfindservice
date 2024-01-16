// 서버측에 사용자가 기입한 아이디, 이메일이 있는지 여부를 물어보는 함수이다.
function idEmailChk() {
    let form = document.createElement("form");
    form.name = "findPasswordForm";
    form.action = "/members/isValidIdEmail";
    form.method = "post";
    form.style.display = 'none';
    let input1 = document.createElement("input");
    input1.value = document.getElementById("userId").value;
    input1.name = "userId"
    form.appendChild(input1);
    let input2 = document.createElement("input");
    input2.value = document.getElementById("email").value;
    input2.name = "email"
    form.appendChild(input2);
    document.body.appendChild(form);
    form.submit();
}

// 비밀번호 초기화 전 양식이 올바른지 확인하는 함수
function chkBeforeSubmit() {
    $("#findPasswordSubmit").on('click', function () {
        return isUserIdEmpty() && isEmailEmpty() && isChkEmpty("아이디, 이메일 확인을 해주세요.") && isEmailConfirm();
    });
}

chkBeforeSubmit();
focusUserId();
focusEmail();