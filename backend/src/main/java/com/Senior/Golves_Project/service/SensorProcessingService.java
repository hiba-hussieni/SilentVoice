package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.SensorDataRequest;
import com.Senior.Golves_Project.entity.FingerState;
import com.Senior.Golves_Project.entity.User;
import lombok.Getter;

import org.springframework.stereotype.Service;

@Service
public class SensorProcessingService {
    private static final int DEFAULT_THRESHOLD = 1000;

    public FingerPattern process(SensorDataRequest request, User user) {
        FingerState thumb = toState(request.getThumb(), user.getThumbThreshold());
        FingerState index = toState(request.getIndex(), user.getIndexThreshold());
        FingerState middle = toState(request.getMiddle(), user.getMiddleThreshold());
        FingerState ring = toState(request.getRing(), user.getRingThreshold());
        FingerState pinky = toState(request.getPinky(), user.getPinkyThreshold());
        return new FingerPattern(thumb, index, middle, ring, pinky);
    }
    private FingerState toState(Integer value, Integer threshold) {
        if (value == null) return FingerState.OPEN;

        if (value < 1300) {
            return FingerState.OPEN;
        } else if (value < 2700) {
            return FingerState.HALF;
        } else {
            return FingerState.CLOSED;
        }
    }

    @Getter
    public static class FingerPattern {
        private final FingerState thumb;
        private final FingerState index;
        private final FingerState middle;
        private final FingerState ring;
        private final FingerState pinky;

        public FingerPattern(FingerState thumb, FingerState index, FingerState middle, FingerState ring, FingerState pinky) {
            this.thumb = thumb;
            this.index = index;
            this.middle = middle;
            this.ring = ring;
            this.pinky = pinky;
        }

        public String asText() {
            return thumb + "," + index + "," + middle + "," + ring + "," + pinky;
        }
    }
    public String generatePattern(SensorDataRequest request) {

        int thumbT = 1000;
        int indexT = 1000;
        int middleT = 1000;
        int ringT = 1000;
        int pinkyT = 1000;

        String thumb = request.getThumb() > thumbT ? "OPEN" : "CLOSED";
        String index = request.getIndex() > indexT ? "OPEN" : "CLOSED";
        String middle = request.getMiddle() > middleT ? "OPEN" : "CLOSED";
        String ring = request.getRing() > ringT ? "OPEN" : "CLOSED";
        String pinky = request.getPinky() > pinkyT ? "OPEN" : "CLOSED";

        return String.join(",", thumb, index, middle, ring, pinky);
    }
}
