package myCookieServlet;

/**
 * Created by Daria Serebryakova on 09.03.2017.
 */

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@WebServlet("/cookieServlet")
public class CookieServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");

        String path = getServletContext().getRealPath("base.txt");
        String[][] arr = getDataAsArray(path);
        verifyUser(user,password,arr,req, resp);

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

    private static void verifyUser(String user, String password,
                                   String[][] arr,
                                   HttpServletRequest req, HttpServletResponse resp) {
        String remember= (String)req.getParameter("ischecked");
        System.out.println("remember = " + remember);

        Cookie[] cookies = req.getCookies();
        //System.out.println(cookies.length);

        if(cookies!=null && cookies.length>1){
            System.out.println("cookies is not null");
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
        else {

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
//                    userCookie.setMaxAge(60*60*0);
//                    passwordCookie.setMaxAge(60*60*0);
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

//        if(null == remember) {
//            Cookie userCookie = new Cookie("login", user);
//            Cookie passwordCookie = new Cookie("password", password);
//            Cookie jSessionId = new Cookie("JSESSIONID JSESSIONID", "0");
//                    userCookie.setMaxAge(60*60*0);
//                    passwordCookie.setMaxAge(60*60*0);
//                    jSessionId.setMaxAge(60*60*0);
//            resp.addCookie(userCookie);
//            resp.addCookie(passwordCookie);
//            resp.addCookie(jSessionId);
//        }




    }

}
