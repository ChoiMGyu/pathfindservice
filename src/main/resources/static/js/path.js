const SEARCH = "search";
const FIND_PATH = "findPath";
const BUILDING = "BUILDING";

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
    resetError();
    $("#carBtn").attr('class', 'form-control btn btn-outline-primary');
    $("#onFootBtn").attr('class', 'form-control btn btn-outline-primary');
    $("#transportation").val("");
    $("#startPoint").val("");
    $("#endPoint").val("");
    $("#start").val("");
    $("#end").val("");
}

function resetSearchForm() {
    $("#searchRequestForm").val("");
}

// 출발지와 도착지 사이의 폴리라인(카카오 api의 객체, 경로를 의미함)들을 저장한다. 새로운 경로 탐색 시 기존 경로를 삭제하기 위해 사용한다.
let drawRoute = [];
var markers = [];

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
function addMarker(position) {
    var marker = new kakao.maps.Marker({
        position: position
    });
    marker.setMap(map); // 지도 위에 마커를 표출합니다
    markers.push(marker);  // 배열에 생성된 마커를 추가합니다

    return marker;
}

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
function removeMarker() {
    console.log("removeMarker() 호출됨");
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

function removePolyline() {
    console.log("removePolyline() 호출됨");
    for (var i = 0; i < drawRoute.length; i++) {
        drawRoute[i].setMap(null);
    }
    drawRoute = [];
}

// JavaScript 코드
// findPlace 함수 정의
function findPlace() {
    // 입력된 값을 가져와서 query 변수에 저장
    console.log("findPlace() 함수 호출됨");
    $("#searchPlaceSection").attr('class', 'mt-3 mb-5');
    resetPlaceError();
    if (!isPlaceEmpty()) return;
    var searchContent = $("#searchRequestForm").val();
    console.log("검색 내용:", searchContent);
    resetError();
    // Ajax 요청 보내기
    $.ajax({
        type: "GET",
        url: "/searchPlace",
        data: {
            searchContent: searchContent
        },
        success: function (response) {
            // 서버로부터의 응답을 처리
            console.log("SearchController에서 반환하는 response:", response);

            if (!response.name) {
                resetPlaceError();
                $("#searchRequestForm").attr('class', "form-control fieldError").focus();
                $("#searchPlaceError").text('검색 내용이 존재하지 않습니다').show();
                return;
            }

            var latitude = response.latitude;
            var longitude = response.longitude;
            console.log("검색된 장소의 위도:", latitude);
            console.log("검색된 장소의 경도:", longitude);

            //현재 지도에 있는 마커들을 지움
            removeMarker();
            //resetFindPathForm();
            $("#findPathSection").attr('class', 'mb-5');
            $("#pathInfoSection").hide();
            removePolyline();

            // 마커가 표시될 위치입니다
            //var markerPosition  = new kakao.maps.LatLng(latitude, longitude);

            // 마커를 생성합니다
            // var marker = new kakao.maps.Marker({
            //     position: markerPosition
            // });
            //var marker = addMarker(markerPosition);

            // 마커가 지도 위에 표시되도록 설정합니다
            //marker.setMap(map);

            //마커를 생성하면서 마커를 배열에 넣음
            setBounds(response);

            /*====================================================================================================*/
            /*최근 검색 추가*/
            addRecentSearch(SEARCH);
            /*====================================================================================================*/


            /*====================================================================================================*/
            /*검색 정보 제공*/
            $("#findPathSection").hide();
            $("#pathInfoSection").hide();
            $("#recentSearchSection").hide();
            $("#searchInfoSection").show();
            $("#searchName").text(response.name);
            if (response.objectType === "BUILDING") $("#searchAddress").text(response.address).show();
            else $("#searchAddress").hide();
            $("#searchDescription").text(response.description);
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
                    "<li onselectstart='return false' class='alert alert-dark alert-dismissible' style='list-style: none; padding-bottom: 0; padding-top: 0; margin-bottom: 0.25rem' id='" + (i + "thSearch").toString() + "'>" +
                    "   <div role = 'button' onclick='deleteRecentSearch(" + i + "); searchAgain(\"" + searchList[i].search + "\");'>"
                    + text +
                    "   </div>" +
                    "   <button style='padding: 0.25rem;' type='button' class='btn-close' data-bs-dismiss='alert' aria-label='Close' onclick='deleteRecentSearch(" + i + ")'></button>" +
                    "</li>");
            } else {
                let text = (searchList[i].transportation + (searchList[i].transportation === "자동차" ? "" : "\u00a0\u00a0\u00a0") + " | " + searchList[i].start + " → " + searchList[i].end).toString();
                //console.log(searchList[i]);
                $("#recentSearch").append(
                    "<li onselectstart='return false' class='alert alert-dark alert-dismissible' style='list-style: none; padding-bottom: 0; padding-top: 0; margin-bottom: 0.25rem' id='" + (i + "thSearch").toString() + "'>" +
                    "   <div role = 'button' onclick='deleteRecentSearch(" + i + "); searchAgain(" + null + ", \"" + searchList[i].transportation + "\", \"" + searchList[i].start + "\", \"" + searchList[i].end + "\");'>"
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

// 서버에게 길찾기 수행을 요청하고 길찾기 결과를 반환받아 길찾기 결과 정보를 사용자에게 제공하는 함수이다.
function findPath() {
    console.log("findPath() 호출됨")
    $("#findPathSection").attr('class', 'mb-5');
    resetError();
    $("#searchPlaceSection").attr('class', 'mt-3 mb-5');
    resetPlaceError();
    if (!isTransportationsEmpty() || !isStartEmpty() || !isEndEmpty()) return;

    /*====================================================================================================*/
    /*출발지, 도착지 정점 아이디 찾기*/
    let searchStartContent = $("#startPoint").val();
    let searchEndContent = $("#endPoint").val();
    let roadVertexStartId, roadVertexEndId, sidewalkVertexStartId, sidewalkVertexEndId;
    let startObjectType, endObjectType;
    let isReturn = false;
    $.ajax({
        type: "GET",
        url: "/searchPlace",
        async: false,
        data: {
            searchContent: searchStartContent
        },
        success: function (response) {
            console.log(response);
            if (!response.name) {
                $("#findPathError").text("올바른 출발지를 입력해 주세요.").show();
                $("#startPoint").attr('class', "form-control fieldError").focus();
                isReturn = true;
                return;
            }
            roadVertexStartId = response.roadVertexId;
            sidewalkVertexStartId = response.sidewalkVertexId;
            startObjectType = response.objectType;
        },
        error: function (error) {
            $("#findPathError").text("올바른 출발지를 입력해 주세요.").show();
            $("#startPoint").attr('class', "form-control fieldError").focus();
        }
    });
    if (isReturn) return;
    $.ajax({
        type: "GET",
        url: "/searchPlace",
        async: false,
        data: {
            searchContent: searchEndContent
        },
        success: function (response) {
            console.log(response);
            if (!response.name) {
                $("#findPathError").text("올바른 도착지를 입력해 주세요.").show();
                $("#endPoint").attr('class', "form-control fieldError").focus();
                isReturn = true;
                return;
            }
            roadVertexEndId = response.roadVertexId;
            sidewalkVertexEndId = response.sidewalkVertexId;
            endObjectType = response.objectType;
        },
        error: function (error) {
            $("#findPathError").text("올바른 도착지를 입력해 주세요.").show();
            $("#endPoint").attr('class', "form-control fieldError").focus();
        }
    });
    if (isReturn) return;
    if ($("#transportation").val() === "자동차") {
        $("#start").val(roadVertexStartId);
        $("#end").val(roadVertexEndId);
    } else {
        $("#start").val(sidewalkVertexStartId);
        $("#end").val(sidewalkVertexEndId);
    }
    /**
     * 길찾기 사이트에서 출발지와 도착지를 정점 아이디로 검색하고 싶다면
     * 윗 부분을 주석 처리하고 아랫부분의 주석을 해제하면 됨.
     */
    /*$("#start").val($("#startPoint").val());
    $("#end").val($("#endPoint").val());*/
    /*====================================================================================================*/

    let graphRequestForm = $("#graphRequestForm").serialize();
    //console.log(graphRequestForm);
    $.ajax({
        type: "get",
        url: "/path?" + graphRequestForm,
        success: function (response) {
            /*====================================================================================================*/
            /*길찾기 경로 지도에 표시*/
            //console.log('distance: ' + response.distance);
            resetSearchForm();

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
                let lineColor = '#db4040', lineType = 'solid';
                if ((i === 1 && startObjectType === BUILDING) || (i === route.length - 1 && endObjectType === BUILDING)) {
                    lineColor = '#808080';
                    lineType = 'dashed';
                }
                let polyline = new kakao.maps.Polyline({
                    path: linePath, // 선을 구성하는 좌표배열 입니다
                    strokeWeight: 5, // 선의 두께 입니다
                    strokeColor: lineColor, // 선의 색깔입니다
                    strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
                    strokeStyle: lineType, // 선의 스타일입니다
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
            $("#pathInfoSection").show();
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
    console.log("지도 범위 재설정 하기");
    // 버튼을 클릭하면 아래 배열의 좌표들이 모두 보이게 지도 범위를 재설정합니다
    var points = [
        new kakao.maps.LatLng(response.latitude, response.longitude)
    ];

    // 지도를 재설정할 범위정보를 가지고 있을 LatLngBounds 객체를 생성합니다
    var bounds = new kakao.maps.LatLngBounds();

    var i, marker;
    for (i = 0; i < points.length; i++) {
        // 배열의 좌표들이 잘 보이게 마커를 지도에 추가합니다
        marker = new kakao.maps.Marker({position: points[i]});
        markers.push(marker);
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
    if ($("#searchRequestForm").val() === "") {
        resetPlaceError();
        $("#searchRequestForm").attr('class', "form-control fieldError").focus();
        $("#searchPlaceError").text('검색 내용을 입력해주세요').show();
        return false;
    }
    return true;
}

// 길찾기에서 출발지와 도착지를 바꾸는 함수이다.
function changeStartEnd() {
    let startPoint = $("#startPoint").val();
    $("#startPoint").val($("#endPoint").val());
    $("#endPoint").val(startPoint);
}

// 검색 결과에서 출발 버튼을 누르면 해당 검색 결과가 출발지로 설정되는 함수이다.
function setSearchStartPoint() {
    $("#startPoint").val($("#searchRequestForm").val());
    goToHome();
}

// 검색 결과에서 도착 버튼을 누르면 해당 검색 결과가 도착지로 설정되는 함수이다.
function setSearchEndPoint() {
    $("#endPoint").val($("#searchRequestForm").val());
    goToHome();
}

// 길찾기, 최근 검색을 사용자에게 보여주는 함수이다.
function goToHome() {
    $("#searchRequestForm").val("");
    $("#searchInfoSection").hide();
    $("#findPathSection").show();
    $("#recentSearchSection").show();
}

$("#findPathSection").show();
$("#recentSearchSection").show();
showRecentSearchList();