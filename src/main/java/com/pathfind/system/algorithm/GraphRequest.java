package com.pathfind.system.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphRequest {
    private Long start;
    private Long end;
    private String transportation;
}
