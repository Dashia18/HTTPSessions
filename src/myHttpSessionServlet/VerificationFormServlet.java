package myVerificationFormServlet;

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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@WebServlet("/verificationFormServlet")
public class VerificationFormServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");

        String path = getServletContext().getRealPath("base.txt");


        String[][] arr =getDataAsArray(path);

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
            HttpSession httpSession = req.getSession(true);
            if (httpSession.isNew()){
                System.out.println("NEW SESSION");
                System.out.println(httpSession.getId());

                boolean loginOk = false;
                boolean pwdOk = false;

                for (String[] str : arr) {
                    if (str[0].equals(user)) {
                        System.out.println(str[0]);
                        loginOk = true;

                        if (str[1].equals(password)) {
                            System.out.println(str[1]);
                            System.out.println("login & psw ok");
                            //login & psw ok
                            pwdOk = true;
                            break;
                        } else {
                            //login ok & pwd bad
                            System.out.println("login ok & pwd bad");
                            break;
                        }
                    } else {
                        //new user
                        System.out.println(str[0]);
                        System.out.println(str[1]);
                        System.out.println("new user");
                    }
                }
                if (loginOk) {
                    if (pwdOk) {
                        //login & psw ok
                        resp.setStatus(HttpServletResponse.SC_OK);
                        req.setAttribute("massage1", "Congratulations, " + user + ", you login!");
                        try {
                            req.getRequestDispatcher("/result.jsp").forward(req, resp);
                        } catch (ServletException | IOException e) {
                            e.printStackTrace();
                        }


                        httpSession.setAttribute("user", user);
                        httpSession.setAttribute("password", password);


                        System.out.println(httpSession.getAttribute("user"));
                        System.out.println(httpSession.getAttribute("password"));


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
            else {
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

}
