#!/usr/bin/env ruby

require 'fileutils'
require 'net/http'

# CONFIGURATION

NUXEO_PATH = '/data/home/mkalam-alami/nuxeo-cap-5.5-tomcat';

# SCRIPT

ARGV.each do |arg|

  case arg
    
    when 'test'
      exec('mvn test')
      
    when 'debug'
      exec('mvn -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8787 -Xnoagent -Djava.compiler=NONE" test')
      
    when 'build'
      exec('mvn clean install -DskipTests=true')
      
    when 'deploy'
      oldJars = Dir[NUXEO_PATH + '/nxserver/plugins/easysoa-*.jar']
      puts "> Deleting", oldJars
      FileUtils.rm oldJars
      newFiles = Dir['easysoa-registry-packaging/target/nxserver']
      puts "> Copying", newFiles
      FileUtils.cp_r newFiles, NUXEO_PATH
      
    when 'run'
      exec(NUXEO_PATH + '/bin/nuxeoctl console')
    
    when 'reset'
      dataFolder = NUXEO_PATH + '/nxserver/data';
      puts "> Deleting", dataFolder
      FileUtils.rm_r dataFolder, :force => true
    
    else
      puts 'Available commands: test, debug, build, deploy, run, reset'

  end
  
end
