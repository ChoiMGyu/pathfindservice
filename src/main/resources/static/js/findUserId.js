// 이메일 중복 여부 및 유효성 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function emailExist() {
    $('#email').change(function () {
        //resetError();
        //if (!isEmailEmpty()) return false;
        const email = $("#email").serialize();
        //console.log(email);
        $.ajax({
            type: "GET",
            url: "/api/recovery/exist/email?" + email,
            success: function (response) {
                //console.log(response);
                $("#emailError").hide();
                $("#email").attr('class', "form-control").focus();
                $("#numberSend").attr("disabled", false);
                bodyAlert(response.message);
            },
            error: function (error) {
                // 에러 처리
                //console.error("에러 발생:", error);
                //console.log(error.responseJSON);
                error.responseJSON.find(function (err) {
                    //console.log(err);
                    $("#emailError").text(err.message).show();
                    $("#email").attr('class', "form-control fieldError").focus();
                    disableEmailNumberSection();
                });
            }
        });
    });
}

// 이메일 인증 번호 확인을 서버에서 진행할 수 있도록 form을 전송하는 함수
function EmailAndEmailNumberChk() {
    // if (!isEmailNumberSend()) {
    //     //console.log("return됨");
    //     return false;
    // }
    resetError();
    //if (!isEmailEmpty()) return false;
    const authNum = $("#emailNumber").serialize();
    //console.log(authNum);
    if (seconds <= 0) {
        //console.log("여기 진입");
        updateCountdown();
        return false;
    }
    // 이메일과 인증 번호 값 가져오기
    const email = $("#email").serialize();
    const emailNumber = $("#emailNumber").serialize();
    const query = email + "&" + emailNumber;
    //console.log("form : " + query);

    // AJAX 요청 보내기
    $.ajax({
        url: '/api/recovery/userid?' + query, // 요청할 URL
        type: 'GET',
        contentType: 'application/json',
        success: function (response, success, xhr) {
            //console.log(response);
            if (response.recoverySuccess === true) {
                window.location.href = "/members/returnId?userId=" + response.userId;
            } else {
                $("#verificationCodeError").text(response.message).show();
            }
        },
        error: function (xhr, status, error) {
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
                        //console.log(firstError);
                        $("#verificationCodeError").text(firstError.message).show();
                        $("#emailNumber").attr('class', "form-control fieldError").focus();
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

emailExist();