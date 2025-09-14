package org.nutriGuideBuddy.infrastructure.mappers;

import org.nutriGuideBuddy.features.food.dto.FoodView;
import org.nutriGuideBuddy.features.food.repository.projetion.FoodProjection;
import org.nutriGuideBuddy.features.food.repository.projetion.ServingProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class FoodMapperDecorator implements FoodMapper {

  private FoodMapper delegate;
  private ServingMapper servingMapper;

  @Autowired
  public void setDelegate(FoodMapper delegate) {
    this.delegate = delegate;
  }

  @Autowired
  public void setServingMapper(ServingMapper servingMapper) {
    this.servingMapper = servingMapper;
  }

  @Override
  public FoodView toView(FoodProjection projection) {
    FoodView view = delegate.toView(projection);
    var mainServing =
        projection.getServing().stream()
            .filter(ServingProjection::getMain)
            .findFirst()
            .map(servingMapper::toView)
            .orElse(null);

    var otherServings =
        view.otherServings().stream().filter(serving -> !serving.equals(mainServing)).toList();

    return new FoodView(
        view.id(),
        view.name(),
        view.foodInfo(),
        mainServing,
        otherServings,
        view.calorie(),
        view.nutritionList());
  }
}
