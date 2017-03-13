package myHttpSessionServlet;

/**
 * Created by Daria Serebryakova on 06.03.2017.
 */
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


@WebServlet("/changePasswordServlet")
public class ChangePasswordServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String user = (String) req.getParameter("login");
        String newPassword = (String) req.getParameter("password");

        String path = getServletContext().getRealPath("base.txt");
        String finalStr = "";

        FileWriter fw = null;
        File file = new File(path);


        System.out.println(file);

        String[][] arr = getDataAsArray(path);
        for (int i = 0; i < arr.length; i++) {
                if (arr[i][0].equals(user)) {
                    System.out.println("arr[1][0] + \" \" + user = " + arr[1][0] + " " + user);
                    arr[i][1] = newPassword;
                    break;
                } else {
                    System.out.println("don`t find in base.txt");
                }
        }

        for (String[] str1: arr){
            finalStr = finalStr +str1[0]+ " "+ str1[1] + "\n";
        }

        System.out.println(finalStr);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(path),false);
            fileWriter.write(finalStr);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpSession httpSession= req.getSession(true);

        httpSession.setAttribute("user", user);
        httpSession.setAttribute("password", newPassword);
        httpSession.setAttribute("pwdTry", 0);

        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1","Congratulations, "+ user + ", you change your password!");
        req.getRequestDispatcher("/result.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);

    }

    public static String txtScanner(String path) {
        String txtFile = "";
        boolean isPrint = true;
        try {
            Scanner in = new Scanner(new File(path));
            StringBuffer data = new StringBuffer();
            while (in.hasNext()) {
                data.append(in.nextLine()).append("\n");
            }

            txtFile = data.toString();
        } catch ( Exception ex ) {
            isPrint = false;
            ex.printStackTrace();
        }

        if(isPrint){
            System.out.println("file \"" + path + "\" found \nfile contents:\n");

            System.out.println(txtFile);
        }

        return txtFile;
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

}
