package com.whatcanicook.mapper;

import com.whatcanicook.dto.model.RecipeStepDto;
import com.whatcanicook.model.RecipeStep;
import org.springframework.stereotype.Component;

@Component
public class RecipeStepMapper {


    public RecipeStepDto toDto(RecipeStep step) {
        return new RecipeStepDto(
                step.getId(),
                step.getStepNumber(),
                step.getDescription()
        );
    }

}
