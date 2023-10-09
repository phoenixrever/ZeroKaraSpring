package com.phoenixhell.zerokaraspring.aop.proxy.cglibproxy;

import org.springframework.cglib.core.Signature;


public class ProxyFastClass {

    //只能掉 不带增强的原始的saveSuper 方法
    static Signature s0 = new Signature("saveSuper", "()V");
    static Signature s1 = new Signature("saveSuper", "(I)V");
    static Signature s2 = new Signature("saveSuper", "(J)V");

    // 获取代理方法的编号
    /*
        Proxy
            saveSuper()              0
            saveSuper(int)           1
            saveSuper(long)          2
        signature 包括方法名字、参数返回值
     */
    public int getIndex(Signature signature) {
        if (s0.equals(signature)) {
            return 0;
        } else if (s1.equals(signature)) {
            return 1;
        } else if (s2.equals(signature)) {
            return 2;
        }
        return -1;
    }

    // 根据方法编号, 正常调用目标对象方法
    //这里需要注意 proxy 里面有 methodProxy.invoke 还有methodProxy.invokeSuper 这2个方法 调用的是 增强的save 还是原始的saveSuper 方法喃
    //我们其实调用的是原始saveSuper 方法 因为增强的功能已经在new MethodInterceptor() 的时候写在里面了
    //当然你也不不可能调用invokeSuper 因为里面会调用methodInterceptor.intercept 就会变成死循环了
    public Object invoke(int index, Object proxy, Object[] args) {
        if (index == 0) {
            ((Proxy) proxy).saveSuper();
            return null;
        } else if (index == 1) {
            ((Proxy) proxy).saveSuper((int) args[0]);
            return null;
        } else if (index == 2) {
            ((Proxy) proxy).saveSuper((long) args[0]);
            return null;
        } else {
            throw new RuntimeException("无此方法");
        }
    }

    public static void main(String[] args) {
        ProxyFastClass fastClass = new ProxyFastClass();
        int index = fastClass.getIndex(new Signature("saveSuper", "()V"));
        System.out.println(index);

        fastClass.invoke(index, new Proxy(), new Object[0]);
    }
}
