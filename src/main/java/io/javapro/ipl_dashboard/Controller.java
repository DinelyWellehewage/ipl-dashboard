package io.javapro.ipl_dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.javapro.ipl_dashboard.model.Match;


@RestController
@RequestMapping("/api")
public class Controller {

    @GetMapping("/match")
    public String getad(){
        Match match = new Match();
        return  match.getTeam1();
    }
    
}
