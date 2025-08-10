package com.thehtechnologies.whatappapi.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ForwardController {

    @GetMapping(value =  "/index")
    public String forward(HttpServletRequest request) {
        String uri = request.getRequestURI();

        log.error("before");
        // Exclusions
        if (uri.startsWith("/api") || uri.startsWith("/ws") || uri.startsWith("/swagger")) {
            log.error("inside api ws swagger");
            return null; // Laisser Spring gérer normalement
        }
        log.error("redirecting");

        return "forward:/index.html";
    }
}
