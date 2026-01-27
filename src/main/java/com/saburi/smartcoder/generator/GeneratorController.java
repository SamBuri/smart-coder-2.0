package com.saburi.smartcoder.generator;

import com.saburi.smartcoder.base.ResponseObj;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/generate")
@RequiredArgsConstructor
public class GeneratorController {

    private final GeneratorService generatorService;
@PostMapping
    public ResponseObj<GeneratorResponse> generate(@RequestBody GeneratorRequest generatorRequest) throws Exception {
        return generatorService.generateCode(generatorRequest);
    }
}
