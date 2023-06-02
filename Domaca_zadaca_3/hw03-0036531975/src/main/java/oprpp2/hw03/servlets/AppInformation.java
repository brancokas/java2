package oprpp2.hw03.servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppInformation implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Long milliseconds = System.currentTimeMillis();
        sce.getServletContext().setAttribute("startTime", milliseconds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
