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

package org.wso2.carbon.identity.mgt.login.module.internal;

import org.wso2.carbon.identity.mgt.service.RealmService;
import org.wso2.carbon.identity.mgt.store.IdentityStore;

/**
 * Carbon security data holder.
 *
 * @since 1.0.0
 */
public class IdentityMgtDataHolder {

    private static IdentityMgtDataHolder instance = new IdentityMgtDataHolder();

    private RealmService<IdentityStore> realmService;

    private IdentityMgtDataHolder() {
    }

    /**
     * Get the instance of this class.
     *
     * @return IdentityMgtDataHolder.
     */
    public static IdentityMgtDataHolder getInstance() {
        return instance;
    }

    void setRealmService(RealmService<IdentityStore> realmService) {
        this.realmService = realmService;
    }

    public RealmService<IdentityStore> getRealmService() {

        if (realmService == null) {
            throw new IllegalStateException("Carbon Realm Service is null.");
        }
        return realmService;
    }

}
