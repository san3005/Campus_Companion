package com.maggie.rapidsync.model.pojo

data class UserMealPlan(
    val id: String?=null,
    val user: User,
    val mealPlan: MealPlan,
    val restaurants: Set<Restaurant> = emptySet(),
)
