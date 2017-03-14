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

@WebServlet("/changePasswordServlet")
public class ChangePasswordServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String user = (String) req.getParameter("login");
        String newPassword = (String) req.getParameter("password");

        String path = getServletContext().getRealPath("base.txt");
        String finalStr = "";
        File file = new File(path);
        System.out.println(file);

        String[][] arr = GetDataAsArray.getDataAsArray(path);
        for (int i = 0; i < arr.length; i++) {
                if (arr[i][0].equals(user)) {
                    arr[i][1] = newPassword;
                    break;
                } else {
                    System.out.println("don`t find in base.txt");
                }
        }

        for (String[] str1: arr){
            finalStr = finalStr +str1[0]+ " "+ str1[1] + "\n";
        }
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



}
