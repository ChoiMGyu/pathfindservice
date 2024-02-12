function selectTransportation(transportation) {
    if (transportation === '자동차') {
        $("#carBtn").attr('class', 'form-control btn btn-primary');
        $("#onFootBtn").attr('class', 'form-control btn btn-outline-primary');
        $("#transportation").val("자동차");
    } else if (transportation === '도보') {
        $("#onFootBtn").attr('class', 'form-control btn btn-primary');
        $("#carBtn").attr('class', 'form-control btn btn-outline-primary');
        $("#transportation").val("도보");
    }
}

function resetFindPathForm() {
    $("#carBtn").attr('class', 'form-control btn btn-outline-primary');
    $("#onFootBtn").attr('class', 'form-control btn btn-outline-primary');
    $("#transportation").val("");
    $("#startPoint").val("");
    $("#endPoint").val("");
}

// 출발지와 도착지 사이의 폴리라인(카카오 api의 객체, 경로를 의미함)들을 저장한다. 새로운 경로 탐색 시 기존 경로를 삭제하기 위해 사용한다.
let drawRoute = [];
let markers = [];

function findPath() {
    $("#findPathSection").attr('class', 'mb-5');
    $("#pathInfo").hide();
    for (let i = 0; i < $("#pathInfo").children().length; i++) {
        $("#pathInfo").children().eq(i).hide();
    }
    resetError();
    if (!isTransportationsEmpty() || !isStartEmpty() || !isEndEmpty()) return;
    let graphRequestForm = $("#graphRequestForm").serialize();
    //console.log(graphRequestForm);
    $.ajax({
        type: "get",
        url: "/path?" + graphRequestForm,
        success: function (response) {
            //console.log('distance: ' + response.distance);
            for (let i = 0; i < drawRoute.length; i++) drawRoute[i].setMap(null);
            drawRoute = [];
            for (let i = 0; i < markers.length; i++) {
                markers[i].setMap(null);
                //markers[i].infowindow.close();
            }
            markers = [];

            let route = response.path;
            for (let i = 1; i < route.length; i++) {
                let linePath = [
                    new kakao.maps.LatLng(route[i - 1].latitude, route[i - 1].longitude),
                    new kakao.maps.LatLng(route[i].latitude, route[i].longitude)
                ]
                let polyline = new kakao.maps.Polyline({
                    path: linePath, // 선을 구성하는 좌표배열 입니다
                    strokeWeight: 5, // 선의 두께 입니다
                    strokeColor: '#db4040', // 선의 색깔입니다
                    strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
                    strokeStyle: 'solid', // 선의 스타일입니다
                    clickable: false
                });
                polyline.setMap(map);
                drawRoute.push(polyline);
            }
            let startPoint = new kakao.maps.Marker({position: new kakao.maps.LatLng(route[0].latitude, route[0].longitude)});
            startPoint.setMap(map);
            markers.push(startPoint);

            /*let startPointInfo = document.createElement('div');
            startPointInfo.textContent = "출발";
            startPointInfo.style.width = "100%";
            startPointInfo.style.padding = "5px";
            startPoint.infowindow = new kakao.maps.InfoWindow({content: startPointInfo});
            startPoint.infowindow.open(map, startPoint);*/

            let endPoint = new kakao.maps.Marker({position: new kakao.maps.LatLng(route[route.length - 1].latitude, route[route.length - 1].longitude)})
            endPoint.setMap(map);
            markers.push(endPoint);

            /*let endPointInfo = document.createElement('div');
            endPointInfo.textContent = "도착";
            endPointInfo.style.width = "100%";
            endPointInfo.style.padding = "5px";
            endPoint.infowindow = new kakao.maps.InfoWindow({content: endPointInfo});
            endPoint.infowindow.open(map, endPoint);*/
            /*====================================================================================================*/
            /*시간 계산*/
            let timeText = "";
            let speed = response.speed * 1000 / 60;
            let minutes = Math.round(response.distance / speed);
            let hours = Math.floor(minutes / 60);
            minutes %= 60;
            if (hours > 0) timeText += hours.toString() + "시간 ";
            timeText += minutes.toString() + "분";
            //console.log(speed);
            $("#time").text(timeText);
            /*====================================================================================================*/

            /*====================================================================================================*/
            /*거리 계산*/
            let distanceText = "\u00a0";
            let distance = response.distance / 1000;
            //console.log(distance);
            if (distance >= 1) distanceText += (Math.round(distance * 10) / 10).toString() + "km";
            else distanceText += Math.round(distance * 1000).toString() + "m";
            $("#distance").text(distanceText);
            /*====================================================================================================*/

            $("#findPathSection").attr('class', 'mb-3');
            $("#pathInfo").show();
            for (let i = 0; i < $("#pathInfo").children().length; i++) {
                $("#pathInfo").children().eq(i).show();
            }
            //console.log($("#pathInfo").children().length);
            //console.log($("#pathInfo").children());
        },
        error: function (error) {
            //alert("에러 발생!");
            //console.log(error);
            //console.log(error.responseJSON);

            let isEnd = false;
            error.responseJSON.find(function (err) {
                if (err.field !== "transportation") return;
                isEnd = true;
                $("#findPathError").text(err.message).show();
                $("#transportation").attr('class', "form-control fieldError").focus();
            });
            if (isEnd) return;
            error.responseJSON.find(function (err) {
                if (err.field !== "start") return;
                isEnd = true;
                $("#findPathError").text(err.message).show();
                $("#startPoint").attr('class', "form-control fieldError").focus();
            });
            if (isEnd) return;
            error.responseJSON.find(function (err) {
                if (err.field !== "end") return;
                isEnd = true;
                $("#findPathError").text(err.message).show();
                $("#endPoint").attr('class', "form-control fieldError").focus();
            });
        }
    })
}

function resetError() {
    $("#startPoint").attr('class', "form-control");
    $("#endPoint").attr('class', "form-control");
    $("#transportation").attr('class', "form-control");
    $("#findPathError").hide();
}

function isStartEmpty() {
    if ($("#startPoint").val() === "") {
        resetError();
        $("#startPoint").attr('class', "form-control fieldError").focus();
        $("#findPathError").text('출발지를 입력해 주세요.').show();
        return false;
    }
    return true;
}

function isEndEmpty() {
    if ($("#endPoint").val() === "") {
        resetError();
        $("#endPoint").attr('class', "form-control fieldError").focus();
        $("#findPathError").text('도착지를 입력해 주세요.').show();
        return false;
    }
    return true;
}

function isTransportationsEmpty() {
    if ($("#transportation").val() === "") {
        resetError();
        $("#transportation").attr('class', "form-control fieldError").focus();
        $("#findPathError").text('이동 수단을 선택해 주세요.').show();
        return false;
    }
    return true;
}