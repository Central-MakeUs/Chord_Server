package com.coachcoach.user.service;

import com.coachcoach.common.api.CatalogQueryApi;
import com.coachcoach.common.api.InsightQueryApi;
import com.coachcoach.common.api.NotificationQueryApi;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.dto.request.OnboardingRequest;
import com.coachcoach.user.repository.RefreshTokenRepository;
import com.coachcoach.user.repository.StoreRepository;
import com.coachcoach.user.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private CatalogQueryApi catalogQueryApi;

    @Mock
    private InsightQueryApi insightQueryApi;

    @Mock
    private NotificationQueryApi notificationQueryApi;

    @Mock
    private KakaoLoginService kakaoLoginService;

    @Mock
    private NaverLoginService naverLoginService;

    @Mock
    private AppleLoginService appleLoginService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("온보딩 시 스토어가 없으면 생성 후 저장한다")
    void onboarding_CreateStoreWhenMissing() {
        // given
        Long userId = 4L;
        Users user = Users.builder()
                .userId(userId)
                .onboardingCompleted(false)
                .build();
        OnboardingRequest request = new OnboardingRequest(
                "코치코치 카페",
                2,
                BigDecimal.valueOf(12000),
                null,
                true
        );

        given(usersRepository.findByUserId(userId)).willReturn(Optional.of(user));
        given(storeRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(storeRepository.save(any(Store.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.onboarding(userId, request);

        // then
        ArgumentCaptor<Store> storeCaptor = ArgumentCaptor.forClass(Store.class);
        verify(storeRepository).save(storeCaptor.capture());
        Store savedStore = storeCaptor.getValue();
        assertThat(savedStore.getUser()).isEqualTo(user);
        assertThat(savedStore.getName()).isEqualTo("코치코치 카페");
        assertThat(savedStore.getEmployees()).isEqualTo(2);
        assertThat(savedStore.getLaborCost()).isEqualByComparingTo(BigDecimal.valueOf(12000));
        assertThat(savedStore.getIncludeWeeklyHolidayPay()).isTrue();
        assertThat(user.getOnboardingCompleted()).isTrue();
    }
}
