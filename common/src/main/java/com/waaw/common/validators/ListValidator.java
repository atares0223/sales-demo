package com.waaw.common.validators;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ListValidator implements ConstraintValidator<ValidList, List<?>> {
    @Override
    public void initialize(ValidList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<?> list, ConstraintValidatorContext context) {
        if (list == null || list.isEmpty()) {
            return true; // 空列表不进行校验
        }

        // 执行自定义的校验逻辑
        for (Object element : list) {

        }

        return true; // 校验通过
    }
}
