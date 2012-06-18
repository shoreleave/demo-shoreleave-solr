
# Partials are written as embedded ruby
require 'erb'

desc "Build the pages and start all servers"
task :start => :default do
    system("cd search; java -jar start.jar& cd ..; lein run& echo")
end
desc "Stop all servers"
task :stop do
    system("killall java")
end
desc "Restart all servers"
task :restart => :stop do
    sleep(2)
    Rake::Task["start"].execute
end

task :default => ['build:fullhtml',]


# Paths and globals
@html_dir = "./resources/pages"              # This is where all the raw HTML pages are
@full_html_dir = "./resources/full_pages"    # This is where the compiled HTML pages are dumped (partials filled in)
@partial_html_dir = "./resources/partials"   # This is where all the partial HTML can be found

###  Partial HTML compiler ###
def write_output( filename, result )
  filename = filename.split( '/' ).last
  File.open( "#{@full_html_dir}/#{filename}", 'wb' ) {|f| f.write( result ) }
  puts "WROTE : #{filename}"
end

def partial( name )
  build_type = @build_type # this gets defined in the build:fullhtml task
  partial_dir = "#{@partial_html_dir}/"
  loaded_name = "#{name}.#{build_type}.partial"
  loaded_name = File.exists?(partial_dir + loaded_name) ?
    loaded_name : "#{name}.partial"
  filename = partial_dir + loaded_name

  if File.exists? filename
    puts "LOADED : #{loaded_name}"
    File.open( filename, 'rb' ).read.chomp
  else
    puts "ERROR : #{filename}"
  end
end
#########
### Rake instructions ###
desc "Start SOLR search"
task :search do
    system("cd search; java -XX:+UseConcMarkSweepGC -jar start.jar")
end

namespace :build do

    desc "Compile partials into the full-html pages"
    task :fullhtml, :build_type do |t, args|
        @build_type = args[:build_type] || ""
        sources = FileList["#{@html_dir}/*.html"]

        sources.each do |f|
            puts "\nCOMPILING : #{f}"
            page = ERB.new( File.open( f, 'rb' ).read )
            write_output f, page.result
        end
    end
end

task :clean => ['clean:fullhtml', ]
namespace :clean do
    desc "Clean up the partial-compiled full HTML pages"
    task :fullhtml do
        system("rm #{@full_html_dir}/*.html")
        puts "Success!  All partial-compiled HTML files removed from #{@full_html_dir}"
    end
end

