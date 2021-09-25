package com.cupcake.learning.exam.util;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class PatchModelMapper extends ModelMapper {

    public PatchModelMapper() {
        super();
        getConfiguration().setPropertyCondition(Conditions.isNotNull());
        getConfiguration().setCollectionsMergeEnabled(false);
        getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }
}
