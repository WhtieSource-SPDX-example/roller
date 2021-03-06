/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.roller.weblogger.ui.core.security;

import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.servlet.http.HttpServletRequest;

import org.apache.roller.weblogger.config.AuthMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.config.WebloggerConfig;
import org.apache.roller.weblogger.pojos.User;

/**
 * @author Elias Torres (<a href="mailto:eliast@us.ibm.com">eliast@us.ibm.com</a>)
 *
 */
public class CustomUserRegistry {
    
    private static final Log LOG = LogFactory.getLog(CustomUserRegistry.class);

    private static final String DEFAULT_SNAME_LDAP_ATTRIBUTE = "screenname";
    private static final String DEFAULT_UID_LDAP_ATTRIBUTE = "uid";
    private static final String DEFAULT_NAME_LDAP_ATTRIBUTE = "cn";
    private static final String DEFAULT_EMAIL_LDAP_ATTRIBUTE = "mail";
    private static final String DEFAULT_LOCALE_LDAP_ATTRIBUTE = "locale";
    private static final String DEFAULT_TIMEZONE_LDAP_ATTRIBUTE = "timezone";
    
    private static final String SNAME_LDAP_PROPERTY = "users.ldap.registry.attributes.screenname";
    private static final String UID_LDAP_PROPERTY = "users.ldap.registry.attributes.uid";
    private static final String NAME_LDAP_PROPERTY = "users.ldap.registry.attributes.name";
    private static final String EMAIL_LDAP_PROPERTY = "users.ldap.registry.attributes.email";
    private static final String LOCALE_LDAP_PROPERTY = "users.ldap.registry.attributes.locale";
    private static final String TIMEZONE_LDAP_PROPERTY = "users.ldap.registry.attributes.timezone";

    public static User getUserDetailsFromAuthentication(HttpServletRequest request) {

        boolean usingLDAP = WebloggerConfig.getAuthMethod() == AuthMethod.LDAP;
        if (!usingLDAP) {
            LOG.info("LDAP is not enabled. Skipping CustomUserRegistry functionality.");
            return null;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        User ud = new User();
        // setting default
        ud.setId(null);
        ud.setLocale(Locale.getDefault().toString());
        ud.setTimeZone(TimeZone.getDefault().getID());
        ud.setDateCreated(new java.util.Date());

        String userName;
        String unusedPassword;
        String fullName = null;
        String email = null;
        String screenName = null;
        String locale = null;
        String timezone = null;
        boolean enabled;

        if(authentication == null) {
            // Try to get SSO data from HttpServletRequest
            userName = getRequestAttribute(request, WebloggerConfig.getProperty(UID_LDAP_PROPERTY, DEFAULT_UID_LDAP_ATTRIBUTE));

            screenName = getRequestAttribute(request, WebloggerConfig.getProperty(SNAME_LDAP_PROPERTY, DEFAULT_SNAME_LDAP_ATTRIBUTE));

            fullName = getRequestAttribute(request, WebloggerConfig.getProperty(NAME_LDAP_PROPERTY, DEFAULT_NAME_LDAP_ATTRIBUTE));

            email = getRequestAttribute(request, WebloggerConfig.getProperty(EMAIL_LDAP_PROPERTY, DEFAULT_EMAIL_LDAP_ATTRIBUTE));

            locale = getRequestAttribute(request, WebloggerConfig.getProperty(LOCALE_LDAP_PROPERTY, DEFAULT_LOCALE_LDAP_ATTRIBUTE));

            timezone = getRequestAttribute(request, WebloggerConfig.getProperty(TIMEZONE_LDAP_PROPERTY, DEFAULT_TIMEZONE_LDAP_ATTRIBUTE));

 
            if (userName == null && fullName == null && screenName == null &&
                    email == null && locale == null && timezone == null) {

                LOG.warn("No Authentication found in SecurityContextHolder and HttpServletRequest.");
                return null;
            } else {
                enabled = true;
            }
        } else {
        
            Object oPrincipal = authentication.getPrincipal();
        
            if(oPrincipal == null) {
                LOG.warn("Principal is null. Skipping auto-registration.");
                return null;
            }
        
            if (!(oPrincipal instanceof UserDetails)) {
                LOG.warn("Unsupported Principal type in Authentication. Skipping auto-registration.");
                LOG.warn("oPrincipal found of type " + oPrincipal.getClass().getName() + "; needs to be UserDetails");
                return null;
            }
        
            UserDetails userDetails = (UserDetails) oPrincipal;
        
            userName = userDetails.getUsername();
            enabled = userDetails.isEnabled();
        
        
            if(userDetails instanceof RollerUserDetails) {
                RollerUserDetails rollerDetails = (RollerUserDetails) userDetails;

                screenName = rollerDetails.getScreenName();
                fullName = rollerDetails.getFullName();
                email = rollerDetails.getEmailAddress();
                locale = rollerDetails.getLocale();
                timezone = rollerDetails.getTimeZone();
            
            } /* Deprecated in Spring Security 2.0.x: http://static.springsource.org/spring-security/site/docs/2.0.x/apidocs/
                 unsure if can be returned in Spring Security 3.1
                else if(userDetails instanceof LdapUserDetails) {
                LdapUserDetails ldapDetails = (LdapUserDetails) userDetails;

                Attributes attributes = ldapDetails.getAttributes();
                screenName = getLdapAttribute(attributes, WebloggerConfig.getProperty(SNAME_LDAP_PROPERTY, DEFAULT_SNAME_LDAP_ATTRIBUTE));
                fullName = getLdapAttribute(attributes, WebloggerConfig.getProperty(NAME_LDAP_PROPERTY, DEFAULT_NAME_LDAP_ATTRIBUTE));
                email = getLdapAttribute(attributes, WebloggerConfig.getProperty(EMAIL_LDAP_PROPERTY, DEFAULT_EMAIL_LDAP_ATTRIBUTE));
                locale = getLdapAttribute(attributes, WebloggerConfig.getProperty(LOCALE_LDAP_PROPERTY, DEFAULT_LOCALE_LDAP_ATTRIBUTE));
                timezone = getLdapAttribute(attributes, WebloggerConfig.getProperty(TIMEZONE_LDAP_PROPERTY, DEFAULT_TIMEZONE_LDAP_ATTRIBUTE));
            
            } */
        }

        // for LDAP we don't store its password in the roller_users table,
        // just an string indicating external auth method being used.
        unusedPassword = WebloggerConfig.getProperty("users.passwords.externalAuthValue","<externalAuth>");
        ud.setPassword(unusedPassword);
        ud.setEnabled(enabled ? Boolean.TRUE : Boolean.FALSE);

        ud.setUserName(userName);
        ud.setFullName(fullName);
        ud.setEmailAddress(email);
        ud.setScreenName(screenName);
        if (locale != null) {
            ud.setLocale(locale);
        }
        if (timezone != null) {
            ud.setTimeZone(timezone);
        }

        return ud;
    }
    
    private static String getLdapAttribute(Attributes attributes, String name) {
        if(attributes == null) {
            return null;
        }
        
        Attribute attribute = attributes.get(name);
        
        if(attribute == null) {
            return null;
        }
        
        Object oValue  = null;
        try {
            oValue = attribute.get();
        } catch (NamingException e) {
            return null;
        }
        
        if (oValue == null) {
            return null;
        }
        
        return oValue.toString();
    }

    private static String getRequestAttribute(HttpServletRequest request, String attributeName) {

        String attr = null;
        Object attrObj = request.getAttribute(attributeName);
        if (attrObj instanceof String) {
            attr = (String)attrObj;
        } else if (attrObj instanceof Set) {
            Set<?> attrSet = (Set)attrObj;           
            if (!attrSet.isEmpty()) {
                attr = (String)attrSet.iterator().next();
            }
        }

        return attr;
    }
    
}
