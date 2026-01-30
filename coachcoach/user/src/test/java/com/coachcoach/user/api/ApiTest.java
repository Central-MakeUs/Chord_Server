package com.coachcoach.user.api;

import com.coachcoach.common.dto.internal.StoreInfo;
import com.coachcoach.common.exception.BusinessException;
import com.coachcoach.user.domain.Store;
import com.coachcoach.user.domain.Users;
import com.coachcoach.user.exception.UserErrorCode;
import com.coachcoach.user.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryApiImpl 테스트")
class UserQueryApiImplTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private UserQueryApiImpl userQueryApi;

    @Test
    @DisplayName("userId로 StoreInfo를 조회한다")
    void findStoreByUserId_Success() {
        // given
        Long userId = 4L;

        // Repository가 반환할 Store 객체 (Mock)
        Store store = Store.builder()
                .userId(userId)
                .name("장수")
                .build();

        // Mock 동작 정의
        given(storeRepository.findByUserId(userId))
                .willReturn(Optional.of(store));

        // 실행
        StoreInfo result = userQueryApi.findStoreByUserId(userId);

        // 검증
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(4L);
        assertThat(result.name()).isEqualTo("장수");

        // Repository 메서드가 정확히 한 번 호출되었는지 확인
        verify(storeRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("null userId로 조회 시 예외 발생")
    void findStoreByUserId_NullUserId_ThrowsException() {
        // given
        given(storeRepository.findByUserId(null))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userQueryApi.findStoreByUserId(null))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessEx = (BusinessException) ex;
                    assertThat(businessEx.getErrorCode()).isEqualTo(UserErrorCode.NOTFOUND_STORE);
                });
    }
}