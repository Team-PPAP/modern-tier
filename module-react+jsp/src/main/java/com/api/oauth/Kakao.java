package com.api.oauth;

import com.constant.KakaoApp;
import com.constant.Service;
import com.dao.UserDAO;
import com.dto.UserDTO;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Path("oauth/kakao")
public class Kakao {

    private final String OAuth_Redirect_URI = Service.USE_DOMAIN + "/api/oauth/kakao";

    private UserDTO userBean;

    // 인가 코드를 받은 뒤, 인가 코드로 액세스 토큰과 리프레시 토큰을 발급 받는 메소드
    public String getAccessToken(String authorize_code) {
        final String Kakao_Token_Req_URI = "https://kauth.kakao.com/oauth/token";

        try {
            String access_token;
            String refresh_token;

            URL url = new URL(Kakao_Token_Req_URI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=" + "authorization_code");
            sb.append("&client_id=" + KakaoApp.REST_API_KEY);
            sb.append("&redirect_uri=" + OAuth_Redirect_URI);
            sb.append("&code=" + authorize_code);
            bw.write(sb.toString());
            bw.flush();

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("에러입니다. 로그를 확인해주세요.");
            }

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            // System.out.println("response body: " + result);

            // Response JSON 파싱
            JSONObject jObject = new JSONObject(result);

            access_token = jObject.getString("access_token");
            refresh_token = jObject.getString("refresh_token");

            userBean.setKakao_access_token(access_token);
            userBean.setKakao_refresh_token(refresh_token);

            br.close();
            bw.close();

            return access_token;
        } catch (Exception ex) {
            System.out.println("getAccessToken 에러: " + ex);
        }
        return null;
    }

    // 로그인한 사용자의 정보를 불러옵니다
    public void getUserInfo(String access_token) {

        final String Kakao_UserInfo_Req_URI = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(Kakao_UserInfo_Req_URI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // 요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("에러입니다. 로그를 확인해주세요.");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            // System.out.println("response body : " + result);

            // Response JSON 파싱
            JSONObject jObject = new JSONObject(result);
            JSONObject kakao_accountObject = jObject.getJSONObject("kakao_account");
            JSONObject profileObject = kakao_accountObject.getJSONObject("profile");

            int id = jObject.getInt("id");

            String nickname = profileObject.getString("nickname");

            String profile_image_url = null;
            if (profileObject.isNull("profile_image_url") == false)
                profile_image_url = profileObject.getString("profile_image_url");

            String email = null;
            if (kakao_accountObject.isNull("email") == false)
                email = kakao_accountObject.getString("email");

            userBean.setKakao_id(id);
            userBean.setKakao_nickname(nickname);
            userBean.setKakao_profile_image_url(profile_image_url);
            userBean.setKakao_email(email);
        } catch (Exception ex) {
            System.out.println("getUserInfo 에러: " + ex);
        }
    }

    @GET
    public Response login(@QueryParam("code") String code, @Context HttpServletRequest req) {
        try {
            userBean = new UserDTO();

            // 인가 코드 확인
            // System.out.println("code: " + code);

            String access_token = getAccessToken(code);

            getUserInfo(access_token);

            System.out.println("User Login");
            System.out.println("Kakao_id: " + userBean.getKakao_id());
            System.out.println("Kakao_nickname: " + userBean.getKakao_nickname());
            System.out.println("Kakao_email: " + userBean.getKakao_email());
            System.out.println("kakao_profile_image_url: " + userBean.getKakao_profile_image_url());
            System.out.println("Kakao_access_token: " + userBean.getKakao_access_token());
            System.out.println("Kakao_refresh_token: " + userBean.getKakao_refresh_token());
            System.out.println("");

            UserDAO userDAO = UserDAO.getInstance();
            userDAO.userInsert(userBean);

            // session 저장
            HttpSession session = req.getSession(true);
            session.setAttribute("kakao_id", userBean.getKakao_id());

            boolean isRiotAccount = userDAO.isUserRiotAccount(userBean.getKakao_id());

            if (isRiotAccount == true) {
                // 홈 화면인 react으로 이동
                URI uri = new URI("/react/dist/");
                return Response.seeOther(uri).build();
            } else {
                // riot 계정 등록 페이지로 이동
                URI uri = new URI("/views/searchRiotAccount/");
                return Response.seeOther(uri).build();
            }

        } catch (Exception ex) {
            System.out.println("login 에러: " + ex);
        }
        return null;
    }
}
