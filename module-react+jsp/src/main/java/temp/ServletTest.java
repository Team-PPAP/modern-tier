package temp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/*
<%-- 서블릿 맵핑 예제1 --%>
<%--    <form action="/api" method="get">--%>
<%--        ID: <input type="text" name="id" value=""><br/>--%>
<%--        PW: <input type="text" name="pw" value=""><br/>--%>
<%--        <input type="submit" value="Submit">--%>
<%--    </form>--%>
*/

@WebServlet(name = "ServletTest", urlPatterns = "/api")
public class ServletTest extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        String id = request.getParameter("id");
        String pw = request.getParameter("pw");

        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println("id: " + id);
        out.println("pw: " + pw);
    }
}
