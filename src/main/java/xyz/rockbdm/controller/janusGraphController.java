package xyz.rockbdm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.rockbdm.utils.JanusGraphHelper;

@RestController
@RequestMapping("/janusgraph")
public class janusGraphController {

    private final JanusGraphHelper janusGraphHelper;

    public janusGraphController(JanusGraphHelper janusGraphHelper) {
        this.janusGraphHelper = janusGraphHelper;
    }

    @GetMapping("/{id}")
    public Object queryById(@PathVariable("id") String id) {
        return janusGraphHelper.queryById(id);
    }
}
