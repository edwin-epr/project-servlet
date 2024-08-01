package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value="/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session
        HttpSession currentSession = request.getSession();

        // Get the playfield object from the session
        Field field = extractField(currentSession);

        // get the index of the cell that was clicked
        int index = getSelectedIndex(request);
        Sign currentSign = field.getField().get(index);

        // Check if the clicked cell is empty.
        // Otherwise, we do nothing and send the user to the same page without changes
        // parameters in the session
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // put a cross in the cell that the user clicked on
        field.getField().put(index, Sign.CROSS);

        // Check if the cross has won after adding the user's last click
        if (checkWin(response, currentSession, field)) {
            return;
        }

        // Get an empty field cell
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            // Check if the zero won after adding the last zero
            if (checkWin(response, currentSession, field)) {
                return;
            }
        }
        // If there is no empty cell and no one wins, then it's a draw
        else {
            // Add a flag to the session that signals that a draw has occurred
            currentSession.setAttribute("draw", true);

            // Read the list of icons
            List<Sign> data = field.getFieldData();

            // Update this list in session
            currentSession.setAttribute("data", data);

            // helmet redirect
            response.sendRedirect("/index.jsp");
            return;
        }

        // Read the list of icons
        List<Sign> data = field.getFieldData();

        // Update field object and icon list in session
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        response.sendRedirect("/index.jsp");
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    /**
     * The method checks if there are three X/O's in a row.
     * returns true/false
     */
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            // Add a flag to indicate that someone has won
            currentSession.setAttribute("winner", winner);

            // Read the list of icons
            List<Sign> data = field.getFieldData();

            // Update this list in session
            currentSession.setAttribute("data", data);

            // helmet redirect
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
