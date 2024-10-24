package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mining")
public class MiningController {

    @Autowired
    private MiningService miningService;

    @GetMapping("/")
    public List<String> getAllMiningData() {
        return miningService.getMiningData();
    }

    @PostMapping("/mine")
    public String performMining() {
        return miningService.mine();
    }

    @PostMapping("/add")
    public String addMiningData(@RequestParam String data) {
        miningService.addMiningData(data);
        return "Data added successfully!";
    }
}
