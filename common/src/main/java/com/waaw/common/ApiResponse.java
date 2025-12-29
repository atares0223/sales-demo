package com.waaw.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    // 可以添加更多自定义方法，例如：
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static ApiResponse error(String message) {
        return error(500, message);
    }

    public static ApiResponse error(Integer status, String message) {
        return ApiResponse.builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}
