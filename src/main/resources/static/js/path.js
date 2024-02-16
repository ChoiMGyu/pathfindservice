const SEARCH = "search";
const FIND_PATH = "findPath";

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
        success: function (response) {
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
            // 원하는 작업 수행


            /*====================================================================================================*/
            /*최근 검색 추가*/
            addRecentSearch(SEARCH);
            /*====================================================================================================*/
        },
        error: function (xhr, status, error) {
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

// 최근 검색 객체를 생성하는 함수이다.
function makeRecentSearch(search, transportation, start, end) {
    return {
        search,
        transportation,
        start,
        end
    };
}

// 최근 검색 리스트에 사용자가 검색한 내용을 추가하는 함수이다.  최근 검색 표시 개수는 최대 10개로 제한해 놓았다.(네이버 길찾기를 참고함.)
function addRecentSearch(type) {
    let recentSearch;
    if (type === SEARCH) recentSearch = makeRecentSearch($("#searchRequestForm").val(), null, null, null);
    else if (type === FIND_PATH) recentSearch = makeRecentSearch(null, $("#transportation").val(), $("#startPoint").val(), $("#endPoint").val());
    //console.log(recentSearch);
    let search = [];
    let previousSearch = JSON.parse(localStorage.getItem("recentSearch"));
    //console.log(JSON.parse(localStorage.getItem("recentSearch")));
    if (previousSearch != null) {
        for (let i = previousSearch.length === 10 ? 1 : 0; i < previousSearch.length; i++) {
            search.push(previousSearch[i]);
        }
        for (let i = 0; i < previousSearch.length; i++) {
            if (JSON.stringify(search[i]) === JSON.stringify(recentSearch)) {
                search.splice(i, 1);
            }
        }
    }
    //console.log(search);
    //console.log(recentSearch);
    search.push(recentSearch);
    //console.log(search);
    localStorage.setItem("recentSearch", JSON.stringify(search));

    showRecentSearchList();
}

// 최근 검색 리스트를 사용자에게 보여주는 함수이다.
function showRecentSearchList() {
    let searchList = JSON.parse(localStorage.getItem("recentSearch"));
    //console.log(JSON.parse(localStorage.getItem("recentSearch")));
    //console.log(searchList.length);
    $("#recentSearch").empty();
    if (searchList == null || searchList.length === 0) {
        $("#recentSearch").hide();
    } else {
        $("#recentSearch").show();
        for (let i = searchList.length - 1; i >= 0; i--) {
            if (searchList[i].search !== null) {
                let text = searchList[i].search.toString();
                $("#recentSearch").append(
                    "<li class='alert alert-dark alert-dismissible' style='list-style: none; padding-bottom: 0; padding-top: 0; margin-bottom: 0.25rem' id='" + (i + "thSearch").toString() + "'>" +
                    "   <div role = 'button' onclick='deleteRecentSearch(" + i + "); searchAgain(\"" + searchList[i].search + "\");'>"
                    + text +
                    "   </div>" +
                    "   <button style='padding: 0.25rem;' type='button' class='btn-close' data-bs-dismiss='alert' aria-label='Close' onclick='deleteRecentSearch(" + i + ")'></button>" +
                    "</li>");
            } else {
                let text = (searchList[i].transportation + (searchList[i].transportation === "자동차" ? "" : "\u00a0\u00a0\u00a0") + " | " + searchList[i].start + " → " + searchList[i].end).toString();
                //console.log(searchList[i]);
                $("#recentSearch").append(
                    "<li class='alert alert-dark alert-dismissible' style='list-style: none; padding-bottom: 0; padding-top: 0; margin-bottom: 0.25rem' id='" + (i + "thSearch").toString() + "'>" +
                    "   <div role = 'button' onclick='deleteRecentSearch(" + i + "); searchAgain(" + null + ", \"" + searchList[i].transportation + "\", " + searchList[i].start + ", " + searchList[i].end + ");'>"
                    + text +
                    "   </div>" +
                    "   <button style='padding: 0.25rem;' type='button' class='btn-close' data-bs-dismiss='alert' aria-label='Close' onclick='deleteRecentSearch(" + i + ")'></button>" +
                    "</li>");
            }
        }
    }
}

// 최근 검색 버튼을 누를 시 다시 해당 검색 결과를 보여주는 함수이다.
function searchAgain(search, transportation, start, end) {
    if (search !== null) {
        $("#searchRequestForm").val(search);
        findPlace();
        //setBounds();
    } else {
        selectTransportation(transportation);
        $("#transportation").val(transportation);
        $("#startPoint").val(start);
        $("#endPoint").val(end);
        findPath();
    }
}

// idx번째 최근 검색을 삭제하는 함수이다.
function deleteRecentSearch(idx) {
    let search = [];
    let previousSearch = JSON.parse(localStorage.getItem("recentSearch"));
    //console.log(JSON.parse(localStorage.getItem("recentSearch")));
    for (let i = 0; i < previousSearch.length; i++) search.push(previousSearch[i]);
    search.splice(idx, 1);
    if (!search.length) {
        $("#recentSearch").hide();
        localStorage.removeItem("recentSearch");
    } else localStorage.setItem("recentSearch", JSON.stringify(search));
    showRecentSearchList();
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
            /*====================================================================================================*/
            /*길찾기 경로 지도에 표시*/
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

            let endPoint = new kakao.maps.Marker({position: new kakao.maps.LatLng(route[route.length - 1].latitude, route[route.length - 1].longitude)});
            endPoint.setMap(map);
            markers.push(endPoint);

            /*let endPointInfo = document.createElement('div');
            endPointInfo.textContent = "도착";
            endPointInfo.style.width = "100%";
            endPointInfo.style.padding = "5px";
            endPoint.infowindow = new kakao.maps.InfoWindow({content: endPointInfo});
            endPoint.infowindow.open(map, endPoint);*/
            /*====================================================================================================*/


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


            /*====================================================================================================*/
            /*길찾기 결과의 거리, 시간 출력*/
            $("#findPathSection").attr('class', 'mb-3');
            $("#pathInfo").show();
            for (let i = 0; i < $("#pathInfo").children().length; i++) {
                $("#pathInfo").children().eq(i).show();
            }
            //console.log($("#pathInfo").children().length);
            //console.log($("#pathInfo").children());
            /*====================================================================================================*/


            /*====================================================================================================*/
            /*최근 검색 추가.*/
            addRecentSearch(FIND_PATH);
            /*====================================================================================================*/


            /*====================================================================================================*/
            /*지도 범위 재설정*/
            // 지도 범위 재설정에 사용되는 변수들이다.
            let minLat = {latitude: 90, longitude: 0};
            let maxLat = {latitude: -90, longitude: 0};
            let minLng = {latitude: 0, longitude: 180};
            let maxLng = {latitude: 0, longitude: -180};

            for (let i = 0; i < route.length; i++) {
                if (minLat.latitude > route[i].latitude) minLat = route[i];
                if (maxLat.latitude < route[i].latitude) maxLat = route[i];
                if (minLng.longitude > route[i].longitude) minLng = route[i];
                if (maxLng.longitude < route[i].longitude) maxLng = route[i];
            }
            let routeBounds = new kakao.maps.LatLngBounds();
            routeBounds.extend(new kakao.maps.LatLng(minLat.latitude, minLat.longitude));
            routeBounds.extend(new kakao.maps.LatLng(maxLat.latitude, maxLat.longitude));
            routeBounds.extend(new kakao.maps.LatLng(minLng.latitude, minLng.longitude));
            routeBounds.extend(new kakao.maps.LatLng(maxLng.latitude, maxLng.longitude));
            map.setBounds(routeBounds);
            /*====================================================================================================*/
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

showRecentSearchList();