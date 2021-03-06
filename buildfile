# Version number for this release
VERSION_NUMBER = "0.1.9-SNAPSHOT"
THIS_VERSION = "0.1.9-SNAPSHOT"
#Release.next_version = "0.1.0-SNAPSHOT"

GROUP = "com.softbork"
COPYRIGHT = "softbork"

require "buildr/ide/eclipse"

# Specify Maven 2.0 remote repositories
repositories.remote << "http://www.ibiblio.org/maven2/"
repositories.remote << "http://mavensync.zkoss.org/maven2"
repositories.remote << "http://reflections.googlecode.com/svn/repo"
repositories.remote << "http://repository.jboss.org/nexus/content/repositories/releases/"

repositories.release_to = 'file:///home/alvi/releases/jeannie'

GEN_STRINGTEMPLATE = transitive('org.antlr:ST4:jar:4.0.4')
GEN_YAML = 'org.yaml:snakeyaml:jar:1.8'
GEN_GSON = 'com.google.code.gson:gson:jar:1.7.1'
GEN_QDOX = 'com.thoughtworks.qdox:qdox:jar:1.12'
GEN_GROOVY = 'org.codehaus.groovy:groovy-all:jar:1.8.0'
#GEN_REFLECTIONS = transitive("org.reflections:reflections:jar:0.9.5-RC2")
GEN_REFLECTIONS = transitive("org.reflections:reflections:jar:0.9.5-RC2").reject { |a| a.group == 'ch.qos.logback' && a.id == 'logback-classic' }
GEN_ANT = transitive("org.apache.ant:ant:jar:1.8.2")
GEN_OPENCSV = 'net.sf.opencsv:opencsv:jar:2.1'

#SLF4J_API = 'org.slf4j:slf4j-api:jar:1.6.1'
SLF4J_IMPL = transitive('ch.qos.logback:logback-classic:jar:0.9.29')
#SLF4J = SLF4J_API, SLF4J_IMPL
SLF4J = SLF4J_IMPL
COMMONS_LOGGING = 'commons-logging:commons-logging:jar:1.1.1'

ALL_TEMPLATE_MODULES = 
	GEN_STRINGTEMPLATE,
	GEN_YAML,
        GEN_GSON,
        GEN_QDOX,
        GEN_GROOVY,
        GEN_REFLECTIONS,
        GEN_ANT,
        GEN_OPENCSV

ALL_COMMON_MODULES = 
	COMMONS_LOGGING,
	SLF4J,
	ALL_TEMPLATE_MODULES

desc "The jeannie generator"
define 'jeannie' do

  project.version = THIS_VERSION
  project.group = GROUP
  manifest["Copyright"] = COPYRIGHT
  manifest["Implementation-Vendor"] = COPYRIGHT
  manifest["Implementation-Version"] = THIS_VERSION

  define 'generator' do
    resources.filter.using 'version'=> THIS_VERSION
    #package(:jar).merge(ALL_COMMON_MODULES)
    compile.with ALL_COMMON_MODULES
    package :jar
    package :sources
  end

  define "extras" do
    task :looper do
      Java::Commands.java(
        :classpath => ['../generator/target/resources', projects('generator'), ALL_COMMON_MODULES],
        :java_args => [
          'com.sb.jeannie.Main',
          '-looper',
          'modules/propertyslurper',
          'extras/src/main/jeannie', 
          'extras/target/generated-sources',
          'extras/src/main/jeannie/jeannie.properties'
          ]
        )
    end

    # whatever you use to generate your sources
    sources = FileList[_("src/main/jeannie/*.csv")]
  
    generate = file(_("target/generated-sources") => sources) do |dir|
    mkdir_p dir.to_s # ensure directory is created
      p 'generating...'
      Java::Commands.java(
      :classpath => ['../generator/target/resources', projects('generator'), ALL_COMMON_MODULES],
      :java_args => [
        'com.sb.jeannie.Main',
        'modules/propertyslurper',
        'extras/src/main/jeannie', 
        'extras/target/generated-sources',
        'extras/src/main/jeannie/jeannie.properties'
        ]
      )
    end

    compile.with ALL_COMMON_MODULES, projects('generator')
    compile.from generate.to_s
  end

  define 'modules' do
      package(:jar, :id=>'propertyslurper').include _('propertyslurper')
      package(:jar, :id=>'testbed').include _('testbed')
  end

  define 'playground' do
    build do
      filter('playground/src/main/shell').into('target').
        using('version'=>version, 'created'=>Time.now).run
    end
  end

  package(:zip).include(projects('generator'), ALL_COMMON_MODULES, :path=>'lib').
                include(projects('modules'), :path=>'modules')
  package(:tgz).include(projects('generator'), ALL_COMMON_MODULES, :path=>'lib').
                include(projects('modules'), :path=>'modules')
end

task :pslooper => 'jeannie:extras:looper'

task :looper do
  Java::Commands.java(
    :classpath => ['generator/target/resources', 'generator/target/classes', ALL_COMMON_MODULES],
    :java_args => [
      'com.sb.jeannie.Main',
      '-looper',
      'modules/testbed',
      'test/input',
      'target/test'
      ]
    )
end
