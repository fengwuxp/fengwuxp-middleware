package com.wind.context.variable.annotations;

/**
 * 上下文变量名称常量
 *
 * @author wuxp
 * @date 2023-10-24 20:56
 **/
public final class ContextVariableNames {

    private ContextVariableNames() {
        throw new AssertionError();
    }

    public static final String USER_ID = "@@__current_user_id__@@";

    public static final String USER_NAME = "@@__current_user_name__@@";

    public static final String TENANT_ID = "@@__current_tenant_id__@@";

    public static final String REQUEST_IP = "@@__current_request_ip__@@";
}
