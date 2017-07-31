package org.atlasapi.feeds.tasks.youview.processing;

import org.atlasapi.feeds.tasks.Task;
import org.atlasapi.reporting.telescope.AtlasFeedsTelescopeProxy;
import org.atlasapi.reporting.telescope.TelescopeProxy;

public interface TaskProcessor {

    /**
     * Processes the main Task action, be that uploading or deleting
     * 
     * @param task
     * @return UpdateProgress representing the progress through the elements processed
     */
    void process(Task task, AtlasFeedsTelescopeProxy telescope);
    
    /**
     * Checks the status of the action represented by the Task on the remote system.
     * 
     * @param task
     * @return UpdateProgress representing the progress through the elements processed
     */
    void checkRemoteStatusOf(Task task);
}
