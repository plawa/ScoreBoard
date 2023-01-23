package org.scoreboard.model;

import java.util.Arrays;

public interface BusinessKeyProvidingEntity {

    Object[] getBusinessKey();

    default boolean hasSameBusinessKey(BusinessKeyProvidingEntity otherEntity) {
        return Arrays.equals(getBusinessKey(), otherEntity.getBusinessKey());
    }

}
