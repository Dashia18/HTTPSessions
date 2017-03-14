package myCookieServlet;

/**
 * Created by Daria Serebryakova on 09.03.2017.
 */

import myHttpSessionServlet.GetDataAsArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/cookieServlet")
public class CookieServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        String path = getServletContext().getRealPath("base.txt");
        verifyUser(req, resp, path);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
    }

    private static void verifyUser(HttpServletRequest req, HttpServletResponse resp, String path) {

        Cookie[] cookies = req.getCookies();

        if(cookies!=null && cookies.length>1){
            System.out.println("cookies is not null");
            getOldCookiesSession(req, resp, cookies);
        }
        else {
            getNewCookiesSession(req, resp, path);
        }
    }

    private static void getNewCookiesSession(HttpServletRequest req, HttpServletResponse resp, String path) {
        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");
        String remember= (String)req.getParameter("ischecked");

        String[][] arr = GetDataAsArray.getDataAsArray(path);

        boolean loginOk = false;
        boolean pwdOk = false;
        for (String[] str : arr) {
            if (str[0].equals(user)) {
                loginOk = true;
                if (str[1].equals(password)) {
                    pwdOk = true;
                    break;
                } else {
                    break;
                }
            }
        }
        if (loginOk) {
            if (pwdOk) {
                //login & psw ok
                if("on".equals(remember)) {
                    Cookie userCookie = new Cookie("login", user);
                    Cookie passwordCookie = new Cookie("password", password);
                    resp.addCookie(userCookie);
                    resp.addCookie(passwordCookie);
                }
                resp.setStatus(HttpServletResponse.SC_OK);
                req.setAttribute("massage1", "Congratulations, " + user + ", you login!");
                try {
                    req.getRequestDispatcher("/result.jsp").forward(req, resp);
                } catch (ServletException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                    //login ok & pwd bad
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    req.setAttribute("massage1", user + ", password is not correct! ");
                    try {
                        req.getRequestDispatcher("/result.jsp").forward(req, resp);
                    } catch (ServletException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        else {
            //new user
            resp.setStatus(HttpServletResponse.SC_OK);
            try {
                req.getRequestDispatcher("/create.html").forward(req, resp);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getOldCookiesSession(HttpServletRequest req, HttpServletResponse resp, Cookie[] cookies) {
        Map<String, String> cookiesMap= new TreeMap<String, String>();
        for (Cookie cookie : cookies) {
            System.out.println( cookie.getName()+" "+ cookie.getValue());
            cookiesMap.put(cookie.getName(), cookie.getValue());
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1", "Congratulations, " + cookiesMap.get("login") + ", you login!");
        try {
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }
}
