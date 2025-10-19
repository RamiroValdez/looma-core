package com.amool.application.port.out;

public interface LoadWorkOwnershipPort {
    boolean isOwner(Long workId, Long userId);
}
