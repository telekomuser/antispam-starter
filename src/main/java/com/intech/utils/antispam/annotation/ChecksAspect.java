package com.intech.utils.antispam.annotation;


import com.intech.utils.antispam.exception.EmptyUserIdException;
import com.intech.utils.antispam.service.AntispamService;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;


@Aspect
@Service
@RequiredArgsConstructor
public class ChecksAspect {

    private final AntispamService antispamService;

    @Around("@annotation(check)")
    public Object check(ProceedingJoinPoint joinPoint, Check check) throws Throwable {
        process(joinPoint, check);
        return joinPoint.proceed();
    }

    @Around("@annotation(checks)")
    public Object multipleCheck(ProceedingJoinPoint joinPoint, Checks checks) throws Throwable {
        Arrays.stream(checks.checks()).forEach(check -> process(joinPoint, check));
        return joinPoint.proceed();
    }

    private void process(ProceedingJoinPoint joinPoint, Check check) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String [] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        for (int index = 0; index < parameterNames.length && index < parameterValues.length; index++) {
            if (parameterNames[index].equals(check.variable())) {
                String userId = (String) Optional.ofNullable(parameterValues[index])
                        .orElseThrow(EmptyUserIdException::new);
                antispamService.checkRequest(userId, check.queryType(), check.properties(), check.repeatProperties());
            }
        }
    }
}
