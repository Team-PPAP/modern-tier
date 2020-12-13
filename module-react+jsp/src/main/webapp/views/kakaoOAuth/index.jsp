<%@ page import="com.constant.KakaoApp" %>
<%@ page import="com.constant.Service" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    /*
    <button id="kakaoLogin"
                onclick="location.href='https://kauth.kakao.com/oauth/authorize?client_id=3888fb0f3d1eb5652a4f2ec494a1d3a7&redirect_uri=http://localhost:8080/api/oauth/kakao&response_type=code&scope=profile,account_email,friends,talk_message'">
            kakao login
        </button>
     */
    final String KAKAO_OAUTH_URI = "https://kauth.kakao.com/oauth/authorize?client_id=" + KakaoApp.REST_API_KEY + "&redirect_uri=" + "http://" + Service.USE_IP + "/api/oauth/kakao&response_type=code&scope=profile,account_email,friends,talk_message";
%>
<!DOCTYPE html>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="index.css"/>
</head>
<body>
<div id="root">
    <div>
        <span>안녕하세요</span>
    </div>
    <div>
        <pre>
modern-tier는 실제 LOL 전적 데이터를 기반하여
등록된 친구들의 랭킹을 한눈에 보여주는 서비스 입니다.

실제 랭킹이 변경되면 해당 내용이 kakao talk으로 전송됩니다.

아직 가입이 되어있지 않으시면 아래 버튼을 통해 가입을 진행해주세요
        </pre>
    </div>
    <div>
        <button id="kakaoLogin" onclick="location.href='<%=KAKAO_OAUTH_URI%>'">
            kakao login
        </button>
    </div>
</div>
</body>
</html>