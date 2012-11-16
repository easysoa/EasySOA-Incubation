#!/usr/bin/env ruby

# WARNING: Linux/Cygwin-dependant

require 'fileutils'
require 'net/http'

EASYSOA_VERSION = '1.0.0-SNAPSHOT' # TODO Extract value from filename
MARKETPLACE_BUILD_OUTPUT = './easysoa-registry-marketplace/target/'
      
def nuxeoctl()
  Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-cap-*-tomcat/bin/nuxeoctl'][0]
end 

def displayHelp()
  puts '', '> HELP'
  puts 'build: Build with Maven. WARNING: Will delete your Nuxeo data/setup'
  puts 'fastbuild: Build with Maven, skip the tests to go faster. WARNING: Will delete your Nuxeo data/setup'
  puts 'test: Run the Maven tests only'
  puts 'deploy: Unzip the Nuxeo distribution if necessary, deploys the EasySOA marketplace package'
  puts 'reset: Deletes the unzipped Nuxeo distribution'
  puts 'run: Runs Nuxeo'
  puts ''
  puts '> EXAMPLES'
  puts './nuxeo.rb test'
  puts './nuxeo.rb fastbuild deploy run'
  puts './nuxeo.rb reset deploy run', ''
end


ARGV.each do |arg|

  case arg
    
    when 'test'
      system('mvn test')
      
    when 'build'
      system('mvn clean install -DskipTests=true -Pmarketplace')
    
    when 'fastbuild'
      system('mvn clean install -DskipTests=true -Pmarketplace')
    
    when 'deploy'
      nuxeoPath = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-cap-*-tomcat'][0]
      
      if nuxeoPath.nil?
        capDistribPath = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-distribution-*.zip'][0]
        puts '> Unzipping ' + capDistribPath + '...'
        system('unzip -q ' + capDistribPath + ' -d ' + MARKETPLACE_BUILD_OUTPUT)
        
        puts '', '> Initializing Nuxeo distribution...'
        nuxeoctlPath = nuxeoctl()
        system('chmod +x ' + nuxeoctlPath)
        system(nuxeoctlPath + ' mp-init')
        puts
      end
      
      nuxeoctlPath = nuxeoctl()
      puts '> Uninstalling package "easy-soa-' + EASYSOA_VERSION  + '"...'
      system(nuxeoctlPath + ' --accept=true mp-remove easy-soa-' + EASYSOA_VERSION)
      
      puts '', '> Installing new EasySOA package...'
      system(nuxeoctlPath + ' --accept=true mp-install ' + Dir[MARKETPLACE_BUILD_OUTPUT + 'easysoa-registry-marketplace-*.zip'][0])
     
    when 'run'
      system(nuxeoctl() + ' console')
    
    when 'reset'
      nuxeoPath = Dir[MARKETPLACE_BUILD_OUTPUT + 'nuxeo-cap-*-tomcat'][0]
      puts '> Deleting', nuxeoPath
      FileUtils.rm_r nuxeoPath, :force => true
    
    else
      displayHelp()
      
  end
  
end

if ARGV.empty?
  displayHelp()
end
