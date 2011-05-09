QuartzRails
===========

This is a tiny Rails plugin to integrate with the [Quartz scheduling library](http://www.quartz-scheduler.org/) 
when running inside a J2EE application server.

It consists of a ServletContextListener which initializes the scheduler,
and a QuartzJob able to run arbitrary Ruby code in the 
JRuby/Rails environment.

GIT repository is located at https://github.com/jkraemer/quartz_rails

Should also be possible to use with any non-Rails rack-based JRuby applications.


Installing the ContextListener
------------------------------
add the following to your `web.xml`:
  
    <listener>
      <listener-class>org.jruby.rack.QuartzContextListener</listener-class>
    </listener>


Declaring your jobs
-------------------

define two context parameters per job in `web.xml``:

    <context-param>
      <param-name>testJobCommand</param-name>         <!-- key made of yourJobName + 'Command' -->
      <param-value>TestJob.instance.run</param-value> <!-- the command to be evaluated at run time -->
    </context-param>
    <context-param>
      <param-name>testJobCronPattern</param-name>     <!-- key made of yourJobName + 'CronPattern' -->
      <param-value>0 0 6 ? * 5</param-value>          <!-- the cron pattern defining when to run the job. Here: Every thursday at 6am -->
    </context-param>



Building from source
--------------------

To build the java source code, copy servlet-api.jar (not included here because
I'm not sure of the licensing terms, but it is delivered with your app server)
to dependencies/ and run jrake.


Author, Credits
---------------

Created by: Jens Krämer, http://www.jkraemer.net/


