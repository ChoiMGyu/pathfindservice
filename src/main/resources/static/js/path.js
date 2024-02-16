const MAP_API = config.apikey;
var container = document.getElementById('map');
var options = {
    center: new kakao.maps.LatLng(35.8330177, 128.7532086),
    level: 4 //지도 레벨 설정 (현재 100m)
};

var map = new kakao.maps.Map(container, options);

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

// JavaScript 코드
// findPlace 함수 정의
function findPlace() {
    // 입력된 값을 가져와서 query 변수에 저장
    console.log("findPlace() 함수 호출됨");
    $("#searchPlaceSection").attr('class', 'mt-3 mb-5');
    resetPlaceError();
    console.log("전");
    if (!isPlaceEmpty()) return;
    console.log("후");
    var searchContent = $("#searchRequestForm").val();
    console.log("검색 내용:", searchContent);
    // Ajax 요청 보내기
    $.ajax({
        type: "GET",
        url: "/searchPlace",
        data: {
            searchContent: searchContent
        },
        success: function(response) {
            // 서버로부터의 응답을 처리
            console.log("검색 결과:", response);

            if(!response.name) {
                console.log("입력하신 내용의 장소가 존재하지 않습니다.");
                return;
            }

            var latitude = response.latitude;
            var longitude = response.longitude;
            console.log("검색된 장소의 위도:", latitude);
            console.log("검색된 장소의 경도:", longitude);

            // 마커가 표시될 위치입니다
            var markerPosition  = new kakao.maps.LatLng(latitude, longitude);

            // 마커를 생성합니다
            var marker = new kakao.maps.Marker({
                position: markerPosition
            });

            // 마커가 지도 위에 표시되도록 설정합니다
            marker.setMap(map);

            setBounds(response);
        },
        error: function(xhr, status, error) {
            // 에러 처리
            console.error("에러 발생:", error);
            //var searchContent = document.getElementById("searchRequestForm").value;

            let isEnd = false;
            error.responseJSON.find(function (err) {
                if (err.field !== "searchContent") return;
                isEnd = true;
                $("#searchPlaceError").text(err.message).show();
                $("#searchRequestForm").attr('class', "form-control fieldError").focus();
            });
        }
    });
}

function findPath() {
    console.log("findPath() 호출됨")
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

function setBounds(response) {
    // LatLngBounds 객체에 추가된 좌표들을 기준으로 지도의 범위를 재설정합니다
    // 이때 지도의 중심좌표와 레벨이 변경될 수 있습니다
    console.log("지도 범위 재설정 하기 - 모든 마커가 보일 수 있게");
    console.log("setBounds()로 전달된 response latitude : " + response.latitude);
    console.log("setBounds()로 전달된 response longitude : " + response.longitude);
    // 버튼을 클릭하면 아래 배열의 좌표들이 모두 보이게 지도 범위를 재설정합니다
    var points = [
        new kakao.maps.LatLng(response.latitude, response.longitude)
    ];

    // 지도를 재설정할 범위정보를 가지고 있을 LatLngBounds 객체를 생성합니다
    var bounds = new kakao.maps.LatLngBounds();

    var i, marker;
    for (i = 0; i < points.length; i++) {
        // 배열의 좌표들이 잘 보이게 마커를 지도에 추가합니다
        marker = new kakao.maps.Marker({ position : points[i] });
        marker.setMap(map);

        // LatLngBounds 객체에 좌표를 추가합니다
        bounds.extend(points[i]);
    }
    map.setBounds(bounds);
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

function resetPlaceError() {
    $("#searchRequestForm").attr('class', "form-control");
    $("#searchPlaceError").hide();
}

function isPlaceEmpty() {
    //console.log("검색내용이 비었는지 확인 :" , $("#searchRequestForm").val());
    if($("#searchRequestForm").val() === "") {
        //console.log("검색내용이 비어서 조건문에 만족함");
        resetPlaceError();
        $("#searchRequestForm").attr('class', "form-control fieldError").focus();
        $("#searchPlaceError").text('검색 내용을 입력해주세요').show();
        return false;
    }
    return true;
}