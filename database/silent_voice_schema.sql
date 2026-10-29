-- SilentVoice: schema only; no user data or seed records.
-- Import into a NEW, EMPTY database named silent_voice_db.
-- Existing tables are not dropped or overwritten.

SET NAMES utf8mb4;

CREATE TABLE `feedback` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `mood` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `gesture_mappings` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `gesture_name` varchar(255) NOT NULL,
  `index_state` enum('CLOSED','HALF','OPEN') NOT NULL,
  `meaning` varchar(255) NOT NULL,
  `middle_state` enum('CLOSED','HALF','OPEN') NOT NULL,
  `pinky_state` enum('CLOSED','HALF','OPEN') NOT NULL,
  `ring_state` enum('CLOSED','HALF','OPEN') NOT NULL,
  `thumb_state` enum('CLOSED','HALF','OPEN') NOT NULL,
  `mode_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `accx` int(11) NOT NULL,
  `accy` int(11) NOT NULL,
  `accz` int(11) NOT NULL,
  `gyrox` int(11) NOT NULL,
  `gyroy` int(11) NOT NULL,
  `gyroz` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `modes` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `icon_name` varchar(255) DEFAULT NULL,
  `color_hex` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `translation_history` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `finger_pattern` varchar(255) DEFAULT NULL,
  `gesture_name` varchar(255) DEFAULT NULL,
  `meaning` varchar(1000) DEFAULT NULL,
  `sentence` varchar(2000) DEFAULT NULL,
  `sos` bit(1) DEFAULT NULL,
  `mode_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `index_threshold` int(11) NOT NULL,
  `middle_threshold` int(11) NOT NULL,
  `password` varchar(255) NOT NULL,
  `pinky_threshold` int(11) NOT NULL,
  `ring_threshold` int(11) NOT NULL,
  `thumb_threshold` int(11) NOT NULL,
  `phone` varchar(20) DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `user_thresholds` (
  `id` bigint(20) NOT NULL,
  `index_threshold` int(11) DEFAULT NULL,
  `middle_threshold` int(11) DEFAULT NULL,
  `pinky_threshold` int(11) DEFAULT NULL,
  `ring_threshold` int(11) DEFAULT NULL,
  `thumb_threshold` int(11) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `feedback`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_feedback_user` (`user_id`);

ALTER TABLE `gesture_mappings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4je8gfm7vx2n5hdala2am69u6` (`mode_id`),
  ADD KEY `FKg8ewdhmr63tyvitrvt0oto5vb` (`user_id`);

ALTER TABLE `modes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK3lhdxp8vpnd818olefetmk16s` (`user_id`);

ALTER TABLE `translation_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKceyr6ckwauydda1jwxefcjam` (`mode_id`),
  ADD KEY `FKqgd6ws42gjwyu2yv1sc5u0y36` (`user_id`);

ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`);

ALTER TABLE `user_thresholds`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `feedback`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `gesture_mappings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `modes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `translation_history`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user_thresholds`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `feedback`
  ADD CONSTRAINT `fk_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `gesture_mappings`
  ADD CONSTRAINT `FK4je8gfm7vx2n5hdala2am69u6` FOREIGN KEY (`mode_id`) REFERENCES `modes` (`id`),
  ADD CONSTRAINT `FKg8ewdhmr63tyvitrvt0oto5vb` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `modes`
  ADD CONSTRAINT `FK3lhdxp8vpnd818olefetmk16s` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `translation_history`
  ADD CONSTRAINT `FKceyr6ckwauydda1jwxefcjam` FOREIGN KEY (`mode_id`) REFERENCES `modes` (`id`),
  ADD CONSTRAINT `FKqgd6ws42gjwyu2yv1sc5u0y36` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
