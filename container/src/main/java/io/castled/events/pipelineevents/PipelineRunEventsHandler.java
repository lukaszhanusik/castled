package io.castled.events.pipelineevents;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.castled.events.CastledEvent;
import io.castled.events.CastledEventsHandler;

@Singleton
public class PipelineRunEventsHandler implements CastledEventsHandler {

    @Inject
    public PipelineRunEventsHandler() {
    }

    @Override
    public void handleCastledEvent(CastledEvent castledEvent) {

    }
}
