package io.castled.jarvis.taskmanager;

import io.castled.jarvis.taskmanager.models.Task;

public interface JarvisJobExecutor {

    String executeJarvisJob(Task task) throws Exception;
}
