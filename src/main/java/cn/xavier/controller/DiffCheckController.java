package cn.xavier.controller;

import cn.xavier.service.IDiffCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Zheng-Wei Shui
 * @date 12/5/2021
 */

@RestController
public class DiffCheckController {

    @Autowired
    private IDiffCheckService diffCheckService;

    @GetMapping("/tables")
    public List<String> getAllTables() {
        return diffCheckService.getAllTables();
    }

    @PostMapping("/diffCheck")
    public Map<String, String> diffCheck(@RequestBody Map<String, String> params) {
        return diffCheckService.diffCheck(params);
    }

}
