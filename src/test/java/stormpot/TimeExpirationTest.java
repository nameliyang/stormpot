/*
 * Copyright 2012 Chris Vest
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormpot;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeExpirationTest {

  private Expiration<Poolable> createExpiration(int ttl) {
    return new TimeExpiration(ttl, TimeUnit.MILLISECONDS);
  }
  
  private SlotInfo<?> infoWithAge(final long ageMillis) {
    return new SlotInfo<Poolable>() {
      public long getAgeMillis() {
        return ageMillis;
      }

      @Override
      public long getClaimCount() {
        return 0;
      }

      @Override
      public Poolable getPoolable() {
        return null;
      }
    };
  }
  
  @Test(expected = IllegalArgumentException.class) public void
  timeUnitCannotBeNull() {
    new TimeExpiration(10, null);
  }
  
  @Test public void
  youngSlotsAreNotInvalid() {
    Expiration<Poolable> expiration = createExpiration(2);
    SlotInfo<?> info = infoWithAge(1);
    assertFalse(expiration.hasExpired(info));
  }

  @Test public void
  slotsAtTheMaximumPermittedAgeAreNotInvalid() {
    Expiration<Poolable> expiration = createExpiration(2);
    SlotInfo<?> info = infoWithAge(2);
    assertFalse(expiration.hasExpired(info));
  }
  
  @Test public void
  slotsOlderThanTheMaximumPermittedAgeAreInvalid() {
    Expiration<Poolable> expiration = createExpiration(2);
    SlotInfo<?> info = infoWithAge(3);
    assertTrue(expiration.hasExpired(info));
  }
  
  @Test(expected = IllegalArgumentException.class) public void
  maxPermittedAgeCannotBeLessThanOne() {
    createExpiration(0);
  }
}
