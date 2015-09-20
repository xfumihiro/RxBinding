package com.jakewharton.rxbinding.weaving.aspect;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

@Aspect
public class DebugAspect {
  private Map<String, Boolean> subscribedMap = new HashMap<>();

  @Pointcut("within(@com.jakewharton.rxbinding.weaving.annotation.Exclusive *)")
  public void withinAnnotatedClass() {}

  @Pointcut("execution(void call(..)) && withinAnnotatedClass()")
  public void subscribe() {}

  @Pointcut("execution(void com.jakewharton.rxbinding.internal.MainThreadSubscription+.onUnsubscribe()) && withinAnnotatedClass()")
  public void unsubscribe() {}

  @Around("subscribe()")
  public Object logAndSubscribe(ProceedingJoinPoint joinPoint) throws Throwable {
    CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
    Class<?> cls = codeSignature.getDeclaringType();

    if (!subscribedMap.containsKey(String.valueOf(cls))) {
      subscribedMap.put(String.valueOf(cls), true);
    } else if (subscribedMap.get(String.valueOf(cls))) {
      Log.d("RxBinding", "Multiple subscribers at once on observable " + cls.getSimpleName());
    }

    return joinPoint.proceed();
  }

  @Around("unsubscribe()")
  public Object logAndUnsubscribe(ProceedingJoinPoint joinPoint) throws Throwable {
    CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
    Class<?> cls = codeSignature.getDeclaringType().getEnclosingClass();
    subscribedMap.remove(String.valueOf(cls));
    return joinPoint.proceed();
  }
}
