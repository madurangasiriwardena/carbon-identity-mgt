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

package org.wso2.carbon.identity.mgt.login.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.mgt.bean.User;
import org.wso2.carbon.identity.mgt.exception.IdentityStoreException;
import org.wso2.carbon.security.caas.api.CarbonPermission;
import org.wso2.carbon.security.caas.user.core.bean.Permission;
import org.wso2.carbon.security.caas.user.core.exception.AuthorizationStoreException;

import java.security.Principal;
import java.util.Objects;

/**
 * This class {@code CarbonPrincipal} is the principal representation of the carbon platform.
 * This is an implementation of {@code Principal}.
 *
 * @since 1.0.0
 */
public class IdentityPrincipal implements Principal {

    private static final Logger log = LoggerFactory.getLogger(IdentityPrincipal.class);

    private User user;

    public IdentityPrincipal() {

    }

    public IdentityPrincipal(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String getName() {
        return user.getUniqueUserId();
    }

    public User getUser() {
        return user;
    }

    /**
     * Checks whether the current principal has a given {@code CarbonPermission}.
     *
     * @param carbonPermission CarbonPermission which needs to be checked with principal instance.
     * @return true if authorized.
     */
    public boolean isAuthorized(CarbonPermission carbonPermission) {

        try {
            return user.isAuthorized(new Permission(carbonPermission.getName(), carbonPermission.getActions()));
        } catch (AuthorizationStoreException | IdentityStoreException e) {
            log.error("Access denied for permission " + carbonPermission.getName() + " for user " + user
                    .getUniqueUserId() + " due to a server error", e);
            return false;
        }
    }
}
