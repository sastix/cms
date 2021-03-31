/*
 * Copyright(c) 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sastix.cms.common.services.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Configuration
@EnableAspectJAutoProxy
public class MethodMonitor {

    static {
        System.setProperty("spring.aop.proxy-target-class","true");
    }

    @Around("execution(* com.sastix..services..*.*(..))")
    public Object logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {
            return joinPoint.proceed(); // if not on DEBUG, no point in writing anything
        }

        String name = joinPoint.getSignature().getName();
        log.debug("==> {}({})", name, argsAsStrings(joinPoint.getArgs()));

        try {
            Object obj = joinPoint.proceed(); //continue on the intercepted method
            log.debug("<==  {}(...) = {}", name, argsAsStrings(obj));
            return obj;
        } catch (Throwable t) {
            log.error("<==! {}(...) => EXCEPTION {}", new Object[]{name,t.getMessage()});
            if (t.getCause() != null) {
                log.error("<==! caused by: {} - message: {}", t.getCause(), t.getCause().getMessage());
            }
            log.error("<==! exception log: ", t);
            throw t;
        }


    }

    private String argsAsStrings(Object obj) {
        if (obj == null) {
            return "null";
        } else if (isArray(obj) && obj instanceof byte[]) {
            return "byte[" + ((byte[])obj).length + "]" ;
        } else if (isArray(obj)) {
            return arrayAsString(obj);
        } else {
            return obj.toString();
        }
    }

    private String argsAsStrings(Object[] args) {
        StringBuilder bld = new StringBuilder("");
        if (args == null || args.length == 0) {
            return "";
        } else {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    bld.append("null,");
                } else if (isArray(args[i]) && args[i] instanceof byte[]) {
                    bld.append("byte[").append(((byte[]) args[i]).length ).append("],");
                } else if (isArray(args[i])) {
                    bld.append(arrayAsString(args[i])).append(",");
                } else {
                    bld.append(args[i].toString()).append(",");
                }
            }
            return bld.substring(0, bld.length() - 1); //strip last ","
        }
    }

    private String arrayAsString(Object e1) {
        if (e1 instanceof Object[])
            return Arrays.toString((Object[]) e1);
        else if (e1 instanceof short[])
            return Arrays.toString((short[]) e1);
        else if (e1 instanceof int[])
            return Arrays.toString((int[]) e1);
        else if (e1 instanceof long[])
            return Arrays.toString((long[]) e1);
        else if (e1 instanceof char[])
            return Arrays.toString((char[]) e1);
        else if (e1 instanceof float[])
            return Arrays.toString((float[]) e1);
        else if (e1 instanceof double[])
            return Arrays.toString((double[]) e1);
        else if (e1 instanceof boolean[])
            return Arrays.toString((boolean[]) e1);
        else
            return e1.toString();
    }

    public static boolean isArray(Object obj)
    {
        return obj.getClass().isArray();
    }

}
