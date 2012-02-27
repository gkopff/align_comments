// ===================================================================
// Tactical comment aligner plugin.
// (c) Copyright 2011 Gregory Kopff
// All Rights Reserved.
// ===================================================================

package align_comments;

import org.eclipse.jface.resource.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.*;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

  // The plug-in ID
  public static final String PLUGIN_ID = "align_comments";

  // The shared instance
  private static Activator plugin;

  /**
   * The constructor
   */
  public Activator()
  {
    ;
  }

  /**
   *  @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;
  }

  /**
   *  @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  /**
   *  Returns the shared instance
   *  @return The shared instance
   */
  public static Activator getDefault()
  {
    return plugin;
  }

  /**
   *  Returns an image descriptor for the image file at the given plug-in relative path
   *  @param path The path
   *  @return The image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }
}
