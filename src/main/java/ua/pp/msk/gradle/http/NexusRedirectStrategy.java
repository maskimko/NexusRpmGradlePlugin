/*
 * Copyright 2016 Maksym Shkolnyi self project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ua.pp.msk.gradle.http;

import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Maksym Shkolnyi aka Maksym Shkolnyi <mshkolnyi@ukr.net> aka maskimko
 */
public class NexusRedirectStrategy extends DefaultRedirectStrategy {
 private Logger logger = LoggerFactory.getLogger(NexusRedirectStrategy.class);
            
    
    @Override
    protected boolean isRedirectable(String method) {
        boolean redirectable = super.isRedirectable(method); //To change body of generated methods, choose Tools | Templates.
        logger.debug("Method " + method + " is redirectable " + redirectable);
        logger.debug("Ignoring and returning true");
        return true;
    }
}
