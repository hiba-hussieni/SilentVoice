package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.ActiveModeRequest;
import com.Senior.Golves_Project.dto.ModeRequest;
import com.Senior.Golves_Project.dto.ModeResponse;
import com.Senior.Golves_Project.entity.Mode;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.GestureMappingRepository;
import com.Senior.Golves_Project.repository.ModeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModeService {

    private final ModeRepository modeRepository;
    private final UserService userService;
    private final GestureMappingRepository gestureMappingRepository;

    public ModeResponse createMode(ModeRequest request) {

        User user = userService.getUserOrThrow(request.getUserId());

        if (modeRepository.existsByUserIdAndNameIgnoreCase(
                user.getId(),
                request.getName()
        )) {
            throw new RuntimeException(
                    "Mode already exists for this user: " + request.getName()
            );
        }

        boolean firstMode =
                modeRepository.findByUserIdOrderByCreatedAtAsc(user.getId()).isEmpty();

        Mode mode = Mode.builder()
                .user(user)
                .name(request.getName())
                .active(firstMode)
                .build();

        return toResponse(modeRepository.save(mode));
    }

    public Mode findOrCreateMode(
            User user,
            String modeName,
            boolean activeIfFirst,
            String iconName,
            String colorHex
    ){

        return modeRepository.findByUserIdAndNameIgnoreCase(
                        user.getId(),
                        modeName
                )
                .orElseGet(() -> {

                    boolean firstMode =
                            modeRepository.findByUserIdOrderByCreatedAtAsc(
                                    user.getId()
                            ).isEmpty();

                    Mode mode = Mode.builder()
                            .user(user)
                            .name(modeName)
                            .active(firstMode && activeIfFirst)
                            .iconName(iconName)
                            .colorHex(colorHex)
                            .build();

                    return modeRepository.save(mode);
                });
    }

    public List<ModeResponse> getUserModes(Long userId) {

        return modeRepository.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Mode getModeOrThrow(Long modeId) {

        return modeRepository.findById(modeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mode not found with id: " + modeId
                        )
                );
    }

    public Mode getActiveModeOrThrow(Long userId) {

        return modeRepository.findByUserIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No active mode found for user: " + userId
                        )
                );
    }

    @Transactional
    public ModeResponse setActiveMode(ActiveModeRequest request) {

        List<Mode> modes =
                modeRepository.findByUserIdOrderByCreatedAtAsc(
                        request.getUserId()
                );

        if (modes.isEmpty()) {
            throw new RuntimeException(
                    "No modes found for user: " + request.getUserId()
            );
        }

        Mode selected = null;

        for (Mode mode : modes) {

            boolean isSelected =
                    mode.getId().equals(request.getModeId());

            mode.setActive(isSelected);

            if (isSelected) {
                selected = mode;
            }
        }

        if (selected == null) {
            throw new RuntimeException(
                    "Mode does not belong to this user or does not exist"
            );
        }

        modeRepository.saveAll(modes);

        return toResponse(selected);
    }

    @Transactional
    public void deleteModeByName(Long userId, String modeName) {

        Mode mode = modeRepository
                .findByUserIdAndNameIgnoreCase(userId, modeName)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mode not found: " + modeName
                        )
                );

        gestureMappingRepository.deleteByUserIdAndModeId(
                userId,
                mode.getId()
        );

        modeRepository.delete(mode);
    }

    public ModeResponse toResponse(Mode mode) {

        return ModeResponse.builder()
                .id(mode.getId())
                .name(mode.getName())
                .active(mode.getActive())
                .iconName(mode.getIconName())
                .colorHex(mode.getColorHex())
                .build();
    }
}