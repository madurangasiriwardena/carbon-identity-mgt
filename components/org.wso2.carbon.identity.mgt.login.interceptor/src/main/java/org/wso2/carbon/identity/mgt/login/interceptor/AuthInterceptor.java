/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.mgt.login.interceptor;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.mgt.login.interceptor.util.AuthCarbonMessage;
import org.wso2.carbon.identity.mgt.login.interceptor.util.Constants;
import org.wso2.carbon.security.caas.api.ProxyCallbackHandler;
import org.wso2.msf4j.Interceptor;
import org.wso2.msf4j.Request;
import org.wso2.msf4j.Response;
import org.wso2.msf4j.ServiceMethodInfo;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Authentication and Authorization interceptor for Carbon Admin Services
 */
@Component(
        name = "org.wso2.carbon.security.interceptor.AuthInterceptor",
        service = Interceptor.class,
        immediate = true
)
public class AuthInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preCall(Request request, Response response, ServiceMethodInfo serviceMethodInfo) throws Exception {

        CallbackHandler callbackHandler = new ProxyCallbackHandler(new AuthCarbonMessage(request));

        LoginContext loginContext;
        try {
            loginContext = new LoginContext(Constants.DEFAULT_JAAS_CONFIG, callbackHandler);
        } catch (LoginException e) {
            log.error("Error occurred while initiating login context.", e);
            sendInternalServerError(response);
            return false;
        }

        try {
            loginContext.login();
        } catch (LoginException e) {
            if (log.isDebugEnabled()) {
                log.debug("Login Failed", e);
            }
            sendUnauthorized(response);
            return false;
        }

        return true;
    }

    @Override
    public void postCall(Request request, int i, ServiceMethodInfo serviceMethodInfo) throws Exception {

    }

    private void sendUnauthorized(Response response) {
        response.setStatus(401);
        response.send();
    }

    private void sendInternalServerError(Response response) {
        response.setStatus(500);
        response.send();
    }

//
//        //TODO: Authorization also handled in this interceptor since we can't decide order of execution for
// interceptors
//
//        if (serviceMethodInfo.getMethod().isAnnotationPresent(Secure.class)) {
//
//            if (!this.isAuthorized(loginContext.getSubject(), buildCarbonPermission(serviceMethodInfo))) {
//                sendUnauthorized(httpResponder);
//                return false;
//            }
//        }
//
//        return true;
//    }
//

//    private boolean isAuthorized(Subject subject, final CarbonPermission requiredPermission) {
//
//        final SecurityManager securityManager;
//
//        if (System.getSecurityManager() == null) {
//            securityManager = new SecurityManager();
//        } else {
//            securityManager = System.getSecurityManager();
//        }
//
//        try {
//            Subject.doAsPrivileged(subject, (PrivilegedExceptionAction) () -> {
//                securityManager.checkPermission(requiredPermission);
//                return null;
//            }, null);
//            return true;
//        } catch (AccessControlException | PrivilegedActionException e) {
//            if (log.isDebugEnabled()) {
//                log.debug("Authorization Failed", e);
//            }
//            return false;
//        }
//    }
//

//
//    private CarbonPermission buildCarbonPermission(ServiceMethodInfo serviceMethodInfo) {
//
//        StringBuilder permissionBuilder = new StringBuilder();
//        permissionBuilder.append(serviceMethodInfo.getMethodName()).append(".")
//                .append(serviceMethodInfo.getMethod().getName());
//
//        return new CarbonPermission(permissionBuilder.toString(), getAction(serviceMethodInfo.getMethod()));
//    }
//
//    private String getAction(Method method) {
//
//        if (method.isAnnotationPresent(GET.class)) {
//            return HttpMethod.GET;
//        } else if (method.isAnnotationPresent(POST.class)) {
//            return HttpMethod.POST;
//        } else if (method.isAnnotationPresent(PUT.class)) {
//            return HttpMethod.PUT;
//        } else if (method.isAnnotationPresent(DELETE.class)) {
//            return HttpMethod.DELETE;
//        }
//        return null;
//    }


}
