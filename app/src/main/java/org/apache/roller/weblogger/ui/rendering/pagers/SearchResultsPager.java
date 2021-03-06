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

package org.apache.roller.weblogger.ui.rendering.pagers;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.roller.weblogger.business.URLStrategy;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.wrapper.WeblogEntryWrapper;
import org.apache.roller.weblogger.ui.rendering.util.WeblogSearchRequest;
import org.apache.roller.weblogger.util.I18nMessages;

/**
 * Pager for navigating through search results.
 */
public class SearchResultsPager implements WeblogEntriesPager {
    
    // message utils for doing i18n messages
    final I18nMessages messageUtils;
    
    // url strategy
    final URLStrategy urlStrategy;
    
    private final Map<Date, Set<WeblogEntryWrapper>> entries;
    
    private final Weblog weblog;
    private final String locale;
    private final String query;
    private final String category;
    private final int page;
    private final boolean moreResults;
    
    public SearchResultsPager(URLStrategy strat, WeblogSearchRequest searchRequest, Map<Date, Set<WeblogEntryWrapper>> entries, boolean more) {
        
        // url strategy for building urls
        this.urlStrategy = strat;
        
        // store search results
        this.entries = entries;
        
        // data from search request
        this.weblog = searchRequest.getWeblog();
        this.query = searchRequest.getQuery();
        this.category = searchRequest.getWeblogCategoryName();
        this.locale = searchRequest.getLocale();
        this.page = searchRequest.getPageNum();
        
        // does this pager have more results?
        this.moreResults = more;
        
        // get a message utils instance to handle i18n of messages
        Locale viewLocale = null;
        if(locale != null) {
            String[] langCountry = locale.split("_");
            if(langCountry.length == 1) {
                viewLocale = new Locale(langCountry[0]);
            } else if(langCountry.length == 2) {
                viewLocale = new Locale(langCountry[0], langCountry[1]);
            }
        } else {
            viewLocale = weblog.getLocaleInstance();
        }
        this.messageUtils = I18nMessages.getMessages(viewLocale);
    }
    
    
    @Override
    public Map<Date, Set<WeblogEntryWrapper>> getEntries() {
        return entries;
    }
    
    
    @Override
    public String getHomeLink() {
        return urlStrategy.getWeblogURL(weblog, locale, false);
    }

    @Override
    public String getHomeName() {
        return messageUtils.getString("searchPager.home");
    }

    
    @Override
    public String getNextLink() {
        if(moreResults) {
            return urlStrategy.getWeblogSearchURL(weblog, locale, query, category, page + 1, false);
        }
        return null;
    }

    @Override
    public String getNextName() {
        if (getNextLink() != null) {
            return messageUtils.getString("searchPager.next");
        }
        return null;
    }

    @Override
    public String getPrevLink() {
        if(page > 0) {
            return urlStrategy.getWeblogSearchURL(weblog, locale, query, category, page - 1, false);
        }
        return null;
    }

    @Override
    public String getPrevName() {
        if (getPrevLink() != null) {
            return messageUtils.getString("searchPager.prev");
        }
        return null;
    }

    
    @Override
    public String getNextCollectionLink() {
        return null;
    }

    @Override
    public String getNextCollectionName() {
        return null;
    }

    @Override
    public String getPrevCollectionLink() {
        return null;
    }

    @Override
    public String getPrevCollectionName() {
        return null;
    }
    
}
