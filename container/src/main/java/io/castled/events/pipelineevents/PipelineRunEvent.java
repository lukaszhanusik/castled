package io.castled.events.pipelineevents;

import io.castled.events.CastledEvent;
import io.castled.events.CastledEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PipelineRunEvent extends CastledEvent {
    private Long pipelineId;
    private Long pipelineRunId;

    public PipelineRunEvent(Long pipelineId, Long pipelineRunId, CastledEventType castledEventType) {
        super(castledEventType);
        this.pipelineId = pipelineId;
        this.pipelineRunId = pipelineRunId;
    }

    @Override
    public String entityId() {
        return String.valueOf(pipelineRunId);
    }
}
