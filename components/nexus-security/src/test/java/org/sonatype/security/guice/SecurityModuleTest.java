/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.security.guice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.guice.bean.binders.ParameterKeys;
import org.sonatype.guice.bean.binders.SpaceModule;
import org.sonatype.guice.bean.binders.WireModule;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.inject.BeanScanning;
import org.sonatype.security.SecuritySystem;
import org.sonatype.sisu.ehcache.CacheManagerComponent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Verifies functionality of SecurityModule.
 * 
 * @since 2.7
 */
public class SecurityModuleTest
{
    private Injector injector;

    @Before
    public void setUp()
    {
        injector = Guice.createInjector( getWireModule() );
    }

    @Test
    public void testInjectionIsSetupCorrectly()
    {
        SecuritySystem securitySystem = injector.getInstance( SecuritySystem.class );

        SecurityManager securityManager = injector.getInstance( SecurityManager.class );

        RealmSecurityManager realmSecurityManager = injector.getInstance( RealmSecurityManager.class );

        assertThat( securitySystem.getSecurityManager(), sameInstance( securityManager ) );
        assertThat( securitySystem.getSecurityManager(), sameInstance( realmSecurityManager ) );

        assertThat( securityManager, instanceOf( DefaultSecurityManager.class ) );
        DefaultSecurityManager defaultSecurityManager = (DefaultSecurityManager) securityManager;

        assertThat( defaultSecurityManager.getSessionManager(), instanceOf( DefaultSessionManager.class ) );
        DefaultSessionManager sessionManager = (DefaultSessionManager) defaultSecurityManager.getSessionManager();
        assertThat( sessionManager.getSessionDAO(), instanceOf( EnterpriseCacheSessionDAO.class ) );
    }

    @After
    public void stopCache()
    {
        if ( injector != null )
        {
            injector.getInstance( CacheManagerComponent.class ).shutdown();
        }
    }

    private Module getWireModule()
    {
        return new WireModule( new SecurityModule(), getSpaceModule(), getPropertiesModule() );
    }

    private Module getSpaceModule()
    {
        return new SpaceModule( new URLClassSpace( getClass().getClassLoader() ), BeanScanning.INDEX );
    }

    protected AbstractModule getPropertiesModule()
    {
        return new AbstractModule()
        {
            @Override
            protected void configure()
            {
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put( "security-xml-file", "target/foo/security.xml" );
                properties.put( "application-conf", "target/plexus-home/conf" );
                binder().bind( ParameterKeys.PROPERTIES ).toInstance( properties );
            }
        };
    }
}
