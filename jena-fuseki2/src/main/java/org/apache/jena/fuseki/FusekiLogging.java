/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.fuseki;

import java.io.File ;
import java.net.URL ;

import org.apache.jena.atlas.lib.StrUtils ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.SysRIOT ;
import org.apache.log4j.PropertyConfigurator ;
import org.apache.log4j.helpers.Loader ;

public class FusekiLogging
{
    private static String log4Jsetup = StrUtils.strjoinNL
        ("## Plain output to stdout",
         "log4j.appender.jena.plainstdout=org.apache.log4j.ConsoleAppender",
         "log4j.appender.jena.plainstdout.target=System.out",
         "log4j.appender.jena.plainstdout.layout=org.apache.log4j.PatternLayout",
         "log4j.appender.jena.plainstdout.layout.ConversionPattern=%d{HH:mm:ss} %-10c{1} %-5p %m%n",
         
         "## Plain output to stderr",
         "log4j.appender.jena.plainstderr=org.apache.log4j.ConsoleAppender",
         "log4j.appender.jena.plainstderr.target=System.err",
         "log4j.appender.jena.plainstderr.layout=org.apache.log4j.PatternLayout",
         "log4j.appender.jena.plainstderr.layout.ConversionPattern=%d{HH:mm:ss} %-10c{1} %-5p %m%n",

         "## Everything", 
         "log4j.rootLogger=INFO, jena.plainstdout",
         "log4j.logger.com.hp.hpl.jena=WARN",
         "log4j.logger.org.openjena=WARN",
         "log4j.logger.org.apache.jena=WARN",

         "log4j.logger.org.apache.jena=WARN",

         "# System logs.",
         "log4j.logger." + Fuseki.serverLogName     + "=INFO",
         "log4j.logger." + Fuseki.actionLogName     + "=INFO",
         "log4j.logger." + Fuseki.adminLogName      + "=INFO",
         "log4j.logger." + Fuseki.validationLogName + "=INFO",
         "log4j.logger." + Fuseki.configLogName     + "=INFO",
         
         "log4j.logger.org.apache.jena.tdb.loader=INFO",
         "log4j.logger.org.eclipse.jetty=WARN" ,
         "log4j.logger.org.apache.shiro=WARN",

         "# NCSA RequestAccess log",
         "log4j.appender.plain=org.apache.log4j.ConsoleAppender",
         "log4j.appender.plain.target=System.out",
         "log4j.appender.plain.layout=org.apache.log4j.PatternLayout",
         "log4j.appender.plain.layout.ConversionPattern=%m%n",
         "log4j.additivity."+Fuseki.requestLogName   + "=false",
         "log4j.logger."+Fuseki.requestLogName       + "=OFF, plain",
         
         "## Parser output", 
         "log4j.additivity" + SysRIOT.riotLoggerName + "=false",
         "log4j.logger." + SysRIOT.riotLoggerName + "=INFO, plainstdout"
            ) ;

    // Set logging.
    // 1/ Use log4j.configuration is defined.
    // 2/ Use file:log4j.properties
    // 3/ Use log4j.properties on the classpath.
    // 4/ Use Built in.

    private static boolean LogLogging = false ;
    
    public static void setLogging() {
        if ( LogLogging )
            System.err.println("Fuseki logging") ;
        // No loggers have been created but configuration may have been set up.
        String x = System.getProperty("log4j.configuration", null) ;
        if ( LogLogging )
            System.err.println("log4j.configuration = " + x) ;

        if ( x != null ) // && !x.equals("set") )
            // Punt to log4j proper, or maybe already set.
            // "set" indicates that logging was set before.
            return ;
        if ( LogLogging )
            System.err.println("Fuseki logging - setup") ;
        // Look for a log4j.properties in the current working directory for easy
        // customization.
        try {
            if ( LogLogging )
                System.err.println("Fuseki logging - look for local log4j.properties") ;
            String fn = "log4j.properties" ;
            File f = new File(fn) ;
            if ( f.exists() ) {
                if ( LogLogging )
                    System.err.println("Fuseki logging - found file:log4j.properties") ;
                PropertyConfigurator.configure(fn) ;
                System.setProperty("log4j.configuration", "file:" + fn) ;
                return ;
            }
            if ( LogLogging )
                System.err.println("Fuseki logging - no local log4j.properties") ;
        }
        catch (Throwable th) {}

        // Try log4j.properties

        // The log4j general is initialization done in a class static
        // in LogManager so it can't be called again in any sensible manner.
        // Instead, we include the same basic mechanism ...
        if ( LogLogging )
            System.err.println("Fuseki logging - look for URL log4j.properties") ;
        URL url = Loader.getResource("log4j.properties") ;
        if ( url != null ) {
            PropertyConfigurator.configure(url) ;
            System.setProperty("log4j.configuration", url.toString()) ;
            return ;
        }

        if ( LogLogging )
            System.err.println("Fuseki logging - LogCtl.resetLogging") ;
        // Use builtin.
        LogCtl.resetLogging(log4Jsetup) ;
        // Stop anything attempting to do it again.
        System.setProperty("log4j.configuration", "set") ;
    }
}

