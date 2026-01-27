package com.saburi.smartcoder.generator;

import com.saburi.smartcoder.base.FileResponse;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class GeneratorResponse {
    String outputFile;
    private String content;
    @Singular
    private List<FileResponse> fileResponses;

}
