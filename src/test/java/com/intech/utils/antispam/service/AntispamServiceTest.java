package com.intech.utils.antispam.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.intech.utils.antispam.annotation.Settings;
import com.intech.utils.antispam.model.entity.BlockedEntity;
import com.intech.utils.antispam.model.repository.inmemory.InmemoryBlockedRepository;
import com.intech.utils.antispam.model.repository.inmemory.InmemoryQueryLogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;



class AntispamServiceTest {

  private BlockedService blockedService;
  private QueryLogService queryLogService;
  private AntispamService antispamService;
  

  @BeforeEach
  void setUp() {
    blockedService = new BlockedService(new InmemoryBlockedRepository());
    queryLogService = new QueryLogService(new InmemoryQueryLogRepository());
    antispamService = new AntispamService(blockedService, queryLogService);
  }

  @Test
  void dontlockByIp() {

    String userId = "212.212.22.1";
    String queryType = "test-ip";

    repeatCheck(5, userId, queryType, firstBlockIp(), defaultSettings());
  }

  @Test
  void lockByIp() {

    String userId = "212.212.22.1";
    String queryType = "test-ip";

    try {
      repeatCheck(10, userId, queryType, firstBlockIp(), defaultSettings());
      assert true;
    } catch(Block5minException exception) {
      assertTrue(
        blockedService.findBlockedSubscriberByUserId(userId, queryType).isPresent()
      );
    }
  }

  @Test
  void unblockTest() {

    String userId = "212.212.22.1";
    String queryType = "test-ip";

    try {
      repeatCheck(10, userId, queryType, firstBlockIp(), defaultSettings());
      assert true;
    } catch(Block5minException exception) {
      antispamService.unlock(userId, queryType);
    } finally {
      assertTrue(
        !blockedService.findBlockedSubscriberByUserId(userId, queryType).isPresent()
      );
      assertTrue(
        queryLogService.findAllQueryByuserId(userId).isEmpty()
      );
    }
  }

  @Test
  void unlockAfterEndTime() {

    String userId = "212.212.22.1";
    String queryType = "test-ip";

    LocalDateTime currentLocalDate = LocalDateTime.now()
        .plusMinutes(10)
        .plusSeconds(1);

    try {
      repeatCheck(10, userId, queryType, firstBlockIp(), defaultSettings());
      assert true;
    } catch(Block5minException exception) {
      try (MockedStatic<LocalDateTime> topDateTimeUtilMock = Mockito.mockStatic(LocalDateTime.class)) {
          topDateTimeUtilMock.when(() -> LocalDateTime.now()).thenReturn(currentLocalDate);
          assertTrue(
            !blockedService.findBlockedSubscriberByUserId(userId, queryType).isPresent()
          );
      }
    }
  }

  @Test
  void repeatBlock() {
    String userId = "212.212.22.1";
    String queryType = "test-ip";

    LocalDateTime currentLocalDate = LocalDateTime.now().plusMinutes(10);

    try {
      repeatCheck(10, userId, queryType, firstBlockIp(), secondBlockIp());
      assert true;
    } catch (Block5minException minuteException) {

      try (MockedStatic<LocalDateTime> topDateTimeUtilMock = Mockito.mockStatic(LocalDateTime.class)) {
        topDateTimeUtilMock.when(() -> LocalDateTime.now()).thenReturn(currentLocalDate);

        try {

          repeatCheck(10, userId, queryType, firstBlockIp(), secondBlockIp());
          assert true;

        } catch(Block1DayException dayException) {

          BlockedEntity blocked = blockedService.findBlockedSubscriberByUserId(userId, queryType)
              .orElseThrow(RuntimeException::new);
          assertTrue(blocked.isRepeat());
        }
      }
    }
  }

  @Test
  void repeatBlockAfter1Day() {
    String userId = "212.212.22.1";
    String queryType = "test-ip";

    LocalDateTime currentLocalDate = LocalDateTime.now()
        .plusDays(1)
        .plusSeconds(1);

    try {
      repeatCheck(10, userId, queryType, firstBlockIp(), secondBlockIp());
      assert true;
    } catch (Block5minException minuteException) {
      try (MockedStatic<LocalDateTime> topDateTimeUtilMock = Mockito.mockStatic(LocalDateTime.class)) {
          topDateTimeUtilMock.when(() -> LocalDateTime.now()).thenReturn(currentLocalDate);
          try {
            repeatCheck(10, userId, queryType, firstBlockIp(), secondBlockIp());
            assert true;
          } catch(Block5minException dayException) {
              BlockedEntity blocked = blockedService.findBlockedSubscriberByUserId(userId, queryType)
                  .orElseThrow(RuntimeException::new);
              assertFalse(blocked.isRepeat());
          }
      }
    }
  }

  private void repeatCheck(int count, String userId, String queryType, Settings properties, Settings repeat) {
    for (int i = 0; i <= count; i++) {
      antispamService.checkRequest(userId, queryType, properties, repeat);
    }
  }



  private Settings firstBlockIp() {
    return createSettings(1, ChronoUnit.MINUTES, 5, ChronoUnit.MINUTES, 5, Block5minException.class);
  }

  private Settings secondBlockIp() {
    return createSettings(1, ChronoUnit.MINUTES, 1, ChronoUnit.DAYS, 5, Block1DayException.class);
  }

  private Settings defaultSettings() {
    return createSettings(0, ChronoUnit.ERAS, 0, ChronoUnit.ERAS, 0, RuntimeException.class);
  }

  private Settings createSettings(
      int blockPeriod, 
      ChronoUnit blockPeriodTimeUnit, 
      int userBlockPeriod,
      ChronoUnit userBlockPeriodTimeUnit,
      int blockCount,
      Class<? extends RuntimeException> exception) {
    return new Settings() {

      @Override
      public Class<? extends Annotation> annotationType() {
        throw new UnsupportedOperationException();
      }
  
      @Override
      public int blockPeriod() {
        return blockPeriod;
      }
  
      @Override
      public ChronoUnit blockPeriodTimeUnit() {
        return blockPeriodTimeUnit;
      }
  
      @Override
      public int userBlockPeriod() {
        return userBlockPeriod;
      }
  
      @Override
      public ChronoUnit userBlockPeriodTimeUnit() {
        return userBlockPeriodTimeUnit;
      }
  
      @Override
      public int blockCount() {
        return blockCount;
      }
  
      @Override
      public Class<? extends RuntimeException> exception() {
          return exception;
      }
    };
  }
  
}

class Block5minException extends RuntimeException {
  public Block5minException() {}
}

class Block1DayException extends RuntimeException {
  public Block1DayException() {}
}
