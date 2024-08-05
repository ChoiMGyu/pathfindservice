function isUserIdEmailCheck() {
    if ($("#userIdCheck").is(":checked") === false || $("#emailCheck").is(":checked") === false) {
        resetError();
        $('#globalError').text('아이디, 이메일 확인을 해주세요.').show();
        return false;
    }
    return true;
}
