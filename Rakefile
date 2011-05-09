require 'rake'
require 'java'

task :default => :build_jar

CLASSES_DIR = 'tmp/classes'

task :build_jar => [ :compile ] do
  jar CLASSES_DIR, 'lib/quartz-rails.jar'
end

task :compile do
  src_dir = 'src/java'
  dest_dir = CLASSES_DIR
  
  FileUtils::mkdir_p(dest_dir)

  javac(src_dir, dest_dir)
end

def jar(src_dir, target)
  `jar cf #{target} -C #{src_dir} .`
end

# inside dependencies/ should be:
# servlet-api.jar
# jruby-rack-0.9.5.jar
# jruby-core-1.3.1.jar
# quartz.jar
# commons-pool-1.3.jar
def build_class_path
  jars = FileList["dependencies/*.jar"]
  jars.map{ |f| File.expand_path f }.join(':')
end

def javac(src_dir, dest_dir)
  java_files = FileList["#{src_dir}/**/*.java"]
  
  unless java_files.empty?
    print "compiling #{java_files.size} java file(s)..."

    args = [ '-cp', build_class_path, '-d', dest_dir, *java_files ]

    buf = java.io.StringWriter.new
    if com.sun.tools.javac.Main.compile(args.to_java(:String), 
                                        java.io.PrintWriter.new(buf)) != 0
      print "FAILED\n\n"
      print buf.to_s
      print "\n"
      fail 'Compile failed'
    end
    print "done\n"
  end
end
