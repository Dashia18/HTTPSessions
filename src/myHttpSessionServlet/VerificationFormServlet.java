package myHttpSessionServlet;

/**
 * Created by Daria Serebryakova on 03.03.2017.
 */

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


@WebServlet("/verificationFormServlet")
public class VerificationFormServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = getServletContext().getRealPath("base.txt");
        verifyUser(path,req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
        }


    private static void verifyUser(String path, HttpServletRequest req, HttpServletResponse resp) {

        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");
        String remember= (String)req.getParameter("ischecked");

        HttpSession httpSession= req.getSession(true);
        Integer pwdTry = getPwdTry(req, resp, httpSession);


        if (httpSession.getAttribute("user") == null ||  httpSession.getAttribute("password")== null ){
            System.out.println("new session");
            loginUserOrCreateNewUser(path, req, resp, httpSession, pwdTry);
        }

        else {
            System.out.println("user = " + user);
            System.out.println("password = " + password);

            if(user.equals("") && password.equals("")) {
                System.out.println("last session");
                String userSessionName =(String) httpSession.getAttribute("user");
                    loginOldUser(req, resp, httpSession, pwdTry, userSessionName);
            }
            else {
                if (password.equals("")){
                    System.out.println("one of old session");
                    Map<String,String> logInUsers = (HashMap<String, String>) httpSession.getAttribute("loginUsers");

                    if(logInUsers != null && logInUsers.get(user) != null) {
                        httpSession.setAttribute("user", user);
                        httpSession.setAttribute("password", logInUsers.get(user));
                        httpSession.setAttribute("pwdTry", 0);

                        loginOldUser(req, resp, httpSession, pwdTry, user);
                    }
                    else {
                        loginUserOrCreateNewUser(path, req, resp, httpSession, pwdTry);
                    }
                }
                else {
                    System.out.println("new user in this session");
                    loginUserOrCreateNewUser(path, req, resp, httpSession, pwdTry);
                }
            }
        }

        if(null == remember) {
            httpSession.invalidate();
            System.out.println("clear httpSession");
        }
    }


    private static Integer getPwdTry(HttpServletRequest req, HttpServletResponse resp,
                                     HttpSession httpSession) {
        String user = (String) req.getParameter("login");

        Integer pwdTry = (Integer)httpSession.getAttribute("pwdTry");
        if(pwdTry == null || !user.equals(httpSession.getAttribute("user"))){
            pwdTry = 0;
        }
        else {
            if(pwdTry>1){
                resp.setStatus(HttpServletResponse.SC_OK);
                System.out.println("GO TO UPDATE password");
                try {
                    req.getRequestDispatcher("/pwdchange.html").forward(req, resp);
                } catch (ServletException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("pwdTry = " + pwdTry);
        return pwdTry;
    }

    private static void loginUserOrCreateNewUser(String path,HttpServletRequest req, HttpServletResponse resp,
                                       HttpSession httpSession, Integer pwdTry) {

        String[][] arr = GetDataAsArray.getDataAsArray(path);
        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");
        System.out.println("Login User or create NEW user");


        boolean loginOk = false;
        boolean pwdOk = false;

        for (String[] str : arr) {
            if (str[0].equals(user)) {
                loginOk = true;
                if (str[1].equals(password)) {
                    System.out.println("login & psw ok");
                    pwdOk = true;
                    break;
                } else {
                    System.out.println("login ok & pwd bad");
                    break;
                }
            } else {
                System.out.println("don`t find in base.txt");
            }
        }
        if (loginOk) {
            if (pwdOk) {
                loginUser(req, resp, httpSession, pwdTry);
            } else {
                badPwdLogin(req, resp, httpSession, pwdTry);
            }
        }
        else {
            createUser(req, resp);
        }
    }

    private static void loginUser(HttpServletRequest req, HttpServletResponse resp,
                                  HttpSession httpSession, Integer pwdTry) {
        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");
        String remember= (String)req.getParameter("ischecked");
        //login & psw ok
        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1", "Congratulations, " + user + ", you login!");
        try {
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

        if("on".equals(remember)) {

            getLoginUsers(user, password, httpSession);

            httpSession.setAttribute("user", user);
            httpSession.setAttribute("password", password);
            httpSession.setAttribute("pwdTry", pwdTry);
        }
    }

    private static void getLoginUsers(String user, String password, HttpSession httpSession) {

        HashMap<String,String> logInUsers = (HashMap<String, String>) httpSession.getAttribute("loginUsers");
        if(logInUsers == null){
            logInUsers = new HashMap<String,String>();
        }
        System.out.println("logInUsers = " + logInUsers);

        logInUsers.put(user,password);
        httpSession.setAttribute("loginUsers", logInUsers);
    }

    private static void badPwdLogin(HttpServletRequest req, HttpServletResponse resp,
                                    HttpSession httpSession, Integer pwdTry) {
        String user = (String) req.getParameter("login");
        String remember= (String)req.getParameter("ischecked");
        //login ok & pwd bad
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        if("on".equals(remember)) {
            pwdTry++;
            System.out.println("pwdTry = " + pwdTry);
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("password", null);
            httpSession.setAttribute("pwdTry", pwdTry);
        }

        req.setAttribute("massage1", user + ", password is not correct! ");

        try {
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void createUser(HttpServletRequest req, HttpServletResponse resp) {
        //new user
        resp.setStatus(HttpServletResponse.SC_OK);
        System.out.println("GO TO CREATE file");

        try {
            req.getRequestDispatcher("/create.html").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void loginOldUser(HttpServletRequest req, HttpServletResponse resp,
                                       HttpSession httpSession, Integer pwdTry,String userSessionName) {
        System.out.println("OLD User");
        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1", "Congratulations, " + userSessionName + ", you login!");
        try {
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

}
