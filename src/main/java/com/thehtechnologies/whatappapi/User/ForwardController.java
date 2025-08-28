package com.thehtechnologies.whatappapi.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class ForwardController {

    @RequestMapping("/index")
    public String forward(HttpServletRequest request) {
        String uri = request.getRequestURI();

        log.error("before");
        // Exclusions
        if (uri.startsWith("/api") || uri.startsWith("/ws") || uri.startsWith("/swagger")) {
            log.error("inside api ws swagger");
            return null; // Laisser Spring gérer normalement
        }
        log.error("redirecting");

        // Forward vers le fichier statique situé dans /static/
        return "forward:/index.html";
    }
}
