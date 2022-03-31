package io.castled.jarvis;

import io.castled.jarvis.taskmanager.JarvisJobExecutor;
import io.castled.jarvis.taskmanager.models.Task;
import io.castled.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyJarvisJobExecutor implements JarvisJobExecutor {
    @Override
    public String executeJarvisJob(Task task) {
        try {
            for (int i = 0; i < 60; i++) {
                System.out.println("printing "+i);
                ThreadUtils.interruptIgnoredSleep(1000);
            }
        } catch (Throwable e) {
            log.error("Dummy Executor failed", e);
        }
        return null;
    }
}
