package com.ss871104.notification.listener;

import com.ss871104.notification.domain.Notification;
import com.ss871104.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(Notification notification) {

        notificationRepository.save(notification);

        log.info("Received Notification: '{}' at '{}'", notification.getMessage(), notification.getEventTime());
    }
}
