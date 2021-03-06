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

package org.apache.roller.planet.tasks;

import java.io.File;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.config.WebloggerConfig;
import org.apache.roller.weblogger.util.Utilities;
//import org.apache.roller.util.UtilitiesModel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
//import org.apache.velocity.texen.Generator;


/**
 * Generates Planet files based on those entries and the Planet configuration. 
 * <pre>
 * - Uses PlanetConfig properties for templateDir, outputDir and template name
 * - Creates outputdir and a subdirectory for each group
 * - Uses Velocity Texen to generate the static files
 * </pre>
 */
public class GeneratePlanetTask extends PlanetTask {
    
    private static final Log log = LogFactory.getLog(GeneratePlanetTask.class);
    
    
    @Override
    public void run() {
        try {            
//          PlanetManager planetManager = WebloggerFactory.getWeblogger().getPlanetManager();
                        
            // Ignore values from database
            //String mainPage = planetManager.getConfiguration().getMainPage();
            //String templateDir = planetManager.getConfiguration().getTemplateDir();
            //String outputDir = planetManager.getConfiguration().getMainPage();
            
            // Use values from PlanetConfig instead
            String mainPage =    
                WebloggerConfig.getProperty("planet.aggregator.mainPage");
            String templateDir = 
                WebloggerConfig.getProperty("planet.aggregator.template.dir"); 
            String outputDir =   
                WebloggerConfig.getProperty("planet.aggregator.output.dir");
            
            log.info("Calling Velocity Texen to generate Planet files");
            log.info("   Control file       ["+mainPage+"]");
            log.info("   Template directory ["+templateDir+"]"); 
            log.info("   Output directory   ["+outputDir+"]");

            // Fire up Velocity engine, point it at templates and init
            VelocityEngine engine = new VelocityEngine();
            engine.setProperty("resource.loaders", "file");
            engine.setProperty("file.resource.loader.class",
              "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            engine.setProperty("file.resource.loader.path", templateDir);
            engine.init();
            
            // Build context with current date 
            VelocityContext context = new VelocityContext();
            context.put("date", new Date());
            // TODO fix: Use one utilities model and not one of the below
            //context.put("utils", new UtilitiesModel());
            context.put("utilities", new Utilities());
            context.put("planet", new StaticPlanetModel());
            
            // Ensure that output directories exists, one for each group
            File outputDirObj = new File(outputDir);
            if (!outputDirObj.exists()) {
                outputDirObj.mkdirs();
            }
            
            // Generate files: execute control template
            throw new UnsupportedOperationException("TODO: upgrade to velocity 2.x");
            /* 
            Generator generator = Generator.getInstance();
            generator.setVelocityEngine(engine);
            generator.setOutputEncoding("UTF-8");
            generator.setInputEncoding("UTF-8");
            generator.setOutputPath(outputDir);
            generator.setTemplatePath(templateDir);
            generator.parse(mainPage, context);
            generator.shutdown();
            */
        } catch (Exception e) {
            log.error("ERROR generating planet", e);
        }
    }
    
    public static void main(String[] args) throws Exception{
        GeneratePlanetTask task = new GeneratePlanetTask();
        task.initialize();
        task.run();
    }
    
}
