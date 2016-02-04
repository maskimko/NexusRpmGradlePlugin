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

import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Maksym Shkolnyi aka Maksym Shkolnyi <mshkolnyi@ukr.net> aka maskimko
 */
public class NexusHostnameVerifier implements X509HostnameVerifier, HostnameVerifier{
 private final Logger logger = LoggerFactory.getLogger(NexusHostnameVerifier.class); 
    
    @Override
    public boolean verify(String host, SSLSession sslSession) {
        try {
        Certificate[] peerCertificates = sslSession.getPeerCertificates();
        for ( Certificate cert : peerCertificates) {
        verify(host, (X509Certificate) cert);
        }
        } catch (SSLException ex) {
            logger.warn("Cannot analize SSL certificates", ex );
        }
        return true;
    }
    
    @Override
    public final void verify(String host, X509Certificate cert) throws SSLException{
        Principal subjectDN = cert.getSubjectDN();
        try {
        Collection<List<?>> subjectAlternativeNames = cert.getSubjectAlternativeNames();
        if (subjectAlternativeNames != null) {
            subjectAlternativeNames.stream().map((subList) -> {
                logger.debug("Processing alternative");
                return subList;
            }).map((subList) -> {
                StringBuilder sb = new StringBuilder();
                subList.stream().forEach((o) -> {
                    sb.append(o.toString()).append(", ");
                }); return sb;
            }).forEach((sb) -> {
                logger.debug(sb.toString());
            });
        }
        } catch (CertificateParsingException ex) {
            logger.info("It is useful to ignore such king of exceptions", ex);
        }
        logger.debug("Subject distiguished name: " + subjectDN.getName());
    }

    @Override
    public void verify(String host, SSLSocket ssls) throws IOException {
       verify(host, ssls.getSession());
    }

    @Override
    public void verify(String string, String[] strings, String[] strings1) throws SSLException {
        //Do nothing
        logger.debug("Skip verification of ssl certificate. Doing nothing.");
    }
}
