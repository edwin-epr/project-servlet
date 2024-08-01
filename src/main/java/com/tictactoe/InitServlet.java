package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Create a new session
        HttpSession currentSession = req.getSession(true);

        // Create a playing field
        Field field = new Field();
        Map<Integer, Sign> fieldData = field.getField();

        // Get a list of field values
        List<Sign> data = field.getFieldData();

        // Adding field parameters to the session (needed to store state between requests)
        currentSession.setAttribute("field", field);
        // and field values ​​sorted by index (required for drawing crosses and zeroes)
        currentSession.setAttribute("data", data);

        // Redirect request to index.jsp page via server
        getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
