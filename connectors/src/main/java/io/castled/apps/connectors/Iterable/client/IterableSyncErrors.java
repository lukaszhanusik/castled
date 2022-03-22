package io.castled.apps.connectors.Iterable.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class IterableSyncErrors {

    private List<String> failedEmails;
    private List<String> failedUserIds;
}
