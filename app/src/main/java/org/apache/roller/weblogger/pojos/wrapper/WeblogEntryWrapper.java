/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package org.apache.roller.weblogger.pojos.wrapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.roller.weblogger.business.URLStrategy;
import org.apache.roller.weblogger.pojos.WeblogEntry;
import org.apache.roller.weblogger.pojos.WeblogEntry.PubStatus;
import org.apache.roller.weblogger.pojos.WeblogEntryTagComparator;
import org.apache.roller.weblogger.util.HTMLSanitizer;


/**
 * Pojo safety wrapper for WeblogEntry objects.
 */
public final class WeblogEntryWrapper {
    
    // keep a reference to the wrapped pojo
    private final WeblogEntry pojo;
    
    // url strategy to use for any url building
    private final URLStrategy urlStrategy;
    
    
    // this is private so that we can force the use of the .wrap(pojo) method
    private WeblogEntryWrapper(WeblogEntry toWrap, URLStrategy strat) {
        this.pojo = toWrap;
        this.urlStrategy = strat;
    }
    
    
    // wrap the given pojo if it is not null
    public static WeblogEntryWrapper wrap(WeblogEntry toWrap, URLStrategy strat) {
        if(toWrap != null) {
            return new WeblogEntryWrapper(toWrap, strat);
        }
        return null;
    }
    
    
    public String getId() {
        return this.pojo.getId();
    }
    
    
    public WeblogCategoryWrapper getCategory() {
        return WeblogCategoryWrapper.wrap(this.pojo.getCategory(), urlStrategy);
    }
    
    
    public List<WeblogCategoryWrapper> getCategories() {      
        return this.pojo.getCategories().stream()
                .map(cat -> WeblogCategoryWrapper.wrap(cat, urlStrategy))
                .collect(Collectors.toList());
    }
    
    
    public WeblogWrapper getWebsite() {
        return WeblogWrapper.wrap(this.pojo.getWebsite(), urlStrategy);
    }
    
    
    public UserWrapper getCreator() {
        return UserWrapper.wrap(this.pojo.getCreator());
    }
    
    
    public String getTitle() {
        return HTMLSanitizer.conditionallySanitize(this.pojo.getTitle());
	}

    
    public String getSummary() {
        return HTMLSanitizer.conditionallySanitize(this.pojo.getSummary());
    }
    
    /**
     * Simply returns the same value that the pojo would have returned.
     */
    public String getText() {
        return HTMLSanitizer.conditionallySanitize(this.pojo.getText());
    }
    
    
    public String getContentType() {
        return this.pojo.getContentType();
    }
    
    
    public String getContentSrc() {
        return this.pojo.getContentSrc();
    }
    
    
    public String getAnchor() {
        return this.pojo.getAnchor();
    }
    
    
    public List<WeblogEntryAttributeWrapper> getEntryAttributes() {
        return this.pojo.getEntryAttributes().stream()
                .map(WeblogEntryAttributeWrapper::wrap)
                .collect(Collectors.toList());
    }
    
    
    public String findEntryAttribute(String name) {
        return this.pojo.findEntryAttribute(name);
    }
    
    
    public Timestamp getPubTime() {
        return this.pojo.getPubTime();
    }
    
    
    public Timestamp getUpdateTime() {
        return this.pojo.getUpdateTime();
    }
    
    
    public PubStatus getStatus() {
        return this.pojo.getStatus();
    }
    
    
    public String getLink() {
        return this.pojo.getLink();
    }
    
    
    public String getPlugins() {
        return this.pojo.getPlugins();
    }
    
    
    public Boolean getAllowComments() {
        return this.pojo.getAllowComments();
    }
    
    
    public Integer getCommentDays() {
        return this.pojo.getCommentDays();
    }
    
    
    public Boolean getRightToLeft() {
        return this.pojo.getRightToLeft();
    }
    
    
    public Boolean getPinnedToMain() {
        return this.pojo.getPinnedToMain();
    }
    
    
    public String getLocale() {
        return this.pojo.getLocale();
    }
    
    
    public List<WeblogEntryTagWrapper> getTags() {
        return this.pojo.getTags().stream()
                .sorted(new WeblogEntryTagComparator()) // by name
                .map(WeblogEntryTagWrapper::wrap)
                .collect(Collectors.toList());
    }
    
    
    public String getTagsAsString() {
        return this.pojo.getTagsAsString();
    }
    
    
    public boolean getCommentsStillAllowed() {
        return this.pojo.getCommentsStillAllowed();
    }
    
    
    public String formatPubTime(String pattern) {
        return this.pojo.formatPubTime(pattern);
    }
    
    
    public String formatUpdateTime(String pattern) {
        return this.pojo.formatUpdateTime(pattern);
    }
    
    
    public List<WeblogEntryCommentWrapper> getComments() {
        return this.pojo.getComments().stream()
                .map(comment -> WeblogEntryCommentWrapper.wrap(comment, urlStrategy))
                .collect(Collectors.toList());
    }
    
    
    public List<WeblogEntryCommentWrapper> getComments(boolean ignoreSpam, boolean approvedOnly) {
        return this.pojo.getComments(ignoreSpam, approvedOnly).stream()
                .map(comment -> WeblogEntryCommentWrapper.wrap(comment, urlStrategy))
                .collect(Collectors.toList());
    }
    
    
    public int getCommentCount() {
        return this.pojo.getCommentCount();
    }
    
    
    public String getPermalink() {
        return this.pojo.getPermalink();
    }
    
    
    public String getPermaLink() {
        return this.pojo.getPermaLink();
    }
    
    
    public String getCommentsLink() {
        return this.pojo.getCommentsLink();
    }
    
    
    public String getDisplayTitle() {
        return this.pojo.getDisplayTitle();
    }
    
    
    public String getRss09xDescription() {
        return this.pojo.getRss09xDescription();
    }
    
    
    public String getRss09xDescription(int maxLength) {
        return this.pojo.getRss09xDescription(maxLength);
    }
    
    
    // TODO: check this method for safety
    public List<String> getPluginsList() {
        return this.pojo.getPluginsList();
    }
    
    
    public String getTransformedText() {
        return this.pojo.getTransformedText();
    }
    
    
    public String getTransformedSummary() {
        return this.pojo.getTransformedSummary();
    }
    
    
    public String displayContent(String readMoreLink) {
        return this.pojo.displayContent(readMoreLink);
    }
    
    
    public String getDisplayContent() {
        return this.pojo.getDisplayContent();
    }

	public String getSearchDescription() {
        return HTMLSanitizer.conditionallySanitize(this.pojo.getSearchDescription());
	}
    
    /**
     * this is a special method to access the original pojo.
     * we don't really want to do this, but it's necessary
     * because some parts of the rendering process still need the
     * orginal pojo object.
     */
    public WeblogEntry getPojo() {
        return this.pojo;
    }
    
}
