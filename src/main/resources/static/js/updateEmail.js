// 서버측에 사용자가 기입한 이메일이 존재하는지 여부를 물어보는 함수이다.
function emailChk() {
    let form = document.createElement("form");
    form.name = "emailRequestDto";
    form.action = "/members/isValidEmail2";
    form.method = "post";
    form.style.display = 'none';
    let input = document.createElement("input");
    input.value = document.getElementById("email").value;
    input.name = "email"
    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}

focusEmail()