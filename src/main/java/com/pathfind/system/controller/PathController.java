package com.pathfind.system.controller;

import com.pathfind.system.findPathDto.GraphVCRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/map/*")
public class PathController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @GetMapping("/path")
    public String getPath(HttpServletRequest request, Model model) {
        logger.info("get find path page");
        //HttpSession session = request.getSession();
        model.addAttribute("graphRequest", new GraphVCRequest());

        return "map/path";
    }
}
