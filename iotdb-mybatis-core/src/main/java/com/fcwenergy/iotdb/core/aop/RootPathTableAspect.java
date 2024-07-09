package com.fcwenergy.iotdb.core.aop;

import com.fcwenergy.iotdb.base.BigdataBaseParam;
import com.fcwenergy.iotdb.core.annotation.GenerateTablePath;
import com.fcwenergy.iotdb.core.util.CesIotDbUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 获取路径表的切面
 * 开关判断是否使用切面注入路径表，当前Mapper参数较多，需要在实体对象参数中新增通用参数，所以暂不启用，但可以留存以打印参数
 *
 * @author endcy
 * @date 2024/06/23 18:30:30
 */
@Aspect
@Component
@Slf4j
public class RootPathTableAspect {

    @Pointcut(value = "@annotation(com.fcwenergy.iotdb.core.annotation.GenerateTablePath)")
    public void point() {
    }

    private static final boolean IS_INJECT = false;

    @Around("point()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!IS_INJECT) {
            return joinPoint.proceed();
        }
        Object object;
        String methodName = joinPoint.getSignature().getDeclaringTypeName();
        GenerateTablePath anno = joinPoint.getSignature().getDeclaringType().getMethod(methodName).getAnnotation(GenerateTablePath.class);
        if (Objects.isNull(anno)) {
            return joinPoint.proceed();
        }
        // 获取 isLogin 参数的值
        boolean isQuery = anno.value();
        Object[] args = joinPoint.getArgs();
        BigdataBaseParam param = BigdataBaseParam.builder().build();
        try {
            Object[] newArgs = new Object[args.length + 1];
            for (Object arg : args) {
                if (!(arg instanceof BigdataBaseParam)) {
                    continue;
                }
                param = (BigdataBaseParam) arg;
            }
            String path = CesIotDbUtil.getRootPath(param, isQuery);
            // 注入最后一个参数为路径表
            newArgs[args.length] = path;
            System.arraycopy(args, 0, newArgs, 0, args.length);
            object = joinPoint.proceed(newArgs);
        } catch (Exception e) {
            log.warn("RootPathTableAspect method:{} param:{} ,err:{}", methodName, args, e.getMessage());
            throw e;
        }
        return object;
    }
}
