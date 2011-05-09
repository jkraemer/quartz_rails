package org.jruby.rack;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jruby.Ruby;
import org.jruby.rack.RackServletContextListener;
import org.jruby.rack.RackApplication;
import org.jruby.rack.RackApplicationFactory;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.quartz.JobExecutionContext;

import org.apache.commons.pool.ObjectPool;

/**
 * Quartz job for running arbitrary Ruby code inside a JRuby/Rack environment.
 *
 * JRuby runtimes are retrieved from the application factory created by RackRailsContextListener.
 */
public class QuartzJob implements Job {

  public void execute(JobExecutionContext context)
			throws JobExecutionException 
  {
    try {
      ServletContext ctx = getServletContext(context);
      JobDataMap data = context.getJobDetail().getJobDataMap();
      String command = data.getString("command");
      ctx.log("executing Job: "+command);

      Ruby runtime = null;
      RackApplication application = null;
      try {
          application = getApplication(ctx);
          runtime = application.getRuntime();
          ctx.log("have runtime");
          runtime.evalScriptlet(command);
          ctx.log("Success!");
      } catch (Exception e) {
          ctx.log("Could not execute: " + command, e);
      } finally {
        if (application != null) {
          returnApplication(ctx, application);
        }
        runtime = null;
        application = null;
      }
    } catch (Exception e) {
      throw new JobExecutionException(e);
    }
	}

	protected RackApplication getApplication(ServletContext ctx)
    throws JobExecutionException, org.jruby.rack.RackInitializationException
  {
		return getFactory(ctx).getApplication();
	}
	
	protected void returnApplication(ServletContext ctx, RackApplication app)
	  throws JobExecutionException 
	{
	  getFactory(ctx).finishedWithApplication(app);
	}
	
	protected RackApplicationFactory getFactory(ServletContext ctx)
	  throws JobExecutionException 
	{
		RackApplicationFactory factory = 
      (RackApplicationFactory)ctx.getAttribute(RackServletContextListener.FACTORY_KEY);
		if (factory == null) {
			throw new JobExecutionException("No rack application factory found, please check your config!");
		}
		return factory;
	}

  protected ServletContext getServletContext(JobExecutionContext ctx) 
    throws org.quartz.SchedulerException 
  {
    return (ServletContext) ctx.getScheduler().getContext().get("servlet_context");
  }
}
