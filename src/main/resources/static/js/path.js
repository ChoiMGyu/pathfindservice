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
    let graphRequestForm = $("#graphRequestForm").serialize();
    //console.log(graphRequestForm);
    $.ajax({
        type: "get",
        url: "/path?" + graphRequestForm,
        success: function (response) {
            console.log('distance: ' + response.distance);
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

            /*            let startPointInfo = document.createElement('div');
                        startPointInfo.textContent = "출발";
                        startPointInfo.style.width = "100%";
                        startPointInfo.style.padding = "5px";
                        startPoint.infowindow = new kakao.maps.InfoWindow({content: startPointInfo});
                        startPoint.infowindow.open(map, startPoint);*/

            let endPoint = new kakao.maps.Marker({position: new kakao.maps.LatLng(route[route.length - 1].latitude, route[route.length - 1].longitude)})
            endPoint.setMap(map);
            markers.push(endPoint);

            /*            let endPointInfo = document.createElement('div');
                        endPointInfo.textContent = "도착";
                        endPointInfo.style.width = "100%";
                        endPointInfo.style.padding = "5px";
                        endPoint.infowindow = new kakao.maps.InfoWindow({content: endPointInfo});
                        endPoint.infowindow.open(map, endPoint);*/
        },
        error: function (error) {
            alert("에러 발생!");
            console.log(error);
        }
    })
}