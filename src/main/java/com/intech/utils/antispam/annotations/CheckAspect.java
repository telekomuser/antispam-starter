package com.intech.utils.antispam.annotations;

import com.intech.utils.antispam.exceptions.EmptyUserIdException;
import com.intech.utils.antispam.models.AvailableType;
import com.intech.utils.antispam.services.AntispamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CheckAspect {

    private final HttpServletRequest request;
    private final AntispamService antispamService;

    @Around("@annotation(Check)")
    public Object check(ProceedingJoinPoint joinPoint, Check check) throws Throwable {
        process(joinPoint, check);
        return joinPoint.proceed();
    }

    @Around("@annotation(checks)")
    public Object multipleCheck(ProceedingJoinPoint joinPoint, Checks checks) throws Throwable {
        Arrays.stream(checks.checks()).forEach(check -> {
            process(joinPoint, check);
        });
        return joinPoint.proceed();
    }

    private void process(ProceedingJoinPoint joinPoint, Check check) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String [] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();
        for (int index = 0; index < parameterNames.length && index < parameterValues.length; index++) {
            AvailableType type = AvailableType.findByName(parameterNames[index]);
            if (type != null) {

            }
            if (parameterNames[index].equals(check.variable())) {
                var userId = (String) Optional.ofNullable(parameterValues[index])
                        .orElseThrow(EmptyUserIdException::new);
                antispamService.checkRequest(userId, check.queryType(), check.strategy());
            }
        }
    }
}
