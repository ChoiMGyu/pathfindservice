var passwordConfirm = 0;
$("#passwordConfirm").on("focusout", function () {
    //console.log("포커스 아웃");
    //console.log($("#passwordConfirm").val());
    if ($("#password").val() !== $("#passwordConfirm").val()) {
        $("#passwordConfirmTxt").html("<span id='emconfirmchk'>비밀번호 다름</span>")
        $("#passwordConfirm").attr('class', 'form-control passwordConfirmError');
    } else {
        $("#passwordConfirmTxt").html("<span id='emconfirmchk'>비밀번호 확인</span>")
        $("#passwordConfirm").attr('class', 'form-control');
        passwordConfirm = 1;
    }
})

// 이메일 인증번호 전송 함수
function checkEmail() {
    if ($('#Chk').val() == "N") {
        alert("아이디, 닉네임, 이메일 중복 확인을 해주세요.");
        return false;
    }
    //console.log("실행");
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
            chkEmailConfirm(data);
            $("numberSend").text('재전송');

            clearInterval(countdown);
            seconds = 60 * 30; // 30분(1800초)

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
        }
    })
}

var seconds; // 남은 시간 변수
var countdown; // 카운트다운을 관리하는 변수

// 시간을 업데이트하고 화면에 표시하는 함수
const updateCountdown = function () {
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

var emailConfirm = 0;

// 이메일 인증번호 체크 함수
function chkEmailConfirm(data) {
    $("#numberCheck").on("click", function () {
        //console.log("키보드 동작");
        //console.log($("#emailconfirm").val());
        //console.log(data);
        if (data != $("#emailconfirm").val()) {
            $("#emailconfirmTxt").html("<span id='emconfirmchk'>인증 실패</span>")
            $("#emailconfirm").attr('class', 'form-control fieldError');
        } else {
            $("#emailconfirmTxt").html("<span id='emconfirmchk'>인증 완료</span>")
            $("#emailconfirm").attr('class', 'form-control');
            emailConfirm = 1;
        }
    })
}

$('#userId').on('focus', function () {
    $('#Chk').attr("value", "N");
    //console.log()
});

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
$("#submit").on('click', function () {
    if ($("#userId").val() == "") {
        alert("아이디를 입력해주세요.");
        $("#userId").focus();
        return false;
    }
    if ($("#nickname").val() == "") {
        alert("닉네임을 입력해주세요.");
        $("#nickname").focus();
        return false;
    }
    if ($("#email").val() == "") {
        alert("이메일을 입력해주세요.");
        $("#email").focus();
        return false;
    }
    if ($('#Chk').val() == "N") {
        alert("아이디, 닉네임, 이메일 중복 확인을 해주세요.");
        return false;
    }
    if (emailConfirm === 0) {
        alert("이메일 인증을 진행해주세요.");
        $("#numberSend").focus();
        return false;
    }
    if ($("#password").val() == "") {
        alert("비밀번호를 입력해주세요.");
        $("#password").focus();
        return false;
    }
    if ($("#passwordConfirm").val() == "") {
        alert("비밀번호 확인을 입력해주세요.");
        $("#passwordConfirm").focus();
        return false;
    }
    if (passwordConfirm === 0) {
        alert("확인 비밀번호가 비밀번호와 다릅니다.");
        $("#passwordConfirm").focus();
        return false;
    }
});