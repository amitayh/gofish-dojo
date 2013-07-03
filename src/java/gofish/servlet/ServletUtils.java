package gofish.servlet;

import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
    
    public static Integer getInteger(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        return (parameter != null) ? new Integer(parameter) : null;
    }
    
    public static boolean getBoolean(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        return (parameter != null) ? parameter.equals("true") : false;
    }

}
