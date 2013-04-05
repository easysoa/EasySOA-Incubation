#!/usr/bin/env ruby

# WARNING: Linux/Cygwin-dependant

require 'fileutils'
require 'net/http'

begin
  require './nuxeo-config.rb'
rescue LoadError
  puts "'nuxeo-config.rb' is not set. Using default config."
  require './nuxeo-config.default.rb'
end
      
PROJECT_PATH = './' + PROJECT_NAME
MARKETPLACE_BUILD_OUTPUT = PROJECT_PATH + '/target/'

def nuxeoctl()
  if defined? NUXEO_PATH
    path = Dir[NUXEO_PATH + 'bin/nuxeoctl'][0]
  else
    path = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-cap-*-tomcat/bin/nuxeoctl'][0]
  end
  if path == nil
    puts "ERROR, is the Nuxeo path (in 'nuxeo-config.rb') defined correctly?"
    exit 0
  else
    return path
  end
end 

def displayHelp()
  puts '', '> AVAILABLE GOALS'
  puts 'build     : Build with Maven.'
  puts 'fastbuild : Build with Maven, skip the tests to go faster'
  puts 'fastestbuild : Build with Maven, skip the tests and run offline to go faster (consequence: won\'t update any SNAPSHOT from remote repos, like NxStudio plugins)'
  puts 'test      : Run the Maven tests only, in debug mode'
  puts 'deploy    : Unzip the Nuxeo distribution if necessary, deploy the marketplace package'
  puts 'reset     : Delete the unzipped Nuxeo distribution, or reset the data of your custom Nuxeo (works only with Derby/H2 configs)'
  puts 'run       : Runs Nuxeo'
  puts ''
  puts '> WARNING'
  puts 'With the default configuration, the whole Nuxeo distribution is deleted during each build.'
  puts 'Deploy Nuxeo outside of the repository if you want to keep your data/config'
  puts '(its path can be set by creating a "nuxeo-config.rb" from the "nuxeo-config.default.rb" example)'
  puts 'Tip: you can grab a Nuxeo DM ZIP in ' + MARKETPLACE_BUILD_OUTPUT + ' after a build.'
  puts ''
  puts '> EXAMPLES'
  puts './nuxeo.rb test'
  puts './nuxeo.rb fastbuild deploy run'
  puts './nuxeo.rb reset deploy run', ''
end


ARGV.each do |arg|

  case arg
 
    when 'test'
      system('mvnDebug -DforkMode=never test')
      
    when 'build'
      system('mvn clean install -Pmarketplace')
    
    when 'fastbuild'
      system('mvn clean install -DskipTests=true -Pmarketplace')
      
    when 'fastestbuild'
      system('mvn clean install -DskipTests=true -o -Pmarketplace')
    
    when 'deploy'
      if defined? NUXEO_PATH
        nuxeoPath = NUXEO_PATH
      else
        nuxeoPath = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-cap-*-tomcat'][0]
      end
      
      if nuxeoPath.nil?
        capDistribPath = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-distribution-tomcat-*.zip'][0]
        if !File.exists?(capDistribPath)
          puts "ERROR: " + capDistribPath + " not found, aborting."
          break
        end
        
        puts '> Unzipping ' + capDistribPath + '...'
        system('unzip -q ' + capDistribPath + ' -d ' + MARKETPLACE_BUILD_OUTPUT)
        
        nuxeoctlPath = nuxeoctl()
        system('chmod +x ' + nuxeoctlPath)
        
        puts '', '> Initializing Nuxeo distribution...'
        system('chmod +x ' + nuxeoctlPath)
        system(nuxeoctlPath + ' mp-init')
        puts
      else
        nuxeoctlPath = nuxeoctl()
        system('chmod +x ' + nuxeoctlPath)
        
        puts '> Uninstalling package "' + PACKAGE_NAME + '"...'
        system(nuxeoctlPath + ' --accept=true mp-remove ' + PACKAGE_NAME)
      end
      
      puts '', '> Installing new marketplace package...'
      system(nuxeoctlPath + ' --accept=true mp-install ' + Dir[MARKETPLACE_BUILD_OUTPUT + PROJECT_NAME + '-*.zip'][0])
     
    when 'run'
      system(nuxeoctl() + ' console')
    
    when 'reset'
      if defined? NUXEO_PATH
        deletePath = Dir[NUXEO_PATH + 'nxserver/data'][0]
      else
        deletePath = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-cap-*-tomcat'][0]
      end
      if !deletePath.nil?
        puts '> Deleting', deletePath
        FileUtils.rm_r deletePath, :force => true
      else
        puts '> Nothing to delete'
      end
      
    else
      displayHelp()
      
  end
  
end

if ARGV.empty?
  displayHelp()
end
