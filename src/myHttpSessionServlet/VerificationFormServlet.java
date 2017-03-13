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
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@WebServlet("/verificationFormServlet")
public class VerificationFormServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = getServletContext().getRealPath("base.txt");
        String[][] arr = getDataAsArray(path);
        verifyUser(arr,req, resp);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
        }


    private static String[][] getDataAsArray(String path){
        List<String> strList = fileScannerToSrtList(path);
        return listToArrayLoginPwd(strList);
    }
    private static List<String> fileScannerToSrtList(String path) {
        List<String> strList = new LinkedList<>();
        try {
            Scanner in = new Scanner(new File(path));
            while (in.hasNext()){
                strList.add(in.nextLine());
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return strList;
    }
    private static String[][] listToArrayLoginPwd(List<String> strList){
        String[][] arr = new String[strList.size()][2];
        Pattern pattern2 = Pattern.compile("[ ,!?\\[\\]]");
        int i = 0;
        for (String str:strList){
            String[] words = pattern2.split(str);
            arr[i][0] = words[0];//0 col
            arr[i][1] = words[1];//1 col
            i++;
        }
        return arr;
    }

    private static void verifyUser(String[][] arr,
                                       HttpServletRequest req, HttpServletResponse resp) {

        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");
        String remember= (String)req.getParameter("ischecked");

            System.out.println("remember = " + remember);

        HttpSession httpSession= req.getSession(true);
            System.out.println("httpSession.isNew() = " + httpSession.isNew());

            System.out.println("httpSession.getAttribute(\"user in\") "+httpSession.getAttribute("user"));
            System.out.println("httpSession.getAttribute(\"password in\") " + httpSession.getAttribute("password"));
            System.out.println("httpSession.getAttribute(\"pwdTry in\") "+httpSession.getAttribute("pwdTry"));

        Integer pwdTry = getPwdTry(req, resp, user, httpSession);


            if (httpSession.getAttribute("user") == null ||  httpSession.getAttribute("password")== null ){

                System.out.println("NEW SESSION");
                System.out.println(httpSession.getId());

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
                        loginUser(req, resp, user, password, remember, httpSession, pwdTry);
                    } else {
                        badPwdLogin(req, resp, user, remember, httpSession, pwdTry);
                    }
                }
                else {
                    createUser(req, resp);
                }
            }
            else {
                openOldSession(req, resp, httpSession);
            }

            if(null == remember) {
                httpSession.invalidate();
                System.out.println("clear httpSession");
            }

        }
    
    private static Integer getPwdTry(HttpServletRequest req, HttpServletResponse resp,
                                     String user, HttpSession httpSession) {

        Integer pwdTry = (Integer)httpSession.getAttribute("pwdTry");
        if(pwdTry == null || !user.equals(httpSession.getAttribute("user"))){
            pwdTry = 0;
        }
        else {
            if(pwdTry>2){
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

    private static void loginUser(HttpServletRequest req, HttpServletResponse resp, String user,
                                  String password, String remember, HttpSession httpSession, Integer pwdTry) {
        //login & psw ok
        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1", "Congratulations, " + user + ", you login!");
        try {
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }

        if("on".equals(remember)) {

            httpSession.setAttribute("user", user);
            httpSession.setAttribute("password", password);
            httpSession.setAttribute("pwdTry", pwdTry);

            System.out.println("httpSession.getAttribute(\"user out\") "+httpSession.getAttribute("user"));
            System.out.println("httpSession.getAttribute(\"password out\") " + httpSession.getAttribute("password"));
            System.out.println("httpSession.getAttribute(\"pwdTry out\") "+httpSession.getAttribute("pwdTry"));
        }
    }

    private static void badPwdLogin(HttpServletRequest req, HttpServletResponse resp, String user,
                                    String remember, HttpSession httpSession, Integer pwdTry) {
        //login ok & pwd bad
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        if("on".equals(remember)) {
            pwdTry++;
            System.out.println("pwdTry = " + pwdTry);
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("password", null);
            httpSession.setAttribute("pwdTry", pwdTry);

            System.out.println("httpSession.getAttribute(\"user out\") "+httpSession.getAttribute("user"));
            System.out.println("httpSession.getAttribute(\"password out\") " + httpSession.getAttribute("password"));
            System.out.println("httpSession.getAttribute(\"pwdTry out\") "+httpSession.getAttribute("pwdTry"));
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

    private static void openOldSession(HttpServletRequest req, HttpServletResponse resp, HttpSession httpSession) {
        System.out.println("OLD SESSION");
        System.out.println(httpSession.getId());
        System.out.println(httpSession.getAttribute("user"));
        System.out.println(httpSession.getAttribute("password"));

        String userSessionName = (String) httpSession.getAttribute("user");

        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1", "Congratulations, " + userSessionName + ", you login!");
        try {
            req.getRequestDispatcher("/result.jsp").forward(req, resp);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

}
