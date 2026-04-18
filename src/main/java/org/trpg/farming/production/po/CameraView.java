package org.trpg.farming.production.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CameraView {
    private Long id;
    private Long resourceId;
    private String cameraCode;
    private String cameraName;
    private String cameraStatus;
    private String liveStreamUrl;
    private String lastCoverImageUrl;
    private LocalDateTime lastOnlineAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}