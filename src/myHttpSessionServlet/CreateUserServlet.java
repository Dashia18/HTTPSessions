package myVerificationFormServlet;

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


@WebServlet("/createUserServlet")
public class CreateUserServlet extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String user = (String) req.getParameter("login");
        String password = (String) req.getParameter("password");

        String path = getServletContext().getRealPath("base.txt");
        String finalStr = user+" "+password;

        FileWriter fw = null;
        File file = new File(path);

        System.out.println(finalStr);
        System.out.println(file);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(path),true);
            fileWriter.write(finalStr + "\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpSession httpSession = req.getSession(true);
        httpSession.setAttribute("user", user);
        httpSession.setAttribute("password", password);

        resp.setStatus(HttpServletResponse.SC_OK);
        req.setAttribute("massage1","Congratulations, "+ user + ", you create profile!");
        req.getRequestDispatcher("/result.jsp").forward(req, resp);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);

    }
}
