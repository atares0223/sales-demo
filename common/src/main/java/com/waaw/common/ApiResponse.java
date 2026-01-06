package com.waaw.common;

import com.waaw.common.enums.ResultCodeEnum;
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
                .status(ResultCodeEnum.SUCCESS.getCode())
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error() {
        return error(ResultCodeEnum.ERROR.getCode(), ResultCodeEnum.ERROR.getMessage());
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(ResultCodeEnum.ERROR.getCode(), message);
    }

    public static <T> ApiResponse<T> error(Integer status, String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(message);
        apiResponse.setStatus(status);
        return apiResponse;
    }
}
