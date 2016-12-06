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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.identity.mgt.login.module.UserNamePasswordLoginModuleFactory;
import org.wso2.carbon.identity.mgt.login.module.UsernamePasswordLoginModule;
import org.wso2.carbon.identity.mgt.service.RealmService;
import org.wso2.carbon.security.caas.boot.ProxyLoginModule;

import java.util.Hashtable;
import javax.security.auth.spi.LoginModule;

/**
 * OSGi service component which handle identity management.
 *
 * @since 1.0.0
 */
@Component(
        name = "org.wso2.carbon.identity.mgt.login.module.internal.IdentityMgtComponent",
        immediate = true,
        property = {
                "componentName=wso2-carbon-identity-mgt"
        }
)
public class IdentityMgtComponent {

    private static final Logger log = LoggerFactory.getLogger(IdentityMgtComponent.class);

    private ServiceRegistration realmServiceRegistration;

    private BundleContext bundleContext;

    @Activate
    public void registerCarbonIdentityMgtProvider(BundleContext bundleContext) {

        this.bundleContext = bundleContext;

        // Registering login module provided by the bundle.
        Hashtable<String, String> usernamePasswordLoginModuleProps = new Hashtable<>();
        usernamePasswordLoginModuleProps.put(ProxyLoginModule.LOGIN_MODULE_SEARCH_KEY,
                UsernamePasswordLoginModule.class.getName());
        bundleContext.registerService(LoginModule.class, new UserNamePasswordLoginModuleFactory(),
                usernamePasswordLoginModuleProps);
    }

    @Deactivate
    public void unregisterCarbonIdentityMgtProvider(BundleContext bundleContext) {

        try {
            if (realmServiceRegistration != null) {
                bundleContext.ungetService(realmServiceRegistration.getReference());
            }
        } catch (Exception e) {
            log.error("Error occurred in un getting service", e);
        }

        log.info("Carbon-Security bundle deactivated successfully.");
    }

    @Reference(
            name = "org.wso2.carbon.identity.mgt.login.module.internal.RealmService",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unRegisterRealmService"
    )
    protected void registerRealmService(RealmService realmService) {
        IdentityMgtDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unRegisterRealmService(RealmService realmService) {
        IdentityMgtDataHolder.getInstance().setRealmService(null);
    }

}
