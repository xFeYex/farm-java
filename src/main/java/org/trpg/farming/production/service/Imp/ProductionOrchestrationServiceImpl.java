package org.trpg.farming.production.service.Imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.trpg.farming.production.common.BizException;
import org.trpg.farming.production.dto.OrchestrationUpdateReq;
import org.trpg.farming.production.dto.OrchestrationUpdateResponse;
import org.trpg.farming.production.entity.OrchestrationConfig;
import org.trpg.farming.production.entity.Subscription;
import org.trpg.farming.production.repository.OrchestrationConfigRepository;
import org.trpg.farming.production.repository.SubscriptionRepository;
import org.trpg.farming.production.service.ProductionOrchestrationService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProductionOrchestrationServiceImpl implements ProductionOrchestrationService {

    private static final DateTimeFormatter START_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Pattern SPRINKLER_ENABLED_PATTERN =
            Pattern.compile("\"sprinklerEnabled\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TARGET_HUMIDITY_PATTERN =
            Pattern.compile("\"targetHumidity\"\\s*:\\s*(-?\\d+)");

    private final SubscriptionRepository subscriptionRepository;
    private final OrchestrationConfigRepository orchestrationConfigRepository;

    @Override
    public OrchestrationUpdateResponse updateSimpleConfig(Long resourceId, OrchestrationUpdateReq request) {
        validateRequest(request);

        Subscription subscription = validateSubscriptionAccess(resourceId, request.getUserId());
        List<String> normalizedStartTimes = normalizeStartTimes(request.getStartTimes());

        OrchestrationConfig config = orchestrationConfigRepository.findByResourceId(resourceId);
        boolean created = false;
        if (config == null) {
            config = buildDefaultConfig(resourceId, subscription.getId());
            created = true;
        }

        String mergedConfigJson = mergeDeviceConfigJson(config.getConfigJson(), request);
        applyRequestToConfig(config, subscription.getId(), request, normalizedStartTimes, mergedConfigJson);

        int affectedRows = created
                ? orchestrationConfigRepository.insert(config)
                : orchestrationConfigRepository.updateById(config);

        if (affectedRows != 1) {
            throw new BizException("编排配置保存失败了，请稍后再试");
        }

        return OrchestrationUpdateResponse.of(
                config,
                normalizedStartTimes,
                readBoolean(mergedConfigJson),
                readInteger(mergedConfigJson)
        );
    }

    private void validateRequest(OrchestrationUpdateReq request) {
        // 这一步先把参数边界卡住，避免存进数据库后才发现时间和次数对不上。
        if (request == null) {
            throw new BizException("请求体不能为空");
        }
        if (request.getUserId() == null) {
            throw new BizException("userId 不能为空");
        }
        if (request.getTimesPerDay() == null || request.getTimesPerDay() <= 0) {
            throw new BizException("timesPerDay 需要是大于 0 的整数");
        }
        if (request.getDurationMinutes() == null || request.getDurationMinutes() <= 0) {
            throw new BizException("durationMinutes 需要是大于 0 的整数");
        }
        if (CollectionUtils.isEmpty(request.getStartTimes())) {
            throw new BizException("startTimes 不能为空");
        }
        if (request.getStartTimes().size() != request.getTimesPerDay()) {
            throw new BizException("startTimes 的数量要和 timesPerDay 保持一致");
        }
        if (request.getTargetHumidity() != null
                && (request.getTargetHumidity() < 0 || request.getTargetHumidity() > 100)) {
            throw new BizException("targetHumidity 建议控制在 0 到 100 之间");
        }
    }

    private Subscription validateSubscriptionAccess(Long resourceId, Long userId) {
        Subscription subscription = subscriptionRepository.findActiveByResourceId(resourceId);
        if (subscription == null) {
            throw new BizException("当前资源没有有效订阅，暂时不能修改编排配置");
        }
        if (subscription.getTenantUserId() == null || !subscription.getTenantUserId().equals(userId)) {
            throw new BizException("只有当前订阅用户本人才能修改编排配置");
        }
        return subscription;
    }

    private List<String> normalizeStartTimes(List<String> startTimes) {
        List<String> normalized = new ArrayList<>(startTimes.size());
        for (String startTime : startTimes) {
            if (!StringUtils.hasText(startTime)) {
                throw new BizException("startTimes 里不能出现空时间");
            }
            try {
                LocalTime parsedTime = LocalTime.parse(startTime.trim(), START_TIME_FORMATTER);
                normalized.add(parsedTime.format(START_TIME_FORMATTER));
            } catch (DateTimeParseException ex) {
                throw new BizException("startTimes 里的时间格式要写成 HH:mm，比如 08:00");
            }
        }
        return normalized;
    }

    private OrchestrationConfig buildDefaultConfig(Long resourceId, Long subscriptionId) {
        // 如果用户第一次调这个接口，就先补一条最基础的配置记录，后面更新会更顺。
        LocalDateTime now = LocalDateTime.now();

        OrchestrationConfig config = new OrchestrationConfig();
        config.setResourceId(resourceId);
        config.setSubscriptionId(subscriptionId);
        config.setOrchestrationType("SIMPLE");
        config.setName("基础灌溉编排");
        config.setScheduleMode("FIXED_TIME");
        config.setStatus("ACTIVE");
        config.setCreatedAt(now);
        config.setUpdatedAt(now);
        return config;
    }

    private String mergeDeviceConfigJson(String currentConfigJson, OrchestrationUpdateReq request) {
        String mergedJson = normalizeJsonObject(currentConfigJson);

        // 当前这条流程只维护白名单里的两个假设备参数，尽量别去碰别的键。
        if (request.getSprinklerEnabled() != null) {
            mergedJson = upsertBooleanField(mergedJson, "sprinklerEnabled", request.getSprinklerEnabled());
        }
        if (request.getTargetHumidity() != null) {
            mergedJson = upsertNumberField(mergedJson, "targetHumidity", request.getTargetHumidity());
        }

        return mergedJson;
    }

    private void applyRequestToConfig(
            OrchestrationConfig config,
            Long subscriptionId,
            OrchestrationUpdateReq request,
            List<String> normalizedStartTimes,
            String mergedConfigJson) {

        LocalDateTime now = LocalDateTime.now();

        config.setSubscriptionId(subscriptionId);
        config.setOrchestrationType("SIMPLE");
        config.setName("基础灌溉编排");
        config.setScheduleMode("FIXED_TIME");
        config.setTimesPerDay(request.getTimesPerDay());
        config.setDurationMinutes(request.getDurationMinutes());
        config.setStartTimesJson(writeStartTimesJson(normalizedStartTimes));
        config.setConfigJson(mergedConfigJson);
        config.setStatus("ACTIVE");
        if (config.getCreatedAt() == null) {
            config.setCreatedAt(now);
        }
        config.setUpdatedAt(now);
    }

    private String writeStartTimesJson(List<String> startTimes) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < startTimes.size(); i++) {
            if (i > 0) {
                jsonBuilder.append(',');
            }
            jsonBuilder.append('"').append(escapeJson(startTimes.get(i))).append('"');
        }
        jsonBuilder.append(']');
        return jsonBuilder.toString();
    }

    private String normalizeJsonObject(String json) {
        if (!StringUtils.hasText(json)) {
            return "{}";
        }
        String trimmedJson = json.trim();
        if (trimmedJson.startsWith("{") && trimmedJson.endsWith("}")) {
            return trimmedJson;
        }
        return "{}";
    }

    private String upsertBooleanField(String json, String fieldName, boolean fieldValue) {
        return upsertRawField(json, fieldName, String.valueOf(fieldValue));
    }

    private String upsertNumberField(String json, String fieldName, int fieldValue) {
        return upsertRawField(json, fieldName, String.valueOf(fieldValue));
    }

    private String upsertRawField(String json, String fieldName, String rawValue) {
        Pattern fieldPattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*([^,}]+)");
        Matcher matcher = fieldPattern.matcher(json);
        if (matcher.find()) {
            return matcher.replaceFirst("\"" + fieldName + "\":" + rawValue);
        }

        String body = json.substring(1, json.length() - 1).trim();
        if (body.isEmpty()) {
            return "{\"" + fieldName + "\":" + rawValue + "}";
        }
        return "{" + body + ",\"" + fieldName + "\":" + rawValue + "}";
    }

    private Boolean readBoolean(String json) {
        Matcher matcher = SPRINKLER_ENABLED_PATTERN.matcher(normalizeJsonObject(json));
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return null;
    }

    private Integer readInteger(String json) {
        Matcher matcher = TARGET_HUMIDITY_PATTERN.matcher(normalizeJsonObject(json));
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
