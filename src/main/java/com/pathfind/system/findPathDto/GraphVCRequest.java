package com.pathfind.system.findPathDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphVCRequest {
    private Long start;
    private Long end;
    private String transportation;
}
